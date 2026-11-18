"""
보스 15종 모델 정의 + 전체 생성
사용법: python3 tools/bossgen/bosses.py <미리보기 폴더> <프로젝트 루트>
"""
import math, sys, os, json, random
sys.path.insert(0, os.path.dirname(__file__))
from bossgen import Box, Part, pack, paint, preview, java_parts, fnum, all_parts
from materials import M, FACE, hexc, mix, cl
from PIL import Image

R = math.radians
B = Box
def P(name, pivot=(0,0,0), rot=(0,0,0), boxes=(), children=()):
    return Part(name, pivot, rot, boxes, children)

def rays(n, length, start, mat, prefix="ray"):
    return [P(f"{prefix}{i}", (0,0,0), (0,0,R(i*360/n)), [B(-0.5, -start-length, -0.5, 1, length, 1, mat)]) for i in range(n)]
def orbit_ring(n, radius, size, mats, prefix="orb"):
    out = []
    for i in range(n):
        a = i * 2 * math.pi / n
        m = mats[i % len(mats)]
        out.append(P(f"{prefix}{i}", (math.cos(a)*radius, 0, math.sin(a)*radius), (0,0,0), [B(-size/2, -size/2, -size/2, size, size, size, m)]))
    return out
def chain(names, pivots, boxes_list):
    """꼬리/목 사슬: 앞의 파트의 자식으로 이어짐"""
    parts = [P(n, pv, (0,0,0), bx) for n, pv, bx in zip(names, pivots, boxes_list)]
    for i in range(len(parts) - 1): parts[i].children.append(parts[i+1])
    return parts[0]

BOSSES = {}

# ---------------- 화염룡 이그니스 ----------------
def ignis():
    m = {"scale": M(0xB32D1A, "scale", accent=0x7A1A10), "belly": M(0xE8A33A, "metal"), "bone": M(0xEFE2C2, "stone", noise=5),
         "wing": M(0x6E1A12, "veins", glow=0xFF8A2A), "fire": M(0xFFB040, glow_all=True)}
    m["face"] = FACE(m["scale"], 0xFFE040)
    head = P("head", (0,0,-7), boxes=[B(-4,-4,-9,8,7,9,"scale",{"front":m["face"]}), B(-3,-1,-14,6,4,5,"scale"),
             B(-3.5,-7,-3,1,4,1,"bone"), B(2.5,-7,-3,1,4,1,"bone"), B(-1,-1.5,-14.5,2,1,1,"fire")],
             children=[P("jaw", (0,3,-5), boxes=[B(-3,0,-9,6,2,9,"scale")])])
    neck = P("neck1", (0,-2,-10), (R(-30),0,0), [B(-3,-3,-8,6,6,8,"scale")], [P("neck2", (0,0,-8), (R(20),0,0), [B(-2.5,-2.5,-7,5,5,7,"scale")], [head])])
    tail = chain(["tail1","tail2","tail3","tail4","tail5"], [(0,-1,10),(0,0,8),(0,0,8),(0,0,7),(0,0,6)],
                 [[B(-3,-3,0,6,6,8,"scale")],[B(-2.5,-2.5,0,5,5,8,"scale")],[B(-2,-2,0,4,4,7,"scale")],[B(-1.5,-1.5,0,3,3,6,"scale")],[B(-2,-0.5,0,4,1,4,"fire")]])
    rw = P("right_wing", (-6,-4,-4), boxes=[B(-14,-1,-1,14,2,2,"bone"), B(-18,-0.5,0,18,1,14,"wing")])
    lw = P("left_wing", (6,-4,-4), boxes=[B(0,-1,-1,14,2,2,"bone"), B(0,-0.5,0,18,1,14,"wing")])
    body = P("body", (0,10,0), boxes=[B(-6,-5,-10,12,10,20,"scale"), B(-5,4,-9,10,2,18,"belly"), B(-1,-7,-6,2,2,14,"bone")],
             children=[neck, tail, rw, lw])
    legs = [P("right_leg",(-5,14,6),boxes=[B(-2,0,-2,4,10,4,"scale")]), P("left_leg",(5,14,6),boxes=[B(-2,0,-2,4,10,4,"scale")]),
            P("right_arm",(-5,14,-6),boxes=[B(-2,0,-2,4,10,4,"scale")]), P("left_arm",(5,14,-6),boxes=[B(-2,0,-2,4,10,4,"scale")])]
    return [body] + legs, m, (128, 128)

