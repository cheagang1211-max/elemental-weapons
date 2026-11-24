package com.elementalweapons.growth;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.boss.ElementalBoss;
import com.elementalweapons.item.ModItems;
import com.elementalweapons.mob.ElementalCreature;
import com.elementalweapons.mob.ElementalMob;
import com.elementalweapons.spell.Fx;

/** 원소 성장(레벨업) 시스템: 저장·동기화·경험치 획득·스탯 적용 */
public final class ModGrowth {
	private ModGrowth() {}

	public static final AttachmentType<Growth> GROWTH = AttachmentRegistry.<Growth>builder()
			.persistent(Growth.CODEC)
			.copyOnDeath()
			.initializer(() -> Growth.DEFAULT)
			.syncWith(Growth.STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
			.buildAndRegister(id("growth"));

	public static final AttachmentType<Paragon> PARAGON = AttachmentRegistry.<Paragon>builder()
			.persistent(Paragon.CODEC)
			.copyOnDeath()
			.initializer(() -> Paragon.DEFAULT)
			.syncWith(Paragon.STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
			.buildAndRegister(id("paragon"));

	public static Paragon paragon(Player p) {
		Paragon g = p.getAttached(PARAGON);
		return g == null ? Paragon.DEFAULT : g;
	}

	public static void setParagon(Player p, Paragon g) { p.setAttached(PARAGON, g); }

	private static Identifier id(String p) { return Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, p); }

	public static Growth get(Player p) {
		Growth g = p.getAttached(GROWTH);
		return g == null ? Growth.DEFAULT : g;
	}

	public static void set(Player p, Growth g) { p.setAttached(GROWTH, g); }

	public static void initialize() {
		PayloadTypeRegistry.serverboundPlay().register(GrowthPayloads.Spend.TYPE, GrowthPayloads.Spend.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(GrowthPayloads.Reset.TYPE, GrowthPayloads.Reset.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(GrowthPayloads.Reset.TYPE, (payload, ctx) -> reset(ctx.player()));
		LevelSync.initialize();
		Jobs.initialize();
		com.elementalweapons.item.ArmorStyle.initialize();
		PayloadTypeRegistry.serverboundPlay().register(GrowthPayloads.GoHome.TYPE, GrowthPayloads.GoHome.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(GrowthPayloads.GoHome.TYPE, (payload, ctx) -> Recall.request(ctx.player()));
		Recall.initialize();
		PayloadTypeRegistry.serverboundPlay().register(GrowthPayloads.OpenCraft.TYPE, GrowthPayloads.OpenCraft.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(GrowthPayloads.OpenCraft.TYPE, (payload, ctx) -> ctx.player().openMenu(new net.minecraft.world.SimpleMenuProvider(
				(id, inv, pl) -> new net.minecraft.world.inventory.CraftingMenu(id, inv, net.minecraft.world.inventory.ContainerLevelAccess.NULL),
				Component.literal("휴대 제작대"))));
		ServerPlayNetworking.registerGlobalReceiver(GrowthPayloads.Spend.TYPE, (payload, ctx) -> spend(ctx.player(), payload.stat(), payload.amount()));
		PayloadTypeRegistry.serverboundPlay().register(GrowthPayloads.SpendParagon.TYPE, GrowthPayloads.SpendParagon.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(GrowthPayloads.SpendParagon.TYPE, (payload, ctx) -> spendParagon(ctx.player(), payload.stat(), payload.amount()));
		PayloadTypeRegistry.clientboundPlay().register(GrowthPayloads.OpenSage.TYPE, GrowthPayloads.OpenSage.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(GrowthPayloads.ResetClass.TYPE, GrowthPayloads.ResetClass.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(GrowthPayloads.ResetClass.TYPE, (payload, ctx) -> sageReset(ctx.player()));
		// 운명의 현자: 처음 접속한 플레이어 근처에 한 번 배치하고, 사라지면 같은 자리에 다시 세움
		net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> server.execute(() -> placeSage(handler.getPlayer())));
		ServerTickEvents.END_SERVER_TICK.register(server -> { if (server.getTickCount() % 200 == 100) keepSage(server.overworld()); });		PayloadTypeRegistry.serverboundPlay().register(GrowthPayloads.ChooseClass.TYPE, GrowthPayloads.ChooseClass.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(GrowthPayloads.ChooseClass.TYPE, (payload, ctx) -> chooseClass(ctx.player(), payload.cls()));
		// 직업에 맞지 않는 무기로는 공격 불가
		net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player, world, hand, target, hit) -> {
			if (player.getMainHandItem().getItem() instanceof com.elementalweapons.item.ElementalWeaponItem w && !canUse(player, w.type())) {
				if (!world.isClientSide()) denyMessage(player, w.type());
				return net.minecraft.world.InteractionResult.FAIL;
			}
			if (player.getMainHandItem().getItem() instanceof com.elementalweapons.item.TrainingWeaponItem tw && !canUse(player, tw.type())) {
				if (!world.isClientSide()) denyMessage(player, tw.type());
				return net.minecraft.world.InteractionResult.FAIL;
			}
			if (!world.isClientSide()) Tutorial.classEvent(player, "hit");
			return net.minecraft.world.InteractionResult.PASS;
		});
		// 원소 방패로 막으면 속성 반격
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (entity instanceof ServerPlayer p && p.isBlocking() && p.getItemBlockingWith().getItem() instanceof com.elementalweapons.item.ElementalWeaponItem w
					&& w.type() == com.elementalweapons.item.WeaponType.SHIELD && source.getEntity() instanceof net.minecraft.world.entity.LivingEntity att && att != p) {
				long now = p.level().getGameTime();
				Long last = COUNTER.get(p.getUUID());
				if (last == null || now - last >= 20) {
					COUNTER.put(p.getUUID(), now);
					com.elementalweapons.spell.ElementEffects.shieldCounter(w.element(), (ServerLevel) p.level(), p, att);
					Tutorial.classEvent(p, "block");
				}
			}
			return true;
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (entity instanceof ElementalBoss) return; // 보스는 ElementalBoss.defeat 에서 처리
			// 마법 폭발·낙하처럼 직접 때린 사람이 안 잡히는 죽음도, 마지막으로 때린 플레이어에게 처치 인정
			ServerPlayer p = source.getEntity() instanceof ServerPlayer sp ? sp
					: entity.getKillCredit() instanceof ServerPlayer sp2 ? sp2 : null;
			if (p == null) return;
			if (entity instanceof net.minecraft.world.entity.monster.Enemy) {
				Tutorial.event(p, 4);
				Jobs.onKill(p);
				oreDrop(p, entity);
				boolean staff = p.getMainHandItem().getItem() instanceof com.elementalweapons.item.ElementalWeaponItem w && w.type() == com.elementalweapons.item.WeaponType.STAFF;
				boolean magic = source.is(net.minecraft.world.damagesource.DamageTypes.INDIRECT_MAGIC) || source.is(net.minecraft.world.damagesource.DamageTypes.MAGIC)
						|| source.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION) || source.getEntity() == null;
				if (staff || magic) Tutorial.classEvent(p, "magic_kill");
			}
			long xp = entity instanceof ElementalMob ? 25 : entity instanceof ElementalCreature ? 5 : entity instanceof net.minecraft.world.entity.monster.Enemy ? 10 : 0;
			if (xp > 0) addXp(p, xp);
		});
		PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, be) -> {
			if (!(player instanceof ServerPlayer sp) || sp.isCreative()) return;
			Identifier key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
			if (!key.getNamespace().equals(ElementalWeaponsMod.MOD_ID)) return;
			String path = key.getPath();
			if (path.equals("orichalcum_ore")) addXp(sp, 30);
			else if (path.endsWith("_ore")) addXp(sp, 8);
		});
		Tutorial.initialize();
		PayloadTypeRegistry.serverboundPlay().register(GrowthPayloads.TutorialSkip.TYPE, GrowthPayloads.TutorialSkip.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(GrowthPayloads.TutorialSkip.TYPE, (payload, ctx) -> Tutorial.set(ctx.player(), payload.restart() ? 1 : Tutorial.DONE));
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 20 != 0) return;
			for (ServerPlayer p : server.getPlayerList().getPlayers()) applyStats(p);
		});
	}

	/** 보스 처치 → 근처 64칸 플레이어 모두 경험치 */
	public static void bossKilled(ServerLevel level, ElementalBoss boss) {
		long hp = boss.maxBigHealth();
		long xp = hp >= 5_000_000_000L ? 50_000 : hp >= 2_500_000_000L ? 12_000 : hp >= 1_500_000_000L ? 6_000 : 3_000;
		for (ServerPlayer p : level.players()) if (p.distanceToSqr(boss) < 64 * 64) { addXp(p, xp); Jobs.onBossKill(p); }
	}

	/** 성장 속도 배율 (얻는 경험치) */
	public static final double XP_MULT = 1.0;

	public static void addXp(ServerPlayer p, long amount) {
		Growth g = get(p);
		if (g.level() >= Growth.MAX_LEVEL) { addParagonXp(p, amount); return; }
		amount = Math.round(amount * XP_MULT);
		int level = g.level();
		long xp = g.xp() + amount;
		int points = g.points();
		int gained = 0;
		while (level < Growth.MAX_LEVEL && xp >= Growth.need(level)) {
			xp -= Growth.need(level);
			level++;
			points += Growth.totalPoints(level) - Growth.totalPoints(level - 1);
			gained++;
		}
		if (level >= Growth.MAX_LEVEL) xp = 0;
		set(p, new Growth(level, xp, points, g.str(), g.mag(), g.vit(), g.agi(), g.cls()));
		if (gained > 0) {
			ServerLevel sl = (ServerLevel) p.level();
			Fx.burst(sl, Fx.dust(0xFFD34D, 1.8F), p.position().add(0, 1.0, 0), 80, 1.0, 0.08);
			Fx.sound(sl, p.position(), SoundEvents.PLAYER_LEVELUP, 1.0F, 0.8F);
			Fx.sound(sl, p.position(), SoundEvents.PLAYER_LEVELUP, 0.7F, 1.4F);
			p.sendSystemMessage(Component.literal("✦ 레벨 업! Lv " + level + "  (스탯 포인트 +" + (Growth.totalPoints(level) - Growth.totalPoints(level - gained)) + ", [K] 성장 창)")
					.withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
			applyStats(p);
			int before = level - gained;
			if (before < Jobs.LV2 && level >= Jobs.LV2 || before < Jobs.LV3 && level >= Jobs.LV3)
				p.sendSystemMessage(Component.literal("⚔ " + (level >= Jobs.LV3 ? 3 : 2) + "차 전직 시험을 받을 수 있습니다! 천공성 1층 훈련장의 '전직 교관'을 찾아가세요. (B키로 천공성 귀환)").withStyle(ChatFormatting.LIGHT_PURPLE));
			if (level >= Evolution.LEVEL && level - gained < Evolution.LEVEL) Evolution.check(p);
		}
	}

	/** 최대 레벨(200) 이후: 초월 경험치 (끝없이 오름) */
	public static void addParagonXp(ServerPlayer p, long amount) {
		Paragon g = paragon(p);
		int level = g.level(), points = g.points(), gained = 0;
		long xp = g.xp() + amount;
		while (xp >= Paragon.need(level)) { xp -= Paragon.need(level); level++; points++; gained++; }
		setParagon(p, new Paragon(level, xp, points, g.pow(), g.arc(), g.imm(), g.swf(), g.best()));
		if (gained > 0) {
			ServerLevel sl = (ServerLevel) p.level();
			Fx.burst(sl, Fx.dust(0xB46CFF, 2.0F), p.position().add(0, 1.0, 0), 120, 1.2, 0.1);
			Fx.ring(sl, Fx.dust(0xFFD34D, 1.6F), p.position().add(0, 0.2, 0), 2.5, 48, 0);
			Fx.sound(sl, p.position(), SoundEvents.PLAYER_LEVELUP, 1.0F, 0.6F);
			Fx.sound(sl, p.position(), SoundEvents.BEACON_POWER_SELECT, 1.0F, 1.2F);
			p.sendSystemMessage(Component.literal("✧ 초월 레벨 업! 초월 " + level + "  (초월 포인트 +" + gained + ", [K] 성장 창 → 초월)")
					.withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
			applyStats(p);
		}
	}

	public static void spendParagon(ServerPlayer p, int stat, int amount) {
		if (stat < 0 || stat > 3 || amount <= 0) return;
		Paragon g = paragon(p);
		int add = Math.min(amount, g.points());
		if (add <= 0) return;
		setParagon(p, g.withStat(stat, g.stat(stat) + add).withPoints(g.points() - add));
		applyStats(p);
	}

	private static final java.util.Map<java.util.UUID, Long> COUNTER = new java.util.HashMap<>();

	/** 직업이 이 무기를 쓸 수 있는가 */
	public static boolean canUse(Player p, com.elementalweapons.item.WeaponType type) {
		// 10레벨 전직 전에는 모든 무기를 써 볼 수 있음
		if (Evolution.element(p) < 0) return true;
		return get(p).playerClass() == type.playerClass();
	}

	private static final String[] REGION_CRYSTAL = {"bloom_crystal", "tide_crystal", "storm_crystal", "gale_crystal", "ember_crystal", "radiant_crystal", "umbral_crystal"};

	/** 원소 지역 몹을 잡으면 일정 확률로 광석 재료 */
	private static void oreDrop(ServerPlayer p, net.minecraft.world.entity.LivingEntity mob) {
		if (!(mob.level() instanceof ServerLevel level)) return;
		int region = com.elementalweapons.world.sky.SkyLayout.regionAt(mob.getX(), mob.getZ());
		if (region < 0) return;
		var rnd = level.getRandom();
		java.util.List<ItemStack> drops = new java.util.ArrayList<>();
		String crystal = region < REGION_CRYSTAL.length ? REGION_CRYSTAL[region] : REGION_CRYSTAL[rnd.nextInt(REGION_CRYSTAL.length)];
		float bonus = region >= REGION_CRYSTAL.length ? 2F : 1F; // 심연은 더 잘 나옴
		if (rnd.nextFloat() < 0.22F * bonus) drops.add(new ItemStack(com.elementalweapons.world.ModBlocks.item(crystal), 1 + rnd.nextInt(2)));
		if (rnd.nextFloat() < 0.08F * bonus) drops.add(new ItemStack(com.elementalweapons.world.ModBlocks.item("raw_aether")));
		if (rnd.nextFloat() < 0.02F * bonus) drops.add(new ItemStack(com.elementalweapons.world.ModBlocks.item("orichalcum_chunk")));
		for (ItemStack s : drops) mob.spawnAtLocation(level, s);
	}

	public static void denyMessage(Player p, com.elementalweapons.item.WeaponType type) {
		com.elementalweapons.item.PlayerClass cur = get(p).playerClass();
		String msg = cur == com.elementalweapons.item.PlayerClass.NONE
				? "먼저 직업을 선택하세요! [K] → " + type.playerClass().koName + "만 " + type.koName + "을(를) 쓸 수 있습니다."
				: type.koName + "은(는) " + type.playerClass().koName + " 전용 무기입니다. (현재 직업: " + cur.koName + ")";
		p.sendOverlayMessage(Component.literal(msg).withStyle(ChatFormatting.RED));
	}

	/** 직업 선택 (처음은 무료, 변경은 원소 정수 3개) */
	public static void chooseClass(ServerPlayer p, int cls) {
		if (cls <= 0 || cls >= com.elementalweapons.item.PlayerClass.values().length) return;
		Growth g = get(p);
		if (g.cls() == cls) return;
		if (g.cls() != 0) {
			p.sendOverlayMessage(Component.literal("직업은 운명의 현자에게서만 바꿀 수 있습니다.").withStyle(ChatFormatting.RED));
			return;
		}
		set(p, g.withClass(cls));
		Tutorial.onClassChosen(p, cls);
		com.elementalweapons.item.PlayerClass pc = com.elementalweapons.item.PlayerClass.of(cls);
		p.sendSystemMessage(Component.literal("✦ 직업: " + pc.koName + "  (사용 무기: " + pc.weapons + ")").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
		Fx.sound((ServerLevel) p.level(), p.position(), SoundEvents.PLAYER_LEVELUP, 1.0F, 1.2F);
	}

	public static final AttachmentType<net.minecraft.core.BlockPos> SAGE_POS = AttachmentRegistry.<net.minecraft.core.BlockPos>builder()
			.persistent(net.minecraft.core.BlockPos.CODEC).buildAndRegister(id("sage_pos"));

	private static void placeSage(ServerPlayer p) {
		ServerLevel level = p.level().getServer().overworld();
		if (level.getAttached(SAGE_POS) != null || p.level() != level) return;
		net.minecraft.world.phys.Vec3 look = p.getLookAngle();
		int x = (int) Math.floor(p.getX() + look.x * 5), z = (int) Math.floor(p.getZ() + look.z * 5);
		int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
		net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(x, y, z);
		level.setAttached(SAGE_POS, pos);
		spawnSage(level, pos);
		p.sendSystemMessage(Component.literal("✦ 운명의 현자가 근처(" + x + ", " + y + ", " + z + ")에 나타났습니다. 직업·스탯 초기화는 현자에게 말을 거세요.").withStyle(ChatFormatting.LIGHT_PURPLE));
	}

	private static void keepSage(ServerLevel level) {
		net.minecraft.core.BlockPos pos = level.getAttached(SAGE_POS);
		if (pos == null || !level.isLoaded(pos)) return;
		if (level.getEntitiesOfClass(com.elementalweapons.mob.FateSage.class, new net.minecraft.world.phys.AABB(pos).inflate(16)).isEmpty()) spawnSage(level, pos);
	}

	private static void spawnSage(ServerLevel level, net.minecraft.core.BlockPos pos) {
		com.elementalweapons.mob.FateSage s = com.elementalweapons.mob.ModMobs.SAGE.create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
		if (s == null) return;
		s.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		level.addFreshEntity(s);
	}

	/** 현자에게 직업·스탯 초기화 (원소 정수 3개) */
	public static void sageReset(ServerPlayer p) {
		if (p.level().getEntitiesOfClass(com.elementalweapons.mob.FateSage.class, p.getBoundingBox().inflate(8)).isEmpty()) {
			p.sendOverlayMessage(Component.literal("운명의 현자 가까이에서만 초기화할 수 있습니다.").withStyle(ChatFormatting.RED));
			return;
		}
		Growth g = get(p);
		if (!p.isCreative()) {
			int need = 3, have = 0;
			for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
				ItemStack s = p.getInventory().getItem(i);
				if (s.is(ModItems.ELEMENT_ESSENCE)) have += s.getCount();
			}
			if (have < need) {
				p.sendOverlayMessage(Component.literal("초기화에는 원소 정수 3개가 필요합니다.").withStyle(ChatFormatting.RED));
				return;
			}
			for (int i = 0; i < p.getInventory().getContainerSize() && need > 0; i++) {
				ItemStack s = p.getInventory().getItem(i);
				if (s.is(ModItems.ELEMENT_ESSENCE)) { int take = Math.min(need, s.getCount()); s.shrink(take); need -= take; }
			}
		}
		int spent = g.str() + g.mag() + g.vit() + g.agi();
		set(p, new Growth(g.level(), g.xp(), g.points() + spent, 0, 0, 0, 0, 0));
		Evolution.reset(p);
		Jobs.reset(p);
		applyStats(p);
		p.sendSystemMessage(Component.literal("✦ 운명이 초기화되었습니다. 전직 창에서 직업과 속성을 다시 고르고 스탯을 다시 찍으세요.").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
		Fx.burst((ServerLevel) p.level(), Fx.dust(0xD080FF, 1.5F), p.position().add(0, 1, 0), 60, 0.8, 0.05);
		Fx.sound((ServerLevel) p.level(), p.position(), SoundEvents.PLAYER_LEVELUP, 1.0F, 0.6F);
		Evolution.check(p);
	}

	public static void spend(ServerPlayer p, int stat, int amount) {
		if (stat < 0 || stat > 3 || amount <= 0) return;
		Growth g = get(p);
		int add = Math.min(amount, Math.min(g.points(), Growth.MAX_STAT - g.stat(stat)));
		if (add <= 0) return;
		set(p, g.withStat(stat, g.stat(stat) + add).withPoints(g.points() - add));
		applyStats(p);
	}

	public static void reset(ServerPlayer p) {
		Growth g = get(p);
		int spent = g.str() + g.mag() + g.vit() + g.agi();
		if (spent == 0) { p.sendOverlayMessage(Component.literal("찍은 스탯이 없습니다.").withStyle(ChatFormatting.GRAY)); return; }
		// 스탯 초기화는 무료 (직업·속성은 운명의 현자에게서만)
		set(p, new Growth(g.level(), g.xp(), g.points() + spent, 0, 0, 0, 0, g.cls()));
		applyStats(p);
		p.sendOverlayMessage(Component.literal("스탯을 초기화했습니다.").withStyle(ChatFormatting.GOLD));
	}

	private static final Identifier HP = id("growth_health"), SPD = id("growth_speed"), ASPD = id("growth_attack_speed"), ATK = id("growth_attack");
	private static final Identifier PHP = id("paragon_health"), PSPD = id("paragon_speed"), PASPD = id("paragon_attack_speed"), PATK = id("paragon_attack");

	public static void applyStats(Player p) {
		Growth g = get(p);
		mod(p, Attributes.MAX_HEALTH, HP, g.bonusHealth(), AttributeModifier.Operation.ADD_VALUE);
		mod(p, Attributes.MOVEMENT_SPEED, SPD, g.speedBonus(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		mod(p, Attributes.ATTACK_SPEED, ASPD, g.attackSpeedBonus(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		mod(p, Attributes.ATTACK_DAMAGE, ATK, g.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE);
		Paragon pg = paragon(p);
		mod(p, Attributes.MAX_HEALTH, PHP, pg.health(), AttributeModifier.Operation.ADD_VALUE);
		mod(p, Attributes.MOVEMENT_SPEED, PSPD, pg.speed(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		mod(p, Attributes.ARMOR_TOUGHNESS, id("paragon_toughness"), pg.toughness(), AttributeModifier.Operation.ADD_VALUE);
		mod(p, Attributes.ATTACK_SPEED, PASPD, pg.attackSpeed(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		mod(p, Attributes.ATTACK_DAMAGE, PATK, pg.damageMult() - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
		mod(p, Attributes.ATTACK_DAMAGE, id("job_attack"), Jobs.damageMult(p) - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);   // 전직 단계 보너스
		if (p.getHealth() > p.getMaxHealth()) p.setHealth(p.getMaxHealth());
	}

	private static void mod(Player p, Holder<Attribute> attr, Identifier id, double v, AttributeModifier.Operation op) {
		AttributeInstance inst = p.getAttribute(attr);
		if (inst == null) return;
		if (v == 0) { inst.removeModifier(id); return; }
		AttributeModifier cur = inst.getModifier(id);
		if (cur != null && cur.amount() == v) return;
		inst.addOrUpdateTransientModifier(new AttributeModifier(id, v, op));
	}
}
