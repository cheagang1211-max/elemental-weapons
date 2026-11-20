"""일반 몹 10종 고퀄 HD 모델. 사용법: python3 tools/bossgen/mobs2.py <프로젝트 루트>"""
import math, sys, os
sys.path.insert(0, os.path.dirname(__file__))
from bossgen import Part, pack, paint, preview
from materials2 import M, FACE
from shapes import P, C, B, chain, bend_chain, spikes, wing, cape, ring_of, claws, teeth, R
from bosses2 import leg, arm, check_unique
from hd import hdify
from PIL import Image
HD = 2
SPECS = {}
def spec(fn): SPECS[fn.__name__] = fn; return fn

@spec
def spark_wisp():
    m = {"core": M(0xCFFFFF, glow_all=True), "shell": M(0x2A4A8A, "veins", glow=0x9FF3FF), "metal": M(0xF0C040, "metal"), "bolt": M(0x9FF0FF, glow_all=True)}
    m["face"] = FACE(m["shell"], 0xFFFFFF, mode="two")
    head = P("head", (0, 10, 0), (0, 0, 0), [C(0, 0, 0, 7, 7, 7, "shell", {"front": m["face"]}), C(0, 0, -3.8, 3, 3, 1, "core"), C(0, -4, 0, 3, 1, 3, "metal"),
             C(0, -5.5, 0, 1, 2, 1, "bolt"), C(-4, 0, 0, 1, 3, 3, "metal"), C(4, 0, 0, 1, 3, 3, "metal")])
    ring = P("ring", (0, 10, 0), (0, 0, 0), [], ring_of("wo", 6, 6, 2, "core"))
    tail = chain("tail", 3, (0, 13.5, 0), 3, 3, 1, "core", axis="y")
    return [head, ring, tail], m

@spec
def magma_brute():
    m = {"rock": M(0x2A1E1A, "lava", glow=0xFF7A1A), "core": M(0xFFB040, glow_all=True), "stone": M(0x3A2A24, "stone"), "horn": M(0x1A1210, "bone")}
    m["face"] = FACE(m["stone"], 0xFFD040, mode="two", mouth=0xFF7A1A, teeth="glow")
    head = P("head", (0, 1, -1), (0, 0, 0), [C(0, -3, -1, 8, 7, 8, "stone", {"front": m["face"]}), C(-4.5, -6, 0, 1, 4, 1, "horn"), C(4.5, -6, 0, 1, 4, 1, "horn"), C(0, -7, 1, 6, 1, 5, "rock")])
    fist = [C(0, 12, 0, 6, 5, 6, "stone"), C(0, 14.5, -1, 6, 1, 4, "core")]
    ra = arm("right", (-8, 3, 0), (5, 6), (5, 6), "rock", None, shoulder="stone", extra_fore=fist)
    la = arm("left", (8, 3, 0), (5, 6), (5, 6), "rock", None, shoulder="stone", extra_fore=fist[:1] + [C(0, 14.5, -1, 6, 1, 4, "core")])
    body = P("body", (0, 3, 0), (0, 0, 0), [C(0, 5, 0, 13, 11, 8, "rock"), C(0, 4, -4.2, 5, 5, 1, "core"), C(0, 0, 0, 14, 3, 9, "stone")] + spikes(3, -4, 4, -1.5, 2, 2, 3, 3, "horn", w=2), [head, ra, la])
    legs = [leg("right_leg", (-3, 13, 0), (5, 6), (5, 5), "rock", "stone"), leg("left_leg", (3, 13, 0), (5, 6), (5, 5), "rock", "stone")]
    return [body] + legs, m

@spec
def tide_spirit():
    m = {"water": M(0x3FA0FF, "veins", glow=0xBFF0FF), "foam": M(0xDDF4FF, glow_all=True), "shell": M(0xE8D8F0, "bone"), "pearl": M(0xF4F0FF, "gem", glow=0xBFE8FF)}
    m["face"] = FACE(m["water"], 0xFFFFFF, mode="two")
    hair = chain("swayH", 3, (0, -6, 3), 3, 6, 3, "foam", axis="y", h0=2, h1=1)
    head = P("head", (0, -6, 0), (0, 0, 0), [C(0, -3.5, 0, 7, 7, 7, "water", {"front": m["face"]}), C(0, -7.5, 0, 5, 1, 5, "shell"), C(0, -8.5, -2, 1, 1, 1, "pearl")], [hair])
    ra = arm("right", (-5, -4, 0), (2, 5), (2, 5), "water", "foam"); la = arm("left", (5, -4, 0), (2, 5), (2, 5), "water", "foam")
    tail = chain("tail", 4, (0, 4, 0), 4, 7, 2, "water", axis="y", h0=4, h1=1)
    body = P("body", (0, 0, 0), (0, 0, 0), [C(0, -1, 0, 8, 10, 5, "water"), C(0, -4, -2.8, 3, 3, 1, "pearl"), C(0, -5, 0, 9, 2, 6, "shell")], [head, ra, la, tail])
    return [body], m

