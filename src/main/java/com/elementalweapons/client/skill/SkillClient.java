package com.elementalweapons.client.skill;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.item.ElementalWeaponItem;
import com.elementalweapons.item.TrainingWeaponItem;
import com.elementalweapons.skill.SkillInfo;
import com.elementalweapons.skill.SkillNet;

/** Z·X·C 스킬 키 + 화면 오른쪽 아래 스킬바 */
public final class SkillClient {
	private SkillClient() {}

	private static final KeyMapping[] KEYS = new KeyMapping[3];
	private static final String[] LABEL = {"Z", "X", "C"};
	private static final long[] READY = new long[3];
	private static final int[] TOTAL = new int[3];
	private static long clock;

	public static void initialize() {
		// 좌클릭 콤보: 직업 무기를 들고 공격 키를 누를 때마다 서버에 알림
		net.fabricmc.fabric.api.event.client.player.ClientPreAttackCallback.EVENT.register((client, player, clicks) -> {
			if (clicks > 0 && client.screen == null && !player.isSpectator()) {
				var it = player.getMainHandItem().getItem();
				if (it instanceof com.elementalweapons.item.ElementalWeaponItem || it instanceof com.elementalweapons.item.TrainingWeaponItem)
					net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new com.elementalweapons.skill.SkillNet.Swing());
			}
			return false;
		});
		int[] codes = {InputConstants.KEY_Z, InputConstants.KEY_X, InputConstants.KEY_C};
		for (int i = 0; i < 3; i++)
			KEYS[i] = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.elementalweapons.skill" + (i + 1), codes[i], KeyMapping.Category.GAMEPLAY));
		ClientTickEvents.END_CLIENT_TICK.register(mc -> {
			clock++;
			if (mc.player == null) return;
			for (int i = 0; i < 3; i++) while (KEYS[i].consumeClick()) if (mc.gui.screen() == null) ClientPlayNetworking.send(new SkillNet.Cast(i));
		});
		ClientPlayNetworking.registerGlobalReceiver(SkillNet.Cooldown.TYPE, (p, ctx) -> { READY[p.slot()] = clock + p.ticks(); TOTAL[p.slot()] = Math.max(1, p.ticks()); });
		ClientPlayNetworking.registerGlobalReceiver(SkillNet.OpenEvolve.TYPE, (p, ctx) -> Minecraft.getInstance().gui.setScreen(new EvolveScreen()));
		HudElementRegistry.addLast(ElementalWeaponsMod.id("skill_bar"), SkillClient::render);
	}

	/** 스크린샷 테스트용 */
	public static void press(int slot) { ClientPlayNetworking.send(new SkillNet.Cast(slot)); }

	private static void render(GuiGraphicsExtractor g, DeltaTracker dt) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;
		ItemStack held = mc.player.getMainHandItem();
		SkillInfo.Info[] infos; int color;
		if (held.getItem() instanceof ElementalWeaponItem w) { infos = SkillInfo.of(w.type()); color = 0xFF000000 | ElementFx.color(w.element()); }
		else if (held.getItem() instanceof TrainingWeaponItem) { infos = SkillInfo.WOOD; color = 0xFFD8B27A; }
		else return;
		Font font = mc.font;
		int size = 22, gap = 4;
		int x0 = g.guiWidth() / 2 + 96, y0 = g.guiHeight() - size - 4;
		for (int i = 0; i < 3; i++) {
			int x = x0 + i * (size + gap);
			boolean locked = infos[i].cooldown() == 0;
			g.fillGradient(x, y0, x + size, y0 + size, 0xE0202438, 0xE0141626);
			g.outline(x, y0, size, size, locked ? 0xFF505468 : color);
			long left = READY[i] - clock;
			if (left > 0) {
				int h = (int) Math.ceil(size * left / (double) TOTAL[i]);
				g.fill(x + 1, y0 + size - h, x + size - 1, y0 + size - 1, 0xA0000000);
				String sec = left >= 20 ? String.valueOf((left + 19) / 20) : String.format("%.1f", left / 20.0);
				g.centeredText(font, sec, x + size / 2, y0 + 7, 0xFFFFFFFF);
			} else {
				g.centeredText(font, LABEL[i], x + size / 2, y0 + 7, locked ? 0xFF6A6E80 : 0xFFFFFFFF);
			}
			g.pose().pushMatrix();
			g.pose().translate(x + size / 2F, y0 - 7);
			g.pose().scale(0.5F, 0.5F);
			g.centeredText(font, infos[i].name(), 0, 0, locked ? 0xFF8A8EA0 : 0xFFE9DDFF);
			g.pose().popMatrix();
		}
	}
}
