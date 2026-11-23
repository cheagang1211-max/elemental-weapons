package com.elementalweapons.client.growth;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.growth.Evolution;
import com.elementalweapons.growth.Growth;
import com.elementalweapons.growth.Jobs;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.PlayerClass;

/** 전직 교관 창: 시험 받기 → 진행도 → 2차 갈래 고르기 / 3차 전직 */
public class JobScreen extends Screen {
	private static final int W = 300, H = 170;
	private int left, top;

	public JobScreen() { super(Component.literal("전직 교관")); }

	private Jobs.Data data() {
		var p = Minecraft.getInstance().player;
		Jobs.Data d = p == null ? null : p.getAttached(Jobs.DATA);
		return d == null ? Jobs.Data.DEFAULT : d;
	}
	private int element() {
		var p = Minecraft.getInstance().player;
		Integer e = p == null ? null : p.getAttached(Evolution.ELEMENT);
		return e == null ? -1 : e;
	}
	private int color() { int e = element(); return 0xFF000000 | (e < 0 ? 0xFFD34D : ElementFx.color(Element.values()[e])); }

	@Override
	protected void init() {
		left = (width - W) / 2; top = (height - H) / 2;
		rebuild();
	}

	private void rebuild() {
		clearWidgets();
		Growth g = GrowthClient.current();
		Jobs.Data d = data();
		int st = Jobs.state(g.level(), element() >= 0, d);
		if (st == 1) addRenderableWidget(Button.builder(Component.literal("⚔ " + (d.stage() + 1) + "차 전직 시험 받기"), b -> send(1, 0))
				.bounds(width / 2 - 70, top + H - 30, 140, 20).build());
		if (st == 3 && d.trial() == 2) for (int b = 1; b <= 2; b++) {
			final int br = b;
			addRenderableWidget(Button.builder(Component.literal("이 길로 전직"), x -> send(2, br))
					.bounds(left + (b == 1 ? 14 : W / 2 + 6), top + H - 28, W / 2 - 20, 18).build());
		}
		if (st == 3 && d.trial() == 3) addRenderableWidget(Button.builder(Component.literal("✦ 3차 전직"), b -> send(3, 0))
				.bounds(width / 2 - 60, top + H - 30, 120, 20).build());
		addRenderableWidget(Button.builder(Component.literal("닫기"), b -> onClose()).bounds(left + W - 44, top + 6, 38, 14).build());
	}

	private void send(int action, int arg) {
		ClientPlayNetworking.send(new Jobs.Action(action, arg));
		if (action != 1) onClose();
		else minecraft.execute(this::rebuild);
	}

	private int lastSig = -1;