# ---------------- 심해의 여왕 마리나 ----------------
def marina():
    m = {"skin": M(0x7FC3C9, "fur", noise=4), "tail": M(0x1E8C8C, "scale", accent=0x0E5050), "hair": M(0x1B3A8A, "cloth"),
         "gold": M(0xF0C860, "metal", edge=0xA07820), "pearl": M(0xF4F0FF, "crystal", glow=0xBFE8FF), "fin": M(0x5FE0E0, "veins", glow=0xBFFFFF),
         "water": M(0x6FD8FF, glow_all=True)}
    m["face"] = FACE(m["skin"], 0xBFFFFF)
    head = P("head", (0,-10,0), boxes=[B(-4,-8,-4,8,8,8,"skin",{"front":m["face"]}), B(-4.5,-8.5,1,9,12,4,"hair"),
             B(-3,-11,-1,1,3,1,"pearl"), B(-0.5,-13,-1,1,5,1,"pearl"), B(2,-11,-1,1,3,1,"pearl"), B(-3.5,-9,-1.5,7,1,3,"gold")])
    trident = [B(-2,-14,-0.5,1,24,1,"gold"), B(-4,-16,-0.5,5,1,1,"gold"), B(-4,-19,-0.5,1,3,1,"pearl"), B(-2,-21,-0.5,1,5,1,"pearl"), B(0,-19,-0.5,1,3,1,"pearl")]
    ra = P("right_arm", (-6,-9,0), boxes=[B(-3,-1,-1.5,3,11,3,"skin")] + trident)
    la = P("left_arm", (6,-9,0), boxes=[B(0,-1,-1.5,3,11,3,"skin")])
    tail = chain(["tail1","tail2","tail3","tail4","tail5"], [(0,1,0),(0,5,0),(0,5,0),(0,5,0),(0,4,0)],
                 [[B(-4.5,0,-3,9,5,6,"tail")],[B(-3.5,0,-2.5,7,5,5,"tail")],[B(-2.5,0,-2,5,5,4,"tail")],[B(-1.5,0,-1.5,3,4,3,"tail")],[B(-5,0,-0.5,10,4,1,"fin")]])
    ring = P("ring", (0,-8,0), children=orbit_ring(8, 10, 2, ["water"]))
    body = P("body", (0,-2,0), boxes=[B(-5,-9,-3,10,10,6,"skin"), B(-5.5,-8,-3.5,11,3,7,"pearl"), B(-5.5,0,-3.5,11,2,7,"gold")],
             children=[head, ra, la, tail, ring])
    return [body], m, (128, 128)

# ---------------- 고대수 실바 ----------------
def silva():
    m = {"bark": M(0x5A3A1E, "bark"), "leaf": M(0x2E7D32, "leaf", accent=0x66BB6A, glow=0xFFB7D5), "moss": M(0x6B8E23, "stone"),
         "flower": M(0xFF8FC0, glow_all=True)}
    m["face"] = FACE(m["bark"], 0x9CFF6A, mouth=0x1A0F05)
    head = P("head", (0,-18,0), boxes=[B(-9,-10,-7,18,10,14,"leaf"), B(-6,-15,-5,12,5,10,"leaf"), B(-3,-17,-3,6,2,6,"flower")])
    ra = P("right_arm", (-6,-15,0), boxes=[B(-3,0,-2,4,14,4,"bark"), B(-5,12,-1,3,4,2,"leaf"), B(-1,13,-1,2,4,2,"bark")])
    la = P("left_arm", (6,-15,0), boxes=[B(-1,0,-2,4,14,4,"bark"), B(2,12,-1,3,4,2,"leaf"), B(-1,13,-1,2,4,2,"bark")])
    body = P("body", (0,8,0), boxes=[B(-5,-18,-4,10,18,8,"bark",{"front":m["face"]}), B(-6,-6,-5,12,4,10,"moss")], children=[head, ra, la])
    legs = [P("right_leg", (-3,8,0), boxes=[B(-3,0,-3,6,16,6,"bark"), B(-4,14,-5,8,2,3,"bark")]),
            P("left_leg", (3,8,0), boxes=[B(-3,0,-3,6,16,6,"bark"), B(-4,14,-5,8,2,3,"bark")])]
    return [body] + legs, m, (128, 128)

