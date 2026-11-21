package com.elementalweapons.world.sky;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/**
 * 지역 섬의 중세 풍경: 마을(오두막·우물·모닥불), 야영지(천막), 망루, 옛 유적, 원소 제단,
 * 다리에서 마을까지 이어지는 가로등 길, 그리고 천공성으로 돌아가는 용의 둥지.
 */
public final class Outposts {
	private Outposts() {}

	static final int VILLAGE = 0, CAMP = 1, TOWER = 2, RUIN = 3, SHRINE = 4;
	record Poi(int type, int x, int y, int z, int seed, int region) {}
	record Road(int ax, int az, int bx, int bz) {}
	private record Plan(List<Poi> pois, List<Road> roads) {}

	private static final Map<Integer, Plan> PLANS = new ConcurrentHashMap<>();
	/** 지역마다 용 발판 (천공성으로 돌아가는 용) */
	public static final List<BlockPos> PADS = new ArrayList<>();
	private static final List<int[]> ROOSTS = new ArrayList<>();   // {x, y, z, ux*1000, uz*1000, region}

	private static final String[] REGION_KO = {"꽃의 숲", "푸른 숲", "뇌운 고원", "질풍 고원", "화산 평원", "광휘 평원", "그림자 숲", "원소의 심연"};
	// 나무, 판자, 돌, 지붕 계단, 양털 색
	private static final String[][] PAL = {
			{"oak", "oak", "mossy_cobblestone", "spruce", "red"},
			{"spruce", "spruce", "stone_bricks", "dark_prismarine", "blue"},
			{"birch", "birch", "deepslate_bricks", "deepslate_tile", "yellow"},
			{"birch", "birch", "calcite", "waxed_cut_copper", "white"},
			{"dark_oak", "dark_oak", "polished_blackstone_bricks", "nether_brick", "orange"},
			{"birch", "birch", "smooth_sandstone", "sandstone", "yellow"},
			{"dark_oak", "dark_oak", "polished_blackstone", "blackstone", "purple"},
			{"crimson", "crimson", "end_stone_bricks", "purpur", "magenta"}};
	private static final String[] GLOW = {"verdant_froglight", "sea_lantern", "glowstone", "sea_lantern", "shroomlight", "ochre_froglight", "pearlescent_froglight", "pearlescent_froglight"};
	private static final String[] GLASS = {"lime", "light_blue", "yellow", "white", "orange", "yellow", "purple", "magenta"};

	static {
		for (SkyLayout.Island is : SkyLayout.ISLANDS) {
			if (is.kind != 1 && is.kind != 5) continue;
			double L = Math.hypot(is.x, is.z), ux = -is.x / L, uz = -is.z / L, px = -uz, pz = ux;
			double bx = is.x + ux * is.r * 0.6 + px * 30, bz = is.z + uz * is.r * 0.6 + pz * 30;
			int x = (int) Math.round(bx), z = (int) Math.round(bz);
			int y = SkyLayout.surfaceTop(x, z);
			if (y == Integer.MIN_VALUE) continue;
			int region = SkyLayout.regionAt(x, z);
			ROOSTS.add(new int[]{x, y, z, (int) (ux * 1000), (int) (uz * 1000), region});
			PADS.add(new BlockPos((int) Math.round(x + px * 5), y + 1, (int) Math.round(z + pz * 5)));
			PADS.add(new BlockPos((int) Math.round(x - px * 5), y + 1, (int) Math.round(z - pz * 5)));
		}
	}

	// ---------------- 배치 계획 (섬마다 한 번) ----------------
	private static Plan plan(SkyLayout.Island is) {
		return PLANS.computeIfAbsent(SkyLayout.ISLANDS.indexOf(is), k -> make(is));
	}

