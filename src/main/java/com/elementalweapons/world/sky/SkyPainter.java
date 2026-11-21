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

	private static boolean logged;
	private WorldGenLevel lv;
	private int minX, minZ, maxX, maxZ;

	/** 청크마다 새 작업자를 만들어 여러 월드 생성 스레드가 동시에 칠할 수 있게 함 (예전엔 한 줄로 줄 서서 느렸음) */
	@Override
	public boolean place(WorldGenLevel level, ChunkGenerator gen, RandomSource random, BlockPos origin) {
		return new SkyPainter().paint(level, random, origin);
	}

	private boolean paint(WorldGenLevel level, RandomSource random, BlockPos origin) {
		this.lv = level;
		minX = (origin.getX() >> 4) << 4; minZ = (origin.getZ() >> 4) << 4; maxX = minX + 15; maxZ = minZ + 15;
		try {
			capital(random);
			for (SkyLayout.Bridge b : SkyLayout.BRIDGES) bridge(b);
			for (SkyLayout.Island is : SkyLayout.ISLANDS) if (is.kind == 3) pavilion(is);
			for (SkyLayout.Island is : SkyLayout.ISLANDS) if (is.kind == 1 || is.kind == 2 || is.kind == 5) Outposts.paint(lv, minX, minZ, is);
			Outposts.roosts(lv, minX, minZ);
			waterfalls();
			for (Arenas.Arena a : Arenas.all()) if (maxX >= a.x() - 32 && minX <= a.x() + 32 && maxZ >= a.z() - 32 && minZ <= a.z() + 32) arena(a);
		} catch (Throwable t) {
			if (!logged) { logged = true; com.elementalweapons.ElementalWeaponsMod.LOGGER.error("[SKY] 페인터 오류", t); }
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
			set(px + i, g + 5 + lvl, pz + k, lvl == 2 ? roof() : st(blk("waxed_cut_copper_slab")));
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
			if (is.kind != 1 && is.kind != 5) continue;
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
		if (maxX < -260 || minX > 260 || maxZ < -260 || minZ > 260) return;
		new Capital(lv, minX, minZ).paint();
		// 천공성 둘레를 떠다니는 작은 섬들
		for (int i = 0; i < 9; i++) {
			double a = Math.toRadians(i * 40 + 17);
			boolean nearBridge = false;
			for (int k = 0; k < 7; k++) { double d = Math.abs(Math.atan2(Math.sin(a - SkyLayout.regionAngle(k)), Math.cos(a - SkyLayout.regionAngle(k)))); if (d < Math.toRadians(12)) nearBridge = true; }
			if (nearBridge || Math.abs(Math.toDegrees(a) - 90) < 25) continue;
			int rad = 160 + (i % 3) * 22;
			int cx = (int) Math.round(Math.cos(a) * rad), cz = (int) Math.round(Math.sin(a) * rad);
			int R = 6 + (i * 7) % 6, top = 175 + (i * 23) % 80;
			if (maxX >= cx - R - 2 && minX <= cx + R + 2 && maxZ >= cz - R - 2 && minZ <= cz + R + 2) floatIslet(cx, cz, R, top, i);
		}
	}

	// ---------------- 보스방 ----------------
	private static final String[][] ARENA_PAL = {   // 바닥, 무늬, 발광, 기둥
			{"mossy_stone_bricks", "moss_block", "verdant_froglight", "stripped_jungle_log"},
			{"prismarine_bricks", "dark_prismarine", "sea_lantern", "prismarine_bricks"},
			{"polished_deepslate", "deepslate_tiles", "glowstone", "deepslate_bricks"},
			{"calcite", "smooth_quartz", "sea_lantern", "quartz_pillar"},
			{"polished_blackstone_bricks", "nether_bricks", "shroomlight", "polished_blackstone"},
			{"smooth_quartz", "chiseled_quartz_block", "ochre_froglight", "quartz_pillar"},
			{"obsidian", "crying_obsidian", "pearlescent_froglight", "polished_blackstone_bricks"},
			{"end_stone_bricks", "purpur_block", "pearlescent_froglight", "purpur_pillar"},
	};

	private void arena(Arenas.Arena a) {
		String[] P = ARENA_PAL[a.region()];
		BlockState floor = st(blk(P[0])), pat = st(blk(P[1])), glow = st(blk(P[2])), pil = st(blk(P[3]));
		int F = a.floor(), R = Arenas.R;
		for (int x = Math.max(minX, a.x() - R - 4); x <= Math.min(maxX, a.x() + R + 4); x++)
			for (int z = Math.max(minZ, a.z() - R - 4); z <= Math.min(maxZ, a.z() + R + 4); z++) {
				int dx = x - a.x(), dz = z - a.z();
				double r = Math.hypot(dx, dz), th = Math.toDegrees(Math.atan2(dz, dx));
				if (r > R + 3.5) continue;
				// 위 비우기 + 바닥 아래 받침
				for (int y = F + 1; y <= F + 34; y++) if (!air(x, y, z)) set(x, y, z, st(Blocks.AIR));
				for (int y = F - 4; y < F; y++) set(x, y, z, y == F - 4 && r > R ? pat : floor);
				// 바닥 무늬: 동심원 룬 고리, 8갈래 빛줄기, 체크
				BlockState f = ((int) Math.floor(r) % 6 == 0) ? pat : (((dx >> 1) + (dz >> 1)) & 1) == 0 ? floor : pat;
				if (Math.abs(r - 9) < 0.5 || Math.abs(r - 17.5) < 0.5) f = Math.floorMod((int) Math.round(th), 15) < 5 ? glow : pat;
				double spoke = Math.abs(((th % 45) + 45) % 45 - 22.5);
				if (spoke > 21.6 && r > 3 && r < R - 1) f = glow;
				if (r > R + 0.5) f = pat;
				set(x, F, z, f);
				// 가장자리 낮은 벽 (네 방향 입구)
				boolean gate = Math.abs(dx) <= 2 || Math.abs(dz) <= 2;
				if (r > R - 0.5 && r <= R + 0.6 && !gate) {
					set(x, F + 1, z, pil);
					if (Math.floorMod((int) Math.round(th), 6) < 3) set(x, F + 2, z, pil);
				}
				// 기둥 8개 + 화로
				for (int k = 0; k < 8; k++) {
					double ang = Math.toRadians(k * 45 + 22.5);
					double px = Math.cos(ang) * 22, pz = Math.sin(ang) * 22;
					double pr = Math.hypot(dx - px, dz - pz);
					if (pr <= 1.7) {
						for (int y = F + 1; y <= F + 10; y++) set(x, y, z, y == F + 10 ? pat : pil);
						if (pr < 0.8) { set(x, F + 11, z, glow); set(x, F + 12, z, st(Blocks.AMETHYST_CLUSTER)); }
					}
				}
				// 수정 탑 자리 (네 곳)
				for (int k = 0; k < 4; k++) {
					double ang = Math.toRadians(k * 90 + 45);
					if (Math.abs(dx - Math.cos(ang) * 17) <= 1.5 && Math.abs(dz - Math.sin(ang) * 17) <= 1.5) set(x, F, z, glow);
				}
				// 가운데 제단
				if (r <= 3.6) {
					set(x, F + 1, z, r <= 2.6 ? floor : st(blk("polished_blackstone_slab")));
					if (r <= 0.5) { set(x, F + 1, z, st(Blocks.LODESTONE)); set(x, F + 2, z, glow); }
				}
			}
	}

	// ---------------- 수도 판타지 장식 ----------------
	private static final String[] GLASS = {"lime", "light_blue", "yellow", "cyan", "orange", "white", "purple"};
	private static final String[] GLOW = {"verdant_froglight", "sea_lantern", "glowstone", "sea_lantern", "shroomlight", "ochre_froglight", "pearlescent_froglight"};
	private static final int CRY_Y = 266, CRY_Z = -6;

	private void fantasy() {
		// 하늘 위 거대 원소 수정 + 기울어진 고리 + 주위를 도는 파편 + 첨탑에서 올라가는 빛줄기
		if (maxX >= -42 && minX <= 42 && maxZ >= CRY_Z - 42 && minZ <= CRY_Z + 42) skyCrystal();
		// 지역으로 가는 대교 사이마다 원소 마법탑 7개
		for (int k = 0; k < 7; k++) {
			double a = SkyLayout.regionAngle(k) + Math.PI / 7;
			int cx = (int) Math.round(Math.cos(a) * 246), cz = (int) Math.round(Math.sin(a) * 246);
			if (maxX >= cx - 14 && minX <= cx + 14 && maxZ >= cz - 14 && minZ <= cz + 14) mageTower(cx, cz, k);
		}
		// 수도 하늘을 떠다니는 작은 섬들 (사슬로 묶임)
		for (int i = 0; i < 12; i++) {
			double a = Math.toRadians(i * 30 + 15);
			boolean nearBridge = false;
			for (int k = 0; k < 7; k++) { double d = Math.abs(Math.atan2(Math.sin(a - SkyLayout.regionAngle(k)), Math.cos(a - SkyLayout.regionAngle(k)))); if (d < Math.toRadians(8)) nearBridge = true; }
			if (nearBridge) continue;
			int rad = 318 + (i % 3) * 18;
			int cx = (int) Math.round(Math.cos(a) * rad), cz = (int) Math.round(Math.sin(a) * rad);
			int R = 7 + (i * 7) % 5, top = 214 + (i * 11) % 24;
			if (maxX >= cx - R - 2 && minX <= cx + R + 2 && maxZ >= cz - R - 2 && minZ <= cz + R + 2) floatIslet(cx, cz, R, top, i);
		}
		// 하늘에 떠 있는 빛 구슬
		for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
			if (h(x, 7, z) % 640 != 0 || x * x + z * z > 370 * 370) continue;
			int y = 194 + h(x, 8, z) % 48;
			int t = h(x, 9, z) % 3;
			if (air(x, y, z)) set(x, y, z, st(blk(t == 0 ? "pearlescent_froglight" : t == 1 ? "verdant_froglight" : "ochre_froglight")));
		}
	}

	private void skyCrystal() {
		for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
			int dx = x, dz = z - CRY_Z;
			// 본체: 길쭉한 팔면체
			int man = Math.abs(dx) + Math.abs(dz);
			if (man <= 11) for (int y = CRY_Y - 26; y <= CRY_Y + 26; y++) {
				double d = man / 11.0 + Math.abs(y - CRY_Y) / 26.0;
				if (d > 1) continue;
				BlockState b;
				if (d > 0.82) b = st(blk(((Math.signum(dx) + 2 * Math.signum(dz) + (y > CRY_Y ? 1 : 0)) % 2 == 0) ? "magenta_stained_glass" : "purple_stained_glass"));
				else if (d < 0.3) b = st(h(x, y, z) % 3 == 0 ? Blocks.AMETHYST_BLOCK : Blocks.SEA_LANTERN);
				else b = st(h(x, y, z) % 4 == 0 ? blk("pink_stained_glass") : Blocks.AMETHYST_BLOCK);
				set(x, y, z, b);
			}
			// 빛줄기: 본성 첨탑 꼭대기 → 수정 아래
			if (dx == 0 && dz == 0) for (int y = G + 53; y < CRY_Y - 26; y++) set(x, y, z, st(Blocks.END_ROD));
			// 기울어진 고리 2개
			for (int y = CRY_Y - 30; y <= CRY_Y + 30; y++) {
				double vx = dx, vy = y - CRY_Y, vz = dz;
				double len = Math.sqrt(vx * vx + vy * vy + vz * vz);
				double t1 = 0.42, p1 = Math.abs(vx * Math.sin(t1) + vy * Math.cos(t1));
				if (p1 < 0.6 && Math.abs(len - 24) < 0.75) set(x, y, z, (Math.floorMod((int) Math.round(Math.atan2(vz, vx) * 24), 6) == 0) ? st(Blocks.SEA_LANTERN) : st(blk("purple_stained_glass")));
				double t2 = -0.5, p2 = Math.abs(vz * Math.sin(t2) + vy * Math.cos(t2));
				if (p2 < 0.6 && Math.abs(len - 30) < 0.75) set(x, y, z, (Math.floorMod((int) Math.round(Math.atan2(vz, vx) * 30), 7) == 0) ? st(Blocks.GLOWSTONE) : st(blk("light_blue_stained_glass")));
			}
			// 주위를 도는 7원소 파편
			for (int k = 0; k < 7; k++) {
				double a = k * Math.PI * 2 / 7 + 0.3;
				int sx = (int) Math.round(Math.cos(a) * 37), sz = (int) Math.round(Math.sin(a) * 37), sy = CRY_Y - 8 + (k % 3) * 9;
				int m2 = Math.abs(dx - sx) + Math.abs(dz - sz);
				if (m2 <= 3) for (int y = sy - 7; y <= sy + 7; y++) {
					double d = m2 / 3.0 + Math.abs(y - sy) / 7.0;
					if (d <= 1) set(x, y, z, d < 0.45 ? st(blk(GLOW[k])) : st(blk(GLASS[k] + "_stained_glass")));
				}
			}
		}
	}

	private void mageTower(int cx, int cz, int k) {
		int Y = G + 1, H = 58;
		BlockState glass = st(blk(GLASS[k] + "_stained_glass")), glow = st(blk(GLOW[k]));
		double toC = Math.atan2(-cz, -cx);
		for (int x = Math.max(minX, cx - 14); x <= Math.min(maxX, cx + 14); x++) for (int z = Math.max(minZ, cz - 14); z <= Math.min(maxZ, cz + 14); z++) {
			double r = Math.hypot(x - cx, z - cz);
			double th = Math.atan2(z - cz, x - cx);
			// 주변 정리 + 원형 광장
			if (r <= 11.5) {
				for (int y = Y; y <= Y + H + 24; y++) if (!air(x, y, z)) set(x, y, z, st(Blocks.AIR));
				for (int y = G - 3; y <= G; y++) set(x, y, z, y < G ? st(Blocks.STONE) : (Math.abs(r - 10) < 0.6 ? st(Blocks.AMETHYST_BLOCK) : ((int) r % 3 == 0 ? st(Blocks.POLISHED_BLACKSTONE_BRICKS) : st(Blocks.CALCITE))));
				if (Math.abs(r - 10.8) < 0.5 && Math.floorMod((int) Math.round(th * 10), 8) == 0) { set(x, Y, z, st(Blocks.POLISHED_BLACKSTONE_WALL)); set(x, Y + 1, z, glow); }
			}
			// 탑 몸체 (위로 갈수록 가늘어짐) + 나선형 원소 유리창
			for (int y = Y; y < Y + H; y++) {
				double R = 6.5 - 2.5 * (y - Y) / (double) H;
				if (r > R + 0.5) continue;
				if (r > R - 1.3) {
					double sp = th / (Math.PI * 2) * 3 + (y - Y) / 9.0;
					boolean win = (sp - Math.floor(sp)) < 0.14;
					boolean door = y < Y + 4 && Math.abs(Math.atan2(Math.sin(th - toC), Math.cos(th - toC))) < 0.28;
					if (door) { set(x, y, z, st(Blocks.AIR)); continue; }
					set(x, y, z, win ? glass : (y - Y) % 12 == 0 ? st(Blocks.CHISELED_POLISHED_BLACKSTONE) : (h(x, y, z) % 9 == 0 ? st(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS) : st(Blocks.POLISHED_BLACKSTONE_BRICKS)));
				} else if ((y - Y) % 12 == 0 && y > Y) set(x, y, z, st(Blocks.DARK_OAK_PLANKS));
				else if (r < 1 && (y - Y) % 12 == 6) set(x, y, z, glow);
			}
			// 꼭대기 테라스 + 받침 장식
			int T = Y + H;
			if (r <= 7.5) { set(x, T, z, r > 6.5 ? st(Blocks.CHISELED_POLISHED_BLACKSTONE) : st(Blocks.POLISHED_BLACKSTONE_BRICKS)); if (r > 6.5 && Math.floorMod((int) Math.round(th * 12), 2) == 0) set(x, T + 1, z, st(Blocks.POLISHED_BLACKSTONE_WALL)); }
			if (r <= 7.5 && r > 4) for (int y = T - 3; y < T; y++) if (r < 4 + (y - (T - 3)) * 1.2 + 0.5) set(x, y, z, st(Blocks.POLISHED_BLACKSTONE_BRICKS));
			// 공중에 뜬 원소 수정
			int m = Math.abs(x - cx) + Math.abs(z - cz);
			if (m <= 3) for (int y = T + 6; y <= T + 18; y++) {
				double d = m / 3.0 + Math.abs(y - (T + 12)) / 6.0;
				if (d <= 1) set(x, y, z, d < 0.45 ? glow : glass);
			}
			// 탑 허리를 감싸는 룬 고리
			int ry = Y + 36;
			if (Math.abs(r - 10) < 0.6 && Math.floorMod((int) Math.round(th * 10), 3) != 0) set(x, ry + (int) Math.round(Math.sin(th) * 1.5), z, glass);
		}
	}

	private void floatIslet(int cx, int cz, int R, int top, int seed) {
		for (int x = Math.max(minX, cx - R - 2); x <= Math.min(maxX, cx + R + 2); x++) for (int z = Math.max(minZ, cz - R - 2); z <= Math.min(maxZ, cz + R + 2); z++) {
			double r = Math.hypot(x - cx, z - cz) + (h(x, seed, z) % 100) / 140.0;
			if (r <= R) {
				int depth = (int) ((R - r) * 1.6) + 2 + h(x, 1, z) % 3;
				for (int y = top - depth; y <= top; y++) set(x, y, z, y == top ? st(Blocks.GRASS_BLOCK) : y > top - 3 ? st(Blocks.DIRT) : (h(x, y, z) % 5 == 0 ? st(Blocks.ANDESITE) : st(Blocks.STONE)));
				if (r < R - 1.5 && h(x, 2, z) % 6 == 0) set(x, top + 1, z, st(h(x, 3, z) % 2 == 0 ? Blocks.ALLIUM : Blocks.CORNFLOWER));
				if (r < 1) { set(x, top + 1, z, st(Blocks.FLOWERING_AZALEA)); }
			}
			// 섬 아래 빛나는 뿌리 수정
			if (x == cx && z == cz) for (int y = top - (int) (R * 1.6) - 6; y < top - (int) (R * 1.6) - 1; y++) set(x, y, z, st(blk(GLOW[seed % 7])));
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
				for (int s = 0; s <= 17; s++) if (km == 17 - s) { set(x, Y + 32 + s, z, roof()); }
				if (km <= 2) for (int y = Y + 32; y < Y + 52; y++) if (km == 0 || y < Y + 50) set(x, y, z, km == 0 && y >= Y + 49 ? st(Blocks.GOLD_BLOCK) : roof());
				if (!wall && km <= 15 && h(x, 0, z) % 37 == 0) set(x, Y + 9, z, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true));
				if (km < 15 && akx <= 1 && kz > -14) set(x, Y, z, st(blk("red_carpet")));
			}
			for (int sx = -1; sx <= 1; sx += 2) for (int sz = -1; sz <= 1; sz += 2) roundTower(x, z, sx * 17, sz * 17 - 6, 5, Y, 44);
			// 정문 앞 계단 장식 + 광장 분수
			if (z >= 46 && z <= 55 && ax <= 10) set(x, G, z, ((x + z) & 1) == 0 ? st(Blocks.SMOOTH_STONE) : st(Blocks.POLISHED_ANDESITE));
			double fr = Math.hypot(x, z - 30);
			if (fr <= 4.5) { set(x, G, z, st(Blocks.WATER)); set(x, G - 1, z, st(Blocks.PRISMARINE_BRICKS)); }
			else if (fr <= 5.5) set(x, Y, z, st(Blocks.STONE_BRICK_WALL));
			if (fr > 5.5 && fr <= 10.5) {
				double ang = Math.toDegrees(Math.atan2(z - 30, x));
				boolean spoke = Math.abs(((ang % 45) + 45) % 45 - 22.5) > 19.5 && fr < 9;
				if (fr > 9.4) set(x, G, z, st(Blocks.AMETHYST_BLOCK));
				else if (Math.abs(fr - 7.2) < 0.45) set(x, G, z, st(Blocks.PURPUR_BLOCK));
				else if (spoke) set(x, G, z, st(Blocks.POLISHED_BLACKSTONE));
				else set(x, G, z, st(Blocks.CALCITE));
				if (Math.abs(fr - 9.9) < 0.5 && Math.abs(((ang % 60) + 60) % 60 - 30) > 26) { set(x, G, z, st(Blocks.SEA_LANTERN)); set(x, Y, z, st(Blocks.AMETHYST_CLUSTER)); }
			}
			if (x == 0 && z == 30) { for (int y = G; y < Y + 3; y++) set(x, y, z, st(Blocks.CHISELED_STONE_BRICKS)); set(x, Y + 3, z, st(Blocks.SEA_LANTERN)); set(x, Y + 4, z, st(Blocks.WATER)); }
		}
	}

	private static boolean y0(int x, int z) { return true; }
	private static BlockState roof() { return st(blk("waxed_oxidized_cut_copper")); }

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
			if (r <= rr + 0.5 && r > rr - 1.2) set(x, Y + H + k, z, roof());
		}
		if (x == cx && z == cz) { set(x, Y + H + roofH, z, st(Blocks.GOLD_BLOCK)); set(x, Y + H + roofH + 1, z, st(blk("lightning_rod"))); }
		if (r <= R + 0.5 && r > R - 0.5 && Math.floorMod(x + z, 2) == 0) set(x, Y + H, z, st(Blocks.STONE_BRICK_WALL));
	}
}
