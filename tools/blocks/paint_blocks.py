"""손그림풍 블록 텍스처 (32px).

방식: 이음매 없는 노이즈 → 몇 단계 톤으로 끊어 칠하기(포스터화) → 모양마다 빛(왼쪽 위 밝게, 오른쪽 아래 그림자)
→ 붓 자국처럼 작은 덧칠 → 어두운 테두리. 바닐라 이름으로 저장해 마크 기본 그림을 덮어씀.
바이옴 색이 입혀지는 잔디 윗면·잎은 회색조로 그림.
"""
import os, sys, math
import numpy as np
from PIL import Image

N = 32
OUT = os.path.join(sys.argv[1] if len(sys.argv) > 1 else ".", "src/main/resources/assets/minecraft/textures/block")
os.makedirs(OUT, exist_ok=True)


def hexc(h):
    return np.array([(h >> 16) & 255, (h >> 8) & 255, h & 255], dtype=float)


def noise(rng, scale=4, oct=3, n=N):
    """이음매 없이 반복되는 값 노이즈 (0..1)"""
    out = np.zeros((n, n))
    amp, tot = 1.0, 0.0
    for o in range(oct):
        g = scale * (2 ** o)
        grid = rng.random((g, g))
        ys = np.arange(n) * g / n
        y0 = np.floor(ys).astype(int); fy = ys - y0
        fy = fy * fy * (3 - 2 * fy)
        y1 = (y0 + 1) % g
        a = grid[y0][:, y0] * 0  # dummy for shape
        A = grid[np.ix_(y0, y0)]; B = grid[np.ix_(y0, y1)]; C = grid[np.ix_(y1, y0)]; D = grid[np.ix_(y1, y1)]
        fx = fy[None, :]; fyy = fy[:, None]
        v = (A * (1 - fx) + B * fx) * (1 - fyy) + (C * (1 - fx) + D * fx) * fyy
        out += v * amp; tot += amp; amp *= 0.5
    out /= tot
    return (out - out.min()) / (out.max() - out.min() + 1e-9)


def bands(t, pal):
    """0..1 값을 팔레트 톤으로 끊어 칠함"""
    pal = [hexc(p) if isinstance(p, int) else p for p in pal]
    idx = np.clip((t * len(pal)).astype(int), 0, len(pal) - 1)
    img = np.zeros((N, N, 3))
    for i, c in enumerate(pal):
        img[idx == i] = c
    return img


def dabs(img, rng, n, pal, w=(1, 3), h=(1, 2), mask=None):
    """붓 자국: 작은 가로 덧칠"""
    pal = [hexc(p) if isinstance(p, int) else p for p in pal]
    for _ in range(n):
        x, y = rng.integers(0, N), rng.integers(0, N)
        ww, hh = rng.integers(w[0], w[1] + 1), rng.integers(h[0], h[1] + 1)
        c = pal[rng.integers(0, len(pal))]
        for dy in range(hh):
            for dx in range(ww):
                xx, yy = (x + dx) % N, (y + dy) % N
                if mask is None or mask[yy, xx]:
                    img[yy, xx] = c
    return img


def shade_regions(img, lab, light=1.25, dark=0.62, gap=None, edge=1):
    """같은 번호끼리 한 덩어리: 위/왼쪽 경계는 밝게, 아래/오른쪽은 어둡게, 경계선(줄눈)은 gap 색"""
    out = img.copy()
    for y in range(N):
        for x in range(N):
            l = lab[y, x]
            if l < 0:
                if gap is not None: out[y, x] = gap
                continue
            up = lab[(y - 1) % N, x]; lf = lab[y, (x - 1) % N]
            dn = lab[(y + 1) % N, x]; rt = lab[y, (x + 1) % N]
            up2 = lab[(y - 2) % N, x]; dn2 = lab[(y + 2) % N, x]
            if up != l or lf != l:
                out[y, x] = img[y, x] * light
            elif dn != l or rt != l:
                out[y, x] = img[y, x] * dark
            elif edge > 1 and (up2 != l):
                out[y, x] = img[y, x] * (1 + (light - 1) * 0.5)
            elif edge > 1 and (dn2 != l):
                out[y, x] = img[y, x] * (1 - (1 - dark) * 0.5)
    return out


