package com.elementalweapons.world.town;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import com.elementalweapons.ElementalWeaponsMod;

/** 마을 구조물 등록 */
public final class ModTowns {
	private ModTowns() {}

	public static StructureType<TownStructure> TOWN;
	public static StructurePieceType TOWN_PIECE;

	public static void initialize() {
		TOWN = Registry.register(BuiltInRegistries.STRUCTURE_TYPE, ElementalWeaponsMod.id("town"), () -> TownStructure.CODEC);
		TOWN_PIECE = Registry.register(BuiltInRegistries.STRUCTURE_PIECE, ElementalWeaponsMod.id("town_piece"),
				(StructurePieceType.ContextlessType) TownPiece::new);
	}
}
