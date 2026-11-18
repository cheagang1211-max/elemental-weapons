package com.elementalweapons.client.boss;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import com.elementalweapons.ElementalWeaponsMod;
import net.minecraft.world.entity.Mob;

import com.elementalweapons.boss.PosedEntity;

/** 보스/몹/상인 공통 렌더러 (텍스처 + 발광 레이어 + 크기 배율) */
public class GenericBossRenderer<T extends Mob & PosedEntity> extends MobRenderer<T, BossRenderState, GenericBossModel> {
	private final Identifier texture;
	private final Identifier glow;
	private final float scale;

	public GenericBossRenderer(EntityRendererProvider.Context ctx, ModelLayerLocation layer,
			Function<ModelPart, GenericBossModel> factory, String id, float scale, float shadow) {
		super(ctx, factory.apply(ctx.bakeLayer(layer)), shadow);
		this.texture = ElementalWeaponsMod.id("textures/entity/" + id + ".png");
		this.glow = ElementalWeaponsMod.id("textures/entity/" + id + "_glow.png");
		this.scale = scale;
		final Identifier glowTex = this.glow;
		this.addLayer(new EyesLayer<BossRenderState, GenericBossModel>(this) {
			@Override
			public RenderType renderType() {
				return RenderTypes.eyes(glowTex);
			}
		});
	}

	@Override
	public BossRenderState createRenderState() {
		return new BossRenderState();
	}

	@Override
	public void extractRenderState(T entity, BossRenderState state, float partialTick) {
		super.extractRenderState(entity, state, partialTick);
		state.attackPose = entity.getAttackPose();
		state.floating = entity.isFloatingBoss();
	}

	@Override
	public Identifier getTextureLocation(BossRenderState state) {
		return texture;
	}

	@Override
	protected void scale(BossRenderState state, PoseStack poseStack) {
		poseStack.scale(scale, scale, scale);
	}
}
