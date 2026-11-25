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

	// 성문 치수 (모형 단위, y는 아래가 +). 문 가운데(원점) = y 24
	static final float GW = 20;          // 문 구멍 반폭
	static final float GB = 50;          // 문 바닥
	static final float GA = 14;          // 아치가 시작되는 높이 (아치 중심)
	static final float TW = 10;          // 탑 폭
	static final float TOP = -16;        // 성벽 꼭대기

	/** 문짝 윤곽: 가운데에서 xc만큼 떨어진 곳의 위쪽 끝 */
	private static float archTop(float xc) { return GA - (float) Math.sqrt(Math.max(0, GW * GW - xc * xc)); }

	public static LayerDefinition gate() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition wall = root.addOrReplaceChild("wall", CubeListBuilder.create(), PartPose.ZERO);
		// 좌우 탑 (16칸씩 쌓은 대리석 + 금 띠 + 빛나는 창)
		for (int sd = -1; sd <= 1; sd += 2) {
			float x0 = sd < 0 ? -GW - TW : GW;
			int seg = 0;
			for (float y = TOP; y < GB; y += 16, seg++) {
				float h = Math.min(16, GB - y);
				wall.addOrReplaceChild("t" + sd + "_" + seg, c(MARBLE, x0, y, -5, TW, h, 10), PartPose.ZERO);
				wall.addOrReplaceChild("tb" + sd + "_" + seg, c(GOLD, x0 - 0.4F, y + h - 1.4F, -5.4F, TW + 0.8F, 1.4F, 10.8F), PartPose.ZERO);
			}
			for (int w = 0; w < 3; w++) {   // 창
				float wy = TOP + 8 + w * 20;
				wall.addOrReplaceChild("win" + sd + w, c(LIGHT, x0 + TW / 2 - 1.5F, wy, -5.3F, 3, 6, 0.6F), PartPose.ZERO);
				wall.addOrReplaceChild("winA" + sd + w, c(GOLD, x0 + TW / 2 - 2.2F, wy - 1.2F, -5.5F, 4.4F, 1.2F, 0.8F), PartPose.ZERO);
			}
			// 탑 꼭대기 흉벽 + 뾰족 지붕
			float tx = x0 + TW / 2;
			wall.addOrReplaceChild("tcap" + sd, c(DARKGOLD, x0 - 1.5F, TOP - 2, -6.5F, TW + 3, 2, 13), PartPose.ZERO);
			for (int k = 0; k < 3; k++)
				for (int zz = -1; zz <= 1; zz += 2)
					wall.addOrReplaceChild("mer" + sd + k + zz, c(MARBLE, x0 - 1.5F + k * 4.5F, TOP - 5, zz < 0 ? -6.5F : 3.5F, 3, 3, 3), PartPose.ZERO);
			wall.addOrReplaceChild("roof1" + sd, c(GOLD, tx - 4, TOP - 9, -4, 8, 4, 8), PartPose.ZERO);
			wall.addOrReplaceChild("roof2" + sd, c(GOLD, tx - 2.8F, TOP - 13, -2.8F, 5.6F, 4, 5.6F), PartPose.ZERO);
			wall.addOrReplaceChild("roof3" + sd, c(GOLD, tx - 1.6F, TOP - 17, -1.6F, 3.2F, 4, 3.2F), PartPose.ZERO);
			wall.addOrReplaceChild("roof4" + sd, c(CRYSTAL, tx - 0.8F, TOP - 22, -0.8F, 1.6F, 5, 1.6F), PartPose.ZERO);
			// 탑에 드리운 깃발
			wall.addOrReplaceChild("ban" + sd, c(LIGHT, tx - 3, TOP + 2, -5.9F, 6, 22, 0.5F), PartPose.ZERO);
			wall.addOrReplaceChild("banT" + sd, c(GOLD, tx - 3.5F, TOP + 1.2F, -6.1F, 7, 1.2F, 0.8F), PartPose.ZERO);
			wall.addOrReplaceChild("banE" + sd, c(CRYSTAL, tx - 1.5F, TOP + 9, -6.2F, 3, 3, 0.4F), PartPose.ZERO);
			// 받침돌
			wall.addOrReplaceChild("base" + sd, c(DARKGOLD, x0 - 1.5F, GB - 3, -6.5F, TW + 3, 3, 13), PartPose.ZERO);
		}
		// 아치 위 성벽 (아치 모양으로 비운 벽돌 줄) + 흉벽
		for (float x = -GW; x < GW; x += 2) {
			float xc = Math.abs(x + 1);
			float at = archTop(xc);
			float h = at - TOP;
			if (h > 0.5F) wall.addOrReplaceChild("w" + (int) (x + GW), c(MARBLE, x, TOP, -4, 2, h, 8), PartPose.ZERO);
		}
		wall.addOrReplaceChild("wtop", c(DARKGOLD, -GW, TOP - 2, -5, GW * 2, 2, 10), PartPose.ZERO);
		for (int k = 0; k < 7; k++) wall.addOrReplaceChild("wm" + k, c(MARBLE, -GW + 1 + k * 6, TOP - 5, -5, 3, 3, 3), PartPose.ZERO);
		// 아치 테두리 돌 (홍예석) + 금 쐐기돌
		int n = 15;
		for (int i = 0; i <= n; i++) {
			float a = (float) (Math.PI + i * Math.PI / n);
			float rr = GW + 2;
			wall.addOrReplaceChild("v" + i, c(i == n / 2 ? CRYSTAL : (i % 2 == 0 ? GOLD : MARBLE), -2.6F, -2.5F, -5.2F, 5.2F, 5, 1.6F),
					at(Mth.cos(a) * rr, GA + Mth.sin(a) * rr, 0, 0, 0, a + Mth.HALF_PI));
		}
		for (int sd = -1; sd <= 1; sd += 2)   // 문설주 금 띠
			wall.addOrReplaceChild("jamb" + sd, c(GOLD, sd < 0 ? -GW - 1.6F : GW, GA, -5.2F, 1.6F, GB - GA, 1.6F), PartPose.ZERO);
		// 문 앞 계단
		for (int k = 0; k < 3; k++)
			wall.addOrReplaceChild("step" + k, c(k == 0 ? DARKGOLD : MARBLE, -GW - 4 + k * 3, GB + k * 2, -10 + k * 2, (GW + 4 - k * 3) * 2, 2, 14 - k * 2), PartPose.ZERO);
		// 문짝 두 장 (경첩은 바깥쪽)
		for (int sd = -1; sd <= 1; sd += 2) {
			PartDefinition door = root.addOrReplaceChild(sd < 0 ? "door_l" : "door_r", CubeListBuilder.create(), PartPose.offset(sd * GW, 24, 0));
			for (float x = 0; x < GW; x += 2) {   // x = 가운데로부터 거리
				float xc = x + 1, top = archTop(Math.min(GW - 0.01F, xc + 1));
				float lx = sd < 0 ? GW - x - 2 : -GW + x;
				door.addOrReplaceChild("p" + (int) x, c(MARBLE, lx, top - 24, -0.9F, 2, GB - top, 1.8F), PartPose.ZERO);
			}
			// 금 띠 (가로) + 징
			for (int k = 0; k < 4; k++) {
				float by = GB - 6 - k * 9 - 24;
				float half = k == 3 ? 14 : GW - 0.5F;
				float bx0 = sd < 0 ? GW - half : -GW;
				door.addOrReplaceChild("band" + k, c(GOLD, bx0, by, -1.4F, half, 1.4F, 0.6F), PartPose.ZERO);
				for (int st = 0; st < 4; st++)
					door.addOrReplaceChild("stud" + k + st, c(CRYSTAL, bx0 + 2 + st * (half - 4) / 3F, by + 0.1F, -1.8F, 1.2F, 1.2F, 0.5F), PartPose.ZERO);
			}
			// 가운데 맞닿는 금 테 + 반쪽 태양 문양 + 문고리
			float cx = sd < 0 ? GW : -GW;   // 문짝 기준 가운데 선
			door.addOrReplaceChild("edge", c(GOLD, sd < 0 ? cx - 1.2F : cx, GA - 20 - 24 + 0.5F, -1.3F, 1.2F, GB - GA + 19, 2.6F), PartPose.ZERO);
			for (int k = 0; k < 5; k++) {
				float a = (float) (Math.PI / 2 + (k + 0.5) * Math.PI / 5);
				float ang = sd < 0 ? a : (float) Math.PI - a;
				PartDefinition ray = door.addOrReplaceChild("ray" + k, CubeListBuilder.create(), at(cx, GA + 6 - 24, -1.6F, 0, 0, ang));
				ray.addOrReplaceChild("b", c(GOLD, 3.5F, -0.5F, -0.3F, 8, 1, 0.6F), PartPose.ZERO);
				ray.addOrReplaceChild("g", c(CRYSTAL, 11, -0.8F, -0.4F, 1.6F, 1.6F, 0.7F), PartPose.ZERO);
			}
			door.addOrReplaceChild("sun", c(CRYSTAL, sd < 0 ? cx - 3 : cx, GA + 3 - 24, -1.9F, 3, 6, 0.8F), PartPose.ZERO);
			door.addOrReplaceChild("knock", c(GOLD, sd < 0 ? cx - 5.5F : cx + 2.5F, GA + 18 - 24, -2.2F, 3, 3, 0.8F), PartPose.ZERO);
		}
		// 문 뒤의 빛 (문 구멍 모양)
		PartDefinition light = root.addOrReplaceChild("light", CubeListBuilder.create(), PartPose.offset(0, 24, 2.2F));
		for (float x = -GW; x < GW; x += 2) {
			float top = archTop(Math.abs(x + 1));
			light.addOrReplaceChild("l" + (int) (x + GW), c(LIGHT, x, top - 24, -0.2F, 2, GB - top, 0.4F), PartPose.ZERO);
		}
		// 성벽 위에 떠 있는 빛나는 문장 (위아래로 떠다님)
		PartDefinition runes = root.addOrReplaceChild("runes", CubeListBuilder.create(), PartPose.offset(0, 24 + TOP - 9, -1));
		runes.addOrReplaceChild("s1", c(CRYSTAL, -3, -3, -0.6F, 6, 6, 1.2F), at(0, 0, 0, 0, 0, QP));
		runes.addOrReplaceChild("s2", c(GOLD, -2.2F, -6, -0.4F, 4.4F, 12, 0.8F), PartPose.ZERO);
		runes.addOrReplaceChild("s3", c(GOLD, -6, -2.2F, -0.4F, 12, 4.4F, 0.8F), PartPose.ZERO);
		runes.addOrReplaceChild("s4", c(LIGHT, -1.2F, -1.2F, -0.9F, 2.4F, 2.4F, 1.8F), PartPose.ZERO);
		// 탑 지붕 위 빛 결정 (빙글 도는 부분)
		PartDefinition spires = root.addOrReplaceChild("spires", CubeListBuilder.create(), PartPose.offset(0, 24, 0));
		for (int sd = -1; sd <= 1; sd += 2)
			spires.addOrReplaceChild("gem" + sd, c(CRYSTAL, -1.5F, -1.5F, -1.5F, 3, 3, 3), at(sd * (GW + TW / 2), TOP - 26, 0, QP, QP, 0));
		// 가운데 꼭대기 왕관
		PartDefinition crown = root.addOrReplaceChild("crown", CubeListBuilder.create(), PartPose.offset(0, 24 + TOP - 2, 0));
		crown.addOrReplaceChild("base", c(GOLD, -6, -3, -3, 12, 3, 6), PartPose.ZERO);
		for (int k = -2; k <= 2; k++) crown.addOrReplaceChild("pk" + k, c(GOLD, -0.8F, -3.5F + Math.abs(k), -0.8F, 1.6F, 3.5F - Math.abs(k), 1.6F), PartPose.offset(k * 2.6F, -3, 0));
		// 좌우 날개 (탑 바깥에서 펼쳐짐)
		for (int sd = -1; sd <= 1; sd += 2) {
			PartDefinition wing = root.addOrReplaceChild(sd < 0 ? "wing_l" : "wing_r", CubeListBuilder.create(), PartPose.offset(sd * (GW + TW), 24 + TOP + 6, 3));
			for (int k = 0; k < 9; k++) {
				float deg = 178 + k * 10.5F;
				float a = (float) Math.toRadians(sd < 0 ? deg : 180 - deg);
				float len = 22 + (k % 3) * 4 + (k > 3 && k < 7 ? 5 : 0);
				wing.addOrReplaceChild("f" + k, c(FEATHER, -1.6F, -len, -0.5F, 3.2F, len, 1), at(0, 0, 0.2F * k, 0, 0, a + Mth.HALF_PI));
				wing.addOrReplaceChild("c" + k, c(FEATHER_GOLD, -1.4F, -len * 0.55F, -0.5F, 2.8F, len * 0.55F, 1), at(0, 0, -1 + 0.1F * k, 0, 0, a + Mth.HALF_PI + sd * 0.05F));
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
			runes.y = 24 + TOP - 9 + Mth.sin(s.time * 0.08F) * 1.2F;
			runes.zRot = Mth.sin(s.time * 0.05F) * 0.08F;
			spires.y = 24 + Mth.sin(s.time * 0.1F) * 1.0F;
			float flap = Mth.sin(s.time * 0.12F) * 0.08F;
			wingL.zRot = flap; wingR.zRot = -flap;
			crown.y = 24 + TOP - 2;
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
