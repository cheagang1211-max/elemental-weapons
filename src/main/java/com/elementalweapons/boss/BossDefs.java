package com.elementalweapons.boss;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static net.minecraft.ChatFormatting.*;

import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

import static com.elementalweapons.boss.PatternType.*;
import static com.elementalweapons.item.Element.*;

import com.elementalweapons.item.ModItems;

/**
 * 보스 15종 설정 (뇌신 볼트라는 VoltraBoss 클래스로 따로 있음)
 *
 *  수호자 7   : 볼트라(전기, 별도) 이그니스(불) 마리나(물) 실바(풀) 제피로스(바람) 녹스(어둠) 루미엘(빛)
 *  지역 지배자 4: 수정 골렘(빛) 용암 거미 여왕(불) 얼음 거인(물) 사막 스핑크스(바람)
 *  혼합 속성 4 : 증기 거신(불+물) 뇌운 와이번(전기+바람) 황혼의 쌍둥이(빛+어둠) 독꽃 히드라(풀+어둠)
 *  최종 보스   : 원소의 근원 프리마 (50억, 체력 1/7마다 속성 변화)
 *
 * pattern(종류, 이름, 속성, 최소페이즈, 가중치, 피해, 개수, 범위)
 */
public final class BossDefs {
	private BossDefs() {}

	public static final Map<String, BossDef> ALL = new LinkedHashMap<>();
	public static final Map<EntityType<?>, BossDef> BY_TYPE = new HashMap<>();

	private static BossDef add(BossDef d) {
		ALL.put(d.id, d);
		d.drop(() -> ModItems.BOSS_CORES.get(d.id), 1, 2);
		return d;
	}

