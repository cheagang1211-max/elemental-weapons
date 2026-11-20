package com.elementalweapons.client.skill;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.client.growth.GrowthClient;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.ModItems;
import com.elementalweapons.item.PlayerClass;
import com.elementalweapons.item.WeaponType;
import com.elementalweapons.skill.SkillNet;

/** 10레벨 속성 진화: 7속성 중 하나 고르기 */
public class EvolveScreen extends Screen {
	private static final String[] DESC = {
			"번개 연쇄 · 빠른 마비", "불꽃 폭발 · 지속 화상", "물결 · 밀쳐내기와 둔화", "꽃잎 · 지속 피해와 치유",
			"돌풍 · 띄우기와 이동", "그림자 · 약화와 흡수", "성스러운 빛 · 기둥과 정화"};
	private static final int CW = 56, CH = 128, GAP = 3;
	private int left, top;

	public EvolveScreen() { super(Component.literal("속성 진화")); }

	private Element[] order() { return Element.values(); }

	@Override
	protected void init() {
		Element[] els = order();
		left = (width - (CW * els.length + GAP * (els.length - 1))) / 2;
		top = (height - CH) / 2 + 8;
		for (int i = 0; i < els.length; i++) {
			final int idx = els[i].ordinal();
			addRenderableWidget(Button.builder(Component.literal("선택"), b -> { ClientPlayNetworking.send(new SkillNet.ChooseElement(idx)); onClose(); })
					.bounds(left + i * (CW + GAP) + 4, top + CH - 22, CW - 8, 16).build());
		}
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mx, int my, float pt) {
		super.extractBackground(g, mx, my, pt);
		Element[] els = order();
		for (int i = 0; i < els.length; i++) {
			int x = left + i * (CW + GAP), c = 0xFF000000 | ElementFx.color(els[i]);
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
		g.centeredText(font, "속성 진화", 0, 0, 0xFFFFD34D);
		g.pose().popMatrix();
		g.centeredText(font, "나무 무기가 사라지고 고른 속성의 무기를 받습니다 · 운명의 현자에게서만 다시 고를 수 있어요", width / 2, top - 12, 0xFFC9C2E6);
		PlayerClass c = GrowthClient.current().playerClass();
		WeaponType wt = WeaponType.SWORD;
		for (WeaponType t : WeaponType.values()) if (t.playerClass() == c && t != WeaponType.SHIELD) { wt = t; break; }
		Element[] els = order();
		for (int i = 0; i < els.length; i++) {
			int x = left + i * (CW + GAP);
			g.pose().pushMatrix();
			g.pose().translate(x + CW / 2F, top + 6);
			g.pose().scale(1.3F, 1.3F);
			g.centeredText(font, els[i].koName, 0, 0, 0xFF000000 | ElementFx.color(els[i]));
			g.pose().popMatrix();
			g.pose().pushMatrix();
			g.pose().translate(x + CW / 2F - 16, top + 24);
			g.pose().scale(2F, 2F);
			g.item(new ItemStack(ModItems.get(els[i], wt)), 0, 0);
			g.pose().popMatrix();
			g.pose().pushMatrix();
			g.pose().translate(x + CW / 2F, top + 66);
			g.pose().scale(0.5F, 0.5F);
			String[] parts = DESC[els[i].ordinal()].split(" · ");
			for (int k = 0; k < parts.length; k++) g.centeredText(font, parts[k], 0, k * 11, 0xFFE9DDFF);
			g.pose().popMatrix();
		}
	}

	@Override
	public boolean isPauseScreen() { return false; }
}
