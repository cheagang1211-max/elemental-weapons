"""일반 몹 10종 모델 + 리소스 생성. 사용법: python3 tools/bossgen/mobs.py <미리보기 폴더> <프로젝트 루트>"""
import math, sys, os, json
sys.path.insert(0, os.path.dirname(__file__))
from bossgen import Box as B, Part, pack, paint, preview
from materials import M, FACE
from bosses import P, rays, orbit_ring, chain, camel, java_model, check_unique
from PIL import Image
R = math.radians

def spark_wisp():
    m = {"core": M(0xCFFFFF, glow_all=True), "shell": M(0x2A4A8A, "veins", glow=0x9FF3FF)}
    head = P("head", (0, 10, 0), boxes=[B(-3, -3, -3, 6, 6, 6, "shell", {"front": FACE(m["shell"], 0xFFFFFF)}), B(-1.5, -1.5, -3.5, 3, 3, 1, "core")])
    ring = P("ring", (0, 10, 0), children=orbit_ring(6, 5, 1.5, ["core"]))
    tail = chain(["tail1", "tail2"], [(0, 13, 0), (0, 3, 0)], [[B(-1.5, 0, -1.5, 3, 3, 3, "core")], [B(-0.5, 0, -0.5, 1, 3, 1, "core")]])
    return [head, ring, tail], m
def magma_brute():
    m = {"rock": M(0x2A1E1A, "lava", glow=0xFF7A1A), "core": M(0xFFB040, glow_all=True)}
    head = P("head", (0, 2, 0), boxes=[B(-4, -6, -4, 8, 6, 8, "rock", {"front": FACE(M(0x2A1E1A, "stone"), 0xFFD040)})])
    ra = P("right_arm", (-7, 4, 0), boxes=[B(-4, -2, -2.5, 4, 14, 5, "rock")]); la = P("left_arm", (7, 4, 0), boxes=[B(0, -2, -2.5, 4, 14, 5, "rock")])
    body = P("body", (0, 2, 0), boxes=[B(-6, 0, -3.5, 12, 11, 7, "rock"), B(-2, 3, -4, 4, 4, 1, "core")], children=[head, ra, la])
    legs = [P("right_leg", (-3, 13, 0), boxes=[B(-2.5, 0, -2.5, 5, 11, 5, "rock")]), P("left_leg", (3, 13, 0), boxes=[B(-2.5, 0, -2.5, 5, 11, 5, "rock")])]
    return [body] + legs, m
def tide_spirit():
    m = {"water": M(0x3FA0FF, "veins", glow=0xBFF0FF), "foam": M(0xDDF4FF, glow_all=True)}
    head = P("head", (0, -6, 0), boxes=[B(-3.5, -7, -3.5, 7, 7, 7, "water", {"front": FACE(m["water"], 0xFFFFFF)})])
    ra = P("right_arm", (-5, -4, 0), boxes=[B(-2, 0, -1.5, 2, 10, 3, "water")]); la = P("left_arm", (5, -4, 0), boxes=[B(0, 0, -1.5, 2, 10, 3, "water")])
    tail = chain(["tail1", "tail2", "tail3"], [(0, 4, 0), (0, 6, 0), (0, 6, 0)], [[B(-3.5, 0, -2, 7, 6, 4, "water")], [B(-2.5, 0, -1.5, 5, 6, 3, "water")], [B(-1, 0, -1, 2, 6, 2, "foam")]])
    body = P("body", (0, 0, 0), boxes=[B(-4, -6, -2.5, 8, 10, 5, "water")], children=[head, ra, la, tail])
    return [body], m