# ---------------- 폭풍새 제피로스 ----------------
def zephyros():
    m = {"feather": M(0xE8F4F2, "feather", accent=0x4FB8A8), "dark": M(0x2F6F73, "feather", accent=0x9FF3E8), "gold": M(0xF0C040, "metal"),
         "tip": M(0xB8FFF0, glow_all=True)}
    m["face"] = FACE(m["feather"], 0x60FFE0)
    head = P("head", (0,-3,-7), boxes=[B(-3.5,-6,-6,7,7,7,"feather",{"front":m["face"]}), B(-1.5,-3,-10,3,3,4,"gold"), B(-0.5,-10,-3,1,5,6,"dark")])
    rw = P("right_wing", (-5,-4,-2), boxes=[B(-12,-1,-1,12,2,3,"dark"), B(-22,0,-1,22,1,12,"feather"), B(-24,0,0,3,1,10,"tip")])
    lw = P("left_wing", (5,-4,-2), boxes=[B(0,-1,-1,12,2,3,"dark"), B(0,0,-1,22,1,12,"feather"), B(21,0,0,3,1,10,"tip")])
    tail = chain(["tail1","tail2"], [(0,-2,7),(0,0,10)], [[B(-4,-1,0,8,2,10,"dark")],[B(-5,-0.5,0,10,1,6,"tip")]])
    body = P("body", (0,10,0), boxes=[B(-5,-5,-7,10,9,14,"feather"), B(-4,-3,-7.5,8,6,1,"dark")], children=[head, rw, lw, tail])
    legs = [P("right_leg", (-3,14,2), boxes=[B(-1,0,-1,2,8,2,"gold"), B(-2,8,-3,4,1,4,"gold")]),
            P("left_leg", (3,14,2), boxes=[B(-1,0,-1,2,8,2,"gold"), B(-2,8,-3,4,1,4,"gold")])]
    return [body] + legs, m, (128, 128)

# ---------------- 그림자 군주 녹스 ----------------
def nox():
    m = {"cloak": M(0x1C1426, "cloth"), "trim": M(0x5A1A8A, "cloth"), "bone": M(0xD8D0C0, "stone", noise=5), "metal": M(0x3A3A48, "metal"),
         "void": M(0x9A3BEA, glow_all=True), "inner": M(0x05000A, None, noise=2)}
    m["face"] = FACE(m["inner"], 0xC060FF)
    head = P("head", (0,-10,0), boxes=[B(-5,-10,-5,10,10,10,"cloak",{"front":m["face"]}), B(-3.5,-13,-1,1,3,1,"bone"), B(-0.5,-14,-1,1,4,1,"bone"), B(2.5,-13,-1,1,3,1,"bone")])
    scythe = [B(-1.5,-14,-0.5,1,30,1,"metal"), B(-12,-15,-0.5,11,2,1,"void"), B(-12,-13,-0.5,4,1,1,"void")]
    ra = P("right_arm", (-7,-8,0), boxes=[B(-3,-1,-2.5,5,12,5,"cloak"), B(-2.5,11,-1.5,3,3,3,"bone")] + scythe)
    la = P("left_arm", (7,-8,0), boxes=[B(-2,-1,-2.5,5,12,5,"cloak"), B(-0.5,11,-1.5,3,3,3,"bone")])
    halo = P("halo", (0,-10,6), children=rays(6, 8, 7, "void"))
    tail = chain(["tail1","tail2","tail3"], [(0,2,0),(0,8,0),(0,8,0)],
                 [[B(-6.5,0,-4.5,13,8,9,"cloak"), B(-6.6,6,-4.6,13,2,9,"trim")],[B(-5,0,-3.5,10,8,7,"cloak")],[B(-3,0,-2,6,6,4,"void")]])
    body = P("body", (0,0,0), boxes=[B(-6,-10,-4,12,12,8,"cloak"), B(-2,-9,-4.5,4,10,1,"trim")], children=[head, ra, la, halo, tail])
    return [body], m, (128, 128)

