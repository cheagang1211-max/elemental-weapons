package com.elementalweapons.world.sky;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;

/**
 * 공중 세계 전체 배치 (고정 설계 지도).
 * 수도(중앙) + 7개 원소 지역 군도(반지름 2300 원형) + 원소의 심연(바깥) + 중간 기착 섬 + 장식용 부유섬.
 */
public final class SkyLayout {
	private SkyLayout() {}

	// ---------------- 데이터 ----------------
	public static final class Island {
		public final double x, z, r, top, depth, amp, peak;
		public final int seed, kind;   // kind: 0 수도 1층, 1 지역 본섬, 2 위성섬, 3 기착섬, 4 장식섬, 5 심연 본섬, 6 천공성 위층
		Island(double x, double z, double r, double top, double depth, double amp, double peak, int seed, int kind) {
			this.x = x; this.z = z; this.r = r; this.top = top; this.depth = depth; this.amp = amp; this.peak = peak; this.seed = seed; this.kind = kind;
		}
	}

	public static final class Bridge {
		public final double ax, az, bx, bz; public double ay, by;
		public final double width, arch; public final int style;  // style 0 대교, 1 지역 연결, 2 위성 다리
		Bridge(double ax, double az, double bx, double bz, double width, double arch, int style) {
			this.ax = ax; this.az = az; this.bx = bx; this.bz = bz; this.width = width; this.arch = arch; this.style = style;
		}
		public double length() { return Math.hypot(bx - ax, bz - az); }
	}

	/** 지역 순서 (바이옴 소스의 regions 목록 순서와 같음) */
	public static final String[] REGION_NAMES = {"grass", "water", "electric", "wind", "fire", "light", "dark"};
	public static final double[] REGION_TOP = {166, 176, 186, 201, 161, 191, 171};
	public static final double RING = 950, MAIN_R = 340, CAPITAL_R = 118, CAPITAL_TOP = 150;
	public static final double ABYSS_DIST = 1700, ABYSS_R = 440, ABYSS_TOP = 151;

	public static final List<Island> ISLANDS = new ArrayList<>();
	public static final List<Bridge> BRIDGES = new ArrayList<>();
	public static final double[][] REGION_CENTER = new double[7][2];
	public static double ABYSS_X, ABYSS_Z;
	public static final BlockPos SPAWN = new BlockPos(0, 151, 22);

	private static final Map<Long, int[]> GRID = new HashMap<>();
	private static final int CELL = 256;


	public static double regionAngle(int k) { return Math.toRadians(-90 + k * 360.0 / 7); }

