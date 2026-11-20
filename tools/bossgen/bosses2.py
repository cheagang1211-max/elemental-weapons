"""
보스 16종 고퀄 모델 v2 (황혼의 숲급 목표)
사용법: python3 tools/bossgen/bosses2.py <미리보기 폴더> <프로젝트 루트> [보스id...]
파트 이름 규칙(GenericBossModel): body head jaw neckN tailN right/left_arm(+2) right/left_leg(+2)
 front_right_leg front_left_leg right/left_wing(+2) capeN halo ring spin* pulse* bob* sway<글자>N legN headN
"""
import math, sys, os
sys.path.insert(0, os.path.dirname(__file__))
from bossgen import Part, pack, paint, preview, all_parts
from materials2 import M, FACE, hexc, mix
from shapes import P, C, B, chain, bend_chain, spikes, wing, cape, ring_of, claws, teeth, R
from PIL import Image

SPECS = {}
HD = 2
def spec(fn): SPECS[fn.__name__] = fn; return fn

def leg(name, pivot, thigh, shin, mat, foot_mat=None, claw_mat=None, knee_name=None, armor=None):
    """허벅지 + 정강이(자식) + 발/발톱. thigh=(w,len) shin=(w,len)"""
    tw, tl = thigh; sw, sl = shin
    sh_boxes = [C(0, sl / 2, 0, sw, sl, sw, mat)]
    if foot_mat: sh_boxes.append(C(0, sl - 1, -1.5, sw + 2, 2, sw + 3, foot_mat))
    if claw_mat: sh_boxes += claws(0, sl, -(sw + 3) / 2 - 1, 3, claw_mat, spread=max(1.5, sw / 2.5), length=2)
    th_boxes = [C(0, tl / 2, 0, tw, tl, tw, mat)]
    if armor: th_boxes.append(C(0, 2, 0, tw + 2, 4, tw + 2, armor))
    shin_p = P(knee_name or (name + "2"), (0, tl, 0), (0, 0, 0), sh_boxes)
    return P(name, pivot, (0, 0, 0), th_boxes, [shin_p])

def arm(side, pivot, upper, fore, mat, hand_mat=None, shoulder=None, extra_fore=(), extra_upper=(), rot=(0, 0, 0)):
    s = -1 if side == "right" else 1
    uw, ul = upper; fw, fl = fore
    ub = [C(s * 0.5, ul / 2, 0, uw, ul, uw, mat)] + list(extra_upper)
    if shoulder: ub.append(C(s * 0.5, 0, 0, uw + 3, 4, uw + 3, shoulder)); ub.append(C(s * 1.5, -2.5, 0, uw + 1, 2, uw + 1, shoulder))
    fb = [C(0, fl / 2, 0, fw, fl, fw, mat)] + list(extra_fore)
    if hand_mat: fb.append(C(0, fl + 1.5, 0, fw + 1, 3, fw + 1, hand_mat))
    return P(f"{side}_arm", pivot, rot, ub, [P(f"{side}_arm2", (s * 0.5, ul, 0), (0, 0, 0), fb)])

# =====================================================================
@spec
def ignis():
    m = {"scale": M(0xB8321C, "scale", accent=0x7A160C), "belly": M(0xF0A040, "plate"), "horn": M(0xE8DCC0, "bone"),
         "wing": M(0x7A1E14, "veins", glow=0xFF9A30), "fire": M(0xFFB040, glow_all=True), "claw": M(0x2A1A16, "metal"),
         "magma": M(0x5A1A10, "lava", glow=0xFFA030), "teeth": M(0xF4EEDC, "plain")}
    m["face"] = FACE(m["scale"], 0xFFE040, mode="two", brow=True)
    jaw = P("jaw", (0, 3, -2), (0, 0, 0), [C(0, 1, -6, 8, 2, 12, "scale"), *teeth(-3.5, 3.5, 0, -11, "teeth", up=True)])
    horns = [bend_chain("hornL", (3, -4, 2), [(4, 2), (4, 2), (3, 1)], "horn", bend=(R(-35), 0, 0)),
             bend_chain("hornR", (-3, -4, 2), [(4, 2), (4, 2), (3, 1)], "horn", bend=(R(-35), 0, 0))]
    for h in horns: h.rot = (R(-55), 0, 0)
    head = P("head", (0, 0, -6), (0, 0, 0), [
        C(0, -1, -4, 10, 8, 10, "scale", {"front": m["face"]}), C(0, 0.5, -12, 8, 5, 8, "scale"),
        C(0, -3.5, -6, 11, 2, 6, "scale"), C(-2, -2, -16, 1, 1, 1, "fire"), C(2, -2, -16, 1, 1, 1, "fire"),
        C(-5.5, 1, -3, 1, 3, 4, "horn"), C(5.5, 1, -3, 1, 3, 4, "horn"), *teeth(-3.5, 3.5, 3, -15.5, "teeth"),
        C(0, 2.5, -10, 6, 1, 8, "fire")], [jaw] + horns)
    neck = chain("neck", 3, (0, -3, -12), 6, 8, 6, "scale", axis="z", direction=-1,
                 decor=lambda i, w, h, L: spikes(2, 0, 0, -h / 2, -1, -L + 1, 2, 2, "horn"))
    # 목 끝에 머리 연결
    n = neck
    while n.children: n = n.children[0]
    n.children.append(head); head.pivot = (0, 0, -6)
    neck.rot = (R(-12), 0, 0)
    tail = chain("tail", 7, (0, -2, 12), 6, 8, 2, "scale", axis="z",
                 decor=lambda i, w, h, L: spikes(2, 0, 0, -h / 2, 1, L - 1, max(1, 3 - i * 0.3), max(1, 3 - i * 0.3), "horn"))
    t = tail
    while t.children: t = t.children[0]
    t.boxes += [C(0, 0, 6, 5, 4, 4, "fire"), C(0, -2, 8, 3, 3, 3, "fire")]
    tail.rot = (R(-8), 0, 0)
    rw = wing("right", (-6, -6, -6), "horn", "wing", span=34, depth=18, fingers=3, mat_claw="claw", rot=(0, R(10), R(-40)))
    lw = wing("left", (6, -6, -6), "horn", "wing", span=34, depth=18, fingers=3, mat_claw="claw", rot=(0, R(-10), R(40)))
    body = P("body", (0, 8, 0), (0, 0, 0), [
        C(0, 0, 0, 16, 13, 26, "scale"), C(0, 6, 0, 12, 2, 22, "belly"), C(0, -6.5, -9, 14, 3, 8, "scale"),
        C(-7, -2, -8, 4, 8, 8, "scale"), C(7, -2, -8, 4, 8, 8, "scale"), C(0, 2, 0, 17, 6, 20, "magma"),
        *spikes(7, 0, 0, -6.5, -11, 11, 2, 5, "horn", w=2)], [neck, tail, rw, lw])
    legs = [leg("right_leg", (-6, 14, 9), (6, 6), (5, 4), "scale", "scale", "claw"),
            leg("left_leg", (6, 14, 9), (6, 6), (5, 4), "scale", "scale", "claw"),
            leg("front_right_leg", (-6, 14, -8), (5, 6), (4, 4), "scale", "scale", "claw", knee_name="front_right_leg2"),
            leg("front_left_leg", (6, 14, -8), (5, 6), (4, 4), "scale", "scale", "claw", knee_name="front_left_leg2")]
    return [body] + legs, m

