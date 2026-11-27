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
 * 대형 기술 입체 모형 12종 (속성마다 같은 배치의 다른 텍스처).
 *  0 성검  1 전투 도끼  2 거대 단검  3 하늘의 활  4 하늘 화살  5 명계의 사신  6 성역의 여신
 *  7 거신(불: 화염 왕관·불주먹 / 전기: 뿔 투구·뇌신 망치)  8 리바이어던  9 세계수  10 폭풍의 대붕  11 심연의 문
 */
public final class GrandModels {
	private GrandModels() {}

	public static final int SWORD = 0, AXE = 1, DAGGER = 2, BOW = 3, ARROW = 4, REAPER = 5, GODDESS = 6, TITAN = 7, LEVIATHAN = 8, TREE = 9, ROC = 10, ABYSS = 11;
	/** 무기 우클릭 연출: 12 낙하 영검  13 대검 원진  14 거대 망치  15 충격 결정 고리  16 초승달 낫날  17 마도서 */
	public static final int BLADE = 12, BLADE_RING = 13, HAMMER = 14, SHOCK = 15, CRESCENT = 16, TOME = 17, SPIRIT_BOW = 18;
	public static final int COUNT = 19;
	public static final float[] SCALE = {6F, 6F, 5F, 6F, 6F, 7F, 7F, 8F, 6F, 7F, 7F, 6F, 1.5F, 1.7F, 1.6F, 1.2F, 1.35F, 1.3F, 1.1F};

	public static class State extends LivingEntityRenderState {
		public int kind, elem;
		public float phase, strikeAge = 99, time, pitch;
		public int strike;
	}

	static final int[] BASE = {0, 0}, TRIM = {64, 0}, CRYSTAL = {128, 0}, LIGHT = {192, 0}, CLOTH = {0, 64}, CLOTH_GLOW = {64, 64}, ARMOR = {128, 64}, DARK = {192, 64};

	static CubeListBuilder c(int[] m, float x, float y, float z, float w, float h, float d) { return CubeListBuilder.create().texOffs(m[0], m[1]).addBox(x, y, z, w, h, d); }
	static PartPose at(float x, float y, float z, float rx, float ry, float rz) { return PartPose.offsetAndRotation(x, y, z, rx, ry, rz); }
	static PartDefinition node(PartDefinition p, String n, float x, float y, float z) { return p.addOrReplaceChild(n, CubeListBuilder.create(), PartPose.offset(x, y, z)); }
	static PartDefinition node(PartDefinition p, String n, PartPose pose) { return p.addOrReplaceChild(n, CubeListBuilder.create(), pose); }
	static void box(PartDefinition p, String n, int[] m, float x, float y, float z, float w, float h, float d) { p.addOrReplaceChild(n, c(m, x, y, z, w, h, d), PartPose.ZERO); }
	static void box(PartDefinition p, String n, int[] m, float x, float y, float z, float w, float h, float d, PartPose pose) { p.addOrReplaceChild(n, c(m, x, y, z, w, h, d), pose); }

	/** 세로로 쌓으며 폭이 변하는 기둥 (y0 위 → y1 아래, 모형 좌표는 아래가 +) */
	static void taper(PartDefinition p, String n, int[] m, float y0, float y1, float w0, float w1, float d0, float d1, int seg) {
		float h = (y1 - y0) / seg;
		for (int i = 0; i < seg; i++) {
			float t = (i + 0.5F) / seg, w = w0 + (w1 - w0) * t, d = d0 + (d1 - d0) * t;
			box(p, n + i, m, -w / 2, y0 + i * h, -d / 2, w, h + 0.05F, d);
		}
	}
	/** 깃털 부채 (위쪽으로 펼침) */
	static void fan(PartDefinition p, String n, int[] m, int[] tip, int count, float len, float deg0, float deg1, float width) {
		for (int k = 0; k < count; k++) {
			float a = (float) Math.toRadians(deg0 + (deg1 - deg0) * k / Math.max(1, count - 1));
			float l = len * (0.8F + 0.2F * (float) Math.sin(k * 1.3));
			box(p, n + k, m, -width / 2, -l, -0.5F, width, l, 1, at(0, 0, 0.15F * k, 0, 0, a));
			box(p, n + "t" + k, tip, -width / 2 + 0.2F, -l * 0.3F, -0.6F, width - 0.4F, l * 0.3F, 1.2F, at(-Mth.sin(a) * l * -0.7F * 0, 0, 0.15F * k, 0, 0, a));
		}
	}
	/** 고리 */
	static void ring(PartDefinition p, String n, int[] m, int count, float r, float w, float h, float d, boolean flat) {
		for (int i = 0; i < count; i++) {
			float a = (float) (i * Math.PI * 2 / count);
			if (flat) box(p, n + i, m, -w / 2, -h / 2, -d / 2, w, h, d, at(Mth.cos(a) * r, 0, Mth.sin(a) * r, 0, -a + Mth.HALF_PI, 0));
			else box(p, n + i, m, -w / 2, -h / 2, -d / 2, w, h, d, at(Mth.cos(a) * r, Mth.sin(a) * r, 0, 0, 0, a + Mth.HALF_PI));
		}
	}

	/** 12종을 한 모형에 모두 담음 (k0~k11, 그릴 때 하나만 보임) */
	public static LayerDefinition createAll() {
		MeshDefinition mesh = new MeshDefinition();
		for (int k = 0; k < COUNT; k++) build(mesh.getRoot().addOrReplaceChild("k" + k, CubeListBuilder.create(), PartPose.ZERO), k);
		return LayerDefinition.create(mesh, 256, 256);
	}

	static void build(PartDefinition root, int kind) {
		switch (kind) {
			case SWORD -> sword(root);
			case AXE -> axe(root);
			case DAGGER -> dagger(root);
			case BOW -> bow(root);
			case ARROW -> arrow(root);
			case REAPER -> reaper(root);
			case GODDESS -> goddess(root);
			case TITAN -> titan(root);
			case LEVIATHAN -> leviathan(root);
			case TREE -> tree(root);
			case ROC -> roc(root);
			case ABYSS -> abyss(root);
			case BLADE -> sword(root);
			case BLADE_RING -> bladeRing(root);
			case HAMMER -> hammer(root);
			case SHOCK -> shock(root);
			case CRESCENT -> crescent(root);
			case TOME -> tome(root);
			default -> bow(root);
		}
	}

	// ---------------------------------------------------------------- 13 대검 원진: 끝이 아래인 대검 6자루가 원을 이룸
	static void bladeRing(PartDefinition root) {
		PartDefinition sp = node(root, "spin", 0, 24, 0);
		for (int i = 0; i < 6; i++) {
			float a = (float) (i * Math.PI / 3);
			PartDefinition g = node(sp, "g" + i, at(Mth.cos(a) * 42, 0, Mth.sin(a) * 42, 0, -a + Mth.HALF_PI, 0.18F));
			box(g, "tip", BASE, -1.5F, -6, -1, 3, 6, 2);
			for (int k = 0; k < 6; k++) {
				float w = 5 + k * 0.9F;
				box(g, "b" + k, BASE, -w / 2, -6 - (k + 1) * 7, -1.4F, w, 7.1F, 2.8F);
				box(g, "e" + k, LIGHT, -w / 2 - 0.5F, -6 - (k + 1) * 7, -0.6F, 0.6F, 7.1F, 1.2F);
				box(g, "f" + k, LIGHT, w / 2 - 0.1F, -6 - (k + 1) * 7, -0.6F, 0.6F, 7.1F, 1.2F);
			}
			box(g, "fuller", CRYSTAL, -0.8F, -46, -1.7F, 1.6F, 38, 3.4F);
			box(g, "guard", TRIM, -8, -53, -2.5F, 16, 4, 5);
			box(g, "gem", CRYSTAL, -2, -53.5F, -3, 4, 4, 6);
			box(g, "grip", ARMOR, -1.6F, -64, -1.6F, 3.2F, 11, 3.2F);
			box(g, "pom", TRIM, -2.6F, -68, -2.6F, 5.2F, 4, 5.2F);
		}
		PartDefinition rr = node(root, "runes", 0, 23, 0);
		ring(rr, "r", CRYSTAL, 24, 46, 4, 0.6F, 2, true);
		ring(rr, "s", LIGHT, 12, 30, 2.4F, 0.6F, 2.4F, true);
	}

