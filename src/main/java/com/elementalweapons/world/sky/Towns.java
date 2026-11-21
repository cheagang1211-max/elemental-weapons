package com.elementalweapons.world.sky;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;

import static com.elementalweapons.world.sky.Outposts.st;

/**
 * 원소 지역 중심 도시 (지역 본섬마다 하나, 손으로 설계한 배치).
 * 광장 + 원소 상징물 · 둥근 길 · 큰길 4개 · 성벽과 성문 · 지역풍 집 6채 · 상인과 대장장이 가게.
 */
final class Towns {
	private Towns() {}

	static final int R = 40;
	private static final String[] NAME = {"엘프 수목 마을", "호수 마을", "피뢰 요새", "바람의 언덕 마을", "용광로 요새", "빛의 신전 도시", "망령 마을", "심연의 전초기지"};
	private static final String[] GROUND = {"blossom_grass", "azure_grass", "storm_grass", "gale_grass", "ember_soil", "radiant_grass", "shadow_grass", "abyss_stone"};
	// 포장석, 포장석2, 성벽돌, 성벽 담, 빛
	private static final String[][] STONE = {
			{"mossy_stone_bricks", "stone_bricks", "mossy_stone_bricks", "mossy_stone_brick_wall", "lantern"},
			{"prismarine_bricks", "stone_bricks", "stone_bricks", "stone_brick_wall", "sea_lantern"},
			{"deepslate_tiles", "polished_deepslate", "deepslate_bricks", "deepslate_brick_wall", "lantern"},
			{"calcite", "smooth_stone", "calcite", "diorite_wall", "lantern"},
			{"polished_blackstone_bricks", "blackstone", "polished_blackstone_bricks", "polished_blackstone_brick_wall", "lantern"},
			{"smooth_sandstone", "cut_sandstone", "smooth_sandstone", "sandstone_wall", "lantern"},
			{"polished_blackstone", "deepslate_tiles", "deepslate_bricks", "deepslate_brick_wall", "soul_lantern"},
			{"end_stone_bricks", "purpur_block", "end_stone_bricks", "end_stone_brick_wall", "soul_lantern"}};

