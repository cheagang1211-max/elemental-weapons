package com.elementalweapons.mob;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.item.Element;

import static com.elementalweapons.mob.MobDef.Kind.*;

/** 일반 몹 7종 + 동물 2종 + 원소족 상인 등록 */
public final class ModMobs {
	private ModMobs() {}

	public static final List<MobDef> HOSTILE = List.of(
			new MobDef("spark_wisp", "스파크 정령", Element.ELECTRIC, FLYING, 22, 4, 0.30, 0.8F, 1.2F, 1.0F, "thunder_plateau"),
			new MobDef("magma_brute", "용암 거한", Element.FIRE, MELEE, 40, 7, 0.24, 1.2F, 2.2F, 1.2F, "volcanic_plains"),
			new MobDef("tide_spirit", "물의 정령", Element.WATER, RANGED, 26, 4, 0.26, 0.8F, 1.9F, 1.0F, "azure_forest"),
			new MobDef("thorn_beast", "가시 덩굴괴물", Element.GRASS, MELEE, 34, 5, 0.27, 1.0F, 1.6F, 1.0F, "blossom_forest"),
			new MobDef("gale_hawk", "질풍 매", Element.WIND, FLYING, 20, 5, 0.32, 1.0F, 0.9F, 1.0F, "gale_highlands"),
			new MobDef("shadow_stalker", "그림자 추적자", Element.DARK, STALKER, 30, 6, 0.30, 0.7F, 2.2F, 1.0F, "shadow_woods"),
			new MobDef("light_sentinel", "빛의 파수꾼", Element.LIGHT, RANGED, 30, 5, 0.25, 0.8F, 2.0F, 1.0F, "radiant_plains"));

	public static final Map<String, EntityType<ElementalMob>> MOBS = new LinkedHashMap<>();
	public static EntityType<ElementalCreature> FLUFFLE;
	public static EntityType<ElementalCreature> HORNED_DEER;
	public static EntityType<ElementalMerchant> MERCHANT;
	public static EntityType<FateSage> SAGE;
	public static EntityType<Shopkeepers.Blacksmith> BLACKSMITH;
	public static EntityType<Shopkeepers.Enchanter> ENCHANTER;
	public static EntityType<Shopkeepers.Instructor> INSTRUCTOR;
	public static EntityType<com.elementalweapons.spell.HeavenVisual> HEAVEN_GATE, ARCHANGEL;
	public static EntityType<com.elementalweapons.spell.GrandVisual> GRAND;
	public static EntityType<Shopkeepers.Alchemist> ALCHEMIST;
	public static EntityType<SkyDragon> SKY_DRAGON;

	public static Item ELEMENTAL_MEAT, COOKED_ELEMENTAL_MEAT, FLUFF;