def save(name, img, alpha=None):
    a = np.clip(img, 0, 255).astype(np.uint8)
    if alpha is not None:
        im = Image.fromarray(np.dstack([a, alpha.astype(np.uint8)]), "RGBA")
    else:
        im = Image.fromarray(a, "RGB")
    im.save(os.path.join(OUT, name + ".png"))
    MADE.append(name)


MADE = []


def gray(img):
    g = img.mean(axis=2, keepdims=True)
    return np.repeat(g, 3, axis=2)


# ---------------- 돌 계열 ----------------
STONE = [0x5E5E62, 0x737378, 0x86868A, 0x98989C, 0xA8A8AC]


def stone(name="stone", pal=STONE, seed=1, speck=None):
    rng = np.random.default_rng(seed)
    t = noise(rng, 3, 3) * 0.75 + noise(rng, 8, 1) * 0.25
    img = bands(t, pal)
    img = dabs(img, rng, 40, pal[1:4], w=(2, 4), h=(1, 1))
    if speck:
        img = dabs(img, rng, 18, speck, w=(1, 1), h=(1, 1))
    # 갈라진 금 몇 줄
    for _ in range(2):
        x, y = rng.integers(0, N), rng.integers(0, N)
        for k in range(rng.integers(4, 8)):
            img[y % N, x % N] = hexc(pal[0]) * 0.8
            x += rng.integers(0, 2); y += 1 if rng.random() < 0.6 else 0
    save(name, img)