@spec
def marina():
    m = {"skin": M(0x86C8CC, "flesh"), "tail": M(0x1E8C8C, "scale", accent=0x0B4F5A), "hair": M(0x23409A, "cloth"),
         "gold": M(0xF0C860, "metal"), "pearl": M(0xF4F0FF, "gem", glow=0xBFE8FF), "fin": M(0x5FE0E0, "veins", glow=0xCFFFFF),
         "water": M(0x6FD8FF, glow_all=True), "coral": M(0xFF7A8A, "stone"), "shell": M(0xE8D8F0, "bone")}
    m["face"] = FACE(m["skin"], 0x9FFFFF, mode="two", mouth=0x2A4050)
    crown = [C(0, -9.5, 0, 9, 1, 9, "gold")] + [C(x, -11.5, -4, 1, 3, 1, "pearl") for x in (-3, 0, 3)] + [C(0, -13, -4, 1, 2, 1, "pearl")]
    hair = chain("swayH", 4, (0, -6, 4), 4, 8, 5, "hair", axis="y", h0=2, h1=1)
    head = P("head", (0, -14, 0), (0, 0, 0), [C(0, -4, 0, 8, 8, 8, "skin", {"front": m["face"]}), C(0, -6.5, 1, 9, 4, 8, "hair"),
             C(-4.5, -3, 0, 1, 4, 3, "fin"), C(4.5, -3, 0, 1, 4, 3, "fin"), *crown], [hair])
    trident = [C(0, -4, -4, 1, 30, 1, "gold"), C(0, -19, -4, 7, 1, 1, "gold"), C(-3, -21, -4, 1, 4, 1, "pearl"),
               C(0, -23, -4, 1, 6, 1, "pearl"), C(3, -21, -4, 1, 4, 1, "pearl"), C(0, -18, -4, 3, 3, 3, "water")]
    ra = arm("right", (-6, -12, 0), (3, 7), (3, 7), "skin", "skin", shoulder="shell", extra_fore=trident)
    la = arm("left", (6, -12, 0), (3, 7), (3, 7), "skin", "skin", shoulder="shell", extra_fore=[C(0, 9, -2, 4, 4, 4, "water")])
    tail = chain("tail", 6, (0, 2, 0), 5, 10, 3, "tail", axis="y", h0=7, h1=2,
                 decor=lambda i, w, h, L: [C(0, L / 2, h / 2 + 0.5, 1, L - 1, 2, "fin")])
    t = tail
    while t.children: t = t.children[0]
    t.boxes += [C(-4, 5, 0, 8, 6, 1, "fin"), C(4, 5, 0, 8, 6, 1, "fin")]
    tail.rot = (R(10), 0, 0)
    ring = P("ring", (0, -6, 0), (0, 0, 0), [], ring_of("orb", 10, 13, 2, "water") + ring_of("pearlo", 5, 9, 1, "pearl", y=-3))
    body = P("body", (0, -2, 0), (0, 0, 0), [C(0, -6, 0, 10, 12, 6, "skin"), C(0, -9.5, 0, 11, 4, 7, "shell"), C(0, -10, -3.5, 3, 3, 1, "pearl"),
             C(0, 1, 0, 11, 3, 7, "gold"), C(0, 1, -3.8, 2, 2, 1, "pearl"), C(-5.5, -3, 0, 1, 6, 5, "coral"), C(5.5, -3, 0, 1, 6, 5, "coral")],
             [head, ra, la, tail, ring])
    return [body], m

@spec
def silva():
    m = {"bark": M(0x5C3A1E, "bark", accent=0x8A6A3A), "leaf": M(0x2E7D32, "leaf", accent=0x7CC46A, glow=0xFFB7D5),
         "moss": M(0x6B8E23, "stone"), "flower": M(0xFF8FC0, glow_all=True), "root": M(0x3E2A16, "bark"),
         "core": M(0x9CFF6A, "gem", glow=0xB8FF80), "mush": M(0xE85040, "stone")}
    m["face"] = FACE(m["bark"], 0x9CFF6A, mode="two", mouth=0x120A04, teeth=False)
    canopy = [C(0, -11, 3, 22, 9, 16, "leaf"), C(-8, -16, 4, 10, 6, 10, "leaf"), C(7, -17, 2, 12, 7, 12, "leaf"), C(0, -20, 4, 10, 5, 10, "leaf"), C(-9, -7, 4, 5, 6, 8, "leaf"), C(9, -7, 4, 5, 6, 8, "leaf")]
    canopy += [C(x, y, z, 2, 2, 2, "flower") for x, y, z in ((-9, -12, -6), (6, -19, -5), (10, -10, -8), (-4, -20, 2), (0, -9, -9.5), (-11, -6, 4))]
    head = P("head", (0, -27, 0), (0, 0, 0), [C(0, -3, 0, 12, 7, 10, "bark", {"front": m["face"]}), *canopy,
             C(-8, -1, 0, 4, 2, 2, "root"), C(8, -1, 0, 4, 2, 2, "root")])
    branch = lambda s: [C(s * 2, 14, 0, 2, 6, 2, "root"), C(s * -1, 15, 0, 2, 5, 2, "root"), C(0, 14, -1, 4, 3, 4, "leaf")]
    ra = arm("right", (-8, -24, 0), (6, 10), (5, 10), "bark", None, shoulder="moss", extra_fore=branch(-1) + [C(0, 4, -3, 3, 3, 1, "mush")])
    la = arm("left", (8, -24, 0), (6, 10), (5, 10), "bark", None, shoulder="moss", extra_fore=branch(1))
    body = P("body", (0, 4, 0), (0, 0, 0), [C(0, -14, 0, 14, 24, 10, "bark"), C(0, -16, -5.5, 5, 5, 1, "core"),
             C(0, -24, 0, 16, 5, 11, "moss"), C(0, -3, 0, 15, 4, 11, "moss"), C(-7.5, -10, -2, 2, 3, 3, "mush"),
             C(6, -6, -5.5, 3, 2, 1, "flower")] + spikes(3, -4, 4, -26.5, 0, 0, 3, 3, "root", w=2), [head, ra, la])
    legs = [leg("right_leg", (-4, 4, 0), (7, 10), (6, 10), "bark", "root", "root"),
            leg("left_leg", (4, 4, 0), (7, 10), (6, 10), "bark", "root", "root")]
    for l in legs: l.boxes.append(C(0, 3, 0, 9, 3, 9, "moss"))
    return [body] + legs, m