	// ---------------------------------------------------------------- 14 거대 망치 (원점 = 자루 윗끝, 아래로 매달림)
	static void hammer(PartDefinition root) {
		PartDefinition a = node(root, "arm", 0, 24, 0);
		for (int i = 0; i < 5; i++) { box(a, "h" + i, ARMOR, -2.4F, i * 10F, -2.4F, 4.8F, 10.1F, 4.8F); box(a, "r" + i, TRIM, -3, i * 10F + 8.4F, -3, 6, 1.6F, 6); }
		box(a, "pom", CRYSTAL, -3.5F, -6, -3.5F, 7, 6, 7);
		PartDefinition head = node(a, "head", 0, 58, 0);
		box(head, "core", BASE, -9, -8, -15, 18, 16, 30);
		box(head, "bandA", TRIM, -10, -9, -9, 20, 18, 3);
		box(head, "bandB", TRIM, -10, -9, 6, 20, 18, 3);
		box(head, "faceF", ARMOR, -8, -7, -17, 16, 14, 2);
		box(head, "faceB", ARMOR, -8, -7, 15, 16, 14, 2);
		box(head, "runeF", CRYSTAL, -4, -4, -18, 8, 8, 1.2F, at(0, 0, 0, 0, 0, 0.785F));
		box(head, "runeB", CRYSTAL, -4, -4, 16.8F, 8, 8, 1.2F, at(0, 0, 0, 0, 0, 0.785F));
		for (int sd = -1; sd <= 1; sd += 2) {
			box(head, "side" + sd, LIGHT, sd < 0 ? -10.5F : 9.5F, -5, -12, 1, 10, 24);
			for (int k = 0; k < 3; k++) box(head, "sp" + sd + k, TRIM, -1.5F, -1.5F, -1.5F, 3, 3, 3, at(sd * 10.5F, -4 + k * 4, -14 + k * 14, 0.785F, 0, 0.785F));
		}
		box(head, "top", TRIM, -6, -12, -6, 12, 4, 12);
	}

	// ---------------------------------------------------------------- 15 충격 결정 고리 (땅에서 솟는 결정 가시)
	static void shock(PartDefinition root) {
		PartDefinition m = node(root, "main", 0, 24, 0);
		for (int i = 0; i < 14; i++) {
			float a = (float) (i * Math.PI * 2 / 14);
			float r = 50 + (i % 2) * 10, h = 20 + (i % 3) * 8;
			PartDefinition sp = node(m, "s" + i, at(Mth.cos(a) * r, 0, Mth.sin(a) * r, 0, -a, 0));
			box(sp, "b", CRYSTAL, -2.2F, -h * 0.62F, -2.2F, 4.4F, h * 0.62F, 4.4F, at(0, 0, 0, 0, 0, 0.4F));
			box(sp, "t", LIGHT, -1.2F, -h, -1.2F, 2.4F, h * 0.42F, 2.4F, at(0, 0, 0, 0, 0, 0.4F));
			box(sp, "rk", BASE, -4, -2, -4, 8, 2, 8, at(0, 0, 0, 0, 0.6F, 0));
		}
		PartDefinition rr = node(m, "runes", 0, -0.5F, 0);
		ring(rr, "r", LIGHT, 36, 40, 4, 0.5F, 1.6F, true);
		ring(rr, "q", CRYSTAL, 48, 66, 4, 0.5F, 2.2F, true);
	}

	// ---------------------------------------------------------------- 16 초승달 낫날 (수평으로 도는 거대한 날)
	static void crescent(PartDefinition root) {
		PartDefinition sp = node(root, "spin", 0, 6, 0);
		int n = 16;
		for (int k = 0; k < n; k++) {
			float t = k / (float) (n - 1);
			float a = (float) Math.toRadians(-110 + 220 * t);
			float w = 3 + 9 * Mth.sin(t * Mth.PI);
			float r = 44;
			box(sp, "b" + k, BASE, -w / 2, -1.2F, -5.2F, w, 2.4F, 10.4F, at(Mth.cos(a) * (r - w / 2 + 2), 0, Mth.sin(a) * (r - w / 2 + 2), 0, -a, 0));
			box(sp, "e" + k, LIGHT, -0.8F, -0.8F, -5.4F, 1.6F, 1.6F, 10.8F, at(Mth.cos(a) * (r + 2.2F), 0, Mth.sin(a) * (r + 2.2F), 0, -a, 0));
		}
		box(sp, "hub", TRIM, -5, -3, -5, 10, 6, 10);
		box(sp, "gem", CRYSTAL, -3, -4, -3, 6, 8, 6, at(0, 0, 0, 0, 0.785F, 0));
		box(sp, "arm", ARMOR, -1.6F, -1.6F, -1.6F, 30, 3.2F, 3.2F, at(0, 0, 0, 0, Mth.PI, 0));
		box(sp, "arm2", ARMOR, -1.6F, -1.6F, -1.6F, 30, 3.2F, 3.2F);
	}

	// ---------------------------------------------------------------- 17 마도서 (펼쳐지는 거대한 책 + 도는 룬 고리)
	static void tome(PartDefinition root) {
		PartDefinition m = node(root, "main", 0, 24, 0);
		box(m, "spine", TRIM, -1.5F, -2, -12, 3, 3, 24);
		for (int sd = -1; sd <= 1; sd += 2) {
			PartDefinition half = node(m, sd < 0 ? "pr" : "pl", 0, 0, 0);
			box(half, "cover", ARMOR, sd < 0 ? -18 : 0, -0.6F, -12.5F, 18, 1.4F, 25);
			box(half, "edge", TRIM, sd < 0 ? -18.6F : 17.6F, -1, -12.5F, 1, 2, 25);
			box(half, "pages", CLOTH, sd < 0 ? -16.5F : 0.5F, -2.6F, -11, 16, 2, 22);
			box(half, "glyph", CRYSTAL, sd < 0 ? -12 : 4, -2.9F, -6, 8, 0.4F, 12);
			box(half, "corner", TRIM, sd < 0 ? -18.6F : 15.6F, -1, -13, 3, 2, 3);
			box(half, "corner2", TRIM, sd < 0 ? -18.6F : 15.6F, -1, 10, 3, 2, 3);
		}
		PartDefinition fl = node(m, "flip", 0, -2, 0);
		box(fl, "pg", CLOTH_GLOW, 0, -0.3F, -10.5F, 15, 0.6F, 21);
		PartDefinition ring1 = node(m, "ring1", 0, -10, 0);
		ring(ring1, "r", LIGHT, 28, 30, 3.2F, 0.6F, 1.6F, true);
		for (int k = 0; k < 6; k++) { float ang = (float) (k * Math.PI / 3); box(ring1, "g" + k, CRYSTAL, -2, -2, -0.4F, 4, 4, 0.8F, at(Mth.cos(ang) * 30, -2, Mth.sin(ang) * 30, 0, -ang + Mth.HALF_PI, 0.785F)); }
		PartDefinition ring2 = node(m, "ring2", 0, -18, 0);
		ring(ring2, "r", CRYSTAL, 20, 20, 3, 0.6F, 1.4F, true);
	}

