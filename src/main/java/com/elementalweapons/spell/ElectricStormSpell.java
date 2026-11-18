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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * ⚡ 전기 지팡이: 하늘에서 직접 그린 지그재그 벼락이 주변 적들에게 차례로 내리꽂힙니다.
 * (마인크래프트 기본 번개 엔티티는 쓰지 않습니다)
 */
public class ElectricStormSpell extends Spell {
	private static final DustParticleOptions CORE = Fx.dust(0xF2FBFF, 1.8F);
	private static final DustParticleOptions GLOW = Fx.dust(0x6FD8FF, 1.2F);
	private static final DustParticleOptions BRANCH = Fx.dust(0xA8ECFF, 0.9F);

	private static final int MAX_TARGETS = 6;
	private static final int INTERVAL = 4; // 벼락 간격 (틱)

	private final List<LivingEntity> targets;
	private final Vec3 fallback;

	public ElectricStormSpell(ServerLevel level, Player caster) {
		super(level, caster);
		List<LivingEntity> found = Fx.enemies(level, caster.position(), 20);
		this.targets = new ArrayList<>(found.subList(0, Math.min(MAX_TARGETS, found.size())));
		this.fallback = Fx.lookPoint(caster, 30);
	}

	@Override
	protected boolean update() {
		int count = Math.max(1, targets.size());
		if (age % INTERVAL == 0 && age / INTERVAL < count) {
			Vec3 pos;
			if (targets.isEmpty()) {
				pos = fallback; // 적이 없으면 바라보는 곳에
			} else {
				LivingEntity t = targets.get(age / INTERVAL);
				if (!t.isAlive()) return false;
				pos = t.position();
			}
			bolt(level, caster, pos, 10.0F);
		}
		return age >= count * INTERVAL + 10;
	}

	/** 하늘에서 ground 지점으로 벼락 한 줄기를 그리고 피해를 줍니다. */
	public static void bolt(ServerLevel level, Player caster, Vec3 ground, float damage) {
		RandomSource r = level.getRandom();
		Vec3 top = ground.add((r.nextDouble() - 0.5) * 6, 24, (r.nextDouble() - 0.5) * 6);
		List<Vec3> path = zigzag(top, ground, 14, 1.4, r);

		// 본 줄기: 하얀 심 + 하늘색 빛
		for (int i = 0; i < path.size() - 1; i++) {
			Fx.line(level, CORE, path.get(i), path.get(i + 1), 0.2);
			Fx.line(level, GLOW, path.get(i), path.get(i + 1), 0.45);
			Fx.burst(level, ParticleTypes.ELECTRIC_SPARK, path.get(i), 2, 0.15, 0.05);
		}
		// 갈라지는 가지 3개
		for (int b = 0; b < 3; b++) {
			Vec3 start = path.get(2 + r.nextInt(path.size() - 5));
			Vec3 end = start.add((r.nextDouble() - 0.5) * 8, -3 - r.nextDouble() * 4, (r.nextDouble() - 0.5) * 8);
			List<Vec3> branch = zigzag(start, end, 5, 0.8, r);
			for (int i = 0; i < branch.size() - 1; i++) {
				Fx.line(level, BRANCH, branch.get(i), branch.get(i + 1), 0.3);
			}
		}
		// 착탄 지점 섬광
		Fx.burst(level, ParticleTypes.ELECTRIC_SPARK, ground.add(0, 0.3, 0), 60, 0.6, 0.6);
		Fx.burst(level, ParticleTypes.END_ROD, ground.add(0, 0.5, 0), 20, 0.3, 0.25);
		Fx.ring(level, GLOW, ground.add(0, 0.1, 0), 1.5, 16, r.nextDouble());
		Fx.ring(level, BRANCH, ground.add(0, 0.1, 0), 2.6, 24, r.nextDouble());

		Fx.sound(level, ground, SoundEvents.LIGHTNING_BOLT_THUNDER, 2.5F, 0.9F + r.nextFloat() * 0.4F);
		Fx.sound(level, ground, SoundEvents.LIGHTNING_BOLT_IMPACT, 2.0F, 1.0F);

		for (LivingEntity e : Fx.enemies(level, ground, 2.5)) {
			Fx.hurt(level, caster, e, damage);
			e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0));
		}
	}

	/** from → to 사이를 segments 마디의 지그재그 경로로 */
	static List<Vec3> zigzag(Vec3 from, Vec3 to, int segments, double jitter, RandomSource r) {
		List<Vec3> pts = new ArrayList<>();
		for (int i = 0; i <= segments; i++) {
			double t = (double) i / segments;
			Vec3 p = from.add(to.subtract(from).scale(t));
			if (i != 0 && i != segments) {
				double j = jitter * Math.sin(Math.PI * t) + 0.3;
				p = p.add((r.nextDouble() - 0.5) * 2 * j, (r.nextDouble() - 0.5) * 0.5, (r.nextDouble() - 0.5) * 2 * j);
			}
			pts.add(p);
		}
		return pts;
	}
}
