package com.elementalweapons.client.armor;

import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.RenderLayerParent;

import com.elementalweapons.ElementalWeaponsMod;

public final class ModArmorDeco {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(ElementalWeaponsMod.id("armor_deco"), "main");

	private ModArmorDeco() {}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void initialize() {
		ModelLayerRegistry.registerModelLayer(LAYER, ArmorDecoMesh::create);
		LivingEntityRenderLayerRegistrationCallback.EVENT.register((type, renderer, helper, ctx) -> {
			if (renderer.getModel() instanceof HumanoidModel) {
				helper.register(new ArmorDecoLayer((RenderLayerParent) renderer, (java.util.function.Supplier<net.minecraft.client.model.geom.ModelPart>) () -> ctx.bakeLayer(LAYER)));
			}
		});
	}
}
