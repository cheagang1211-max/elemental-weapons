package com.elementalweapons.client.growth;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.item.ElementalWeaponItem;
import com.elementalweapons.item.Enhancement;
import com.elementalweapons.item.ModItems;

/** 7층 강화의 제단: 오른손 무기 강화 창 */
public class EnhanceScreen extends Screen {
	private static final int W = 300, H = 236;
	private int left, top;
	private boolean protect;
	private Button go, guard;
	static int flash, flashKind, flashLevel;

	public EnhanceScreen() { super(Component.literal("강화의 제단")); }

	public static void result(int kind, int level) { flash = 40; flashKind = kind; flashLevel = level; }

	@Override
	protected void init() {
		left = (width - W) / 2; top = (height - H) / 2;
		go = addRenderableWidget(Button.builder(Component.literal("⚒ 강화하기"), b -> ClientPlayNetworking.send(new Enhancement.Try(protect)))
				.bounds(left + W - 112, top + H - 28, 100, 20).build());
		guard = addRenderableWidget(Button.builder(Component.literal(""), b -> protect = !protect)
				.bounds(left + 12, top + H - 28, 170, 20).build());
		addRenderableWidget(Button.builder(Component.literal("닫기"), b -> onClose()).bounds(left + W - 44, top + 6, 38, 14).build());
	}

	@Override
	public void tick() {
		if (flash > 0) flash--;
		var p = minecraft.player;
		if (p == null) return;
		int have = Enhancement.count(p.getInventory(), ModItems.BLESS_STONE);
		if (have == 0) protect = false;
		guard.setMessage(Component.literal((protect ? "☑" : "☐") + " 천상의 가호석 사용 (보유 " + have + ")"));
		guard.active = have > 0;
		go.active = Enhancement.problem(p, protect) == null;
	}

	@Override public boolean isPauseScreen() { return false; }

