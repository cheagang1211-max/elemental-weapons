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

	private static float lerp(float a, float b, float w) { return a + (b - a) * w; }
	/** 부드러운 시작·끝 */
	private static float ease(float w) { if (w <= 0 || w >= 1) return w; return w * w * (3 - 2 * w); }

	@Override
	public void setupAnim(BossRenderState s) {
		super.setupAnim(s);
		float t = s.ageInTicks;
		float w1 = ease(s.pose[1]), w2 = ease(s.pose[2]), w3 = ease(s.pose[3]);
		float act = Math.min(1F, w1 + w2 + w3), calm = 1F - act;
		float walk = s.walkAnimationPos, amt = s.walkS;
		float idle = 1F - amt;
		float breath = Mth.sin(t * 0.08F);
		float step = Mth.sin(walk * 0.6F), stepAbs = Math.abs(step);
		float lean = Mth.clamp(s.turn * 0.012F, -0.22F, 0.22F);
		float fl = s.flinch;
		// 예비 동작(뒤로 젖힘) → 내지르기(앞으로 튕김): 공격 자세가 바뀐 직후 0.5초
		float ag = s.actAge;
		boolean attacking = s.attackPose > 0;
		float wind = attacking ? (ag < 5 ? ease(ag / 5F) : ag < 9 ? 1 - ease((ag - 5) / 4F) : 0) : 0;
		float punch = attacking && ag >= 5 && ag < 14 ? Mth.sin((ag - 5) / 9F * Mth.PI) : 0;
		float shake = attacking && ag >= 5 && ag < 11 ? Mth.sin(ag * 3.1F) * (1 - (ag - 5) / 6F) : 0;

		ModelPart body = p("body");
		if (body != null) {
			if (s.floating) {
				body.y += Mth.sin(t * 0.1F) * 1.2F;
				body.xRot += Mth.clamp(-s.vy * 0.8F, -0.25F, 0.25F) + amt * 0.18F;   // 날아갈 때 앞으로 기울기
			} else {
				body.y += -stepAbs * 1.1F * amt + breath * 0.25F * idle;           // 걸음마다 살짝 내려앉음, 숨쉬기
			}
			body.xRot += breath * 0.015F + 0.4F * w2 - 0.12F * w1 - fl * 0.22F - wind * 0.22F + punch * 0.16F;
			body.zRot += shake * 0.04F;
			body.zRot += lean + Mth.sin(walk * 0.3F) * 0.05F * amt + Mth.sin(t * 0.031F) * 0.02F * idle;   // 몸무게 옮기기
			body.yRot += step * 0.07F * amt;
		}
		ModelPart head = p("head");
		if (head != null) {
			head.yRot += s.headYawS * Mth.DEG_TO_RAD * 0.8F + (Mth.sin(t * 0.021F) * 0.12F + Mth.sin(t * 0.047F) * 0.04F) * idle * calm;   // 가만히 있을 땐 두리번
			head.xRot += -wind * 0.18F + punch * 0.1F + s.headPitchS * Mth.DEG_TO_RAD * 0.8F + breath * 0.03F + Mth.sin(t * 0.033F) * 0.04F * idle * calm - 0.35F * w3 - fl * 0.3F;
			head.zRot += -lean * 0.6F - Mth.sin(walk * 0.3F) * 0.04F * amt;
			if (body == null) head.y += -stepAbs * 0.8F * amt;
		}
		for (int i = 1; i <= 3; i++) {
			ModelPart h = p("head" + i);
			if (h == null) continue;
			h.yRot += s.headYawS * Mth.DEG_TO_RAD * 0.5F + Mth.sin(t * 0.07F + i * 2.1F) * 0.22F;
			h.xRot += Mth.sin(t * 0.05F + i) * 0.15F - 0.4F * w1;
		}
		ModelPart jaw = p("jaw");
		if (jaw != null) jaw.xRot += lerp(0.06F + Mth.sin(t * 0.1F) * 0.05F, 0.55F, Math.max(w1, w3)) + fl * 0.3F;
		ModelPart halo = p("halo");
		if (halo != null) { halo.zRot += t * (0.025F + 0.095F * w1); halo.y += Mth.sin(t * 0.07F) * 0.6F; }
		ModelPart ring = p("ring");
		if (ring != null) { ring.yRot += t * (0.04F + 0.14F * w3); ring.xRot += Mth.sin(t * 0.03F) * 0.12F; }

		// 팔: 걸을 때 반대로 흔들고 팔꿈치가 늦게 따라옴, 공격 자세는 섞어서 전환
		float sway = Mth.sin(t * 0.06F) * 0.06F;
		arm(p("right_arm"), p("right_arm2"), 1, sway, walk, amt, breath, w1, w2, w3, fl);
		arm(p("left_arm"), p("left_arm2"), -1, sway, walk, amt, breath, w1, w2, w3, fl);
		ModelPart ra = p("right_arm"), la = p("left_arm");
		if (ra != null) ra.xRot += -wind * 0.6F + punch * 0.5F;
		if (la != null) la.xRot += -wind * 0.6F + punch * 0.5F;
		// 날개 (안쪽 + 바깥 마디가 늦게 따라옴), 돌진할수록 빠르게
		float f1 = Mth.sin(t * 0.22F), f2 = Mth.sin(t * 0.55F);
		float flap = lerp(f1, f2, w2) * (0.42F + Mth.clamp(s.vy, 0F, 0.4F)), flap2 = lerp(Mth.sin(t * 0.22F - 0.9F), Mth.sin(t * 0.55F - 0.9F), w2) * 0.5F;
		ModelPart rw = p("right_wing"), lw = p("left_wing"), rw2 = p("right_wing2"), lw2 = p("left_wing2");
		if (rw != null) { rw.zRot += flap; rw.yRot += 0.5F * w3; }
		if (lw != null) { lw.zRot -= flap; lw.yRot -= 0.5F * w3; }
		if (rw2 != null) rw2.zRot += flap2;
		if (lw2 != null) lw2.zRot -= flap2;
		// 꼬리·목·망토: 몸을 돌리면 반대로 휘고, 걸을수록 크게 흔들림
		for (int i = 1; i <= 8; i++) {
			ModelPart tl = p("tail" + i);
			if (tl == null) break;
			tl.yRot += Mth.sin(t * 0.09F - i * 0.7F) * (0.09F + 0.06F * amt) - lean * 0.35F * i / 3F;
			tl.xRot += -punch * 0.08F * i + Mth.sin(t * 0.06F - i * 0.6F) * 0.035F + Mth.sin(walk * 0.6F - i * 0.8F) * 0.05F * amt;
		}
		for (int i = 1; i <= 4; i++) {
			ModelPart nk = p("neck" + i);
			if (nk == null) break;
			nk.xRot += Mth.sin(t * 0.06F - i * 0.5F) * 0.05F - 0.1F * w1 + Mth.sin(walk * 0.6F - i * 0.5F) * 0.04F * amt;
			nk.yRot += Mth.sin(t * 0.05F - i * 0.5F) * 0.06F + s.headYawS * Mth.DEG_TO_RAD * 0.12F;
		}
		for (int i = 1; i <= 8; i++) {
			ModelPart cp = p("cape" + i);
			if (cp == null) break;
			cp.xRot += 0.06F + Mth.sin(t * 0.1F - i * 0.6F) * 0.06F + amt * 0.25F * (i == 1 ? 1 : 0.35F) + Math.max(0F, -s.vy) * 0.4F;
			cp.zRot += -lean * 0.4F;
			cp.xRot += punch * 0.25F * (1 + i * 0.2F) + fl * 0.2F;
		}
		for (List<ModelPart> chain : sways.values()) {
			int i = 0;
			for (ModelPart sp : chain) {
				i++;
				sp.zRot += Mth.sin(t * 0.08F - i * 0.7F + chain.size()) * 0.06F - lean * 0.2F;
				sp.xRot += Mth.sin(t * 0.065F - i * 0.6F) * 0.06F + amt * 0.08F;
			}
		}
		// 쌍둥이: 서로 반대로 떠오르고 살짝 돌며, 공격 땐 바깥으로 벌어짐
		for (int i = 1; i <= 2; i++) {
			ModelPart tw = p("twin" + i);
			if (tw == null) continue;
			float sgn = i == 1 ? -1 : 1;
			tw.y += Mth.sin(t * 0.09F + i * Mth.PI) * 1.4F;
			tw.yRot += Mth.sin(t * 0.04F + i) * 0.12F;
			tw.x += sgn * (act * 2.5F + punch * 2F);
			tw.zRot += sgn * (0.04F * Mth.sin(t * 0.07F) + act * 0.12F);
		}
		for (ModelPart sp : spins) sp.zRot += t * 0.08F;
		for (int i = 0; i < bobs.size(); i++) bobs.get(i).y += Mth.sin(t * 0.09F + i * 1.7F) * 1.5F;
		for (int i = 0; i < pulses.size(); i++) {
			float k = 1.0F + Mth.sin(t * 0.15F + i) * 0.12F;
			ModelPart pp = pulses.get(i);
			pp.xScale = k; pp.yScale = k; pp.zScale = k;
		}
		// 다리 (허벅지 + 무릎이 늦게 굽어 발이 들림)
		float c = Mth.cos(walk * 0.6F) * 0.8F * amt;
		legPhase("right_leg", walk, amt, 0F); legPhase("left_leg", walk, amt, Mth.PI);
		legPhase("front_right_leg", walk, amt, Mth.PI); legPhase("front_left_leg", walk, amt, 0F);
		for (int i = 1; i <= 8; i++) {
			ModelPart lg = p("leg" + i);
			if (lg == null) break;
			float ph = (i % 2 == 0 ? Mth.PI : 0.0F) + i * 0.4F;
			lg.yRot += Mth.cos(walk * 0.8F + ph) * 0.35F * amt + Mth.sin(t * 0.05F + i) * 0.04F;
			lg.zRot += Math.abs(Mth.sin(walk * 0.8F + ph)) * 0.25F * amt * (i % 2 == 0 ? 1 : -1);
		}
	}

	private void arm(ModelPart a, ModelPart a2, int side, float sway, float walk, float amt, float breath, float w1, float w2, float w3, float fl) {
		if (a == null) return;
		float swing = Mth.cos(walk * 0.6F) * 0.55F * amt * side;
		float nx = a.xRot + side * sway - swing, nz = a.zRot + side * (0.12F + sway + fl * 0.35F);
		float x = nx, z = nz;
		x = lerp(x, -2.6F, w1); z = lerp(z, side * 0.3F, w1);
		x = lerp(x, -1.4F, w2);
		z = lerp(z, side * 1.3F, w3);
		a.xRot = x; a.zRot = z;
		if (a2 != null) {
			float elbow = -0.25F - breath * 0.05F - Math.max(0F, Mth.sin(walk * 0.6F * side + 0.9F)) * 0.35F * amt;
			float e = lerp(a2.xRot + elbow, -0.3F, w1);
			e = lerp(e, -0.8F, w2);
			e = lerp(e, -0.2F, w3);
			a2.xRot = e;
		}
	}

	private void legPhase(String name, float walk, float amt, float ph) {
		ModelPart l = p(name), k = p(name + "2");
		if (l == null) return;
		l.xRot += Mth.cos(walk * 0.6F + ph) * 0.8F * amt;
		if (k != null) k.xRot += Math.max(0.0F, Mth.sin(walk * 0.6F + ph - 0.6F)) * 0.9F * amt;
	}

	private void leg(String name, float swing) {
		ModelPart l = p(name), k = p(name + "2");
		if (l == null) return;
		l.xRot += swing;
		if (k != null) k.xRot += Math.max(0.0F, -swing) * 0.9F;
	}
}
