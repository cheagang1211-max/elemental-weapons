package com.elementalweapons.fx;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import com.elementalweapons.ElementalWeaponsMod;

/** 베기 궤적용 발광 입자 (색 = RGB, 알파 = 크기) */
public final class ModParticles {
	public static final ParticleType<ColorParticleOption> SLASH = Registry.register(BuiltInRegistries.PARTICLE_TYPE, ElementalWeaponsMod.id("slash"),
			FabricParticleTypes.complex(true, ColorParticleOption::codec, ColorParticleOption::streamCodec));

	public static final ParticleType<FxOptions> FX = Registry.register(BuiltInRegistries.PARTICLE_TYPE, ElementalWeaponsMod.id("fx"),
			FabricParticleTypes.complex(true, FxOptions.CODEC, FxOptions.STREAM));

	private ModParticles() {}

	public static void initialize() {}

	/** rgb 색, size 0~1 (실제 크기 0.04~0.64) */
	public static ColorParticleOption slash(int rgb, float size) {
		int a = Math.max(1, Math.min(255, Math.round(size * 255)));
		return ColorParticleOption.create(SLASH, (a << 24) | (rgb & 0xFFFFFF));
	}
}
