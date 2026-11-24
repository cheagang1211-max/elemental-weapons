package com.elementalweapons.client.armor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/**
 * 직업별 입체 복장 (갑옷 위에 덧입음). 휴머노이드와 같은 뼈대라 동작을 그대로 따라감.
 *  마법사: 뾰족 모자·어깨 망토·발목까지 오는 로브·긴 망토·넓은 소매
 *  서포터: 주교관·영대(스톨)·흰 사제복·짧은 망토
 *  암살자: 두건·복면·목도리 꼬리·등의 단검·팔 보호대
 *  궁수:   뾰족 두건·짧은 망토·등의 화살통·가죽 팔보호대
 *  사신:   깊은 두건 속 빛나는 눈·찢어진 망토 자락·해골 어깨갑
 *  기사:   (판금 위에) 긴 망토와 문장 휘장
 *  전사:   (판금 위에) 모피 어깨 두르개·큰 버클 허리띠·허리 천
 * 부위 이름: 직업|착용칸(h,c,l,f)|움직임(n,c=망토,f=앞자락,b=뒷자락,t=흔들림)|번호
 */
public final class ClassArmorMesh {
	private ClassArmorMesh() {}

	/** 만들어진 부위: {뼈 이름, 부위 이름} */
	public static final List<String[]> PARTS = new ArrayList<>();

	static final int[] CLOTH = {0, 0}, TRIM = {64, 0}, DARK = {0, 32}, FUR = {64, 32}, METAL = {0, 64}, GLOW = {64, 64};

	private static CubeListBuilder c(int[] m, float x, float y, float z, float w, float h, float d) {
		return CubeListBuilder.create().texOffs(m[0], m[1]).addBox(x, y, z, w, h, d);
	}
	private static PartPose at(float x, float y, float z, float rx, float ry, float rz) { return PartPose.offsetAndRotation(x, y, z, rx, ry, rz); }

	private static int n;
	private static String bone;
	private static PartDefinition B;
	private static String cls;

	private static void on(String boneName, PartDefinition p) { bone = boneName; B = p; }
	/** 새 부위 (뼈 바로 아래) */
	private static PartDefinition part(char slot, char anim, CubeListBuilder cubes, PartPose pose) {
		String name = cls + "|" + slot + "|" + anim + "|" + (n++);
		PARTS.add(new String[]{bone, name});
		return B.addOrReplaceChild(name, cubes, pose);
	}

