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
			if (tick == 15) checkWorld(level);
			if (tick == 16) checkMobs(level);
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

	/** 지형 생성 확인: 스폰 주변 블록 중 우리 모드 블록 개수 */
	private static void checkWorld(ServerLevel level) {
		java.util.Map<String, Integer> counts = new java.util.TreeMap<>();
		net.minecraft.core.BlockPos.MutableBlockPos pos = new net.minecraft.core.BlockPos.MutableBlockPos();
		for (int x = -64; x < 64; x++) for (int z = -64; z < 64; z++) for (int y = -64; y < 200; y++) {
			pos.set(x, y, z);
			net.minecraft.world.level.block.Block b = level.getBlockState(pos).getBlock();
			net.minecraft.resources.Identifier id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(b);
			if (id.getNamespace().equals(ElementalWeaponsMod.MOD_ID)) counts.merge(id.getPath(), 1, Integer::sum);
		}
		String biome = level.getBiome(new net.minecraft.core.BlockPos(0, 64, 0)).getRegisteredName();
		boolean ok = counts.getOrDefault("aether_stone", 0) > 100000 && counts.keySet().stream().anyMatch(k -> k.endsWith("_ore"));
		if (!ok) failures++;
		ElementalWeaponsMod.LOGGER.info("[SELFTEST] {} WORLD 스폰 바이옴={} 블록={}", ok ? "OK  " : "FAIL", biome, counts);
	}

	/** 일반 몹/동물/상인: 소환 → 몇 틱 진행 → 상인 거래 목록 확인 */
	private static void checkMobs(ServerLevel level) {
		int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, 20, 20);
		List<EntityType<? extends net.minecraft.world.entity.Mob>> types = new ArrayList<>(com.elementalweapons.mob.ModMobs.MOBS.values());
		types.add(com.elementalweapons.mob.ModMobs.FLUFFLE);
		types.add(com.elementalweapons.mob.ModMobs.HORNED_DEER);
		int ok = 0;
		for (EntityType<? extends net.minecraft.world.entity.Mob> t : types) {
			net.minecraft.world.entity.Mob m = t.create(level, EntitySpawnReason.COMMAND);
			m.snapTo(20.5, y + 1, 20.5);
			level.addFreshEntity(m);
			for (int i = 0; i < 40; i++) m.tick();
			m.kill(level);
			ok++;
		}
		com.elementalweapons.mob.ElementalMerchant merchant = com.elementalweapons.mob.ModMobs.MERCHANT.create(level, EntitySpawnReason.COMMAND);
		merchant.snapTo(22.5, y + 1, 22.5);
		level.addFreshEntity(merchant);
		int offers = merchant.getOffers().size();
		merchant.discard();
		boolean pass = ok == types.size() && offers >= 10;
		if (!pass) failures++;
		ElementalWeaponsMod.LOGGER.info("[SELFTEST] {} MOBS 몹 {}종 소환/진행/처치, 상인 거래 {}개", pass ? "OK  " : "FAIL", ok, offers);
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
