"""보스 얼굴 v3 — HD(2배) 픽셀로 눈동자·흰자·눈썹·코 그림자·입·광채를 그림"""
from materials2 import hexc, mix


def _rect(x, y, x0, y0, w, h):
    return x0 <= x < x0 + w and y0 <= y < y0 + h


def FACE3(mat, eye, kind="human", skin_shadow=0.72, mouth=None, iris=None, brow=None, glow_eyes=True,
          eye_y=0.42, eye_gap=0.22, eye_w=0.16, beard=None, mark=None, mark_col=None, scar=False, fangs=False, blush=None):
    """
    kind: human(흰자+홍채+동공) / beast(가는 빛나는 눈) / visor(빛나는 가로 틈) / skull(눈구멍+빛)
          void(눈 없는 그림자 + 빛나는 점) / mask(금속 가면 눈 구멍)
    """
    eye = hexc(eye)
    iris = hexc(iris) if iris is not None else eye
    mouth = hexc(mouth) if mouth is not None else None
    mark_col = hexc(mark_col) if mark_col is not None else eye
    brow_c = hexc(brow) if brow is not None else None

    def paint(x, y, w, h, face, rnd):
        c, g = mat(x, y, w, h, face, rnd)
        if w < 6 or h < 6:
            return c, g
        cx = (w - 1) / 2
        ey = int(h * eye_y)
        ew = max(2, int(round(w * eye_w)))
        eh = max(2, ew * 2 // 3) if kind == "human" else max(1, ew // 3 + 1)
        lx = int(round(cx - w * eye_gap - ew / 2 + 0.5)); rx = int(round(cx + w * eye_gap - ew / 2 + 0.5))
        dark = mix(c, (10, 6, 16), 1 - skin_shadow)
        if kind in ("human", "beast", "mask"):
            # 눈두덩 그림자
            for ex in (lx, rx):
                if _rect(x, y, ex - 1, ey - 1, ew + 2, eh + 2) and not _rect(x, y, ex, ey, ew, eh):
                    return mix(c, dark, 0.6), g
            # 눈썹
            if brow_c is not None or kind == "beast":
                bc = brow_c or mix(c, (0, 0, 0), 0.6)
                for i, ex in enumerate((lx, rx)):
                    slope = (x - ex) if i == 0 else (ex + ew - 1 - x)
                    if ex - 1 <= x <= ex + ew and y == ey - 2 - (1 if slope > ew * 0.55 and kind == "beast" else 0):
                        return bc, None
            for ex in (lx, rx):
                if _rect(x, y, ex, ey, ew, eh):
                    lx_, ly_ = x - ex, y - ey
                    if kind == "human":
                        icx = ew / 2 - 0.5
                        if abs(lx_ - icx) <= max(0.6, ew * 0.28) and ly_ >= 0:
                            core = ly_ == eh // 2 and abs(lx_ - icx) < 0.6
                            col = (16, 12, 24) if core else iris
                            if ly_ == 0 and lx_ == int(icx): col = (255, 255, 255)
                            return col, (iris if glow_eyes and not core else None)
                        return (238, 236, 230) if ly_ > 0 else (200, 196, 190), None
                    if kind == "beast":
                        bright = mix(eye, (255, 255, 255), 0.55) if abs(lx_ - (ew - 1) / 2) < 0.8 else eye
                        return bright, eye
                    if kind == "mask":
                        return (8, 6, 12), None
            if kind == "mask":
                for ex in (lx, rx):
                    if _rect(x, y, ex + ew // 2 - 1, ey + 1, 2, 1):
                        return eye, eye
        if kind == "visor":
            vy0, vy1 = ey, ey + max(2, h // 8)
            if 1 <= x <= w - 2 and vy0 - 1 <= y <= vy1:
                if y == vy0 - 1 or y == vy1:
                    return mix(c, (0, 0, 0), 0.5), None
                t = abs(x - cx) / (w / 2)
                col = mix(mix(eye, (255, 255, 255), 0.7), eye, min(1, t * 1.6))
                return col, col
        if kind == "skull":
            for ex in (lx, rx):
                if _rect(x, y, ex - 1, ey - 1, ew + 2, eh + 3):
                    if _rect(x, y, ex + ew // 2 - 1, ey + 1, 2, 2) or _rect(x, y, ex + ew // 2 - 1, ey, 2, 1):
                        return mix(eye, (255, 255, 255), 0.5), eye
                    return (12, 8, 16), None
            ny = ey + eh + 3
            if abs(x - cx) < 1.1 and ny <= y <= ny + 1:
                return (20, 14, 20), None
        if kind == "void":
            for ex in (lx, rx):
                if _rect(x, y, ex, ey, ew, max(1, eh)):
                    return eye, eye
            if abs(x - cx) < w * 0.38 and y >= ey - 2:
                return mix(c, (4, 2, 8), 0.75), None
        # 코 그림자
        if kind in ("human",) and h >= 12:
            ny = ey + eh + 1
            if ny <= y <= ny + max(1, h // 10) and int(cx) <= x <= int(cx) + 1:
                return mix(c, dark, 0.5 if x == int(cx) + 1 else 0.25), g
        # 입
        if mouth is not None:
            my = int(h * 0.78)
            mw = max(2, int(w * 0.26))
            if y == my and abs(x - cx) <= mw / 2:
                return mouth, None
            if y == my + 1 and abs(x - cx) <= mw / 2 - 1:
                return mix(c, dark, 0.4), g
            if fangs and y == my + 1 and abs(abs(x - cx) - mw / 2 + 0.5) < 0.6:
                return (240, 236, 220), None
        if blush is not None and kind == "human" and y == ey + eh + 1:
            for ex in (lx, rx):
                if _rect(x, y, ex, ey + eh + 1, ew, 1):
                    return mix(c, hexc(blush), 0.35), g
        if mark is not None:
            if mark == "forehead" and abs(x - cx) < 1.1 and int(h * 0.14) <= y <= int(h * 0.14) + 2:
                return mark_col, mark_col
            if mark == "tears":
                for ex in (lx, rx):
                    if x == ex + ew // 2 and ey + eh + 1 <= y <= ey + eh + 3 + (x % 2):
                        return mark_col, mark_col
            if mark == "stripes":
                for ex in (lx, rx):
                    if (x == ex - 2 or x == ex + ew + 1) and ey - 1 <= y <= ey + eh + 2:
                        return mark_col, mark_col
        if scar and abs((x - lx) - (y - ey + 3)) < 0.7 and lx - 2 <= x <= lx + ew + 1:
            return mix(c, (60, 20, 20), 0.6), g
        if beard is not None and y >= int(h * 0.68):
            if not (mouth is not None and y == int(h * 0.78) and abs(x - cx) <= max(2, int(w * 0.26)) / 2):
                return beard(x, y, w, h, face, rnd)
        return c, g
    return paint
