package com.elementalweapons.client.boss;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/**
 * 모든 설정형 보스가 쓰는 모델. 파트 이름 규칙으로 애니메이션이 자동 적용됩니다.
 *  body(둥실) head(시선) head1~3(여러 머리) jaw(입) right_arm/left_arm(팔) right_wing/left_wing(날개)
 *  halo(회전) ring(가로 회전) tail1~8(꼬리 사슬) neck1~4(목) right_leg/left_leg(걷기) leg1~8(거미 다리)
 *  attackPose: 1 = 시전(팔 들기) / 2 = 돌진 / 3 = 방출(팔 벌리기)
 */
public class GenericBossModel extends EntityModel<BossRenderState> {
	private final Map<String, ModelPart> parts = new HashMap<>();

	public GenericBossModel(ModelPart root, String[][] paths) {
		super(root);
		for (String[] path : paths) {
			ModelPart m = root;
			for (String name : path) m = m.getChild(name);
			parts.put(path[path.length - 1], m);
		}
	}

	private ModelPart p(String name) {
		return parts.get(name);
	}

	@Override
	public void setupAnim(BossRenderState s) {
		super.setupAnim(s);
		float t = s.ageInTicks;
		int pose = s.attackPose;

		ModelPart body = p("body");
		if (body != null) {
			if (s.floating) body.y += Mth.sin(t * 0.1F) * 1.0F;
			if (pose == 2) body.xRot += 0.45F;
		}
		ModelPart head = p("head");
		if (head != null) {
			head.yRot = s.yRot * Mth.DEG_TO_RAD;
			head.xRot += s.xRot * Mth.DEG_TO_RAD;
		}
		for (int i = 1; i <= 3; i++) {
			ModelPart h = p("head" + i);
			if (h == null) continue;
			h.yRot += s.yRot * Mth.DEG_TO_RAD * 0.6F + Mth.sin(t * 0.07F + i * 2.1F) * 0.25F;
			h.xRot += Mth.sin(t * 0.05F + i) * 0.15F + (pose == 1 ? -0.4F : 0.0F);
		}
		ModelPart jaw = p("jaw");
		if (jaw != null) jaw.xRot += (pose == 1 || pose == 3) ? 0.6F : 0.08F + Mth.sin(t * 0.1F) * 0.05F;
		ModelPart halo = p("halo");
		if (halo != null) halo.zRot += t * (pose == 1 ? 0.15F : 0.03F);
		ModelPart ring = p("ring");
		if (ring != null) ring.yRot += t * (pose == 3 ? 0.2F : 0.05F);

		// 팔
		float sway = Mth.sin(t * 0.06F) * 0.05F;
		ModelPart ra = p("right_arm"), la = p("left_arm");
		if (ra != null && la != null) {
			ra.zRot += 0.15F + sway; la.zRot += -0.15F - sway;
			ra.xRot += sway; la.xRot += -sway;
			switch (pose) {
				case 1 -> { ra.xRot = -2.7F; ra.zRot = 0.35F; la.xRot = -2.7F; la.zRot = -0.35F; }
				case 2 -> { ra.xRot = 0.9F; la.xRot = 0.9F; }
				case 3 -> { ra.zRot = 1.35F; la.zRot = -1.35F; }
				default -> { }
			}
		}
		// 날개
		ModelPart rw = p("right_wing"), lw = p("left_wing");
		if (rw != null && lw != null) {
			float speed = pose == 2 ? 0.6F : 0.25F;
			float flap = 0.25F + Mth.sin(t * speed) * 0.45F;
			rw.zRot += flap; lw.zRot += -flap;
		}
		// 꼬리, 목
		for (int i = 1; i <= 8; i++) {
			ModelPart tl = p("tail" + i);
			if (tl == null) break;
			tl.zRot += Mth.sin(t * 0.08F + i * 0.8F) * 0.05F * i;
			tl.xRot += Mth.sin(t * 0.06F + i) * 0.04F;
		}
		for (int i = 1; i <= 4; i++) {
			ModelPart nk = p("neck" + i);
			if (nk == null) break;
			nk.xRot += Mth.sin(t * 0.06F + i) * 0.05F;
			nk.yRot += Mth.sin(t * 0.05F + i) * 0.06F;
		}
		// 다리
		float walk = s.walkAnimationPos, amt = Math.min(1.0F, s.walkAnimationSpeed);
		ModelPart rl = p("right_leg"), ll = p("left_leg");
		if (rl != null && ll != null) {
			rl.xRot += Mth.cos(walk * 0.6F) * 0.9F * amt;
			ll.xRot += Mth.cos(walk * 0.6F + Mth.PI) * 0.9F * amt;
		}
		for (int i = 1; i <= 8; i++) {
			ModelPart lg = p("leg" + i);
			if (lg == null) break;
			float ph = (i % 2 == 0 ? Mth.PI : 0.0F) + i * 0.4F;
			lg.yRot += Mth.cos(walk * 0.8F + ph) * 0.35F * amt + Mth.sin(t * 0.05F + i) * 0.03F;
			lg.zRot += Math.abs(Mth.sin(walk * 0.8F + ph)) * 0.25F * amt * (i <= 4 ? 1 : -1);
		}
	}
}