	private int color(ItemStack w) {
		return w.getItem() instanceof ElementalWeaponItem ew ? 0xFF000000 | ElementFx.color(ew.element()) : 0xFFFFD34D;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mx, int my, float pt) {
		super.extractBackground(g, mx, my, pt);
		var p = minecraft.player;
		if (p == null) return;
		ItemStack w = p.getMainHandItem();
		int c = color(w);
		g.fillGradient(left, top, left + W, top + H, 0xF0201A10, 0xF0120E1E);
		g.outline(left, top, W, H, c);
		g.outline(left + 2, top + 2, W - 4, H - 4, 0x40FFD34D);
		g.pose().pushMatrix();
		g.pose().translate(left + 10, top + 8);
		g.pose().scale(1.4F, 1.4F);
		g.text(font, "⚒ 강화의 제단", 0, 0, 0xFFFFD34D, true);
		g.pose().popMatrix();
		if (!(w.getItem() instanceof ElementalWeaponItem ew)) {
			g.centeredText(font, "오른손에 원소 무기를 들고 오세요", left + W / 2, top + 90, 0xFFE0D8F0);
			g.centeredText(font, "(나무 훈련용 무기는 강화할 수 없습니다)", left + W / 2, top + 104, 0xFF8A8AA0);
			return;
		}
		int lv = Enhancement.level(w), n = lv + 1;
		// 무기 칸
		int bx = left + 14, by = top + 30;
		g.fill(bx, by, bx + 40, by + 40, 0xFF0E0C16);
		g.outline(bx, by, 40, 40, c);
		g.pose().pushMatrix();
		g.pose().translate(bx + 4, by + 4);
		g.pose().scale(2F, 2F);
		g.item(w, 0, 0);
		g.pose().popMatrix();
		g.text(font, w.getHoverName(), bx + 48, by + 2, 0xFFFFFFFF, true);
		if (lv >= Enhancement.MAX) {
			g.text(font, "최대 강화 +" + lv + " 달성!", bx + 48, by + 16, 0xFFFF9AFF, false);
			stats(g, lv, lv, bx + 48, by + 30);
			return;
		}
		g.pose().pushMatrix();
		g.pose().translate(bx + 48, by + 14);
		g.pose().scale(1.5F, 1.5F);
		g.text(font, "+" + lv + "  →  +" + n, 0, 0, tier(n), true);
		g.pose().popMatrix();
		stats(g, lv, n, left + 14, top + 78);
		// 확률
		int ch = Enhancement.chance(w), fb = Math.min(30, Enhancement.fails(w) * 5);
		int cc = ch >= 80 ? 0xFF7CFF7A : ch >= 40 ? 0xFFFFE07A : 0xFFFF7A6A;
		g.text(font, "성공 확률 " + ch + "%" + (fb > 0 ? "  (실패 보정 +" + fb + "%)" : ""), left + 160, top + 78, cc, true);
		String fail = !Enhancement.dropsOnFail(n) ? "실패해도 단계는 그대로" : protect ? "실패해도 가호석이 지켜 줌" : "실패하면 +" + (lv - 1) + "로 내려감";
		g.text(font, fail, left + 160, top + 90, Enhancement.dropsOnFail(n) && !protect ? 0xFFFF9A8A : 0xFFB0B0C8, false);
		// 재료
		Enhancement.Cost cost = Enhancement.cost(n);
		int y = top + 134;
		g.text(font, "필요한 재료", left + 14, y - 12, 0xFFFFD34D, false);
		int col = 0;
		col = mat(g, ModItems.ENHANCE_STONE, cost.stones(), col, y);
		col = mat(g, Enhancement.crystalFor(ew.element()), cost.crystals(), col, y);
		col = mat(g, Enhancement.dust(), cost.dust(), col, y);
		if (cost.essence() > 0) col = mat(g, ModItems.ELEMENT_ESSENCE, cost.essence(), col, y);
		String why = Enhancement.problem(p, protect);
		if (why != null) g.centeredText(font, why, left + W / 2, top + H - 44, 0xFFFF8A7A);
		// 결과 번쩍
		if (flash > 0) {
			float a = Math.min(1F, flash / 15F);
			int fc = flashKind == 1 ? 0xFFE27A : flashKind == 2 ? 0xFF4A3A : 0xB0B0C0;
			g.fill(left + 3, top + 3, left + W - 3, top + H - 3, ((int) (a * 60) << 24) | fc);
			g.pose().pushMatrix();
			g.pose().translate(left + W / 2F, top + H / 2F - 10);
			g.pose().scale(2.4F, 2.4F);
			String t = flashKind == 1 ? "성공! +" + flashLevel : flashKind == 2 ? "실패… +" + flashLevel : flashKind == 3 ? "실패 · 가호석이 지킴" : "실패";
			g.centeredText(font, t, 0, 0, ((int) (a * 255) << 24) | fc);
			g.pose().popMatrix();
		}
	}

	private static int tier(int n) { return n >= 15 ? 0xFFFF9AFF : n >= 10 ? 0xFFFFC84A : n >= 5 ? 0xFF7AE8FF : 0xFFFFFFFF; }

	private void stats(GuiGraphicsExtractor g, int a, int b, int x, int y) {
		g.text(font, String.format("공격력    +%.1f → +%.1f", Enhancement.attackBonus(a), Enhancement.attackBonus(b)), x, y, 0xFFE0D8F0, false);
		g.text(font, String.format("기술 피해 +%d%% → +%d%%", a * 5, b * 5), x, y + 12, 0xFFE0D8F0, false);
		g.text(font, String.format("보스 피해 x%.1f → x%.1f", 1 + 0.4 * a, 1 + 0.4 * b), x, y + 24, 0xFFE0D8F0, false);
	}

	private int mat(GuiGraphicsExtractor g, Item item, int need, int col, int y) {
		var p = minecraft.player;
		int have = Enhancement.count(p.getInventory(), item);
		int x = left + 14 + (col % 2) * 140, yy = y + (col / 2) * 22;
		g.fill(x, yy, x + 20, yy + 20, 0xFF0E0C16);
		g.item(new ItemStack(item), x + 2, yy + 2);
		g.text(font, new ItemStack(item).getHoverName(), x + 24, yy + 2, 0xFFE0D8F0, false);
		g.text(font, have + " / " + need, x + 24, yy + 11, have >= need ? 0xFF7CFF7A : 0xFFFF7A6A, false);
		return col + 1;
	}
}
