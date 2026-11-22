package com.elementalweapons.spell;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * 🦇 어둠 지팡이: 박쥐 떼가 소환되고, 나를 감싼 어둠의 기운이 박쥐들을 통해
 * 주변 적들에게 어둠의 화살을 쏘아 피해와 시듦 효과를 줍니다. 끝나면 박쥐는 연기로 사라집니다.
 */
public class DarkSwarmSpell extends Spell {
	private static final com.elementalweapons.fx.FxOptions VOID = Fx.dust(0x2A0A3A, 2.0F);
	private static final com.elementalweapons.fx.FxOptions SHADOW = Fx.dust(0x5A1A8A, 1.4F);
	private static final com.elementalweapons.fx.FxOptions BOLT = Fx.dust(0x9A3BEA, 1.3F);

	private static final int BATS = 10;
	private static final int DURATION = 120; // 6초
	private static final double RADIUS = 14;

	private final List<Entity> bats = new ArrayList<>();

	public DarkSwarmSpell(ServerLevel level, Player caster) {
		super(level, caster);
	}

	@Override
	protected boolean update() {
		RandomSource r = level.getRandom();
		Vec3 c = caster.position();

		if (age == 0) {
			for (int i = 0; i < BATS; i++) {
				Entity bat = EntityTypes.BAT.create(level, EntitySpawnReason.MOB_SUMMONED);
				if (bat != null) bat.addTag(com.elementalweapons.world.VanillaPurge.ALLOWED);
				if (bat == null) continue;
				double a = i * 2 * Math.PI / BATS;
				bat.setPos(c.x + Math.cos(a) * 2, c.y + 2 + r.nextDouble(), c.z + Math.sin(a) * 2);
				level.addFreshEntity(bat);
				bats.add(bat);
				Fx.burst(level, com.elementalweapons.fx.P.SQUID_INK, bat.position(), 5, 0.2, 0.02);
			}
			Fx.sound(level, c, SoundEvents.BAT_TAKEOFF, 2.0F, 0.7F);
			Fx.sound(level, c, SoundEvents.ELDER_GUARDIAN_CURSE, 0.7F, 1.4F);
		}

		// 나를 감싸는 어둠의 나선
		for (int k = 0; k < 3; k++) {
			double a = age * 0.25 + k * 2 * Math.PI / 3;
			double rr = 3 + Math.sin(age * 0.1) * 0.5;
			double h = (age * 0.08 + k) % 3;
			Vec3 p = c.add(Math.cos(a) * rr, h, Math.sin(a) * rr);
			Fx.at(level, VOID, p);
			Fx.at(level, com.elementalweapons.fx.P.SQUID_INK, p);
		}
		Fx.ring(level, VOID, c.add(0, 0.1, 0), 5, 20, age * 0.05);
		if (age % 5 == 0) Fx.burst(level, com.elementalweapons.fx.P.SOUL, c.add(0, 1, 0), 2, 2.0, 0.02);
		for (Entity bat : bats) {
			if (!bat.isRemoved()) Fx.at(level, SHADOW, bat.position());
		}

		// 0.75초마다 박쥐 → 적에게 어둠의 화살
		if (age % 15 == 7) {
			List<LivingEntity> foes = Fx.enemies(level, c, RADIUS);
			int n = Math.min(8, foes.size());
			for (int i = 0; i < n; i++) {
				LivingEntity e = foes.get(i);
				Vec3 from = randomBatPos(r);
				Vec3 to = e.position().add(0, e.getBbHeight() * 0.5, 0);
				Fx.line(level, BOLT, from, to, 0.3);
				Fx.burst(level, com.elementalweapons.fx.P.SQUID_INK, to, 8, 0.3, 0.05);
				Fx.hurt(level, caster, e, 4.0F);
				e.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));
			}
			if (n > 0) Fx.sound(level, c, SoundEvents.WITHER_SHOOT, 0.6F, 1.5F);
		}
		return age >= DURATION;
	}

	private Vec3 randomBatPos(RandomSource r) {
		List<Entity> alive = new ArrayList<>();
		for (Entity b : bats) if (!b.isRemoved()) alive.add(b);
		if (alive.isEmpty()) return caster.getEyePosition();
		return alive.get(r.nextInt(alive.size())).position();
	}

	@Override
	protected void onEnd() {
		for (Entity bat : bats) {
			if (!bat.isRemoved()) {
				Fx.burst(level, com.elementalweapons.fx.P.LARGE_SMOKE, bat.position(), 8, 0.2, 0.02);
				bat.discard();
			}
		}
	}

	/** 검/대검용: 어둠 폭발 */
	public static void burst(ServerLevel level, Player caster, Vec3 pos) {
		Fx.burst(level, com.elementalweapons.fx.P.SQUID_INK, pos.add(0, 0.6, 0), 30, 0.8, 0.08);
		Fx.burst(level, SHADOW, pos.add(0, 0.8, 0), 40, 1.2, 0.02);
		Fx.ring(level, VOID, pos.add(0, 0.1, 0), 2.5, 20, 0);
		for (LivingEntity e : Fx.enemies(level, pos, 3.5)) {
			Fx.hurt(level, caster, e, 6.0F);
			e.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));
		}
		Fx.sound(level, pos, SoundEvents.WITHER_SHOOT, 0.5F, 1.8F);
	}
}