	@Override
	public void tick() {
		Jobs.Data d = data();
		int sig = d.stage() * 1000 + d.trial() * 100 + (Jobs.done(d) ? 1 : 0);
		if (sig != lastSig) { lastSig = sig; rebuild(); }
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mx, int my, float pt) {
		super.extractBackground(g, mx, my, pt);
		int c = color();
		g.fillGradient(left, top, left + W, top + H, 0xF0161A30, 0xF0120C24);
		g.outline(left, top, W, H, c);
		Growth gr = GrowthClient.current();
		Jobs.Data d = data();
		if (Jobs.state(gr.level(), element() >= 0, d) == 3 && d.trial() == 2) {
			for (int b = 0; b < 2; b++) {
				int x = left + (b == 0 ? 8 : W / 2 + 2);
				g.fillGradient(x, top + 48, x + W / 2 - 10, top + H - 6, 0xC0242A48, 0xC01A1430);
				g.outline(x, top + 48, W / 2 - 10, H - 54, c);
			}
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float pt) {
		super.extractRenderState(g, mx, my, pt);
		Growth gr = GrowthClient.current();
		Jobs.Data d = data();
		int cls = gr.cls(), c = color();
		boolean evolved = element() >= 0;
		int st = Jobs.state(gr.level(), evolved, d);
		g.pose().pushMatrix();
		g.pose().translate(left + 10, top + 8);
		g.pose().scale(1.4F, 1.4F);
		g.text(font, "⚔ 전직 교관", 0, 0, 0xFFFF8A8A, true);
		g.pose().popMatrix();
		String now = evolved ? "현재: " + Jobs.title(cls, d.branch(), d.stage()) + " (" + Math.max(1, d.stage()) + "차) · Lv " + gr.level() : "현재: 견습 · Lv " + gr.level();
		g.text(font, now, left + 10, top + 26, c, false);
		int y = top + 50;
		if (!evolved) {
			line(g, "먼저 10레벨에 1차 전직(직업·속성)을 하세요. [K] 성장 창", y, 0xFFE0D8F0);
			return;
		}
		if (d.stage() >= 3) {
			line(g, "✦ 최종 전직을 마쳤습니다. 더 이상 가르칠 것이 없군요.", y, 0xFFFFE08A);
			line(g, "전직 기술 [V]: " + Jobs.skill(cls, d.branch(), 3), y + 14, 0xFFE0D8F0);
			return;
		}
		int next = d.stage() + 1, need = next == 2 ? Jobs.LV2 : Jobs.LV3;
		int k = next == 2 ? Jobs.KILLS2 : Jobs.KILLS3, b = next == 2 ? Jobs.BOSSES2 : Jobs.BOSSES3;
		switch (st) {
			case 0 -> {
				line(g, next + "차 전직은 Lv " + need + "부터 받을 수 있습니다. (지금 Lv " + gr.level() + ")", y, 0xFFE0D8F0);
				line(g, "시험: 원소 지역 몹 " + k + "마리 + 보스 " + b + "마리 처치", y + 14, 0xFFB8B0D0);
				if (next == 3) line(g, "다음 직업: " + Jobs.title(cls, d.branch(), 3) + " · 각성 기술 [V]", y + 28, c);
				else preview(g, cls, y + 34);
			}
			case 1 -> {
				line(g, next + "차 전직 시험을 받을 수 있습니다!", y, 0xFFFFE08A);
				line(g, "시험: 원소 지역 몹 " + k + "마리 + 보스(보스 관문에서 소환석) " + b + "마리 처치", y + 14, 0xFFE0D8F0);
				if (next == 3) line(g, "다음 직업: " + Jobs.title(cls, d.branch(), 3), y + 28, c); else preview(g, cls, y + 34);
			}
			case 2 -> {
				line(g, next + "차 전직 시험 중", y, 0xFFFFE08A);
				line(g, "원소 지역 몹  " + Math.min(k, d.kills()) + " / " + k, y + 18, 0xFFE0D8F0);
				line(g, "보스 처치  " + Math.min(b, d.bosses()) + " / " + b, y + 32, 0xFFE0D8F0);
				int bw = W - 40, x0 = left + 20;
				g.fill(x0, y + 50, x0 + bw, y + 56, 0xFF2A2A40);
				double pr = (Math.min(k, d.kills()) / (double) k + Math.min(b, d.bosses()) / (double) b) / 2;
				g.fill(x0, y + 50, x0 + (int) (bw * pr), y + 56, c);
			}
			case 3 -> {
				if (d.trial() == 2) {
					for (int br = 1; br <= 2; br++) {
						int x = left + (br == 1 ? 14 : W / 2 + 8), yy = top + 54;
						g.pose().pushMatrix();
						g.pose().translate(x, yy);
						g.pose().scale(1.3F, 1.3F);
						g.text(font, Jobs.title(cls, br, 2), 0, 0, c, true);
						g.pose().popMatrix();
						wrap(g, Jobs.desc(cls, br), x, yy + 16, W / 2 - 24, 0xFFE0D8F0);
						g.text(font, "[V] " + Jobs.skill(cls, br, 2), x, yy + 50, 0xFFFFE08A, false);
						g.text(font, "3차 → " + Jobs.title(cls, br, 3), x, yy + 64, 0xFFB8B0D0, false);
					}
					g.centeredText(font, "시험 완료! 갈래를 고르세요 (되돌릴 수 없음)", width / 2, top + 40, 0xFF9AF09A);
				} else {
					line(g, "시험 완료! 최종 전직을 받을 수 있습니다.", y, 0xFF9AF09A);
					line(g, "→ " + Jobs.title(cls, d.branch(), 3) + " · [V] " + Jobs.skill(cls, d.branch(), 3), y + 16, c);
					line(g, "전직하면 광륜과 빛의 날개가 생기고 기술이 각성합니다.", y + 32, 0xFFE0D8F0);
				}
			}
			default -> {}
		}
	}

	private void preview(GuiGraphicsExtractor g, int cls, int y) {
		if (cls <= 0) return;
		for (int br = 1; br <= 2; br++)
			line(g, "갈래 " + br + ": " + Jobs.title(cls, br, 2) + " → " + Jobs.title(cls, br, 3) + "  (" + Jobs.desc(cls, br) + ")", y + (br - 1) * 12, 0xFFB8B0D0);
	}

	private void line(GuiGraphicsExtractor g, String s, int y, int col) {
		wrap(g, s, left + 12, y, W - 24, col);
	}

	private void wrap(GuiGraphicsExtractor g, String s, int x, int y, int w, int col) {
		int yy = y;
		for (var l : font.split(Component.literal(s), w)) { g.text(font, l, x, yy, col, false); yy += 10; }
	}

	@Override
	public boolean isPauseScreen() { return false; }

	static PlayerClass cls() { return GrowthClient.current().playerClass(); }
}
