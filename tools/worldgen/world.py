"""
원소 세계 생성기: 블록 목록 하나로 자바 등록 코드 + 텍스처 + 모델 + 드롭 + 태그 + 지형 생성 JSON 생성
사용법: python3 tools/worldgen/world.py <프로젝트 루트> <바닐라 데이터 폴더>
"""
import json, math, os, random, sys, copy
from PIL import Image
ROOT, VAN = sys.argv[1], sys.argv[2]
NS = "elementalweapons"
RES = f"{ROOT}/src/main/resources"; A = f"{RES}/assets/{NS}"; D = f"{RES}/data/{NS}"; MC = f"{RES}/data/minecraft"
def w(p, o):
    os.makedirs(os.path.dirname(p), exist_ok=True)
    open(p, "w", encoding="utf-8").write(json.dumps(o, ensure_ascii=False, indent="\t") + "\n")
def hx(h): return ((h >> 16) & 255, (h >> 8) & 255, h & 255)
def mix(a, b, t): return tuple(int(a[i] + (b[i] - a[i]) * max(0, min(1, t))) for i in range(3))
def jit(c, r, a): return tuple(max(0, min(255, c[i] + r.randint(-a, a))) for i in range(3))

# ======================= 7개 숲/바이옴 테마 =======================
# key: (바이옴 id, 바이옴 한국어, 원소, 나무 한국어, 흙 블록 id, 흙 한국어, 흙 윗면색, 나무껍질색, 나무속색, 잎색, 잎 포인트색, 광석 id, 결정 id, 결정 한국어, 결정색, 잎 발광)
THEMES = [
 ("storm",   "thunder_plateau", "뇌전 고원",   "electric", "뇌전",  "storm_grass",   "뇌전 잔디",   0x3A6A9A, 0x2A2F4A, 0x8A9AC0, 0x2E5AA0, 0x9FF3FF, "storm_ore",  "storm_crystal",  "뇌전 결정", 0x9FF3FF, False),
 ("ember",   "volcanic_plains", "화산 평원",   "fire",     "불꽃",  "ember_soil",    "잿빛 흙",     0x4A3A34, 0x3A1A12, 0xC86A3A, 0xC8501A, 0xFFB040, "ember_ore",  "ember_crystal",  "불꽃 결정", 0xFF7A1A, False),
 ("azure",   "azure_forest",    "청해의 숲",   "water",    "청해",  "azure_grass",   "청해 잔디",   0x2A9AA0, 0x2A3A4A, 0x9AC8D8, 0x2A8AC8, 0xBFF0FF, "tide_ore",   "tide_crystal",   "조류 결정", 0x3FA0FF, False),
 ("blossom", "blossom_forest",  "꽃의 숲",     "grass",    "꽃",    "blossom_grass", "꽃잔디",      0x4AA04A, 0x5A3A2A, 0xE8C8A8, 0xF08AC0, 0xFFE0F0, "bloom_ore",  "bloom_crystal",  "개화 결정", 0x7BD36B, False),
 ("gale",    "gale_highlands",  "바람의 고원", "wind",     "바람",  "gale_grass",    "바람 이끼",   0x8AC8B8, 0x6A7A7A, 0xD8E8E0, 0x6AB8A0, 0xE8FFF8, "gale_ore",   "gale_crystal",   "질풍 결정", 0xDFFFF6, False),
 ("shadow",  "shadow_woods",    "그림자 숲",   "dark",     "그림자", "shadow_grass",  "그림자 흙",   0x2A1A3A, 0x1A1222, 0x5A3A6A, 0x3A1A5A, 0xB060FF, "umbral_ore", "umbral_crystal", "그림자 결정", 0x9A3BEA, True),
 ("radiant", "radiant_plains",  "빛의 평원",   "light",    "빛",    "radiant_grass", "빛 잔디",     0xE8D070, 0xC8B080, 0xF8F0D8, 0xF8E070, 0xFFFFFF, "radiant_ore","radiant_crystal","광휘 결정", 0xFFE27A, True),
]
OCEAN = ("elemental_ocean", "원소의 바다")

# ======================= 블록 목록 =======================
BLOCKS = []   # (id, 한국어, 종류, 추가정보)
BLOCKS += [("aether_stone", "원소석", "stone", {}), ("abyss_stone", "심연석", "deepstone", {}),
           ("elemental_dirt", "원소 흙", "dirt", {}), ("elemental_sand", "원소 모래", "sand", {})]