	private static void build() {
		// 천공성: 1층(두꺼운 바위 섬) 위로 점점 작아지는 원반 층 4개
		Island cap = add(0, 0, Capital.FR[0], Capital.FY[0] + 1, 110, 0, 0, 1, 0);   // 땅 맨 윗블록 = FY
		for (int f = 1; f < Capital.FY.length; f++) add(0, 0, Capital.FR[f], Capital.FY[f] + 1, 12, 0, 0, 10 + f, 6);
		for (int k = 0; k < 7; k++) {
			double a = regionAngle(k);
			REGION_CENTER[k][0] = Math.cos(a) * RING; REGION_CENTER[k][1] = Math.sin(a) * RING;
		}
		double aa = (regionAngle(5) + regionAngle(6)) / 2;
		ABYSS_X = Math.cos(aa) * ABYSS_DIST; ABYSS_Z = Math.sin(aa) * ABYSS_DIST;

		Island[] mains = new Island[7];
		for (int k = 0; k < 7; k++) mains[k] = add(REGION_CENTER[k][0], REGION_CENTER[k][1], MAIN_R, REGION_TOP[k], 115, 26, 30, 100 + k, 1);
		Island abyss = add(ABYSS_X, ABYSS_Z, ABYSS_R, ABYSS_TOP, 125, 22, 40, 200, 5);

		// 위성섬 (지역마다 9개, 심연 7개): 본섬 바깥쪽 부채꼴에, 다른 섬·대교 길과 겹치지 않게
		List<double[]> lanes = new ArrayList<>();
		for (int k = 0; k < 7; k++) {
			lanes.add(new double[]{0, 0, REGION_CENTER[k][0], REGION_CENTER[k][1]});
			int n2 = (k + 1) % 7;
			lanes.add(new double[]{REGION_CENTER[k][0], REGION_CENTER[k][1], REGION_CENTER[n2][0], REGION_CENTER[n2][1]});
		}
		lanes.add(new double[]{REGION_CENTER[5][0], REGION_CENTER[5][1], ABYSS_X, ABYSS_Z});
		lanes.add(new double[]{REGION_CENTER[6][0], REGION_CENTER[6][1], ABYSS_X, ABYSS_Z});
		for (int k = 0; k < 8; k++) {
			Island m = k < 7 ? mains[k] : abyss;
			int n = k < 7 ? 9 : 7;
			double out = Math.atan2(m.z, m.x);
			double spread = k < 7 ? Math.toRadians(150) : Math.toRadians(260);
			for (int i = 0; i < n; i++) {
				int s = 1000 + k * 37 + i;
				double ang = out - spread / 2 + spread * (i + 0.5 + 0.3 * (hash(s, 1, 2) - 0.5)) / n;
				double rr = 85 + 85 * hash(s, 5, 6);
				double dist = m.r + rr + 55 + 90 * hash(s, 3, 4);
				double x = m.x + Math.cos(ang) * dist, z = m.z + Math.sin(ang) * dist;
				boolean clash = Math.hypot(x, z) < CAPITAL_R + rr + 80;
				for (Island o : ISLANDS) if (!clash && o != m && Math.hypot(o.x - x, o.z - z) < (o.r + rr) * 1.15 + 40) clash = true;
				for (double[] l : lanes) if (!clash && segDist(x, z, l[0], l[1], l[2], l[3]) < rr + 45) clash = true;
				if (clash) continue;
				Island sat = add(x, z, rr, m.top - 22 + 44 * hash(s, 7, 8), Math.min(80, rr * 0.5), 14, 12, s, 2);
				bridgeBetween(m, sat, 5.0, 4, 2);
			}
		}
		// 수도 → 각 지역 대교 (중간 기착섬 3개)
		for (int k = 0; k < 7; k++) chain(cap, mains[k], new double[]{0.3, 0.7}, 11.0, 0, 2000 + k);
		// 이웃 지역끼리 (기착섬 2개)
		for (int k = 0; k < 7; k++) chain(mains[k], mains[(k + 1) % 7], new double[]{0.5}, 9.0, 1, 3000 + k);
		// 빛·어둠 → 심연
		chain(mains[5], abyss, new double[]{0.5}, 11.0, 0, 4000);
		chain(mains[6], abyss, new double[]{0.5}, 11.0, 0, 4001);

		// 장식용 부유섬
		int big = ISLANDS.size();
		for (int cx = -31; cx <= 31; cx++) for (int cz = -31; cz <= 31; cz++) {
			int s = 50000 + cx * 131 + cz * 7919;
			if (hash(s, 9, 1) > 0.42) continue;
			double x = (cx + hash(s, 2, 3)) * 110, z = (cz + hash(s, 4, 5)) * 110;
			if (Math.hypot(x, z) > 2500 || Math.hypot(x, z) < 200) continue;
			double r = 6 + 22 * Math.pow(hash(s, 6, 7), 2);
			boolean clash = false;
			for (int i = 0; i < big && !clash; i++) { Island o = ISLANDS.get(i); if (Math.hypot(o.x - x, o.z - z) < o.r * 1.25 + r + 25) clash = true; }
			for (Bridge b : BRIDGES) { if (clash) break; if (segDist(x, z, b) < r + b.width + 18) clash = true; }
			if (clash) continue;
			add(x, z, r, 220 + 35 * hash(s, 8, 9), r * 0.9, 3, 0, s, 4); // 장식 섬은 하늘 높이에만 (밑으로 떨어지는 공허에는 없음)
		}
		// 다리 높이 (지형이 정해진 뒤 계산)
		for (Bridge b : BRIDGES) {
			if (Double.isNaN(b.ay)) b.ay = surfaceOr(b.ax, b.az, CAPITAL_TOP - 1);
			if (Double.isNaN(b.by)) b.by = surfaceOr(b.bx, b.bz, CAPITAL_TOP - 1);
		}
	}

	private static double surfaceOr(double x, double z, double def) {
		int s = surfaceTop((int) Math.floor(x), (int) Math.floor(z));
		return s == Integer.MIN_VALUE ? def : s;
	}

