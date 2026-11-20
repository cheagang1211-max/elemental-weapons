"""보스 텍스처 재질 v2 — 도트 그림 규칙(3톤 팔레트, 테두리 음영, 면 그라데이션, 하이라이트)"""
import math, colorsys
from bossgen import FACE_LIGHT

def hexc(h): return ((h >> 16) & 255, (h >> 8) & 255, h & 255) if isinstance(h, int) else h
def cl(c): return tuple(max(0, min(255, int(round(v)))) for v in c[:3])
def mix(a, b, t): return cl(tuple(a[i] + (b[i] - a[i]) * t for i in range(3)))

def hue_shift(c, dl, dh, ds=0.0):
    r, g, b = [v / 255 for v in c]
    h, l, s = colorsys.rgb_to_hls(r, g, b)
    h = (h + dh) % 1.0; l = max(0, min(1, l + dl)); s = max(0, min(1, s + ds))
    return cl(tuple(v * 255 for v in colorsys.hls_to_rgb(h, l, s)))

def ramp(base):
    """그림자(어둡고 푸르게) ~ 하이라이트(밝고 따뜻하게) 5단계"""
    b = hexc(base)
    return [hue_shift(b, -0.28, 0.03, 0.05), hue_shift(b, -0.14, 0.015, 0.03), b, hue_shift(b, 0.11, -0.015, -0.02), hue_shift(b, 0.24, -0.03, -0.08)]

def h2(x, y, s=0):
    n = (x * 374761393 + y * 668265263 + s * 2147483647) & 0xFFFFFFFF
    n = (n ^ (n >> 13)) * 1274126177 & 0xFFFFFFFF
    return (n ^ (n >> 16)) / 0xFFFFFFFF

PS = 2  # 무늬 확대 배율 (HD)
FACE_IDX = {"top": 1.0, "front": 0.0, "right": -0.6, "left": -0.6, "back": -0.9, "bottom": -1.4}