for t in THEMES:
    key, biome, bko, el, wko, grass, gko = t[:7]
    BLOCKS.append((grass, gko, "grass", {"theme": t}))
    BLOCKS.append((f"{key}_log", f"{wko} 나무", "log", {"theme": t}))
    BLOCKS.append((f"{key}_planks", f"{wko} 판자", "planks", {"theme": t}))
    BLOCKS.append((f"{key}_leaves", f"{wko} 나뭇잎", "leaves", {"theme": t, "glow": t[16]}))
    BLOCKS.append((t[12], f"{t[14]} 원석", "ore", {"theme": t, "drop": t[13], "color": t[15], "xp": (3, 7)}))
BLOCKS.append(("aether_ore", "에테르 원석", "ore", {"drop": "raw_aether", "color": 0xC8F0FF, "xp": (0, 2), "stone_tool": True}))
BLOCKS.append(("orichalcum_ore", "오리하르콘 원석", "deepore", {"drop": "orichalcum_chunk", "color": 0xFFB060, "xp": (4, 9)}))

# 재료 아이템 (광석 드롭 + 제련 결과)
MATERIALS = [(t[13], t[14], t[15]) for t in THEMES] + [
    ("raw_aether", "에테르 원석 덩어리", 0xA8D8F0), ("aether_ingot", "에테르 주괴", 0xD8F4FF),
    ("orichalcum_chunk", "오리하르콘 조각", 0xE89040), ("orichalcum_ingot", "오리하르콘 주괴", 0xFFC070)]

# ======================= 텍스처 (16x16) =======================
def tex_stone(base, seed, cracks=0.12):
    r = random.Random(seed); im = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            c = jit(base, r, 10)
            if r.random() < cracks: c = mix(c, (0, 0, 0), 0.3)
            if (x * 7 + y * 5) % 23 == 0: c = mix(c, (255, 255, 255), 0.15)
            im.putpixel((x, y), c + (255,))
    return im
def tex_ore(base_im, color, seed):
    r = random.Random(seed); im = base_im.copy(); c = hx(color)
    spots = [(3, 4), (10, 3), (6, 9), (12, 11), (2, 12), (9, 7)]
    for (sx, sy) in spots:
        for dx, dy in ((0, 0), (1, 0), (0, 1), (1, 1)):
            if r.random() < 0.85:
                im.putpixel((sx + dx, sy + dy), (mix(c, (255, 255, 255), 0.3) if (dx + dy) == 0 else mix(c, (0, 0, 0), 0.25)) + (255,))
    return im
def tex_grass_top(col, seed):
    r = random.Random(seed); im = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            c = jit(hx(col), r, 12)
            if r.random() < 0.1: c = mix(c, (255, 255, 255), 0.18)
            im.putpixel((x, y), c + (255,))
    return im
def tex_grass_side(col, dirt_im, seed):
    r = random.Random(seed); im = dirt_im.copy()
    for x in range(16):
        h = 3 + r.randint(0, 2)
        for y in range(h): im.putpixel((x, y), jit(hx(col), r, 10) + (255,))
    return im
def tex_log_side(bark, seed):
    r = random.Random(seed); im = Image.new("RGBA", (16, 16)); b = hx(bark)
    for y in range(16):
        for x in range(16):
            c = jit(b, r, 8)
            if x % 4 == 0: c = mix(c, (0, 0, 0), 0.3)
            if r.random() < 0.06: c = mix(c, (255, 255, 255), 0.15)
            im.putpixel((x, y), c + (255,))
    return im
def tex_log_top(bark, inner, seed):
    r = random.Random(seed); im = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            d = max(abs(x - 7.5), abs(y - 7.5))
            if d > 6.5: c = jit(hx(bark), r, 8)
            else: c = mix(hx(inner), (0, 0, 0), 0.18 if int(d) % 2 == 0 else 0.0); c = jit(c, r, 5)
            im.putpixel((x, y), c + (255,))
    return im
