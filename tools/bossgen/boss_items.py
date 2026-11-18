"""보스 핵/소환석/원소 정수 아이템 텍스처 + 이름 + 레시피 생성"""
import json, math, random, sys, os, glob
from PIL import Image
root = sys.argv[1]
A = f"{root}/src/main/resources/assets/elementalweapons"; D = f"{root}/src/main/resources/data/elementalweapons/recipe"
def w(p, o): open(p, "w", encoding="utf-8").write(json.dumps(o, ensure_ascii=False, indent="\t") + "\n")
def lerp(a, b, t): t = max(0, min(1, t)); return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))
def hx(h): return ((h >> 16) & 255, (h >> 8) & 255, h & 255)
N = 32
# 아이디: (한국어, 영어, 핵 중심색, 핵 바깥색, 테두리색, 소환석 룬색, 소환 재료, 속성 무기 재료로 쓰는 속성)
BOSSES = {
 "ignis": ("화염룡 이그니스", "Ignis", 0xFFE080, 0xFF5A10, 0x8A2A10, 0xFF8A2A, "minecraft:blaze_rod", "fire"),
 "marina": ("심해의 여왕 마리나", "Marina", 0xE8FFFF, 0x2A8AFF, 0xF0C860, 0x6FD8FF, "minecraft:prismarine_shard", "water"),
 "silva": ("고대수 실바", "Silva", 0xFFE0F0, 0x3EA040, 0x6A4A20, 0x9CFF6A, "minecraft:moss_block", "grass"),
 "zephyros": ("폭풍새 제피로스", "Zephyros", 0xFFFFFF, 0x7FE8D8, 0xC0C8C8, 0xB8FFF0, "minecraft:breeze_rod", "wind"),
 "nox": ("그림자 군주 녹스", "Nox", 0xE0A0FF, 0x5A1A8A, 0x2A2030, 0xB060FF, "minecraft:echo_shard", "dark"),
 "lumiel": ("성기사 루미엘", "Lumiel", 0xFFFFFF, 0xFFD040, 0xE0B040, 0xFFE27A, "minecraft:glowstone", "light"),
 "crystal_golem": ("수정 골렘", "Crystal Golem", 0xFFE0FF, 0xD050E0, 0x6A6070, 0xFF9CFF, "elementalweapons:lumiel_core", None),
 "lava_queen": ("용암 거미 여왕", "Lava Queen", 0xFFC060, 0xC02010, 0x221A1A, 0xFF5A1A, "elementalweapons:ignis_core", None),
 "frost_giant": ("얼음 거인", "Frost Giant", 0xFFFFFF, 0x7FD0FF, 0xA8C8E0, 0x9FE8FF, "elementalweapons:marina_core", None),
 "sphinx": ("사막 스핑크스", "Sphinx", 0xFFF0B0, 0xE0A030, 0x2A4AA0, 0xFFD860, "elementalweapons:zephyros_core", None),
 "steam_colossus": ("증기 거신", "Steam Colossus", 0xFFE0C0, 0xC06A3A, 0x5A5E66, 0xFF8A2A, None, None),
 "storm_wyvern": ("뇌운 와이번", "Storm Wyvern", 0xE0FFFF, 0x3A5ACA, 0x1E2A5A, 0x9FF3FF, None, None),
 "twilight_twins": ("황혼의 쌍둥이", "Twilight Twins", 0xFFFFFF, 0x9A50D0, 0xF0C040, 0xD0A0FF, None, None),
 "hydra": ("독꽃 히드라", "Venom Hydra", 0xFFD0E8, 0x2A8A40, 0x1E4A2A, 0xFF6AB0, None, None),
 "prima": ("원소의 근원 프리마", "Prima", 0xFFFFFF, 0xC0C0FF, 0xF0F0F0, 0xFFFFFF, None, None),
}
def outline(img, col=(18, 12, 38, 255)):
    o = img.copy()
    for y in range(N):
        for x in range(N):
            if img.getpixel((x, y))[3] == 0 and any(0 <= x + a < N and 0 <= y + b < N and img.getpixel((x + a, y + b))[3] > 0 for a, b in ((1,0),(-1,0),(0,1),(0,-1))):
                o.putpixel((x, y), col)
    return o
