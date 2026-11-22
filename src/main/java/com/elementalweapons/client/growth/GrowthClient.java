package com.elementalweapons.client.growth;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.growth.Growth;
import com.elementalweapons.growth.ModGrowth;

/** 성장 시스템 클라이언트: K키, HUD, 레벨업 연출 상태 */
public final class GrowthClient {
	private GrowthClient() {}

	public static KeyMapping OPEN_KEY, HOME_KEY;
	static int lastLevel = -1;
	static long lastXp = -1;
	static int levelUpTicks = 0;
	static int levelUpFrom = 0, levelUpTo = 0;
	static final List<int[]> popups = new ArrayList<>(); // {amount, age}
	static int tick = 0;
	static boolean prompted = false;
	static int lastCls = -1;

	public static void initialize() {
		OPEN_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.elementalweapons.growth", InputConstants.KEY_K, KeyMapping.Category.GAMEPLAY));
		HOME_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.elementalweapons.home", InputConstants.KEY_B, KeyMapping.Category.GAMEPLAY));
		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "growth_hud"), GrowthHud::render);
		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "tutorial_hud"), TutorialHud::render);
		ClientTickEvents.END_CLIENT_TICK.register(GrowthClient::tick);
		ClientPlayNetworking.registerGlobalReceiver(com.elementalweapons.growth.GrowthPayloads.OpenSage.TYPE,
				(payload, ctx) -> Minecraft.getInstance().gui.setScreen(new SageScreen()));
	}

	public static Growth current() {
		Minecraft mc = Minecraft.getInstance();
		return mc.player == null ? Growth.DEFAULT : ModGrowth.get(mc.player);
	}

	private static void tick(Minecraft mc) {
		tick++;
		if (mc.player == null) { lastLevel = -1; return; }
		while (HOME_KEY.consumeClick()) ClientPlayNetworking.send(com.elementalweapons.growth.GrowthPayloads.GoHome.INSTANCE);
		while (OPEN_KEY.consumeClick()) {
			if (mc.gui.screen() == null) mc.gui.setScreen(new GrowthScreen());
		}
		Growth g = current();
		lastCls = g.cls();
		if (lastLevel >= 0) {
			if (g.level() > lastLevel) {
				levelUpTicks = 70; levelUpFrom = lastLevel; levelUpTo = g.level();
			}
			long gain = g.level() > lastLevel ? -1 : g.xp() - lastXp;
			if (gain > 0) popups.add(new int[]{(int) gain, 0});
		}
		lastLevel = g.level(); lastXp = g.xp();
		if (levelUpTicks > 0) levelUpTicks--;
		popups.removeIf(p -> ++p[1] > 40);
	}

	/** 스크린샷 테스트용: 레벨업 연출 강제 */
	public static void debugLevelUp(int from, int to) { levelUpTicks = 70; levelUpFrom = from; levelUpTo = to; }
}
