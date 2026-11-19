package com.elementalweapons.client.growth;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import com.elementalweapons.growth.Growth;

/** 왼쪽 위 레벨 패널 + 레벨업 연출 */
public final class GrowthHud {
	private GrowthHud() {}

	public static void render(GuiGraphicsExtractor g, DeltaTracker dt) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;
		Font font = mc.font;
		Growth gr = GrowthClient.current();
		int x = 4, y = 4, w = 132, h = 36;
		// 패널
		g.fillGradient(x, y, x + w, y + h, 0xE00E1022, 0xE01E1230);
		g.outline(x, y, w, h, 0xC0A68CFF);
		// 레벨 배지
		int bx = x + 3, by = y + 3, bs = 30;
		g.fillGradient(bx, by, bx + bs, by + bs, 0xFFFFE9A0, 0xFFB8650F);
		g.outline(bx, by, bs, bs, 0xFFFFF0C0);
		g.centeredText(font, "LV", bx + bs / 2, by + 3, 0xFF5A2C00);
		String lv = String.valueOf(gr.level());
		g.pose().pushMatrix();
		float s = lv.length() >= 3 ? 1.2F : 1.6F;
		g.pose().translate(bx + bs / 2F, by + 13);
		g.pose().scale(s, s);
		g.centeredText(font, lv, 0, 0, 0xFFFFFFFF);
		g.pose().popMatrix();
		// 이름·칭호
		int tx = bx + bs + 5;
		g.text(font, mc.player.getName().getString(), tx, y + 4, 0xFFE9DDFF, true);
		g.text(font, gr.title(), tx, y + 14, 0xFFFFCF6B, true);
		// 포인트
		if (gr.points() > 0) {
			String pts = "+" + gr.points();
			int pw = font.width(pts) + 6;
			int px = x + w - pw - 3;
			boolean blink = (GrowthClient.tick / 10) % 2 == 0;
			g.fill(px, y + 3, px + pw, y + 13, blink ? 0xFFFFD34D : 0xFFE0A920);
			g.text(font, pts, px + 3, y + 4, 0xFF1B1200, false);
		}
		// 경험치 바
		int barX = tx, barY = y + 25, barW = x + w - 4 - tx, barH = 7;
		g.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF000000);
		g.fill(barX, barY, barX + barW, barY + barH, 0xFF0B0B16);
		boolean max = gr.level() >= Growth.MAX_LEVEL;
		double ratio = max ? 1.0 : Math.min(1.0, gr.xp() / (double) Growth.need(gr.level()));
		int fw = (int) (barW * ratio);
		for (int i = 0; i < fw; i++) g.fill(barX + i, barY, barX + i + 1, barY + barH, grad(i / (double) Math.max(1, barW)));
		if (fw > 0) g.fill(barX + fw - 1, barY, barX + fw, barY + barH, 0xFFFFFFFF);
		String xpText = max ? "MAX" : fmt(gr.xp()) + " / " + fmt(Growth.need(gr.level()));
		g.pose().pushMatrix();
		g.pose().translate(barX + barW / 2F, barY);
		g.pose().scale(0.75F, 0.75F);
		g.centeredText(font, xpText, 0, 1, 0xFFFFFFFF);
		g.pose().popMatrix();
		// 경험치 획득 팝업
		int py = y + 8;
		for (int[] p : GrowthClient.popups) {
			int a = (int) (255 * Math.max(0, 1 - p[1] / 40.0));
			if (a < 8) continue;
			g.text(font, "+" + p[0] + " XP", x + w + 4, py - p[1] / 3, (a << 24) | 0x7FF3FF, true);
			py += 10;
		}
		// 레벨업 연출
		if (GrowthClient.levelUpTicks > 0) levelUp(g, font, GrowthClient.levelUpTicks);
	}

	private static void levelUp(GuiGraphicsExtractor g, Font font, int t) {
		int sw = g.guiWidth(), sh = g.guiHeight();
		int age = 70 - t;
		float fade = t < 15 ? t / 15F : 1F;
		float pop = age < 6 ? 0.6F + age / 15F : 1F;
		int a = (int) (255 * fade);
		int cy = (int) (sh * 0.24);
		// 빛 띠
		int glowA = (int) (110 * fade);
		g.fillGradient(0, cy - 26, sw, cy, (glowA / 6) << 24 | 0xFFC23A, glowA << 24 | 0xFFC23A);
		g.fillGradient(0, cy, sw, cy + 26, glowA << 24 | 0xFFC23A, (glowA / 6) << 24 | 0xFFC23A);
		// 반짝이
		for (int i = 0; i < 28; i++) {
			double ang = i / 28.0 * Math.PI * 2 + age * 0.03;
			double r = 70 + age * 1.2 + (i % 3) * 8;
			int px = (int) (sw / 2 + Math.cos(ang) * r), py = (int) (cy + Math.sin(ang) * r * 0.35);
			g.fill(px, py, px + 2, py + 2, a << 24 | 0xFFE27A);
		}
		g.pose().pushMatrix();
		g.pose().translate(sw / 2F, cy - 14);
		g.pose().scale(3F * pop, 3F * pop);
		g.centeredText(font, "LEVEL UP!", 0, 0, a << 24 | 0xFFC23A);
		g.pose().popMatrix();
		g.pose().pushMatrix();
		g.pose().translate(sw / 2F, cy + 14);
		g.pose().scale(1.5F, 1.5F);
		g.centeredText(font, "Lv " + GrowthClient.levelUpFrom + " → Lv " + GrowthClient.levelUpTo, 0, 0, a << 24 | 0xFFFFFF);
		g.pose().popMatrix();
		g.centeredText(font, "스탯 포인트 +" + (GrowthClient.levelUpTo - GrowthClient.levelUpFrom) * Growth.POINTS_PER_LEVEL + "  ·  [K] 성장 창 열기", sw / 2, cy + 32, a << 24 | 0xFFE9A8);
	}

	static int grad(double t) {
		int[] c = t < 0.6 ? lerp(0x2FE0FF, 0x7A6BFF, t / 0.6) : lerp(0x7A6BFF, 0xFF6BD6, (t - 0.6) / 0.4);
		return 0xFF000000 | c[0] << 16 | c[1] << 8 | c[2];
	}

	private static int[] lerp(int a, int b, double t) {
		int[] r = new int[3];
		for (int k = 0; k < 3; k++) {
			int sh = 16 - k * 8;
			int ca = (a >> sh) & 255, cb = (b >> sh) & 255;
			r[k] = (int) (ca + (cb - ca) * t);
		}
		return r;
	}

	static String fmt(long v) { return String.format("%,d", v); }
}
