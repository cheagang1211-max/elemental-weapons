"""
보스 모델 v3 — 실루엣·층·장식을 크게 보강한 재디자인 (bosses2.SPECS 를 덮어씀)
파트 이름 규칙은 v2 와 같음 (GenericBossModel 자동 애니메이션)
"""
import math, sys, os
sys.path.insert(0, os.path.dirname(__file__))
import bosses2
from bosses2 import leg, arm
from materials2 import M, FACE, hexc, mix
from faces3 import FACE3
from shapes import P, C, B, chain, bend_chain, spikes, wing, cape, ring_of, claws, teeth, R

SPECS = bosses2.SPECS
def spec(fn): SPECS[fn.__name__] = fn; return fn


def stack(cx, y0, y1, cz, w0, w1, d0, d1, mat, n):
    """위(y0)→아래(y1)로 폭이 변하는 기둥 (몸통·팔다리를 둥글게)"""
    out = []
    h = (y1 - y0) / n
    for i in range(n):
        t = (i + 0.5) / n
        out.append(C(cx, y0 + h * (i + 0.5), cz, w0 + (w1 - w0) * t, abs(h) + 0.01, d0 + (d1 - d0) * t, mat))
    return out


def rounded(cx, cy, cz, w, h, d, mat, k=1):
    """모서리를 깎은 덩어리 (가운데 큰 상자 + 면마다 살짝 작은 상자)"""
    return [C(cx, cy, cz, w, h - 2 * k, d - 2 * k, mat), C(cx, cy, cz, w - 2 * k, h, d - 2 * k, mat), C(cx, cy, cz, w - 2 * k, h - 2 * k, d, mat)]


def shard(name, pivot, rot, h, w, mat, tip=None):
    """끝으로 갈수록 가늘어지는 결정/뿔 (위로)"""
    bx = [C(0, -h * 0.3, 0, w, h * 0.6, w, mat), C(0, -h * 0.75, 0, w * 0.6, h * 0.35, w * 0.6, tip or mat), C(0, -h * 0.97, 0, max(1, w * 0.3), h * 0.12, max(1, w * 0.3), tip or mat)]
    return P(name, pivot, rot, bx, field=False)


# =====================================================================
@spec
def frost_giant():
    m = {"skin": M(0x8FB0CC, "flesh"), "ice": M(0xBFEFFF, "crystal", glow=0xE8FFFF), "fur": M(0xECE6DA, "fur"), "fur2": M(0x9C8C78, "fur"),
         "metal": M(0x5A6A80, "metal"), "iron": M(0x3A4252, "metal"), "beard": M(0xF4FAFF, "fur"), "leather": M(0x5A4030, "cloth", trim=0x3A2818),
         "bone": M(0xE8E0CC, "bone"), "rune": M(0x2A3446, "rune", glow=0x9FF4FF), "deep": M(0x6FA8D8, "crystal", glow=0xBFF4FF)}
    m["face"] = FACE3(m["skin"], 0x9FF4FF, kind="beast", brow=0xF4FAFF, mouth=0x24303C, skin_shadow=0.6,
                      beard=m["beard"], eye_y=0.38, eye_w=0.17)
    # 머리: 뿔 투구 + 얼음 왕관 + 긴 수염 땋기
    beard = chain("swayB", 4, (0, 0, -6.2), 3.5, 10, 4, "beard", axis="y", h0=3, h1=2)
    braid = lambda s: P(f"swayH{1 if s < 0 else 2}", (s * 5, -2, 1), (0, 0, 0), [C(0, 3, 0, 2, 6, 2, "beard"), C(0, 6.5, 0, 2.6, 1, 2.6, "metal"), C(0, 9, 0, 1.6, 4, 1.6, "beard")], field=False)
    horns = []
    for s in (-1, 1):
        h = bend_chain("hornF" + ("L" if s > 0 else "R"), (s * 5.5, -7, 0), [(4, 3), (4, 2.4), (4, 2), (3, 1.4)], "bone", bend=(0, 0, -s * R(22)))
        h.rot = (0, 0, s * R(80))
        horns.append(h)
    crown = [C(0, -9.5, 0, 11, 2, 11, "iron"), C(0, -10.8, 0, 12, 1, 12, "metal")]
    crown += [C(x, -13 - hh / 2, -5.2 + abs(x) * 0.2, 1.6, hh, 1.6, "ice") for x, hh in ((-4.5, 3), (-2.2, 5), (0, 7), (2.2, 5), (4.5, 3))]
    crown += [C(0, -10.5, -6.1, 3, 3, 1, "deep")]
    head = P("head", (0, -31, -3), (0, 0, 0), rounded(0, -5, 0, 11, 11, 10, "skin") + [
        C(0, -5, -5.1, 11, 11, 1, "skin", {"front": m["face"]}), C(0, -4, -5.8, 2, 3, 1, "skin"),
        C(-5.2, -4, 0, 1, 3, 3, "skin"), C(5.2, -4, 0, 1, 3, 3, "skin"), C(0, -8.5, 0.5, 10.5, 2, 10.5, "beard"), *crown],
        [beard, braid(-1), braid(1)] + horns)
    # 몸통: 넓은 가슴 + 털 망토 + 얼음 흉갑
    torso = stack(0, -30, -8, 0, 22, 16, 13, 10, "skin", 6)
    mantle = [C(0, -28, 0.5, 26, 6, 15, "fur"), C(-11, -25.5, 0.5, 6, 5, 14, "fur"), C(11, -25.5, 0.5, 6, 5, 14, "fur"), C(0, -31.5, 2, 18, 2, 10, "fur2")]
    plate = [C(0, -19, -6.6, 14, 12, 1.2, "ice"), C(0, -19, -7.3, 6, 6, 1, "deep"), C(0, -25, -6.8, 16, 2, 1, "iron"),
             C(-4.5, -13, -6.4, 4, 2, 1, "iron"), C(4.5, -13, -6.4, 4, 2, 1, "iron")]
    belt = [C(0, -2.5, 0, 16, 6, 10, "leather"), C(0, -7, 0, 18, 4, 12, "leather"), C(0, -7, -6.3, 5, 5, 1, "metal"), C(0, -7, -6.9, 3, 3, 1, "bone"),
            C(-7, -7, -6.2, 2, 2, 1, "iron"), C(7, -7, -6.2, 2, 2, 1, "iron")]
    loin = chain("swayL", 2, (0, -5, -5.5), 4, 9, 8, "fur2", axis="y", h0=1, h1=1)
    loin2 = chain("swayK", 2, (0, -5, 5.5), 4, 12, 10, "fur2", axis="y", h0=1, h1=1)
    capeP = cape((0, -30, 7), 6, 22, 5, "fur", depth=2)
    for i, cp in enumerate([capeP]):
        cp.boxes.append(C(0, 0.5, 0.5, 24, 2, 3, "fur2"))
    shards_back = [shard(f"fgs{i}", (x, -32, 5), (R(-25), 0, R(x * 2.5)), hh, 3, "ice") for i, (x, hh) in enumerate(((-7, 8), (-3, 11), (3, 11), (7, 8)))]
    # 팔: 굵은 팔뚝 + 금속 팔찌 + 얼음 도끼/몽둥이
    def club():
        b = [C(0, 12, -2, 3, 14, 3, "leather"), C(0, 7, -2, 3.6, 1, 3.6, "iron"), C(0, 17, -2, 3.6, 1, 3.6, "iron")]
        b += rounded(0, 24, -2, 8, 10, 8, "ice", 1.5)
        b += [C(-4.6, 23, -2, 2, 5, 2, "deep"), C(4.6, 25, -2, 2, 5, 2, "deep"), C(0, 24, -6.6, 2, 5, 2, "deep"), C(0, 30, -2, 3, 3, 3, "ice")]
        return b
    pauldron = lambda s: [C(s * 0.5, -1, 0, 10, 5, 10, "fur"), C(s * 1.5, -3.5, 0, 9, 2, 9, "iron"), C(s * 1.5, -5, -2, 2, 3, 2, "ice"), C(s * 1.5, -5.5, 2, 2, 4, 2, "ice")]
    ra = arm("right", (-12, -27, 0), (8, 13), (7, 12), "skin", "skin", extra_upper=pauldron(-1),
             extra_fore=club() + [C(0, 3.5, 0, 9, 5, 9, "iron"), C(0, 3.5, -4.6, 3, 2, 1, "rune")])
    la = arm("left", (12, -27, 0), (8, 13), (7, 12), "skin", "skin", extra_upper=pauldron(1),
             extra_fore=[C(0, 3.5, 0, 9, 5, 9, "iron"), C(0, 3.5, -4.6, 3, 2, 1, "rune"), C(0, 13.5, 0, 8, 3, 8, "skin"),
                         C(-2, 15.5, -3, 2, 2, 2, "skin"), C(2, 15.5, -3, 2, 2, 2, "skin"), C(0, 10, 4.6, 2, 6, 2, "ice")])
    body = P("body", (0, -4, 0), (0, 0, 0), torso + mantle + plate + belt, [head, ra, la, capeP, loin, loin2] + shards_back)
    def gleg(name, x):
        l = leg(name, (x, -2, 0), (8, 13), (7, 13), "leather", None, None)
        l.boxes += [C(0, 4, 0, 9.4, 4, 9.4, "fur2"), C(0, 9, -4.4, 4, 3, 1, "metal")]
        sh = l.children[0]
        sh.boxes += [C(0, 7, 0, 9, 8, 9, "fur"), C(0, 3, 0, 8.4, 1.4, 8.4, "iron"), C(0, 12, -1.5, 9, 2, 12, "iron"), C(0, 12.6, -7, 7, 1, 2, "metal")]
        return l
    return [body, gleg("right_leg", -5), gleg("left_leg", 5)], m


