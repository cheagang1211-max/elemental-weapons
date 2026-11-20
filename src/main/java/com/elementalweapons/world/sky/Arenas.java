package com.elementalweapons.world.sky;

import java.util.ArrayList;
import java.util.List;

/** 공중 세계의 보스방 위치: 지역 본섬마다 그 속성 보스의 방 (가운데 + 바깥쪽/옆쪽), 심연 한가운데는 프리마 */
public final class Arenas {
	private Arenas() {}

	public record Arena(String boss, int x, int z, int floor, int region) {}

	public static final int R = 26;
	private static final String[][] BY_REGION = {
			{"silva", "hydra"},                         // 풀
			{"marina", "frost_giant"},                  // 물
			{"voltra", "storm_wyvern"},                 // 번개
			{"zephyros", "sphinx"},                     // 바람
			{"ignis", "lava_queen", "steam_colossus"},  // 불
			{"lumiel", "crystal_golem", "twilight_twins"}, // 빛
			{"nox"},                                    // 어둠
	};
	private static List<Arena> LIST;

	public static synchronized List<Arena> all() {
		if (LIST != null) return LIST;
		List<Arena> out = new ArrayList<>();
		for (int k = 0; k < 7; k++) {
			double[] rc = SkyLayout.REGION_CENTER[k];
			double a = SkyLayout.regionAngle(k);
			double ox = Math.cos(a), oz = Math.sin(a), px = -oz, pz = ox;
			String[] b = BY_REGION[k];
			for (int i = 0; i < b.length; i++) {
				double x = rc[0] + (i == 1 ? ox * 190 : i == 2 ? px * 190 : 0);
				double z = rc[1] + (i == 1 ? oz * 190 : i == 2 ? pz * 190 : 0);
				out.add(make(b[i], (int) Math.round(x), (int) Math.round(z), k));
			}
		}
		out.add(make("prima", (int) Math.round(SkyLayout.ABYSS_X), (int) Math.round(SkyLayout.ABYSS_Z), 7));
		LIST = out;
		return out;
	}

	private static Arena make(String boss, int x, int z, int region) {
		int f = SkyLayout.surfaceTop(x, z);
		if (f < 60) f = (int) (region < 7 ? SkyLayout.REGION_TOP[region] : SkyLayout.ABYSS_TOP);
		return new Arena(boss, x, z, f, region);
	}

	public static Arena near(double x, double z, double range) {
		for (Arena a : all()) if ((a.x - x) * (a.x - x) + (a.z - z) * (a.z - z) < range * range) return a;
		return null;
	}

	public static Arena of(String boss) {
		for (Arena a : all()) if (a.boss.equals(boss)) return a;
		return null;
	}
}
