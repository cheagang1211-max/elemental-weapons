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

	private static Identifier id(String p) { return Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, p); }

	public static Growth get(Player p) {
		Growth g = p.getAttached(GROWTH);
		return g == null ? Growth.DEFAULT : g;
	}

	public static void set(Player p, Growth g) { p.setAttached(GROWTH, g); }

	public static void initialize() {
		PayloadTypeRegistry.serverboundPlay().register(GrowthPayloads.Spend.TYPE, GrowthPayloads.Spend.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(GrowthPayloads.Reset.TYPE, GrowthPayloads.Reset.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(GrowthPayloads.Spend.TYPE, (payload, ctx) -> spend(ctx.player(), payload.stat(), payload.amount()));
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
				}
			}
			return true;
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (entity instanceof ElementalBoss) return; // 보스는 ElementalBoss.defeat 에서 처리
			if (!(source.getEntity() instanceof ServerPlayer p)) return;
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
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 20 != 0) return;
			for (ServerPlayer p : server.getPlayerList().getPlayers()) applyStats(p);
		});
	}

	/** 보스 처치 → 근처 64칸 플레이어 모두 경험치 */
	public static void bossKilled(ServerLevel level, ElementalBoss boss) {
		long hp = boss.maxBigHealth();
		long xp = hp >= 5_000_000_000L ? 50_000 : hp >= 2_500_000_000L ? 12_000 : hp >= 1_500_000_000L ? 6_000 : 3_000;
		for (ServerPlayer p : level.players()) if (p.distanceToSqr(boss) < 64 * 64) addXp(p, xp);
	}

	public static void addXp(ServerPlayer p, long amount) {
		Growth g = get(p);
		if (g.level() >= Growth.MAX_LEVEL) return;
		int level = g.level();
		long xp = g.xp() + amount;
		int points = g.points();
		int gained = 0;
		while (level < Growth.MAX_LEVEL && xp >= Growth.need(level)) {
			xp -= Growth.need(level);
			level++;
			points += Growth.POINTS_PER_LEVEL;
			gained++;
		}
		if (level >= Growth.MAX_LEVEL) xp = 0;
		set(p, new Growth(level, xp, points, g.str(), g.mag(), g.vit(), g.agi(), g.cls()));
		if (gained > 0) {
			ServerLevel sl = (ServerLevel) p.level();
			Fx.burst(sl, Fx.dust(0xFFD34D, 1.8F), p.position().add(0, 1.0, 0), 80, 1.0, 0.08);
			Fx.sound(sl, p.position(), SoundEvents.PLAYER_LEVELUP, 1.0F, 0.8F);
			Fx.sound(sl, p.position(), SoundEvents.PLAYER_LEVELUP, 0.7F, 1.4F);
			p.sendSystemMessage(Component.literal("✦ 레벨 업! Lv " + level + "  (스탯 포인트 +" + gained * Growth.POINTS_PER_LEVEL + ", [K] 성장 창)")
					.withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
			applyStats(p);
		}
	}

	private static final java.util.Map<java.util.UUID, Long> COUNTER = new java.util.HashMap<>();

	/** 직업이 이 무기를 쓸 수 있는가 */
	public static boolean canUse(Player p, com.elementalweapons.item.WeaponType type) {
		return get(p).playerClass() == type.playerClass();
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
		applyStats(p);
		p.sendSystemMessage(Component.literal("✦ 운명이 초기화되었습니다. 새 직업을 고르고 스탯을 다시 찍으세요.").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
		Fx.burst((ServerLevel) p.level(), Fx.dust(0xD080FF, 1.5F), p.position().add(0, 1, 0), 60, 0.8, 0.05);
		Fx.sound((ServerLevel) p.level(), p.position(), SoundEvents.PLAYER_LEVELUP, 1.0F, 0.6F);
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
		if (spent == 0) return;
		if (!p.isCreative()) {
			ItemStack found = ItemStack.EMPTY;
			for (int i = 0; i < p.getInventory().getContainerSize(); i++) {
				ItemStack s = p.getInventory().getItem(i);
				if (s.is(ModItems.ELEMENT_ESSENCE)) { found = s; break; }
			}
			if (found.isEmpty()) {
				p.sendOverlayMessage(Component.literal("스탯 초기화에는 원소 정수 1개가 필요합니다.").withStyle(ChatFormatting.RED));
				return;
			}
			found.shrink(1);
		}
		set(p, new Growth(g.level(), g.xp(), g.points() + spent, 0, 0, 0, 0, g.cls()));
		applyStats(p);
		p.sendOverlayMessage(Component.literal("스탯을 초기화했습니다.").withStyle(ChatFormatting.GOLD));
	}

	private static final Identifier HP = id("growth_health"), SPD = id("growth_speed"), ASPD = id("growth_attack_speed"), ATK = id("growth_attack");

	public static void applyStats(Player p) {
		Growth g = get(p);
		mod(p, Attributes.MAX_HEALTH, HP, g.bonusHealth(), AttributeModifier.Operation.ADD_VALUE);
		mod(p, Attributes.MOVEMENT_SPEED, SPD, g.speedBonus(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		mod(p, Attributes.ATTACK_SPEED, ASPD, g.attackSpeedBonus(), AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
		mod(p, Attributes.ATTACK_DAMAGE, ATK, g.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE);
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
