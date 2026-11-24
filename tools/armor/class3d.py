"""직업별 입체 복장 생성기 (v2)
상자마다 텍스처 자리를 따로 잡아(겹치지 않게) 면마다 음영·테두리·재질 무늬를 2배 해상도로 직접 그림.
출력: ClassArmorMesh.java (직업별 LayerDefinition), armor_deco/class/<직업>_<속성>(.png/_glow.png), 평면 갑옷 텍스처, 장비 JSON
"""
import json, math, os, random
from PIL import Image

HERE = os.path.dirname(__file__)
ROOT = os.path.join(HERE, "..", "..", "src/main")
ASSETS = ROOT + "/resources/assets/elementalweapons"
T = ASSETS + "/textures/entity"
JAVA = ROOT + "/java/com/elementalweapons/client/armor/ClassArmorMesh.java"
S = 2              # 텍스처 배율 (모형 1칸 = 2픽셀)
ATLAS = 256        # 모형 단위 텍스처 크기

ELS = ["electric", "fire", "water", "grass", "wind", "dark", "light", "neutral"]
EL = {   # 주색, 짙은색, 빛
    "electric": ((62, 78, 168), (20, 24, 58), (255, 232, 84)),
    "fire":     ((168, 36, 28), (58, 12, 10), (255, 146, 46)),
    "water":    ((30, 96, 176), (8, 28, 64), (96, 214, 255)),
    "grass":    ((44, 124, 60), (14, 42, 20), (255, 164, 214)),
    "wind":     ((96, 182, 170), (30, 70, 68), (176, 255, 228)),
    "dark":     ((80, 34, 118), (22, 8, 36), (206, 96, 255)),
    "light":    ((236, 220, 170), (124, 100, 52), (255, 228, 112)),
    "neutral":  ((118, 120, 132), (40, 42, 50), (200, 222, 255)),
}
GOLD = (226, 178, 64)
SILVER = (196, 202, 214)
CLOTH_CLASSES = {"mage", "assassin", "support", "reaper", "ranger"}

def mix(a, b, t): return tuple(max(0, min(255, int(round(a[i] + (b[i] - a[i]) * t)))) for i in range(3))
def mul(c, f): return tuple(max(0, min(255, int(v * f))) for v in c)

def palette(cls, el):
    m, d, g = EL[el]
    P = dict(glow=g, gem=g, gold=GOLD, metal=SILVER, bone=(226, 216, 190), leather=(108, 72, 42), fur=(132, 104, 76), feather=mix((210, 200, 180), m, 0.35))
    if cls == "mage":
        P.update(cloth=m, cloth2=mix(m, (240, 230, 210), 0.55), dark=d, trim=GOLD, leather=(86, 52, 34))
    elif cls == "support":
        P.update(cloth=(242, 240, 232), cloth2=m, dark=mix((210, 212, 222), m, 0.15), trim=GOLD, feather=(250, 246, 230))
    elif cls == "assassin":
        P.update(cloth=mix((30, 30, 38), m, 0.2), cloth2=m, dark=(14, 14, 18), trim=mix(SILVER, m, 0.3), leather=(44, 38, 40), metal=(176, 182, 196))
    elif cls == "reaper":
        P.update(cloth=mix((24, 20, 30), m, 0.16), cloth2=mix((40, 34, 46), m, 0.3), dark=(8, 6, 10), trim=mix((90, 84, 96), g, 0.35), metal=(110, 106, 118))
    elif cls == "ranger":
        P.update(cloth=mix((78, 100, 48), m, 0.35), cloth2=mix((120, 88, 52), m, 0.15), dark=(52, 36, 22), trim=mix((150, 108, 58), m, 0.15), feather=mix((176, 150, 110), m, 0.3))
    elif cls == "knight":
        P.update(cloth=m, cloth2=mix(m, (255, 255, 255), 0.6), dark=d, trim=GOLD, fur=(236, 234, 228))
    elif cls == "warrior":
        P.update(cloth=mix(m, (100, 40, 20), 0.4), cloth2=mix(m, (60, 40, 30), 0.5), dark=(54, 34, 20), trim=(160, 118, 64), metal=(140, 140, 148))
    return P

# ------------------------------------------------------------------ 모양 정의
class Part:
    def __init__(self, bone, box, mat, pivot=(0, 0, 0), rot=(0, 0, 0), parent=None, slot="c", anim="n", deco=None):
        self.bone, self.box, self.mat, self.pivot, self.rot, self.parent = bone, box, mat, pivot, rot, parent
        self.slot, self.anim, self.deco = slot, anim, deco or {}
        self.children = []; self.uv = None; self.name = None

class Outfit:
    def __init__(self, cls):
        self.cls = cls; self.parts = []
    def add(self, bone, box, mat, pivot=(0, 0, 0), rot=(0, 0, 0), parent=None, slot="c", anim="n", **deco):
        p = Part(bone, box, mat, pivot, rot, parent, slot, anim, deco)
        if parent is not None: parent.children.append(p); p.slot = parent.slot
        self.parts.append(p); return p

def ring(o, bone, n, r, center, size, mat, slot, anim, plane="xy", phase=0.0, parent_pose=None, **deco):
    """빛 고리: 가운데 축 부위 하나에 조각 n개"""
    hub = o.add(bone, (0, 0, 0, 0, 0, 0), "none", center, (0, 0, 0), slot=slot, anim=anim)
    w, h, d = size
    for k in range(n):
        a = phase + 2 * math.pi * k / n
        if plane == "xy":
            o.add(bone, (-w / 2, -h / 2, -d / 2, w, h, d), mat, (math.cos(a) * r, math.sin(a) * r, 0), (0, 0, a + math.pi / 2), parent=hub, **deco)
        else:
            o.add(bone, (-w / 2, -h / 2, -d / 2, w, h, d), mat, (math.cos(a) * r, 0, math.sin(a) * r), (0, -a + math.pi / 2, 0), parent=hub, **deco)
    return hub

def robe_skirt(o, mat, trim, length, open_front=False, inner=None, slot="l", width=9.8):
    hw = width / 2
    if open_front:
        for s in (-1, 1):
            f = o.add("body", (-1.6, 0, -0.5, 3.2, length, 1), mat, (s * 3.2, 12, -2.95), slot=slot, anim="f", bands="bottom", side=s)
        if inner:
            o.add("body", (-1.7, 0, -0.4, 3.4, length - 1, 0.8), inner, (0, 12, -2.85), slot=slot, anim="f", bands="bottom")
    else:
        o.add("body", (-hw, 0, -0.5, width, length, 1), mat, (0, 12, -2.95), slot=slot, anim="f", bands="bottom", stripe=True)
    o.add("body", (-hw, 0, -0.5, width, length + 0.5, 1), mat, (0, 12, 2.95), slot=slot, anim="b", bands="bottom")
    for s in (-1, 1):
        o.add("body", (-0.5, 0, -2.9, 1, length, 5.8), mat, (s * 4.85, 12, 0), slot=slot, anim="n", bands="bottom")

