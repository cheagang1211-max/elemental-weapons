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
 * 수도 시가지 (한 채 한 채 손으로 짓듯이 설계).
 * 성을 중심으로 8갈래 큰길, 갈래 사이마다 기능 건물 1채 + 집 1~2채, 정원·가로수·가로등.
 *  대장간 · 여관 · 시장 · 운명의 전당 · 마법 연구소 · 훈련장 · 균열의 관문 · 창고와 농장
 */
public final class Capital {
	public static final int G = 170;

	public enum Kind { SMITH, INN, MARKET, SAGE_HALL, MAGE_LAB, TRAINING, RIFT_GATE, STORAGE, HOUSE }

	public record Lot(Kind kind, int x, int z, int rot, int seed) {
		int half() { return kind == Kind.HOUSE ? 8 : kind == Kind.MARKET || kind == Kind.TRAINING ? 15 : 12; }
	}

	public static final List<Lot> LOTS = new ArrayList<>();
	public static BlockPos SAGE_POS = new BlockPos(0, G + 1, 18);
	public static final List<BlockPos> MERCHANTS = new ArrayList<>();

	static {
		Kind[] spec = {Kind.SMITH, Kind.INN, Kind.MARKET, Kind.SAGE_HALL, Kind.MAGE_LAB, Kind.TRAINING, Kind.RIFT_GATE, Kind.STORAGE};
		for (int k = 0; k < 8; k++) {
			double a = Math.toRadians(22.5 + k * 45);
			int x = (int) Math.round(Math.cos(a) * 110), z = (int) Math.round(Math.sin(a) * 110);
			LOTS.add(new Lot(spec[k], x, z, faceCenter(x, z), 100 + k));
			for (int s = -1; s <= 1; s += 2) {
				if (k % 2 == 1 && s > 0) continue;                    // 집은 모두 12채
				double b = a + Math.toRadians(11 * s);
				int hx = (int) Math.round(Math.cos(b) * 168), hz = (int) Math.round(Math.sin(b) * 168);
				LOTS.add(new Lot(Kind.HOUSE, hx, hz, faceCenter(hx, hz), 500 + k * 7 + s));
			}
		}
		for (Lot l : LOTS) {
			if (l.kind == Kind.SAGE_HALL) SAGE_POS = new BlockPos(l.x, G + 1, l.z);
			if (l.kind == Kind.MARKET) { MERCHANTS.add(new BlockPos(l.x - 5, G + 1, l.z)); MERCHANTS.add(new BlockPos(l.x + 5, G + 1, l.z)); }
			if (l.kind == Kind.SMITH) MERCHANTS.add(new BlockPos(l.x, G + 1, l.z));
		}
	}

	/** 정면(+z 로컬)이 성 쪽을 보게 하는 회전 */
	private static int faceCenter(int x, int z) {
		double dx = -x, dz = -z;
		if (Math.abs(dx) > Math.abs(dz)) return dx < 0 ? 1 : 3;   // 정면 -x: 회전1, +x: 회전3
		return dz > 0 ? 0 : 2;
	}

	// ---------------- 그리기 ----------------
	private final WorldGenLevel lv;
	private final int minX, minZ, maxX, maxZ;

	public Capital(WorldGenLevel lv, int minX, int minZ) { this.lv = lv; this.minX = minX; this.minZ = minZ; this.maxX = minX + 15; this.maxZ = minZ + 15; }

