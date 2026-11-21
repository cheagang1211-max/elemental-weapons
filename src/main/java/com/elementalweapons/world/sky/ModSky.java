package com.elementalweapons.world.sky;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.growth.ModGrowth;
import com.elementalweapons.spell.Fx;

/** 공중 세계 등록 + 공허 추락 보호 + 수도 스폰 */
public final class ModSky {
	private ModSky() {}

	private static final Map<UUID, Vec3> SAFE = new HashMap<>();

	public static void initialize() {
		SkyMobSpawner.initialize();
		try { ElementalWeaponsMod.LOGGER.info("[SKY] 배치 준비: 섬 {}개 다리 {}개", SkyLayout.ISLANDS.size(), SkyLayout.BRIDGES.size()); }
		catch (Throwable t) { ElementalWeaponsMod.LOGGER.error("[SKY] 배치 오류", t); }
		Registry.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, ElementalWeaponsMod.id("sky_islands"), SkyDensity.CODEC);
		Registry.register(BuiltInRegistries.BIOME_SOURCE, ElementalWeaponsMod.id("sky_regions"), SkyBiomeSource.CODEC);
		Registry.register(BuiltInRegistries.FEATURE_TYPE, ElementalWeaponsMod.id("sky_painter"), SkyPainter.CODEC);

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			ServerLevel ow = server.overworld();
			if (!active(ow)) return;
			ow.setRespawnData(LevelData.RespawnData.of(Level.OVERWORLD, SkyLayout.SPAWN, 0F, 0F));
			// 죽어도 아이템 유지 (인벤세이브) — 게임 규칙 이름이 버전마다 달라 두 가지 다 시도
			var src = server.createCommandSourceStack().withSuppressedOutput();
			try { server.getCommands().performPrefixedCommand(src, "gamerule keep_inventory true"); } catch (Throwable ignored) {}
			try { server.getCommands().performPrefixedCommand(src, "gamerule keepInventory true"); } catch (Throwable ignored) {}
			if (ow.getAttached(ModGrowth.SAGE_POS) == null) ow.setAttached(ModGrowth.SAGE_POS, Capital.SAGE_POS);
			ElementalWeaponsMod.LOGGER.info("공중 세계: 섬 {}개, 다리 {}개", SkyLayout.ISLANDS.size(), SkyLayout.BRIDGES.size());
		});
		// 낙하·공허 피해 없음
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (entity instanceof ServerPlayer p && active((ServerLevel) p.level())
					&& (source.is(DamageTypes.FALL) || source.is(DamageTypes.FELL_OUT_OF_WORLD))) return false;
			return true;
		});
		// 공허로 떨어지면 마지막으로 서 있던 곳으로 복귀
		// 층마다 가게 주인이 늘 자리에 있도록 + 용의 둥지마다 천공룡
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 100 != 50) return;
			ServerLevel ow = server.overworld();
			if (!active(ow)) return;
			try {
				sweepStrays(ow);
				for (Capital.Npc n : Capital.NPCS) keepNpc(ow, n);
				for (net.minecraft.core.BlockPos pad : Capital.DRAGON_PADS) keepDragon(ow, pad);
				for (net.minecraft.core.BlockPos pad : Outposts.PADS) keepDragon(ow, pad);
			} catch (Throwable t) { ElementalWeaponsMod.LOGGER.error("[SKY] 주민 유지 오류", t); }
		});
		// 가운데 탑 승강기: 빛 기둥 안에서 위로 떠오름, Shift를 누르면 천천히 내려감
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ServerLevel ow = server.overworld();
			if (!active(ow)) return;
			for (ServerPlayer p : ow.players()) lift(p);
		});
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ServerLevel ow = server.overworld();
			if (!active(ow)) return;
			for (ServerPlayer p : ow.players()) {
				if (p.getY() < 10) {
					Vec3 back = SAFE.getOrDefault(p.getUUID(), Vec3.atBottomCenterOf(SkyLayout.SPAWN));
					p.teleportTo(back.x, back.y + 0.5, back.z);
					p.setDeltaMovement(Vec3.ZERO);
					p.resetFallDistance();
					p.sendOverlayMessage(Component.literal("공허의 바람이 당신을 되돌려 놓았습니다").withStyle(ChatFormatting.AQUA));
					Fx.sound(ow, p.position(), SoundEvents.ENDERMAN_TELEPORT, 0.8F, 1.3F);
				} else if (server.getTickCount() % 10 == 0 && p.onGround() && p.getY() > 60) {
					SAFE.put(p.getUUID(), p.position());
				}
			}
		});
	}

	private static final Map<UUID, Integer> PAD_WAIT = new HashMap<>();

	private static void lift(ServerPlayer p) {
		double r2 = p.getX() * p.getX() + p.getZ() * p.getZ();
		if (r2 > (Capital.SHAFT_R + 0.3) * (Capital.SHAFT_R + 0.3) || p.getY() < Capital.FY[0] || p.getY() > Capital.TOP + 1 || p.isPassenger() || p.isSpectator()) {
			PAD_WAIT.remove(p.getUUID());
			return;
		}
		var lev = net.minecraft.world.effect.MobEffects.LEVITATION;
		int floor = Capital.floorAt(p.getY());
		if (p.isShiftKeyDown()) {
			p.removeEffect(lev);
			if (!p.onGround()) {   // 일정한 속도로 내려감 (층당 약 3초)
				Vec3 v = p.getDeltaMovement();
				p.setDeltaMovement(v.x * 0.5, -0.55, v.z * 0.5);
				p.syncVelocity = true;
				p.resetFallDistance();
			}
			if (p.tickCount % 10 == 0) p.sendOverlayMessage(Component.literal("▼ 내려가는 중 · " + Capital.FLOOR_NAME[floor] + "  (원하는 층에서 Shift를 떼고 옆으로)").withStyle(ChatFormatting.AQUA));
			return;
		}
		// 1층 바닥(빛 발판)에 서 있으면 1초 기다린 뒤 출발 (내려온 사람이 바로 다시 뜨지 않게)
		if (p.onGround() && p.getY() < Capital.FY[0] + 1.5) {
			int w = PAD_WAIT.merge(p.getUUID(), 1, Integer::sum);
			if (w < 20) { if (w == 1) p.sendOverlayMessage(Component.literal("✦ 천공의 승강기 — 잠시 뒤 위로 떠오릅니다 (Shift: 그대로 머물기)").withStyle(ChatFormatting.AQUA)); return; }
		}
		p.addEffect(new net.minecraft.world.effect.MobEffectInstance(lev, 6, 6, false, false));
		p.resetFallDistance();
		if (p.tickCount % 10 == 0) p.sendOverlayMessage(Component.literal("▲ 올라가는 중 · " + Capital.FLOOR_NAME[floor] + "  (원하는 층에서 옆으로 나가세요 · Shift: 내려가기)").withStyle(ChatFormatting.AQUA));
	}

	private static net.minecraft.world.entity.EntityType<? extends com.elementalweapons.mob.ElementalMerchant> npcType(String t) {
		return switch (t) {
			case "blacksmith" -> com.elementalweapons.mob.ModMobs.BLACKSMITH;
			case "enchanter" -> com.elementalweapons.mob.ModMobs.ENCHANTER;
			case "alchemist" -> com.elementalweapons.mob.ModMobs.ALCHEMIST;
			default -> com.elementalweapons.mob.ModMobs.MERCHANT;
		};
	}

	/** 몸(2칸)이 들어갈 빈자리: 원래 자리에서 위로 최대 4칸 */
	private static net.minecraft.core.BlockPos freeSpot(ServerLevel ow, net.minecraft.core.BlockPos pos) {
		for (int dy = 0; dy <= 4; dy++) {
			var b = pos.above(dy);
			if (ow.getBlockState(b).getCollisionShape(ow, b).isEmpty() && ow.getBlockState(b.above()).getCollisionShape(ow, b.above()).isEmpty()) return b;
		}
		return pos;
	}

	/** 예전 세계·예전 버전이 남긴 떠돌이 가게 주인 정리 (천공성 안) */
	private static void sweepStrays(ServerLevel ow) {
		if (ow.getNearestPlayer(0, 220, 0, 200, false) == null) return;
		var box = new net.minecraft.world.phys.AABB(-140, 120, -140, 140, 320, 140);
		for (var m : ow.getEntitiesOfClass(com.elementalweapons.mob.ElementalMerchant.class, box, m -> m.post() == null)) m.discard();
	}

	private static void keepNpc(ServerLevel ow, Capital.Npc n) {
		net.minecraft.core.BlockPos pos = n.pos();
		if (!ow.isLoaded(pos) || ow.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 96, false) == null) return;
		var type = npcType(n.type());
		var mine = ow.getEntities(type, new net.minecraft.world.phys.AABB(pos).inflate(8), e -> pos.equals(e.post()));
		for (int i = 1; i < mine.size(); i++) mine.get(i).discard();   // 중복은 하나만 남김
		if (!mine.isEmpty()) return;
		var m = type.create(ow, net.minecraft.world.entity.EntitySpawnReason.STRUCTURE);
		if (m == null) return;
		var at = freeSpot(ow, pos);
		m.snapTo(at.getX() + 0.5, at.getY(), at.getZ() + 0.5, (float) Math.toDegrees(Math.atan2(pos.getX(), -pos.getZ())), 0);
		m.setPost(at);
		ow.addFreshEntity(m);
	}

	private static void keepDragon(ServerLevel ow, net.minecraft.core.BlockPos pad) {
		if (!ow.isLoaded(pad) || ow.getNearestPlayer(pad.getX(), pad.getY(), pad.getZ(), 96, false) == null) return;
		var all = ow.getEntities(com.elementalweapons.mob.ModMobs.SKY_DRAGON, new net.minecraft.world.phys.AABB(pad).inflate(96), d -> pad.equals(d.home()));
		boolean kept = false;
		for (var d : all) {   // 둥지 근처에 쉬고 있는 용은 하나만
			if (d.isVehicle() || d.distanceToSqr(pad.getX() + 0.5, pad.getY(), pad.getZ() + 0.5) > 64) { kept = true; continue; }
			if (kept) d.discard(); else kept = true;
		}
		if (!all.isEmpty()) return;
		var d = com.elementalweapons.mob.ModMobs.SKY_DRAGON.create(ow, net.minecraft.world.entity.EntitySpawnReason.STRUCTURE);
		if (d == null) return;
		double yaw = Math.toDegrees(Math.atan2(-pad.getX(), pad.getZ()));
		d.snapTo(pad.getX() + 0.5, pad.getY(), pad.getZ() + 0.5, (float) yaw, 0);
		d.setHome(pad);
		ow.addFreshEntity(d);
	}

	private static Boolean cached;

	public static boolean active(ServerLevel level) {
		if (level.dimension() != Level.OVERWORLD) return false;
		if (cached == null) cached = level.getChunkSource().getGenerator().getBiomeSource() instanceof SkyBiomeSource;
		return cached;
	}

	public static void reset() { cached = null; }
}
