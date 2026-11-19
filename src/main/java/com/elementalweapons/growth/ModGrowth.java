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
		ServerPlayNetworking.registerGlobalReceiver(GrowthPayloads.Reset.TYPE, (payload, ctx) -> reset(ctx.player()));

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
		set(p, new Growth(level, xp, points, g.str(), g.mag(), g.vit(), g.agi()));
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
		set(p, new Growth(g.level(), g.xp(), g.points() + spent, 0, 0, 0, 0));
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
