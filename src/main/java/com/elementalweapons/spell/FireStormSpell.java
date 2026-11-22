package com.elementalweapons.spell;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 🔥 불 지팡이: 내 주변에 불꽃 소용돌이(불폭풍)가 점점 커지다가 마지막에 연쇄 폭발합니다.
 * 폭발은 블록을 부수지 않고, 시전자 본인은 피해를 받지 않습니다.
 */
public class FireStormSpell extends Spell {
	private static final com.elementalweapons.fx.FxOptions EMBER = Fx.dust(0xFF7A1A, 1.5F);
	private static final com.elementalweapons.fx.FxOptions HOT = Fx.dust(0xFFD84A, 1.2F);

	private static final int GROW_TICKS = 50;   // 불폭풍이 커지는 시간 (2.5초)
	private static final double MAX_RADIUS = 7.0;

	private final Vec3 center;

	public FireStormSpell(ServerLevel level, Player caster) {
		super(level, caster);
		this.center = caster.position();
		SpellManager.add(new MagicCircle(level, caster, com.elementalweapons.item.Element.FIRE, center.add(0, 0.1, 0), MAX_RADIUS + 1.5, GROW_TICKS + 30, false));
	}

	@Override
	protected boolean update() {
		RandomSource r = level.getRandom();

		if (age < GROW_TICKS) {
			double radius = 1.5 + (MAX_RADIUS - 1.5) * age / GROW_TICKS;
			// 불꽃 나선 3갈래
			for (int arm = 0; arm < 3; arm++) {
				for (double h = 0; h <= 4.5; h += 0.4) {
					double ang = age * 0.35 + arm * (2 * Math.PI / 3) + h * 0.7;
					double rr = radius * (0.7 + 0.08 * h);
					Vec3 p = center.add(Math.cos(ang) * rr, h, Math.sin(ang) * rr);
					Fx.burst(level, com.elementalweapons.fx.P.FLAME, p, 1, 0.05, 0.01);
					if (h % 1.2 < 0.4) Fx.at(level, EMBER, p);
				}
			}
			// 바닥 불꽃 고리 + 용암 방울 + 연기
			Fx.ring(level, HOT, center.add(0, 0.15, 0), radius, 24, age * 0.1);
			for (int i = 0; i < 3; i++) {
				double a = r.nextDouble() * Math.PI * 2, d = r.nextDouble() * radius;
				Fx.at(level, com.elementalweapons.fx.P.LAVA, center.add(Math.cos(a) * d, 0.3, Math.sin(a) * d));
			}
			Fx.burst(level, com.elementalweapons.fx.P.LARGE_SMOKE, center.add(0, 5, 0), 3, radius * 0.5, 0.02);

			// 0.5초마다 소용돌이 안의 적에게 불을 붙임
			if (age % 10 == 0) {
				Fx.sound(level, center, SoundEvents.BLAZE_SHOOT, 1.5F, 0.6F + age / 100F);
				for (LivingEntity e : Fx.enemies(level, center, radius + 1)) {
					e.igniteForSeconds(4);
					Fx.hurt(level, caster, e, 2.0F);
				}
			}
			return false;
		}

		if (age == GROW_TICKS) {
			// 💥 연쇄 폭발: 가운데 1번 + 둘레 6번
			// (바닐라 폭발은 쓰지 않음: 지형·아이템·아군 피해와 처치 판정 오류가 생길 수 있어서, 적에게만 직접 피해)
			fireBlast(level, caster, center, 5.0, 16.0F);
			for (int i = 0; i < 6; i++) {
				double a = i * Math.PI / 3;
				fireBlast(level, caster, center.add(Math.cos(a) * 4.5, 0, Math.sin(a) * 4.5), 3.2, 10.0F);
			}
			Fx.burst(level, com.elementalweapons.fx.P.FLAME, center.add(0, 1, 0), 90, 3.0, 0.25);
			Fx.burst(level, EMBER, center.add(0, 2, 0), 50, 4.0, 0.1);
			for (LivingEntity e : Fx.enemies(level, center, 9)) {
				e.igniteForSeconds(6);
			}
		}
		return age >= GROW_TICKS + 5;
	}

	/** 검/대검용 작은 화염 폭발 */
	public static void blast(ServerLevel level, Player caster, Vec3 pos) {
		fireBlast(level, caster, pos, 3.0, 7.0F);
		Fx.burst(level, com.elementalweapons.fx.P.FLAME, pos.add(0, 0.5, 0), 40, 0.8, 0.15);
		Fx.burst(level, EMBER, pos.add(0, 0.8, 0), 25, 1.0, 0.05);
		for (LivingEntity e : Fx.enemies(level, pos, 3.5)) {
			e.igniteForSeconds(4);
		}
	}

	/** 폭발 대신: 범위 안 적에게 직접 피해 + 밀치기 + 손그림 폭발 연출 */
	public static void fireBlast(ServerLevel level, Player caster, Vec3 pos, double r, float dmg) {
		com.elementalweapons.boss.ElementFx.impact(level, com.elementalweapons.item.Element.FIRE, pos, r);
		Fx.at(level, com.elementalweapons.fx.P.EXPLOSION, pos.add(0, 0.8, 0));
		for (LivingEntity e : Fx.enemies(level, pos, r)) {
			Fx.hurt(level, caster, e, dmg);
			Vec3 push = e.position().subtract(pos).multiply(1, 0, 1);
			if (push.lengthSqr() > 1.0E-4) push = push.normalize().scale(0.6);
			Fx.launch(e, push.x, 0.35, push.z);
		}
		Fx.sound(level, pos, net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE.value(), 0.8F, 1.2F);
	}
}
