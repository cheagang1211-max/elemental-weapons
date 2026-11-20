package com.elementalweapons.world.sky;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.mob.ElementalCreature;
import com.elementalweapons.mob.ElementalMob;
import com.elementalweapons.mob.ModMobs;

/**
 * 공중 세계 전용 몹 스폰.
 * 바닐라 자연 스폰은 "어두운 곳 + 높이를 무작위로 골라 땅 찾기" 방식이라, 높이 떠 있는 섬 위에서는 거의 실패함.
 * 그래서 플레이어 주변 섬 땅 위에 지역 속성에 맞는 몹을 직접 스폰 (수도는 안전 지대라 제외).
 */
public final class SkyMobSpawner {
	private SkyMobSpawner() {}

	private static final String[] REGION_MOB = {"thorn_beast", "tide_spirit", "spark_wisp", "gale_hawk", "magma_brute", "light_sentinel", "shadow_stalker"};

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 40 != 7) return;
			ServerLevel level = server.overworld();
			if (!ModSky.active(level)) return;
			try {
				for (ServerPlayer p : level.players()) if (!p.isSpectator()) around(level, p);
			} catch (Throwable t) {
				ElementalWeaponsMod.LOGGER.error("[SKY] 몹 스폰 오류", t);
			}
		});
	}

	private static void around(ServerLevel level, ServerPlayer p) {
		AABB box = p.getBoundingBox().inflate(64, 48, 64);
		boolean night = level.isDarkOutside();
		int hostile = level.getEntitiesOfClass(ElementalMob.class, box).size();
		int cap = night ? 12 : 7;
		RandomSource r = level.getRandom();
		for (int i = 0; i < 3 && hostile < cap; i++) {
			EntityType<?> type = pick(level, p, r, true);
			if (type != null && trySpawn(level, p, r, type)) hostile++;
		}
		if (!night && level.getEntitiesOfClass(ElementalCreature.class, box).size() < 4 && r.nextInt(3) == 0) {
			trySpawn(level, p, r, r.nextBoolean() ? ModMobs.FLUFFLE : ModMobs.HORNED_DEER);
		}
	}

	private static EntityType<?> pick(ServerLevel level, ServerPlayer p, RandomSource r, boolean hostile) {
		int region = SkyLayout.regionAt(p.getX(), p.getZ());
		if (region < 0) return null;
		if (region >= 7) {
			List<EntityType<?>> all = new ArrayList<>(ModMobs.MOBS.values());
			return all.get(r.nextInt(all.size()));
		}
		// 지역 몹 위주, 가끔 다른 속성도
		String id = r.nextInt(5) == 0 ? REGION_MOB[r.nextInt(7)] : REGION_MOB[region];
		return ModMobs.MOBS.get(id);
	}

	private static boolean trySpawn(ServerLevel level, ServerPlayer p, RandomSource r, EntityType<?> type) {
		double a = r.nextDouble() * Math.PI * 2, d = 18 + r.nextDouble() * 28;
		int x = (int) Math.floor(p.getX() + Math.cos(a) * d), z = (int) Math.floor(p.getZ() + Math.sin(a) * d);
		if (SkyLayout.regionAt(x, z) < 0) return false;                         // 수도 = 안전 지대
		if (!level.hasChunkAt(new BlockPos(x, 0, z))) return false;
		int ground = SkyLayout.surfaceTop(x, z);
		if (ground < 40) return false;                                            // 허공
		int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
		if (Math.abs(y - (ground + 1)) > 6) return false;                         // 섬 땅 위만 (다리·건물 지붕 제외)
		BlockPos pos = new BlockPos(x, y, z);
		if (!level.getBlockState(pos.below()).isSolid() || !level.isEmptyBlock(pos) || !level.isEmptyBlock(pos.above())) return false;
		if (Arenas.near(x, z, Arenas.R + 8) != null) return false;                // 보스방 안 제외
		if (!(type.create(level, EntitySpawnReason.NATURAL) instanceof Mob m)) return false;
		m.snapTo(x + 0.5, y, z + 0.5, r.nextFloat() * 360, 0);
		if (!level.noCollision(m)) return false;
		level.addFreshEntity(m);
		return true;
	}
}