	// ---------------------------------------------------------------- 0 성검 (끝이 원점, 위로 솟음)
	static void sword(PartDefinition root) {
		PartDefinition s = node(root, "main", 0, 24, 0);
		// 칼날: 끝에서 위로 점점 넓어짐
		box(s, "tip0", BASE, -0.8F, -4, -0.8F, 1.6F, 4, 1.6F);
		box(s, "tip1", BASE, -1.6F, -9, -1.1F, 3.2F, 5, 2.2F);
		for (int i = 0; i < 9; i++) {
			float w = 4.2F + i * 0.7F, y = -9 - (i + 1) * 6.5F;
			box(s, "b" + i, BASE, -w / 2, y, -1.4F, w, 6.6F, 2.8F);
			box(s, "e" + i, LIGHT, -w / 2 - 0.4F, y, -0.5F, 0.5F, 6.6F, 1);
			box(s, "f" + i, LIGHT, w / 2 - 0.1F, y, -0.5F, 0.5F, 6.6F, 1);
		}
		box(s, "fuller", CRYSTAL, -0.7F, -66, -1.6F, 1.4F, 56, 3.2F);
		for (int i = 0; i < 6; i++) box(s, "rune" + i, LIGHT, -1.4F, -20 - i * 8, -1.8F, 2.8F, 2.8F, 0.3F, at(0, 0, 0, 0, 0, 0.785F));
		// 날밑: 날개처럼 펼친 금장식
		PartDefinition g = node(s, "guard", 0, -68, 0);
		box(g, "c", TRIM, -6, -3, -3, 12, 6, 6);
		box(g, "gem", CRYSTAL, -2.5F, -2.5F, -3.6F, 5, 5, 1.2F, at(0, 0, 0, 0, 0, 0.785F));
		for (int sd = -1; sd <= 1; sd += 2) {
			PartDefinition arm = node(g, "a" + sd, at(sd * 5.5F, 0, 0, 0, 0, sd * -0.35F));
			for (int k = 0; k < 3; k++) box(arm, "s" + k, TRIM, sd < 0 ? -9 - k * 8 : k * 8, -2 + k * 0.4F, -2, 9, 4 - k * 0.8F, 4, PartPose.ZERO);
			box(arm, "end", CRYSTAL, sd < 0 ? -27 : 22, -3, -2.5F, 5, 5, 5);
			PartDefinition wing = node(arm, "w", sd * 10, -2, 1.5F);
			for (int k = 0; k < 6; k++) {
				float a = (float) Math.toRadians(sd < 0 ? 200 + k * 14 : -20 - k * 14);
				float l = 12 + k * 2.4F;
				box(wing, "f" + k, CLOTH_GLOW, -1.4F, -l, -0.4F, 2.8F, l, 0.8F, at(0, 0, 0.1F * k, 0, 0, a + Mth.HALF_PI));
			}
		}
		// 손잡이 + 머리
		for (int i = 0; i < 4; i++) { box(s, "grip" + i, ARMOR, -2, -87 + i * 4.5F, -2, 4, 4.6F, 4); box(s, "wrap" + i, TRIM, -2.4F, -83 + i * 4.5F, -2.4F, 4.8F, 0.8F, 4.8F); }
		box(s, "pom", CRYSTAL, -3, -3, -3, 6, 6, 6, at(0, -92, 0, 0.785F, 0, 0.785F));
		box(s, "pomr", TRIM, -4, -1, -4, 8, 2, 8, at(0, -89, 0, 0, 0.785F, 0));
		PartDefinition runes = node(root, "runes", 0, -10, 0);
		ring(runes, "r", CRYSTAL, 10, 16, 3, 6, 0.8F, true);
	}

	// ---------------------------------------------------------------- 1 전투 도끼 (원점 = 자루 윗끝, 아래로 매달림)
	static void axe(PartDefinition root) {
		PartDefinition a = node(root, "arm", 0, 24, 0);
		for (int i = 0; i < 7; i++) { box(a, "h" + i, ARMOR, -2.2F, i * 10F, -2.2F, 4.4F, 10.1F, 4.4F); box(a, "r" + i, TRIM, -2.7F, i * 10F + 8.5F, -2.7F, 5.4F, 1.4F, 5.4F); }
		box(a, "pom", CRYSTAL, -3.5F, -5, -3.5F, 7, 6, 7);
		box(a, "spike", TRIM, -1.2F, -12, -1.2F, 2.4F, 8, 2.4F);
		PartDefinition head = node(a, "head", 0, 70, 0);
		box(head, "core", TRIM, -4, -6, -4, 8, 14, 8);
		box(head, "gem", CRYSTAL, -2, -2, -4.6F, 4, 4, 1, at(0, 2, 0, 0, 0, 0.785F));
		// 초승달 양날 (앞 = -z, 뒤 = +z)
		for (int sd = -1; sd <= 1; sd += 2) {
			float span = sd < 0 ? 30 : 18;
			int n = sd < 0 ? 9 : 6;
			for (int k = 0; k < n; k++) {
				float t = (k - (n - 1) / 2F) / ((n - 1) / 2F);
				float len = span * (1 - 0.35F * t * t);
				box(head, "bl" + sd + k, BASE, -1.5F, -2.5F, 0, 3, 5.2F, len, at(0, t * (sd < 0 ? 18 : 11), sd * 3.5F, 0, sd < 0 ? Mth.PI : 0, 0));
				box(head, "ed" + sd + k, LIGHT, -1, -2.6F, len - 0.4F, 2, 5.4F, 1.6F, at(0, t * (sd < 0 ? 18 : 11), sd * 3.5F, 0, sd < 0 ? Mth.PI : 0, 0));
			}
		}
		box(head, "bottom", TRIM, -1.5F, 8, -1.5F, 3, 10, 3);
	}

	// ---------------------------------------------------------------- 2 거대 단검 (끝이 원점, 위로)
	static void dagger(PartDefinition root) {
		PartDefinition d = node(root, "main", 0, 24, 0);
		for (int i = 0; i < 7; i++) {
			float w = 1.4F + i * 0.75F, y = -(i + 1) * 5F, off = (float) Math.sin(i * 0.4F) * 1.6F;
			box(d, "b" + i, BASE, -w / 2 + off, y, -0.9F, w, 5.1F, 1.8F);
			box(d, "e" + i, LIGHT, w / 2 + off - 0.2F, y, -0.4F, 0.6F, 5.1F, 0.8F);
		}
		box(d, "guard", TRIM, -7, -38, -2, 14, 3, 4);
		for (int sd = -1; sd <= 1; sd += 2) box(d, "gt" + sd, CRYSTAL, -1.5F, -1.5F, -1.5F, 3, 3, 3, at(sd * 8, -36.5F, 0, 0.785F, 0, 0.785F));
		for (int i = 0; i < 3; i++) box(d, "h" + i, ARMOR, -1.6F, -50 + i * 4, -1.6F, 3.2F, 4.1F, 3.2F);
		box(d, "ring", TRIM, -3, -54, -0.8F, 6, 4, 1.6F);
		box(d, "gem", CRYSTAL, -1.2F, -53.2F, -1.2F, 2.4F, 2.4F, 2.4F);
	}

	// ---------------------------------------------------------------- 3 하늘의 활 (가운데 원점, 앞 = -z)
	static void bow(PartDefinition root) {
		PartDefinition b = node(root, "main", 0, 24, 0);
		box(b, "grip", ARMOR, -2.5F, -6, -2.5F, 5, 12, 5);
		box(b, "gripr", TRIM, -3, -7, -3, 6, 2, 6);
		box(b, "gripr2", TRIM, -3, 5, -3, 6, 2, 6);
		box(b, "gem", CRYSTAL, -2, -2, -3.6F, 4, 4, 1.2F, at(0, 0, 0, 0, 0, 0.785F));
		for (int sd = -1; sd <= 1; sd += 2) {
			PartDefinition limb = node(b, "limb" + sd, 0, sd * 6, 0);
			float y = 0, z = 0;
			for (int k = 0; k < 8; k++) {
				float a = 0.08F + k * 0.07F;
				float len = 6.2F;
				box(limb, "s" + k, k % 3 == 2 ? TRIM : BASE, -1.6F + k * 0.08F, sd < 0 ? -len : 0, -1.4F, 3.2F - k * 0.16F, len, 2.8F, at(0, y, z, sd * -a, 0, 0));
				y += sd * len * Mth.cos(a); z += len * Mth.sin(a);
			}
			box(limb, "tip", CRYSTAL, -1.8F, -1.8F, -1.8F, 3.6F, 3.6F, 3.6F, at(0, y, z, 0.785F, 0.785F, 0));
			PartDefinition fe = node(limb, "fe", 0, y * 0.5F, z * 0.5F - 1);
			for (int k = 0; k < 4; k++) box(fe, "f" + k, CLOTH_GLOW, -0.4F, sd < 0 ? -10 + k : 0, 0, 0.8F, 10 - k, 3, at(0, 0, 0, sd * (0.5F + k * 0.25F), 0, 0));
		}
		// 시위 (윗·아랫 반쪽, 당김에 따라 회전) + 걸린 화살
		box(b, "su", LIGHT, -0.35F, 0, -0.35F, 0.7F, 1, 0.7F, PartPose.offset(0, -50, 22));
		node(b, "string_u", 0, -48.6F, 21.4F).addOrReplaceChild("s", c(LIGHT, -0.3F, 0, -0.3F, 0.6F, 48.6F, 0.6F), PartPose.ZERO);
		node(b, "string_d", 0, 48.6F, 21.4F).addOrReplaceChild("s", c(LIGHT, -0.3F, -48.6F, -0.3F, 0.6F, 48.6F, 0.6F), PartPose.ZERO);
		PartDefinition ar = node(b, "nocked", 0, 0, 21.4F);
		box(ar, "shaft", ARMOR, -0.8F, -0.8F, -60, 1.6F, 1.6F, 60);
		box(ar, "head", CRYSTAL, -2.2F, -2.2F, -66, 4.4F, 4.4F, 6, at(0, 0, 0, 0, 0, 0.785F));
		for (int k = 0; k < 3; k++) box(ar, "fl" + k, CLOTH_GLOW, -0.3F, 0, -1, 0.6F, 4, 8, at(0, 0, 0, 0, 0, k * 2.094F));
	}