@spec
def twilight_twins():
    """황혼의 쌍둥이: 달의 마녀(왼) + 해의 기사(오) 가 등을 맞댄 채 떠 있음, 둘을 잇는 황혼 고리"""
    m = {"dark": M(0x3A2458, "robe", glow=0xC080FF), "dark2": M(0x1A1028, "cloth"), "light": M(0xF6EED8, "robe", glow=0xFFD860), "light2": M(0xE8D8B0, "cloth"),
         "dskin": M(0x7A6A98, "flesh"), "lskin": M(0xF2D8C0, "flesh"), "dhair": M(0x3A2A5A, "fur"), "lhair": M(0xFFE8A0, "fur"),
         "void": M(0xA060FF, glow_all=True), "sun": M(0xFFD860, glow_all=True), "gold": M(0xF0C040, "metal"), "silver": M(0xB8C0D8, "metal"),
         "moon": M(0xD8E0FF, "crystal", glow=0xE8ECFF), "ember": M(0xFF9A40, "crystal", glow=0xFFC070), "star": M(0xFFFFFF, glow_all=True)}
    m["dface"] = FACE3(m["dskin"], 0xD090FF, kind="human", iris=0xC070FF, brow=0x2A1A40, mouth=0x4A2A5A, mark="tears", mark_col=0xB070FF)
    m["lface"] = FACE3(m["lskin"], 0xFFC040, kind="human", iris=0xFFB030, brow=0xC09040, mouth=0xB06050, mark="forehead", mark_col=0xFFD860, blush=0xFF9080)

    def twin(sfx, x, sign, skin, face, robe, robe2, hair, glowm, metal, gem):
        side = "right" if sign < 0 else "left"
        # 머리 + 머리카락 + 관
        hairb = [C(0, -5, 1.2, 8.6, 9, 7.6, hair), C(0, -9.2, 0.5, 8, 1.4, 8, hair)]
        if sfx == "1":   # 달: 긴 머리 + 초승달 관
            locks = chain("swayM", 4, (0, -2, 3.5), 3.5, 8, 5, hair, axis="y", h0=2, h1=1.5)
            tiara = [C(0, -9.8, -2.5, 6, 1, 1, metal), C(-3.5, -11, -2.5, 1, 3, 1, "moon"), C(3.5, -11, -2.5, 1, 3, 1, "moon"),
                     C(-2.5, -12.5, -2.5, 1, 1, 1, "moon"), C(2.5, -12.5, -2.5, 1, 1, 1, "moon"), C(0, -10.8, -3.2, 2, 2, 1, glowm)]
        else:            # 해: 짧은 머리 + 햇살 관
            locks = chain("swayN", 2, (0, -2, 3.5), 3, 8, 6, hair, axis="y", h0=2, h1=1.5)
            tiara = [C(0, -9.8, -2.5, 8, 1, 1, metal)] + [C(xx, -11.5 - (2 - abs(xx)) * 0.6, -2.6, 1, 2 + (2 - abs(xx)) * 1.2, 1, glowm) for xx in (-3, -1.5, 0, 1.5, 3)]
        head = P("head" + sfx, (0, -23, 0), (0, 0, 0), [C(0, -4, 0, 7, 8, 7, skin), C(0, -4, -3.6, 7, 8, 1, skin, {"front": m[face]}),
                 C(-3.8, -5, 0.5, 1, 6, 6, hair), C(3.8, -5, 0.5, 1, 6, 6, hair)] + hairb + tiara, [locks])
        # 소매가 넓은 팔 + 지팡이/검
        if sfx == "1":
            weapon = [C(0, 6, -3, 1.2, 30, 1.2, metal), C(0, -10, -3, 5, 1, 1, metal), C(-2.5, -12, -3, 1, 4, 1, "moon"), C(2.5, -12, -3, 1, 4, 1, "moon"),
                      C(0, -11.5, -3, 3, 3, 3, glowm), C(0, -14, -3, 1, 2, 1, "moon")]
        else:
            weapon = [C(0, 12, -3, 2, 4, 2, metal), C(0, 14.5, -3, 7, 1.4, 2, metal), C(0, 26, -3, 3, 22, 1, "light2"), C(0, 26, -3.6, 1, 20, 1, glowm), C(0, 37.5, -3, 1.5, 2, 1, "light2")]
        sleeve = [C(sign * 0.8, 9, 0, 5, 6, 5, robe), C(sign * 0.8, 11.5, 0, 5.6, 1, 5.6, metal)]
        arm_o = P(side + "_arm", (sign * 5.5, -20.5, 0), (0, 0, sign * R(-12)), [C(sign * 0.6, 4, 0, 3, 9, 3, robe), C(sign * 0.6, 0, 0, 5, 3, 5, metal), C(sign * 0.6, -1.5, 0, 4, 1, 4, gem)],
                  [P(side + "_arm2", (sign * 0.6, 8, 0), (0, 0, 0), sleeve + [C(0, 12.5, 0, 2.4, 2.4, 2.4, skin)] + weapon)])
        orb = P("bob_" + sfx, (-sign * 11, -26, -2), (0, 0, 0), [C(0, 0, 0, 4, 4, 4, glowm), C(0, 0, 0, 7, 1, 1, metal), C(0, 0, 0, 1, 7, 1, metal), C(0, 0, 0, 1, 1, 7, metal)])
        # 치마/로브 자락 (흔들림)
        robe_c = chain("sway" + ("D" if sfx == "1" else "E"), 3, (0, -6, 0), 6, 10, 14, robe, axis="y", h0=8, h1=11)
        e = robe_c
        while e.children: e = e.children[0]
        e.boxes += [C(0, 6, -5.2 if sfx == "1" else -5.2, 14.4, 1.4, 1, metal)]
        torso = stack(0, -20, -8, 0, 9, 7, 6, 5, robe, 4) + [C(0, -17, -3.2, 4, 4, 1, metal), C(0, -17, -3.8, 2, 2, 1, gem),
                 C(0, -8, 0, 9, 2, 6.4, metal), C(0, -20.5, 0, 10, 1.6, 6.6, robe2)]
        wingp = []
        for s2 in (-1, 1):   # 등 뒤 작은 빛 날개
            if s2 != -sign: continue
            for k in range(4):
                wingp.append(P(f"tw{sfx}f{k}", (s2 * 3, -18, 3.5), (R(-15), 0, s2 * R(40 + k * 22)), [B(-0.6, -12 + k, -0.5, 1.2, 12 - k, 1, glowm)], field=False))
        return P("twin" + sfx, (x, 0, 0), (0, sign * R(-12), sign * R(-5)), torso, [head, arm_o, orb, robe_c] + wingp)

    d = twin("1", -6.5, -1, "dskin", "dface", "dark", "dark2", "dhair", "void", "silver", "moon")
    l = twin("2", 6.5, 1, "lskin", "lface", "light", "light2", "lhair", "sun", "gold", "ember")
    # 황혼 고리: 반은 달빛, 반은 햇빛
    ring_parts = []
    for i in range(16):
        a = i * 2 * math.pi / 16
        mat = "void" if i < 8 else "sun"
        ring_parts.append(P(f"tr{i}", (math.cos(a) * 16, 0, math.sin(a) * 16), (0, -a, 0), [C(0, 0, 0, 1.2, 1.2, 6.4, mat)], field=False))
    ring_parts += [P("trm", (16, 0, 0), (0, 0, 0), [C(0, 0, 0, 3, 3, 3, "moon")], field=False), P("trs", (-16, 0, 0), (0, 0, 0), [C(0, 0, 0, 3, 3, 3, "ember")], field=False)]
    ring = P("ring", (0, -12, 0), (R(12), 0, 0), [], ring_parts)
    halo = P("halo", (0, -36, 6), (0, 0, 0), [C(0, 0, -0.6, 3, 3, 1, "star")] +
             [C(math.cos(k * math.pi / 4) * 9, math.sin(k * math.pi / 4) * 9, 0, 1.6, 1.6, 1, "void" if math.cos(k * math.pi / 4) < 0 else "sun") for k in range(8)])
    body = P("body", (0, 6, 0), (0, 0, 0), [C(0, -2, 0, 4, 4, 4, "star"), C(0, -2, 0, 6, 1, 1, "silver"), C(0, -2, 0, 1, 6, 1, "gold")], [d, l, ring, halo])
    return [body], m


@spec
def crystal_golem():
    m = {"rock": M(0x4A3A52, "stone"), "rock2": M(0x5E4C66, "stone"), "crystal": M(0xE070FF, "crystal", glow=0xFFB0FF), "core": M(0xFF80FF, "gem", glow=0xFFC0FF),
         "dark": M(0x2A2030, "stone"), "shard": M(0xB0F0FF, "crystal", glow=0xE0FFFF), "vein": M(0x3A2A42, "veins", glow=0xFF90FF),
         "moss": M(0x5A7A4A, "leaf", accent=0x7A9A5A)}
    m["face"] = FACE3(m["rock2"], 0xFF90FF, kind="beast", skin_shadow=0.5, eye_y=0.45, eye_w=0.2)

    def cluster(prefix, x, y, z, s=1.0, tilt=0.0):
        return [shard(f"{prefix}a", (x, y, z), (R(tilt), 0, R(-12)), 9 * s, 3 * s, "crystal", "shard"),
                shard(f"{prefix}b", (x + 2.5 * s, y + 0.5, z + 1), (R(tilt + 10), 0, R(28)), 6 * s, 2.4 * s, "shard"),
                shard(f"{prefix}c", (x - 2.4 * s, y + 0.5, z - 1), (R(tilt - 15), 0, R(-35)), 5 * s, 2 * s, "crystal", "shard")]

    head = P("head", (0, -27, -6), (0, 0, 0), rounded(0, -3.5, -1, 10, 8, 9, "rock2") + [C(0, -3.5, -5.6, 10, 8, 1, "rock2", {"front": m["face"]}),
             C(0, -8, 0, 12, 2, 9, "dark"), C(0, -1, -6, 8, 2, 1, "dark"), C(-6, -5, -1, 2, 4, 6, "dark"), C(6, -5, -1, 2, 4, 6, "dark")],
             cluster("hc", 0, -9, 1, 0.9) + [shard("hcd", (-4, -8.5, 2), (R(-20), 0, R(-40)), 5, 2, "shard")])
    def fist(s):
        b = rounded(0, 15, 0, 11, 9, 11, "rock", 1.5) + [C(0, 19.5, -2, 10, 2, 7, "dark"), C(0, 12, -5.6, 8, 3, 1, "vein")]
        b += [C(x, 20, -5, 2.4, 2.4, 2.4, "dark") for x in (-3.3, 0, 3.3)]
        return b
    ra = arm("right", (-16, -22, 0), (9, 11), (8, 10), "rock", None, extra_fore=fist(-1),
             extra_upper=rounded(-1, -1, 0, 13, 8, 12, "rock2", 1.5) + [C(-1, 4, -5.8, 6, 3, 1, "vein")])
    la = arm("left", (16, -22, 0), (9, 11), (8, 10), "rock", None, extra_fore=fist(1),
             extra_upper=rounded(1, -1, 0, 13, 8, 12, "rock2", 1.5) + [C(1, 4, -5.8, 6, 3, 1, "vein")])
    ra.children[0].children += cluster("rfc", -1, 10, 2, 0.8, 30)
    la.children[0].children += cluster("lfc", 1, 10, 2, 0.8, 30)
    ra.children += cluster("rsc", -2, -5, 1, 1.3)
    la.children += cluster("lsc", 2, -5, 1, 1.4)
    torso = rounded(0, -16, 0, 24, 18, 15, "rock", 2) + [C(0, -25, 0, 26, 4, 16, "dark"), C(0, -6, 0, 18, 6, 13, "dark"),
             C(0, -16, -7.8, 13, 13, 1, "dark"), C(0, -16, -8.4, 9, 9, 1, "vein"), C(-9, -12, -7.6, 3, 8, 1, "vein"), C(9, -19, -7.6, 3, 7, 1, "vein"),
             C(-6, -26.5, 4, 8, 2, 6, "moss"), C(8, -26, -3, 5, 1.6, 5, "moss"), C(0, -9.5, -7, 22, 1.4, 1, "rock2")]
    back = cluster("bc1", -7, -26, 4, 1.6, -15) + cluster("bc2", 6, -27, 3, 1.8, -10) + cluster("bc3", 0, -27, 6, 1.3, -30)
    body = P("body", (0, 2, 0), (0, 0, 0), torso, [head, ra, la, P("pulse_core", (0, -16, -9), (0, 0, 0), [C(0, 0, 0, 5, 5, 2, "core"), C(0, 0, -1, 2, 2, 1, "shard")])] + back)
    def gl(name, x):
        l = leg(name, (x, 2, 0), (9, 11), (9, 11), "rock", "dark", None)
        l.boxes += rounded(0, 2, 0, 11, 5, 11, "rock2", 1)
        l.children[0].boxes += [C(0, 10, -2, 11, 3, 13, "dark"), C(0, 4, -4.8, 5, 4, 1, "vein")]
        return l
    return [body, gl("right_leg", -6), gl("left_leg", 6)], m


