package com.elementalweapons.client.growth;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.fx.FxOptions;
import com.elementalweapons.growth.Tutorial;
import com.elementalweapons.world.sky.Arenas;
import com.elementalweapons.world.sky.Capital;
import com.elementalweapons.world.sky.SkyLayout;

/** 튜토리얼 목표 쪽으로 바닥에 흐르는 빛 화살표 (»») */
public final class TutorialArrows {
	private TutorialArrows() {}

	public record Target(Vec3 pos, String name) {}

	/** 지금 단계에서 가야 할 곳 (없으면 null) */
	public static Target target(Minecraft mc) {
		var p = mc.player;
		if (p == null) return null;
		int step = Tutorial.get(p).step();
		double r = Math.hypot(p.getX(), p.getZ());
		boolean inCitadel = r < 160 && p.getY() > Capital.FY[0] - 30;
		switch (step) {
			case 3, 4, 6 -> {
				if (inCitadel && !p.isPassenger()) {
					if (p.getY() > Capital.FY[0] + 20) return new Target(new Vec3(0.5, p.getY(), 0.5), "가운데 탑 승강기 (아래를 보면 내려가요)");
					return new Target(new Vec3(Capital.ROOST_X + 0.5, Capital.FY[0] + 1, Capital.ROOST_Z + 0.5), "용의 둥지");
				}
				if (SkyLayout.regionAt(p.getX(), p.getZ()) >= 0 && step != 3) return null;   // 이미 원소 지역
				double[] best = null; double bd = Double.MAX_VALUE;
				for (double[] c : SkyLayout.REGION_CENTER) { double d = Math.hypot(c[0] - p.getX(), c[1] - p.getZ()); if (d < bd) { bd = d; best = c; } }
				return best == null ? null : new Target(new Vec3(best[0], p.getY(), best[1]), "가장 가까운 원소 지역");
			}
			case 9 -> {
				Arenas.Arena best = null; double bd = Double.MAX_VALUE;
				for (Arenas.Arena a : Arenas.all()) { double d = p.distanceToSqr(a.x(), p.getY(), a.z()); if (d < bd) { bd = d; best = a; } }
				return best == null ? null : new Target(new Vec3(best.x() + 0.5, p.getY(), best.z() + 0.5), "가장 가까운 보스방");
			}
			default -> { return null; }
		}
	}

	private static int tick;

	public static void tick(Minecraft mc) {
		if (mc.player == null || mc.level == null || mc.isPaused()) return;
		if (++tick % 5 != 0) return;
		Target t = target(mc);
		if (t == null) return;
		var p = mc.player;
		Vec3 to = t.pos().subtract(p.position());
		Vec3 dir = new Vec3(to.x, 0, to.z);
		double dist = dir.length();
		if (dist < 3) return;
		dir = dir.normalize();
		Vec3 side = new Vec3(-dir.z, 0, dir.x);
		double phase = (tick % 20) / 20.0 * 2.2;
		boolean air = p.isPassenger() || p.getAbilities().flying;
		FxOptions dot = FxOptions.of(FxOptions.ORB, 0xFFE070, 0.22F, 10);
		for (int k = 0; k < 5; k++) {
			double d = 2.0 + k * 2.2 + phase;
			if (d > dist - 1) break;
			Vec3 c = p.position().add(dir.scale(d));
			double y = air ? p.getY() - 0.6 : ground(mc, c);
			if (Double.isNaN(y)) continue;
			// > 모양 (꼭짓점이 목표 쪽)
			for (int i = -5; i <= 5; i++) {
				double back = Math.abs(i) * 0.3, off = i * 0.24;
				Vec3 q = c.add(dir.scale(-back)).add(side.scale(off));
				mc.level.addParticle(dot, q.x, y + 0.08, q.z, 0, 0, 0);
			}
		}
	}

	/** 그 지점의 바닥 높이 (플레이어 근처 위아래 5칸) */
	private static double ground(Minecraft mc, Vec3 c) {
		int x = (int) Math.floor(c.x), z = (int) Math.floor(c.z), y0 = (int) Math.floor(mc.player.getY());
		for (int y = y0 + 2; y >= y0 - 5; y--) {
			BlockPos b = new BlockPos(x, y, z);
			if (!mc.level.getBlockState(b).getCollisionShape(mc.level, b).isEmpty() && mc.level.getBlockState(b.above()).getCollisionShape(mc.level, b.above()).isEmpty())
				return y + 1;
		}
		return Double.NaN;
	}
}
