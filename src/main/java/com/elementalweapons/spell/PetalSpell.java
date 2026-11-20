package com.elementalweapons.spell;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * 🌸 풀 지팡이: 먼 곳에서 꽃잎 무리가 날아와 적을 감싸고 맴돌며 지속 피해를 줍니다.
 */
public class PetalSpell extends Spell {
	private static final DustParticleOptions[] PETALS = {
			Fx.dust(0xFFB7D5, 1.1F), Fx.dust(0xFF8FC0, 1.0F), Fx.dust(0xFFE8F2, 0.9F), Fx.dust(0x7BD36B, 0.8F)
	};

	private final List<LivingEntity> targets;
	private final List<Vec3[]> origins = new ArrayList<>();
	private final int flyTicks;
	private final int swirlTicks;
	private final float damagePerHit;

	public PetalSpell(ServerLevel level, Player caster, List<LivingEntity> targets, int flyTicks, int swirlTicks, float damagePerHit) {
		super(level, caster);
		this.targets = new ArrayList<>(targets);
		this.flyTicks = flyTicks;
		this.swirlTicks = swirlTicks;
		this.damagePerHit = damagePerHit;
		RandomSource r = level.getRandom();
		double far = flyTicks > 15 ? 14 : 6;
		for (LivingEntity t : this.targets) {
			Vec3[] o = new Vec3[3];
			for (int i = 0; i < 3; i++) {
				double a = r.nextDouble() * Math.PI * 2;
				o[i] = t.position().add(Math.cos(a) * far, 4 + r.nextDouble() * 4, Math.sin(a) * far);
			}
			origins.add(o);
		}
	}

	/** 지팡이용: 주변 16블록의 적 최대 8마리. 적이 없으면 null */
	public static PetalSpell forStaff(ServerLevel level, Player caster) {
		List<LivingEntity> found = Fx.enemies(level, caster.position(), 16);
		if (found.isEmpty()) return null;
		// 8초 동안 0.5초마다 2 피해
		return new PetalSpell(level, caster, found.subList(0, Math.min(8, found.size())), 25, 160, 2.0F);
	}

	@Override
	protected boolean update() {
		RandomSource r = level.getRandom();
		if (age == 0) Fx.sound(level, caster.position(), SoundEvents.AMETHYST_BLOCK_CHIME, 2.0F, 1.6F);

		if (age < flyTicks) {
			// 꽃잎 무리가 곡선을 그리며 날아옴
			double p = (age + 1.0) / flyTicks;
			for (int i = 0; i < targets.size(); i++) {
				LivingEntity t = targets.get(i);
				if (!t.isAlive()) continue;
				Vec3 end = t.position().add(0, 1, 0);
				for (Vec3 o : origins.get(i)) {
					for (int k = 0; k < 3; k++) {
						double pp = Math.max(0, p - k * 0.04);
						Vec3 pos = o.add(end.subtract(o).scale(pp)).add(0, Math.sin(pp * Math.PI) * 2.5, 0);
						Fx.burst(level, PETALS[r.nextInt(PETALS.length)], pos, 2, 0.25, 0);
					}
				}
			}
			return false;
		}

		// 적 주위를 맴돌며 지속 피해
		int t = age - flyTicks;
		boolean anyAlive = false;
		for (LivingEntity e : targets) {
			if (!e.isAlive()) continue;
			anyAlive = true;
			Vec3 c = e.position();
			double w = e.getBbWidth() * 0.5 + 0.8;
			for (int k = 0; k < 6; k++) {
				double a = t * 0.3 + k * Math.PI / 3;
				double y = 0.3 + (k % 3) * 0.6 + Math.sin(t * 0.2 + k) * 0.2;
				Fx.at(level, PETALS[k % PETALS.length], c.add(Math.cos(a) * w, y, Math.sin(a) * w));
			}
			if (t % 8 == 0) Fx.burst(level, com.elementalweapons.fx.P.HAPPY_VILLAGER, c.add(0, 1, 0), 2, 0.5, 0);
			if (t % 10 == 0) Fx.hurt(level, caster, e, damagePerHit);
		}
		return !anyAlive || t >= swirlTicks;
	}
}