@spec
def steam_colossus():
    m = {"brass": M(0xC08030, "plate", trim=0x6A4018), "brass2": M(0xD8A050, "metal"), "iron": M(0x4A4A52, "metal"), "dark": M(0x26262C, "metal"),
         "steam": M(0xF0F0F0, glow_all=True), "furnace": M(0x3A1A10, "lava", glow=0xFFA040), "copper": M(0xD07A50, "metal"), "pipe": M(0x7A7A80, "metal"),
         "lens": M(0x60E0FF, "gem", glow=0x9FF0FF), "glass": M(0x9FE8FF, "crystal", glow=0xC0F4FF), "rivet": M(0xE8C070, "plain")}
    m["face"] = FACE3(m["iron"], 0x60E0FF, kind="visor", eye_y=0.36)
    # 머리: 잠수 헬멧 같은 둥근 투구 + 렌즈 + 굴뚝
    head = P("head", (0, -31, -3), (0, 0, 0), rounded(0, -4.5, 0, 11, 10, 10, "iron", 1.5) + [C(0, -4.5, -5.2, 11, 10, 1, "iron", {"front": m["face"]}),
             C(0, -10, 0, 7, 2, 7, "brass"), C(-6.5, -4.5, 0, 2, 5, 5, "copper"), C(6.5, -4.5, 0, 2, 5, 5, "copper"),
             C(-7.6, -4.5, 0, 1, 3, 3, "lens"), C(7.6, -4.5, 0, 1, 3, 3, "lens"), C(0, -1, -5.6, 7, 2, 1, "brass"),
             C(-3, -12, 1, 2, 4, 2, "pipe"), C(-3, -14.5, 1, 3, 1, 3, "dark"), C(0, 0.5, 0, 12, 1.4, 11, "brass")])
    def gear(name, x, y, z, r=4):
        teeth_ = [C(math.cos(i * math.pi / 4) * r, math.sin(i * math.pi / 4) * r, 0, 2, 2, 1, "brass2") for i in range(8)]
        return P(name, (x, y, z), (0, 0, 0), [C(0, 0, 0, r * 1.6, r * 1.6, 1, "brass"), C(0, 0, -0.5, 2, 2, 1, "iron")] + teeth_, field=True)
    def hammer():
        b = [C(0, 12, 0, 3, 10, 3, "pipe"), C(0, 9, 0, 4, 1, 4, "brass")]
        b += rounded(0, 21, 0, 12, 8, 8, "iron", 1) + [C(-6.5, 21, 0, 1, 6, 6, "brass"), C(6.5, 21, 0, 1, 6, 6, "brass"), C(0, 21, -4.4, 6, 4, 1, "furnace"),
              C(-4, 17, 0, 1, 1, 9, "rivet"), C(4, 17, 0, 1, 1, 9, "rivet"), C(0, 25.5, 0, 13, 1, 9, "dark")]
        return b
    piston = lambda s: [C(s * 4.5, 4, 2, 1.6, 9, 1.6, "pipe"), C(s * 4.5, 0, 2, 2.4, 2, 2.4, "brass2")]
    ra = arm("right", (-15, -27, 0), (8, 11), (8, 10), "brass", None, extra_upper=rounded(-1, -1, 0, 12, 7, 11, "iron", 1.5) + [C(-1, -5, 0, 6, 1.4, 6, "brass2")] + piston(-1),
             extra_fore=hammer() + [C(0, 3, 0, 10, 4, 10, "copper"), C(0, 3, -5.2, 4, 2, 1, "lens")])
    la = arm("left", (15, -27, 0), (8, 11), (8, 10), "brass", None, extra_upper=rounded(1, -1, 0, 12, 7, 11, "iron", 1.5) + [C(1, -5, 0, 6, 1.4, 6, "brass2")] + piston(1),
             extra_fore=[C(0, 3, 0, 10, 4, 10, "copper"), C(0, 13, -1, 8, 8, 8, "iron"), C(0, 17.5, -1, 6, 1, 6, "furnace"),
                         C(-3, 18, -4, 2, 3, 2, "dark"), C(0, 18, -4, 2, 3, 2, "dark"), C(3, 18, -4, 2, 3, 2, "dark")])
    stacks = []
    for x in (-7, 7):
        stacks += [C(x, -37, 5, 4, 12, 4, "pipe"), C(x, -43.5, 5, 5.4, 1.6, 5.4, "dark"), C(x, -33, 5, 5, 1.2, 5, "brass2")]
    torso = rounded(0, -17, 0, 26, 20, 15, "brass", 2) + [
        C(0, -16, -7.8, 12, 11, 1, "dark"), C(0, -16, -8.3, 10, 9, 1, "furnace"), C(0, -16, -8.9, 11, 1, 1, "iron"), C(0, -12.5, -8.9, 11, 1, 1, "iron"),
        C(-3, -16, -8.9, 1, 9, 1, "iron"), C(3, -16, -8.9, 1, 9, 1, "iron"),
        C(0, -28, 0, 28, 4, 16, "iron"), C(0, -30.5, 0, 14, 2, 10, "dark"), C(0, -5, 0, 20, 4, 13, "iron"), C(0, -2.5, 0, 16, 2, 11, "dark"),
        C(0, 1.5, 0, 18, 9, 12, "iron"), C(0, 1, -6.2, 6, 5, 1, "brass2"), C(-8, 1, 0, 3, 4, 10, "brass"), C(8, 1, 0, 3, 4, 10, "brass"),
        C(-10.5, -10, -7.6, 3, 3, 1, "lens"), C(10.5, -10, -7.6, 3, 3, 1, "glass"), C(10.5, -14, -7.6, 3, 3, 1, "glass")] + stacks
    torso += [C(x, y, -7.9, 1, 1, 1, "rivet") for x in (-12, 12) for y in (-24, -20, -16, -12, -8)]
    torso += [C(0, -20, 8, 18, 12, 2, "copper"), C(-5, -20, 9.4, 3, 10, 1, "pipe"), C(5, -20, 9.4, 3, 10, 1, "pipe"), C(0, -26, 9.4, 13, 2, 1, "pipe")]
    body = P("body", (0, -4, 0), (0, 0, 0), torso, [head, ra, la, gear("spin_g1", -8, -21, -8.1), gear("spin_g2", 9, -22, -8.1, 3),
             gear("spin_g3", 0, -20, 9.6, 5),
             P("bob_s1", (-7, -47, 5), (0, 0, 0), [C(0, 0, 0, 4, 4, 4, "steam"), C(1.5, -2, 0.5, 2.6, 2.6, 2.6, "steam")]),
             P("bob_s2", (7, -48, 5), (0, 0, 0), [C(0, 0, 0, 4, 4, 4, "steam"), C(-1.5, -2.5, 0, 2.6, 2.6, 2.6, "steam")])])
    def sl(name, x):
        l = leg(name, (x, 0, 0), (9, 12), (9, 12), "iron", None, None)
        l.boxes += rounded(0, 2.5, 0, 11, 6, 11, "brass", 1.5) + [C(0, 2.5, -5.8, 4, 4, 1, "copper")]
        l.children[0].boxes += [C(0, 9, -1, 12, 6, 14, "brass"), C(0, 12.5, -1, 13, 1, 15, "dark"), C(0, 4, -4.8, 6, 6, 1, "pipe"),
                                C(-5, 1, 2, 1.6, 6, 1.6, "pipe"), C(5, 1, 2, 1.6, 6, 1.6, "pipe")]
        return l
    return [body, sl("right_leg", -7), sl("left_leg", 7)], m


@spec
def nox():
    """밤의 군주: 공중에 뜬 해골 망령 왕. 너덜너덜한 망토 자락 여러 갈래, 가시 왕관, 빛나는 갈비뼈, 거대한 낫"""
    m = {"robe": M(0x24142E, "robe", glow=0xB070FF), "robe2": M(0x3A2050, "robe"), "bone": M(0xDCD4C2, "bone"), "void": M(0xA050FF, glow_all=True),
         "metal": M(0x3E3A4A, "metal"), "metal2": M(0x6A6080, "metal"), "blade": M(0xB088FF, "crystal", glow=0xE0C0FF), "dark": M(0x0E0812, "cloth"),
         "chain": M(0x585868, "metal"), "rune": M(0x1E1028, "rune", glow=0xC080FF)}
    m["skull"] = FACE3(m["bone"], 0xC070FF, kind="skull", mouth=0x1A1020, fangs=True, eye_w=0.2, eye_y=0.36)
    crown = [C(0, -10, 0, 10, 1.6, 10, "metal")] + [C(x, -12.5 - hh / 2, z, 1.4, hh, 1.4, "metal2") for x, z, hh in
             ((-4.4, -4.4, 3), (-2.2, -4.6, 5), (0, -4.8, 7), (2.2, -4.6, 5), (4.4, -4.4, 3), (-4.6, 0, 4), (4.6, 0, 4), (0, 4.6, 4))] + [C(0, -12, -5.2, 2, 2, 1, "void")]
    hood = [C(0, -5, 1.5, 12, 12, 11, "robe"), C(0, -11.5, 2.5, 9, 2, 9, "robe"), C(0, 1, 2, 13, 2, 11, "robe2"), C(0, -5, -4.4, 10, 1, 1, "robe2")]
    head = P("head", (0, -21, -1), (0, 0, 0), [C(0, -4.5, -1, 8, 9, 8, "bone"), C(0, -4.5, -5.1, 8, 9, 1, "bone", {"front": m["skull"]}), C(0, 0.5, -3, 6, 2, 5, "bone")] + hood + crown)
    scythe = [C(0, 4, -6, 1.4, 40, 1.4, "metal"), C(0, -16, -6, 3, 3, 3, "metal2"), C(0, -16, -6, 1.6, 1.6, 4.4, "void")]
    blade = P("scythe_blade", (0, -16, -6), (0, 0, 0), [C(0, 0, -5, 1, 3, 10, "blade"), C(0, 1.5, -11, 1, 4, 4, "blade"), C(0, 3.5, -13.5, 1, 4, 2, "blade"),
              C(0, -1.6, -5, 1.4, 1, 10, "metal2"), C(0, 6, -14.5, 1, 3, 1, "blade")], field=False)
    scythe_parts = [blade, P("scythe_tail", (0, 24, -6), (0, 0, 0), [C(0, 0, 0, 2, 3, 2, "void"), C(0, 2.5, 0, 1, 2, 1, "metal2")], field=False)]
    ra = arm("right", (-8, -19, 0), (3, 9), (2.4, 9), "bone", "bone", extra_upper=[C(-0.5, 4, 0, 6, 9, 6, "robe"), C(-1, -1, 0, 7, 4, 7, "metal"), C(-1, -3.5, 0, 2, 3, 2, "metal2")],
             extra_fore=scythe + [C(0, 5, 0, 5.6, 5, 5.6, "robe2"), C(0, 8, 0, 6.4, 1, 6.4, "dark")])
    ra.children[0].children += scythe_parts
    la = arm("left", (8, -19, 0), (3, 9), (2.4, 9), "bone", "bone", extra_upper=[C(0.5, 4, 0, 6, 9, 6, "robe"), C(1, -1, 0, 7, 4, 7, "metal"), C(1, -3.5, 0, 2, 3, 2, "metal2")],
             extra_fore=[C(0, 5, 0, 5.6, 5, 5.6, "robe2"), C(0, 8, 0, 6.4, 1, 6.4, "dark"), C(0, 13, -1, 4, 4, 4, "void"), C(0, 13, -1, 6, 1, 1, "metal2")])
    rib = [C(0, -14, -4.4, 7, 7, 1, "dark")] + [C(0, -16.5 + k * 1.8, -4.9, 6 - k, 0.8, 1, "bone") for k in range(4)] + [C(0, -14, -4.9, 1, 7, 1, "bone"), C(0, -13.5, -5.2, 2, 2, 1, "void")]
    strands = []
    for i, (x, z, n) in enumerate(((-5, -3, 5), (0, -4, 6), (5, -3, 5), (-5.5, 3, 6), (0, 4, 7), (5.5, 3, 6))):
        letter = "RSTUVW"[i]
        st = chain("sway" + letter, n, (x, -3, z), 4, 5, 2, "robe" if i % 2 == 0 else "dark", axis="y", h0=3, h1=1)
        strands.append(st)
    capeP = cape((0, -18, 5), 6, 16, 5, "dark")
    ring = P("ring", (0, -12, 0), (0, 0, 0), [], [P(f"nx{i}", (math.cos(i * math.pi / 3) * 15, math.sin(i * 2.1) * 2, math.sin(i * math.pi / 3) * 15), (0, 0, 0),
              [C(0, 0, 0, 3, 3, 3, "void"), C(0, 0, 0, 1, 5, 1, "rune")], field=False) for i in range(6)])
    chains = [C(-6, -6, -4.6, 1, 1, 1, "chain"), C(-4, -5, -4.8, 1, 1, 1, "chain"), C(-2, -4.5, -5, 1, 1, 1, "chain"), C(0, -4.3, -5, 1, 1, 1, "chain"),
              C(2, -4.5, -5, 1, 1, 1, "chain"), C(4, -5, -4.8, 1, 1, 1, "chain"), C(6, -6, -4.6, 1, 1, 1, "chain")]
    torso = stack(0, -19, -2, 0, 13, 9, 8, 7, "robe", 4) + rib + chains + [C(0, -18.5, 0, 16, 3, 10, "metal"), C(0, -20.5, 0, 12, 2, 8, "metal2"),
             C(0, -3, 0, 12, 2, 8.4, "rune")] + spikes(5, -7, 7, -19.5, 1, 1, 3, 3, "metal2")
    body = P("body", (0, 0, 0), (0, 0, 0), torso, [head, ra, la, capeP, ring] + strands)
    return [body], m


