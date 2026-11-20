package com.elementalweapons.spell;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * ✨ 빛 지팡이: 하늘에 황금빛 천국의 문이 열리고, 문 안에서 날개 달린 대천사가 내려와
 * 빛의 창을 던지며 함께 싸웁니다. 시간이 지나면 대천사는 문 안으로 돌아가고 문이 닫힙니다.
 *
 * 진행 (틱): 0~30 문 열림 → 30~55 대천사 강림 → 55~185 공격 → 185~210 귀환 → 216 종료
 */
public class HeavenGateSpell extends Spell {
	private static final com.elementalweapons.fx.FxOptions GOLD = Fx.dust(0xFFC830, 2.0F);
	private static final com.elementalweapons.fx.FxOptions PALE_GOLD = Fx.dust(0xFFF4C0, 1.3F);
	private static final com.elementalweapons.fx.FxOptions RAY = Fx.dust(0xFFE89A, 1.0F);
	private static final com.elementalweapons.fx.FxOptions WHITE = Fx.dust(0xFFFFFF, 1.2F);
	private static final com.elementalweapons.fx.FxOptions SKIN = Fx.dust(0xFFF1E0, 1.2F);
	private static final com.elementalweapons.fx.FxOptions HALO = Fx.dust(0xFFD84A, 1.0F);
	private static final com.elementalweapons.fx.FxOptions FEATHER_TIP = Fx.dust(0xFFE38A, 1.0F);
	private static final com.elementalweapons.fx.FxOptions SPEAR = Fx.dust(0xFFE070, 1.6F);

	private static final double GATE_HALF_WIDTH = 3.2;
	private static final double ANGEL_SCALE = 1.4;
	private static final double ATTACK_RANGE = 26;

	/** 문이 하늘에서 내려오는 시간 (틱) */
	public static final int DESCEND_TICKS = 35;
	/** 문이 처음 나타나는 높이 (최종 위치보다 몇 블록 위) */
	public static final double DESCEND_HEIGHT = 25;
	/** 대천사가 나타나는 시점 / 공격 시작 / 귀환 시작 */
	public static final int ANGEL_APPEAR = 40;
	public static final int ANGEL_READY = 65;
	public static final int ANGEL_RETURN = 185;

	private final Vec3 fwd;    // 시전자 → 문 방향 (수평)
	private final Vec3 right;  // 문의 가로 방향
	private final Vec3 gate;   // 문 최종 위치 (중심)
	private Vec3 gateNow;      // 지금 문 위치 (내려오는 중)
	private Vec3 angel;        // 대천사 발 위치

	public HeavenGateSpell(ServerLevel level, Player caster) {
		super(level, caster);
		this.fwd = Fx.flatLook(caster);
		this.right = new Vec3(-fwd.z, 0, fwd.x);
		this.gate = gateCenter(caster);
		this.gateNow = gate.add(0, DESCEND_HEIGHT, 0);
		this.angel = gate;
	}

	/** 문 최종 위치: 시전자 앞 9블록, 위 11블록. (클라이언트 카메라도 같은 계산을 씀) */
	public static Vec3 gateCenter(Player caster) {
		return caster.position().add(Fx.flatLook(caster).scale(9)).add(0, 11, 0);
	}

	/** 시전 후 age 틱일 때 문이 최종 위치보다 얼마나 위에 있는지 (부드럽게 감속하며 내려옴) */
	public static double descentOffset(int age) {
		if (age >= DESCEND_TICKS) return 0;
		double t = (double) age / DESCEND_TICKS;
		double remain = 1 - t;
		return DESCEND_HEIGHT * remain * remain * remain;
	}

	/** 문 평면 좌표 → 월드 좌표 (a: 가로, b: 세로, c: 앞뒤 깊이) */
	private Vec3 plane(Vec3 origin, double a, double b, double c) {
		return origin.add(right.scale(a)).add(0, b, 0).add(fwd.scale(c));
	}

