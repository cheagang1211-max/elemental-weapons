package com.elementalweapons;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import com.elementalweapons.boss.ElementalBoss;
import com.elementalweapons.boss.ModEntities;

/**
 * 자동 테스트 (GitHub Actions 전용). 환경변수 EW_SELFTEST=1 일 때만 동작.
 * 서버에 보스와 표적을 소환해 30초간 전투시키고, 페이즈 2/3 전환과 사망 보상까지 실행해 봅니다.
 * 실제 게임에서는 아무 일도 하지 않습니다.
 */
public final class DevSelfTest {
	private DevSelfTest() {}

	private static int tick = -1;
	private static ElementalBoss boss;
	private static LivingEntity dummy;
	private static final java.util.Set<String> PATTERNS = new java.util.TreeSet<>();

	public static void initialize() {
		if (!"1".equals(System.getenv("EW_SELFTEST"))) return;
		ElementalWeaponsMod.LOGGER.info("[SELFTEST] 활성화");
		ServerLifecycleEvents.SERVER_STARTED.register(server -> tick = 0);
		ServerTickEvents.END_SERVER_TICK.register(DevSelfTest::onTick);
	}

	private static void onTick(MinecraftServer server) {
		if (tick < 0) return;
		ServerLevel level = server.overworld();
		try {
			if (tick == 5) {
				// 플레이어 없이도 몹이 움직이도록 주변 7x7 청크 강제 로딩
				for (int cx = -3; cx <= 3; cx++) for (int cz = -3; cz <= 3; cz++) level.setChunkForced(cx, cz, true);
			}
			if (tick == 20) {
				int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, 0, 0);
				boss = ModEntities.VOLTRA.create(level, EntitySpawnReason.COMMAND);
				boss.snapTo(0.5, y + 4, 0.5);
				level.addFreshEntity(boss);
				dummy = EntityTypes.HUSK.create(level, EntitySpawnReason.COMMAND);
				dummy.snapTo(8.5, y, 0.5);
				dummy.setPermanentlyInvulnerable(true);
				level.addFreshEntity(dummy);
				boss.debugForcedTarget = dummy;
				ElementalWeaponsMod.LOGGER.info("[SELFTEST] 보스 소환: 체력 {}", boss.getBigHealth());
			}
			if (boss != null && tick > 20 && tick < 620 && boss.isAlive()) {
				String st = boss.debugState;
				int i = st.indexOf("pattern=");
				if (i >= 0) { String pn = st.substring(i + 8, st.indexOf(' ', i)); if (!pn.equals("-")) PATTERNS.add(pn); }
				if (tick % 100 == 0) {
					ElementalWeaponsMod.LOGGER.info("[SELFTEST] t={} 체력={} 페이즈={} 자세={} 위치={} 보스틱={} AI={} 상태={} 표적={}",
							tick, boss.getBigHealth(), boss.getPhase(), boss.getAttackPose(), boss.position(),
							boss.tickCount, boss.debugAiSteps, boss.debugState, dummy.position());
				}
			}
			if (tick == 220) boss.debugDamage(level, boss.getBigHealth() / 2);           // → 2페이즈
			if (tick == 420) boss.debugDamage(level, boss.getBigHealth() * 6 / 10);      // → 3페이즈
			if (tick == 620) {
				boss.debugDamage(level, Long.MAX_VALUE / 4);                             // 처치
				ElementalWeaponsMod.LOGGER.info("[SELFTEST] 처치 시도: 살아있음={}", boss.isAlive());
			}
			if (tick == 680) {
				int drops = level.getEntitiesOfClass(ItemEntity.class, new AABB(-40, -64, -40, 40, 400, 40)).size();
				ElementalWeaponsMod.LOGGER.info("[SELFTEST] 드롭 아이템 엔티티 수={}", drops);
				ElementalWeaponsMod.LOGGER.info("[SELFTEST] 실행된 패턴: {}", PATTERNS);
				ElementalWeaponsMod.LOGGER.info(drops > 0 ? "[SELFTEST] PASS" : "[SELFTEST] FAIL (드롭 없음)");
				tick = -2;
			}
		} catch (Exception e) {
			ElementalWeaponsMod.LOGGER.error("[SELFTEST] FAIL 예외", e);
			tick = -2;
		}
		if (tick >= 0) tick++;
	}
}
