package com.elementalweapons.rift;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.growth.ModGrowth;
import com.elementalweapons.item.ModItems;
import com.elementalweapons.mob.ModMobs;
import com.elementalweapons.spell.Fx;

/**
 * 원소 균열: 단계(티어)가 끝없이 올라가는 도전.
 *  반지름 16칸 결계 안에서 3웨이브를 버티고(마지막은 균열 수호자) 제한 시간 안에 정리하면 돌파.
 *  단계가 오를수록 적의 체력·공격력이 계속 강해지고 보상(경험치/초월 경험치·원소 정수·소환석)도 커짐.
 */
public class Rift {
	public static final double R = 16;
	private static final int TIME = 20 * 240;
	private static final Identifier HP = Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "rift_health");
	private static final Identifier ATK = Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "rift_attack");
	private static final Identifier SCALE = Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "rift_scale");

	final ServerLevel level;
	final Vec3 c;
	final int tier;
	final Set<UUID> players = new HashSet<>();
	private final List<LivingEntity> mobs = new ArrayList<>();
	private final ServerBossEvent bar;
	private int age, wave = 0, pause = 60;
	boolean finished;
	/** 열쇠를 쓴 사람 (실패 시 열쇠 반환) */
	UUID opener;

	Rift(ServerLevel level, Vec3 c, int tier, List<ServerPlayer> who) {
		this.level = level; this.c = c; this.tier = tier;
		bar = new ServerBossEvent(UUID.randomUUID(), Component.literal(""), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_10);
		for (ServerPlayer p : who) { players.add(p.getUUID()); bar.addPlayer(p); }
		Fx.sound(level, c, SoundEvents.END_PORTAL_SPAWN, 1.0F, 1.3F);
		Fx.sound(level, c, SoundEvents.WITHER_SPAWN, 0.5F, 1.6F);
		for (ServerPlayer p : who) p.sendSystemMessage(Component.literal("✧ 원소 균열 " + tier + "단계가 열렸습니다! 결계 안에서 3웨이브를 정리하세요 (제한 4분)")
				.withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
	}

	private List<ServerPlayer> alive() {
		List<ServerPlayer> out = new ArrayList<>();
		for (UUID id : players) {
			ServerPlayer p = level.getServer().getPlayerList().getPlayer(id);
			if (p != null && p.isAlive() && p.level() == level) out.add(p);
		}
		return out;
	}

	void tick() {
		age++;
		List<ServerPlayer> ps = alive();
		// 결계: 원기둥 모양의 보라 입자 + 가운데 빛기둥
		if (age % 6 == 0) {
			for (int k = 0; k < 2; k++) Fx.ring(level, Fx.dust(0xB46CFF, 1.8F), c.add(0, 0.3 + k * 2.2, 0), R, 44, age * 0.02 + k);
			Fx.ring(level, com.elementalweapons.fx.P.REVERSE_PORTAL, c.add(0, 0.2, 0), R - 0.5, 20, age * 0.05);
			for (int y = 0; y < 12; y += 2) Fx.at(level, com.elementalweapons.fx.P.END_ROD, c.add(0, y, 0));
		}
		// 결계 밖으로 나가면 안쪽으로 되돌림
		for (ServerPlayer p : ps) {
			Vec3 d = p.position().subtract(c).multiply(1, 0, 1);
			if (d.length() > R + 1) {
				Vec3 in = c.add(d.normalize().scale(R - 2));
				p.teleportTo(in.x, Math.max(p.getY(), c.y), in.z);
				p.sendOverlayMessage(Component.literal("균열의 결계가 당신을 붙잡습니다").withStyle(ChatFormatting.DARK_PURPLE));
			}
		}
		mobs.removeIf(m -> !m.isAlive() || m.isRemoved());
		// 몹도 결계 밖으로 못 나감
		for (LivingEntity m : mobs) if (m.position().subtract(c).multiply(1, 0, 1).length() > R + 2) m.teleportTo(c.x, c.y + 1, c.z);
		int left = TIME - age;
		bar.setName(Component.literal("✧ 원소 균열 " + tier + "단계 · 웨이브 " + Math.max(1, wave) + "/3 · 남은 적 " + mobs.size()
				+ " · " + left / 1200 + ":" + String.format("%02d", left / 20 % 60)));
		bar.setProgress(Math.max(0, left / (float) TIME));
		if (ps.isEmpty()) { fail("균열에 남은 도전자가 없습니다"); return; }
		if (left <= 0) { fail("시간이 다 되어 균열이 닫혔습니다"); return; }
		if (mobs.isEmpty()) {
			if (wave >= 3) { success(ps); return; }
			if (--pause <= 0) { wave++; spawnWave(ps); pause = 50; }
		}
		for (LivingEntity m : mobs) if (m instanceof Mob mob && (mob.getTarget() == null || !mob.getTarget().isAlive()) && !ps.isEmpty())
			mob.setTarget(ps.get(level.getRandom().nextInt(ps.size())));
	}

	private void spawnWave(List<ServerPlayer> ps) {
		List<EntityType<?>> types = new ArrayList<>(ModMobs.MOBS.values());
		int n = Math.min(14, 3 + tier / 2 + wave + (ps.size() - 1) * 2);
		double hp = 0.4 * tier + 0.25 * (ps.size() - 1), atk = 0.15 * tier;
		for (int i = 0; i < n; i++) {
			double a = i * Math.PI * 2 / n + wave;
			Vec3 pos = c.add(Math.cos(a) * 10, 1, Math.sin(a) * 10);
			LivingEntity m = spawn(types.get(level.getRandom().nextInt(types.size())), pos, hp, atk, 1.0);
			if (m != null) mobs.add(m);
		}
		if (wave == 3) {
			LivingEntity g = spawn(types.get(level.getRandom().nextInt(types.size())), c.add(0, 1, 0), hp * 3 + 6, atk * 1.5 + 0.5, 1.8);
			if (g != null) {
				g.setCustomName(Component.literal("균열 수호자 · " + tier + "단계").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
				g.setCustomNameVisible(true);
				g.setGlowingTag(true);
				mobs.add(g);
			}
		}
		Fx.sound(level, c, SoundEvents.EVOKER_PREPARE_SUMMON, 1.0F, 0.8F);
		for (ServerPlayer p : ps) p.sendOverlayMessage(Component.literal(wave == 3 ? "⚠ 마지막 웨이브 — 균열 수호자 등장!" : "웨이브 " + wave + "/3").withStyle(ChatFormatting.LIGHT_PURPLE));
	}

	private LivingEntity spawn(EntityType<?> type, Vec3 pos, double hp, double atk, double scale) {
		if (!(type.create(level, EntitySpawnReason.MOB_SUMMONED) instanceof LivingEntity m)) return null;
		m.snapTo(pos.x, pos.y, pos.z, level.getRandom().nextFloat() * 360, 0);
		mod(m.getAttribute(Attributes.MAX_HEALTH), HP, hp);
		mod(m.getAttribute(Attributes.ATTACK_DAMAGE), ATK, atk);
		if (scale != 1.0) mod(m.getAttribute(Attributes.SCALE), SCALE, scale - 1);
		m.setHealth(m.getMaxHealth());
		if (m instanceof Mob mob) mob.setPersistenceRequired();
		level.addFreshEntity(m);
		Fx.burst(level, Fx.dust(0xB46CFF, 1.4F), pos.add(0, 1, 0), 24, 0.6, 0.05);
		Fx.burst(level, com.elementalweapons.fx.P.REVERSE_PORTAL, pos.add(0, 1, 0), 20, 0.5, 0.1);
		return m;
	}

	private static void mod(AttributeInstance inst, Identifier id, double v) {
		if (inst == null || v <= 0) return;
		inst.addOrUpdateTransientModifier(new AttributeModifier(id, v, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
	}

	private void success(List<ServerPlayer> ps) {
		finished = true;
		long xp = Math.round(1500 * Math.pow(1 + tier, 1.25));
		int ess = 1 + tier / 3;
		for (ServerPlayer p : ps) {
			ModGrowth.addXp(p, xp);
			var pg = ModGrowth.paragon(p);
			if (tier > pg.best()) ModGrowth.setParagon(p, ModGrowth.paragon(p).withBest(tier));
			give(p, new ItemStack(ModItems.ELEMENT_ESSENCE, ess));
			give(p, new ItemStack(RiftItems.RIFT_KEY, 1));
			if (tier % 5 == 0) {
				List<net.minecraft.world.item.Item> stones = new ArrayList<>(ModItems.BOSS_SUMMONS.values());
				give(p, new ItemStack(stones.get(level.getRandom().nextInt(stones.size()))));
			}
			p.sendSystemMessage(Component.literal("✧ 균열 " + tier + "단계 돌파! 경험치 +" + xp + ", 원소 정수 +" + ess + ", 균열의 열쇠 +1"
					+ (tier % 5 == 0 ? ", 보스 소환석 +1" : "") + "  (최고 기록: " + Math.max(tier, pg.best()) + "단계)").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
		}
		Fx.burst(level, Fx.dust(0xFFD34D, 2.0F), c.add(0, 2, 0), 160, 3, 0.2);
		Fx.sound(level, c, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
		close();
	}

	private void give(ServerPlayer p, ItemStack s) { if (!p.getInventory().add(s)) p.spawnAtLocation(level, s); }

	public void fail(String why) {
		finished = true;
		for (ServerPlayer p : alive()) p.sendSystemMessage(Component.literal("✧ " + why + " (균열 " + tier + "단계 실패)").withStyle(ChatFormatting.RED));
		for (LivingEntity m : mobs) m.discard();
		Fx.sound(level, c, SoundEvents.BEACON_DEACTIVATE, 1.0F, 0.7F);
		close();
	}

	private void close() {
		bar.removeAllPlayers();
		Fx.burst(level, com.elementalweapons.fx.P.REVERSE_PORTAL, c.add(0, 1, 0), 200, 6, 0.3);
	}
}
