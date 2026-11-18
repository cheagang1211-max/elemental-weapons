"""보스 텍스처 재질 (칠하기 함수 생성기)"""
import math
from bossgen import FACE_LIGHT, shade

def cl(c): return tuple(max(0, min(255, int(v))) for v in c[:3])
def jit(c, rnd, a): return cl((c[0]+rnd.randint(-a,a), c[1]+rnd.randint(-a,a), c[2]+rnd.randint(-a,a)))
def mix(a, b, t): return cl(tuple(a[i]+(b[i]-a[i])*t for i in range(3)))
def hexc(h): return ((h>>16)&255, (h>>8)&255, h&255)

def M(base, pat=None, edge=None, accent=None, glow=None, noise=7, glow_all=False):
    base = hexc(base) if isinstance(base, int) else base
    edge = hexc(edge) if isinstance(edge, int) else edge
    accent = hexc(accent) if isinstance(accent, int) else accent
    glow = hexc(glow) if isinstance(glow, int) else glow
    def paint(x, y, w, h, face, rnd):
        k = FACE_LIGHT[face]
        if glow_all:
            c = jit(base, rnd, noise)
            if (x + y) % 3 == 0: c = mix(c, (255,255,255), 0.35)
            return c, mix(c, (255,255,255), 0.15)
        border = min(w, h) > 2 and (x in (0, w-1) or y in (0, h-1))
        if edge and border: return cl(shade(edge, k)), None
        c = jit(base, rnd, noise); g = None
        if pat == "scale":
            if y % 2 == 0 and (x + (y // 2) % 2 * 2) % 4 == 0: c = mix(c, (0,0,0), 0.3)
            elif y % 2 == 1 and (x + (y // 2) % 2 * 2) % 4 == 1: c = mix(c, (255,255,255), 0.12)
        elif pat == "bark":
            if x % 3 == 0: c = mix(c, (0,0,0), 0.3)
            if rnd.random() < 0.05: c = mix(c, (255,255,255), 0.15)
        elif pat == "leaf":
            c = mix(base, accent or base, rnd.random() * 0.5)
            if glow and rnd.random() < 0.05: return glow, glow
        elif pat == "crystal":
            if (x + y) % 4 == 0: c = mix(c, (255,255,255), 0.35)
            elif (x - y) % 5 == 0: c = mix(c, (0,0,0), 0.2)
            if glow and (x * 3 + y) % 7 == 0: g = glow
        elif pat == "cloth":
            if y % 4 == 0 or x % 5 == 0: c = mix(c, (0,0,0), 0.18)
        elif pat == "feather":
            if (x + y) % 4 == 0: c = mix(c, (0,0,0), 0.18)
            if accent and y >= h * 0.7: c = mix(c, accent, 0.7)
        elif pat == "metal":
            if x % 4 == 1 and y % 4 == 1: c = mix(c, (255,255,255), 0.35)
            if y % 6 == 5: c = mix(c, (0,0,0), 0.2)
        elif pat == "stone":
            if rnd.random() < 0.08: c = mix(c, (0,0,0), 0.35)
        elif pat == "fur":
            if (x * 2 + y) % 5 == 0: c = mix(c, (255,255,255), 0.12)
            if rnd.random() < 0.1: c = mix(c, (0,0,0), 0.15)
        elif pat == "rune":
            if glow and face in ("front", "back") and w >= 5 and (x == w // 2 or (y == h // 2 and x % 2 == 0)):
                return glow, glow
        elif pat == "lava":
            if (x * 7 + y * 3) % 11 == 0 or rnd.random() < 0.07:
                gc = glow or (255, 140, 30); return gc, gc
        elif pat == "veins":
            if glow and ((x + y * 2) % 9 == 0 or (x * 2 - y) % 11 == 0): return glow, glow
        elif pat == "split":
            if x >= w / 2: c = jit(accent, rnd, noise)
        if accent and pat in ("metal",) and border:
            c = accent
        return cl(shade(c, k)), g
    return paint

def FACE(mat, eye, mode="two", eye_row=None, mouth=None, glow_eyes=True):
    """머리 앞면: 재질 + 눈"""
    eye = hexc(eye) if isinstance(eye, int) else eye
    mouth = hexc(mouth) if isinstance(mouth, int) else mouth
    def paint(x, y, w, h, face, rnd):
        c, g = mat(x, y, w, h, face, rnd)
        ey = eye_row if eye_row is not None else max(1, h // 2 - 1)
        ew = max(1, w // 8)
        if mode == "two":
            xs = list(range(w // 4 - ew // 2, w // 4 - ew // 2 + ew)) + list(range(w - w // 4 - (ew + 1) // 2, w - w // 4 - (ew + 1) // 2 + ew))
        elif mode == "one":
            xs = list(range(w // 2 - ew, w // 2 + ew))
        elif mode == "many":
            xs = [x2 for x2 in range(1, w - 1) if x2 % 2 == 1]
        else:
            xs = []
        rows = [ey] if mode == "many" else [ey, ey + (1 if h >= 8 else 0)]
        if mode == "many": rows = [ey, ey + 2]
        if y in rows and x in xs:
            return eye, (eye if glow_eyes else None)
        if y == ey - 1 and x in xs and mode != "many":
            return mix(c, (0, 0, 0), 0.5), g
        if mouth and h >= 6 and y == h - 2 and w // 4 <= x < w - w // 4:
            return mouth, None
        return c, g
    return paint
