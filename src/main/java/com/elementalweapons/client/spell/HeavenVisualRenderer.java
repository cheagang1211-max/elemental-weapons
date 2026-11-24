package com.elementalweapons.client.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.spell.HeavenVisual;

/** 천상의 원환문 / 대천사 렌더러: 항상 밝게, 멀리서도 보이게, 발광 레이어 */
public class HeavenVisualRenderer extends MobRenderer<HeavenVisual, HeavenModels.State, EntityModel<HeavenModels.State>> {
	private final Identifier texture;
	private final float scale;
	private final float tilt;

	public HeavenVisualRenderer(EntityRendererProvider.Context ctx, EntityModel<HeavenModels.State> model, String id, float scale) {
		super(ctx, model, 0);
		this.texture = ElementalWeaponsMod.id("textures/entity/" + id + ".png");
		this.scale = scale;
		this.tilt = id.equals("heaven_gate") ? -32F : 0F;
		final Identifier glow = ElementalWeaponsMod.id("textures/entity/" + id + "_glow.png");
		this.addLayer(new EyesLayer<HeavenModels.State, EntityModel<HeavenModels.State>>(this) {
			@Override
			public RenderType renderType() { return RenderTypes.eyes(glow); }
		});
	}

	@Override
	public HeavenModels.State createRenderState() { return new HeavenModels.State(); }

	@Override
	public void extractRenderState(HeavenVisual e, HeavenModels.State s, float pt) {
		super.extractRenderState(e, s, pt);
		s.open = e.open();
		s.strikeAge = e.tickCount - e.strike() + pt;
		if (e.strike() <= 0) s.strikeAge = 99;
		s.time = e.tickCount + pt;
	}

	@Override
	protected boolean affectedByCulling(HeavenVisual e) { return false; }

	@Override
	protected int getBlockLightLevel(HeavenVisual e, BlockPos pos) { return 15; }

	@Override
	protected boolean shouldShowName(HeavenVisual e, double d) { return false; }

	@Override
	public Identifier getTextureLocation(HeavenModels.State s) { return texture; }

	@Override
	protected void scale(HeavenModels.State s, PoseStack p) {
		p.scale(scale, scale, scale);
		if (tilt != 0) p.rotateDegrees(com.mojang.math.Axis.XP, tilt);   // 원 중심을 축으로 시전자 쪽으로 기울임
	}

	public static final ModelLayerLocation GATE = new ModelLayerLocation(ElementalWeaponsMod.id("heaven_gate"), "main");
	public static final ModelLayerLocation ANGEL = new ModelLayerLocation(ElementalWeaponsMod.id("archangel"), "main");

	public static void register() {
		net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry.registerModelLayer(GATE, HeavenModels::gate);
		net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry.registerModelLayer(ANGEL, HeavenModels::angel);
		net.minecraft.client.renderer.entity.EntityRenderers.register(com.elementalweapons.mob.ModMobs.HEAVEN_GATE,
				ctx -> new HeavenVisualRenderer(ctx, new HeavenModels.GateModel(ctx.bakeLayer(GATE)), "heaven_gate", 3.6F));
		net.minecraft.client.renderer.entity.EntityRenderers.register(com.elementalweapons.mob.ModMobs.ARCHANGEL,
				ctx -> new HeavenVisualRenderer(ctx, new HeavenModels.AngelModel(ctx.bakeLayer(ANGEL)), "archangel", 4.0F));
	}
}