@spec
def thorn_beast():
    m = {"vine": M(0x2E6A2A, "leaf", accent=0x4A9A3A, glow=0xFF7AC0), "thorn": M(0xD8D0A0, "bone"), "petal": M(0xFF8AC0, glow_all=True), "bark": M(0x4A3020, "bark")}
    m["face"] = FACE(m["bark"], 0xFFE040, mode="two", mouth=0x1A0A05, teeth=True)
    jaw = P("jaw", (0, 1.5, -1), (0, 0, 0), [C(0, 1, -3, 6, 2, 6, "bark")])
    head = P("head", (0, -1, -7), (0, 0, 0), [C(0, -1.5, -3, 8, 6, 7, "bark", {"front": m["face"]}), C(0, -5, -1, 10, 2, 5, "petal"),
             C(-4.5, -5, 0, 1, 3, 1, "thorn"), C(4.5, -5, 0, 1, 3, 1, "thorn")], [jaw])
    tail = chain("tail", 3, (0, -2, 6), 3, 3, 1, "vine", axis="z")
    body = P("body", (0, 14, 0), (0, 0, 0), [C(0, -1, 0, 10, 8, 13, "vine"), C(0, 3.5, 0, 8, 1, 11, "bark")] + spikes(5, 0, 0, -5, -5, 5, 3, 4, "thorn", w=1) +
             [C(-5, -3, 3, 2, 2, 2, "petal"), C(5, -2, -3, 2, 2, 2, "petal")], [head, tail])
    legs = [leg("right_leg", (-4, 17, 4), (3, 4), (3, 3), "vine", "bark", "thorn"), leg("left_leg", (4, 17, 4), (3, 4), (3, 3), "vine", "bark", "thorn"),
            leg("front_right_leg", (-4, 17, -4), (3, 4), (3, 3), "vine", "bark", "thorn", knee_name="front_right_leg2"),
            leg("front_left_leg", (4, 17, -4), (3, 4), (3, 3), "vine", "bark", "thorn", knee_name="front_left_leg2")]
    return [body] + legs, m

@spec
def gale_hawk():
    m = {"f": M(0xE8F4F2, "feather", accent=0x6AB8A0), "dark": M(0x3A7A74, "feather", accent=0x9FF3E8), "gold": M(0xF0C040, "metal"), "tip": M(0xB8FFF0, glow_all=True), "talon": M(0x3A3A40, "metal")}
    m["face"] = FACE(m["f"], 0x60FFE0, mode="two")
    head = P("head", (0, -2, -5), (0, 0, 0), [C(0, -2, -2, 5, 5, 5, "f", {"front": m["face"]}), C(0, -0.5, -5.5, 2, 2, 3, "gold"), C(0, -5, 0, 1, 2, 4, "dark")])
    rw = wing("right", (-3, -2, -1), "dark", "f", span=22, depth=8, fingers=3, rot=(0, 0, R(-10)))
    lw = wing("left", (3, -2, -1), "dark", "f", span=22, depth=8, fingers=3, rot=(0, 0, R(10)))
    tail = chain("tail", 2, (0, 0, 4), 3, 5, 6, "dark", axis="z", h0=1, h1=1)
    body = P("body", (0, 15, 0), (0, 0, 0), [C(0, 0, 0, 6, 6, 9, "f"), C(0, 2, -2, 5, 3, 4, "f"), C(0, -3.2, 1, 4, 1, 6, "dark")], [head, rw, lw, tail])
    legs = [leg("right_leg", (-1.5, 18, 1), (2, 3), (1, 3), "dark", "talon", "talon"), leg("left_leg", (1.5, 18, 1), (2, 3), (1, 3), "dark", "talon", "talon")]
    return [body] + legs, m

