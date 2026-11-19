package com.elementalweapons.client.growth;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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

	public static KeyMapping OPEN_KEY;
	static int lastLevel = -1;
	static long lastXp = -1;
	static int levelUpTicks = 0;
	static int levelUpFrom = 0, levelUpTo = 0;
	static final List<int[]> popups = new ArrayList<>(); // {amount, age}
	static int tick = 0;

	public static void initialize() {
		OPEN_KEY = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.elementalweapons.growth", GLFW.GLFW_KEY_K, KeyMapping.Category.GAMEPLAY));
		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "growth_hud"), GrowthHud::render);
		ClientTickEvents.END_CLIENT_TICK.register(GrowthClient::tick);
	}

	public static Growth current() {
		Minecraft mc = Minecraft.getInstance();
		return mc.player == null ? Growth.DEFAULT : ModGrowth.get(mc.player);
	}

	private static void tick(Minecraft mc) {
		tick++;
		if (mc.player == null) { lastLevel = -1; return; }
		while (OPEN_KEY.consumeClick()) {
			if (mc.gui.screen() == null) mc.gui.setScreen(new GrowthScreen());
		}
		Growth g = current();
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