	private boolean in(int x, int z) { return x >= minX && x <= maxX && z >= minZ && z <= maxZ; }
	private void set(int x, int y, int z, BlockState s) { if (in(x, z)) lv.setBlock(new BlockPos(x, y, z), s, 2); }
	private BlockState get(int x, int y, int z) { return lv.getBlockState(new BlockPos(x, y, z)); }
	private static BlockState st(Block b) { return b.defaultBlockState(); }
	private static BlockState b(String id) { return BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("minecraft", id)).defaultBlockState(); }
	private static int h(int x, int y, int z) { int n = x * 73856093 ^ y * 19349663 ^ z * 83492791; n ^= n >>> 13; n *= 0x5bd1e995; return (n ^ n >>> 15) & 0x7fffffff; }

	public void paint() {
		if (maxX < -205 || minX > 205 || maxZ < -205 || minZ > 205) { outerGarden(); return; }
		ground();
		for (Lot l : LOTS) {
			int r = l.half() + 3;
			if (l.x + r < minX || l.x - r > maxX || l.z + r < minZ || l.z - r > maxZ) continue;
			Bld o = new Bld(l);
			o.clear(-l.half(), l.half());
			switch (l.kind) {
				case SMITH -> smith(o);
				case INN -> inn(o);
				case MARKET -> market(o);
				case SAGE_HALL -> sageHall(o);
				case MAGE_LAB -> mageLab(o);
				case TRAINING -> training(o);
				case RIFT_GATE -> riftGate(o);
				case STORAGE -> storage(o);
				case HOUSE -> house(o);
			}
		}
		trees();
		outerGarden();
	}

	/** 바닥: 8갈래 큰길, 둘레길, 정원, 꽃 */
	private void ground() {
		for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
			double r = Math.hypot(x + 0.5, z + 0.5);
			if (r < 57 || r > 197) continue;
			if (Math.abs(x) <= 56 && Math.abs(z) <= 56) continue;   // 성 광장
			double th = Math.toDegrees(Math.atan2(z + 0.5, x + 0.5));
			double toAve = Math.abs(((th % 45) + 45 + 22.5) % 45 - 22.5) * Math.PI / 180 * r;   // 큰길 중심까지 거리
			boolean avenue = toAve < 3.2;
			boolean lane = Math.abs(r - 140) < 1.6;
			for (int y = G + 1; y <= G + 4; y++) if (!get(x, y, z).isAir() && (avenue || lane)) set(x, y, z, st(Blocks.AIR));
			if (avenue) {
				int k = h(x, 0, z) % 10;
				set(x, G, z, toAve > 2.2 ? (k < 3 ? st(Blocks.MOSSY_COBBLESTONE) : st(Blocks.COBBLESTONE)) : (k < 5 ? st(Blocks.POLISHED_ANDESITE) : k < 8 ? st(Blocks.STONE_BRICKS) : st(Blocks.CRACKED_STONE_BRICKS)));
				// 가로등 (16칸마다 양쪽)
				if (Math.abs(toAve - 3.6) < 0.5 && Math.floorMod((int) Math.round(r), 16) == 0) lamp(x, z);
			} else if (lane) {
				set(x, G, z, h(x, 1, z) % 4 == 0 ? st(Blocks.COARSE_DIRT) : b("dirt_path"));
			} else if (get(x, G + 1, z).isAir()) {
				set(x, G, z, st(Blocks.GRASS_BLOCK));
				int k = h(x, 2, z) % 100;
				if (k < 7) set(x, G + 1, z, st(Blocks.SHORT_GRASS));
				else if (k < 10) set(x, G + 1, z, k == 7 ? st(Blocks.POPPY) : k == 8 ? st(Blocks.CORNFLOWER) : st(Blocks.ALLIUM));
				else if (k == 10) set(x, G + 1, z, st(Blocks.LILY_OF_THE_VALLEY));
			}
		}
	}

	private void lamp(int x, int z) {
		set(x, G + 1, z, st(Blocks.POLISHED_BLACKSTONE_WALL));
		set(x, G + 2, z, st(Blocks.POLISHED_BLACKSTONE_WALL));
		set(x, G + 3, z, st(Blocks.POLISHED_BLACKSTONE_WALL));
		set(x, G + 4, z, st(Blocks.LANTERN));
	}

	/** 큰길 가로수 (손질한 둥근 수형) */
	private void trees() {
		for (int k = 0; k < 8; k++) {
			double a = Math.toRadians(k * 45);
			for (int r = 72; r <= 188; r += 18) for (int s = -1; s <= 1; s += 2) {
				double px = Math.cos(a) * r - Math.sin(a) * 6.5 * s, pz = Math.sin(a) * r + Math.cos(a) * 6.5 * s;
				int x = (int) Math.round(px), z = (int) Math.round(pz);
				if (Math.abs(x) <= 58 && Math.abs(z) <= 58) continue;
				boolean busy = false;
				for (Lot l : LOTS) if (Math.abs(l.x - x) <= l.half() + 3 && Math.abs(l.z - z) <= l.half() + 3) busy = true;
				if (!busy) tree(x, z, (k + r) % 3 == 0 ? "flowering_azalea_leaves" : "cherry_leaves", 5);
			}
		}
	}

	private void tree(int x, int z, String leaves, int H) {
		BlockState leaf = b(leaves).setValue(BlockStateProperties.PERSISTENT, true);
		BlockState log = leaves.startsWith("cherry") ? b("cherry_log") : st(Blocks.OAK_LOG);
		for (int dx = -3; dx <= 3; dx++) for (int dz = -3; dz <= 3; dz++) for (int dy = -2; dy <= 2; dy++) {
			double d = Math.sqrt(dx * dx + dz * dz * 1.0 + dy * dy * 1.6);
			if (d <= 3.0 - (h(x + dx, dy, z + dz) % 4 == 0 ? 0.6 : 0) && in(x + dx, z + dz) && get(x + dx, G + H + dy, z + dz).isAir())
				set(x + dx, G + H + dy, z + dz, leaf);
		}
		for (int y = G + 1; y < G + H + 1; y++) set(x, y, z, log);
		set(x, G, z, st(Blocks.GRASS_BLOCK));
	}

	/** 순환로 바깥 정원: 숲과 꽃밭 (탑·다리 근처는 비움) */
	private void outerGarden() {
		if (maxX < -380 || minX > 380 || maxZ < -380 || minZ > 380) return;
		for (int cx = Math.floorDiv(minX, 22) - 1; cx <= Math.floorDiv(maxX, 22) + 1; cx++)
			for (int cz = Math.floorDiv(minZ, 22) - 1; cz <= Math.floorDiv(maxZ, 22) + 1; cz++) {
				int s = h(cx, 77, cz);
				if (s % 100 > 42) continue;
				int x = cx * 22 + s % 13 - 6, z = cz * 22 + (s / 13) % 13 - 6;
				double r = Math.hypot(x, z);
				if (r < 210 || r > 355) continue;
				boolean near = false;
				for (int k = 0; k < 7; k++) {
					double a = SkyLayout.regionAngle(k);
					double along = x * Math.cos(a) + z * Math.sin(a), across = Math.abs(-x * Math.sin(a) + z * Math.cos(a));
					if (along > 0 && across < 16) near = true;                                  // 대교 길
					double ta = a + Math.PI / 7;
					if (Math.hypot(x - Math.cos(ta) * 246, z - Math.sin(ta) * 246) < 18) near = true;   // 마법탑
				}
				if (near || SkyLayout.surfaceTop(x, z) < G - 3) continue;
				int H = 4 + s % 3;
				if (x - 4 > maxX || x + 4 < minX || z - 4 > maxZ || z + 4 < minZ) continue;
				tree(x, z, s % 3 == 0 ? "flowering_azalea_leaves" : "cherry_leaves", H);
			}
	}

	// ---------------- 건물 도구 (로컬 좌표: 정면 = +z, 가운데 = 0,0) ----------------
	private final class Bld {
		final Lot l;
		final Rotation rot;
		Bld(Lot l) { this.l = l; this.rot = switch (l.rot) { case 1 -> Rotation.CLOCKWISE_90; case 2 -> Rotation.CLOCKWISE_180; case 3 -> Rotation.COUNTERCLOCKWISE_90; default -> Rotation.NONE; }; }
		int wx(int lx, int lz) { return switch (l.rot) { case 1 -> l.x - lz; case 2 -> l.x - lx; case 3 -> l.x + lz; default -> l.x + lx; }; }
		int wz(int lx, int lz) { return switch (l.rot) { case 1 -> l.z + lx; case 2 -> l.z - lz; case 3 -> l.z - lx; default -> l.z + lz; }; }
		void p(int lx, int ly, int lz, BlockState s) { Capital.this.set(wx(lx, lz), G + ly, wz(lx, lz), s.rotate(rot)); }
		void fill(int x0, int y0, int z0, int x1, int y1, int z1, BlockState s) {
			for (int x = x0; x <= x1; x++) for (int y = y0; y <= y1; y++) for (int z = z0; z <= z1; z++) p(x, y, z, s);
		}
		void clear(int a, int b2) {
			for (int x = a - 1; x <= b2 + 1; x++) for (int z = a - 1; z <= b2 + 1; z++) {
				for (int y = 1; y <= 24; y++) p(x, y, z, st(Blocks.AIR));
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
				BlockPos pos = new BlockPos(wx(x, z), G + y, wz(x, z));
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

	private static BlockState stairs(String id) { return b(id + "_stairs"); }
	private static BlockState slab(String id) { return b(id + "_slab"); }

	// ---------------- 기능 건물 ----------------

	/** 대장간: 돌 화덕과 용암 화로, 모루·숫돌·대장장이 작업대·용광로, 열린 작업장 */
	private void smith(Bld o) {
		o.floor(-9, -7, 9, 7, 0, st(Blocks.STONE_BRICKS));
		o.walls(-9, -7, 9, 7, 1, 5, st(Blocks.COBBLESTONE), st(Blocks.POLISHED_ANDESITE), st(Blocks.STRIPPED_SPRUCE_LOG));
		for (int x = -8; x <= 8; x++) for (int y = 1; y <= 4; y++) o.p(x, y, 7, st(Blocks.AIR));     // 정면은 트인 작업장
		for (int x = -9; x <= 9; x += 6) for (int y = 1; y <= 5; y++) o.p(x, y, 7, st(Blocks.STRIPPED_SPRUCE_LOG));
		o.gable(-9, -7, 9, 7, 6, stairs("deepslate_tile"), slab("deepslate_tile"), st(Blocks.SPRUCE_PLANKS));
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
		o.sign(0, 5, 8, "대장간", "강화·수리·제련");
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
		o.sign(0, 1, 13, "시장", "원소 상인");
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
		o.sign(2, 4, 9, "마법 연구소", "마법부여·물약");
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
				{"cherry", "calcite", "deepslate_tile"}, {"spruce", "light_gray_terracotta", "waxed_cut_copper"}};
		String[] P = pal[Math.floorMod(s, pal.length)];
		int w = 4 + s % 2, d = 3 + (s / 2) % 2;
		BlockState log = b(P[0].equals("dark_oak") ? "dark_oak_log" : P[0].equals("cherry") ? "cherry_log" : P[0] + "_log");
		o.floor(-w, -d, w, d, 0, b(P[0] + "_planks"));
		o.walls(-w, -d, w, d, 1, 4, st(Blocks.COBBLESTONE), b(P[1]), log);
		boolean two = s % 3 == 0;
		if (two) { o.walls(-w, -d, w, d, 5, 7, b(P[0] + "_planks"), b(P[1]), log); o.floor(-w + 1, -d + 1, w - 1, d - 1, 4, b(P[0] + "_planks")); }
		String roof = P[2];
		BlockState rs = roof.equals("purpur") ? b("purpur_stairs") : stairs(roof), rl = roof.equals("purpur") ? b("purpur_slab") : slab(roof);
		o.gable(-w, -d, w, d, two ? 8 : 5, rs, rl, b(P[1]));
		o.door(0, 1, d, P[0].equals("cherry") ? "cherry" : P[0]);
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
