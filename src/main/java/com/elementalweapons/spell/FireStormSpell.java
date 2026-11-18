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
	private static final DustParticleOptions EMBER = Fx.dust(0xFF7A1A, 1.5F);
	private static final DustParticleOptions HOT = Fx.dust(0xFFD84A, 1.2F);

	private static final int GROW_TICKS = 50;   // 불폭풍이 커지는 시간 (2.5초)
	private static final double MAX_RADIUS = 7.0;

	private final Vec3 center;

	public FireStormSpell(ServerLevel level, Player caster) {
		super(level, caster);
		this.center = caster.position();
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
					Fx.burst(level, ParticleTypes.FLAME, p, 1, 0.05, 0.01);
					if (h % 1.2 < 0.4) Fx.at(level, EMBER, p);
				}
			}
			// 바닥 불꽃 고리 + 용암 방울 + 연기
			Fx.ring(level, HOT, center.add(0, 0.15, 0), radius, 24, age * 0.1);
			for (int i = 0; i < 3; i++) {
				double a = r.nextDouble() * Math.PI * 2, d = r.nextDouble() * radius;
				Fx.at(level, ParticleTypes.LAVA, center.add(Math.cos(a) * d, 0.3, Math.sin(a) * d));
			}
			Fx.burst(level, ParticleTypes.LARGE_SMOKE, center.add(0, 5, 0), 3, radius * 0.5, 0.02);

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
			level.explode(caster, center.x, center.y + 0.5, center.z, 3.5F, Level.ExplosionInteraction.NONE);
			for (int i = 0; i < 6; i++) {
				double a = i * Math.PI / 3;
				level.explode(caster, center.x + Math.cos(a) * 4.5, center.y + 0.5, center.z + Math.sin(a) * 4.5,
						2.5F, Level.ExplosionInteraction.NONE);
			}
			Fx.burst(level, ParticleTypes.FLAME, center.add(0, 1, 0), 250, 3.0, 0.3);
			Fx.burst(level, ParticleTypes.LAVA, center.add(0, 1, 0), 40, 2.5, 0.5);
			Fx.burst(level, EMBER, center.add(0, 2, 0), 120, 4.0, 0.1);
			for (LivingEntity e : Fx.enemies(level, center, 9)) {
				e.igniteForSeconds(6);
			}
		}
		return age >= GROW_TICKS + 5;
	}

	/** 검/대검용 작은 화염 폭발 */
	public static void blast(ServerLevel level, Player caster, Vec3 pos) {
		level.explode(caster, pos.x, pos.y + 0.3, pos.z, 1.6F, Level.ExplosionInteraction.NONE);
		Fx.burst(level, ParticleTypes.FLAME, pos.add(0, 0.5, 0), 40, 0.8, 0.15);
		Fx.burst(level, EMBER, pos.add(0, 0.8, 0), 25, 1.0, 0.05);
		for (LivingEntity e : Fx.enemies(level, pos, 3.5)) {
			e.igniteForSeconds(4);
		}
	}
}