@spec
def prima():
    """원초의 여제: 일곱 속성 결정 날개, 네 팔, 별의 관"""
    cols = {"fire": 0xFF6A30, "water": 0x3AA0FF, "grass": 0x60E060, "wind": 0xB0FFF0, "dark": 0xA050FF, "light": 0xFFF090, "elec": 0x60E0FF}
    m = {k: M(v, "crystal", glow=v) for k, v in cols.items()}
    m.update({"body": M(0xF0EEFF, "plate"), "gold": M(0xF0C040, "metal"), "gold2": M(0xC89020, "metal"), "robe": M(0x2E2258, "robe", glow=0xD8C0FF), "robe2": M(0x4A3A88, "robe"),
              "core": M(0xFFFFFF, glow_all=True), "skin": M(0xE8E0FF, "flesh"), "hair": M(0xF4F0FF, "fur"), "star": M(0xFFF6D0, glow_all=True)})
    m["face"] = FACE3(m["skin"], 0xFFFFFF, kind="human", iris=0xE0D0FF, brow=0xC0B8E0, mouth=0x9A6A9A, mark="forehead", mark_col=0xFFF6D0)
    crown = [C(0, -9.6, 0, 9, 1.4, 9, "gold")] + [C(x, -11.5 - hh / 2, -4.2, 1.2, hh, 1.2, k) for x, k, hh in
             ((-4, "fire", 2), (-2.6, "water", 3), (-1.3, "grass", 4), (0, "light", 6), (1.3, "wind", 4), (2.6, "elec", 3), (4, "dark", 2))]
    hair = chain("swayH", 4, (0, -4, 3.8), 4, 8, 6, "hair", axis="y", h0=2, h1=1)
    head = P("head", (0, -30, 0), (0, 0, 0), [C(0, -4, 0, 8, 8, 8, "skin"), C(0, -4, -4.1, 8, 8, 1, "skin", {"front": m["face"]}),
             C(0, -6, 1.2, 8.8, 5, 7.6, "hair"), C(-4.2, -3.5, 1, 1, 7, 6, "hair"), C(4.2, -3.5, 1, 1, 7, 6, "hair"), *crown], [hair])
    def arm4(side, y, gem, idx):
        s = -1 if side == "right" else 1
        nm = f"{side}_arm" if idx == 0 else f"sway{'P' if s < 0 else 'Q'}1"
        fore = [C(s * 0.5, 13, 0, 3, 8, 3, "body"), C(s * 0.5, 9.5, 0, 4, 1.4, 4, "gold"), C(s * 0.5, 18.5, 0, 4.4, 4.4, 4.4, gem), C(s * 0.5, 18.5, 0, 6.4, 1, 1, "gold2"), C(s * 0.5, 18.5, 0, 1, 6.4, 1, "gold2")]
        return P(nm, (s * 8, y, 0), (0, 0, s * R(-22 - idx * 34)), [C(s * 0.5, 4, 0, 3.4, 9, 3.4, "body"), C(s * 0.5, 0, 0, 5.4, 4, 5.4, "gold"), C(s * 0.5, -2.4, 0, 2, 2, 2, gem)] + fore)
    arms = [arm4("right", -25, "fire", 0), arm4("left", -25, "water", 0), arm4("right", -19, "dark", 1), arm4("left", -19, "light", 1)]
    # 결정 날개: 일곱 속성 칼날을 부채꼴로
    wings = []
    for s in (-1, 1):
        side = "right" if s < 0 else "left"
        blades = []
        for i, k in enumerate(cols):
            a = R(-5 + i * 12)
            L = 11 + 6 * math.sin(i / 6 * math.pi)
            blades.append(P(f"pw{side[0]}{i}", (0, 0, 0.4 * i), (0, 0, s * (R(90) + a)), [B(-1.2, -L, -0.5, 2.4, L, 1, k), B(-0.6, -L - 3, -0.5, 1.2, 3, 1, k)], field=False))
        wings.append(P(f"{side}_wing", (s * 4, -26, 5), (0, s * R(-35), 0), [C(0, 0, 0, 3, 3, 3, "gold")], blades))
    halo = P("halo", (0, -37, 6), (0, 0, 0), [C(math.cos(i * math.pi / 10) * 9.5, math.sin(i * math.pi / 10) * 9.5, 0, 1.6, 1.6, 1, "star") for i in range(20)] +
             [C(math.cos(a) * 12.5, math.sin(a) * 12.5, 0, 3, 3, 1, k) for a, k in zip([i * 2 * math.pi / 7 - math.pi / 2 for i in range(7)], cols)])
    ring = P("ring", (0, -14, 0), (0, 0, 0), [], [P(f"porb{k}", (math.cos(i * 2 * math.pi / 7) * 20, math.sin(i * 1.7) * 2, math.sin(i * 2 * math.pi / 7) * 20), (0, 0, 0),
              [C(0, 0, 0, 4, 4, 4, k), C(0, 0, 0, 1.4, 7, 1.4, k)], field=False) for i, k in enumerate(cols)])
    robe = chain("swayR", 4, (0, -11, 0), 6, 13, 20, "robe", axis="y", h0=9, h1=14)
    e = robe
    while e.children: e = e.children[0]
    e.boxes += [C(0, 6, 0, 21, 1.4, 15, "gold")]
    capeP = cape((0, -27, 4.6), 6, 15, 5, "robe2")
    torso = stack(0, -27, -12, 0, 12, 9, 7.5, 6, "body", 4) + [C(0, -21, -3.9, 6, 6, 1, "gold"), C(0, -21, -4.5, 3.4, 3.4, 1, "core"),
             C(0, -26.5, 0, 17, 3, 9, "gold"), C(-8.5, -28, 0, 5, 4, 7, "gold"), C(8.5, -28, 0, 5, 4, 7, "gold"), C(-8.5, -30.5, 0, 2, 2, 2, "fire"), C(8.5, -30.5, 0, 2, 2, 2, "water"),
             C(0, -12, 0, 13, 2, 8, "gold"), C(0, -12, -4.2, 3, 3, 1, "light")]
    body = P("body", (0, 0, 0), (0, 0, 0), torso, [head, halo, ring, robe, capeP] + arms + wings)
    return [body], m


@spec
def lava_queen():
    """용암 여왕: 거미 몸통 위의 여왕. 불꽃 머리칼, 흑요석 흉갑, 마디진 다리"""
    m = {"chitin": M(0x2A1410, "plate"), "chitin2": M(0x3E2018, "scale", accent=0x1A0A08), "lava": M(0x5A1A10, "lava", glow=0xFFA030), "skin": M(0xC8603A, "flesh"),
         "gold": M(0xF0B030, "metal"), "fire": M(0xFFB040, glow_all=True), "fire2": M(0xFF6A20, glow_all=True), "fang": M(0xF0E8D0, "bone"),
         "obs": M(0x1A1020, "crystal", glow=0x8A3AFF), "ember": M(0xFF7A30, "gem", glow=0xFFB060)}
    m["face"] = FACE3(m["skin"], 0xFFE040, kind="human", iris=0xFFC020, brow=0x401008, mouth=0x601818, mark="stripes", mark_col=0xFFB040)
    flames = [C(x, -10 - hh / 2, z, 1.6, hh, 1.6, "fire" if (i % 2) else "fire2") for i, (x, z, hh) in enumerate(((-3, 1, 4), (-1, 2, 6), (1, 2, 7), (3, 1, 4), (0, 3.5, 5), (-2, 4, 3), (2, 4, 3)))]
    hair = chain("swayF", 4, (0, -3, 3.6), 4, 7, 3, "fire2", axis="y", h0=2, h1=1)
    head = P("head", (0, -15, -10), (0, 0, 0), [C(0, -3.5, 0, 7, 7, 7, "skin"), C(0, -3.5, -3.6, 7, 7, 1, "skin", {"front": m["face"]}),
             C(0, -7.4, 0, 8, 1.4, 8, "gold"), C(0, -8.6, -4, 2, 2, 1, "ember"), C(-3.8, -4, 0, 1, 5, 6, "fire2"), C(3.8, -4, 0, 1, 5, 6, "fire2")] + flames, [hair])
    torso = stack(0, -12, -1, -10, 9, 7, 6, 5, "skin", 3) + [C(0, -8, -12.7, 9, 6, 1, "obs"), C(0, -8, -13.2, 3, 3, 1, "ember"),
             C(0, -11.5, -10, 11, 2.4, 7, "gold"), C(-5.5, -12, -10, 3, 3, 6, "chitin"), C(5.5, -12, -10, 3, 3, 6, "chitin"),
             C(0, -1, -10, 10, 3, 7, "chitin2"), C(-5.5, -14, -10, 1, 3, 1, "fang"), C(5.5, -14, -10, 1, 3, 1, "fang")]
    spear = [C(0, 6, -2, 1.2, 26, 1.2, "chitin"), C(0, -8, -2, 3, 2, 3, "gold"), C(0, -11, -2, 2, 5, 1, "fire"), C(0, -14, -2, 1, 2, 1, "fire2"),
             C(-1.6, -9.5, -2, 1, 2, 1, "fire2"), C(1.6, -9.5, -2, 1, 2, 1, "fire2")]
    ra = arm("right", (-5.5, -11, -10), (3, 7), (2.6, 7), "skin", "chitin", shoulder="chitin", extra_fore=spear + [C(0, 2, 0, 3.6, 3, 3.6, "gold")])
    la = arm("left", (5.5, -11, -10), (3, 7), (2.6, 7), "skin", "chitin", shoulder="chitin", extra_fore=[C(0, 2, 0, 3.6, 3, 3.6, "gold"), C(0, 9.5, -1, 3, 3, 3, "fire")])
    abdomen = P("tail1", (0, -1, 6), (R(-18), 0, 0), rounded(0, -3, 9, 17, 14, 19, "lava", 2) + [C(0, -10.4, 9, 13, 2, 15, "chitin2"),
                C(0, -3, 18.8, 7, 7, 1, "fire"), C(0, -3, 19.4, 3, 3, 1, "fire2")] +
                [C(x, -10.5, z, 2.4, 2.4, 2.4, "obs") for x, z in ((-4, 4), (4, 7), (-3, 11), (3, 14))] + spikes(5, 0, 0, -11.5, 2, 16, 2, 4, "chitin", w=2))
    legs = []
    for i in range(8):
        side = -1 if i % 2 == 0 else 1
        k = i // 2
        z = -5 + k * 4.2
        yaw = side * R(-42 + k * 28)
        tip = P(f"leg{i + 1}c", (side * 12, 0, 0), (0, 0, side * R(35)), [C(side * 5, 0, 0, 10, 1.6, 1.6, "chitin"), C(side * 10, 0, 0, 2, 2, 2, "fang")], field=False)
        outer = P(f"leg{i + 1}b", (side * 11, 0, 0), (0, 0, side * R(55)), [C(side * 6, 0, 0, 12, 2.4, 2.4, "chitin2"), C(side * 1, -1.6, 0, 1, 2, 1, "fang"), C(side * 6, 0, 0, 3, 3, 3, "lava")], [tip])
        legs.append(P(f"leg{i + 1}", (side * 5, 1, z), (0, yaw, side * R(-35)), [C(side * 5.5, 0, 0, 11, 3.4, 3.4, "chitin"), C(side * 10.6, 0, 0, 3.6, 3.6, 3.6, "lava")], [outer]))
    body = P("body", (0, 11, 0), (0, 0, 0), rounded(0, 0, 0, 13, 9, 15, "chitin", 1.5) + [C(0, 1, 0, 14, 4, 16, "lava"), *torso,
             C(-2.4, 3, -8, 1.4, 4, 1.4, "fang"), C(2.4, 3, -8, 1.4, 4, 1.4, "fang"), C(0, -4.8, 0, 9, 1, 11, "chitin2")], [head, ra, la, abdomen] + legs)
    return [body], m


