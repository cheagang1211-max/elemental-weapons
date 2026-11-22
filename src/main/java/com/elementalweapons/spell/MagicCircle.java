package com.elementalweapons.spell;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.fx.FxOptions;
import com.elementalweapons.item.Element;

/**
 * 속성 마법진: 마법사 궁극기의 중심.
 *  처음 0.8초 동안 선이 그려지듯 나타나고, 천천히 돌다가 끝날 때 줄어들며 사라짐.
 *  속성마다 문양이 다름 — 전기 육망성+번개 갈래 / 불 삼각+불꽃 / 물 물결 고리 / 풀 꽃잎 / 바람 소용돌이 / 어둠 오망성 / 빛 팔각 별+빛살
 */
public class MagicCircle extends Spell {
	private static final int DRAW_IN = 16, EVERY = 3;
	private final Element el;
	private final Vec3 c;
	private final double r;
	private final int duration;
	private final List<double[]> segs = new ArrayList<>();   // {u1, v1, u2, v2} (반지름 1 기준)
	private final FxOptions main, glow, rune;

	/** silent: 시작 소리 없이 */
	public MagicCircle(ServerLevel level, Player caster, Element el, Vec3 center, double radius, int duration, boolean silent) {
		super(level, caster);
		this.el = el; this.c = center; this.r = radius; this.duration = duration;
		int c1 = ElementFx.color(el), c2 = ElementFx.color2(el);
		main = FxOptions.of(FxOptions.ORB, c1, 0.16F, EVERY + 2);
		glow = FxOptions.of(FxOptions.ORB, c2, 0.11F, EVERY + 2);
		rune = FxOptions.of(FxOptions.RUNE, c2, 0.5F, EVERY + 2).flat();
		build();
		if (!silent) Fx.sound(level, center, SoundEvents.EVOKER_PREPARE_SUMMON, 1.5F, 1.4F);
	}

	private void poly(int n, double rad, double rot, int step) {
		for (int i = 0; i < n; i++) {
			double a1 = rot + i * Math.PI * 2 / n, a2 = rot + (i + step) * Math.PI * 2 / n;
			segs.add(new double[]{Math.cos(a1) * rad, Math.sin(a1) * rad, Math.cos(a2) * rad, Math.sin(a2) * rad});
		}
	}

	private void curve(java.util.function.DoubleUnaryOperator rad, int samples, double turns) {
		double pu = Double.NaN, pv = 0;
		for (int i = 0; i <= samples; i++) {
			double t = i * Math.PI * 2 * turns / samples, rr = rad.applyAsDouble(t);
			double u = Math.cos(t) * rr, v = Math.sin(t) * rr;
			if (!Double.isNaN(pu)) segs.add(new double[]{pu, pv, u, v});
			pu = u; pv = v;
		}
	}

