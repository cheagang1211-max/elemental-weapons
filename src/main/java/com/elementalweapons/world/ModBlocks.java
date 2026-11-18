package com.elementalweapons.world;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import com.elementalweapons.ElementalWeaponsMod;

/**
 * 원소 세계의 블록/재료 등록. 목록은 tools/worldgen/world.py 가 WorldBlockList 로 생성합니다.
 */
public final class ModBlocks {
	private ModBlocks() {}

	public enum Kind { STONE, DEEPSTONE, DIRT, SAND, GRASS, LOG, PLANKS, LEAVES, ORE, DEEP_ORE }

	public static final Map<String, Block> BLOCKS = new LinkedHashMap<>();
	public static final Map<String, Item> ITEMS = new LinkedHashMap<>();

	public static final ResourceKey<CreativeModeTab> TAB_KEY =
			ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ElementalWeaponsMod.id("world"));

	static void add(String id, Kind kind, int xpMin, int xpMax, boolean glow) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, ElementalWeaponsMod.id(id));
		BlockBehaviour.Properties p = BlockBehaviour.Properties.of().setId(key);
		switch (kind) {
			case STONE -> p.strength(1.5F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.STONE).mapColor(MapColor.STONE);
			case DEEPSTONE -> p.strength(3.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE).mapColor(MapColor.DEEPSLATE);
			case DIRT -> p.strength(0.5F).sound(SoundType.GRAVEL).mapColor(MapColor.DIRT);
			case SAND -> p.strength(0.5F).sound(SoundType.SAND).mapColor(MapColor.SAND);
			case GRASS -> p.strength(0.6F).sound(SoundType.GRASS).mapColor(MapColor.GRASS);
			case LOG, PLANKS -> p.strength(2.0F, 3.0F).sound(SoundType.WOOD).mapColor(MapColor.WOOD).ignitedByLava();
			case LEAVES -> p.strength(0.2F).sound(SoundType.GRASS).mapColor(MapColor.PLANT).ignitedByLava();
			case ORE -> p.strength(3.0F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.STONE).mapColor(MapColor.STONE);
			case DEEP_ORE -> p.strength(4.5F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE).mapColor(MapColor.DEEPSLATE);
		}
		if (glow) p.lightLevel(state -> 7);
		Function<BlockBehaviour.Properties, Block> factory = switch (kind) {
			case LOG -> RotatedPillarBlock::new;
			case ORE, DEEP_ORE -> props -> new DropExperienceBlock(UniformInt.of(xpMin, xpMax), props);
			default -> Block::new;
		};
		Block block = Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(p));
		BLOCKS.put(id, block);

		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, ElementalWeaponsMod.id(id));
		Item item = Registry.register(BuiltInRegistries.ITEM, itemKey,
				new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
		ITEMS.put(id, item);
	}

	static void material(String id) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ElementalWeaponsMod.id(id));
		ITEMS.put(id, Registry.register(BuiltInRegistries.ITEM, key, new Item(new Item.Properties().setId(key))));
	}

	public static Item item(String id) {
		return ITEMS.get(id);
	}

	public static void initialize() {
		WorldBlockList.blocks();
		WorldBlockList.materials();
		CreativeModeTab tab = FabricCreativeModeTab.builder()
				.icon(() -> new ItemStack(ITEMS.get("storm_grass")))
				.title(Component.translatable("itemGroup.elementalweapons.world"))
				.displayItems((params, output) -> {
					for (Item it : ITEMS.values()) output.accept(it);
				})
				.build();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB_KEY, tab);
	}
}
