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

	/** 캐릭터 교체 직후: 바뀐 경험치 총량을 '새로 얻은 경험치'로 착각하지 않게 */
	public static void forget(ServerPlayer p) { LAST.remove(p.getUUID()); }

	private static long cumulative(Growth g) {
		long t = g.xp();
		for (int l = 1; l < g.level(); l++) t += Growth.need(l);
		return t;
	}

	/** 포인트 = 레벨에 맞는 합계 − 찍은 스탯 (예전 규칙으로 더 받은 포인트는 정리, 너무 많이 찍었으면 초기화) */
	private static void fixPoints(ServerPlayer p) {
		Growth g = ModGrowth.get(p);
		int total = Growth.totalPoints(g.level());
		int spent = g.str() + g.mag() + g.vit() + g.agi();
		if (spent + g.points() == total) return;
		if (spent > total) {
			ModGrowth.set(p, new Growth(g.level(), g.xp(), total, 0, 0, 0, 0, g.cls()));
			p.sendSystemMessage(net.minecraft.network.chat.Component.literal("✦ 스탯 포인트 규칙이 바뀌어 스탯을 초기화했습니다. [K]에서 다시 찍어 주세요 (100레벨에 모든 스탯 최대).").withStyle(net.minecraft.ChatFormatting.GOLD));
		} else ModGrowth.set(p, g.withPoints(total - spent));
		ModGrowth.applyStats(p);
	}

	private static void sync(ServerPlayer p) {
		if (p.tickCount % 20 == 5) fixPoints(p);
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
