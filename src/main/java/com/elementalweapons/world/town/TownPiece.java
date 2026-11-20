package com.elementalweapons.world.town;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;

import com.elementalweapons.mob.ModMobs;

/**
 * 마을 하나 전체를 그리는 조각. 배치는 seed 로 결정되어 어느 청크에서 그려도 같은 모양이 됨.
 * 좌표는 마을 왼쪽 위 (x0, z0), 땅 높이 y0 기준 상대좌표.
 */
public class TownPiece extends StructurePiece {
	public static final int SIZE = 112;
	private final int x0, y0, z0;
	private final long seed;
	private final int flags;   // 1 서 2 동 4 북 8 남 (성벽), 16 대성당, 32 성문
	public static final int ALL = 63;
	private transient List<Feature> plan;

	public TownPiece(int x0, int y0, int z0, long seed) { this(x0, y0, z0, seed, ALL); }

	public TownPiece(int x0, int y0, int z0, long seed, int flags) {
		super(ModTowns.TOWN_PIECE, 0, new BoundingBox(x0, y0 - 10, z0, x0 + SIZE - 1, y0 + 48, z0 + SIZE - 1));
		this.x0 = x0; this.y0 = y0; this.z0 = z0; this.seed = seed; this.flags = flags;
	}

	public TownPiece(CompoundTag tag) {
		super(ModTowns.TOWN_PIECE, tag);
		this.x0 = tag.getIntOr("tx", 0); this.y0 = tag.getIntOr("ty", 64); this.z0 = tag.getIntOr("tz", 0); this.seed = tag.getLongOr("seed", 0L); this.flags = tag.getIntOr("flags", ALL);
	}

	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
		tag.putInt("tx", x0); tag.putInt("ty", y0); tag.putInt("tz", z0); tag.putLong("seed", seed); tag.putInt("flags", flags);
	}

	// ===================== 배치 계획 =====================
	private enum Kind { HOUSE, TOWER, GATE, FOUNTAIN, LAMP, TREE, STALL, BED, NPC }
	private record Feature(Kind kind, int x, int z, int w, int d, int a, int b, Direction face) {
		boolean touches(int cx0, int cz0, int cx1, int cz1, int pad) {
			return x - pad <= cx1 && x + w + pad >= cx0 && z - pad <= cz1 && z + d + pad >= cz0;
		}
	}

	private static final int ROAD = 5;
	private static final int[] AVENUES = {6, 36, 54, 72, 102};   // 도로 시작 좌표 (가로/세로 같음)

	private List<Feature> plan() {
		if (plan != null) return plan;
		Random r = new Random(seed);
		List<Feature> f = new ArrayList<>();
		// 중앙 광장 (분수)
		f.add(new Feature(Kind.FOUNTAIN, 41, 41, 31, 31, 0, 0, Direction.NORTH));
		// 남쪽 대로 위 성문, 북동쪽 대성당 탑
		if ((flags & 32) != 0) f.add(new Feature(Kind.GATE, 50, 92, 13, 9, 0, 0, Direction.SOUTH));
		if ((flags & 16) != 0) f.add(new Feature(Kind.TOWER, 80, 13, 15, 15, 0, 0, Direction.SOUTH));
		// 블록마다 집 채우기
		int[] cells = {11, 41, 59, 77};   // 각 구역 시작 (도로 사이)
		int[] cellLen = {25, 13, 13, 25};
		for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++) {
			int cx = cells[i], cz = cells[j], cw = cellLen[i], cd = cellLen[j];
			if ((i == 1 || i == 2) && (j == 1 || j == 2)) continue;                  // 광장
			if (i == 3 && j == 0 && (flags & 16) != 0) continue;                     // 대성당
			int x = cx;
			while (x < cx + cw - 6) {
				int w = 7 + r.nextInt(4);
				if (x + w > cx + cw) w = cx + cw - x;
				if (w < 6) break;
				int z = cz;
				while (z < cz + cd - 6) {
					int d = 7 + r.nextInt(4);
					if (z + d > cz + cd) d = cz + cd - z;
					if (d < 6) break;
					if (r.nextInt(9) == 0) { f.add(new Feature(Kind.TREE, x + w / 2, z + d / 2, 1, 1, 0, 0, Direction.NORTH)); f.add(new Feature(Kind.BED, x + 1, z + 1, w - 2, d - 2, 0, 0, Direction.NORTH)); }
					else {
						Direction face = z == cz ? Direction.NORTH : (z + d >= cz + cd ? Direction.SOUTH : (x == cx ? Direction.WEST : Direction.EAST));
						f.add(new Feature(Kind.HOUSE, x, z, w - 1, d - 1, 2 + r.nextInt(2), r.nextInt(4), face));
					}
					z += d;
				}
				x += w;
			}
		}
		// 가로등 (도로 가장자리)
		for (int a : AVENUES) for (int t = 4; t < SIZE - 4; t += 11) {
			f.add(new Feature(Kind.LAMP, a - 1, t, 1, 1, 0, 0, Direction.NORTH));
			f.add(new Feature(Kind.LAMP, t, a + ROAD, 1, 1, 0, 0, Direction.NORTH));
		}
		// 광장 노점
		int[][] stalls = {{44, 44}, {64, 44}, {44, 64}, {64, 64}};
		for (int[] s : stalls) f.add(new Feature(Kind.STALL, s[0], s[1], 4, 4, r.nextInt(6), 0, Direction.NORTH));
		f.add(new Feature(Kind.NPC, 50, 46, 1, 1, 0, 0, Direction.NORTH));
		f.add(new Feature(Kind.NPC, 62, 66, 1, 1, 1, 0, Direction.NORTH));
		plan = f;
		return f;
	}

	// ===================== 그리기 =====================
	private WorldGenLevel lv;
	private BoundingBox box;

	private void set(int x, int y, int z, BlockState s) {
		BlockPos p = new BlockPos(x0 + x, y0 + y, z0 + z);
		if (box.isInside(p)) lv.setBlock(p, s, 2);
	}

	private static BlockState st(Block b) { return b.defaultBlockState(); }
	private static Block blk(String id) { return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.fromNamespaceAndPath("minecraft", id)); }
	private static int h(int x, int y, int z) { int n = x * 73856093 ^ y * 19349663 ^ z * 83492791; n ^= n >>> 13; n *= 0x5bd1e995; return (n ^ n >>> 15) & 0x7fffffff; }

	private BlockState road(int x, int z) {
		int k = h(x, 0, z) % 100;
		if (k < 38) return st(Blocks.COBBLESTONE);
		if (k < 58) return st(Blocks.STONE);
		if (k < 74) return st(Blocks.ANDESITE);
		if (k < 86) return st(Blocks.MOSSY_COBBLESTONE);
		if (k < 94) return st(Blocks.GRAVEL);
		return st(Blocks.MOSS_BLOCK);
	}

	private boolean isRoad(int x, int z) {
		for (int a : AVENUES) if ((x >= a && x < a + ROAD) || (z >= a && z < a + ROAD)) return true;
		return false;
	}

	@Override
	public void postProcess(WorldGenLevel level, StructureManager sm, ChunkGenerator gen, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
		this.lv = level; this.box = chunkBox;
		int cx0 = chunkBox.minX() - x0, cz0 = chunkBox.minZ() - z0, cx1 = chunkBox.maxX() - x0, cz1 = chunkBox.maxZ() - z0;
		// 1) 땅 고르기: 바닥 + 아래 채우기 + 위 비우기
		for (int x = Math.max(0, cx0); x <= Math.min(SIZE - 1, cx1); x++) for (int z = Math.max(0, cz0); z <= Math.min(SIZE - 1, cz1); z++) {
			for (int y = 0; y <= 32; y++) set(x, y, z, st(Blocks.AIR));
			boolean road = isRoad(x, z);
			boolean edge = x == 0 || z == 0 || x == SIZE - 1 || z == SIZE - 1;
			set(x, -1, z, edge ? st(Blocks.STONE_BRICKS) : road ? road(x, z) : st(Blocks.GRASS_BLOCK));
			for (int y = -2; y >= -10; y--) {
				BlockPos p = new BlockPos(x0 + x, y0 + y, z0 + z);
				if (!chunkBox.isInside(p)) continue;
				BlockState cur = level.getBlockState(p);
				if (!cur.isAir() && cur.getFluidState().isEmpty() && y < -4) break;
				set(x, y, z, y > -4 ? st(Blocks.DIRT) : st(Blocks.STONE));
			}
			if (!road && !edge) {
				int k = h(x, 1, z) % 100;
				if (k < 10) set(x, 0, z, st(Blocks.SHORT_GRASS));
				else if (k < 13) set(x, 0, z, st(k == 10 ? Blocks.POPPY : k == 11 ? Blocks.CORNFLOWER : Blocks.ALLIUM));
			}
			if (road && h(x, 2, z) % 23 == 0) set(x, 0, z, st(Blocks.MOSS_CARPET));
			// 도로 경계석
			if (road) for (int a : AVENUES) if (x == a || x == a + ROAD - 1 || z == a || z == a + ROAD - 1) { if (h(x, 3, z) % 3 == 0) set(x, -1, z, st(Blocks.STONE_BRICKS)); }
		}
		// 1-b) 성벽 + 모서리 탑
		for (int x = Math.max(0, cx0); x <= Math.min(SIZE - 1, cx1); x++) for (int z = Math.max(0, cz0); z <= Math.min(SIZE - 1, cz1); z++) {
			boolean edge = (x == 0 && (flags & 1) != 0) || (x == SIZE - 1 && (flags & 2) != 0) || (z == 0 && (flags & 4) != 0) || (z == SIZE - 1 && (flags & 8) != 0);
			if (!edge || isRoad(x, z)) continue;
			// 낮은 담장: 돌벽돌 한 줄 + 담장 블록, 9칸마다 등불 기둥
			set(x, 0, z, st(h(x, 0, z) % 5 == 0 ? Blocks.MOSSY_STONE_BRICKS : Blocks.STONE_BRICKS));
			boolean post = (x + z) % 9 == 0;
			set(x, 1, z, st(post ? Blocks.CHISELED_STONE_BRICKS : Blocks.STONE_BRICK_WALL));
			if (post) { set(x, 2, z, st(Blocks.STONE_BRICK_WALL)); set(x, 3, z, st(Blocks.LANTERN)); }
			for (int y = 2; y < 6; y++) if (!post || y > 3) set(x, y, z, st(Blocks.AIR));
		}
		// 2) 건물들
		for (Feature f : plan()) {
			if (!f.touches(cx0, cz0, cx1, cz1, 12)) continue;
			switch (f.kind()) {
				case HOUSE -> house(f);
				case TOWER -> cathedral(f);
				case GATE -> gate(f);
				case FOUNTAIN -> plaza(f);
				case LAMP -> lamp(f.x(), f.z());
				case TREE -> tree(f.x(), f.z());
				case BED -> bed(f);
				case STALL -> stall(f);
				case NPC -> npc(f);
			}
		}
	}

	// ---------- 집 ----------
	private static final Block[] UPPER = {Blocks.CALCITE, blk("white_terracotta"), Blocks.SMOOTH_SANDSTONE, Blocks.CALCITE};
	private static final Block[] BEAM = {Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_OAK_LOG};

	private void house(Feature f) {
		int x = f.x(), z = f.z(), w = f.w(), d = f.d(), floors = f.a(), v = f.b();
		int H = floors * 4;
		for (int i = 0; i <= w; i++) for (int k = 0; k <= d; k++) {
			boolean wallX = i == 0 || i == w, wallZ = k == 0 || k == d;
			set(x + i, -1, z + k, st(Blocks.STONE_BRICKS));
			for (int y = 0; y < H; y++) {
				boolean floorLine = y % 4 == 0 && y > 0;
				if (wallX || wallZ) {
					boolean corner = wallX && wallZ;
					boolean ground = y < 4;
					BlockState s;
					if (corner) s = ground ? st(Blocks.POLISHED_ANDESITE) : st(BEAM[v]).setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
					else if (floorLine) s = st(BEAM[v]).setValue(BlockStateProperties.AXIS, wallX ? Direction.Axis.Z : Direction.Axis.X);
					else if (ground) s = h(x + i, y, z + k) % 5 == 0 ? st(Blocks.MOSSY_STONE_BRICKS) : (h(x + i, y, z + k) % 7 == 0 ? st(Blocks.CRACKED_STONE_BRICKS) : st(Blocks.STONE_BRICKS));
					else s = (wallZ ? (i % 3 == 0) : (k % 3 == 0)) ? st(BEAM[v]).setValue(BlockStateProperties.AXIS, Direction.Axis.Y) : st(UPPER[v]);
					// 창문
					int ly = y % 4;
					boolean along = wallZ ? (i % 3 != 0 && i > 0 && i < w) : (k % 3 != 0 && k > 0 && k < d);
					if (!corner && along && (ly == 1 || ly == 2) && !(ground && isDoor(f, i, k))) {
						s = st(Blocks.GLASS_PANE).setValue(wallZ ? BlockStateProperties.EAST : BlockStateProperties.NORTH, true).setValue(wallZ ? BlockStateProperties.WEST : BlockStateProperties.SOUTH, true);
					}
					set(x + i, y, z + k, s);
				} else if (floorLine) set(x + i, y, z + k, st(Blocks.SPRUCE_PLANKS));
				else if (y == 0) set(x + i, y - 1, z + k, st(Blocks.SPRUCE_PLANKS));
			}
		}
		// 꽃 상자 (위층 창문 아래 바깥)
		for (int fl = 1; fl < floors; fl++) {
			int y = fl * 4;
			for (int i = 1; i < w; i++) if (i % 3 != 0) {
				set(x + i, y, z - 1, st(h(i, y, z) % 2 == 0 ? Blocks.FLOWERING_AZALEA_LEAVES : Blocks.AZALEA_LEAVES).setValue(BlockStateProperties.PERSISTENT, true));
				set(x + i, y, z + d + 1, st(h(i, y, z + 1) % 2 == 0 ? Blocks.FLOWERING_AZALEA_LEAVES : Blocks.AZALEA_LEAVES).setValue(BlockStateProperties.PERSISTENT, true));
			}
		}
		// 담쟁이 (벽 바깥에 늘어짐)
		for (int i = 0; i <= w; i++) for (int k = 0; k <= d; k += d) {
			if (h(x + i, 7, z + k) % 4 != 0) continue;
			int len = 2 + h(x + i, 8, z + k) % (H - 1);
			int oz = k == 0 ? z - 1 : z + d + 1;
			for (int y = H - 1; y >= H - len && y > 0; y--)
				set(x + i, y, oz, st(Blocks.VINE).setValue(k == 0 ? BlockStateProperties.SOUTH : BlockStateProperties.NORTH, true));
		}
		// 앞마당 울타리 꽃
		for (int i = 0; i <= w; i++) if (h(x + i, 9, z) % 3 == 0) set(x + i, 0, z - 1, st(h(i, 1, z) % 2 == 0 ? Blocks.AZALEA : Blocks.FLOWERING_AZALEA));
		// 문
		door(f);
		// 지붕 (긴 쪽을 따라 박공)
		roof(x, z, w, d, H);
		// 굴뚝 + 실내 등
		for (int y = H - 1; y < H + Math.max(w, d) / 2 + 2; y++) set(x + 1, y, z + 1, st(Blocks.BRICKS));
		set(x + 1, H + Math.max(w, d) / 2 + 2, z + 1, st(Blocks.CAMPFIRE).setValue(BlockStateProperties.LIT, false));
		set(x + w / 2, 3, z + d / 2, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true));
	}

	private boolean isDoor(Feature f, int i, int k) {
		int w = f.w(), d = f.d();
		return switch (f.face()) {
			case NORTH -> k == 0 && i == w / 2;
			case SOUTH -> k == d && i == w / 2;
			case WEST -> i == 0 && k == d / 2;
			default -> i == w && k == d / 2;
		};
	}

	private void door(Feature f) {
		int x = f.x(), z = f.z(), w = f.w(), d = f.d();
		int dx, dz;
		switch (f.face()) {
			case NORTH -> { dx = x + w / 2; dz = z; }
			case SOUTH -> { dx = x + w / 2; dz = z + d; }
			case WEST -> { dx = x; dz = z + d / 2; }
			default -> { dx = x + w; dz = z + d / 2; }
		}
		BlockState door = st(Blocks.SPRUCE_DOOR).setValue(BlockStateProperties.HORIZONTAL_FACING, f.face().getOpposite());
		set(dx, 0, dz, door.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
		set(dx, 1, dz, door.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
		int ox = dx + f.face().getStepX(), oz = dz + f.face().getStepZ();
		set(ox, 2, oz, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, false));
		set(ox, 1, oz, st(Blocks.AIR));
		set(dx, 2, dz, st(Blocks.SPRUCE_PLANKS));
	}

	// 판타지 지붕 색: 청록 동판, 보라 퍼퍼, 짙은 남색 타일, 프리즈머린, 주황 동판
	private static final String[][] ROOFS = {
		{"waxed_oxidized_cut_copper_stairs", "waxed_oxidized_cut_copper_slab", "white_terracotta"},
		{"purpur_stairs", "purpur_slab", "white_terracotta"},
		{"deepslate_tile_stairs", "deepslate_tile_slab", "light_gray_terracotta"},
		{"dark_prismarine_stairs", "dark_prismarine_slab", "white_terracotta"},
		{"waxed_cut_copper_stairs", "waxed_cut_copper_slab", "orange_terracotta"},
		{"waxed_oxidized_cut_copper_stairs", "waxed_oxidized_cut_copper_slab", "white_terracotta"},
	};

	private void roof(int x, int z, int w, int d, int base) {
		String[] RP = ROOFS[h(x, 77, z) % ROOFS.length];
		boolean alongX = w >= d;
		int span = alongX ? d : w, len = alongX ? w : d;
		for (int s = 0; s <= span / 2 + 1; s++) {
			for (int l = -1; l <= len + 1; l++) {
				int y = base + s;
				int a = s - 1, b = span - s + 1;
				if (a > b) continue;
				Direction up1 = alongX ? Direction.SOUTH : Direction.EAST;   // 계단 면 방향
				BlockState s1 = st(blk(RP[0])).setValue(BlockStateProperties.HORIZONTAL_FACING, up1);
				BlockState s2 = st(blk(RP[0])).setValue(BlockStateProperties.HORIZONTAL_FACING, up1.getOpposite());
				if (alongX) { set(x + l, y, z + a, s1); set(x + l, y, z + b, s2); }
				else { set(x + a, y, z + l, s1); set(x + b, y, z + l, s2); }
				if (a == b) { if (alongX) set(x + l, y, z + a, st(blk(RP[1]))); else set(x + a, y, z + l, st(blk(RP[1]))); }
				// 박공벽
				if (l == 0 || l == len) for (int m = a + 1; m < b; m++) {
					if (alongX) set(x + l, y, z + m, st(blk(RP[2]))); else set(x + m, y, z + l, st(blk(RP[2])));
				}
			}
		}
	}

	private void watchtower(int x, int z) {
		for (int i = 0; i < 5; i++) for (int k = 0; k < 5; k++) for (int y = 0; y < 11; y++) {
			boolean wall = i == 0 || k == 0 || i == 4 || k == 4;
			if (wall) set(x + i, y, z + k, st(y == 10 ? Blocks.POLISHED_ANDESITE : (h(x + i, y, z + k) % 6 == 0 ? Blocks.MOSSY_STONE_BRICKS : Blocks.STONE_BRICKS)));
			else if (y == 10) set(x + i, y, z + k, st(Blocks.SPRUCE_PLANKS));
		}
		for (int i = 0; i < 5; i++) for (int k = 0; k < 5; k++) if ((i == 0 || k == 0 || i == 4 || k == 4) && (i + k) % 2 == 0) set(x + i, 11, z + k, st(Blocks.STONE_BRICK_WALL));
		set(x + 2, 12, z + 2, st(Blocks.LANTERN));
		for (int y = 2; y < 10; y += 4) { set(x + 2, y, z, st(blk("glass_pane"))); set(x + 2, y, z + 4, st(blk("glass_pane"))); }
	}

	// ---------- 대성당 탑 ----------
	private void cathedral(Feature f) {
		int x = f.x(), z = f.z(), W = f.w();
		int H = 28;
		for (int i = 0; i < W; i++) for (int k = 0; k < W; k++) {
			boolean wall = i == 0 || k == 0 || i == W - 1 || k == W - 1;
			boolean corner = (i <= 1 || i >= W - 2) && (k <= 1 || k >= W - 2);
			set(x + i, -1, z + k, st(Blocks.POLISHED_DEEPSLATE));
			for (int y = 0; y < H; y++) {
				if (corner) set(x + i, y, z + k, st(y % 6 == 5 ? Blocks.CHISELED_STONE_BRICKS : Blocks.STONE_BRICKS));
				else if (wall) {
					boolean win = (i == W / 2 || k == W / 2 || i == W / 2 - 2 || i == W / 2 + 2 || k == W / 2 - 2 || k == W / 2 + 2) && y % 8 >= 2 && y % 8 <= 6;
					set(x + i, y, z + k, win ? st(blk("purple_stained_glass")) : st(h(x + i, y, z + k) % 6 == 0 ? Blocks.MOSSY_STONE_BRICKS : Blocks.STONE_BRICKS));
				} else if (y % 8 == 0 && y > 0) set(x + i, y, z + k, st(Blocks.DARK_OAK_PLANKS));
			}
		}
		// 첨탑
		for (int s = 0; s <= W / 2; s++) for (int i = s; i < W - s; i++) for (int k = s; k < W - s; k++)
			if (i == s || k == s || i == W - 1 - s || k == W - 1 - s) set(x + i, H + s * 2, z + k, st(blk("waxed_cut_copper")));
		for (int s = 0; s <= W / 2; s++) for (int i = s; i < W - s; i++) for (int k = s; k < W - s; k++)
			if (i == s || k == s || i == W - 1 - s || k == W - 1 - s) set(x + i, H + s * 2 + 1, z + k, st(blk("waxed_cut_copper")));
		int top = H + W + 1;
		set(x + W / 2, top, z + W / 2, st(Blocks.GOLD_BLOCK));
		set(x + W / 2, top + 1, z + W / 2, st(blk("lightning_rod")));
		// 문 + 현자
		for (int y = 0; y < 4; y++) for (int i = W / 2 - 1; i <= W / 2 + 1; i++) set(x + i, y, z + W - 1, y == 3 ? st(Blocks.CHISELED_STONE_BRICKS) : st(Blocks.AIR));
		set(x + W / 2, 6, z + W / 2, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true));
		spawnMob(ModMobs.SAGE, x + W / 2, 0, z + W / 2);
	}

	// ---------- 성문 ----------
	private void gate(Feature f) {
		int x = f.x(), z = f.z(), W = f.w(), D = f.d();
		for (int i = 0; i < W; i++) for (int k = 0; k < D; k++) for (int y = 0; y < 14; y++) {
			boolean tower = i < 3 || i >= W - 3;
			boolean arch = !tower && y < 6 + (i == 3 || i == W - 4 ? -1 : 0);
			if (arch) continue;
			boolean shell = i == 0 || i == W - 1 || k == 0 || k == D - 1 || y == 13 || y == 6 || tower;
			if (!shell) continue;
			BlockState s = y < 6 ? st(Blocks.STONE_BRICKS) : (y == 6 || y == 13 ? st(Blocks.POLISHED_ANDESITE) : st(Blocks.CALCITE));
			if (y > 6 && y < 13 && (k == 0 || k == D - 1) && i % 2 == 1 && !tower && y % 3 != 0)
				s = st(Blocks.GLASS_PANE).setValue(BlockStateProperties.EAST, true).setValue(BlockStateProperties.WEST, true);
			if (tower && (i == 0 || i == W - 1 || i == 2 || i == W - 3) && (k == 0 || k == D - 1) && y > 0) s = st(Blocks.STRIPPED_DARK_OAK_LOG).setValue(BlockStateProperties.AXIS, Direction.Axis.Y);
			set(x + i, y, z + k, s);
		}
		roof(x, z, W - 1, D - 1, 14);
		for (int i = 1; i < W - 1; i++) { set(x + i, 7, z - 1, st(blk("andesite_wall"))); set(x + i, 7, z + D, st(blk("andesite_wall"))); }
		set(x + W / 2, 5, z + D / 2, st(Blocks.LANTERN).setValue(BlockStateProperties.HANGING, true));
		set(x + W / 2, 9, z - 1, st(Blocks.GOLD_BLOCK));   // 시계 자리
	}

	// ---------- 광장 + 분수 ----------
	private void plaza(Feature f) {
		int x = f.x(), z = f.z(), S = f.w();
		int c = S / 2;
		for (int i = 0; i < S; i++) for (int k = 0; k < S; k++) {
			int dx = i - c, dz = k - c; double r = Math.sqrt(dx * dx + dz * dz);
			BlockState floor = ((dx + dz) & 1) == 0 ? st(Blocks.POLISHED_ANDESITE) : st(Blocks.STONE_BRICKS);
			if (r > 12.5) floor = road(x + i, z + k);
			set(x + i, -1, z + k, floor);
			if (r <= 5.5 && r > 4.5) { set(x + i, 0, z + k, st(Blocks.STONE_BRICK_WALL)); set(x + i, -1, z + k, st(Blocks.STONE_BRICKS)); }
			else if (r <= 4.5) { set(x + i, -1, z + k, st(Blocks.WATER)); set(x + i, -2, z + k, st(Blocks.PRISMARINE_BRICKS)); }
			else if (r > 9 && r < 10.5 && h(i, 9, k) % 4 != 0) set(x + i, 0, z + k, st(h(i, 0, k) % 3 == 0 ? Blocks.RED_TULIP : Blocks.OXEYE_DAISY));
			if (r > 9 && r < 10.5) set(x + i, -1, z + k, st(Blocks.GRASS_BLOCK));
		}
		// 가운데 기둥 + 물줄기
		for (int y = -1; y < 4; y++) set(x + c, y, z + c, st(Blocks.CHISELED_STONE_BRICKS));
		set(x + c, 4, z + c, st(Blocks.WATER));
		set(x + c, 5, z + c, st(Blocks.SEA_LANTERN));
	}

	private void lamp(int x, int z) {
		if (!isRoad(x + 1, z) && !isRoad(x, z - 1) && !isRoad(x, z + 1)) return;
		set(x, -1, z, st(Blocks.STONE_BRICKS));
		set(x, 0, z, st(Blocks.POLISHED_BLACKSTONE_WALL));
		set(x, 1, z, st(Blocks.POLISHED_BLACKSTONE_WALL));
		set(x, 2, z, st(Blocks.POLISHED_BLACKSTONE_WALL));
		set(x, 3, z, st(Blocks.LANTERN));
	}

	private void tree(int x, int z) {
		int H = 5 + h(x, 0, z) % 3;
		for (int y = 0; y < H; y++) set(x, y, z, st(Blocks.OAK_LOG));
		for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) for (int dy = -1; dy <= 2; dy++) {
			if (Math.abs(dx) + Math.abs(dz) + Math.max(0, dy) > 4) continue;
			if (dx == 0 && dz == 0 && dy < 1) continue;
			set(x + dx, H - 1 + dy, z + dz, st(h(x + dx, dy, z + dz) % 5 == 0 ? Blocks.FLOWERING_AZALEA_LEAVES : Blocks.OAK_LEAVES).setValue(BlockStateProperties.PERSISTENT, true));
		}
	}

	private void bed(Feature f) {
		for (int i = 0; i < f.w(); i++) for (int k = 0; k < f.d(); k++) {
			boolean edge = i == 0 || k == 0 || i == f.w() - 1 || k == f.d() - 1;
			set(f.x() + i, -1, f.z() + k, edge ? st(Blocks.STONE_BRICKS) : st(Blocks.GRASS_BLOCK));
			if (!edge && h(i, 5, k) % 3 == 0) set(f.x() + i, 0, f.z() + k, st(h(i, 6, k) % 2 == 0 ? Blocks.ROSE_BUSH : Blocks.PEONY)
					.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
			if (!edge && h(i, 5, k) % 3 == 0) set(f.x() + i, 1, f.z() + k, st(h(i, 6, k) % 2 == 0 ? Blocks.ROSE_BUSH : Blocks.PEONY)
					.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
		}
	}

	private static final Block[] WOOL = {blk("red_wool"), blk("blue_wool"), blk("yellow_wool"), blk("green_wool"), blk("purple_wool"), blk("orange_wool")};

	private void stall(Feature f) {
		int x = f.x(), z = f.z();
		for (int i = 0; i < 4; i++) for (int k = 0; k < 4; k++) {
			if ((i == 0 || i == 3) && (k == 0 || k == 3)) for (int y = 0; y < 3; y++) set(x + i, y, z + k, st(Blocks.SPRUCE_FENCE));
			set(x + i, 3, z + k, st(((i + k) & 1) == 0 ? WOOL[f.a()] : blk("white_wool")));
		}
		set(x + 1, 0, z + 1, st(Blocks.BARREL)); set(x + 2, 0, z + 1, st(Blocks.CRAFTING_TABLE));
	}

	private void npc(Feature f) {
		spawnMob(f.a() == 0 ? ModMobs.MERCHANT : ModMobs.MERCHANT, f.x(), 0, f.z());
	}

	private void spawnMob(net.minecraft.world.entity.EntityType<? extends Mob> type, int x, int y, int z) {
		BlockPos p = new BlockPos(x0 + x, y0 + y, z0 + z);
		if (!box.isInside(p)) return;
		Mob m = type.create(lv.getLevel(), EntitySpawnReason.STRUCTURE);
		if (m == null) return;
		m.snapTo(p.getX() + 0.5, p.getY(), p.getZ() + 0.5);
		m.setPersistenceRequired();
		lv.addFreshEntity(m);
	}
}
