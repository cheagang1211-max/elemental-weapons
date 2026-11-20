// 자동 생성 파일 (tools/bossgen/bosses2.py)
package com.elementalweapons.client.boss;

import java.util.function.Function;

import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderers;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.boss.BossDef;
import com.elementalweapons.boss.BossDefs;
import com.elementalweapons.boss.ModEntities;
import com.elementalweapons.client.boss.gen.IgnisModel;
import com.elementalweapons.client.boss.gen.MarinaModel;
import com.elementalweapons.client.boss.gen.SilvaModel;
import com.elementalweapons.client.boss.gen.ZephyrosModel;
import com.elementalweapons.client.boss.gen.NoxModel;
import com.elementalweapons.client.boss.gen.LumielModel;
import com.elementalweapons.client.boss.gen.CrystalGolemModel;
import com.elementalweapons.client.boss.gen.LavaQueenModel;
import com.elementalweapons.client.boss.gen.FrostGiantModel;
import com.elementalweapons.client.boss.gen.SphinxModel;
import com.elementalweapons.client.boss.gen.SteamColossusModel;
import com.elementalweapons.client.boss.gen.StormWyvernModel;
import com.elementalweapons.client.boss.gen.TwilightTwinsModel;
import com.elementalweapons.client.boss.gen.HydraModel;
import com.elementalweapons.client.boss.gen.PrimaModel;
import com.elementalweapons.client.boss.gen.VoltraModel;

public final class GeneratedBossModels {
	private GeneratedBossModels() {}

	public static void register() {
		ModelLayerLocation layer;
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("ignis"), "main");
		ModelLayerRegistry.registerModelLayer(layer, IgnisModel::createBodyLayer);
		register("ignis", layer, IgnisModel::new);
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("marina"), "main");
		ModelLayerRegistry.registerModelLayer(layer, MarinaModel::createBodyLayer);
		register("marina", layer, MarinaModel::new);
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("silva"), "main");
		ModelLayerRegistry.registerModelLayer(layer, SilvaModel::createBodyLayer);
		register("silva", layer, SilvaModel::new);
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("zephyros"), "main");
		ModelLayerRegistry.registerModelLayer(layer, ZephyrosModel::createBodyLayer);
		register("zephyros", layer, ZephyrosModel::new);
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("nox"), "main");
		ModelLayerRegistry.registerModelLayer(layer, NoxModel::createBodyLayer);
		register("nox", layer, NoxModel::new);
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("lumiel"), "main");
		ModelLayerRegistry.registerModelLayer(layer, LumielModel::createBodyLayer);
		register("lumiel", layer, LumielModel::new);
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("crystal_golem"), "main");
		ModelLayerRegistry.registerModelLayer(layer, CrystalGolemModel::createBodyLayer);
		register("crystal_golem", layer, CrystalGolemModel::new);
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("lava_queen"), "main");
		ModelLayerRegistry.registerModelLayer(layer, LavaQueenModel::createBodyLayer);
		register("lava_queen", layer, LavaQueenModel::new);
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("frost_giant"), "main");
		ModelLayerRegistry.registerModelLayer(layer, FrostGiantModel::createBodyLayer);
		register("frost_giant", layer, FrostGiantModel::new);
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("sphinx"), "main");
		ModelLayerRegistry.registerModelLayer(layer, SphinxModel::createBodyLayer);
		register("sphinx", layer, SphinxModel::new);
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("steam_colossus"), "main");
		ModelLayerRegistry.registerModelLayer(layer, SteamColossusModel::createBodyLayer);
		register("steam_colossus", layer, SteamColossusModel::new);
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("storm_wyvern"), "main");
		ModelLayerRegistry.registerModelLayer(layer, StormWyvernModel::createBodyLayer);
		register("storm_wyvern", layer, StormWyvernModel::new);
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("twilight_twins"), "main");
		ModelLayerRegistry.registerModelLayer(layer, TwilightTwinsModel::createBodyLayer);
		register("twilight_twins", layer, TwilightTwinsModel::new);
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("hydra"), "main");
		ModelLayerRegistry.registerModelLayer(layer, HydraModel::createBodyLayer);
		register("hydra", layer, HydraModel::new);
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("prima"), "main");
		ModelLayerRegistry.registerModelLayer(layer, PrimaModel::createBodyLayer);
		register("prima", layer, PrimaModel::new);
		layer = new ModelLayerLocation(ElementalWeaponsMod.id("voltra"), "main");
		ModelLayerRegistry.registerModelLayer(layer, VoltraModel::createBodyLayer);
		registerVoltra(layer, VoltraModel::new);
	}

	private static void register(String id, ModelLayerLocation layer, Function<ModelPart, GenericBossModel> factory) {
		BossDef def = BossDefs.ALL.get(id);
		EntityRenderers.register(ModEntities.BOSSES.get(id),
				ctx -> new GenericBossRenderer<>(ctx, layer, factory, id, def.renderScale, def.shadow));
	}

	private static void registerVoltra(ModelLayerLocation layer, Function<ModelPart, GenericBossModel> factory) {
		EntityRenderers.register(ModEntities.VOLTRA, ctx -> new GenericBossRenderer<>(ctx, layer, factory, "voltra", 2.7F, 2.0F));
	}
}
