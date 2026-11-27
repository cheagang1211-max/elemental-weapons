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
	private static final String[] PDESCS = {"모든 피해 +1% (상한 없음)", "마법 피해 +1%, 쿨타임 감소 (최대 35%)", "최대 체력 +1, 방어 강도 (최대 16)", "이동 속도·공격 속도 (최대 30%/50%)"};
	private static final int[] PCOLORS = {0xFFFF5A5A, 0xFFC27AFF, 0xFFFFD34D, 0xFF6AE8FF};
	public static boolean paragonMode = false;
	private static final int PW = 164, PG = 4, RH = 11;
	private int left, top, px;
	private final Button[] coreBtns = new Button[com.elementalweapons.growth.CoreForge.IDS.length];

	public GrowthScreen() { super(Component.literal("원소 성장")); }

	@Override
	protected void init() {
		left = (width - W - PG - PW) / 2;
		top = (height - H) / 2;
		px = left + W + PG;
		for (int i = 0; i < coreBtns.length; i++) {
			final int idx = i;
			coreBtns[i] = addRenderableWidget(Button.builder(Component.literal("+"), b -> ClientPlayNetworking.send(new com.elementalweapons.growth.CoreForge.Up(idx)))
					.bounds(px + PW - 15, top + 27 + i * RH, 12, 10).build());
		}
		for (int i = 0; i < 4; i++) {
			final int stat = i;
			addRenderableWidget(Button.builder(Component.literal("+"), b -> {
					int n = minecraft.hasShiftDown() ? 5 : 1;
					if (paragonMode) ClientPlayNetworking.send(new GrowthPayloads.SpendParagon(stat, n));
					else ClientPlayNetworking.send(new GrowthPayloads.Spend(stat, n)); })
					.bounds(left + W - 28, top + 32 + i * 34 + 6, 20, 20).build());
		}
		addRenderableWidget(Button.builder(Component.literal("✧ 초월"), b -> paragonMode = !paragonMode)
				.bounds(left + W - 104, top + 4, 50, 16).build());
		if (minecraft.player != null && com.elementalweapons.growth.Evolution.pending(minecraft.player))
			addRenderableWidget(Button.builder(Component.literal("✦ 전직"), b -> minecraft.gui.setScreen(new com.elementalweapons.client.skill.EvolveScreen()))
					.bounds(left + 98, top + H + 4, 90, 16).build());
		addRenderableWidget(Button.builder(Component.literal("스탯 초기화 (무료)"), b -> ClientPlayNetworking.send(GrowthPayloads.Reset.INSTANCE))
				.bounds(left + W - 102, top + H + 4, 102, 16).build());
		addRenderableWidget(Button.builder(Component.literal("⚒ 제작대 열기"), b -> ClientPlayNetworking.send(GrowthPayloads.OpenCraft.INSTANCE))
				.bounds(left + W - 102, top + H + 22, 102, 16).build());
		boolean tutDone = minecraft.player != null && com.elementalweapons.growth.Tutorial.get(minecraft.player).step() >= com.elementalweapons.growth.Tutorial.DONE;
		addRenderableWidget(Button.builder(Component.literal(tutDone ? "튜토리얼 다시" : "튜토리얼 끄기"), b -> {
					ClientPlayNetworking.send(new GrowthPayloads.TutorialSkip(tutDone)); onClose(); })
				.bounds(left + 8, top + H + 4, 84, 16).build());
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
		// 핵 강화 칸
		g.fillGradient(px, top, px + PW, top + H, 0xF5151A33, 0xF5160E28);
		g.outline(px, top, PW, H, 0xFFFFB347);
		g.fillGradient(px + 1, top + 1, px + PW - 1, top + 24, 0x40FFB347, 0x00FFB347);
		g.horizontalLine(px + 1, px + PW - 2, top + 24, 0x80FFB347);
		for (int i = 0; i < com.elementalweapons.growth.CoreForge.IDS.length; i++)
			if (i % 2 == 0) g.fill(px + 2, top + 26 + i * RH, px + PW - 2, top + 26 + (i + 1) * RH, 0x14FFFFFF);
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
		renderCores(g);
		if (paragonMode) { renderParagon(g, gr, mx, my); return; }
		g.text(font, "원소 성장", left + 8, top + 8, 0xFFFFFFFF, true);
		g.text(font, "Lv " + gr.level(), left + 62, top + 8, 0xFFFFD34D, true);
		g.text(font, gr.playerClass() == com.elementalweapons.item.PlayerClass.NONE ? "견습" : gr.playerClass().koName, left + 100, top + 8, gr.playerClass().color, true);
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
		g.pose().pushMatrix();
		g.pose().translate(left + W - 8, top + H - 18);
		g.pose().scale(0.7F, 0.7F);
		g.text(font, "직업·속성 초기화: 운명의 현자", -font.width("직업·속성 초기화: 운명의 현자"), 0, 0xFFD080FF, false);
		g.pose().popMatrix();
		g.text(font, "남은 포인트", left + 98, top + H - 18, 0xFFFFFFFF, true);
		g.text(font, String.valueOf(gr.points()), left + 98 + font.width("남은 포인트 "), top + H - 18, 0xFFFFD34D, true);
		g.pose().pushMatrix();
		g.pose().translate(left + 98, top + H - 8);
		g.pose().scale(0.6F, 0.6F);
		g.text(font, "Shift+클릭: 5포인트씩 · 스탯 최대 " + Growth.MAX_STAT, 0, 0, 0xFF8A83A8, false);
		g.pose().popMatrix();
	}

	private void renderParagon(GuiGraphicsExtractor g, Growth gr, int mx, int my) {
		com.elementalweapons.growth.Paragon pg = minecraft.player == null ? com.elementalweapons.growth.Paragon.DEFAULT : com.elementalweapons.growth.ModGrowth.paragon(minecraft.player);
		boolean open = gr.level() >= Growth.MAX_LEVEL;
		g.text(font, "✧ 초월", left + 8, top + 8, 0xFFE0B0FF, true);
		g.text(font, "초월 " + pg.level(), left + 54, top + 8, 0xFFFFD34D, true);
		g.text(font, "[K] 닫기", left + W - 46, top + 8, 0xFF9A93B8, false);
		if (minecraft.player != null) {
			InventoryScreen.extractEntityInInventoryFollowsMouse(g, left + 10, top + 32, left + 90, top + 112, 32, 0.0625F, mx, my, minecraft.player);
		}
		int sy = top + 118;
		line(g, "모든 피해", "+" + pct(pg.damageMult() - 1), sy);
		line(g, "마법 피해", "+" + pct(pg.spellMult() - 1), sy + 10);
		line(g, "쿨타임 감소", "-" + pct(pg.cooldownCut()), sy + 20);
		line(g, "체력/방어", "+" + (int) pg.health() + " / " + String.format("%.1f", pg.toughness()), sy + 30);
		line(g, "균열 최고", pg.best() + "단계", sy + 40);
		for (int i = 0; i < 4; i++) {
			int ry = top + 32 + i * 34;
			int v = pg.stat(i);
			g.text(font, com.elementalweapons.growth.Paragon.NAMES[i], left + 104, ry + 4, PCOLORS[i], true);
			g.pose().pushMatrix();
			g.pose().translate(left + 104, ry + 15);
			g.pose().scale(0.75F, 0.75F);
			g.text(font, PDESCS[i], 0, 0, 0xFFC9C2E6, false);
			g.pose().popMatrix();
			int bx = left + 104, bw = 140, by = ry + 24;
			g.fill(bx, by, bx + bw, by + 3, 0xFF0B0B16);
			g.fill(bx, by, bx + (int) (bw * (1 - Math.exp(-v / 100.0))), by + 3, PCOLORS[i]);
			String vs = String.valueOf(v);
			g.pose().pushMatrix();
			g.pose().translate(left + W - 36 - font.width(vs) * 1.5F, ry + 9);
			g.pose().scale(1.5F, 1.5F);
			g.text(font, vs, 0, 0, 0xFFFFFFFF, true);
			g.pose().popMatrix();
		}
		int barX = left + 98, barY = top + H - 38, barW = W - 104;
		g.fill(barX, barY, barX + barW, barY + 8, 0xFF0B0B16);
		int fw = (int) (barW * Math.min(1.0, pg.xp() / (double) com.elementalweapons.growth.Paragon.need(pg.level())));
		for (int i = 0; i < fw; i++) g.fill(barX + i, barY, barX + i + 1, barY + 8, 0xFF000000 | (0x8A4AE0 + (i * 0x000100) % 0x4000));
		String xt = open ? "다음 초월까지 " + GrowthHud.fmt(pg.xp()) + " / " + GrowthHud.fmt(com.elementalweapons.growth.Paragon.need(pg.level())) + " XP" : Growth.MAX_LEVEL + "레벨에 도달하면 초월이 열립니다";
		g.pose().pushMatrix();
		g.pose().translate(barX + barW / 2F, barY + 1);
		g.pose().scale(0.75F, 0.75F);
		g.centeredText(font, xt, 0, 0, 0xFFFFFFFF);
		g.pose().popMatrix();
		g.text(font, "초월 포인트", left + 98, top + H - 18, 0xFFFFFFFF, true);
		g.text(font, String.valueOf(pg.points()), left + 98 + font.width("초월 포인트 "), top + H - 18, 0xFFE0B0FF, true);
		g.pose().pushMatrix();
		g.pose().translate(left + 98, top + H - 8);
		g.pose().scale(0.6F, 0.6F);
		g.text(font, "초월 스탯은 상한 없음 · 균열의 열쇠로 무한 균열 도전", 0, 0, 0xFF8A83A8, false);
		g.pose().popMatrix();
	}

	private void renderCores(GuiGraphicsExtractor g) {
		if (minecraft.player == null) return;
		var p = minecraft.player;
		g.text(font, "◆ 핵 강화", px + 6, top + 6, 0xFFFFC870, true);
		small(g, "보스 핵으로 영구 강화 · 최대 " + com.elementalweapons.growth.CoreForge.MAX + "단계", px + 6, top + 16, 0.6F, 0xFF9A93B8);
		for (int i = 0; i < coreBtns.length; i++) {
			int y = top + 26 + i * RH;
			int lv = com.elementalweapons.growth.CoreForge.level(p, i);
			net.minecraft.world.item.Item it = com.elementalweapons.growth.CoreForge.core(i);
			int have = it == null ? 0 : com.elementalweapons.growth.CoreForge.count(p, it);
			int need = com.elementalweapons.growth.CoreForge.cost(lv);
			boolean max = lv >= com.elementalweapons.growth.CoreForge.MAX;
			if (it != null) {
				g.pose().pushMatrix();
				g.pose().translate(px + 3, y + 1.5F);
				g.pose().scale(0.5F, 0.5F);
				g.item(new net.minecraft.world.item.ItemStack(it), 0, 0);
				g.pose().popMatrix();
			}
			int col = com.elementalweapons.growth.CoreForge.COLORS[i];
			small(g, com.elementalweapons.growth.CoreForge.NAMES[i], px + 13, y + 3, 0.62F, lv > 0 ? col : 0xFF8A83A8);
			small(g, max ? "MAX" : "Lv" + lv, px + 52, y + 3, 0.62F, max ? 0xFFFFD34D : 0xFFFFFFFF);
			String st = com.elementalweapons.growth.CoreForge.STAT[i] + " " + com.elementalweapons.growth.CoreForge.fmt(i, com.elementalweapons.growth.CoreForge.value(i, lv));
			small(g, st, px + 67, y + 3, 0.58F, lv > 0 ? 0xFF9FF0FF : 0xFF6A6488);
			if (!max) small(g, have + "/" + need, px + 127, y + 3, 0.58F, have >= need ? 0xFF7CFF8A : 0xFFFF7A7A);
			coreBtns[i].active = !max && have >= need;
			coreBtns[i].visible = !max;
		}
	}

	private void small(GuiGraphicsExtractor g, String t, float x, float y, float sc, int col) {
		g.pose().pushMatrix();
		g.pose().translate(x, y);
		g.pose().scale(sc, sc);
		g.text(font, t, 0, 0, col, false);
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