	private static Plan make(SkyLayout.Island is) {
		List<Poi> out = new ArrayList<>();
		List<Road> roads = new ArrayList<>();
		boolean main = is.kind != 2;
		int want = main ? 16 : (is.r > 130 ? 4 : 2);
		for (int t = 0; t < want * 8 && out.size() < want; t++) {
			double a = SkyLayout.hash(is.seed, t, 11) * Math.PI * 2, rr = Math.sqrt(SkyLayout.hash(is.seed, t, 12)) * is.r * (main ? 0.7 : 0.6);
			if (main && out.isEmpty()) { a = Math.atan2(-is.z, -is.x) + 0.9; rr = is.r * 0.38; }   // 마을은 천공성 쪽에 가깝게
			int x = (int) Math.round(is.x + Math.cos(a) * rr), z = (int) Math.round(is.z + Math.sin(a) * rr);
			int type = main && out.isEmpty() ? VILLAGE : pick(SkyLayout.hash(is.seed, t, 13));
			int need = type == VILLAGE ? 26 : 10;
			if (!free(x, z, need, out)) continue;
			int y = SkyLayout.surfaceTop(x, z);
			if (y == Integer.MIN_VALUE || y < 60) continue;
			// 경사가 심한 곳은 피함
			int y1 = SkyLayout.surfaceTop(x + need / 2, z), y2 = SkyLayout.surfaceTop(x - need / 2, z), y3 = SkyLayout.surfaceTop(x, z + need / 2), y4 = SkyLayout.surfaceTop(x, z - need / 2);
			if (Math.max(Math.max(Math.abs(y1 - y), Math.abs(y2 - y)), Math.max(Math.abs(y3 - y), Math.abs(y4 - y))) > (type == VILLAGE ? 7 : 5)) continue;
			out.add(new Poi(type, x, y, z, is.seed * 31 + t, Math.max(0, SkyLayout.regionAt(x, z))));
		}
		if (main && !out.isEmpty() && out.get(0).type == VILLAGE) {
			Poi v = out.get(0);
			for (SkyLayout.Bridge b : SkyLayout.BRIDGES) {
				for (int e = 0; e < 2; e++) {
					double ex = e == 0 ? b.ax : b.bx, ez = e == 0 ? b.az : b.bz;
					if (Math.hypot(ex - is.x, ez - is.z) > is.r * 0.85 || b.style == 2 && e == 0) continue;
					roads.add(new Road((int) ex, (int) ez, v.x, v.z));
				}
			}
			for (Arenas.Arena ar : Arenas.all()) if (Math.hypot(ar.x() - is.x, ar.z() - is.z) < is.r) {
				double dx = v.x - ar.x(), dz = v.z - ar.z(), L = Math.hypot(dx, dz);
				roads.add(new Road((int) (ar.x() + dx / L * (Arenas.R + 4)), (int) (ar.z() + dz / L * (Arenas.R + 4)), v.x, v.z));
			}
		}
		return new Plan(out, roads);
	}

	private static int pick(double h) { return h < 0.34 ? CAMP : h < 0.56 ? TOWER : h < 0.8 ? RUIN : SHRINE; }

	private static boolean free(int x, int z, int need, List<Poi> out) {
		if (Arenas.near(x, z, Arenas.R + need + 8) != null) return false;
		for (Poi p : out) if (Math.hypot(p.x - x, p.z - z) < need + (p.type == VILLAGE ? 26 : 12)) return false;
		for (SkyLayout.Bridge b : SkyLayout.BRIDGES) {
			if (Math.min(Math.hypot(b.ax - x, b.az - z), Math.hypot(b.bx - x, b.bz - z)) < need + 12) return false;
			if (SkyLayout.segDist(x, z, b) < need + b.width) return false;
		}
		for (int[] r : ROOSTS) if (Math.hypot(r[0] - x, r[2] - z) < need + 18) return false;
		return true;
	}

	/** 스크린샷용: 지역 본섬 k의 시설 목록 {종류, x, y, z} */
	public static List<int[]> pois(int region) {
		List<int[]> out = new ArrayList<>();
		for (SkyLayout.Island is : SkyLayout.ISLANDS) {
			if (is.kind != 1 || SkyLayout.regionAt(is.x, is.z) != region) continue;
			for (Poi p : plan(is).pois) out.add(new int[]{p.type, p.x, p.y, p.z});
		}
		return out;
	}

