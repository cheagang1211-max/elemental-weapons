package com.elementalweapons.client.spell;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.Mth;

/**
 * 천상의 원환문과 대천사 입체 모형 (상자 수백 개를 원·부채꼴로 배치).
 *  원환문: 대리석 원 테 + 금 상감, 돌아가는 룬 고리, 바깥으로 뻗은 수정 첨탑 12개, 반원 문짝 두 장(태양 문양),
 *          문 뒤의 빛 원판, 꼭대기 별 왕관, 아래 수정 펜던트, 좌우 깃털 날개
 *  대천사: 은백색 갑옷·금 장식, 빛나는 눈가리개 투구와 깃 장식, 돌아가는 광륜, 날개 세 쌍, 긴 빛의 창
 */
public final class HeavenModels {
	private HeavenModels() {}

	/** 렌더 상태: 열림(0~1), 찌르기 경과 틱, 시간 */
	public static class State extends LivingEntityRenderState {
		public float open, strikeAge = 99, time;
	}

	// 텍스처 칸 (256×256, 64칸마다 재질)
	static final int[] MARBLE = {0, 0}, GOLD = {64, 0}, CRYSTAL = {128, 0}, LIGHT = {192, 0}, FEATHER = {0, 64}, FEATHER_GOLD = {64, 64}, ARMOR = {128, 64}, DARKGOLD = {192, 64};

	private static CubeListBuilder c(int[] m, float x, float y, float z, float w, float h, float d) {
		return CubeListBuilder.create().texOffs(m[0], m[1]).addBox(x, y, z, w, h, d);
	}
	private static PartPose at(float x, float y, float z, float rx, float ry, float rz) { return PartPose.offsetAndRotation(x, y, z, rx, ry, rz); }

	static final float QP = (float) (Math.PI / 4);
	static final float R = 28;   // 원 테 반지름 (모형 단위)
	static final float RD = 25;  // 문짝 반지름