@spec
def zephyros():
    m = {"feather": M(0xE6F2F0, "feather", accent=0x4FB8A8), "dark": M(0x2F6F73, "feather", accent=0x9FF3E8), "gold": M(0xF0C040, "metal"),
         "tip": M(0xB8FFF0, glow_all=True), "talon": M(0x3A3A40, "metal"), "gem": M(0x60FFE0, "gem", glow=0x9FFFF0)}
    m["face"] = FACE(m["feather"], 0x60FFE0, mode="two")
    crest = chain("swayC", 4, (0, -6, 2), 3, 2, 1, "dark", axis="z", h0=4, h1=2, rot=(R(30), 0, 0))
    head = P("head", (0, -4, -9), (0, 0, 0), [C(0, -3, -3, 8, 8, 8, "feather", {"front": m["face"]}), C(0, -1, -9, 3, 3, 5, "gold"),
             C(0, 1, -11.5, 2, 3, 1, "gold"), C(0, -7.5, -3, 6, 1, 4, "gold"), C(0, -8.5, -5, 2, 2, 1, "gem")], [crest])
    rw = wing("right", (-6, -5, -2), "dark", "feather", span=44, depth=18, fingers=4, rot=(0, 0, R(-10)))
    lw = wing("left", (6, -5, -2), "dark", "feather", span=44, depth=18, fingers=4, rot=(0, 0, R(10)))
    for w_ in (rw, lw):
        s = -1 if w_.name.startswith("right") else 1
        w_.children[0].boxes += [B(min(0, s * 22) + (s * 20 if s > 0 else 0), 0.5, 2, 2, 1, 16, "tip")]
    tail = chain("tail", 3, (0, 0, 9), 6, 10, 14, "dark", axis="z", h0=1, h1=1)
    tail.boxes.append(C(0, 0, 3, 4, 1, 6, "tip"))
    body = P("body", (0, 6, 0), (0, 0, 0), [C(0, 0, 0, 12, 12, 18, "feather"), C(0, 4, -4, 10, 6, 8, "feather"),
             C(0, -2, -9.5, 8, 6, 1, "gold"), C(0, -2, -10, 2, 2, 1, "gem"), C(0, -6.5, 2, 10, 1, 12, "dark")], [head, rw, lw, tail])
    legs = [leg("right_leg", (-3, 12, 2), (4, 6), (2, 6), "dark", "talon", "talon"), leg("left_leg", (3, 12, 2), (4, 6), (2, 6), "dark", "talon", "talon")]
    return [body] + legs, m

@spec
def nox():
    m = {"robe": M(0x24142E, "cloth"), "bone": M(0xD8D0C0, "bone"), "void": M(0x9A4DFF, glow_all=True),
         "metal": M(0x4A4A5A, "metal"), "blade": M(0xB088FF, "crystal", glow=0xE0C0FF), "dark": M(0x120A18, "cloth")}
    m["skull"] = FACE(m["bone"], 0xC070FF, mode="two", mouth=0x1A1020, teeth=True)
    hood = [C(0, -5, 1, 11, 11, 11, "robe"), C(0, -10.5, 3, 7, 2, 7, "robe"), C(0, -12, 5, 3, 2, 3, "robe")]
    head = P("head", (0, -20, 0), (0, 0, 0), [C(0, -4, -1, 8, 8, 8, "bone", {"front": m["skull"]}), *hood, C(0, -9, -5.5, 2, 2, 1, "void")])
    scythe = [C(0, 4, -6, 1, 34, 1, "metal"), C(0, -12, -6, 3, 2, 3, "void"), C(0, -13, -11, 1, 3, 10, "blade"),
              C(0, -11, -15, 1, 3, 4, "blade"), C(0, -9, -17, 1, 3, 2, "blade"), C(0, 21, -6, 2, 2, 2, "void")]
    ra = arm("right", (-7, -18, 0), (3, 9), (2, 9), "bone", "bone", shoulder="metal", extra_fore=scythe, extra_upper=[C(0, 4, 0, 5, 8, 5, "robe")])
    la = arm("left", (7, -18, 0), (3, 9), (2, 9), "bone", "bone", shoulder="metal", extra_upper=[C(0, 4, 0, 5, 8, 5, "robe")],
             extra_fore=[C(0, 12, -1, 3, 3, 3, "void")])
    robe = chain("swayR", 4, (0, 2, 0), 5, 13, 7, "robe", axis="y", h0=9, h1=5)
    robe.children[0].children[0].children[0].boxes += [C(-3, 6, 0, 2, 3, 4, "dark"), C(3, 7, 0, 2, 4, 4, "dark")]
    capeP = cape((0, -17, 4), 5, 14, 5, "dark")
    ring = P("ring", (0, -10, 0), (0, 0, 0), [], ring_of("shade", 6, 14, 3, "void"))
    body = P("body", (0, 0, 0), (0, 0, 0), [C(0, -9, 0, 12, 18, 8, "robe"), C(0, -14, -4.5, 6, 6, 1, "bone"), C(0, -16, 0, 14, 3, 9, "metal"),
             C(0, -15, -4.8, 2, 2, 1, "void"), C(0, -4, -4.5, 12, 2, 1, "metal")] + spikes(4, -6, 6, -17.5, 0, 0, 3, 3, "metal"),
             [head, ra, la, robe, capeP, ring])
    return [body], m

@spec
def lumiel():
    m = {"armor": M(0xE8ECF4, "plate"), "gold": M(0xF0C040, "metal"), "skin": M(0xF2D2B0, "flesh"),
         "wing": M(0xFFFDF0, "feather", accent=0xFFE7A0), "light": M(0xFFF6B0, glow_all=True), "cloth": M(0xFFFFFF, "cloth"),
         "blade": M(0xFFF8D8, "crystal", glow=0xFFFFC0)}
    m["helm"] = FACE(m["armor"], 0xFFF080, mode="visor")
    head = P("head", (0, -24, 0), (0, 0, 0), [C(0, -4, 0, 8, 8, 8, "armor", {"front": m["helm"]}), C(0, -8.5, 0, 2, 3, 9, "gold"),
             C(-4.5, -5, 1, 1, 3, 5, "gold"), C(4.5, -5, 1, 1, 3, 5, "gold"), C(-5.5, -7, 2, 1, 4, 2, "wing"), C(5.5, -7, 2, 1, 4, 2, "wing")])
    halo = P("halo", (0, -33, 3), (0, 0, 0), [C(0, -6, 0, 12, 1, 1, "light"), C(0, 6, 0, 12, 1, 1, "light"), C(-6, 0, 0, 1, 12, 1, "light"),
             C(6, 0, 0, 1, 12, 1, "light"), C(-4.5, -4.5, 0, 2, 2, 1, "light"), C(4.5, -4.5, 0, 2, 2, 1, "light"), C(-4.5, 4.5, 0, 2, 2, 1, "light"), C(4.5, 4.5, 0, 2, 2, 1, "light")])
    sword = [C(0, 12, -3, 2, 3, 2, "gold"), C(0, 10, -3, 7, 1, 2, "gold"), C(0, 0, -3, 3, 20, 1, "blade"), C(0, -11, -3, 1, 2, 1, "blade")]
    sword = [C(0, 12, -3, 2, 3, 2, "gold"), C(0, 14, -3, 7, 1, 2, "gold"), C(0, 25, -3, 3, 20, 1, "blade"), C(0, 36, -3, 1, 2, 1, "blade")]
    ra = arm("right", (-7, -22, 0), (4, 9), (4, 9), "armor", "gold", shoulder="gold", extra_fore=sword)
    la = arm("left", (7, -22, 0), (4, 9), (4, 9), "armor", "gold", shoulder="gold", extra_fore=[C(2.5, 5, 0, 1, 10, 8, "armor"), C(3, 5, 0, 1, 4, 4, "gold")])
    rw = wing("right", (-4, -20, 4), "gold", "wing", span=40, depth=20, fingers=4, rot=(R(-10), R(-25), R(-35)))
    lw = wing("left", (4, -20, 4), "gold", "wing", span=40, depth=20, fingers=4, rot=(R(-10), R(25), R(35)))
    skirt = cape((0, -6, -1), 3, 12, 5, "cloth", prefix="swayS", depth=8)
    capeP = cape((0, -20, 4.5), 5, 12, 5, "cloth")
    body = P("body", (0, 0, 0), (0, 0, 0), [C(0, -14, 0, 12, 16, 7, "armor"), C(0, -17, -3.8, 6, 6, 1, "gold"), C(0, -17, -4.3, 2, 2, 1, "light"),
             C(0, -6, 0, 13, 3, 8, "gold")], [head, halo, ra, la, rw, lw, skirt, capeP])
    legs = [leg("right_leg", (-3, 4, 0), (5, 10), (4, 10), "armor", "gold", None, armor="gold"), leg("left_leg", (3, 4, 0), (5, 10), (4, 10), "armor", "gold", None, armor="gold")]
    for l in legs: l.pivot = (l.pivot[0], -5, 0)
    return [body] + legs, m

