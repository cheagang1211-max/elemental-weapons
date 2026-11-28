"""이 세계 전용 도전과제 생성 (바닐라 도전과제는 AdvancementFilterMixin 이 없앰)
사용법: python3 tools/achievements/gen.py <프로젝트 루트>
코드에서 주는 것(trigger impossible)은 Achieve.java 가 criterion "done" 으로 줌."""
import sys, os, json, shutil
ROOT = sys.argv[1]; NS = "elementalweapons"
OUT = f"{ROOT}/src/main/resources/data/{NS}/advancement"
shutil.rmtree(OUT, ignore_errors=True)

def I(x): return x if ":" in x else f"{NS}:{x}"
CODE = {"done": {"trigger": "minecraft:impossible"}}
def adv(path, parent, icon, title, desc, crit=None, frame="task", bg=None, hidden=False, req=None, toast=True):
    d = {"display": {"icon": {"id": I(icon)}, "title": title, "description": desc, "frame": frame,
                     "show_toast": toast, "announce_to_chat": toast, "hidden": hidden}}
    if bg: d["display"]["background"] = bg
    if parent: d["parent"] = f"{NS}:{parent}"
    d["criteria"] = crit or CODE
    if req: d["requirements"] = req
    os.makedirs(os.path.dirname(f"{OUT}/{path}.json"), exist_ok=True)
    json.dump(d, open(f"{OUT}/{path}.json", "w"), ensure_ascii=False, indent="\t")

TICK = {"start": {"trigger": "minecraft:tick"}}
def has(*items): return {"trigger": "minecraft:inventory_changed", "conditions": {"items": [{"items": I(i)} for i in items]}}
def biome(b): return {"trigger": "minecraft:location", "conditions": {"player": {"location": {"biomes": I(b)}}}}

# ───────────── 1. 원소의 여정 (성장) ─────────────
J = "journey"
adv(f"{J}/root", None, "fire_sword", "원소의 여정", "하늘 위 원소 세계에 첫발을 디뎠다", TICK, bg=f"{NS}:block/aether_stone", toast=False)
adv(f"{J}/class", f"{J}/root", "wooden_sword", "운명의 갈림길", "직업을 정한다")
adv(f"{J}/evolve", f"{J}/class", "electric_staff", "원소의 각성", "10레벨에 속성을 골라 무기를 진화시킨다")
adv(f"{J}/skill", f"{J}/evolve", "wind_dagger", "손끝의 기술", "Z·X·C 스킬을 처음 쓴다")
adv(f"{J}/finisher", f"{J}/skill", "fire_greatsword", "삼연격", "좌클릭 3연타로 마무리 기술을 터뜨린다")
adv(f"{J}/grand", f"{J}/finisher", "light_book", "천지를 가르는 일격", "Shift+우클릭 대형 기술을 쓴다", frame="goal")
adv(f"{J}/lv30", f"{J}/evolve", "experience_bottle".replace("experience_bottle", "minecraft:experience_bottle"), "숙련자", "30레벨에 이른다")
adv(f"{J}/lv60", f"{J}/lv30", "minecraft:experience_bottle", "베테랑", "60레벨에 이른다")
adv(f"{J}/lv100", f"{J}/lv60", "minecraft:nether_star", "영웅", "100레벨에 이른다", frame="goal")
adv(f"{J}/lv150", f"{J}/lv100", "minecraft:nether_star", "살아 있는 전설", "150레벨에 이른다", frame="goal")
adv(f"{J}/lv200", f"{J}/lv150", "element_essence", "원소의 화신", "최고 레벨 200에 이른다", frame="challenge")
adv(f"{J}/paragon", f"{J}/lv200", "element_essence", "한계 너머", "파라곤 레벨을 처음 올린다", frame="challenge")
adv(f"{J}/job2", f"{J}/lv60", "minecraft:writable_book", "두 갈래 길", "전직 교관의 시험을 통과해 2차 전직을 한다", frame="goal")
adv(f"{J}/job3", f"{J}/job2", "minecraft:enchanted_book", "정점에 선 자", "3차 전직을 마친다", frame="challenge")
adv(f"{J}/second", f"{J}/class", "minecraft:armor_stand", "또 다른 나", "두 번째 캐릭터를 키우기 시작한다")
adv(f"{J}/duel", f"{J}/class", "minecraft:iron_sword", "투기장의 첫 승리", "투기장 결투에서 이긴다")
adv(f"{J}/duel10", f"{J}/duel", "minecraft:golden_sword", "투기장의 패자", "투기장 결투에서 10번 이긴다", frame="challenge")

