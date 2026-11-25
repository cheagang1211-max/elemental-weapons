package com.elementalweapons.world.sky;

import java.util.ArrayDeque;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.item.Element;

/**
 * 천공성 증축 (서버가 켜져 있는 동안 청크 하나씩 지어 넣음, 한 번만):
 *  지하 · 원소의 성소  — 섬 암반 속 거대한 동굴, 무너진 7속성 신전과 부서진 대제단 (폐허)
 *  6층  · 영웅의 투기장 — 왕성 위에 떠 있는 원형 투기장: 관중석, 2단 아치 외벽, 영웅 석상, 화로, 깃발
 *  7층  · 천상의 정상   — 구름 위의 섬: 돔 신전과 기둥 회랑, 천상의 성문, 천사 석상, 벚꽃 정원
 * 가운데 승강기 빛 기둥이 지하에서 7층까지 이어짐.
 */
public final class Annex {
	private Annex() {}

	public static final int YB = 118, Y6 = 336, Y7 = 392;
	public static final int LIFT_TOP = Y7 + 3;
	private static final int VERSION = 1;
	private static final int CH = 4;   // 청크 범위 -CH..CH-1

	public static final AttachmentType<Integer> BUILT = AttachmentRegistry.<Integer>builder()
			.persistent(com.mojang.serialization.Codec.INT).buildAndRegister(ElementalWeaponsMod.id("annex_built"));