@spec
def marina():
    """바다의 여왕: 인어. 산호 왕관, 물결 머리칼, 조개 갑옷, 지느러미 꼬리, 삼지창"""
    m = {"skin": M(0x9AD8D8, "flesh"), "tail": M(0x1E8C8C, "scale", accent=0x0B4F5A), "tail2": M(0x2FB0A8, "scale", accent=0x157070), "hair": M(0x2A50B8, "fur"),
         "gold": M(0xF0C860, "metal"), "pearl": M(0xF4F0FF, "gem", glow=0xBFE8FF), "fin": M(0x5FE0E0, "veins", glow=0xCFFFFF),
         "water": M(0x6FD8FF, glow_all=True), "coral": M(0xFF7A8A, "stone"), "shell": M(0xF0DDF0, "bone"), "shell2": M(0xD8A8D0, "plate")}
    m["face"] = FACE3(m["skin"], 0x9FFFFF, kind="human", iris=0x30C0E0, brow=0x1A3080, mouth=0x5A6A80, blush=0xFF9AAA)
    crown = [C(0, -9.4, 0, 9, 1.2, 9, "gold")] + [C(x, -11 - hh / 2, -4, 1.4, hh, 1.4, "coral") for x, hh in ((-3.5, 3), (-1.5, 4), (1.5, 4), (3.5, 3))] + \
            [C(0, -12.5, -4, 1.4, 5, 1.4, "pearl"), C(-2.6, -13.5, -4, 1, 1, 1, "coral"), C(2.6, -13.5, -4, 1, 1, 1, "coral")]
    hair = [chain("swayH", 5, (0, -5, 4), 4, 9, 5, "hair", axis="y", h0=3, h1=1.5), chain("swayI", 4, (-4, -4, 2.5), 4, 3, 2, "hair", axis="y", h0=2, h1=1),
            chain("swayJ", 4, (4, -4, 2.5), 4, 3, 2, "hair", axis="y", h0=2, h1=1)]
    head = P("head", (0, -15, 0), (0, 0, 0), [C(0, -4, 0, 8, 8, 8, "skin"), C(0, -4, -4.1, 8, 8, 1, "skin", {"front": m["face"]}), C(0, -6.8, 1, 9, 3.6, 8, "hair"),
             C(-4.6, -3, 0.5, 1, 4, 4, "fin"), C(4.6, -3, 0.5, 1, 4, 4, "fin"), C(-5.4, -4.5, 1.5, 1, 3, 3, "fin"), C(5.4, -4.5, 1.5, 1, 3, 3, "fin"), *crown], hair)
    trident = [C(0, -2, -4, 1.2, 32, 1.2, "gold"), C(0, -18, -4, 8, 1.2, 1.2, "gold"), C(-3.6, -20.5, -4, 1.2, 5, 1.2, "pearl"),
               C(0, -22.5, -4, 1.4, 8, 1.4, "pearl"), C(3.6, -20.5, -4, 1.2, 5, 1.2, "pearl"), C(0, -16.5, -4, 3, 3, 3, "water"), C(0, 14, -4, 2, 2, 2, "pearl")]
    ra = arm("right", (-6, -13, 0), (3, 7), (3, 7), "skin", "skin", extra_upper=[C(-0.5, 0, 0, 5, 3, 5, "shell"), C(-0.5, -2, 0, 4, 1, 4, "shell2")],
             extra_fore=trident + [C(0, 3, 0, 3.6, 2, 3.6, "gold")])
    la = arm("left", (6, -13, 0), (3, 7), (3, 7), "skin", "skin", extra_upper=[C(0.5, 0, 0, 5, 3, 5, "shell"), C(0.5, -2, 0, 4, 1, 4, "shell2")],
             extra_fore=[C(0, 3, 0, 3.6, 2, 3.6, "gold"), C(0, 10, -2, 5, 5, 5, "water"), C(0, 10, -2, 2.4, 2.4, 2.4, "pearl")])
    tail = chain("tail", 6, (0, 3, 0), 5, 11, 3, "tail", axis="y", h0=8, h1=2.4,
                 decor=lambda i, w, h, L: [C(0, L / 2, h / 2 + 0.6, 1.2, L - 1, 2.6, "fin"), C(0, L / 2, -h / 2 - 0.3, w * 0.7, L, 1, "tail2")])
    t = tail
    while t.children: t = t.children[0]
    t.boxes += [P and B(-11, 3, -0.5, 10, 8, 1, "fin"), B(1, 3, -0.5, 10, 8, 1, "fin"), B(-8, 10, -0.5, 6, 3, 1, "fin"), B(2, 10, -0.5, 6, 3, 1, "fin")]
    tail.rot = (R(12), 0, 0)
    ring = P("ring", (0, -6, 0), (0, 0, 0), [], ring_of("orb", 10, 14, 2.4, "water") + ring_of("pearlo", 5, 10, 1.4, "pearl", y=-3))
    torso = stack(0, -12, 2, 0, 10, 8, 6, 6, "skin", 3) + [C(-2.6, -8.5, -3.2, 4, 4, 1.4, "shell"), C(2.6, -8.5, -3.2, 4, 4, 1.4, "shell"),
             C(0, -8.5, -3.8, 1.4, 1.4, 1, "pearl"), C(0, -12, 0, 11, 2, 7, "gold"), C(0, 1.5, 0, 11, 3, 7, "tail2"), C(0, 0, -3.8, 2.4, 2.4, 1, "pearl"),
             C(-6, -5, 0, 1, 7, 5, "coral"), C(6, -5, 0, 1, 7, 5, "coral"), C(0, 3.5, -3.6, 10, 1, 1, "pearl")]
    body = P("body", (0, -2, 0), (0, 0, 0), torso, [head, ra, la, tail, ring])
    return [body], m


@spec
def silva():
    """숲의 고목 수호자: 나뭇가지 뿔 관, 이끼 망토, 빛나는 심장, 뿌리 손가락, 늘어진 덩굴"""
    m = {"bark": M(0x5C3A1E, "bark", accent=0x8A6A3A), "bark2": M(0x7A5430, "bark", accent=0x9A7A4A), "leaf": M(0x2E7D32, "leaf", accent=0x7CC46A, glow=0xFFB7D5),
         "leaf2": M(0x4CA040, "leaf", accent=0xA8E070), "moss": M(0x6B8E23, "fur"), "flower": M(0xFF8FC0, glow_all=True), "root": M(0x3E2A16, "bark"),
         "core": M(0x9CFF6A, "gem", glow=0xB8FF80), "mush": M(0xE85040, "stone"), "mushs": M(0xF4E8D0, "bone"), "vine": M(0x3A7A30, "leaf", accent=0x5AA048)}
    m["face"] = FACE3(m["bark2"], 0xB8FF80, kind="beast", skin_shadow=0.45, mouth=0x120A04, eye_y=0.4, eye_w=0.18)
    def antler(name, s):
        b1 = P(name, (s * 4, -7, 0), (0, 0, s * R(35)), [C(0, -3, 0, 2, 6, 2, "root")], [
            P(name + "b", (0, -6, 0), (0, 0, -s * R(30)), [C(0, -3, 0, 1.6, 6, 1.6, "root"), C(0, -6.5, 0, 4, 3, 4, "leaf2"), C(s * 1, -8, 0, 1.4, 1.4, 1.4, "flower")], field=False),
            P(name + "c", (0, -3, 0), (0, 0, s * R(55)), [C(0, -2.5, 0, 1.4, 5, 1.4, "root"), C(0, -5.5, 0, 3, 2.4, 3, "leaf")], field=False)], field=False)
        return b1
    canopy = [C(0, -12, 3, 20, 7, 14, "leaf"), C(-7, -15, 4, 9, 5, 9, "leaf2"), C(6, -16, 2, 10, 6, 10, "leaf"), C(0, -18, 5, 9, 4, 8, "leaf2")]
    canopy += [C(x, y, z, 2, 2, 2, "flower") for x, y, z in ((-8, -13, -3), (5, -18, -2), (9, -11, -3.5), (-3, -19, 2), (0, -11, -4.5))]
    head = P("head", (0, -27, -1), (0, 0, 0), rounded(0, -3.5, 0, 12, 8, 10, "bark2") + [C(0, -3.5, -5.2, 12, 8, 1, "bark2", {"front": m["face"]}),
             C(0, 1, -4, 8, 2, 4, "moss"), C(0, 2, -3, 6, 3, 2, "vine")] + canopy, [antler("antL", 1), antler("antR", -1)])
    def hand(s):
        b = [C(0, 12, 0, 6, 4, 6, "bark")] + [C(s * (i - 1) * 2, 16, -1.5, 1.4, 5 + (i % 2), 1.4, "root") for i in range(3)] + [C(-s * 3, 14, 1, 1.4, 4, 1.4, "root")]
        return b
    ra = arm("right", (-9, -24, 0), (7, 10), (6, 10), "bark", None, extra_upper=[C(-0.5, -1, 0, 9, 5, 9, "moss"), C(-1, -3.5, -2, 4, 3, 4, "leaf2"), C(-2, 4, -3.6, 3, 2, 1, "mush")],
             extra_fore=hand(-1) + [C(0, 3, -3.4, 3, 1, 2, "mush"), C(0, 3.6, -3.4, 2, 1, 2, "mushs")])
    la = arm("left", (9, -24, 0), (7, 10), (6, 10), "bark", None, extra_upper=[C(0.5, -1, 0, 9, 5, 9, "moss"), C(1, -3.5, 2, 4, 3, 4, "leaf2")], extra_fore=hand(1))
    vines = [chain("swayV", 4, (-5, -18, -5.6), 3.5, 1.4, 1, "vine", axis="y"), chain("swayW", 3, (4, -20, -5.6), 3.5, 1.4, 1, "vine", axis="y"),
             chain("swayX", 4, (6, -24, 5.6), 3.5, 1.4, 1, "vine", axis="y")]
    capeP = cape((0, -25, 6), 5, 16, 5, "moss", depth=2)
    torso = stack(0, -27, -2, 0, 16, 13, 11, 10, "bark", 5) + [C(0, -16, -6, 7, 7, 1, "bark2"), C(0, -16, -6.6, 4, 4, 1, "core"),
             C(0, -26, 0, 18, 4, 12, "moss"), C(0, -3, 0, 16, 3, 12, "moss"), C(-8, -10, -2, 3, 1.4, 3, "mush"), C(-8, -9.2, -2, 2, 1, 2, "mushs"),
             C(7, -6, -5.7, 3, 2, 1, "flower"), C(-4, -21, -5.8, 3, 3, 1, "leaf2"), C(6, -14, -5.6, 2, 6, 1, "root")]
    body = P("body", (0, 4, 0), (0, 0, 0), torso, [head, ra, la, capeP] + vines)
    def rl(name, x):
        l = leg(name, (x, 4, 0), (8, 10), (7, 10), "bark", "root", None)
        l.boxes.append(C(0, 2, 0, 10, 3, 10, "moss"))
        sh = l.children[0]
        sh.boxes += [C(-3.5, 9, -3.5, 2, 3, 2, "root"), C(3.5, 9, -3.5, 2, 3, 2, "root"), C(0, 9.5, -5, 2, 2, 3, "root"), C(-3.5, 9.5, 3, 2, 2, 2, "root")]
        return l
    return [body, rl("right_leg", -4.5), rl("left_leg", 4.5)], m


