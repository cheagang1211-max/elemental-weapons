package com.elementalweapons.client.growth;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.elementalweapons.growth.GrowthPayloads;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.ModItems;
import com.elementalweapons.item.PlayerClass;
import com.elementalweapons.item.WeaponType;

/** 직업 선택 화면 */
public class ClassSelectScreen extends Screen {
	private static final PlayerClass[] CLASSES = {PlayerClass.MAGE, PlayerClass.WARRIOR, PlayerClass.KNIGHT, PlayerClass.ASSASSIN};
	private static final String[][] DESC = {
			{"원소 마법으로 멀리서 공격", "지팡이 스킬: 속성 대마법"},
			{"무거운 무기로 강하게 공격", "대검: 주변 6방향 강타", "망치: 땅 내리치기 충격파"},
			{"검과 방패로 공격과 방어", "검: 원거리 속성 일격", "방패: 막으면 속성 반격"},
			{"빠른 단검으로 기습", "단검: 순간 돌진"}};
	private static final WeaponType[][] WEAPONS = {{WeaponType.STAFF}, {WeaponType.GREATSWORD, WeaponType.HAMMER}, {WeaponType.SWORD, WeaponType.SHIELD}, {WeaponType.DAGGER}};
	private static final int CW = 96, CH = 150, GAP = 8;
	private int left, top;

	public ClassSelectScreen() { super(Component.literal("직업 선택")); }

	@Override
	protected void init() {
		left = (width - (CW * 4 + GAP * 3)) / 2;
		top = (height - CH) / 2 + 8;
		PlayerClass cur = GrowthClient.current().playerClass();
		for (int i = 0; i < 4; i++) {
			PlayerClass pc = CLASSES[i];
			String label = cur == pc ? "현재 직업" : (cur == PlayerClass.NONE ? "선택" : "변경 (정수 3)");
			Button b = Button.builder(Component.literal(label), btn -> {
				ClientPlayNetworking.send(new GrowthPayloads.ChooseClass(pc.ordinal()));
				onClose();
			}).bounds(left + i * (CW + GAP) + 8, top + CH - 24, CW - 16, 18).build();
			b.active = cur != pc;
			addRenderableWidget(b);
		}
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mx, int my, float pt) {
		super.extractBackground(g, mx, my, pt);
		for (int i = 0; i < 4; i++) {
			int x = left + i * (CW + GAP);
			boolean hover = mx >= x && mx < x + CW && my >= top && my < top + CH;
			g.fillGradient(x, top, x + CW, top + CH, 0xF0151A33, 0xF0160E28);
			g.outline(x, top, CW, CH, hover ? 0xFFFFFFFF : CLASSES[i].color);
			g.fillGradient(x + 1, top + 1, x + CW - 1, top + 30, (CLASSES[i].color & 0x00FFFFFF) | 0x50000000, 0x00000000);
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float pt) {
		super.extractRenderState(g, mx, my, pt);
		g.pose().pushMatrix();
		g.pose().translate(width / 2F, top - 30);
		g.pose().scale(2F, 2F);
		g.centeredText(font, "직업 선택", 0, 0, 0xFFFFD34D);
		g.pose().popMatrix();
		g.centeredText(font, "직업마다 쓸 수 있는 무기가 정해져 있어요", width / 2, top - 10, 0xFFC9C2E6);
		for (int i = 0; i < 4; i++) {
			int x = left + i * (CW + GAP);
			PlayerClass pc = CLASSES[i];
			g.pose().pushMatrix();
			g.pose().translate(x + CW / 2F, top + 8);
			g.pose().scale(1.6F, 1.6F);
			g.centeredText(font, pc.koName, 0, 0, pc.color);
			g.pose().popMatrix();
			// 무기 아이콘
			WeaponType[] ws = WEAPONS[i];
			int iconW = ws.length * 36;
			for (int k = 0; k < ws.length; k++) {
				ItemStack st = new ItemStack(ModItems.WEAPONS.get(Element.values()[(i * 2 + k) % 7]).get(ws[k]));
				g.pose().pushMatrix();
				g.pose().translate(x + CW / 2F - iconW / 2F + k * 36 + 2, top + 32);
				g.pose().scale(2F, 2F);
				g.item(st, 0, 0);
				g.pose().popMatrix();
			}
			g.centeredText(font, pc.weapons, x + CW / 2, top + 70, 0xFFFFFFFF);
			String[] d = DESC[i];
			for (int k = 0; k < d.length; k++) {
				g.pose().pushMatrix();
				g.pose().translate(x + CW / 2F, top + 84 + k * 10);
				g.pose().scale(0.7F, 0.7F);
				g.centeredText(font, d[k], 0, 0, k == 0 ? 0xFFE9DDFF : 0xFFAFA8CC);
				g.pose().popMatrix();
			}
		}
	}

	@Override
	public boolean isPauseScreen() { return false; }
}
