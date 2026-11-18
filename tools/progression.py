"""성장 구조: 도구/갑옷 리소스, 무기/소환석 레시피를 새 세계 재료로 연결. 사용법: python3 tools/progression.py <프로젝트 루트>"""
import json, os, sys, glob
from PIL import Image
ROOT = sys.argv[1]; NS = "elementalweapons"
A = f"{ROOT}/src/main/resources/assets/{NS}"; D = f"{ROOT}/src/main/resources/data/{NS}"; MC = f"{ROOT}/src/main/resources/data/minecraft"
def w(p, o):
    os.makedirs(os.path.dirname(p), exist_ok=True); open(p, "w", encoding="utf-8").write(json.dumps(o, ensure_ascii=False, indent="\t") + "\n")
def hx(h): return ((h >> 16) & 255, (h >> 8) & 255, h & 255)
def mix(a, b, t): return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))
TIERS = {"aether": ("에테르", 0xBFE8FF, 0x6AA8D0), "orichalcum": ("오리하르콘", 0xFFC070, 0xC07A30)}
# 16x16 도안: X=재료 밝음, x=재료 어두움, h=손잡이, o=테두리
SHAPES = {
"sword": ["............ooo.","...........oXXo.","..........oXXxo.",".........oXXxo..","........oXXxo...",".......oXXxo....","..oo..oXXxo.....","..oXooXXxo......","...oXXXxo.......","....oXxo........","...ohoXXo.......","..ohho.oXo......",".ohho...oo......","ohho............","ooo.............","................"],
"pickaxe": ["................","....ooooooo.....","...oXXXXXXXo....","..oXxooooxXXo...","..oXo...ohoxXo..",".oXo...ohho.oXo.",".oo...ohho...oo.",".....ohho.......","....ohho........","...ohho.........","..ohho..........",".ohho...........","ohho............","oho.............","oo..............","................"],
"axe": ["................",".......ooo......","......oXXXo.....",".....oXXXXXo....","....oXXxohXo....","....oXxohho.....",".....ooohho.....","......ohho......",".....ohho.......","....ohho........","...ohho.........","..ohho..........",".ohho...........","ohho............","oo..............","................"],
"shovel": ["................","..........ooo...",".........oXXXo..","........oXXXXo..",".........oXXXo..","........ohoXo...",".......ohho.o...","......ohho......",".....ohho.......","....ohho........","...ohho.........","..ohho..........",".ohho...........","ohho............","oo..............","................"],
"hoe": ["................","......oooooo....",".....oXXXXXXo...","......oooohXo...",".........ohho...","........ohho....",".......ohho.....","......ohho......",".....ohho.......","....ohho........","...ohho.........","..ohho..........",".ohho...........","ohho............","oo..............","................"],
"helmet": ["................","................","................","....oooooooo....","...oXXXXXXXXo...","..oXXxxxxxxXXo..","..oXo......oXo..","..oXo......oXo..","..ooo......ooo..","................","................","................","................","................","................","................"],
"chestplate": ["................","..ooo......ooo..","..oXXo....oXXo..","..oXXXooooXXXo..","..oXXXXXXXXXXo..","...oXXXXXXXXo...","...oXxXXXXxXo...","...oXXXXXXXXo...","...oXXXXXXXXo...","...oXxXXXXxXo...","...oXXXXXXXXo...","...oooooooooo...","................","................","................","................"],
"leggings": ["................","...oooooooooo...","...oXXXXXXXXo...","...oXxxxxxxXo...","...oXXoooOXXo...","...oXXo..oXXo...","...oXXo..oXXo...","...oXXo..oXXo...","...oXxo..oxXo...","...oXXo..oXXo...","...oXXo..oXXo...","...oooo..oooo...","................","................","................","................"],
"boots": ["................","................","................","................","................","................","...oooo..oooo...","...oXXo..oXXo...","...oXXo..oXXo...","...oXXo..oXXo...","..oXXXo..oXXXo..","..oXxxo..oxxXo..","..ooooo..ooooo..","................","................","................"],
}
lang = {}
for tier, (ko, light, dark) in TIERS.items():
    for kind, rows in SHAPES.items():
        im = Image.new("RGBA", (16, 16))
        for y, row in enumerate(rows):
            for x, ch in enumerate(row[:16]):
                c = {"X": hx(light), "x": hx(dark), "O": hx(dark), "h": (110, 72, 40), "o": (24, 18, 30)}.get(ch)
                if c: im.putpixel((x, y), c + (255,))
        os.makedirs(f"{A}/textures/item", exist_ok=True)
        im.save(f"{A}/textures/item/{tier}_{kind}.png")
        model = "minecraft:item/handheld" if kind in ("sword", "pickaxe", "axe", "shovel", "hoe") else "minecraft:item/generated"
        w(f"{A}/models/item/{tier}_{kind}.json", {"parent": model, "textures": {"layer0": f"{NS}:item/{tier}_{kind}"}})
        w(f"{A}/items/{tier}_{kind}.json", {"model": {"type": "minecraft:model", "model": f"{NS}:item/{tier}_{kind}"}})
        kko = {"sword": "검", "pickaxe": "곡괭이", "axe": "도끼", "shovel": "삽", "hoe": "괭이", "helmet": "투구", "chestplate": "흉갑", "leggings": "각반", "boots": "부츠"}[kind]
        lang[f"item.{NS}.{tier}_{kind}"] = f"{ko} {kko}"
    # 갑옷 착용 텍스처 (64x32) + 장비 에셋
    for layer in ("humanoid", "humanoid_leggings"):
        im = Image.new("RGBA", (64, 32))
        for y in range(32):
            for x in range(64):
                c = hx(light) if (x + y) % 6 else hx(dark)
                if x % 8 in (0, 7) or y % 8 in (0, 7): c = mix(c, (0, 0, 0), 0.35)
                im.putpixel((x, y), c + (255,))
        os.makedirs(f"{A}/textures/entity/equipment/{layer}", exist_ok=True)
        im.save(f"{A}/textures/entity/equipment/{layer}/{tier}.png")
    w(f"{A}/equipment/{tier}.json", {"layers": {"humanoid": [{"texture": f"{NS}:{tier}"}], "humanoid_leggings": [{"texture": f"{NS}:{tier}"}]}})
    # 레시피
    ing = f"{NS}:{tier}_ingot"
    pats = {"sword": ["X", "X", "S"], "pickaxe": ["XXX", " S ", " S "], "axe": ["XX", "XS", " S"], "shovel": ["X", "S", "S"], "hoe": ["XX", " S", " S"],
            "helmet": ["XXX", "X X"], "chestplate": ["X X", "XXX", "XXX"], "leggings": ["XXX", "X X", "X X"], "boots": ["X X", "X X"]}
    for kind, pat in pats.items():
        key = {"X": ing}
        if "S" in "".join(pat): key["S"] = "minecraft:stick"
        w(f"{D}/recipe/{tier}_{kind}.json", {"type": "minecraft:crafting_shaped", "category": "equipment", "pattern": pat, "key": key, "result": {"id": f"{NS}:{tier}_{kind}", "count": 1}})
    w(f"{D}/tags/item/{tier}_tool_materials.json", {"replace": False, "values": [ing]})
