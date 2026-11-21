package com.elementalweapons.world.sky;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;

/**
 * 천공성 (아인크라드처럼 위로 쌓인 공중 도시).
 *  1층 용의 둥지 · 2층 장인의 거리 · 3층 마법사의 정원 · 4층 균열의 전당 · 5층 천공의 왕성
 * 가운데 탑의 빛 기둥(승강기)으로 층을 오가고, 1층 용의 둥지에서 천공룡을 타고 다른 섬으로 날아감.
 * 층마다 대장장이·상인이 고르게 있음.
 */
public final class Capital {
	public static final int[] FY = {150, 184, 216, 246, 274};
	public static final int[] FR = {118, 96, 78, 60, 44};
	public static final String[] FLOOR_NAME = {"1층 · 용의 둥지", "2층 · 장인의 거리", "3층 · 마법사의 정원", "4층 · 균열의 전당", "5층 · 천공의 왕성"};
	public static final int CORE_R = 9, SHAFT_R = 2, TOP = FY[4] + 22;
	public static final int G = FY[0];

	public enum Kind { ROOST, INN, MARKET, SAGE_HALL, SMITH, STORAGE, TRAINING, FORGE, ALCHEMY, HOUSE, GARDEN, ENCHANT, MAGE_LAB, STALL, REPAIR, RIFT_GATE }

	public record Lot(Kind kind, int floor, int x, int z, int rot, int seed) {
		public int y() { return FY[floor]; }
		int half() {
			return switch (kind) {
				case MARKET, TRAINING -> 15; case SAGE_HALL, RIFT_GATE -> 13; case SMITH, FORGE, INN, STORAGE -> 12;
				case MAGE_LAB, GARDEN -> 10; case ENCHANT -> 8; case ALCHEMY, HOUSE -> 8; case REPAIR -> 7; case STALL -> 6; case ROOST -> 0;
			};
		}
	}

	/** 가게 주인 자리 (type: merchant · blacksmith · enchanter · alchemist) */
	public record Npc(String type, BlockPos pos) {}
	public record Pillar(int floor, int x, int z) {}

	public static final List<Lot> LOTS = new ArrayList<>();
	public static final List<Npc> NPCS = new ArrayList<>();
	public static final List<Pillar> PILLARS = new ArrayList<>();
	public static final List<BlockPos> DRAGON_PADS = new ArrayList<>();
	public static BlockPos SAGE_POS = new BlockPos(0, G + 1, -40);
	/** 용의 둥지 (1층 남쪽, 가장자리 밖으로 튀어나온 원형 발판) */
	public static final int ROOST_X = 0, ROOST_Z = 104, ROOST_R = 22;

	static {
		// 1층: 대교 7갈래 사이마다 한 채
		double[] gap = new double[7];
		for (int k = 0; k < 7; k++) gap[k] = Math.toDegrees(SkyLayout.regionAngle(k)) + 360.0 / 14;
		Kind[] f1 = {Kind.SAGE_HALL, Kind.STORAGE, Kind.INN, Kind.ROOST, Kind.MARKET, Kind.SMITH, Kind.TRAINING};
		for (int k = 0; k < 7; k++) if (f1[k] != Kind.ROOST) lot(f1[k], 0, gap[k], 62, 100 + k);
		for (int k = 0; k < 7; k++) if (f1[k] != Kind.ROOST) pillar(0, gap[k], 86);
		// 2층
		Kind[] f2 = {Kind.FORGE, Kind.ALCHEMY, Kind.HOUSE, Kind.MARKET, Kind.GARDEN, Kind.HOUSE};
		for (int k = 0; k < 6; k++) { lot(f2[k], 1, 30 + k * 60, 54, 200 + k); pillar(1, k * 60, 68); }
		// 3층
		Kind[] f3 = {Kind.ENCHANT, Kind.STALL, Kind.MAGE_LAB, Kind.REPAIR};
		for (int k = 0; k < 4; k++) { lot(f3[k], 2, 45 + k * 90, 44, 300 + k); pillar(2, k * 90, 52); }
		// 4층
		Kind[] f4 = {Kind.RIFT_GATE, Kind.REPAIR, Kind.STALL};
		for (int k = 0; k < 3; k++) { lot(f4[k], 3, 90 + k * 120, 34, 400 + k); pillar(3, 30 + k * 120, 38); }

		for (Lot l : LOTS) {
			int y = l.y() + 1;
			switch (l.kind) {
				case SAGE_HALL -> SAGE_POS = new BlockPos(l.x, y, l.z);
				case MARKET -> { npc("merchant", l, -9, 0, y); npc("merchant", l, 9, 0, y); }
				case SMITH, FORGE -> npc("blacksmith", l, 0, -2, y);
				case REPAIR -> npc("blacksmith", l, 0, -1, y);
				case ALCHEMY -> npc("alchemist", l, 0, -1, y);
				case STALL -> npc("merchant", l, 0, 0, y);
				case ENCHANT -> npc("enchanter", l, 0, -3, y);
				default -> { }
			}
		}
		// 5층 왕성 안뜰의 왕실 상인들
		NPCS.add(new Npc("merchant", new BlockPos(-14, FY[4] + 1, 16)));
		NPCS.add(new Npc("enchanter", new BlockPos(14, FY[4] + 1, 16)));
		NPCS.add(new Npc("alchemist", new BlockPos(-16, FY[4] + 1, -12)));
		NPCS.add(new Npc("blacksmith", new BlockPos(16, FY[4] + 1, -12)));
		// 용 발판 4개
		int[][] pads = {{-13, 6}, {-5, 13}, {5, 13}, {13, 6}};
		for (int[] p : pads) DRAGON_PADS.add(new BlockPos(ROOST_X + p[0], G + 1, ROOST_Z + p[1]));
	}

	private static void lot(Kind k, int floor, double deg, double r, int seed) {
		double a = Math.toRadians(deg);
		int x = (int) Math.round(Math.cos(a) * r), z = (int) Math.round(Math.sin(a) * r);
		LOTS.add(new Lot(k, floor, x, z, faceCenter(x, z), seed));
	}

	private static void pillar(int floor, double deg, double r) {
		double a = Math.toRadians(deg);
		PILLARS.add(new Pillar(floor, (int) Math.round(Math.cos(a) * r), (int) Math.round(Math.sin(a) * r)));
	}

	private static void npc(String type, Lot l, int lx, int lz, int y) { NPCS.add(new Npc(type, new BlockPos(wx(l, lx, lz), y, wz(l, lx, lz)))); }

	static int wx(Lot l, int lx, int lz) { return switch (l.rot) { case 1 -> l.x - lz; case 2 -> l.x - lx; case 3 -> l.x + lz; default -> l.x + lx; }; }
	static int wz(Lot l, int lx, int lz) { return switch (l.rot) { case 1 -> l.z + lx; case 2 -> l.z - lz; case 3 -> l.z - lx; default -> l.z + lz; }; }

	/** 정면(+z 로컬)이 가운데 탑 쪽을 보게 하는 회전 */
	private static int faceCenter(int x, int z) {
		double dx = -x, dz = -z;
		if (Math.abs(dx) > Math.abs(dz)) return dx < 0 ? 1 : 3;
		return dz > 0 ? 0 : 2;
	}

	/** 지금 서 있는 층 (0..4) */
	public static int floorAt(double y) {
		int f = 0;
		for (int k = 0; k < FY.length; k++) if (y >= FY[k] - 2) f = k;
		return f;
	}

	// ---------------- 그리기 ----------------
	private final WorldGenLevel lv;
	private final int minX, minZ, maxX, maxZ;

	public Capital(WorldGenLevel lv, int minX, int minZ) { this.lv = lv; this.minX = minX; this.minZ = minZ; this.maxX = minX + 15; this.maxZ = minZ + 15; }

