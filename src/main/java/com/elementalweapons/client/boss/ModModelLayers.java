package com.elementalweapons.client.boss;

import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.boss.ModEntities;

/** 보스 모델/렌더러 등록 (클라이언트) */
public final class ModModelLayers {
	private ModModelLayers() {}

	public static final ModelLayerLocation VOLTRA = new ModelLayerLocation(ElementalWeaponsMod.id("voltra"), "main");

	public static void initialize() {
		ModelLayerRegistry.registerModelLayer(VOLTRA, VoltraModel::createBodyLayer);
		EntityRenderers.register(ModEntities.VOLTRA, VoltraRenderer::new);
	}
}