# ---------------- 성기사 루미엘 ----------------
def lumiel():
    m = {"armor": M(0xF2F0E8, "rune", edge=0xE0B040, glow=0xFFE27A), "gold": M(0xF0C040, "metal"), "wing": M(0xFFFFFF, "feather", accent=0xFFE89A),
         "blade": M(0xFFF4C0, glow_all=True), "cloth": M(0x3050C0, "cloth")}
    m["face"] = FACE(M(0xF2F0E8, "metal"), 0xFFE27A, mode="two")
    head = P("head", (0,-10,0), boxes=[B(-4,-8,-4,8,8,8,"armor",{"front":m["face"]}), B(-0.5,-12,-3,1,4,8,"gold")])
    ra = P("right_arm", (-7,-8,0), boxes=[B(-3.5,-3,-3.5,6,4,7,"gold"), B(-2.5,0,-2,4,11,4,"armor"), B(-1.5,10,-14,2,2,16,"blade"), B(-3,9.5,-1,5,3,2,"gold")])
    la = P("left_arm", (7,-8,0), boxes=[B(-2.5,-3,-3.5,6,4,7,"gold"), B(-1.5,0,-2,4,11,4,"armor"), B(2.5,2,-5,1,10,10,"armor"), B(3,5,-2,1,4,4,"blade")])
    rw = P("right_wing", (-3,-8,3), boxes=[B(-18,-6,0,18,12,1,"wing")])
    lw = P("left_wing", (3,-8,3), boxes=[B(0,-6,0,18,12,1,"wing")])
    ring = P("ring", (0,-21,0), children=orbit_ring(8, 5, 1.5, ["blade"]))
    skirt = P("tail1", (0,4,0), boxes=[B(-5,0,-3,10,7,6,"armor"), B(-1.5,0,-3.5,3,8,1,"cloth")])
    body = P("body", (0,0,0), boxes=[B(-5.5,-10,-3,11,11,6,"armor"), B(-4.5,1,-2.5,9,3,5,"gold")], children=[head, ra, la, rw, lw, ring, skirt])
    legs = [P("right_leg", (-2.5,11,0), boxes=[B(-2,0,-2,4,13,4,"armor")]), P("left_leg", (2.5,11,0), boxes=[B(-2,0,-2,4,13,4,"armor")])]
    return [body] + legs, m, (128, 128)

# ---------------- 수정 골렘 ----------------
def crystal_golem():
    m = {"stone": M(0x6A6070, "stone"), "crystal": M(0xE070F0, "crystal", glow=0xFFB0FF), "core": M(0xFFC0FF, glow_all=True)}
    m["face"] = FACE(m["stone"], 0xFF9CFF, mode="one")
    head = P("head", (0,-12,-2), boxes=[B(-3.5,-6,-4,7,6,7,"stone",{"front":m["face"]})])
    ra = P("right_arm", (-9,-10,0), boxes=[B(-6,-2,-3.5,7,18,7,"stone"), B(-6.5,14,-4,8,6,8,"crystal")])
    la = P("left_arm", (9,-10,0), boxes=[B(-1,-2,-3.5,7,18,7,"stone"), B(-1.5,14,-4,8,6,8,"crystal")])
    body = P("body", (0,8,0), boxes=[B(-8,-12,-5,16,14,10,"stone"), B(-2,-8,-5.5,4,4,1,"core"),
             B(-6,-17,-1,3,6,3,"crystal"), B(2,-18,0,3,7,3,"crystal"), B(-1,-20,1,3,9,3,"crystal"), B(5,-15,-2,2,4,2,"crystal")],
             children=[head, ra, la])
    legs = [P("right_leg", (-4,8,0), boxes=[B(-3.5,0,-3.5,7,16,7,"stone")]), P("left_leg", (4,8,0), boxes=[B(-3.5,0,-3.5,7,16,7,"stone")])]
    return [body] + legs, m, (128, 128)

# ---------------- 용암 거미 여왕 ----------------
def lava_queen():
    m = {"shell": M(0x221A1A, "lava", glow=0xFF7A1A), "leg": M(0x2E2020, "metal"), "gold": M(0xF0B040, "metal"), "eye": M(0xFF3020, glow_all=True)}
    m["face"] = FACE(M(0x221A1A, "stone"), 0xFF4020, mode="many")
    legs = []
    for i, z in enumerate([-3,-1,1,3]):
        spread = R(-30 + i * 20)
        legs.append(P(f"leg{i+1}", (-5,0,z), (0, spread, R(30)), [B(-12,-1,-1,12,2,2,"leg")],
                      [P(f"foot{i+1}", (-12,0,0), (0,0,R(60)), [B(-1,0,-1,2,10,2,"leg")])]))
        legs.append(P(f"leg{i+5}", (5,0,z), (0, -spread, R(-30)), [B(0,-1,-1,12,2,2,"leg")],
                      [P(f"foot{i+5}", (12,0,0), (0,0,R(-60)), [B(-1,0,-1,2,10,2,"leg")])]))
    head = P("head", (0,-1,-5), boxes=[B(-4,-3,-6,8,6,6,"shell",{"front":m["face"]}), B(-3,-5,-4,1,2,1,"gold"), B(-0.5,-6,-4,1,3,1,"gold"), B(2,-5,-4,1,2,1,"gold")],
             children=[P("jaw", (0,2,-6), boxes=[B(-3,0,-2,2,3,2,"gold"), B(1,0,-2,2,3,2,"gold")])])
    abdomen = P("tail1", (0,-1,5), boxes=[B(-7,-5,0,14,10,14,"shell")])
    body = P("body", (0,14,0), boxes=[B(-5,-4,-5,10,7,10,"shell")], children=[head, abdomen] + legs)
    return [body], m, (128, 128)