def hood(o, cloth, rim, deep=0.0, peak=0.0, mat_in="dark"):
    g = deep + 0.7
    o.add("head", (-4.8 - g, -9.0 - g, -4.6 - g, 9.6 + 2 * g, 1.2, 9.6 + 2 * g), cloth, slot="h", bands="none")
    for s in (-1, 1):
        o.add("head", (-0.5, 0, -4.6 - g, 1, 8.6 + g, 9.6 + 2 * g), cloth, (s * (4.4 + g), -8.6 - g, 0), slot="h", folds=True)
    o.add("head", (-4.8 - g, -8.8 - g, 4.1 + g, 9.6 + 2 * g, 9.0 + g, 1), cloth, slot="h", bands="bottom")
    o.add("head", (-5.0 - g, -9.4 - g, -5.0 - g, 10 + 2 * g, 1.4, 1), rim, slot="h")
    for s in (-1, 1):
        o.add("head", (-0.6, 0, -0.5, 1.2, 8.6 + g, 1), rim, (s * (4.5 + g), -9.0 - g, -4.6 - g), slot="h")
    if peak:
        o.add("head", (-3, -0.6, -2.4, 6, 1.0, 2.6), cloth, (0, -9.2 - g, -4.7 - g), (-0.3, 0, 0), slot="h")
    t1 = o.add("head", (-2.4, -0.7, 0, 4.8, 1.4, 3.6), cloth, (0, -8.8 - g, 4.0 + g), (-0.9, 0, 0), slot="h")
    t2 = o.add("head", (-1.5, -0.6, 0, 3, 1.2, 3.2), cloth, (0, 0, 3.4), (-0.45, 0, 0), parent=t1)
    o.add("head", (-0.7, -0.5, 0, 1.4, 1.0, 2.4), cloth, (0, 0, 3.0), (-0.4, 0, 0), parent=t2)

def sleeve(o, mat, trim, flare=1.0, glow_ring=False):
    for s, bone, x0 in ((-1, "right_arm", -3.0), (1, "left_arm", -1.0)):
        cx = x0 + 2
        o.add(bone, (cx - 3.3 - 0.3 * flare, 3.5, -3.3 - 0.3 * flare, 6.6 + 0.6 * flare, 4, 6.6 + 0.6 * flare), mat, slot="c", folds=True)
        o.add(bone, (cx - 3.5 - 0.6 * flare, 7.3, -3.5 - 0.6 * flare, 7.0 + 1.2 * flare, 3.2, 7.0 + 1.2 * flare), mat, slot="c", bands="bottom")
        if glow_ring:
            o.add(bone, (cx - 3.15, 9.6, -3.15, 6.3, 0.7, 6.3), "glow", slot="c")

def boots(o, mat, cuff, curl=False):
    for bone in ("right_leg", "left_leg"):
        o.add(bone, (-3.2, 8.4, -3.2, 6.4, 1.4, 6.4), cuff, slot="f")
        o.add(bone, (-2.6, 10.9, -4.1, 5.2, 1.3, 1.2), mat, slot="f")
        if curl:
            c1 = o.add(bone, (-0.9, -1.0, -1.6, 1.8, 1.0, 1.8), mat, (0, 12.1, -4.1), (0.5, 0, 0), slot="f")
            o.add(bone, (-0.5, -1.2, -0.8, 1.0, 1.2, 1.0), "gold", (0, -1.0, -1.4), (0.9, 0, 0), parent=c1)

