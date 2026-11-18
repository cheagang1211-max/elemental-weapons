package com.elementalweapons.mob;

import com.elementalweapons.item.Element;

/**
 * 일반 몹 설정.
 * @param kind MELEE(근접) / RANGED(원거리 구체) / FLYING(날아다니며 급강하) / STALKER(순간이동 근접)
 */
public record MobDef(String id, String ko, Element element, Kind kind, double health, double damage, double speed,
		float width, float height, float renderScale, String biome) {
	public enum Kind { MELEE, RANGED, FLYING, STALKER }
}