@spec
def crystal_golem():
    m = {"rock": M(0x4A3A52, "stone"), "crystal": M(0xE070FF, "crystal", glow=0xFFB0FF), "core": M(0xFF80FF, "gem", glow=0xFFC0FF),
         "dark": M(0x2A2030, "stone"), "shard": M(0xB0F0FF, "crystal", glow=0xE0FFFF)}
    m["face"] = FACE(m["rock"], 0xFF90FF, mode="two")
    clusters = lambda x, y, z, s=1: [C(x, y - 3 * s, z, 2 * s, 6 * s, 2 * s, "crystal"), C(x + 2 * s, y - 2 * s, z + 1, 2 * s, 4 * s, 2 * s, "shard"), C(x - 1.5 * s, y - 1.5 * s, z - 1, 2, 3 * s, 2, "crystal")]
    head = P("head", (0, -27, -5), (0, 0, 0), [C(0, -3, -1, 10, 8, 9, "rock", {"front": m["face"]}), C(0, -7.5, 0, 12, 2, 9, "dark")] + clusters(0, -8, 1) + clusters(-4, -7, 2, 0.7))
    fist = lambda s: [C(0, 14, 0, 9, 8, 9, "rock"), C(0, 18.5, -2, 9, 2, 5, "dark")] + clusters(s * 3, 10, 0, 0.8)
    ra = arm("right", (-15, -22, 0), (8, 11), (7, 10), "rock", None, shoulder="dark", extra_fore=fist(-1), extra_upper=clusters(-1, -3, 0, 1.3))
    la = arm("left", (15, -22, 0), (8, 11), (7, 10), "rock", None, shoulder="dark", extra_fore=fist(1), extra_upper=clusters(1, -3, 0, 1.3))
    body = P("body", (0, 2, 0), (0, 0, 0), [C(0, -15, 0, 22, 18, 14, "rock"), C(0, -15, -7.5, 8, 8, 1, "core"), C(0, -15, -7, 12, 12, 1, "dark"),
             C(0, -5, 0, 16, 6, 12, "dark"), C(0, -24, 0, 24, 4, 15, "dark")] + clusters(-6, -25, 4, 1.4) + clusters(6, -26, 3, 1.6) + clusters(0, -26, 6, 1.2),
             [head, ra, la, P("pulse_core", (0, -15, -8), (0, 0, 0), [C(0, 0, 0, 4, 4, 2, "core")])])
    legs = [leg("right_leg", (-6, 2, 0), (8, 11), (8, 11), "rock", "dark", None), leg("left_leg", (6, 2, 0), (8, 11), (8, 11), "rock", "dark", None)]
    return [body] + legs, m

@spec
def lava_queen():
    m = {"chitin": M(0x2A1410, "plate"), "lava": M(0x5A1A10, "lava", glow=0xFFA030), "skin": M(0xC05030, "flesh"), "gold": M(0xF0B030, "metal"),
         "fire": M(0xFFB040, glow_all=True), "fang": M(0xF0E8D0, "bone")}
    m["face"] = FACE(m["skin"], 0xFFE040, mode="two", mouth=0x401010)
    head = P("head", (0, -14, -10), (0, 0, 0), [C(0, -3, 0, 7, 7, 7, "skin", {"front": m["face"]}), C(0, -7, 0, 8, 2, 8, "gold")] +
             [C(x, -9.5, -3, 1, 3, 1, "fire") for x in (-3, -1, 1, 3)] + [C(0, -11, -3, 1, 2, 1, "fire")] + [C(0, -2, 4.5, 6, 8, 2, "chitin")])
    torso = [C(0, -7, -10, 9, 10, 6, "skin"), C(0, -9, -10, 10, 3, 7, "gold"), C(0, -1, -10, 10, 3, 7, "chitin")]
    ra = arm("right", (-5, -10, -10), (3, 6), (2, 7), "skin", "chitin", shoulder="chitin", extra_fore=[C(0, 8, -1, 1, 4, 1, "fang")])
    la = arm("left", (5, -10, -10), (3, 6), (2, 7), "skin", "chitin", shoulder="chitin", extra_fore=[C(0, 8, -1, 1, 4, 1, "fang")])
    abdomen = P("tail1", (0, -1, 6), (R(-15), 0, 0), [C(0, -3, 8, 16, 13, 18, "lava"), C(0, -10, 8, 12, 2, 14, "chitin"), C(0, -3, 17.5, 6, 6, 1, "fire")] +
                spikes(4, 0, 0, -11, 2, 14, 2, 3, "chitin", w=2))
    legs = []
    for i in range(8):
        side = -1 if i % 2 == 0 else 1
        k = i // 2
        z = -6 + k * 4
        yaw = side * R(-40 + k * 27)
        outer = P(f"leg{i + 1}b", (side * 12, 0, 0), (0, 0, side * R(70)), [C(side * 7, 0, 0, 14, 2, 2, "chitin"), C(side * 14, 0, 0, 2, 2, 2, "fire")], field=False)
        legs.append(P(f"leg{i + 1}", (side * 4, 1, z), (0, yaw, side * R(-30)), [C(side * 6, 0, 0, 12, 3, 3, "chitin"), C(side * 11, 0, 0, 3, 3, 3, "lava")], [outer]))
    body = P("body", (0, 12, 0), (0, 0, 0), [C(0, 0, 0, 12, 8, 14, "chitin"), C(0, 1, 0, 13, 4, 15, "lava"), *torso,
             C(-2, 3, -15, 1, 3, 1, "fang"), C(2, 3, -15, 1, 3, 1, "fang")], [head, ra, la, abdomen] + legs)
    return [body], m

