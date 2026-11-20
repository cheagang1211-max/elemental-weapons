package com.elementalweapons.item;

/** 직업: 직업마다 쓸 수 있는 무기가 정해져 있음 */
public enum PlayerClass {
	NONE("없음", "None", "", 0xFFAAAAAA),
	MAGE("마법사", "Mage", "지팡이", 0xFFB0A2FF),
	WARRIOR("전사", "Warrior", "대검 · 망치", 0xFFFF8A6A),
	KNIGHT("기사", "Knight", "검 + 방패", 0xFF8AD0FF),
	ASSASSIN("암살자", "Assassin", "단검", 0xFF6AFFBA),
	SUPPORT("서포터", "Support", "마법서", 0xFFFFB0E0);

	public final String koName, enName, weapons;
	public final int color;

	PlayerClass(String koName, String enName, String weapons, int color) {
		this.koName = koName; this.enName = enName; this.weapons = weapons; this.color = color;
	}

	public static PlayerClass of(int i) {
		PlayerClass[] v = values();
		return i >= 0 && i < v.length ? v[i] : NONE;
	}
}