# ---------------- 얼음 거인 ----------------
def frost_giant():
    m = {"skin": M(0xA8C8E0, "fur", noise=5), "fur": M(0xF0F0F0, "fur"), "ice": M(0x9FE8FF, "crystal", glow=0xDDF8FF), "beard": M(0xE8F4FF, "fur")}
    m["face"] = FACE(m["skin"], 0x7FE0FF)
    head = P("head", (0,-14,0), boxes=[B(-4,-8,-4,8,8,8,"skin",{"front":m["face"]}), B(-4,-3,-5,8,6,2,"beard"),
             B(-3.5,-11,-1,1,3,1,"ice"), B(-1.5,-12,-1,1,4,1,"ice"), B(0.5,-12,-1,1,4,1,"ice"), B(2.5,-11,-1,1,3,1,"ice")])
    ra = P("right_arm", (-8,-12,0), boxes=[B(-5,-2,-3,5,16,6,"skin"), B(-5.5,-3,-3.5,6,5,7,"fur"), B(-5.5,12,-2.5,6,12,5,"ice")])
    la = P("left_arm", (8,-12,0), boxes=[B(0,-2,-3,5,16,6,"skin"), B(-0.5,-3,-3.5,6,5,7,"fur")])
    body = P("body", (0,8,0), boxes=[B(-7,-14,-4,14,14,8,"skin"), B(-7.5,-2,-4.5,15,4,9,"fur")], children=[head, ra, la])
    legs = [P("right_leg", (-3.5,8,0), boxes=[B(-3,0,-3,6,16,6,"fur")]), P("left_leg", (3.5,8,0), boxes=[B(-3,0,-3,6,16,6,"fur")])]
    return [body] + legs, m, (128, 128)

# ---------------- 사막 스핑크스 ----------------
def sphinx():
    m = {"fur": M(0xD8B060, "fur"), "skin": M(0xC89058, "fur", noise=4), "nemes": M(0x2A4AA0, "split", accent=0xF0C040),
         "gold": M(0xF0C040, "metal"), "wing": M(0xE8C878, "feather", accent=0xF8E0A0), "sun": M(0xFFD860, glow_all=True)}
    m["face"] = FACE(m["skin"], 0x60E0FF)
    head = P("head", (0,-4,-10), boxes=[B(-6,-9,-3,12,12,6,"nemes"), B(-4,-8,-4,8,8,7,"skin",{"front":m["face"]}), B(-1,0,-5,2,4,1,"gold")])
    halo = P("halo", (0,-10,-4), children=rays(10, 5, 9, "sun"))
    rw = P("right_wing", (-5,-5,-4), boxes=[B(-16,-1,0,16,1,10,"wing")])
    lw = P("left_wing", (5,-5,-4), boxes=[B(0,-1,0,16,1,10,"wing")])
    tail = chain(["tail1","tail2"], [(0,-3,10),(0,0,10)], [[B(-1,-1,0,2,2,10,"fur")],[B(-1.5,-1.5,0,3,3,4,"gold")]])
    body = P("body", (0,12,0), boxes=[B(-6,-5,-10,12,10,20,"fur"), B(-6,5,-9,4,7,4,"fur"), B(2,5,-9,4,7,4,"fur"), B(-6,5,5,4,7,4,"fur"), B(2,5,5,4,7,4,"fur")],
             children=[head, halo, rw, lw, tail])
    return [body], m, (128, 128)

