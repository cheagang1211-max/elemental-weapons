package com.elementalweapons.client.fx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.fx.ModParticles;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.ElementalWeaponItem;
import com.elementalweapons.item.WeaponType;

/**
 * 근접 무기를 휘두르면 무기 종류별 콤보 궤적(초승달 모양 빛의 띠)을 그립니다.
 * 검 3연격(가로-역가로-내려찍기), 대검 큰 원호, 망치 내려찍기+충격파, 낫 거대 회전, 단검 X자 2연타
 */
public final class SlashFx {
	private SlashFx() {}

	/** 진행 중인 궤적 하나 */
	private static final class Arc {
		final Player p; final double a0, a1, roll, r1, width; final int dur, c1, c2; final boolean slam;
		int age = -0; double prog = 0; int delay;
		Arc(Player p, double a0, double a1, double roll, double r1, double width, int dur, int c1, int c2, boolean slam, int delay) {
			this.p = p; this.a0 = Math.toRadians(a0); this.a1 = Math.toRadians(a1); this.roll = Math.toRadians(roll);
			this.r1 = r1; this.width = width; this.dur = dur; this.c1 = c1; this.c2 = c2; this.slam = slam; this.delay = delay;
		}
	}

	private static final List<Arc> ARCS = new ArrayList<>();
	/** 플레이어 id → {직전 swingTime, 콤보 번호, 마지막 휘두른 틱} */
	private static final Map<Integer, int[]> STATE = new HashMap<>();
	private static int clock;

	public static void tick(Minecraft mc) {
		ClientLevel level = mc.level;
		if (level == null || mc.player == null) { ARCS.clear(); STATE.clear(); return; }
		clock++;
		for (Player p : level.players()) {
			if (p.distanceToSqr(mc.player) > 64 * 64) continue;
			int[] st = STATE.computeIfAbsent(p.getId(), k -> new int[]{0, 0, -100});
			int prog = p.isSwinging() ? Math.round(p.getSwingAnimation(1.0F) * 1000) : 0;
			var sw = p.getCurrentSwing();
			boolean start = p.isSwinging() && (sw == null || sw.hand() == InteractionHand.MAIN_HAND) && (st[0] == 0 || prog < st[0] - 50);
			st[0] = prog;
			if (!start) continue;
			if (!(p.getMainHandItem().getItem() instanceof ElementalWeaponItem w)) continue;
			int combo = clock - st[2] < 30 ? (st[1] + 1) % 3 : 0;
			st[1] = combo; st[2] = clock;
			begin(mc, p, w.type(), w.element(), combo);
		}
		for (Iterator<Arc> it = ARCS.iterator(); it.hasNext(); ) {
			Arc a = it.next();
			if (a.p.isRemoved()) { it.remove(); continue; }
			if (a.delay > 0) { a.delay--; continue; }
			a.age++;
			double np = Math.min(1.0, a.age / (double) a.dur);
			draw(level, a, a.prog, np);
			a.prog = np;
			if (np >= 1.0) {
				if (a.slam) slamBurst(level, a);
				it.remove();
			}
		}
	}

	private static void begin(Minecraft mc, Player p, WeaponType type, Element el, int combo) {
		int c1 = ElementFx.color(el), c2 = ElementFx.color2(el);
		if (el == Element.DARK) c2 = 0xD7A6FF;
		double lunge = 0;
		switch (type) {
			case DAGGER -> {
				double s = combo % 2 == 0 ? 1 : -1;
				ARCS.add(new Arc(p, -75 * s, 75 * s, 40, 1.9, 0.55, 2, c1, c2, false, 0));
				ARCS.add(new Arc(p, 75 * s, -75 * s, -40, 1.9, 0.55, 2, c1, c2, false, 2));
				lunge = 0.45;
			}
			case SWORD -> {
				if (combo == 0) ARCS.add(new Arc(p, -115, 105, 15, 2.5, 0.9, 3, c1, c2, false, 0));
				else if (combo == 1) ARCS.add(new Arc(p, 115, -105, -20, 2.5, 0.9, 3, c1, c2, false, 0));
				else ARCS.add(new Arc(p, 125, -50, 92, 2.6, 1.0, 3, c1, c2, true, 0));
				lunge = 0.22;
			}
			case GREATSWORD -> {
				if (combo == 0) ARCS.add(new Arc(p, -135, 130, 8, 3.4, 1.5, 4, c1, c2, false, 0));
				else if (combo == 1) ARCS.add(new Arc(p, 135, -130, -12, 3.4, 1.5, 4, c1, c2, false, 0));
				else ARCS.add(new Arc(p, 135, -45, 90, 3.5, 1.6, 4, c1, c2, true, 0));
				lunge = 0.32;
			}
			case HAMMER -> {
				ARCS.add(new Arc(p, 145, -38, 90, 2.8, 1.2, 4, c1, c2, true, 0));
				lunge = 0.2;
			}
			case SCYTHE -> {
				if (combo == 0) ARCS.add(new Arc(p, -165, 165, 4, 3.7, 1.6, 5, c1, c2, false, 0));
				else if (combo == 1) ARCS.add(new Arc(p, 165, -165, -14, 3.7, 1.6, 5, c1, c2, false, 0));
				else { ARCS.add(new Arc(p, -150, 140, 55, 3.5, 1.5, 4, c1, c2, false, 0)); ARCS.add(new Arc(p, 150, -140, -55, 3.2, 1.2, 4, c2, c1, false, 2)); }
				lunge = 0.3;
			}
			default -> { return; }
		}
		// 내 캐릭터: 휘두를 때 앞으로 살짝 내딛기 (블록을 캐는 중이면 제외)
		if (p == mc.player && p.onGround() && !p.isShiftKeyDown() && (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK)) {
			Vec3 f = Vec3.directionFromRotation(0, p.getYRot());
			p.setDeltaMovement(p.getDeltaMovement().add(f.x * lunge, 0.02, f.z * lunge));
		}
	}