@spec
def shadow_stalker():
    m = {"c": M(0x14101C, "cloth"), "v": M(0x9A3BEA, glow_all=True), "bone": M(0x2A2234, "bone"), "blade": M(0xB088FF, "crystal", glow=0xE0C0FF)}
    m["face"] = FACE(M(0x05000A, "plain"), 0xC060FF, mode="two")
    hood = [C(0, -4, 1, 8, 8, 8, "c"), C(0, -8.5, 3, 4, 2, 4, "c")]
    head = P("head", (0, -10, 0), (0, 0, 0), [C(0, -3.5, -0.5, 6, 6, 6, "bone", {"front": m["face"]}), *hood])
    claw = [C(0, 14, -1, 1, 5, 3, "blade"), C(-1, 14, -1, 1, 4, 1, "blade")]
    ra = arm("right", (-5, -9, 0), (2, 7), (2, 7), "c", "bone", extra_fore=claw); la = arm("left", (5, -9, 0), (2, 7), (2, 7), "c", "bone", extra_fore=claw)
    capeP = cape((0, -9, 2.8), 5, 8, 4, "c")
    body = P("body", (0, 0, 0), (0, 0, 0), [C(0, -4, 0, 8, 12, 5, "c"), C(0, -5, -2.7, 2, 8, 1, "v"), C(0, -9, 0, 9, 2, 6, "bone")], [head, ra, la, capeP])
    legs = [leg("right_leg", (-2, 2, 0), (3, 11), (3, 11), "c", "bone"), leg("left_leg", (2, 2, 0), (3, 11), (3, 11), "c", "bone")]
    return [body] + legs, m

@spec
def light_sentinel():
    m = {"a": M(0xF2F0E8, "plate"), "gold": M(0xE0B040, "metal"), "g": M(0xFFE27A, glow_all=True), "rune": M(0xF2F0E8, "rune", glow=0xFFE27A)}
    m["face"] = FACE(m["a"], 0xFFE27A, mode="visor")
    head = P("head", (0, -8, 0), (0, 0, 0), [C(0, -3, 0, 6, 6, 6, "a", {"front": m["face"]}), C(0, -6.5, 0, 1, 2, 7, "gold"), C(-3.5, -4, 1, 1, 3, 3, "gold"), C(3.5, -4, 1, 1, 3, 3, "gold")])
    halo = P("halo", (0, -16, 2), (0, 0, 0), [], ring_of("lh", 6, 4, 1, "g"))
    spear = [C(0, 6, -2, 1, 22, 1, "gold"), C(0, -6, -2, 3, 3, 1, "g"), C(0, -8.5, -2, 1, 3, 1, "g")]
    ra = arm("right", (-5, -6, 0), (3, 5), (3, 5), "a", "gold", shoulder="gold", extra_fore=spear)
    la = arm("left", (5, -6, 0), (3, 5), (3, 5), "a", "gold", shoulder="gold", extra_fore=[C(2, 4, 0, 1, 8, 6, "rune")])
    body = P("body", (0, 0, 0), (0, 0, 0), [C(0, -2, 0, 8, 12, 5, "rune"), C(0, -6, -2.8, 3, 3, 1, "g"), C(0, 3.5, 0, 9, 2, 6, "gold")], [head, halo, ra, la])
    legs = [leg("right_leg", (-2, 4, 0), (3, 10), (3, 10), "a", "gold", None, armor="gold"), leg("left_leg", (2, 4, 0), (3, 10), (3, 10), "a", "gold", None, armor="gold")]
    return [body] + legs, m

@spec
def fluffle():
    m = {"fur": M(0xF4EEF8, "fur"), "pink": M(0xFFB0D0, "fur"), "nose": M(0xFF7AA0, "plain")}
    m["face"] = FACE(m["fur"], 0x302040, mode="two", mouth=0xFF8AB0, brow=False)
    ears = [P("swayE1", (-3, -5, 0), (0, 0, R(-15)), [C(0, -2, 0, 2, 4, 2, "fur"), C(0, -2, -1, 1, 3, 1, "pink")]), P("swayF1", (3, -5, 0), (0, 0, R(15)), [C(0, -2, 0, 2, 4, 2, "fur"), C(0, -2, -1, 1, 3, 1, "pink")])]
    body = P("body", (0, 18, 0), (0, 0, 0), [C(0, 0, 0, 10, 9, 10, "fur", {"front": m["face"]}), C(0, 1, -5.2, 2, 1, 1, "nose"), C(0, 2, 5.5, 3, 3, 2, "fur"), C(-5.3, 1, 0, 1, 4, 5, "fur"), C(5.3, 1, 0, 1, 4, 5, "fur")], ears)
    legs = [P(f"leg{i + 1}", (x, 22, z), (0, 0, 0), [C(0, 1, 0, 2, 2, 3, "pink")]) for i, (x, z) in enumerate([(-3, -3), (3, -3), (-3, 3), (3, 3)])]
    return [body] + legs, m

