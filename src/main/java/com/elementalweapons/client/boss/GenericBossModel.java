package com.elementalweapons.client.boss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

/**
 * 모든 보스가 쓰는 모델. 파트 이름 규칙으로 애니메이션이 자동 적용됩니다.
 *  body(호흡·둥실) head(시선) head1~3 / jaw / right_arm(2) left_arm(2) / right_wing(2) left_wing(2)
 *  halo ring / tailN neckN capeN / right_leg(2) left_leg(2) front_right_leg front_left_leg / legN(거미)
 *  sway<대문자>N : 촉수·머리카락·로브 사슬 흔들림, spin_* : 회전, bob_* : 위아래, pulse_* : 크기 맥동
 *  attackPose: 1 = 시전 / 2 = 돌진 / 3 = 방출
 */
public class GenericBossModel extends EntityModel<BossRenderState> {
	private final Map<String, ModelPart> parts = new HashMap<>();
	private final Map<String, List<ModelPart>> sways = new TreeMap<>();
	private final List<ModelPart> spins = new ArrayList<>(), bobs = new ArrayList<>(), pulses = new ArrayList<>();

	public GenericBossModel(ModelPart root, String[][] paths) {
		super(root);
		for (String[] path : paths) {
			ModelPart m = root;
			for (String name : path) m = m.getChild(name);
			String n = path[path.length - 1];
			parts.put(n, m);
			if (n.startsWith("spin")) spins.add(m);
			else if (n.startsWith("bob")) bobs.add(m);
			else if (n.startsWith("pulse")) pulses.add(m);
			else if (n.startsWith("sway") && n.length() >= 6 && Character.isUpperCase(n.charAt(4))) {
				sways.computeIfAbsent(n.substring(0, 5), k -> new ArrayList<>()).add(m);
			}
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
		float walk = s.walkAnimationPos, amt = Math.min(1.0F, s.walkAnimationSpeed);
		float breath = Mth.sin(t * 0.08F);

		ModelPart body = p("body");
		if (body != null) {
			if (s.floating) body.y += Mth.sin(t * 0.1F) * 1.2F;
			body.xRot += breath * 0.015F;
			if (pose == 2) body.xRot += 0.4F;
			if (pose == 1) body.xRot -= 0.12F;
		}
		ModelPart head = p("head");
		if (head != null) {
			head.yRot += s.yRot * Mth.DEG_TO_RAD * 0.8F;
			head.xRot += s.xRot * Mth.DEG_TO_RAD * 0.8F + breath * 0.03F;
			if (pose == 3) head.xRot -= 0.35F;
		}
		for (int i = 1; i <= 3; i++) {
			ModelPart h = p("head" + i);
			if (h == null) continue;
			h.yRot += s.yRot * Mth.DEG_TO_RAD * 0.5F + Mth.sin(t * 0.07F + i * 2.1F) * 0.22F;
			h.xRot += Mth.sin(t * 0.05F + i) * 0.15F + (pose == 1 ? -0.4F : 0.0F);
		}
		ModelPart jaw = p("jaw");
		if (jaw != null) jaw.xRot += (pose == 1 || pose == 3) ? 0.55F : 0.06F + Mth.sin(t * 0.1F) * 0.05F;
		ModelPart halo = p("halo");
		if (halo != null) { halo.zRot += t * (pose == 1 ? 0.12F : 0.025F); halo.y += Mth.sin(t * 0.07F) * 0.6F; }
		ModelPart ring = p("ring");
		if (ring != null) { ring.yRot += t * (pose == 3 ? 0.18F : 0.04F); ring.xRot += Mth.sin(t * 0.03F) * 0.12F; }

		// 팔 (위팔 + 아래팔)
		float sway = Mth.sin(t * 0.06F) * 0.06F;
		ModelPart ra = p("right_arm"), la = p("left_arm"), ra2 = p("right_arm2"), la2 = p("left_arm2");
		if (ra != null) {
			ra.zRot += 0.12F + sway; ra.xRot += sway - Mth.cos(walk * 0.6F) * 0.5F * amt;
			if (ra2 != null) ra2.xRot -= 0.25F + breath * 0.05F;
			switch (pose) {
				case 1 -> { ra.xRot = -2.6F; ra.zRot = 0.3F; if (ra2 != null) ra2.xRot = -0.3F; }
				case 2 -> { ra.xRot = -1.4F; if (ra2 != null) ra2.xRot = -0.8F; }
				case 3 -> { ra.zRot = 1.3F; if (ra2 != null) ra2.xRot = -0.2F; }
				default -> { }
			}
		}
		if (la != null) {
			la.zRot += -0.12F - sway; la.xRot += -sway + Mth.cos(walk * 0.6F) * 0.5F * amt;
			if (la2 != null) la2.xRot -= 0.25F + breath * 0.05F;
			switch (pose) {
				case 1 -> { la.xRot = -2.6F; la.zRot = -0.3F; if (la2 != null) la2.xRot = -0.3F; }
				case 2 -> { la.xRot = -1.4F; if (la2 != null) la2.xRot = -0.8F; }
				case 3 -> { la.zRot = -1.3F; if (la2 != null) la2.xRot = -0.2F; }
				default -> { }
			}
		}
		// 날개 (안쪽 + 바깥 마디가 늦게 따라옴)
		float spd = pose == 2 ? 0.55F : 0.22F;
		float flap = Mth.sin(t * spd) * 0.42F, flap2 = Mth.sin(t * spd - 0.9F) * 0.5F;
		ModelPart rw = p("right_wing"), lw = p("left_wing"), rw2 = p("right_wing2"), lw2 = p("left_wing2");
		if (rw != null) rw.zRot += flap;
		if (lw != null) lw.zRot -= flap;
		if (rw2 != null) rw2.zRot += flap2;
		if (lw2 != null) lw2.zRot -= flap2;
		if (pose == 3) { if (rw != null) rw.yRot += 0.5F; if (lw != null) lw.yRot -= 0.5F; }
		// 꼬리, 목, 망토
		for (int i = 1; i <= 8; i++) {
			ModelPart tl = p("tail" + i);
			if (tl == null) break;
			tl.yRot += Mth.sin(t * 0.09F - i * 0.7F) * 0.09F;
			tl.xRot += Mth.sin(t * 0.06F - i * 0.6F) * 0.035F;
		}
		for (int i = 1; i <= 4; i++) {
			ModelPart nk = p("neck" + i);
			if (nk == null) break;
			nk.xRot += Mth.sin(t * 0.06F - i * 0.5F) * 0.05F + (pose == 1 ? -0.1F : 0.0F);
			nk.yRot += Mth.sin(t * 0.05F - i * 0.5F) * 0.06F;
		}
		for (int i = 1; i <= 8; i++) {
			ModelPart cp = p("cape" + i);
			if (cp == null) break;
			cp.xRot += 0.06F + Mth.sin(t * 0.1F - i * 0.6F) * 0.06F + amt * 0.12F * (i == 1 ? 1 : 0.3F);
		}
		for (List<ModelPart> chain : sways.values()) {
			int i = 0;
			for (ModelPart sp : chain) {
				i++;
				sp.zRot += Mth.sin(t * 0.08F - i * 0.7F + chain.size()) * 0.06F;
				sp.xRot += Mth.sin(t * 0.065F - i * 0.6F) * 0.06F;
			}
		}
		for (ModelPart sp : spins) sp.zRot += t * 0.08F;
		for (int i = 0; i < bobs.size(); i++) bobs.get(i).y += Mth.sin(t * 0.09F + i * 1.7F) * 1.5F;
		for (int i = 0; i < pulses.size(); i++) {
			float k = 1.0F + Mth.sin(t * 0.15F + i) * 0.12F;
			ModelPart pp = pulses.get(i);
			pp.xScale = k; pp.yScale = k; pp.zScale = k;
		}
		// 다리 (허벅지 + 무릎)
		float c = Mth.cos(walk * 0.6F) * 0.8F * amt;
		leg("right_leg", c); leg("left_leg", -c);
		leg("front_right_leg", -c); leg("front_left_leg", c);
		for (int i = 1; i <= 8; i++) {
			ModelPart lg = p("leg" + i);
			if (lg == null) break;
			float ph = (i % 2 == 0 ? Mth.PI : 0.0F) + i * 0.4F;
			lg.yRot += Mth.cos(walk * 0.8F + ph) * 0.35F * amt + Mth.sin(t * 0.05F + i) * 0.04F;
			lg.zRot += Math.abs(Mth.sin(walk * 0.8F + ph)) * 0.25F * amt * (i % 2 == 0 ? 1 : -1);
		}
	}

	private void leg(String name, float swing) {
		ModelPart l = p(name), k = p(name + "2");
		if (l == null) return;
		l.xRot += swing;
		if (k != null) k.xRot += Math.max(0.0F, -swing) * 0.9F;
	}
}