	private boolean in(int x, int z) { return x >= minX && x <= maxX && z >= minZ && z <= maxZ; }
	private void set(int x, int y, int z, BlockState s) { if (in(x, z) && y > -60 && y < 319) lv.setBlock(new BlockPos(x, y, z), s, 2); }
	private BlockState get(int x, int y, int z) { return in(x, z) ? lv.getBlockState(new BlockPos(x, y, z)) : Blocks.AIR.defaultBlockState(); }   // 이 청크 밖은 읽지 않음
	private boolean air(int x, int y, int z) { return get(x, y, z).isAir(); }
	private static BlockState st(Block b) { return b.defaultBlockState(); }
	private static BlockState b(String id) { return BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("minecraft", id)).defaultBlockState(); }
	private static int h(int x, int y, int z) { int n = x * 73856093 ^ y * 19349663 ^ z * 83492791; n ^= n >>> 13; n *= 0x5bd1e995; return (n ^ n >>> 15) & 0x7fffffff; }
	private BlockState brick(int x, int y, int z) { int k = h(x, y, z) % 11; return st(k == 0 ? Blocks.MOSSY_STONE_BRICKS : k == 1 ? Blocks.CRACKED_STONE_BRICKS : Blocks.STONE_BRICKS); }

	private static BlockState stairs(String id) { return b(id + "_stairs"); }
	private static BlockState slab(String id) { return b(id + "_slab"); }

	/** 이 기둥에 바닥 높이가 Y인 층이 있는지 */
	static boolean hasFloor(int x, int z, int Y) {
		float[] c = SkyLayout.column(x, z);
		for (int i = 0; i < c.length; i += 2) if ((int) Math.ceil(c[i + 1]) - 1 == Y) return true;
		return false;
	}

	/** (x,z)에서 y0 위로 처음 만나는 윗층 바닥 밑면 */
	static int ceiling(int x, int z, int y0) {
		float[] c = SkyLayout.column(x, z);
		int best = 400;
		for (int i = 0; i < c.length; i += 2) if (c[i] > y0 + 1 && c[i] < best) best = (int) Math.floor(c[i]);
		return best;
	}

	public void paint() {
		if (maxX < -150 || minX > 150 || maxZ < -150 || minZ > 150) return;
		for (int f = 0; f < FY.length; f++) {
			int R = FR[f] + 2;
			if (maxX < -R || minX > R || maxZ < -R || minZ > R) continue;
			ground(f);
		}
		for (Lot l : LOTS) {
			int r = l.half() + 3;
			if (l.x + r < minX || l.x - r > maxX || l.z + r < minZ || l.z - r > maxZ) continue;
			Bld o = new Bld(l);
			o.clear(-l.half(), l.half());
			switch (l.kind) {
				case SMITH -> smith(o, "deepslate_tile", "대장간");
				case FORGE -> smith(o, "dark_oak", "대장장이 공방");
				case INN -> inn(o);
				case MARKET -> market(o);
				case SAGE_HALL -> sageHall(o);
				case MAGE_LAB -> mageLab(o);
				case TRAINING -> training(o);
				case RIFT_GATE -> riftGate(o);
				case STORAGE -> storage(o);
				case HOUSE -> house(o);
				case ALCHEMY -> alchemy(o);
				case STALL -> stall(o);
				case REPAIR -> repair(o);
				case ENCHANT -> enchant(o);
				case GARDEN -> garden(o);
				default -> { }
			}
		}
		for (int f = 0; f < FY.length; f++) decor(f);
		for (Pillar p : PILLARS) if (p.x + 5 >= minX && p.x - 5 <= maxX && p.z + 5 >= minZ && p.z - 5 <= maxZ) pillar(p);
		underside();
		if (maxX >= ROOST_X - ROOST_R - 3 && minX <= ROOST_X + ROOST_R + 3 && maxZ >= ROOST_Z - ROOST_R - 3 && minZ <= ROOST_Z + ROOST_R + 3) roost();
		if (maxX >= -40 && minX <= 40 && maxZ >= -40 && minZ <= 40) castle();
		if (maxX >= -12 && minX <= 12 && maxZ >= -12 && minZ <= 12) core();
	}

	// ---------------- 층 바닥: 광장 · 큰길 · 가장자리 성벽 ----------------
	private boolean avenue1(double x, double z, double r) {
		if (r < 10) return false;
		for (int k = 0; k < 7; k++) {
			double a = SkyLayout.regionAngle(k);
			double along = x * Math.cos(a) + z * Math.sin(a), across = Math.abs(-x * Math.sin(a) + z * Math.cos(a));
			if (along > 0 && across < 3.2) return true;
		}
		return false;
	}

	private boolean spoke(int f, double x, double z, double r) {
		if (f == 0) return avenue1(x, z, r);
		for (Lot l : LOTS) {
			if (l.floor != f) continue;
			double L = Math.hypot(l.x, l.z), ux = l.x / L, uz = l.z / L;
			double along = x * ux + z * uz, across = Math.abs(-x * uz + z * ux);
			if (along > 0 && along < L - l.half() + 1 && across < 1.6) return true;
		}
		return false;
	}

	private boolean inLot(int f, int x, int z, int pad) {
		for (Lot l : LOTS) if (l.floor == f && Math.abs(l.x - x) <= l.half() + pad && Math.abs(l.z - z) <= l.half() + pad) return true;
		if (f == 0 && Math.hypot(x - ROOST_X, z - ROOST_Z) < ROOST_R + pad) return true;
		return false;
	}

	private void ground(int f) {
		int Y = FY[f], R = FR[f];
		double plaza = f == 0 ? 30 : f == 4 ? 0 : 18;
		for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
			double r = Math.hypot(x + 0.5, z + 0.5);
			if (r > R + 1 || r <= CORE_R + 0.5) continue;
			if (!hasFloor(x, z, Y)) continue;
			int k = h(x, f, z) % 10;
			if (r < plaza) {   // 광장: 동심원 무늬
				int ring = (int) r;
				BlockState s = ring % 7 == 0 ? st(Blocks.CHISELED_STONE_BRICKS) : ring % 7 == 3 ? st(Blocks.POLISHED_ANDESITE) : (k < 6 ? st(Blocks.STONE_BRICKS) : k < 8 ? st(Blocks.SMOOTH_STONE) : st(Blocks.CRACKED_STONE_BRICKS));
				set(x, Y, z, s);
				clearAbove(x, Y, z, 5);
			} else if (spoke(f, x + 0.5, z + 0.5, r)) {
				set(x, Y, z, k < 4 ? st(Blocks.POLISHED_ANDESITE) : k < 7 ? st(Blocks.STONE_BRICKS) : k < 9 ? st(Blocks.COBBLESTONE) : st(Blocks.MOSSY_COBBLESTONE));
				clearAbove(x, Y, z, 5);
			} else if (f == 0 && Math.abs(r - 40) < 1.6) {
				set(x, Y, z, k < 3 ? st(Blocks.COARSE_DIRT) : b("dirt_path"));
				clearAbove(x, Y, z, 4);
			}
			// 가장자리 낮은 성벽 + 등불 기둥 (1층은 대교·둥지 쪽이 트임)
			if (r > R - 2.6 && r <= R - 1.5) {
				boolean open = f == 0 && (avenue1(x + 0.5, z + 0.5, r) || Math.hypot(x - ROOST_X, z - ROOST_Z) < ROOST_R + 1);
				if (!open) {
					double th = Math.atan2(z + 0.5, x + 0.5);
					boolean post = Math.floorMod((int) Math.round(th * R / 9.0), 1) == 0 && Math.floorMod((int) Math.round(th * R), 9) == 0;
					set(x, Y + 1, z, post ? st(Blocks.CHISELED_STONE_BRICKS) : (k < 2 ? st(Blocks.MOSSY_STONE_BRICK_WALL) : st(Blocks.STONE_BRICK_WALL)));
					if (post) { set(x, Y + 2, z, st(Blocks.STONE_BRICK_WALL)); set(x, Y + 3, z, st(Blocks.LANTERN)); }
				}
			}
		}
		// 1층 큰길 가로등 + 화로
		if (f == 0) for (int k = 0; k < 7; k++) {
			double a = SkyLayout.regionAngle(k);
			for (int d = 36; d <= R - 6; d += 14) for (int s = -1; s <= 1; s += 2) {
				int x = (int) Math.round(Math.cos(a) * d - Math.sin(a) * 4.6 * s), z = (int) Math.round(Math.sin(a) * d + Math.cos(a) * 4.6 * s);
				if (!in(x, z) || inLot(0, x, z, 1)) continue;
				if (d == 36 + 28) brazier(x, Y, z); else lamp(x, Y, z);
			}
		}
		// 광장 둘레 화로 8개
		if (plaza > 0) for (int k = 0; k < 8; k++) {
			double a = Math.toRadians(k * 45 + 22.5);
			int x = (int) Math.round(Math.cos(a) * (plaza - 2)), z = (int) Math.round(Math.sin(a) * (plaza - 2));
			if (in(x, z) && !spoke(f, x + 0.5, z + 0.5, plaza)) brazier(x, Y, z);
		}
	}

	private void clearAbove(int x, int Y, int z, int n) { for (int y = Y + 1; y <= Y + n; y++) if (!air(x, y, z)) set(x, y, z, st(Blocks.AIR)); }

	private void lamp(int x, int Y, int z) {
		clearAbove(x, Y, z, 5);
		set(x, Y + 1, z, st(Blocks.POLISHED_BLACKSTONE_WALL));
		set(x, Y + 2, z, st(Blocks.POLISHED_BLACKSTONE_WALL));
		set(x, Y + 3, z, st(Blocks.POLISHED_BLACKSTONE_WALL));
		set(x, Y + 4, z, st(Blocks.LANTERN));
	}

	/** 돌 화로 (모닥불) */
	private void brazier(int x, int Y, int z) {
		clearAbove(x, Y, z, 4);
		set(x, Y + 1, z, st(Blocks.CHISELED_STONE_BRICKS));
		set(x, Y + 2, z, b("campfire"));
	}

	// ---------------- 층 장식: 나무 · 모닥불 쉼터 · 짐수레 · 우물 · 꽃밭 ----------------
	private void decor(int f) {
		int Y = FY[f], R = FR[f];
		if (maxX < -R || minX > R || maxZ < -R || minZ > R) return;
		int cell = 9;
		for (int cx = Math.floorDiv(minX, cell) - 1; cx <= Math.floorDiv(maxX, cell) + 1; cx++)
			for (int cz = Math.floorDiv(minZ, cell) - 1; cz <= Math.floorDiv(maxZ, cell) + 1; cz++) {
				int s = h(cx, 31 + f, cz);
				int x = cx * cell + 2 + s % 5, z = cz * cell + 2 + (s / 5) % 5;
				double r = Math.hypot(x, z);
				double plaza = f == 0 ? 32 : 20;
				if (r < plaza || r > R - 5 || f == 4) continue;
				if (inLot(f, x, z, 3) || spoke(f, x + 0.5, z + 0.5, r) || spoke(f, x + 3.5, z + 0.5, r) || spoke(f, x - 2.5, z + 0.5, r) || spoke(f, x + 0.5, z + 3.5, r) || spoke(f, x + 0.5, z - 2.5, r)) continue;
				if (f == 0 && Math.abs(r - 40) < 4) continue;
				boolean nearPillar = false;
				for (Pillar p : PILLARS) if ((p.floor == f || p.floor == f - 1) && Math.hypot(p.x - x, p.z - z) < 6) nearPillar = true;
				if (nearPillar) continue;
				if (!hasFloor(x, z, Y)) continue;
				if (ceiling(x, z, Y) - Y < 9) continue;
				int k = (s / 25) % 100;
				if (k < 42) tree(x, Y, z, s);
				else if (k < 52) lamp(x, Y, z);
				else if (k < 60) campfireRing(x, Y, z, s);
				else if (k < 68) cart(x, Y, z, s);
				else if (k < 74) bench(x, Y, z, s);
				else if (k < 78) well(x, Y, z);
				else if (k < 84) haystack(x, Y, z, s);
				else flowerBed(x, Y, z, s);
			}
	}

	private static final String[][] TREES = {{"oak_log", "oak_leaves"}, {"birch_log", "birch_leaves"}, {"spruce_log", "spruce_leaves"}, {"oak_log", "azalea_leaves"}, {"dark_oak_log", "dark_oak_leaves"}};

	/** 손질한 둥근 나무 (초록 잎) */
	private void tree(int x, int Y, int z, int s) {
		String[] t = TREES[s % TREES.length];
		BlockState leaf = b(t[1]).setValue(BlockStateProperties.PERSISTENT, true), log = b(t[0]);
		boolean tall = t[0].startsWith("spruce");
		int H = 5 + s % 3 + (tall ? 2 : 0);
		for (int dx = -3; dx <= 3; dx++) for (int dz = -3; dz <= 3; dz++) for (int dy = -3; dy <= 3; dy++) {
			double d = tall ? Math.hypot(dx, dz) + Math.max(0, dy) * 0.9 - (dy < -1 ? 0.5 : 0) : Math.sqrt(dx * dx + dz * dz + dy * dy * 1.5);
			if (d <= (tall ? 2.6 : 3.0) - (h(x + dx, dy, z + dz) % 4 == 0 ? 0.6 : 0) && air(x + dx, Y + H + dy, z + dz)) set(x + dx, Y + H + dy, z + dz, leaf);
		}
		for (int y = Y + 1; y < Y + H + 1; y++) set(x, y, z, log);
		set(x, Y, z, st(Blocks.GRASS_BLOCK));
		if (h(x, 5, z) % 3 == 0) set(x + 1, Y + 1, z, st(Blocks.FERN));
	}

	/** 모닥불 + 통나무 의자 4개 */
	private void campfireRing(int x, int Y, int z, int s) {
		for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) { set(x + dx, Y, z + dz, Math.abs(dx) + Math.abs(dz) <= 1 ? st(Blocks.GRAVEL) : b("dirt_path")); clearAbove(x + dx, Y, z + dz, 3); }
		set(x, Y + 1, z, b("campfire"));
		set(x + 2, Y + 1, z, b("stripped_spruce_log").setValue(BlockStateProperties.AXIS, Direction.Axis.Z));
		set(x - 2, Y + 1, z, b("stripped_spruce_log").setValue(BlockStateProperties.AXIS, Direction.Axis.Z));
		set(x, Y + 1, z + 2, b("stripped_spruce_log").setValue(BlockStateProperties.AXIS, Direction.Axis.X));
		if (s % 2 == 0) set(x, Y + 1, z - 2, st(Blocks.BARREL));
	}

	/** 짐수레: 바퀴(덫문) + 짐칸 + 건초·통 */
	private void cart(int x, int Y, int z, int s) {
		boolean ew = s % 2 == 0;
		for (int i = -1; i <= 1; i++) {
			int px = ew ? x + i : x, pz = ew ? z : z + i;
			clearAbove(px, Y, pz, 3);
			set(px, Y + 1, pz, st(Blocks.SPRUCE_PLANKS));
		}
		set(ew ? x - 1 : x, Y + 2, ew ? z : z - 1, st(Blocks.HAY_BLOCK));
		set(ew ? x + 1 : x, Y + 2, ew ? z : z + 1, st(Blocks.BARREL));
		set(ew ? x + 2 : x, Y + 1, ew ? z : z + 2, st(Blocks.SPRUCE_FENCE));
		Direction d = ew ? Direction.NORTH : Direction.WEST;
		set(ew ? x : x - 1, Y + 1, ew ? z - 1 : z, b("spruce_trapdoor").setValue(BlockStateProperties.HORIZONTAL_FACING, d).setValue(BlockStateProperties.OPEN, true));
		set(ew ? x : x + 1, Y + 1, ew ? z + 1 : z, b("spruce_trapdoor").setValue(BlockStateProperties.HORIZONTAL_FACING, d.getOpposite()).setValue(BlockStateProperties.OPEN, true));
	}

	private void bench(int x, int Y, int z, int s) {
		boolean ew = s % 2 == 0;
		for (int i = -1; i <= 1; i++) {
			int px = ew ? x + i : x, pz = ew ? z : z + i;
			clearAbove(px, Y, pz, 3);
			set(px, Y + 1, pz, b("spruce_stairs").setValue(BlockStateProperties.HORIZONTAL_FACING, ew ? Direction.NORTH : Direction.WEST));
		}
		set(ew ? x : x + 1, Y + 1, ew ? z + 1 : z, b("potted_red_tulip"));
		lampSmall(ew ? x + 2 : x, Y, ew ? z : z + 2);
	}

	private void lampSmall(int x, int Y, int z) { clearAbove(x, Y, z, 3); set(x, Y + 1, z, st(Blocks.SPRUCE_FENCE)); set(x, Y + 2, z, st(Blocks.LANTERN)); }

	/** 돌 우물 + 지붕 */
	private void well(int x, int Y, int z) {
		for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) {
			clearAbove(x + dx, Y, z + dz, 5);
			boolean rim = Math.max(Math.abs(dx), Math.abs(dz)) == 2, inner = Math.max(Math.abs(dx), Math.abs(dz)) <= 1;
			if (inner) { set(x + dx, Y, z + dz, st(Blocks.WATER)); set(x + dx, Y - 1, z + dz, st(Blocks.STONE_BRICKS)); }
			else if (rim) { set(x + dx, Y, z + dz, st(Blocks.COBBLESTONE)); set(x + dx, Y + 1, z + dz, st(Blocks.MOSSY_COBBLESTONE_WALL)); }
			if (Math.abs(dx) == 2 && Math.abs(dz) == 2) { set(x + dx, Y + 2, z + dz, st(Blocks.SPRUCE_FENCE)); set(x + dx, Y + 3, z + dz, st(Blocks.SPRUCE_FENCE)); }
			if (Math.abs(dx) <= 2 && Math.abs(dz) <= 2) set(x + dx, Y + 4, z + dz, Math.abs(dz) == 2 ? b("spruce_slab") : b("spruce_planks"));
		}
		set(x, Y + 3, z, st(Blocks.IRON_CHAIN));
		set(x, Y + 2, z, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true));
	}

	private void haystack(int x, int Y, int z, int s) {
		for (int dx = 0; dx <= 1; dx++) for (int dz = 0; dz <= 1; dz++) { clearAbove(x + dx, Y, z + dz, 3); set(x + dx, Y + 1, z + dz, st(Blocks.HAY_BLOCK)); }
		set(x, Y + 2, z, st(Blocks.HAY_BLOCK));
		set(x + 2, Y + 1, z, s % 2 == 0 ? st(Blocks.BARREL) : b("composter"));
		set(x - 1, Y + 1, z + 1, b("pumpkin"));
	}

	private void flowerBed(int x, int Y, int z, int s) {
		String[] fl = {"poppy", "cornflower", "allium", "oxeye_daisy", "azure_bluet", "lily_of_the_valley", "red_tulip", "orange_tulip"};
		for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) {
			if (Math.abs(dx) + Math.abs(dz) > 3 || !air(x + dx, Y + 1, z + dz)) continue;
			if (h(x + dx, 3, z + dz) % 3 != 0) set(x + dx, Y + 1, z + dz, b(fl[(s + dx * 3 + dz) & 7]));
		}
		set(x, Y + 1, z, st(Blocks.FLOWERING_AZALEA));
	}

	// ---------------- 층 사이 돌기둥 ----------------
	private void pillar(Pillar p) {
		int Y = FY[p.floor];
		for (int x = p.x - 4; x <= p.x + 4; x++) for (int z = p.z - 4; z <= p.z + 4; z++) {
			if (!in(x, z)) continue;
			double r = Math.hypot(x - p.x, z - p.z);
			int top = Math.min(ceiling(x, z, Y), FY[p.floor + 1] - 1);
			if (top >= 399) top = FY[p.floor + 1] - 4;
			if (r <= 3.5 && r > 2.3) {   // 받침돌
				set(x, Y + 1, z, st(Blocks.STONE_BRICKS)); set(x, Y + 2, z, b("stone_brick_slab"));
				set(x, top - 1, z, st(Blocks.STONE_BRICKS));
			}
			if (r <= 2.3) for (int y = Y + 1; y <= top; y++) {
				int dy = y - Y;
				set(x, y, z, dy % 9 == 0 ? st(Blocks.CHISELED_STONE_BRICKS) : r > 1.6 && h(x, y, z) % 13 == 0 ? st(Blocks.MOSSY_STONE_BRICKS) : brick(x, y, z));
			}
			// 기둥에 걸린 등불·덩굴
			if (Math.abs(r - 3.0) < 0.5 && (x == p.x || z == p.z)) {
				set(x, Y + 5, z, st(Blocks.LANTERN));
				for (int y = top - 1; y > top - 6 && y > Y + 7; y--) if (air(x, y, z)) set(x, y, z, b("vine").setValue(x > p.x ? BlockStateProperties.WEST : x < p.x ? BlockStateProperties.EAST : z > p.z ? BlockStateProperties.NORTH : BlockStateProperties.SOUTH, true));
			}
		}
	}

	// ---------------- 윗층 밑면: 매달린 등불 · 빛나는 수정 · 사슬 ----------------
	private void underside() {
		for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
			double r = Math.hypot(x, z);
			if (r < CORE_R + 2) continue;
			for (int f = 1; f < FY.length; f++) {
				if (r > FR[f] - 2) continue;
				int bottom = ceiling(x, z, FY[f - 1] + 1);
				if (bottom >= 399 || bottom > FY[f]) continue;
				int k = h(x, f * 7, z) % 260;
				if (k == 0) {   // 사슬 + 등불
					int n = 2 + h(x, 1, z) % 4;
					for (int y = bottom - 1; y >= bottom - n; y--) if (air(x, y, z)) set(x, y, z, st(Blocks.IRON_CHAIN));
					if (air(x, bottom - n - 1, z)) set(x, bottom - n - 1, z, (h(x, 2, z) % 3 == 0 ? st(Blocks.SOUL_LANTERN) : st(Blocks.LANTERN)).setValue(BlockStateProperties.HANGING, true));
				} else if (k < 4) {   // 종유석 + 빛나는 끝
					int n = 2 + h(x, 3, z) % 5;
					for (int y = bottom - 1; y >= bottom - n; y--) if (air(x, y, z)) set(x, y, z, y == bottom - n ? st(Blocks.SHROOMLIGHT) : y > bottom - 2 ? st(Blocks.STONE) : st(Blocks.ANDESITE));
				} else if (k < 7 && air(x, bottom - 1, z)) set(x, bottom - 1, z, st(Blocks.GLOW_LICHEN).setValue(BlockStateProperties.UP, true));
			}
		}
	}

	// ---------------- 가운데 탑 (승강기) ----------------
	private void core() {
		for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
			double r = Math.hypot(x + 0.5, z + 0.5);
			if (r > CORE_R + 0.6) continue;
			double th = Math.toDegrees(Math.atan2(z + 0.5, x + 0.5));
			boolean doorDir = Math.abs(((th % 90) + 90) % 90 - 45) > 45 - 17;    // 동서남북 4방향 문
			for (int y = FY[0]; y <= TOP + 22; y++) {
				int f = floorAt(y), Yf = FY[f], dy = y - Yf;
				if (y > TOP) {   // 왕성 첨탑 지붕
					double rr = (CORE_R + 2) * (1 - (y - TOP) / 22.0);
					if (r <= rr + 0.5 && r > rr - 1.4) set(x, y, z, b("waxed_oxidized_cut_copper"));
					else if (r <= rr - 1.4) set(x, y, z, st(Blocks.AIR));
					if (r < 0.8 && y >= TOP + 20) set(x, y, z, y == TOP + 22 ? b("lightning_rod") : st(Blocks.GOLD_BLOCK));
					continue;
				}
				if (r <= SHAFT_R + 0.5) {   // 빛 기둥 (승강기 통로)
					if (y == FY[0]) set(x, y, z, b("pearlescent_froglight"));
					else if (y >= TOP) set(x, y, z, st(Blocks.SEA_LANTERN));
					else set(x, y, z, st(Blocks.AIR));
					continue;
				}
				if (r <= CORE_R - 1) {   // 층마다 둥근 복도, 그 사이는 빈 공간
					if (dy == 0) set(x, y, z, r <= SHAFT_R + 1.5 ? (Math.floorMod((int) Math.round(th / 30), 2) == 0 ? st(Blocks.SEA_LANTERN) : st(Blocks.POLISHED_DEEPSLATE)) : ((int) r % 2 == 0 ? st(Blocks.CALCITE) : st(Blocks.POLISHED_DEEPSLATE)));
					else if (y == TOP) set(x, y, z, st(Blocks.DEEPSLATE_TILES));
					else set(x, y, z, st(Blocks.AIR));
					continue;
				}
				// 탑 벽
				boolean door = doorDir && dy >= 1 && dy <= 5 && (f < 4 || dy <= 5);
				if (door) { set(x, y, z, st(Blocks.AIR)); continue; }
				boolean band = dy == 0 || dy == 6;
				boolean win = !band && dy % 7 >= 2 && dy % 7 <= 4 && Math.floorMod((int) Math.round(th), 30) < 8;
				BlockState s = band ? st(Blocks.CHISELED_STONE_BRICKS) : win ? b("light_blue_stained_glass") : (h(x, y, z) % 9 == 0 ? st(Blocks.CRACKED_DEEPSLATE_BRICKS) : st(Blocks.DEEPSLATE_BRICKS));
				if (y == TOP) s = st(Blocks.POLISHED_DEEPSLATE);
				set(x, y, z, s);
				if (!band && dy % 7 == 5 && Math.floorMod((int) Math.round(th), 45) == 0 && r < CORE_R - 0.4) set(x, y, z, st(Blocks.SEA_LANTERN));
			}
		}
		// 층마다 문 위 간판
		for (int f = 0; f < FY.length; f++) {
			coreSign(0, FY[f] + 6, CORE_R + 1, Direction.SOUTH, f);
			coreSign(0, FY[f] + 6, -CORE_R - 1, Direction.NORTH, f);
		}
	}

	private void coreSign(int x, int y, int z, Direction d, int f) {
		if (!in(x, z)) return;
		set(x, y, z, b("dark_oak_wall_sign").setValue(BlockStateProperties.HORIZONTAL_FACING, d));
		try {
			if (lv.getBlockEntity(new BlockPos(x, y, z)) instanceof SignBlockEntity sb) {
				List<Component> lines = List.of(Component.literal(FLOOR_NAME[f]), Component.literal("빛 기둥: 상승"), Component.literal("Shift: 하강"), Component.empty());
				sb.setText(new SignText(lines, lines, net.minecraft.world.item.DyeColor.WHITE, true), net.minecraft.world.level.block.entity.SignTextSlot.FRONT);
			}
		} catch (Throwable ignored) {}
	}

	// ---------------- 1층 용의 둥지 ----------------
	private void roost() {
		int Y = G;
		for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
			double dx = x - ROOST_X, dz = z - ROOST_Z, r = Math.hypot(dx, dz);
			if (r > ROOST_R + 0.5) continue;
			clearAbove(x, Y, z, 10);
			int k = h(x, 77, z) % 10;
			BlockState s = Math.abs(r - ROOST_R + 1) < 1 ? st(Blocks.CHISELED_STONE_BRICKS) : ((int) r % 5 == 0 ? st(Blocks.POLISHED_ANDESITE) : k < 7 ? st(Blocks.STONE_BRICKS) : st(Blocks.MOSSY_STONE_BRICKS));
			set(x, Y, z, s);
			// 섬 밖으로 튀어나온 부분 받침 (거꾸로 된 계단식)
			boolean overVoid = Math.hypot(x, z) > FR[0] - 3;
			if (overVoid) {
				int depth = 2 + (int) ((ROOST_R - r) * 0.8);
				for (int y = Y - 1; y >= Y - depth; y--) if (air(x, y, z)) set(x, y, z, brick(x, y, z));
				if (h(x, 4, z) % 23 == 0) { for (int y = Y - depth - 1; y > Y - depth - 4; y--) set(x, y, z, st(Blocks.IRON_CHAIN)); set(x, Y - depth - 4, z, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true)); }
			}
			// 가장자리 난간 (바깥쪽만) + 깃대
			if (r > ROOST_R - 0.6 && dz > 2) {
				double th = Math.atan2(dz, dx);
				boolean post = Math.floorMod((int) Math.round(th * ROOST_R), 7) == 0;
				set(x, Y + 1, z, post ? st(Blocks.CHISELED_STONE_BRICKS) : st(Blocks.STONE_BRICK_WALL));
				if (post) { for (int y = Y + 2; y <= Y + 6; y++) set(x, y, z, st(Blocks.SPRUCE_FENCE)); set(x, Y + 7, z, st(Blocks.LANTERN)); }
			}
		}
		// 용 발판 (빛나는 원)
		for (BlockPos p : DRAGON_PADS) for (int x = p.getX() - 3; x <= p.getX() + 3; x++) for (int z = p.getZ() - 3; z <= p.getZ() + 3; z++) {
			if (!in(x, z)) continue;
			double r = Math.hypot(x - p.getX(), z - p.getZ());
			if (r <= 3.4 && r > 2.5) set(x, Y, z, Math.floorMod(x + z, 2) == 0 ? b("pearlescent_froglight") : st(Blocks.AMETHYST_BLOCK));
			else if (r <= 2.5) set(x, Y, z, st(Blocks.SMOOTH_QUARTZ));
		}
		// 둥지 장식: 건초 더미, 물그릇, 고기 걸이, 안내판
		int[][] hay = {{-18, -6}, {18, -6}, {-19, -3}, {19, -3}};
		for (int[] hp : hay) if (in(ROOST_X + hp[0], ROOST_Z + hp[1])) { set(ROOST_X + hp[0], Y + 1, ROOST_Z + hp[1], st(Blocks.HAY_BLOCK)); set(ROOST_X + hp[0], Y + 2, ROOST_Z + hp[1], st(Blocks.HAY_BLOCK)); }
		if (in(-9, ROOST_Z - 4)) set(ROOST_X - 9, Y + 1, ROOST_Z - 4, b("water_cauldron").setValue(BlockStateProperties.LEVEL_CAULDRON, 3));
		if (in(9, ROOST_Z - 4)) set(ROOST_X + 9, Y + 1, ROOST_Z - 4, b("water_cauldron").setValue(BlockStateProperties.LEVEL_CAULDRON, 3));
		brazierIf(ROOST_X - 6, Y, ROOST_Z - 12); brazierIf(ROOST_X + 6, Y, ROOST_Z - 12);
		if (in(0, ROOST_Z - 16)) {
			set(0, Y + 1, ROOST_Z - 16, st(Blocks.SPRUCE_FENCE));
			set(0, Y + 2, ROOST_Z - 16, b("dark_oak_sign").setValue(BlockStateProperties.ROTATION_16, 8));
			try {
				if (lv.getBlockEntity(new BlockPos(0, Y + 2, ROOST_Z - 16)) instanceof SignBlockEntity sb) {
					List<Component> lines = List.of(Component.literal("용의 둥지"), Component.literal("용을 우클릭해 타기"), Component.literal("W·Space로 비행"), Component.literal("Shift: 내리기"));
					sb.setText(new SignText(lines, lines, net.minecraft.world.item.DyeColor.WHITE, true), net.minecraft.world.level.block.entity.SignTextSlot.FRONT);
				}
			} catch (Throwable ignored) {}
		}
	}

	private void brazierIf(int x, int Y, int z) { if (in(x, z)) brazier(x, Y, z); }

	// ---------------- 5층 천공의 왕성 ----------------
	private void castle() {
		int Y = FY[4];
		for (int x = Math.max(minX, -40); x <= Math.min(maxX, 40); x++) for (int z = Math.max(minZ, -40); z <= Math.min(maxZ, 40); z++) {
			double r = Math.hypot(x + 0.5, z + 0.5), th = Math.toDegrees(Math.atan2(z + 0.5, x + 0.5));
			if (r > 40 || r <= CORE_R + 0.6) continue;
			if (!hasFloor(x, z, Y)) continue;
			// 안뜰 바닥 (붉은 융단 길 + 체크무늬)
			boolean carpet = Math.abs(x) <= 1 && z > CORE_R && z < 36;
			set(x, Y, z, ((x + z) & 1) == 0 ? st(Blocks.POLISHED_ANDESITE) : st(Blocks.CALCITE));
			clearAbove(x, Y, z, 14);
			if (carpet) set(x, Y + 1, z, b("red_carpet"));
			// 성벽 (반지름 35~37, 높이 9, 남쪽 정문)
			if (r > 34.5 && r <= 37.5) {
				boolean gate = z > 0 && Math.abs(x) <= 2;
				for (int y = Y + 1; y <= Y + 9; y++) { if (gate && y <= Y + 5) continue; set(x, y, z, y == Y + 9 ? st(Blocks.POLISHED_ANDESITE) : brick(x, y, z)); }
				if (r > 36.5 && Math.floorMod((int) Math.round(th * 1.6), 2) == 0) set(x, Y + 10, z, st(Blocks.STONE_BRICK_WALL));
				if (r <= 35.5 && Math.floorMod((int) Math.round(th), 20) == 0) set(x, Y + 7, z, st(Blocks.LANTERN));
			}
			// 모서리 원형 탑 4개
			for (int q = 0; q < 4; q++) {
				double a = Math.toRadians(45 + q * 90);
				double tx = Math.cos(a) * 36, tz = Math.sin(a) * 36, tr = Math.hypot(x - tx, z - tz);
				if (tr <= 5.5) {
					for (int y = Y + 1; y <= Y + 16; y++) set(x, y, z, tr > 4.3 ? ((y - Y) % 6 >= 2 && (y - Y) % 6 <= 3 && (Math.abs(x - Math.round(tx)) < 1 || Math.abs(z - Math.round(tz)) < 1) ? b("glass_pane") : brick(x, y, z)) : ((y - Y) % 8 == 0 ? st(Blocks.SPRUCE_PLANKS) : st(Blocks.AIR)));
					for (int k = 0; k < 14; k++) {
						double rr = 6.5 * (1 - k / 14.0);
						if (tr <= rr + 0.5 && tr > rr - 1.2) set(x, Y + 17 + k, z, b("waxed_oxidized_cut_copper"));
					}
					if (tr < 0.7) { set(x, Y + 31, z, st(Blocks.GOLD_BLOCK)); set(x, Y + 32, z, b("lightning_rod")); }
				}
			}
			// 안뜰: 정원 화단, 왕실 깃발, 화로
			if (r > CORE_R + 3 && r < 33) {
				if (r > 24 && r < 30 && Math.floorMod((int) Math.round(th), 30) < 14 && !carpet) {
					set(x, Y, z, st(Blocks.GRASS_BLOCK));
					if (h(x, 1, z) % 3 == 0) set(x, Y + 1, z, h(x, 2, z) % 2 == 0 ? st(Blocks.ROSE_BUSH).setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER) : st(Blocks.LILY_OF_THE_VALLEY));
					if (get(x, Y + 1, z).is(Blocks.ROSE_BUSH)) set(x, Y + 2, z, st(Blocks.ROSE_BUSH).setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
				}
				if (Math.abs(r - 18) < 0.5 && Math.floorMod((int) Math.round(th), 45) == 22) { for (int y = Y + 1; y <= Y + 5; y++) set(x, y, z, st(Blocks.POLISHED_BLACKSTONE_WALL)); set(x, Y + 6, z, b("soul_campfire")); }
			}
			// 왕실 상인 노점 4개 (차양)
			for (Npc n : NPCS) if (n.pos().getY() == Y + 1 && Math.abs(n.pos().getX() - x) <= 2 && Math.abs(n.pos().getZ() - z) <= 2) {
				int ax = Math.abs(n.pos().getX() - x), az = Math.abs(n.pos().getZ() - z);
				if (ax == 2 && az == 2) for (int y = Y + 1; y <= Y + 3; y++) set(x, y, z, st(Blocks.DARK_OAK_FENCE));
				set(x, Y + 4, z, b((((x + z) & 1) == 0 ? "purple" : "yellow") + "_wool"));
			}
		}
	}

	// ---------------- 새 건물 ----------------

	/** 연금술 상점: 이끼 낀 돌집, 초록 유리창, 앞 계산대, 가마솥과 양조대 */
	private void alchemy(Bld o) {
		o.floor(-6, -5, 6, 5, 0, st(Blocks.MOSSY_COBBLESTONE));
		o.walls(-6, -5, 6, 1, 1, 5, st(Blocks.MOSSY_STONE_BRICKS), st(Blocks.SPRUCE_PLANKS), st(Blocks.STRIPPED_DARK_OAK_LOG));
		o.gable(-6, -5, 6, 1, 6, b("mossy_stone_brick_stairs"), b("mossy_stone_brick_slab"), st(Blocks.SPRUCE_PLANKS));
		for (int x = -5; x <= 5; x++) for (int y = 1; y <= 4; y++) o.p(x, y, 1, st(Blocks.AIR));      // 앞은 트인 가게
		for (int x = -6; x <= 6; x += 12) for (int y = 1; y <= 5; y++) o.p(x, y, 1, st(Blocks.STRIPPED_DARK_OAK_LOG));
		for (int x = -4; x <= 4; x++) if (x != 0) o.p(x, 1, 2, st(Blocks.SPRUCE_PLANKS));            // 계산대
		o.p(-3, 2, 2, st(Blocks.BREWING_STAND)); o.p(3, 2, 2, b("potted_red_mushroom")); o.p(-1, 2, 2, b("potted_brown_mushroom"));
		for (int x = -5; x <= 5; x++) o.p(x, 3, -4, x % 2 == 0 ? st(Blocks.BOOKSHELF) : b("lime_stained_glass"));
		o.p(-5, 1, -4, b("water_cauldron").setValue(BlockStateProperties.LEVEL_CAULDRON, 3)); o.p(-4, 1, -4, st(Blocks.BREWING_STAND)); o.p(5, 1, -4, st(Blocks.BARREL)); o.p(4, 1, -4, st(Blocks.BARREL));
		o.p(0, 4, -1, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true));
		o.chimney(4, -3, 5, 10);
		for (int x = -6; x <= 6; x++) o.p(x, 5, 2, b("lime_wool"));
		o.sign(0, 4, 2, "연금술 상점", "물약·황금 사과");
		o.flowers(-6, 4, -3, 5); o.flowers(3, 4, 6, 5);
		o.path(3, 5);
	}

	/** 노점: 차양 하나 + 통·상자 + 상인 */
	private void stall(Bld o) {
		o.floor(-5, -4, 5, 4, 0, st(Blocks.SMOOTH_STONE));
		for (int x = -3; x <= 3; x++) { o.p(x, 1, 1, st(Blocks.SPRUCE_PLANKS)); o.p(x, 2, 1, x % 2 == 0 ? st(Blocks.BARREL) : b("composter")); }
		o.p(0, 1, 1, st(Blocks.AIR)); o.p(0, 2, 1, st(Blocks.AIR));
		for (int y = 1; y <= 3; y++) for (int sx = -1; sx <= 1; sx += 2) { o.p(sx * 4, y, 2, st(Blocks.SPRUCE_FENCE)); o.p(sx * 4, y, -2, st(Blocks.SPRUCE_FENCE)); }
		String c = new String[]{"red", "blue", "orange", "lime"}[Math.floorMod(o.l.seed(), 4)];
		for (int x = -5; x <= 5; x++) for (int z = -3; z <= 3; z++) o.p(x, 4, z, b(((x + 5) % 2 == 0 ? c : "white") + "_wool"));
		o.p(-3, 1, -2, st(Blocks.CHEST)); o.p(3, 1, -2, st(Blocks.BARREL)); o.p(2, 1, -2, st(Blocks.HAY_BLOCK));
		o.p(0, 3, 0, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true));
		o.sign(2, 3, 3, "노점", "원소 상인");
	}

	/** 수리 공방: 네 기둥 지붕 아래 모루·숫돌·작업대·용광로, 대장장이 */
	private void repair(Bld o) {
		o.floor(-6, -5, 6, 5, 0, st(Blocks.STONE_BRICKS));
		for (int sx = -1; sx <= 1; sx += 2) for (int sz = -1; sz <= 1; sz += 2) for (int y = 1; y <= 4; y++) o.p(sx * 5, y, sz * 4, st(Blocks.STRIPPED_SPRUCE_LOG));
		o.gable(-5, -4, 5, 4, 5, stairs("spruce"), slab("spruce"), st(Blocks.SPRUCE_PLANKS));
		o.p(-2, 1, -2, st(Blocks.ANVIL)); o.p(0, 1, -3, st(Blocks.SMITHING_TABLE)); o.p(2, 1, -2, st(Blocks.GRINDSTONE));
		o.p(-4, 1, -3, st(Blocks.BLAST_FURNACE)); o.p(4, 1, -3, b("lava_cauldron"));
		o.p(-4, 1, 2, st(Blocks.BARREL)); o.p(4, 1, 2, st(Blocks.CHEST));
		o.p(0, 4, 0, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true));
		o.chimney(-4, -3, 2, 9);
		o.sign(0, 3, 5, "수리 공방", "대장장이");
		o.path(5, 4);
	}

	/** 마법부여사의 탑: 둥근 돌탑, 책장 고리, 마법부여대, 보라 유리, 원뿔 지붕 */
	private void enchant(Bld o) {
		int R = 6, H = 14;
		for (int x = -R - 1; x <= R + 1; x++) for (int z = -R - 1; z <= R + 1; z++) {
			double r = Math.hypot(x, z);
			if (r > R + 0.5) continue;
			o.p(x, 0, z, r < 2 ? st(Blocks.AMETHYST_BLOCK) : ((int) r % 2 == 0 ? st(Blocks.POLISHED_DEEPSLATE) : st(Blocks.DEEPSLATE_TILES)));
			for (int y = 1; y <= H; y++) {
				if (r > R - 0.8) {
					boolean win = y % 5 >= 2 && y % 5 <= 3 && (Math.abs(x) <= 0 || Math.abs(z) <= 0);
					boolean door = z > 0 && Math.abs(x) <= 1 && y <= 3;
					o.p(x, y, z, door ? st(Blocks.AIR) : win ? b("purple_stained_glass") : y % 7 == 0 ? st(Blocks.CHISELED_POLISHED_BLACKSTONE) : st(Blocks.POLISHED_BLACKSTONE_BRICKS));
				} else if (y == 7 && r > 2.5) o.p(x, y, z, st(Blocks.DARK_OAK_PLANKS));
				else if (r > R - 2 && y <= 2 && !(z > 0 && Math.abs(x) <= 1)) o.p(x, y, z, st(Blocks.BOOKSHELF));
				else o.p(x, y, z, st(Blocks.AIR));
			}
			for (int k = 0; k < 9; k++) {
				double rr = (R + 1.5) * (1 - k / 9.0);
				if (r <= rr + 0.5 && r > rr - 1.2) o.p(x, H + 1 + k, z, b("purpur_block"));
			}
		}
		o.p(0, 1, 0, st(Blocks.ENCHANTING_TABLE));
		o.p(0, H + 10, 0, st(Blocks.AMETHYST_CLUSTER));
		o.p(0, 6, 0, st(Blocks.SOUL_LANTERN).setValue(BlockStateProperties.HANGING, true));
		o.p(-2, 1, 3, b("purple_candle")); o.p(2, 1, 3, b("purple_candle"));
		o.sign(2, 3, R + 1, "마법부여사의 탑", "마법 책·청금석");
		o.path(R + 1, 3);
	}

	/** 정원: 가운데 분수, 꽃밭, 벤치, 둥근 나무 */
	private void garden(Bld o) {
		for (int x = -10; x <= 10; x++) for (int z = -10; z <= 10; z++) {
			double d = Math.hypot(x, z);
			if (d > 10.4) continue;
			boolean path = Math.abs(x) <= 1 || Math.abs(z) <= 1 || Math.abs(d - 6) < 0.7;
			o.p(x, 0, z, path ? (h(o.wx(x, z), 0, o.wz(x, z)) % 3 == 0 ? st(Blocks.COBBLESTONE) : b("dirt_path")) : st(Blocks.GRASS_BLOCK));
			if (!path && d > 3.5 && h(o.wx(x, z), 1, o.wz(x, z)) % 3 == 0) {
				String[] fl = {"poppy", "cornflower", "allium", "oxeye_daisy", "azure_bluet", "lily_of_the_valley"};
				o.p(x, 1, z, b(fl[h(o.wx(x, z), 2, o.wz(x, z)) % fl.length]));
			}
			if (d <= 2.6) { o.p(x, 0, z, st(Blocks.WATER)); o.p(x, -1, z, st(Blocks.PRISMARINE_BRICKS)); }
			else if (d <= 3.5) o.p(x, 1, z, st(Blocks.STONE_BRICK_WALL));
		}
		for (int y = 0; y <= 3; y++) o.p(0, y, 0, st(Blocks.CHISELED_STONE_BRICKS));
		o.p(0, 4, 0, st(Blocks.WATER));
		for (int q = 0; q < 4; q++) {
			int x = q < 2 ? -7 : 7, z = q % 2 == 0 ? -7 : 7;
			Capital.this.tree(o.wx(x, z), o.l.y(), o.wz(x, z), o.l.seed() + q);
		}
		for (int x = -3; x <= 3; x += 6) { o.p(x, 1, 8, stairs("spruce").setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)); o.p(x, 1, -8, stairs("spruce").setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)); }
		o.sign(2, 2, 10, "하늘 정원", null);
	}

	// ---------------- 건물 도구 (로컬 좌표: 정면 = +z, 가운데 = 0,0) ----------------
	private final class Bld {
		final Lot l;
		final Rotation rot;
		Bld(Lot l) { this.l = l; this.rot = switch (l.rot) { case 1 -> Rotation.CLOCKWISE_90; case 2 -> Rotation.CLOCKWISE_180; case 3 -> Rotation.COUNTERCLOCKWISE_90; default -> Rotation.NONE; }; }
		int wx(int lx, int lz) { return switch (l.rot) { case 1 -> l.x - lz; case 2 -> l.x - lx; case 3 -> l.x + lz; default -> l.x + lx; }; }
		int wz(int lx, int lz) { return switch (l.rot) { case 1 -> l.z + lx; case 2 -> l.z - lz; case 3 -> l.z - lx; default -> l.z + lz; }; }
		void p(int lx, int ly, int lz, BlockState s) { Capital.this.set(wx(lx, lz), l.y() + ly, wz(lx, lz), s.rotate(rot)); }
		void fill(int x0, int y0, int z0, int x1, int y1, int z1, BlockState s) {
			for (int x = x0; x <= x1; x++) for (int y = y0; y <= y1; y++) for (int z = z0; z <= z1; z++) p(x, y, z, s);
		}
		void clear(int a, int b2) {
			for (int x = a - 1; x <= b2 + 1; x++) for (int z = a - 1; z <= b2 + 1; z++) {
				int top = Math.min(24, ceiling(wx(x, z), wz(x, z), l.y()) - l.y() - 1);
				for (int y = 1; y <= top; y++) p(x, y, z, st(Blocks.AIR));
				p(x, 0, z, st(Blocks.GRASS_BLOCK));
				for (int y = -3; y < 0; y++) p(x, y, z, st(Blocks.DIRT));
			}
		}
		/** 벽: 아랫단 돌, 위는 회벽 + 기둥 */
		void walls(int x0, int z0, int x1, int z1, int y0, int y1, BlockState base, BlockState plaster, BlockState post) {
			for (int x = x0; x <= x1; x++) for (int z = z0; z <= z1; z++) {
				boolean edge = x == x0 || x == x1 || z == z0 || z == z1;
				if (!edge) continue;
				boolean corner = (x == x0 || x == x1) && (z == z0 || z == z1);
				for (int y = y0; y <= y1; y++) p(x, y, z, corner ? post : y == y0 ? base : plaster);
			}
		}
		void floor(int x0, int z0, int x1, int z1, int y, BlockState s) { fill(x0, y, z0, x1, y, z1, s); }
		void door(int x, int y, int z, String wood) {
			BlockState d = b(wood + "_door").setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
			p(x, y, z, d.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
			p(x, y + 1, z, d.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
		}
		/** 창문 + 덧창 + 화분 (정면 z 벽) */
		void window(int x, int y, int z, boolean front) {
			p(x, y, z, b("glass_pane").setValue(BlockStateProperties.EAST, true).setValue(BlockStateProperties.WEST, true));
			p(x, y + 1, z, b("glass_pane").setValue(BlockStateProperties.EAST, true).setValue(BlockStateProperties.WEST, true));
			int o = front ? 1 : -1;
			p(x, y - 1, z + o, b("spruce_trapdoor").setValue(BlockStateProperties.HALF, Half.TOP).setValue(BlockStateProperties.HORIZONTAL_FACING, front ? Direction.SOUTH : Direction.NORTH));
			p(x, y, z + o, h(wx(x, z), y, wz(x, z)) % 2 == 0 ? b("potted_red_tulip") : b("potted_blue_orchid"));
		}
		/** 박공 지붕 (x 방향 용마루), 처마가 1칸 나옴 */
		void gable(int x0, int z0, int x1, int z1, int y, BlockState stair, BlockState slab, BlockState gableWall) {
			int depth = z1 - z0;
			for (int s = 0; s <= depth / 2 + 1; s++) {
				int a = z0 - 1 + s, c = z1 + 1 - s;
				if (a > c) break;
				for (int x = x0 - 1; x <= x1 + 1; x++) {
					if (a == c) p(x, y + s, a, slab);
					else {
						p(x, y + s, a, stair.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
						p(x, y + s, c, stair.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
					}
				}
				if (s > 0) for (int z = a + 1; z < c; z++) { p(x0, y + s, z, gableWall); p(x1, y + s, z, gableWall); }
			}
		}
		void sign(int x, int y, int z, String line1, String line2) {
			p(x, y, z, b("dark_oak_wall_sign").setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
			try {
				BlockPos pos = new BlockPos(wx(x, z), l.y() + y, wz(x, z));
				if (in(pos.getX(), pos.getZ()) && lv.getBlockEntity(pos) instanceof SignBlockEntity sb) {
					java.util.List<Component> lines = java.util.List.of(Component.empty(), Component.literal(line1),
							line2 == null ? Component.empty() : Component.literal(line2), Component.empty());
					sb.setText(new SignText(lines, lines, net.minecraft.world.item.DyeColor.WHITE, true), net.minecraft.world.level.block.entity.SignTextSlot.FRONT);
				}
			} catch (Throwable ignored) {}
		}
		void chimney(int x, int z, int y0, int y1) {
			for (int y = y0; y <= y1; y++) p(x, y, z, st(Blocks.BRICKS));
			p(x, y1 + 1, z, b("campfire"));
		}
		/** 정문에서 큰길 쪽으로 나가는 오솔길 */
		void path(int z0, int len) { for (int z = z0; z < z0 + len; z++) for (int x = -1; x <= 1; x++) p(x, 0, z, h(wx(x, z), 0, wz(x, z)) % 3 == 0 ? st(Blocks.COBBLESTONE) : b("dirt_path")); }
		void flowers(int x0, int z0, int x1, int z1) {
			for (int x = x0; x <= x1; x++) for (int z = z0; z <= z1; z++) {
				int k = h(wx(x, z), 9, wz(x, z)) % 5;
				p(x, 1, z, k == 0 ? st(Blocks.PEONY) : k == 1 ? st(Blocks.ROSE_BUSH) : k == 2 ? st(Blocks.ALLIUM) : k == 3 ? st(Blocks.CORNFLOWER) : st(Blocks.AZURE_BLUET));
				if (k <= 1) p(x, 2, z, (k == 0 ? st(Blocks.PEONY) : st(Blocks.ROSE_BUSH)).setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
			}
		}
	}

	/** 대장간: 돌 화덕과 용암 화로, 모루·숫돌·대장장이 작업대·용광로, 열린 작업장 */
	private void smith(Bld o, String roof, String title) {
		o.floor(-9, -7, 9, 7, 0, st(Blocks.STONE_BRICKS));
		o.walls(-9, -7, 9, 7, 1, 5, st(Blocks.COBBLESTONE), st(Blocks.POLISHED_ANDESITE), st(Blocks.STRIPPED_SPRUCE_LOG));
		for (int x = -8; x <= 8; x++) for (int y = 1; y <= 4; y++) o.p(x, y, 7, st(Blocks.AIR));     // 정면은 트인 작업장
		for (int x = -9; x <= 9; x += 6) for (int y = 1; y <= 5; y++) o.p(x, y, 7, st(Blocks.STRIPPED_SPRUCE_LOG));
		o.gable(-9, -7, 9, 7, 6, stairs(roof), slab(roof), st(Blocks.SPRUCE_PLANKS));
		// 화덕 (용암)
		o.fill(-8, 1, -6, -4, 3, -4, st(Blocks.BRICKS));
		o.p(-6, 2, -5, st(Blocks.LAVA)); o.p(-6, 1, -5, st(Blocks.MAGMA_BLOCK)); o.p(-6, 3, -4, st(Blocks.IRON_BARS));
		o.chimney(-6, -5, 4, 11);
		o.p(-2, 1, -5, st(Blocks.ANVIL)); o.p(0, 1, -5, st(Blocks.SMITHING_TABLE)); o.p(2, 1, -5, st(Blocks.GRINDSTONE));
		o.p(4, 1, -6, st(Blocks.BLAST_FURNACE)); o.p(5, 1, -6, st(Blocks.FURNACE)); o.p(6, 1, -6, st(Blocks.BARREL));
		o.p(7, 1, -6, b("water_cauldron").setValue(BlockStateProperties.LEVEL_CAULDRON, 3));
		o.p(-8, 1, 2, st(Blocks.CHEST)); o.p(-8, 1, 3, st(Blocks.CHEST));
		for (int x = 3; x <= 7; x++) o.p(x, 3, -7 + 1, b("lightning_rod").setValue(BlockStateProperties.FACING, Direction.SOUTH));   // 벽에 걸린 무기 걸이
		o.p(-3, 4, 0, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true)); o.p(3, 4, 0, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true));
		o.sign(0, 5, 8, title, "강화 재료·갑옷·도구");
		o.path(8, 6);
	}

	/** 여관: 2층 목조, 1층 식당(탁자·통), 2층 침대방, 굴뚝, 발코니 */
	private void inn(Bld o) {
		o.floor(-10, -8, 10, 8, 0, st(Blocks.SPRUCE_PLANKS));
		o.walls(-10, -8, 10, 8, 1, 5, st(Blocks.COBBLESTONE), b("white_terracotta"), st(Blocks.DARK_OAK_LOG));
		o.walls(-10, -8, 10, 8, 6, 9, st(Blocks.DARK_OAK_PLANKS), b("white_terracotta"), st(Blocks.DARK_OAK_LOG));
		for (int x = -10; x <= 10; x++) { o.p(x, 5, -8, st(Blocks.DARK_OAK_LOG)); o.p(x, 5, 8, st(Blocks.DARK_OAK_LOG)); }
		o.floor(-9, -7, 9, 7, 5, st(Blocks.SPRUCE_PLANKS));
		o.gable(-10, -8, 10, 8, 10, stairs("dark_oak"), slab("dark_oak"), b("white_terracotta"));
		o.door(0, 1, 8, "spruce"); o.door(1, 1, 8, "spruce");
		for (int x : new int[]{-7, -4, 4, 7}) { o.window(x, 2, 8, true); o.window(x, 7, 8, true); o.window(x, 2, -8, false); }
		// 1층 식당
		for (int x : new int[]{-6, 0, 6}) { o.p(x, 1, -2, st(Blocks.SPRUCE_FENCE)); o.p(x, 2, -2, b("spruce_pressure_plate")); o.p(x - 1, 1, -2, stairs("spruce").setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)); o.p(x + 1, 1, -2, stairs("spruce").setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)); }
		for (int x = -9; x <= -6; x++) o.p(x, 1, -7, st(Blocks.BARREL));
		o.p(8, 1, -7, st(Blocks.SMOKER)); o.p(7, 1, -7, st(Blocks.FURNACE)); o.p(6, 1, -7, st(Blocks.CRAFTING_TABLE));
		o.chimney(9, -7, 2, 15);
		// 2층 침대방 (리스폰 지점)
		for (int x : new int[]{-8, -5, 5, 8}) {
			o.p(x, 6, -6, b("red_bed").setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).setValue(BlockStateProperties.BED_PART, net.minecraft.world.level.block.state.properties.BedPart.HEAD));
			o.p(x, 6, -5, b("red_bed").setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH).setValue(BlockStateProperties.BED_PART, net.minecraft.world.level.block.state.properties.BedPart.FOOT));
		}
		for (int z = 2; z <= 5; z++) o.p(9, 5, z, st(Blocks.AIR));
		for (int y = 1; y <= 5; y++) o.p(9, y, 6 - y, stairs("spruce").setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));   // 계단
		o.p(0, 4, 0, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true));
		o.sign(-2, 4, 9, "여관", "침대에서 쉬어 가세요");
		o.flowers(-9, 9, -3, 10); o.flowers(3, 9, 9, 10);
		o.path(9, 5);
	}

	/** 시장: 줄무늬 차양을 친 노점 6개 + 가운데 분수, 상인 2명 */
	private void market(Bld o) {
		o.floor(-14, -14, 14, 14, 0, st(Blocks.SMOOTH_STONE));
		for (int x = -14; x <= 14; x++) for (int z = -14; z <= 14; z++) if (((x + z) & 3) == 0) o.p(x, 0, z, st(Blocks.POLISHED_ANDESITE));
		String[] awn = {"red", "yellow", "blue", "lime", "orange", "purple"};
		int i = 0;
		for (int sx = -1; sx <= 1; sx += 2) for (int k = -1; k <= 1; k++) {
			int cx = sx * 9, cz = k * 9;
			for (int x = -2; x <= 2; x++) { o.p(cx + x, 1, cz - 1, st(Blocks.SPRUCE_PLANKS)); o.p(cx + x, 2, cz - 1, x % 2 == 0 ? st(Blocks.BARREL) : b("composter")); }
			for (int y = 1; y <= 3; y++) { o.p(cx - 2, y, cz + 1, st(Blocks.SPRUCE_FENCE)); o.p(cx + 2, y, cz + 1, st(Blocks.SPRUCE_FENCE)); o.p(cx - 2, y, cz - 2, st(Blocks.SPRUCE_FENCE)); o.p(cx + 2, y, cz - 2, st(Blocks.SPRUCE_FENCE)); }
			for (int x = -3; x <= 3; x++) for (int z = -3; z <= 2; z++) o.p(cx + x, 4, cz + z, b(((x + 3) % 2 == 0 ? awn[i % awn.length] : "white") + "_wool"));
			o.p(cx, 3, cz, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true));
			i++;
		}
		// 분수
		for (int x = -3; x <= 3; x++) for (int z = -3; z <= 3; z++) {
			double d = Math.hypot(x, z);
			if (d <= 3.4 && d > 2.5) o.p(x, 1, z, st(Blocks.STONE_BRICK_WALL));
			else if (d <= 2.5) { o.p(x, 0, z, st(Blocks.WATER)); o.p(x, -1, z, st(Blocks.PRISMARINE_BRICKS)); }
		}
		for (int y = 0; y <= 3; y++) o.p(0, y, 0, st(Blocks.CHISELED_STONE_BRICKS));
		o.p(0, 4, 0, st(Blocks.WATER));
		o.sign(0, 1, 13, "시장", "원소 상인 2명");
	}

	/** 운명의 전당: 보라 유리 돔 지붕의 원형 전당, 운명의 현자가 있는 곳 */
	private void sageHall(Bld o) {
		for (int x = -11; x <= 11; x++) for (int z = -11; z <= 11; z++) {
			double d = Math.hypot(x, z);
			if (d > 11.4) continue;
			o.p(x, 0, z, d < 4 ? b("purpur_block") : ((int) d % 3 == 0 ? st(Blocks.AMETHYST_BLOCK) : st(Blocks.CALCITE)));
			if (d > 10.4) for (int y = 1; y <= 7; y++) {
				boolean win = y >= 3 && y <= 5 && Math.floorMod((int) Math.round(Math.toDegrees(Math.atan2(z, x))), 30) < 10;
				o.p(x, y, z, win ? b("purple_stained_glass") : y == 7 ? st(Blocks.CHISELED_QUARTZ_BLOCK) : st(Blocks.QUARTZ_BRICKS));
			}
			// 돔
			for (int y = 8; y <= 16; y++) {
				double rr = 11.4 * Math.sqrt(Math.max(0, 1 - Math.pow((y - 7) / 9.5, 2)));
				if (d <= rr + 0.5 && d > rr - 1.0) o.p(x, y, z, (y + (int) d) % 3 == 0 ? st(Blocks.PURPUR_BLOCK) : b("purple_stained_glass"));
			}
		}
		for (int y = 1; y <= 2; y++) for (int x = -1; x <= 1; x++) o.p(x, y, 11, st(Blocks.AIR));
		o.p(0, 17, 0, st(Blocks.AMETHYST_BLOCK)); o.p(0, 18, 0, st(Blocks.END_ROD));
		for (int k = 0; k < 6; k++) {   // 안쪽 기둥과 촛대
			double a = Math.toRadians(k * 60);
			int x = (int) Math.round(Math.cos(a) * 7), z = (int) Math.round(Math.sin(a) * 7);
			for (int y = 1; y <= 6; y++) o.p(x, y, z, b("purpur_pillar"));
			o.p(x, 7, z, st(Blocks.AMETHYST_CLUSTER));
		}
		o.p(0, 1, -3, st(Blocks.LECTERN)); o.p(-2, 1, -3, b("purple_candle")); o.p(2, 1, -3, b("purple_candle"));
		o.sign(2, 3, 12, "운명의 전당", "직업·스탯 초기화");
		o.path(12, 4);
	}

	/** 마법 연구소: 돌 탑 + 책장 서재, 마법부여대·양조대, 별 관측대 */
	private void mageLab(Bld o) {
		o.floor(-8, -8, 8, 8, 0, st(Blocks.POLISHED_DEEPSLATE));
		o.walls(-8, -8, 8, 8, 1, 6, st(Blocks.DEEPSLATE_BRICKS), st(Blocks.DEEPSLATE_BRICKS), st(Blocks.POLISHED_BLACKSTONE));
		o.gable(-8, -8, 8, 8, 7, stairs("deepslate_tile"), slab("deepslate_tile"), st(Blocks.DEEPSLATE_BRICKS));
		o.door(0, 1, 8, "dark_oak");
		for (int x : new int[]{-5, 5}) { o.window(x, 2, 8, true); o.window(x, 2, -8, false); }
		for (int x = -7; x <= 7; x++) for (int y = 1; y <= 3; y++) { if (Math.abs(x) > 1) o.p(x, y, -7, st(Blocks.BOOKSHELF)); }
		for (int z = -6; z <= 6; z++) for (int y = 1; y <= 2; y++) { o.p(-7, y, z, st(Blocks.BOOKSHELF)); }
		o.p(0, 1, -3, st(Blocks.ENCHANTING_TABLE)); o.p(5, 1, -5, st(Blocks.BREWING_STAND)); o.p(6, 1, -5, b("water_cauldron").setValue(BlockStateProperties.LEVEL_CAULDRON, 3));
		o.p(-3, 1, 4, st(Blocks.CARTOGRAPHY_TABLE));
		// 관측탑
		for (int x = 4; x <= 8; x++) for (int z = 4; z <= 8; z++) {
			boolean e = x == 4 || x == 8 || z == 4 || z == 8;
			for (int y = 1; y <= 16; y++) if (e) o.p(x, y, z, y % 5 == 0 ? b("purple_stained_glass") : st(Blocks.DEEPSLATE_BRICKS));
		}
		for (int x = 3; x <= 9; x++) for (int z = 3; z <= 9; z++) { o.p(x, 17, z, st(Blocks.POLISHED_BLACKSTONE_BRICKS)); if (x == 3 || x == 9 || z == 3 || z == 9) o.p(x, 18, z, st(Blocks.POLISHED_BLACKSTONE_WALL)); }
		o.p(6, 18, 6, st(Blocks.AMETHYST_CLUSTER)); o.p(6, 19, 6, st(Blocks.END_ROD));
		o.p(0, 5, 0, st(Blocks.SOUL_LANTERN).setValue(BlockStateProperties.HANGING, true));
		o.sign(2, 4, 9, "마법 연구소", "마법부여대·양조대");
		o.path(9, 5);
	}

	/** 훈련장: 모래 마당, 허수아비, 과녁, 무기 걸이, 관중 벤치 */
	private void training(Bld o) {
		for (int x = -14; x <= 14; x++) for (int z = -14; z <= 14; z++) {
			boolean edge = Math.abs(x) == 14 || Math.abs(z) == 14;
			o.p(x, 0, z, edge ? st(Blocks.STONE_BRICKS) : (h(o.wx(x, z), 1, o.wz(x, z)) % 5 == 0 ? st(Blocks.GRAVEL) : b("sand")));
			if (edge && !(Math.abs(x) <= 2 && z == 14)) { o.p(x, 1, z, st(Blocks.SPRUCE_FENCE)); if ((x + z) % 7 == 0) { o.p(x, 2, z, st(Blocks.SPRUCE_FENCE)); o.p(x, 3, z, st(Blocks.LANTERN)); } }
		}
		for (int k = -2; k <= 2; k++) {   // 허수아비
			int x = k * 5, z = -6;
			o.p(x, 1, z, st(Blocks.SPRUCE_FENCE)); o.p(x, 2, z, st(Blocks.HAY_BLOCK)); o.p(x, 3, z, b("carved_pumpkin").setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
			o.p(x - 1, 2, z, st(Blocks.SPRUCE_FENCE)); o.p(x + 1, 2, z, st(Blocks.SPRUCE_FENCE));
		}
		for (int k = -2; k <= 2; k++) { o.p(k * 5, 1, -12, st(Blocks.HAY_BLOCK)); o.p(k * 5, 2, -12, st(Blocks.TARGET)); }   // 과녁
		for (int x = -12; x <= -6; x++) o.p(x, 1, 10, stairs("spruce").setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
		for (int x = 6; x <= 12; x++) o.p(x, 1, 10, stairs("spruce").setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
		o.p(-12, 1, 4, st(Blocks.BARREL)); o.p(-12, 2, 4, st(Blocks.BARREL));
		o.sign(3, 2, 15, "훈련장", "스킬 Z·X·C 연습");
	}

	/** 균열의 관문: 자수정과 흑요석 아치, 바닥 마법진 */
	private void riftGate(Bld o) {
		for (int x = -12; x <= 12; x++) for (int z = -12; z <= 12; z++) {
			double d = Math.hypot(x, z);
			if (d > 12.4) continue;
			o.p(x, 0, z, Math.abs(d - 10) < 0.6 || Math.abs(d - 5) < 0.6 ? st(Blocks.AMETHYST_BLOCK) : (d < 5 ? b("purpur_block") : st(Blocks.POLISHED_BLACKSTONE_BRICKS)));
			double th = Math.toDegrees(Math.atan2(z, x));
			if (d < 10 && d > 5 && Math.abs(((th % 45) + 45) % 45 - 22.5) > 21) o.p(x, 0, z, b("crying_obsidian"));
		}
		for (int y = 1; y <= 10; y++) for (int x = -5; x <= 5; x++) {   // 아치
			double ar = Math.hypot(x, (y - 1) * 0.9);
			if (ar <= 5.6 && ar > 4.4 || (Math.abs(x) == 5 && y <= 5)) o.p(x, y, 0, h(x, y, 5) % 3 == 0 ? b("crying_obsidian") : st(Blocks.OBSIDIAN));
			else if (ar <= 4.4 && y <= 5 + (int) Math.sqrt(Math.max(0, 19 - x * x))) o.p(x, y, 0, b("purple_stained_glass"));
		}
		o.p(0, 1, 3, st(Blocks.LODESTONE)); o.p(0, 2, 3, st(Blocks.AMETHYST_CLUSTER));
		for (int sx = -1; sx <= 1; sx += 2) { for (int y = 1; y <= 4; y++) o.p(sx * 9, y, 6, st(Blocks.POLISHED_BLACKSTONE_WALL)); o.p(sx * 9, 5, 6, st(Blocks.SOUL_LANTERN)); }
		o.sign(0, 1, 7, "균열의 관문", "균열의 열쇠로 무한 도전");
	}

	/** 창고와 농장: 상자·통·엔더상자 창고, 옆에 밭과 퇴비통 */
	private void storage(Bld o) {
		o.floor(-6, -8, 6, 2, 0, st(Blocks.OAK_PLANKS));
		o.walls(-6, -8, 6, 2, 1, 4, st(Blocks.COBBLESTONE), st(Blocks.OAK_PLANKS), st(Blocks.OAK_LOG));
		o.gable(-6, -8, 6, 2, 5, stairs("spruce"), slab("spruce"), st(Blocks.OAK_PLANKS));
		o.door(0, 1, 2, "oak");
		for (int x = -5; x <= 5; x++) { if (x == 0) continue; o.p(x, 1, -7, x % 2 == 0 ? st(Blocks.BARREL) : st(Blocks.CHEST)); o.p(x, 2, -7, st(Blocks.BARREL)); }
		o.p(-5, 1, -3, st(Blocks.ENDER_CHEST)); o.p(5, 1, -3, st(Blocks.ENDER_CHEST));
		o.window(-3, 2, 2, true); o.window(3, 2, 2, true);
		for (int x = -11; x <= 11; x++) for (int z = 4; z <= 11; z++) {   // 밭
			boolean water = z == 7 && x % 4 == 0;
			o.p(x, 0, z, water ? st(Blocks.WATER) : b("farmland").setValue(BlockStateProperties.MOISTURE, 7));
			if (!water) o.p(x, 1, z, (x + 11) / 4 % 3 == 0 ? b("wheat").setValue(BlockStateProperties.AGE_7, 7) : (x + 11) / 4 % 3 == 1 ? b("carrots").setValue(BlockStateProperties.AGE_7, 7) : b("potatoes").setValue(BlockStateProperties.AGE_7, 7));
		}
		for (int x = -12; x <= 12; x++) { o.p(x, 1, 3, st(Blocks.OAK_FENCE)); o.p(x, 1, 12, st(Blocks.OAK_FENCE)); }
		o.p(-12, 1, 7, b("composter")); o.p(12, 1, 7, st(Blocks.HAY_BLOCK));
		o.sign(2, 3, 3, "창고·농장", "상자·엔더상자·밭");
	}

	/** 집: 크기·지붕·재질이 조금씩 다른 아담한 집 (12채) */
	private void house(Bld o) {
		int s = o.l.seed;
		String[][] pal = {
				{"spruce", "white_terracotta", "deepslate_tile"}, {"oak", "calcite", "waxed_oxidized_cut_copper"},
				{"dark_oak", "smooth_sandstone", "dark_prismarine"}, {"birch", "white_terracotta", "purpur"},
				{"birch", "calcite", "deepslate_tile"}, {"spruce", "light_gray_terracotta", "waxed_cut_copper"}};
		String[] P = pal[Math.floorMod(s, pal.length)];
		int w = 4 + s % 2, d = 3 + (s / 2) % 2;
		BlockState log = b(P[0].equals("dark_oak") ? "dark_oak_log" : P[0] + "_log");
		o.floor(-w, -d, w, d, 0, b(P[0] + "_planks"));
		o.walls(-w, -d, w, d, 1, 4, st(Blocks.COBBLESTONE), b(P[1]), log);
		boolean two = s % 3 == 0;
		if (two) { o.walls(-w, -d, w, d, 5, 7, b(P[0] + "_planks"), b(P[1]), log); o.floor(-w + 1, -d + 1, w - 1, d - 1, 4, b(P[0] + "_planks")); }
		String roof = P[2];
		BlockState rs = roof.equals("purpur") ? b("purpur_stairs") : stairs(roof), rl = roof.equals("purpur") ? b("purpur_slab") : slab(roof);
		o.gable(-w, -d, w, d, two ? 8 : 5, rs, rl, b(P[1]));
		o.door(0, 1, d, P[0]);
		o.window(-w + 2, 2, d, true); o.window(w - 2, 2, d, true); o.window(-w + 2, 2, -d, false);
		if (two) { o.window(-w + 2, 6, d, true); o.window(w - 2, 6, d, true); }
		o.chimney(w - 1, -d + 1, 2, (two ? 8 : 5) + d + 1);
		o.p(-w + 1, 1, -d + 1, st(Blocks.CRAFTING_TABLE)); o.p(-w + 2, 1, -d + 1, st(Blocks.BARREL));
		o.p(w - 1, 1, d - 1, b("red_bed").setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).setValue(BlockStateProperties.BED_PART, net.minecraft.world.level.block.state.properties.BedPart.FOOT));
		o.p(w - 1, 1, d - 2, b("red_bed").setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).setValue(BlockStateProperties.BED_PART, net.minecraft.world.level.block.state.properties.BedPart.HEAD));
		o.p(0, 3, 0, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true));
		// 앞마당 울타리와 꽃
		for (int x = -w - 1; x <= w + 1; x++) if (Math.abs(x) > 1) o.p(x, 1, d + 4, st(Blocks.SPRUCE_FENCE));
		o.flowers(-w, d + 2, -2, d + 3); o.flowers(2, d + 2, w, d + 3);
		o.path(d + 1, 6);
	}
}