# ───────────── 2. 천공 탐험 ─────────────
E = "explore"
BIOMES = [("capital_gardens", "수도 정원", "minecraft:bell"), ("thunder_plateau", "뇌전 고원", "storm_crystal"), ("volcanic_plains", "화산 평원", "ember_crystal"),
          ("azure_forest", "청해의 숲", "tide_crystal"), ("blossom_forest", "꽃의 숲", "bloom_crystal"), ("gale_highlands", "바람의 고원", "gale_crystal"),
          ("shadow_woods", "그림자 숲", "umbral_crystal"), ("radiant_plains", "빛의 평원", "radiant_crystal"), ("elemental_ocean", "원소의 바다", "minecraft:heart_of_the_sea"),
          ("elemental_abyss", "원소의 심연", "minecraft:sculk")]
adv(f"{E}/root", None, "minecraft:feather", "천공 탐험", "끝없이 펼쳐진 하늘섬을 누빈다", TICK, bg=f"{NS}:block/radiant_grass_top", toast=False)
adv(f"{E}/capital_gardens", f"{E}/root", "minecraft:bell", "수도에 오다", "거점 수도의 정원에 들어선다", {"v": biome("capital_gardens")})
for b, name, icon in BIOMES[1:9]:
    adv(f"{E}/{b}", f"{E}/capital_gardens", icon, f"{name}에 발을 딛다", f"{name}에 간다", {"v": biome(b)})
adv(f"{E}/elemental_abyss", f"{E}/elemental_ocean", "minecraft:sculk", "심연을 들여다본 자", "멀리 떨어진 원소의 심연에 들어선다", {"v": biome("elemental_abyss")}, frame="goal")
adv(f"{E}/all", f"{E}/elemental_abyss", "minecraft:filled_map", "천공의 지도", "하늘 세계의 모든 지역을 밟는다",
    {b: biome(b) for b, _, _ in BIOMES}, frame="challenge", req=[[b] for b, _, _ in BIOMES])
adv(f"{E}/summit", f"{E}/capital_gardens", "minecraft:bell", "정상의 종", "정상의 종을 울려 오늘의 하사품을 받는다")

# ───────────── 3. 보스 토벌 ─────────────
B = "bosses"
BOSS = [("voltra", "뇌신 볼트라", "voltra_core", None),
        ("ignis", "화염룡 이그니스", "ignis_core", "voltra"), ("marina", "심해의 여왕 마리나", "marina_core", "voltra"), ("silva", "고대수 실바", "silva_core", "voltra"),
        ("zephyros", "폭풍새 제피로스", "zephyros_core", "voltra"), ("nox", "그림자 군주 녹스", "nox_core", "voltra"), ("lumiel", "성기사 루미엘", "lumiel_core", "voltra"),
        ("crystal_golem", "수정 골렘", "crystal_golem_core", "lumiel"), ("lava_queen", "용암 거미 여왕", "lava_queen_core", "ignis"),
        ("frost_giant", "얼음 거인", "frost_giant_core", "marina"), ("sphinx", "사막 스핑크스", "sphinx_core", "zephyros"),
        ("steam_colossus", "증기 거신", "steam_colossus_core", "lava_queen"), ("storm_wyvern", "뇌운 와이번", "storm_wyvern_core", "sphinx"),
        ("twilight_twins", "황혼의 쌍둥이", "twilight_twins_core", "nox"), ("hydra", "독꽃 히드라", "hydra_core", "silva")]
