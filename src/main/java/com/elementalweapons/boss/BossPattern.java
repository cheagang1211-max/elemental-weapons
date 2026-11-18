package com.elementalweapons.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

/**
 * 보스 공격 패턴 하나.
 *
 * @param name     패턴 이름 (디버그/공지용)
 * @param duration 패턴 길이 (틱)
 * @param minPhase 이 페이즈 이상에서만 사용 (1~3)
 * @param weight   선택될 확률 가중치
 * @param action   매 틱 실행되는 동작 (t = 패턴 시작 후 지난 틱)
 */
public record BossPattern(String name, int duration, int minPhase, int weight, Action action) {
	@FunctionalInterface
	public interface Action {
		void tick(ServerLevel level, LivingEntity target, int t);
	}
}
