package com.elementalweapons.boss;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.item.Element;
import com.elementalweapons.mob.ModMobs;
import com.elementalweapons.spell.Fx;
import com.elementalweapons.world.sky.Arenas;

/**
 * 보스 기믹 전투.
 *  · 원소 장판: 플레이어 발밑에 예고 원이 줄어들다 폭발 (페이즈가 오를수록 자주, 많이)
 *  · 수정 탑(보스방 전용): 탑 꼭대기 수정이 살아 있는 동안 보스는 피해 90% 감소 → 수정을 부숴야 함 (1·3페이즈)
 *  · 결계(보스방 전용): 전투 중 보스방 밖으로 못 나감
 *  · 하수인: 2·3페이즈에 속성 몹 소환
 *  · 보스별: 어둠의 파동(빛의 원 안에 숨기), 폭풍 고리(가장자리 피해), 심판의 빛(빠른 추적 장판), 빙결 장판(둔화)
 */
public class BossFight {
	private static final List<BossFight> ACTIVE = new ArrayList<>();

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (Iterator<BossFight> it = ACTIVE.iterator(); it.hasNext(); ) {
				BossFight f = it.next();
				try { if (f.tick()) { f.end(); it.remove(); } }
				catch (Throwable t) { ElementalWeaponsMod.LOGGER.error("[BOSS] 기믹 오류", t); f.end(); it.remove(); }
			}
		});
		// 도전자가 모두 쓰러지면 보스전 종료 + 소환석 반환
		net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!(entity instanceof ServerPlayer dead)) return;
			for (BossFight f : new ArrayList<>(ACTIVE)) {
				if (f.aborted || f.level != dead.level()) continue;
				boolean involved = dead.getUUID().equals(f.summoner) || dead.position().distanceToSqr(f.c) < 48 * 48;
				if (!involved) continue;
				respawnAt(dead, f.c, f.arena != null ? Arenas.R + 4 : 12);
				boolean others = false;
				for (ServerPlayer q : f.fighters()) if (q != dead) { others = true; break; }
				if (!others) {
					f.abort();
					if (f.instanced) RESPAWN_AT.remove(dead.getUUID());   // 전용 방이 끝났으면 천공성에서 부활
				}
			}
		});
		net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents.AFTER_RESPAWN.register((oldP, newP, alive) -> {
			Vec3 at = RESPAWN_AT.remove(newP.getUUID());
			if (at != null) {
				newP.teleportTo(at.x, at.y, at.z);
				newP.sendSystemMessage(Component.literal("✦ 도전하던 곳 입구에서 다시 일어났습니다.").withStyle(ChatFormatting.GRAY));
			}
			giveRefund(newP);
		});
		net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((h, s, server) -> server.execute(() -> giveRefund(h.getPlayer())));
	}

	private static final java.util.Map<java.util.UUID, List<net.minecraft.world.item.ItemStack>> REFUNDS = new java.util.HashMap<>();

	/** 다시 살아났을 때 돌려줄 아이템 */
	private static final java.util.Map<java.util.UUID, Vec3> RESPAWN_AT = new java.util.HashMap<>();

	/** 보스방·균열에서 죽으면 그 입구(결계 바깥)에서 부활 */
	public static void respawnAt(ServerPlayer dead, Vec3 center, double dist) {
		ServerLevel l = (ServerLevel) dead.level();
		Vec3 d = dead.position().subtract(center).multiply(1, 0, 1);
		d = d.lengthSqr() < 0.01 ? new Vec3(0, 0, 1) : d.normalize();
		for (int k = 0; k < 12; k++) {   // 바닥이 있는 방향을 찾아 돌려 봄
			double a = Math.atan2(d.z, d.x) + k * Math.PI / 6;
			int x = (int) Math.floor(center.x + Math.cos(a) * dist), z = (int) Math.floor(center.z + Math.sin(a) * dist);
			int y = l.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
			if (y > l.getMinY() + 2 && Math.abs(y - center.y) < 30) { RESPAWN_AT.put(dead.getUUID(), new Vec3(x + 0.5, y, z + 0.5)); return; }
		}
		RESPAWN_AT.put(dead.getUUID(), center.add(0, 1, 0));
	}

	public static void refundLater(java.util.UUID who, net.minecraft.world.item.ItemStack s) {
		REFUNDS.computeIfAbsent(who, k -> new ArrayList<>()).add(s.copy());
	}

	public static void giveRefundNow(ServerPlayer p) { giveRefund(p); }

	private static void giveRefund(ServerPlayer p) {
		if (p.isDeadOrDying()) return;
		List<net.minecraft.world.item.ItemStack> list = REFUNDS.remove(p.getUUID());
		if (list == null) return;
		for (net.minecraft.world.item.ItemStack s : list) {
			Component name = s.getHoverName().copy();
			if (!p.getInventory().add(s)) p.spawnAtLocation((ServerLevel) p.level(), s);
			p.sendSystemMessage(Component.literal("✦ 도전에 실패해 ").append(name).append(Component.literal("을(를) 돌려받았습니다.")).withStyle(ChatFormatting.AQUA));
		}
	}

	public static void start(ServerLevel level, ElementalBoss boss, Vec3 center, Arenas.Arena arena) {
		start(level, boss, center, arena, null, net.minecraft.world.item.ItemStack.EMPTY);
	}

	/** summoner: 소환한 플레이어, refund: 실패하면 돌려줄 소환석 */
	public static void start(ServerLevel level, ElementalBoss boss, Vec3 center, Arenas.Arena arena, ServerPlayer summoner, net.minecraft.world.item.ItemStack refund) {
		start(level, boss, center, arena, summoner, refund, false);
	}

	/** instanced: 보스 관문 전용 방 (모두 쓰러지면 천공성에서 부활) */
	public static void start(ServerLevel level, ElementalBoss boss, Vec3 center, Arenas.Arena arena, ServerPlayer summoner, net.minecraft.world.item.ItemStack refund, boolean instanced) {
		BossFight f = new BossFight(level, boss, center, arena);
		f.instanced = instanced;
		f.summoner = summoner == null ? null : summoner.getUUID();
		f.refund = refund == null ? net.minecraft.world.item.ItemStack.EMPTY : refund.copy();
		ACTIVE.add(f);
	}

	private java.util.UUID summoner;
	private net.minecraft.world.item.ItemStack refund = net.minecraft.world.item.ItemStack.EMPTY;
	private boolean aborted;
	private boolean instanced;

	private void abort() {
		aborted = true;
		announce("✖ 도전자가 모두 쓰러져 보스전이 끝났습니다." + (refund.isEmpty() ? "" : " 소환석은 돌려드립니다."), ChatFormatting.RED);
		if (summoner != null && !refund.isEmpty()) REFUNDS.computeIfAbsent(summoner, k -> new ArrayList<>()).add(refund.copy());
		boss.cancelFight();
		Fx.sound(level, c, SoundEvents.BEACON_DEACTIVATE, 2F, 0.8F);
	}

	/** 수정 탑이 하나라도 살아 있으면 보스 보호 */
	public static boolean shielded(ElementalBoss boss) {
		for (BossFight f : ACTIVE) if (f.boss == boss && f.coresAlive() > 0) return true;
		return false;
	}

	private record Hazard(Vec3 pos, double r, int delay, int kind) {}   // kind 0 폭발, 1 빙결, 2 안전지대(어둠의 파동)

	private final ServerLevel level;
	private final ElementalBoss boss;
	private final Vec3 c;
	private final Arenas.Arena arena;
	private final String id;
	private final Element el;
	private final List<Hazard> hazards = new ArrayList<>();
	private final List<int[]> hazardAge = new ArrayList<>();
	private final List<BlockPos> cores = new ArrayList<>();
	private final List<LivingEntity> adds = new ArrayList<>();
	private int age, phase = 0, nextHazard = 120, nextSpecial = 260;
	private final boolean darkPulse, storm, judgement, frost;

	private BossFight(ServerLevel level, ElementalBoss boss, Vec3 c, Arenas.Arena arena) {
		this.level = level; this.boss = boss; this.c = c; this.arena = arena;
		Identifier key = BuiltInRegistries.ENTITY_TYPE.getKey(boss.getType());
		this.id = key.getPath();
		this.el = boss.element();
		darkPulse = id.equals("nox") || id.equals("twilight_twins") || id.equals("prima");
		storm = id.equals("zephyros") || id.equals("storm_wyvern") || id.equals("sphinx") || id.equals("prima");
		judgement = id.equals("lumiel") || id.equals("crystal_golem") || id.equals("prima");
		frost = id.equals("marina") || id.equals("frost_giant");
		if (arena != null) announce("⚔ 보스방 결계가 닫혔습니다! 보스를 쓰러뜨리기 전까지 나갈 수 없습니다.", ChatFormatting.GOLD);
	}

	private List<ServerPlayer> fighters() {
		List<ServerPlayer> out = new ArrayList<>();
		for (ServerPlayer p : level.players()) if (p.isAlive() && !p.isSpectator() && p.position().distanceToSqr(c) < 48 * 48) out.add(p);
		return out;
	}

	private void announce(String msg, ChatFormatting color) {
		for (ServerPlayer p : level.players()) if (p.position().distanceToSqr(c) < 80 * 80) p.sendSystemMessage(Component.literal(msg).withStyle(color, ChatFormatting.BOLD));
	}

	/** true = 전투 끝 */
	private boolean tick() {
		age++;
		if (aborted) return true;
		if (boss.isRemoved() || !boss.isAlive()) {
			if (arena != null) announce("✦ 보스방 결계가 풀렸습니다.", ChatFormatting.GREEN);
			return true;
		}
		List<ServerPlayer> ps = fighters();
		if (ps.isEmpty() && age > 200) return false;  // 아무도 없으면 대기 (보스는 그대로)
		int ph = boss.getPhase();
		if (ph != phase) { phase = ph; onPhase(ph, ps); }

		// 결계
		if (arena != null) {
			if (age % 8 == 0) Fx.ring(level, ElementFx.dust(el, 1.6F), c.add(0, 1.2, 0), Arenas.R + 0.5, 48, age * 0.01);
			for (ServerPlayer p : ps) {
				Vec3 d = p.position().subtract(c).multiply(1, 0, 1);
				if (d.length() > Arenas.R + 1.5 && d.length() < 40) {
					Vec3 in = c.add(d.normalize().scale(Arenas.R - 2));
					p.teleportTo(in.x, c.y + 1, in.z);
					p.sendOverlayMessage(Component.literal("보스방 결계가 길을 막습니다").withStyle(ChatFormatting.GOLD));
				}
			}
			// 보스도 방 안에
			if (boss.position().subtract(c).multiply(1, 0, 1).length() > Arenas.R + 4) boss.teleportTo(c.x, c.y + 3, c.z);
		}

		// 수정 탑: 보스로 이어지는 빛줄기 + 보호막
		int alive = coresAlive();
		if (alive > 0 && age % 4 == 0) {
			for (BlockPos bp : cores) if (isCore(bp)) Fx.line(level, ElementFx.dust(el, 1.1F), Vec3.atCenterOf(bp), boss.position().add(0, boss.getBbHeight() * 0.5, 0), 0.8);
			Fx.sphere(level, ElementFx.dust(el, 1.3F), boss.position().add(0, boss.getBbHeight() * 0.5, 0), boss.getBbWidth() + 1.2, 40);
		}
		for (Iterator<BlockPos> it = cores.iterator(); it.hasNext(); ) {
			BlockPos bp = it.next();
			if (!isCore(bp)) {
				it.remove();
				Fx.burst(level, ElementFx.particle(el), Vec3.atCenterOf(bp), 40, 0.8, 0.2);
				Fx.sound(level, Vec3.atCenterOf(bp), SoundEvents.AMETHYST_BLOCK_BREAK, 1.5F, 0.6F);
				Fx.sound(level, Vec3.atCenterOf(bp), SoundEvents.GENERIC_EXPLODE.value(), 0.6F, 1.4F);
				announce(cores.isEmpty() ? "✦ 수정 탑이 모두 부서졌습니다! 보스의 보호막이 사라졌다!" : "수정 탑 파괴! (남은 탑 " + cores.size() + ")", ChatFormatting.AQUA);
				for (int y = 0; y < 4; y++) { BlockPos q = bp.below(y + 1); if (level.getBlockState(q).is(Blocks.POLISHED_BLACKSTONE_BRICKS)) level.setBlock(q, Blocks.AIR.defaultBlockState(), 3); }
			}
		}

		// 원소 장판
		if (--nextHazard <= 0 && !ps.isEmpty()) {
			int extra = phase >= 3 ? 3 : phase == 2 ? 2 : 1;
			for (ServerPlayer p : ps) hazard(p.position(), 3.2, judgement ? 20 : 34, frost ? 1 : 0);
			for (int i = 0; i < extra; i++) {
				double a = level.getRandom().nextDouble() * Math.PI * 2, r = 4 + level.getRandom().nextDouble() * (Arenas.R - 6);
				hazard(c.add(Math.cos(a) * r, 0, Math.sin(a) * r), 3.6, 40, frost ? 1 : 0);
			}
			nextHazard = phase >= 3 ? 60 : phase == 2 ? 90 : 130;
			Fx.sound(level, c, SoundEvents.BEACON_ACTIVATE, 0.8F, 1.8F);
		}
		// 어둠의 파동: 빛의 원 안으로 숨기
		if (darkPulse && --nextSpecial <= 0 && !ps.isEmpty()) {
			for (int i = 0; i < 3; i++) {
				double a = level.getRandom().nextDouble() * Math.PI * 2, r = 6 + level.getRandom().nextDouble() * 12;
				hazards.add(new Hazard(ground(c.add(Math.cos(a) * r, 0, Math.sin(a) * r)), 3.5, 70, 2)); hazardAge.add(new int[]{0});
			}
			announce("⚠ 어둠의 파동이 몰려옵니다 — 빛의 원 안으로 피하세요!", ChatFormatting.DARK_PURPLE);
			nextSpecial = phase >= 3 ? 220 : 320;
		}
		// 폭풍 고리 (3페이즈): 가장자리는 위험
		if (storm && phase >= 3 && arena != null) {
			if (age % 8 == 0) for (int k = 0; k < 2; k++) Fx.ring(level, com.elementalweapons.fx.P.CLOUD, c.add(0, 0.5 + k * 2, 0), Arenas.R - 3 + k, 36, age * 0.1);
			if (age % 20 == 0) for (ServerPlayer p : ps) if (p.position().subtract(c).multiply(1, 0, 1).length() > Arenas.R - 7) {
				p.hurtServer(level, level.damageSources().indirectMagic(boss, boss), 4.0F);
				Vec3 in = c.subtract(p.position()).multiply(1, 0, 1).normalize().scale(0.8);
				Fx.launch(p, in.x, 0.3, in.z);
				p.sendOverlayMessage(Component.literal("폭풍 고리 — 가운데로!").withStyle(ChatFormatting.AQUA));
			}
		}
		tickHazards(ps);
		adds.removeIf(m -> !m.isAlive());
		return false;
	}

	private void onPhase(int ph, List<ServerPlayer> ps) {
		if (arena != null && (ph == 1 || ph == 3 || id.equals("prima"))) raiseCores(ph);
		if (ph >= 2) {
			int n = ph == 2 ? 3 : 5;
			EntityType<?> t = ModMobs.MOBS.get(mobFor(el));
			if (t != null) for (int i = 0; i < n; i++) {
				double a = i * Math.PI * 2 / n;
				if (t.create(level, EntitySpawnReason.MOB_SUMMONED) instanceof LivingEntity m) {
					Vec3 p = ground(c.add(Math.cos(a) * 9, 0, Math.sin(a) * 9));
					m.snapTo(p.x, p.y + 1, p.z, 0, 0);
					if (m instanceof Mob mob && !ps.isEmpty()) mob.setTarget(ps.get(i % ps.size()));
					level.addFreshEntity(m); adds.add(m);
					Fx.burst(level, ElementFx.particle(el), p.add(0, 1, 0), 20, 0.5, 0.1);
				}
			}
			announce(ph == 2 ? "⚠ 보스가 하수인을 불러냈습니다!" : "⚠ 보스가 격노했습니다! 장판이 더 자주 떨어집니다.", ChatFormatting.RED);
		}
	}

	public static String mobFor(Element e) {
		return switch (e) {
			case DARK -> "shadow_stalker"; case FIRE -> "magma_brute"; case ELECTRIC -> "spark_wisp"; case LIGHT -> "light_sentinel";
			case WIND -> "gale_hawk"; case WATER -> "tide_spirit"; case GRASS -> "thorn_beast";
		};
	}

	private Block coreBlock() {
		return switch (el) {
			case GRASS -> Blocks.VERDANT_FROGLIGHT; case FIRE -> Blocks.SHROOMLIGHT; case LIGHT -> Blocks.OCHRE_FROGLIGHT;
			case DARK -> Blocks.PEARLESCENT_FROGLIGHT; case ELECTRIC -> Blocks.GLOWSTONE; default -> Blocks.SEA_LANTERN;
		};
	}

	private boolean isCore(BlockPos bp) { return level.getBlockState(bp).is(coreBlock()); }

	private int coresAlive() { int n = 0; for (BlockPos bp : cores) if (isCore(bp)) n++; return n; }

	private void raiseCores(int ph) {
		for (BlockPos bp : cores) level.setBlock(bp, Blocks.AIR.defaultBlockState(), 3);
		cores.clear();
		int F = arena.floor();
		for (int k = 0; k < 4; k++) {
			double a = Math.toRadians(k * 90 + 45);
			int x = arena.x() + (int) Math.round(Math.cos(a) * 17), z = arena.z() + (int) Math.round(Math.sin(a) * 17);
			for (int y = 1; y <= 3; y++) level.setBlock(new BlockPos(x, F + y, z), Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState(), 3);
			BlockPos core = new BlockPos(x, F + 4, z);
			level.setBlock(core, coreBlock().defaultBlockState(), 3);
			cores.add(core);
			Fx.burst(level, ElementFx.particle(el), Vec3.atCenterOf(core), 30, 0.6, 0.15);
		}
		Fx.sound(level, c, SoundEvents.BEACON_POWER_SELECT, 1.5F, 0.6F);
		announce("⚠ 원소 수정 탑 4개가 솟아올랐습니다! 수정이 살아 있는 동안 보스는 거의 피해를 받지 않습니다 — 탑 꼭대기 수정을 부수세요!", ChatFormatting.AQUA);
	}

	private Vec3 ground(Vec3 p) {
		BlockPos bp = BlockPos.containing(p.x, c.y + 6, p.z);
		for (int i = 0; i < 16 && level.getBlockState(bp).isAir(); i++) bp = bp.below();
		return new Vec3(p.x, bp.getY() + 1.05, p.z);
	}

	private void hazard(Vec3 pos, double r, int delay, int kind) {
		hazards.add(new Hazard(ground(pos), r, delay, kind));
		hazardAge.add(new int[]{0});
	}

	private void tickHazards(List<ServerPlayer> ps) {
		for (int i = hazards.size() - 1; i >= 0; i--) {
			Hazard h = hazards.get(i);
			int a = ++hazardAge.get(i)[0];
			double prog = a / (double) h.delay;
			if (h.kind == 2) {
				// 안전지대: 빛 기둥 원
				if (a % 3 == 0) { Fx.ring(level, Fx.dust(0xFFF2B0, 1.6F), h.pos, h.r, 36, a * 0.1); Fx.at(level, com.elementalweapons.fx.P.END_ROD, h.pos.add(0, (a % 30) / 10.0, 0)); }
				if (a >= h.delay) {
					for (ServerPlayer p : ps) {
						boolean safe = false;
						for (Hazard o : hazards) if (o.kind == 2 && o.pos.distanceToSqr(p.position().x, o.pos.y, p.position().z) < o.r * o.r) safe = true;
						if (!safe) {
							p.hurtServer(level, level.damageSources().indirectMagic(boss, boss), 14.0F);
							p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0));
							p.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1));
						}
					}
					Fx.burst(level, Fx.dust(0x2A0A3A, 2.5F), c.add(0, 1.5, 0), 300, Arenas.R * 0.5, 0.05);
					Fx.sound(level, c, SoundEvents.WARDEN_SONIC_BOOM, 1.2F, 0.7F);
				}
			} else {
				// 예고: 바깥 원 + 안쪽으로 차오르는 원
				if (a % 2 == 0) {
					Fx.ring(level, Fx.dust(0xFF3030, 1.1F), h.pos, h.r, 28, 0);
					Fx.ring(level, ElementFx.dust(el, 1.2F), h.pos, h.r * prog, 20, a * 0.2);
				}
				if (a >= h.delay) {
					ElementFx.impact(level, el, h.pos, h.r);
					for (ServerPlayer p : ps) if (p.position().distanceToSqr(h.pos) < h.r * h.r + 1) {
						p.hurtServer(level, level.damageSources().indirectMagic(boss, boss), 6.0F + 3.0F * phase);
						if (h.kind == 1) p.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 2));
					}
				}
			}
			if (a >= h.delay) { hazards.remove(i); hazardAge.remove(i); }
		}
	}

	private void end() {
		for (BlockPos bp : cores) {
			level.setBlock(bp, Blocks.AIR.defaultBlockState(), 3);
			for (int y = 1; y <= 3; y++) { BlockPos q = bp.below(y); if (level.getBlockState(q).is(Blocks.POLISHED_BLACKSTONE_BRICKS)) level.setBlock(q, Blocks.AIR.defaultBlockState(), 3); }
		}
		cores.clear();
		for (LivingEntity m : adds) if (m.isAlive()) m.discard();
	}
}
