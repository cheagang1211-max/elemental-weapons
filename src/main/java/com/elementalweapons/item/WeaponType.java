package com.elementalweapons.item;

/** 9가지 무기 종류 */
public enum WeaponType {
	DAGGER("dagger", "단검", "Dagger"),
	SWORD("sword", "검", "Sword"),
	GREATSWORD("greatsword", "대검", "Greatsword"),
	STAFF("staff", "지팡이", "Staff"),
	HAMMER("hammer", "망치", "Hammer"),
	SHIELD("shield", "방패", "Shield"),
	BOOK("book", "마법서", "Grimoire"),
	SCYTHE("scythe", "낫", "Scythe"),
	BOW("bow", "활", "Bow");

	public final String id;
	public final String koName;
	public final String enName;

	/** 이 무기를 쓸 수 있는 직업 */
	public PlayerClass playerClass() {
		return switch (this) {
			case STAFF -> PlayerClass.MAGE;
			case GREATSWORD -> PlayerClass.WARRIOR;
			case HAMMER -> PlayerClass.BREAKER;
			case SWORD, SHIELD -> PlayerClass.KNIGHT;
			case DAGGER -> PlayerClass.ASSASSIN;
			case BOOK -> PlayerClass.SUPPORT;
			case SCYTHE -> PlayerClass.REAPER;
			case BOW -> PlayerClass.RANGER;
		};
	}

	WeaponType(String id, String koName, String enName) {
		this.id = id;
		this.koName = koName;
		this.enName = enName;
	}
}