@spec
def horned_deer():
    m = {"fur": M(0x9A6A40, "fur"), "belly": M(0xE8D0B0, "fur"), "horn": M(0xBFF0FF, "crystal", glow=0xE0FFFF), "hoof": M(0x2A1A10, "metal")}
    m["face"] = FACE(m["fur"], 0x201010, mode="two", brow=False)
    antl = lambda s: [C(s * 2, -6, -1, 1, 5, 1, "horn"), C(s * 3.5, -8, -1, 3, 1, 1, "horn"), C(s * 4.5, -9.5, -1, 1, 3, 1, "horn"), C(s * 2.5, -10, -1, 1, 3, 1, "horn")]
    head = P("head", (0, -3, -7), (0, 0, 0), [C(0, -2, -2, 5, 5, 6, "fur", {"front": m["face"]}), C(0, 0, -5.5, 3, 3, 3, "belly"), C(-3, -4, 0, 1, 2, 2, "fur"), C(3, -4, 0, 1, 2, 2, "fur")] + antl(-1) + antl(1))
    neck = P("neck1", (0, -3, -6), (R(-30), 0, 0), [C(0, -2, -1, 4, 6, 4, "fur")], [head])
    head.pivot = (0, -5, 0)
    body = P("body", (0, 10, 0), (0, 0, 0), [C(0, 0, 0, 8, 7, 15, "fur"), C(0, 3.2, 0, 6, 1, 13, "belly"), C(0, -1, 8, 2, 3, 2, "belly")], [neck])
    legs = [leg("right_leg", (-3, 13, 5), (2, 6), (2, 5), "fur", "hoof"), leg("left_leg", (3, 13, 5), (2, 6), (2, 5), "fur", "hoof"),
            leg("front_right_leg", (-3, 13, -5), (2, 6), (2, 5), "fur", "hoof", knee_name="front_right_leg2"),
            leg("front_left_leg", (3, 13, -5), (2, 6), (2, 5), "fur", "hoof", knee_name="front_left_leg2")]
    return [body] + legs, m

@spec
def elemental_merchant():
    m = {"robe": M(0x3A2A6A, "cloth"), "trim": M(0xF0C040, "metal"), "skin": M(0xC8A080, "flesh"), "gem": M(0x9FF3FF, "gem", glow=0x9FF3FF), "pack": M(0x6A4A2A, "bark"),
         "cloth2": M(0x8A3A3A, "cloth"), "glow": M(0x9FF3FF, glow_all=True), "beard": M(0xE8E4DA, "fur")}
    m["face"] = FACE(m["skin"], 0x60D0FF, mode="two")
    hat = [C(0, -9, 0, 10, 1, 10, "robe"), C(0, -11, 0, 7, 3, 7, "robe"), C(0, -13.5, 1, 4, 2, 4, "robe"), C(0, -15, 2, 2, 2, 2, "robe"), C(0, -9.5, -5, 2, 2, 1, "gem")]
    head = P("head", (0, 0, 0), (0, 0, 0), [C(0, -4.5, 0, 8, 9, 8, "skin", {"front": m["face"]}), C(0, -1, -4.3, 6, 4, 1, "beard"), C(0, 0, -4.8, 4, 3, 1, "beard"), *hat])
    lantern = [C(0, 12, -2, 1, 3, 1, "trim"), C(0, 15, -2, 3, 4, 3, "glow"), C(0, 13, -2, 4, 1, 4, "trim")]
    ra = arm("right", (-5, 2, 0), (3, 6), (3, 6), "robe", "skin", shoulder="trim", extra_fore=lantern)
    la = arm("left", (5, 2, 0), (3, 6), (3, 6), "robe", "skin", shoulder="trim")
    pack_ = [C(0, 6, 5, 9, 10, 5, "pack"), C(0, 1, 5, 10, 2, 6, "cloth2"), C(-5, 8, 5, 1, 6, 4, "trim"), C(0, 12, 6, 6, 2, 3, "cloth2"), C(3, 4, 8, 2, 2, 1, "gem")]
    body = P("body", (0, 0, 0), (0, 0, 0), [C(0, 6, 0, 8, 12, 6, "robe"), C(0, 17.5, 0, 9, 13, 7, "robe"), C(0, 12, -3.6, 2, 22, 1, "trim"), C(0, 11, 0, 9, 2, 7, "cloth2"), *pack_], [head, ra, la])
    return [body], m