@spec
def frost_giant():
    m = {"skin": M(0x9AB8D0, "flesh"), "ice": M(0xBFEFFF, "crystal", glow=0xE8FFFF), "fur": M(0xE8E4DA, "fur"), "metal": M(0x5A6A80, "metal"),
         "beard": M(0xF0F8FF, "fur"), "leather": M(0x5A4030, "cloth"), "eye": M(0x80E0FF, glow_all=True)}
    m["face"] = FACE(m["skin"], 0x80F0FF, mode="two")
    beard = chain("swayB", 3, (0, 0, -4), 4, 8, 4, "beard", axis="y", h0=2, h1=1)
    crown = [C(x, -11, 0, 1, h, 1, "ice") for x, h in ((-3, 3), (-1, 5), (1, 5), (3, 3))] + [C(0, -9, 0, 9, 1, 9, "metal")]
    head = P("head", (0, -32, 0), (0, 0, 0), [C(0, -4, 0, 9, 9, 9, "skin", {"front": m["face"]}), C(0, -1, -4.5, 9, 3, 1, "beard"), *crown], [beard])
    club = [C(0, 12, -2, 3, 10, 3, "leather"), C(0, 21, -2, 7, 10, 7, "ice"), C(-3.5, 20, -2, 2, 4, 2, "ice"), C(3.5, 23, -2, 2, 4, 2, "ice"), C(0, 26.5, -2, 3, 3, 3, "ice")]
    ra = arm("right", (-10, -30, 0), (6, 13), (6, 12), "skin", "skin", shoulder="fur", extra_fore=club + [C(0, 4, 0, 7, 5, 7, "metal")])
    la = arm("left", (10, -30, 0), (6, 13), (6, 12), "skin", "skin", shoulder="fur", extra_fore=[C(0, 4, 0, 7, 5, 7, "metal")])
    capeP = cape((0, -29, 6), 6, 18, 5, "fur", depth=2)
    body = P("body", (0, -4, 0), (0, 0, 0), [C(0, -15, 0, 16, 22, 10, "skin"), C(0, -22, 0, 20, 7, 12, "fur"), C(0, -10, -5.2, 14, 12, 1, "ice"),
             C(0, -4, 0, 17, 4, 11, "leather"), C(0, -4, -5.8, 4, 3, 1, "metal")] + spikes(4, -8, 8, -25.5, 0, 0, 3, 3, "ice", w=2),
             [head, ra, la, capeP])
    legs = [leg("right_leg", (-4, -2, 0), (7, 13), (6, 13), "leather", "metal", None, armor="fur"), leg("left_leg", (4, -2, 0), (7, 13), (6, 13), "leather", "metal", None, armor="fur")]
    return [body] + legs, m

@spec
def sphinx():
    m = {"fur": M(0xD8A858, "fur"), "gold": M(0xF0C040, "metal"), "blue": M(0x2050B0, "cloth"), "skin": M(0xC08850, "flesh"),
         "sun": M(0xFFE070, glow_all=True), "stone": M(0xE0C890, "stone"), "wing": M(0xF0D890, "feather", accent=0x3060C0)}
    m["face"] = FACE(m["skin"], 0xFFE070, mode="two", mouth=0x502010)
    nemes = [C(0, -5, 1, 11, 9, 9, "blue"), C(-5.5, 1, -1, 2, 8, 4, "blue"), C(5.5, 1, -1, 2, 8, 4, "blue"), C(0, -9.5, 0, 10, 1, 8, "gold"),
             C(0, -10, -4.5, 2, 3, 1, "gold"), C(0, 4, 4.5, 3, 6, 2, "blue")]
    head = P("head", (0, -10, -13), (0, 0, 0), [C(0, -4, -1, 8, 8, 8, "skin", {"front": m["face"]}), C(0, 2, -5, 2, 4, 1, "gold"), *nemes])
    sun = P("halo", (0, -26, -9), (0, 0, 0), [C(0, 0, 0, 8, 8, 1, "sun")] + [P(f"sray{i}", (0, 0, 0), (0, 0, R(i * 45)), [B(-0.5, -11, 0, 1, 5, 1, "sun")], field=False) for i in range(8)][0:0] +
              [C(math.cos(i * math.pi / 4) * 7, math.sin(i * math.pi / 4) * 7, 0, 2, 2, 1, "sun") for i in range(8)])
    rw = wing("right", (-6, -6, -4), "gold", "wing", span=32, depth=16, fingers=3, rot=(R(-20), R(-20), R(-50)))
    lw = wing("left", (6, -6, -4), "gold", "wing", span=32, depth=16, fingers=3, rot=(R(-20), R(20), R(50)))
    tail = chain("tail", 5, (0, -2, 12), 4, 2, 2, "fur", axis="z")
    tail.rot = (R(-30), 0, 0)
    body = P("body", (0, 8, 0), (0, 0, 0), [C(0, 0, 0, 14, 12, 24, "fur"), C(0, -4, -11, 14, 10, 6, "fur"), C(0, -6, -12, 12, 6, 6, "gold"),
             C(0, -6, -15.2, 4, 4, 1, "blue"), C(0, 6.5, 0, 10, 1, 20, "stone")], [head, rw, lw, tail])
    legs = [leg("right_leg", (-5, 13, 9), (5, 6), (4, 5), "fur", "fur", "gold"), leg("left_leg", (5, 13, 9), (5, 6), (4, 5), "fur", "fur", "gold"),
            leg("front_right_leg", (-5, 13, -9), (5, 6), (4, 5), "fur", "fur", "gold", knee_name="front_right_leg2"),
            leg("front_left_leg", (5, 13, -9), (5, 6), (4, 5), "fur", "fur", "gold", knee_name="front_left_leg2")]
    return [body, sun] + legs, m