def build():
    outfits = {}

    # ===================== 마법사: 대마도사 =====================
    o = Outfit("mage")
    brim = o.add("head", (-6.8, -9.4, -6.8, 13.6, 1, 13.6), "cloth", slot="h", bands="edge")
    for s in (-1, 1):
        o.add("head", (-5.5, 0, -1.4, 11, 0.8, 1.6), "cloth", (0, -9.2, 6.6 * s), (0.28 * -s, 0, 0), parent=brim, bands="none")
    o.add("head", (-5.3, -11.4, -5.3, 10.6, 2.2, 10.6), "trim", parent=brim)
    o.add("head", (-1.1, -11.2, -5.8, 2.2, 1.8, 0.6), "gem", parent=brim)
    for s in (-1, 1):
        o.add("head", (-0.6, 0, -0.2, 1.2, 7, 0.4), "cloth2", (1.8 * s, -9.6, 5.4), (0.25, 0, 0.1 * s), parent=brim, anim="t", bands="bottom")
    c1 = o.add("head", (-4.1, -4, -4.1, 8.2, 4, 8.2), "cloth", (0, -11.2, 0.3), parent=brim, folds=True)
    c2 = o.add("head", (-3.1, -4, -3.1, 6.2, 4, 6.2), "cloth", (0, -4, 0), (-0.24, 0, 0), parent=c1, folds=True)
    c3 = o.add("head", (-2.1, -3.6, -2.1, 4.2, 3.6, 4.2), "cloth", (0, -3.8, 0), (-0.38, 0, 0.08), parent=c2, folds=True)
    c4 = o.add("head", (-1.2, -3.2, -1.2, 2.4, 3.2, 2.4), "cloth", (0, -3.4, 0), (-0.55, 0, 0.1), parent=c3)
    c5 = o.add("head", (-0.6, -2.4, -0.6, 1.2, 2.4, 1.2), "cloth", (0, -3.0, 0), (-0.7, 0, 0), parent=c4)
    o.add("head", (-0.75, -0.75, -0.75, 1.5, 1.5, 1.5), "glow", (0, -2.6, 0), (0.78, 0.78, 0), parent=c5)
    # 높은 깃 + 두 겹 어깨 망토 + 브로치
    o.add("body", (-4.6, -3.4, 0, 9.2, 3.6, 1.1), "trim", (0, 0.2, 1.6), (-0.22, 0, 0))
    for s in (-1, 1):
        o.add("body", (-0.55, -2.6, -1.8, 1.1, 2.8, 3.6), "trim", (s * 4.3, 0.1, 0.2), (0, 0, 0.18 * s))
    o.add("body", (-5.6, -0.8, -3.4, 11.2, 3.4, 6.8), "cloth", bands="bottom")
    o.add("body", (-6.0, 2.2, -3.6, 12.0, 2.1, 7.2), "dark", bands="bottom")
    for k in range(5):
        o.add("body", (-0.8, -0.8, -0.3, 1.6, 1.6, 0.6), "trim", (-4 + k * 2, 4.4, -3.6), (0, 0, 0.785))
    o.add("body", (-1.3, -1.3, -0.5, 2.6, 2.6, 0.8), "gold", (0, 1.4, -3.7), (0, 0, 0.785))
    o.add("body", (-0.7, -0.7, -0.4, 1.4, 1.4, 0.6), "gem", (0, 1.4, -4.1), (0, 0, 0.785))
    # 허리띠 + 마법서 + 물약
    o.add("body", (-5.2, 9.2, -3.2, 10.4, 1.4, 6.4), "leather", stitch=True)
    o.add("body", (-1.1, 8.9, -3.6, 2.2, 2.0, 0.6), "gold")
    book = o.add("body", (-1.6, 0, -0.9, 3.2, 4, 1.8), "leather", (-5.6, 10.2, -0.4), (0, 0, 0.12))
    o.add("body", (-1.7, 0.4, -1.0, 0.4, 3.2, 2.0), "gold", parent=book)
    o.add("body", (-0.9, 1.1, -1.0, 1.8, 1.8, 0.2), "glow", parent=book)
    for k in range(2):
        v = o.add("body", (-0.6, 0, -0.6, 1.2, 1.8, 1.2), "gem", (3.2 + k * 1.5, 10.4, -3.5), (0, 0, 0))
        o.add("body", (-0.4, -0.5, -0.4, 0.8, 0.5, 0.8), "leather", parent=v)
    # 겉옷 자락(앞이 트임) + 속 로브 + 긴 망토
    robe_skirt(o, "cloth", "trim", 11.2, open_front=True, inner="cloth2")
    cape = o.add("body", (-4.8, 0, 0, 9.6, 22, 0.8), "cloth", (0, 0.4, 3.2), anim="c", bands="bottom", runes=True)
    o.add("body", (-4.9, 0, -0.1, 9.8, 1.4, 1.0), "trim", parent=cape)
    # 시그니처: 등 뒤에서 도는 룬 고리 (안·바깥 두 겹)
    hub = ring(o, "body", 16, 8.0, (0, 5, 7.0), (2.8, 0.9, 0.6), "glow", "c", "r")
    ring(o, "body", 8, 5.2, (0, 5, 7.1), (1.4, 1.4, 0.6), "gem", "c", "R", phase=0.39)
    sleeve(o, "cloth", "trim", flare=1.3, glow_ring=True)
    boots(o, "dark", "trim", curl=True)
    outfits["mage"] = o

    # ===================== 서포터: 대사제 =====================
    o = Outfit("support")
    m = o.add("head", (-3.3, -15.2, -2.1, 6.6, 6.6, 4.2), "cloth", slot="h", bands="bottom")
    o.add("head", (-2.3, -2.3, -2.0, 4.6, 4.6, 4.0), "cloth", (0, -15.2, 0), (0, 0, 0.785), parent=m)
    for s in (-1, 1):
        o.add("head", (-0.4, -6.4, -2.3, 0.8, 6.6, 4.6), "gold", (3.3 * s, -8.8, 0), (0, 0, 0.12 * -s), parent=m)
    o.add("head", (-0.5, -14.6, -2.5, 1, 4.8, 0.5), "glow", parent=m)
    o.add("head", (-1.8, -13.3, -2.5, 3.6, 1, 0.5), "glow", parent=m)
    o.add("head", (-0.8, -17.6, -0.8, 1.6, 1.6, 1.6), "gem", (0, 0, 0), (0.78, 0.78, 0), parent=m)
    o.add("head", (-5.3, -8.8, -5.3, 10.6, 1.2, 10.6), "trim", slot="h")
    veil = o.add("head", (-4.4, 0, -0.3, 8.8, 9.5, 0.5), "cloth", (0, -8.2, 5.3), (0.12, 0, 0), slot="h", anim="t", bands="bottom")
    ring(o, "head", 18, 5.6, (0, -6, 6.4), (2.0, 0.6, 0.4), "glow", "h", "r")
    # 어깨 망토(물결 테), 제의(앞·뒤), 영대, 금 날개 장식
    o.add("body", (-5.6, -0.8, -3.4, 11.2, 4.6, 6.8), "cloth", bands="bottom")
    for k in range(6):
        o.add("body", (-0.8, -0.8, -0.3, 1.6, 1.6, 0.6), "trim", (-4.5 + k * 1.8, 4.0, -3.5), (0, 0, 0.785))
    ch = o.add("body", (-3.4, 0, -0.3, 6.8, 13, 0.5), "cloth2", (0, 3.0, -3.35), anim="n", bands="edge", cross=True)
    o.add("body", (-3.4, 0, -0.3, 6.8, 15, 0.5), "cloth2", (0, 3.0, 3.35), anim="c", bands="edge", cross=True)
    for s in (-1, 1):
        st = o.add("body", (-0.8, 0, -0.3, 1.6, 17, 0.5), "trim", (4.0 * s, 0, -3.6), anim="n")
        o.add("body", (-1.2, 15.6, -0.4, 2.4, 1.6, 0.6), "gold", parent=st)
    o.add("body", (-5.2, 9.4, -3.2, 10.4, 1.0, 6.4), "gold")
    for s in (-1, 1):
        w = o.add("body", (-0.4, -5, 0, 0.8, 5, 2.6), "metal", (1.6 * s, 3.5, 3.6), (-0.5, 0.75 * s, 0.6 * s), deco_feather=True)
        for k in range(3):
            o.add("body", (-0.3, -4 + k, 0, 0.6, 4 - k, 1.6), "feather", (0, 0, 1.8 + k * 1.2), (0.1 * k, 0, 0), parent=w)
    robe_skirt(o, "cloth", "trim", 11.4)
    sleeve(o, "cloth", "trim", flare=1.6)
    boots(o, "cloth", "trim")
    outfits["support"] = o

    # ===================== 암살자: 그림자 처형자 =====================
    o = Outfit("assassin")
    hood(o, "cloth", "dark", deep=0.2, peak=1)
    o.add("head", (-5.1, -4.6, -5.4, 10.2, 4.6, 0.6), "dark", slot="h", stitch=True)
    o.add("head", (-3.2, -5.4, -5.45, 6.4, 0.6, 0.4), "glow", slot="h")
    o.add("body", (-5.4, -1.4, -3.4, 10.8, 2.8, 6.8), "cloth2", folds=True)
    t1 = o.add("body", (-1.0, 0, 0, 2.0, 14, 0.4), "cloth2", (1.6, 0.8, 3.4), (0.12, 0, 0.1), anim="t", bands="bottom")
    o.add("body", (-1.0, 0, 0, 2.0, 10, 0.4), "cloth2", (-1.4, 0.8, 3.4), (0.12, 0, -0.08), anim="t", bands="bottom")
    for s in (-1, 1):   # 가슴 X자 벨트
        b = o.add("body", (-0.6, -7.2, -0.25, 1.2, 14.4, 0.5), "leather", (0, 5.0, -3.25), (0, 0, 0.62 * s), stitch=True)
    o.add("body", (-1.0, -1.0, -0.4, 2.0, 2.0, 0.6), "metal", (0, 5.0, -3.6), (0, 0, 0.785))
    for k in range(3):
        o.add("body", (-0.3, -1.4, -0.2, 0.6, 2.8, 0.3), "metal", (-2.4 + k * 0.9, 3.0 + k * 0.6, -3.65), (0, 0, 0.62))
    for s in (-1, 1):   # 등의 엇갈린 단검
        sh = o.add("body", (-0.8, -4, -0.5, 1.6, 7, 1.0), "leather", (0, 6, 3.7), (0, 0, 0.7 * s))
        o.add("body", (-1.3, -5.4, -0.7, 2.6, 0.7, 1.4), "metal", parent=sh)
        o.add("body", (-0.45, -8.5, -0.4, 0.9, 3.1, 0.8), "leather", parent=sh)
        o.add("body", (-0.15, 3, -0.55, 0.3, 3.8, 0.3), "glow", parent=sh)
    o.add("body", (-5.2, 9.0, -3.2, 10.4, 1.6, 6.4), "leather", stitch=True)
    o.add("body", (2.0, 9.6, -4.0, 2.2, 2.2, 1.0), "leather")
    for x, an in ((-2.4, "f"), (2.4, "f")):
        o.add("body", (-1.9, 0, -0.3, 3.8, 7, 0.6), "cloth", (x, 11.4, -2.95), slot="l", anim=an, bands="bottom")
    o.add("body", (-4.6, 0, -0.3, 9.2, 8.5, 0.6), "cloth", (0, 11.4, 2.95), slot="l", anim="b", bands="bottom")
    # 왼어깨 가죽 어깨갑 + 칼깃
    p = o.add("left_arm", (-2.2, -3.8, -3.5, 6.8, 3.4, 7.0), "leather", stitch=True)
    for k in range(3):
        o.add("left_arm", (-0.3, -3.5 + k * 0.5, -0.4, 0.6, 3.5 - k * 0.5, 0.8), "metal", (3.6 + k * 0.5, -3.6, -1.8 + k * 1.8), (0.3 - k * 0.3, 0, 0.6), parent=p)
    for bone, x0, s in (("right_arm", -3.0, -1), ("left_arm", -1.0, 1)):
        o.add(bone, (x0 - 1.3, 5.5, -3.3, 6.6, 4.4, 6.6), "leather", stitch=True)
        if bone == "right_arm":
            o.add(bone, (x0 - 1.7, 4.8, -0.3, 0.4, 7.0, 0.6), "glow")
    for bone in ("right_leg", "left_leg"):
        o.add(bone, (-2.4, 2.5, -3.1, 4.8, 4, 0.6), "leather", slot="l", stitch=True)
        o.add(bone, (-3.1, 7.6, -3.1, 6.2, 1, 6.2), "dark", slot="f")
    outfits["assassin"] = o

    # ===================== 궁수: 숲의 파수꾼 =====================
    o = Outfit("ranger")
    hood(o, "cloth", "trim", deep=0.1, peak=1)
    f = o.add("head", (-0.4, -5, -0.2, 0.8, 5, 0.4), "feather", (4.6, -9.0, 2.0), (-0.5, 0, 0.45), slot="h")
    o.add("head", (-0.3, -1.2, -0.25, 0.6, 1.2, 0.5), "glow", (0, -5, 0), parent=f)
    # 깃털 망토 (어깨부터 겹겹이)
    for row in range(3):
        n = 7 - row
        for k in range(n):
            x = -4.2 + k * (8.4 / max(1, n - 1)) if n > 1 else 0
            o.add("body", (-0.9, 0, -0.25, 1.8, 6 + row * 2.5, 0.5), "feather", (x, -0.2 + row * 2.6, 3.3 + row * 0.25), (0.08 + row * 0.04, 0, (x / 4.2) * 0.18), anim="c")
    for s in (-1, 1):
        o.add("body", (-1.0, 0, -0.25, 2.0, 5, 0.5), "feather", (s * 5.4, -0.4, 0.4), (0, 0, 0.35 * s), anim="t")
    o.add("body", (-5.5, -1.0, -3.4, 11, 2.8, 6.8), "cloth", bands="bottom")
    o.add("body", (-1.0, 0.2, -3.9, 2.0, 2.0, 0.6), "gem")
    # 잎사귀 어깨갑 (왼쪽)
    for k in range(3):
        o.add("left_arm", (-1.6, -0.4, -2.8, 3.2, 0.8, 5.6 - k), "cloth2", (2.6 + k * 0.9, -3.0 + k * 1.3, 0), (0, 0, 0.45), stitch=True)
    o.add("left_arm", (-0.3, -0.3, -2.0, 0.6, 0.6, 4.0), "glow", (3.2, -3.4, 0), (0, 0, 0.45))
    # 가슴끈 + 화살통
    o.add("body", (-0.6, -7.6, -0.25, 1.2, 15.2, 0.5), "leather", (0, 5.2, -3.25), (0, 0, 0.6), stitch=True)
    q = o.add("body", (-1.6, 0, -1.6, 3.2, 10.5, 3.2), "leather", (2.6, 0.2, 5.6), (0.12, 0, -0.5), stitch=True)
    o.add("body", (-1.8, 0.2, -1.8, 3.6, 1.0, 3.6), "trim", parent=q)
    o.add("body", (-1.8, 8.6, -1.8, 3.6, 1.0, 3.6), "trim", parent=q)
    for k in range(4):
        a = o.add("body", (-0.25, -4.2, -0.25, 0.5, 4.2, 0.5), "dark", (-0.9 + (k % 2) * 1.6, 0.4, -0.8 + (k // 2) * 1.6), (0.05 * k, 0, 0.1 - 0.06 * k), parent=q)
        o.add("body", (-0.6, -2.6, -0.1, 1.2, 2.6, 0.2), "feather", (0, -3.6, 0), (0, 0.5 * k, 0), parent=a)
        o.add("body", (-0.35, -1.0, -0.35, 0.7, 1.0, 0.7), "glow", (0, -4.2, 0), (0.78, 0, 0), parent=a)
    o.add("body", (-5.2, 9.0, -3.2, 10.4, 1.4, 6.4), "leather", stitch=True)
    o.add("body", (1.6, 9.5, -4.0, 2.2, 2.4, 1.0), "leather", stitch=True)
    hn = o.add("body", (-0.6, 0, -0.6, 1.2, 3.2, 1.2), "bone", (-4.0, 10.0, -3.6), (0, 0, -0.9))
    o.add("body", (-0.8, 3.0, -0.8, 1.6, 0.6, 1.6), "gold", parent=hn)
    for x in (-2.3, 2.3):
        o.add("body", (-2.0, 0, -0.3, 4.0, 6.5, 0.6), "cloth", (x, 11.2, -2.95), slot="l", anim="f", bands="bottom")
    for bone, x0 in (("right_arm", -3.0), ("left_arm", -1.0)):
        o.add(bone, (x0 - 1.3, 5.4, -3.3, 6.6, 4.6, 6.6), "leather", stitch=True)
        o.add(bone, (x0 - 1.4, 9.6, -3.4, 6.8, 0.8, 6.8), "trim")
    for bone in ("right_leg", "left_leg"):
        o.add(bone, (-3.3, 7.4, -3.3, 6.6, 1.8, 6.6), "fur", slot="f")
        o.add(bone, (-1.6, 3.2, -3.1, 3.2, 2.4, 0.6), "leather", slot="l", stitch=True)
    outfits["ranger"] = o

    # ===================== 사신: 영혼 수확자 =====================
    o = Outfit("reaper")
    hood(o, "cloth", "dark", deep=0.7, peak=0)
    o.add("head", (-6.2, 0, -2.0, 12.4, 2.4, 2.2), "cloth", (0, -10.6, -6.0), (-0.3, 0, 0), slot="h")
    for s in (-1, 1):
        o.add("head", (-0.7, -0.5, -0.3, 1.4, 1.0, 0.4), "glow", (1.9 * s, -4.6, -5.4), (0, 0, 0.25 * s), slot="h")
        h1 = o.add("head", (-0.9, -3.2, -0.9, 1.8, 3.2, 1.8), "dark", (3.2 * s, -10.6, -0.6), (-0.25, 0, 0.55 * s), slot="h")
        h2 = o.add("head", (-0.7, -3.0, -0.7, 1.4, 3.0, 1.4), "dark", (0, -3.0, 0), (-0.55, 0, -0.25 * s), parent=h1)
        h3 = o.add("head", (-0.5, -2.6, -0.5, 1.0, 2.6, 1.0), "bone", (0, -3.0, 0), (-0.6, 0, -0.2 * s), parent=h2)
        o.add("head", (-0.25, -1.4, -0.25, 0.5, 1.4, 0.5), "glow", (0, -2.6, 0), (-0.4, 0, 0), parent=h3)
    # 갈비뼈 흉갑 + 등뼈
    o.add("body", (-0.6, 0, -0.5, 1.2, 9, 0.8), "bone", (0, 0.8, -3.3))
    for k in range(4):
        for s in (-1, 1):
            L = 3.4 - k * 0.3
            o.add("body", ((0 if s > 0 else -L), -0.4, -0.35, L, 0.8, 0.7), "bone", (0.4 * s, 2.0 + k * 1.9, -3.3), (0, 0, (0.28 + k * 0.05) * s))
    for k in range(6):
        o.add("body", (-0.6, -0.6, -0.4, 1.2, 1.2, 0.9), "bone", (0, 1.0 + k * 1.8, 3.2), (0, 0, 0.785))
    # 찢어진 망토 두 겹 + 빛나는 끝
    lens = [19, 15, 21, 16, 20, 14, 18]
    for k, L in enumerate(lens):
        st = o.add("body", (-0.75, 0, 0, 1.5, L, 0.5), "cloth", (-4.5 + k * 1.5, 0.2, 3.6), (0.05, 0, (k - 3) * 0.04), anim="t", bands="bottom")
        o.add("body", (-0.5, L - 0.2, -0.05, 1.0, 1.0, 0.6), "glow", parent=st)
    for k, L in enumerate([12, 9, 13, 10, 12]):
        o.add("body", (-1.0, 0, 0, 2.0, L, 0.5), "cloth2", (-4 + k * 2, 0.0, 4.2), (0.12, 0, (k - 2) * 0.06), anim="t", bands="bottom")
    for s in (-1, 1):
        o.add("body", (-0.3, 0, -2.2, 0.6, 15, 4.4), "cloth", (5.3 * s, 0.4, 0.4), (0, 0, 0.05 * s), anim="t", bands="bottom")
    o.add("body", (-5.3, -1.2, -3.3, 10.6, 2.4, 6.6), "dark", folds=True)
    # 사슬 허리띠 + 영혼 등불
    for k in range(9):
        o.add("body", (-0.55, -0.4, -0.3, 1.1, 0.8, 0.6), "metal", (-4.0 + k, 9.6 + (0.3 if k % 2 else 0), -3.35))
    ln = o.add("body", (-0.15, 0, -0.15, 0.3, 1.6, 0.3), "metal", (-5.6, 10.2, -1.2), anim="t")
    lb = o.add("body", (-1.0, 1.6, -1.0, 2.0, 0.5, 2.0), "metal", parent=ln)
    o.add("body", (-0.8, 2.1, -0.8, 1.6, 2.2, 1.6), "glow", parent=ln)
    o.add("body", (-1.0, 4.3, -1.0, 2.0, 0.5, 2.0), "metal", parent=ln)
    for k in range(3):
        o.add("body", (-1.0, 0, -0.3, 2.0, 9 - k * 1.5 + (3 if k == 1 else 0), 0.6), "cloth", (-2.6 + k * 2.6, 11.6, -2.95), slot="l", anim="f", bands="bottom")
    # 해골 어깨갑 (왼쪽), 뼈 팔찌 (오른쪽), 찢어진 소매
    sk = o.add("left_arm", (-2.2, -3.9, -3.5, 6.8, 3.2, 7.0), "dark", stitch=True)
    skull = o.add("left_arm", (-1.9, -3.6, -1.9, 3.8, 3.6, 3.8), "bone", (2.2, -3.8, 0), (0, -0.5, 0.25), parent=sk)
    o.add("left_arm", (-1.4, 0, -2.0, 2.8, 1.0, 2.8), "bone", parent=skull)
    for s in (-1, 1):
        o.add("left_arm", (-0.5, -0.5, -0.2, 1.0, 1.0, 0.3), "glow", (0.85 * s, -1.9, -2.0), parent=skull)
    for bone, x0 in (("right_arm", -3.0), ("left_arm", -1.0)):
        o.add(bone, (-1.2, 0, -0.3, 2.4, 7, 0.5), "cloth", (x0 + 2, 5.5, 3.2), anim="t", bands="bottom")
    for k in range(4):
        o.add("right_arm", (-4.3, 6.0 + k * 1.1, -3.3, 6.6, 0.6, 6.6), "bone")
    # 몸 주위를 도는 영혼불
    hub = o.add("body", (0, 0, 0, 0, 0, 0), "none", (0, 6, 0), slot="c", anim="o")
    for k in range(3):
        a = k * 2 * math.pi / 3
        o.add("body", (-0.7, -0.7, -0.7, 1.4, 1.4, 1.4), "glow", (math.cos(a) * 8, -2 + k * 2.5, math.sin(a) * 8), (0.78, 0.6, 0), parent=hub)
        o.add("body", (-0.4, 0.6, -0.4, 0.8, 1.6, 0.8), "glow", (math.cos(a) * 8, -2 + k * 2.5, math.sin(a) * 8), parent=hub)
    boots(o, "dark", "bone")
    outfits["reaper"] = o

    # ===================== 기사: 성기사 단장 (판금 위) =====================
    o = Outfit("knight")
    cape = o.add("body", (-5.2, 0, 0, 10.4, 21, 0.8), "cloth", (0, -0.4, 4.4), anim="c", bands="bottom", crest=True)
    o.add("body", (-5.4, -0.2, -0.2, 10.8, 1.6, 1.2), "gold", parent=cape)
    for s in (-1, 1):
        o.add("body", (-1.3, -1.3, -1.0, 2.6, 2.6, 1.6), "gold", (4.4 * s, -0.8, 4.1), (0, 0, 0.785))
        o.add("body", (-0.6, -0.6, -1.3, 1.2, 1.2, 0.6), "gem", (4.4 * s, -0.8, 3.8), (0, 0, 0.785))
    o.add("body", (-6.0, -2.4, 2.4, 12, 2.6, 2.8), "fur")
    tab = o.add("body", (-2.7, 0, -0.3, 5.4, 10.5, 0.5), "cloth", (0, 10.6, -4.35), slot="l", anim="f", bands="bottom", crest=True)
    o.add("body", (-2.7, 0, -0.3, 5.4, 9.5, 0.5), "cloth", (0, 10.6, 4.3), slot="l", anim="b", bands="bottom")
    for k in range(3):
        o.add("head", (-0.6, -5, -0.5, 1.2, 5, 1.6), "cloth" if k != 1 else "fur", (-0.9 + k * 0.9, -10.6, -0.5 + k * 0.3), (-0.6 - k * 0.15, 0, (k - 1) * 0.15), slot="h", anim="t")
    outfits["knight"] = o

    # ===================== 전사: 전장의 군주 (판금 위) =====================
    o = Outfit("warrior")
    o.add("body", (-6.4, -2.0, -3.8, 12.8, 3.4, 7.6), "fur")
    o.add("body", (-5.6, -3.6, 1.6, 11.2, 3.6, 3.0), "fur")
    cl = o.add("body", (-5.4, 0, 0, 10.8, 14, 0.9), "fur", (0, 0.6, 4.2), anim="c", bands="none")
    for k in range(5):
        o.add("body", (-1.0, 0, -0.1, 2.0, 2.6 + (k % 2) * 1.4, 1.0), "fur", (-4.2 + k * 2.1, 13.6, 0), parent=cl)
    for s in (-1, 1):   # 쇠사슬 (어깨에서 허리로)
        for k in range(7):
            o.add("body", (-0.45, -0.55, -0.25, 0.9, 1.1, 0.5), "metal", (s * (3.6 - k * 0.55), 0.6 + k * 1.3, -3.9), (0, 0, 0.5 * s + (0.6 if k % 2 else 0)))
    o.add("body", (-5.0, 9.2, -3.1, 10, 2.2, 6.2), "leather", stitch=True)
    sk = o.add("body", (-1.6, -1.6, -1.6, 3.2, 3.0, 3.2), "bone", (0, 10.2, -4.4))
    o.add("body", (-1.1, 1.4, -1.6, 2.2, 0.9, 2.4), "bone", parent=sk)
    for s in (-1, 1):
        o.add("body", (-0.45, -0.45, -0.2, 0.9, 0.9, 0.3), "glow", (0.75 * s, -0.4, -1.7), parent=sk)
        hr = o.add("body", (-0.4, -1.8, -0.4, 0.8, 1.8, 0.8), "bone", (1.4 * s, -1.2, -0.6), (0, 0, 0.9 * s), parent=sk)
    o.add("body", (-2.2, 0, -0.3, 4.4, 9, 0.5), "cloth", (0, 11.8, -4.4), slot="l", anim="f", bands="bottom")
    o.add("body", (-2.6, 0, -0.3, 5.2, 8, 0.5), "cloth", (0, 11.8, 3.3), slot="l", anim="b", bands="bottom")
    o.add("right_arm", (-6.9, -0.6, -4.0, 3.2, 2.2, 8.0), "fur")
    o.add("left_arm", (3.7, -0.6, -4.0, 3.2, 2.2, 8.0), "fur")
    for bone in ("right_arm", "left_arm"):
        o.add(bone, (-2.6 if bone == "right_arm" else -0.6, 6.2, -2.6, 5.2, 3.2, 5.2), "leather", stitch=True)
    for bone in ("right_leg", "left_leg"):
        o.add(bone, (-2.8, 6.2, -2.8, 5.6, 1.8, 5.6), "fur", slot="f")
    outfits["warrior"] = o
    return outfits

# ------------------------------------------------------------------ 텍스처 자리 배정
def uvsize(p):
    x, y, z, w, h, d = p.box
    return math.ceil(2 * (w + d) + 0.01) + 1, math.ceil(d + h + 0.01) + 1

def pack(o):
    items = sorted([p for p in o.parts if p.mat != "none"], key=lambda p: -uvsize(p)[1])
    x = y = rowh = 0
    for p in items:
        w, h = uvsize(p)
        if x + w > ATLAS: x, y, rowh = 0, y + rowh, 0
        p.uv = (x, y); x += w; rowh = max(rowh, h)
    if y + rowh > ATLAS: raise SystemExit(f"{o.cls}: 텍스처 자리 부족 ({y + rowh})")

# ------------------------------------------------------------------ 칠하기
def faces(p):
    """면 목록: (이름, u, v, w, h) — 모형 단위"""
    x, y, z, w, h, d = p.box
    u, v = p.uv
    return [("top", u + d, v, w, d), ("bottom", u + d + w, v, w, d),
            ("side", u, v + d, d, h), ("front", u + d, v + d, w, h),
            ("side", u + d + w, v + d, d, h), ("back", u + 2 * d + w, v + d, w, h)]

def paint_face(px, gp, P, p, fname, u, v, w, h, rnd):
    X0, Y0 = int(round(u * S)), int(round(v * S)); W, H = max(1, int(round(w * S))), max(1, int(round(h * S)))
    mat = p.mat; dec = p.deco
    base = P.get(mat, P["cloth"]) if mat not in ("gold",) else P["gold"]
    glowing = mat in ("glow", "gem")
    for j in range(H):
        for i in range(W):
            X, Y = X0 + i, Y0 + j
            if X >= ATLAS * S or Y >= ATLAS * S: continue
            ty = j / max(1, H - 1)
            # 1) 재질 무늬
            if mat in ("cloth", "cloth2", "dark"):
                c = mul(base, 1.10 - 0.28 * ty if fname not in ("top", "bottom") else 1.0)
                if dec.get("folds", True) and fname in ("front", "back", "side") and W >= 6:
                    f = math.sin((i + 0.5) / W * math.pi * max(1, W // 6) * 2)
                    c = mul(c, 1 + 0.07 * f)
                c = mul(c, 1 + rnd.uniform(-0.035, 0.035))
                if (i + j) % 2 == 0: c = mul(c, 0.985)            # 직조
            elif mat in ("trim", "gold"):
                g = base
                c = mul(g, 1.18 - 0.4 * ty) if H > 2 else g
                if ((i // 2) + (j // 2)) % 3 == 0 and W > 3 and H > 3: c = mul(c, 0.78)   # 새김 무늬
                if i == 1 or j == 1: c = mix(c, (255, 250, 220), 0.35)
            elif mat == "metal":
                c = mul(base, 1.15 - 0.35 * ty)
                if j % 3 == 0: c = mul(c, 1.06)
                if W > 5 and H > 5 and (i in (1, W - 2)) and (j in (1, H - 2)): c = mix(c, (255, 255, 255), 0.5)   # 리벳
            elif mat == "leather":
                c = mul(base, 1.12 - 0.3 * ty)
                c = mul(c, 1 + rnd.uniform(-0.08, 0.08))
                if dec.get("stitch") and W > 5 and H > 5 and (i == 2 or i == W - 3 or j == 2 or j == H - 3) and (i + j) % 3 != 0:
                    c = mix(c, (220, 196, 150), 0.55)
            elif mat == "fur":
                streak = rnd.random()
                c = mul(base, 0.75 + 0.45 * (1 - ty) * (0.6 + 0.4 * streak))
                if (i * 7 + j * 3) % 5 == 0: c = mul(c, 0.8)
                if j > H * 0.7 and rnd.random() < 0.3: c = mix(c, (240, 232, 220), 0.35)
            elif mat == "feather":
                cxm = abs(i - W / 2) / max(1, W / 2)
                c = mul(base, 1.08 - 0.25 * ty - 0.15 * cxm)
                if abs(i - W // 2) < 1 and fname in ("front", "back"): c = mix(c, (255, 255, 240), 0.5)    # 깃대
                elif (i + j) % 3 == 0: c = mul(c, 0.88)                                                     # 깃가지
                if ty > 0.82: c = mix(c, P["glow"] if p.bone == "head" else mix(base, (255, 255, 255), 0.5), 0.45)
            elif mat == "bone":
                c = mul(base, 1.05 - 0.25 * ty)
                if rnd.random() < 0.05: c = mul(c, 0.75)
            elif mat == "glow":
                cx, cy = abs(i - (W - 1) / 2) / max(1, W / 2), abs(j - (H - 1) / 2) / max(1, H / 2)
                c = mix(base, (255, 255, 255), max(0, 0.7 - max(cx, cy)))
            elif mat == "gem":
                c = mul(base, 1.25 if (i < W / 2) == (j < H / 2) else 0.8)
                c = mix(c, (255, 255, 255), 0.3 if i == 1 and j == 1 else 0)
            else:
                c = base
            # 2) 천 띠 장식 (아래 끝단 / 가장자리 / 가운데 줄 / 룬 / 십자 / 문장)
            if mat in ("cloth", "cloth2", "dark", "fur") and fname in ("front", "back", "side"):
                band = dec.get("bands")
                bh = 2 * S
                if band in ("bottom", "edge") and j >= H - bh:
                    c = mul(P["trim"], 1.1 if (i // 2) % 2 == 0 else 0.85)
                    if j == H - bh: c = mul(P["trim"], 0.6)
                if band == "edge" and fname != "side" and (i < S or i >= W - S):
                    c = mul(P["trim"], 1.0)
                if dec.get("stripe") and fname == "front" and abs(i - W // 2) <= S // 2 + 0:
                    c = P["trim"]
                emb = None
                if dec.get("runes") and fname == "back" and 4 * S < j < H - 4 * S:
                    if rune(i, j, W): emb = P["glow"]
                if dec.get("cross") and fname in ("front", "back"):
                    cxp, cyp = W // 2, int(H * 0.38)
                    if (abs(i - cxp) <= S // 2 + 1 and abs(j - cyp) <= 3 * S) or (abs(j - cyp + S) <= S // 2 and abs(i - cxp) <= 2 * S): emb = P["glow"]
                if dec.get("crest") and fname in ("front", "back"):
                    cxp, cyp = W // 2, int(H * 0.3)
                    dx, dy = abs(i - cxp), j - cyp
                    if 0 <= dy < 6 * S and dx <= (3 * S if dy < 3 * S else max(0, 3 * S - (dy - 3 * S))):   # 방패
                        c = P["trim"] if dx > 3 * S - 2 or dy < 2 else P["cloth2"]
                        if dx <= 1 or abs(dy - 2 * S) <= 1: emb = P["glow"]
                if emb is not None:
                    c = emb; gp[X, Y] = emb + (255,)
            # 3) 면 음영 + 테두리
            if fname == "top": c = mul(c, 1.12)
            elif fname == "bottom": c = mul(c, 0.7)
            elif fname == "side": c = mul(c, 0.9)
            edge = i == 0 or j == 0 or i == W - 1 or j == H - 1
            if edge and not glowing and W > 2 and H > 2:
                c = mul(c, 0.62) if (j == H - 1 or i == W - 1) else mix(c, (255, 255, 255), 0.12)
            px[X, Y] = c + (255,)
            if glowing: gp[X, Y] = c + (255,)
            elif mat == "trim" and P["trim"] == GOLD and (i + j) % 7 == 0: gp[X, Y] = mix(c, (255, 240, 180), 0.3) + (110,)

RUNE = ["0110", "1001", "1111", "1001", "0110", "0000", "1110", "0100", "0100", "0111", "0000", "1011", "1010", "1110", "0010"]
def rune(i, j, W):
    gx, gy = (i // S) % 6, (j // S) % 17
    if gx >= 4 or gy >= len(RUNE): return False
    return RUNE[gy][gx] == "1" and (i // S) // 6 < 2

def textures(o, P):
    px_img = Image.new("RGBA", (ATLAS * S, ATLAS * S), (0, 0, 0, 0)); gl = Image.new("RGBA", (ATLAS * S, ATLAS * S), (0, 0, 0, 0))
    px, gp = px_img.load(), gl.load()
    rnd = random.Random(1234)
    for p in o.parts:
        if p.mat == "none": continue
        for fname, u, v, w, h in faces(p):
            if w <= 0 or h <= 0: continue
            paint_face(px, gp, P, p, fname, u, v, w, h, rnd)
    return px_img, gl

# ------------------------------------------------------------------ 평면 갑옷 (HD 2배, 64x32 배치)
def flat(cls, el):
    P = palette(cls, el)
    a = Image.new("RGBA", (64 * S, 32 * S), (0, 0, 0, 0)); b = Image.new("RGBA", (64 * S, 32 * S), (0, 0, 0, 0))
    pa, pb = a.load(), b.load()
    rnd = random.Random(99)
    def box(px, u, v, w, h, d, mat, deco=None, only=None):
        p = Part("x", (0, 0, 0, w, h, d), mat, deco=deco or {}); p.uv = (u, v)
        for fname, fu, fv, fw, fh in faces(p):
            if only and fname not in only: continue
            paint_face(px, DummyGlow(), P, p, fname, fu, fv, fw, fh, rnd)
    hooded = cls in ("assassin", "reaper", "ranger")
    # 머리
    if hooded:
        box(pa, 0, 0, 8, 8, 8, "cloth", {"bands": "none"})
        for y in range(8 * S, 16 * S):            # 얼굴 앞면 비움 (두건 모형이 테를 둘러줌)
            for x in range(8 * S, 16 * S): pa[x, y] = (0, 0, 0, 0)
        if cls == "reaper":
            for y in range(8 * S, 16 * S):
                for x in range(8 * S, 16 * S): pa[x, y] = (10, 8, 12, 255)
    else:
        box(pa, 0, 0, 8, 8, 8, "cloth2" if cls == "support" else "dark", {"bands": "none"})
        for y in range(0, 16 * S):              # 머리띠만 남김
            for x in range(0, 32 * S):
                if not (9 * S <= y < 11 * S and y >= 8 * S): pa[x, y] = (0, 0, 0, 0)
    # 몸통·팔
    box(pa, 16, 16, 8, 12, 4, "cloth", {"stripe": cls in ("mage", "support"), "bands": "none"})
    box(pa, 40, 16, 4, 12, 4, "cloth", {"bands": "bottom"})
    # 신발
    box(pa, 0, 16, 4, 12, 4, "leather" if cls in ("ranger", "assassin") else "dark", {"stitch": True})
    for y in range(20 * S, 28 * S):
        for x in range(0, 16 * S): pa[x, y] = (0, 0, 0, 0)
    for y in range(16 * S, 20 * S):
        for x in range(4 * S, 8 * S): pa[x, y] = (0, 0, 0, 0)
    # 하의
    box(pb, 16, 16, 8, 12, 4, "cloth", {"bands": "none"})
    for y in range(16 * S, 27 * S):
        for x in range(16 * S, 40 * S): pb[x, y] = (0, 0, 0, 0) if y < 26 * S else pb[x, y]
    box(pb, 0, 16, 4, 12, 4, "cloth" if cls in ("mage", "support", "reaper") else "cloth2", {"bands": "bottom" if cls in ("mage", "support", "reaper") else "none"})
    return a, b

class DummyGlow:
    def __setitem__(self, k, v): pass

# ------------------------------------------------------------------ 자바 코드
def jf(v): return f"{v:.3f}F".replace(".000F", "F")

def java(outfits):
    L = []
    L.append("// 자동 생성 파일 (tools/armor/class3d.py) - 모양을 바꾸려면 생성기를 수정하세요.")
    L.append("package com.elementalweapons.client.armor;\n")
    L.append("import java.util.ArrayList;\nimport java.util.List;\n")
    L.append("import net.minecraft.client.model.geom.PartPose;\nimport net.minecraft.client.model.geom.builders.CubeListBuilder;\nimport net.minecraft.client.model.geom.builders.LayerDefinition;\nimport net.minecraft.client.model.geom.builders.MeshDefinition;\nimport net.minecraft.client.model.geom.builders.PartDefinition;\n")
    L.append("/** 직업별 입체 복장 (직업마다 따로 된 모형·텍스처). 부위 이름: 착용칸|움직임|번호 */")
    L.append("public final class ClassArmorMesh {\n\tprivate ClassArmorMesh() {}\n")
    L.append("\tpublic static final String[] CLASSES = {" + ", ".join(f'"{c}"' for c in outfits) + "};")
    L.append("\t/** 직업별 부위 목록: {뼈 이름, 부위 이름} */")
    L.append("\tpublic static final List<List<String[]>> PARTS = new ArrayList<>();")
    L.append("\tstatic { for (int i = 0; i < CLASSES.length; i++) PARTS.add(new ArrayList<>()); }\n")
    L.append("\tprivate static PartDefinition[] bones(MeshDefinition mesh) {\n\t\tPartDefinition r = mesh.getRoot();\n\t\tPartDefinition head = r.addOrReplaceChild(\"head\", CubeListBuilder.create(), PartPose.ZERO);\n\t\thead.addOrReplaceChild(\"hat\", CubeListBuilder.create(), PartPose.ZERO);\n\t\treturn new PartDefinition[]{head,\n\t\t\tr.addOrReplaceChild(\"body\", CubeListBuilder.create(), PartPose.ZERO),\n\t\t\tr.addOrReplaceChild(\"right_arm\", CubeListBuilder.create(), PartPose.offset(-5F, 2F, 0F)),\n\t\t\tr.addOrReplaceChild(\"left_arm\", CubeListBuilder.create(), PartPose.offset(5F, 2F, 0F)),\n\t\t\tr.addOrReplaceChild(\"right_leg\", CubeListBuilder.create(), PartPose.offset(-1.9F, 12F, 0F)),\n\t\t\tr.addOrReplaceChild(\"left_leg\", CubeListBuilder.create(), PartPose.offset(1.9F, 12F, 0F))};\n\t}\n")
    BI = {"head": 0, "body": 1, "right_arm": 2, "left_arm": 3, "right_leg": 4, "left_leg": 5}
    L.append("\tpublic static LayerDefinition create(int idx) {\n\t\tMeshDefinition mesh = new MeshDefinition();\n\t\tPartDefinition[] b = bones(mesh);\n\t\tList<String[]> out = PARTS.get(idx);\n\t\tout.clear();\n\t\tswitch (idx) {")
    for ci, (cls, o) in enumerate(outfits.items()):
        L.append(f"\t\t\tcase {ci} -> {cls}(b, out);")
    L.append("\t\t}\n\t\treturn LayerDefinition.create(mesh, " + str(ATLAS) + ", " + str(ATLAS) + ");\n\t}\n")
    for cls, o in outfits.items():
        L.append(f"\tprivate static void {cls}(PartDefinition[] b, List<String[]> out) {{")
        # 이름 붙이기
        n = 0
        for p in o.parts:
            if p.parent is None:
                p.name = f"{p.slot}|{p.anim}|{n}"; n += 1
            else:
                p.name = f"c{n}"; n += 1
        var = {}
        for k, p in enumerate(o.parts):
            x, y, z, w, h, d = p.box
            cubes = "CubeListBuilder.create()" if p.mat == "none" else f"CubeListBuilder.create().texOffs({p.uv[0]}, {p.uv[1]}).addBox({jf(x)}, {jf(y)}, {jf(z)}, {jf(w)}, {jf(h)}, {jf(d)})"
            pose = f"PartPose.offsetAndRotation({', '.join(jf(t) for t in p.pivot)}, {', '.join(jf(t) for t in p.rot)})"
            parent = f"b[{BI[p.bone]}]" if p.parent is None else var[id(p.parent)]
            vn = f"p{k}"; var[id(p)] = vn
            L.append(f"\t\tPartDefinition {vn} = {parent}.addOrReplaceChild(\"{p.name}\", {cubes}, {pose});")
            if p.parent is None:
                L.append(f"\t\tout.add(new String[]{{\"{p.bone}\", \"{p.name}\"}});")
        L.append("\t}\n")
    L.append("}")
    open(JAVA, "w").write("\n".join(L) + "\n")

def main():
    outfits = build()
    for o in outfits.values(): pack(o)
    java(outfits)
    os.makedirs(f"{T}/armor_deco/class", exist_ok=True)
    for cls, o in outfits.items():
        for el in ELS:
            P = palette(cls, el)
            img, gl = textures(o, P)
            img.save(f"{T}/armor_deco/class/{cls}_{el}.png", optimize=True); gl.save(f"{T}/armor_deco/class/{cls}_{el}_glow.png", optimize=True)
            if cls in CLOTH_CLASSES:
                a, b = flat(cls, el)
                a.save(f"{T}/equipment/humanoid/cls_{cls}_{el}.png", optimize=True); b.save(f"{T}/equipment/humanoid_leggings/cls_{cls}_{el}.png", optimize=True)
            for tier in ("aether", "orichalcum"):
                tex = f"cls_{cls}_{el}" if cls in CLOTH_CLASSES else (tier if el == "neutral" else f"{tier}_{el}")
                json.dump({"layers": {"humanoid": [{"texture": f"elementalweapons:{tex}"}], "humanoid_leggings": [{"texture": f"elementalweapons:{tex}"}]}},
                          open(f"{ASSETS}/equipment/{tier}_{cls}_{el}.json", "w"), indent="\t")
    print("ok", {c: len(o.parts) for c, o in outfits.items()})

if __name__ == "__main__":
    main()
