package com.elementalweapons.world.sky;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 보스 전용 방 16개 — 천공성 보스 관문에서 소환석을 쓰면 이곳으로 이동.
 * 세계 먼 곳(x 30000)에 보스마다 하나씩, 처음 쓸 때 지어짐. 방마다 테마·장식이 모두 다름.
 */
public final class BossRooms {
	private BossRooms() {}

	public static final List<String> BOSSES = List.of("silva", "hydra", "marina", "frost_giant", "voltra", "storm_wyvern", "zephyros", "sphinx",
			"ignis", "lava_queen", "steam_colossus", "lumiel", "crystal_golem", "twilight_twins", "nox", "prima");
	public static final String[] NAMES = {"숲의 성소", "독늪의 둥지", "해저 신전", "얼음 왕좌", "폭풍 첨탑", "번개 절벽", "바람의 정원", "사막의 신전",
			"화산 분화구", "용암 궁정", "증기 공장", "빛의 대성당", "수정 동굴", "황혼의 정원", "그림자 성채", "근원의 제단"};
	public static final int R = 24, Y = 150;

	public static BlockPos center(int idx) { return new BlockPos(30000, Y, 30000 + idx * 600); }
	public static BlockPos entrance(int idx) { return center(idx).offset(0, 1, R - 4); }
	public static int index(String boss) { return BOSSES.indexOf(boss); }