@spec
def steam_colossus():
    m = {"brass": M(0xC08030, "plate", trim=0x6A4018), "iron": M(0x4A4A52, "metal"), "steam": M(0xF0F0F0, glow_all=True), "furnace": M(0x3A1A10, "lava", glow=0xFFA040),
         "copper": M(0xD07A50, "metal"), "pipe": M(0x7A7A80, "metal"), "lens": M(0x60E0FF, "gem", glow=0x9FF0FF)}
    m["face"] = FACE(m["iron"], 0x60E0FF, mode="visor")
    head = P("head", (0, -30, -2), (0, 0, 0), [C(0, -4, 0, 10, 8, 9, "iron", {"front": m["face"]}), C(0, -9, 0, 6, 2, 6, "brass"),
             C(-6, -4, 0, 2, 4, 4, "copper"), C(6, -4, 0, 2, 4, 4, "copper"), C(0, -11, 0, 2, 3, 2, "pipe")])
    gear = lambda x, y, z: P(f"spin_g{abs(int(x))}{abs(int(y))}", (x, y, z), (0, 0, 0), [C(0, 0, 0, 6, 6, 1, "brass"), C(0, 0, 0, 8, 2, 1, "brass"), C(0, 0, 0, 2, 8, 1, "brass"), C(0, 0, -0.5, 2, 2, 1, "iron")])
    hammer = [C(0, 12, 0, 3, 8, 3, "pipe"), C(0, 18, 0, 10, 7, 7, "iron"), C(-5.5, 18, 0, 1, 5, 5, "brass"), C(5.5, 18, 0, 1, 5, 5, "brass")]
    ra = arm("right", (-14, -27, 0), (7, 11), (7, 10), "brass", None, shoulder="iron", extra_fore=hammer + [C(0, 3, 0, 9, 4, 9, "copper")])
    la = arm("left", (14, -27, 0), (7, 11), (7, 10), "brass", None, shoulder="iron",
             extra_fore=[C(0, 3, 0, 9, 4, 9, "copper"), C(0, 13, -1, 6, 6, 6, "iron"), C(0, 16.5, -1, 4, 1, 4, "furnace")])
    stacks = [C(x, -36, 5, 3, 10, 3, "pipe") for x in (-6, 6)] + [C(x, -41.5, 5, 4, 1, 4, "iron") for x in (-6, 6)]
    body = P("body", (0, -4, 0), (0, 0, 0), [C(0, -16, 0, 24, 20, 14, "brass"), C(0, -15, -7.2, 10, 10, 1, "furnace"), C(0, -15, -7.6, 12, 1, 1, "iron"),
             C(0, -11, -7.6, 12, 1, 1, "iron"), C(0, -27, 0, 26, 4, 15, "iron"), C(0, -5, 0, 18, 4, 12, "iron"), C(-10, -10, -7.4, 2, 2, 1, "lens"),
             *stacks, P and C(0, -20, 7.5, 16, 10, 2, "copper")], [head, ra, la, gear(-9, -20, -7.6), gear(9, -21, -7.6),
             P("bob_s1", (-6, -44, 5), (0, 0, 0), [C(0, 0, 0, 3, 3, 3, "steam")]), P("bob_s2", (6, -45, 5), (0, 0, 0), [C(0, 0, 0, 3, 3, 3, "steam")])])
    legs = [leg("right_leg", (-6, 0, 0), (8, 12), (8, 12), "iron", "brass", None, armor="brass"), leg("left_leg", (6, 0, 0), (8, 12), (8, 12), "iron", "brass", None, armor="brass")]
    return [body] + legs, m

@spec
def storm_wyvern():
    m = {"scale": M(0x1E2A4A, "scale", accent=0x3A5AA0), "belly": M(0x8AA0C8, "plate"), "bolt": M(0x9FE8FF, glow_all=True), "horn": M(0xD0D8E8, "bone"),
         "wing": M(0x2A3A6A, "veins", glow=0x9FE8FF), "claw": M(0x101018, "metal")}
    m["face"] = FACE(m["scale"], 0x9FF0FF, mode="two")
    jaw = P("jaw", (0, 2, -3), (0, 0, 0), [C(0, 1, -5, 6, 2, 10, "scale"), *teeth(-2.5, 2.5, 0, -9.5, "horn", up=True)])
    head = P("head", (0, 0, -5), (0, 0, 0), [C(0, -1, -3, 8, 6, 8, "scale", {"front": m["face"]}), C(0, 0, -10, 6, 4, 7, "scale"),
             *teeth(-2.5, 2.5, 2, -13, "horn"), C(0, -4.5, -2, 2, 1, 6, "bolt")], [jaw,
             bend_chain("hornWL", (3, -3, 1), [(5, 2), (4, 1)], "horn", bend=(R(-30), 0, 0)), bend_chain("hornWR", (-3, -3, 1), [(5, 2), (4, 1)], "horn", bend=(R(-30), 0, 0))])
    for h in head.children[1:]: h.rot = (R(-60), 0, 0)
    neck = chain("neck", 3, (0, -2, -9), 5, 6, 5, "scale", axis="z", direction=-1, decor=lambda i, w, h, L: [C(0, -h / 2 - 1, -L / 2, 1, 2, 2, "bolt")])
    n = neck
    while n.children: n = n.children[0]
    n.children.append(head)
    neck.rot = (R(-25), 0, 0)
    tail = chain("tail", 7, (0, -1, 9), 6, 6, 2, "scale", axis="z", decor=lambda i, w, h, L: [C(0, -h / 2 - 0.5, L / 2, 1, 1, 3, "bolt")])
    t = tail
    while t.children: t = t.children[0]
    t.boxes += [C(0, 0, 6, 6, 1, 5, "bolt"), C(0, -2, 6, 1, 4, 5, "bolt")]
    rw = wing("right", (-5, -4, -4), "horn", "wing", span=50, depth=22, fingers=4, mat_claw="claw", rot=(0, R(10), R(-25)))
    lw = wing("left", (5, -4, -4), "horn", "wing", span=50, depth=22, fingers=4, mat_claw="claw", rot=(0, R(-10), R(25)))
    body = P("body", (0, 8, 0), (0, 0, 0), [C(0, 0, 0, 12, 11, 20, "scale"), C(0, 5, 0, 9, 2, 16, "belly"), *spikes(5, 0, 0, -5.5, -8, 8, 2, 3, "horn", w=1)],
             [neck, tail, rw, lw])
    legs = [leg("right_leg", (-5, 12, 5), (5, 6), (3, 6), "scale", "claw", "claw"), leg("left_leg", (5, 12, 5), (5, 6), (3, 6), "scale", "claw", "claw")]
    return [body] + legs, m

@spec
def twilight_twins():
    m = {"dark": M(0x2A1A40, "cloth"), "light": M(0xF6EED8, "cloth"), "dskin": M(0x5A4A70, "flesh"), "lskin": M(0xF2D8C0, "flesh"),
         "void": M(0xA060FF, glow_all=True), "sun": M(0xFFE080, glow_all=True), "gold": M(0xF0C040, "metal"), "silver": M(0xA0A8C0, "metal")}
    m["dface"] = FACE(m["dskin"], 0xC080FF, mode="two"); m["lface"] = FACE(m["lskin"], 0x80C0FF, mode="two")
    def twin(sfx, x, skin, face, robe, glowm, metal, sign):
        head = P("head" + sfx, (0, -24, 0), (0, 0, 0), [C(0, -4, 0, 7, 8, 7, skin, {"front": m[face]}), C(0, -6, 1.5, 8, 6, 6, robe),
                 C(0, -9, 0, 6, 1, 6, metal), C(0, -10.5, -2, 1, 2, 1, glowm)])
        arm_o = P(("right" if sign < 0 else "left") + "_arm", (sign * 6, -21, 0), (0, 0, sign * R(-20)), [C(sign * 1, 6, 0, 3, 13, 3, robe), C(sign * 1, 14, -1, 4, 4, 4, glowm)])
        orb = P("bob_" + sfx, (sign * 10, -26, -4), (0, 0, 0), [C(0, 0, 0, 4, 4, 4, glowm), C(0, 0, 0, 6, 1, 1, metal), C(0, 0, 0, 1, 6, 1, metal)])
        robe_c = chain("sway" + sfx.upper(), 3, (0, -4, 0), 6, 9, 11, robe, axis="y", h0=7, h1=8)
        return P("twin" + sfx, (x, 0, 0), (0, 0, sign * R(-6)), [C(0, -13, 0, 8, 14, 6, robe), C(0, -18, -3.2, 4, 4, 1, metal), C(0, -18, -3.6, 2, 2, 1, glowm)],
                 [head, arm_o, orb, robe_c])
    d = twin("1", -6, "dskin", "dface", "dark", "void", "silver", -1)
    l = twin("2", 6, "lskin", "lface", "light", "sun", "gold", 1)
    ring = P("ring", (0, -12, 0), (0, 0, 0), [], ring_of("tw", 8, 15, 2, "void")[:4] + [P(f"tws{i}", (math.cos(i * 1.57 + 0.78) * 15, 0, math.sin(i * 1.57 + 0.78) * 15), (0, 0, 0), [C(0, 0, 0, 2, 2, 2, "sun")], field=False) for i in range(4)])
    body = P("body", (0, 6, 0), (0, 0, 0), [C(0, -2, 0, 4, 4, 4, "void")], [d, l, ring])
    return [body], m