def thorn_beast():
    m = {"vine": M(0x2E6A2A, "leaf", accent=0x4A9A3A, glow=0xFF7AC0), "thorn": M(0xD8D0A0, "stone"), "petal": M(0xFF8AC0, glow_all=True)}
    head = P("head", (0, 12, -6), boxes=[B(-4, -4, -6, 8, 7, 6, "vine", {"front": FACE(m["vine"], 0xFFE040, mouth=0x1A0A05)}), B(-5, -6, -3, 10, 2, 4, "petal")])
    body = P("body", (0, 14, 0), boxes=[B(-5, -5, -6, 10, 8, 12, "vine"), B(-1, -8, -4, 1, 3, 1, "thorn"), B(2, -8, 0, 1, 3, 1, "thorn"), B(-3, -8, 2, 1, 3, 1, "thorn")])
    legs = [P(f"leg{i+1}", (x, 17, z), boxes=[B(-1.5, 0, -1.5, 3, 7, 3, "vine")]) for i, (x, z) in enumerate([(-4, -4), (4, -4), (-4, 4), (4, 4)])]
    return [body, head] + legs, m
def gale_hawk():
    m = {"f": M(0xE8F4F2, "feather", accent=0x6AB8A0), "gold": M(0xF0C040, "metal"), "tip": M(0xB8FFF0, glow_all=True)}
    head = P("head", (0, 14, -4), boxes=[B(-2, -3, -3, 4, 4, 4, "f", {"front": FACE(m["f"], 0x60FFE0)}), B(-1, -1, -5, 2, 2, 2, "gold")])
    rw = P("right_wing", (-3, 15, 0), boxes=[B(-10, 0, -2, 10, 1, 6, "f"), B(-12, 0, -1, 2, 1, 4, "tip")])
    lw = P("left_wing", (3, 15, 0), boxes=[B(0, 0, -2, 10, 1, 6, "f"), B(10, 0, -1, 2, 1, 4, "tip")])
    body = P("body", (0, 16, 0), boxes=[B(-3, -2, -4, 6, 4, 9, "f"), B(-2, -1, 5, 4, 1, 4, "tip")])
    return [body, head, rw, lw], m
def shadow_stalker():
    m = {"c": M(0x14101C, "cloth"), "v": M(0x9A3BEA, glow_all=True)}
    head = P("head", (0, -10, 0), boxes=[B(-3.5, -7, -3.5, 7, 7, 7, "c", {"front": FACE(M(0x05000A, None), 0xC060FF)})])
    ra = P("right_arm", (-5, -9, 0), boxes=[B(-2, 0, -1.5, 2, 14, 3, "c"), B(-2.5, 13, -2, 3, 3, 1, "v")]); la = P("left_arm", (5, -9, 0), boxes=[B(0, 0, -1.5, 2, 14, 3, "c"), B(-0.5, 13, -2, 3, 3, 1, "v")])
    body = P("body", (0, 0, 0), boxes=[B(-4, -10, -2.5, 8, 12, 5, "c"), B(-1, -9, -3, 2, 8, 1, "v")], children=[head, ra, la])
    legs = [P("right_leg", (-2, 2, 0), boxes=[B(-1.5, 0, -1.5, 3, 22, 3, "c")]), P("left_leg", (2, 2, 0), boxes=[B(-1.5, 0, -1.5, 3, 22, 3, "c")])]
    return [body] + legs, m
def light_sentinel():
    m = {"a": M(0xF2F0E8, "rune", edge=0xE0B040, glow=0xFFE27A), "g": M(0xFFE27A, glow_all=True)}
    head = P("head", (0, -8, 0), boxes=[B(-3, -6, -3, 6, 6, 6, "a", {"front": FACE(M(0xF2F0E8, "metal"), 0xFFE27A, mode="one")})])
    ring = P("ring", (0, -16, 0), children=orbit_ring(6, 4, 1, ["g"]))
    ra = P("right_arm", (-5, -6, 0), boxes=[B(-2, 0, -1.5, 2, 10, 3, "a"), B(-1.5, -6, -0.5, 1, 18, 1, "g")]); la = P("left_arm", (5, -6, 0), boxes=[B(0, 0, -1.5, 2, 10, 3, "a")])
    body = P("body", (0, 0, 0), boxes=[B(-4, -8, -2.5, 8, 12, 5, "a")], children=[head, ring, ra, la])
    legs = [P("right_leg", (-2, 4, 0), boxes=[B(-1.5, 0, -1.5, 3, 20, 3, "a")]), P("left_leg", (2, 4, 0), boxes=[B(-1.5, 0, -1.5, 3, 20, 3, "a")])]
    return [body] + legs, m