	static {
		// ======================= 속성 수호자 =======================
		add(BossDef.of("ignis", "화염룡 이그니스", "Ignis, the Flame Dragon").element(FIRE, null).health(1_200_000_000L)
				.look("✹", GOLD, BossEvent.BossBarColor.RED).size(4.0F, 4.0F, 3.0F, 2.2F, 2.2F).hover(5.0, 9.0, 0.32, true)
				.lines("하찮은 것들이 감히 용의 둥지에!", "내 비늘이 녹아내릴 만큼 뜨거워지는군!", "모든 것을 재로 만들어 주마!")
				.pattern(BARRAGE, "화염구 연사", FIRE, 1, 3, 6, 3, 2.5)
				.pattern(RAIN, "불비", FIRE, 1, 2, 7, 2, 2.5)
				.pattern(DASH, "화염 돌진", FIRE, 1, 2, 9, 1, 3.5)
				.pattern(METEOR, "용암 운석", FIRE, 2, 3, 15, 0, 6.0)
				.pattern(BEAM, "용의 숨결", FIRE, 3, 3, 8, 1, 18)
				.drop(() -> com.elementalweapons.world.ModBlocks.item("ember_crystal"), 8, 16).drop(() -> com.elementalweapons.world.ModBlocks.item("orichalcum_ingot"), 2, 4));

		add(BossDef.of("marina", "심해의 여왕 마리나", "Marina, Queen of the Deep").element(WATER, null).health(1_200_000_000L)
				.look("≋", AQUA, BossEvent.BossBarColor.BLUE).size(2.4F, 5.0F, 4.0F, 2.0F, 1.5F).hover(3.0, 8.0, 0.28, true)
				.lines("바다의 분노를 맛보아라.", "조류가 바뀌었다. 너희는 이제 끝이다!", "깊은 바닷속으로 끌어내려 주마!")
				.pattern(RAIN, "해일 기둥", WATER, 1, 3, 7, 1, 2.5)
				.pattern(VORTEX, "소용돌이 감옥", WATER, 1, 2, 10, 0, 6.0)
				.pattern(ORBIT, "물방울 궤도", WATER, 1, 2, 5, 6, 1.4)
				.pattern(SUMMON, "심해의 하수인", WATER, 1, 1, 0, 2, 0)
				.pattern(NOVA, "파도 고리", WATER, 2, 3, 9, 0, 8.0)
				.drop(() -> com.elementalweapons.world.ModBlocks.item("tide_crystal"), 8, 16).drop(() -> com.elementalweapons.world.ModBlocks.item("orichalcum_ingot"), 2, 4));

		add(BossDef.of("silva", "고대수 실바", "Silva, the Ancient Tree").element(GRASS, null).health(1_300_000_000L)
				.look("❀", GREEN, BossEvent.BossBarColor.GREEN).size(3.6F, 6.0F, 5.0F, 2.4F, 2.2F).hover(0.5, 6.0, 0.22, false)
				.lines("천 년을 자라온 뿌리가 깨어난다.", "숲이 비명을 지른다! 용서하지 않겠다!", "모든 생명을 대지로 돌려보내리라!")
				.pattern(SLAM, "대지 강타", GRASS, 1, 3, 10, 0, 16)
				.pattern(RAIN, "가시 덩굴", GRASS, 1, 3, 7, 2, 2.5)
				.pattern(SUMMON, "숲의 수호자", GRASS, 1, 1, 0, 2, 0)
				.pattern(NOVA, "꽃가루 폭발", GRASS, 2, 2, 8, 0, 8.0)
				.pattern(MARK, "생명 흡수의 표식", GRASS, 3, 3, 13, 0, 3.8)
				.drop(() -> com.elementalweapons.world.ModBlocks.item("bloom_crystal"), 8, 16).drop(() -> com.elementalweapons.world.ModBlocks.item("orichalcum_ingot"), 2, 4));

		add(BossDef.of("zephyros", "폭풍새 제피로스", "Zephyros, the Storm Bird").element(WIND, null).health(1_200_000_000L)
				.look("☁", WHITE, BossEvent.BossBarColor.WHITE).size(4.0F, 3.0F, 2.2F, 2.2F, 2.0F).hover(7.0, 10.0, 0.38, true)
				.lines("하늘의 주인은 나 하나뿐이다!", "바람이 거세진다. 날아가지 않게 조심해라!", "폭풍의 눈 속으로 사라져라!")
				.pattern(DASH, "급강하", WIND, 1, 3, 9, 2, 3.5)
				.pattern(ORBIT, "회오리 깃털", WIND, 1, 2, 5, 8, 1.4)
				.pattern(BARRAGE, "깃털 탄막", WIND, 1, 2, 5, 4, 2.0)
				.pattern(VORTEX, "폭풍의 눈", WIND, 2, 2, 9, 0, 6.0)
				.pattern(BEAM, "진공 칼날", WIND, 3, 3, 8, 3, 16)
				.drop(() -> com.elementalweapons.world.ModBlocks.item("gale_crystal"), 8, 16).drop(() -> com.elementalweapons.world.ModBlocks.item("orichalcum_ingot"), 2, 4));

		add(BossDef.of("nox", "그림자 군주 녹스", "Nox, Lord of Shadows").element(DARK, null).health(1_400_000_000L)
				.look("☾", DARK_PURPLE, BossEvent.BossBarColor.PURPLE).size(2.4F, 5.0F, 4.2F, 2.0F, 1.5F).hover(2.5, 6.0, 0.30, true)
				.lines("어둠은 언제나 너희 곁에 있었다.", "그림자가 짙어진다... 느껴지는가?", "영원한 밤이 시작된다!")
				.pattern(BLINK, "그림자 도약", DARK, 1, 3, 10, 0, 4.5)
				.pattern(MARK, "죽음의 낙인", DARK, 1, 2, 12, 0, 3.8)
				.pattern(SUMMON, "망령 소환", DARK, 1, 1, 0, 2, 0)
				.pattern(ORBIT, "공허 구체", DARK, 1, 2, 6, 6, 1.4)
				.pattern(VORTEX, "심연의 손", DARK, 2, 3, 10, 0, 6.0)
				.drop(() -> com.elementalweapons.world.ModBlocks.item("umbral_crystal"), 8, 16).drop(() -> com.elementalweapons.world.ModBlocks.item("orichalcum_ingot"), 2, 4));

		add(BossDef.of("lumiel", "성기사 루미엘", "Lumiel, the Holy Paladin").element(LIGHT, null).health(1_400_000_000L)
				.look("✦", YELLOW, BossEvent.BossBarColor.YELLOW).size(2.4F, 5.2F, 4.4F, 2.0F, 1.5F).hover(3.0, 7.0, 0.30, true)
				.lines("빛의 심판을 받아라.", "신성한 빛이 더욱 강해진다!", "천상의 군세여, 나에게 힘을!")
				.pattern(BEAM, "성광 십자", LIGHT, 1, 3, 8, 4, 18)
				.pattern(RAIN, "빛의 창", LIGHT, 1, 2, 7, 1, 2.5)
				.pattern(DASH, "성검 돌격", LIGHT, 1, 2, 10, 1, 3.5)
				.pattern(METEOR, "심판의 빛", LIGHT, 2, 3, 15, 0, 6.0)
				.pattern(NOVA, "신성 결계", LIGHT, 3, 2, 10, 0, 9.0)
				.drop(() -> com.elementalweapons.world.ModBlocks.item("radiant_crystal"), 8, 16).drop(() -> com.elementalweapons.world.ModBlocks.item("orichalcum_ingot"), 2, 4));

		// ======================= 지역 지배자 =======================
		add(BossDef.of("crystal_golem", "수정 골렘", "Crystal Golem").element(LIGHT, null).health(1_500_000_000L)
				.look("◆", LIGHT_PURPLE, BossEvent.BossBarColor.PINK).size(3.6F, 5.0F, 4.0F, 2.4F, 2.4F).hover(0.5, 5.0, 0.20, false)
				.lines("...침입자 감지. 배제한다.", "...공명 출력 상승.", "...전 수정 폭주 모드.")
				.pattern(SLAM, "수정 내려찍기", LIGHT, 1, 3, 11, 0, 16)
				.pattern(BARRAGE, "수정 파편", LIGHT, 1, 2, 6, 5, 2.0)
				.pattern(NOVA, "공명", LIGHT, 1, 2, 9, 0, 7.0)
				.pattern(METEOR, "수정 낙하", LIGHT, 2, 3, 14, 0, 5.0)
				.drop(() -> com.elementalweapons.world.ModBlocks.item("radiant_crystal"), 12, 20).drop(() -> com.elementalweapons.world.ModBlocks.item("aether_ingot"), 8, 16));

		add(BossDef.of("lava_queen", "용암 거미 여왕", "Lava Spider Queen").element(FIRE, null).health(1_500_000_000L)
				.look("✹", RED, BossEvent.BossBarColor.RED).size(4.4F, 2.4F, 1.6F, 2.2F, 2.4F).hover(0.5, 6.0, 0.34, false)
				.lines("내 둥지에 들어온 먹잇감이로군.", "아이들아, 식사 시간이다!", "녹아내려라, 모조리!")
				.pattern(DASH, "도약", FIRE, 1, 3, 10, 2, 3.5)
				.pattern(RAIN, "용암 거미줄", FIRE, 1, 2, 7, 2, 3.0)
				.pattern(SUMMON, "새끼 거미", FIRE, 1, 1, 0, 3, 0)
				.pattern(VORTEX, "거미줄 끌어당기기", FIRE, 2, 2, 9, 0, 5.0)
				.pattern(ORBIT, "용암 알", FIRE, 3, 2, 6, 6, 1.4)
				.drop(() -> com.elementalweapons.world.ModBlocks.item("ember_crystal"), 12, 20).drop(() -> com.elementalweapons.world.ModBlocks.item("aether_ingot"), 8, 16));

		add(BossDef.of("frost_giant", "얼음 거인", "Frost Giant").element(WATER, null).health(1_500_000_000L)
				.look("❄", AQUA, BossEvent.BossBarColor.WHITE).size(3.6F, 6.4F, 5.6F, 2.6F, 2.4F).hover(0.5, 6.0, 0.20, false)
				.lines("얼어붙어라, 작은 것들아.", "눈보라가 몰아친다!", "영원한 겨울 속에 잠들어라!")
				.pattern(SLAM, "빙하 진동", WATER, 1, 3, 11, 0, 16)
				.pattern(METEOR, "빙산 투척", WATER, 1, 2, 14, 0, 5.0)
				.pattern(BARRAGE, "고드름", WATER, 1, 2, 6, 4, 2.0)
				.pattern(NOVA, "한파", WATER, 2, 3, 9, 0, 8.0)
				.drop(() -> com.elementalweapons.world.ModBlocks.item("tide_crystal"), 12, 20).drop(() -> com.elementalweapons.world.ModBlocks.item("aether_ingot"), 8, 16));

		add(BossDef.of("sphinx", "사막 스핑크스", "Desert Sphinx").element(WIND, null).health(1_500_000_000L)
				.look("☀", GOLD, BossEvent.BossBarColor.YELLOW).size(3.6F, 4.0F, 3.4F, 2.2F, 2.2F).hover(2.0, 7.0, 0.26, true)
				.lines("수수께끼를 풀지 못한 자, 모래가 되리라.", "모래 폭풍이 일어난다!", "사막의 저주를 받아라!")
				.pattern(BEAM, "모래 폭풍 광선", WIND, 1, 3, 8, 2, 18)
				.pattern(RAIN, "모래 기둥", WIND, 1, 2, 7, 2, 2.5)
				.pattern(MARK, "수수께끼의 저주", WIND, 1, 2, 12, 0, 3.8)
				.pattern(BLINK, "신기루", WIND, 2, 2, 9, 0, 4.5)
				.pattern(VORTEX, "유사 지옥", WIND, 3, 3, 10, 0, 6.0)
				.drop(() -> com.elementalweapons.world.ModBlocks.item("gale_crystal"), 12, 20).drop(() -> com.elementalweapons.world.ModBlocks.item("aether_ingot"), 8, 16));

		// ======================= 혼합 속성 =======================
		add(BossDef.of("steam_colossus", "증기 거신", "Steam Colossus").element(FIRE, WATER).health(2_500_000_000L)
				.look("♨", GOLD, BossEvent.BossBarColor.RED).size(3.8F, 6.6F, 5.8F, 2.6F, 2.6F).hover(0.5, 6.0, 0.22, false)
				.lines("보일러 압력 상승. 전투 개시.", "압력 한계 돌파!", "과열! 과열! 폭발한다!")
				.pattern(SLAM, "증기 해머", FIRE, 1, 3, 12, 0, 16)
				.pattern(BARRAGE, "증기탄", WATER, 1, 2, 7, 4, 2.2)
				.pattern(NOVA, "과열 폭발", FIRE, 1, 2, 11, 0, 8.0)
				.pattern(RAIN, "끓는 비", WATER, 1, 2, 8, 2, 2.5)
				.pattern(BEAM, "증기 분사", FIRE, 2, 3, 9, 2, 18)
				.pattern(METEOR, "보일러 폭발", FIRE, 3, 3, 16, 0, 6.0)
				.drop(() -> ModItems.ELEMENT_ESSENCE, 2, 3).drop(() -> com.elementalweapons.world.ModBlocks.item("orichalcum_ingot"), 4, 6));

		add(BossDef.of("storm_wyvern", "뇌운 와이번", "Storm Wyvern").element(ELECTRIC, WIND).health(2_500_000_000L)
				.look("⚡", AQUA, BossEvent.BossBarColor.BLUE).size(4.4F, 3.4F, 2.6F, 2.4F, 2.4F).hover(7.0, 10.0, 0.40, true)
				.lines("하늘이 울부짖는다!", "번개와 바람이 하나가 된다!", "폭풍의 끝을 보여주마!")
				.pattern(DASH, "번개 활강", ELECTRIC, 1, 3, 11, 2, 3.5)
				.pattern(RAIN, "뇌운", ELECTRIC, 1, 2, 8, 2, 2.5)
				.pattern(ORBIT, "폭풍 고리", WIND, 1, 2, 6, 8, 1.4)
				.pattern(VORTEX, "태풍의 눈", WIND, 1, 2, 10, 0, 6.0)
				.pattern(BEAM, "뇌전 브레스", ELECTRIC, 2, 3, 9, 1, 20)
				.pattern(MARK, "천둥의 낙인", ELECTRIC, 3, 3, 14, 0, 3.8)
				.drop(() -> ModItems.ELEMENT_ESSENCE, 2, 3).drop(() -> com.elementalweapons.world.ModBlocks.item("orichalcum_ingot"), 4, 6));

		add(BossDef.of("twilight_twins", "황혼의 쌍둥이", "Twilight Twins").element(LIGHT, DARK).health(3_000_000_000L)
				.look("☯", LIGHT_PURPLE, BossEvent.BossBarColor.PURPLE).size(3.6F, 5.0F, 4.2F, 2.0F, 2.0F).hover(3.0, 7.0, 0.30, true)
				.lines("빛과 어둠, 우리는 둘이자 하나.", "낮과 밤이 뒤섞인다!", "황혼의 끝에서 함께 사라지자!")
				.pattern(BEAM, "빛과 어둠의 교차", LIGHT, 1, 2, 9, 2, 18)
				.pattern(BEAM, "어둠의 반달", DARK, 1, 2, 9, 2, 18)
				.pattern(BLINK, "황혼 도약", DARK, 1, 2, 11, 0, 4.5)
				.pattern(ORBIT, "쌍성 궤도", DARK, 1, 2, 7, 6, 1.4)
				.pattern(MARK, "일식", LIGHT, 2, 3, 15, 0, 4.0)
				.pattern(METEOR, "월광 낙하", LIGHT, 3, 3, 16, 0, 6.0)
				.drop(() -> ModItems.ELEMENT_ESSENCE, 3, 4).drop(() -> com.elementalweapons.world.ModBlocks.item("orichalcum_ingot"), 5, 8));

		add(BossDef.of("hydra", "독꽃 히드라", "Venom Bloom Hydra").element(GRASS, DARK).health(3_000_000_000L)
				.look("❀", DARK_GREEN, BossEvent.BossBarColor.GREEN).size(4.4F, 5.0F, 4.2F, 2.4F, 2.6F).hover(0.5, 7.0, 0.22, false)
				.lines("세 개의 머리가 너희를 노린다.", "독이 퍼진다... 숨이 막히지?", "썩어 문드러져라!")
				.pattern(BARRAGE, "독 꽃가루", GRASS, 1, 3, 7, 3, 2.2)
				.pattern(RAIN, "가시 늪", GRASS, 1, 2, 8, 2, 2.5)
				.pattern(VORTEX, "늪의 손아귀", DARK, 1, 2, 11, 0, 6.0)
				.pattern(SUMMON, "독꽃 하수인", GRASS, 1, 1, 0, 3, 0)
				.pattern(BEAM, "삼두 브레스", DARK, 2, 3, 9, 3, 16)
				.pattern(NOVA, "부패 폭발", DARK, 3, 2, 12, 0, 9.0)
				.drop(() -> ModItems.ELEMENT_ESSENCE, 3, 4).drop(() -> com.elementalweapons.world.ModBlocks.item("orichalcum_ingot"), 5, 8));

		// ======================= 최종 보스 =======================
		add(BossDef.of("prima", "원소의 근원 프리마", "Prima, Origin of Elements").shifting().element(ELECTRIC, null).health(5_000_000_000L)
				.look("✪", WHITE, BossEvent.BossBarColor.BLUE).size(3.0F, 6.0F, 4.6F, 2.4F, 2.0F).hover(5.0, 9.0, 0.34, true)
				.lines("나는 모든 원소의 시작이자 끝.", "일곱 빛깔의 힘이 깨어난다!", "근원으로 돌아가라, 모든 것이여!")
				.pattern(RAIN, "원소 강림", null, 1, 3, 8, 3, 2.8)
				.pattern(BARRAGE, "원소 탄막", null, 1, 2, 7, 3, 2.2)
				.pattern(NOVA, "근원의 파동", null, 1, 2, 11, 0, 9.0)
				.pattern(BEAM, "칠색 광선", null, 1, 2, 9, 3, 20)
				.pattern(ORBIT, "원소 궤도", null, 1, 2, 7, 7, 1.4)
				.pattern(DASH, "근원 돌진", null, 1, 1, 11, 2, 3.8)
				.pattern(VORTEX, "근원의 소용돌이", null, 2, 2, 11, 0, 6.5)
				.pattern(METEOR, "원소 운석", null, 2, 3, 17, 0, 6.5)
				.pattern(MARK, "근원의 낙인", null, 2, 2, 16, 0, 4.0)
				.pattern(BLINK, "차원 도약", null, 3, 2, 12, 0, 5.0)
				.drop(() -> ModItems.ELEMENT_ESSENCE, 8, 12).drop(() -> com.elementalweapons.world.ModBlocks.item("orichalcum_ingot"), 8, 12).drop(() -> com.elementalweapons.world.ModBlocks.item("aether_ingot"), 16, 32));
	}
}