	private static ArrayDeque<int[]> queue;
	public static boolean done;

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ServerLevel ow = server.overworld();
			if (!ModSky.active(ow) || ModSky.OLD_WORLD) return;
			try {
				Integer v = ow.getAttached(BUILT);
				if (v != null && v >= VERSION) { done = true; if (server.getTickCount() % 100 == 61) labels(server, ow); return; }
				if (ow.getMaxY() < LIFT_TOP + 40) {   // 세계 높이가 아직 안 올라감
					if (server.getTickCount() % 6000 == 1) ElementalWeaponsMod.LOGGER.warn("[ANNEX] 세계 높이 한도({})가 낮아 6·7층을 지을 수 없습니다", ow.getMaxY());
				}
				if (queue == null) {
					queue = new ArrayDeque<>();
					for (int cx = -CH; cx < CH; cx++) for (int cz = -CH; cz < CH; cz++) queue.add(new int[]{cx, cz});
					ElementalWeaponsMod.LOGGER.info("[ANNEX] 천공성 증축 시작 (청크 {}개)", queue.size());
				}
				if (server.getTickCount() % 2 != 0) return;
				int[] c = queue.poll();
				if (c != null) {
					ow.getChunk(c[0], c[1]);
					new Annex.Builder(ow, c[0] * 16, c[1] * 16).build();
				}
				if (queue.isEmpty()) {
					ow.setAttached(BUILT, VERSION);
					queue = null; done = true;
					ElementalWeaponsMod.LOGGER.info("[ANNEX] 천공성 증축 완료 (지하 성소 · 6층 투기장 · 7층 정상)");
				}
			} catch (Throwable t) { ElementalWeaponsMod.LOGGER.error("[ANNEX] 오류", t); }
		});
	}

	/** 승강기 안내용 층 이름 */
	public static String floorName(double y) {
		if (y < Capital.FY[0] - 2) return "지하 · 원소의 성소";
		if (y >= Y7 - 2) return "7층 · 천상의 정상";
		if (y >= Y6 - 2) return "6층 · 영웅의 투기장";
		return Capital.FLOOR_NAME[Capital.floorAt(y)];
	}

	private static final double[] SHRINE_A = new double[7];
	static { for (int k = 0; k < 7; k++) SHRINE_A[k] = k * Math.PI * 2 / 7 - Math.PI / 2; }
	private static final int SHRINE_R = 30;

	private static void labels(MinecraftServer server, ServerLevel ow) {
		BossGate.label(server, ow, new BlockPos(0, Y6 + 9, 31), "ew_label_arena", "⚔ 영웅의 투기장", "#FF8A4D", "6층 · 영웅들이 실력을 겨루는 곳");
		BossGate.label(server, ow, new BlockPos(0, Y7 + 17, -23), "ew_label_summit", "✦ 천상의 정상", "#FFE27A", "7층 · 구름 위의 성문과 신전");
		BossGate.label(server, ow, new BlockPos(0, YB + 9, 10), "ew_label_shrine", "◈ 원소의 성소", "#B48CFF", "지하 · 무너진 일곱 신전");
		for (int k = 0; k < 7; k++) {
			Element e = Element.values()[k];
			int x = (int) Math.round(Math.cos(SHRINE_A[k]) * SHRINE_R), z = (int) Math.round(Math.sin(SHRINE_A[k]) * SHRINE_R);
			BossGate.label(server, ow, new BlockPos(x, YB + 8, z), "ew_label_shrine" + k, e.koName + "의 성소", String.format("#%06X", e.color), "무너진 신전");
		}
	}

	// =====================================================================
	static final class Builder {
		private final ServerLevel lv;
		private final int minX, minZ, maxX, maxZ;
		Builder(ServerLevel lv, int minX, int minZ) { this.lv = lv; this.minX = minX; this.minZ = minZ; maxX = minX + 15; maxZ = minZ + 15; }

		private boolean in(int x, int z) { return x >= minX && x <= maxX && z >= minZ && z <= maxZ; }
		private void set(int x, int y, int z, BlockState s) {
			if (!in(x, z) || y <= lv.getMinY() || y >= lv.getMaxY()) return;
			lv.setBlock(new BlockPos(x, y, z), s, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
		}
		private BlockState get(int x, int y, int z) { return lv.getBlockState(new BlockPos(x, y, z)); }
		private void setIfAir(int x, int y, int z, BlockState s) { if (in(x, z) && get(x, y, z).isAir()) set(x, y, z, s); }

		static BlockState b(String id) { return BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("minecraft", id)).defaultBlockState(); }
		static BlockState b(String id, String... kv) {
			BlockState s = b(id);
			for (int i = 0; i + 1 < kv.length; i += 2) s = with(s, kv[i], kv[i + 1]);
			return s;
		}
		static BlockState with(BlockState s, String prop, String val) {
			Property<?> p = s.getBlock().getStateDefinition().getProperty(prop);
			return p == null ? s : setAny(s, p, val);
		}
		private static <T extends Comparable<T>> BlockState setAny(BlockState s, Property<T> p, String v) {
			return p.getValue(v).map(x -> s.setValue(p, x)).orElse(s);
		}
		static int h(int x, int y, int z) { int n = x * 73856093 ^ y * 19349663 ^ z * 83492791; n ^= n >>> 13; n *= 0x5bd1e995; return (n ^ n >>> 15) & 0x7fffffff; }
		static double noise(int x, int z, double s) {   // 부드러운 값 잡음 0..1
			double fx = x / s, fz = z / s; int ix = (int) Math.floor(fx), iz = (int) Math.floor(fz);
			double tx = fx - ix, tz = fz - iz; tx = tx * tx * (3 - 2 * tx); tz = tz * tz * (3 - 2 * tz);
			double a = (h(ix, 7, iz) % 1000) / 1000.0, b2 = (h(ix + 1, 7, iz) % 1000) / 1000.0, c = (h(ix, 7, iz + 1) % 1000) / 1000.0, d = (h(ix + 1, 7, iz + 1) % 1000) / 1000.0;
			return (a * (1 - tx) + b2 * tx) * (1 - tz) + (c * (1 - tx) + d * tx) * tz;
		}
		/** 중심에서 바깥을 향하는 방향 이름 */
		static String outward(double x, double z) {
			if (Math.abs(x) > Math.abs(z)) return x > 0 ? "east" : "west";
			return z > 0 ? "south" : "north";
		}
		static String inward(double x, double z) {
			String o = outward(x, z);
			return switch (o) { case "east" -> "west"; case "west" -> "east"; case "south" -> "north"; default -> "south"; };
		}

		void build() {
			if (maxX >= -64 && minX <= 64 && maxZ >= -64 && minZ <= 64) {
				shrines();
				if (lv.getMaxY() > LIFT_TOP + 40) { arena(); summit(); }
				shaft();
			}
		}

		// ---------------- 승강기 빛 기둥 ----------------
		private void shaft() {
			for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
				double r = Math.hypot(x + 0.5, z + 0.5);
				if (r > Capital.SHAFT_R + 3.6) continue;
				if (r <= Capital.SHAFT_R + 0.5) {
					set(x, YB, z, b("pearlescent_froglight"));
					for (int y = YB + 1; y <= Capital.FY[0]; y++) set(x, y, z, b("air"));
					if (lv.getMaxY() > LIFT_TOP + 40) {
						for (int y = Capital.TOP; y < Y7 + 3; y++) set(x, y, z, b("air"));
						set(x, Y7 + 3, z, b("pearlescent_froglight"));
					}
				} else if (r <= Capital.SHAFT_R + 1.5) {
					// 1층 바닥 구멍 테두리
					set(x, Capital.FY[0], z, b("gold_block"));
					if (lv.getMaxY() > LIFT_TOP + 40) {
						for (int y = Capital.TOP + 14; y < Y6 - 2; y++) setIfAir(x, y, z, b("white_stained_glass"));   // 지붕 위 빛 통로
						for (int y = Y6 + 14; y < Y7 - 2; y += 9) set(x, y, z, b("gold_block"));                        // 떠 있는 금 고리
					}
				} else if (r <= Capital.SHAFT_R + 2.5 && lv.getMaxY() > LIFT_TOP + 40) {
					for (int y = Y6 + 14; y < Y7 - 2; y += 9) if ((x + z) % 2 == 0) set(x, y, z, b("end_rod"));
				}
			}
		}

		// ================================================================
		// 지하 · 원소의 성소 (폐허)
		// ================================================================
		private static final String[][] EL = {   // 바닥, 기둥, 빛, 제단 위, 덧칠
				{"oxidized_cut_copper", "weathered_copper", "copper_bulb", "lightning_rod", "exposed_cut_copper"},
				{"polished_blackstone_bricks", "polished_basalt", "magma_block", "campfire", "cracked_polished_blackstone_bricks"},
				{"prismarine_bricks", "dark_prismarine", "sea_lantern", "conduit", "prismarine"},
				{"moss_block", "mossy_stone_bricks", "shroomlight", "flowering_azalea", "azalea_leaves"},
				{"calcite", "quartz_pillar", "verdant_froglight", "end_rod", "smooth_quartz"},
				{"polished_blackstone", "obsidian", "crying_obsidian", "soul_campfire", "sculk"},
				{"smooth_quartz", "quartz_pillar", "pearlescent_froglight", "end_rod", "gold_block"},
		};
		private static final int RB = 48;

		private int caveH(int x, int z) {
			double r = Math.hypot(x + 0.5, z + 0.5);
			if (r > RB) return 0;
			return (int) (25 * Math.sqrt(1 - (r / RB) * (r / RB)) + noise(x, z, 9) * 3);
		}

		private void shrines() {
			for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
				int H = caveH(x, z);
				if (H < 2) continue;
				if (get(x, YB - 3, z).isAir() || get(x, YB + H + 2, z).isAir()) continue;   // 섬이 얇은 곳은 뚫지 않음
				double r = Math.hypot(x + 0.5, z + 0.5);
				int hh = h(x, 1, z);
				// 바닥: 금 간 돌 · 이끼 · 자갈 · 웅덩이
				double n = noise(x, z, 6);
				BlockState fl = hh % 9 == 0 ? b("cracked_stone_bricks") : hh % 9 == 1 ? b("mossy_cobblestone") : hh % 9 == 2 ? b("cobblestone")
						: n > 0.72 ? b("moss_block") : n < 0.12 ? b("coarse_dirt") : hh % 3 == 0 ? b("mossy_stone_bricks") : b("stone_bricks");
				if (r > 12 && noise(x + 300, z, 5) > 0.86) fl = b("water");
				set(x, YB, z, fl);
				for (int y = YB + 1; y <= YB + H; y++) set(x, y, z, b("air"));
				// 천장: 돌 → 응회암, 매달린 뿌리 · 빛나는 덩굴 · 등불
				set(x, YB + H + 1, z, hh % 23 == 0 ? b("shroomlight") : b("tuff"));
				if (hh % 11 == 0 && H > 6) set(x, YB + H, z, b("hanging_roots"));
				else if (hh % 13 == 1 && H > 8) {
					int len = 2 + hh % 4;
					for (int i = 0; i < len; i++) set(x, YB + H - i, z, b(i == len - 1 ? "cave_vines" : "cave_vines_plant", "berries", "true"));
				} else if (hh % 97 == 3 && H > 10) {
					for (int i = 0; i < 3; i++) set(x, YB + H - i, z, b("chain"));
					set(x, YB + H - 3, z, b("lantern", "hanging", "true"));
				} else if (hh % 71 == 5 && H > 6) set(x, YB + H, z, b("spore_blossom"));
				// 바닥 잔해
				if (fl.is(Blocks.MOSS_BLOCK) && hh % 3 == 0) set(x, YB + 1, z, b("moss_carpet"));
				else if (hh % 37 == 0) set(x, YB + 1, z, b("cobblestone_slab"));
				else if (hh % 53 == 0) set(x, YB + 1, z, b("stone_brick_slab"));
				else if (hh % 211 == 0) set(x, YB + 1, z, b("bone_block"));
			}
			// 바깥 고리 기둥 (일부 무너짐)
			for (int k = 0; k < 14; k++) {
				double a = (k + 0.5) * Math.PI * 2 / 14 - Math.PI / 2;
				int cx = (int) Math.round(Math.cos(a) * 41), cz = (int) Math.round(Math.sin(a) * 41);
				int top = k % 3 == 1 ? 4 + h(k, 2, 3) % 5 : 99;
				pillar(cx, cz, 1.6, top, "stone_bricks", "chiseled_stone_bricks");
			}
			for (int k = 0; k < 7; k++) shrine(k);
			altar();
		}

		private void pillar(int cx, int cz, double rad, int top, String body, String band) {
			for (int x = cx - 2; x <= cx + 2; x++) for (int z = cz - 2; z <= cz + 2; z++) {
				if (!in(x, z) || Math.hypot(x - cx, z - cz) > rad) continue;
				int H = caveH(x, z);
				int ht = Math.min(H, top);
				for (int y = YB + 1; y <= YB + ht; y++) {
					int dy = y - YB;
					BlockState s = dy == 1 || dy % 8 == 0 ? b(band) : h(x, y, z) % 7 == 0 ? b("cracked_stone_bricks") : b(body);
					set(x, y, z, s);
				}
				if (top < H) {   // 부러진 기둥 둘레 돌무더기
					for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++)
						if (h(x + dx, 5, z + dz) % 4 == 0) setIfAir(x + dx, YB + 1, z + dz, b("cobblestone_slab"));
				}
			}
		}

		private void shrine(int k) {
			String[] m = EL[k];
			int cx = (int) Math.round(Math.cos(SHRINE_A[k]) * SHRINE_R), cz = (int) Math.round(Math.sin(SHRINE_A[k]) * SHRINE_R);
			if (cx + 8 < minX || cx - 8 > maxX || cz + 8 < minZ || cz - 8 > maxZ) return;
			int[] ph = {11, 4, 8, 2};   // 네 기둥 높이 (무너진 정도)
			for (int x = cx - 7; x <= cx + 7; x++) for (int z = cz - 7; z <= cz + 7; z++) {
				if (!in(x, z)) continue;
				int dx = x - cx, dz = z - cz;
				double d = Math.hypot(dx, dz);
				int hh = h(x, 3, z);
				if (d <= 5.5) {
					set(x, YB + 1, z, hh % 6 == 0 ? b("cracked_stone_bricks") : hh % 6 == 1 ? b(m[4]) : b(m[0]));
					for (int y = YB + 2; y <= YB + 12; y++) if (!get(x, y, z).isAir() && y < YB + caveH(x, z)) set(x, y, z, b("air"));
				} else if (d <= 6.6) set(x, YB + 1, z, hh % 3 == 0 ? b("cobblestone_slab") : b("stone_brick_slab"));
				// 네 모서리 기둥
				int pi = (dx > 0 ? 1 : 0) + (dz > 0 ? 2 : 0);
				if (Math.abs(Math.abs(dx) - 4) <= 0 && Math.abs(Math.abs(dz) - 4) <= 0) {
					for (int y = YB + 2; y <= YB + 1 + ph[pi]; y++) set(x, y, z, (y - YB) % 5 == 0 ? b(m[2]) : b(m[1]));
					if (ph[pi] < 6) for (int i = 1; i <= 3; i++) setIfAir(x + (dx > 0 ? i : -i), YB + 2, z, b(m[1]));   // 쓰러진 기둥
				}
				// 남은 지붕 조각 (한쪽만)
				if (Math.abs(dx) <= 5 && Math.abs(dz) <= 5 && dx + dz >= 3) set(x, YB + 12, z, hh % 5 == 0 ? b("air") : b(m[4].equals("azalea_leaves") ? "mossy_stone_brick_slab" : "stone_brick_slab", "type", "top"));
				// 풀 성소는 넝쿨이 덮음
				if (k == 3 && hh % 4 == 0 && d <= 6) setIfAir(x, YB + 13, z, b("azalea_leaves", "persistent", "true"));
				// 물 성소: 제단 둘레 물길
				if (k == 2 && d > 2.2 && d <= 3.2) set(x, YB + 1, z, b("water"));
			}
			// 제단
			if (in(cx, cz)) {
				set(cx, YB + 2, cz, b("chiseled_stone_bricks"));
				set(cx, YB + 3, cz, b(m[2], "lit", "true"));
				set(cx, YB + 4, cz, b(m[3], "lit", "true"));
			}
		}

		private void altar() {
			for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
				double r = Math.hypot(x + 0.5, z + 0.5);
				if (r > 9.5 || r <= Capital.SHAFT_R + 0.5) continue;
				if (caveH(x, z) < 2) continue;
				double th = Math.atan2(z + 0.5, x + 0.5);
				int seg = Math.floorMod((int) Math.floor((th + Math.PI / 2) / (Math.PI * 2 / 7)), 7);
				if (r <= 3.5) set(x, YB, z, b("gold_block"));
				else if (r <= 5) set(x, YB, z, b(EL[seg][0]));
				else if (r <= 6.5) set(x, YB, z, b("chiseled_stone_bricks"));
				else if (r <= 7.6) { set(x, YB, z, b("polished_andesite")); set(x, YB + 1, z, h(x, 4, z) % 4 == 0 ? b("air") : b("stone_brick_slab")); }
			}
			// 부서진 일곱 비석
			for (int k = 0; k < 7; k++) {
				double a = SHRINE_A[k];
				int x = (int) Math.round(Math.cos(a) * 8.5), z = (int) Math.round(Math.sin(a) * 8.5);
				if (!in(x, z)) continue;
				int ht = 2 + h(k, 9, 1) % 5;
				for (int y = YB + 1; y <= YB + ht; y++) set(x, y, z, b("chiseled_stone_bricks"));
				set(x, YB + ht + 1, z, b(EL[k][2], "lit", "true"));
			}
		}

		// ================================================================
		// 6층 · 영웅의 투기장
		// ================================================================
		private static final double RA = 20, RS = 33, RW = 36, RI = 42;

		private void arena() {
			for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
				double r = Math.hypot(x + 0.5, z + 0.5);
				if (r > RI + 0.5) continue;
				double th = Math.atan2(z + 0.5, x + 0.5);
				int hh = h(x, 6, z);
				// 떠 있는 섬 바닥
				int depth = 3 + (int) ((RI - r) * 0.32) + (int) (noise(x, z, 7) * 3);
				for (int y = Y6 - depth; y < Y6; y++) set(x, y, z, y < Y6 - depth + 2 ? b("deepslate") : y < Y6 - 2 ? b(hh % 5 == 0 ? "andesite" : "stone") : b("polished_andesite"));
				if (r <= Capital.SHAFT_R + 0.5) continue;
				// 매달린 사슬 등불 (밑면)
				if (hh % 61 == 0 && r > 18) { for (int i = 1; i <= 3; i++) set(x, Y6 - depth - i, z, b("chain")); set(x, Y6 - depth - 4, z, b("lantern", "hanging", "true")); }
				double deg = Math.toDegrees(th);
				double gateOff = Math.abs(((deg % 90) + 90 + 45) % 90 - 45);     // 동서남북 문까지 각도
				boolean gate = gateOff * Math.PI / 180 * r < 2.6;
				double aisle = Math.abs(((deg - 45) % 45 + 45 + 22.5) % 45 - 22.5) * Math.PI / 180 * r;
				if (r <= RA) {   // 모래 경기장
					BlockState s = r <= 3.6 ? b("chiseled_quartz_block") : r <= 4.6 ? b("gold_block")
							: (r > 6 && r <= 7) || (r > 12 && r <= 13) ? b("cut_sandstone") : r > 18.6 ? b("chiseled_sandstone") : b("smooth_sandstone");
					if (r > 4.6 && r <= 12 && Math.abs(((deg % 45) + 45 + 22.5) % 45 - 22.5) * Math.PI / 180 * r < 0.6) s = b("cut_red_sandstone");   // 빛살 무늬
					set(x, Y6, z, s);
				} else if (r <= RA + 1.5) {   // 경기장 벽
					set(x, Y6, z, b("polished_andesite"));
					if (gate) { for (int y = Y6 + 1; y <= Y6 + 6; y++) set(x, y, z, b("air")); continue; }
					for (int y = Y6 + 1; y <= Y6 + 2; y++) set(x, y, z, b("polished_andesite"));
					set(x, Y6 + 3, z, b("chiseled_stone_bricks"));
					if (hh % 13 == 0) set(x, Y6 + 4, z, b("lantern"));
				} else if (r <= RS) {   // 관중석
					set(x, Y6, z, b("polished_andesite"));
					if (gate) {
						for (int y = Y6 + 1; y <= Y6 + 6; y++) set(x, y, z, b("air"));
						set(x, Y6 + 7, z, b("chiseled_stone_bricks"));   // 입구 통로 천장
						int tier = (int) ((r - RA - 1.5) / 1.5);
						for (int y = Y6 + 8; y <= Y6 + 3 + tier; y++) set(x, y, z, b("stone_bricks"));
						if (Y6 + 3 + tier >= Y6 + 8) set(x, Y6 + 4 + tier, z, b("stone_brick_stairs", "facing", outward(x + 0.5, z + 0.5)));
						continue;
					}
					int tier = (int) ((r - RA - 1.5) / 1.5);
					int sy = Y6 + 3 + tier;
					for (int y = Y6 + 1; y < sy; y++) set(x, y, z, b("stone_bricks"));
					boolean isAisle = aisle < 1.2;
					set(x, sy, z, b(isAisle ? "quartz_stairs" : tier % 3 == 2 ? "red_nether_brick_stairs" : "stone_brick_stairs", "facing", outward(x + 0.5, z + 0.5)));
					for (int y = sy + 1; y <= Y6 + 26; y++) set(x, y, z, b("air"));
				} else if (r <= RW) {   // 2단 아치 외벽
					set(x, Y6, z, b("polished_andesite"));
					double arcLen = Math.toRadians(((deg % 9) + 9) % 9) * r;       // 9도마다 아치
					double mid = Math.toRadians(4.5) * r, s = Math.abs(arcLen - mid);
					boolean outer = r > RW - 1;
					for (int dy = 1; dy <= 26; dy++) {
						int y = Y6 + dy;
						BlockState bs;
						boolean pil = s > mid - 0.9;                                 // 아치 사이 기둥
						if (dy == 10 || dy == 20) bs = b("chiseled_sandstone");     // 띠 장식
						else if (dy == 26) bs = (Math.floorMod((int) Math.round(deg / 4.5), 2) == 0 && outer) ? b("cut_sandstone") : b("air");   // 흉벽
						else if (dy == 25) bs = b("smooth_sandstone");
						else if (pil) bs = dy % 5 == 0 ? b("cut_sandstone") : b("sandstone");
						else {
							boolean open1 = dy <= 6 + (s < 1.0 ? 1 : 0) && outer;          // 1단: 막힌 아치(바깥 한 겹만 패임)
							boolean open2 = dy >= 12 && dy <= 16 + (s < 1.0 ? 1 : 0);      // 2단: 뚫린 회랑
							boolean win = dy >= 22 && dy <= 23 && s < 0.6;                 // 위 작은 창
							bs = open1 || open2 || win ? b("air") : b("smooth_sandstone");
						}
						if (gate && dy <= 7) bs = dy == 7 ? b("chiseled_quartz_block") : b("air");
						set(x, y, z, bs);
					}
					if (gate && r > RW - 1) set(x, Y6 + 6, z, b("iron_bars"));   // 내려오다 만 철창
					// 바깥벽 깃발
					if (outer && Math.floorMod((int) Math.round(deg), 36) < 2 && s < 0.6) {
						String f = outward(x + 0.5, z + 0.5);
						int bx = x + (f.equals("east") ? 1 : f.equals("west") ? -1 : 0), bz = z + (f.equals("south") ? 1 : f.equals("north") ? -1 : 0);
						set(bx, Y6 + 19, bz, b("red_wall_banner", "facing", f));
					}
				} else {   // 바깥 둘레 길
					set(x, Y6, z, hh % 4 == 0 ? b("andesite") : b("polished_andesite"));
					if (r > RI - 0.8 && hh % 3 != 0) set(x, Y6 + 1, z, b("andesite_wall"));
				}
			}
			// 성벽 위 영웅 석상 8개 + 화로
			for (int k = 0; k < 16; k++) {
				double a = Math.toRadians(k * 22.5 + 11.25);
				int x = (int) Math.round(Math.cos(a) * 34.5), z = (int) Math.round(Math.sin(a) * 34.5);
				if (!in(x, z)) continue;
				int y = Y6 + 26;
				if (k % 2 == 0) {
					set(x, y, z, b("gold_block"));
					set(x, y + 1, z, b("polished_deepslate_wall"));
					set(x, y + 2, z, b("iron_block"));
					set(x, y + 3, z, b("chiseled_quartz_block"));
					String in = inward(x, z);
					int sx = in.equals("east") || in.equals("west") ? 0 : 1, sz = sx == 0 ? 1 : 0;
					set(x + sx, y + 2, z + sz, b("lightning_rod"));
					set(x + sx, y + 3, z + sz, b("lightning_rod"));
					set(x - sx, y + 2, z - sz, b("iron_trapdoor", "facing", in, "open", "true"));
				} else {
					set(x, y, z, b("chiseled_stone_bricks"));
					set(x, y + 1, z, b("campfire"));
				}
			}
		}

		// ================================================================
		// 7층 · 천상의 정상
		// ================================================================
		private static final double RT = 34;

		private void summit() {
			for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
				double r = Math.hypot(x + 0.5, z + 0.5);
				int hh = h(x, 7, z);
				double cn = noise(x + 50, z - 30, 4);
				// 구름 테두리
				if (r > RT - 4 && r <= RT + 5 && cn > 0.42) {
					int top = Y7 - 1 + (r > RT ? (int) (cn * 3) : 0), bot = Y7 - 2 - (int) (cn * 4);
					for (int y = bot; y <= top; y++) setIfAir(x, y, z, b("white_wool"));
				}
				if (r > RT + 0.5) continue;
				int depth = 2 + (int) ((RT - r) * 0.38) + (int) (noise(x, z, 6) * 2);
				for (int y = Y7 - depth; y < Y7; y++) set(x, y, z, y < Y7 - depth + 2 ? b("white_wool") : b(hh % 4 == 0 ? "diorite" : "calcite"));
				double th = Math.atan2(z + 0.5, x + 0.5), deg = Math.toDegrees(th);
				boolean path = (Math.abs(x + 0.5) <= 2 || Math.abs(z + 0.5) <= 2) && r > 15;
				// 바닥
				BlockState fl;
				if (r <= 3.6) fl = b("gold_block");
				else if (r <= 15) fl = (r > 14.3) ? b("gold_block") : ((int) r % 3 == 0 ? b("chiseled_quartz_block") : b("smooth_quartz"));
				else if (path) fl = Math.min(Math.abs(x + 0.5), Math.abs(z + 0.5)) > 1.5 ? b("gold_block") : b("quartz_bricks");
				else fl = b("grass_block");
				set(x, Y7, z, fl);
				for (int y = Y7 + 1; y <= Y7 + 34; y++) set(x, y, z, b("air"));
				if (!path && r > 15.5 && r < RT - 1.5 && fl.is(Blocks.GRASS_BLOCK)) {
					int f = hh % 17;
					String fid = f == 0 ? "lily_of_the_valley" : f == 1 ? "oxeye_daisy" : f == 2 ? "azure_bluet" : f == 3 ? "white_tulip" : f == 4 ? "short_grass" : null;
					if (fid != null) set(x, Y7 + 1, z, b(fid));
				}
				// 가장자리 난간
				if (r > RT - 1.5 && !path) set(x, Y7 + 1, z, hh % 7 == 0 ? b("gold_block") : b("diorite_wall"));
				// 승강기 둘레 끝 막대
				if (r > 3.6 && r <= 4.6 && Math.floorMod((int) Math.round(deg), 45) < 6) set(x, Y7 + 1, z, b("end_rod"));
				// 기둥 회랑 (16개)
				double pa = Math.toRadians(Math.floorMod((int) Math.round(deg / 22.5), 16) * 22.5);
				double pdx = x + 0.5 - Math.cos(pa) * 12, pdz = z + 0.5 - Math.sin(pa) * 12;
				if (Math.hypot(pdx, pdz) <= 1.0) {
					set(x, Y7 + 1, z, b("chiseled_quartz_block"));
					for (int y = Y7 + 2; y <= Y7 + 10; y++) set(x, y, z, b("quartz_pillar"));
					set(x, Y7 + 11, z, b("gold_block"));
				}
				// 처마 고리
				if (r > 10.6 && r <= 13.6) { set(x, Y7 + 12, z, b("smooth_quartz")); if (r > 12.6) set(x, Y7 + 13, z, b("gold_block")); }
				// 돔 (유리 + 금 갈비)
				if (r <= 13.6) {
					for (int y = Y7 + 13; y <= Y7 + 26; y++) {
						double d = Math.sqrt(r * r + (y - (Y7 + 12)) * (double) (y - (Y7 + 12)));
						if (Math.abs(d - 13) < 0.62) {
							boolean rib = Math.floorMod((int) Math.round(deg), 30) < 5 || r < 1.6;
							set(x, y, z, rib ? b("gold_block") : b("white_stained_glass"));
						}
					}
					if (r < 0.8) { for (int y = Y7 + 26; y <= Y7 + 29; y++) set(x, y, z, b("end_rod")); for (int y = Y7 + 16; y <= Y7 + 24; y++) set(x, y, z, b("chain")); set(x, Y7 + 15, z, b("lantern", "hanging", "true")); }
				}
				// 정원 벚나무 6그루
				for (int t = 0; t < 6; t++) {
					double ta = Math.toRadians(t * 60 + 30);
					int tx = (int) Math.round(Math.cos(ta) * 24), tz = (int) Math.round(Math.sin(ta) * 24);
					int dx = x - tx, dz = z - tz;
					if (dx == 0 && dz == 0) for (int y = Y7 + 1; y <= Y7 + 5; y++) set(x, y, z, b("birch_log"));
					for (int y = Y7 + 4; y <= Y7 + 8; y++) {
						double ld = Math.sqrt(dx * dx + dz * dz + (y - (Y7 + 6.5)) * (y - (Y7 + 6.5)) * 1.6);
						if (ld < 3.2 && !(dx == 0 && dz == 0 && y <= Y7 + 5) && h(x, y, z) % 9 != 0) set(x, y, z, b("cherry_leaves", "persistent", "true"));
					}
				}
				// 천사 석상 4개 (대각선)
				for (int s = 0; s < 4; s++) {
					double sa = Math.toRadians(45 + s * 90);
					int sx = (int) Math.round(Math.cos(sa) * 19), sz = (int) Math.round(Math.sin(sa) * 19);
					if (x == sx && z == sz) {
						set(x, Y7 + 1, z, b("gold_block"));
						for (int y = Y7 + 2; y <= Y7 + 4; y++) set(x, y, z, b("quartz_pillar"));
						set(x, Y7 + 5, z, b("chiseled_quartz_block"));
						set(x, Y7 + 6, z, b("end_rod"));
					}
					// 날개: 몸 뒤(바깥쪽) 좌우로 펼침
					double ox = Math.cos(sa), oz = Math.sin(sa);
					double lx = -oz, lz = ox;
					double rx = x + 0.5 - (sx + 0.5), rz = z + 0.5 - (sz + 0.5);
					double back = rx * ox + rz * oz, side = rx * lx + rz * lz;
					if (back > 0.5 && back < 1.6 && Math.abs(side) >= 0.8 && Math.abs(side) <= 3.2) {
						int wy0 = Y7 + 3, wy1 = Y7 + 5 + (Math.abs(side) < 2 ? 1 : 0);
						for (int y = wy0; y <= wy1; y++) set(x, y, z, b("white_wool"));
					}
				}
			}
			heavenGate();
		}

		/** 북쪽 끝의 천상의 성문 (탑 둘 + 아치 + 열린 금문) */
		private void heavenGate() {
			int zf = -30, zb = -25;   // 성벽 두께 z -30..-25
			for (int x = minX; x <= maxX; x++) for (int z = minZ; z <= maxZ; z++) {
				if (x < -13 || x > 12 || z < -32 || z > -19) continue;
				int ax = Math.abs(x * 2 + 1);   // 가운데 대칭 거리 *2
				boolean tower = ax >= 13 && ax <= 25 && z >= -31 && z <= -24;
				boolean wall = ax < 13 && z >= zf && z <= zb;
				for (int dy = 1; dy <= 30; dy++) {
					int y = Y7 + dy;
					BlockState s = null;
					if (tower) {
						boolean edge = ax == 13 || ax == 25 || z == -31 || z == -24;
						if (dy <= 22) s = edge ? (dy % 6 == 0 ? b("gold_block") : ((dy % 8 == 4 || dy % 8 == 5) && (z == -24) && ax == 19 ? b("sea_lantern") : b("quartz_bricks"))) : b("smooth_quartz");
						else if (dy == 23) s = edge && ((x + z) % 2 == 0) ? b("smooth_quartz") : (edge ? b("air") : b("gold_block"));
						else {   // 금 지붕
							int k = dy - 23;
							double cx = (ax <= 19 ? 1 : 1) * 19 / 2.0, dd = Math.max(Math.abs(ax / 2.0 - cx), Math.abs(z + 27.5));
							if (dd <= 3.5 - k * 0.7) s = b("gold_block");
							else if (k == 6 && dd < 0.8) s = b("end_rod");
						}
					} else if (wall) {
						double xo = ax / 2.0;
						boolean open = (xo <= 4.5 && dy <= 10) || (dy > 10 && xo * xo + (dy - 10) * (dy - 10) <= 4.5 * 4.5);
						boolean trim = !open && ((xo <= 5.6 && dy <= 10 && xo > 4.5) || (dy > 10 && xo * xo + (dy - 10) * (dy - 10) <= 5.6 * 5.6)) && (z == zf || z == zb);
						if (dy <= 18) s = open ? b("air") : trim ? b("gold_block") : dy % 6 == 0 ? b("gold_block") : b("quartz_bricks");
						else if (dy == 19) s = (x % 2 == 0) ? b("smooth_quartz") : b("air");
						if (open && dy == 1) s = null;
					}
					if (s != null) set(x, y, z, s);
				}
				// 문턱의 빛 + 활짝 열린 금문 (안쪽으로 젖혀짐)
				if (ax <= 9 && z >= zf && z <= zb) set(x, Y7, z, b("pearlescent_froglight"));
				if ((ax == 9 || ax == 7) && z > zb && z <= zb + 4) {
					for (int dy = 1; dy <= 12; dy++) set(x, Y7 + dy, z, (dy % 4 == 0 || z == zb + 4) ? b("gold_block") : b("smooth_quartz"));
				}
			}
		}
	}
}
