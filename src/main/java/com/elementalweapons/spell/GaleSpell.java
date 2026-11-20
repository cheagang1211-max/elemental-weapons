package com.elementalweapons.spell;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * 🌪️ 바람 지팡이: 나를 중심으로 거대한 광풍(회오리)이 일어나 주변 적들을 하늘 높이 띄워 올립니다.
 * (높이 떠오른 적은 떨어질 때 낙하 피해도 받아요)
 */
public class GaleSpell extends Spell {
	private static final com.elementalweapons.fx.FxOptions WIND = Fx.dust(0xF0FFFA, 1.3F);
	private static final com.elementalweapons.fx.FxOptions MINT = Fx.dust(0xB8F5E4, 1.0F);

	private static final double RADIUS = 11;
	private static final int DURATION = 36;

	private final Vec3 center;

	public GaleSpell(ServerLevel level, Player caster) {
		super(level, caster);
		this.center = caster.position();
	}

	@Override
	protected boolean update() {
		// 깔때기 모양 회오리 4갈래
		for (int arm = 0; arm < 4; arm++) {
			for (double h = 0; h <= 7; h += 0.5) {
				double rr = 1.5 + h * 1.0;
				double a = age * 0.55 + arm * Math.PI / 2 + h * 0.5;
				Vec3 p = center.add(Math.cos(a) * rr, h, Math.sin(a) * rr);
				Fx.burst(level, com.elementalweapons.fx.P.CLOUD, p, 1, 0.05, 0.02);
				if (((int) (h * 2)) % 2 == 0) Fx.at(level, arm % 2 == 0 ? WIND : MINT, p);
			}
		}
		if (age % 4 == 0) Fx.ring(level, com.elementalweapons.fx.P.SWEEP_ATTACK, center.add(0, 0.5, 0), 5, 8, age * 0.2);

		// 두 번에 걸쳐 띄워 올림
		if (age == 0 || age == 14) {
			if (age == 0) Fx.sound(level, center, SoundEvents.ELYTRA_FLYING, 2.0F, 1.2F);
			else Fx.sound(level, center, SoundEvents.PHANTOM_FLAP, 2.0F, 1.2F);
			for (LivingEntity e : Fx.enemies(level, center, RADIUS)) {
				Vec3 d = e.position().subtract(center);
				Vec3 flat = new Vec3(d.x, 0, d.z);
				flat = flat.lengthSqr() < 1.0E-4 ? Vec3.ZERO : flat.normalize();
				Fx.launch(e, flat.x * 0.5, age == 0 ? 1.5 : 1.0, flat.z * 0.5);
				Fx.hurt(level, caster, e, 4.0F);
				Fx.burst(level, com.elementalweapons.fx.P.CLOUD, e.position(), 10, 0.4, 0.1);
			}
		}
		return age >= DURATION;
	}

	/** 검/대검용: 작은 돌풍으로 적을 띄움 */
	public static void gust(ServerLevel level, Player caster, Vec3 pos) {
		Fx.burst(level, com.elementalweapons.fx.P.CLOUD, pos.add(0, 0.5, 0), 30, 1.2, 0.15);
		Fx.ring(level, WIND, pos.add(0, 0.3, 0), 2.5, 20, 0);
		Fx.at(level, com.elementalweapons.fx.P.SWEEP_ATTACK, pos.add(0, 1, 0));
		for (LivingEntity e : Fx.enemies(level, pos, 3.5)) {
			Fx.launch(e, 0, 1.1, 0);
			Fx.hurt(level, caster, e, 4.0F);
		}
		Fx.sound(level, pos, SoundEvents.PHANTOM_FLAP, 1.5F, 1.4F);
	}
}
