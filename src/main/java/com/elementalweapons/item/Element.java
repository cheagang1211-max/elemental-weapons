package com.elementalweapons.item;

/** 7가지 속성 */
public enum Element {
	//        아이디       한국어   영어        대표색      지팡이 쿨다운(틱, 20틱=1초)
	ELECTRIC("electric", "전기", "Electric", 0x8FE8FF, 100),
	FIRE    ("fire",     "불",   "Fire",     0xFF7A2A, 160),
	WATER   ("water",    "물",   "Water",    0x3FA0FF, 140),
	GRASS   ("grass",    "풀",   "Grass",    0xFF9AC8, 160),
	WIND    ("wind",     "바람", "Wind",     0xD8FFF2, 100),
	DARK    ("dark",     "어둠", "Dark",     0x7A2AC0, 200),
	LIGHT   ("light",    "빛",   "Light",    0xFFE27A, 300);

	public final String id;
	public final String koName;
	public final String enName;
	public final int color;
	public final int staffCooldown;

	Element(String id, String koName, String enName, int color, int staffCooldown) {
		this.id = id;
		this.koName = koName;
		this.enName = enName;
		this.color = color;
		this.staffCooldown = staffCooldown;
	}
}
