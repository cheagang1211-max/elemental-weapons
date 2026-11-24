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
import com.elementalweapons.item.PlayerClass;

/** 직업 복장을 갑옷 위에 덧그림 (직업×속성 색) */
public class ClassDecoLayer<S extends HumanoidRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
	private static final String[] EL = {"electric", "fire", "water", "grass", "wind", "dark", "light", "neutral"};
	private final ClassDecoModel[] models = new ClassDecoModel[PlayerClass.values().length];
	private final Identifier[][] tex = new Identifier[PlayerClass.values().length][EL.length], glow = new Identifier[PlayerClass.values().length][EL.length];

	public ClassDecoLayer(RenderLayerParent<S, M> parent, java.util.function.IntFunction<ModelPart> baker) {
		super(parent);
		for (int i = 0; i < ClassArmorMesh.CLASSES.length; i++) {
			int cls = PlayerClass.valueOf(ClassArmorMesh.CLASSES[i].toUpperCase()).ordinal();
			models[cls] = new ClassDecoModel(baker.apply(i), i);
		}
		for (PlayerClass c : PlayerClass.values()) {
			if (c == PlayerClass.NONE) continue;
			for (int e = 0; e < EL.length; e++) {
				String n = c.name().toLowerCase() + "_" + EL[e];
				tex[c.ordinal()][e] = ElementalWeaponsMod.id("textures/entity/armor_deco/class/" + n + ".png");
				glow[c.ordinal()][e] = ElementalWeaponsMod.id("textures/entity/armor_deco/class/" + n + "_glow.png");
			}
		}
	}

	@Override
	public void submit(PoseStack ps, SubmitNodeCollector c, int light, S s, float yRot, float xRot) {
		if (s.isInvisible) return;
		int cls = ClassDecoModel.classOf(s);
		if (cls <= 0 || models[cls] == null) return;
		ClassDecoModel model = models[cls];
		int el = ArmorDecoModel.elementOf(s);
		if (el < 0) el = 7;
		c.submitModel(model, s, ps, RenderTypes.armorCutoutNoCull(tex[cls][el]), light, OverlayTexture.NO_OVERLAY, 0);
		c.submitModel(model, s, ps, RenderTypes.eyes(glow[cls][el]), light, OverlayTexture.NO_OVERLAY, 0);
	}
}