# ---------------- 증기 거신 ----------------
def steam_colossus():
    m = {"copper": M(0xB86A3A, "metal", accent=0x7A3A18), "steel": M(0x5A5E66, "metal"), "fire": M(0xFF8A2A, glow_all=True),
         "dark": M(0x2A2A30, "metal"), "water": M(0x6FD8FF, glow_all=True)}
    m["face"] = FACE(m["steel"], 0x6FD8FF)
    head = P("head", (0,-14,-2), boxes=[B(-4,-6,-4,8,6,7,"steel",{"front":m["face"]})])
    ra = P("right_arm", (-9,-12,0), boxes=[B(-5,-2,-3,6,10,6,"copper"), B(-4,8,-2,4,4,4,"steel"), B(-7,12,-4,10,8,8,"steel")])
    la = P("left_arm", (9,-12,0), boxes=[B(-1,-2,-3,6,14,6,"copper"), B(0,12,-2,4,6,4,"dark"), B(0.5,17,-1.5,3,2,3,"water")])
    body = P("body", (0,8,0), boxes=[B(-8,-14,-6,16,16,12,"copper"), B(-4,-8,-7,8,6,1,"fire"),
             B(-6,-21,-2,3,7,3,"dark"), B(3,-23,-1,3,9,3,"dark")], children=[head, ra, la])
    legs = [P("right_leg", (-4,8,0), boxes=[B(-3.5,0,-3.5,7,16,7,"copper")]), P("left_leg", (4,8,0), boxes=[B(-3.5,0,-3.5,7,16,7,"copper")])]
    return [body] + legs, m, (128, 128)

# ---------------- 뇌운 와이번 ----------------
def storm_wyvern():
    m = {"scale": M(0x1E2A5A, "veins", glow=0x9FF3FF), "belly": M(0x7A8AB0, "scale"), "horn": M(0xBFF8FF, glow_all=True),
         "wing": M(0x2A3A7A, "veins", glow=0xDFFFF6)}
    m["face"] = FACE(M(0x1E2A5A, "scale"), 0xBFF8FF)
    head = P("head", (0,0,-7), boxes=[B(-3.5,-3,-8,7,6,8,"scale",{"front":m["face"]}), B(-2.5,-1,-12,5,3,4,"scale"),
             B(-3,-6,-2,1,4,1,"horn"), B(2,-6,-2,1,4,1,"horn")], children=[P("jaw", (0,2,-5), boxes=[B(-2.5,0,-7,5,1,7,"belly")])])
    neck = P("neck1", (0,-1,-9), (R(-25),0,0), [B(-2.5,-2.5,-7,5,5,7,"scale")], [head])
    rw = P("right_wing", (-5,-3,-3), boxes=[B(-16,-1,-1,16,2,2,"scale"), B(-20,-0.5,0,20,1,16,"wing")])
    lw = P("left_wing", (5,-3,-3), boxes=[B(0,-1,-1,16,2,2,"scale"), B(0,-0.5,0,20,1,16,"wing")])
    tail = chain(["tail1","tail2","tail3","tail4"], [(0,-1,9),(0,0,7),(0,0,7),(0,0,6)],
                 [[B(-2.5,-2.5,0,5,5,7,"scale")],[B(-2,-2,0,4,4,7,"scale")],[B(-1.5,-1.5,0,3,3,6,"scale")],[B(-2.5,-0.5,0,5,1,5,"horn")]])
    body = P("body", (0,10,0), boxes=[B(-5,-4,-9,10,8,18,"scale"), B(-4,3,-8,8,2,16,"belly")], children=[neck, rw, lw, tail])
    legs = [P("right_leg", (-3,14,4), boxes=[B(-1.5,0,-1.5,3,8,3,"scale")]), P("left_leg", (3,14,4), boxes=[B(-1.5,0,-1.5,3,8,3,"scale")])]
    return [body] + legs, m, (128, 128)

# ---------------- 황혼의 쌍둥이 ----------------
def twilight_twins():
    m = {"robe": M(0x1A0A2A, "split", accent=0xF4EEDC), "light": M(0xF4EEDC, "cloth"), "dark": M(0x1A0A2A, "cloth"),
         "lglow": M(0xFFE27A, glow_all=True), "dglow": M(0xB060FF, glow_all=True), "wingd": M(0x2A1040, "feather", accent=0x7A2AC0),
         "wingl": M(0xFFFFFF, "feather", accent=0xFFE89A)}
    h1 = P("head1", (-3,-10,0), boxes=[B(-3,-6,-3,6,6,6,"dark",{"front":FACE(m["dark"], 0xC070FF)})])
    h2 = P("head2", (3,-10,0), boxes=[B(-3,-6,-3,6,6,6,"light",{"front":FACE(m["light"], 0xFFC830)})])
    ra = P("right_arm", (-7,-8,0), boxes=[B(-3,-1,-2,4,11,4,"dark"), B(-2,10,-12,1,2,14,"dglow")])
    la = P("left_arm", (7,-8,0), boxes=[B(-1,-1,-2,4,11,4,"light"), B(1,-6,-0.5,1,22,1,"lglow"), B(0,-9,-1.5,3,3,3,"lglow")])
    rw = P("right_wing", (-3,-8,3), boxes=[B(-16,-6,0,16,12,1,"wingd")])
    lw = P("left_wing", (3,-8,3), boxes=[B(0,-6,0,16,12,1,"wingl")])
    ring = P("ring", (0,-19,0), children=orbit_ring(8, 6, 1.5, ["lglow","dglow"]))
    tail = chain(["tail1","tail2","tail3"], [(0,1,0),(0,10,0),(0,8,0)],
                 [[B(-6,0,-3.5,12,10,7,"robe")],[B(-4.5,0,-2.5,9,8,5,"robe")],[B(-2,0,-1.5,4,5,3,"dglow")]])
    body = P("body", (0,0,0), boxes=[B(-6,-10,-3,6,11,6,"dark"), B(0,-10,-3,6,11,6,"light")], children=[h1, h2, ra, la, rw, lw, ring, tail])
    return [body], m, (128, 128)