	private static Island add(double x, double z, double r, double top, double depth, double amp, double peak, int seed, int kind) {
		Island is = new Island(x, z, r, top, depth, amp, peak, seed, kind);
		int idx = ISLANDS.size();
		ISLANDS.add(is);
		double rr = r * 1.2 + 4;
		for (int gx = (int) Math.floor((x - rr) / CELL); gx <= (int) Math.floor((x + rr) / CELL); gx++)
			for (int gz = (int) Math.floor((z - rr) / CELL); gz <= (int) Math.floor((z + rr) / CELL); gz++) {
				long key = ((long) gx << 32) ^ (gz & 0xffffffffL);
				int[] old = GRID.get(key);
				int[] nw = old == null ? new int[1] : java.util.Arrays.copyOf(old, old.length + 1);
				nw[nw.length - 1] = idx;
				GRID.put(key, nw);
			}
		return is;
	}

	private static void bridgeBetween(Island a, Island b, double width, double arch, int style) {
		double dx = b.x - a.x, dz = b.z - a.z, L = Math.hypot(dx, dz);
		dx /= L; dz /= L;
		double sa = a.kind == 3 ? 0 : a.r * 0.78, sb = b.kind == 3 ? 0 : b.r * 0.72;
		if (a.kind == 0) sa = a.r * 0.9;
		Bridge br = new Bridge(a.x + dx * sa, a.z + dz * sa, b.x - dx * sb, b.z - dz * sb, width, arch, style);
		br.ay = Double.NaN; br.by = Double.NaN;
		BRIDGES.add(br);
	}

	/** a → (기착섬들) → b */
	private static void chain(Island a, Island b, double[] ts, double width, int style, int seed) {
		Island prev = a;
		double dx = b.x - a.x, dz = b.z - a.z, L = Math.hypot(dx, dz);
		for (int i = 0; i < ts.length; i++) {
			double t = (a.r + ts[i] * (L - a.r - b.r)) / L;
			double top = a.top + (b.top - a.top) * t + 6 * Math.sin(t * Math.PI);
			Island w = add(a.x + dx * t, a.z + dz * t, 42, top, 34, 0, 0, seed * 10 + i, 3);
			bridgeBetween(prev, w, width, Math.min(14, Math.hypot(dx, dz) * (ts[0]) / 40), style);
			prev = w;
		}
		bridgeBetween(prev, b, width, Math.min(14, Math.hypot(dx, dz) * (ts[0]) / 40), style);
	}

	public static double segDist(double x, double z, double ax, double az, double bx, double bz) {
		double vx = bx - ax, vz = bz - az, L2 = vx * vx + vz * vz;
		double t = L2 == 0 ? 0 : Math.max(0, Math.min(1, ((x - ax) * vx + (z - az) * vz) / L2));
		return Math.hypot(x - (ax + vx * t), z - (az + vz * t));
	}

	public static double segDist(double x, double z, Bridge b) {
		double vx = b.bx - b.ax, vz = b.bz - b.az, L2 = vx * vx + vz * vz;
		double t = Math.max(0, Math.min(1, ((x - b.ax) * vx + (z - b.az) * vz) / L2));
		return Math.hypot(x - (b.ax + vx * t), z - (b.az + vz * t));
	}

	// ---------------- 노이즈 ----------------
	public static double hash(int a, int b, int c) {
		long h = a * 0x9E3779B97F4A7C15L ^ b * 0xC2B2AE3D27D4EB4FL ^ c * 0x165667B19E3779F9L;
		h ^= h >>> 33; h *= 0xff51afd7ed558ccdL; h ^= h >>> 33; h *= 0xc4ceb9fe1a85ec53L; h ^= h >>> 33;
		return (h >>> 11) * 0x1.0p-53;
	}

	private static double smooth(double t) { return t * t * (3 - 2 * t); }

	public static double vnoise(double x, double z, int s) {
		int xi = (int) Math.floor(x), zi = (int) Math.floor(z);
		double fx = smooth(x - xi), fz = smooth(z - zi);
		double a = hash(xi, zi, s), b = hash(xi + 1, zi, s), c = hash(xi, zi + 1, s), d = hash(xi + 1, zi + 1, s);
		return a + (b - a) * fx + (c - a) * fz + (a - b - c + d) * fx * fz;
	}

	public static double fbm(double x, double z, int s) {
		return (vnoise(x, z, s) + 0.5 * vnoise(x * 2.03, z * 2.03, s + 1) + 0.25 * vnoise(x * 4.1, z * 4.1, s + 2)) / 1.75;
	}

	// ---------------- 지형 ----------------
	private static final int CACHE = 4096;
	private static final ThreadLocal<long[]> CK = ThreadLocal.withInitial(() -> { long[] k = new long[CACHE]; java.util.Arrays.fill(k, Long.MIN_VALUE); return k; });
	private static final ThreadLocal<float[][]> CV = ThreadLocal.withInitial(() -> new float[CACHE][]);
	private static final float[] EMPTY = new float[0];

