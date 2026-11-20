package com.elementalweapons.world.sky;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import com.elementalweapons.world.town.TownPiece;

/** 청크마다 한 번: 다리·수도·성·기착섬 정자·폭포를 그 청크 안에만 그림 */
public final class SkyPainter implements Feature {
	public static final SkyPainter INSTANCE = new SkyPainter();
	public static final MapCodec<SkyPainter> CODEC = MapCodec.unit(INSTANCE);

	@Override public MapCodec<? extends Feature> codec() { return CODEC; }

	private WorldGenLevel lv;
	private int minX, minZ, maxX, maxZ;

	@Override
	public synchronized boolean place(WorldGenLevel level, ChunkGenerator gen, RandomSource random, BlockPos origin) {
		this.lv = level;
		minX = (origin.getX() >> 4) << 4; minZ = (origin.getZ() >> 4) << 4; maxX = minX + 15; maxZ = minZ + 15;
		try {
			capital(random);
			for (SkyLayout.Bridge b : SkyLayout.BRIDGES) bridge(b);
			for (SkyLayout.Island is : SkyLayout.ISLANDS) if (is.kind == 3) pavilion(is);
			waterfalls();
		} finally { lv = null; }
		return true;
	}

	// ---------------- 도우미 ----------------
	private boolean in(int x, int z) { return x >= minX && x <= maxX && z >= minZ && z <= maxZ; }
	private void set(int x, int y, int z, BlockState s) { if (in(x, z) && y > -60 && y < 310) lv.setBlock(new BlockPos(x, y, z), s, 2); }
	private boolean air(int x, int y, int z) { return lv.getBlockState(new BlockPos(x, y, z)).isAir(); }
	private void setIfAir(int x, int y, int z, BlockState s) { if (in(x, z) && air(x, y, z)) set(x, y, z, s); }
	private static BlockState st(Block b) { return b.defaultBlockState(); }
	private static Block blk(String id) { return BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("minecraft", id)); }
	private static int h(int x, int y, int z) { int n = x * 73856093 ^ y * 19349663 ^ z * 83492791; n ^= n >>> 13; n *= 0x5bd1e995; return (n ^ n >>> 15) & 0x7fffffff; }
	private BlockState brick(int x, int y, int z) { int k = h(x, y, z) % 11; return st(k == 0 ? Blocks.MOSSY_STONE_BRICKS : k == 1 ? Blocks.CRACKED_STONE_BRICKS : Blocks.STONE_BRICKS); }

	// ---------------- 다리 ----------------
	private void bridge(SkyLayout.Bridge b) {
		double W = b.width, hw = W / 2;
		double pad = hw + 8;
		if (Math.max(b.ax, b.bx) + pad < minX || Math.min(b.ax, b.bx) - pad > maxX || Math.max(b.az, b.bz) + pad < minZ || Math.min(b.az, b.bz) - pad > maxZ) return;
		double vx = b.bx - b.ax, vz = b.bz - b.az, L = Math.hypot(vx, vz);
		if (L < 1) return;
		double ux = vx / L, uz = vz / L;
		int P = b.style == 2 ? 36 : 56;              // 교각 간격
		for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
			double px = x + 0.5 - b.ax, pz = z + 0.5 - b.az;
			double s = px * ux + pz * uz;
			if (s < -1 || s > L + 1) continue;
			double d = -px * uz + pz * ux;
			double ad = Math.abs(d);
			if (ad > hw + 0.5) continue;
			double t = Math.max(0, Math.min(1, s / L));
			int deck = (int) Math.round(b.ay + (b.by - b.ay) * t + b.arch * Math.sin(t * Math.PI));
			boolean edge = ad > hw - 1;
			// 위 비우기
			for (int y = deck + 1; y <= deck + 6; y++) if (!air(x, y, z)) set(x, y, z, st(Blocks.AIR));
			// 바닥
			BlockState top;
			if (edge) top = st(Blocks.POLISHED_ANDESITE);
			else if (ad < 1.0 && b.style != 2) top = st(Blocks.SMOOTH_STONE);
			else top = ((x + z) & 1) == 0 ? st(Blocks.STONE_BRICKS) : st(h(x, deck, z) % 5 == 0 ? Blocks.MOSSY_STONE_BRICKS : Blocks.POLISHED_ANDESITE);
			if (b.style == 2) top = edge ? st(Blocks.SPRUCE_LOG) : st(Blocks.SPRUCE_PLANKS);
			set(x, deck, z, top);
			// 난간 + 가로등 기둥
			int along = (int) Math.floor(s);
			if (edge) {
				boolean post = Math.floorMod(along, 14) == 0;
				if (b.style == 2) {
					set(x, deck + 1, z, st(Blocks.SPRUCE_FENCE));
					if (Math.floorMod(along, 12) == 0) { set(x, deck + 2, z, st(Blocks.SPRUCE_FENCE)); set(x, deck + 3, z, st(Blocks.LANTERN)); }
				} else if (post) {
					set(x, deck + 1, z, st(Blocks.CHISELED_STONE_BRICKS));
					set(x, deck + 2, z, st(Blocks.STONE_BRICK_WALL));
					set(x, deck + 3, z, st(Blocks.STONE_BRICK_WALL));
					set(x, deck + 4, z, st(Blocks.LANTERN));
				} else set(x, deck + 1, z, st(Blocks.STONE_BRICK_WALL));
			}
			// 아래: 아치형 받침
			int mod = Math.floorMod(along, P);
			double u = Math.abs(mod - P / 2.0) / (P / 2.0);           // 0 = 경간 중앙, 1 = 교각
			int thick = b.style == 2 ? 1 : 2 + (int) Math.round(9 * u * u * u);
			for (int y = deck - 1; y >= deck - thick; y--) {
				if (!air(x, y, z)) break;
				set(x, y, z, b.style == 2 ? st(Blocks.SPRUCE_PLANKS) : (y == deck - thick && thick > 2 ? st(Blocks.STONE_BRICK_SLAB).setValue(BlockStateProperties.SLAB_TYPE, net.minecraft.world.level.block.state.properties.SlabType.TOP) : brick(x, y, z)));
			}
			// 교각 (공허로 늘어진 기둥)
			if (b.style != 2 && (mod == 0 || mod == P - 1 || mod == 1) && s > 8 && s < L - 8) {
				double ph = hw - 1;
				if (ad < ph) {
					int depth = 26 + (int) (20 * SkyLayout.hash(along / P, b.style, 3));
					for (int y = deck - thick - 1; y > deck - thick - depth; y--) {
						int k = deck - thick - y;
						if (ad > ph - k / 7.0) break;          // 아래로 갈수록 가늘게
						if (!air(x, y, z)) break;
						set(x, y, z, brick(x, y, z));
					}
				}
			}
			// 다리 밑 매달린 등불·덩굴
			if (b.style != 2 && !edge && mod == P / 2 && Math.abs(ad - hw / 2) < 0.6) set(x, deck - thick - 1, z, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true));
		}
	}

	// ---------------- 기착섬 정자 ----------------
	private void pavilion(SkyLayout.Island is) {
		int cx = (int) Math.floor(is.x), cz = (int) Math.floor(is.z);
		if (cx + 30 < minX || cx - 30 > maxX || cz + 30 < minZ || cz - 30 > maxZ) return;
		// 다리 방향과 수직으로 비켜 세움
		SkyLayout.Bridge near = null;
		for (SkyLayout.Bridge b : SkyLayout.BRIDGES) if (Math.hypot(b.bx - is.x, b.bz - is.z) < 2 || Math.hypot(b.ax - is.x, b.az - is.z) < 2) { near = b; break; }
		double nx = 1, nz = 0;
		if (near != null) { double L = near.length(); nx = -(near.bz - near.az) / L; nz = (near.bx - near.ax) / L; }
		int px = (int) Math.round(cx + nx * 18), pz = (int) Math.round(cz + nz * 18);
		int g = SkyLayout.surfaceTop(px, pz);
		if (g == Integer.MIN_VALUE) return;
		for (int i = -4; i <= 4; i++) for (int k = -4; k <= 4; k++) {
			int x = px + i, z = pz + k;
			set(x, g, z, (Math.abs(i) == 4 || Math.abs(k) == 4) ? st(Blocks.STONE_BRICKS) : st(Blocks.POLISHED_ANDESITE));
			for (int y = g + 1; y <= g + 7; y++) set(x, y, z, st(Blocks.AIR));
			if (Math.abs(i) == 3 && Math.abs(k) == 3) for (int y = g + 1; y <= g + 4; y++) set(x, y, z, st(Blocks.STRIPPED_DARK_OAK_LOG));
		}
		for (int lvl = 0; lvl < 3; lvl++) for (int i = -4 + lvl; i <= 4 - lvl; i++) for (int k = -4 + lvl; k <= 4 - lvl; k++)
			set(px + i, g + 5 + lvl, pz + k, lvl == 2 ? st(blk("waxed_cut_copper")) : st(blk("waxed_cut_copper_slab")));
		set(px, g + 4, pz, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true));
		set(px - 1, g + 1, pz + 2, st(Blocks.SPRUCE_STAIRS)); set(px + 1, g + 1, pz + 2, st(Blocks.SPRUCE_STAIRS));
		set(px, g + 1, pz - 2, st(Blocks.FLOWERING_AZALEA));
	}

	// ---------------- 폭포 ----------------
	private static List<int[]> FALLS;

	private static synchronized List<int[]> falls() {
		if (FALLS != null) return FALLS;
		List<int[]> out = new ArrayList<>();
		for (SkyLayout.Island is : SkyLayout.ISLANDS) {
			if (is.kind != 1 && is.kind != 5 && is.kind != 0) continue;
			int n = is.kind == 0 ? 4 : 7;
			for (int i = 0; i < n; i++) {
				double a = (i + 0.3 + 0.4 * SkyLayout.hash(is.seed, i, 77)) * Math.PI * 2 / n;
				int lastX = 0, lastZ = 0; boolean found = false;
				for (double r = is.r * 0.7; r < is.r * 1.3; r += 1) {
					int x = (int) Math.floor(is.x + Math.cos(a) * r), z = (int) Math.floor(is.z + Math.sin(a) * r);
					if (SkyLayout.surfaceTop(x, z) == Integer.MIN_VALUE) break;
					lastX = x; lastZ = z; found = true;
				}
				if (!found) continue;
				int x = (int) Math.floor(lastX - Math.cos(a) * 3), z = (int) Math.floor(lastZ - Math.sin(a) * 3);
				int y = SkyLayout.surfaceTop(x, z);
				if (y != Integer.MIN_VALUE) out.add(new int[]{x, y, z});
			}
		}
		FALLS = out;
		return out;
	}

	private void waterfalls() {
		for (int[] f : falls()) {
			if (!in(f[0], f[2])) continue;
			set(f[0], f[1], f[2], st(Blocks.WATER));
			for (int y = f[1] + 1; y < f[1] + 4; y++) set(f[0], y, f[2], st(Blocks.AIR));
		}
	}

	// ---------------- 수도 ----------------
	private static final int G = 170;   // 수도 바닥 높이

	private void capital(RandomSource random) {
		if (maxX < -400 || minX > 400 || maxZ < -400 || minZ > 400) return;
		// 외곽 순환로
		for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
			double r = Math.hypot(x + 0.5, z + 0.5);
			if (Math.abs(r - 200) < 3.6) {
				if (SkyLayout.surfaceTop(x, z) < G - 2) continue;
				set(x, G, z, Math.abs(r - 200) > 2.6 ? st(Blocks.STONE_BRICKS) : st(Blocks.POLISHED_ANDESITE));
				for (int y = G + 1; y <= G + 6; y++) if (!air(x, y, z)) set(x, y, z, st(Blocks.AIR));
				if (Math.abs(r - 203.3) < 0.5 && h((int) Math.round(Math.atan2(z, x) * 40), 0, 0) % 2 == 0 && Math.floorMod((int) (Math.atan2(z, x) * 200 / Math.PI), 11) == 0) {
					set(x, G + 1, z, st(Blocks.POLISHED_BLACKSTONE_WALL)); set(x, G + 2, z, st(Blocks.POLISHED_BLACKSTONE_WALL)); set(x, G + 3, z, st(Blocks.LANTERN));
				}
			}
		}
		// 9개 구역: 가운데는 왕성, 나머지는 시가지
		BoundingBox box = new BoundingBox(minX, -64, minZ, maxX, 319, maxZ);
		ChunkPos cp = new ChunkPos(minX >> 4, minZ >> 4);
		for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) {
			int x0 = -168 + (i + 1) * 112, z0 = -168 + (j + 1) * 112;
			if (x0 > maxX || x0 + 111 < minX || z0 > maxZ || z0 + 111 < minZ) continue;
			if (i == 0 && j == 0) { castle(); continue; }
			int flags = (i == -1 ? 1 : 0) | (i == 1 ? 2 : 0) | (j == -1 ? 4 : 0) | (j == 1 ? 8 : 0)
					| (i == 1 && j == -1 ? 16 : 0) | (i == 0 && j == 1 ? 32 : 0);
			new TownPiece(x0, G + 1, z0, 777L + i * 31 + j * 97, flags).postProcess(lv, null, null, random, box, cp, BlockPos.ZERO);
		}
	}

	// ---------------- 왕성 ----------------
	private void castle() {
		int Y = G + 1;
		for (int x = Math.max(minX, -56); x <= Math.min(maxX, 55); x++) for (int z = Math.max(minZ, -56); z <= Math.min(maxZ, 55); z++) {
			int ax = Math.abs(x), az = Math.abs(z);
			// 바닥 + 비우기
			set(x, G, z, ((x + z) & 1) == 0 ? st(Blocks.POLISHED_ANDESITE) : st(Blocks.STONE_BRICKS));
			for (int y = Y; y <= Y + 70; y++) if (!air(x, y, z)) set(x, y, z, st(Blocks.AIR));
			// 바깥 성벽 (반지름 44, 두께 3, 높이 13)
			int m = Math.max(ax, az);
			if (m >= 42 && m <= 44) {
				boolean gate = z > 0 && az >= 42 && ax <= 3;
				for (int y = Y; y < Y + 13; y++) {
					if (gate && y < Y + 8) continue;
					set(x, y, z, y == Y + 12 ? st(Blocks.POLISHED_ANDESITE) : brick(x, y, z));
				}
				if (m == 44 && ((x + z) & 1) == 0) set(x, Y + 13, z, st(Blocks.STONE_BRICK_WALL));
				if (m == 42 && Math.floorMod(x + z, 9) == 0) set(x, Y + 10, z, st(Blocks.LANTERN));
			}
			// 모서리 원형 탑 4개
			for (int sx = -1; sx <= 1; sx += 2) for (int sz = -1; sz <= 1; sz += 2) roundTower(x, z, sx * 44, sz * 44, 7, Y, 26);
			// 성 안뜰 정원
			if (m < 40 && m > 24) {
				if (((ax / 4) + (az / 4)) % 5 == 0 && ax % 4 != 0 && az % 4 != 0) {
					set(x, G, z, st(Blocks.GRASS_BLOCK));
					if (h(x, 1, z) % 3 == 0) set(x, Y, z, st(h(x, 2, z) % 2 == 0 ? Blocks.ALLIUM : Blocks.LILY_OF_THE_VALLEY));
				}
				if (ax % 12 == 6 && az % 12 == 6) { set(x, Y, z, st(Blocks.POLISHED_BLACKSTONE_WALL)); set(x, Y + 1, z, st(Blocks.POLISHED_BLACKSTONE_WALL)); set(x, Y + 2, z, st(Blocks.LANTERN)); }
			}
			// 본성 (가로세로 34, 높이 32) + 모서리 첨탑
			int kx = x, kz = z + 6, akx = Math.abs(kx), akz = Math.abs(kz);
			int km = Math.max(akx, akz);
			if (km <= 17) {
				boolean wall = km >= 16;
				for (int y = Y; y < Y + 32; y++) {
					if (wall) {
						boolean door = kz > 0 && akx <= 2 && y < Y + 6;
						if (door) continue;
						boolean win = (akx % 5 == 2 || akz % 5 == 2) && (y - Y) % 10 >= 3 && (y - Y) % 10 <= 7 && km == 17;
						set(x, y, z, win ? st(blk("purple_stained_glass")) : (y - Y) % 10 == 0 ? st(Blocks.CHISELED_STONE_BRICKS) : (y > Y + 10 ? st(Blocks.CALCITE) : brick(x, y, z)));
					} else if ((y - Y) % 10 == 0 && y > Y) set(x, y, z, st(Blocks.DARK_OAK_PLANKS));
				}
				// 피라미드 지붕
				for (int s = 0; s <= 17; s++) if (km == 17 - s) { set(x, Y + 32 + s, z, st(blk("waxed_cut_copper"))); }
				if (km <= 2) for (int y = Y + 32; y < Y + 52; y++) if (km == 0 || y < Y + 50) set(x, y, z, km == 0 && y >= Y + 49 ? st(Blocks.GOLD_BLOCK) : st(blk("waxed_cut_copper")));
				if (!wall && km <= 15 && h(x, 0, z) % 37 == 0) set(x, Y + 9, z, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true));
				if (km < 15 && akx <= 1 && kz > -14) set(x, Y, z, st(blk("red_carpet")));
			}
			for (int sx = -1; sx <= 1; sx += 2) for (int sz = -1; sz <= 1; sz += 2) roundTower(x, z, sx * 17, sz * 17 - 6, 5, Y, 44);
			// 정문 앞 계단 장식 + 광장 분수
			if (z >= 46 && z <= 55 && ax <= 10) set(x, G, z, ((x + z) & 1) == 0 ? st(Blocks.SMOOTH_STONE) : st(Blocks.POLISHED_ANDESITE));
			double fr = Math.hypot(x, z - 30);
			if (fr <= 4.5) { set(x, G, z, st(Blocks.WATER)); set(x, G - 1, z, st(Blocks.PRISMARINE_BRICKS)); }
			else if (fr <= 5.5) set(x, Y, z, st(Blocks.STONE_BRICK_WALL));
			if (x == 0 && z == 30) { for (int y = G; y < Y + 3; y++) set(x, y, z, st(Blocks.CHISELED_STONE_BRICKS)); set(x, Y + 3, z, st(Blocks.SEA_LANTERN)); set(x, Y + 4, z, st(Blocks.WATER)); }
		}
	}

	private static boolean y0(int x, int z) { return true; }

	private void roundTower(int x, int z, int cx, int cz, int R, int Y, int H) {
		double r = Math.hypot(x - cx, z - cz);
		if (r > R + 0.5) return;
		boolean shell = r > R - 1.5;
		for (int y = Y; y < Y + H; y++) {
			if (shell) {
				boolean win = (y - Y) % 8 >= 3 && (y - Y) % 8 <= 5 && (Math.abs(x - cx) <= 0 || Math.abs(z - cz) <= 0);
				set(x, y, z, win ? st(blk("glass_pane")) : brick(x, y, z));
			} else if ((y - Y) % 8 == 0 && y > Y) set(x, y, z, st(Blocks.SPRUCE_PLANKS));
		}
		// 원뿔 지붕 + 금 첨두
		int roofH = R * 2 + 4;
		for (int k = 0; k < roofH; k++) {
			double rr = (R + 1) * (1 - k / (double) roofH);
			if (r <= rr + 0.5 && r > rr - 1.2) set(x, Y + H + k, z, st(blk("waxed_cut_copper")));
		}
		if (x == cx && z == cz) { set(x, Y + H + roofH, z, st(Blocks.GOLD_BLOCK)); set(x, Y + H + roofH + 1, z, st(blk("lightning_rod"))); }
		if (r <= R + 0.5 && r > R - 0.5 && Math.floorMod(x + z, 2) == 0) set(x, Y + H, z, st(Blocks.STONE_BRICK_WALL));
	}
}