# ---------------- 독꽃 히드라 ----------------
def hydra():
    m = {"scale": M(0x1E4A2A, "scale", accent=0x0E2A16), "back": M(0x2A5A30, "veins", glow=0xFF7AC0), "petal": M(0xFF6AB0, glow_all=True),
         "belly": M(0x8AA060, "scale")}
    def head(i, x):
        return P(f"head{i}", (0,0,-10), boxes=[B(-3,-3,-6,6,5,6,"scale",{"front":FACE(m["scale"], 0xFF60C0)}), B(-4,-5,-2,8,2,4,"petal"), B(-2,0,-9,4,2,3,"belly")])
    necks = [P("neck1", (-5,-5,-6), (R(-35),0,R(20)), [B(-2,-2,-10,4,4,10,"scale")], [head(1,-5)]),
             P("neck2", (0,-6,-6), (R(-45),0,0), [B(-2,-2,-12,4,4,12,"scale")], [P("head2", (0,0,-12), boxes=[B(-3,-3,-6,6,5,6,"scale",{"front":FACE(m["scale"], 0xFF60C0)}), B(-4,-5,-2,8,2,4,"petal")])]),
             P("neck3", (5,-5,-6), (R(-35),0,R(-20)), [B(-2,-2,-10,4,4,10,"scale")], [head(3,5)])]
    tail = chain(["tail1","tail2","tail3"], [(0,0,7),(0,0,8),(0,0,8)], [[B(-3,-3,0,6,6,8,"scale")],[B(-2,-2,0,4,4,8,"scale")],[B(-3,-3,0,6,6,4,"petal")]])
    body = P("body", (0,12,0), boxes=[B(-8,-6,-7,16,12,14,"back"), B(-7,5,-6,14,1,12,"belly"), B(-8,6,4,4,6,4,"scale"), B(4,6,4,4,6,4,"scale")],
             children=necks + [tail])
    legs = [P("right_leg", (-6,18,-4), boxes=[B(-2.5,0,-2.5,5,6,5,"scale")]), P("left_leg", (6,18,-4), boxes=[B(-2.5,0,-2.5,5,6,5,"scale")])]
    return [body] + legs, m, (128, 128)

# ---------------- 원소의 근원 프리마 ----------------
def prima():
    m = {"crystal": M(0xF4F0FF, "crystal", glow=0xFFFFFF), "core": M(0xFFFFFF, glow_all=True)}
    els = [0x9FF3FF, 0xFF7A1A, 0x3FA0FF, 0x7BD36B, 0xDFFFF6, 0x9A3BEA, 0xFFE27A]
    for i, c in enumerate(els): m[f"e{i}"] = M(c, glow_all=True)
    head = P("head", (0,-14,0), boxes=[B(-3,-8,-3,6,8,6,"crystal",{"front":FACE(m["crystal"], 0xFFFFFF)}), B(-1,-12,-1,2,4,2,"core")])
    ring = P("ring", (0,-6,0), children=[P(f"shard{i}", (math.cos(i*2*math.pi/7)*12, 0, math.sin(i*2*math.pi/7)*12), (0,0,0),
                                            [B(-1.5,-4,-1.5,3,8,3,f"e{i}")]) for i in range(7)])
    halo = P("halo", (0,-8,5), children=rays(14, 6, 10, "core"))
    ra = P("right_arm", (-9,-10,0), boxes=[B(-2,0,-2,4,10,4,"crystal"), B(-3,10,-3,6,6,6,"core")])
    la = P("left_arm", (9,-10,0), boxes=[B(-2,0,-2,4,10,4,"crystal"), B(-3,10,-3,6,6,6,"core")])
    tail = chain(["tail1","tail2","tail3"], [(0,2,0),(0,6,0),(0,6,0)], [[B(-3,0,-3,6,6,6,"crystal")],[B(-2,0,-2,4,6,4,"crystal")],[B(-1,0,-1,2,6,2,"core")]])
    body = P("body", (0,0,0), boxes=[B(-4,-14,-4,8,16,8,"crystal"), B(-1.5,-10,-4.5,3,6,1,"core")], children=[head, ring, halo, ra, la, tail])
    return [body], m, (128, 128)

