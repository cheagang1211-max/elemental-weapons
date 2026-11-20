package com.elementalweapons.boss;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import com.elementalweapons.ElementalWeaponsMod;

/** 보스/몹 등록 */
public final class ModEntities {
	private ModEntities() {}

	public static final EntityType<VoltraBoss> VOLTRA = register("voltra",
			EntityType.Builder.<VoltraBoss>of(VoltraBoss::new, MobCategory.MONSTER)
					.sized(3.0F, 6.4F)
					.eyeHeight(5.4F)
					.fireImmune()
					.clientTrackingRange(10)
					.noLootTable(),
			VoltraBoss.createAttributes());

	/** 설정 기반 보스 15종 (아이디 → 엔티티 타입) */
	public static final java.util.Map<String, EntityType<ConfiguredBoss>> BOSSES = new java.util.LinkedHashMap<>();

	static {
		for (BossDef def : BossDefs.ALL.values()) {
			EntityType<ConfiguredBoss> type = register(def.id,
					EntityType.Builder.<ConfiguredBoss>of(ConfiguredBoss::new, MobCategory.MONSTER)
							.sized(def.width, def.height)
							.eyeHeight(def.eyeHeight)
							.fireImmune()
							.clientTrackingRange(10)
							.noLootTable(),
					ConfiguredBoss.createAttributes());
			BossDefs.BY_TYPE.put(type, def);
			BOSSES.put(def.id, type);
		}
	}

	private static <T extends LivingEntity> EntityType<T> register(String name, EntityType.Builder<T> builder,
			AttributeSupplier.Builder attributes) {
		ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, ElementalWeaponsMod.id(name));
		EntityType<T> type = Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
		FabricDefaultAttributeRegistry.register(type, attributes);
		return type;
	}

	public static void initialize() {
		// 클래스 로딩으로 위 필드들이 등록됨
	}
}
