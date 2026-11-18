package com.elementalweapons.boss;

import com.elementalweapons.item.Element;

/**
 * 보스 패턴 설정 한 줄.
 *
 * @param type     패턴 종류 (PatternType)
 * @param name     화면에 보일 패턴 이름
 * @param element  이 패턴의 속성 (null이면 보스의 현재 속성)
 * @param minPhase 몇 페이즈부터 쓰는지
 * @param weight   선택 확률 가중치
 * @param damage   플레이어 피해 (하트 반 칸 = 1)
 * @param count    개수 (투사체 수, 돌진 횟수, 광선 수 등)
 * @param radius   범위
 */
public record PatternSpec(PatternType type, String name, Element element, int minPhase, int weight,
		float damage, int count, double radius) {

	public static PatternSpec of(PatternType type, String name, Element element, int minPhase, int weight,
			float damage, int count, double radius) {
		return new PatternSpec(type, name, element, minPhase, weight, damage, count, radius);
	}
}