def fluffle():
    m = {"fur": M(0xF4EEF8, "fur"), "pink": M(0xFFB0D0, "fur")}
    body = P("body", (0, 17, 0), boxes=[B(-5, -5, -5, 10, 10, 10, "fur", {"front": FACE(m["fur"], 0x302040, glow_eyes=False, mouth=0xFF8AB0)}), B(-4, -8, -1, 2, 3, 2, "pink"), B(2, -8, -1, 2, 3, 2, "pink")])
    legs = [P(f"leg{i+1}", (x, 22, z), boxes=[B(-1, 0, -1, 2, 2, 2, "pink")]) for i, (x, z) in enumerate([(-3, -3), (3, -3), (-3, 3), (3, 3)])]
    return [body] + legs, m
def horned_deer():
    m = {"fur": M(0x9A6A40, "fur"), "belly": M(0xE8D0B0, "fur"), "horn": M(0xBFF0FF, glow_all=True)}
    head = P("head", (0, 4, -7), boxes=[B(-2.5, -5, -5, 5, 5, 6, "fur", {"front": FACE(m["fur"], 0x201010, glow_eyes=False)}), B(-1.5, -2, -7, 3, 2, 2, "belly"),
             B(-3, -9, -1, 1, 4, 1, "horn"), B(2, -9, -1, 1, 4, 1, "horn"), B(-5, -10, -1, 2, 1, 1, "horn"), B(3, -10, -1, 2, 1, 1, "horn")])
    body = P("body", (0, 10, 0), boxes=[B(-4, -4, -8, 8, 7, 16, "fur"), B(-3, 2, -7, 6, 2, 14, "belly")], children=[head])
    legs = [P(f"leg{i+1}", (x, 13, z), boxes=[B(-1, 0, -1, 2, 11, 2, "fur")]) for i, (x, z) in enumerate([(-3, -6), (3, -6), (-3, 6), (3, 6)])]
    return [body] + legs, m
def elemental_merchant():
    m = {"robe": M(0x3A2A6A, "cloth"), "trim": M(0xF0C040, "metal"), "skin": M(0xC8A080, "fur", noise=4), "gem": M(0x9FF3FF, glow_all=True), "pack": M(0x6A4A2A, "bark")}
    head = P("head", (0, 0, 0), boxes=[B(-4, -10, -4, 8, 10, 8, "skin", {"front": FACE(m["skin"], 0x60D0FF)}), B(-4.5, -11, -4.5, 9, 4, 9, "robe"), B(-1, -11.5, -5, 2, 2, 1, "gem")])
    ra = P("right_arm", (-5, 2, 0), boxes=[B(-3, -2, -2, 3, 12, 4, "robe")]); la = P("left_arm", (5, 2, 0), boxes=[B(0, -2, -2, 3, 12, 4, "robe")])
    body = P("body", (0, 0, 0), boxes=[B(-4, 0, -3, 8, 12, 6, "robe"), B(-4.5, 11, -3.5, 9, 13, 7, "robe"), B(-1, 1, -3.5, 2, 22, 1, "trim"), B(-4, 2, 3, 8, 9, 4, "pack")],
             children=[head, ra, la])
    return [body], m

SPECS = {"spark_wisp": spark_wisp, "magma_brute": magma_brute, "tide_spirit": tide_spirit, "thorn_beast": thorn_beast, "gale_hawk": gale_hawk,
         "shadow_stalker": shadow_stalker, "light_sentinel": light_sentinel, "fluffle": fluffle, "horned_deer": horned_deer, "elemental_merchant": elemental_merchant}
SCALE = {"magma_brute": 1.2}

