"""
원소 무기 28종 3D 모델 생성기
- 손에 들면 3D(JSON 블록 모델 요소), 인벤토리(gui)에서는 기존 2D 애니메이션 아이콘
사용법: python3 tools/weapons/weapons3d.py <프로젝트 루트> [미리보기.png]
"""
import json, os, sys, math, colorsys
from PIL import Image, ImageDraw

ROOT = sys.argv[1]; NS = "elementalweapons"
A = f"{ROOT}/src/main/resources/assets/{NS}"

def hexc(h): return ((h >> 16) & 255, (h >> 8) & 255, h & 255)
def cl(c): return tuple(max(0, min(255, int(v))) for v in c)
def mix(a, b, t): return cl(tuple(a[i] + (b[i] - a[i]) * t for i in range(3)))
def tone(c, dl, dh=0.0):
    r, g, b = [v / 255 for v in c]; h, l, s = colorsys.rgb_to_hls(r, g, b)
    return cl(tuple(v * 255 for v in colorsys.hls_to_rgb((h + dh) % 1, max(0, min(1, l + dl)), s)))

# 속성 팔레트: blade(칼날), edge(날 끝 하이라이트), metal(가드), metal2, grip, gem(발광), accent
EL = {
 "electric": dict(blade=0x6FC8FF, edge=0xE8FBFF, metal=0x2A3A6A, metal2=0xF0C040, grip=0x1A2040, gem=0x9FF0FF, accent=0xFFF070, style="bolt"),
 "fire":     dict(blade=0xE0502A, edge=0xFFD27A, metal=0x3A1A14, metal2=0xF0A030, grip=0x4A1A10, gem=0xFFB040, accent=0xFFE070, style="flame"),
 "water":    dict(blade=0x3A8CE0, edge=0xC8F0FF, metal=0xD8E4F0, metal2=0x40C0D0, grip=0x1A4A6A, gem=0x6FE8FF, accent=0xF4F0FF, style="wave"),
 "grass":    dict(blade=0x5AB84A, edge=0xD8FFB0, metal=0x5A3A1E, metal2=0xB0D060, grip=0x3A2A16, gem=0xFF8FC0, accent=0xFFD0E8, style="leaf"),
 "wind":     dict(blade=0xB8F0E8, edge=0xFFFFFF, metal=0x3A8A84, metal2=0xE8F4F2, grip=0x2A5A58, gem=0x9FFFF0, accent=0xFFFFFF, style="feather"),
 "dark":     dict(blade=0x4A2A6A, edge=0xC090FF, metal=0x1A1020, metal2=0x6A3A8A, grip=0x120A18, gem=0xB060FF, accent=0xE0C0FF, style="thorn"),
 "light":    dict(blade=0xFFF4D0, edge=0xFFFFFF, metal=0xF0C040, metal2=0xFFFFFF, grip=0xE8E0C8, gem=0xFFF090, accent=0xFFFFE0, style="wing"),
}
TILE = ["blade", "blade_d", "edge", "metal", "metal_d", "metal2", "grip", "gem", "accent", "fuller"]

