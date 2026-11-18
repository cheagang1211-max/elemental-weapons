package com.elementalweapons.boss;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.item.Element;
import com.elementalweapons.spell.ElectricStormSpell;
import com.elementalweapons.spell.Fx;

/** 속성별 보스 공격 연출 (착탄, 투사체 꼬리, 피격 상태이상) */
public final class ElementFx {
	private ElementFx() {}

	public static int color(Element e) {
		return switch (e) {
			case ELECTRIC -> 0x9FF3FF;
			case FIRE -> 0xFF7A1A;
			case WATER -> 0x3FA0FF;
			case GRASS -> 0x7BD36B;
			case WIND -> 0xDFFFF6;
			case DARK -> 0x7A2AC0;
			case LIGHT -> 0xFFE27A;
		};
	}

	public static int color2(Element e) {
		return switch (e) {
			case ELECTRIC -> 0xF2FBFF;
			case FIRE -> 0xFFD84A;
			case WATER -> 0xDDF4FF;
			case GRASS -> 0xFFB7D5;
			case WIND -> 0xB8F5E4;
			case DARK -> 0x2A0A3A;
			case LIGHT -> 0xFFFFFF;
		};
	}

	public static DustParticleOptions dust(Element e, float size) {
		return Fx.dust(color(e), size);
	}

	public static ParticleOptions particle(Element e) {
		return switch (e) {
			case ELECTRIC -> ParticleTypes.ELECTRIC_SPARK;
			case FIRE -> ParticleTypes.FLAME;
			case WATER -> ParticleTypes.SPLASH;
			case GRASS -> ParticleTypes.CHERRY_LEAVES;
			case WIND -> ParticleTypes.CLOUD;
			case DARK -> ParticleTypes.SQUID_INK;
			case LIGHT -> ParticleTypes.END_ROD;
		};
	}

	/** 투사체/구체 한 칸 그리기 */
	public static void orb(ServerLevel level, Element e, Vec3 pos, double size) {
		Fx.sphere(level, dust(e, 1.6F), pos, size, 10);
		Fx.at(level, Fx.dust(color2(e), 1.4F), pos);
		Fx.burst(level, particle(e), pos, 1, size * 0.4, 0.01);
	}

	/** 착탄 연출 */
	public static void impact(ServerLevel level, Element e, Vec3 pos, double r) {
		RandomSource rnd = level.getRandom();
		switch (e) {
			case ELECTRIC -> ElectricStormSpell.drawBolt(level, pos);
			case FIRE -> {
				for (double y = 0; y < 5; y += 0.5) Fx.burst(level, ParticleTypes.FLAME, pos.add(0, y, 0), 4, r * 0.25, 0.02);
				Fx.burst(level, ParticleTypes.LAVA, pos.add(0, 0.5, 0), 12, r * 0.4, 0.3);
				Fx.at(level, ParticleTypes.EXPLOSION, pos.add(0, 0.5, 0));
				Fx.sound(level, pos, SoundEvents.BLAZE_SHOOT, 1.6F, 0.6F);
			}
			case WATER -> {
				for (double y = 0; y < 5; y += 0.4) Fx.burst(level, ParticleTypes.SPLASH, pos.add(0, y, 0), 6, r * 0.2, 0.1);
				Fx.burst(level, ParticleTypes.FALLING_WATER, pos.add(0, 5, 0), 20, r * 0.4, 0);
				Fx.sound(level, pos, SoundEvents.GENERIC_SPLASH, 1.6F, 0.7F);
			}
			case GRASS -> {
				for (int i = 0; i < 6; i++) {
					Vec3 b = pos.add((rnd.nextDouble() - 0.5) * r * 1.6, 0, (rnd.nextDouble() - 0.5) * r * 1.6);
					Fx.line(level, Fx.dust(0x3E8E3A, 1.4F), b, b.add(0, 1.5 + rnd.nextDouble() * 2, 0), 0.2);
				}
				Fx.burst(level, ParticleTypes.CHERRY_LEAVES, pos.add(0, 1, 0), 30, r * 0.5, 0.05);
				Fx.sound(level, pos, SoundEvents.AMETHYST_BLOCK_CHIME, 1.6F, 0.5F);
			}
			case WIND -> {
				Fx.burst(level, ParticleTypes.GUST, pos.add(0, 0.8, 0), 4, r * 0.4, 0);
				Fx.ring(level, ParticleTypes.CLOUD, pos.add(0, 0.3, 0), r, 20, 0);
				Fx.sound(level, pos, SoundEvents.PHANTOM_FLAP, 1.6F, 0.8F);
			}
			case DARK -> {
				Fx.sphere(level, Fx.dust(0x2A0A3A, 2.0F), pos.add(0, 1, 0), r * 0.7, 40);
				Fx.burst(level, ParticleTypes.SQUID_INK, pos.add(0, 1, 0), 30, r * 0.4, 0.1);
				Fx.burst(level, ParticleTypes.SOUL, pos.add(0, 0.5, 0), 6, r * 0.4, 0.03);
				Fx.sound(level, pos, SoundEvents.WITHER_SHOOT, 1.0F, 0.7F);
			}
			case LIGHT -> {
				for (double y = 0; y <= 16; y += 0.5) Fx.burst(level, ParticleTypes.END_ROD, pos.add(0, y, 0), 1, 0.15, 0);
				Fx.ring(level, Fx.dust(0xFFC830, 1.8F), pos.add(0, 0.1, 0), r, 24, 0);
				Fx.sound(level, pos, SoundEvents.BELL_BLOCK, 1.4F, 1.4F);
			}
		}
		Fx.ring(level, dust(e, 1.5F), pos.add(0, 0.15, 0), r, Math.max(12, (int) (r * 8)), rnd.nextDouble());
	}

	/** 맞은 플레이어에게 속성 상태이상 */
	public static void onHit(Element e, Player p) {
		switch (e) {
			case ELECTRIC -> p.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 25, 2));
			case FIRE -> p.igniteForSeconds(4);
			case WATER -> p.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
			case GRASS -> p.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 1));
			case WIND -> p.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 15, 1));
			case DARK -> {
				p.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));
				p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0));
			}
			case LIGHT -> p.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
		}
	}
}
