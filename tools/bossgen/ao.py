"""
텍스처 굽기(베이킹): 앰비언트 오클루전 + 전체 높이 그라데이션 + 윗빛/역광
모든 박스 면의 텍스처 픽셀을 3D 위치로 되돌린 뒤, 바깥쪽 반구로 짧은 광선을 쏴서
다른 박스에 얼마나 가려지는지 계산 → 틈·겹친 부분·안쪽이 자연스럽게 어두워짐.
"""
import math
import numpy as np
from PIL import Image
from bossgen import faces_of, matmul, rx, ry, rz, apply


def _walk(part, M, T, out):
    piv = apply(M, part.pivot)
    T2 = (T[0] + piv[0], T[1] + piv[1], T[2] + piv[2])
    M2 = matmul(M, matmul(rz(part.rot[2]), matmul(ry(part.rot[1]), rx(part.rot[0]))))
    for b in part.boxes:
        out.append((b, np.array(M2, float), np.array(T2, float)))
    for c in part.children:
        _walk(c, M2, T2, out)


def _dirs(n=24, seed=3):
    rng = np.random.default_rng(seed)
    v = rng.normal(size=(n * 4, 3))
    v /= np.linalg.norm(v, axis=1, keepdims=True)
    return v


def bake(root, tex, glow=None, strength=0.62, radius=(1.4, 3.0, 5.5), sky=0.10, ground=0.16, warm=None):
    boxes = []
    _walk(root, [[1, 0, 0], [0, 1, 0], [0, 0, 1]], (0, 0, 0), boxes)
    # 박스 로컬 좌표계 (점 p_world → local = M^T (p - T))
    inv = [(b, M.T, T, np.array([b.x, b.y, b.z]), np.array([b.x + b.w, b.y + b.h, b.z + b.d])) for b, M, T in boxes]
    pts, nors, pix = [], [], []
    for b, M, T in boxes:
        c_local = np.array([b.x + b.w / 2, b.y + b.h / 2, b.z + b.d / 2])
        for name, verts, (u1, v1, u2, v2) in faces_of(b):
            x0, x1 = sorted((u1, u2)); y0, y1 = sorted((v1, v2))
            if x1 - x0 <= 0 or y1 - y0 <= 0:
                continue
            V = [np.array(v, float) for v in verts]
            us, vs = np.meshgrid(np.arange(x0, x1) + 0.5, np.arange(y0, y1) + 0.5)
            a = (us - u2) / (u1 - u2)
            bb = (vs - v1) / (v2 - v1)
            P = ((1 - bb)[..., None] * ((1 - a)[..., None] * V[0] + a[..., None] * V[1]) +
                 bb[..., None] * ((1 - a)[..., None] * V[3] + a[..., None] * V[2]))
            n = np.cross(V[1] - V[0], V[3] - V[0])
            if np.linalg.norm(n) == 0:
                continue
            n = n / np.linalg.norm(n)
            fc = (V[0] + V[2]) / 2
            if np.dot(n, fc - c_local) < 0:
                n = -n
            Pw = P.reshape(-1, 3) @ M.T + T
            nw = M @ n
            pts.append(Pw)
            nors.append(np.repeat(nw[None], len(Pw), 0))
            px = np.stack([np.floor(us).astype(int).ravel(), np.floor(vs).astype(int).ravel()], 1)
            pix.append(px)
    if not pts:
        return tex
    P = np.concatenate(pts); N = np.concatenate(nors); PX = np.concatenate(pix)
    D = _dirs()
    occ = np.zeros(len(P))
    total = 0
    # 반구 방향만 (법선과 같은 쪽)
    for r in radius:
        for d in D[::4]:
            dd = np.where((N @ d)[:, None] < 0, -d[None], d[None])   # 반구로 뒤집기
            w = np.abs(N @ d) + 0.15                                  # 법선 쪽일수록 중요
            Q = P + N * 0.35 + dd * r
            hit = np.zeros(len(P), bool)
            for b, Mi, T, lo, hi in inv:
                L = (Q - T) @ Mi.T
                hit |= np.all((L > lo - 0.01) & (L < hi + 0.01), axis=1)
            occ += hit * w
            total += w
    occ = occ / total
    # 전체 높이 (y 가 아래로 +): 위가 밝고 발밑이 어둡게
    ys = P[:, 1]
    top, bot = ys.min(), ys.max()
    hgt = 1 - (ys - top) / max(1e-6, bot - top)          # 1 = 꼭대기
    up = -N[:, 1]                                          # 위를 보는 면 = +1
    f = (1 - strength * np.clip(occ * 1.6, 0, 1)) * (1 - ground * (1 - hgt) + sky * hgt) * (1 + 0.06 * up)
    arr = np.array(tex).astype(float)
    g = np.array(glow) if glow is not None else None
    xs, yy = PX[:, 0], PX[:, 1]
    mask = arr[yy, xs, 3] > 0
    if g is not None:
        mask &= g[yy, xs, 3] == 0                         # 빛나는 픽셀은 그대로
    k = f[mask][:, None]
    rgb = arr[yy[mask], xs[mask], :3] * k
    # 그림자는 살짝 차갑게(파랑), 빛은 살짝 따뜻하게
    shade = np.clip(1 - k, 0, 1)
    rgb[:, 2] += shade[:, 0] * 14
    rgb[:, 0] -= shade[:, 0] * 6
    arr[yy[mask], xs[mask], :3] = np.clip(rgb, 0, 255)
    return Image.fromarray(arr.astype(np.uint8), "RGBA")
