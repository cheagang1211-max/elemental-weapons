package com.elementalweapons.item;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.boss.ModEntities;

public class ModItems {

	// ===== 무기 재질 (모든 원소 무기 공통) =====
	public static final ToolMaterial ELEMENTAL_MATERIAL = new ToolMaterial(
			BlockTags.INCORRECT_FOR_DIAMOND_TOOL, // 다이아 도구급
			2000,  // 내구도 (다이아 = 1561)
			8.0F,  // 채굴 속도
			4.0F,  // 추가 공격력 (다이아 = 3.0)
			15,    // 마법부여 잘 붙는 정도
			ItemTags.DIAMOND_TOOL_MATERIALS // 다이아몬드로 수리
	);

	/** WEAPONS.get(속성).get(종류) 로 꺼내 쓰세요 */
	public static final Map<Element, Map<WeaponType, Item>> WEAPONS = new EnumMap<>(Element.class);

	public static final ResourceKey<CreativeModeTab> TAB_KEY =
			ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ElementalWeaponsMod.id("weapons"));

	static {
		// 7속성 x 4종류 = 28개 자동 등록 (아이디 예: fire_greatsword)
		for (Element element : Element.values()) {
			Map<WeaponType, Item> byType = new EnumMap<>(WeaponType.class);
			for (WeaponType type : WeaponType.values()) {
				Item.Properties props = stats(type, new Item.Properties().rarity(Rarity.EPIC).fireResistant());
				Item item = register(key(element.id + "_" + type.id),
						p -> new ElementalWeaponItem(p, element, type), props);
				byType.put(type, item);
			}
			WEAPONS.put(element, byType);
		}
	}

	// ===== 보스 관련 아이템 =====
	/** 뇌신의 핵: 볼트라 처치 보상. 전기 속성 무기 제작 재료 */
	public static final Item VOLTRA_CORE = register(key("voltra_core"), Item::new,
			new Item.Properties().rarity(Rarity.EPIC).fireResistant());
	/** 뇌신의 소환석: 우클릭하면 볼트라 소환 */
	public static final Item VOLTRA_SUMMON = register(key("voltra_summon"),
			p -> new BossSummonItem(p, () -> ModEntities.VOLTRA),
			new Item.Properties().rarity(Rarity.EPIC).stacksTo(16));

	/** 원소 정수: 혼합 속성/최종 보스 보상. 무기 강화 재료 */
	public static final Item ELEMENT_ESSENCE = register(key("element_essence"), Item::new,
			new Item.Properties().rarity(Rarity.EPIC).fireResistant());

	/** 보스별 핵 / 소환석 (아이디 → 아이템). 볼트라는 위의 VOLTRA_CORE / VOLTRA_SUMMON */
	public static final Map<String, Item> BOSS_CORES = new java.util.LinkedHashMap<>();
	public static final Map<String, Item> BOSS_SUMMONS = new java.util.LinkedHashMap<>();

	static {
		for (com.elementalweapons.boss.BossDef def : com.elementalweapons.boss.BossDefs.ALL.values()) {
			String id = def.id;
			BOSS_CORES.put(id, register(key(id + "_core"), Item::new,
					new Item.Properties().rarity(Rarity.EPIC).fireResistant()));
			BOSS_SUMMONS.put(id, register(key(id + "_summon"),
					p -> new BossSummonItem(p, () -> ModEntities.BOSSES.get(id)),
					new Item.Properties().rarity(Rarity.EPIC).stacksTo(16)));
		}
	}

	/** 무기 종류별 능력치 (공격력, 공격 속도) */
	private static Item.Properties stats(WeaponType type, Item.Properties props) {
		return switch (type) {
			case DAGGER -> props.sword(ELEMENTAL_MATERIAL, 1.0F, -1.6F);     // 약하지만 빠름
			case SWORD -> props.sword(ELEMENTAL_MATERIAL, 3.0F, -2.4F);      // 바닐라 검과 같은 비율
			case GREATSWORD -> props.sword(ELEMENTAL_MATERIAL, 7.0F, -3.0F); // 강하지만 느림
			case STAFF -> props.stacksTo(1);                                  // 마법 전용
		};
	}

	public static Item get(Element element, WeaponType type) {
		return WEAPONS.get(element).get(type);
	}

	private static ResourceKey<Item> key(String name) {
		return ResourceKey.create(Registries.ITEM, ElementalWeaponsMod.id(name));
	}

	public static Item register(ResourceKey<Item> itemKey, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
		Item item = itemFactory.apply(settings.setId(itemKey));
		Registry.register(BuiltInRegistries.ITEM, itemKey, item);
		return item;
	}

	public static void initialize() {
		// 전용 크리에이티브 탭 "원소 무기"
		CreativeModeTab tab = FabricCreativeModeTab.builder()
				.icon(() -> new ItemStack(get(Element.LIGHT, WeaponType.STAFF)))
				.title(Component.translatable("itemGroup.elementalweapons.weapons"))
				.displayItems((params, output) -> {
					output.accept(VOLTRA_SUMMON);
					for (Item it : BOSS_SUMMONS.values()) output.accept(it);
					output.accept(VOLTRA_CORE);
					for (Item it : BOSS_CORES.values()) output.accept(it);
					output.accept(ELEMENT_ESSENCE);
					for (Element element : Element.values()) {
						for (WeaponType type : WeaponType.values()) {
							output.accept(get(element, type));
						}
					}
				})
				.build();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB_KEY, tab);
	}
}