@spec
def hydra():
    m = {"scale": M(0x1E5A2A, "scale", accent=0x0E3A18), "belly": M(0xC8D890, "plate"), "petal": M(0xFF70B0, "leaf", accent=0xFFB0D8, glow=0xFFD0F0),
         "poison": M(0xB0FF60, glow_all=True), "horn": M(0xE8E0C8, "bone"), "vine": M(0x2E7D32, "bark")}
    def headN(i, x, yaw):
        face = FACE(m["scale"], 0xD0FF60, mode="two"); m[f"face{i}"] = face
        jaw = P(f"jawh{i}", (0, 2, -2), (R(15), 0, 0), [C(0, 1, -4, 6, 2, 8, "scale"), *teeth(-2.5, 2.5, 0, -7.5, "horn", up=True), C(0, 0, -3, 4, 1, 5, "poison")], field=False)
        petals = [C(px, py, 1.5, 2, 2, 1, "petal") for px, py in ((-4, -4), (4, -4), (0, -5.5))]
        h = P(f"head{i}", (0, 0, -6), (0, 0, 0), [C(0, -1, -3, 7, 6, 7, "scale", {"front": face}), C(0, 0, -9, 5, 4, 6, "scale"), *teeth(-2, 2, 2, -11.5, "horn"),
              C(0, -4.5, -2, 1, 2, 4, "horn"), *petals], [jaw])
        nk = chain(f"neckh{i}x", 4, (x, -6, -6), 6, 6, 5, "scale", axis="y", direction=-1, decor=lambda k, w, hh, L: [C(0, -L / 2, w / 2 + 0.5, 3, L - 1, 1, "belly")])
        e = nk
        while e.children: e = e.children[0]
        e.children.append(h); h.pivot = (0, -6, -2)
        nk.rot = (R(-15 if x == 0 else -5), 0, R(x * 3.0))
        return nk
    heads = [headN(1, -10, R(35)), headN(2, 0, 0), headN(3, 10, R(-35))]
    tail = chain("tail", 6, (0, 0, 12), 6, 9, 2, "scale", axis="z", decor=lambda i, w, h, L: [C(0, -h / 2 - 1, L / 2, 2, 2, 2, "petal")] if i % 2 == 0 else [])
    vines = [C(-9, -2, 0, 2, 2, 22, "vine"), C(9, -2, 0, 2, 2, 22, "vine")]
    body = P("body", (0, 10, 0), (0, 0, 0), [C(0, 0, 0, 18, 14, 24, "scale"), C(0, 7, 0, 14, 2, 20, "belly"), *vines,
             *spikes(5, 0, 0, -7, -8, 10, 3, 4, "petal", w=2)] + [C(x, -7, z, 2, 2, 2, "poison") for x, z in ((-6, -4), (6, 2), (-4, 8))], heads + [tail])
    legs = [leg("right_leg", (-8, 15, 8), (6, 5), (5, 4), "scale", "scale", "horn"), leg("left_leg", (8, 15, 8), (6, 5), (5, 4), "scale", "scale", "horn"),
            leg("front_right_leg", (-8, 15, -8), (6, 5), (5, 4), "scale", "scale", "horn", knee_name="front_right_leg2"),
            leg("front_left_leg", (8, 15, -8), (6, 5), (5, 4), "scale", "scale", "horn", knee_name="front_left_leg2")]
    return [body] + legs, m

@spec
def prima():
    cols = {"fire": 0xFF6A30, "water": 0x3AA0FF, "grass": 0x60E060, "wind": 0xB0FFF0, "dark": 0xA050FF, "light": 0xFFF090, "elec": 0x60E0FF}
    m = {k: M(v, "gem", glow=v) for k, v in cols.items()}
    m.update({"body": M(0xF4F2FF, "crystal", glow=0xFFFFFF), "gold": M(0xF0C040, "metal"), "robe": M(0x3A2A6A, "cloth"),
              "core": M(0xFFFFFF, glow_all=True), "skin": M(0xE8E0FF, "flesh")})
    m["face"] = FACE(m["skin"], 0xFFFFFF, mode="two")
    crown = [C(0, -9, 0, 9, 1, 9, "gold")] + [C(x, -11, -4, 1, 3, 1, k) for x, k in ((-4, "fire"), (-2, "water"), (0, "light"), (2, "grass"), (4, "dark"))]
    head = P("head", (0, -30, 0), (0, 0, 0), [C(0, -4, 0, 8, 8, 8, "skin", {"front": m["face"]}), *crown, C(0, -6, 3, 9, 7, 4, "robe")])
    def arm4(side, y, gem, idx):
        s = -1 if side == "right" else 1
        nm = f"{side}_arm" if idx == 0 else f"sway{'P' if s < 0 else 'Q'}1"
        return P(nm, (s * 8, y, 0), (0, 0, s * R(-25 - idx * 30)), [C(s * 1, 6, 0, 3, 12, 3, "body"), C(s * 1, 13.5, 0, 4, 4, 4, gem), C(s, 1, 0, 5, 4, 5, "gold")])
    arms = [arm4("right", -25, "fire", 0), arm4("left", -25, "water", 0), arm4("right", -18, "dark", 1), arm4("left", -18, "light", 1)]
    halo = P("halo", (0, -36, 5), (0, 0, 0), [C(0, -9, 0, 14, 1, 1, "core"), C(0, 9, 0, 14, 1, 1, "core"), C(-9, 0, 0, 1, 14, 1, "core"), C(9, 0, 0, 1, 14, 1, "core")] +
             [C(math.cos(a) * 9, math.sin(a) * 9, 0, 3, 3, 1, k) for a, k in zip([i * math.pi / 3.5 for i in range(7)], cols)])
    ring = P("ring", (0, -16, 0), (0, 0, 0), [], [P(f"porb{k}", (math.cos(i * 2 * math.pi / 7) * 18, 0, math.sin(i * 2 * math.pi / 7) * 18), (0, 0, 0), [C(0, 0, 0, 4, 4, 4, k), C(0, 0, 0, 2, 6, 2, k)], field=False) for i, k in enumerate(cols)])
    robe = chain("swayR", 4, (0, -10, 0), 6, 12, 18, "robe", axis="y", h0=8, h1=12)
    capeP = cape((0, -27, 4.5), 6, 14, 5, "robe")
    body = P("body", (0, 0, 0), (0, 0, 0), [C(0, -19, 0, 12, 16, 7, "body"), C(0, -21, -3.8, 5, 5, 1, "core"), C(0, -26, 0, 16, 3, 9, "gold"),
             C(-8, -28, 0, 4, 4, 6, "gold"), C(8, -28, 0, 4, 4, 6, "gold"), C(0, -12, 0, 13, 2, 8, "gold")], [head, halo, ring, robe, capeP] + arms)
    return [body], m

