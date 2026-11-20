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
	private static final PlayerClass[] CLASSES = {PlayerClass.MAGE, PlayerClass.WARRIOR, PlayerClass.KNIGHT, PlayerClass.ASSASSIN, PlayerClass.SUPPORT, PlayerClass.REAPER, PlayerClass.RANGER};
	private static final String[][] DESC = {
			{"원소 마법으로 멀리서 공격", "지팡이 스킬: 속성 대마법"},
			{"무거운 무기로 강하게 공격", "대검: 주변 6방향 강타", "망치: 땅 내리치기 충격파"},
			{"검과 방패로 공격과 방어", "검: 원거리 속성 일격", "방패: 막으면 속성 반격"},
			{"빠른 단검으로 기습", "단검: 순간 돌진"},
			{"아군을 돕는 지원 마법", "마법서: 주변 아군 강화·회복", "어둠: 적 약화"},
			{"낫으로 넓게 베어 가르기", "낫: 회전 베기", "적을 끌어당긴 뒤 폭발"},
			{"멀리서 속성 화살 사격", "활: 당길수록 강해짐", "끝까지 당기면 5연발"}};
	private static final WeaponType[][] WEAPONS = {{WeaponType.STAFF}, {WeaponType.GREATSWORD, WeaponType.HAMMER}, {WeaponType.SWORD, WeaponType.SHIELD}, {WeaponType.DAGGER}, {WeaponType.BOOK}, {WeaponType.SCYTHE}, {WeaponType.BOW}};
	private static final int CW = 56, CH = 150, GAP = 3;
	private int left, top;

	public ClassSelectScreen() { super(Component.literal("직업 선택")); }

	@Override
	protected void init() {
		left = (width - (CW * CLASSES.length + GAP * (CLASSES.length - 1))) / 2;
		top = (height - CH) / 2 + 8;
		PlayerClass cur = GrowthClient.current().playerClass();
		for (int i = 0; i < CLASSES.length; i++) {
			PlayerClass pc = CLASSES[i];
			String label = cur == pc ? "현재 직업" : (cur == PlayerClass.NONE ? "선택" : "잠김");
			Button b = Button.builder(Component.literal(label), btn -> {
				ClientPlayNetworking.send(new GrowthPayloads.ChooseClass(pc.ordinal()));
				onClose();
			}).bounds(left + i * (CW + GAP) + 4, top + CH - 24, CW - 8, 18).build();
			b.active = cur == PlayerClass.NONE;
			addRenderableWidget(b);
		}
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mx, int my, float pt) {
		super.extractBackground(g, mx, my, pt);
		for (int i = 0; i < CLASSES.length; i++) {
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
		g.centeredText(font, "한 번 고르면 운명의 현자에게 가야만 바꿀 수 있어요", width / 2, top + CH + 6, 0xFFD080FF);
		for (int i = 0; i < CLASSES.length; i++) {
			int x = left + i * (CW + GAP);
			PlayerClass pc = CLASSES[i];
			g.pose().pushMatrix();
			g.pose().translate(x + CW / 2F, top + 8);
			g.pose().scale(1.3F, 1.3F);
			g.centeredText(font, pc.koName, 0, 0, pc.color);
			g.pose().popMatrix();
			// 무기 아이콘
			WeaponType[] ws = WEAPONS[i];
			int iconW = ws.length * 26;
			for (int k = 0; k < ws.length; k++) {
				ItemStack st = new ItemStack(ModItems.WEAPONS.get(Element.values()[(i * 2 + k) % 7]).get(ws[k]));
				g.pose().pushMatrix();
				g.pose().translate(x + CW / 2F - iconW / 2F + k * 26 + 1, top + 34);
				g.pose().scale(1.5F, 1.5F);
				g.item(st, 0, 0);
				g.pose().popMatrix();
			}
			g.centeredText(font, pc.weapons, x + CW / 2, top + 70, 0xFFFFFFFF);
			String[] d = DESC[i];
			for (int k = 0; k < d.length; k++) {
				g.pose().pushMatrix();
				g.pose().translate(x + CW / 2F, top + 84 + k * 10);
				g.pose().scale(0.5F, 0.5F);
				g.centeredText(font, d[k], 0, 0, k == 0 ? 0xFFE9DDFF : 0xFFAFA8CC);
				g.pose().popMatrix();
			}
		}
	}

	@Override
	public boolean isPauseScreen() { return false; }
}
