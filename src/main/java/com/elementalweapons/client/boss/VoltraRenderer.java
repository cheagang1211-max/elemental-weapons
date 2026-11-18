package com.elementalweapons.client.boss;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.boss.VoltraBoss;

public class VoltraRenderer extends MobRenderer<VoltraBoss, VoltraRenderState, VoltraModel> {
	private static final Identifier TEXTURE = ElementalWeaponsMod.id("textures/entity/voltra.png");
	/** 어둠 속에서도 빛나는 부분 (눈, 번개 문양, 후광) */
	private static final Identifier GLOW = ElementalWeaponsMod.id("textures/entity/voltra_glow.png");
	/** 모델 크기 배율 (모델 높이 약 2.6블록 x 2 = 약 5.2블록) */
	private static final float SCALE = 2.0F;

	public VoltraRenderer(EntityRendererProvider.Context context) {
		super(context, new VoltraModel(context.bakeLayer(ModModelLayers.VOLTRA)), 1.6F);
		this.addLayer(new EyesLayer<VoltraRenderState, VoltraModel>(this) {
			@Override
			public RenderType renderType() {
				return RenderTypes.eyes(GLOW);
			}
		});
	}

	@Override
	public VoltraRenderState createRenderState() {
		return new VoltraRenderState();
	}

	@Override
	public void extractRenderState(VoltraBoss entity, VoltraRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		state.attackPose = entity.getAttackPose();
	}

	@Override
	public Identifier getTextureLocation(VoltraRenderState state) {
		return TEXTURE;
	}

	@Override
	protected void scale(VoltraRenderState state, PoseStack poseStack) {
		poseStack.scale(SCALE, SCALE, SCALE);
	}
}
