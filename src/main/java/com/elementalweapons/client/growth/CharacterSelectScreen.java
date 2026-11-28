package com.elementalweapons.client.growth;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerSkin;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.growth.Characters;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.PlayerClass;

/** 접속할 때 캐릭터 고르기 (메이플스토리처럼 9칸) */
public class CharacterSelectScreen extends Screen {
	private static final EquipmentSlot[] GEAR = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND};
	private static final Identifier WHITE = Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "textures/entity/white_avatar.png");

	private final Characters.Open data;
	private final List<RemotePlayer> avatars = new ArrayList<>();
	private int sel;
	private int cw, ch, gx, gy, gap = 6;
	private long lastClick;
	private int lastSlot = -1;

	public CharacterSelectScreen(Characters.Open data) {
		super(Component.literal("캐릭터 선택"));
		this.data = data;
		this.sel = data.active();
	}

	/** 직업이 없고 처음인 캐릭터: 흰 사람 */
	private static boolean blank(Characters.Info f) { return !f.used() || f.cls() <= 0; }

	@Override
	protected void init() {
		avatars.clear();
		var mc = minecraft;
		for (Characters.Info f : data.infos()) {
			RemotePlayer a;
			if (blank(f)) {
				a = new RemotePlayer(mc.level, mc.player.getGameProfile()) {
					private final PlayerSkin skin = PlayerSkin.insecure(new ClientAsset.ResourceTexture(WHITE, WHITE), null, null, mc.player.getSkin().model());
					@Override public PlayerSkin getSkin() { return skin; }
				};
			} else a = new RemotePlayer(mc.level, mc.player.getGameProfile());
			a.setId(-90000 - avatars.size());
			for (int k = 0; k < GEAR.length && k < f.gear().size(); k++) a.setItemSlot(GEAR[k], f.gear().get(k));
			avatars.add(a);
		}
		// 두 줄 (5 + 4)
		cw = Math.min(78, (width - 24 - gap * 4) / 5);
		ch = Math.min(100, (height - 70 - gap) / 2);
		gx = (width - (cw * 5 + gap * 4)) / 2;
		gy = 34;
		int by = gy + ch * 2 + gap + 8;
		addRenderableWidget(Button.builder(Component.literal("▶ 게임 시작"), b -> start()).bounds(width / 2 - 60, Math.min(by, height - 24), 120, 20).build());
	}

	private int[] cell(int i) {
		int row = i < 5 ? 0 : 1, col = i < 5 ? i : i - 5;
		int x = gx + col * (cw + gap) + (row == 1 ? (cw + gap) / 2 : 0);
		int y = gy + row * (ch + gap);
		return new int[]{x, y};
	}

	private void start() {
		ClientPlayNetworking.send(new Characters.Pick(sel));
		minecraft.gui.setScreen(null);
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent e, boolean dbl) {
		for (int i = 0; i < data.infos().size(); i++) {
			int[] c = cell(i);
			if (e.x() >= c[0] && e.x() < c[0] + cw && e.y() >= c[1] && e.y() < c[1] + ch) {
				long now = System.currentTimeMillis();
				boolean twice = i == lastSlot && now - lastClick < 400;
				sel = i; lastSlot = i; lastClick = now;
				if (twice || dbl) start();
				return true;
			}
		}
		return super.mouseClicked(e, dbl);
	}

	@Override
	public boolean shouldCloseOnEsc() { return false; }

	@Override
	public boolean isPauseScreen() { return false; }

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mx, int my, float pt) {
		g.fillGradient(0, 0, width, height, 0xF0141032, 0xF0080614);
		// 별
		for (int i = 0; i < 70; i++) {
			int x = (int) ((i * 97L + 13) % Math.max(1, width)), y = (int) ((i * 53L + 7) % Math.max(1, height));
			g.fill(x, y, x + 1, y + 1, (i % 3 == 0) ? 0x90FFFFFF : 0x50B0C8FF);
		}
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float pt) {
		super.extractRenderState(g, mx, my, pt);
		g.centeredText(font, "✦ 캐릭터 선택 ✦", width / 2, 10, 0xFFFFD34D);
		g.centeredText(font, "캐릭터를 고르고 '게임 시작' (두 번 누르면 바로 시작) · 다시 접속하면 바꿀 수 있어요", width / 2, 21, 0xFF9A93B8);
		for (int i = 0; i < data.infos().size(); i++) {
			Characters.Info f = data.infos().get(i);
			int[] c = cell(i);
			int x = c[0], y = c[1];
			boolean hover = mx >= x && mx < x + cw && my >= y && my < y + ch;
			boolean on = i == sel;
			int accent = blank(f) ? 0xFFE8E8F0 : PlayerClass.of(f.cls()).color;
			g.fillGradient(x, y, x + cw, y + ch, on ? 0xC0302A60 : 0x90201C40, on ? 0xC0181430 : 0x90100C20);
			g.outline(x, y, cw, ch, on ? 0xFFFFD34D : hover ? 0xFFB0A8E0 : 0xFF4A4070);
			// 받침대
			int px = x + cw / 2;
			int foot = y + ch - 26;
			g.fill(px - cw / 3, foot, px + cw / 3, foot + 3, on ? 0xFFFFD34D : (accent & 0x00FFFFFF) | 0x90000000);
			g.fill(px - cw / 3 + 3, foot + 3, px + cw / 3 - 3, foot + 5, 0x60000000);
			// 아바타
			RemotePlayer a = avatars.get(i);
			int size = Math.max(14, (int) (ch * 0.3F));
			InventoryScreen.extractEntityInInventoryFollowsMouse(g, x + 2, y + 4, x + cw - 2, foot + 2, size, 0.0625F, on ? mx : px, on ? my : y + ch / 2, a);
			// 글자
			int ty = foot + 7;
			if (!f.used()) {
				g.centeredText(font, "새 캐릭터", px, ty, 0xFFD8D8E8);
				g.centeredText(font, "슬롯 " + (i + 1), px, ty + 9, 0xFF7A7398);
			} else {
				g.centeredText(font, "Lv." + f.level(), px, ty, 0xFFFFFFFF);
				String job = f.cls() <= 0 ? "직업 없음" : (f.job().isEmpty() ? PlayerClass.of(f.cls()).koName : f.job());
				String el = f.elem() >= 0 && f.elem() < Element.values().length ? " · " + Element.values()[f.elem()].koName : "";
				int ec = f.elem() >= 0 && f.elem() < Element.values().length ? (0xFF000000 | Element.values()[f.elem()].color) : accent;
				small(g, job + el, px, ty + 9, ec, cw - 4);
			}
			if (i == data.active()) small(g, "▼ 마지막 플레이", px, y + 3, 0xFF7CFF8A, cw - 4);
		}
	}

	private void small(GuiGraphicsExtractor g, String t, int cx, int y, int col, int maxW) {
		float sc = Math.min(1F, maxW / (float) Math.max(1, font.width(t)));
		sc = Math.max(0.55F, Math.min(0.8F, sc));
		g.pose().pushMatrix();
		g.pose().translate(cx, y);
		g.pose().scale(sc, sc);
		g.centeredText(font, t, 0, 0, col);
		g.pose().popMatrix();
	}
}
