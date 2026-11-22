package com.elementalweapons.client.skill;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.ModItems;
import com.elementalweapons.item.PlayerClass;
import com.elementalweapons.item.WeaponType;
import com.elementalweapons.skill.SkillNet;

/** 10레벨 전직: 1단계 직업 고르기 → 2단계 속성 고르기 */
public class EvolveScreen extends Screen {
	private static final String[] DESC = {
			"번개 연쇄 · 빠른 마비", "불꽃 폭발 · 지속 화상", "물결 · 밀쳐내기와 둔화", "꽃잎 · 지속 피해와 치유",
			"돌풍 · 띄우기와 이동", "그림자 · 약화와 흡수", "성스러운 빛 · 기둥과 정화"};
	private static final String[] CLASS_DESC = {"",
			"원거리 마법 · 강한 한 방", "넓게 베기 · 높은 체력", "찌르기 · 방패로 막기", "돌진 · 빠른 연타",
			"치유 · 아군 지원", "주변 베기 · 흡수", "멀리서 화살 · 집중 사격"};
	private static final int CW = 56, CH = 128, GAP = 3;
	private int left, top;
	/** 0이면 직업 고르는 단계 */
	private int cls = 0;

	public EvolveScreen() { super(Component.literal("전직")); }

	private static WeaponType mainType(PlayerClass c) {
		for (WeaponType t : WeaponType.values()) if (t.playerClass() == c && t != WeaponType.SHIELD) return t;
		return WeaponType.SWORD;
	}

	private int count() { return cls == 0 ? PlayerClass.values().length - 1 : Element.values().length; }

	@Override
	protected void init() {
		int n = count();
		left = (width - (CW * n + GAP * (n - 1))) / 2;
		top = (height - CH) / 2 + 8;
		for (int i = 0; i < n; i++) {
			final int idx = i;
			addRenderableWidget(Button.builder(Component.literal("선택"), b -> pick(idx))
					.bounds(left + i * (CW + GAP) + 4, top + CH - 22, CW - 8, 16).build());
		}
		if (cls != 0) addRenderableWidget(Button.builder(Component.literal("← 직업 다시 고르기"), b -> { cls = 0; clearWidgets(); init(); })
				.bounds(width / 2 - 60, top + CH + 6, 120, 18).build());
	}

	private void pick(int i) {
		if (cls == 0) { cls = i + 1; clearWidgets(); init(); return; }
		ClientPlayNetworking.send(new SkillNet.ChooseElement(cls, Element.values()[i].ordinal()));
		onClose();
	}

	/** 사진 테스트용 */
	public void pickForTest(int i) { pick(i); }

	private int color(int i) {
		return cls == 0 ? (0xFF000000 | PlayerClass.of(i + 1).color) : (0xFF000000 | ElementFx.color(Element.values()[i]));
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mx, int my, float pt) {
		super.extractBackground(g, mx, my, pt);
		for (int i = 0; i < count(); i++) {
			int x = left + i * (CW + GAP), c = color(i);
			boolean hover = mx >= x && mx < x + CW && my >= top && my < top + CH;
			g.fillGradient(x, top, x + CW, top + CH, 0xF0151A33, 0xF0160E28);
			g.fillGradient(x + 1, top + 1, x + CW - 1, top + 40, (c & 0x00FFFFFF) | 0x60000000, 0x00000000);
			g.outline(x, top, CW, CH, hover ? 0xFFFFFFFF : c);
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float pt) {
		super.extractRenderState(g, mx, my, pt);
		g.pose().pushMatrix();
		g.pose().translate(width / 2F, top - 32);
		g.pose().scale(2F, 2F);
		g.centeredText(font, cls == 0 ? "전직 1/2 · 직업" : "전직 2/2 · 속성", 0, 0, 0xFFFFD34D);
		g.pose().popMatrix();
		g.centeredText(font, cls == 0 ? "앞으로 쓸 직업을 고르세요 · 고른 직업의 무기만 쓸 수 있게 돼요"
				: PlayerClass.of(cls).koName + "의 속성을 고르세요 · 나무 무기가 사라지고 속성 무기를 받습니다 (운명의 현자에게서만 다시 고를 수 있어요)",
				width / 2, top - 12, 0xFFC9C2E6);
		for (int i = 0; i < count(); i++) {
			int x = left + i * (CW + GAP);
			String name; ItemStack icon; String desc;
			if (cls == 0) {
				PlayerClass c = PlayerClass.of(i + 1);
				name = c.koName; icon = new ItemStack(ModItems.get(Element.values()[i % Element.values().length], mainType(c))); desc = CLASS_DESC[i + 1] + " · " + c.weapons;
			} else {
				Element e = Element.values()[i];
				name = e.koName; icon = new ItemStack(ModItems.get(e, mainType(PlayerClass.of(cls)))); desc = DESC[e.ordinal()];
			}
			g.pose().pushMatrix();
			g.pose().translate(x + CW / 2F, top + 6);
			g.pose().scale(1.3F, 1.3F);
			g.centeredText(font, name, 0, 0, color(i));
			g.pose().popMatrix();
			g.pose().pushMatrix();
			g.pose().translate(x + CW / 2F - 16, top + 24);
			g.pose().scale(2F, 2F);
			g.item(icon, 0, 0);
			g.pose().popMatrix();
			g.pose().pushMatrix();
			g.pose().translate(x + CW / 2F, top + 66);
			g.pose().scale(0.5F, 0.5F);
			String[] parts = desc.split(" · ");
			for (int k = 0; k < parts.length; k++) g.centeredText(font, parts[k], 0, k * 11, 0xFFE9DDFF);
			g.pose().popMatrix();
		}
	}

	@Override
	public boolean isPauseScreen() { return false; }
}