	private static BlockState mod(String id) { return BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("elementalweapons", id)).defaultBlockState(); }
	private static int h(int x, int y, int z) { int n = x * 73856093 ^ y * 19349663 ^ z * 83492791; n ^= n >>> 13; n *= 0x5bd1e995; return (n ^ n >>> 15) & 0x7fffffff; }

	/** 집 터 8곳 (둥근 길 바깥, 큰길 사이). 0,4번은 가게 */
	static int[] lot(int cx, int cz, int i) {
		double a = Math.toRadians(22.5 + i * 45);
		return new int[]{(int) Math.round(cx + Math.cos(a) * 27), (int) Math.round(cz + Math.sin(a) * 27)};
	}

	static int facing(int lx, int lz, int cx, int cz) {   // 문이 광장 쪽
		double dx = cx - lx, dz = cz - lz;
		if (Math.abs(dx) > Math.abs(dz)) return dx > 0 ? 3 : 1;
		return dz > 0 ? 0 : 2;
	}

	/** 가게 주인 자리 */
	static void npcs(Outposts.Poi o, List<Capital.Npc> out) {
		int[] a = lot(o.x(), o.z(), 0), b = lot(o.x(), o.z(), 4);
		out.add(new Capital.Npc("merchant", new BlockPos(a[0], o.y() + 1, a[1])));
		out.add(new Capital.Npc("blacksmith", new BlockPos(b[0], o.y() + 1, b[1])));
	}

	static void paint(Outposts.W w, Outposts.Poi o, String[] pal) {
		int rg = Math.min(7, o.region()), cx = o.x(), cz = o.z(), Y = o.y();
		String[] S = STONE[rg];
		BlockState ground = mod(GROUND[rg]);
		if (ground.isAir()) ground = st("grass_block");
		// 1) 땅 고르기 + 바닥 무늬
		for (int x = cx - R - 1; x <= cx + R + 1; x++) for (int z = cz - R - 1; z <= cz + R + 1; z++) {
			if (!w.in(x, z)) continue;
			double r = Math.hypot(x - cx, z - cz);
			if (r > R + 0.5) continue;
			int k = h(x, 1, z) % 10;
			boolean avenue = Math.abs(x - cx) <= 2 || Math.abs(z - cz) <= 2;
			BlockState top;
			if (r <= 11) top = ((int) r % 4 == 0) ? st("chiseled_stone_bricks") : (k < 7 ? st(S[0]) : st(S[1]));
			else if (Math.abs(r - 15) <= 1.2 || (avenue && r < R - 1)) top = k < 6 ? st(S[0]) : k < 8 ? st(S[1]) : st("cobblestone");
			else top = ground;
			// 지형이 높으면 깎고, 낮으면 채움
			for (int y = Y + 1; y <= Y + 24; y++) if (!w.air(x, y, z)) w.set(x, y, z, st("air"));
			w.set(x, Y, z, top);
			for (int y = Y - 1; y >= Y - 14 && (w.air(x, y, z) || y >= Y - 2); y--) w.set(x, y, z, y >= Y - 2 ? mod("elemental_dirt").isAir() ? st("dirt") : mod("elemental_dirt") : st(S[2]));
			// 성벽 (반지름 37~39) + 문 4개
			if (r > 37 && r <= 39.5 && !avenue) {
				for (int y = Y + 1; y <= Y + 4; y++) w.set(x, y, z, y == Y + 4 ? st(S[3]) : (h(x, y, z) % 7 == 0 ? st("cracked_stone_bricks") : st(S[2])));
			}
		}
		// 성문 기둥 + 등불, 큰길 가로등, 둥근 길 화로
		for (int q = 0; q < 4; q++) {
			int dx = new int[]{1, 0, -1, 0}[q], dz = new int[]{0, 1, 0, -1}[q];
			for (int s = -1; s <= 1; s += 2) {
				int gx = cx + dx * 38 + dz * 3 * s, gz = cz + dz * 38 + dx * 3 * s;
				for (int y = 1; y <= 6; y++) w.set(gx, Y + y, gz, st(S[2]));
				w.set(gx, Y + 7, gz, st(S[4]));
				for (int d = 18; d <= 34; d += 8) lamp(w, cx + dx * d + dz * 3 * s, Y, cz + dz * d + dx * 3 * s, S);
			}
			for (int k = -2; k <= 2; k++) w.set(cx + dx * 38 + dz * k, Y + 6, cz + dz * 38 + dx * k, st(S[2]));
		}
		for (int k = 0; k < 8; k++) {
			double a = Math.toRadians(k * 45 + 22.5);
			int x = (int) Math.round(cx + Math.cos(a) * 12.5), z = (int) Math.round(cz + Math.sin(a) * 12.5);
			w.set(x, Y + 1, z, st("chiseled_stone_bricks")); w.set(x, Y + 2, z, rg == 6 || rg == 7 ? st("soul_campfire") : st("campfire"));
		}
		// 2) 집 6채 + 가게 2곳
		for (int i = 0; i < 8; i++) {
			int[] l = lot(cx, cz, i);
			int f = facing(l[0], l[1], cx, cz);
			if (i == 0 || i == 4) shop(w, l[0], Y, l[1], f, pal, i == 0 ? "상인" : "대장장이", i == 4);
			else Outposts.cottage(w, l[0], Y, l[1], f, pal, o.seed() + i);
		}
		// 3) 남은 자리: 지역 나무 · 꽃밭 · 우물 · 벤치
		for (int k = 0; k < 16; k++) {
			double a = Math.toRadians(k * 22.5 + 11), rr = 33 + (k % 2) * 1.5;
			int x = (int) Math.round(cx + Math.cos(a) * rr), z = (int) Math.round(cz + Math.sin(a) * rr);
			if (Math.abs(x - cx) <= 4 || Math.abs(z - cz) <= 4) continue;
			if (k % 3 == 0) tree(w, x, Y, z, pal, rg);
			else if (k % 3 == 1) flowerBed(w, x, Y, z, rg);
		}
		// 4) 원소 상징물
		switch (rg) {
			case 0 -> greatTree(w, cx, Y, cz);
			case 1 -> lakeFountain(w, cx, Y, cz);
			case 2 -> lightningSpire(w, cx, Y, cz);
			case 3 -> windBellTower(w, cx, Y, cz);
			case 4 -> forgeBrazier(w, cx, Y, cz);
			case 5 -> lightTemple(w, cx, Y, cz);
			default -> soulObelisk(w, cx, Y, cz, rg == 7);
		}
		// 5) 성문 앞 안내판
		w.sign(cx + 4, Y + 1, cz + 36, 0, NAME[rg], "원소의 도시", "상인 · 대장장이");
	}

	private static void lamp(Outposts.W w, int x, int Y, int z, String[] S) {
		for (int y = 1; y <= 3; y++) w.set(x, Y + y, z, st("polished_blackstone_wall"));
		w.set(x, Y + 4, z, st(S[4]));
	}

	/** 가게: 차양 + 계산대 + 상품 (대장장이는 모루·화로) */
	private static void shop(Outposts.W w, int cx, int Y, int cz, int facing, String[] pal, String who, boolean smith) {
		java.util.function.IntBinaryOperator X = (lx, lz) -> switch (facing) { case 1 -> cx - lz; case 2 -> cx - lx; case 3 -> cx + lz; default -> cx + lx; };
		java.util.function.IntBinaryOperator Z = (lx, lz) -> switch (facing) { case 1 -> cz + lx; case 2 -> cz - lz; case 3 -> cz - lx; default -> cz + lz; };
		for (int lx = -4; lx <= 4; lx++) for (int lz = -4; lz <= 4; lz++) {
			int x = X.applyAsInt(lx, lz), z = Z.applyAsInt(lx, lz);
			w.set(x, Y, z, st(pal[1] + "_planks"));
			for (int y = 1; y <= 6; y++) w.set(x, Y + y, z, st("air"));
			if (Math.abs(lx) == 4 && Math.abs(lz) == 4) for (int y = 1; y <= 3; y++) w.set(x, Y + y, z, st(Outposts.log(pal[0])));
			w.set(x, Y + 4, z, st((((lx + 4) / 2) % 2 == 0 ? pal[4] : "white") + "_wool"));
			if (lz == 2 && Math.abs(lx) <= 3 && lx != 0) w.set(x, Y + 1, z, st(pal[1] + "_planks"));
			if (lz == -3 && Math.abs(lx) <= 3) w.set(x, Y + 1, z, st("barrel"));
		}
		if (smith) {
			w.set(X.applyAsInt(-2, -3), Y + 1, Z.applyAsInt(-2, -3), st("anvil"));
			w.set(X.applyAsInt(2, -3), Y + 1, Z.applyAsInt(2, -3), st("blast_furnace"));
			w.set(X.applyAsInt(0, -3), Y + 1, Z.applyAsInt(0, -3), st("smithing_table"));
			w.set(X.applyAsInt(3, -3), Y + 1, Z.applyAsInt(3, -3), st("lava_cauldron"));
		} else {
			w.set(X.applyAsInt(-2, 2), Y + 2, Z.applyAsInt(-2, 2), st("potted_red_tulip"));
			w.set(X.applyAsInt(2, 2), Y + 2, Z.applyAsInt(2, 2), st("composter"));
		}
		w.set(X.applyAsInt(0, 0), Y + 3, Z.applyAsInt(0, 0), st("lantern").setValue(BlockStateProperties.HANGING, true));
		int sx = X.applyAsInt(3, 4), sz = Z.applyAsInt(3, 4);
		w.set(sx, Y + 1, sz, st(pal[0] + "_fence"));
		w.sign(sx, Y + 2, sz, Math.floorMod(new int[]{0, 4, 8, 12}[facing], 16), who, smith ? "강화 재료 · 갑옷" : "원소 상인");
	}

	private static void tree(Outposts.W w, int x, int Y, int z, String[] pal, int rg) {
		String[] leaves = {"oak_leaves", "azalea_leaves", "birch_leaves", "birch_leaves", "dark_oak_leaves", "flowering_azalea_leaves", "dark_oak_leaves", "dark_oak_leaves"};
		BlockState leaf = st(leaves[rg]).setValue(BlockStateProperties.PERSISTENT, true);
		int H = 5 + h(x, 0, z) % 2;
		for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) for (int dy = -2; dy <= 2; dy++)
			if (dx * dx + dz * dz + dy * dy * 1.4 <= 6.5 - (h(x + dx, dy, z + dz) % 3) * 0.6) w.set(x + dx, Y + H + dy, z + dz, leaf);
		for (int y = 1; y <= H; y++) w.set(x, Y + y, z, st(Outposts.log(pal[0])));
	}

	private static void flowerBed(Outposts.W w, int x, int Y, int z, int rg) {
		String[][] fl = {{"poppy", "allium", "oxeye_daisy"}, {"blue_orchid", "azure_bluet", "cornflower"}, {"dandelion", "cornflower", "azure_bluet"}, {"white_tulip", "azure_bluet", "oxeye_daisy"},
				{"orange_tulip", "red_tulip", "dandelion"}, {"dandelion", "sunflower", "oxeye_daisy"}, {"wither_rose", "allium", "lily_of_the_valley"}, {"allium", "lily_of_the_valley", "azure_bluet"}};
		for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
			w.set(x + dx, Y, z + dz, st("grass_block"));
			String f = fl[rg][h(x + dx, 2, z + dz) % 3];
			if (!f.equals("sunflower")) w.set(x + dx, Y + 1, z + dz, st(f));
		}
		for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++)
			if (Math.max(Math.abs(dx), Math.abs(dz)) == 2) w.set(x + dx, Y + 1, z + dz, st("spruce_slab"));
	}

	// ---------------- 원소 상징물 ----------------

	/** 꽃의 숲: 거대한 나무 + 뿌리 + 매달린 등불 + 둘레 벤치 */
	private static void greatTree(Outposts.W w, int cx, int Y, int cz) {
		BlockState log = st("oak_log"), wood = st("oak_wood");
		for (int dx = -9; dx <= 9; dx++) for (int dz = -9; dz <= 9; dz++) {
			double r = Math.hypot(dx, dz);
			int x = cx + dx, z = cz + dz;
			if (r <= 2.6) for (int y = 1; y <= 22; y++) w.set(x, Y + y, z, r > 1.6 ? wood : log);
			// 뿌리
			if (r > 2.6 && r < 6 && (dx == 0 || dz == 0 || Math.abs(dx) == Math.abs(dz))) { w.set(x, Y + 1, z, wood); if (r < 4) w.set(x, Y + 2, z, wood); }
			if (Math.abs(r - 8) < 0.6 && h(x, 3, z) % 3 == 0) w.set(x, Y + 1, z, st("spruce_stairs").setValue(BlockStateProperties.HORIZONTAL_FACING, dx > 0 == (Math.abs(dx) > Math.abs(dz)) ? (Math.abs(dx) > Math.abs(dz) ? Direction.EAST : Direction.SOUTH) : (Math.abs(dx) > Math.abs(dz) ? Direction.WEST : Direction.NORTH)));
		}
		// 가지 + 잎 지붕
		for (int b = 0; b < 6; b++) {
			double a = b * Math.PI / 3;
			for (int k = 3; k <= 8; k++) w.set(cx + (int) Math.round(Math.cos(a) * k), Y + 17 + k / 3, cz + (int) Math.round(Math.sin(a) * k), wood);
		}
		for (int dx = -12; dx <= 12; dx++) for (int dz = -12; dz <= 12; dz++) for (int dy = -4; dy <= 6; dy++) {
			double d = Math.sqrt(dx * dx + dz * dz + dy * dy * 3.2);
			if (d <= 12 - (h(cx + dx, dy, cz + dz) % 4) * 0.5 && w.air(cx + dx, Y + 22 + dy, cz + dz))
				w.set(cx + dx, Y + 22 + dy, cz + dz, (h(cx + dx, dy + 9, cz + dz) % 5 == 0 ? st("flowering_azalea_leaves") : st("oak_leaves")).setValue(BlockStateProperties.PERSISTENT, true));
		}
		for (int q = 0; q < 8; q++) {
			double a = q * Math.PI / 4;
			int x = cx + (int) Math.round(Math.cos(a) * 7), z = cz + (int) Math.round(Math.sin(a) * 7);
			for (int y = Y + 17; y >= Y + 12; y--) w.set(x, y, z, st("iron_chain"));
			w.set(x, Y + 11, z, st("lantern").setValue(BlockStateProperties.HANGING, true));
		}
	}

	/** 푸른 숲: 프리즈머린 테두리 호수 + 물이 흘러내리는 첨탑 */
	private static void lakeFountain(Outposts.W w, int cx, int Y, int cz) {
		for (int dx = -9; dx <= 9; dx++) for (int dz = -9; dz <= 9; dz++) {
			double r = Math.hypot(dx, dz);
			int x = cx + dx, z = cz + dz;
			if (r <= 8) { w.set(x, Y, z, st("water")); w.set(x, Y - 1, z, r < 6 ? st("water") : st("prismarine")); w.set(x, Y - 2, z, st("prismarine_bricks")); if (r > 3 && h(x, 1, z) % 9 == 0) w.set(x, Y + 1, z, st("lily_pad")); }
			else if (r <= 9.3) { w.set(x, Y, z, st("dark_prismarine")); w.set(x, Y + 1, z, st("prismarine_wall")); }
		}
		for (int y = 0; y <= 9; y++) for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
			boolean core = dx == 0 && dz == 0;
			if (core || y < 7) w.set(cx + dx, Y + y, cz + dz, y % 3 == 2 && !core ? st("sea_lantern") : st("prismarine_bricks"));
		}
		w.set(cx, Y + 10, cz, st("water"));
		for (Direction d : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) w.set(cx + d.getStepX() * 2, Y + 7, cz + d.getStepZ() * 2, st("water"));
	}

	/** 뇌운 고원: 심층암 + 구리 첨탑, 꼭대기 피뢰침, 엔드 막대 불빛 */
	private static void lightningSpire(Outposts.W w, int cx, int Y, int cz) {
		for (int dx = -4; dx <= 4; dx++) for (int dz = -4; dz <= 4; dz++) {
			double r = Math.hypot(dx, dz);
			int x = cx + dx, z = cz + dz;
			int top = (int) (24 - r * 4.5);
			for (int y = 1; y <= top; y++) {
				if (r > 4.2) continue;
				BlockState s = y % 6 == 0 ? st("waxed_cut_copper") : y % 6 == 3 && r > 2 ? st("amethyst_block") : st("deepslate_bricks");
				w.set(x, Y + y, z, s);
			}
			if (Math.abs(r - 3.6) < 0.5 && (dx == 0 || dz == 0)) w.set(x, Y + 1 + (int) (24 - r * 4.5), z, st("end_rod"));
		}
		for (int y = 25; y <= 28; y++) w.set(cx, Y + y, cz, y == 28 ? st("lightning_rod") : st("waxed_copper_block"));
		for (int q = 0; q < 4; q++) {
			int x = cx + new int[]{6, 0, -6, 0}[q], z = cz + new int[]{0, 6, 0, -6}[q];
			for (int y = 1; y <= 4; y++) w.set(x, Y + y, z, st("waxed_cut_copper"));
			w.set(x, Y + 5, z, st("lightning_rod"));
		}
	}

	/** 질풍 고원: 하얀 종탑 + 깃발 */
	private static void windBellTower(Outposts.W w, int cx, int Y, int cz) {
		for (int dx = -3; dx <= 3; dx++) for (int dz = -3; dz <= 3; dz++) {
			int x = cx + dx, z = cz + dz;
			boolean edge = Math.abs(dx) == 3 || Math.abs(dz) == 3, corner = Math.abs(dx) == 3 && Math.abs(dz) == 3;
			for (int y = 1; y <= 20; y++) {
				if (y <= 14 && edge) w.set(x, Y + y, z, (dz == 3 && dx == 0 && y <= 3) ? st("air") : (y % 5 == 0 ? st("smooth_stone") : st("calcite")));
				else if (y > 14 && corner) w.set(x, Y + y, z, st("birch_log"));
				else if (y == 15) w.set(x, Y + y, z, st("birch_planks"));
			}
			int m = Math.max(Math.abs(dx), Math.abs(dz));
			w.set(x, Y + 21 + (3 - m), z, st("waxed_cut_copper_slab"));
		}
		w.set(cx, Y + 19, cz, st("bell").setValue(BlockStateProperties.BELL_ATTACHMENT, net.minecraft.world.level.block.state.properties.BellAttachType.CEILING));
		w.set(cx, Y + 20, cz, st("birch_planks"));
		for (int q = 0; q < 4; q++) {   // 바람에 날리는 깃발
			int dx = new int[]{1, 0, -1, 0}[q], dz = new int[]{0, 1, 0, -1}[q];
			int x = cx + dx * 7, z = cz + dz * 7;
			for (int y = 1; y <= 7; y++) w.set(x, Y + y, z, st("birch_fence"));
			for (int k = 1; k <= 3; k++) for (int y = 5; y <= 6; y++) w.set(x + dz * k, Y + y, z + dx * k, st(k % 2 == 0 ? "white_wool" : "light_blue_wool"));
		}
	}

	/** 화산 평원: 큰 돌 화로(용암) + 흑암석 기둥 + 사슬 */
	private static void forgeBrazier(Outposts.W w, int cx, int Y, int cz) {
		for (int dx = -5; dx <= 5; dx++) for (int dz = -5; dz <= 5; dz++) {
			double r = Math.hypot(dx, dz);
			int x = cx + dx, z = cz + dz;
			if (r <= 2.2) for (int y = 1; y <= 3; y++) w.set(x, Y + y, z, st("polished_blackstone_bricks"));
			if (r <= 4.5 && r > 2.2) { w.set(x, Y + 4, z, st("polished_blackstone_bricks")); w.set(x, Y + 3, z, r < 3.5 ? st("polished_blackstone_bricks") : st("air")); }
			if (r <= 3.6) w.set(x, Y + 5, z, r > 2.8 ? st("polished_blackstone_brick_wall") : st("lava"));
			if (r <= 2.8) w.set(x, Y + 4, z, st("magma_block"));
		}
		for (int q = 0; q < 4; q++) {
			double a = q * Math.PI / 2 + Math.PI / 4;
			int x = cx + (int) Math.round(Math.cos(a) * 8), z = cz + (int) Math.round(Math.sin(a) * 8);
			for (int y = 1; y <= 9; y++) w.set(x, Y + y, z, y == 9 ? st("chiseled_polished_blackstone") : st("polished_basalt"));
			w.set(x, Y + 10, z, st("campfire"));
			w.set(cx + (int) Math.round(Math.cos(a) * 5), Y + 1, cz + (int) Math.round(Math.sin(a) * 5), st("anvil"));
		}
	}

	/** 광휘 평원: 석영 단상 + 금 기둥 4개 + 신호기 빛 기둥 */
	private static void lightTemple(Outposts.W w, int cx, int Y, int cz) {
		for (int dx = -7; dx <= 7; dx++) for (int dz = -7; dz <= 7; dz++) {
			int x = cx + dx, z = cz + dz, m = Math.max(Math.abs(dx), Math.abs(dz));
			if (m <= 7) w.set(x, Y + 1, z, m == 7 ? st("quartz_stairs").setValue(BlockStateProperties.HORIZONTAL_FACING, Math.abs(dx) == 7 ? (dx > 0 ? Direction.WEST : Direction.EAST) : (dz > 0 ? Direction.NORTH : Direction.SOUTH)) : st("smooth_quartz"));
			if (m <= 5) w.set(x, Y + 2, z, m == 5 ? st("chiseled_quartz_block") : st("quartz_bricks"));
		}
		for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) w.set(cx + dx, Y + 2, cz + dz, st("iron_block"));
		w.set(cx, Y + 3, cz, st("beacon"));
		for (int q = 0; q < 4; q++) {
			int x = cx + (q < 2 ? -4 : 4), z = cz + (q % 2 == 0 ? -4 : 4);
			for (int y = 3; y <= 10; y++) w.set(x, Y + y, z, st("quartz_pillar"));
			w.set(x, Y + 11, z, st("gold_block")); w.set(x, Y + 12, z, st("lantern"));
		}
	}

	/** 그림자 숲 · 심연: 흑암석 오벨리스크 + 우는 흑요석 + 영혼 불꽃 */
	private static void soulObelisk(Outposts.W w, int cx, int Y, int cz, boolean abyss) {
		for (int y = 1; y <= 18; y++) {
			int s = y < 4 ? 2 : y < 12 ? 1 : 0;
			for (int dx = -s; dx <= s; dx++) for (int dz = -s; dz <= s; dz++)
				w.set(cx + dx, Y + y, cz + dz, y % 5 == 0 && (dx != 0 || dz != 0 || s == 0) ? st("crying_obsidian") : abyss ? st("purpur_block") : st("polished_blackstone_bricks"));
		}
		w.set(cx, Y + 19, cz, st("soul_campfire"));
		for (int q = 0; q < 8; q++) {
			double a = q * Math.PI / 4;
			int x = cx + (int) Math.round(Math.cos(a) * 6), z = cz + (int) Math.round(Math.sin(a) * 6);
			w.set(x, Y + 1, z, st("polished_blackstone_wall")); w.set(x, Y + 2, z, st("soul_lantern"));
			if (q % 2 == 0) w.set(x, Y, z, st("soul_soil"));
		}
	}
}
