package com.elementalweapons.client.spell;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.spell.GrandVisual;

/** 대형 기술 모형 렌더러: 종류마다 크기, 속성마다 텍스처, 항상 밝게, 빛나는 층 */
public class GrandVisualRenderer extends MobRenderer<GrandVisual, GrandModels.State, GrandModels.AllModel> {
	private static final String[] EL = {"electric", "fire", "water", "grass", "wind", "dark", "light"};
	private static final Identifier[] TEX = new Identifier[7], GLOW = new Identifier[7];
	static { for (int i = 0; i < 7; i++) { TEX[i] = ElementalWeaponsMod.id("textures/entity/grand_" + EL[i] + ".png"); GLOW[i] = ElementalWeaponsMod.id("textures/entity/grand_" + EL[i] + "_glow.png"); } }
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(ElementalWeaponsMod.id("grand_visual"), "main");

	public GrandVisualRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new GrandModels.AllModel(ctx.bakeLayer(LAYER)), 0);
		addLayer(new RenderLayer<GrandModels.State, GrandModels.AllModel>(this) {
			@Override
			public void submit(PoseStack ps, SubmitNodeCollector c, int light, GrandModels.State s, float yRot, float xRot) {
				c.submitModel(getParentModel(), s, ps, RenderTypes.eyes(GLOW[Mth.clamp(s.elem, 0, 6)]), light, OverlayTexture.NO_OVERLAY, 0);
			}
		});
	}

	@Override public GrandModels.State createRenderState() { return new GrandModels.State(); }

	@Override
	public void extractRenderState(GrandVisual e, GrandModels.State s, float pt) {
		super.extractRenderState(e, s, pt);
		s.kind = e.kind(); s.elem = e.elem(); s.phase = e.phase(); s.pitch = e.pitch();
		s.strike = e.strike();
		s.strikeAge = e.strike() <= 0 ? 99 : e.tickCount - (e.strike() >> 1) + pt;
		s.time = e.tickCount + pt;
	}

	@Override protected boolean affectedByCulling(GrandVisual e) { return false; }
	@Override protected int getBlockLightLevel(GrandVisual e, BlockPos pos) { return 15; }
	@Override protected boolean shouldShowName(GrandVisual e, double d) { return false; }
	@Override public Identifier getTextureLocation(GrandModels.State s) { return TEX[Mth.clamp(s.elem, 0, 6)]; }

	@Override
	protected void scale(GrandModels.State s, PoseStack p) {
		float k = GrandModels.SCALE[Mth.clamp(s.kind, 0, GrandModels.COUNT - 1)];
		if (s.kind == GrandModels.TREE) { float g = Mth.clamp(s.phase, 0, 1); k *= 0.08F + 0.92F * g * g * (3 - 2 * g); }
		p.scale(k, k, k);
		if (s.pitch != 0) p.rotateDegrees(com.mojang.math.Axis.XP, s.pitch);
	}

	public static void register() {
		net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry.registerModelLayer(LAYER, GrandModels::createAll);
		net.minecraft.client.renderer.entity.EntityRenderers.register(com.elementalweapons.mob.ModMobs.GRAND, GrandVisualRenderer::new);
	}
}
