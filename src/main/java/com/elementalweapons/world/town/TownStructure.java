package com.elementalweapons.world.town;

import java.util.Optional;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

/** 대규모 마을 (112 x 112 블록) */
public class TownStructure extends Structure {
	public static final MapCodec<TownStructure> CODEC = simpleCodec(TownStructure::new);

	public TownStructure(StructureSettings settings) {
		super(settings);
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext ctx) {
		int x0 = ctx.chunkPos().getMinBlockX() - 48, z0 = ctx.chunkPos().getMinBlockZ() - 48;
		int y = getMeanFirstOccupiedHeight(ctx, x0, TownPiece.SIZE, z0, TownPiece.SIZE);
		int sea = ctx.chunkGenerator().getSeaLevel();
		if (y <= sea + 1) return Optional.empty();
		long seed = ctx.seed() ^ ctx.chunkPos().toLong() * 31L;
		BlockPos center = new BlockPos(x0 + TownPiece.SIZE / 2, y, z0 + TownPiece.SIZE / 2);
		if (!ctx.validBiome().test(ctx.chunkGenerator().getBiomeSource().getNoiseBiome(center.getX() >> 2, y >> 2, center.getZ() >> 2, ctx.randomState().sampler())))
			return Optional.empty();
		return Optional.of(new GenerationStub(center, b -> b.addPiece(new TownPiece(x0, y, z0, seed))));
	}

	@Override
	public StructureType<?> type() {
		return ModTowns.TOWN;
	}
}