	private void build() {
		switch (el) {
			case ELECTRIC -> {   // 육망성 + 번개 갈래
				poly(3, 0.92, Math.PI / 2, 1); poly(3, 0.92, -Math.PI / 2, 1);
				for (int i = 0; i < 6; i++) {
					double a = i * Math.PI / 3 + Math.PI / 6;
					double[] p = {0.35, 0.5, 0.62, 0.8};
					for (int k = 0; k < 3; k++) {
						double o1 = (k % 2 == 0 ? 0.08 : -0.08), o2 = -o1;
						segs.add(new double[]{Math.cos(a + o1) * p[k], Math.sin(a + o1) * p[k], Math.cos(a + o2) * p[k + 1], Math.sin(a + o2) * p[k + 1]});
					}
				}
			}
			case FIRE -> {   // 삼각형 두 겹 + 불꽃 갈래
				poly(3, 0.92, Math.PI / 2, 1); poly(3, 0.55, -Math.PI / 2, 1);
				for (int i = 0; i < 9; i++) {
					double a = i * Math.PI * 2 / 9;
					segs.add(new double[]{Math.cos(a) * 0.6, Math.sin(a) * 0.6, Math.cos(a + 0.12) * 0.82, Math.sin(a + 0.12) * 0.82});
				}
			}
			case WATER -> {   // 물결 고리 세 겹
				curve(t -> 0.78 + 0.05 * Math.sin(t * 8), 96, 1);
				curve(t -> 0.55 + 0.05 * Math.sin(t * 6 + 1), 72, 1);
				curve(t -> 0.3, 40, 1);
			}
			case GRASS -> {   // 여섯 장 꽃잎 + 안쪽 원
				curve(t -> 0.9 * Math.abs(Math.cos(3 * t)), 144, 1);
				curve(t -> 0.25, 32, 1);
			}
			case WIND -> {   // 세 갈래 소용돌이
				for (int k = 0; k < 3; k++) {
					double off = k * Math.PI * 2 / 3;
					double pu = Double.NaN, pv = 0;
					for (int i = 0; i <= 40; i++) {
						double t = i / 40.0, a = off + t * Math.PI * 1.6, rr = 0.12 + 0.8 * t;
						double u = Math.cos(a) * rr, v = Math.sin(a) * rr;
						if (!Double.isNaN(pu)) segs.add(new double[]{pu, pv, u, v});
						pu = u; pv = v;
					}
				}
			}
			case DARK -> {   // 오망성 + 안쪽 오각형
				poly(5, 0.92, Math.PI / 2, 2);
				poly(5, 0.36, -Math.PI / 2, 1);
			}
			case LIGHT -> {   // 팔각 별(두 사각형) + 빛살
				poly(4, 0.92, 0, 1); poly(4, 0.92, Math.PI / 4, 1);
				for (int i = 0; i < 16; i++) {
					double a = i * Math.PI / 8;
					segs.add(new double[]{Math.cos(a) * 0.25, Math.sin(a) * 0.25, Math.cos(a) * (i % 2 == 0 ? 0.6 : 0.45), Math.sin(a) * (i % 2 == 0 ? 0.6 : 0.45)});
				}
			}
		}
	}

	private Vec3 at(double u, double v, double rot, double scale) {
		double cs = Math.cos(rot), sn = Math.sin(rot);
		double x = (u * cs - v * sn) * r * scale, z = (u * sn + v * cs) * r * scale;
		return c.add(x, 0, z);
	}

	@Override
	protected boolean update() {
		if (age >= duration) return true;
		if (age % EVERY != 0) return false;
		double draw = Math.min(1.0, (age + 1.0) / DRAW_IN);                       // 그려지는 정도
		double scale = age > duration - 10 ? Math.max(0.05, (duration - age) / 10.0) : 1.0;   // 끝날 때 줄어듦
		double rot = age * 0.012;
		// 바깥 고리 두 겹 (그려지는 동안 시계 방향으로 이어짐)
		double step = 0.55 / (r * scale);
		for (double a = 0; a < Math.PI * 2 * draw; a += step) {
			Fx.at(level, main, at(Math.cos(a), Math.sin(a), rot, scale));
			if (((int) (a / step)) % 2 == 0) Fx.at(level, glow, at(Math.cos(a) * 0.94, Math.sin(a) * 0.94, -rot * 2, scale));
		}
		// 문양 선
		int n = (int) Math.ceil(segs.size() * draw);
		for (int i = 0; i < n; i++) {
			double[] sgm = segs.get(i);
			Vec3 a = at(sgm[0], sgm[1], -rot, scale), b = at(sgm[2], sgm[3], -rot, scale);
			double len = a.distanceTo(b);
			for (double d = 0; d <= len; d += 0.5) Fx.at(level, i % 3 == 0 ? glow : main, a.lerp(b, len < 1e-6 ? 0 : d / len));
		}
		// 고리 위를 도는 룬 문자
		if (draw >= 1) for (int k = 0; k < 12; k++) {
			double a = k * Math.PI / 6;
			Fx.at(level, rune, at(Math.cos(a) * 1.08, Math.sin(a) * 1.08, rot * 3, scale));
		}
		// 마법진에서 피어오르는 속성 입자
		if (age % 6 == 0) for (int k = 0; k < 6; k++) {
			double a = level.getRandom().nextDouble() * Math.PI * 2, rr = level.getRandom().nextDouble() * r * scale;
			Fx.at(level, ElementFx.particle(el), c.add(Math.cos(a) * rr, 0.2, Math.sin(a) * rr));
		}
		return false;
	}
}