	public static int[] roost(int i) { return i < ROOSTS.size() ? ROOSTS.get(i) : null; }

	// ---------------- 그리기 ----------------
	private static final class W {
		final WorldGenLevel lv; final int minX, minZ, maxX, maxZ;
		W(WorldGenLevel lv, int minX, int minZ) { this.lv = lv; this.minX = minX; this.minZ = minZ; this.maxX = minX + 15; this.maxZ = minZ + 15; }
		boolean in(int x, int z) { return x >= minX && x <= maxX && z >= minZ && z <= maxZ; }
		void set(int x, int y, int z, BlockState s) { if (in(x, z) && y > -60 && y < 318) lv.setBlock(new BlockPos(x, y, z), s, 2); }
		BlockState get(int x, int y, int z) { return in(x, z) ? lv.getBlockState(new BlockPos(x, y, z)) : Blocks.AIR.defaultBlockState(); }
		boolean air(int x, int y, int z) { return get(x, y, z).isAir(); }
		void clear(int x, int y, int z, int n) { for (int k = 1; k <= n; k++) if (!air(x, y + k, z)) set(x, y + k, z, st("air")); }
		/** 바닥을 y로 평평하게 (아래 빈 곳은 흙으로 채움) */
		void pad(int x, int y, int z, BlockState top, int clearH) {
			set(x, y, z, top);
			for (int k = 1; k <= 6 && air(x, y - k, z); k++) set(x, y - k, z, st("dirt"));
			clear(x, y, z, clearH);
		}
		void sign(int x, int y, int z, int rot16, String... text) {
			if (!in(x, z)) return;
			set(x, y, z, st("dark_oak_sign").setValue(BlockStateProperties.ROTATION_16, rot16));
			try {
				if (lv.getBlockEntity(new BlockPos(x, y, z)) instanceof SignBlockEntity sb) {
					List<Component> lines = new ArrayList<>();
					for (int i = 0; i < 4; i++) lines.add(i < text.length ? Component.literal(text[i]) : Component.empty());
					sb.setText(new SignText(lines, lines, net.minecraft.world.item.DyeColor.WHITE, true), net.minecraft.world.level.block.entity.SignTextSlot.FRONT);
				}
			} catch (Throwable ignored) {}
		}
	}