	@Override
	protected boolean update() {
		RandomSource r = level.getRandom();
		if (age == 0) Fx.sound(level, gate, SoundEvents.BEACON_ACTIVATE, 3.0F, 0.6F);

		// ----- 천국의 문: 하늘 높은 곳에서 천천히 내려옴 -----
		gateNow = gate.add(0, descentOffset(age), 0);
		double open;
		if (age < DESCEND_TICKS) open = 0.35 + 0.65 * age / DESCEND_TICKS; // 내려오며 활짝 열림
		else if (age < 190) open = 1.0;
		else open = Math.max(0, 1.0 - (age - 190) / 25.0);
		if (open > 0 && age % 2 == 0) drawGate(open, r);
		// 내려오는 동안 문 뒤로 빛 꼬리
		if (age < DESCEND_TICKS) {
			Fx.burst(level, com.elementalweapons.fx.P.END_ROD, gateNow.add(0, 3, 0), 4, 1.5, 0.02);
			Fx.burst(level, PALE_GOLD, gateNow.add(0, 5, 0), 8, 2.0, 0.0);
		}
		if (age == DESCEND_TICKS) {
			// 도착 순간: 빛이 터지며 종소리
			Fx.burst(level, com.elementalweapons.fx.P.END_ROD, gate, 80, 2.5, 0.2);
			Fx.sound(level, gate, SoundEvents.BELL_BLOCK, 3.0F, 0.7F);
		}

		// 문 아래로 쏟아지는 빛기둥 (문이 도착한 뒤)
		if (age >= DESCEND_TICKS && age < 200 && age % 3 == 0) {
			for (int i = 0; i < 6; i++) {
				double a = (r.nextDouble() - 0.5) * GATE_HALF_WIDTH * 1.6 * open;
				double y = -4.5 - r.nextDouble() * 9;
				Fx.burst(level, com.elementalweapons.fx.P.END_ROD, plane(gate, a, y, 0), 1, 0.05, 0.01);
			}
			Fx.ring(level, PALE_GOLD, plane(gate, 0, -11, 0).add(0, 0.1, 0), 3.5 * open, 24, age * 0.03);
		}

		// ----- 대천사 -----
		if (age == ANGEL_APPEAR) {
			Fx.sound(level, gate, SoundEvents.TOTEM_USE, 2.5F, 0.8F);
			Fx.burst(level, com.elementalweapons.fx.P.END_ROD, gate, 60, 1.5, 0.15);
		}
		if (age >= ANGEL_APPEAR && age < 210) {
			double p;
			if (age < ANGEL_READY) p = (double) (age - ANGEL_APPEAR) / (ANGEL_READY - ANGEL_APPEAR); // 강림
			else if (age < ANGEL_RETURN) p = 1.0;                                                      // 전투
			else p = Math.max(0, 1.0 - (age - ANGEL_RETURN) / 25.0);                                  // 귀환
			double bob = (age >= ANGEL_READY && age < ANGEL_RETURN) ? Math.sin(age * 0.1) * 0.3 : 0;
			angel = gate.add(fwd.scale(-3.5 * p)).add(0, -3.0 * p - 1.0 + bob, 0);
			if (age % 2 == 0) drawAngel(angel);

			// 빛의 창 공격 (0.6초마다)
			if (age >= ANGEL_READY && age < ANGEL_RETURN && (age - ANGEL_READY) % 12 == 0) {
				attack();
			}
		}
		return age >= 216;
	}

	private void drawGate(double open, RandomSource r) {
		Vec3 gate = this.gateNow; // 지금 위치에 그리기
		double w = GATE_HALF_WIDTH * open;
		// 바깥 금테 (기둥 2개 + 아치)
		for (double b = -4.5; b <= 1.5; b += 0.35) {
			Fx.at(level, GOLD, plane(gate, -w, b, 0));
			Fx.at(level, GOLD, plane(gate, w, b, 0));
			Fx.at(level, PALE_GOLD, plane(gate, -w + 0.4, b, 0));
			Fx.at(level, PALE_GOLD, plane(gate, w - 0.4, b, 0));
		}
		for (double t = 0; t <= Math.PI; t += 0.12) {
			Fx.at(level, GOLD, plane(gate, Math.cos(t) * w, 1.5 + Math.sin(t) * w, 0));
			Fx.at(level, PALE_GOLD, plane(gate, Math.cos(t) * (w - 0.4), 1.5 + Math.sin(t) * (w - 0.4), 0));
		}
		// 문 바닥 구름
		for (double a = -w; a <= w; a += 0.6) {
			Fx.burst(level, com.elementalweapons.fx.P.CLOUD, plane(gate, a, -4.7, 0), 1, 0.1, 0.0);
		}
		// 문 안쪽 빛 (아른거림)
		for (int i = 0; i < 25; i++) {
			double a = (r.nextDouble() * 2 - 1) * (w - 0.5);
			double b = -4.3 + r.nextDouble() * (5.8 + Math.max(0, w - 0.5));
			if (b > 1.5 && a * a + (b - 1.5) * (b - 1.5) > (w - 0.5) * (w - 0.5)) continue;
			Fx.at(level, i % 5 == 0 ? PALE_GOLD : WHITE, plane(gate, a, b, 0.1));
		}
		// 문 뒤에서 퍼지는 후광 (천천히 회전)
		for (int k = 0; k < 12; k++) {
			double ang = k * Math.PI / 6 + age * 0.02;
			for (double d = w + 0.8; d <= w + 2.5; d += 0.45) {
				Fx.at(level, RAY, plane(gate, Math.cos(ang) * d, 0 + Math.sin(ang) * d, 0.3));
			}
		}
	}

