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

	/** 단검 돌진 시 남는 속성 파티클 */
	public static void dashBurst(Element element, ServerLevel level, Vec3 pos) {
		ParticleOptions p = switch (element) {
			case ELECTRIC -> ParticleTypes.ELECTRIC_SPARK;
			case FIRE -> ParticleTypes.FLAME;
			case WATER -> ParticleTypes.SPLASH;
			case GRASS -> Fx.dust(0xFFB7D5, 1.0F);
			case WIND -> ParticleTypes.CLOUD;
			case DARK -> ParticleTypes.SQUID_INK;
			case LIGHT -> ParticleTypes.END_ROD;
		};
		Fx.burst(level, p, pos.add(0, 1, 0), 25, 0.5, 0.08);
		Fx.burst(level, Fx.dust(element.color, 1.3F), pos.add(0, 1, 0), 20, 0.6, 0.02);
	}
}