adv(f"{B}/root", None, "rift_key", "보스 토벌", "원소 세계를 지배하는 강대한 존재들에 맞선다", TICK, bg=f"{NS}:block/abyss_stone", toast=False)
for bid, name, icon, par in BOSS:
    fusion = bid in ("steam_colossus", "storm_wyvern", "twilight_twins", "hydra")
    adv(f"{B}/{bid}", f"{B}/{par or 'root'}", icon, f"{name} 토벌", f"{name}을(를) 쓰러뜨린다", frame="goal" if fusion else "task")
adv(f"{B}/prima", f"{B}/twilight_twins", "prima_core", "근원에 닿다", "최종 보스 원소의 근원 프리마를 쓰러뜨린다", frame="challenge")
ALL = [b[0] for b in BOSS] + ["prima"]
adv(f"{B}/all", f"{B}/prima", "element_essence", "원소 세계의 정복자", "모든 보스를 한 번 이상 쓰러뜨린다",
    {b: {"trigger": "minecraft:impossible"} for b in ALL}, frame="challenge", req=[[b] for b in ALL])

# ───────────── 4. 장인의 길 (재료·강화) ─────────────
C = "craft"
CRYST = ["storm_crystal", "ember_crystal", "tide_crystal", "bloom_crystal", "gale_crystal", "umbral_crystal", "radiant_crystal"]
adv(f"{C}/root", None, "aether_pickaxe", "장인의 길", "새로운 광물과 재료로 장비를 단련한다", TICK, bg=f"{NS}:block/orichalcum_ore", toast=False)
adv(f"{C}/dust", f"{C}/root", "element_dust", "반짝이는 가루", "원소 가루를 얻는다", {"v": has("element_dust")})
adv(f"{C}/crystal", f"{C}/dust", "radiant_crystal", "원소 결정", "원소 결정을 하나 얻는다", {c: has(c) for c in CRYST}, req=[CRYST])
adv(f"{C}/all_crystals", f"{C}/crystal", "umbral_crystal", "일곱 빛깔", "일곱 가지 원소 결정을 모두 모은다", {c: has(c) for c in CRYST}, frame="goal", req=[[c] for c in CRYST])
adv(f"{C}/aether", f"{C}/root", "aether_ingot", "하늘의 금속", "에테르 주괴를 얻는다", {"v": has("aether_ingot")})
adv(f"{C}/aether_armor", f"{C}/aether", "aether_chestplate", "에테르의 가호", "에테르 갑옷 한 벌을 모두 갖춘다",
    {"v": has("aether_helmet", "aether_chestplate", "aether_leggings", "aether_boots")})
adv(f"{C}/orichalcum", f"{C}/aether", "orichalcum_ingot", "전설의 금속", "오리하르콘 주괴를 얻는다", {"v": has("orichalcum_ingot")}, frame="goal")
adv(f"{C}/orichalcum_armor", f"{C}/orichalcum", "orichalcum_chestplate", "오리하르콘의 갑주", "오리하르콘 갑옷 한 벌을 모두 갖춘다",
    {"v": has("orichalcum_helmet", "orichalcum_chestplate", "orichalcum_leggings", "orichalcum_boots")}, frame="challenge")
adv(f"{C}/stone", f"{C}/root", "enhance_stone", "강화석", "강화석을 얻는다", {"v": has("enhance_stone")})
adv(f"{C}/enh5", f"{C}/stone", "enhance_stone", "날이 서다", "무기를 +5까지 강화한다")
adv(f"{C}/enh10", f"{C}/enh5", "bless_stone", "명검", "무기를 +10까지 강화한다", frame="goal")
adv(f"{C}/enh15", f"{C}/enh10", "bless_stone", "신의 무기", "무기를 최대치 +15까지 강화한다", frame="challenge")
adv(f"{C}/core", f"{C}/stone", "nox_core", "핵의 힘", "보스의 핵으로 개인 강화를 처음 한다", frame="goal")
adv(f"{C}/essence", f"{C}/core", "element_essence", "원소의 정수", "원소 정수를 얻는다", {"v": has("element_essence")}, frame="goal")
adv(f"{C}/rift", f"{C}/root", "rift_key", "균열의 열쇠", "균열의 열쇠를 손에 넣는다", {"v": has("rift_key")})
n = sum(len(fs) for _, _, fs in os.walk(OUT))
print("advancements", n)