	static BlockState st(String id) { return BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("minecraft", id)).defaultBlockState(); }
	private static int h(int x, int y, int z) { int n = x * 73856093 ^ y * 19349663 ^ z * 83492791; n ^= n >>> 13; n *= 0x5bd1e995; return (n ^ n >>> 15) & 0x7fffffff; }
	private static String log(String w) { return w.equals("crimson") ? "crimson_stem" : w + "_log"; }

	public static void paint(WorldGenLevel lv, int minX, int minZ, SkyLayout.Island is) {
		if (minX + 15 < is.x - is.r * 1.2 || minX > is.x + is.r * 1.2 || minZ + 15 < is.z - is.r * 1.2 || minZ > is.z + is.r * 1.2) return;
		Plan p = plan(is);
		W w = new W(lv, minX, minZ);
		for (Road r : p.roads) road(w, r);
		for (Poi o : p.pois) {
			int R = o.type == VILLAGE ? 24 : 9;
			if (o.x + R < minX || o.x - R > w.maxX || o.z + R < minZ || o.z - R > w.maxZ) continue;
			String[] pal = PAL[Math.min(7, o.region)];
			switch (o.type) {
				case VILLAGE -> village(w, o, pal);
				case CAMP -> camp(w, o, pal);
				case TOWER -> tower(w, o, pal);
				case RUIN -> ruin(w, o, pal);
				default -> shrine(w, o);
			}
		}
	}

	// ---------------- 가로등 길 ----------------
	private static void road(W w, Road r) {
		double pad = 6;
		if (Math.max(r.ax, r.bx) + pad < w.minX || Math.min(r.ax, r.bx) - pad > w.maxX || Math.max(r.az, r.bz) + pad < w.minZ || Math.min(r.az, r.bz) - pad > w.maxZ) return;
		double vx = r.bx - r.ax, vz = r.bz - r.az, L = Math.hypot(vx, vz);
		if (L < 4) return;
		double ux = vx / L, uz = vz / L;
		for (int x = w.minX; x <= w.maxX; x++) for (int z = w.minZ; z <= w.maxZ; z++) {
			double px = x + 0.5 - r.ax, pz = z + 0.5 - r.az, s = px * ux + pz * uz;
			if (s < 0 || s > L) continue;
			double d = -px * uz + pz * ux, ad = Math.abs(d);
			if (ad > 3.5) continue;
			int y = SkyLayout.surfaceTop(x, z);
			if (y == Integer.MIN_VALUE) continue;
			if (w.air(x, y, z) || !w.get(x, y, z).getFluidState().isEmpty()) continue;
			if (ad < 1.6) { w.set(x, y, z, h(x, 0, z) % 5 == 0 ? st("coarse_dirt") : h(x, 0, z) % 7 == 0 ? st("cobblestone") : st("dirt_path")); w.clear(x, y, z, 5); }
			// 18칸마다 엇갈려 가로등
			int along = (int) Math.floor(s);
			if (Math.floorMod(along, 18) == 0 && Math.abs(ad - 2.8) < 0.5 && (d > 0) == (Math.floorMod(along / 18, 2) == 0)) {
				w.clear(x, y, z, 5);
				for (int k = 1; k <= 3; k++) w.set(x, y + k, z, st("spruce_fence"));
				w.set(x, y + 4, z, st("lantern"));
			}
		}
	}

	// ---------------- 마을: 우물 + 모닥불 광장, 오두막 4~5채, 가로등, 안내판 ----------------
	private static void village(W w, Poi o, String[] pal) {
		int Y = o.y;
		// 광장
		for (int x = o.x - 9; x <= o.x + 9; x++) for (int z = o.z - 9; z <= o.z + 9; z++) {
			double r = Math.hypot(x - o.x, z - o.z);
			if (r > 9) continue;
			w.pad(x, Y, z, r < 3 ? st("cobblestone") : h(x, 1, z) % 4 == 0 ? st("coarse_dirt") : h(x, 1, z) % 5 == 0 ? st("mossy_cobblestone") : st("dirt_path"), 9);
		}
		// 우물
		for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
			int x = o.x + dx, z = o.z + dz;
			if (dx == 0 && dz == 0) { w.set(x, Y, z, st("water")); w.set(x, Y - 1, z, st("stone_bricks")); }
			else { w.set(x, Y + 1, z, st("cobblestone_wall")); }
			if (Math.abs(dx) == 1 && Math.abs(dz) == 1) { w.set(x, Y + 2, z, st(pal[0] + "_fence")); w.set(x, Y + 3, z, st(pal[0] + "_fence")); }
			w.set(x, Y + 4, z, st(pal[1] + "_slab"));
		}
		w.set(o.x, Y + 3, o.z, st("lantern").setValue(BlockStateProperties.HANGING, true));
		// 모닥불 + 통나무 의자
		int fx = o.x + 5, fz = o.z + 4;
		w.set(fx, Y + 1, fz, st("campfire"));
		w.set(fx + 2, Y + 1, fz, st("stripped_" + log(pal[0])).setValue(BlockStateProperties.AXIS, Direction.Axis.Z));
		w.set(fx - 2, Y + 1, fz, st("stripped_" + log(pal[0])).setValue(BlockStateProperties.AXIS, Direction.Axis.Z));
		w.set(fx, Y + 1, fz + 2, st("stripped_" + log(pal[0])).setValue(BlockStateProperties.AXIS, Direction.Axis.X));
		// 가로등 4개
		for (int q = 0; q < 4; q++) {
			int x = o.x + (q < 2 ? -7 : 7), z = o.z + (q % 2 == 0 ? -7 : 7);
			for (int k = 1; k <= 3; k++) w.set(x, Y + k, z, st("spruce_fence"));
			w.set(x, Y + 4, z, st("lantern"));
		}
		// 안내판
		w.sign(o.x - 4, Y + 1, o.z + 3, 0, REGION_KO[Math.min(7, o.region)], "개척 마을", "모닥불에서 쉬어 가세요");
		// 오두막 (광장 바깥 둘레, 문은 광장 쪽)
		int n = 4 + o.seed % 2;
		for (int i = 0; i < n; i++) {
			double a = i * Math.PI * 2 / n + (o.seed % 7) * 0.3;
			int cx = (int) Math.round(o.x + Math.cos(a) * 15), cz = (int) Math.round(o.z + Math.sin(a) * 15);
			int facing = Math.abs(Math.cos(a)) > Math.abs(Math.sin(a)) ? (Math.cos(a) > 0 ? 3 : 1) : (Math.sin(a) > 0 ? 2 : 0);
			cottage(w, cx, Y, cz, facing, pal, o.seed + i);
		}
	}

	/** 오두막: 돌 기단 + 판자 벽 + 통나무 기둥 + 박공 지붕 + 굴뚝 + 창문 + 화분. facing 0:+z 1:-x 2:-z 3:+x (문 방향) */
	private static void cottage(W w, int cx, int Y, int cz, int facing, String[] pal, int seed) {
		int hw = 3, hd = 3;
		java.util.function.IntBinaryOperator X = (lx, lz) -> switch (facing) { case 1 -> cx - lz; case 2 -> cx - lx; case 3 -> cx + lz; default -> cx + lx; };
		java.util.function.IntBinaryOperator Z = (lx, lz) -> switch (facing) { case 1 -> cz + lx; case 2 -> cz - lz; case 3 -> cz - lx; default -> cz + lz; };
		Direction front = switch (facing) { case 1 -> Direction.WEST; case 2 -> Direction.NORTH; case 3 -> Direction.EAST; default -> Direction.SOUTH; };
		BlockState logS = st(log(pal[0])), plank = st(pal[1] + "_planks"), stone = st(pal[2]);
		BlockState stair = st(pal[3] + "_stairs");
		for (int lx = -hw - 1; lx <= hw + 1; lx++) for (int lz = -hd - 2; lz <= hd + 2; lz++) {
			int x = X.applyAsInt(lx, lz), z = Z.applyAsInt(lx, lz);
			w.pad(x, Y, z, Math.abs(lx) <= hw && Math.abs(lz) <= hd ? plank : st("dirt_path"), 10);
		}
		for (int lx = -hw; lx <= hw; lx++) for (int lz = -hd; lz <= hd; lz++) {
			boolean edge = Math.abs(lx) == hw || Math.abs(lz) == hd, corner = Math.abs(lx) == hw && Math.abs(lz) == hd;
			int x = X.applyAsInt(lx, lz), z = Z.applyAsInt(lx, lz);
			if (!edge) continue;
			for (int y = 1; y <= 4; y++) {
				BlockState s = corner ? logS : y == 1 ? stone : plank;
				boolean door = lz == hd && lx == 0 && y <= 2;
				boolean win = !corner && y == 2 && ((Math.abs(lx) == hw && lz == 0) || (lz == -hd && lx == 0) || (lz == hd && Math.abs(lx) == 2));
				if (door) s = st(pal[0] + "_door").setValue(BlockStateProperties.HORIZONTAL_FACING, front).setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, y == 1 ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER);
				else if (win) s = st("glass_pane");
				w.set(x, Y + y, z, s);
			}
		}
		// 박공 지붕 (문 방향과 나란한 용마루)
		for (int s = 0; s <= hw + 1; s++) for (int lz = -hd - 1; lz <= hd + 1; lz++) {
			int a = -hw - 1 + s, c = hw + 1 - s;
			if (a > c) break;
			Direction da = facing == 0 || facing == 2 ? (facing == 0 ? Direction.EAST : Direction.WEST) : (facing == 1 ? Direction.SOUTH : Direction.NORTH);
			if (a == c) w.set(X.applyAsInt(a, lz), Y + 5 + s, Z.applyAsInt(a, lz), st(pal[3] + "_slab"));
			else {
				w.set(X.applyAsInt(a, lz), Y + 5 + s, Z.applyAsInt(a, lz), stair.setValue(BlockStateProperties.HORIZONTAL_FACING, da));
				w.set(X.applyAsInt(c, lz), Y + 5 + s, Z.applyAsInt(c, lz), stair.setValue(BlockStateProperties.HORIZONTAL_FACING, da.getOpposite()));
			}
			if (s > 0 && Math.abs(lz) == hd) for (int lx = a + 1; lx < c; lx++) w.set(X.applyAsInt(lx, lz), Y + 4 + s, Z.applyAsInt(lx, lz), plank);
		}
		// 굴뚝 + 실내 + 화분 + 앞 등불
		int chx = X.applyAsInt(hw - 1, -hd + 1), chz = Z.applyAsInt(hw - 1, -hd + 1);
		for (int y = Y + 1; y <= Y + 9; y++) w.set(chx, y, chz, st("bricks"));
		w.set(chx, Y + 10, chz, st("campfire"));
		w.set(X.applyAsInt(-hw + 1, -hd + 1), Y + 1, Z.applyAsInt(-hw + 1, -hd + 1), st("crafting_table"));
		w.set(X.applyAsInt(-hw + 1, 0), Y + 1, Z.applyAsInt(-hw + 1, 0), st("barrel"));
		w.set(X.applyAsInt(0, 0), Y + 4, Z.applyAsInt(0, 0), st("lantern").setValue(BlockStateProperties.HANGING, true));
		w.set(X.applyAsInt(2, hd + 1), Y + 1, Z.applyAsInt(2, hd + 1), seed % 2 == 0 ? st("potted_red_tulip") : st("potted_cornflower"));
		w.set(X.applyAsInt(-2, hd + 1), Y + 1, Z.applyAsInt(-2, hd + 1), st("spruce_fence"));
		w.set(X.applyAsInt(-2, hd + 1), Y + 2, Z.applyAsInt(-2, hd + 1), st("lantern"));
	}

	// ---------------- 야영지: 천막 2개 + 모닥불 + 짐 ----------------
	private static void camp(W w, Poi o, String[] pal) {
		int Y = o.y;
		for (int x = o.x - 7; x <= o.x + 7; x++) for (int z = o.z - 7; z <= o.z + 7; z++) if (Math.hypot(x - o.x, z - o.z) <= 7) w.pad(x, Y, z, h(x, 2, z) % 3 == 0 ? st("coarse_dirt") : st("dirt_path"), 6);
		w.set(o.x, Y + 1, o.z, st("campfire"));
		w.set(o.x + 2, Y + 1, o.z, st("stripped_" + log(pal[0])).setValue(BlockStateProperties.AXIS, Direction.Axis.Z));
		w.set(o.x - 2, Y + 1, o.z, st("stripped_" + log(pal[0])).setValue(BlockStateProperties.AXIS, Direction.Axis.Z));
		for (int t = -1; t <= 1; t += 2) {   // 천막 (양털 A자)
			int tz = o.z + t * 4;
			for (int dx = -2; dx <= 2; dx++) for (int dy = 0; dy <= 2; dy++) {
				for (int s = -1; s <= 1; s += 2) {
					int z = tz + s * (2 - dy);
					w.set(o.x + dx, Y + 1 + dy, z, st((dy == 2 ? "white" : pal[4]) + "_wool"));
				}
				if (dy == 2) w.set(o.x + dx, Y + 3, tz, st(pal[4] + "_wool"));
			}
			w.set(o.x - 3, Y + 1, tz, st("spruce_fence")); w.set(o.x + 3, Y + 1, tz, st("spruce_fence"));
			w.set(o.x, Y + 1, tz, st(pal[4] + "_carpet"));
		}
		w.set(o.x + 4, Y + 1, o.z + 1, st("barrel")); w.set(o.x + 4, Y + 1, o.z - 1, st("chest"));
		w.set(o.x - 4, Y + 1, o.z + 1, st("hay_block"));
		for (int k = 1; k <= 4; k++) w.set(o.x - 5, Y + k, o.z - 2, st("spruce_fence"));
		w.set(o.x - 5, Y + 5, o.z - 2, st("lantern"));
	}

	// ---------------- 망루 ----------------
	private static void tower(W w, Poi o, String[] pal) {
		int Y = o.y;
		BlockState logS = st(log(pal[0])), plank = st(pal[1] + "_planks"), stone = st(pal[2]);
		for (int dx = -3; dx <= 3; dx++) for (int dz = -3; dz <= 3; dz++) w.pad(o.x + dx, Y, o.z + dz, stone, 16);
		for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) {
			int x = o.x + dx, z = o.z + dz;
			boolean edge = Math.abs(dx) == 2 || Math.abs(dz) == 2, corner = Math.abs(dx) == 2 && Math.abs(dz) == 2;
			for (int y = 1; y <= 12; y++) {
				if (y <= 4 && edge) w.set(x, Y + y, z, (dz == 2 && dx == 0 && y <= 2) ? st("air") : stone);
				else if (y > 4 && corner) w.set(x, Y + y, z, logS);
				else if (y > 4 && edge && (y == 8)) w.set(x, Y + y, z, st(pal[0] + "_fence"));
			}
			w.set(x, Y + 12, z, plank);
			if (edge) w.set(x, Y + 13, z, corner ? logS : st(pal[0] + "_fence"));
			if (corner) w.set(x, Y + 14, z, st("lantern"));
		}
		for (int y = 1; y <= 11; y++) { w.set(o.x, Y + y, o.z - 1, plank); w.set(o.x, Y + y, o.z, st("ladder").setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)); }
		w.set(o.x, Y + 12, o.z, st("air"));
		for (int dx = -3; dx <= 3; dx++) for (int dz = -3; dz <= 3; dz++) {   // 지붕
			int m = Math.max(Math.abs(dx), Math.abs(dz));
			w.set(o.x + dx, Y + 15 + (3 - m), o.z + dz, st(pal[3] + "_slab"));
		}
		w.set(o.x + 1, Y + 13, o.z + 1, st(pal[4] + "_banner"));
	}

	// ---------------- 옛 유적: 무너진 돌벽 + 아치 + 이끼 ----------------
	private static void ruin(W w, Poi o, String[] pal) {
		int Y = o.y;
		for (int dx = -7; dx <= 7; dx++) for (int dz = -7; dz <= 7; dz++) {
			int x = o.x + dx, z = o.z + dz;
			double r = Math.hypot(dx, dz);
			if (r > 7.4) continue;
			if (h(x, 3, z) % 3 != 0) w.pad(x, Y, z, h(x, 4, z) % 2 == 0 ? st("mossy_cobblestone") : st("cobblestone"), 7);
			boolean wall = Math.max(Math.abs(dx), Math.abs(dz)) == 6;
			if (wall) {
				int hgt = 1 + h(x, 5, z) % 5 - (Math.abs(dx) < 2 || Math.abs(dz) < 2 ? 4 : 0);
				for (int y = 1; y <= hgt; y++) w.set(x, Y + y, z, h(x, y, z) % 3 == 0 ? st("mossy_stone_bricks") : h(x, y, z) % 4 == 0 ? st("cracked_stone_bricks") : st("stone_bricks"));
				if (hgt > 0 && h(x, 6, z) % 4 == 0) w.set(x, Y + hgt + 1, z, st("moss_carpet"));
			}
		}
		for (int y = 1; y <= 6; y++) for (int dx = -2; dx <= 2; dx++) {   // 아치
			double ar = Math.hypot(dx, (y - 1) * 1.1);
			if ((Math.abs(dx) == 2 && y <= 4) || (ar > 2.4 && ar <= 3.4 && y > 3)) w.set(o.x + dx, Y + y, o.z, st("stone_bricks"));
		}
		w.set(o.x, Y + 1, o.z + 3, st("chiseled_stone_bricks")); w.set(o.x, Y + 2, o.z + 3, st("soul_lantern"));
		w.set(o.x - 3, Y + 1, o.z - 3, st("cracked_stone_bricks")); w.set(o.x + 3, Y + 1, o.z - 2, st("mossy_stone_brick_stairs"));
	}

	// ---------------- 원소 제단 ----------------
	private static void shrine(W w, Poi o) {
		int Y = o.y, rg = Math.min(7, o.region);
		for (int dx = -4; dx <= 4; dx++) for (int dz = -4; dz <= 4; dz++) {
			double r = Math.hypot(dx, dz);
			if (r > 4.4) continue;
			w.pad(o.x + dx, Y, o.z + dz, Math.abs(r - 3.6) < 0.6 ? st("chiseled_stone_bricks") : st("polished_andesite"), 9);
		}
		for (int y = 1; y <= 2; y++) w.set(o.x, Y + y, o.z, st("chiseled_stone_bricks"));
		w.set(o.x, Y + 3, o.z, st(GLOW[rg]));
		for (int y = 4; y <= 6; y++) w.set(o.x, Y + y, o.z, st(GLASS[rg] + "_stained_glass"));
		for (int q = 0; q < 4; q++) {
			int x = o.x + (q < 2 ? -3 : 3), z = o.z + (q % 2 == 0 ? -3 : 3);
			w.set(x, Y + 1, z, st("stone_brick_wall")); w.set(x, Y + 2, z, st("lantern"));
		}
	}

	// ---------------- 지역 용의 둥지 ----------------
	public static void roosts(WorldGenLevel lv, int minX, int minZ) {
		W w = new W(lv, minX, minZ);
		for (int[] r : ROOSTS) {
			int cx = r[0], Y = r[1], cz = r[2];
			if (cx + 12 < minX || cx - 12 > w.maxX || cz + 12 < minZ || cz - 12 > w.maxZ) continue;
			for (int x = cx - 11; x <= cx + 11; x++) for (int z = cz - 11; z <= cz + 11; z++) {
				double d = Math.hypot(x - cx, z - cz);
				if (d > 11) continue;
				w.pad(x, Y, z, Math.abs(d - 10.4) < 0.6 ? st("chiseled_stone_bricks") : (int) d % 4 == 0 ? st("polished_andesite") : st("stone_bricks"), 10);
				if (d > 10.5 && h(x, 9, z) % 5 == 0) { for (int k = 1; k <= 3; k++) w.set(x, Y + k, z, st("spruce_fence")); w.set(x, Y + 4, z, st("lantern")); }
			}
			for (BlockPos p : PADS) if (Math.hypot(p.getX() - cx, p.getZ() - cz) < 8) for (int x = p.getX() - 3; x <= p.getX() + 3; x++) for (int z = p.getZ() - 3; z <= p.getZ() + 3; z++) {
				double d = Math.hypot(x - p.getX(), z - p.getZ());
				if (d <= 3.4 && d > 2.5) w.set(x, Y, z, Math.floorMod(x + z, 2) == 0 ? st("pearlescent_froglight") : st("amethyst_block"));
				else if (d <= 2.5) w.set(x, Y, z, st("smooth_quartz"));
			}
			int ux = r[3], uz = r[4];
			int sx = cx + Math.round(ux * 8 / 1000f), sz = cz + Math.round(uz * 8 / 1000f);
			w.set(sx, Y + 1, sz, st("campfire"));
			int gx = cx - Math.round(ux * 8 / 1000f), gz = cz - Math.round(uz * 8 / 1000f);
			w.set(gx, Y + 1, gz, st("spruce_fence"));
			int rot = Math.floorMod(Math.round((float) (Math.toDegrees(Math.atan2(-ux, uz)) / 22.5)), 16);
			w.sign(gx, Y + 2, gz, rot, "용의 둥지", "천공성 행", "용을 우클릭해 타기");
		}
	}
}