	public static LayerDefinition gate() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		// 원 테 (대리석) + 안쪽 금 상감 + 바깥 금 테
		PartDefinition ring = root.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offset(0, 24, 0));
		int n = 48;
		for (int i = 0; i < n; i++) {
			float a = (float) (i * Math.PI * 2 / n), seg = (float) (2 * Math.PI * R / n) + 0.4F;
			ring.addOrReplaceChild("m" + i, c(MARBLE, -seg / 2, -2.5F, -2.5F, seg, 5, 5), at(Mth.cos(a) * R, Mth.sin(a) * R, 0, 0, 0, a + Mth.HALF_PI));
			ring.addOrReplaceChild("g" + i, c(GOLD, -seg / 2, -0.6F, -2.8F, seg * 0.92F, 1.2F, 5.6F), at(Mth.cos(a) * (R - 3), Mth.sin(a) * (R - 3), 0, 0, 0, a + Mth.HALF_PI));
			ring.addOrReplaceChild("o" + i, c(GOLD, -seg / 2, -0.5F, -1.5F, seg * 1.05F, 1, 3), at(Mth.cos(a) * (R + 3), Mth.sin(a) * (R + 3), 0, 0, 0, a + Mth.HALF_PI));
			if (i % 4 == 0)   // 테 위의 장식 돌기
				ring.addOrReplaceChild("k" + i, c(DARKGOLD, -1.5F, -1.5F, -3.4F, 3, 3, 1), at(Mth.cos(a) * R, Mth.sin(a) * R, 0, 0, 0, a + QP));
		}
		// 바깥으로 뻗은 수정 첨탑 (해가 빛살을 뻗듯)
		PartDefinition spires = root.addOrReplaceChild("spires", CubeListBuilder.create(), PartPose.offset(0, 24, 0));
		for (int i = 0; i < 12; i++) {
			float a = (float) (i * Math.PI / 6 - Math.PI / 2);
			float len = i % 2 == 0 ? 15 : 9;
			PartDefinition s = spires.addOrReplaceChild("s" + i, c(CRYSTAL, -1.5F, -len, -1.5F, 3, len, 3), at(Mth.cos(a) * (R + 4), Mth.sin(a) * (R + 4), 0, 0, 0, a + Mth.HALF_PI));
			s.addOrReplaceChild("t", c(CRYSTAL, -0.8F, -5, -0.8F, 1.6F, 5, 1.6F), at(0, -len, 0, 0, 0, 0));
			s.addOrReplaceChild("b", c(GOLD, -2.2F, -1.5F, -2.2F, 4.4F, 1.5F, 4.4F), PartPose.ZERO);
		}
		// 돌아가는 룬 고리
		PartDefinition runes = root.addOrReplaceChild("runes", CubeListBuilder.create(), PartPose.offset(0, 24, 0));
		for (int i = 0; i < 24; i++) {
			float a = (float) (i * Math.PI / 12);
			runes.addOrReplaceChild("r" + i, i % 2 == 0 ? c(CRYSTAL, -1.6F, -2.4F, -0.4F, 3.2F, 4.8F, 0.8F) : c(GOLD, -0.6F, -0.6F, -0.3F, 1.2F, 1.2F, 0.6F),
					at(Mth.cos(a) * (R + 8), Mth.sin(a) * (R + 8), 1.5F, 0, 0, a + Mth.HALF_PI));
		}
		// 문짝 두 장 (반원), 경첩은 양 끝
		for (int side = -1; side <= 1; side += 2) {
			String nm = side < 0 ? "door_l" : "door_r";
			PartDefinition door = root.addOrReplaceChild(nm, CubeListBuilder.create(), PartPose.offset(side * RD, 24, 0));
			for (float x = 0; x < RD; x += 2) {   // 세로 판자 줄
				float xc = x + 1, hh = (float) Math.sqrt(Math.max(1, RD * RD - xc * xc));
				float lx = side < 0 ? (RD - x - 2) : (-RD + x);   // 경첩 기준 위치
				door.addOrReplaceChild("p" + (int) x, c(MARBLE, lx, -hh, -0.8F, 2, hh * 2, 1.6F), PartPose.ZERO);
			}
			// 태양 문양: 가운데 원 + 빛살 + 동심 호
			float cx = -side * RD;   // 문짝 기준으로 본 원의 중심
			for (int k = 0; k < 7; k++) {
				float a = (float) (Math.PI / 2 + (k + 0.5) * Math.PI / 7) * (side < 0 ? 1 : -1) + (side < 0 ? 0 : (float) Math.PI);
				PartDefinition ray = door.addOrReplaceChild("ray" + k, CubeListBuilder.create(), at(cx, 0, -1.1F, 0, 0, a));
				ray.addOrReplaceChild("b", c(GOLD, 6, -0.5F, -0.3F, 17, 1, 0.6F), PartPose.ZERO);
				ray.addOrReplaceChild("gem", c(CRYSTAL, 21, -1, -0.5F, 2, 2, 0.8F), PartPose.ZERO);
			}
			for (int ring2 = 0; ring2 < 3; ring2++) {
				float rr = 6 + ring2 * 7;
				for (int k = 0; k < 10; k++) {
					float a = (float) (Math.PI / 2 + (k + 0.5) * Math.PI / 10);
					if (side > 0) a = (float) (Math.PI - a);
					float seg = (float) (Math.PI * rr / 10) + 0.3F;
					door.addOrReplaceChild("arc" + ring2 + "_" + k, c(ring2 == 0 ? CRYSTAL : GOLD, -seg / 2, -0.4F, -0.4F, seg, 0.8F, 0.8F),
							at(cx + Mth.cos(a) * rr, Mth.sin(a) * rr, -1.1F, 0, 0, a + Mth.HALF_PI));
				}
			}
			door.addOrReplaceChild("edge", c(GOLD, side < 0 ? RD - 1.2F : -RD, -RD + 2, -1.2F, 1.2F, (RD - 2) * 2, 2.4F), PartPose.ZERO);   // 가운데 맞닿는 금 테
		}
		// 문 뒤의 빛 원판
		PartDefinition light = root.addOrReplaceChild("light", CubeListBuilder.create(), PartPose.offset(0, 24, 2.2F));
		for (float x = -RD; x < RD; x += 2) {
			float xc = x + 1, hh = (float) Math.sqrt(Math.max(1, RD * RD - xc * xc));
			light.addOrReplaceChild("l" + (int) (x + RD), c(LIGHT, x, -hh, -0.2F, 2, hh * 2, 0.4F), PartPose.ZERO);
		}
		// 꼭대기 별 왕관
		PartDefinition crown = root.addOrReplaceChild("crown", CubeListBuilder.create(), PartPose.offset(0, 24 - R - 4, 0));
		crown.addOrReplaceChild("base", c(GOLD, -7, -2, -2.5F, 14, 2, 5), PartPose.ZERO);
		crown.addOrReplaceChild("t2", c(MARBLE, -5, -4, -2, 10, 2, 4), PartPose.ZERO);
		crown.addOrReplaceChild("star1", c(CRYSTAL, -3.5F, -3.5F, -0.6F, 7, 7, 1.2F), at(0, -9, 0, 0, 0, QP));
		crown.addOrReplaceChild("star2", c(CRYSTAL, -3.5F, -3.5F, -0.7F, 7, 7, 1.4F), at(0, -9, 0, 0, 0, 0));
		crown.addOrReplaceChild("core", c(LIGHT, -1.5F, -1.5F, -1.2F, 3, 3, 2.4F), at(0, -9, 0, 0, 0, QP));
		for (int s = -1; s <= 1; s += 2) crown.addOrReplaceChild("pin" + s, c(CRYSTAL, -0.7F, -6, -0.7F, 1.4F, 6, 1.4F), at(s * 5.5F, -2, 0, 0, 0, s * 0.35F));
		// 아래 수정 펜던트
		PartDefinition pend = root.addOrReplaceChild("pendant", CubeListBuilder.create(), PartPose.offset(0, 24 + R + 2.5F, 0));
		pend.addOrReplaceChild("a", c(GOLD, -3, 0, -2, 6, 2, 4), PartPose.ZERO);
		pend.addOrReplaceChild("b", c(CRYSTAL, -2, 2, -1.5F, 4, 4, 3), PartPose.ZERO);
		pend.addOrReplaceChild("c", c(CRYSTAL, -1.2F, 6, -1, 2.4F, 4, 2), PartPose.ZERO);
		pend.addOrReplaceChild("d", c(CRYSTAL, -0.5F, 10, -0.5F, 1, 4, 1), PartPose.ZERO);
		// 좌우 날개 (긴 깃 + 덮깃)
		for (int side = -1; side <= 1; side += 2) {
			PartDefinition wing = root.addOrReplaceChild(side < 0 ? "wing_l" : "wing_r", CubeListBuilder.create(), PartPose.offset(side * (R + 1), 20, 3));
			for (int k = 0; k < 9; k++) {
				float deg = 178 + k * 10.5F;   // 옆 → 위쪽으로 펼침
				float a = (float) Math.toRadians(side < 0 ? deg : 180 - deg);
				float len = 22 + (k % 3) * 4 + (k > 3 && k < 7 ? 5 : 0);
				wing.addOrReplaceChild("f" + k, c(FEATHER, -1.6F, -len, -0.5F, 3.2F, len, 1), at(0, 0, 0.2F * k, 0, 0, a + Mth.HALF_PI));
				wing.addOrReplaceChild("c" + k, c(FEATHER_GOLD, -1.4F, -len * 0.55F, -0.5F, 2.8F, len * 0.55F, 1), at(0, 0, -1 + 0.1F * k, 0, 0, a + Mth.HALF_PI + side * 0.05F));
			}
		}
		return LayerDefinition.create(mesh, 256, 256);
	}

	public static LayerDefinition angel() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		// 긴 갑옷 치마 (발까지)
		PartDefinition skirt = root.addOrReplaceChild("skirt", CubeListBuilder.create(), PartPose.offset(0, 12, 0));
		for (int i = 0; i < 10; i++) {
			float a = (float) (i * Math.PI * 2 / 10);
			skirt.addOrReplaceChild("s" + i, c(i % 2 == 0 ? ARMOR : MARBLE, -2.2F, 0, -0.5F, 4.4F, 12, 1), at(Mth.sin(a) * 4.2F, 0, Mth.cos(a) * 3.2F, 0.18F * Mth.cos(a), a, 0));
			skirt.addOrReplaceChild("h" + i, c(GOLD, -2.2F, 11, -0.6F, 4.4F, 1, 1.2F), at(Mth.sin(a) * 4.2F, 0, Mth.cos(a) * 3.2F, 0.18F * Mth.cos(a), a, 0));
		}
		skirt.addOrReplaceChild("belt", c(GOLD, -5, -1, -3.2F, 10, 2, 6.4F), PartPose.ZERO);
		// 몸통
		PartDefinition torso = root.addOrReplaceChild("torso", c(ARMOR, -5, -13, -3, 10, 13, 6), PartPose.offset(0, 12, 0));
		torso.addOrReplaceChild("plate", c(GOLD, -3.5F, -12, -3.6F, 7, 6, 1), PartPose.ZERO);
		torso.addOrReplaceChild("gem", c(CRYSTAL, -1.5F, -10.5F, -4.1F, 3, 3, 1), PartPose.ZERO);
		torso.addOrReplaceChild("cape", c(FEATHER, -5, -13, 3, 10, 22, 0.6F), at(0, 0, 0, 0.12F, 0, 0));
		// 머리 (투구)
		PartDefinition head = root.addOrReplaceChild("head", c(ARMOR, -3.5F, -8, -3.5F, 7, 8, 7), PartPose.offset(0, -1, 0));
		head.addOrReplaceChild("visor", c(LIGHT, -3, -5, -3.9F, 6, 1.2F, 0.6F), PartPose.ZERO);
		head.addOrReplaceChild("crest", c(GOLD, -0.6F, -12, -3, 1.2F, 5, 7), PartPose.ZERO);
		for (int s = -1; s <= 1; s += 2)
			for (int k = 0; k < 3; k++) head.addOrReplaceChild("hw" + s + k, c(FEATHER, -0.8F, -6 + k, -0.3F, 1.6F, 6 - k, 0.6F), at(s * 3.8F, -5, 0.5F + k, -0.5F - k * 0.25F, 0, s * 0.9F));
		// 광륜
		PartDefinition halo = root.addOrReplaceChild("halo", CubeListBuilder.create(), PartPose.offset(0, -13.5F, 1));
		for (int i = 0; i < 20; i++) {
			float a = (float) (i * Math.PI * 2 / 20);
			halo.addOrReplaceChild("h" + i, c(i % 5 == 0 ? CRYSTAL : GOLD, -1.1F, -0.4F, -0.5F, 2.2F, 0.8F, 1), at(Mth.cos(a) * 6, 0, Mth.sin(a) * 6, 0, -a + Mth.HALF_PI, 0));
		}
		// 어깨·팔
		for (int s = -1; s <= 1; s += 2) {
			root.addOrReplaceChild(s < 0 ? "paul_r" : "paul_l", c(GOLD, -2.5F, -2.5F, -3.5F, 5, 4, 7), PartPose.offset(s * -6.5F, 0, 0));
		}
		PartDefinition armR = root.addOrReplaceChild("arm_r", c(ARMOR, -2, -1, -2, 3.5F, 13, 3.5F), PartPose.offset(-6.5F, 1, 0));
		PartDefinition spear = armR.addOrReplaceChild("spear", c(GOLD, -0.6F, -26, -0.6F, 1.2F, 44, 1.2F), at(-0.2F, 11, 0, Mth.HALF_PI, 0, 0));
		spear.addOrReplaceChild("guard", c(GOLD, -3, -26, -1, 6, 1.4F, 2), PartPose.ZERO);
		spear.addOrReplaceChild("blade", c(CRYSTAL, -1.5F, -36, -0.6F, 3, 10, 1.2F), PartPose.ZERO);
		spear.addOrReplaceChild("tip", c(LIGHT, -0.7F, -40, -0.4F, 1.4F, 4, 0.8F), PartPose.ZERO);
		root.addOrReplaceChild("arm_l", c(ARMOR, -1.5F, -1, -2, 3.5F, 13, 3.5F), PartPose.offset(6.5F, 1, 0));
		// 날개 세 쌍 (위·가운데·아래)
		for (int pair = 0; pair < 3; pair++)
			for (int side = -1; side <= 1; side += 2) {
				PartDefinition w = root.addOrReplaceChild("wing" + pair + (side < 0 ? "r" : "l"), CubeListBuilder.create(), PartPose.offset(side * -2.5F, 2 + pair * 4.5F, 3.5F));
				float base = pair == 0 ? 200 : pair == 1 ? 180 : 160, span = pair == 0 ? 70 : 50;
				int feathers = pair == 0 ? 8 : 6;
				for (int k = 0; k < feathers; k++) {
					float deg = base + k * span / feathers;
					float a = (float) Math.toRadians(side < 0 ? deg : 180 - deg);
					float len = (pair == 0 ? 24 : pair == 1 ? 18 : 13) + (k % 2) * 3;
					w.addOrReplaceChild("f" + k, c(k == feathers - 1 ? FEATHER_GOLD : FEATHER, -1.5F, -len, -0.5F, 3, len, 1), at(0, 0, 0.25F * k, 0, 0, a + Mth.HALF_PI));
				}
			}
		return LayerDefinition.create(mesh, 256, 256);
	}

	/** 원환문 모형 */
	public static class GateModel extends EntityModel<State> {
		private final ModelPart runes, doorL, doorR, light, wingL, wingR, crown, spires;
		public GateModel(ModelPart root) {
			super(root);
			runes = root.getChild("runes"); doorL = root.getChild("door_l"); doorR = root.getChild("door_r"); light = root.getChild("light");
			wingL = root.getChild("wing_l"); wingR = root.getChild("wing_r"); crown = root.getChild("crown"); spires = root.getChild("spires");
		}
		@Override
		public void setupAnim(State s) {
			super.setupAnim(s);
			float o = Mth.clamp(s.open, 0, 1), e = o * o * (3 - 2 * o);
			doorL.yRot = -e * 1.75F;       // 문짝이 안쪽으로 크게 열림
			doorR.yRot = e * 1.75F;
			light.visible = o > 0.02F;
			runes.zRot = s.time * 0.02F;
			spires.zRot = -s.time * 0.004F;
			float flap = Mth.sin(s.time * 0.12F) * 0.08F;
			wingL.zRot = flap; wingR.zRot = -flap;
			crown.y = 24 - R - 4 + Mth.sin(s.time * 0.1F) * 0.8F;
		}
	}

	/** 대천사 모형 */
	public static class AngelModel extends EntityModel<State> {
		private final ModelPart halo, armR, head;
		private final ModelPart[] wings = new ModelPart[6];
		public AngelModel(ModelPart root) {
			super(root);
			halo = root.getChild("halo"); armR = root.getChild("arm_r"); head = root.getChild("head");
			for (int p = 0; p < 3; p++) { wings[p * 2] = root.getChild("wing" + p + "r"); wings[p * 2 + 1] = root.getChild("wing" + p + "l"); }
		}
		@Override
		public void setupAnim(State s) {
			super.setupAnim(s);
			halo.yRot = s.time * 0.06F;
			halo.y = -13.5F + Mth.sin(s.time * 0.1F) * 0.5F;
			float spread = Mth.clamp(s.open, 0, 1);
			for (int p = 0; p < 3; p++) {
				float flap = Mth.sin(s.time * 0.15F + p * 0.6F) * 0.12F;
				wings[p * 2].zRot = flap - (1 - spread) * 0.9F;
				wings[p * 2 + 1].zRot = -flap + (1 - spread) * 0.9F;
				wings[p * 2].yRot = -0.35F; wings[p * 2 + 1].yRot = 0.35F;
			}
			// 창 찌르기: 뒤로 당겼다가 앞으로 내지름
			float a = s.strikeAge;
			float pull = a < 3 ? a / 3F : a < 6 ? 1 - (a - 3) / 3F * 1.6F : a < 12 ? -0.6F + (a - 6) / 6F * 0.6F : 0;
			armR.xRot = -0.25F - pull * 0.9F;
			armR.zRot = 0.15F;
			head.xRot = 0.1F;
		}
	}
}