	private static BlockState b(String id) { return BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("minecraft", id)).defaultBlockState(); }
	private static int h(int x, int z) { int n = x * 73856093 ^ z * 19349663; n ^= n >>> 13; n *= 0x5bd1e995; return (n ^ n >>> 15) & 0x7fffffff; }

	/** 테마: 바닥 두 가지(무늬), 벽, 벽 장식, 빛, 지붕(null이면 하늘이 열린 방) */
	private record Theme(String f1, String f2, String wall, String accent, String light, String roof) {}

	private static final Theme[] THEMES = {
			new Theme("moss_block", "grass_block", "mossy_stone_bricks", "oak_log", "shroomlight", null),                 // 실바
			new Theme("mud", "mangrove_roots", "mud_bricks", "mangrove_log", "verdant_froglight", null),                   // 히드라
			new Theme("prismarine_bricks", "dark_prismarine", "prismarine", "prismarine_bricks", "sea_lantern", "dark_prismarine"), // 마리나
			new Theme("packed_ice", "blue_ice", "packed_ice", "snow_block", "sea_lantern", null),                         // 서리 거인
			new Theme("oxidized_cut_copper", "weathered_cut_copper", "oxidized_copper", "cut_copper", "end_rod", null),   // 볼트라
			new Theme("deepslate_tiles", "polished_deepslate", "deepslate_bricks", "oxidized_copper", "pearlescent_froglight", null), // 폭풍 와이번
			new Theme("quartz_block", "smooth_quartz", "quartz_pillar", "white_wool", "sea_lantern", null),               // 제피로스
			new Theme("cut_sandstone", "smooth_sandstone", "sandstone", "chiseled_sandstone", "ochre_froglight", null),    // 스핑크스
			new Theme("blackstone", "magma_block", "polished_blackstone_bricks", "basalt", "shroomlight", null),           // 이그니스
			new Theme("nether_bricks", "red_nether_bricks", "nether_bricks", "gilded_blackstone", "shroomlight", "nether_bricks"), // 용암 여왕
			new Theme("iron_block", "polished_andesite", "cut_copper", "iron_block", "redstone_lamp", "smooth_stone"),     // 증기 거인
			new Theme("quartz_bricks", "gold_block", "quartz_bricks", "gold_block", "glowstone", null),                   // 루미엘
			new Theme("amethyst_block", "calcite", "amethyst_block", "calcite", "ochre_froglight", "amethyst_block"),      // 수정 골렘
			new Theme("quartz_block", "blackstone", "purpur_block", "obsidian", "pearlescent_froglight", null),           // 황혼 쌍둥이
			new Theme("deepslate_bricks", "polished_blackstone", "deepslate_tiles", "crying_obsidian", "soul_lantern", "deepslate_tiles"), // 녹스
			new Theme("obsidian", "end_stone_bricks", "obsidian", "crying_obsidian", "beacon", null)};                    // 프리마

	/** 방 짓기 (서버 스레드, 처음 한 번) */
	public static void build(ServerLevel l, int idx) {
		BlockPos c = center(idx);
		for (int cx = (c.getX() - R - 8) >> 4; cx <= (c.getX() + R + 8) >> 4; cx++)
			for (int cz = (c.getZ() - R - 8) >> 4; cz <= (c.getZ() + R + 8) >> 4; cz++) l.getChunk(cx, cz);
		Theme t = THEMES[idx];
		BlockState f1 = b(t.f1), f2 = b(t.f2), wall = b(t.wall), acc = b(t.accent), light = b(t.light), air = b("air");
		int H = 12;
		for (int x = -R - 1; x <= R + 1; x++)
			for (int z = -R - 1; z <= R + 1; z++) {
				double r = Math.sqrt(x * x + z * z);
				if (r > R + 0.5) continue;
				// 바닥 (아래로 갈수록 좁아지는 섬 모양)
				int depth = 2 + (int) Math.round((R - r) * 0.35);
				for (int y = -depth; y <= 0; y++) set(l, c, x, y, z, y == 0 ? floor(idx, x, z, r, f1, f2, light) : wall);
				for (int y = 1; y <= H + 6; y++) set(l, c, x, y, z, air);
				// 벽 고리
				if (r > R - 1.2) {
					boolean gate = z > R - 3 && Math.abs(x) <= 2;   // 남쪽 입구 자리(장식만, 막혀 있음)
					for (int y = 1; y <= H; y++) {
						boolean window = !gate && y >= 4 && y <= 7 && (h(x, z) % 9 == 0 || (Math.abs(Math.atan2(z, x)) * 8 / Math.PI) % 1 < 0.18);
						set(l, c, x, y, z, window && t.roof == null ? air : gate && y <= 4 ? acc : wall);
					}
					set(l, c, x, H + 1, z, acc);
				}
				// 지붕 (돔)
				if (t.roof != null) {
					int ry = H + 1 + (int) Math.round(Math.sqrt(Math.max(0, R * R - r * r)) * 0.25);
					set(l, c, x, ry, z, h(x, z) % 23 == 0 ? light : b(t.roof));
				}
			}
		// 기둥 8개
		for (int k = 0; k < 8; k++) {
			double a = k * Math.PI / 4 + Math.PI / 8;
			int px = (int) Math.round(Math.cos(a) * (R - 4)), pz = (int) Math.round(Math.sin(a) * (R - 4));
			for (int y = 1; y <= H - 2; y++) for (int dx = 0; dx <= 1; dx++) for (int dz = 0; dz <= 1; dz++) set(l, c, px + dx, y, pz + dz, y % 4 == 0 ? acc : wall);
			set(l, c, px, H - 1, pz, light);
		}
		// 가운데 제단
		for (int x = -3; x <= 3; x++) for (int z = -3; z <= 3; z++) if (x * x + z * z <= 10) set(l, c, x, 0, z, acc);
		set(l, c, 0, 0, 0, light);
		features(l, c, idx);
		// 입구 표시
		set(l, c, 0, 0, R - 4, light);
	}

	private static BlockState floor(int idx, int x, int z, double r, BlockState f1, BlockState f2, BlockState light) {
		int ring = (int) (r / 4);
		boolean alt = switch (idx % 4) {
			case 0 -> ring % 2 == 0;                          // 동심원
			case 1 -> ((x >> 2) + (z >> 2)) % 2 == 0;          // 체크
			case 2 -> (int) ((Math.atan2(z, x) + Math.PI) * 6 / Math.PI) % 2 == 0;   // 부채꼴
			default -> h(x, z) % 3 == 0;                       // 얼룩
		};
		if (Math.abs(r - 12) < 0.5 && h(x, z) % 4 == 0) return light;
		return alt ? f1 : f2;
	}

	private static void set(ServerLevel l, BlockPos c, int x, int y, int z, BlockState s) {
		l.setBlock(c.offset(x, y, z), s, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
	}

	/** 보스마다 다른 장식 */
	private static void features(ServerLevel l, BlockPos c, int idx) {
		switch (BOSSES.get(idx)) {
			case "silva" -> {   // 큰 나무 네 그루 + 꽃
				for (int k = 0; k < 4; k++) tree(l, c, (int) (Math.cos(k * Math.PI / 2 + 0.6) * 15), (int) (Math.sin(k * Math.PI / 2 + 0.6) * 15), "oak_log", "azalea_leaves", 9);
				scatter(l, c, 60, "flowering_azalea", 1);
			}
			case "hydra" -> {   // 독 웅덩이 + 맹그로브
				pools(l, c, "water", 5, 3);
				for (int k = 0; k < 3; k++) tree(l, c, (int) (Math.cos(k * 2.1) * 16), (int) (Math.sin(k * 2.1) * 16), "mangrove_log", "mangrove_leaves", 7);
				scatter(l, c, 40, "moss_carpet", 1);
			}
			case "marina" -> {   // 물이 흐르는 고리 + 산호 블록
				ring(l, c, R - 3, 0, "water");
				scatter(l, c, 30, "tube_coral_block", 1); scatter(l, c, 30, "brain_coral_block", 1);
			}
			case "frost_giant" -> {   // 얼음 가시 + 눈
				for (int k = 0; k < 10; k++) spike(l, c, (int) (Math.cos(k * 0.63) * (10 + k % 3 * 4)), (int) (Math.sin(k * 0.63) * (10 + k % 3 * 4)), "packed_ice", 4 + k % 4);
				scatter(l, c, 80, "snow", 1);
			}
			case "voltra" -> {   // 피뢰침 기둥 + 구리 격자
				for (int k = 0; k < 8; k++) { int x = (int) (Math.cos(k * Math.PI / 4) * 14), z = (int) (Math.sin(k * Math.PI / 4) * 14); column(l, c, x, z, "copper_block", 4); set(l, c, x, 5, z, b("lightning_rod")); }
				scatter(l, c, 30, "end_rod", 1);
			}
			case "storm_wyvern" -> {   // 떠 있는 구름 덩어리
				for (int k = 0; k < 9; k++) blob(l, c, (int) (Math.cos(k * 0.7) * 14), 9 + k % 3, (int) (Math.sin(k * 0.7) * 14), "white_wool", 2);
				for (int k = 0; k < 6; k++) column(l, c, (int) (Math.cos(k * 1.05) * 9), (int) (Math.sin(k * 1.05) * 9), "oxidized_copper", 3);
			}
			case "zephyros" -> {   // 흰 아치 + 깃발 기둥
				for (int k = 0; k < 6; k++) arch(l, c, k * Math.PI / 3, 15, "quartz_pillar");
				scatter(l, c, 20, "white_carpet", 1);
			}
			case "sphinx" -> {   // 모서리 피라미드 4개 + 금
				for (int k = 0; k < 4; k++) pyramid(l, c, (int) (Math.cos(k * Math.PI / 2 + Math.PI / 4) * 14), (int) (Math.sin(k * Math.PI / 2 + Math.PI / 4) * 14), "sandstone", 4);
				scatter(l, c, 10, "gold_block", 0);
			}
			case "ignis" -> {   // 용암 해자 고리 + 현무암 기둥
				ring(l, c, 17, 0, "lava"); ring(l, c, 16, 0, "lava");
				for (int k = 0; k < 7; k++) column(l, c, (int) (Math.cos(k * 0.9) * 8), (int) (Math.sin(k * 0.9) * 8), "basalt", 2 + k % 3);
			}
			case "lava_queen" -> {   // 용암 폭포 + 왕좌
				for (int k = 0; k < 6; k++) { int x = (int) (Math.cos(k * 1.05) * (R - 2)), z = (int) (Math.sin(k * 1.05) * (R - 2)); set(l, c, x, 10, z, b("lava")); }
				column(l, c, 0, -8, "gilded_blackstone", 5);
				scatter(l, c, 20, "crimson_roots", 1);
			}
			case "steam_colossus" -> {   // 연기 나는 모닥불 + 사슬 + 파이프
				for (int k = 0; k < 8; k++) set(l, c, (int) (Math.cos(k * 0.785) * 18), 1, (int) (Math.sin(k * 0.785) * 18), b("campfire"));
				for (int k = 0; k < 6; k++) { int x = (int) (Math.cos(k * 1.05) * 11), z = (int) (Math.sin(k * 1.05) * 11); for (int y = 6; y <= 12; y++) set(l, c, x, y, z, b("iron_chain")); }
				for (int k = 0; k < 4; k++) column(l, c, (int) (Math.cos(k * 1.57 + 0.4) * 15), (int) (Math.sin(k * 1.57 + 0.4) * 15), "iron_bars", 8);
			}
			case "lumiel" -> {   // 황금 기둥 + 빛 창문
				for (int k = 0; k < 6; k++) column(l, c, (int) (Math.cos(k * 1.05) * 12), (int) (Math.sin(k * 1.05) * 12), "gold_block", 6);
				scatter(l, c, 16, "glowstone", 0);
			}
			case "crystal_golem" -> {   // 자수정 덩어리
				for (int k = 0; k < 12; k++) spike(l, c, (int) (Math.cos(k * 0.52) * (8 + k % 4 * 3)), (int) (Math.sin(k * 0.52) * (8 + k % 4 * 3)), "amethyst_block", 2 + k % 5);
				scatter(l, c, 40, "amethyst_cluster", 1);
			}
			case "twilight_twins" -> {   // 반은 빛, 반은 어둠
				for (int x = -R; x <= R; x++) for (int z = -R; z <= R; z++) if (x * x + z * z < (R - 2) * (R - 2) && x < 0 && (x + z) % 3 == 0) set(l, c, x, 0, z, b("black_concrete"));
				column(l, c, -10, 0, "obsidian", 6); column(l, c, 10, 0, "quartz_pillar", 6);
				set(l, c, -10, 7, 0, b("soul_lantern")); set(l, c, 10, 7, 0, b("lantern"));
			}
			case "nox" -> {   // 영혼 불 + 우는 흑요석 기둥
				for (int k = 0; k < 10; k++) { int x = (int) (Math.cos(k * 0.63) * 15), z = (int) (Math.sin(k * 0.63) * 15); set(l, c, x, 0, z, b("soul_soil")); set(l, c, x, 1, z, b("soul_fire")); }
				for (int k = 0; k < 4; k++) column(l, c, (int) (Math.cos(k * 1.57) * 9), (int) (Math.sin(k * 1.57) * 9), "crying_obsidian", 5);
			}
			case "prima" -> {   // 일곱 속성 기둥 고리
				String[] cols = {"yellow_stained_glass", "red_stained_glass", "blue_stained_glass", "lime_stained_glass", "light_gray_stained_glass", "purple_stained_glass", "white_stained_glass"};
				for (int k = 0; k < 7; k++) { int x = (int) (Math.cos(k * Math.PI * 2 / 7) * 15), z = (int) (Math.sin(k * Math.PI * 2 / 7) * 15); column(l, c, x, z, cols[k], 7); set(l, c, x, 8, z, b("sea_lantern")); }
				ring(l, c, 6, 0, "crying_obsidian");
			}
			default -> {}
		}
	}

	private static void column(ServerLevel l, BlockPos c, int x, int z, String id, int h) { for (int y = 1; y <= h; y++) set(l, c, x, y, z, b(id)); }
	private static void spike(ServerLevel l, BlockPos c, int x, int z, String id, int h) {
		for (int y = 1; y <= h; y++) { int w = (h - y) / 3; for (int dx = -w; dx <= w; dx++) for (int dz = -w; dz <= w; dz++) set(l, c, x + dx, y, z + dz, b(id)); }
	}
	private static void tree(ServerLevel l, BlockPos c, int x, int z, String log, String leaves, int h) {
		for (int y = 1; y <= h; y++) set(l, c, x, y, z, b(log));
		for (int dx = -3; dx <= 3; dx++) for (int dy = -2; dy <= 2; dy++) for (int dz = -3; dz <= 3; dz++)
			if (dx * dx + dy * dy * 2 + dz * dz <= 12 && !(dx == 0 && dz == 0 && dy < 1)) set(l, c, x + dx, h + dy, z + dz, b(leaves).trySetValue(net.minecraft.world.level.block.LeavesBlock.PERSISTENT, true));
	}
	private static void blob(ServerLevel l, BlockPos c, int x, int y, int z, String id, int r) {
		for (int dx = -r; dx <= r; dx++) for (int dy = -1; dy <= 1; dy++) for (int dz = -r; dz <= r; dz++) if (dx * dx + dy * dy * 3 + dz * dz <= r * r + 1) set(l, c, x + dx, y + dy, z + dz, b(id));
	}
	private static void pyramid(ServerLevel l, BlockPos c, int x, int z, String id, int h) {
		for (int y = 0; y < h; y++) { int w = h - 1 - y; for (int dx = -w; dx <= w; dx++) for (int dz = -w; dz <= w; dz++) set(l, c, x + dx, y + 1, z + dz, b(id)); }
	}
	private static void arch(ServerLevel l, BlockPos c, double a, int r, String id) {
		int x = (int) Math.round(Math.cos(a) * r), z = (int) Math.round(Math.sin(a) * r);
		int px = (int) Math.round(-Math.sin(a) * 2), pz = (int) Math.round(Math.cos(a) * 2);
		column(l, c, x + px, z + pz, id, 6); column(l, c, x - px, z - pz, id, 6);
		for (int t = -2; t <= 2; t++) set(l, c, x + (int) Math.round(-Math.sin(a) * t), 7, z + (int) Math.round(Math.cos(a) * t), b(id));
	}
	private static void ring(ServerLevel l, BlockPos c, double r, int y, String id) {
		for (double a = 0; a < Math.PI * 2; a += 0.5 / r) set(l, c, (int) Math.round(Math.cos(a) * r), y, (int) Math.round(Math.sin(a) * r), b(id));
	}
	private static void pools(ServerLevel l, BlockPos c, String id, int n, int r) {
		for (int k = 0; k < n; k++) {
			int px = (int) (Math.cos(k * 1.3 + 0.5) * 13), pz = (int) (Math.sin(k * 1.3 + 0.5) * 13);
			for (int dx = -r; dx <= r; dx++) for (int dz = -r; dz <= r; dz++) if (dx * dx + dz * dz <= r * r) set(l, c, px + dx, 0, pz + dz, b(id));
		}
	}
	private static void scatter(ServerLevel l, BlockPos c, int n, String id, int y) {
		for (int k = 0; k < n; k++) {
			int hh = h(k * 31 + 7, k * 17 + 3);
			double a = (hh % 6283) / 1000.0, r = 5 + (hh / 6283 % 1000) / 1000.0 * (R - 8);
			int x = (int) Math.round(Math.cos(a) * r), z = (int) Math.round(Math.sin(a) * r);
			if (l.getBlockState(c.offset(x, y, z)).isAir() || y == 0) set(l, c, x, y, z, b(id));
		}
	}
}
