package com.elementalweapons.client.armor;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * 속성마다 다른 갑옷 장식 (투구 장식 + 등 장식). 착용자의 속성에 맞는 것만 보임.
 *  전기: 번개 볏·등의 번개 갈래 / 불: 불꽃 볏·등 불꽃 깃 / 물: 지느러미 볏·등지느러미 / 풀: 사슴뿔 가지·등 잎사귀
 *  바람: 투구 날개·등 깃털 날개 / 어둠: 굽은 뿔(기본)·등 가시 / 빛: 광륜·작은 빛 날개
 */
public final class ElementArmorParts {
	private ElementArmorParts() {}

	public static final String[] IDS = {"electric", "fire", "water", "grass", "wind", "dark", "light"};
	private static final int PU = 50, PV = 0;      // 판금 무늬 자리
	private static final int GU = 4, GV = 67;      // 빛나는 무늬 자리

	private static CubeListBuilder plate(float x, float y, float z, float w, float h, float d) { return CubeListBuilder.create().texOffs(PU, PV).addBox(x, y, z, w, h, d); }
	private static CubeListBuilder glow(float x, float y, float z, float w, float h, float d) { return CubeListBuilder.create().texOffs(GU, GV).addBox(x, y, z, w, h, d); }
	private static PartPose at(float x, float y, float z, float rx, float ry, float rz) { return PartPose.offsetAndRotation(x, y, z, rx, ry, rz); }