	/** 이 기둥의 [아래, 위] 구간들 (여러 섬이 겹칠 수 있음) */
	public static float[] column(int x, int z) {
		long key = ((long) x << 32) ^ (z & 0xffffffffL);
		int slot = (int) ((key * 0x9E3779B97F4A7C15L) >>> 52) & (CACHE - 1);
		long[] ks = CK.get(); float[][] vs = CV.get();
		if (ks[slot] == key) return vs[slot];
		float[] out = compute(x, z);
		ks[slot] = key; vs[slot] = out;
		return out;
	}

	private static float[] compute(int x, int z) {
		int[] list = GRID.get(((long) Math.floorDiv(x, CELL) << 32) ^ (Math.floorDiv(z, CELL) & 0xffffffffL));
		if (list == null) return EMPTY;
		float[] buf = null; int n = 0;
		for (int idx : list) {
			Island is = ISLANDS.get(idx);
			double dx = x - is.x, dz = z - is.z;
			double dist = Math.sqrt(dx * dx + dz * dz);
			if (dist > is.r * 1.2 + 2) continue;
			double wf = Math.max(8, is.r * 0.22);
			double reff = is.r * (is.kind == 0 || is.kind == 6 ? 0.98 + 0.04 * vnoise(x / wf, z / wf, is.seed) : 0.8 + 0.38 * vnoise(x / wf, z / wf, is.seed));
			double r = dist / reff;
			if (r >= 1) continue;
			double e = 1 - r;
			double top;
			if (is.kind == 0 || is.kind == 6) {
				top = is.top;   // 반듯한 층 바닥
			} else if (is.kind == 3) {
				top = is.top - (e < 0.15 ? (0.15 - e) * 25 : 0);
			} else {
				double rim = smooth(Math.min(1, e / 0.3));
				top = is.top + is.amp * (fbm(x / 200.0, z / 200.0, is.seed) - 0.45) * 2 * rim
						+ is.peak * Math.max(0, fbm(x / 70.0, z / 70.0, is.seed + 7) - 0.55) * 3.5 * rim
						- (e < 0.1 ? (0.1 - e) * 40 : 0);
			}
			double bottom = is.kind == 6
					? is.top - 4 - is.depth * Math.pow(e, 1.5) - 3 * vnoise(x / 9.0, z / 9.0, is.seed + 3)       // 얇은 원반 (아래층 머리 위 공간 확보)
					: is.top - 4 - is.depth * Math.pow(e, 0.55) * (0.6 + 0.55 * vnoise(x / 18.0, z / 18.0, is.seed + 3));
			if (top <= bottom) continue;
			if (buf == null) buf = new float[8];
			if (n + 2 > buf.length) buf = java.util.Arrays.copyOf(buf, buf.length * 2);
			buf[n++] = (float) bottom; buf[n++] = (float) top;
		}
		return buf == null ? EMPTY : java.util.Arrays.copyOf(buf, n);
	}

	public static float density(int x, int y, int z) {
		if (y < -40 || y > 300) return -1;
		float[] c = column(x, z);
		float best = -1;
		for (int i = 0; i < c.length; i += 2) {
			float v = Math.min(c[i + 1] - y, y - c[i]);
			if (v > best) best = v;
		}
		return Math.max(-1, Math.min(1, best / 4f));
	}

	/** 가장 높은 땅 블록 y (없으면 MIN_VALUE) */
	public static int surfaceTop(int x, int z) {
		float[] c = column(x, z);
		int best = Integer.MIN_VALUE;
		for (int i = 0; i < c.length; i += 2) best = Math.max(best, (int) Math.ceil(c[i + 1]) - 1);
		return best;
	}

	// ---------------- 지역 ----------------
	/** -1 수도, 0..6 지역, 7 심연 */
	public static int regionAt(double x, double z) {
		if (Math.hypot(x, z) < 400) return -1;
		double best = Math.hypot(x - ABYSS_X, z - ABYSS_Z) * 0.85; int bi = 7;
		for (int k = 0; k < 7; k++) {
			double d = Math.hypot(x - REGION_CENTER[k][0], z - REGION_CENTER[k][1]);
			if (d < best) { best = d; bi = k; }
		}
		return bi;
	}

	static {
		build();   // 모든 정적 필드가 준비된 뒤 실행
	}
}
