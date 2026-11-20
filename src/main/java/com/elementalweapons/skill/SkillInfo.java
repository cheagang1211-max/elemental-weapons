package com.elementalweapons.skill;

import com.elementalweapons.item.WeaponType;

/** Z·X·C 스킬 이름과 재사용 대기시간(틱). 서버와 화면(HUD)이 같이 씀 */
public final class SkillInfo {
	private SkillInfo() {}

	public record Info(String name, int cooldown) {}

	/** 나무 무기: Z = 구르기, X·C 는 속성 진화 후 해금 */
	public static final Info[] WOOD = {new Info("구르기", 60), new Info("잠김 (10레벨 진화)", 0), new Info("잠김 (10레벨 진화)", 0)};

	public static Info[] of(WeaponType t) {
		return switch (t) {
			case SWORD -> new Info[]{new Info("질풍 찌르기", 100), new Info("십자 베기", 160), new Info("성역 결계", 600)};
			case GREATSWORD -> new Info[]{new Info("도약 내려찍기", 160), new Info("회오리 베기", 200), new Info("검기 방출", 400)};
			case HAMMER -> new Info[]{new Info("지진", 200), new Info("망치 투척", 160), new Info("대지 분쇄", 500)};
			case DAGGER -> new Info[]{new Info("그림자 걸음", 120), new Info("칼날 부채", 140), new Info("연무", 500)};
			case STAFF -> new Info[]{new Info("원소 화살", 80), new Info("순간 이동", 160), new Info("원소 폭풍", 600)};
			case BOOK -> new Info[]{new Info("치유의 빛", 200), new Info("수호의 막", 300), new Info("축복의 장", 700)};
			case SCYTHE -> new Info[]{new Info("영혼 수확", 160), new Info("망령 질주", 140), new Info("사신의 낫", 600)};
			case BOW -> new Info[]{new Info("후퇴 사격", 120), new Info("화살비", 300), new Info("관통 화살", 500)};
			case SHIELD -> new Info[]{new Info("방패 돌진", 120), new Info("도발의 함성", 300), new Info("철벽", 600)};
		};
	}

	/** 무기 단계(1~4): 레벨 10~29 / 30~59 / 60~99 / 100+ */
	public static int tier(int level) { return level >= 100 ? 4 : level >= 60 ? 3 : level >= 30 ? 2 : 1; }

	public static float tierMult(int tier) { return switch (tier) { case 4 -> 2.0F; case 3 -> 1.55F; case 2 -> 1.25F; default -> 1.0F; }; }
}
