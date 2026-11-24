package com.elementalweapons.client.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import com.elementalweapons.ElementalWeaponsMod;

/** 갑옷 위에 입체 판금·가시·발광 장식을 덧그림 */
public class ArmorDecoLayer<S extends HumanoidRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
	private static final String[] TIERS = {"aether", "orichalcum"};
	private final ArmorDecoModel[] models = new ArmorDecoModel[TIERS.length];
	private final Identifier[] tex = new Identifier[TIERS.length], glow = new Identifier[TIERS.length];
	private final Identifier[][] elTex = new Identifier[TIERS.length][7], elGlow = new Identifier[TIERS.length][7];

	public ArmorDecoLayer(RenderLayerParent<S, M> parent, java.util.function.Supplier<ModelPart> baker) {
		super(parent);
		for (int i = 0; i < TIERS.length; i++) {
			models[i] = new ArmorDecoModel(baker.get(), TIERS[i]);
			tex[i] = ElementalWeaponsMod.id("textures/entity/armor_deco/" + TIERS[i] + ".png");
			glow[i] = ElementalWeaponsMod.id("textures/entity/armor_deco/" + TIERS[i] + "_glow.png");
			for (int e = 0; e < 7; e++) {
				elTex[i][e] = ElementalWeaponsMod.id("textures/entity/armor_deco/" + TIERS[i] + "_" + ElementArmorParts.IDS[e] + ".png");
				elGlow[i][e] = ElementalWeaponsMod.id("textures/entity/armor_deco/" + TIERS[i] + "_" + ElementArmorParts.IDS[e] + "_glow.png");
			}
		}
	}

	@Override
	public void submit(PoseStack ps, SubmitNodeCollector c, int light, S s, float yRot, float xRot) {
		if (s.isInvisible) return;
		for (int i = 0; i < TIERS.length; i++) {
			if (!models[i].wears(s)) continue;
			int el = ArmorDecoModel.elementOf(s);
			Identifier t = el < 0 ? tex[i] : elTex[i][el], g = el < 0 ? glow[i] : elGlow[i][el];
			c.submitModel(models[i], s, ps, RenderTypes.armorCutoutNoCull(t), light, OverlayTexture.NO_OVERLAY, 0);
			c.submitModel(models[i], s, ps, RenderTypes.eyes(g), light, OverlayTexture.NO_OVERLAY, 0);
		}
	}
}
