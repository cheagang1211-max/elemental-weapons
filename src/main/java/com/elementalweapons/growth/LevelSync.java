package com.elementalweapons.growth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;

/**
 * 마인크래프트 경험치 레벨 = 게임 레벨.
 *  · 화면 아래 초록 경험치 바/숫자를 게임 레벨·경험치로 덮어씀
 *  · 바닐라 경험치(구슬·광석·화로)를 얻으면 그만큼 게임 경험치로 바꿔 줌
 *  · 마법 부여대·모루가 깎는 레벨은 다시 덮어써지므로 게임 레벨은 줄지 않음
 */
public final class LevelSync {
	private LevelSync() {}

	/** 마지막으로 써 넣은 totalExperience */
	private static final Map<UUID, Integer> LAST = new HashMap<>();

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer p : server.getPlayerList().getPlayers()) sync(p);
		});
		ServerPlayConnectionEvents.DISCONNECT.register((h, s) -> LAST.remove(h.getPlayer().getUUID()));
	}

	private static long cumulative(Growth g) {
		long t = g.xp();
		for (int l = 1; l < g.level(); l++) t += Growth.need(l);
		return t;
	}

	private static void sync(ServerPlayer p) {
		Integer last = LAST.get(p.getUUID());
		if (last != null && p.totalExperience > last) {
			int gained = p.totalExperience - last;
			p.totalExperience = last;
			ModGrowth.addXp(p, gained);
		}
		Growth g = ModGrowth.get(p);
		float progress;
		if (g.level() >= Growth.MAX_LEVEL) {
			Paragon pg = ModGrowth.paragon(p);
			progress = (float) Math.min(1.0, pg.xp() / (double) Paragon.need(pg.level()));
		} else progress = (float) Math.min(0.999, g.xp() / (double) Growth.need(g.level()));
		int total = (int) Math.min(Integer.MAX_VALUE / 2, cumulative(g));
		if (p.experienceLevel != g.level() || Math.abs(p.experienceProgress - progress) > 1.0E-4 || p.totalExperience != total) {
			p.experienceLevel = g.level();
			p.experienceProgress = progress;
			p.totalExperience = total;
		}
		LAST.put(p.getUUID(), total);
	}
}
