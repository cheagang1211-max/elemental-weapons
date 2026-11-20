package com.elementalweapons.world.sky;

import java.util.List;
import java.util.stream.Stream;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

/** 공중 세계 바이옴 배치: 수도 / 7지역 / 심연 */
public class SkyBiomeSource extends BiomeSource {
	public static final MapCodec<SkyBiomeSource> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			Biome.CODEC.fieldOf("capital").forGetter(s -> s.capital),
			Biome.CODEC.fieldOf("abyss").forGetter(s -> s.abyss),
			Biome.CODEC.listOf().fieldOf("regions").forGetter(s -> s.regions)).apply(i, SkyBiomeSource::new));

	private final Holder<Biome> capital, abyss;
	private final List<Holder<Biome>> regions;

	public SkyBiomeSource(Holder<Biome> capital, Holder<Biome> abyss, List<Holder<Biome>> regions) {
		this.capital = capital; this.abyss = abyss; this.regions = regions;
	}

	@Override protected MapCodec<? extends BiomeSource> codec() { return CODEC; }

	@Override
	protected Stream<Holder<Biome>> collectPossibleBiomes() {
		return Stream.concat(Stream.of(capital, abyss), regions.stream());
	}

	@Override
	public BiomeResolver createResolver(Climate.Sampler sampler) {
		return (qx, qy, qz) -> {
			int r;
			try { r = SkyLayout.regionAt(qx * 4 + 2, qz * 4 + 2); }
			catch (Throwable t) { com.elementalweapons.ElementalWeaponsMod.LOGGER.error("[SKY] 바이옴 오류", t); return capital; }
			if (r < 0) return capital;
			if (r == 7) return abyss;
			return regions.get(Math.min(r, regions.size() - 1));
		};
	}
}
