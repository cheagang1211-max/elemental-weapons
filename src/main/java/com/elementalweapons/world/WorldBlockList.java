// 자동 생성 파일 (tools/worldgen/world.py) - 직접 수정하지 마세요
package com.elementalweapons.world;

import com.elementalweapons.world.ModBlocks.Kind;

final class WorldBlockList {
	private WorldBlockList() {}

	static void blocks() {
		ModBlocks.add("aether_stone", Kind.STONE, 0, 0, false);
		ModBlocks.add("abyss_stone", Kind.DEEPSTONE, 0, 0, false);
		ModBlocks.add("elemental_dirt", Kind.DIRT, 0, 0, false);
		ModBlocks.add("elemental_sand", Kind.SAND, 0, 0, false);
		ModBlocks.add("storm_grass", Kind.GRASS, 0, 0, false);
		ModBlocks.add("storm_log", Kind.LOG, 0, 0, false);
		ModBlocks.add("storm_planks", Kind.PLANKS, 0, 0, false);
		ModBlocks.add("storm_leaves", Kind.LEAVES, 0, 0, false);
		ModBlocks.add("storm_ore", Kind.ORE, 3, 7, false);
		ModBlocks.add("ember_soil", Kind.GRASS, 0, 0, false);
		ModBlocks.add("ember_log", Kind.LOG, 0, 0, false);
		ModBlocks.add("ember_planks", Kind.PLANKS, 0, 0, false);
		ModBlocks.add("ember_leaves", Kind.LEAVES, 0, 0, false);
		ModBlocks.add("ember_ore", Kind.ORE, 3, 7, false);
		ModBlocks.add("azure_grass", Kind.GRASS, 0, 0, false);
		ModBlocks.add("azure_log", Kind.LOG, 0, 0, false);
		ModBlocks.add("azure_planks", Kind.PLANKS, 0, 0, false);
		ModBlocks.add("azure_leaves", Kind.LEAVES, 0, 0, false);
		ModBlocks.add("tide_ore", Kind.ORE, 3, 7, false);
		ModBlocks.add("blossom_grass", Kind.GRASS, 0, 0, false);
		ModBlocks.add("blossom_log", Kind.LOG, 0, 0, false);
		ModBlocks.add("blossom_planks", Kind.PLANKS, 0, 0, false);
		ModBlocks.add("blossom_leaves", Kind.LEAVES, 0, 0, false);
		ModBlocks.add("bloom_ore", Kind.ORE, 3, 7, false);
		ModBlocks.add("gale_grass", Kind.GRASS, 0, 0, false);
		ModBlocks.add("gale_log", Kind.LOG, 0, 0, false);
		ModBlocks.add("gale_planks", Kind.PLANKS, 0, 0, false);
		ModBlocks.add("gale_leaves", Kind.LEAVES, 0, 0, false);
		ModBlocks.add("gale_ore", Kind.ORE, 3, 7, false);
		ModBlocks.add("shadow_grass", Kind.GRASS, 0, 0, false);
		ModBlocks.add("shadow_log", Kind.LOG, 0, 0, false);
		ModBlocks.add("shadow_planks", Kind.PLANKS, 0, 0, false);
		ModBlocks.add("shadow_leaves", Kind.LEAVES, 0, 0, true);
		ModBlocks.add("umbral_ore", Kind.ORE, 3, 7, true);
		ModBlocks.add("radiant_grass", Kind.GRASS, 0, 0, false);
		ModBlocks.add("radiant_log", Kind.LOG, 0, 0, false);
		ModBlocks.add("radiant_planks", Kind.PLANKS, 0, 0, false);
		ModBlocks.add("radiant_leaves", Kind.LEAVES, 0, 0, true);
		ModBlocks.add("radiant_ore", Kind.ORE, 3, 7, true);
		ModBlocks.add("aether_ore", Kind.ORE, 0, 2, false);
		ModBlocks.add("orichalcum_ore", Kind.DEEP_ORE, 4, 9, false);
	}

	static void materials() {
		ModBlocks.material("storm_crystal");
		ModBlocks.material("ember_crystal");
		ModBlocks.material("tide_crystal");
		ModBlocks.material("bloom_crystal");
		ModBlocks.material("gale_crystal");
		ModBlocks.material("umbral_crystal");
		ModBlocks.material("radiant_crystal");
		ModBlocks.material("raw_aether");
		ModBlocks.material("aether_ingot");
		ModBlocks.material("orichalcum_chunk");
		ModBlocks.material("orichalcum_ingot");
		ModBlocks.material("element_dust");
	}
}