	public static LayerDefinition create() {
		PARTS.clear();
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
		head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
		PartDefinition ra = root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5, 2, 0));
		PartDefinition la = root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5, 2, 0));
		PartDefinition rl = root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12, 0));
		PartDefinition ll = root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12, 0));

		// ================= 마법사 =================
		cls = "mage";
		on("head", head);
		PartDefinition brim = part('h', 'n', c(DARK, -6.5F, -8.6F, -6.5F, 13, 1, 13), PartPose.ZERO);
		brim.addOrReplaceChild("band", c(TRIM, -4.6F, -10.6F, -4.6F, 9.2F, 2, 9.2F), PartPose.ZERO);
		PartDefinition cone = brim.addOrReplaceChild("cone", c(CLOTH, -4, -4, -4, 8, 4, 8), PartPose.offset(0, -8.6F, 0.3F));
		PartDefinition c2 = cone.addOrReplaceChild("c2", c(CLOTH, -3, -4, -3, 6, 4, 6), at(0, -4, 0, -0.22F, 0, 0));
		PartDefinition c3 = c2.addOrReplaceChild("c3", c(CLOTH, -2, -3.5F, -2, 4, 3.5F, 4), at(0, -3.8F, 0, -0.35F, 0, 0));
		PartDefinition c4 = c3.addOrReplaceChild("c4", c(CLOTH, -1, -3, -1, 2, 3, 2), at(0, -3.3F, 0, -0.5F, 0, 0));
		c4.addOrReplaceChild("star", c(GLOW, -0.7F, -1.4F, -0.7F, 1.4F, 1.4F, 1.4F), at(0, -3, 0, 0.6F, 0.6F, 0));
		cone.addOrReplaceChild("gem", c(GLOW, -1, -3, -4.5F, 2, 2, 0.6F), PartPose.ZERO);
		on("body", body);
		part('c', 'n', c(TRIM, -4.5F, -1, -2.6F, 9, 2, 5.2F), PartPose.ZERO);                    // 높은 깃
		PartDefinition mantle = part('c', 'n', c(CLOTH, -5, -0.5F, -2.9F, 10, 4, 5.8F), PartPose.ZERO);
		mantle.addOrReplaceChild("edge", c(TRIM, -5.1F, 3.3F, -3, 10.2F, 0.8F, 6), PartPose.ZERO);
		mantle.addOrReplaceChild("clasp", c(GLOW, -1, 0.5F, -3.4F, 2, 2, 0.6F), PartPose.ZERO);
		part('c', 'n', c(TRIM, -4.5F, 9.4F, -2.5F, 9, 1.2F, 5), PartPose.ZERO);                  // 허리띠
		PartDefinition cape = part('c', 'c', c(CLOTH, -4.5F, 0, 0, 9, 22, 0.8F), PartPose.offset(0, 0.3F, 2.4F));
		cape.addOrReplaceChild("hem", c(TRIM, -4.6F, 21, -0.05F, 9.2F, 1.2F, 0.9F), PartPose.ZERO);
		cape.addOrReplaceChild("rune", c(GLOW, -1.5F, 8, 0.75F, 3, 3, 0.2F), PartPose.ZERO);
		skirt('l', CLOTH, TRIM, 11);
		on("right_arm", ra);
		part('c', 'n', c(CLOTH, -3.6F, 4.5F, -2.6F, 5.2F, 5.5F, 5.2F), PartPose.ZERO).addOrReplaceChild("t", c(TRIM, -3.8F, 9.8F, -2.8F, 5.6F, 1, 5.6F), PartPose.ZERO);
		on("left_arm", la);
		part('c', 'n', c(CLOTH, -1.6F, 4.5F, -2.6F, 5.2F, 5.5F, 5.2F), PartPose.ZERO).addOrReplaceChild("t", c(TRIM, -1.8F, 9.8F, -2.8F, 5.6F, 1, 5.6F), PartPose.ZERO);

		// ================= 서포터 =================
		cls = "support";
		on("head", head);
		PartDefinition mitre = part('h', 'n', c(CLOTH, -3.2F, -15, -2, 6.4F, 6.5F, 4), PartPose.ZERO);
		mitre.addOrReplaceChild("peak", c(CLOTH, -2.2F, -2.2F, -1.9F, 4.4F, 4.4F, 3.8F), at(0, -15, 0, 0, 0, 0.785F));
		mitre.addOrReplaceChild("band", c(TRIM, -3.4F, -9.6F, -2.2F, 6.8F, 1.4F, 4.4F), PartPose.ZERO);
		mitre.addOrReplaceChild("cross1", c(GLOW, -0.5F, -14.5F, -2.4F, 1, 4.5F, 0.5F), PartPose.ZERO);
		mitre.addOrReplaceChild("cross2", c(GLOW, -1.7F, -13.3F, -2.4F, 3.4F, 1, 0.5F), PartPose.ZERO);
		mitre.addOrReplaceChild("circ", c(TRIM, -4.5F, -8.4F, -4.5F, 9, 1, 9), PartPose.ZERO);
		for (int s = -1; s <= 1; s += 2) mitre.addOrReplaceChild("lap" + s, c(TRIM, -0.6F, 0, -0.2F, 1.2F, 7, 0.4F), at(1.5F * s, -8, 4.4F, 0.15F, 0, 0));
		on("body", body);
		PartDefinition sm = part('c', 'n', c(CLOTH, -5, -0.5F, -2.9F, 10, 5, 5.8F), PartPose.ZERO);
		sm.addOrReplaceChild("edge", c(TRIM, -5.1F, 4.2F, -3, 10.2F, 0.8F, 6), PartPose.ZERO);
		for (int s = -1; s <= 1; s += 2) {
			PartDefinition st = part('c', 'n', c(TRIM, -0.8F, 0, -0.3F, 1.6F, 15, 0.5F), PartPose.offset(2.2F * s, -0.4F, -2.8F));
			st.addOrReplaceChild("x1", c(GLOW, -0.3F, 11.5F, -0.5F, 0.6F, 2.4F, 0.3F), PartPose.ZERO);
			st.addOrReplaceChild("x2", c(GLOW, -0.9F, 12.2F, -0.5F, 1.8F, 0.6F, 0.3F), PartPose.ZERO);
		}
		part('c', 'n', c(METAL, -4.4F, 9.5F, -2.5F, 8.8F, 1, 5), PartPose.ZERO);
		PartDefinition sc = part('c', 'c', c(CLOTH, -4.5F, 0, 0, 9, 15, 0.7F), PartPose.offset(0, 0.3F, 2.4F));
		sc.addOrReplaceChild("hem", c(TRIM, -4.6F, 14.2F, -0.05F, 9.2F, 1, 0.8F), PartPose.ZERO);
		sc.addOrReplaceChild("sun", c(GLOW, -1.5F, 4, 0.65F, 3, 3, 0.2F), PartPose.ZERO);
		skirt('l', CLOTH, TRIM, 11);
		on("right_arm", ra);
		part('c', 'n', c(CLOTH, -3.6F, 5, -2.6F, 5.2F, 5, 5.2F), PartPose.ZERO).addOrReplaceChild("t", c(TRIM, -3.8F, 9.8F, -2.8F, 5.6F, 1, 5.6F), PartPose.ZERO);
		on("left_arm", la);
		part('c', 'n', c(CLOTH, -1.6F, 5, -2.6F, 5.2F, 5, 5.2F), PartPose.ZERO).addOrReplaceChild("t", c(TRIM, -1.8F, 9.8F, -2.8F, 5.6F, 1, 5.6F), PartPose.ZERO);

		// ================= 암살자 =================
		cls = "assassin";
		on("head", head);
		hood(CLOTH, TRIM, 0, false);
		part('h', 'n', c(DARK, -4.4F, -3.9F, -4.6F, 8.8F, 3.9F, 0.5F), PartPose.ZERO);            // 복면
		on("body", body);
		part('c', 'n', c(TRIM, -4.6F, -0.8F, -2.7F, 9.2F, 2.2F, 5.4F), PartPose.ZERO);           // 목도리
		for (int s = -1; s <= 1; s += 2) part('c', 't', c(TRIM, -0.9F, 0, 0, 1.8F, 10 + s, 0.4F), at(1.3F * s, 0.6F, 2.7F, 0.15F, 0, 0.08F * s));
		part('c', 'n', c(DARK, -4.4F, 9, -2.5F, 8.8F, 1.6F, 5), PartPose.ZERO);
		for (int s = -1; s <= 1; s += 2) {
			PartDefinition dg = part('c', 'n', c(METAL, -0.4F, -5, -0.3F, 0.8F, 6, 0.5F), at(1.8F * s, 10.2F, 2.8F, 0, 0, 1.25F * s));
			dg.addOrReplaceChild("hilt", c(DARK, -0.6F, 1, -0.4F, 1.2F, 2.5F, 0.8F), PartPose.ZERO);
			dg.addOrReplaceChild("edge", c(GLOW, -0.1F, -5, -0.35F, 0.2F, 5, 0.1F), PartPose.ZERO);
		}
		part('l', 'f', c(CLOTH, -3, 0, -0.3F, 6, 5, 0.6F), PartPose.offset(0, 12, -2.4F));
		part('l', 'b', c(CLOTH, -3, 0, -0.3F, 6, 5, 0.6F), PartPose.offset(0, 12, 2.4F));
		on("right_arm", ra);
		part('c', 'n', c(DARK, -3.4F, 5.5F, -2.4F, 4.8F, 4, 4.8F), PartPose.ZERO).addOrReplaceChild("b", c(GLOW, -3.75F, 6, -0.3F, 0.4F, 3.5F, 0.6F), PartPose.ZERO);
		on("left_arm", la);
		part('c', 'n', c(DARK, -1.4F, 5.5F, -2.4F, 4.8F, 4, 4.8F), PartPose.ZERO).addOrReplaceChild("b", c(GLOW, 3.35F, 6, -0.3F, 0.4F, 3.5F, 0.6F), PartPose.ZERO);
		on("right_leg", rl); part('f', 'n', c(DARK, -2.3F, 5, -2.3F, 4.6F, 1, 4.6F), PartPose.ZERO);
		on("left_leg", ll); part('f', 'n', c(DARK, -2.3F, 5, -2.3F, 4.6F, 1, 4.6F), PartPose.ZERO);

		// ================= 궁수 =================
		cls = "ranger";
		on("head", head);
		hood(CLOTH, TRIM, 1, false);
		on("body", body);
		PartDefinition rm = part('c', 'n', c(CLOTH, -5, -0.5F, -2.9F, 10, 3.5F, 5.8F), PartPose.ZERO);
		rm.addOrReplaceChild("edge", c(TRIM, -5.1F, 2.7F, -3, 10.2F, 0.8F, 6), PartPose.ZERO);
		rm.addOrReplaceChild("pin", c(METAL, -0.8F, 0.5F, -3.3F, 1.6F, 1.6F, 0.5F), PartPose.ZERO);
		PartDefinition rc = part('c', 'c', c(CLOTH, -4.5F, 0, 0, 9, 13, 0.7F), PartPose.offset(0, 0.3F, 2.4F));
		rc.addOrReplaceChild("hem", c(TRIM, -4.6F, 12.2F, -0.05F, 9.2F, 1, 0.8F), PartPose.ZERO);
		PartDefinition q = part('c', 'n', c(DARK, -1.5F, 0, -1.5F, 3, 10, 3), at(2.2F, 0.5F, 4.6F, 0.1F, 0, -0.45F));
		q.addOrReplaceChild("ring", c(TRIM, -1.7F, 0.2F, -1.7F, 3.4F, 1, 3.4F), PartPose.ZERO);
		q.addOrReplaceChild("ring2", c(TRIM, -1.7F, 7.5F, -1.7F, 3.4F, 1, 3.4F), PartPose.ZERO);
		for (int k = 0; k < 3; k++) {
			PartDefinition ar = q.addOrReplaceChild("arrow" + k, c(METAL, -0.25F, -4, -0.25F, 0.5F, 4, 0.5F), at(-0.8F + k * 0.8F, 0.3F, -0.4F + (k % 2) * 0.8F, 0, 0, 0));
			ar.addOrReplaceChild("fl", c(FUR, -0.6F, -2.5F, -0.1F, 1.2F, 2.5F, 0.2F), at(0, -3.5F, 0, 0, 0.4F * k, 0));
			ar.addOrReplaceChild("gl", c(GLOW, -0.3F, -4.6F, -0.3F, 0.6F, 0.7F, 0.6F), PartPose.ZERO);
		}
		part('c', 'n', c(TRIM, -4.4F, 9, -2.5F, 8.8F, 1.4F, 5), PartPose.ZERO);
		part('c', 'n', c(DARK, 2, 9.4F, -3.3F, 2, 2.2F, 1), PartPose.ZERO);
		part('l', 'f', c(CLOTH, -2.5F, 0, -0.3F, 5, 4, 0.6F), PartPose.offset(0, 12, -2.4F));
		on("right_arm", ra);
		part('c', 'n', c(TRIM, -3.4F, 5.5F, -2.4F, 4.8F, 4.5F, 4.8F), PartPose.ZERO);
		on("left_arm", la);
		part('c', 'n', c(TRIM, -1.4F, 5.5F, -2.4F, 4.8F, 4.5F, 4.8F), PartPose.ZERO).addOrReplaceChild("guard", c(METAL, 3.3F, 5.5F, -1.5F, 0.5F, 4, 3), PartPose.ZERO);
		on("right_leg", rl); part('f', 'n', c(FUR, -2.4F, 8, -2.4F, 4.8F, 1.5F, 4.8F), PartPose.ZERO);
		on("left_leg", ll); part('f', 'n', c(FUR, -2.4F, 8, -2.4F, 4.8F, 1.5F, 4.8F), PartPose.ZERO);

		// ================= 사신 =================
		cls = "reaper";
		on("head", head);
		hood(CLOTH, DARK, 2, true);
		for (int s = -1; s <= 1; s += 2) part('h', 'n', c(GLOW, -0.6F, -0.4F, -0.15F, 1.2F, 0.8F, 0.3F), PartPose.offset(1.8F * s, -4.4F, -4.4F));
		on("body", body);
		part('c', 'n', c(DARK, -4.6F, -0.8F, -2.7F, 9.2F, 2, 5.4F), PartPose.ZERO);
		float[] len = {19, 15, 21, 17, 20, 14};
		for (int k = 0; k < 6; k++) part('c', 't', c(CLOTH, -0.9F, 0, 0, 1.8F, len[k], 0.6F), at(-4.2F + k * 1.7F, 0.3F, 2.4F, 0.06F, 0, (k - 2.5F) * 0.035F));
		for (int s = -1; s <= 1; s += 2) part('c', 't', c(CLOTH, -0.3F, 0, -2, 0.6F, 16, 4), at(4.3F * s, 0.3F, 0.3F, 0, 0, 0.05F * s));
		part('c', 'n', c(DARK, -4.4F, 9.4F, -2.5F, 8.8F, 1, 5), PartPose.ZERO);
		for (int k = 0; k < 3; k++) part('l', 'f', c(CLOTH, -1.1F, 0, -0.3F, 2.2F, 9 - k * 2 + (k == 1 ? 3 : 0), 0.6F), PartPose.offset(-2.6F + k * 2.6F, 12, -2.4F));
		on("left_arm", la);
		PartDefinition sk = part('c', 'n', c(METAL, -1.5F, -3, -3, 5.5F, 3, 6), PartPose.ZERO);
		PartDefinition skull = sk.addOrReplaceChild("skull", c(METAL, -1.8F, -3.4F, -1.8F, 3.6F, 3.4F, 3.6F), at(1.8F, -3, 0, 0, -0.5F, 0.25F));
		skull.addOrReplaceChild("jaw", c(METAL, -1.3F, 0, -1.9F, 2.6F, 1, 2.6F), PartPose.ZERO);
		for (int s = -1; s <= 1; s += 2) skull.addOrReplaceChild("eye" + s, c(GLOW, -0.45F, -0.45F, -0.2F, 0.9F, 0.9F, 0.3F), PartPose.offset(0.8F * s, -1.8F, -1.95F));
		part('c', 't', c(CLOTH, -1.4F, 0, -0.3F, 2.8F, 7, 0.6F), PartPose.offset(1, 6, 2.3F));
		on("right_arm", ra);
		part('c', 't', c(CLOTH, -1.4F, 0, -0.3F, 2.8F, 7, 0.6F), PartPose.offset(-1, 6, 2.3F));

		// ================= 기사 (판금 위에) =================
		cls = "knight";
		on("body", body);
		PartDefinition kc = part('c', 'c', c(CLOTH, -5, 0, 0, 10, 21, 0.7F), PartPose.offset(0, -0.6F, 4.3F));
		kc.addOrReplaceChild("hem", c(TRIM, -5.1F, 20.2F, -0.05F, 10.2F, 1, 0.8F), PartPose.ZERO);
		kc.addOrReplaceChild("crest", c(GLOW, -1.5F, 5, 0.65F, 3, 4, 0.2F), PartPose.ZERO);
		kc.addOrReplaceChild("crest2", c(TRIM, -2.2F, 4.3F, 0.6F, 4.4F, 0.6F, 0.2F), PartPose.ZERO);
		for (int s = -1; s <= 1; s += 2) part('c', 'n', c(TRIM, -1, -1, -1, 2, 2, 2), PartPose.offset(4.2F * s, -1, 4));
		PartDefinition tab = part('l', 'f', c(CLOTH, -2.5F, 0, -0.3F, 5, 10, 0.5F), PartPose.offset(0, 10.8F, -4.3F));
		tab.addOrReplaceChild("em", c(GLOW, -1, 2, -0.45F, 2, 2.5F, 0.2F), PartPose.ZERO);
		tab.addOrReplaceChild("hem", c(TRIM, -2.6F, 9.2F, -0.35F, 5.2F, 0.8F, 0.6F), PartPose.ZERO);

		// ================= 전사 (판금 위에) =================
		cls = "warrior";
		on("body", body);
		part('c', 'n', c(FUR, -6, -1.6F, -3.6F, 12, 3, 7.2F), PartPose.ZERO);
		part('c', 'n', c(FUR, -5, -3.2F, 2, 10, 3, 2.6F), PartPose.ZERO);
		part('c', 'n', c(TRIM, -5, 9.4F, -3.1F, 10, 2, 6.2F), PartPose.ZERO);
		part('c', 'n', c(METAL, -1.6F, 9, -3.7F, 3.2F, 2.8F, 0.7F), PartPose.ZERO).addOrReplaceChild("g", c(GLOW, -0.6F, 9.8F, -3.95F, 1.2F, 1.2F, 0.3F), PartPose.ZERO);
		part('l', 'f', c(CLOTH, -2, 0, -0.3F, 4, 9, 0.5F), PartPose.offset(0, 11.5F, -4.1F));
		part('l', 'b', c(CLOTH, -2.5F, 0, -0.3F, 5, 8, 0.5F), PartPose.offset(0, 11.5F, 3.2F));
		on("right_arm", ra); part('c', 'n', c(FUR, -6.8F, -0.3F, -3.9F, 3, 2, 7.8F), PartPose.ZERO);
		on("left_arm", la); part('c', 'n', c(FUR, 3.8F, -0.3F, -3.9F, 3, 2, 7.8F), PartPose.ZERO);
		on("right_leg", rl); part('f', 'n', c(FUR, -2.7F, 6.5F, -2.7F, 5.4F, 1.6F, 5.4F), PartPose.ZERO);
		on("left_leg", ll); part('f', 'n', c(FUR, -2.7F, 6.5F, -2.7F, 5.4F, 1.6F, 5.4F), PartPose.ZERO);

		return LayerDefinition.create(mesh, 128, 128);
	}

	/** 로브 자락 (앞·뒤·양옆), 발목 가까이까지 */
	private static void skirt(char slot, int[] cloth, int[] trim, float len) {
		PartDefinition f = part(slot, 'f', c(cloth, -4.4F, 0, -0.5F, 8.8F, len, 1), PartPose.offset(0, 12, -2.3F));
		f.addOrReplaceChild("hem", c(trim, -4.5F, len - 0.8F, -0.6F, 9, 1, 1.2F), PartPose.ZERO);
		f.addOrReplaceChild("mid", c(trim, -0.5F, 0, -0.65F, 1, len - 1, 0.2F), PartPose.ZERO);
		PartDefinition b = part(slot, 'b', c(cloth, -4.4F, 0, -0.5F, 8.8F, len, 1), PartPose.offset(0, 12, 2.3F));
		b.addOrReplaceChild("hem", c(trim, -4.5F, len - 0.8F, -0.6F, 9, 1, 1.2F), PartPose.ZERO);
		for (int s = -1; s <= 1; s += 2) {
			PartDefinition sd = part(slot, 's', c(cloth, -0.5F, 0, -2.4F, 1, len, 4.8F), PartPose.offset(4.3F * s, 12, 0));
			sd.addOrReplaceChild("hem", c(trim, -0.6F, len - 0.8F, -2.5F, 1.2F, 1, 5), PartPose.ZERO);
		}
	}

	/** 두건 (머리를 감싸는 껍데기 + 뒤로 늘어진 끝), style 0 암살자 / 1 궁수(앞챙) / 2 사신(깊고 큼) */
	private static void hood(int[] cloth, int[] rim, int style, boolean deep) {
		float g = deep ? 0.6F : 0;
		part('h', 'n', c(cloth, -4.7F - g, -8.8F - g, -4.4F - g, 9.4F + 2 * g, 1, 9.3F + 2 * g), PartPose.ZERO);
		for (int s = -1; s <= 1; s += 2)
			part('h', 'n', c(cloth, -0.45F, 0, -4.4F - g, 0.9F, 8.4F + g, 9.2F + 2 * g), PartPose.offset(s * (4.25F + g), -8.4F - g, 0));
		part('h', 'n', c(cloth, -4.7F - g, -8.6F - g, 4.0F + g, 9.4F + 2 * g, 8.6F + g, 0.9F), PartPose.ZERO);
		part('h', 'n', c(rim, -4.8F - g, -9.1F - g, -4.8F - g, 9.6F + 2 * g, 1.3F, 0.8F), PartPose.ZERO);
		if (style == 1) part('h', 'n', c(rim, -3.5F, -0.5F, -2.2F, 7, 0.8F, 2.2F), at(0, -8.6F, -4.6F, -0.25F, 0, 0));
		if (deep) part('h', 'n', c(rim, -5.4F, 0, -1.8F, 10.8F, 2.2F, 2), at(0, -9.6F, -5.2F, -0.3F, 0, 0));
		PartDefinition tail = part('h', 'n', c(cloth, -2.2F, -0.6F, 0, 4.4F, 1.4F, 3.5F + g * 2), at(0, -8.4F - g, 3.6F + g, -0.85F, 0, 0));
		tail.addOrReplaceChild("t2", c(cloth, -1.2F, -0.5F, 0, 2.4F, 1.1F, 3), at(0, 0, 3.3F + g * 2, -0.5F, 0, 0));
	}
}