def core(c_in, c_out, rim, rainbow=False):
    img = Image.new("RGBA", (N, N)); cx = cy = 15.5
    els = [0x9FF3FF, 0xFF7A1A, 0x3FA0FF, 0x7BD36B, 0xDFFFF6, 0x9A3BEA, 0xFFE27A]
    for y in range(N):
        for x in range(N):
            r = math.hypot(x - cx, y - cy)
            if r < 8.5:
                c = lerp(hx(c_in), hx(c_out), r / 7)
                if rainbow and r > 3:
                    a = (math.degrees(math.atan2(y - cy, x - cx)) % 360) / 360
                    c = lerp(c, hx(els[int(a * 7) % 7]), 0.6)
                if x < cx - 2 and y < cy - 2 and r < 5: c = lerp(c, (255, 255, 255), 0.5)
                img.putpixel((x, y), c + (255,))
            elif r < 11:
                ang = math.degrees(math.atan2(y - cy, x - cx)) % 360
                if r < 9.6 or int(ang / 30) % 2 == 0:
                    img.putpixel((x, y), lerp(lerp(hx(rim), (0, 0, 0), 0.4), lerp(hx(rim), (255, 255, 255), 0.3), (cy - y + 11) / 22) + (255,))
    return outline(img)
def stone(rune, seed):
    rnd = random.Random(seed); img = Image.new("RGBA", (N, N))
    for y in range(4, 29):
        for x in range(6, 26):
            if (x, y) in ((6,4),(25,4),(6,28),(25,28)): continue
            edge = x in (6, 25) or y in (4, 28)
            v = 60 + rnd.randint(-10, 10)
            img.putpixel((x, y), ((v, v + 4, v + 16) if not edge else (25, 22, 40)) + (255,))
    rc = hx(rune); dark = lerp(rc, (0, 0, 0), 0.4)
    # 룬: 원 + 세로선 (보스마다 모양 조금씩 다르게)
    for t in range(0, 360, 8):
        x = int(15.5 + math.cos(math.radians(t)) * 6); y = int(16 + math.sin(math.radians(t)) * 6)
        img.putpixel((x, y), rc + (255,))
    k = seed % 4
    for y in range(9, 24):
        img.putpixel((15 + (k == 1) * ((y % 4) - 1), y), rc + (255,)); img.putpixel((16, y), dark + (255,))
    for x in range(10, 22):
        if k >= 2: img.putpixel((x, 16 + (k == 3) * ((x % 3) - 1)), rc + (255,))
    for (x, y) in [(9, 7), (22, 7), (9, 25), (22, 25)]: img.putpixel((x, y), (255, 215, 90, 255))
    return img
