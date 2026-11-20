package com.elementalweapons.world.sky;

import com.mojang.serialization.MapCodec;

import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;

/** 공중섬 지형 밀도 함수 (elementalweapons:sky_islands) */
public final class SkyDensity implements DensityFunction {
	public static final SkyDensity INSTANCE = new SkyDensity();
	public static final MapCodec<SkyDensity> CODEC = MapCodec.unit(INSTANCE);

	private static final DensitySampler SAMPLER = new DensitySampler() {
		@Override
		public void sampleVolume(SamplerContext ctx, DensityBuffer buffer, DensityVolume volume) {
			DensitySampler.sampleVolumeNaive(ctx, buffer, volume, this);
		}

		@Override
		public float sampleValue(SamplerContext ctx, int x, int y, int z) {
			return SkyLayout.density(x, y, z);
		}
	};

	private SkyDensity() {}

	@Override public DensitySampler compileSampler(CompileContext ctx) { return SAMPLER; }
	@Override public DensityFunction rewriteChildren(DfRewriteRule rule) { return this; }
	@Override public Interval range() { return Interval.of(-1F, 1F); }
	@Override public int domainAxes() { return ALL_AXES; }
	@Override public MapCodec<? extends DensityFunction> codec() { return CODEC; }
}