@spec
def voltra():
    """뇌신 볼트라: 날개 투구, 번개 결정 견갑, 겹판 치마, 번개 창, 번개 망토"""
    m = {"armor": M(0x2A3050, "plate"), "armor2": M(0x3A4270, "metal"), "gold": M(0xF0C040, "metal"), "bolt": M(0x9FE8FF, glow_all=True), "skin": M(0xC8D0E8, "flesh"),
         "cloth": M(0x1A2A6A, "cloth"), "crystal": M(0x60E0FF, "crystal", glow=0xB0F8FF), "dark": M(0x141828, "metal"), "fur": M(0xE8ECF4, "fur")}
    m["helm"] = FACE3(m["armor"], 0x9FF8FF, kind="visor", eye_y=0.4)
    wingsH = []
    for s in (-1, 1):
        for k in range(3):
            wingsH.append(P(f"vh{'l' if s > 0 else 'r'}{k}", (s * 4.6, -6, 1), (R(-20), 0, s * R(30 + k * 18)), [B(-0.6, -6 + k, -1.5, 1.2, 6 - k, 3, "gold" if k < 2 else "bolt")], field=False))
    head = P("head", (0, -26, 0), (0, 0, 0), rounded(0, -4.5, 0, 9, 9, 9, "armor", 1) + [C(0, -4.5, -4.6, 9, 9, 1, "armor", {"front": m["helm"]}),
             C(0, -9.4, 0, 2.4, 2, 10, "gold"), C(0, -10.5, 1, 1.4, 1.4, 6, "bolt"), C(0, -1, -5, 6, 2, 1, "gold"), C(0, -2.6, -5.4, 1.4, 3, 1, "gold")], wingsH)
    spear = [C(0, 6, -3, 1.2, 40, 1.2, "gold"), C(0, -15, -3, 3.4, 3.4, 3.4, "crystal"), C(0, -20, -3, 1.6, 7, 1.6, "bolt"),
             C(-2, -18, -3, 1, 4, 1, "bolt"), C(2, -18, -3, 1, 4, 1, "bolt"), C(-3, -17, -3, 1, 1, 1, "bolt"), C(3, -17, -3, 1, 1, 1, "bolt"),
             C(0, -12.5, -3, 4.4, 1, 1.6, "gold"), C(0, 26, -3, 2.4, 2.4, 2.4, "crystal")]
    paul = lambda s: [C(s * 1, -1, 0, 8, 4, 8, "armor2"), C(s * 1.5, -3.4, 0, 7, 2, 7, "gold"), C(s * 3, -5.5, -1.5, 1.6, 4, 1.6, "crystal"), C(s * 3, -6.5, 1.5, 1.6, 6, 1.6, "crystal"),
                      C(s * 1, 1.8, 0, 8.4, 1, 8.4, "dark")]
    ra = arm("right", (-8, -24, 0), (4, 9), (4, 9), "armor", "gold", extra_upper=paul(-1), extra_fore=spear + [C(0, 3, 0, 5, 4, 5, "armor2"), C(0, 3, -2.6, 2, 2, 1, "bolt")])
    la = arm("left", (8, -24, 0), (4, 9), (4, 9), "armor", "gold", extra_upper=paul(1), extra_fore=[C(0, 3, 0, 5, 4, 5, "armor2"), C(0, 3, -2.6, 2, 2, 1, "bolt"), C(0, 12.5, -1, 3, 3, 3, "bolt")])
    halo = P("halo", (0, -31, 6), (0, 0, 0), [C(0, 0, 0, 3, 3, 1, "crystal")] + [C(math.cos(i * math.pi / 6) * 9, math.sin(i * math.pi / 6) * 9, 0, 1.4 + (i % 2), 3 - (i % 2), 1, "bolt") for i in range(12)])
    capeP = cape((0, -22, 4.6), 6, 14, 5, "cloth")
    e = capeP
    while e.children: e = e.children[0]
    e.boxes += [C(0, 5.2, 0, 14.6, 1, 1.4, "bolt")]
    capeP.boxes += [C(0, 0, 0.5, 16, 2, 2.4, "fur")]
    skirt = [P(f"vsk{i}", (x, -6, z), (rx, 0, rz), [C(0, 3.5, 0, w, 7, 1, "armor2"), C(0, 7, 0, w, 1, 1.2, "gold")], field=False)
             for i, (x, z, w, rx, rz) in enumerate(((0, -4, 6, R(-10), 0), (-5, -2.5, 4, R(-6), R(10)), (5, -2.5, 4, R(-6), R(-10)), (-5.5, 2.5, 4, R(6), R(10)), (5.5, 2.5, 4, R(6), R(-10))))]
    torso = stack(0, -24, -8, 0, 13, 11, 8, 7, "armor", 4) + [C(0, -18, -4.2, 7, 6, 1, "gold"), C(0, -18, -4.8, 3, 4, 1, "bolt"),
             C(0, -7, 0, 14, 3, 8.6, "gold"), C(0, -7, -4.6, 3, 3, 1, "crystal"), C(0, -22.5, 0, 15, 2.4, 9, "armor2"),
             C(-3, -13, -4, 3, 1, 1, "bolt"), C(3, -13, -4, 3, 1, 1, "bolt"), C(0, -3, 0, 12, 6, 7, "cloth")]
    body = P("body", (0, 0, 0), (0, 0, 0), torso, [head, ra, la, halo, capeP] + skirt)
    def vl(name, x):
        l = leg(name, (x, -4, 0), (5, 9), (4.4, 10), "armor", "gold", None, armor="gold")
        l.children[0].boxes += [C(0, 1, -2.6, 4, 3, 1, "armor2"), C(0, 0, -3.2, 1.4, 1.4, 1, "bolt"), C(0, 8, 0, 5.4, 3, 5.4, "armor2")]
        return l
    return [body, vl("right_leg", -3), vl("left_leg", 3)], m


@spec
def lumiel():
    """대천사 루미엘: 네 날개, 깃털 투구, 성검과 방패, 겹판 치마, 빛의 관"""
    m = {"armor": M(0xE8ECF4, "plate"), "armor2": M(0xC8D0E0, "metal"), "gold": M(0xF0C040, "metal"), "skin": M(0xF2D2B0, "flesh"),
         "wing": M(0xFFFDF0, "feather", accent=0xFFE7A0), "wing2": M(0xF4EEDC, "feather", accent=0xE8D8A0), "light": M(0xFFF6B0, glow_all=True), "cloth": M(0xFFFFFF, "cloth"),
         "cloth2": M(0x5A7AD0, "cloth"), "blade": M(0xFFF8D8, "crystal", glow=0xFFFFC0), "gem": M(0x80C0FF, "gem", glow=0xC0E8FF)}
    m["helm"] = FACE3(m["armor"], 0xFFF080, kind="visor", eye_y=0.42)
    plume = chain("swayP", 3, (0, -9, 1), 3, 2, 1.4, "cloth2", axis="z", h0=3, h1=2)
    plume.rot = (R(40), 0, 0)
    head = P("head", (0, -24, 0), (0, 0, 0), rounded(0, -4.5, 0, 9, 9, 9, "armor", 1) + [C(0, -4.5, -4.6, 9, 9, 1, "armor", {"front": m["helm"]}),
             C(0, -9.4, 0, 2, 2, 10, "gold"), C(0, -4, -5.2, 1.4, 6, 1, "gold"),
             C(-5, -6, 1.5, 1, 3, 4, "wing"), C(5, -6, 1.5, 1, 3, 4, "wing"), C(-5.8, -8, 2.5, 1, 4, 3, "wing"), C(5.8, -8, 2.5, 1, 4, 3, "wing")], [plume])
    halo = P("halo", (0, -35, 4), (0, 0, 0), [C(math.cos(i * math.pi / 8) * 6.5, math.sin(i * math.pi / 8) * 6.5, 0, 1.6, 1.6, 1, "light") for i in range(16)])
    sword = [C(0, 12, -3, 2, 3, 2, "gold"), C(0, 14, -3, 8, 1.4, 2, "gold"), C(-4, 14, -3, 1.6, 2.4, 2.4, "gem"), C(4, 14, -3, 1.6, 2.4, 2.4, "gem"),
             C(0, 26, -3, 3.4, 22, 1, "blade"), C(0, 26, -3.6, 1, 20, 1, "light"), C(0, 37.5, -3, 1.6, 2, 1, "blade")]
    shield = [C(3, 5, 0, 1.4, 14, 10, "armor"), C(3.6, 5, 0, 1, 12, 8, "gold"), C(4.2, 5, 0, 1, 6, 2, "light"), C(4.2, 5, 0, 1, 2, 6, "light"), C(3, 12.6, 0, 1.4, 1.4, 6, "armor")]
    paul = lambda s: [C(s * 1, -1, 0, 7, 4, 7, "armor"), C(s * 1, -3, 0, 6, 1.6, 6, "gold"), C(s * 2.8, -1, 0, 1, 5, 6, "wing2")]
    ra = arm("right", (-7, -22, 0), (4, 9), (4, 9), "armor", "gold", extra_upper=paul(-1), extra_fore=sword)
    la = arm("left", (7, -22, 0), (4, 9), (4, 9), "armor", "gold", extra_upper=paul(1), extra_fore=shield)
    wings = [wing("right", (-4, -20, 4), "gold", "wing", span=40, depth=20, fingers=4, rot=(R(-10), R(-25), R(-35))),
             wing("left", (4, -20, 4), "gold", "wing", span=40, depth=20, fingers=4, rot=(R(-10), R(25), R(35)))]
    small = []
    for s in (-1, 1):
        fe = [P(f"lw{'l' if s > 0 else 'r'}{k}", (0, 0, 0.3 * k), (0, 0, s * R(100 + k * 14)), [B(-1, -14 + k * 2, -0.5, 2, 14 - k * 2, 1, "wing2")], field=False) for k in range(4)]
        small.append(P(f"lws{'l' if s > 0 else 'r'}", (s * 3, -12, 4.2), (R(10), s * R(-20), 0), [C(0, 0, 0, 2, 2, 2, "gold")], fe, field=False))
    skirt = cape((0, -6, -1), 3, 12, 5, "cloth", prefix="swayS", depth=8)
    capeP = cape((0, -20, 4.5), 5, 12, 5, "cloth2")
    torso = stack(0, -22, -7, 0, 12, 10, 7.4, 6.6, "armor", 4) + [C(0, -17, -3.9, 7, 7, 1, "gold"), C(0, -17, -4.5, 3, 3, 1, "light"),
             C(0, -6, 0, 13, 3, 8, "gold"), C(0, -6, -4.2, 2.4, 2.4, 1, "gem"), C(0, -21.5, 0, 13, 2, 8, "armor2"), C(-3, -12, -3.7, 2, 4, 1, "armor2"), C(3, -12, -3.7, 2, 4, 1, "armor2")]
    body = P("body", (0, 0, 0), (0, 0, 0), torso, [head, halo, ra, la, skirt, capeP] + wings + small)
    legs = [leg("right_leg", (-3, -5, 0), (5, 10), (4, 10), "armor", "gold", None, armor="gold"), leg("left_leg", (3, -5, 0), (5, 10), (4, 10), "armor", "gold", None, armor="gold")]
    for l in legs: l.children[0].boxes += [C(0, 0, -2.4, 4, 3, 1, "gold"), C(0, 8.5, 0, 5, 2, 5, "armor2")]
    return [body] + legs, m