	private void drawAngel(Vec3 base) {
		double s = ANGEL_SCALE;
		// 머리 위 광륜 (수평 고리)
		Fx.ring(level, HALO, base.add(0, 2.45 * s, 0), 0.35 * s, 12, age * 0.1);
		// 머리
		for (int i = 0; i < 8; i++) {
			double t = i * Math.PI / 4;
			Fx.at(level, SKIN, plane(base, Math.cos(t) * 0.22 * s, (2.05 + Math.sin(t) * 0.22) * s, 0));
		}
		Fx.at(level, SKIN, plane(base, 0, 2.05 * s, 0));
		// 하얀 로브 (아래로 갈수록 넓어짐)
		for (double b = 0; b <= 1.75; b += 0.15) {
			double hw = 0.15 + (1.75 - b) / 1.75 * 0.45;
			for (double a = -hw; a <= hw + 1.0E-3; a += 0.18) {
				Fx.at(level, WHITE, plane(base, a * s, b * s, 0));
			}
		}
		Fx.line(level, HALO, plane(base, -0.35 * s, 1.0 * s, 0), plane(base, 0.35 * s, 1.0 * s, 0), 0.15); // 금색 허리띠
		// 팔
		Fx.line(level, WHITE, plane(base, -0.28 * s, 1.6 * s, 0), plane(base, -0.65 * s, 1.0 * s, 0), 0.15);
		Fx.line(level, WHITE, plane(base, 0.28 * s, 1.6 * s, 0), plane(base, 0.65 * s, 1.0 * s, 0), 0.15);
		// 오른손의 빛의 검
		Fx.line(level, SPEAR, plane(base, 0.65 * s, 1.0 * s, 0), plane(base, 0.95 * s, 2.9 * s, 0), 0.15);
		Fx.at(level, com.elementalweapons.fx.P.END_ROD, plane(base, 0.95 * s, 2.9 * s, 0));
		// 날개 (양쪽 6장씩, 펄럭임)
		double flap = Math.sin(age * 0.25) * 12;
		for (int side = -1; side <= 1; side += 2) {
			for (int k = 0; k < 6; k++) {
				double ang = Math.toRadians(-20 + k * 17 + flap);
				double len = (2.8 - k * 0.25) * s;
				for (double t = 0.1; t <= 1.0; t += 0.08) {
					double a = side * (0.2 * s + Math.cos(ang) * len * t);
					double b = 1.5 * s + Math.sin(ang) * len * t - 0.5 * t * t;
					double c = Math.abs(a) * 0.35; // 날개는 뒤로 살짝 젖혀짐
					Fx.at(level, t > 0.8 ? FEATHER_TIP : WHITE, plane(base, a, b, c));
				}
			}
		}
	}

	private void attack() {
		var foes = Fx.enemies(level, caster.position(), ATTACK_RANGE);
		if (foes.isEmpty()) return;
		LivingEntity target = foes.get(0);
		Vec3 from = plane(angel, 0.95 * ANGEL_SCALE, 2.9 * ANGEL_SCALE, 0);
		Vec3 to = target.position().add(0, target.getBbHeight() * 0.5, 0);
		Fx.line(level, SPEAR, from, to, 0.3);
		Fx.line(level, com.elementalweapons.fx.P.END_ROD, from, to, 0.8);
		Fx.burst(level, com.elementalweapons.fx.P.END_ROD, to, 25, 0.3, 0.15);
		Fx.burst(level, PALE_GOLD, to, 20, 0.5, 0.02);
		Fx.hurt(level, caster, target, 9.0F);
		target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
		Fx.sound(level, to, SoundEvents.BELL_BLOCK, 1.5F, 1.6F);
		Fx.sound(level, to, SoundEvents.AMETHYST_BLOCK_CHIME, 2.0F, 1.8F);
	}

	/** 검/대검용: 하늘에서 내리꽂히는 빛기둥 */
	public static void lightPillar(ServerLevel level, Player caster, Vec3 pos) {
		for (double y = 0; y <= 20; y += 0.4) {
			Fx.burst(level, com.elementalweapons.fx.P.END_ROD, pos.add(0, y, 0), 1, 0.12, 0.0);
		}
		for (double y = 0; y <= 6; y += 1.5) {
			Fx.ring(level, PALE_GOLD, pos.add(0, y, 0), 0.7, 10, y);
		}
		Fx.ring(level, GOLD, pos.add(0, 0.1, 0), 2.5, 24, 0);
		Fx.burst(level, com.elementalweapons.fx.P.END_ROD, pos.add(0, 0.5, 0), 30, 0.4, 0.2);
		for (LivingEntity e : Fx.enemies(level, pos, 2.5)) {
			Fx.hurt(level, caster, e, 8.0F);
			e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
		}
		Fx.sound(level, pos, SoundEvents.BELL_BLOCK, 1.5F, 1.4F);
	}
}