	// ---------------------------------------------------------------- 4 하늘 화살 (앞 = -z)
	static void arrow(PartDefinition root) {
		PartDefinition a = node(root, "main", 0, 24, 0);
		box(a, "shaft", ARMOR, -1.2F, -1.2F, -30, 2.4F, 2.4F, 60);
		for (int i = 0; i < 4; i++) box(a, "band" + i, TRIM, -1.6F, -1.6F, -24 + i * 14, 3.2F, 3.2F, 1.4F);
		box(a, "head", CRYSTAL, -3.5F, -3.5F, -4, 7, 7, 8, at(0, 0, -34, 0, 0, 0.785F));
		box(a, "head2", LIGHT, -1.5F, -1.5F, -4, 3, 3, 6, at(0, 0, -39, 0, 0, 0.785F));
		for (int k = 0; k < 4; k++) box(a, "fl" + k, CLOTH_GLOW, -0.4F, 0, 0, 0.8F, 6, 12, at(0, 0, 18, 0, 0, k * Mth.HALF_PI));
		for (int k = 0; k < 6; k++) box(a, "trail" + k, LIGHT, -0.8F + k * 0.1F, -0.8F + k * 0.1F, 0, 1.6F - k * 0.2F, 1.6F - k * 0.2F, 8, PartPose.offset(0, 0, 30 + k * 8));
	}

	// ---------------------------------------------------------------- 5 명계의 사신 (아래 원점, 다리 없이 넝마 자락)
	static void reaper(PartDefinition root) {
		PartDefinition r = node(root, "main", 0, 24, 0);
		// 넝마 자락 (아래로 갈수록 갈라짐)
		PartDefinition rags = node(r, "rags", 0, -18, 0);
		for (int k = 0; k < 12; k++) {
			float a = (float) (k * Math.PI * 2 / 12);
			float len = 14 + (k * 7 % 5) * 2.2F;
			box(rags, "r" + k, k % 2 == 0 ? CLOTH : CLOTH_GLOW, -2.4F, 0, -0.6F, 4.8F, len, 1.2F, at(Mth.cos(a) * 7, 0, Mth.sin(a) * 6, 0.15F * Mth.sin(a), -a + Mth.HALF_PI, 0));
		}
		taper(r, "robe", CLOTH, -52, -18, 14, 18, 10, 13, 5);
		box(r, "belt", TRIM, -8, -36, -6, 16, 2.4F, 12);
		taper(r, "chest", DARK, -66, -52, 15, 14, 9, 9, 2);
		// 갈비뼈 장식
		for (int k = 0; k < 4; k++) box(r, "rib" + k, TRIM, -6 + k * 0.4F, -63 + k * 3, -5.2F, 12 - k * 0.8F, 1.2F, 0.8F);
		// 두건
		PartDefinition hood = node(r, "hood", 0, -68, 0);
		box(hood, "h", CLOTH, -8, -15, -7, 16, 16, 15);
		box(hood, "brim", CLOTH, -8.5F, -16, -8.6F, 17, 4, 4);
		box(hood, "face", DARK, -6, -12, -7.4F, 12, 11, 1);
		for (int sd = -1; sd <= 1; sd += 2) box(hood, "eye" + sd, LIGHT, -1.4F, -0.8F, -0.4F, 2.8F, 1.6F, 0.6F, PartPose.offset(sd * 2.6F, -7, -7.6F));
		box(hood, "tail", CLOTH, -4, -2, 0, 8, 4, 10, at(0, -12, 7, -0.6F, 0, 0));
		// 어깨 해골 갑
		for (int sd = -1; sd <= 1; sd += 2) box(r, "sh" + sd, TRIM, -5, -4, -5, 10, 6, 10, PartPose.offset(sd * 11, -66, 0));
		// 팔 (오른팔에 대낫)
		PartDefinition ra = node(r, "arm_r", -12, -62, 0);
		box(ra, "u", BASE, -2, 0, -2, 4, 16, 4);
		PartDefinition rf = node(ra, "f", 0, 16, 0);
		box(rf, "l", BASE, -1.6F, 0, -1.6F, 3.2F, 14, 3.2F);
		box(rf, "hand", TRIM, -2.5F, 13, -2.5F, 5, 5, 5);
		PartDefinition sc = node(rf, "scythe", 0, 15, 0);
		for (int i = 0; i < 8; i++) box(sc, "h" + i, ARMOR, -1.4F, -30 + i * 10, -1.4F, 2.8F, 10.1F, 2.8F);
		box(sc, "top", TRIM, -2.5F, -34, -2.5F, 5, 5, 5);
		for (int k = 0; k < 10; k++) {
			float a = k * 0.16F;
			box(sc, "bl" + k, BASE, -1, -2.2F, 0, 2, 4.4F + (k < 7 ? 1 : -k * 0.2F), 6.2F, at(0, -32 + Mth.sin(a) * 26, -Mth.cos(a) * 0 - k * 5.6F + 2, -a * 0.9F, 0, 0));
			box(sc, "be" + k, LIGHT, -0.6F, 2.4F, 0, 1.2F, 1, 6.2F, at(0, -32 + Mth.sin(a) * 26, -k * 5.6F + 2, -a * 0.9F, 0, 0));
		}
		PartDefinition la = node(r, "arm_l", 12, -62, 0);
		box(la, "u", BASE, -2, 0, -2, 4, 16, 4);
		box(la, "f", BASE, -1.6F, 16, -1.6F, 3.2F, 14, 3.2F);
		box(la, "hand", TRIM, -2.5F, 29, -2.5F, 5, 5, 5);
		box(la, "orb", CRYSTAL, -3, 34, -3, 6, 6, 6);
	}

	// ---------------------------------------------------------------- 6 성역의 여신 (아래 원점)
	static void goddess(PartDefinition root) {
		PartDefinition g = node(root, "main", 0, 24, 0);
		taper(g, "robe", CLOTH, -46, 0, 12, 26, 9, 20, 6);
		for (int k = 0; k < 10; k++) {
			float a = (float) (k * Math.PI * 2 / 10);
			box(g, "fold" + k, CLOTH_GLOW, -1.6F, -40, -0.5F, 3.2F, 40, 1, at(Mth.cos(a) * 11, 0, Mth.sin(a) * 8.5F, 0.06F * Mth.sin(a), -a + Mth.HALF_PI, 0));
		}
		box(g, "sash", TRIM, -7, -48, -5.4F, 14, 3, 10.8F);
		taper(g, "torso", CLOTH, -66, -46, 10, 12, 7, 8, 3);
		box(g, "chest", TRIM, -5, -64, -4.4F, 10, 6, 1);
		box(g, "gem", CRYSTAL, -1.5F, -62.5F, -5, 3, 3, 1, at(0, 0, 0, 0, 0, 0));
		PartDefinition head = node(g, "head", 0, -68, 0);
		box(head, "face", BASE, -4, -9, -4, 8, 9, 8);
		box(head, "veil", CLOTH, -5, -10, -4, 10, 4, 9.5F);
		for (int sd = -1; sd <= 1; sd += 2) box(head, "vs" + sd, CLOTH, -1, -7, -4, 2, 22, 9, PartPose.offset(sd * 4.6F, 0, 0.5F));
		box(head, "vb", CLOTH, -5, -7, 0, 10, 24, 1.4F, PartPose.offset(0, 0, 4.6F));
		box(head, "crown", TRIM, -4.6F, -11.4F, -4.6F, 9.2F, 2, 9.2F);
		for (int k = -2; k <= 2; k++) box(head, "cp" + k, CRYSTAL, -0.8F, -4 + Math.abs(k), -0.8F, 1.6F, 4 - Math.abs(k), 1.6F, PartPose.offset(k * 2.2F, -11, -4.4F));
		PartDefinition halo = node(g, "halo", 0, -74, 8);
		ring(halo, "h", TRIM, 24, 12, 3.4F, 1.4F, 1, false);
		ring(halo, "g", CRYSTAL, 8, 12, 2, 2, 1.4F, false);
		for (int sd = -1; sd <= 1; sd += 2) {
			PartDefinition arm = node(g, sd < 0 ? "arm_r" : "arm_l", sd * 7, -63, 0);
			box(arm, "u", CLOTH, -2, 0, -2, 4, 12, 4);
			PartDefinition f = node(arm, "f", 0, 12, 0);
			box(f, "s", CLOTH_GLOW, -2.4F, 0, -2.4F, 4.8F, 11, 4.8F);
			box(f, "h", BASE, -1.5F, 11, -1.5F, 3, 4, 3);
			box(f, "orb", LIGHT, -2.5F, 15, -2.5F, 5, 5, 5);
			PartDefinition wing = node(g, sd < 0 ? "wing_r" : "wing_l", sd * 4, -60, 5);
			for (int k = 0; k < 9; k++) {
				float a = (float) Math.toRadians(sd < 0 ? 196 + k * 11 : -16 - k * 11);
				float l = 22 + (k % 3) * 5;
				box(wing, "f" + k, k % 3 == 2 ? CLOTH_GLOW : CLOTH, -1.6F, -l, -0.5F, 3.2F, l, 1, at(0, 0, 0.2F * k, 0, 0, a + Mth.HALF_PI));
			}
		}
	}

