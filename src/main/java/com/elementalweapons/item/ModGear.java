package com.elementalweapons.item;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import com.elementalweapons.ElementalWeaponsMod;

/**
 * 새 도구·갑옷 2등급
 *  - 에테르 (철 등급보다 조금 좋음): 에테르 주괴
 *  - 오리하르콘 (네더라이트보다 좋음): 오리하르콘 주괴
 */
public final class ModGear {
	private ModGear() {}

	public static final Map<String, Item> ITEMS = new LinkedHashMap<>();

	private static TagKey<Item> tag(String name) {
		return TagKey.create(Registries.ITEM, ElementalWeaponsMod.id(name));
	}

	public static final TagKey<Item> AETHER_REPAIR = tag("aether_tool_materials");
	public static final TagKey<Item> ORICHALCUM_REPAIR = tag("orichalcum_tool_materials");

	public static final ToolMaterial AETHER_TOOL = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 450, 6.5F, 2.5F, 16, AETHER_REPAIR);
	public static final ToolMaterial ORICHALCUM_TOOL = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2600, 10.0F, 5.0F, 18, ORICHALCUM_REPAIR);

	private static ResourceKey<EquipmentAsset> asset(String name) {
		return ResourceKey.create(EquipmentAssets.ROOT_ID, ElementalWeaponsMod.id(name));
	}

	private static Map<ArmorType, Integer> defense(int boots, int legs, int chest, int helmet, int body) {
		Map<ArmorType, Integer> m = new EnumMap<>(ArmorType.class);
		m.put(ArmorType.BOOTS, boots);
		m.put(ArmorType.LEGGINGS, legs);
		m.put(ArmorType.CHESTPLATE, chest);
		m.put(ArmorType.HELMET, helmet);
		m.put(ArmorType.BODY, body);
		return m;
	}

	public static final ArmorMaterial AETHER_ARMOR = new ArmorMaterial(20, defense(2, 6, 7, 2, 6), 12,
			SoundEvents.ARMOR_EQUIP_IRON, 1.0F, 0.0F, AETHER_REPAIR, asset("aether"));
	public static final ArmorMaterial ORICHALCUM_ARMOR = new ArmorMaterial(45, defense(3, 7, 9, 4, 12), 18,
			SoundEvents.ARMOR_EQUIP_NETHERITE, 4.0F, 0.15F, ORICHALCUM_REPAIR, asset("orichalcum"));

	private static void add(String id, UnaryOperator<Item.Properties> props) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ElementalWeaponsMod.id(id));
		ITEMS.put(id, Registry.register(BuiltInRegistries.ITEM, key, new Item(props.apply(new Item.Properties()).setId(key))));
	}

	private static void tier(String name, ToolMaterial tool, ArmorMaterial armor, boolean fireproof) {
		UnaryOperator<Item.Properties> base = p -> fireproof ? p.fireResistant().rarity(Rarity.RARE) : p;
		add(name + "_sword", p -> base.apply(p).sword(tool, 3.0F, -2.4F));
		add(name + "_pickaxe", p -> base.apply(p).pickaxe(tool, 1.0F, -2.8F));
		add(name + "_axe", p -> base.apply(p).axe(tool, 5.5F, -3.0F));
		add(name + "_shovel", p -> base.apply(p).shovel(tool, 1.5F, -3.0F));
		add(name + "_hoe", p -> base.apply(p).hoe(tool, -2.0F, -1.0F));
		add(name + "_helmet", p -> base.apply(p).humanoidArmor(armor, ArmorType.HELMET));
		add(name + "_chestplate", p -> base.apply(p).humanoidArmor(armor, ArmorType.CHESTPLATE));
		add(name + "_leggings", p -> base.apply(p).humanoidArmor(armor, ArmorType.LEGGINGS));
		add(name + "_boots", p -> base.apply(p).humanoidArmor(armor, ArmorType.BOOTS));
	}

	public static void initialize() {
		tier("aether", AETHER_TOOL, AETHER_ARMOR, false);
		tier("orichalcum", ORICHALCUM_TOOL, ORICHALCUM_ARMOR, true);
	}
}