	public static void add(PartDefinition head, PartDefinition body) {
		// ---- 전기 ----
		PartDefinition h = head.addOrReplaceChild("el_electric_helm", CubeListBuilder.create(), PartPose.ZERO);
		for (int i = 0; i < 5; i++) h.addOrReplaceChild("bolt" + i, glow(-0.5F, -2.5F, -0.5F, 1, 3, 1), at(i % 2 == 0 ? 0.6F : -0.6F, -10F + (i % 2) * 0.4F, -4F + i * 1.9F, 0, 0, i % 2 == 0 ? 0.55F : -0.55F));
		for (int s = -1; s <= 1; s += 2) {
			PartDefinition b = h.addOrReplaceChild("side" + s, plate(-0.5F, -3F, -0.5F, 1, 3, 1), at(4.6F * s, -6F, -1F, 0, 0, 0.6F * s));
			b.addOrReplaceChild("tip", glow(-0.5F, -3F, -0.5F, 1, 3, 1), at(0, -3F, 0, 0, 0, -1.1F * s));
		}
		PartDefinition b = body.addOrReplaceChild("el_electric_back", CubeListBuilder.create(), PartPose.ZERO);
		for (int s = -1; s <= 1; s += 2) {
			PartDefinition z1 = b.addOrReplaceChild("zig" + s, glow(-0.5F, -4F, -0.5F, 1, 4, 1), at(2.2F * s, 3F, 2.8F, -0.4F, 0, 0.5F * s));
			z1.addOrReplaceChild("zag", glow(-0.5F, -4F, -0.5F, 1, 4, 1), at(0, -4F, 0, 0, 0, -1.0F * s)).addOrReplaceChild("zig2", glow(-0.5F, -3F, -0.5F, 1, 3, 1), at(0, -4F, 0, 0, 0, 1.0F * s));
		}
		// ---- 불 ----
		h = head.addOrReplaceChild("el_fire_helm", CubeListBuilder.create(), PartPose.ZERO);
		h.addOrReplaceChild("c", glow(-0.5F, -6F, -0.5F, 1, 6, 1), at(0, -9.5F, -1F, -0.35F, 0, 0)).addOrReplaceChild("c2", glow(-0.5F, -3F, -0.5F, 1, 3, 1), at(0, -6F, 0, -0.5F, 0, 0));
		for (int s = -1; s <= 1; s += 2) {
			h.addOrReplaceChild("f" + s, glow(-0.5F, -4.5F, -0.5F, 1, 4.5F, 1), at(2.4F * s, -9.3F, 0, -0.3F, 0, 0.35F * s)).addOrReplaceChild("f2", glow(-0.5F, -2.5F, -0.5F, 1, 2.5F, 1), at(0, -4.5F, 0, -0.4F, 0, 0.3F * s));
			h.addOrReplaceChild("cheek" + s, plate(-0.5F, -2F, -2F, 1, 4, 4), at(4.7F * s, -3.5F, -1F, 0, 0, 0));
		}
		b = body.addOrReplaceChild("el_fire_back", CubeListBuilder.create(), PartPose.ZERO);
		for (int k = -1; k <= 1; k++) b.addOrReplaceChild("plume" + (k + 1), glow(-0.5F, -7F, -0.5F, 1, 7, 1), at(1.6F * k, 3F, 2.8F, -0.55F, 0, 0.3F * k)).addOrReplaceChild("t", glow(-0.5F, -3F, -0.5F, 1, 3, 1), at(0, -7F, 0, -0.5F, 0, 0.25F * k));
		// ---- 물 ----
		h = head.addOrReplaceChild("el_water_helm", CubeListBuilder.create(), PartPose.ZERO);
		h.addOrReplaceChild("crest", plate(-0.5F, -3F, -4F, 1, 3, 9), at(0, -9.5F, 0, 0.12F, 0, 0));
		h.addOrReplaceChild("crestglow", glow(-0.6F, -1F, -3.5F, 1.2F, 1, 8), at(0, -12F, 0, 0.12F, 0, 0));
		for (int s = -1; s <= 1; s += 2) h.addOrReplaceChild("fin" + s, plate(-0.25F, -3F, 0, 0.5F, 3, 4), at(4.7F * s, -4F, -1F, 0.3F, 0.5F * s, 0.2F * s));
		b = body.addOrReplaceChild("el_water_back", CubeListBuilder.create(), PartPose.ZERO);
		b.addOrReplaceChild("dorsal", plate(-0.5F, -6F, 0, 1, 6, 4), at(0, 7F, 2.4F, -0.6F, 0, 0));
		b.addOrReplaceChild("dorsalglow", glow(-0.6F, -5F, 3.5F, 1.2F, 5, 1), at(0, 7F, 2.4F, -0.6F, 0, 0));
		// ---- 풀 ----
		h = head.addOrReplaceChild("el_grass_helm", CubeListBuilder.create(), PartPose.ZERO);
		for (int s = -1; s <= 1; s += 2) {
			PartDefinition a1 = h.addOrReplaceChild("antler" + s, plate(-0.5F, -4F, -0.5F, 1, 4, 1), at(3F * s, -8.5F, 0, -0.2F, 0, 0.45F * s));
			PartDefinition a2 = a1.addOrReplaceChild("a2", plate(-0.5F, -4F, -0.5F, 1, 4, 1), at(0, -4F, 0, -0.3F, 0, 0.35F * s));
			a1.addOrReplaceChild("br", plate(-0.5F, -3F, -0.5F, 1, 3, 1), at(0, -2.5F, 0, -0.9F, 0, -0.3F * s));
			a2.addOrReplaceChild("leaf", glow(-1F, -2F, -0.25F, 2, 2, 0.5F), at(0, -4F, 0, 0, 0, 0.78F));
		}
		b = body.addOrReplaceChild("el_grass_back", CubeListBuilder.create(), PartPose.ZERO);
		for (int s = -1; s <= 1; s += 2) b.addOrReplaceChild("leafback" + s, plate(-2F, -5F, -0.25F, 4, 5, 0.5F), at(2.2F * s, 4F, 2.6F, -0.4F, 0.3F * s, 0.5F * s));
		b.addOrReplaceChild("vine", glow(-0.5F, 0, -0.5F, 1, 10, 1), at(0, 1F, 2.6F, 0, 0, 0.1F));
		// ---- 바람 ----
		h = head.addOrReplaceChild("el_wind_helm", CubeListBuilder.create(), PartPose.ZERO);
		for (int s = -1; s <= 1; s += 2) {
			PartDefinition w = h.addOrReplaceChild("wing" + s, plate(-0.25F, -3F, 0, 0.5F, 3, 6), at(4.6F * s, -6F, -1F, -0.35F, 0.45F * s, 0));
			w.addOrReplaceChild("tip", glow(-0.3F, -2F, 4F, 0.6F, 2, 3), at(0, -1.5F, 0, -0.35F, 0, 0));
		}
		b = body.addOrReplaceChild("el_wind_back", CubeListBuilder.create(), PartPose.ZERO);
		for (int s = -1; s <= 1; s += 2)
			for (int k = 0; k < 3; k++) b.addOrReplaceChild("feather" + s + k, k == 2 ? glow(-0.25F, -8F, 0, 0.5F, 8, 2) : plate(-0.25F, -8F + k, 0, 0.5F, 8 - k, 2.5F),
					at(1.8F * s, 3F + k * 1.5F, 2.4F, -0.35F - k * 0.15F, 0.9F * s, (0.55F + k * 0.3F) * s));
		// ---- 어둠 (기본 뿔에 더해) ----
		h = head.addOrReplaceChild("el_dark_helm", CubeListBuilder.create(), PartPose.ZERO);
		h.addOrReplaceChild("eye", glow(-2.5F, -0.5F, -0.5F, 5, 1, 1), at(0, -4.5F, -5.4F, 0, 0, 0));
		b = body.addOrReplaceChild("el_dark_back", CubeListBuilder.create(), PartPose.ZERO);
		for (int k = 0; k < 3; k++) b.addOrReplaceChild("spine" + k, plate(-0.5F, -4F + k, -0.5F, 1, 4 - k, 1), at(0, 2F + k * 3.2F, 2.6F, -0.9F, 0, 0));
		for (int s = -1; s <= 1; s += 2) b.addOrReplaceChild("blade" + s, glow(-0.5F, -6F, -0.5F, 1, 6, 1), at(2.5F * s, 3F, 2.6F, -0.7F, 0, 0.45F * s));
		// ---- 빛 ----
		h = head.addOrReplaceChild("el_light_helm", CubeListBuilder.create(), PartPose.ZERO);
		for (int k = 0; k < 12; k++) {
			float a = (float) (k * Math.PI / 6);
			h.addOrReplaceChild("halo" + k, glow(-0.75F, -0.4F, -0.4F, 1.5F, 0.8F, 0.8F), at((float) Math.cos(a) * 4.2F, -12.5F, (float) Math.sin(a) * 4.2F + 1F, 0, -a + (float) Math.PI / 2, 0));
		}
		h.addOrReplaceChild("crown", plate(-0.5F, -3F, -0.5F, 1, 3, 1), at(0, -9F, -4.5F, -0.2F, 0, 0));
		b = body.addOrReplaceChild("el_light_back", CubeListBuilder.create(), PartPose.ZERO);
		for (int s = -1; s <= 1; s += 2) {
			PartDefinition w = b.addOrReplaceChild("lwing" + s, plate(-0.25F, -7F, 0, 0.5F, 7, 3), at(1.6F * s, 3F, 2.4F, -0.2F, 0.8F * s, 0.7F * s));
			w.addOrReplaceChild("edge", glow(-0.3F, -7F, 2.6F, 0.6F, 7, 1), PartPose.ZERO);
		}
	}
}
