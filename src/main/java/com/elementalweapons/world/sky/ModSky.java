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
		// 시장·대장간 상인이 늘 자리에 있도록
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 200 != 50) return;
			net.minecraft.server.level.ServerLevel ow = server.overworld();
			if (!active(ow)) return;
			for (net.minecraft.core.BlockPos pos : Capital.MERCHANTS) {
				if (!ow.isLoaded(pos)) continue;
				if (!ow.getEntitiesOfClass(com.elementalweapons.mob.ElementalMerchant.class, new net.minecraft.world.phys.AABB(pos).inflate(16)).isEmpty()) continue;
				var m = com.elementalweapons.mob.ModMobs.MERCHANT.create(ow, net.minecraft.world.entity.EntitySpawnReason.STRUCTURE);
				if (m == null) continue;
				m.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
				m.setPersistenceRequired();
				ow.addFreshEntity(m);
			}
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

	private static Boolean cached;

	public static boolean active(ServerLevel level) {
		if (level.dimension() != Level.OVERWORLD) return false;
		if (cached == null) cached = level.getChunkSource().getGenerator().getBiomeSource() instanceof SkyBiomeSource;
		return cached;
	}

	public static void reset() { cached = null; }
}