SPECS = {"ignis": ignis, "marina": marina, "silva": silva, "zephyros": zephyros, "nox": nox, "lumiel": lumiel,
         "crystal_golem": crystal_golem, "lava_queen": lava_queen, "frost_giant": frost_giant, "sphinx": sphinx,
         "steam_colossus": steam_colossus, "storm_wyvern": storm_wyvern, "twilight_twins": twilight_twins, "hydra": hydra, "prima": prima}

def camel(s): return "".join(w.capitalize() for w in s.split("_"))

def java_model(cls, roots, tw, th):
    lines = []
    for p in roots: java_parts(p, "root", lines)
    paths = []
    def walk(p, path):
        path = path + [p.name]; paths.append(path)
        for c in p.children: walk(c, path)
    for p in roots: walk(p, [])
    parr = ",\n\t\t\t".join("{" + ", ".join(f"\"{x}\"" for x in path) + "}" for path in paths)
    return f"""// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class {cls} extends GenericBossModel {{
	private static final String[][] PATHS = {{
			{parr}
	}};

	public {cls}(ModelPart root) {{
		super(root, PATHS);
	}}

	public static LayerDefinition createBodyLayer() {{
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
{chr(10).join(lines)}
		return LayerDefinition.create(mesh, {tw}, {th});
	}}
}}
"""

def check_unique(roots):
    # 같은 부모 아래 같은 이름 금지 + 애니메이션 이름 중복 경고
    seen = {}
    for r in roots:
        for p in all_parts(r):
            names = [c.name for c in p.children]
            assert len(names) == len(set(names)), (p.name, names)
            seen[p.name] = seen.get(p.name, 0) + 1
    dup = [k for k, v in seen.items() if v > 1]
    assert not dup, f"duplicate part names: {dup}"

if __name__ == "__main__":
    out, root_dir = sys.argv[1], sys.argv[2]
    os.makedirs(out, exist_ok=True)
    jdir = f"{root_dir}/src/main/java/com/elementalweapons/client/boss/gen"; os.makedirs(jdir, exist_ok=True)
    tdir = f"{root_dir}/src/main/resources/assets/elementalweapons/textures/entity"; os.makedirs(tdir, exist_ok=True)
    reg = []
    sheet = Image.new("RGBA", (5 * 300, 3 * 300), (34, 34, 46, 255))
    for i, (bid, fn) in enumerate(SPECS.items()):
        roots, mats, (tw, th) = fn()
        check_unique(roots)
        root = Part("root", children=roots, field=False)
        try:
            pack(root, tw, th)
        except SystemExit:
            tw, th = 256, 256; pack(root, tw, th)
        tex, glow = paint(root, tw, th, mats, seed=hash(bid) % 1000)
        tex.save(f"{tdir}/{bid}.png"); glow.save(f"{tdir}/{bid}_glow.png")
        cls = camel(bid) + "Model"
        open(f"{jdir}/{cls}.java", "w").write(java_model(cls, roots, tw, th))
        reg.append((bid, cls))
        pv = preview(root, tex, size=300, yaw=-35, pitch=14, glow=glow)
        sheet.paste(pv, ((i % 5) * 300, (i // 5) * 300))
        print(bid, tw, th)
    sheet.save(f"{out}/bosses_sheet.png")
    # 등록 코드
    regs = "\n".join(f"""		layer = new ModelLayerLocation(ElementalWeaponsMod.id("{b}"), "main");
		ModelLayerRegistry.registerModelLayer(layer, {c}::createBodyLayer);
		register("{b}", layer, {c}::new);""" for b, c in reg)
    imports = "\n".join(f"import com.elementalweapons.client.boss.gen.{c};" for _, c in reg)
    open(f"{root_dir}/src/main/java/com/elementalweapons/client/boss/GeneratedBossModels.java", "w").write(f"""// 자동 생성 파일 (tools/bossgen/bosses.py)
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
				ctx -> new GenericBossRenderer<>(ctx, layer, factory, id, def.renderScale, def.shadow));
	}}
}}
""")
    print("done")