	// ---------------------------------------------------------------- 7 거신 (허리까지 땅에서 솟음, 아래 원점)
	static void titan(PartDefinition root) {
		PartDefinition t = node(root, "main", 0, 24, 0);
		taper(t, "waist", ARMOR, -14, 0, 18, 22, 12, 14, 2);
		box(t, "belt", TRIM, -11, -16, -7.6F, 22, 4, 15.2F);
		for (int sd = -1; sd <= 1; sd += 2) {
			box(t, "chest" + sd, ARMOR, sd < 0 ? -13 : 0, -38, -8, 13, 22, 16);
			box(t, "pec" + sd, BASE, sd < 0 ? -11 : 1, -36, -9, 10, 9, 1.6F);
		}
		box(t, "core", CRYSTAL, -3, -32, -9.6F, 6, 6, 1.6F, at(0, 0, 0, 0, 0, 0));
		for (int k = 0; k < 3; k++) box(t, "ab" + k, BASE, -5, -26 + k * 3.4F, -8.4F, 10, 2.6F, 1);
		PartDefinition head = node(t, "head", 0, -40, 0);
		box(head, "neck", ARMOR, -4, -3, -4, 8, 4, 8);
		box(head, "skull", BASE, -6, -14, -6, 12, 12, 12);
		box(head, "jaw", ARMOR, -5, -5, -6.6F, 10, 4, 3);
		for (int sd = -1; sd <= 1; sd += 2) box(head, "eye" + sd, LIGHT, -1.6F, -0.8F, -0.3F, 3.2F, 1.6F, 0.6F, PartPose.offset(sd * 2.8F, -9, -6.4F));
		// 불: 뿔 + 불꽃 왕관
		PartDefinition fire = node(head, "fire", 0, 0, 0);
		for (int sd = -1; sd <= 1; sd += 2) {
			PartDefinition h1 = node(fire, "horn" + sd, at(sd * 6, -12, -1, -0.3F, 0, sd * -0.6F));
			box(h1, "a", DARK, -1.6F, -7, -1.6F, 3.2F, 7, 3.2F);
			box(h1, "b", DARK, -1.1F, -6, -1.1F, 2.2F, 6, 2.2F, at(0, -7, 0, -0.5F, 0, sd * 0.4F));
		}
		for (int k = 0; k < 7; k++) { float a = (float) (Math.PI + k * Math.PI / 6); box(fire, "fl" + k, CRYSTAL, -1.2F, -6 - (k % 2) * 4, -1.2F, 2.4F, 6 + (k % 2) * 4, 2.4F, at(Mth.cos(a) * 5, -14, -2 + Mth.sin(a) * 0, 0, 0, (k - 3) * 0.18F)); }
		// 전기: 뿔 투구 + 볏
		PartDefinition elec = node(head, "elec", 0, 0, 0);
		box(elec, "helm", TRIM, -6.6F, -15, -6.6F, 13.2F, 6, 13.2F);
		box(elec, "crest", CRYSTAL, -1, -24, -6, 2, 10, 13);
		for (int sd = -1; sd <= 1; sd += 2) box(elec, "w" + sd, TRIM, -1, -10, -2, 2, 10, 5, at(sd * 7, -12, 0, 0, 0, sd * -0.5F));
		// 어깨
		for (int sd = -1; sd <= 1; sd += 2) {
			box(t, "sh" + sd, TRIM, -7, -6, -8, 14, 9, 16, PartPose.offset(sd * 17, -38, 0));
			box(t, "shs" + sd, CRYSTAL, -1.5F, -8, -1.5F, 3, 8, 3, PartPose.offset(sd * 19, -43, 0));
		}
		// 팔
		for (int sd = -1; sd <= 1; sd += 2) {
			PartDefinition arm = node(t, sd < 0 ? "arm_r" : "arm_l", sd * 18, -35, 0);
			box(arm, "u", ARMOR, -4.5F, 0, -4.5F, 9, 18, 9);
			PartDefinition f = node(arm, "f", 0, 18, 0);
			box(f, "l", BASE, -4, 0, -4, 8, 16, 8);
			box(f, "cuff", TRIM, -5, 2, -5, 10, 4, 10);
			box(f, "fist", ARMOR, -5, 15, -5.5F, 10, 9, 11);
			box(f, "glow", LIGHT, -4.2F, 23.4F, -4.8F, 8.4F, 1, 9.6F);
			if (sd < 0) {   // 오른손: 뇌신의 망치 (전기만 보임)
				PartDefinition ham = node(f, "hammer", 0, 20, 0);
				for (int i = 0; i < 4; i++) box(ham, "h" + i, ARMOR, -1.6F, -28 + i * 8, -1.6F, 3.2F, 8.1F, 3.2F, PartPose.offset(0, 0, -6));
				box(ham, "head", TRIM, -7, -9, -12, 14, 12, 12, PartPose.offset(0, -30, 0));
				box(ham, "gem", CRYSTAL, -3, -6, -12.6F, 6, 6, 1, PartPose.offset(0, -30, 0));
				box(ham, "gem2", CRYSTAL, -3, -6, -0.4F, 6, 6, 1, PartPose.offset(0, -30, 0));
			}
		}
	}

	// ---------------------------------------------------------------- 8 리바이어던 (머리 원점, 몸은 +z 뒤로 이어짐)
	static void leviathan(PartDefinition root) {
		PartDefinition head = node(root, "head", 0, 24, 0);
		box(head, "skull", BASE, -7, -6, -10, 14, 10, 14);
		box(head, "snout", BASE, -5, -4, -22, 10, 6, 12);
		PartDefinition jaw = node(head, "jaw", 0, 4, -4);
		box(jaw, "j", ARMOR, -5.5F, 0, -18, 11, 4, 18);
		for (int k = 0; k < 6; k++) box(jaw, "t" + k, TRIM, -4.5F + k * 1.8F, -2, -17.5F, 1, 2, 1);
		for (int k = 0; k < 6; k++) box(head, "ut" + k, TRIM, -4.5F + k * 1.8F, 2, -21.5F, 1, 2, 1);
		for (int sd = -1; sd <= 1; sd += 2) {
			box(head, "eye" + sd, LIGHT, -0.5F, -1.2F, -1.6F, 1, 2.4F, 3.2F, PartPose.offset(sd * 7, -2, -7));
			PartDefinition h = node(head, "horn" + sd, at(sd * 5, -6, -2, 0.7F, 0, sd * -0.4F));
			for (int k = 0; k < 3; k++) box(h, "s" + k, TRIM, -1.4F + k * 0.3F, 0, -1.4F + k * 0.3F, 2.8F - k * 0.6F, 6, 2.8F - k * 0.6F, at(0, 0, k * 5.2F, 0.3F * k, 0, 0));
			PartDefinition fin = node(head, "fin" + sd, at(sd * 7, 0, 0, 0, sd * 0.5F, sd * 0.3F));
			for (int k = 0; k < 4; k++) box(fin, "f" + k, CLOTH_GLOW, -0.4F, -2, 0, 0.8F, 4 + k * 2, 10 - k, at(0, 0, k * 2, -0.3F * k, 0, 0));
			for (int k = 0; k < 3; k++) box(head, "wh" + sd + k, CRYSTAL, -0.3F, -0.3F, 0, 0.6F, 0.6F, 10, at(sd * 4, 1, -20, 0.2F + k * 0.15F, sd * (2.5F + k * 0.2F), 0));
		}
		box(head, "crest", CLOTH_GLOW, -0.5F, -12, -6, 1, 8, 14);
		PartDefinition prev = head;
		float z = 4;
		for (int i = 0; i < 14; i++) {
			float w = 12 - i * 0.6F;
			PartDefinition seg = node(prev, "seg" + i, 0, 0, z);
			box(seg, "b", BASE, -w / 2, -w / 2, 0, w, w, 9);
			box(seg, "belly", TRIM, -w / 2 + 1, w / 2 - 0.6F, 0.5F, w - 2, 1.2F, 8);
			box(seg, "fin", CLOTH_GLOW, -0.5F, -w / 2 - 5, 1, 1, 5, 6);
			if (i % 3 == 1) for (int sd = -1; sd <= 1; sd += 2) box(seg, "sf" + sd, CLOTH_GLOW, -0.4F, 0, 0, 0.8F, 8, 7, at(sd * w / 2, 0, 1, 0, 0, sd * 1.9F));
			prev = seg; z = 9;
		}
		PartDefinition tail = node(prev, "tailfin", 0, 0, 9);
		for (int sd = -1; sd <= 1; sd += 2) box(tail, "t" + sd, CLOTH_GLOW, -0.5F, 0, 0, 1, 12, 8, at(0, 0, 0, sd * 0.9F, 0, 0));
	}

