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
		var slow = net.minecraft.world.effect.MobEffects.SLOW_FALLING;
		int floor = Capital.floorAt(p.getY());
		if (p.isShiftKeyDown()) {
			p.removeEffect(lev);
			p.addEffect(new net.minecraft.world.effect.MobEffectInstance(slow, 10, 0, false, false));
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

	private static void keepNpc(ServerLevel ow, Capital.Npc n) {
		net.minecraft.core.BlockPos pos = n.pos();
		if (!ow.isLoaded(pos) || ow.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 96, false) == null) return;
		net.minecraft.world.entity.EntityType<? extends com.elementalweapons.mob.ElementalMerchant> type = switch (n.type()) {
			case "blacksmith" -> com.elementalweapons.mob.ModMobs.BLACKSMITH;
			case "enchanter" -> com.elementalweapons.mob.ModMobs.ENCHANTER;
			case "alchemist" -> com.elementalweapons.mob.ModMobs.ALCHEMIST;
			default -> com.elementalweapons.mob.ModMobs.MERCHANT;
		};
		var near = ow.getEntities(type, new net.minecraft.world.phys.AABB(pos).inflate(2.5, 2, 2.5), e -> true);
		if (!near.isEmpty()) return;
		var m = type.create(ow, net.minecraft.world.entity.EntitySpawnReason.STRUCTURE);
		if (m == null) return;
		m.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 180, 0);
		m.setPersistenceRequired();
		ow.addFreshEntity(m);
	}

	private static void keepDragon(ServerLevel ow, net.minecraft.core.BlockPos pad) {
		if (!ow.isLoaded(pad) || ow.getNearestPlayer(pad.getX(), pad.getY(), pad.getZ(), 96, false) == null) return;
		var all = ow.getEntities(com.elementalweapons.mob.ModMobs.SKY_DRAGON, new net.minecraft.world.phys.AABB(pad).inflate(96), d -> pad.equals(d.home()));
		if (!all.isEmpty()) return;
		var d = com.elementalweapons.mob.ModMobs.SKY_DRAGON.create(ow, net.minecraft.world.entity.EntitySpawnReason.STRUCTURE);
		if (d == null) return;
		double yaw = Math.toDegrees(Math.atan2(-(pad.getX() - 0.0), pad.getZ() - 0.0));
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
