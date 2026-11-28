package com.elementalweapons.spell;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.item.Element;

/** 낫 우클릭 '사신의 회오리': 두 바퀴 돌며 넓은 원을 베고, 적을 끌어당겨 피를 빨아들인 뒤 속성 폭발 */
public class ReapSpell extends Spell {
	private static final int DURATION = 12;
	private final Element element;
	private final float m;
	private final Set<LivingEntity> hit = new HashSet<>();
	private final double startYaw;

	public ReapSpell(ServerLevel level, Player caster, Element element, float m) {
		super(level, caster);
		this.element = element;
		this.m = m;
		this.startYaw = Math.toRadians(caster.getYRot() + 90);
		Fx.sound(level, caster.position(), SoundEvents.PLAYER_ATTACK_SWEEP, 1.2F, 0.6F);
		Fx.sound(level, caster.position(), SoundEvents.WITHER_SHOOT, 0.35F, 1.6F);
	}

	@Override
	protected boolean update() {
		if (casterGone()) return true;
		Vec3 c = caster.position().add(0, 1.0, 0);
		// 베기 궤적: 매 틱 40도씩 회전하며 반지름 2~5 범위를 채움 + 잔상
		for (int s = 0; s < 3; s++) {
			double a = startYaw + (age * 3 + s) * (Math.PI * 4 / (DURATION * 3)) + Math.PI;   // 두 바퀴
			for (double r = 1.8; r <= 5.0; r += 0.55) {
				Vec3 p = c.add(Math.cos(a) * r, Math.sin(a * 2) * 0.12 + (r - 3) * 0.05, Math.sin(a) * r);
				Fx.at(level, r > 4.2 ? ElementFx.particle(element) : ElementFx.dust(element, 1.4F), p);
			}
			Fx.at(level, com.elementalweapons.fx.P.SWEEP_ATTACK, c.add(Math.cos(a) * 3.4, 0, Math.sin(a) * 3.4));
		}
		if (age == 2 || age == 8) Fx.ring(level, ElementFx.dust(element, 1.8F), c.add(0, -0.8, 0), 5.2, 48, age);
		// 범위 안 적: 처음 닿을 때 피해 + 끌어당김
		for (LivingEntity e : Fx.enemies(level, c, 5.5)) {
			if (e == caster || !hit.add(e)) continue;
			Fx.hurt(level, caster, e, 14.0F * m);
			e.damageCooldownTime = 0;
			Vec3 pull = c.subtract(e.position()).multiply(1, 0, 1).normalize().scale(0.7);
			Fx.launch(e, pull.x, 0.25, pull.z);
			caster.heal(1.5F);   // 흡혈
			Fx.line(level, ElementFx.dust(element, 0.7F), e.position().add(0, 1, 0), c, 0.6);
		}
		// 두 번째 바퀴: 이미 벤 적도 한 번 더
		if (age == DURATION / 2) for (LivingEntity e : hit) if (e.isAlive() && e.distanceToSqr(caster) < 7 * 7) { Fx.hurt(level, caster, e, 7.0F * m); e.damageCooldownTime = 0; }
		if (age >= DURATION - 1) {
			ElementFx.impact(level, element, caster.position(), 4.5);
			if (hit.size() >= 2) com.elementalweapons.growth.Tutorial.classEvent(caster, "reap2");
			for (LivingEntity e : hit) if (e.isAlive()) { Fx.hurt(level, caster, e, 9.0F * m); e.damageCooldownTime = 0; }
			caster.heal(Math.min(6, hit.size() * 1.0F));
			Fx.sound(level, caster.position(), SoundEvents.PLAYER_ATTACK_CRIT, 1.0F, 0.7F);
			return true;
		}
		return false;
	}
}
