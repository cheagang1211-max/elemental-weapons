"""손그림 느낌의 연출 스프라이트 (32x32, 흑백 + 알파 → 게임에서 속성 색으로 물들임)
규칙: 부드러운 그라데이션 대신 2~3단 띠 명암, 가장자리 한 줄은 진하게, 핵심부는 흰색.
종류마다 4프레임 애니메이션. 사용법: python3 tools/fx/fx_sprites.py <프로젝트 루트> [미리보기.png]"""
import sys, os, math, random
from PIL import Image, ImageDraw

ROOT = sys.argv[1]
OUT = f"{ROOT}/src/main/resources/assets/elementalweapons/textures/particle/fx"
N, SS = 32, 4
KINDS = ["orb", "slash", "ring", "spark", "flame", "petal", "swirl", "bolt", "smoke", "star", "drop", "rune", "shard", "leaf"]


class C:
    """4배로 그린 뒤 줄이고, 알파/명도를 띠로 끊어 '손으로 칠한' 단계감을 냄"""
    def __init__(s):
        s.im = Image.new("LA", (N * SS, N * SS), (0, 0))
        s.d = ImageDraw.Draw(s.im)
    def P(s, pts): return [(x * SS, y * SS) for x, y in pts]
    def poly(s, pts, v, a=255): s.d.polygon(s.P(pts), fill=(v, a))
    def ell(s, cx, cy, rx, ry, v, a=255): s.d.ellipse([(cx - rx) * SS, (cy - ry) * SS, (cx + rx) * SS, (cy + ry) * SS], fill=(v, a))
    def line(s, pts, w, v, a=255): s.d.line(s.P(pts), fill=(v, a), width=max(1, int(w * SS)), joint="curve")
    def arc(s, cx, cy, r, a0, a1, w, v, a=255, steps=40):
        pts = [(cx + math.cos(math.radians(a0 + (a1 - a0) * i / steps)) * r, cy + math.sin(math.radians(a0 + (a1 - a0) * i / steps)) * r) for i in range(steps + 1)]
        s.line(pts, w, v, a)
    def done(s):
        sm = s.im.resize((N, N), Image.BOX)
        out = Image.new("RGBA", (N, N), (0, 0, 0, 0))
        px, o = sm.load(), out.load()
        for y in range(N):
            for x in range(N):
                v, a = px[x, y]
                if a < 40: continue
                a2 = 255 if a > 200 else 190 if a > 120 else 110
                v2 = 255 if v > 225 else 205 if v > 160 else 150 if v > 95 else 100
                o[x, y] = (v2, v2, v2, a2)
        # 가장자리 한 줄 진하게 (윤곽)
        solid = [[o[x, y][3] > 0 for x in range(N)] for y in range(N)]
        for y in range(N):
            for x in range(N):
                if not solid[y][x]: continue
                if any(not (0 <= x + dx < N and 0 <= y + dy < N and solid[y + dy][x + dx]) for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))):
                    r, g, b, a = o[x, y]
                    o[x, y] = (max(90, r - 70), max(90, g - 70), max(90, b - 70), min(a, 200))
        return out


def orb(f):
    c = C(); r = 11 - f * 1.5
    c.ell(16, 16, r, r, 150, 150); c.ell(16, 16, r * 0.7, r * 0.7, 210); c.ell(15, 15, r * 0.35, r * 0.35, 255)
    return c.done()

def slash(f):
    # 초승달 베기: 프레임이 지날수록 가늘어지고 끝이 갈라짐
    c = C(); t = [6, 4.6, 3.2, 1.8][f]
    outer = [(16 + math.cos(math.radians(a)) * 14, 18 + math.sin(math.radians(a)) * 12) for a in range(200, 341, 5)]
    inner = [(16 + math.cos(math.radians(a)) * (14 - t), 18 + math.sin(math.radians(a)) * (12 - t * 1.4) + t * 0.2) for a in range(340, 199, -5)]
    c.poly(outer + inner, 150, 170)
    outer2 = [(16 + math.cos(math.radians(a)) * 14, 18 + math.sin(math.radians(a)) * 12) for a in range(215, 326, 5)]
    inner2 = [(16 + math.cos(math.radians(a)) * (14 - t * 0.55), 18 + math.sin(math.radians(a)) * (12 - t * 0.8)) for a in range(325, 214, -5)]
    c.poly(outer2 + inner2, 255)
    if f >= 2:
        for k in range(3):
            a = math.radians(230 + k * 40); c.line([(16 + math.cos(a) * 14, 18 + math.sin(a) * 12), (16 + math.cos(a) * 16.5, 18 + math.sin(a) * 14)], 0.9, 230)
    return c.done()