@spec
def voltra():
    m = {"armor": M(0x2A3050, "plate"), "gold": M(0xF0C040, "metal"), "bolt": M(0x9FE8FF, glow_all=True), "skin": M(0xC8D0E8, "flesh"),
         "cloth": M(0x1A2A6A, "cloth"), "crystal": M(0x60E0FF, "crystal", glow=0xB0F8FF)}
    m["helm"] = FACE(m["armor"], 0x9FF8FF, mode="visor")
    wingsH = [C(-5, -6, 1, 1, 5, 4, "gold"), C(5, -6, 1, 1, 5, 4, "gold"), C(-6, -9, 2, 1, 4, 3, "bolt"), C(6, -9, 2, 1, 4, 3, "bolt")]
    head = P("head", (0, -26, 0), (0, 0, 0), [C(0, -4, 0, 8, 8, 8, "armor", {"front": m["helm"]}), C(0, -8.5, 0, 2, 2, 8, "gold"), *wingsH,
             C(0, -2, -4.3, 4, 1, 1, "bolt")])
    spear = [C(0, 6, -3, 1, 36, 1, "gold"), C(0, -13, -3, 3, 3, 3, "crystal"), C(0, -17, -3, 1, 5, 1, "bolt"), C(-1.5, -15, -3, 1, 3, 1, "bolt"), C(1.5, -15, -3, 1, 3, 1, "bolt")]
    ra = arm("right", (-8, -24, 0), (4, 9), (4, 9), "armor", "gold", shoulder="gold", extra_fore=spear)
    la = arm("left", (8, -24, 0), (4, 9), (4, 9), "armor", "gold", shoulder="gold", extra_fore=[C(0, 12, -1, 3, 3, 3, "bolt")])
    halo = P("halo", (0, -30, 6), (0, 0, 0), [C(0, 0, 0, 3, 3, 1, "crystal")] + [C(math.cos(i * math.pi / 4) * 8, math.sin(i * math.pi / 4) * 8, 0, 1 + (i % 2), 3 - (i % 2), 1, "bolt") for i in range(8)])
    capeP = cape((0, -22, 4.5), 6, 13, 5, "cloth")
    body = P("body", (0, 0, 0), (0, 0, 0), [C(0, -15, 0, 12, 16, 7, "armor"), C(0, -18, -3.8, 6, 5, 1, "gold"), C(0, -18, -4.2, 2, 3, 1, "bolt"),
             C(0, -7, 0, 13, 3, 8, "gold"), C(0, -3, 0, 12, 6, 7, "cloth")], [head, ra, la, halo, capeP])
    legs = [leg("right_leg", (-3, -4, 0), (5, 9), (4, 10), "armor", "gold", None, armor="gold"), leg("left_leg", (3, -4, 0), (5, 9), (4, 10), "armor", "gold", None, armor="gold")]
    return [body] + legs, m

# =====================================================================
def check_unique(roots):
    seen = {}
    for r in roots:
        for p in all_parts(r):
            names = [c.name for c in p.children]
            assert len(names) == len(set(names)), (p.name, names)
            seen[p.name] = seen.get(p.name, 0) + 1
    dup = [k for k, v in seen.items() if v > 1]
    assert not dup, f"duplicate part names: {dup}"

if __name__ == "__main__":
    from bosses import java_model, camel
    out, root_dir = sys.argv[1], sys.argv[2]
    os.makedirs(out, exist_ok=True)
    jdir = f"{root_dir}/src/main/java/com/elementalweapons/client/boss/gen"
    tdir = f"{root_dir}/src/main/resources/assets/elementalweapons/textures/entity"
    reg = []
    for bid, fn in SPECS.items():
        roots, mats = fn()
        check_unique(roots)
        from hd import hdify
        hdify(roots, HD)
        root = Part("root", children=roots, field=False)
        for tw in (512, 1024):
            try: pack(root, tw, tw); break
            except SystemExit: continue
        tex, glow = paint(root, tw, tw, mats, seed=sum(map(ord, bid)))
        tex.save(f"{tdir}/{bid}.png"); glow.save(f"{tdir}/{bid}_glow.png")
        cls = camel(bid) + "Model"
        open(f"{jdir}/{cls}.java", "w").write(java_model(cls, roots, tw, tw).replace("tools/bossgen/bosses.py", "tools/bossgen/bosses2.py"))
        reg.append((bid, cls))
        print(bid, tw)
    regs = "\n".join(f"""		layer = new ModelLayerLocation(ElementalWeaponsMod.id("{b}"), "main");
		ModelLayerRegistry.registerModelLayer(layer, {c}::createBodyLayer);
		{"registerVoltra(layer, " + c + "::new);" if b == "voltra" else f'register("{b}", layer, {c}::new);'}""" for b, c in reg)
    imports = "\n".join(f"import com.elementalweapons.client.boss.gen.{c};" for _, c in reg)
    open(f"{root_dir}/src/main/java/com/elementalweapons/client/boss/GeneratedBossModels.java", "w").write(f"""// 자동 생성 파일 (tools/bossgen/bosses2.py)
package com.elementalweapons.client.boss;

import java.util.function.Function;

import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderers;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.boss.BossDef;
import com.elementalweapons.boss.BossDefs;
import com.elementalweapons.boss.ModEntities;
{imports}

public final class GeneratedBossModels {{
	private GeneratedBossModels() {{}}

	public static void register() {{
		ModelLayerLocation layer;
{regs}
	}}

	private static void register(String id, ModelLayerLocation layer, Function<ModelPart, GenericBossModel> factory) {{
		BossDef def = BossDefs.ALL.get(id);
		EntityRenderers.register(ModEntities.BOSSES.get(id),
				ctx -> new GenericBossRenderer<>(ctx, layer, factory, id, def.renderScale / {HD}F, def.shadow));
	}}

	private static void registerVoltra(ModelLayerLocation layer, Function<ModelPart, GenericBossModel> factory) {{
		EntityRenderers.register(ModEntities.VOLTRA, ctx -> new GenericBossRenderer<>(ctx, layer, factory, "voltra", 2.7F / {HD}F, 2.0F));
	}}
}}
""")
    print("done")
