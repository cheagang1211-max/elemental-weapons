"""고급 도형 헬퍼 (y 아래쪽이 +, 단위 = 픽셀)"""
import math
from bossgen import Box, Part
R = math.radians

def P(name, pivot=(0, 0, 0), rot=(0, 0, 0), boxes=(), children=(), field=True):
    return Part(name, pivot, rot, list(boxes), list(children), field)

def C(cx, cy, cz, w, h, d, mat, faces=None):
    """중심 좌표로 박스"""
    w, h, d = max(1, int(round(w))), max(1, int(round(h))), max(1, int(round(d)))
    return Box(cx - w / 2, cy - h / 2, cz - d / 2, w, h, d, mat, faces)

def B(x, y, z, w, h, d, mat, faces=None):
    return Box(x, y, z, max(1, int(round(w))), max(1, int(round(h))), max(1, int(round(d))), mat, faces)

def lerp(a, b, t): return a + (b - a) * t

def chain(prefix, n, pivot, length, w0, w1, mat, h0=None, h1=None, axis="z", direction=1, rot=(0, 0, 0), extra=None, start=1, decor=None):
    """꼬리/목/촉수 사슬: prefix1..n, 각 마디가 다음 마디의 부모. extra(i, seg)->[boxes]"""
    h0 = h0 if h0 is not None else w0; h1 = h1 if h1 is not None else w1
    segs = []
    for i in range(n):
        t = i / max(1, n - 1)
        w, h = lerp(w0, w1, t), lerp(h0, h1, t)
        L = length if not callable(length) else length(i)
        if axis == "z":
            bx = [C(0, 0, direction * L / 2, w, h, L + 0.0, mat)]
            nxt = (0, 0, direction * L)
        elif axis == "y":
            bx = [C(0, direction * L / 2, 0, w, L, h, mat)]
            nxt = (0, direction * L, 0)
        else:
            bx = [C(direction * L / 2, 0, 0, L, h, w, mat)]
            nxt = (direction * L, 0, 0)
        if decor: bx += decor(i, w, h, L)
        segs.append((bx, nxt, rot if i == 0 else (rot if callable(rot) is False and False else (0, 0, 0))))
    parts = []
    piv = pivot
    for i, (bx, nxt, r) in enumerate(segs):
        parts.append(P(f"{prefix}{i + start}", piv, rot if i == 0 else (0, 0, 0), bx))
        piv = nxt
    for i in range(len(parts) - 1): parts[i].children.append(parts[i + 1])
    return parts[0]

def bend_chain(prefix, pivot, segs, mat, axis="y", bend=(0, 0, 0)):
    """뿔/발톱처럼 휘는 사슬: segs = [(len, w)], 마디마다 bend 만큼 꺾임 (애니메이션 없음 이름 권장)"""
    parts = []
    piv = pivot
    for i, (L, w) in enumerate(segs):
        if axis == "y": bx = [C(0, -L / 2, 0, w, L, w, mat)]; nxt = (0, -L + 0.5, 0)
        else: bx = [C(0, 0, -L / 2, w, w, L, mat)]; nxt = (0, 0, -L + 0.5)
        parts.append(P(f"{prefix}{i + 1}", piv, bend if i > 0 else (0, 0, 0), bx))
        piv = nxt
    for i in range(len(parts) - 1): parts[i].children.append(parts[i + 1])
    return parts[0]

def spikes(n, x0, x1, y, z0, z1, h0, h1, mat, w=1):
    out = []
    for i in range(n):
        t = i / max(1, n - 1)
        hh = lerp(h0, h1, math.sin(t * math.pi) if h0 == h1 else t)
        out.append(B(lerp(x0, x1, t) - w / 2, y - hh, lerp(z0, z1, t) - w / 2, w, hh, w, mat))
    return out

def wing(side, pivot, bone, membrane, span=22, depth=16, fingers=3, mat_claw=None, rot=(0, 0, 0), membrane_glow=None):
    """2단 박쥐/용 날개. side = 'right'(-x) | 'left'(+x). 바깥 마디 = {side}_wing2"""
    s = -1 if side == "right" else 1
    half = span // 2
    inner = [C(s * half / 2, 0, 0, half, 2, 2, bone)]
    strips = 4
    for k in range(strips):
        sw = half / strips
        dd = depth * (0.82 + 0.18 * math.cos((k + 0.5) / strips * math.pi * 2))
        inner.append(B(min(0, s * half) + k * sw, 0, 1, sw, 1, dd, membrane))
    outer = [C(s * half / 2, 0, 0, half, 2, 2, bone)]
    for f in range(fingers):
        a = (f + 1) / (fingers + 1)
        fx = s * half * (0.35 + 0.6 * a)
        outer.append(B(fx - 0.5, 0, 1, 1, 1, depth + 2 - f * 2, bone))
    for k in range(fingers + 1):
        sw = half / (fingers + 1)
        dd = (depth - 2) * (1.0 - 0.18 * k / max(1, fingers)) * (0.8 if k % 2 else 1.0)
        outer.append(B(min(0, s * half) + k * sw, 0.3, 1, sw, 1, dd, membrane))
    if mat_claw: outer.append(C(s * (half + 1), 0, 0, 2, 2, 2, mat_claw))
    w2 = P(f"{side}_wing2", (s * half, 0, 0), (0, 0, 0), outer)
    return P(f"{side}_wing", pivot, rot, inner, [w2])

def cape(pivot, n, w, seg, mat, prefix="cape", depth=1):
    return chain(prefix, n, pivot, seg, w, w * 0.95, mat, h0=depth, h1=depth, axis="y")

def ring_of(prefix, n, radius, size, mat, y=0, tilt=0.0):
    out = []
    for i in range(n):
        a = i * 2 * math.pi / n
        out.append(P(f"{prefix}{i}", (math.cos(a) * radius, y + math.sin(a) * radius * tilt, math.sin(a) * radius), (0, a, 0), [C(0, 0, 0, size, size, size, mat)], field=False))
    return out

def claws(x, y, z, n, mat, spread=2, length=3, dirz=-1):
    return [B(x + (i - (n - 1) / 2) * spread - 0.5, y - 1, z + (dirz * length if dirz < 0 else 0), 1, 1, length, mat) for i in range(n)]

def teeth(x0, x1, y, z, mat, up=False, step=2):
    out = []
    x = x0
    while x < x1:
        out.append(B(x, y - (2 if up else 0), z, 1, 2, 1, mat)); x += step
    return out
