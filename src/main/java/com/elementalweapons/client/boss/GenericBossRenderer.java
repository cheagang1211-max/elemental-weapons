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
		state.bigLabel = label(entity) != null;
	}

	/** 가게 주인·현자 이름표 (멀리서도 보이게) */
	private static net.minecraft.network.chat.Component label(Mob e) {
		String t; int c;
		if (e instanceof com.elementalweapons.mob.Shopkeepers.Blacksmith) { t = "⚒ 대장장이"; c = 0xFFA040; }
		else if (e instanceof com.elementalweapons.mob.Shopkeepers.Enchanter) { t = "✦ 마법부여사"; c = 0xC890FF; }
		else if (e instanceof com.elementalweapons.mob.Shopkeepers.Alchemist) { t = "⚗ 연금술사"; c = 0x70F090; }
		else if (e instanceof com.elementalweapons.mob.ElementalMerchant) { t = "◆ 상인"; c = 0xFFDC40; }
		else if (e instanceof com.elementalweapons.mob.FateSage) { t = "✧ 운명의 현자"; c = 0x90E8FF; }
		else return null;
		return net.minecraft.network.chat.Component.literal(t).withStyle(st -> st.withColor(c).withBold(true));
	}

	@Override
	protected boolean shouldShowName(T entity, double distSq) {
		return label(entity) != null ? distSq < 64 * 64 : super.shouldShowName(entity, distSq);
	}

	@Override
	protected net.minecraft.network.chat.Component getNameTag(T entity) {
		var l = label(entity);
		return l != null ? l : super.getNameTag(entity);
	}

	@Override
	protected void submitNameDisplay(BossRenderState state, PoseStack pose, net.minecraft.client.renderer.SubmitNodeCollector nodes, net.minecraft.client.renderer.state.level.CameraRenderState cam) {
		if (!state.bigLabel || state.nameTagAttachment == null) { super.submitNameDisplay(state, pose, nodes, cam); return; }
		// 이름표 위치를 기준으로 1.7배 크게, 조금 위로
		var a = state.nameTagAttachment;
		pose.pushPose();
		pose.translate(a.x, a.y + 0.1, a.z);
		pose.scale(1.7F, 1.7F, 1.7F);
		pose.translate(-a.x, -a.y, -a.z);
		super.submitNameDisplay(state, pose, nodes, cam);
		pose.popPose();
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
