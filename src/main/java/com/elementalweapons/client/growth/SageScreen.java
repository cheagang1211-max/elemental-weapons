package com.elementalweapons.client.growth;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import com.elementalweapons.growth.Growth;
import com.elementalweapons.growth.GrowthPayloads;

/** 운명의 현자 대화 창: 직업·스탯 초기화 */
public class SageScreen extends Screen {
	private static final int W = 240, H = 150;
	private int left, top;

	public SageScreen() { super(Component.literal("운명의 현자")); }

	@Override
	protected void init() {
		left = (width - W) / 2; top = (height - H) / 2;
		addRenderableWidget(Button.builder(Component.literal("운명 초기화 (원소 정수 3)"), b -> {
			ClientPlayNetworking.send(GrowthPayloads.ResetClass.INSTANCE);
			onClose();
		}).bounds(left + 12, top + H - 28, 140, 18).build());
		addRenderableWidget(Button.builder(Component.literal("그만두기"), b -> onClose()).bounds(left + W - 82, top + H - 28, 70, 18).build());
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mx, int my, float pt) {
		super.extractBackground(g, mx, my, pt);
		g.fillGradient(left, top, left + W, top + H, 0xF51A1030, 0xF50E0A1E);
		g.outline(left, top, W, H, 0xFFD080FF);
		g.fillGradient(left + 1, top + 1, left + W - 1, top + 26, 0x50D080FF, 0x00D080FF);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float pt) {
		super.extractRenderState(g, mx, my, pt);
		Growth gr = GrowthClient.current();
		g.pose().pushMatrix();
		g.pose().translate(left + W / 2F, top + 8);
		g.pose().scale(1.4F, 1.4F);
		g.centeredText(font, "운명의 현자", 0, 0, 0xFFE8C0FF);
		g.pose().popMatrix();
		String[] lines = {
				"\"그대의 길을 되돌리고 싶은가?\"",
				"",
				"현재 직업: " + gr.playerClass().koName + "   찍은 스탯: " + (gr.str() + gr.mag() + gr.vit() + gr.agi()),
				"초기화하면 직업이 사라지고 스탯 포인트를 모두 돌려받아요.",
				"레벨과 경험치는 그대로 남아요.",
				"새 직업을 고르고 스탯을 처음부터 다시 찍을 수 있어요."};
		for (int i = 0; i < lines.length; i++) {
			g.pose().pushMatrix();
			g.pose().translate(left + W / 2F, top + 34 + i * 12);
			g.pose().scale(0.85F, 0.85F);
			g.centeredText(font, lines[i], 0, 0, i == 0 ? 0xFFFFD34D : 0xFFD8D2F0);
			g.pose().popMatrix();
		}
	}

	@Override
	public boolean isPauseScreen() { return false; }
}
