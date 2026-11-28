package com.elementalweapons.growth;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.item.ElementalWeaponItem;
import com.elementalweapons.item.Enhancement;

/**
 * 이 세계 전용 도전과제 중 코드로 주는 것 (data/elementalweapons/advancement, tools/achievements/gen.py).
 * 상태로 판단하는 것은 2초마다 확인하고, 순간 행동(스킬·결투·보스 등)은 그 자리에서 줌.
 */
public final class Achieve {
	private Achieve() {}

	public static void grant(ServerPlayer p, String path) { grant(p, path, "done"); }

	public static void grant(ServerPlayer p, String path, String crit) {
		MinecraftServer s = p.level().getServer();
		if (s == null) return;
		AdvancementHolder h = s.getAdvancements().get(ElementalWeaponsMod.id(path));
		if (h == null) return;
		if (!p.getAdvancements().getOrStartProgress(h).isDone()) p.getAdvancements().award(h, crit);
	}

	/** 보스 처치 (보스 근처에 있던 모두) */
	public static void boss(ServerPlayer p, String id) {
		grant(p, "bosses/" + id);
		grant(p, "bosses/all", id);
	}

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 40 != 7) return;
			for (ServerPlayer p : server.getPlayerList().getPlayers()) {
				try { check(p); } catch (Throwable t) { ElementalWeaponsMod.LOGGER.error("[도전과제] 확인 오류", t); }
			}
		});
	}

	private static void check(ServerPlayer p) {
		if (Characters.choosing(p)) return;
		// 지역 도착
		var bk = p.level().getBiome(p.blockPosition()).unwrapKey();
		if (bk.isPresent() && bk.get().identifier().getNamespace().equals(ElementalWeaponsMod.MOD_ID)) {
			String b = bk.get().identifier().getPath();
			grant(p, "explore/" + b, "v");
			grant(p, "explore/all", b);
		}
		Growth g = ModGrowth.get(p);
		if (g.cls() > 0) grant(p, "journey/class");
		if (Evolution.element(p) >= 0) grant(p, "journey/evolve");
		int lv = g.level();
		if (lv >= 30) grant(p, "journey/lv30");
		if (lv >= 60) grant(p, "journey/lv60");
		if (lv >= 100) grant(p, "journey/lv100");
		if (lv >= 150) grant(p, "journey/lv150");
		if (lv >= Growth.MAX_LEVEL) grant(p, "journey/lv200");
		if (ModGrowth.paragon(p).level() >= 1) grant(p, "journey/paragon");
		int stage = Jobs.get(p).stage();
		if (stage >= 2) grant(p, "journey/job2");
		if (stage >= 3) grant(p, "journey/job3");
		Characters.Roster r = p.getAttached(Characters.ROSTER);
		if (r != null) {
			int used = 1;
			for (int i = 0; i < r.slots().size(); i++) if (i != r.active() && !r.slots().get(i).isEmpty()) used++;
			if (used >= 2) grant(p, "journey/second");
		}
		int best = 0;
		for (ItemStack st : p.getInventory()) if (st.getItem() instanceof ElementalWeaponItem) best = Math.max(best, Enhancement.level(st));
		if (best >= 5) grant(p, "craft/enh5");
		if (best >= 10) grant(p, "craft/enh10");
		if (best >= Enhancement.MAX) grant(p, "craft/enh15");
		for (int i = 0; i < 20; i++) if (CoreForge.level(p, i) > 0) { grant(p, "craft/core"); break; }
	}

	/** 투기장 승리 (통산 n승) */
	public static void duelWin(ServerPlayer p, int wins) {
		grant(p, "journey/duel");
		if (wins >= 10) grant(p, "journey/duel10");
	}

}