@spec
def sphinx():
    """태양의 스핑크스: 큰 파라오 머리, 줄무늬 네메스, 사자 몸, 금 목걸이, 날개, 태양 원반"""
    m = {"fur": M(0xD8A858, "fur"), "fur2": M(0xB88838, "fur"), "gold": M(0xF0C040, "metal"), "blue": M(0x2050B0, "cloth"), "skin": M(0xC08850, "flesh"),
         "sun": M(0xFFE070, glow_all=True), "stone": M(0xE0C890, "stone"), "wing": M(0xF0D890, "feather", accent=0x3060C0), "lapis": M(0x2A5ACC, "gem", glow=0x6A9AFF),
         "claw": M(0x3A2A1A, "metal"), "nemes": M(0x2050B0, "plain")}
    def nemes_p(x, y, w, h, face, rnd):
        c = (32, 80, 176) if (y // 3) % 2 == 0 else (236, 190, 70)
        return c, None
    m["nem"] = nemes_p
    m["face"] = FACE3(m["skin"], 0xFFE070, kind="human", iris=0x3A70E0, brow=0x1A1010, mouth=0x6A3020, mark="stripes", mark_col=0x2A5ACC)
    nemes = [C(0, -6, 1.5, 12, 10, 10, "nem"), C(-6.2, 1, -1, 2, 10, 5, "nem"), C(6.2, 1, -1, 2, 10, 5, "nem"), C(0, -11.2, 0.5, 11, 1.4, 9, "gold"),
             C(0, -12.5, -4.6, 2, 3, 1.4, "gold"), C(0, -14.2, -4.8, 1.4, 1.4, 1.4, "lapis"), C(0, 3, 5.5, 4, 8, 2, "nem")]
    head = P("head", (0, -11, -14), (0, 0, 0), [C(0, -4.5, -1, 10, 10, 9, "skin"), C(0, -4.5, -5.6, 10, 10, 1, "skin", {"front": m["face"]}),
             C(0, 2, -5, 2, 5, 1.4, "gold"), C(0, 4.6, -5, 2.4, 1, 1.4, "lapis")] + nemes)
    collar = [C(0, -6, -12, 16, 3, 9, "gold"), C(0, -4.6, -13, 14, 1.4, 8, "lapis"), C(0, -3.6, -14, 12, 1, 7, "gold")]
    sun = P("halo", (0, -28, -10), (0, 0, 0), [C(0, 0, 0, 8, 8, 1, "sun"), C(0, 0, -0.6, 4, 4, 1, "gold")] +
              [C(math.cos(i * math.pi / 6) * 7.5, math.sin(i * math.pi / 6) * 7.5, 0, 1.6 + (i % 2), 1.6 + (i % 2), 1, "sun") for i in range(12)])
    rw = wing("right", (-6, -6, -4), "gold", "wing", span=36, depth=18, fingers=4, rot=(R(-20), R(-20), R(-50)))
    lw = wing("left", (6, -6, -4), "gold", "wing", span=36, depth=18, fingers=4, rot=(R(-20), R(20), R(50)))
    tail = chain("tail", 5, (0, -3, 12), 4, 2, 1.6, "fur", axis="z")
    t = tail
    while t.children: t = t.children[0]
    t.boxes += [C(0, 0, 4, 3, 3, 4, "fur2"), C(0, 0, 2, 2.4, 2.4, 1, "gold")]
    tail.rot = (R(-30), 0, 0)
    body = P("body", (0, 8, 0), (0, 0, 0), rounded(0, 0, 0, 14, 12, 24, "fur", 1.5) + [C(0, -4, -10, 15, 11, 7, "fur2"), *collar,
             C(0, 6.6, 0, 10, 1, 20, "stone"), C(0, -6.5, 4, 8, 1, 14, "fur2"), C(-7.2, 0, 4, 1, 4, 10, "gold"), C(7.2, 0, 4, 1, 4, 10, "gold")], [head, rw, lw, tail])
    def paw(name, pv, knee=None):
        l = leg(name, pv, (5, 6), (4.4, 5), "fur", "fur", None, knee_name=knee)
        l.children[0].boxes += [C(0, 4.5, -1.5, 6, 2, 7, "fur2")] + [C(x, 5, -5.2, 1, 1, 1.6, "claw") for x in (-2, 0, 2)] + [C(0, 0, 0, 5, 1.4, 5, "gold")]
        return l
    legs = [paw("right_leg", (-5, 13, 9)), paw("left_leg", (5, 13, 9)),
            paw("front_right_leg", (-5, 13, -9), "front_right_leg2"), paw("front_left_leg", (5, 13, -9), "front_left_leg2")]
    return [body, sun] + legs, m


def dragon_head(name, m_scale, face, horn, teethm, glowm, size=1.0, jaw_name="jaw", horn_prefix="horn", extra=()):
    """용 머리: 주둥이 + 눈썹 융기 + 콧구멍 불빛 + 이빨 + 아래턱 + 뿔 2쌍"""
    k = size
    jaw = P(jaw_name, (0, 3 * k, -2 * k), (0, 0, 0), [C(0, 1 * k, -6 * k, 8 * k, 2 * k, 12 * k, m_scale), C(0, 0, -6 * k, 6 * k, 1, 10 * k, glowm),
            *teeth(-3.5 * k, 3.5 * k, 0, -11 * k, teethm, up=True)], field=(jaw_name == "jaw"))
    horns = []
    for s in (-1, 1):
        h = bend_chain(f"{horn_prefix}{'L' if s > 0 else 'R'}", (s * 3.5 * k, -4 * k, 2 * k), [(5 * k, 2.6 * k), (4 * k, 2 * k), (4 * k, 1.4 * k)], horn, bend=(R(-28), 0, 0))
        h.rot = (R(-60), s * R(-18), 0)
        horns.append(h)
        h2 = bend_chain(f"{horn_prefix}{'l' if s > 0 else 'r'}2", (s * 5.4 * k, -1 * k, 0), [(3 * k, 1.6 * k), (3 * k, 1.2 * k)], horn, bend=(R(-25), 0, 0))
        h2.rot = (R(-75), s * R(-40), 0)
        horns.append(h2)
    boxes = rounded(0, -1 * k, -4 * k, 11 * k, 9 * k, 10 * k, m_scale) + [C(0, -1 * k, -9.1 * k, 11 * k, 9 * k, 1, m_scale, {"front": face}),
             C(0, 0.6 * k, -13 * k, 8 * k, 5 * k, 8 * k, m_scale), C(0, -2.6 * k, -12 * k, 6 * k, 1.4 * k, 7 * k, m_scale),
             C(-3.6 * k, -4.4 * k, -7 * k, 3.4 * k, 1.6 * k, 6 * k, m_scale), C(3.6 * k, -4.4 * k, -7 * k, 3.4 * k, 1.6 * k, 6 * k, m_scale),
             C(-2 * k, -1.5 * k, -17.1 * k, 1.4 * k, 1.4 * k, 1, glowm), C(2 * k, -1.5 * k, -17.1 * k, 1.4 * k, 1.4 * k, 1, glowm),
             *teeth(-3.5 * k, 3.5 * k, 3 * k, -16.5 * k, teethm),
             C(-6 * k, 0, -3 * k, 1.4 * k, 4 * k, 5 * k, horn), C(6 * k, 0, -3 * k, 1.4 * k, 4 * k, 5 * k, horn)] + list(extra)
    return boxes, [jaw] + horns


@spec
def ignis():
    """화염룡 이그니스: 큰 머리·뿔 2쌍·빛나는 목 비늘·용암 갈라진 배·불꽃 꼬리 끝·핏줄 빛나는 날개"""
    m = {"scale": M(0xB8321C, "scale", accent=0x7A160C), "scale2": M(0x8A2414, "scale", accent=0x5A100A), "belly": M(0xF0A040, "plate"),
         "horn": M(0xE8DCC0, "bone"), "wing": M(0x7A1E14, "veins", glow=0xFF9A30), "fire": M(0xFFB040, glow_all=True), "fire2": M(0xFF6A20, glow_all=True),
         "claw": M(0x2A1A16, "metal"), "magma": M(0x5A1A10, "lava", glow=0xFFA030), "teeth": M(0xF4EEDC, "plain")}
    m["face"] = FACE3(m["scale"], 0xFFE040, kind="beast", skin_shadow=0.45, eye_y=0.32, eye_gap=0.3, eye_w=0.16)
    hb, hc = dragon_head("head", "scale", m["face"], "horn", "teeth", "fire2")
    head = P("head", (0, 0, -6), (0, 0, 0), hb, hc)
    neck = chain("neck", 3, (0, -3, -12), 6, 9, 7, "scale", axis="z", direction=-1,
                 decor=lambda i, w, h, L: spikes(2, 0, 0, -h / 2, -1, -L + 1, 2, 2.5, "horn") + [C(0, h / 2, -L / 2, w * 0.7, 1, L, "belly"), C(0, h / 2 + 0.3, -L / 2, 2, 1, L - 1, "fire")])
    n = neck
    while n.children: n = n.children[0]
    n.children.append(head); head.pivot = (0, 0, -6)
    neck.rot = (R(-14), 0, 0)
    tail = chain("tail", 7, (0, -2, 12), 6, 9, 2.4, "scale", axis="z",
                 decor=lambda i, w, h, L: spikes(2, 0, 0, -h / 2, 1, L - 1, max(1.2, 3.4 - i * 0.35), max(1.2, 3.4 - i * 0.35), "horn") + [C(0, h / 2, L / 2, w * 0.7, 1, L, "belly")])
    t = tail
    while t.children: t = t.children[0]
    t.boxes += [C(0, 0, 5, 7, 1.4, 6, "claw"), C(0, 0, 6, 5, 1.6, 4, "fire2"), C(0, -2.5, 7, 3, 4, 3, "fire"), C(0, -5, 7.5, 1.6, 3, 1.6, "fire2")]
    tail.rot = (R(-8), 0, 0)
    rw = wing("right", (-6, -6, -6), "horn", "wing", span=40, depth=20, fingers=4, mat_claw="claw", rot=(0, R(10), R(-40)))
    lw = wing("left", (6, -6, -6), "horn", "wing", span=40, depth=20, fingers=4, mat_claw="claw", rot=(0, R(-10), R(40)))
    body = P("body", (0, 8, 0), (0, 0, 0), rounded(0, 0, 0, 17, 13, 27, "scale", 1.5) + [
        C(0, 6.2, 0, 12, 2, 23, "belly"), C(0, 6.8, 0, 6, 1, 18, "magma"), C(0, -6.5, -9, 15, 3, 9, "scale2"),
        C(-7.5, -2, -8, 4, 8, 9, "scale2"), C(7.5, -2, -8, 4, 8, 9, "scale2"), C(0, 2, 0, 17.6, 5, 20, "magma"),
        C(0, -7.5, 4, 10, 2, 12, "scale2"), *spikes(8, 0, 0, -6.5, -12, 12, 2.4, 5.5, "horn", w=2),
        *[C(s * 8.8, 1, z, 1, 2, 2, "fire") for s in (-1, 1) for z in (-6, 0, 6)]], [neck, tail, rw, lw])
    legs = [leg("right_leg", (-6.5, 14, 9), (6.5, 6), (5, 4), "scale", "scale2", "claw"),
            leg("left_leg", (6.5, 14, 9), (6.5, 6), (5, 4), "scale", "scale2", "claw"),
            leg("front_right_leg", (-6.5, 14, -8), (5.5, 6), (4.4, 4), "scale", "scale2", "claw", knee_name="front_right_leg2"),
            leg("front_left_leg", (6.5, 14, -8), (5.5, 6), (4.4, 4), "scale", "scale2", "claw", knee_name="front_left_leg2")]
    for l in legs: l.boxes.append(C(0, 1, 0, 7.4, 4, 7.4, "scale2"))
    return [body] + legs, m


@spec
def storm_wyvern():
    """폭풍 와이번: 뿔 투구 같은 머리, 번개 등지느러미, 끝이 번개 칼날인 꼬리, 큰 막 날개"""
    m = {"scale": M(0x1E2A4A, "scale", accent=0x3A5AA0), "scale2": M(0x2A3A6A, "scale", accent=0x4A6AB0), "belly": M(0x8AA0C8, "plate"),
         "bolt": M(0x9FE8FF, glow_all=True), "horn": M(0xD0D8E8, "bone"), "wing": M(0x2A3A6A, "veins", glow=0x9FE8FF), "claw": M(0x101018, "metal"),
         "teeth": M(0xF0F4FF, "plain")}
    m["face"] = FACE3(m["scale"], 0x9FF0FF, kind="beast", skin_shadow=0.45, eye_y=0.32, eye_gap=0.3, eye_w=0.16)
    hb, hc = dragon_head("head", "scale", m["face"], "horn", "teeth", "bolt", size=0.85, horn_prefix="hornW")
    head = P("head", (0, 0, -5), (0, 0, 0), hb + [C(0, -5.5, -3, 1.4, 2, 8, "bolt")], hc)
    neck = chain("neck", 3, (0, -2, -9), 5, 6.4, 5, "scale", axis="z", direction=-1,
                 decor=lambda i, w, h, L: [C(0, -h / 2 - 1.2, -L / 2, 1, 2.4, 2.4, "bolt"), C(0, h / 2, -L / 2, w * 0.7, 1, L, "belly")])
    n = neck
    while n.children: n = n.children[0]
    n.children.append(head)
    neck.rot = (R(-25), 0, 0)
    tail = chain("tail", 7, (0, -1, 9), 6, 6.4, 2, "scale", axis="z", decor=lambda i, w, h, L: [C(0, -h / 2 - 0.8, L / 2, 1, 1.6, 3.4, "bolt")])
    t = tail
    while t.children: t = t.children[0]
    t.boxes += [C(0, 0, 6, 7, 1.2, 6, "bolt"), C(0, -2.5, 6, 1.2, 5, 6, "bolt"), C(0, 0, 3, 3, 3, 3, "claw")]
    rw = wing("right", (-5, -4, -4), "horn", "wing", span=54, depth=24, fingers=4, mat_claw="claw", rot=(0, R(10), R(-25)))
    lw = wing("left", (5, -4, -4), "horn", "wing", span=54, depth=24, fingers=4, mat_claw="claw", rot=(0, R(-10), R(25)))
    body = P("body", (0, 8, 0), (0, 0, 0), rounded(0, 0, 0, 12, 11, 20, "scale", 1.5) + [C(0, 5.2, 0, 9, 2, 16, "belly"), C(0, -5.6, 0, 8, 1.4, 16, "scale2"),
             *spikes(6, 0, 0, -6, -8, 9, 2, 3.4, "bolt", w=1), C(-6.4, -1, -4, 1.4, 4, 6, "scale2"), C(6.4, -1, -4, 1.4, 4, 6, "scale2")],
             [neck, tail, rw, lw])
    legs = [leg("right_leg", (-5, 12, 5), (5, 6), (3.4, 6), "scale", "claw", "claw"), leg("left_leg", (5, 12, 5), (5, 6), (3.4, 6), "scale", "claw", "claw")]
    return [body] + legs, m


@spec
def zephyros():
    """바람의 대붕 제피로스: 볏 깃털 부채, 금 가면, 끝이 빛나는 긴 날개, 길게 갈라진 꼬리깃"""
    m = {"feather": M(0xE6F2F0, "feather", accent=0x4FB8A8), "dark": M(0x2F6F73, "feather", accent=0x9FF3E8), "gold": M(0xF0C040, "metal"),
         "tip": M(0xB8FFF0, glow_all=True), "talon": M(0x3A3A40, "metal"), "gem": M(0x60FFE0, "gem", glow=0x9FFFF0), "beak": M(0xF0D070, "bone")}
    m["face"] = FACE3(m["feather"], 0x60FFE0, kind="beast", skin_shadow=0.5, eye_y=0.38, eye_gap=0.28)
    crest = [P(f"zc{i}", (0, -6, 1), (R(-30 - i * 14), 0, 0), [B(-0.8, -9 + i, -0.5, 1.6, 9 - i, 1, "dark"), B(-0.6, -10 + i, -0.5, 1.2, 1.6, 1, "tip")], field=False) for i in range(5)]
    head = P("head", (0, -4, -9), (0, 0, 0), rounded(0, -3, -3, 9, 8, 8, "feather", 1) + [C(0, -3, -7.1, 9, 8, 1, "feather", {"front": m["face"]}),
             C(0, -1, -9.5, 3.4, 3, 5, "beak"), C(0, 0.6, -12.2, 2.4, 2.6, 1.6, "beak"), C(0, 1.6, -9, 2.6, 1, 4, "beak"),
             C(0, -6.4, -6.2, 7, 1.4, 1.4, "gold"), C(0, -7.6, -6.6, 2, 2, 1, "gem"), C(-4.6, -3, -4, 1, 3, 4, "gold"), C(4.6, -3, -4, 1, 3, 4, "gold")], crest)
    rw = wing("right", (-6, -5, -2), "dark", "feather", span=50, depth=20, fingers=5, rot=(0, 0, R(-10)))
    lw = wing("left", (6, -5, -2), "dark", "feather", span=50, depth=20, fingers=5, rot=(0, 0, R(10)))
    for w_ in (rw, lw):
        s = -1 if w_.name.startswith("right") else 1
        w_.children[0].boxes += [B((s * 25 if s > 0 else -25) - (0 if s > 0 else 0), 0.5, 2, 2, 1, 18, "tip"), B((s * 25 if s > 0 else -25) - s * 4, 0.6, 2, 2, 1, 19, "tip")]
    tail = chain("tail", 3, (0, 0, 9), 6, 10, 15, "dark", axis="z", h0=1, h1=1)
    t = tail
    while t.children: t = t.children[0]
    t.boxes += [B(-8, 0, 4, 4, 1, 10, "feather"), B(4, 0, 4, 4, 1, 10, "feather"), B(-9, 0.2, 12, 3, 1, 4, "tip"), B(6, 0.2, 12, 3, 1, 4, "tip")]
    body = P("body", (0, 6, 0), (0, 0, 0), rounded(0, 0, 0, 13, 12, 18, "feather", 1.5) + [C(0, 4, -4, 10, 6, 8, "feather"),
             C(0, -2, -9.6, 9, 6, 1, "gold"), C(0, -2, -10.2, 2.4, 2.4, 1, "gem"), C(0, -6.5, 2, 10, 1.4, 12, "dark"),
             C(0, -3, -8.6, 11, 1.4, 4, "dark")], [head, rw, lw, tail])
    legs = [leg("right_leg", (-3, 12, 2), (4, 6), (2.4, 6), "dark", "talon", "talon"), leg("left_leg", (3, 12, 2), (4, 6), (2.4, 6), "dark", "talon", "talon")]
    for l in legs: l.boxes.append(C(0, 1, 0, 5.4, 3, 5.4, "feather"))
    return [body] + legs, m


@spec
def hydra():
    """꽃의 히드라: 세 머리(가운데가 큼)·꽃잎 볏·독 빛나는 목구멍, 덩굴 감긴 몸, 꽃 가시 등"""
    m = {"scale": M(0x1E5A2A, "scale", accent=0x0E3A18), "scale2": M(0x2E7A3A, "scale", accent=0x1A5A28), "belly": M(0xC8D890, "plate"),
         "petal": M(0xFF70B0, "leaf", accent=0xFFB0D8, glow=0xFFD0F0), "poison": M(0xB0FF60, glow_all=True), "horn": M(0xE8E0C8, "bone"),
         "vine": M(0x2E7D32, "bark"), "teeth": M(0xF4F0DC, "plain"), "bloom": M(0xFFD0E8, glow_all=True)}
    heads = []
    for i, (x, sz, tilt) in enumerate(((-10, 0.7, -8), (0, 0.85, -14), (10, 0.7, -8))):
        face = FACE3(m["scale"], 0xD0FF60, kind="beast", skin_shadow=0.45, eye_y=0.32, eye_gap=0.3, eye_w=0.16)
        m[f"face{i}"] = face
        hb, hc = dragon_head(f"head{i + 1}", "scale", face, "horn", "teeth", "poison", size=sz, jaw_name=f"jawh{i + 1}", horn_prefix=f"hh{i}")
        petals = [P(f"hp{i}{k}", (0, -4 * sz, 2 * sz), (R(-40), 0, R(-60 + k * 30)), [B(-1.2, -6, -0.5, 2.4, 6, 1, "petal"), B(-0.6, -7, -0.5, 1.2, 1.4, 1, "bloom")], field=False) for k in range(5)]
        h = P(f"head{i + 1}", (0, -6, -2), (R(10), 0, 0), hb, hc + petals)
        nk = chain(f"neckh{i + 1}x", 4, (x, -6, -6), 6, 7 * sz / 0.7 * 0.8, 5.4, "scale", axis="y", direction=-1,
                   decor=lambda k, w, hh, L: [C(0, -L / 2, w / 2 + 0.6, 3.4, L - 0.6, 1.2, "belly"), C(0, -L / 2, -w / 2 - 0.6, 1.4, 2, 1.4, "petal")])
        e = nk
        while e.children: e = e.children[0]
        e.children.append(h)
        nk.rot = (R(tilt), 0, R(x * 2.4))
        heads.append(nk)
    tail = chain("tail", 6, (0, 0, 12), 6, 10, 2.4, "scale", axis="z",
                 decor=lambda i, w, h, L: ([C(0, -h / 2 - 1, L / 2, 2.4, 2.4, 2.4, "petal")] if i % 2 == 0 else []) + [C(0, h / 2, L / 2, w * 0.7, 1, L, "belly")])
    t = tail
    while t.children: t = t.children[0]
    t.boxes += [C(0, 0, 4, 5, 5, 5, "petal"), C(0, 0, 4, 2.4, 6, 2.4, "bloom")]
    vines = [C(-9.8, -2, 0, 1.6, 1.6, 24, "vine"), C(9.8, -2, 0, 1.6, 1.6, 24, "vine"), C(0, -7.8, 4, 19, 1.6, 1.6, "vine"), C(0, -7.8, -6, 19, 1.6, 1.6, "vine")]
    flowers = []
    for j, (x, z) in enumerate(((-5, -6), (5, -2), (-4, 3), (5, 7), (0, 10))):
        flowers += [C(x, -8.5, z, 4, 1.4, 4, "petal"), C(x, -9.4, z, 1.6, 1.6, 1.6, "bloom")]
    body = P("body", (0, 10, 0), (0, 0, 0), rounded(0, 0, 0, 19, 14, 25, "scale", 1.5) + [C(0, 7, 0, 14, 2, 21, "belly"), C(0, -6.8, 0, 12, 1.4, 20, "scale2"),
             *vines, *flowers, *spikes(5, 0, 0, -7.4, -8, 10, 3, 4, "horn", w=2)] + [C(x, -7, z, 2, 2, 2, "poison") for x, z in ((-7, -4), (7, 2), (-6, 8))], heads + [tail])
    def hl(name, pv, knee=None):
        l = leg(name, pv, (6.4, 5), (5.4, 4), "scale", "scale2", "horn", knee_name=knee)
        l.boxes.append(C(0, 1, 0, 7.6, 3.4, 7.6, "scale2"))
        return l
    legs = [hl("right_leg", (-8, 15, 8)), hl("left_leg", (8, 15, 8)), hl("front_right_leg", (-8, 15, -8), "front_right_leg2"), hl("front_left_leg", (8, 15, -8), "front_left_leg2")]
    return [body] + legs, m