def ring(f):
    c = C(); r = 8 + f * 2.2; w = [3.2, 2.6, 2.0, 1.2][f]
    c.arc(16, 16, r, 0, 360, w, 220, 230 - f * 30, 48)
    c.arc(16, 16, r - w * 0.6, 0, 360, w * 0.45, 255, 255 - f * 40, 48)
    for k in range(6):
        a = k * 60 + f * 15; c.ell(16 + math.cos(math.radians(a)) * (r + w), 16 + math.sin(math.radians(a)) * (r + w), 0.9, 0.9, 255, 200)
    return c.done()

def spark(f):
    c = C(); L = [9, 13, 10, 6][f]; w = [2.4, 2.0, 1.6, 1.2][f]
    for a in (0, 90):
        dx, dy = math.cos(math.radians(a)), math.sin(math.radians(a))
        c.poly([(16 - dx * L, 16 - dy * L), (16 - dy * w, 16 + dx * w), (16 + dx * L, 16 + dy * L), (16 + dy * w, 16 - dx * w)], 255)
    for a in (45, 135):
        dx, dy = math.cos(math.radians(a)), math.sin(math.radians(a))
        c.line([(16 - dx * L * 0.45, 16 - dy * L * 0.45), (16 + dx * L * 0.45, 16 + dy * L * 0.45)], 1.0, 200)
    return c.done()

def flame(f):
    c = C(); sway = [0, 1.5, -1, 0.5][f]; h = [22, 24, 20, 16][f]
    base = 27
    c.poly([(8, base), (6 + sway, base - h * 0.45), (12 + sway, base - h * 0.7), (11 + sway * 1.6, base - h), (17 + sway, base - h * 0.62),
            (20 + sway * 1.4, base - h * 0.95), (23 + sway, base - h * 0.5), (26, base - h * 0.3), (24, base)], 150)
    c.poly([(11, base), (10 + sway, base - h * 0.4), (14 + sway, base - h * 0.62), (16 + sway * 1.3, base - h * 0.8), (19 + sway, base - h * 0.45), (21, base)], 210)
    c.ell(16 + sway * 0.4, base - 4, 4, 4.5, 255)
    return c.done()

def petal(f):
    c = C(); ang = math.radians(f * 40)
    def R(x, y): return (16 + (x - 16) * math.cos(ang) - (y - 16) * math.sin(ang), 16 + (x - 16) * math.sin(ang) + (y - 16) * math.cos(ang))
    pts = [R(16, 5), R(22, 10), R(23, 18), R(18, 26), R(16, 23), R(14, 26), R(9, 18), R(10, 10)]
    c.poly(pts, 210); c.poly([R(16, 9), R(19, 13), R(17, 20), R(15, 20), R(13, 13)], 255)
    c.line([R(16, 22), R(16, 12)], 0.8, 150)
    return c.done()