	// ---------------------------------------------------------------- 9 세계수 (아래 원점)
	static void tree(PartDefinition root) {
		PartDefinition t = node(root, "main", 0, 24, 0);
		for (int k = 0; k < 8; k++) {   // 뿌리
			float a = (float) (k * Math.PI / 4);
			PartDefinition r = node(t, "root" + k, at(Mth.cos(a) * 5, -1, Mth.sin(a) * 5, 0, -a, 0));
			box(r, "a", BASE, 0, -2.5F, -2, 12, 5, 4, at(0, 0, 0, 0, 0, 0.35F));
			box(r, "b", BASE, 10, 0, -1.5F, 10, 3.6F, 3, at(0, 0, 0, 0, 0, 0.15F));
			box(r, "g", CRYSTAL, 19, 1, -1, 3, 2, 2);
		}
		PartDefinition trunk = t;
		for (int i = 0; i < 8; i++) {   // 꼬인 줄기
			float w = 18 - i * 1.5F;
			PartDefinition seg = node(trunk, "tr" + i, at(0, i == 0 ? 0 : -9, 0, 0, 0.18F, 0));
			box(seg, "b", BASE, -w / 2, -9.2F, -w / 2, w, 9.4F, w);
			if (i % 2 == 0) box(seg, "v", CRYSTAL, -w / 2 - 0.3F, -8, -1, 0.6F, 5, 2);
			if (i >= 3) {
				int nb = 2;
				for (int b = 0; b < nb; b++) {
					float a = (float) (i * 1.7 + b * Math.PI);
					PartDefinition br = node(seg, "br" + b, at(0, -5, 0, 0, a, -0.9F + i * 0.05F));
					box(br, "l", BASE, -1.6F, -18 + i, -1.6F, 3.2F, 18 - i, 3.2F);
					PartDefinition leaf = node(br, "leaf", 0, -18 + i, 0);
					box(leaf, "c1", CLOTH, -9, -7, -9, 18, 9, 14);
					box(leaf, "c1b", CLOTH, -7, -7, 5, 14, 9, 6);
					box(leaf, "c2", CLOTH, -6, -12, -6, 12, 6, 12);
					box(leaf, "bl", CRYSTAL, -2, -11, -2, 4, 2, 4);
					box(leaf, "fr", LIGHT, 4, 1, -1, 2, 2, 2);
				}
			}
			trunk = seg;
		}
		PartDefinition crown = node(trunk, "crown", 0, -9, 0);
		for (int q = 0; q < 4; q++) box(crown, "c1" + q, CLOTH, q % 2 == 0 ? -16 : 0, -10, q < 2 ? -16 : 0, 16, 12, 16);
		for (int q = 0; q < 6; q++) { float qa = (float) (q * Math.PI / 3); box(crown, "cs" + q, CLOTH, -8, -6, -8, 16, 10, 16, at(Mth.cos(qa) * 18, -2, Mth.sin(qa) * 18, 0, qa, 0)); }
		box(crown, "c2a", CLOTH, -12, -20, -12, 12, 11, 24);
		box(crown, "c2b", CLOTH, 0, -20, -12, 12, 11, 24);
		box(crown, "c3", CLOTH_GLOW, -6, -24, -6, 12, 7, 12);
		for (int k = 0; k < 10; k++) { float a = (float) (k * Math.PI / 5); box(crown, "b" + k, CRYSTAL, -1.5F, -1.5F, -1.5F, 3, 3, 3, at(Mth.cos(a) * 13, -6 - (k % 3) * 5, Mth.sin(a) * 13, 0.7F, 0.7F, 0)); }
		box(crown, "core", LIGHT, -3, -30, -3, 6, 6, 6, at(0, 0, 0, 0.785F, 0.785F, 0));
	}

	// ---------------------------------------------------------------- 10 폭풍의 대붕 (가운데 원점, 앞 = -z)
	static void roc(PartDefinition root) {
		PartDefinition b = node(root, "main", 0, 24, 0);
		box(b, "body", CLOTH, -7, -7, -12, 14, 12, 26);
		box(b, "chest", CLOTH_GLOW, -6, -2, -13, 12, 8, 10);
		PartDefinition neck = node(b, "neck", 0, -3, -12);
		box(neck, "n", CLOTH, -4, -5, -8, 8, 8, 9, at(0, 0, 0, 0.3F, 0, 0));
		PartDefinition head = node(neck, "head", 0, -4, -8);
		box(head, "h", BASE, -4.5F, -5, -9, 9, 8, 10);
		box(head, "beak", TRIM, -2, -2, -16, 4, 4, 8);
		box(head, "beak2", TRIM, -1.4F, 1, -15, 2.8F, 2, 6, at(0, 0, 0, 0.3F, 0, 0));
		for (int sd = -1; sd <= 1; sd += 2) box(head, "eye" + sd, LIGHT, -0.4F, -0.8F, -1, 0.8F, 1.6F, 2, PartPose.offset(sd * 4.5F, -2, -6));
		for (int k = 0; k < 5; k++) box(head, "crest" + k, CLOTH_GLOW, -0.6F, -10 + k, 0, 1.2F, 10 - k, 2.4F, at(0, -4, -3 + k * 2.2F, -0.6F - k * 0.15F, 0, 0));
		for (int sd = -1; sd <= 1; sd += 2) {
			PartDefinition w = node(b, sd < 0 ? "wing_r" : "wing_l", sd * 6, -4, -4);
			box(w, "arm", CLOTH, sd < 0 ? -20 : 0, -1.5F, -3, 20, 3, 10);
			for (int k = 0; k < 8; k++) box(w, "f" + k, k % 2 == 0 ? CLOTH : CLOTH_GLOW, -1.4F, 0, -1, 2.8F, 18 + k, 2, at(sd * (2 + k * 2.4F), 1, 5, Mth.HALF_PI - 0.2F + k * 0.03F, 0, 0));
			PartDefinition w2 = node(w, "outer", sd * 20, 0, 0);
			box(w2, "arm", CLOTH, sd < 0 ? -18 : 0, -1.2F, -2.5F, 18, 2.4F, 8);
			for (int k = 0; k < 8; k++) box(w2, "f" + k, k % 3 == 0 ? CLOTH_GLOW : CLOTH, -1.4F, 0, -1, 2.8F, 22 - k * 1.2F, 2, at(sd * (2 + k * 2.2F), 0.5F, 4, Mth.HALF_PI - 0.1F - k * 0.06F, sd * 0.06F * k, 0));
		}
		PartDefinition tail = node(b, "tail", 0, -2, 13);
		for (int k = 0; k < 9; k++) box(tail, "t" + k, k % 2 == 0 ? CLOTH_GLOW : CLOTH, -1.5F, 0, 0, 3, 1.2F, 26 - Math.abs(k - 4) * 2.5F, at(0, 0, 0, 0.1F, (k - 4) * 0.13F, 0));
		for (int sd = -1; sd <= 1; sd += 2) {
			box(b, "leg" + sd, TRIM, -1.4F, 4, -1.4F, 2.8F, 6, 2.8F, PartPose.offset(sd * 3.5F, 0, 4));
			box(b, "claw" + sd, DARK, -2.4F, 10, -3, 4.8F, 2, 5, PartPose.offset(sd * 3.5F, 0, 4));
		}
	}