def make_texture(el):
    p = {k: hexc(v) for k, v in EL[el].items() if k != "style"}
    cols = {"blade": p["blade"], "blade_d": tone(p["blade"], -0.15), "edge": p["edge"], "metal": p["metal"], "metal_d": tone(p["metal"], -0.12),
            "metal2": p["metal2"], "grip": p["grip"], "gem": p["gem"], "accent": p["accent"], "fuller": tone(p["blade"], -0.25)}
    im = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    for i, k in enumerate(TILE):
        tx, ty = (i % 4) * 16, (i // 4) * 16
        base = cols[k]
        for y in range(16):
            for x in range(16):
                c = base
                if k in ("blade", "blade_d", "edge"):
                    c = mix(tone(base, 0.08), tone(base, -0.1), y / 15)
                    if (x + y) % 9 == 0: c = tone(c, 0.12)
                elif k in ("metal", "metal_d", "metal2"):
                    c = tone(base, 0.12 if (x - y) % 7 == 0 else (-0.08 if y % 5 == 4 else 0))
                elif k == "grip":
                    c = tone(base, -0.12 if (y + x // 2) % 4 == 0 else 0.03)
                elif k == "gem":
                    d = abs(x - 7.5) + abs(y - 7.5)
                    c = mix((255, 255, 255), base, min(1, d / 6))
                elif k == "accent":
                    c = tone(base, 0.05 if (x + y) % 3 else -0.05)
                im.putpixel((tx + x, ty + y), c + (255,))
    im.save(f"{A}/textures/item/3d/{el}_weapon.png")

PPU = 4  # 1 모델 단위당 텍스처 픽셀 (HD)
FACE_DIRS = ("north", "south", "east", "west", "up", "down")

def face_dims(e, d):
    (x0, y0, z0), (x1, y1, z1) = e["from"], e["to"]
    w, h, dd = x1 - x0, y1 - y0, z1 - z0
    return {"north": (w, h), "south": (w, h), "east": (dd, h), "west": (dd, h), "up": (w, dd), "down": (w, dd)}[d]

def paint_face(im, px, py, pw, ph, k, d, pal, el_style, length_hint):
    """면 하나를 그리기: k=재질 키, d=방향"""
    base = pal[k]
    light = {"up": 0.12, "north": 0.04, "south": 0.0, "east": -0.06, "west": -0.06, "down": -0.18}[d]
    for yy in range(ph):
        for xx in range(pw):
            fy = yy / max(1, ph - 1); fx = xx / max(1, pw - 1)
            c = base; lv = light
            if k in ("blade", "blade_d"):
                lv += 0.10 - 0.22 * fy                                  # 끝(위)이 밝고 손잡이 쪽이 어둡게
                if abs((xx - yy * 0.5) % (pw + 6) - 2) < 1.0: lv += 0.18   # 사선 광택
                if d in ("north", "south") and pw >= 6 and xx in (0, pw - 1): lv += 0.22
                if d in ("north", "south") and pw >= 8 and yy % 10 in (3, 4, 5) and abs(xx - pw / 2) < 1.2 and 0.12 < fy < 0.8:
                    c = pal["accent"]; lv = 0.05                          # 룬 각인
            elif k == "edge":
                lv += 0.18 - 0.12 * fy + (0.1 if xx == 0 else 0)
            elif k == "fuller":
                lv += -0.05 + (0.12 if xx == 0 else 0) - (0.1 if xx == pw - 1 else 0)
            elif k in ("metal", "metal_d", "metal2"):
                lv += 0.06 * math.sin(yy * 2.2) + (0.16 if yy == 0 or xx == 0 else 0) - (0.16 if yy == ph - 1 or xx == pw - 1 else 0)
                if pw >= 6 and ph >= 6 and (xx, yy) in ((1, 1), (pw - 2, 1), (1, ph - 2), (pw - 2, ph - 2)): lv += 0.3
            elif k == "grip":
                band = (xx + yy) % 6
                lv += -0.16 if band == 0 else (0.08 if band in (2, 3) else 0)
                if d in ("up", "down"): lv = light - 0.05
            elif k == "gem":
                cx, cy = (pw - 1) / 2, (ph - 1) / 2
                dd_ = abs(xx - cx) / max(1, cx + 0.5) + abs(yy - cy) / max(1, cy + 0.5)
                c = mix((255, 255, 255), base, min(1, dd_ * 0.9)); lv = 0.08 - 0.12 * dd_
                if xx < cx and yy < cy and dd_ < 0.6: lv += 0.15
            elif k == "accent":
                lv += 0.1 - 0.15 * abs(fy - 0.5)
            im.putpixel((px + xx, py + yy), tone(c, lv) + (255,))

def uv_of(k):
    i = TILE.index(k); return [(i % 4) * 4, (i // 4) * 4, (i % 4) * 4 + 4, (i // 4) * 4 + 4]

GLOW = {"gem", "accent", "edge"}
def E(x0, y0, z0, x1, y1, z1, k, rot=None, glow=None):
    f = {d: {"uv": uv_of(k), "texture": "#t"} for d in ("north", "south", "east", "west", "up", "down")}
    e = {"from": [round(min(x0, x1), 3), round(min(y0, y1), 3), round(min(z0, z1), 3)], "to": [round(max(x0, x1), 3), round(max(y0, y1), 3), round(max(z0, z1), 3)], "faces": f}
    if rot: e["rotation"] = {"angle": rot[0], "axis": rot[1], "origin": rot[2]}
    if glow or (glow is None and k in ("gem",)): e["light_emission"] = 12
    e["_k"] = k
    return e

def box(cx, cy, cz, w, h, d, k, rot=None, glow=None):
    return E(cx - w / 2, cy - h / 2, cz - d / 2, cx + w / 2, cy + h / 2, cz + d / 2, k, rot, glow)

def blade(y0, length, width, thick=1.0, tip=4, fuller=True, curve=0):
    """칼날: 몸통 + 양날 + 홈 + 점점 좁아지는 끝"""
    out = []
    body = length - tip
    out.append(box(8, y0 + body / 2, 8, width, body, thick, "blade"))
    out.append(box(8 - width / 2 - 0.25, y0 + body / 2, 8, 0.5, body, thick * 0.6, "edge", glow=False))
    out.append(box(8 + width / 2 + 0.25, y0 + body / 2, 8, 0.5, body, thick * 0.6, "edge", glow=False))
    if fuller and width >= 2: out.append(box(8, y0 + body / 2 - 1, 8, 0.6, body - 3, thick + 0.1, "fuller"))
    w = width
    for i in range(tip):
        w = max(0.5, w * 0.68)
        out.append(box(8, y0 + body + i + 0.5, 8, w, 1, thick * (0.9 - i * 0.12), "blade" if i < tip - 1 else "edge"))
    return out

def guard(style, y, w, big=1.0):
    g = [box(8, y, 8, w, 1.5 * big, 2.2 * big, "metal"), box(8, y, 8, 2.2 * big, 2.2 * big, 2.6 * big, "metal2"), box(8, y, 8, 1.2 * big, 1.2 * big, 2.8 * big, "gem")]
    h = w / 2
    if style == "flame":
        for s in (-1, 1):
            g += [box(8 + s * (h - 0.5), y + 1.5 * big, 8, 1, 2.5 * big, 1.2, "metal2"), box(8 + s * (h - 1.2), y + 3 * big, 8, 1, 1.5 * big, 1, "accent", glow=True),
                  box(8 + s * (h + 0.3), y + 0.5, 8, 1, 1.5, 1.2, "metal2")]
    elif style == "bolt":
        for s in (-1, 1):
            g += [box(8 + s * (h + 0.5), y + 1, 8, 1, 1.5, 1, "metal2"), box(8 + s * (h + 1.5), y + 2.2, 8, 1, 1.5, 1, "accent", glow=True), box(8 + s * (h + 0.8), y - 1, 8, 1, 1, 1, "metal2")]
    elif style == "wave":
        for s in (-1, 1):
            g += [box(8 + s * h, y + 1, 8, 1.2, 2, 1.4, "metal2"), box(8 + s * (h - 1), y + 2.2, 8, 1.2, 1, 1.2, "metal2"), box(8 + s * (h + 0.6), y - 0.8, 8, 1, 1, 1, "accent", glow=True)]
    elif style == "leaf":
        for s in (-1, 1):
            g += [box(8 + s * (h + 0.5), y + 0.8, 8, 2, 1, 1.4, "metal2", rot=(22.5 * s, "z", [8 + s * h, y, 8])), box(8 + s * (h + 2), y + 1.5, 8, 1, 1, 1, "accent", glow=True)]
    elif style == "feather":
        for s in (-1, 1):
            for k in range(3):
                g.append(box(8 + s * (h - 1 + k), y + 0.6 + k * 0.6, 8, 1, 1.2 + k * 0.4, 1, "metal2"))
    elif style == "thorn":
        for s in (-1, 1):
            g += [box(8 + s * (h + 0.5), y - 1, 8, 1, 2.5, 1, "metal2"), box(8 + s * (h + 0.5), y - 2.6, 8, 0.6, 1, 0.6, "accent", glow=True), box(8 + s * (h - 1), y + 1.2, 8, 1, 1.5, 1, "metal")]
    elif style == "wing":
        for s in (-1, 1):
            for k in range(3):
                g.append(box(8 + s * (h - 0.5 + k * 0.8), y + 0.8 + k * 0.9, 8, 1.2, 1.4, 1, "metal2" if k < 2 else "accent", glow=(k == 2)))
    return g

def grip(y0, length, pommel_size=1.6):
    g = [box(8, y0 + length / 2, 8, 1.4, length, 1.4, "grip")]
    for i in range(int(length // 2)):
        g.append(box(8, y0 + 1 + i * 2, 8, 1.6, 0.5, 1.6, "metal_d"))
    g += [box(8, y0 - 0.6, 8, pommel_size + 0.4, 1.2, pommel_size + 0.4, "metal"), box(8, y0 - 1.6, 8, pommel_size, pommel_size, pommel_size, "gem")]
    return g

def staff_head(style, y):
    h = []
    core = {"bolt": 3.0, "flame": 3.2, "wave": 3.0, "leaf": 2.6, "feather": 2.6, "thorn": 2.8, "wing": 3.0}[style]
    h += [box(8, y + 4, 8, core, core * 1.6, core, "gem"), box(8, y + 4, 8, core * 0.55, core * 2.1, core * 0.55, "accent", glow=True)]
    h += [box(8, y, 8, 3, 1.2, 3, "metal2"), box(8, y + 0.9, 8, 2.2, 0.8, 2.2, "metal")]
    if style == "bolt":
        for s in (-1, 1):
            h += [box(8 + s * 2.5, y + 2, 8, 1, 3, 1, "metal2"), box(8 + s * 3.2, y + 4.5, 8, 1, 2, 1, "metal2"), box(8 + s * 2.4, y + 6.6, 8, 1, 2, 1, "accent", glow=True)]
    elif style == "flame":
        for s in (-1, 1):
            h += [box(8 + s * 2.6, y + 2.5, 8, 1, 4, 1, "metal2", rot=(-22.5 * s, "z", [8 + s * 2.6, y + 1, 8])), box(8 + s * 1.8, y + 7.5, 8, 1, 2, 1, "accent", glow=True)]
        h.append(box(8, y + 8.5, 8, 1, 2, 1, "accent", glow=True))
    elif style == "wave":
        for k in range(4):
            a = k * math.pi / 2
            h.append(box(8 + math.cos(a) * 3, y + 4, 8 + math.sin(a) * 3, 1, 1, 1, "accent", glow=True))
        for s in (-1, 1): h.append(box(8 + s * 2.6, y + 3.5, 8, 1, 5, 1, "metal2"))
        h.append(box(8, y + 6.6, 8, 6, 1, 1, "metal2"))
    elif style == "leaf":
        for k in range(5):
            a = k * 2 * math.pi / 5
            h.append(box(8 + math.cos(a) * 2.4, y + 3 + (k % 2), 8 + math.sin(a) * 2.4, 1.2, 2.4, 1.2, "metal2"))
        h += [box(8 + 2, y + 6.5, 8, 1.2, 1.2, 1.2, "accent", glow=True), box(8 - 2.2, y + 6, 8.8, 1.2, 1.2, 1.2, "accent", glow=True)]
    elif style == "feather":
        for s in (-1, 1):
            for k in range(3):
                h.append(box(8 + s * (2 + k * 0.7), y + 2 + k * 1.6, 8, 1, 2.4, 0.8, "metal2"))
    elif style == "thorn":
        for s in (-1, 1):
            h += [box(8 + s * 2.2, y + 4.5, 8, 1, 6, 1, "metal"), box(8 + s * 1.6, y + 8, 8, 0.8, 1.5, 0.8, "accent", glow=True)]
        h += [box(8, y + 4, 8 + 2.2, 1, 5, 1, "metal"), box(8, y + 4, 8 - 2.2, 1, 5, 1, "metal")]
    elif style == "wing":
        for s in (-1, 1):
            for k in range(4):
                h.append(box(8 + s * (2.2 + k * 0.9), y + 3 + k * 0.9, 8, 1.2, 1.6, 0.8, "metal2" if k < 3 else "accent", glow=(k == 3)))
        h.append(box(8, y + 9, 8, 4, 0.6, 0.6, "accent", glow=True))
    return h

def blade_deco(style, y0, length, width, thick):
    out = []
    n = int(length // 3)
    for i in range(1, n):
        y = y0 + i * 3
        for sd in (-1, 1):
            x = 8 + sd * (width / 2 + 0.6)
            if style == "flame" and i % 2 == 0: out.append(box(x, y, 8, 0.8, 1.6, thick * 0.5, "accent", glow=True))
            elif style == "bolt" and i % 2 == sd % 2: out.append(box(x, y, 8, 0.8, 1, thick * 0.5, "accent", glow=True))
            elif style == "thorn": out.append(box(x + sd * 0.3, y - 0.5, 8, 0.8, 0.8, 0.6, "metal2"))
            elif style == "wave" and i % 2 == 0: out.append(box(x - sd * 0.2, y, 8, 0.6, 1.4, thick * 0.6, "edge"))
            elif style == "feather" and i % 2 == 1: out.append(box(x + sd * 0.4, y + 0.6, 8, 1, 0.6, 0.6, "edge"))
        if style == "leaf" and i % 2 == 0:
            out += [box(8 - 0.8, y + 0.4, 8, 1.2, 0.4, thick + 0.2, "fuller"), box(8 + 0.8, y + 0.4, 8, 1.2, 0.4, thick + 0.2, "fuller")]
        if style == "wing" and i % 3 == 0: out.append(box(8, y, 8, width * 0.5, 0.5, thick + 0.2, "metal2"))
    return out

def scaled(es, k, oy):
    for e in es:
        for key in ("from", "to"):
            v = e[key]; e[key] = [8 + (v[0] - 8) * k, oy + (v[1] - oy) * k, 8 + (v[2] - 8) * k]
        if "rotation" in e:
            o = e["rotation"]["origin"]; e["rotation"]["origin"] = [8 + (o[0] - 8) * k, oy + (o[1] - oy) * k, 8 + (o[2] - 8) * k]
    return es

def build(el, wt):
    st = EL[el]["style"]
    if wt == "dagger":
        es = grip(1, 4, 1.4) + guard(st, 5.5, 5, 0.8) + blade(6.2, 9, 2.2, 0.8, tip=3, fuller=False) + blade_deco(st, 6.2, 7, 2.2, 0.8)
    elif wt == "sword":
        es = grip(0, 6) + guard(st, 6.6, 8) + blade(7.4, 18, 2.8, 1.0, tip=4) + blade_deco(st, 7.4, 15, 2.8, 1.0)
    elif wt == "greatsword":
        es = grip(-4, 9, 2.0) + guard(st, 5.8, 12, 1.4) + blade(6.8, 24, 4.6, 1.4, tip=5) + blade_deco(st, 6.8, 20, 4.6, 1.4) + [box(8, 10, 8, 2.2, 3, 1.6, "metal2"), box(8, 10, 8, 1, 1, 1.8, "gem")]
    else:  # staff
        es = [box(8, 2, 8, 1.4, 30, 1.4, "grip")] + [box(8, -12 + i * 7, 8, 1.8, 0.8, 1.8, "metal2") for i in range(4)] + \
             [box(8, -13.5, 8, 2, 1.5, 2, "metal")] + scaled(staff_head(st, 17.2), 1.45, 17.2)
    # 범위 제한 (-16~32)
    for e in es:
        e["from"] = [max(-16, min(32, v)) for v in e["from"]]; e["to"] = [max(-16, min(32, v)) for v in e["to"]]
    return es

DISPLAY = {
    "dagger":     {"thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [0, 1, 1], "scale": [0.85, 0.85, 0.85]},
                   "firstperson_righthand": {"rotation": [0, 90, 20], "translation": [1, 3, 0], "scale": [0.5, 0.5, 0.5]}},
    "sword":      {"thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [0, 3, 1], "scale": [0.85, 0.85, 0.85]},
                   "firstperson_righthand": {"rotation": [0, 90, 20], "translation": [1, 2, 0], "scale": [0.42, 0.42, 0.42]}},
    "greatsword": {"thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [0, 5, 1], "scale": [0.95, 0.95, 0.95]},
                   "firstperson_righthand": {"rotation": [0, 90, 20], "translation": [1, 0, 0], "scale": [0.34, 0.34, 0.34]}},
    "staff":      {"thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [0, 1, 1], "scale": [0.85, 0.85, 0.85]},
                   "firstperson_righthand": {"rotation": [0, 90, 15], "translation": [2, 3, 0], "scale": [0.28, 0.28, 0.28]}},
}
for v in DISPLAY.values():
    v["thirdperson_lefthand"] = v["thirdperson_righthand"]; v["firstperson_lefthand"] = v["firstperson_righthand"]
    v["fixed"] = {"rotation": [0, 0, 0], "translation": [0, 0, 0], "scale": [0.5, 0.5, 0.5]}
    v["ground"] = {"rotation": [0, 0, 0], "translation": [0, 2, 0], "scale": [0.4, 0.4, 0.4]}
    v["head"] = {"rotation": [0, 0, 0], "translation": [0, 0, 0], "scale": [1, 1, 1]}

def preview(es, size=220):
    """평면 음영 미리보기 (정면 살짝 기울임)"""
    img = Image.new("RGBA", (size, size), (34, 34, 46, 255)); d = ImageDraw.Draw(img)
    tex = {}
    quads = []
    yaw = math.radians(30); cy_, sy_ = math.cos(yaw), math.sin(yaw)
    def proj(x, y, z):
        x -= 8; z -= 8
        rx = x * cy_ - z * sy_; rz = x * sy_ + z * cy_
        return rx, y, rz
    for e in es:
        (x0, y0, z0), (x1, y1, z1) = e["from"], e["to"]
        k = e["_k"]
        V = [(x0, y0, z0), (x1, y0, z0), (x1, y1, z0), (x0, y1, z0), (x0, y0, z1), (x1, y0, z1), (x1, y1, z1), (x0, y1, z1)]
        for idx, lt in (((0, 1, 2, 3), 1.0), ((1, 5, 6, 2), 0.8), ((3, 2, 6, 7), 1.15), ((4, 0, 3, 7), 0.8)):
            pts = [proj(*V[i]) for i in idx]
            quads.append((sum(p[2] for p in pts) / 4, pts, k, lt, "light_emission" in e))
    quads.sort(key=lambda q: -q[0])
    sc = size / 44
    for _, pts, k, lt, gl in quads:
        c = CUR[k]; c = tuple(min(255, int(v * (1.2 if gl else lt))) for v in c)
        d.polygon([(size / 2 + p[0] * sc, size * 0.82 - p[1] * sc) for p in pts], fill=c, outline=tuple(int(v * 0.6) for v in c))
    return img

def hd_texture(el, wt, es, pal):
    """요소마다 면을 따로 배치(선반 패킹)하고 직접 그린 텍스처를 만든다"""
    rects = []
    for ei, e in enumerate(es):
        k = e.pop("_k")
        for d in FACE_DIRS:
            fw, fh = face_dims(e, d)
            pw, ph = max(1, round(fw * PPU)), max(1, round(fh * PPU))
            rects.append((ph, pw, ei, d, k))
    for size in (128, 256, 512):
        x = y = rowh = 0; placed = []; ok = True
        for ph, pw, ei, d, k in sorted(rects, key=lambda r: -r[0]):
            if x + pw > size: x, y, rowh = 0, y + rowh, 0
            if y + ph > size or pw > size: ok = False; break
            placed.append((x, y, pw, ph, ei, d, k)); x += pw; rowh = max(rowh, ph)
        if ok: break
    im = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    for x, y, pw, ph, ei, d, k in placed:
        paint_face(im, x, y, pw, ph, k, d, pal, EL[el]["style"], 0)
        u = 16 / size
        es[ei]["faces"][d] = {"uv": [round(x * u, 4), round(y * u, 4), round((x + pw) * u, 4), round((y + ph) * u, 4)], "texture": "#t"}
    im.save(f"{A}/textures/item/3d/{el}_{wt}.png")

if __name__ == "__main__":
    os.makedirs(f"{A}/textures/item/3d", exist_ok=True); os.makedirs(f"{A}/models/item/3d", exist_ok=True)
    tiles = []
    for el in EL:
        p = EL[el]
        CUR = {"blade": hexc(p["blade"]), "blade_d": tone(hexc(p["blade"]), -0.15), "edge": hexc(p["edge"]), "metal": hexc(p["metal"]), "metal_d": tone(hexc(p["metal"]), -0.12),
               "metal2": hexc(p["metal2"]), "grip": hexc(p["grip"]), "gem": hexc(p["gem"]), "accent": hexc(p["accent"]), "fuller": tone(hexc(p["blade"]), -0.25)}
        for wt in ("dagger", "sword", "greatsword", "staff"):
            es = build(el, wt)
            if len(sys.argv) > 2: tiles.append(preview(es))
            hd_texture(el, wt, es, CUR)
            model = {"textures": {"t": f"{NS}:item/3d/{el}_{wt}", "particle": f"{NS}:item/{el}_{wt}"}, "gui_light": "front", "display": DISPLAY[wt], "elements": es}
            json.dump(model, open(f"{A}/models/item/3d/{el}_{wt}.json", "w"), indent="\t")
            json.dump({"model": {"type": "minecraft:select", "property": "minecraft:display_context",
                       "cases": [{"when": ["gui"], "model": {"type": "minecraft:model", "model": f"{NS}:item/{el}_{wt}"}}],
                       "fallback": {"type": "minecraft:model", "model": f"{NS}:item/3d/{el}_{wt}"}}}, open(f"{A}/items/{el}_{wt}.json", "w"), indent="\t")
    if len(sys.argv) > 2:
        sheet = Image.new("RGBA", (220 * 4, 220 * 7))
        for i, t in enumerate(tiles): sheet.paste(t, ((i % 4) * 220, (i // 4) * 220))
        sheet.save(sys.argv[2])
    print("weapons3d ok", len(EL) * 4)
