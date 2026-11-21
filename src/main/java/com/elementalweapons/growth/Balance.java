package com.elementalweapons.growth;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

/** 난이도 조정: 몬스터(보스 포함)가 플레이어에게 주는 피해를 하트 1칸(2) 줄임. 최소 반 칸은 들어감. */
public final class Balance {
	private Balance() {}

	public static final float MONSTER_DAMAGE_CUT = 2.0F;
	private static boolean reducing;

	public static void initialize() {
		net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.JOIN.register((h, sender, server) -> server.execute(() -> Tutorial.grantArmorIfMissing(h.getPlayer())));
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (reducing || !(entity instanceof ServerPlayer p)) return true;
			Entity atk = source.getEntity();
			if (!(atk instanceof Enemy) || atk instanceof Player) return true;
			float cut = Math.max(0.5F, amount - MONSTER_DAMAGE_CUT);
			if (cut >= amount) return true;
			reducing = true;
			try { p.hurtServer((ServerLevel) p.level(), source, cut); } finally { reducing = false; }
			return false;   // 원래 피해는 취소하고 줄인 피해만 들어감
		});
	}
}