def M(base, pat="plain", accent=None, glow=None, trim=None, glow_all=False, rim=True, seed=0, dither=True):
    R = ramp(base)
    A = ramp(accent) if accent is not None else None
    G = hexc(glow) if glow is not None else None
    T = ramp(trim) if trim is not None else None

    def paint(x, y, w, h, face, rnd):
        if glow_all:
            band = (x + y * 2) % 5
            c = mix(R[3], R[4], 0.6) if band == 0 else (R[2] if band in (2, 3) else R[3])
            return c, mix(c, (255, 255, 255), 0.1)
        lv = 2.0 + FACE_IDX[face]
        if face not in ("top", "bottom") and h >= 4:
            lv += 0.7 * (0.5 - y / max(1, h - 1))  # 위가 밝고 아래가 어둡게
        g = None
        n = h2(x, y, seed + hash(face) % 97)
        # 패턴 (큰 무늬는 PS배 확대 좌표, 작은 입체감은 실제 픽셀)
        X, Y, W, H = x // PS, y // PS, max(1, w // PS), max(1, h // PS)
        sx, sy = x % PS, y % PS
        if pat in ("scale", "plate", "stone", "bark", "crystal", "metal", "feather", "fur", "bone"):
            lv += 0.3 if sy == 0 else (-0.3 if sy == PS - 1 else 0)
        if pat == "scale":
            cw, ch = 6, 4
            row = y // ch; ox = (row % 2) * (cw // 2)
            lx, ly = (x + ox) % cw, y % ch
            dxc = abs(lx - (cw - 1) / 2) / (cw / 2)
            if ly >= ch - 1 - int(dxc * 1.5): lv -= 1.1          # 비늘 아래 가장자리 그림자
            elif ly == 0 and dxc < 0.6: lv += 0.9                 # 비늘 윗면 반사광
            elif ly == 1 and dxc < 0.3: lv += 0.4
            if A and (row % 6 == 0) and face != "top": return pick(A, lv), None
        elif pat == "plate":
            gx, gy = x % 12, y % 12
            if gx == 0 or gy == 0: lv -= 1.2
            elif gx == 1 or gy == 1: lv += 0.7
            elif gx == 11 or gy == 11: lv -= 0.5
            if gx in (3, 9) and gy in (3, 9): lv += 1.5 if (gx + gy) % 2 == 0 else 1.2
            if (x - y) % 17 == 0: lv += 0.4
        elif pat == "bark":
            k = (X + int(2 * math.sin(Y * 0.6 + X))) % 4
            lv += -1.2 if k == 0 else (0.4 if k == 2 else 0)
            if n < 0.04 and A: return pick(A, lv), None
        elif pat == "leaf":
            lv += (n - 0.5) * 2.2
            if A and n > 0.82: return pick(A, lv + 0.5), None
            if G and n < 0.03: return G, G
        elif pat == "crystal":
            d = (X + Y) % 7
            lv += 1.6 if d == 0 else (0.7 if d == 1 else (-0.8 if d in (4, 5) else 0))
            if G and (X * 5 + Y * 3) % 13 == 0: g = G
        elif pat == "cloth":
            lv += 0.5 * math.sin(X * 1.1) - (0.6 if Y % 5 == 4 else 0)
            if T and (Y < 2 or Y >= H - 2): return pick(T, lv + 0.3), None
        elif pat == "fur":
            s = (X * 3 + Y * 2 + int(n * 3)) % 6
            lv += 0.8 if s == 0 else (-0.7 if s == 3 else 0)
        elif pat == "feather":
            v = (Y + abs(X - W // 2)) % 4
            lv += -0.9 if v == 0 else (0.5 if v == 2 else 0)
            if A and Y >= H * 0.65: return pick(A, lv), None
        elif pat == "stone":
            lv += (n - 0.5) * 1.4
            if (X * 7 + Y * 13) % 29 == 0: lv -= 1.6
        elif pat == "bone":
            lv += 0.4 if (X + Y) % 9 == 0 else 0
            if Y % 4 == 3 and W >= 3: lv -= 0.6
        elif pat == "metal":
            lv += 1.3 if (X - Y) % 8 == 0 else (0.6 if (X - Y) % 8 == 1 else 0)
            if Y % 7 == 6: lv -= 0.8
        elif pat == "rune":
            if G and W >= 5 and H >= 5 and rune(X, Y, W, H): return G, G
            lv += (n - 0.5) * 0.8
        elif pat == "lava":
            crack = abs(math.sin(X * 0.9 + math.sin(Y * 0.7) * 2)) < 0.18 or abs(math.sin(Y * 1.3 + X * 0.3)) < 0.12
            if crack:
                gc = G or (255, 150, 40); return gc, gc
            lv += (n - 0.5) * 1.0 - 0.4
        elif pat == "veins":
            if G and (abs(math.sin(X * 0.8 + Y * 0.35)) < 0.12): return G, G
            lv += (n - 0.5) * 0.8
        elif pat == "flesh":
            lv += (n - 0.5) * 0.7 + (0.5 if (X * 3 + Y) % 11 == 0 else 0)
        elif pat == "energy":
            band = (X + Y * 2) % 5
            c = R[4] if band == 0 else R[3]
            return c, c
        elif pat == "gem":
            cx, cy = (W - 1) / 2, (H - 1) / 2
            dd = abs(X - cx) + abs(Y - cy)
            c = pick(R, 3.6 - dd * 0.8)
            if dd < 1.1: return (255, 255, 255), (G or c)
            return c, (G if G else None)
        else:
            lv += (n - 0.5) * 0.6 if dither else 0
        # 테두리 (윤곽 음영 + 위쪽 하이라이트)
        if rim and w >= 3 and h >= 3:
            if x == 0 or x == w - 1: lv -= 0.9
            elif pat not in ("cloth", "fur") and y == h - 1: lv -= 0.9
            elif pat not in ("cloth", "fur") and y == 0: lv += 0.9
        if rim and w >= 8 and h >= 8 and (x in (1, w - 2) or y == h - 2): lv -= 0.35
        if T and rim and w >= 4 and h >= 4 and (x == 0 or x == w - 1 or y == 0 or y == h - 1):
            return pick(T, lv + 0.6), None
        return pick(R, lv), g
    return paint

def pick(R, lv):
    i = max(0, min(4, int(round(lv))))
    return R[i]

def rune(x, y, w, h):
    cx, cy = w // 2, h // 2
    if x == cx and 1 <= y <= h - 2: return True
    if y == cy and 1 <= x <= w - 2 and x % 2 == 0: return True
    if (x - cx) == (y - cy) and abs(x - cx) == 2: return True
    return False

def FACE(mat, eye, mode="two", mouth=None, teeth=False, brow=True, eye_y=None, eye_w=None):
    eye = hexc(eye)
    mouth = hexc(mouth) if mouth is not None else None
    def paint(x, y, w, h, face, rnd):
        c, g = mat(x, y, w, h, face, rnd)
        ey = eye_y if eye_y is not None else max(1, int(h * 0.4))
        ew = eye_w or max(1, w // 6)
        if mode == "two":
            l0 = max(1, w // 4 - ew // 2); r0 = w - l0 - ew
            xs = set(range(l0, l0 + ew)) | set(range(r0, r0 + ew))
        elif mode == "one":
            xs = set(range(w // 2 - ew // 2 - 1, w // 2 + ew // 2 + 1))
        elif mode == "visor":
            xs = set(range(1, w - 1))
        elif mode == "many":
            xs = {i for i in range(1, w - 1) if i % 3 == 1}
        else:
            xs = set()
        if x in xs and (y == ey or (mode != "visor" and h >= 9 and y == ey + 1)):
            core = mix(eye, (255, 255, 255), 0.55) if (y == ey and (x - min(xs)) % ew == 0) else eye
            return core, eye
        if brow and x in xs and y == ey - 1:
            return mix(c, (0, 0, 0), 0.55), None
        if mouth and h >= 6:
            my = h - max(2, h // 4)
            if y == my and w // 4 <= x < w - w // 4:
                return mouth, (mouth if teeth == "glow" else None)
            if teeth and y == my - 1 and w // 4 <= x < w - w // 4 and x % 2 == 0:
                return (235, 230, 215), None
        return c, g
    return paint
