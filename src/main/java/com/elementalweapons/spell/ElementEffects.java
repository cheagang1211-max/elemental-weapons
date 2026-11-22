package com.elementalweapons.spell;

import java.util.List;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.item.Element;

/** 속성별 효과를 고르는 곳 */
public final class ElementEffects {
	private ElementEffects() {}

	/** 지팡이 마법 생성. null이면 쓸 수 없는 상황(예: 주변에 적이 없음) */
	public static Spell staffSpell(Element element, ServerLevel level, Player caster) {
		// 마법진이 위치를 스스로 정하지 않는 마법은 여기서 그림 (불·물·바람·빛은 각 마법 안에서)
		switch (element) {
			case ELECTRIC -> SpellManager.add(new MagicCircle(level, caster, element, caster.position().add(0, 16, 0), 9, 60, false));   // 벼락이 떨어지는 하늘
			case GRASS -> SpellManager.add(new MagicCircle(level, caster, element, caster.position().add(0, 0.1, 0), 7, 80, false));
			case DARK -> SpellManager.add(new MagicCircle(level, caster, element, caster.position().add(0, 0.1, 0), 7, 120, false));
			default -> {}
		}
		return switch (element) {
			case ELECTRIC -> new ElectricStormSpell(level, caster);
			case FIRE -> new FireStormSpell(level, caster);
			case WATER -> new WaterOrbSpell(level, caster);
			case GRASS -> PetalSpell.forStaff(level, caster);
			case WIND -> new GaleSpell(level, caster);
			case DARK -> new DarkSwarmSpell(level, caster);
			case LIGHT -> new HeavenGateSpell(level, caster);
		};
	}

	/** 검/대검: pos 지점에 속성 공격 한 번 */
	public static void strike(Element element, ServerLevel level, Player caster, Vec3 pos) {
		switch (element) {
			case ELECTRIC -> ElectricStormSpell.bolt(level, caster, pos, 8.0F);
			case FIRE -> FireStormSpell.blast(level, caster, pos);
			case WATER -> WaterOrbSpell.splash(level, caster, pos, 7.0F, 3.5);
			case GRASS -> {
				List<LivingEntity> foes = Fx.enemies(level, pos, 3.5);
				Fx.burst(level, Fx.dust(0xFFB7D5, 1.2F), pos.add(0, 0.8, 0), 40, 1.2, 0.02);
				if (!foes.isEmpty()) SpellManager.add(new PetalSpell(level, caster, foes, 10, 60, 1.5F));
			}
			case WIND -> GaleSpell.gust(level, caster, pos);
			case DARK -> DarkSwarmSpell.burst(level, caster, pos);
			case LIGHT -> HeavenGateSpell.lightPillar(level, caster, pos);
		}
	}

	/** 망치: 땅을 내리쳐 충격파 (주변 적에게 피해 + 띄우기) */
	public static void hammerSlam(Element element, ServerLevel level, Player caster) {
		Vec3 c = caster.position();
		com.elementalweapons.boss.ElementFx.impact(level, element, c, 5.0);
		for (int k = 1; k <= 3; k++) Fx.ring(level, com.elementalweapons.boss.ElementFx.particle(element), c.add(0, 0.2, 0), k * 2.0, 18 * k, 0.02);
		for (LivingEntity e : Fx.enemies(level, c, 6.5)) {
			if (e == caster) continue;
			e.hurtServer(level, level.damageSources().playerAttack(caster), 14.0F);
			e.push(0, 0.9, 0);
		}
		Fx.sound(level, c, net.minecraft.sounds.SoundEvents.ANVIL_LAND, 1.0F, 0.6F);
	}

