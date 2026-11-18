package com.elementalweapons;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import com.elementalweapons.boss.ElementalBoss;
import com.elementalweapons.boss.ModEntities;

/**
 * 자동 테스트 (GitHub Actions 전용). 환경변수 EW_SELFTEST=1 일 때만 동작.
 * 모든 보스를 차례로 소환해 각각 약 10초씩 전투시키고, 페이즈 2/3 전환과 처치·보상까지 확인합니다.
 * 실제 게임에서는 아무 일도 하지 않습니다.
 */
public final class DevSelfTest {
	private DevSelfTest() {}

	private static final int PER_BOSS = 220;
	private static int tick = -1;
	private static final List<EntityType<? extends ElementalBoss>> TYPES = new ArrayList<>();
	private static ElementalBoss boss;
	private static LivingEntity dummy;
	private static final Set<String> patterns = new TreeSet<>();
	private static int failures = 0;

	public static void initialize() {
		if (!"1".equals(System.getenv("EW_SELFTEST"))) return;
		ElementalWeaponsMod.LOGGER.info("[SELFTEST] 활성화");
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			TYPES.add(ModEntities.VOLTRA);
			TYPES.addAll(ModEntities.BOSSES.values());
			tick = 0;
		});
		ServerTickEvents.END_SERVER_TICK.register(DevSelfTest::onTick);
	}

	private static void onTick(MinecraftServer server) {
		if (tick < 0) return;
		ServerLevel level = server.overworld();
		try {
			if (tick == 5) {
				for (int cx = -4; cx <= 4; cx++) for (int cz = -4; cz <= 4; cz++) level.setChunkForced(cx, cz, true);
			}
			int idx = (tick - 20) / PER_BOSS;
			int k = (tick - 20) % PER_BOSS;
			if (tick >= 20 && idx >= TYPES.size()) {
				ElementalWeaponsMod.LOGGER.info(failures == 0 ? "[SELFTEST] PASS (보스 {}종)" : "[SELFTEST] FAIL ({}종 중 실패 " + failures + ")", TYPES.size());
				tick = -2;
				return;
			}
			if (tick >= 20) step(level, TYPES.get(idx), k);
		} catch (Exception e) {
			ElementalWeaponsMod.LOGGER.error("[SELFTEST] FAIL 예외", e);
			failures++;
			tick = -2;
			ElementalWeaponsMod.LOGGER.info("[SELFTEST] FAIL (예외)");
			return;
		}
		tick++;
	}

	private static void step(ServerLevel level, EntityType<? extends ElementalBoss> type, int k) {
		int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, 0, 0);
		if (k == 0) {
			patterns.clear();
			boss = type.create(level, EntitySpawnReason.COMMAND);
			boss.snapTo(0.5, y + 4, 0.5);
			level.addFreshEntity(boss);
			if (dummy == null || !dummy.isAlive()) {
				dummy = EntityTypes.HUSK.create(level, EntitySpawnReason.COMMAND);
				dummy.setPermanentlyInvulnerable(true);
				level.addFreshEntity(dummy);
			}
			dummy.snapTo(8.5, y, 0.5);
			boss.debugForcedTarget = dummy;
		}
		if (boss.isAlive()) {
			String st = boss.debugState;
			int i = st.indexOf("pattern=");
			if (i >= 0) {
				int end = st.indexOf(" rest=", i);
				String pn = st.substring(i + 8, end < 0 ? st.length() : end);
				if (!pn.equals("-")) patterns.add(pn);
			}
		}
		if (k == 70) boss.debugDamage(level, boss.getBigHealth() / 2);
		if (k == 140) boss.debugDamage(level, boss.getBigHealth() * 6 / 10);
		if (k == 200) boss.debugDamage(level, Long.MAX_VALUE / 4);
		if (k == 215) {
			AABB box = new AABB(-60, -64, -60, 60, 400, 60);
			List<ItemEntity> drops = level.getEntitiesOfClass(ItemEntity.class, box);
			boolean ok = !boss.isAlive() && !drops.isEmpty() && boss.debugAiSteps > 100;
			if (!ok) failures++;
			ElementalWeaponsMod.LOGGER.info("[SELFTEST] {} {} | AI={} 페이즈={} 드롭={} 패턴={}", ok ? "OK  " : "FAIL",
					EntityType.getKey(type), boss.debugAiSteps, boss.getPhase(), drops.size(), patterns);
			for (ItemEntity it : drops) it.discard();
			// 소환된 부하 정리
			for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, e -> e != dummy && !(e instanceof net.minecraft.world.entity.player.Player))) e.discard();
		}
	}
}