	// ---------------------------------------------------------------- 11 심연의 문 (가운데 원점, 앞 = -z): 가시 고리 + 눈 + 두 팔
	static void abyss(PartDefinition root) {
		PartDefinition a = node(root, "main", 0, 24, 0);
		PartDefinition ring = node(a, "ring", 0, 0, 0);
		ring(ring, "r", ARMOR, 36, 30, 5.6F, 5, 5, false);
		ring(ring, "t", TRIM, 36, 26.5F, 5, 1.2F, 5.4F, false);
		for (int k = 0; k < 16; k++) { float ang = (float) (k * Math.PI / 8); box(ring, "sp" + k, DARK, -1.6F, -10 - (k % 2) * 5, -1.6F, 3.2F, 10 + (k % 2) * 5, 3.2F, at(Mth.cos(ang) * 32, Mth.sin(ang) * 32, 0, 0, 0, ang + Mth.HALF_PI)); }
		PartDefinition swirl = node(a, "swirl", 0, 0, 1.5F);
		for (int k = 0; k < 18; k++) { float ang = (float) (k * Math.PI / 9); box(swirl, "s" + k, k % 3 == 0 ? CRYSTAL : DARK, -1, -10, -0.4F, 2, 20, 0.8F, at(Mth.cos(ang) * 13, Mth.sin(ang) * 13, 0, 0, 0, ang + 0.6F)); }
		box(a, "void", DARK, -22, -22, 3, 22, 44, 1);
		box(a, "void2", DARK, 0, -22, 3, 22, 44, 1);
		// 눈
		PartDefinition eye = node(a, "eye", 0, 0, -1);
		box(eye, "white", LIGHT, -10, -5, -1, 20, 10, 2);
		box(eye, "iris", CRYSTAL, -4.5F, -4.5F, -1.6F, 9, 9, 1);
		box(eye, "pupil", DARK, -1.2F, -4, -2, 2.4F, 8, 1);
		box(eye, "lid_u", ARMOR, -11, -12, -2.4F, 22, 7, 2, PartPose.offset(0, 0, 0));
		box(eye, "lid_d", ARMOR, -11, 5, -2.4F, 22, 7, 2, PartPose.offset(0, 0, 0));
		// 두 팔
		for (int sd = -1; sd <= 1; sd += 2) {
			PartDefinition arm = node(a, sd < 0 ? "arm_r" : "arm_l", sd * 17, 6, -2);
			box(arm, "u", ARMOR, -4, -4, -26, 8, 8, 26);
			for (int k = 0; k < 4; k++) box(arm, "sp" + k, DARK, -1, -8, -1, 2, 5, 2, PartPose.offset(0, -2, -4 - k * 6));
			PartDefinition f = node(arm, "f", 0, 0, -26);
			box(f, "l", BASE, -3.5F, -3.5F, -22, 7, 7, 22);
			box(f, "cuff", TRIM, -4.5F, -4.5F, -6, 9, 9, 4);
			PartDefinition hand = node(f, "hand", 0, 0, -22);
			box(hand, "palm", BASE, -5, -2, -8, 10, 4, 8);
			for (int k = 0; k < 4; k++) {
				PartDefinition fg = node(hand, "fg" + k, at(-4 + k * 2.7F, 0, -8, 0, 0, 0));
				box(fg, "a", BASE, -0.9F, -0.9F, -6, 1.8F, 1.8F, 6);
				box(fg, "b", DARK, -0.7F, -0.7F, -5, 1.4F, 1.4F, 5, at(0, 0, -6, 0.6F, 0, 0));
			}
			box(hand, "thumb", BASE, -0.9F, -0.9F, -6, 1.8F, 1.8F, 6, at(sd * -5, 0, -2, 0, sd * 0.8F, 0));
		}
	}

	// ================================================================== 움직임
	public static class AllModel extends EntityModel<State> {
		private final Anim[] anims = new Anim[COUNT];
		private final ModelPart[] subs = new ModelPart[COUNT];
		public AllModel(ModelPart root) {
			super(root);
			for (int k = 0; k < COUNT; k++) { subs[k] = root.getChild("k" + k); anims[k] = new Anim(subs[k], k); }
		}
		@Override
		public void setupAnim(State s) {
			super.setupAnim(s);
			for (int k = 0; k < COUNT; k++) subs[k].visible = k == s.kind;
			if (s.kind >= 0 && s.kind < COUNT) anims[s.kind].setupAnim(s);
		}
	}

	public static class Anim {
		private final int kind;
		private final ModelPart root;
		public Anim(ModelPart root, int kind) { this.root = root; this.kind = kind; }

		private ModelPart get(String... path) {
			ModelPart m = root;
			try { for (String p : path) m = m.getChild(p); } catch (Exception e) { return null; }
			return m;
		}
		private static float ease(float x) { x = Mth.clamp(x, 0, 1); return x * x * (3 - 2 * x); }