	/** 마법서: 주변 아군(플레이어)에게 속성 지원 효과 */
	public static void supportAura(Element element, ServerLevel level, Player caster) {
		Vec3 c = caster.position();
		int dur = 20 * 15;
		for (Player ally : level.getEntitiesOfClass(Player.class, caster.getBoundingBox().inflate(10.0))) {
			switch (element) {
				case FIRE -> { buff(ally, net.minecraft.world.effect.MobEffects.STRENGTH, dur, 1); buff(ally, net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE, dur, 0); }
				case WATER -> { ally.heal(8.0F); buff(ally, net.minecraft.world.effect.MobEffects.REGENERATION, 20 * 8, 0); }
				case GRASS -> { buff(ally, net.minecraft.world.effect.MobEffects.REGENERATION, dur, 1); buff(ally, net.minecraft.world.effect.MobEffects.SATURATION, 20, 0); }
				case WIND -> { buff(ally, net.minecraft.world.effect.MobEffects.SPEED, dur, 1); buff(ally, net.minecraft.world.effect.MobEffects.JUMP_BOOST, dur, 1); }
				case ELECTRIC -> { buff(ally, net.minecraft.world.effect.MobEffects.HASTE, dur, 1); buff(ally, net.minecraft.world.effect.MobEffects.SPEED, dur, 0); }
				case DARK -> { buff(ally, net.minecraft.world.effect.MobEffects.RESISTANCE, dur, 1); buff(ally, net.minecraft.world.effect.MobEffects.NIGHT_VISION, 20 * 30, 0); }
				case LIGHT -> { ally.heal(10.0F); buff(ally, net.minecraft.world.effect.MobEffects.ABSORPTION, dur, 2); ally.removeEffect(net.minecraft.world.effect.MobEffects.POISON); ally.removeEffect(net.minecraft.world.effect.MobEffects.WITHER); }
			}
			Fx.line(level, com.elementalweapons.boss.ElementFx.dust(element, 1.0F), c.add(0, 1.2, 0), ally.position().add(0, 1.0, 0), 0.4);
			Fx.burst(level, com.elementalweapons.boss.ElementFx.particle(element), ally.position().add(0, 1.0, 0), 12, 0.5, 0.03);
		}
		if (element == Element.DARK) {   // 어둠: 주변 적 약화
			for (LivingEntity e : Fx.enemies(level, c, 10.0)) {
				e.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, dur, 1));
				e.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOWNESS, dur, 1));
			}
		}
		for (int k = 1; k <= 3; k++) Fx.ring(level, com.elementalweapons.boss.ElementFx.dust(element, 1.2F), c.add(0, 0.1 + k * 0.5, 0), 3.0 + k * 2.2, 30 + k * 10, 0);
		Fx.sound(level, c, net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, 0.8F, 1.6F);
	}

	private static void buff(Player p, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> eff, int dur, int amp) {
		p.addEffect(new net.minecraft.world.effect.MobEffectInstance(eff, dur, amp));
	}

	/** 방패로 막았을 때 속성 반격 */
	public static void shieldCounter(Element element, ServerLevel level, Player owner, LivingEntity attacker) {
		Vec3 p = attacker.position();
		switch (element) {
			case ELECTRIC -> ElectricStormSpell.bolt(level, owner, p, 6.0F);
			case FIRE -> { attacker.igniteForSeconds(4.0F); Fx.burst(level, com.elementalweapons.fx.P.FLAME, p.add(0, 1, 0), 20, 0.5, 0.05); }
			case WATER -> { Vec3 d = p.subtract(owner.position()).normalize(); attacker.push(d.x * 1.5, 0.4, d.z * 1.5); Fx.burst(level, com.elementalweapons.fx.P.SPLASH, p.add(0, 1, 0), 30, 0.6, 0.1); }
			case GRASS -> { owner.heal(2.0F); Fx.burst(level, Fx.dust(0xFFB7D5, 1.0F), owner.position().add(0, 1, 0), 15, 0.6, 0.02); }
			case WIND -> { Vec3 d = p.subtract(owner.position()).normalize(); attacker.push(d.x * 2.2, 0.6, d.z * 2.2); Fx.burst(level, com.elementalweapons.fx.P.CLOUD, p.add(0, 1, 0), 15, 0.5, 0.05); }
			case DARK -> { attacker.hurtServer(level, level.damageSources().playerAttack(owner), 4.0F); owner.heal(2.0F); Fx.burst(level, com.elementalweapons.fx.P.SQUID_INK, p.add(0, 1, 0), 15, 0.4, 0.02); }
			case LIGHT -> { owner.heal(3.0F); Fx.burst(level, com.elementalweapons.fx.P.END_ROD, owner.position().add(0, 1, 0), 20, 0.6, 0.03); }
		}
	}

	/** 단검 돌진 시 남는 속성 파티클 */
	public static void dashBurst(Element element, ServerLevel level, Vec3 pos) {
		ParticleOptions p = switch (element) {
			case ELECTRIC -> com.elementalweapons.fx.P.ELECTRIC_SPARK;
			case FIRE -> com.elementalweapons.fx.P.FLAME;
			case WATER -> com.elementalweapons.fx.P.SPLASH;
			case GRASS -> Fx.dust(0xFFB7D5, 1.0F);
			case WIND -> com.elementalweapons.fx.P.CLOUD;
			case DARK -> com.elementalweapons.fx.P.SQUID_INK;
			case LIGHT -> com.elementalweapons.fx.P.END_ROD;
		};
		Fx.burst(level, p, pos.add(0, 1, 0), 25, 0.5, 0.08);
		Fx.burst(level, Fx.dust(element.color, 1.3F), pos.add(0, 1, 0), 20, 0.6, 0.02);
	}
}