ko = {}; en = {}
for i, (bid, (k, e, ci, co, rim, rune, mat, weapon_el)) in enumerate(BOSSES.items()):
    core(ci, co, rim, rainbow=(bid == "prima")).save(f"{A}/textures/item/{bid}_core.png")
    stone(rune, i + 3).save(f"{A}/textures/item/{bid}_summon.png")
    for n in (f"{bid}_core", f"{bid}_summon"):
        w(f"{A}/models/item/{n}.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"elementalweapons:item/{n}"}})
        w(f"{A}/items/{n}.json", {"model": {"type": "minecraft:model", "model": f"elementalweapons:item/{n}"}})
    ko[f"entity.elementalweapons.{bid}"] = k; en[f"entity.elementalweapons.{bid}"] = e
    short = k.split(" ")[-1]
    ko[f"item.elementalweapons.{bid}_core"] = f"{short}의 핵"; en[f"item.elementalweapons.{bid}_core"] = f"{e}'s Core"
    ko[f"item.elementalweapons.{bid}_summon"] = f"{short}의 소환석"; en[f"item.elementalweapons.{bid}_summon"] = f"{e} Summoning Stone"
# 원소 정수
core(0xFFFFFF, 0xB0A0FF, 0xF0C040, rainbow=True).save(f"{A}/textures/item/element_essence.png")
w(f"{A}/models/item/element_essence.json", {"parent": "minecraft:item/generated", "textures": {"layer0": "elementalweapons:item/element_essence"}})
w(f"{A}/items/element_essence.json", {"model": {"type": "minecraft:model", "model": "elementalweapons:item/element_essence"}})
ko["item.elementalweapons.element_essence"] = "원소 정수"; en["item.elementalweapons.element_essence"] = "Elemental Essence"
for lang, d in (("ko_kr", ko), ("en_us", en)):
    p = f"{A}/lang/{lang}.json"; cur = json.load(open(p, encoding="utf-8")); cur.update(d); w(p, cur)
# ----- 레시피 -----
def shaped(name, pattern, key, result):
    w(f"{D}/{name}.json", {"type": "minecraft:crafting_shaped", "category": "misc", "pattern": pattern, "key": key, "result": {"id": result, "count": 1}})
def shapeless(name, ings, result):
    w(f"{D}/{name}.json", {"type": "minecraft:crafting_shapeless", "category": "misc", "ingredients": ings, "result": {"id": result, "count": 1}})
for bid, (k, e, ci, co, rim, rune, mat, weapon_el) in BOSSES.items():
    if mat and mat.startswith("minecraft:"):   # 수호자: 속성 재료 + 다이아 블록
        shaped(f"{bid}_summon", [" X ", "XBX", " X "], {"X": mat, "B": "minecraft:diamond_block"}, f"elementalweapons:{bid}_summon")
    elif mat:                                  # 지역 지배자: 같은 속성 수호자의 핵 + 다이아
        shaped(f"{bid}_summon", [" D ", "DCD", " D "], {"C": mat, "D": "minecraft:diamond"}, f"elementalweapons:{bid}_summon")
C = lambda b: f"elementalweapons:{b}_core"
shapeless("steam_colossus_summon", [C("ignis"), C("marina"), C("lava_queen"), C("frost_giant"), "minecraft:diamond_block"], "elementalweapons:steam_colossus_summon")
shapeless("storm_wyvern_summon", [C("voltra"), C("zephyros"), C("sphinx"), "minecraft:diamond_block"], "elementalweapons:storm_wyvern_summon")
shapeless("twilight_twins_summon", [C("lumiel"), C("nox"), C("crystal_golem"), "minecraft:diamond_block"], "elementalweapons:twilight_twins_summon")
shapeless("hydra_summon", [C("silva"), C("nox"), C("lava_queen"), "minecraft:emerald_block"], "elementalweapons:hydra_summon")
shapeless("prima_summon", [C("steam_colossus"), C("storm_wyvern"), C("twilight_twins"), C("hydra"),
                           "elementalweapons:element_essence", "elementalweapons:element_essence", "elementalweapons:element_essence",
                           "elementalweapons:element_essence", "minecraft:nether_star"], "elementalweapons:prima_summon")
# ----- 속성 무기 재료 = 그 속성 수호자의 핵 -----
guardian = {"fire": "ignis", "water": "marina", "grass": "silva", "wind": "zephyros", "dark": "nox", "light": "lumiel"}
for el, b in guardian.items():
    for f in glob.glob(f"{D}/{el}_*.json"):
        if f.endswith("_summon.json"): continue
        d = json.load(open(f, encoding="utf-8"))
        d["key"]["E"] = C(b)
        w(f, d)
print("items ok", len(BOSSES))