@spec
def fate_sage():
    m = {"robe": M(0x2A1A4A, "cloth"), "robe2": M(0x4A2A7A, "cloth"), "gold": M(0xE8C060, "metal"), "skin": M(0xD8B898, "flesh"),
         "beard": M(0xF4F0F8, "fur"), "rune": M(0xD080FF, glow_all=True), "wood": M(0x4A3020, "bark"), "orb": M(0xE0B0FF, "gem", glow=0xF0D0FF)}
    m["face"] = FACE(m["skin"], 0xE0A0FF, mode="two")
    hood = [C(0, -5, 1, 10, 11, 10, "robe"), C(0, -10.5, 3, 6, 2, 6, "robe"), C(0, -0.5, -4.6, 8, 1, 1, "gold")]
    beard = chain("swayB", 3, (0, -1, -4.5), 3, 6, 3, "beard", axis="y", h0=2, h1=1)
    head = P("head", (0, 0, 0), (0, 0, 0), [C(0, -4.5, -0.5, 8, 9, 8, "skin", {"front": m["face"]}), *hood], [beard])
    staff = [C(0, 4, -2, 1, 26, 1, "wood"), C(0, -10, -2, 3, 3, 3, "gold"), C(0, -13, -2, 3, 3, 3, "orb"), C(0, -15.5, -2, 1, 2, 1, "rune")]
    ra = arm("right", (-5, 2, 0), (3, 6), (3, 6), "robe2", "skin", shoulder="gold", extra_fore=staff)
    la = arm("left", (5, 2, 0), (3, 6), (3, 6), "robe2", "skin", shoulder="gold", extra_fore=[C(0, 12, -2, 4, 5, 2, "robe"), C(0, 12, -3.2, 2, 2, 1, "rune")])
    ring = P("ring", (0, -4, 0), (0, 0, 0), [], ring_of("sr", 8, 9, 1, "rune", y=0))
    body = P("body", (0, 0, 0), (0, 0, 0), [C(0, 6, 0, 8, 12, 6, "robe2"), C(0, 17.5, 0, 10, 13, 8, "robe"), C(0, 12, -3.6, 2, 22, 1, "gold"),
             C(0, 11, 0, 9, 2, 7, "gold"), C(0, 11, -4, 2, 2, 1, "rune"), C(4.6, 14, -1, 1, 4, 3, "wood")], [head, ra, la, ring])
    return [body], m

SCALE = {"magma_brute": 1.2}

if __name__ == "__main__":
    from bosses import java_model, camel
    root_dir = sys.argv[1]
    jdir = f"{root_dir}/src/main/java/com/elementalweapons/client/boss/gen"; tdir = f"{root_dir}/src/main/resources/assets/elementalweapons/textures/entity"
    reg = []
    sheet = Image.new("RGBA", (5 * 260, 2 * 260), (34, 34, 46, 255))
    for i, (mid, fn) in enumerate(SPECS.items()):
        roots, mats = fn(); check_unique(roots); hdify(roots, HD)
        root = Part("root", children=roots, field=False)
        for tw in (128, 256, 512):
            try: pack(root, tw, tw); break
            except SystemExit: continue
        tex, glow = paint(root, tw, tw, mats, seed=sum(map(ord, mid)))
        tex.save(f"{tdir}/{mid}.png"); glow.save(f"{tdir}/{mid}_glow.png")
        cls = camel(mid) + "Model"
        open(f"{jdir}/{cls}.java", "w").write(java_model(cls, roots, tw, tw).replace("tools/bossgen/bosses.py", "tools/bossgen/mobs2.py"))
        reg.append((mid, cls))
        sheet.paste(preview(root, tex, size=260, yaw=-35, pitch=12, glow=glow), ((i % 5) * 260, (i // 5) * 260))
    sheet.save("/tmp/mobs2_sheet.png")
    lines = []
    for mid, cls in reg:
        typ = {"fluffle": "ModMobs.FLUFFLE", "horned_deer": "ModMobs.HORNED_DEER", "elemental_merchant": "ModMobs.MERCHANT", "fate_sage": "ModMobs.SAGE"}.get(mid, f'ModMobs.MOBS.get("{mid}")')
        lines.append(f'		layer = new ModelLayerLocation(ElementalWeaponsMod.id("{mid}"), "main");\n		ModelLayerRegistry.registerModelLayer(layer, {cls}::createBodyLayer);\n		final ModelLayerLocation l_{mid} = layer;\n		EntityRenderers.register({typ}, ctx -> new GenericBossRenderer<>(ctx, l_{mid}, {cls}::new, "{mid}", {SCALE.get(mid, 1.0) / HD}F, 0.5F));')
    imports = "\n".join(f"import com.elementalweapons.client.boss.gen.{c};" for _, c in reg)
    open(f"{root_dir}/src/main/java/com/elementalweapons/client/boss/GeneratedMobModels.java", "w").write(f"""// 자동 생성 파일 (tools/bossgen/mobs2.py)
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
    print("mobs2 ok")