def tex_planks(inner, seed):
    r = random.Random(seed); im = Image.new("RGBA", (16, 16)); b = mix(hx(inner), (0, 0, 0), 0.1)
    for y in range(16):
        for x in range(16):
            c = jit(b, r, 6)
            if y % 4 == 3: c = mix(c, (0, 0, 0), 0.35)
            if (y // 4) % 2 == 0 and x == 7 or (y // 4) % 2 == 1 and x == 13: c = mix(c, (0, 0, 0), 0.3)
            im.putpixel((x, y), c + (255,))
    return im
def tex_leaves(col, accent, seed):
    r = random.Random(seed); im = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            c = jit(hx(col), r, 14)
            if r.random() < 0.18: c = mix(c, (0, 0, 0), 0.3)
            if r.random() < 0.06: c = hx(accent)
            im.putpixel((x, y), c + (255,))
    return im
def tex_item(color, kind, seed):
    """재료 아이템: 결정(gem) / 덩어리(raw) / 주괴(ingot)"""
    im = Image.new("RGBA", (16, 16)); c = hx(color); r = random.Random(seed)
    if kind == "ingot":
        for y in range(5, 12):
            for x in range(2 + (11 - y) // 2, 14 - (y - 5) // 2):
                cc = mix(c, (255, 255, 255), 0.35) if y == 5 else (mix(c, (0, 0, 0), 0.35) if y == 11 else c)
                im.putpixel((x, y), cc + (255,))
    elif kind == "raw":
        for y in range(3, 14):
            for x in range(3, 13):
                if math.hypot(x - 7.5, y - 8) < 5 + r.random():
                    im.putpixel((x, y), (mix(c, (60, 60, 60), 0.35) if r.random() < 0.3 else c) + (255,))
    else:
        for y in range(1, 15):
            for x in range(1, 15):
                if abs(x - 7.5) + abs(y - 7.5) * 0.8 < 6.5:
                    cc = mix(c, (255, 255, 255), 0.5) if x < 7 and y < 7 else (mix(c, (0, 0, 0), 0.3) if x + y > 18 else c)
                    im.putpixel((x, y), cc + (255,))
    o = im.copy()
    for y in range(16):
        for x in range(16):
            if im.getpixel((x, y))[3] == 0 and any(0 <= x + a < 16 and 0 <= y + b < 16 and im.getpixel((x + a, y + b))[3] > 0 for a, b in ((1,0),(-1,0),(0,1),(0,-1))):
                o.putpixel((x, y), (20, 16, 30, 255))
    return o

os.makedirs(f"{A}/textures/block", exist_ok=True); os.makedirs(f"{A}/textures/item", exist_ok=True)
STONE = tex_stone((120, 118, 140), 1); STONE.save(f"{A}/textures/block/aether_stone.png")
DEEP = tex_stone((52, 46, 66), 2, 0.2); DEEP.save(f"{A}/textures/block/abyss_stone.png")
DIRT = tex_stone((110, 84, 70), 3, 0.06); DIRT.save(f"{A}/textures/block/elemental_dirt.png")
tex_stone((214, 206, 176), 4, 0.04).save(f"{A}/textures/block/elemental_sand.png")

def bs_simple(bid): w(f"{A}/blockstates/{bid}.json", {"variants": {"": {"model": f"{NS}:block/{bid}"}}})
def item_model(bid): w(f"{A}/items/{bid}.json", {"model": {"type": "minecraft:model", "model": f"{NS}:block/{bid}"}})
def loot_self(bid): w(f"{D}/loot_table/blocks/{bid}.json", {"type": "minecraft:block", "pools": [{"rolls": 1, "entries": [{"type": "minecraft:item", "name": f"{NS}:{bid}"}], "conditions": [{"condition": "minecraft:survives_explosion"}]}]})
def loot_drop(bid, item, count=None, silk=True):
    e = {"type": "minecraft:item", "name": f"{NS}:{item}"}
    if count: e["functions"] = [{"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": count[0], "max": count[1]}}]
    ent = [{"type": "minecraft:alternatives", "children": [{"type": "minecraft:item", "condition": "minecraft:tool/can_silk_touch", "name": f"{NS}:{bid}"}, e]}] if silk else [e]
    w(f"{D}/loot_table/blocks/{bid}.json", {"type": "minecraft:block", "pools": [{"rolls": 1, "entries": ent}]})

tags = {k: [] for k in ["mineable/pickaxe", "mineable/shovel", "mineable/axe", "mineable/hoe", "needs_stone_tool", "needs_iron_tool",
                        "logs", "logs_that_burn", "planks", "leaves", "dirt", "base_stone_overworld", "overworld_carver_replaceables", "stone_ore_replaceables"]}
item_tags = {k: [] for k in ["planks", "logs", "logs_that_burn", "leaves", "stone_tool_materials", "stone_crafting_materials", "dirt"]}
lang = {}
recipes = {}

for bid, ko, kind, info in BLOCKS:
    lang[f"block.{NS}.{bid}"] = ko
    full = f"{NS}:{bid}"
    t = info.get("theme")
    if kind in ("stone", "deepstone", "dirt", "sand"):
        w(f"{A}/models/block/{bid}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"{NS}:block/{bid}"}})
        bs_simple(bid); item_model(bid); loot_self(bid)
        tags["overworld_carver_replaceables"].append(full)
        if kind in ("stone", "deepstone"):
            tags["mineable/pickaxe"].append(full); tags["base_stone_overworld"].append(full); tags["stone_ore_replaceables"].append(full)
            item_tags["stone_tool_materials"].append(full); item_tags["stone_crafting_materials"].append(full)
        else:
            tags["mineable/shovel"].append(full)
            if kind == "dirt": tags["dirt"].append(full); item_tags["dirt"].append(full)
    elif kind == "grass":
        tex_grass_top(t[7], hash(bid) % 999).save(f"{A}/textures/block/{bid}_top.png")
        tex_grass_side(t[7], DIRT, hash(bid) % 777).save(f"{A}/textures/block/{bid}_side.png")
        w(f"{A}/models/block/{bid}.json", {"parent": "minecraft:block/cube_bottom_top", "textures": {"top": f"{NS}:block/{bid}_top", "side": f"{NS}:block/{bid}_side", "bottom": f"{NS}:block/elemental_dirt"}})
        bs_simple(bid); item_model(bid)
        loot_drop(bid, "elemental_dirt")
        tags["mineable/shovel"].append(full); tags["dirt"].append(full); tags["overworld_carver_replaceables"].append(full); item_tags["dirt"].append(full)
    elif kind == "log":
        tex_log_side(t[8], hash(bid) % 999).save(f"{A}/textures/block/{bid}.png")
        tex_log_top(t[8], t[9], hash(bid) % 555).save(f"{A}/textures/block/{bid}_top.png")
        w(f"{A}/models/block/{bid}.json", {"parent": "minecraft:block/cube_column", "textures": {"end": f"{NS}:block/{bid}_top", "side": f"{NS}:block/{bid}"}})
        w(f"{A}/models/block/{bid}_horizontal.json", {"parent": "minecraft:block/cube_column_horizontal", "textures": {"end": f"{NS}:block/{bid}_top", "side": f"{NS}:block/{bid}"}})
        w(f"{A}/blockstates/{bid}.json", {"variants": {"axis=y": {"model": f"{NS}:block/{bid}"},
            "axis=z": {"model": f"{NS}:block/{bid}_horizontal", "x": 90}, "axis=x": {"model": f"{NS}:block/{bid}_horizontal", "x": 90, "y": 90}}})
        item_model(bid); loot_self(bid)
        for k in ("mineable/axe", "logs", "logs_that_burn"): tags[k].append(full)
        item_tags["logs"].append(full); item_tags["logs_that_burn"].append(full)
        recipes[f"{t[0]}_planks_from_log"] = {"type": "minecraft:crafting_shapeless", "category": "building", "group": "planks",
                                              "ingredients": [full], "result": {"id": f"{NS}:{t[0]}_planks", "count": 4}}
    elif kind == "planks":
        tex_planks(t[9], hash(bid) % 999).save(f"{A}/textures/block/{bid}.png")
        w(f"{A}/models/block/{bid}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"{NS}:block/{bid}"}})
        bs_simple(bid); item_model(bid); loot_self(bid)
        tags["mineable/axe"].append(full); tags["planks"].append(full); item_tags["planks"].append(full)
    elif kind == "leaves":
        tex_leaves(t[10], t[11], hash(bid) % 999).save(f"{A}/textures/block/{bid}.png")
        w(f"{A}/models/block/{bid}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"{NS}:block/{bid}"}})
        bs_simple(bid); item_model(bid)
        w(f"{D}/loot_table/blocks/{bid}.json", {"type": "minecraft:block", "pools": [{"rolls": 1, "entries": [{"type": "minecraft:alternatives", "children": [
            {"type": "minecraft:item", "conditions": [{"condition": "minecraft:any_of", "terms": [{"condition": "minecraft:match_tool", "predicate": {"items": "minecraft:shears"}}, {"condition": "minecraft:tool/can_silk_touch"}]}], "name": full},
            {"type": "minecraft:item", "conditions": [{"condition": "minecraft:random_chance", "chance": 0.15}], "name": "minecraft:stick"}]}]}]})
        tags["mineable/hoe"].append(full); tags["leaves"].append(full); item_tags["leaves"].append(full)
    elif kind in ("ore", "deepore"):
        base = DEEP if kind == "deepore" else STONE
        tex_ore(base, info["color"], hash(bid) % 999).save(f"{A}/textures/block/{bid}.png")
        w(f"{A}/models/block/{bid}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"{NS}:block/{bid}"}})
        bs_simple(bid); item_model(bid)
        loot_drop(bid, info["drop"], (1, 2) if kind == "ore" and not info.get("stone_tool") else None)
        tags["mineable/pickaxe"].append(full)
        tags["needs_stone_tool" if info.get("stone_tool") else "needs_iron_tool"].append(full)

for mid, ko, col in MATERIALS:
    kind = "ingot" if mid.endswith("ingot") else ("raw" if mid.startswith("raw") or mid.endswith("chunk") else "gem")
    tex_item(col, kind, hash(mid) % 999).save(f"{A}/textures/item/{mid}.png")
    w(f"{A}/models/item/{mid}.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/{mid}"}})
    w(f"{A}/items/{mid}.json", {"model": {"type": "minecraft:model", "model": f"{NS}:item/{mid}"}})
    lang[f"item.{NS}.{mid}"] = ko

# 제련 + 바닐라 호환 레시피
for src, dst in (("raw_aether", "aether_ingot"), ("orichalcum_chunk", "orichalcum_ingot")):
    for typ, t in (("smelting", 200), ("blasting", 100)):
        recipes[f"{dst}_from_{typ}"] = {"type": f"minecraft:{typ}", "category": "misc", "ingredient": f"{NS}:{src}", "result": {"id": f"{NS}:{dst}"}, "experience": 0.8, "cookingtime": t}
recipes["iron_ingot_from_aether"] = {"type": "minecraft:crafting_shapeless", "category": "misc", "ingredients": [f"{NS}:aether_ingot"], "result": {"id": "minecraft:iron_ingot", "count": 1}}
recipes["diamond_from_orichalcum"] = {"type": "minecraft:crafting_shapeless", "category": "misc", "ingredients": [f"{NS}:orichalcum_ingot"], "result": {"id": "minecraft:diamond", "count": 1}}
for rid, r in recipes.items(): w(f"{D}/recipe/{rid}.json", r)

for k, v in tags.items(): w(f"{MC}/tags/block/{k}.json", {"replace": False, "values": v})
for k, v in item_tags.items(): w(f"{MC}/tags/item/{k}.json", {"replace": False, "values": v})

# ======================= 지형 생성 =======================
WG = f"{D}/worldgen"
# 1) 노이즈 설정: 바닐라 '큰 바이옴' 지형 + 우리 기본 돌 + 우리 지표면 규칙
ns = json.load(open(f"{VAN}/data/minecraft/worldgen/noise_settings/large_biomes.json"))
ns["default_block"] = f"{NS}:aether_stone"
ns["material_rule"] = f"{NS}:elemental"
w(f"{WG}/noise_settings/elemental.json", ns)
# 2) 지표면 규칙
def block(b): return {"type": "minecraft:block", "result_state": b}
def cond(c, then): return {"type": "minecraft:condition", "if_true": c, "then_run": then}
top_seq, under_seq = [], []
for t in THEMES:
    top_seq.append(cond({"type": "minecraft:biome", "biome_is": f"{NS}:{t[1]}"},
                        {"type": "minecraft:sequence", "sequence": [cond("minecraft:not_underwater", block(f"{NS}:{t[5]}")), block(f"{NS}:elemental_sand")]}))
top_seq.append(cond({"type": "minecraft:biome", "biome_is": f"{NS}:{OCEAN[0]}"}, block(f"{NS}:elemental_sand")))
top_seq.append(block(f"{NS}:elemental_dirt"))
under_seq = [cond({"type": "minecraft:biome", "biome_is": f"{NS}:{OCEAN[0]}"}, block(f"{NS}:elemental_sand")), block(f"{NS}:elemental_dirt")]
rule = {"type": "minecraft:sequence", "sequence": [
    "minecraft:bedrock_floor",
    cond({"type": "minecraft:above_preliminary_surface"}, {"type": "minecraft:sequence", "sequence": [
        cond("minecraft:on_floor", {"type": "minecraft:sequence", "sequence": top_seq}),
        cond("minecraft:under_floor", {"type": "minecraft:sequence", "sequence": under_seq})]}),
    cond({"type": "minecraft:vertical_gradient", "false_at_and_above": {"absolute": 8}, "random_name": f"{NS}:abyss", "true_at_and_below": {"absolute": 0}},
         block(f"{NS}:abyss_stone"))]}
w(f"{WG}/material_rule/elemental.json", rule)
# 3) 나무
TREE_SHAPES = {
 "storm":   ({"type": "minecraft:straight_trunk_placer", "base_height": 6, "height_rand_a": 2, "height_rand_b": 0}, {"type": "minecraft:blob_foliage_placer", "height": 3, "offset": 0, "radius": 2}, 3),
 "ember":   ({"type": "minecraft:forking_trunk_placer", "base_height": 5, "height_rand_a": 2, "height_rand_b": 2}, {"type": "minecraft:acacia_foliage_placer", "offset": 0, "radius": 2}, 1),
 "azure":   ({"type": "minecraft:straight_trunk_placer", "base_height": 5, "height_rand_a": 2, "height_rand_b": 0}, {"type": "minecraft:blob_foliage_placer", "height": 3, "offset": 0, "radius": 3}, 4),
 "blossom": ({"type": "minecraft:straight_trunk_placer", "base_height": 5, "height_rand_a": 1, "height_rand_b": 0}, {"type": "minecraft:blob_foliage_placer", "height": 4, "offset": 0, "radius": 3}, 4),
 "gale":    ({"type": "minecraft:straight_trunk_placer", "base_height": 7, "height_rand_a": 3, "height_rand_b": 0}, {"type": "minecraft:blob_foliage_placer", "height": 5, "offset": 0, "radius": 1}, 2),
 "shadow":  ({"type": "minecraft:straight_trunk_placer", "base_height": 8, "height_rand_a": 3, "height_rand_b": 0}, {"type": "minecraft:blob_foliage_placer", "height": 4, "offset": 0, "radius": 3}, 6),
 "radiant": ({"type": "minecraft:straight_trunk_placer", "base_height": 4, "height_rand_a": 1, "height_rand_b": 0}, {"type": "minecraft:blob_foliage_placer", "height": 2, "offset": 0, "radius": 2}, 1),
}
def ore(name, blk, size, count, lo, hi, targets):
    w(f"{WG}/feature/{name}.json", {"type": "minecraft:ore", "discard_chance_on_air_exposure": 0.0, "size": size,
        "targets": [{"state": blk, "target": {"predicate_type": "minecraft:block_match", "block": tb}} for tb in targets]})
    w(f"{WG}/placed_feature/{name}.json", {"feature": f"{NS}:{name}", "placement": [{"type": "minecraft:count", "count": count}, {"type": "minecraft:in_square"},
        {"type": "minecraft:height_range", "height": {"type": "minecraft:trapezoid", "max_inclusive": {"absolute": hi}, "min_inclusive": {"absolute": lo}}}, {"type": "minecraft:biome"}]})
STONES = [f"{NS}:aether_stone", f"{NS}:abyss_stone"]
ore("ore_aether", f"{NS}:aether_ore", 9, 20, -40, 140, STONES)
ore("ore_orichalcum", f"{NS}:orichalcum_ore", 4, 4, -80, -8, [f"{NS}:abyss_stone"])
for t in THEMES:
    key = t[0]; trunk, foliage, count = TREE_SHAPES[key]
    w(f"{WG}/feature/{key}_tree.json", {"type": "minecraft:tree", "below_trunk_provider": "minecraft:soil_beneath_tree", "decorators": [],
        "foliage_placer": foliage, "foliage_provider": {"id": f"{NS}:{key}_leaves"}, "ignore_vines": True,
        "minimum_size": {"type": "minecraft:two_layers_feature_size"}, "trunk_placer": trunk,
        "trunk_provider": {"id": f"{NS}:{key}_log", "properties": {"axis": "y"}}})
    w(f"{WG}/placed_feature/{key}_trees.json", {"feature": f"{NS}:{key}_tree", "placement": [{"type": "minecraft:count", "count": count}, {"type": "minecraft:in_square"},
        {"type": "minecraft:surface_water_depth_filter", "max_water_depth": 0}, {"type": "minecraft:heightmap", "heightmap": "OCEAN_FLOOR"},
        {"type": "minecraft:block_predicate_filter", "predicate": {"type": "minecraft:would_survive", "state": "minecraft:oak_sapling"}}, {"type": "minecraft:biome"}]})
    ore(f"ore_{t[12]}", f"{NS}:{t[12]}", 6, 9, -32, 110, STONES)
# 4) 바이옴
MONSTERS = [{"type": "minecraft:zombie", "count": 4, "weight": 100}, {"type": "minecraft:skeleton", "count": 4, "weight": 100},
            {"type": "minecraft:spider", "count": 4, "weight": 100}, {"type": "minecraft:creeper", "count": 4, "weight": 80}]
MOB_OF = {"thunder_plateau": "spark_wisp", "volcanic_plains": "magma_brute", "azure_forest": "tide_spirit", "blossom_forest": "thorn_beast",
          "gale_highlands": "gale_hawk", "shadow_woods": "shadow_stalker", "radiant_plains": "light_sentinel", "elemental_ocean": "tide_spirit"}
CREATURES = [{"type": f"{NS}:fluffle", "count": {"type": "minecraft:uniform", "max_inclusive": 4, "min_inclusive": 2}, "weight": 10},
             {"type": f"{NS}:horned_deer", "count": {"type": "minecraft:uniform", "max_inclusive": 3, "min_inclusive": 1}, "weight": 8},
             {"type": f"{NS}:elemental_merchant", "count": 1, "weight": 1}]
def biome(bid, temp, rain, water, sky, ore_feats, veg_feats, monsters=None):
    monsters = [{"type": f"{NS}:{MOB_OF[bid]}", "count": {"type": "minecraft:uniform", "max_inclusive": 3, "min_inclusive": 1}, "weight": 100}]
    creatures = [] if bid == "elemental_ocean" else CREATURES
    feats = [[] for _ in range(11)]
    feats[1] = ["minecraft:lake_lava_underground"]
    feats[6] = ["elementalweapons:ore_aether", "elementalweapons:ore_orichalcum"] + ore_feats
    feats[9] = veg_feats
    feats[10] = ["minecraft:freeze_top_layer"]
    w(f"{WG}/biome/{bid}.json", {
        "attributes": {"minecraft:gameplay/natural_mob_spawns": {"argument": {"spawn_costs": {}, "spawns_by_category": {"monster": monsters, "creature": creatures}}, "modifier": "overlay"},
                       "minecraft:visual/sky_color": sky, "minecraft:visual/water_fog_color": water},
        "carvers": ["minecraft:cave", "minecraft:cave_extra_underground", "minecraft:canyon"],
        "downfall": 0.8 if rain else 0.0, "effects": {"water_color": water}, "features": feats,
        "has_precipitation": rain, "temperature": temp})
SKY = {"storm": "#5a7aff", "ember": "#ff9a6a", "azure": "#6ad0ff", "blossom": "#ffb0e0", "gale": "#c8f8ff", "shadow": "#3a2a5a", "radiant": "#fff0b0"}
WATER = {"storm": "#3a6aff", "ember": "#ff7a3a", "azure": "#2ac8ff", "blossom": "#7ad0ff", "gale": "#a8f0ff", "shadow": "#4a2a7a", "radiant": "#fff0a0"}
TEMP = {"storm": 0.7, "ember": 1.6, "azure": 0.6, "blossom": 0.7, "gale": 0.2, "shadow": 0.5, "radiant": 0.8}
for t in THEMES:
    biome(t[1], TEMP[t[0]], t[0] not in ("ember",), WATER[t[0]], SKY[t[0]], [f"elementalweapons:ore_{t[12]}"], [f"elementalweapons:{t[0]}_trees"])
    lang[f"biome.{NS}.{t[1]}"] = t[2]
biome(OCEAN[0], 0.5, True, "#1a6aff", "#6ab0ff", ["elementalweapons:ore_tide_ore"], [])
lang[f"biome.{NS}.{OCEAN[0]}"] = OCEAN[1]
# 5) 바이옴 배치 (기후 값)
def P(b, t, h, c, e=(-1, 1), wd=(-1, 1)):
    return {"biome": f"{NS}:{b}", "parameters": {"temperature": list(t), "humidity": list(h), "continentalness": list(c), "erosion": list(e), "weirdness": list(wd), "depth": 0.0, "offset": 0.0}}
LAND = (-0.19, 1.0)
biomes = [P(OCEAN[0], (-1, 1), (-1, 1), (-1.2, -0.19)),
          P("gale_highlands", (-1, -0.45), (-1, 1), LAND),
          P("azure_forest", (-0.45, -0.15), (-1, 1), LAND),
          P("blossom_forest", (-0.15, 0.2), (0.0, 1), LAND),
          P("radiant_plains", (-0.15, 0.2), (-1, 0.0), LAND),
          P("thunder_plateau", (0.2, 0.55), (-1, 0.1), LAND),
          P("shadow_woods", (0.2, 0.55), (0.1, 1), LAND),
          P("volcanic_plains", (0.55, 1), (-1, 1), LAND)]
# 6) 월드 프리셋: 오버월드 하나만 (네더/엔드 없음)
w(f"{WG}/world_preset/elemental_world.json", {"dimensions": {"minecraft:overworld": {"type": "minecraft:overworld", "generator": {
    "type": "minecraft:noise", "biome_source": {"type": "minecraft:multi_noise", "biomes": biomes}, "settings": f"{NS}:elemental"}}}})
w(f"{MC}/tags/worldgen/world_preset/normal.json", {"replace": False, "values": [f"{NS}:elemental_world"]})
lang[f"generator.{NS}.elemental_world"] = "원소 세계"
# 7) 이름
p = f"{A}/lang/ko_kr.json"; cur = json.load(open(p, encoding="utf-8")); cur.update(lang); w(p, cur)
en = {k: k.split(".")[-1].replace("_", " ").title() for k in lang}; en[f"generator.{NS}.elemental_world"] = "Elemental World"
p = f"{A}/lang/en_us.json"; cur = json.load(open(p, encoding="utf-8")); cur.update(en); w(p, cur)

# ======================= 자바 등록 코드 =======================
ktype = {"stone": "STONE", "deepstone": "DEEPSTONE", "dirt": "DIRT", "sand": "SAND", "grass": "GRASS", "log": "LOG", "planks": "PLANKS", "leaves": "LEAVES", "ore": "ORE", "deepore": "DEEP_ORE"}
lines = []
for bid, ko, kind, info in BLOCKS:
    glow = "true" if info.get("glow") or kind in ("ore",) and info.get("theme") and info["theme"][16] else "false"
    xp = info.get("xp", (0, 0))
    lines.append(f'		add("{bid}", Kind.{ktype[kind]}, {xp[0]}, {xp[1]}, {glow});')
mats = "\n".join(f'		material("{m[0]}");' for m in MATERIALS)
open(f"{ROOT}/src/main/java/com/elementalweapons/world/WorldBlockList.java", "w").write(f"""// 자동 생성 파일 (tools/worldgen/world.py) - 직접 수정하지 마세요
package com.elementalweapons.world;

import com.elementalweapons.world.ModBlocks.Kind;

final class WorldBlockList {{
	private WorldBlockList() {{}}

	static void blocks() {{
{chr(10).join(f'		ModBlocks.{l.strip()}' for l in lines)}
	}}

	static void materials() {{
{chr(10).join(f'		ModBlocks.{l.strip()}' for l in mats.splitlines())}
	}}
}}
""")
print("world ok:", len(BLOCKS), "blocks,", len(MATERIALS), "materials")