		public void setupAnim(State s) {
			float t = s.time, ph = Mth.clamp(s.phase, 0, 1), a = s.strikeAge;
			switch (kind) {
				case SWORD -> {
					ModelPart r = get("runes");
					if (r != null) { r.yRot = t * 0.08F; r.visible = ph > 0.05F; r.y = -10 - ph * 30; }
				}
				case AXE -> {
					ModelPart arm = get("main", "arm");
					if (arm == null) arm = get("arm");
					// 들어올렸다가(10틱) 내려찍기(4틱)
					float ang = a < 10 ? -2.7F * ease(a / 10F) : a < 14 ? -2.7F + 2.85F * ease((a - 10) / 4F) : a < 30 ? 0.15F - 0.15F * ease((a - 14) / 16F) : 0;
					if (s.strike <= 0) ang = -2.7F * ph;
					if (arm != null) arm.xRot = ang;
				}
				case BOW, SPIRIT_BOW -> {
					float pull = ph;
					ModelPart su = get("main", "string_u"), sd = get("main", "string_d"), nock = get("main", "nocked");
					float back = 21.4F + pull * 16;
					if (su != null) su.xRot = -(float) Math.atan2(back - 21.4F, 48.6F);
					if (sd != null) sd.xRot = (float) Math.atan2(back - 21.4F, 48.6F);
					if (nock != null) { nock.z = back; nock.visible = s.strike <= 0; }
				}
				case REAPER -> {
					ModelPart hood = get("main", "hood"), rags = get("main", "rags"), ra = get("main", "arm_r"), la = get("main", "arm_l");
					if (hood != null) hood.xRot = Mth.sin(t * 0.07F) * 0.05F + 0.1F;
					if (rags != null) rags.yRot = Mth.sin(t * 0.1F) * 0.08F;
					if (ra != null) { ra.zRot = 1.35F; ra.xRot = -0.2F + Mth.sin(t * 0.08F) * 0.05F; }
					if (la != null) { la.zRot = -0.5F - ph * 0.6F; la.xRot = -0.6F; }
				}
				case GODDESS -> {
					ModelPart halo = get("main", "halo"), ra = get("main", "arm_r"), la = get("main", "arm_l"), wr = get("main", "wing_r"), wl = get("main", "wing_l");
					if (halo != null) halo.zRot = t * 0.03F;
					float open = ease(ph);
					if (ra != null) { ra.zRot = 0.3F + open * 1.0F; ra.xRot = -0.9F + open * 0.5F; }
					if (la != null) { la.zRot = -0.3F - open * 1.0F; la.xRot = -0.9F + open * 0.5F; }
					float f = Mth.sin(t * 0.08F) * 0.06F;
					if (wr != null) wr.zRot = f - (1 - open) * 0.6F;
					if (wl != null) wl.zRot = -f + (1 - open) * 0.6F;
				}
				case TITAN -> {
					ModelPart fire = get("main", "head", "fire"), elec = get("main", "head", "elec"), ham = get("main", "arm_r", "f", "hammer");
					boolean isElec = s.elem == 0;
					if (fire != null) fire.visible = !isElec;
					if (elec != null) elec.visible = isElec;
					if (ham != null) ham.visible = isElec;
					ModelPart ra = get("main", "arm_r"), la = get("main", "arm_l"), raf = get("main", "arm_r", "f"), laf = get("main", "arm_l", "f"), head = get("main", "head");
					float br = Mth.sin(t * 0.06F);
					// 내려치기: 팔을 들어 올렸다가 내리꽂음 (짝수 오른팔, 홀수 왼팔)
					boolean right = (s.strike & 1) == 0;
					float lift = a < 8 ? ease(a / 8F) : a < 12 ? 1 - ease((a - 8) / 4F) * 1.3F : a < 24 ? -0.3F + 0.3F * ease((a - 12) / 12F) : 0;
					if (s.strike <= 0) lift = 0;
					float both = ph > 0.92F ? ease((ph - 0.92F) / 0.08F) : 0;
					if (ra != null) { ra.xRot = -0.3F - (right ? lift * 2.6F : 0) - both * 2.8F + br * 0.04F; ra.zRot = 0.25F; }
					if (la != null) { la.xRot = -0.3F - (!right ? lift * 2.6F : 0) - both * 2.8F - br * 0.04F; la.zRot = -0.25F; }
					if (raf != null) raf.xRot = -0.5F - (right ? Math.max(0, lift) * 0.4F : 0);
					if (laf != null) laf.xRot = -0.5F - (!right ? Math.max(0, lift) * 0.4F : 0);
					if (head != null) { head.xRot = 0.15F + br * 0.03F - both * 0.4F; head.yRot = Mth.sin(t * 0.03F) * 0.15F; }
				}
				case LEVIATHAN -> {
					ModelPart m = get("head");
					for (int i = 0; i < 14 && m != null; i++) {
						m = get(path(i));
						if (m == null) break;
						m.yRot = Mth.sin(t * 0.14F - i * 0.55F) * 0.22F;
						m.xRot = Mth.sin(t * 0.09F - i * 0.4F) * 0.12F;
					}
					ModelPart jaw = get("head", "jaw");
					if (jaw != null) jaw.xRot = (a < 20 && s.strike > 0) ? 0.8F * Mth.sin(a / 20F * Mth.PI) : 0.08F + Mth.sin(t * 0.1F) * 0.05F;
				}
				case TREE -> {
					ModelPart crown = get("main");
					if (crown != null) crown.yRot = Mth.sin(t * 0.02F) * 0.03F;
				}
				case ROC -> {
					float f = Mth.sin(t * 0.25F);
					ModelPart wr = get("main", "wing_r"), wl = get("main", "wing_l"), wr2 = get("main", "wing_r", "outer"), wl2 = get("main", "wing_l", "outer"), tail = get("main", "tail"), neck = get("main", "neck");
					if (wr != null) wr.zRot = f * 0.55F;
					if (wl != null) wl.zRot = -f * 0.55F;
					if (wr2 != null) wr2.zRot = Mth.sin(t * 0.25F - 0.8F) * 0.45F;
					if (wl2 != null) wl2.zRot = -Mth.sin(t * 0.25F - 0.8F) * 0.45F;
					if (tail != null) tail.xRot = Mth.sin(t * 0.12F) * 0.1F;
					if (neck != null) neck.xRot = (s.strike > 0 && a < 16 ? 0.4F * Mth.sin(a / 16F * Mth.PI) : 0) + Mth.sin(t * 0.1F) * 0.05F;
				}
				case ABYSS -> {
					ModelPart sw = get("main", "swirl"), lu = get("main", "eye", "lid_u"), ld = get("main", "eye", "lid_d"), ring = get("main", "ring");
					if (sw != null) sw.zRot = t * 0.05F;
					if (ring != null) ring.zRot = -t * 0.006F;
					float open = ease(ph * 3);
					if (lu != null) lu.y = -open * 7;
					if (ld != null) ld.y = open * 7;
					ModelPart iris = get("main", "eye");
					if (iris != null) { iris.xRot = Mth.sin(t * 0.05F) * 0.1F; iris.yRot = Mth.sin(t * 0.037F) * 0.15F; }
					boolean right = (s.strike & 1) == 0;
					for (int sd = -1; sd <= 1; sd += 2) {
						ModelPart arm = get("main", sd < 0 ? "arm_r" : "arm_l"), f = get("main", sd < 0 ? "arm_r" : "arm_l", "f");
						boolean active = s.strike > 0 && (sd < 0) == right;
						float reach = ease(ph * 2.5F);
						float slam = active ? (a < 8 ? -ease(a / 8F) * 0.8F : a < 12 ? -0.8F + ease((a - 8) / 4F) * 1.9F : a < 26 ? 1.1F - ease((a - 12) / 14F) * 1.1F : 0) : 0;
						if (arm != null) { arm.xRot = 0.2F + slam; arm.yRot = sd * (0.35F - reach * 0.2F); arm.z = -2 + (1 - reach) * 30; arm.visible = reach > 0.02F; }
						if (f != null) f.xRot = 0.4F + Mth.sin(t * 0.07F + sd) * 0.08F + Math.max(0, slam) * 0.3F;
					}
				}
				case BLADE -> {
					ModelPart r = get("runes");
					if (r != null) { r.yRot = t * 0.2F; r.visible = true; r.y = -10 - ph * 30; }
				}
				case BLADE_RING -> {
					ModelPart sp = get("spin"), rr = get("runes");
					if (sp != null) sp.yRot = t * 0.35F * (1.2F - ph);
					if (rr != null) { rr.yRot = -t * 0.08F; float k = 0.3F + 0.7F * ease(ph * 2); rr.xScale = k; rr.zScale = k; }
				}
				case HAMMER -> {
					ModelPart arm = get("arm");
					// 하늘에서 떨어져 내리꽂힘 (위치는 서버가 옮김) → 꽂힌 뒤 부르르 떨림
					float wob = ph > 0.3F ? Mth.sin(t * 2.2F) * 0.05F * Math.max(0, 1 - (ph - 0.3F) * 3) : 0;
					if (arm != null) { arm.xRot = wob; arm.zRot = wob * 0.7F; }
				}
				case SHOCK -> {
					float rise = ph < 0.35F ? ease(ph / 0.35F) : ph > 0.75F ? 1 - ease((ph - 0.75F) / 0.25F) : 1;
					for (int i = 0; i < 14; i++) {
						ModelPart sp = get("main", "s" + i);
						if (sp == null) continue;
						float d = Mth.clamp(rise * 1.4F - (i % 3) * 0.15F, 0, 1);
						sp.y = (1 - d) * 26;
						sp.yScale = 0.2F + 0.8F * d;
					}
					ModelPart rr = get("main", "runes");
					if (rr != null) { rr.yRot = t * 0.1F; float k = 0.4F + 0.9F * ease(ph * 1.5F); rr.xScale = k; rr.zScale = k; }
				}
				case CRESCENT -> {
					ModelPart sp = get("spin");
					if (sp != null) { sp.yRot = -ph * Mth.TWO_PI * 2; sp.zRot = Mth.sin(ph * Mth.PI * 2) * 0.12F; }
				}
				case TOME -> {
					float open = ease(ph * 2.5F);
					ModelPart pr = get("main", "pr"), pl = get("main", "pl"), fl = get("main", "flip"), r1 = get("main", "ring1"), r2 = get("main", "ring2");
					if (pr != null) pr.zRot = -(1 - open) * 1.45F + 0.12F;
					if (pl != null) pl.zRot = (1 - open) * 1.45F - 0.12F;
					if (fl != null) { fl.zRot = -((t * 0.18F) % Mth.PI); fl.visible = open > 0.8F; }
					if (r1 != null) { r1.yRot = t * 0.06F; float k = open; r1.xScale = k; r1.zScale = k; r1.y = -10 - open * 4; }
					if (r2 != null) { r2.yRot = -t * 0.1F; float k = open * 0.9F; r2.xScale = k; r2.zScale = k; r2.y = -18 - open * 6; }
					ModelPart m = get("main");
					if (m != null) m.y = 24 + Mth.sin(t * 0.12F) * 1.5F;
				}
				default -> { }
			}
		}

		private static String[] path(int i) {
			String[] p = new String[i + 2];
			p[0] = "head";
			for (int k = 0; k <= i; k++) p[k + 1] = "seg" + k;
			return p;
		}
	}
}