if __name__ == "__main__":
    out, root_dir = sys.argv[1], sys.argv[2]
    jdir = f"{root_dir}/src/main/java/com/elementalweapons/client/boss/gen"; tdir = f"{root_dir}/src/main/resources/assets/elementalweapons/textures/entity"
    reg = []; sheet = Image.new("RGBA", (5 * 240, 2 * 240), (34, 34, 46, 255))
    for i, (mid, fn) in enumerate(SPECS.items()):
        roots, mats = fn(); check_unique(roots)
        root = Part("root", children=roots, field=False); pack(root, 64, 64) if False else None
        tw, th = 64, 64
        try: pack(root, tw, th)
        except SystemExit: tw, th = 128, 128; pack(root, tw, th)
        tex, glow = paint(root, tw, th, mats, seed=hash(mid) % 1000)
        tex.save(f"{tdir}/{mid}.png"); glow.save(f"{tdir}/{mid}_glow.png")
        cls = camel(mid) + "Model"
        open(f"{jdir}/{cls}.java", "w").write(java_model(cls, roots, tw, th))
        reg.append((mid, cls))
        sheet.paste(preview(root, tex, size=240, yaw=-35, pitch=14, glow=glow), ((i % 5) * 240, (i // 5) * 240))
    sheet.save(f"{out}/mobs_sheet.png")
    lines = []
    for mid, cls in reg:
        typ = {"fluffle": "ModMobs.FLUFFLE", "horned_deer": "ModMobs.HORNED_DEER", "elemental_merchant": "ModMobs.MERCHANT"}.get(mid, f'ModMobs.MOBS.get("{mid}")')
        lines.append(f'		layer = new ModelLayerLocation(ElementalWeaponsMod.id("{mid}"), "main");\n		ModelLayerRegistry.registerModelLayer(layer, {cls}::createBodyLayer);\n		final ModelLayerLocation l_{mid} = layer;\n		EntityRenderers.register({typ}, ctx -> new GenericBossRenderer<>(ctx, l_{mid}, {cls}::new, "{mid}", {SCALE.get(mid, 1.0)}F, 0.5F));')
    imports = "\n".join(f"import com.elementalweapons.client.boss.gen.{c};" for _, c in reg)
    open(f"{root_dir}/src/main/java/com/elementalweapons/client/boss/GeneratedMobModels.java", "w").write(f"""// 자동 생성 파일 (tools/bossgen/mobs.py)
package com.elementalweapons.client.boss;

import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderers;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.mob.ModMobs;
{imports}

public final class GeneratedMobModels {{
	private GeneratedMobModels() {{}}

	public static void register() {{
		ModelLayerLocation layer;
{chr(10).join(lines)}
	}}
}}
""")
    # ----- 리소스 -----
    A = f"{root_dir}/src/main/resources/assets/elementalweapons"; D = f"{root_dir}/src/main/resources/data/elementalweapons"
    def w(p, o):
        os.makedirs(os.path.dirname(p), exist_ok=True); open(p, "w", encoding="utf-8").write(json.dumps(o, ensure_ascii=False, indent="\t") + "\n")
    names = {"spark_wisp": "스파크 정령", "magma_brute": "용암 거한", "tide_spirit": "물의 정령", "thorn_beast": "가시 덩굴괴물", "gale_hawk": "질풍 매",
             "shadow_stalker": "그림자 추적자", "light_sentinel": "빛의 파수꾼", "fluffle": "솜뭉치", "horned_deer": "뿔사슴", "elemental_merchant": "원소족 상인"}
    crystal = {"spark_wisp": "storm_crystal", "magma_brute": "ember_crystal", "tide_spirit": "tide_crystal", "thorn_beast": "bloom_crystal",
               "gale_hawk": "gale_crystal", "shadow_stalker": "umbral_crystal", "light_sentinel": "radiant_crystal"}
    def entry(item, lo, hi, chance=None):
        e = {"type": "minecraft:item", "name": item, "functions": [{"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}}]}
        pool = {"rolls": 1, "entries": [e]}
        if chance: pool["conditions"] = [{"condition": "minecraft:random_chance", "chance": chance}]
        return pool
    for mid, c in crystal.items():
        w(f"{D}/loot_table/entities/{mid}.json", {"type": "minecraft:entity", "pools": [entry(f"elementalweapons:{c}", 1, 1, 0.35), entry("elementalweapons:raw_aether", 0, 2)]})
    w(f"{D}/loot_table/entities/fluffle.json", {"type": "minecraft:entity", "pools": [entry("elementalweapons:elemental_meat", 1, 2), entry("elementalweapons:fluff", 1, 3)]})
    w(f"{D}/loot_table/entities/horned_deer.json", {"type": "minecraft:entity", "pools": [entry("elementalweapons:elemental_meat", 2, 3), entry("minecraft:leather", 0, 2)]})
    # 음식/솜털 아이템 텍스처
    def item_tex(name, base, accent, shape):
        im = Image.new("RGBA", (16, 16))
        for y in range(16):
            for x in range(16):
                if shape == "meat" and (x - 7.5) ** 2 / 36 + (y - 8) ** 2 / 20 < 1: im.putpixel((x, y), (base if (x + y) % 5 else accent) + (255,))
                if shape == "meat" and 11 <= x <= 14 and 3 <= y <= 5: im.putpixel((x, y), (240, 235, 220, 255))
                if shape == "fluff" and math.hypot(x - 7.5, y - 8) < 5.5 + math.sin(math.atan2(y - 8, x - 7.5) * 6): im.putpixel((x, y), (base if (x * y) % 7 else accent) + (255,))
        o = im.copy()
        for y in range(16):
            for x in range(16):
                if im.getpixel((x, y))[3] == 0 and any(0 <= x + a < 16 and 0 <= y + b < 16 and im.getpixel((x + a, y + b))[3] > 0 for a, b in ((1,0),(-1,0),(0,1),(0,-1))):
                    o.putpixel((x, y), (40, 20, 30, 255))
        o.save(f"{A}/textures/item/{name}.png")
        w(f"{A}/models/item/{name}.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"elementalweapons:item/{name}"}})
        w(f"{A}/items/{name}.json", {"model": {"type": "minecraft:model", "model": f"elementalweapons:item/{name}"}})
    item_tex("elemental_meat", (220, 90, 110), (250, 170, 180), "meat")
    item_tex("cooked_elemental_meat", (150, 80, 40), (200, 130, 70), "meat")
    item_tex("fluff", (245, 240, 250), (255, 200, 225), "fluff")
    rec = f"{D}/recipe"
    for typ, t in (("smelting", 200), ("smoking", 100), ("campfire_cooking", 600)):
        w(f"{rec}/cooked_elemental_meat_{typ}.json", {"type": f"minecraft:{typ}", "category": "food", "ingredient": "elementalweapons:elemental_meat", "result": {"id": "elementalweapons:cooked_elemental_meat"}, "experience": 0.35, "cookingtime": t})
    w(f"{rec}/white_wool_from_fluff.json", {"type": "minecraft:crafting_shaped", "category": "building", "pattern": ["FF", "FF"], "key": {"F": "elementalweapons:fluff"}, "result": {"id": "minecraft:white_wool", "count": 1}})
    w(f"{rec}/string_from_fluff.json", {"type": "minecraft:crafting_shapeless", "category": "misc", "ingredients": ["elementalweapons:fluff"], "result": {"id": "minecraft:string", "count": 2}})
    for lang in ("ko_kr", "en_us"):
        p = f"{A}/lang/{lang}.json"; d = json.load(open(p, encoding="utf-8"))
        for mid, k in names.items(): d[f"entity.elementalweapons.{mid}"] = k if lang == "ko_kr" else mid.replace("_", " ").title()
        for iid, k in (("elemental_meat", "원소 고기"), ("cooked_elemental_meat", "구운 원소 고기"), ("fluff", "솜털")):
            d[f"item.elementalweapons.{iid}"] = k if lang == "ko_kr" else iid.replace("_", " ").title()
        w(p, d)
    print("mobs ok")