def swirl(f):
    c = C(); off = f * 30
    pts = [(16 + math.cos(math.radians(a + off)) * (2 + a / 60), 16 + math.sin(math.radians(a + off)) * (2 + a / 60) * 0.8) for a in range(0, 660, 12)]
    c.line(pts, [2.6, 2.2, 1.8, 1.2][f], 230)
    c.line(pts[: len(pts) // 2], [1.2, 1.0, 0.8, 0.6][f], 255)
    return c.done()

def bolt(f):
    random.seed(77 + f); c = C()
    pts = [(16 + (random.random() - 0.5) * 6, 2)]
    for i in range(1, 7): pts.append((16 + (random.random() - 0.5) * 14, 2 + i * 4.6))
    c.line(pts, [3.2, 2.6, 2.0, 1.2][f], 190); c.line(pts, [1.4, 1.2, 0.9, 0.6][f], 255)
    b = pts[3]; c.line([b, (b[0] + 7, b[1] + 5), (b[0] + 9, b[1] + 10)], 1.0, 220)
    return c.done()

def smoke(f):
    random.seed(11 + f); c = C(); g = 1 + f * 0.25
    for i in range(5):
        x, y, r = 10 + random.random() * 12, 10 + random.random() * 12, (4 + random.random() * 4) * g
        c.ell(x, y, r, r, 120 if i % 2 else 150, 210 - f * 40)
    c.ell(14, 13, 3 * g, 3 * g, 190, 200 - f * 40)
    return c.done()

def star(f):
    c = C(); r = [10, 12.5, 9, 6][f]; ri = r * 0.42
    pts = []
    for k in range(10):
        a = math.radians(-90 + k * 36); rr = r if k % 2 == 0 else ri
        pts.append((16 + math.cos(a) * rr, 16 + math.sin(a) * rr))
    c.poly(pts, 230); c.ell(16, 16, ri * 0.8, ri * 0.8, 255)
    return c.done()

def drop(f):
    c = C(); sq = [0, 1, 2, 3][f]
    if f < 3:
        c.poly([(16, 4 + sq), (22 - sq * 0.3, 16), (22, 21), (19, 26), (13, 26), (10, 21), (10 - sq * 0.3 + 0.6, 16)], 190)
        c.ell(16, 20, 5.5, 5.5, 190); c.ell(14, 18, 2, 2.6, 255)
    else:
        for k in range(6):
            a = math.radians(k * 60); c.ell(16 + math.cos(a) * 9, 18 + math.sin(a) * 5, 1.8, 1.8, 220)
        c.ell(16, 20, 6, 2, 160, 180)
    return c.done()

def rune(f):
    c = C(); rot = f * 12
    c.arc(16, 16, 13, 0, 360, 1.4, 230); c.arc(16, 16, 9.5, 0, 360, 1.0, 200)
    for k in range(6):
        a = math.radians(rot + k * 60); b = math.radians(rot + k * 60 + 120)
        c.line([(16 + math.cos(a) * 9.5, 16 + math.sin(a) * 9.5), (16 + math.cos(b) * 9.5, 16 + math.sin(b) * 9.5)], 0.9, 210)
    for k in range(12):
        a = math.radians(rot * 1.5 + k * 30); x, y = 16 + math.cos(a) * 11.3, 16 + math.sin(a) * 11.3
        c.line([(x - 0.8, y - 0.8), (x + 0.8, y + 0.8)], 0.8, 255) if k % 2 else c.ell(x, y, 0.7, 0.7, 255)
    c.ell(16, 16, 2.4, 2.4, 255)
    return c.done()

def shard(f):
    c = C(); a = math.radians(f * 30)
    def R(x, y): return (16 + (x - 16) * math.cos(a) - (y - 16) * math.sin(a), 16 + (x - 16) * math.sin(a) + (y - 16) * math.cos(a))
    c.poly([R(16, 3), R(21, 13), R(18, 28), R(13, 25), R(11, 12)], 160); c.poly([R(16, 3), R(21, 13), R(16, 22)], 230); c.line([R(16, 6), R(15, 20)], 0.8, 255)
    return c.done()

def leaf(f):
    c = C(); a = math.radians(-30 + f * 25)
    def R(x, y): return (16 + (x - 16) * math.cos(a) - (y - 16) * math.sin(a), 16 + (x - 16) * math.sin(a) + (y - 16) * math.cos(a))
    c.poly([R(16, 4), R(23, 11), R(22, 21), R(16, 28), R(10, 21), R(9, 11)], 180)
    c.poly([R(16, 6), R(21, 12), R(16, 24)], 220); c.line([R(16, 27), R(16, 7)], 0.9, 255)
    for k in range(3): c.line([R(16, 11 + k * 5), R(20 - k, 9 + k * 5)], 0.6, 140); c.line([R(16, 11 + k * 5), R(12 + k, 9 + k * 5)], 0.6, 140)
    return c.done()

FUNCS = [orb, slash, ring, spark, flame, petal, swirl, bolt, smoke, star, drop, rune, shard, leaf]

if __name__ == "__main__":
    os.makedirs(OUT, exist_ok=True)
    names = []
    sheet = Image.new("RGBA", (N * 4 * 4, N * 4 * len(KINDS)), (30, 30, 40, 255))
    for k, (name, fn) in enumerate(zip(KINDS, FUNCS)):
        for f in range(4):
            im = fn(f)
            im.save(f"{OUT}/{name}_{f}.png")
            names.append(f"elementalweapons:fx/{name}_{f}")
            big = im.resize((N * 4, N * 4), Image.NEAREST)
            sheet.paste(big, (f * N * 4, k * N * 4), big)
    import json
    json.dump({"textures": names}, open(f"{ROOT}/src/main/resources/assets/elementalweapons/particles/fx.json", "w"), indent="\t")
    if len(sys.argv) > 2: sheet.save(sys.argv[2])
    print("fx sprites", len(names))