def bricks(name, pal, mortar, rows=4, cols=2, seed=2, moss=None, crack=False, offset=True, bevel=(1.22, 0.6), stone_noise=True):
    rng = np.random.default_rng(seed)
    lab = -np.ones((N, N), dtype=int)
    rh = N // rows
    img = np.zeros((N, N, 3))
    k = 0
    for r in range(rows):
        bw = N // cols
        off = (bw // 2) if (offset and r % 2) else 0
        for c in range(cols + 1):
            x0 = c * bw - off
            for y in range(r * rh + 1, (r + 1) * rh):
                for x in range(x0 + 1, x0 + bw):
                    lab[y, x % N] = k
            k += 1
    t = noise(rng, 4, 2)
    base = bands(t, pal)
    # 벽돌마다 살짝 다른 밝기
    tint = {i: 0.88 + 0.24 * rng.random() for i in range(k)}
    for y in range(N):
        for x in range(N):
            if lab[y, x] >= 0:
                base[y, x] *= tint[lab[y, x]]
    base = dabs(base, rng, 30, pal[1:-1], w=(2, 3), h=(1, 1), mask=lab >= 0)
    img = shade_regions(base, lab, bevel[0], bevel[1], gap=hexc(mortar), edge=2)
    if crack:
        for _ in range(3):
            x, y = rng.integers(2, N - 2), rng.integers(2, N - 2)
            for s in range(rng.integers(5, 10)):
                if lab[y % N, x % N] >= 0: img[y % N, x % N] = hexc(mortar)
                x += rng.choice([-1, 0, 1]); y += 1
    if moss:
        m = noise(rng, 3, 2); m2 = noise(rng, 6, 2)
        mpal = [hexc(c) for c in moss]
        for y in range(N):
            for x in range(N):
                if m[y, x] > 0.62 or (lab[y, x] < 0 and m[y, x] > 0.45):
                    k = int(np.clip((m2[y, x]) * len(mpal), 0, len(mpal) - 1))
                    img[y, x] = mpal[k] * (0.8 if lab[y, x] < 0 else 1.0)
    save(name, img)


def voronoi_stones(name, pal, gap, n=14, seed=3, moss=None, rounding=1.0):
    rng = np.random.default_rng(seed)
    pts = rng.random((n, 2)) * N
    lab = np.zeros((N, N), dtype=int)
    d1 = np.full((N, N), 1e9); d2 = np.full((N, N), 1e9)
    for i, (px, py) in enumerate(pts):
        for oy in (-N, 0, N):
            for ox in (-N, 0, N):
                yy, xx = np.mgrid[0:N, 0:N]
                d = np.hypot(xx - (px + ox), yy - (py + oy))
                closer = d < d1
                d2 = np.where(closer, d1, np.minimum(d2, d))
                lab = np.where(closer, i, lab)
                d1 = np.where(closer, d, d1)
    edge = (d2 - d1) < 1.6 * rounding
    lab2 = np.where(edge, -1, lab)
    t = noise(rng, 4, 2)
    base = bands(t, pal)
    tint = 0.82 + 0.36 * rng.random(n)
    for i in range(n):
        base[lab == i] *= tint[i]
    base = dabs(base, rng, 25, pal[1:-1], w=(1, 2), h=(1, 1), mask=lab2 >= 0)
    img = shade_regions(base, lab2, 1.3, 0.58, gap=hexc(gap), edge=2)
    if moss:
        m = noise(rng, 3, 2); m2 = noise(rng, 6, 2)
        mpal = [hexc(c) for c in moss]
        for y in range(N):
            for x in range(N):
                if m[y, x] > 0.72 or (lab2[y, x] < 0 and m[y, x] > 0.5):
                    k = int(np.clip((m2[y, x]) * len(mpal), 0, len(mpal) - 1))
                    img[y, x] = mpal[k] * (0.8 if lab2[y, x] < 0 else 1.0)
    save(name, img)


def smooth(name, pal, seed=4, border=True, frame=None):
    rng = np.random.default_rng(seed)
    t = noise(rng, 2, 3) * 0.6 + 0.2
    img = bands(t, pal)
    img = dabs(img, rng, 26, pal, w=(2, 5), h=(1, 1))
    if border:
        fr = hexc(frame if frame else pal[0])
        img[0, :] = img[0, :] * 1.18; img[:, 0] = img[:, 0] * 1.18
        img[N - 1, :] = fr * 0.8; img[:, N - 1] = fr * 0.8
    save(name, img)


def polished(name, pal, seed=5, frame=0x3A3A3E):
    rng = np.random.default_rng(seed)
    t = noise(rng, 3, 2) * 0.5 + 0.25
    img = bands(t, pal)
    img = dabs(img, rng, 20, pal[1:], w=(2, 4), h=(1, 1))
    lab = np.zeros((N, N), dtype=int)
    lab[0, :] = -1; lab[:, 0] = -1
    img = shade_regions(img, lab, 1.2, 0.7, gap=hexc(frame), edge=2)
    save(name, img)


def chiseled(name, pal, mortar, seed=6):
    rng = np.random.default_rng(seed)
    t = noise(rng, 4, 2)
    base = bands(t, pal)
    lab = np.zeros((N, N), dtype=int)
    lab[0, :] = -1; lab[:, 0] = -1
    # 가운데 동그란 문양 + 테두리 띠
    yy, xx = np.mgrid[0:N, 0:N]
    r = np.hypot(xx - 16, yy - 16)
    lab[(r > 6) & (r < 8)] = -1
    lab[(r <= 6)] = 1
    lab[(yy > 2) & (yy < 4) | (yy > N - 5) & (yy < N - 3)] = -1
    img = shade_regions(base, lab, 1.25, 0.6, gap=hexc(mortar), edge=2)
    for a in range(8):
        x = int(16 + math.cos(a * math.pi / 4) * 3.5); y = int(16 + math.sin(a * math.pi / 4) * 3.5)
        img[y, x] = hexc(pal[-1])
    save(name, img)


# ---------------- 흙·풀 ----------------
DIRT = [0x5A3E2A, 0x6E4C33, 0x82593B, 0x966A48]


def dirt(name="dirt", pal=DIRT, seed=10, pebbles=10, coarse=False):
    rng = np.random.default_rng(seed)
    t = noise(rng, 4, 3)
    img = bands(t, pal)
    img = dabs(img, rng, 50, pal, w=(1, 3), h=(1, 2))
    for _ in range(pebbles if not coarse else pebbles * 2):
        x, y = rng.integers(0, N - 1), rng.integers(0, N - 1)
        c = hexc(0x8A8278) if coarse else hexc(0x9A7A5C)
        img[y, x] = c * 1.15; img[y, (x + 1) % N] = c; img[(y + 1) % N, x] = c * 0.75; img[(y + 1) % N, (x + 1) % N] = c * 0.6
    return img


def grass_top_gray(seed=11):
    rng = np.random.default_rng(seed)
    t = noise(rng, 4, 3)
    pal = [0x6A6A6A, 0x828282, 0x9A9A9A, 0xB0B0B0, 0xC4C4C4]
    img = bands(t, pal)
    # 풀잎 덩어리: 짧은 세로 획 (밝은 끝)
    for _ in range(90):
        x, y = rng.integers(0, N), rng.integers(0, N)
        L = rng.integers(2, 4)
        for k in range(L):
            v = 0.75 + 0.35 * (k / L)
            img[(y - k) % N, x] = hexc(0xA8A8A8) * v
        img[(y - L) % N, x] = hexc(0xD8D8D8)
    return img


def leaves_gray(seed, holes=0.18, clump=4, base=0xA0A0A0, colored=None):
    """잎: 동그란 잎 덩어리 + 빛 + 구멍"""
    rng = np.random.default_rng(seed)
    pal = colored if colored else [0x505050, 0x6E6E6E, 0x8C8C8C, 0xAAAAAA, 0xC8C8C8]
    img = np.zeros((N, N, 3)); alpha = np.zeros((N, N))
    hp = [hexc(c) for c in pal]
    img[:] = hp[0]; alpha[:] = 255
    for _ in range(26):
        cx, cy = rng.random() * N, rng.random() * N
        R = clump * (0.7 + 0.6 * rng.random())
        for y in range(N):
            for x in range(N):
                for oy in (-N, 0, N):
                    for ox in (-N, 0, N):
                        dx, dy = x - cx - ox, y - cy - oy
                        d = math.hypot(dx, dy)
                        if d < R:
                            # 왼쪽 위가 밝은 둥근 잎 덩어리
                            light = 0.5 - (dx + dy) / (2.8 * R) + 0.25 * (1 - d / R)
                            i = int(np.clip(light * len(hp), 1, len(hp) - 1))
                            img[y, x] = hp[i]
    hole = noise(rng, 6, 2)
    alpha[hole < holes] = 0
    img[(hole >= holes) & (hole < holes + 0.05)] = hp[0] * 0.8
    return img, alpha


# ---------------- 나무 ----------------
def planks(name, pal, seed, grain=0.5, nails=True):
    rng = np.random.default_rng(seed)
    lab = -np.ones((N, N), dtype=int)
    rows = 4; rh = N // rows
    k = 0
    for r in range(rows):
        cut = rng.integers(8, 24) if r % 2 else rng.integers(4, 28)
        for y in range(r * rh + 1, (r + 1) * rh):
            for x in range(N):
                lab[y, x] = k if x < cut else k + 1
        for y in range(r * rh + 1, (r + 1) * rh): lab[y, cut] = -1
        k += 2
    # 결 무늬: 가로로 늘인 노이즈
    g = np.zeros((N, N))
    for y in range(N):
        g[y] = np.sin((np.arange(N) / N) * 2 * math.pi * (1 + (y % 3)) + y * 0.9) * 0.5 + 0.5
    t = noise(rng, 2, 2) * (1 - grain) + g * grain * 0.6 + noise(rng, 8, 1) * 0.2
    base = bands(t, pal)
    tint = 0.9 + 0.2 * rng.random(k)
    for y in range(N):
        for x in range(N):
            if lab[y, x] >= 0: base[y, x] *= tint[lab[y, x]]
    # 옹이
    for _ in range(2):
        x, y = rng.integers(3, N - 3), rng.integers(0, rows) * rh + rh // 2
        base[y, x] = hexc(pal[0]) * 0.8; base[y, x + 1] = hexc(pal[0]); base[y - 1, x] = hexc(pal[1])
    img = shade_regions(base, lab, 1.18, 0.62, gap=hexc(pal[0]) * 0.55, edge=2)
    if nails:
        for r in range(rows):
            for x in (2, N - 3):
                img[r * rh + rh // 2, x] = hexc(0x2A2A2A)
                img[r * rh + rh // 2 - 1, x] = hexc(0x8A8A8A)
    save(name, img)


def log_side(name, pal, seed, birch=False):
    rng = np.random.default_rng(seed)
    if birch:
        img = bands(noise(rng, 3, 2) * 0.4 + 0.6, pal)
        for _ in range(16):   # 자작나무 검은 무늬
            x, y = rng.integers(0, N), rng.integers(0, N)
            w = rng.integers(2, 7)
            for dx in range(w):
                img[y, (x + dx) % N] = hexc(0x2A2A28)
                if rng.random() < 0.5: img[(y + 1) % N, (x + dx) % N] = hexc(0x4A4A44)
    else:
        img = np.zeros((N, N, 3))
        # 세로 껍질 줄기: 열마다 다른 너비의 판
        x = 0
        while x < N:
            w = rng.integers(2, 5)
            v = rng.random()
            for dx in range(w):
                col = (x + dx) % N
                t = noise(rng, 1, 1)[:, 0] * 0.3 + v * 0.5 + (0.2 if dx == 0 else 0)
                for y in range(N):
                    tt = np.clip(t[y] + (0.15 * math.sin(y * 0.6 + x)), 0, 0.999)
                    img[y, col] = hexc(pal[int(tt * len(pal))])
                if dx == w - 1:
                    img[:, col] *= 0.6
            x += w
        img = dabs(img, rng, 20, pal[:2], w=(1, 1), h=(2, 4))
    save(name, img)


def log_top(name, bark, ring, seed):
    rng = np.random.default_rng(seed)
    img = np.zeros((N, N, 3))
    yy, xx = np.mgrid[0:N, 0:N]
    r = np.maximum(abs(xx - 15.5), abs(yy - 15.5)) * 0.6 + np.hypot(xx - 15.5, yy - 15.5) * 0.4
    rp = [hexc(c) for c in ring]
    bn = noise(rng, 6, 1)
    for y in range(N):
        for x in range(N):
            if r[y, x] > 14.0:
                img[y, x] = hexc(bark[0]) * (0.8 + 0.35 * bn[y, x]) if r[y, x] > 15 else hexc(bark[1]) * 0.7
            else:
                k = int(r[y, x] + 0.6 * math.sin(math.atan2(y - 15.5, x - 15.5) * 3)) % 4
                img[y, x] = rp[[1, 2, 1, 0][k]] * (1.1 - r[y, x] / 40)
    img[15:17, 15:17] = rp[0] * 0.8
    save(name, img)


# ================= 실제 그리기 =================
stone("stone")
stone("andesite", [0x6A6A6A, 0x7E7E7E, 0x929290, 0xA6A6A2, 0xB8B8B4], 21, speck=[0xC8C8C4, 0x5A5A5A])
stone("calcite", [0xC8C8C4, 0xD6D6D2, 0xE2E2DE, 0xECECE8, 0xF6F6F2], 22, speck=[0xB8B8B4])
voronoi_stones("cobblestone", [0x56565A, 0x6C6C70, 0x828286, 0x96969A, 0xA8A8AC], 0x2E2E32, 14, 30)
voronoi_stones("mossy_cobblestone", [0x56565A, 0x6C6C70, 0x828286, 0x96969A, 0xA8A8AC], 0x2E2E32, 14, 30, moss=[0x4A6A2A, 0x5A7E32, 0x6A9038])
voronoi_stones("gravel", [0x6A625C, 0x7E7670, 0x948A84, 0xA89E96, 0xBAB0A8], 0x48423E, 34, 31, rounding=0.7)
SB = [0x6A6A6E, 0x7C7C80, 0x8E8E92, 0xA0A0A4, 0xB0B0B4]
bricks("stone_bricks", SB, 0x3E3E42, rows=2, cols=1, seed=40)
bricks("mossy_stone_bricks", SB, 0x3E3E42, rows=2, cols=1, seed=40, moss=[0x4A6A2A, 0x5A7E32, 0x6A9038])
bricks("cracked_stone_bricks", SB, 0x3E3E42, rows=2, cols=1, seed=40, crack=True)
chiseled("chiseled_stone_bricks", SB, 0x3E3E42)
DS = [0x2A2A30, 0x34343A, 0x3E3E46, 0x4A4A52, 0x56565E]
bricks("deepslate_bricks", DS, 0x18181C, rows=4, cols=2, seed=41)
bricks("cracked_deepslate_bricks", DS, 0x18181C, rows=4, cols=2, seed=41, crack=True)
bricks("deepslate_tiles", DS, 0x141418, rows=4, cols=4, seed=42, offset=False)
polished("polished_deepslate", DS, 43, frame=0x1A1A1E)
polished("polished_andesite", [0x7A7A78, 0x888886, 0x969694, 0xA4A4A2, 0xB0B0AE], 44, frame=0x5A5A58)
smooth("smooth_stone", [0x8A8A8E, 0x96969A, 0xA2A2A6, 0xAEAEB2], 45)
smooth("smooth_stone_slab_side", [0x8A8A8E, 0x96969A, 0xA2A2A6, 0xAEAEB2], 46)
bricks("bricks", [0x7A3A2A, 0x8E4632, 0xA0523A, 0xB06244, 0xBE7050], 0xB8ACA0, rows=4, cols=2, seed=47)

d = dirt("dirt"); save("dirt", d)
save("coarse_dirt", dirt("coarse_dirt", seed=12, pebbles=14, coarse=True))
gt = grass_top_gray(); save("grass_block_top", gt)
# 옆면: 흙 + 위쪽 풀(회색, 색이 입혀짐) 들쭉날쭉한 끝
side = dirt("grass_block_side", seed=13)
save("grass_block_side", side)
ov = np.zeros((N, N, 3)); oa = np.zeros((N, N))
rng = np.random.default_rng(14)
for x in range(N):
    h = 5 + int(3 * math.sin(x * 0.8) + 2 * rng.random())
    for y in range(h):
        ov[y, x] = hexc(0xB4B4B4) * (1.05 - y / (h * 2.2))
        oa[y, x] = 255
    ov[h - 1, x] = hexc(0x7A7A7A); oa[h - 1, x] = 255
    if rng.random() < 0.3: ov[h, x] = hexc(0x6E6E6E); oa[h, x] = 255
save("grass_block_side_overlay", ov, oa)
# 흙길
pt = dirt("dirt_path_top", [0x7A5E3A, 0x8E6E44, 0xA07E50, 0xB08C5C], 15, pebbles=16)
save("dirt_path_top", pt)
ps = dirt("dirt_path_side", [0x5A3E2A, 0x6E4C33, 0x82593B, 0x966A48], 16)
ps[0:2, :] = hexc(0xA07E50); ps[2, :] = hexc(0x7A5E3A)
save("dirt_path_side", ps)

planks("oak_planks", [0x6A4A26, 0x8A6234, 0xA27640, 0xB8884C, 0xC89A5A], 50)
planks("spruce_planks", [0x3E2A18, 0x553A22, 0x6A4A2C, 0x7C5834, 0x8A643C], 51)
planks("birch_planks", [0x9A8656, 0xB4A06A, 0xC8B47C, 0xD8C48C, 0xE4D29A], 52)
planks("dark_oak_planks", [0x261808, 0x34220E, 0x442E16, 0x52381C, 0x5E4224], 53)
log_side("oak_log", [0x3A2A18, 0x4E3A22, 0x63492C, 0x765836], 60)
log_side("spruce_log", [0x241810, 0x332216, 0x432E1E, 0x523A26], 61)
log_side("dark_oak_log", [0x1E140A, 0x2A1C10, 0x382616, 0x44301C], 62)
log_side("birch_log", [0xC8C4B8, 0xD8D4C8, 0xE6E2D6, 0xF2EEE4], 63, birch=True)
log_top("oak_log_top", [0x4E3A22, 0x3A2A18], [0x8A6234, 0xA27640, 0xB8884C], 64)
log_top("spruce_log_top", [0x332216, 0x241810], [0x553A22, 0x6A4A2C, 0x7C5834], 65)
log_top("dark_oak_log_top", [0x2A1C10, 0x1E140A], [0x34220E, 0x442E16, 0x52381C], 66)
log_top("birch_log_top", [0xD8D4C8, 0x8A8678], [0xB4A06A, 0xC8B47C, 0xD8C48C], 67)
log_side("stripped_spruce_log", [0x553A22, 0x6A4A2C, 0x7C5834, 0x8A643C], 68)
log_side("stripped_dark_oak_log", [0x34220E, 0x442E16, 0x52381C, 0x5E4224], 69)

for nm, sd, cl in (("oak_leaves", 70, 4), ("birch_leaves", 71, 3.5), ("dark_oak_leaves", 72, 4.5), ("spruce_leaves", 73, 2.6)):
    img, a = leaves_gray(sd, 0.16, cl)
    save(nm, img, a)
img, a = leaves_gray(74, 0.14, 4, colored=[0x2E4A1E, 0x3E6226, 0x507A30, 0x64903A, 0x7AA648])
save("azalea_leaves", img, a)

print(len(MADE), "textures:", " ".join(MADE))


# ================= 원소 지역 전용 블록 (모드 텍스처) =================
import colorsys
MOD = os.path.join(sys.argv[1] if len(sys.argv) > 1 else ".", "src/main/resources/assets/elementalweapons/textures/block")
ORIG = os.path.join(os.path.dirname(os.path.abspath(__file__)), "orig")   # 처음 그림의 색만 참고 (한 번 복사해 둠)
os.makedirs(ORIG, exist_ok=True)


def src(name):
    p = os.path.join(ORIG, name + ".png")
    if not os.path.exists(p):
        Image.open(os.path.join(MOD, name + ".png")).save(p)
    return Image.open(p).convert("RGBA")


def tones(name, n=5, lo=0.62, hi=1.28, sat=1.0):
    a = np.asarray(src(name)).astype(float)
    m = a[..., 3] > 0
    c = a[..., :3][m].mean(axis=0) / 255
    hh, ll, ss = colorsys.rgb_to_hls(*c)
    out = []
    for i in range(n):
        f = lo + (hi - lo) * i / (n - 1)
        l2 = min(0.95, ll * f)
        r, g, b = colorsys.hls_to_rgb(hh + (i - n // 2) * 0.008, l2, min(1, ss * sat * (1.1 - 0.15 * i / n)))
        out.append((int(r * 255) << 16) | (int(g * 255) << 8) | int(b * 255))
    return out


def save_mod(name, img, alpha=None):
    global OUT
    o = OUT; OUT = MOD
    save(name, img, alpha)
    OUT = o


DIRT_M = tones("elemental_dirt", 4)
for reg, top, side, kind in (("blossom", "blossom_grass_top", "blossom_grass_side", "grass"), ("azure", "azure_grass_top", "azure_grass_side", "grass"),
                             ("gale", "gale_grass_top", "gale_grass_side", "grass"), ("radiant", "radiant_grass_top", "radiant_grass_side", "grass"),
                             ("shadow", "shadow_grass_top", "shadow_grass_side", "grass"), ("storm", "storm_grass_top", "storm_grass_side", "grass"),
                             ("ember", "ember_soil_top", "ember_soil_side", "soil")):
    gp = tones(top, 5, 0.6, 1.3)
    seed = sum(map(ord, reg))
    if kind == "grass":
        g = grass_top_gray(seed) / 200.0
        t = g.mean(axis=2); t = np.clip((t - t.min()) / (t.max() - t.min() + 1e-9) * 1.15 - 0.08, 0, 0.999)
        img = bands(t, gp)
        rng = np.random.default_rng(seed)
        img = dabs(img, rng, 14, gp[3:], w=(1, 1), h=(1, 1))
    else:
        rng = np.random.default_rng(seed)
        img = bands(noise(rng, 4, 3), gp)
        img = dabs(img, rng, 40, gp, w=(1, 3), h=(1, 1))
        for _ in range(6):   # 빛나는 갈라진 틈
            x, y = rng.integers(0, N), rng.integers(0, N)
            for k in range(rng.integers(3, 7)):
                img[y % N, x % N] = hexc(0xFFB040); x += rng.integers(-1, 2); y += 1
    save_mod(top, img)
    sd = dirt(seed=seed + 1, pal=DIRT_M)
    rng = np.random.default_rng(seed + 2)
    for x in range(N):
        hgt = 5 + int(3 * math.sin(x * 0.8 + seed) + 2 * rng.random())
        for y in range(hgt):
            sd[y, x] = hexc(gp[min(4, 3 - y // 3 + (1 if y == 0 else 0))]) if y < hgt - 1 else hexc(gp[0]) * 0.8
    save_mod(side, sd)
    if reg != "ember" or True:
        lp = tones(reg + "_leaves", 5, 0.55, 1.35)
        li, la = leaves_gray(seed + 3, 0.16, 4, colored=lp)
        save_mod(reg + "_leaves", li, la)
        bp = tones(reg + "_log", 4, 0.6, 1.25)
        log_side(reg + "_log", bp, seed + 4)
        os.replace(os.path.join(OUT, reg + "_log.png"), os.path.join(MOD, reg + "_log.png")); MADE.append(reg + "_log*")
        pp = tones(reg + "_planks", 5, 0.55, 1.3)
        planks(reg + "_planks", pp, seed + 5)
        os.replace(os.path.join(OUT, reg + "_planks.png"), os.path.join(MOD, reg + "_planks.png"))
        rp = tones(reg + "_log_top", 3, 0.75, 1.25)
        log_top(reg + "_log_top", bp[1:3][::-1], rp, seed + 6)
        os.replace(os.path.join(OUT, reg + "_log_top.png"), os.path.join(MOD, reg + "_log_top.png"))

save_mod("elemental_dirt", dirt(seed=900, pal=DIRT_M))
for nm, sd_ in (("aether_stone", 901), ("abyss_stone", 902), ("elemental_sand", 903)):
    pal = tones(nm, 5, 0.65, 1.25)
    o = OUT; OUT = MOD
    stone(nm, pal, sd_)
    OUT = o
print("mod textures done")
