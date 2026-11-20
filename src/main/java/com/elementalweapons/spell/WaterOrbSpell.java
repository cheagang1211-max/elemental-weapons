package com.elementalweapons.spell;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * 💧 물 지팡이: 바라보는 지점 하늘 위로 사방에서 물줄기가 모여 거대한 물구슬이 되고,
 * 다 모이면 그 지점으로 떨어져 터지며 주변 적을 밀쳐냅니다.
 */
public class WaterOrbSpell extends Spell {
	private static final com.elementalweapons.fx.FxOptions DEEP = Fx.dust(0x1E6FD9, 1.8F);
	private static final com.elementalweapons.fx.FxOptions MID = Fx.dust(0x3FA0FF, 1.5F);
	private static final com.elementalweapons.fx.FxOptions FOAM = Fx.dust(0xDDF4FF, 1.2F);

	private static final int GATHER = 40;  // 모이는 시간
	private static final int FALL = 12;    // 떨어지는 시간
	private static final double HEIGHT = 14;
	private static final double ORB_RADIUS = 3.0;

	private final Vec3 target;
	private final Vec3 sky;
	private final Vec3[] streams = new Vec3[8];

	public WaterOrbSpell(ServerLevel level, Player caster) {
		super(level, caster);
		this.target = Fx.lookPoint(caster, 40);
		this.sky = target.add(0, HEIGHT, 0);
		RandomSource r = level.getRandom();
		for (int i = 0; i < streams.length; i++) {
			Vec3 d = new Vec3(r.nextGaussian(), r.nextGaussian() * 0.5, r.nextGaussian());
			streams[i] = d.lengthSqr() < 1.0E-4 ? new Vec3(1, 0, 0) : d.normalize();
		}
	}

	@Override
	protected boolean update() {
		RandomSource r = level.getRandom();

		if (age < GATHER) {
			double p = (double) age / GATHER;
			double size = 0.6 + (ORB_RADIUS - 0.6) * p;
			// 사방에서 빨려 들어오는 물줄기
			for (int i = 0; i < streams.length; i++) {
				for (int k = 0; k < 3; k++) {
					double phase = ((age + i * 5 + k * 4) % 12) / 12.0;
					Vec3 pos = sky.add(streams[i].scale(size + 10 * (1 - phase)));
					Fx.at(level, k == 0 ? FOAM : MID, pos);
				}
				Fx.at(level, com.elementalweapons.fx.P.FALLING_WATER, sky.add(streams[i].scale(size + 6)));
			}
			// 점점 커지는 물구슬
			Fx.sphere(level, DEEP, sky, size, 30 + (int) (40 * p));
			Fx.sphere(level, FOAM, sky, size * 0.6, 10);
			Fx.burst(level, com.elementalweapons.fx.P.SPLASH, sky, 6, size * 0.5, 0.1);
			// 지면 표시 (어디에 떨어질지)
			Fx.ring(level, MID, target.add(0, 0.1, 0), 5.0, 30, age * 0.05);
			if (age % 8 == 0) Fx.sound(level, sky, SoundEvents.BUCKET_FILL, 1.5F, 0.5F + (float) p);
			return false;
		}

		if (age < GATHER + FALL) {
			double t = (double) (age - GATHER) / FALL;
			Vec3 c = sky.add(0, -HEIGHT * t * t, 0); // 가속하며 떨어짐
			Fx.sphere(level, DEEP, c, ORB_RADIUS, 70);
			Fx.sphere(level, FOAM, c, ORB_RADIUS * 0.6, 15);
			Fx.burst(level, com.elementalweapons.fx.P.FALLING_WATER, c.add(0, ORB_RADIUS, 0), 10, 1.5, 0);
			return false;
		}

		if (age == GATHER + FALL) {
			// 💦 착탄
			splash(level, caster, target, 14.0F, 5.5);
			Fx.burst(level, com.elementalweapons.fx.P.SPLASH, target.add(0, 0.5, 0), 500, 3.0, 0.5);
			Fx.burst(level, com.elementalweapons.fx.P.CLOUD, target.add(0, 0.5, 0), 40, 2.0, 0.15);
			Fx.sphere(level, MID, target.add(0, 1, 0), 2.0, 80);
			Fx.sound(level, target, SoundEvents.PLAYER_SPLASH_HIGH_SPEED, 3.0F, 0.6F);
			Fx.sound(level, target, SoundEvents.GENERIC_SPLASH, 3.0F, 0.5F);
			return false;
		}

		// 퍼져 나가는 물결
		double rr = (age - GATHER - FALL) * 0.8;
		Fx.ring(level, FOAM, target.add(0, 0.15, 0), rr, 40, r.nextDouble());
		Fx.ring(level, com.elementalweapons.fx.P.SPLASH, target.add(0, 0.2, 0), rr, 20, 0);
		return age >= GATHER + FALL + 10;
	}

	/** 물보라 피해 + 밀쳐내기 + 불 끄기 (검/대검도 사용) */
	public static void splash(ServerLevel level, Player caster, Vec3 pos, float damage, double radius) {
		Fx.burst(level, com.elementalweapons.fx.P.SPLASH, pos.add(0, 0.5, 0), 120, radius * 0.4, 0.4);
		Fx.burst(level, MID, pos.add(0, 0.8, 0), 40, radius * 0.3, 0.05);
		for (LivingEntity e : Fx.enemies(level, pos, radius)) {
			Fx.hurt(level, caster, e, damage);
			Vec3 d = e.position().subtract(pos);
			Vec3 flat = new Vec3(d.x, 0, d.z);
			flat = flat.lengthSqr() < 1.0E-4 ? new Vec3(0, 0, 0) : flat.normalize();
			Fx.launch(e, flat.x * 1.2, 0.5, flat.z * 1.2);
			e.clearFire();
		}
		Fx.sound(level, pos, SoundEvents.GENERIC_SPLASH, 1.5F, 0.8F);
	}
}