	private static Vec3[] frame(Player p) {
		double yaw = Math.toRadians(p.getYRot()), pitch = Math.toRadians(p.getXRot() * 0.45);
		Vec3 f = new Vec3(-Math.sin(yaw) * Math.cos(pitch), -Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch));
		Vec3 r = new Vec3(-Math.cos(yaw), 0, -Math.sin(yaw));
		Vec3 u = r.cross(f).normalize();
		return new Vec3[]{f, r, u};
	}

	private static void draw(ClientLevel level, Arc a, double p0, double p1) {
		Vec3[] fr = frame(a.p);
		Vec3 f = fr[0], r = fr[1], u = fr[2];
		Vec3 rr = r.scale(Math.cos(a.roll)).add(u.scale(Math.sin(a.roll)));
		Vec3 c = a.p.getEyePosition().subtract(u.scale(0.45)).add(f.scale(0.15));
		double span = Math.abs(a.a1 - a.a0);
		int steps = Math.max(2, (int) Math.ceil(span * (p1 - p0) / Math.toRadians(2.5)));
		for (int i = 0; i <= steps; i++) {
			double t = p0 + (p1 - p0) * i / steps;
			double th = a.a0 + (a.a1 - a.a0) * t;
			Vec3 dir = f.scale(Math.cos(th)).add(rr.scale(Math.sin(th)));
			Vec3 tan = f.scale(-Math.sin(th)).add(rr.scale(Math.cos(th))).scale(Math.signum(a.a1 - a.a0));
			double thick = a.width * Math.pow(Math.sin(Math.PI * Math.min(1, Math.max(0, t))), 0.7) + 0.08;
			int layers = Math.max(2, (int) Math.round(thick / 0.16));
			for (int k = 0; k <= layers; k++) {
				double q = k / (double) layers;             // 0 = 바깥 날, 1 = 안쪽
				double rad = a.r1 - thick * q;
				Vec3 pos = c.add(dir.scale(rad));
				int col = q < 0.18 ? mix(a.c2, 0xFFFFFF, 0.55) : q < 0.5 ? a.c2 : a.c1;
				float size = (float) (q < 0.18 ? 0.42 : 0.55 - 0.3 * q);
				Vec3 v = tan.scale(0.06).add(dir.scale(0.015));
				level.addAlwaysVisibleParticle(ModParticles.slash(col, size), pos.x, pos.y, pos.z, v.x, v.y, v.z);
			}
		}
	}

	private static void slamBurst(ClientLevel level, Arc a) {
		Vec3[] fr = frame(a.p);
		Vec3 f = new Vec3(fr[0].x, 0, fr[0].z).normalize();
		Vec3 c = a.p.position().add(f.scale(2.2)).add(0, 0.15, 0);
		for (int i = 0; i < 48; i++) {
			double ang = i * Math.PI * 2 / 48;
			Vec3 d = new Vec3(Math.cos(ang), 0, Math.sin(ang));
			level.addAlwaysVisibleParticle(ModParticles.slash(i % 2 == 0 ? a.c1 : a.c2, 0.6F), c.x + d.x * 0.4, c.y, c.z + d.z * 0.4, d.x * 0.42, 0.02, d.z * 0.42);
		}
		for (int i = 0; i < 16; i++) {
			level.addAlwaysVisibleParticle(ModParticles.slash(mix(a.c2, 0xFFFFFF, 0.5), 0.5F), c.x, c.y, c.z, (Math.random() - 0.5) * 0.3, 0.25 + Math.random() * 0.3, (Math.random() - 0.5) * 0.3);
		}
	}

	private static int mix(int a, int b, double t) {
		int r = (int) (((a >> 16) & 255) * (1 - t) + ((b >> 16) & 255) * t);
		int g = (int) (((a >> 8) & 255) * (1 - t) + ((b >> 8) & 255) * t);
		int bl = (int) ((a & 255) * (1 - t) + (b & 255) * t);
		return (r << 16) | (g << 8) | bl;
	}
}