	private static <T extends Mob> EntityType<T> register(String id, EntityType.Builder<T> builder) {
		ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, ElementalWeaponsMod.id(id));
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
	}

	private static Item item(String id, Item.Properties props) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, ElementalWeaponsMod.id(id));
		return Registry.register(BuiltInRegistries.ITEM, key, new Item(props.setId(key)));
	}

	public static void initialize() {
		for (MobDef d : HOSTILE) {
			EntityType<ElementalMob> type = register(d.id(), FabricEntityType.Builder.<ElementalMob>createMob(ElementalMob::new, MobCategory.MONSTER,
					mob -> mob.spawnPlacement(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules)
							.defaultAttributes(() -> ElementalMob.attributes(d)))
					.sized(d.width(), d.height()).clientTrackingRange(8));
			ElementalMob.DEFS.put(type, d);
			MOBS.put(d.id(), type);
		}
		FLUFFLE = register("fluffle", FabricEntityType.Builder.<ElementalCreature>createMob(ElementalCreature::new, MobCategory.CREATURE,
				mob -> mob.spawnPlacement(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules)
						.defaultAttributes(() -> ElementalCreature.attributes(8, 0.25)))
				.sized(0.8F, 0.8F).clientTrackingRange(8));
		HORNED_DEER = register("horned_deer", FabricEntityType.Builder.<ElementalCreature>createMob(ElementalCreature::new, MobCategory.CREATURE,
				mob -> mob.spawnPlacement(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules)
						.defaultAttributes(() -> ElementalCreature.attributes(14, 0.28)))
				.sized(1.0F, 1.6F).clientTrackingRange(10));
		MERCHANT = register("elemental_merchant", FabricEntityType.Builder.<ElementalMerchant>createMob(ElementalMerchant::new, MobCategory.CREATURE,
				mob -> mob.spawnPlacement(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules)
						.defaultAttributes(ElementalMerchant::attributes))
				.sized(0.6F, 1.95F).clientTrackingRange(10));

		SAGE = register("fate_sage", FabricEntityType.Builder.<FateSage>createMob(FateSage::new, MobCategory.MISC,
				mob -> mob.defaultAttributes(FateSage::attributes))
				.sized(0.7F, 2.1F).clientTrackingRange(10));

		BLACKSMITH = register("blacksmith", FabricEntityType.Builder.<Shopkeepers.Blacksmith>createMob(Shopkeepers.Blacksmith::new, MobCategory.MISC,
				mob -> mob.defaultAttributes(ElementalMerchant::attributes)).sized(0.7F, 2.0F).clientTrackingRange(10));
		ENCHANTER = register("enchanter", FabricEntityType.Builder.<Shopkeepers.Enchanter>createMob(Shopkeepers.Enchanter::new, MobCategory.MISC,
				mob -> mob.defaultAttributes(ElementalMerchant::attributes)).sized(0.6F, 2.1F).clientTrackingRange(10));
		HEAVEN_GATE = register("heaven_gate", FabricEntityType.Builder.<com.elementalweapons.spell.HeavenVisual>createMob(com.elementalweapons.spell.HeavenVisual::new, MobCategory.MISC,
				mob -> mob.defaultAttributes(com.elementalweapons.spell.HeavenVisual::attributes)).sized(1F, 1F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune());
		ARCHANGEL = register("archangel", FabricEntityType.Builder.<com.elementalweapons.spell.HeavenVisual>createMob(com.elementalweapons.spell.HeavenVisual::new, MobCategory.MISC,
				mob -> mob.defaultAttributes(com.elementalweapons.spell.HeavenVisual::attributes)).sized(1F, 1F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune());
		GRAND = register("grand_visual", FabricEntityType.Builder.<com.elementalweapons.spell.GrandVisual>createMob(com.elementalweapons.spell.GrandVisual::new, MobCategory.MISC,
				mob -> mob.defaultAttributes(com.elementalweapons.spell.HeavenVisual::attributes)).sized(1F, 1F).clientTrackingRange(16).updateInterval(1).noSave().fireImmune());
		INSTRUCTOR = register("job_instructor", FabricEntityType.Builder.<Shopkeepers.Instructor>createMob(Shopkeepers.Instructor::new, MobCategory.MISC,
				mob -> mob.defaultAttributes(ElementalMerchant::attributes)).sized(0.6F, 2.1F).clientTrackingRange(10));
		ALCHEMIST = register("alchemist", FabricEntityType.Builder.<Shopkeepers.Alchemist>createMob(Shopkeepers.Alchemist::new, MobCategory.MISC,
				mob -> mob.defaultAttributes(ElementalMerchant::attributes)).sized(0.6F, 1.95F).clientTrackingRange(10));
		SKY_DRAGON = register("sky_dragon", FabricEntityType.Builder.<SkyDragon>createMob(SkyDragon::new, MobCategory.MISC,
				mob -> mob.defaultAttributes(SkyDragon::attributes)).sized(2.6F, 2.4F).clientTrackingRange(16));

		ELEMENTAL_MEAT = item("elemental_meat", new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).build()));
		COOKED_ELEMENTAL_MEAT = item("cooked_elemental_meat", new Item.Properties().food(new FoodProperties.Builder().nutrition(8).saturationModifier(0.8F).build()));
		FLUFF = item("fluff", new Item.Properties());
	}
}
