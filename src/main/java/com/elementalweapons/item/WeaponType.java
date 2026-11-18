package com.elementalweapons.item;

/** 4가지 무기 종류 */
public enum WeaponType {
	DAGGER("dagger", "단검", "Dagger"),
	SWORD("sword", "검", "Sword"),
	GREATSWORD("greatsword", "대검", "Greatsword"),
	STAFF("staff", "지팡이", "Staff");

	public final String id;
	public final String koName;
	public final String enName;

	WeaponType(String id, String koName, String enName) {
		this.id = id;
		this.koName = koName;
		this.enName = enName;
	}
}
