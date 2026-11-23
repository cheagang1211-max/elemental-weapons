// 자동 생성 파일 (tools/bossgen/mobs2.py)
package com.elementalweapons.client.boss;

import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.mob.ModMobs;
import com.elementalweapons.client.boss.gen.SparkWispModel;
import com.elementalweapons.client.boss.gen.MagmaBruteModel;
import com.elementalweapons.client.boss.gen.TideSpiritModel;
import com.elementalweapons.client.boss.gen.ThornBeastModel;
import com.elementalweapons.client.boss.gen.GaleHawkModel;
import com.elementalweapons.client.boss.gen.ShadowStalkerModel;
import com.elementalweapons.client.boss.gen.LightSentinelModel;
import com.elementalweapons.client.boss.gen.FluffleModel;
import com.elementalweapons.client.boss.gen.HornedDeerModel;
import com.elementalweapons.client.boss.gen.ElementalMerchantModel;
import com.elementalweapons.client.boss.gen.FateSageModel;
import com.elementalweapons.client.boss.gen.BlacksmithModel;
import com.elementalweapons.client.boss.gen.EnchanterModel;
import com.elementalweapons.client.boss.gen.AlchemistModel;
import com.elementalweapons.client.boss.gen.SkyDragonModel;

public final class GeneratedMobModels {
	private GeneratedMobModels() {}