# 바닐라 장비 태그 (마법부여 등)
for tagname, kind in (("swords", "sword"), ("pickaxes", "pickaxe"), ("axes", "axe"), ("shovels", "shovel"), ("hoes", "hoe"),
                      ("head_armor", "helmet"), ("chest_armor", "chestplate"), ("leg_armor", "leggings"), ("foot_armor", "boots")):
    p = f"{MC}/tags/item/{tagname}.json"
    cur = json.load(open(p)) if os.path.exists(p) else {"replace": False, "values": []}
    for tier in TIERS:
        v = f"{NS}:{tier}_{kind}"
        if v not in cur["values"]: cur["values"].append(v)
    w(p, cur)
# ---------- 무기 레시피: 수호자의 핵 + 오리하르콘 + 속성 결정 ----------
CRYSTAL = {"electric": "storm_crystal", "fire": "ember_crystal", "water": "tide_crystal", "grass": "bloom_crystal", "wind": "gale_crystal", "dark": "umbral_crystal", "light": "radiant_crystal"}
CORE = {"electric": "voltra", "fire": "ignis", "water": "marina", "grass": "silva", "wind": "zephyros", "dark": "nox", "light": "lumiel"}
O = f"{NS}:orichalcum_ingot"
for el in CRYSTAL:
    c, x = f"{NS}:{CORE[el]}_core", f"{NS}:{CRYSTAL[el]}"
    rec = {"dagger": ([" O", "C "], {"O": O, "C": c}), "sword": ["C", "O", "S"], "greatsword": [" O ", "XOX", "CSC"], "staff": ["XCX", " S ", " S "]}
    for wt, spec in rec.items():
        if isinstance(spec, tuple): pat, key = spec
        else:
            pat = spec; key = {}
            for ch, val in (("C", c), ("O", O), ("S", "minecraft:stick"), ("X", x)):
                if ch in "".join(pat): key[ch] = val
        w(f"{D}/recipe/{el}_{wt}.json", {"type": "minecraft:crafting_shaped", "category": "equipment", "pattern": pat, "key": key, "result": {"id": f"{NS}:{el}_{wt}", "count": 1}})
# ---------- 소환석: 새 세계 재료 ----------
SUMMON_EL = {"voltra": "electric", "ignis": "fire", "marina": "water", "silva": "grass", "zephyros": "wind", "nox": "dark", "lumiel": "light"}
for b, el in SUMMON_EL.items():
    w(f"{D}/recipe/{b}_summon.json", {"type": "minecraft:crafting_shaped", "category": "misc", "pattern": [" X ", "XOX", " X "],
        "key": {"X": f"{NS}:{CRYSTAL[el]}", "O": O}, "result": {"id": f"{NS}:{b}_summon", "count": 1}})
for f in glob.glob(f"{D}/recipe/*_summon.json"):
    d = json.load(open(f, encoding="utf-8"))
    s = json.dumps(d).replace('"minecraft:diamond_block"', f'"{O}"').replace('"minecraft:emerald_block"', f'"{O}"').replace('"minecraft:diamond"', f'"{NS}:aether_ingot"')
    w(f, json.loads(s))
# ---------- 농사용: 원소 흙 → 바닐라 흙 (괭이로 경작 가능) ----------
w(f"{D}/recipe/dirt_from_elemental_dirt.json", {"type": "minecraft:crafting_shapeless", "category": "building", "ingredients": [f"{NS}:elemental_dirt"], "result": {"id": "minecraft:dirt", "count": 1}})
for lang_code in ("ko_kr", "en_us"):
    p = f"{A}/lang/{lang_code}.json"; d = json.load(open(p, encoding="utf-8"))
    for k, v in lang.items(): d[k] = v if lang_code == "ko_kr" else k.split(".")[-1].replace("_", " ").title()
    w(p, d)
print("progression ok", len(lang))
