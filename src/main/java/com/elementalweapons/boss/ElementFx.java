package com.elementalweapons.boss;

import com.elementalweapons.fx.FxOptions;

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

	public static FxOptions dust(Element e, float size) {
		return Fx.dust(color(e), size);
	}

	/** 속성별 손그림 입자 (불꽃 혀, 물방울, 꽃잎, 바람 소용돌이, 번개 조각, 그림자 연기, 별빛) */
	public static ParticleOptions particle(Element e) {
		return switch (e) {
			case ELECTRIC -> FxOptions.of(FxOptions.BOLT, 0xBFF6FF, 0.2F, 7).spin(25F);
			case FIRE -> FxOptions.of(FxOptions.FLAME, 0xFF8A2A, 0.22F, 12).gravity(-0.012F);
			case WATER -> FxOptions.of(FxOptions.DROP, 0x5AB8FF, 0.15F, 14).gravity(0.035F);
			case GRASS -> FxOptions.of(FxOptions.PETAL, 0xFFB7D5, 0.17F, 24).spin(9F).gravity(0.01F);
			case WIND -> FxOptions.of(FxOptions.SWIRL, 0xE8FFF8, 0.26F, 12).spin(-16F).grow(1.4F);
			case DARK -> FxOptions.of(FxOptions.SMOKE, 0x3A1458, 0.3F, 16).grow(1.6F);
			case LIGHT -> FxOptions.of(FxOptions.STAR, 0xFFE27A, 0.18F, 14).spin(6F);
		};
	}

	/** 투사체/구체 한 칸 그리기 */
	public static void orb(ServerLevel level, Element e, Vec3 pos, double size) {
		Fx.at(level, FxOptions.of(FxOptions.ORB, color(e), (float) size * 0.9F, 4), pos);
		Fx.at(level, FxOptions.of(FxOptions.ORB, 0xFFFFFF, (float) size * 0.45F, 4), pos);
		Fx.burst(level, particle(e), pos, 1, size * 0.4, 0.01);
	}

	/** 바닥에 눕는 고리 (충격파) */
	public static void groundRing(ServerLevel level, int rgb, Vec3 pos, double r, float grow, int life) {
		Fx.at(level, FxOptions.of(FxOptions.RING, rgb, (float) r, life).grow(grow).flat(), pos.add(0, 0.08, 0));
	}

	/** 바닥 마법진 */
	public static void rune(ServerLevel level, int rgb, Vec3 pos, double r, int life, float spin) {
		Fx.at(level, FxOptions.of(FxOptions.RUNE, rgb, (float) r, life).spin(spin).flat(), pos.add(0, 0.06, 0));
	}

	/** 사방으로 튀는 입자 */
	public static void radial(ServerLevel level, ParticleOptions p, Vec3 pos, int n, double speed, double up) {
		RandomSource rnd = level.getRandom();
		for (int i = 0; i < n; i++) {
			double a = i * Math.PI * 2 / n + rnd.nextDouble() * 0.4;
			Fx.move(level, p, pos, new Vec3(Math.cos(a) * speed, up * (0.6 + rnd.nextDouble() * 0.8), Math.sin(a) * speed));
		}
	}

	/** 착탄 연출: 바닥 충격파 고리 + 번쩍임 + 속성 그림 입자 */
	public static void impact(ServerLevel level, Element e, Vec3 pos, double r) {
		RandomSource rnd = level.getRandom();
		int c1 = color(e), c2 = color2(e);
		groundRing(level, c1, pos, r * 0.45, 2.2F, 11);
		groundRing(level, 0xFFFFFF, pos, r * 0.25, 2.8F, 7);
		Fx.at(level, FxOptions.of(FxOptions.STAR, 0xFFFFFF, (float) Math.min(2.5, r * 0.35), 6).grow(0.3F).spin(12F), pos.add(0, 0.9, 0));
		radial(level, particle(e), pos.add(0, 0.4, 0), 14, 0.22 + r * 0.04, 0.12);
		switch (e) {
			case ELECTRIC -> {
				ElectricStormSpell.drawBolt(level, pos);
				for (int i = 0; i < 5; i++) Fx.at(level, FxOptions.of(FxOptions.BOLT, 0xE8FCFF, 0.6F, 6).spin(30F), pos.add((rnd.nextDouble() - 0.5) * r, 0.6 + rnd.nextDouble(), (rnd.nextDouble() - 0.5) * r));
			}
			case FIRE -> {
				for (int i = 0; i < 12; i++) Fx.move(level, FxOptions.of(FxOptions.FLAME, i % 3 == 0 ? 0xFFD84A : 0xFF6A1A, 0.45F, 14).grow(0.5F), pos.add((rnd.nextDouble() - 0.5) * r, 0.2, (rnd.nextDouble() - 0.5) * r), new Vec3(0, 0.12 + rnd.nextDouble() * 0.12, 0));
				for (int i = 0; i < 5; i++) Fx.move(level, FxOptions.of(FxOptions.SMOKE, 0x3A3030, 0.5F, 22).grow(1.8F), pos.add(0, 1, 0), new Vec3((rnd.nextDouble() - 0.5) * 0.1, 0.08, (rnd.nextDouble() - 0.5) * 0.1));
				Fx.sound(level, pos, SoundEvents.BLAZE_SHOOT, 1.6F, 0.6F);
			}
			case WATER -> {
				for (int i = 0; i < 18; i++) Fx.move(level, FxOptions.of(FxOptions.DROP, i % 2 == 0 ? 0xDDF4FF : 0x3FA0FF, 0.2F, 20).gravity(0.05F), pos.add(0, 0.3, 0), new Vec3((rnd.nextDouble() - 0.5) * 0.4, 0.35 + rnd.nextDouble() * 0.25, (rnd.nextDouble() - 0.5) * 0.4));
				groundRing(level, 0x9FE0FF, pos, r * 0.6, 1.6F, 16);
				Fx.sound(level, pos, SoundEvents.GENERIC_SPLASH, 1.6F, 0.7F);
			}
			case GRASS -> {
				for (int i = 0; i < 16; i++) Fx.move(level, FxOptions.of(i % 3 == 0 ? FxOptions.LEAF : FxOptions.PETAL, i % 3 == 0 ? 0x7BD36B : 0xFFB7D5, 0.22F, 30).spin(10F).gravity(0.008F), pos.add(0, 0.6, 0), new Vec3((rnd.nextDouble() - 0.5) * 0.5, 0.2 + rnd.nextDouble() * 0.2, (rnd.nextDouble() - 0.5) * 0.5));
				rune(level, 0x7BD36B, pos, r * 0.5, 16, 4F);
				Fx.sound(level, pos, SoundEvents.AMETHYST_BLOCK_CHIME, 1.6F, 0.5F);
			}
			case WIND -> {
				Fx.at(level, FxOptions.of(FxOptions.SWIRL, 0xF2FFFA, (float) r * 0.6F, 14).grow(1.8F).spin(-24F).flat(), pos.add(0, 0.3, 0));
				Fx.at(level, FxOptions.of(FxOptions.SWIRL, 0xB8F5E4, (float) r * 0.4F, 12).grow(1.6F).spin(-30F), pos.add(0, 1.2, 0));
				Fx.sound(level, pos, SoundEvents.PHANTOM_FLAP, 1.6F, 0.8F);
			}
			case DARK -> {
				for (int i = 0; i < 10; i++) Fx.move(level, FxOptions.of(FxOptions.SMOKE, i % 2 == 0 ? 0x2A0A3A : 0x6A2AA0, 0.5F, 22).grow(1.8F), pos.add(0, 0.5, 0), new Vec3((rnd.nextDouble() - 0.5) * 0.3, 0.05 + rnd.nextDouble() * 0.08, (rnd.nextDouble() - 0.5) * 0.3));
				rune(level, 0x9A4AE0, pos, r * 0.6, 18, -6F);
				Fx.sound(level, pos, SoundEvents.WITHER_SHOOT, 1.0F, 0.7F);
			}
			case LIGHT -> {
				rune(level, 0xFFD84A, pos, r * 0.7, 20, 5F);
				for (double y = 0.5; y <= 9; y += 0.7) Fx.at(level, FxOptions.of(FxOptions.STAR, y < 3 ? 0xFFFFFF : 0xFFE27A, (float) (0.5 - y * 0.03), 10).spin(8F), pos.add(0, y, 0));
				Fx.sound(level, pos, SoundEvents.BELL_BLOCK, 1.4F, 1.4F);
			}
		}
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