	public static void register() {
		ModelLayerLocation layer;
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("spark_wisp"), "main");
		ModelLayerRegistry.registerModelLayer(layer, SparkWispModel::createBodyLayer);
		final ModelLayerLocation l_spark_wisp = layer;
		EntityRenderers.register(ModMobs.MOBS.get("spark_wisp"), ctx -> new GenericBossRenderer<>(ctx, l_spark_wisp, SparkWispModel::new, "spark_wisp", 0.5F, 0.5F));
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("magma_brute"), "main");
		ModelLayerRegistry.registerModelLayer(layer, MagmaBruteModel::createBodyLayer);
		final ModelLayerLocation l_magma_brute = layer;
		EntityRenderers.register(ModMobs.MOBS.get("magma_brute"), ctx -> new GenericBossRenderer<>(ctx, l_magma_brute, MagmaBruteModel::new, "magma_brute", 0.6F, 0.5F));
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("tide_spirit"), "main");
		ModelLayerRegistry.registerModelLayer(layer, TideSpiritModel::createBodyLayer);
		final ModelLayerLocation l_tide_spirit = layer;
		EntityRenderers.register(ModMobs.MOBS.get("tide_spirit"), ctx -> new GenericBossRenderer<>(ctx, l_tide_spirit, TideSpiritModel::new, "tide_spirit", 0.5F, 0.5F));
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("thorn_beast"), "main");
		ModelLayerRegistry.registerModelLayer(layer, ThornBeastModel::createBodyLayer);
		final ModelLayerLocation l_thorn_beast = layer;
		EntityRenderers.register(ModMobs.MOBS.get("thorn_beast"), ctx -> new GenericBossRenderer<>(ctx, l_thorn_beast, ThornBeastModel::new, "thorn_beast", 0.5F, 0.5F));
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("gale_hawk"), "main");
		ModelLayerRegistry.registerModelLayer(layer, GaleHawkModel::createBodyLayer);
		final ModelLayerLocation l_gale_hawk = layer;
		EntityRenderers.register(ModMobs.MOBS.get("gale_hawk"), ctx -> new GenericBossRenderer<>(ctx, l_gale_hawk, GaleHawkModel::new, "gale_hawk", 0.5F, 0.5F));
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("shadow_stalker"), "main");
		ModelLayerRegistry.registerModelLayer(layer, ShadowStalkerModel::createBodyLayer);
		final ModelLayerLocation l_shadow_stalker = layer;
		EntityRenderers.register(ModMobs.MOBS.get("shadow_stalker"), ctx -> new GenericBossRenderer<>(ctx, l_shadow_stalker, ShadowStalkerModel::new, "shadow_stalker", 0.5F, 0.5F));
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("light_sentinel"), "main");
		ModelLayerRegistry.registerModelLayer(layer, LightSentinelModel::createBodyLayer);
		final ModelLayerLocation l_light_sentinel = layer;
		EntityRenderers.register(ModMobs.MOBS.get("light_sentinel"), ctx -> new GenericBossRenderer<>(ctx, l_light_sentinel, LightSentinelModel::new, "light_sentinel", 0.5F, 0.5F));
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("fluffle"), "main");
		ModelLayerRegistry.registerModelLayer(layer, FluffleModel::createBodyLayer);
		final ModelLayerLocation l_fluffle = layer;
		EntityRenderers.register(ModMobs.FLUFFLE, ctx -> new GenericBossRenderer<>(ctx, l_fluffle, FluffleModel::new, "fluffle", 0.5F, 0.5F));
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("horned_deer"), "main");
		ModelLayerRegistry.registerModelLayer(layer, HornedDeerModel::createBodyLayer);
		final ModelLayerLocation l_horned_deer = layer;
		EntityRenderers.register(ModMobs.HORNED_DEER, ctx -> new GenericBossRenderer<>(ctx, l_horned_deer, HornedDeerModel::new, "horned_deer", 0.5F, 0.5F));
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("elemental_merchant"), "main");
		ModelLayerRegistry.registerModelLayer(layer, ElementalMerchantModel::createBodyLayer);
		final ModelLayerLocation l_elemental_merchant = layer;
		EntityRenderers.register(ModMobs.MERCHANT, ctx -> new GenericBossRenderer<>(ctx, l_elemental_merchant, ElementalMerchantModel::new, "elemental_merchant", 0.5F, 0.5F));
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("fate_sage"), "main");
		ModelLayerRegistry.registerModelLayer(layer, FateSageModel::createBodyLayer);
		final ModelLayerLocation l_fate_sage = layer;
		EntityRenderers.register(ModMobs.SAGE, ctx -> new GenericBossRenderer<>(ctx, l_fate_sage, FateSageModel::new, "fate_sage", 0.5F, 0.5F));
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("blacksmith"), "main");
		ModelLayerRegistry.registerModelLayer(layer, BlacksmithModel::createBodyLayer);
		final ModelLayerLocation l_blacksmith = layer;
		EntityRenderers.register(ModMobs.BLACKSMITH, ctx -> new GenericBossRenderer<>(ctx, l_blacksmith, BlacksmithModel::new, "blacksmith", 0.5F, 0.5F));
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("enchanter"), "main");
		ModelLayerRegistry.registerModelLayer(layer, EnchanterModel::createBodyLayer);
		final ModelLayerLocation l_enchanter = layer;
		EntityRenderers.register(ModMobs.ENCHANTER, ctx -> new GenericBossRenderer<>(ctx, l_enchanter, EnchanterModel::new, "enchanter", 0.5F, 0.5F));
		EntityRenderers.register(ModMobs.INSTRUCTOR, ctx -> new GenericBossRenderer<>(ctx, l_enchanter, EnchanterModel::new, "enchanter", 0.5F, 0.5F));
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("alchemist"), "main");
		ModelLayerRegistry.registerModelLayer(layer, AlchemistModel::createBodyLayer);
		final ModelLayerLocation l_alchemist = layer;
		EntityRenderers.register(ModMobs.ALCHEMIST, ctx -> new GenericBossRenderer<>(ctx, l_alchemist, AlchemistModel::new, "alchemist", 0.5F, 0.5F));
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("sky_dragon"), "main");
		ModelLayerRegistry.registerModelLayer(layer, SkyDragonModel::createBodyLayer);
		final ModelLayerLocation l_sky_dragon = layer;
		EntityRenderers.register(ModMobs.SKY_DRAGON, ctx -> new GenericBossRenderer<>(ctx, l_sky_dragon, SkyDragonModel::new, "sky_dragon", 1.0F, 0.5F));
	}
}
