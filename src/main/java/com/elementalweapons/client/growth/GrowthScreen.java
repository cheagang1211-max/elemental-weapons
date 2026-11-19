package com.elementalweapons.client.growth;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;

import com.elementalweapons.growth.Growth;
import com.elementalweapons.growth.GrowthPayloads;

/** K키 성장 창 */
public class GrowthScreen extends Screen {
	private static final int W = 300, H = 214;
	private static final String[] NAMES = {"⚔ 힘", "✦ 마력", "❤ 체력", "➶ 민첩"};
	private static final String[] DESCS = {"보스 피해 +3%, 근접 공격력 +0.2", "지팡이 피해 +3%, 쿨타임 -0.6%", "최대 체력 +1 (하트 반 칸)", "이동 속도 +0.4%, 공격 속도 +0.6%"};
	private static final int[] COLORS = {0xFFFF8A6A, 0xFFB0A2FF, 0xFFFF6A84, 0xFF6AFFBA};
	private int left, top;

	public GrowthScreen() { super(Component.literal("원소 성장")); }

	@Override
	protected void init() {
		left = (width - W) / 2;
		top = (height - H) / 2;
		for (int i = 0; i < 4; i++) {
			final int stat = i;
			addRenderableWidget(Button.builder(Component.literal("+"), b ->
					ClientPlayNetworking.send(new GrowthPayloads.Spend(stat, minecraft.hasShiftDown() ? 5 : 1)))
					.bounds(left + W - 28, top + 32 + i * 34 + 6, 20, 20).build());
		}
		addRenderableWidget(Button.builder(Component.literal("초기화 (원소 정수 1)"), b ->
				ClientPlayNetworking.send(GrowthPayloads.Reset.INSTANCE))
				.bounds(left + W - 112, top + H - 22, 104, 16).build());
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mx, int my, float pt) {
		super.extractBackground(g, mx, my, pt);
		g.fillGradient(left, top, left + W, top + H, 0xF5151A33, 0xF5160E28);
		g.outline(left, top, W, H, 0xFFA68CFF);
		g.fillGradient(left + 1, top + 1, left + W - 1, top + 24, 0x40FFC850, 0x00FFC850);
		g.horizontalLine(left + 1, left + W - 2, top + 24, 0x80A68CFF);
		// 왼쪽 캐릭터 칸
		g.fill(left + 8, top + 30, left + 92, top + H - 8, 0x60000000);
		g.outline(left + 8, top + 30, 84, H - 38, 0xFF3B3260);
		// 스탯 줄
		for (int i = 0; i < 4; i++) {
			int ry = top + 32 + i * 34;
			g.fill(left + 98, ry, left + W - 6, ry + 31, 0x18FFFFFF);
			g.outline(left + 98, ry, W - 104, 31, 0xFF3B3260);
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float pt) {
		super.extractRenderState(g, mx, my, pt);
		Growth gr = GrowthClient.current();
		g.text(font, "원소 성장", left + 8, top + 8, 0xFFFFFFFF, true);
		g.text(font, "Lv " + gr.level(), left + 62, top + 8, 0xFFFFD34D, true);
		g.text(font, gr.title(), left + 100, top + 8, 0xFFFFCF6B, true);
		g.text(font, "[K] 닫기", left + W - 46, top + 8, 0xFF9A93B8, false);
		if (minecraft.player != null) {
			InventoryScreen.extractEntityInInventoryFollowsMouse(g, left + 10, top + 32, left + 90, top + 112, 32, 0.0625F, mx, my, minecraft.player);
		}
		int sy = top + 118;
		line(g, "보스 피해", "+" + pct(gr.meleeBossBonus() - 1), sy);
		line(g, "지팡이 피해", "+" + pct(gr.staffBossBonus() - 1), sy + 10);
		line(g, "지팡이 쿨타임", "-" + pct(1 - gr.staffCooldownFactor()), sy + 20);
		line(g, "최대 체력", "+" + (int) gr.bonusHealth(), sy + 30);
		line(g, "이동 속도", "+" + pct(gr.speedBonus()), sy + 40);
		for (int i = 0; i < 4; i++) {
			int ry = top + 32 + i * 34;
			int v = gr.stat(i);
			g.text(font, NAMES[i], left + 104, ry + 4, COLORS[i], true);
			g.pose().pushMatrix();
			g.pose().translate(left + 104, ry + 15);
			g.pose().scale(0.75F, 0.75F);
			g.text(font, DESCS[i], 0, 0, 0xFFC9C2E6, false);
			g.pose().popMatrix();
			int bx = left + 104, bw = 140, by = ry + 24;
			g.fill(bx, by, bx + bw, by + 3, 0xFF0B0B16);
			g.fill(bx, by, bx + bw * v / Growth.MAX_STAT, by + 3, COLORS[i]);
			String vs = String.valueOf(v);
			g.pose().pushMatrix();
			g.pose().translate(left + W - 36 - font.width(vs) * 1.5F, ry + 9);
			g.pose().scale(1.5F, 1.5F);
			g.text(font, vs, 0, 0, v >= Growth.MAX_STAT ? 0xFFFFD34D : 0xFFFFFFFF, true);
			g.pose().popMatrix();
		}
		// 경험치 바 & 남은 포인트
		int barX = left + 98, barY = top + H - 38, barW = W - 104;
		g.fill(barX, barY, barX + barW, barY + 8, 0xFF0B0B16);
		boolean max = gr.level() >= Growth.MAX_LEVEL;
		int fw = (int) (barW * (max ? 1.0 : Math.min(1.0, gr.xp() / (double) Growth.need(gr.level()))));
		for (int i = 0; i < fw; i++) g.fill(barX + i, barY, barX + i + 1, barY + 8, GrowthHud.grad(i / (double) barW));
		String xt = max ? "최대 레벨" : "다음 레벨까지 " + GrowthHud.fmt(gr.xp()) + " / " + GrowthHud.fmt(Growth.need(gr.level())) + " XP";
		g.pose().pushMatrix();
		g.pose().translate(barX + barW / 2F, barY + 1);
		g.pose().scale(0.75F, 0.75F);
		g.centeredText(font, xt, 0, 0, 0xFFFFFFFF);
		g.pose().popMatrix();
		g.text(font, "남은 포인트", left + 98, top + H - 18, 0xFFFFFFFF, true);
		g.text(font, String.valueOf(gr.points()), left + 98 + font.width("남은 포인트 "), top + H - 18, 0xFFFFD34D, true);
		g.pose().pushMatrix();
		g.pose().translate(left + 98, top + H - 8);
		g.pose().scale(0.6F, 0.6F);
		g.text(font, "Shift+클릭: 5포인트씩 · 스탯 최대 " + Growth.MAX_STAT, 0, 0, 0xFF8A83A8, false);
		g.pose().popMatrix();
	}

	private void line(GuiGraphicsExtractor g, String k, String v, int y) {
		g.pose().pushMatrix();
		g.pose().translate(left + 12, y);
		g.pose().scale(0.75F, 0.75F);
		g.text(font, k, 0, 0, 0xFFD8D2F0, false);
		g.text(font, v, (int) (76 / 0.75F) - font.width(v), 0, 0xFF9FF0FF, false);
		g.pose().popMatrix();
	}

	private static String pct(double v) { return Math.round(v * 100) + "%"; }

	@Override
	public boolean keyPressed(net.minecraft.client.input.KeyEvent e) {
		if (GrowthClient.OPEN_KEY.matches(e)) { onClose(); return true; }
		return super.keyPressed(e);
	}

	@Override
	public boolean isPauseScreen() { return false; }
}
