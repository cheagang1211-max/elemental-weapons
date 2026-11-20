"""속성 × 무기종류 스프라이트 디자인"""
class Scaled:
    """좌표를 (cx,cy) 기준 S배로 키워 그리는 래퍼"""
    def __init__(s, cv, cx, cy, S): s.cv, s.cx, s.cy, s.S = cv, cx, cy, S
    def _p(s, pts): return [(s.cx + (x - s.cx) * s.S, s.cy + (y - s.cy) * s.S) for x, y in pts]
    def poly(s, pts, c, glow=False): s.cv.poly(s._p(pts), c, glow)
    def rect(s, x0, y0, x1, y1, c, glow=False): s.poly([(x0, y0), (x1, y0), (x1, y1), (x0, y1)], c, glow)
    def circ(s, cx, cy, r, c, glow=False): (x, y), = s._p([(cx, cy)]); s.cv.circ(x, y, r * s.S, c, glow)
    def line(s, pts, w, c, glow=False): s.cv.line(s._p(pts), w * s.S, c, glow)
    def arc(s, cx, cy, r, a0, a1, w, c, glow=False, steps=24): (x, y), = s._p([(cx, cy)]); s.cv.arc(x, y, r * s.S, a0, a1, w * s.S, c, glow, steps)
    def gem(s, cx, cy, r, base, glow=True): (x, y), = s._p([(cx, cy)]); s.cv.gem(x, y, r * s.S, base, glow)
import math
from sprite_draw import Canvas, PAL, hexc, tone, aura

def P_(el): return {k: hexc(v) for k, v in PAL[el].items()}

def blade_profile(el, t, w):
    """t: 0=가드 1=끝 → (중심 이동, 반폭)"""
    if el == "fire":     return 1.6 * math.sin(t * 9.5), w * (1 - 0.55 * t) * (1 + 0.22 * math.sin(t * 19 + 1))
    if el == "electric": return 2.4 * (4 * abs((t * 1.6 + 0.25) % 1 - 0.5) - 1), w * (0.95 - 0.45 * t)
    if el == "water":    return 5.0 * t * t, w * (0.85 + 0.35 * math.sin(t * 2.8))
    if el == "grass":    return 0.0, w * 1.25 * max(0.12, math.sin(math.pi * (0.08 + 0.92 * t))) ** 0.75
    if el == "wind":     return 4.0 * t ** 1.7, w * 0.7 * (1 - 0.3 * t)
    if el == "dark":     return 0.0, w * (1.05 - 0.35 * t) + (0.9 if int(t * 14) % 3 == 0 and t < 0.85 else 0)
    return 0.0, w * (0.9 + 0.35 * min(t, 0.7))

def blade(cv, el, xc, y_bot, y_top, w, tip=0.2):
    p = P_(el)
    n = 48
    L, Rr, mid = [], [], []
    for i in range(n + 1):
        t = i / n
        c, hw = blade_profile(el, t, w)
        if t > 1 - tip: hw *= max(0.0, (1 - t) / tip)
        y = y_bot + (y_top - y_bot) * t
        L.append((xc + c - hw, y)); Rr.append((xc + c + hw, y)); mid.append((xc + c, y))
    cv.poly(L + Rr[::-1], tone(p["blade2"], -0.05))                        # 그림자 면
    inner_r = [((m[0] + r[0]) / 2, r[1]) for m, r in zip(mid, Rr)]
    cv.poly(mid + inner_r[::-1], tone(p["blade2"], 0.1))                    # 중간 톤
    cv.poly(L + mid[::-1], p["blade"])                                      # 밝은 면
    inner_l = [((m[0] + l[0]) / 2, l[1]) for m, l in zip(mid, L)]
    cv.poly(inner_l[: int(n * 0.9)] + mid[: int(n * 0.9)][::-1], tone(p["blade"], 0.1))   # 반사광 띠
    cv.line(L, 0.55, tone(p["blade"], 0.3))                                 # 날 하이라이트
    cv.line(Rr, 0.45, tone(p["blade2"], -0.18))                             # 반대쪽 날 그림자
    for i in range(3, int(n * 0.7), 5):                                     # 사선 광택
        a, b = L[i], mid[min(n, i + 3)]
        cv.line([a, b], 0.35, tone(p["blade"], 0.35))
    # 홈(풀러) + 각인 룬
    fl = [(m[0], m[1]) for m in mid[2:int(n * 0.62)]]
    cv.line(fl, 1.1, tone(p["blade2"], -0.12))
    for i in range(4, int(n * 0.6), 4):
        x, y = mid[i]
        cv.rect(x - 0.35, y - 0.6, x + 0.35, y + 0.2, hexc(PAL[el]["glow"]), glow=True)
    if el in ("fire", "electric", "dark", "light"):                         # 빛나는 중심 문양
        core = [(m[0], m[1]) for m in mid[2:int(n * 0.85)]]
        cv.line(core, 1.6 if el != "light" else 1.2, hexc(PAL[el]["glow"]), glow=True)
    if el == "dark":
        for i in range(4, n - 6, 7):
            for sd in (-1, 1):
                x, y = (Rr[i] if sd > 0 else L[i])
                cv.poly([(x, y + 1), (x + sd * 3, y - 2), (x, y - 1.5)], p["blade"], glow=True)
    if el == "grass":
        for i in range(6, n - 8, 6):
            cv.line([mid[i], (L[i + 3][0] + 1, L[i + 3][1])], 0.6, tone(p["blade2"], 0.0))
            cv.line([mid[i], (Rr[i + 3][0] - 1, Rr[i + 3][1])], 0.6, tone(p["blade2"], 0.0))
    if el == "water":
        for i in range(8, n - 8, 9): cv.arc(mid[i][0] - 0.5, mid[i][1], 1.5, 180, 360, 0.6, hexc(PAL[el]["glow"]), glow=True)
    if el == "wind":
        for i in range(6, n - 6, 6): cv.line([L[i], (L[i][0] - 2.2, L[i][1] - 2.4)], 0.8, p["metal"])
    if el == "electric":
        for i in range(10, n - 8, 12): cv.circ(mid[i][0] + 2.5, mid[i][1] - 1, 0.9, hexc(PAL[el]["glow"]), glow=True)
    return mid

def wing(cv, x, y, sd, size, c1, c2, n=4, up=True):
    for k in range(n):
        a = math.radians(-20 - k * 22) if up else math.radians(20 + k * 22)
        L = size * (1 - k * 0.15)
        ex, ey = x + sd * math.cos(a) * L, y + math.sin(a) * L
        cv.poly([(x, y - 1), (ex, ey), (x + sd * math.cos(a) * L * 0.55, y + math.sin(a) * L * 0.55 + 2.4), (x, y + 1.2)], c1 if k % 2 == 0 else c2)

def guard(cv, el, xc, y, w, big=1.0):
    p = P_(el); h = w / 2
    if el == "fire":
        for sd in (-1, 1):
            cv.poly([(xc, y - 1.5), (xc + sd * h, y - 1), (xc + sd * (h + 2) * big, y - 6 * big), (xc + sd * (h - 1), y - 3), (xc, y + 1.5)], p["metal"])
            cv.poly([(xc + sd * (h - 1), y - 2), (xc + sd * (h + 1.5) * big, y - 7.5 * big), (xc + sd * (h + 0.5), y - 2.6)], p["glow"], glow=True)
    elif el == "electric":
        for sd in (-1, 1):
            cv.line([(xc, y), (xc + sd * h * 0.6, y - 2), (xc + sd * h * 0.4, y + 1), (xc + sd * h, y - 1.5), (xc + sd * (h + 2) * big, y - 5 * big)], 2.0 * big, p["metal"])
            cv.line([(xc + sd * h, y - 1.5), (xc + sd * (h + 2) * big, y - 5 * big)], 1.0, p["glow"], glow=True)
    elif el == "water":
        for sd in (-1, 1):
            cv.arc(xc + sd * h * 0.6, y, h * 0.55, 180 if sd < 0 else 0, 360 if sd < 0 else 180, 1.8 * big, p["metal"])
            cv.circ(xc + sd * (h + 0.8), y + 0.5, 1.4 * big, p["gem"], glow=True)
    elif el == "grass":
        for sd in (-1, 1):
            cv.poly([(xc, y), (xc + sd * h * 0.5, y - 3 * big), (xc + sd * (h + 2) * big, y - 1), (xc + sd * h * 0.5, y + 1.5)], p["blade"])
            cv.line([(xc, y), (xc + sd * (h + 1) * big, y - 1)], 0.6, p["blade2"])
            cv.circ(xc + sd * (h * 0.7), y + 2.2, 1.3 * big, p["glow"], glow=True)
    elif el == "wind":
        for sd in (-1, 1): wing(cv, xc + sd * 2, y, sd, h + 3 * big, p["metal"], p["metal2"], n=3)
    elif el == "dark":
        for sd in (-1, 1):
            cv.poly([(xc, y - 1), (xc + sd * (h + 2) * big, y - 3 * big), (xc + sd * h * 0.7, y), (xc + sd * (h + 1) * big, y + 3 * big), (xc + sd * h * 0.5, y + 1.5), (xc, y + 1.5)], p["metal"])
            cv.poly([(xc + sd * (h + 2) * big, y - 3 * big), (xc + sd * (h + 3.5) * big, y - 5 * big), (xc + sd * (h + 1) * big, y - 2.5 * big)], p["blade"], glow=True)
    else:
        for sd in (-1, 1): wing(cv, xc + sd * 2.5, y, sd, h + 4 * big, p["metal2"], tone(p["metal"], 0.1), n=4)
    for sd in (-1, 1):                                                      # 금 소용돌이 장식
        cv.arc(xc + sd * h * 0.35, y + 2.2 * big, 1.6 * big, 0, 300 if sd > 0 else -300, 0.55, p["metal2"])
        cv.arc(xc + sd * h * 0.62, y - 2.0 * big, 1.2 * big, 180, 450, 0.5, p["metal2"])
        cv.gem(xc + sd * h * 0.8, y + 0.2, 1.0 * big, p["gem"])
    cv.poly([(xc - 2.2 * big, y + 1.3 * big), (xc + 2.2 * big, y + 1.3 * big), (xc, y + 4.2 * big)], p["metal2"])   # 아래 뾰족 장식
    cv.rect(xc - h * 0.55, y - 1.3 * big, xc + h * 0.55, y + 1.3 * big, p["metal"] if el not in ("light",) else p["metal"])
    cv.line([(xc - h * 0.55, y - 1.2 * big), (xc + h * 0.55, y - 1.2 * big)], 0.6, tone(p["metal"], 0.2))
    cv.gem(xc, y, 2.2 * big, p["gem"])

def grip(cv, el, xc, y0, y1, w=2.2):
    p = P_(el)
    cv.rect(xc - w / 2, y0, xc + w / 2, y1, p["grip"])
    cv.rect(xc - w / 2, y0, xc - w / 2 + 0.5, y1, tone(p["grip"], 0.15))
    y = y0 + 0.8
    while y < y1 - 0.6:
        cv.line([(xc - w / 2, y), (xc + w / 2, y + 1.2)], 0.45, tone(p["grip"], 0.25))
        cv.line([(xc + w / 2, y), (xc - w / 2, y + 1.2)], 0.35, tone(p["grip"], -0.15)); y += 1.6
    for yy in (y0 + 0.2, (y0 + y1) / 2):
        cv.rect(xc - w / 2 - 0.4, yy, xc + w / 2 + 0.4, yy + 0.7, p["metal2"])
    cv.rect(xc - w / 2 - 0.7, y1, xc + w / 2 + 0.7, y1 + 1.2, p["metal2"])
    cv.poly([(xc - 2.4, y1 + 1.2), (xc + 2.4, y1 + 1.2), (xc + 1.4, y1 + 4.6), (xc - 1.4, y1 + 4.6)], p["metal"])
    cv.gem(xc, y1 + 2.8, 1.5, p["gem"])
    cv.poly([(xc - 0.8, y1 + 4.6), (xc + 0.8, y1 + 4.6), (xc, y1 + 6.0)], p["metal2"])

def staff_head(cv, el, xc, y, S=1.45):
    p = P_(el)
    cv = Scaled(cv, xc, y, S)
    cv.circ(xc, y, 6.5, tone(p["dark"], 0.05)); cv.circ(xc, y, 5.3, (0, 0, 0)) if False else None
    if el == "light":
        cv.arc(xc, y - 1, 8.5, 0, 360, 1.2, p["glow"], glow=True, steps=40)
        for sd in (-1, 1): wing(cv, xc + sd * 3, y + 2, sd, 11, p["metal2"], tone(p["metal"], 0.1), n=4)
    elif el == "dark":
        for sd in (-1, 1): cv.poly([(xc + sd * 2, y + 3), (xc + sd * 10, y - 6), (xc + sd * 6, y - 1), (xc + sd * 9, y + 4), (xc + sd * 4, y + 4)], p["metal"])
        cv.arc(xc, y, 7.5, 200, 340, 1.4, p["metal2"])
    elif el == "fire":
        for k in range(5):
            a = math.radians(-90 + (k - 2) * 30)
            cv.poly([(xc + math.cos(a) * 3, y + math.sin(a) * 3 + 2), (xc + math.cos(a) * 10, y + math.sin(a) * 10), (xc + math.cos(a + 0.35) * 4, y + math.sin(a + 0.35) * 4 + 2)], p["glow"] if k % 2 else p["blade"], glow=True)
    elif el == "electric":
        for sd in (-1, 1):
            cv.line([(xc + sd * 2, y + 4), (xc + sd * 6, y), (xc + sd * 4, y - 2), (xc + sd * 8, y - 7)], 1.8, p["metal"])
            cv.line([(xc + sd * 6, y), (xc + sd * 4, y - 2), (xc + sd * 8, y - 7)], 0.8, p["glow"], glow=True)
    elif el == "water":
        cv.arc(xc, y, 7.5, 140, 400, 1.8, p["metal"], steps=40)
        for k in range(6): a = k * math.pi / 3; cv.circ(xc + math.cos(a) * 7.5, y + math.sin(a) * 7.5, 1.0, p["glow"], glow=True)
    elif el == "grass":
        for k in range(6):
            a = math.radians(k * 60)
            cv.poly([(xc, y), (xc + math.cos(a - 0.3) * 6, y + math.sin(a - 0.3) * 6), (xc + math.cos(a) * 9, y + math.sin(a) * 9), (xc + math.cos(a + 0.3) * 6, y + math.sin(a + 0.3) * 6)], p["blade"] if k % 2 else p["glow"], glow=(k % 2 == 0))
    else:
        for sd in (-1, 1): wing(cv, xc + sd * 2, y + 2, sd, 10, p["metal"], p["metal2"], n=4)
    # 중심 수정
    cv.poly([(xc, y - 7), (xc + 3.6, y), (xc, y + 5), (xc - 3.6, y)], tone(p["gem"], -0.2), glow=True)
    cv.poly([(xc, y - 6), (xc + 2, y - 0.5), (xc, y + 3), (xc - 1.5, y)], p["gem"], glow=True)
    cv.circ(xc - 0.8, y - 2.5, 0.8, (255, 255, 255), glow=True)

def hammer_head(cv, el, xc, y, S=1.25):
    p = P_(el)
    cv = Scaled(cv, xc, y, S)
    if el == "dark":
        cv.circ(xc, y, 8, p["metal"])
        for k in range(8):
            a = k * math.pi / 4
            cv.poly([(xc + math.cos(a - 0.3) * 7, y + math.sin(a - 0.3) * 7), (xc + math.cos(a) * 12, y + math.sin(a) * 12), (xc + math.cos(a + 0.3) * 7, y + math.sin(a + 0.3) * 7)], p["metal2"])
        cv.circ(xc, y, 3.4, p["gem"], glow=True)
        return
    W, H = (24, 13) if el != "wind" else (20, 10)
    if el == "water":
        cv.rect(xc - 4, y - 7, xc + 4, y + 7, p["metal"])
        for sd in (-1, 1): cv.poly([(xc + sd * 4, y - 6), (xc + sd * 12, y - 4), (xc + sd * 12, y + 4), (xc + sd * 4, y + 6)], p["metal2"])
        cv.gem(xc, y, 3.4, p["gem"]); return
    cv.rect(xc - W / 2, y - H / 2, xc + W / 2, y + H / 2, p["metal"] if el != "grass" else p["grip"])
    cv.rect(xc - W / 2, y - H / 2, xc + W / 2, y - H / 2 + 1.4, tone(p["metal"] if el != "grass" else p["grip"], 0.18))
    for sd in (-1, 1):
        cv.rect(xc + sd * W / 2 - (2 if sd > 0 else 0), y - H / 2 - 1, xc + sd * W / 2 + (0 if sd > 0 else 2), y + H / 2 + 1, p["metal2"])
        for yy in (y - H / 2 + 1.2, y + H / 2 - 1.2):
            cv.circ(xc + sd * (W / 2 - 3.5), yy, 0.6, tone(p["metal2"], 0.2))
        cv.poly([(xc + sd * (W / 2 + 0.5), y - 1.5), (xc + sd * (W / 2 + 3.5), y), (xc + sd * (W / 2 + 0.5), y + 1.5)], p["metal2"])
    cv.line([(xc - W / 2 + 3, y + H / 2 - 2.2), (xc + W / 2 - 3, y + H / 2 - 2.2)], 0.4, p["metal2"])
    cv.line([(xc - W / 2 + 3, y - H / 2 + 2.2), (xc + W / 2 - 3, y - H / 2 + 2.2)], 0.4, p["metal2"])
    if el == "fire":
        for k in range(4): cv.poly([(xc - 9 + k * 6, y - H / 2), (xc - 7 + k * 6, y - H / 2 - 6 - (k % 2) * 2), (xc - 5 + k * 6, y - H / 2)], p["glow"], glow=True)
        cv.line([(xc - 8, y), (xc - 3, y + 2), (xc + 2, y - 2), (xc + 8, y + 1)], 1.2, p["glow"], glow=True)
    elif el == "electric":
        cv.line([(xc - 2, y - H / 2 - 7), (xc + 2, y - 3), (xc - 2, y + 1), (xc + 3, y + H / 2 + 5)], 1.6, p["glow"], glow=True)
    elif el == "grass":
        for k in range(5): cv.circ(xc - 9 + k * 4.5, y - H / 2, 2.0, p["blade"])
        cv.circ(xc - 6, y - H / 2 - 1.5, 1.3, p["glow"], glow=True); cv.circ(xc + 5, y - H / 2 - 1.2, 1.3, p["glow"], glow=True)
    elif el == "wind":
        for sd in (-1, 1): wing(cv, xc + sd * W / 2, y, sd, 9, p["metal"], p["metal2"], n=3)
    elif el == "light":
        cv.circ(xc, y, 4.5, p["glow"], glow=True)
        for k in range(8): a = k * math.pi / 4; cv.line([(xc + math.cos(a) * 5, y + math.sin(a) * 5), (xc + math.cos(a) * 7, y + math.sin(a) * 7)], 1.0, p["glow"], glow=True)
    if el != "light": cv.gem(xc, y, 2.8, p["gem"])

def shield(cv, el):
    p = P_(el); xc = 32
    if el in ("water", "wind"):
        cv.circ(xc, 32, 24, p["metal2"]); cv.circ(xc, 32, 21.5, p["metal"]); cv.circ(xc, 32, 18, tone(p["blade2"], 0.05))
    else:
        pts = [(xc - 22, 10), (xc + 22, 10), (xc + 22, 32), (xc, 58), (xc - 22, 32)]
        inner = [(xc - 18.5, 13), (xc + 18.5, 13), (xc + 18.5, 31), (xc, 52.5), (xc - 18.5, 31)]
        cv.poly(pts, p["metal2"]); cv.poly(inner, tone(p["blade2"], 0.0) if el != "light" else p["blade"])
        cv.poly([(xc - 18.5, 13), (xc, 13), (xc, 52.5), (xc - 18.5, 31)], tone(p["blade2"] if el != "light" else p["blade"], 0.08))
    # 문장
    if el == "fire":
        for k in range(3): cv.poly([(xc - 7 + k * 7, 40), (xc - 5 + k * 7, 22 - (k % 2) * 6), (xc - 1 + k * 7, 40)], p["glow"], glow=True)
    elif el == "electric": cv.line([(xc + 3, 16), (xc - 4, 30), (xc + 4, 30), (xc - 3, 46)], 3.0, p["glow"], glow=True)
    elif el == "water":
        for r in (6, 11): cv.arc(xc, 32, r, 200, 520, 1.6, p["glow"], glow=True, steps=36)
        cv.gem(xc, 32, 3.2, p["gem"])
    elif el == "grass":
        cv.poly([(xc, 16), (xc + 8, 30), (xc, 46), (xc - 8, 30)], p["blade"]); cv.line([(xc, 18), (xc, 44)], 1.0, p["blade2"]); cv.circ(xc, 30, 2.4, p["glow"], glow=True)
    elif el == "wind":
        for k in range(3): cv.arc(xc, 32, 6 + k * 4, k * 60, k * 60 + 200, 1.6, p["glow"] if k == 0 else p["metal2"], glow=(k == 0))
    elif el == "dark":
        for sd in (-1, 1): cv.poly([(xc, 26), (xc + sd * 14, 20), (xc + sd * 10, 28), (xc + sd * 14, 34), (xc, 32)], p["metal"])
        cv.gem(xc, 29, 3.4, p["gem"])
    else:
        cv.rect(xc - 1.6, 16, xc + 1.6, 48, p["glow"], glow=True); cv.rect(xc - 9, 24, xc + 9, 27.2, p["glow"], glow=True); cv.gem(xc, 25.6, 3.0, p["gem"])
    for (x, y) in ((xc - 18, 14), (xc + 18, 14), (xc - 18, 30), (xc + 18, 30), (xc - 9, 44), (xc + 9, 44)): cv.circ(x, y, 1.0, tone(p["metal2"], 0.15))
    cv.arc(xc, 32, 15, 200, 340, 0.5, tone(p["metal2"], 0.1), steps=30)

def scythe(cv, el):
    """낫: 세로 자루 + 위쪽에서 왼쪽 아래로 휘어지는 초승달 날"""
    p = P_(el); xc = 38
    # 자루 (살짝 휨)
    shaft = [(xc + 0.0, 9), (xc + 0.6, 30), (xc + 0.2, 50), (xc - 0.8, 62)]
    cv.line(shaft, 3.2, p["grip"])
    cv.line([(x - 1.0, y) for x, y in shaft], 0.8, tone(p["grip"], 0.18))
    for k in range(10):
        y = 30 + k * 3.0
        cv.line([(xc - 1.4, y), (xc + 1.8, y + 1.5)], 0.6, p["metal2"])
    for y in (28, 46): cv.rect(xc - 2.4, y, xc + 2.4, y + 1.6, p["metal2"]); cv.gem(xc, y + 0.8, 0.9, p["gem"])
    cv.poly([(xc - 2, 61), (xc + 1.2, 61), (xc - 1.4, 66)], p["metal2"])
    # 날: 바깥 호를 따라 두께가 끝으로 갈수록 얇아지는 초승달
    def pt(cx, cy, r, a): return (cx + math.cos(math.radians(a)) * r, cy + math.sin(math.radians(a)) * r)
    OC, R0 = (xc + 1, 40), 28
    def edge(t, inset=0.0):
        a = 276 - 112 * t; th = 9.0 * (1 - t) ** 0.85 + 0.25
        return pt(OC[0], OC[1], R0 - inset * th, a)
    ts = [i / 30 for i in range(31)]
    cv.poly([edge(t) for t in ts] + [edge(t, 1.0) for t in reversed(ts)], p["blade2"])
    cv.poly([edge(t, 0.12) for t in ts[:-1]] + [edge(t, 0.62) for t in reversed(ts[:-1])], p["blade"])
    cv.line([edge(t, 0.04) for t in ts], 0.9, p["glow"], glow=True)
    # 속성 장식
    if el == "fire":
        for k in range(5):
            t = 0.12 + k * 0.15; a = 276 - 112 * t; x, y = edge(t); cv.poly([(x - 1.5, y + 1), (x + math.cos(math.radians(a)) * 5, y + math.sin(math.radians(a)) * 5), (x + 1.5, y + 1)], p["glow"], glow=True)
    elif el == "electric":
        cv.line([edge(t, 0.45 + (0.2 if k % 2 else -0.2)) for k, t in enumerate((0.1, 0.25, 0.4, 0.55, 0.7, 0.82))], 1.1, p["glow"], glow=True)
    elif el == "water":
        cv.line([edge(t, 0.5) for t in ts[2:26]], 0.8, p["glow"], glow=True)
        for t in (0.2, 0.45, 0.7): x, y = edge(t, 0.5); cv.circ(x, y, 1.1, p["glow"], glow=True)
    elif el == "grass":
        for t in (0.25, 0.5): x, y = edge(t, 0.5); cv.poly([(x, y), (x - 3, y - 2), (x - 5, y + 1), (x - 2, y + 2)], p["glow"], glow=True)
    elif el == "wind":
        wing(cv, xc + 1, 15, 1, 9, p["metal"], p["metal2"], n=3)
    elif el == "dark":
        for k in range(6):
            x, y = edge(0.08 + k * 0.13, 1.0); cv.poly([(x - 1, y), (x + 1.2, y + 3), (x + 2, y - 0.5)], p["dark"])
        cv.gem(xc + 1, 20, 2.4, p["gem"])
    else:
        cv.arc(xc + 1, 14, 6, 0, 360, 0.8, p["glow"], glow=True, steps=30)
    # 날과 자루를 잇는 받침 + 뒤쪽 가시
    cv.poly([(xc - 3, 12), (xc + 3.5, 10), (xc + 3.5, 19), (xc - 2.5, 18)], p["metal"])
    cv.poly([(xc + 3, 11), (xc + 11, 5), (xc + 6, 14)], p["metal2"])
    cv.gem(xc + 0.5, 14.5, 2.0, p["gem"])

def bow(cv, el):
    """활: 왼쪽으로 휜 몸체 + 오른쪽 시위, 끝이 젖혀진 리커브"""
    p = P_(el)
    cx, cy, R = 54, 32, 33
    def pt(a, r=R): return (cx + math.cos(math.radians(a)) * r, cy + math.sin(math.radians(a)) * r)
    top, bot = pt(222), pt(138)
    # 시위
    cv.line([(top[0] + 3.5, top[1] - 3), (bot[0] + 3.5, bot[1] + 3)], 0.6, tone(p["metal"], 0.25))
    cv.line([(top[0] + 3.5, top[1] - 3), (bot[0] + 3.5, bot[1] + 3)], 0.3, p["glow"], glow=True)
    # 몸체 (두께가 가운데 두껍고 끝으로 갈수록 얇아짐)
    for a0 in range(138, 222, 2):
        t = abs(a0 - 180) / 42
        w = 4.2 - 2.4 * t
        cv.line([pt(a0), pt(a0 + 2)], w, p["blade2"])
    for a0 in range(140, 220, 2):
        t = abs(a0 - 180) / 42
        cv.line([pt(a0, R - 1.1), pt(a0 + 2, R - 1.1)], max(0.6, 1.6 - 1.0 * t), p["blade"])
    cv.arc(cx, cy, R + 0.4, 146, 214, 0.6, p["glow"], glow=True, steps=30)
    # 젖혀진 끝 (리커브) + 끝 장식
    for sgn, e in ((-1, top), (1, bot)):
        tipx, tipy = e[0] + 5, e[1] + sgn * 5
        cv.line([e, (e[0] + 2.5, e[1] + sgn * 3), (tipx, tipy)], 1.6, p["metal"])
        cv.gem(tipx, tipy, 1.6, p["gem"])
    # 손잡이
    gx, gy = pt(180)
    cv.rect(gx - 2.6, gy - 5.5, gx + 2.6, gy + 5.5, p["grip"])
    for y in range(int(gy - 5), int(gy + 5), 2): cv.line([(gx - 2.6, y), (gx + 2.6, y + 1)], 0.5, tone(p["grip"], 0.2))
    cv.rect(gx - 3.2, gy - 6.5, gx + 3.2, gy - 5.3, p["metal2"]); cv.rect(gx - 3.2, gy + 5.3, gx + 3.2, gy + 6.5, p["metal2"])
    cv.gem(gx - 3.5, gy, 2.2, p["gem"])
    # 속성 날개/장식 (몸체 바깥쪽)
    if el in ("wind", "light"):
        for a, sd in ((158, 1), (202, -1)):
            x, y = pt(a, R + 1.5); wing(cv, x, y, -1, 8, p["metal"], p["metal2"], n=3, up=(sd < 0))
    elif el == "fire":
        for a in (152, 166, 194, 208):
            x, y = pt(a, R + 1.5); cv.poly([(x, y - 1.5), (x - 5, y), (x, y + 1.5)], p["glow"], glow=True)
    elif el == "electric":
        for a in (160, 200):
            x, y = pt(a, R + 1); cv.line([(x, y), (x - 3, y + 1.5), (x - 1.5, y + 3), (x - 5, y + 5)], 0.9, p["glow"], glow=True)
    elif el == "dark":
        for a in (150, 162, 198, 210):
            x, y = pt(a, R + 1.5); cv.poly([(x, y - 1), (x - 4, y + (1 if a < 180 else -1) * 1.5), (x, y + 1)], p["metal2"])
    elif el == "grass":
        for a in (156, 204):
            x, y = pt(a, R + 1.2); cv.poly([(x, y), (x - 4, y - 2), (x - 6, y + 1), (x - 2, y + 2)], p["glow"], glow=True)
    else:
        for a in (155, 205):
            x, y = pt(a, R + 1.5); cv.circ(x - 1.5, y, 1.4, p["glow"], glow=True)

def base_design(cv, el, wt):
    p = P_(el); xc = 32
    if wt == "sword":
        grip(cv, el, xc, 51, 58, 2.6); blade(cv, el, xc, 48, 3, 4.4); guard(cv, el, xc, 49.5, 22, 1.3)
    elif wt == "greatsword":
        grip(cv, el, xc, 51, 59, 3.2); blade(cv, el, xc, 47, 1, 7.2, tip=0.16); guard(cv, el, xc, 48.5, 32, 1.8)
        cv.gem(xc, 40, 2.4, p["gem"]); cv.gem(xc, 33, 1.6, p["gem"])
    elif wt == "dagger":
        grip(cv, el, xc, 49, 57, 2.4); blade(cv, el, xc, 46, 14, 4.0, tip=0.3); guard(cv, el, xc, 47.5, 16, 1.1)
    elif wt == "staff":
        cv.rect(xc - 1.7, 24, xc + 1.7, 62, p["grip"])
        cv.rect(xc - 1.7, 24, xc - 1.1, 62, tone(p["grip"], 0.15))
        for k in range(14):                                             # 나선 감기
            y = 25 + k * 2.6
            cv.line([(xc - 1.7, y), (xc + 1.7, y + 1.6)], 0.5, p["metal2"])
        for y in (27, 44, 58): cv.rect(xc - 2.6, y, xc + 2.6, y + 1.5, p["metal2"]); cv.gem(xc, y + 0.75, 0.9, p["gem"])
        for sd, yy in ((-1, 30), (1, 34)):                              # 매달린 장식
            cv.line([(xc + sd * 2.4, 27.5), (xc + sd * 4.2, yy)], 0.35, p["metal2"])
            cv.gem(xc + sd * 4.2, yy + 1.2, 1.1, p["gem"])
        for sd in (-1, 1): cv.gem(xc + sd * 11, 18, 1.5, p["glow"])     # 떠다니는 수정
        staff_head(cv, el, xc, 13)
    elif wt == "hammer":
        cv.rect(xc - 1.4, 22, xc + 1.4, 61, p["grip"])
        for y in range(40, 60, 3): cv.line([(xc - 1.4, y), (xc + 1.4, y + 1)], 0.6, tone(p["grip"], 0.2))
        cv.rect(xc - 2, 60, xc + 2, 62.5, p["metal2"])
        hammer_head(cv, el, xc, 14)
    elif wt == "shield":
        shield(cv, el)
    elif wt == "scythe":
        scythe(cv, el)
    elif wt == "bow":
        bow(cv, el)

def book(cv, el, tier=1):
    """마법서: 펼쳐진 책 (표지·페이지·책갈피·속성 문장)"""
    p = P_(el); xc = 32
    cv.poly([(xc - 24, 22), (xc, 26), (xc + 24, 22), (xc + 26, 46), (xc, 50), (xc - 26, 46)], p["dark"])               # 표지 그림자
    cv.poly([(xc - 23, 20), (xc, 24), (xc + 23, 20), (xc + 25, 44), (xc, 48), (xc - 25, 44)], p["metal"])
    for sd in (-1, 1):                                                                                          # 펼친 페이지
        cv.poly([(xc, 24), (xc + sd * 21, 19.5), (xc + sd * 22.5, 41), (xc, 45)], (246, 236, 214))
        cv.poly([(xc, 24), (xc + sd * 6, 23), (xc + sd * 7, 44), (xc, 45)], (226, 212, 186))
        for k in range(5):
            y0 = 25 + k * 3.6
            cv.line([(xc + sd * 9, y0 + 0.5), (xc + sd * 19, y0 - 0.6)], 0.5, (176, 160, 140))
    cv.line([(xc, 24), (xc, 45)], 0.8, p["dark"])
    cv.rect(xc - 1.2, 44, xc + 1.2, 54, p["gem"])                                                                  # 책갈피
    cv.poly([(xc - 1.2, 54), (xc + 1.2, 54), (xc, 52)], p["dark"])
    for (x, y) in ((xc - 24, 21), (xc + 24, 21), (xc - 25.5, 45), (xc + 25.5, 45)): cv.gem(x, y, 1.6, p["metal2"], glow=False)
    # 페이지 위에 떠오른 속성 문장
    cv.arc(xc + 13, 32, 5, 0, 360, 0.7, p["glow"], glow=True, steps=30)
    cv.gem(xc + 13, 32, 2.4, p["gem"])
    cv.arc(xc - 13, 32, 4, 200, 520, 0.6, tone(p["blade2"], -0.1), steps=24)
    if tier >= 2:
        for k in range(3): cv.rect(xc - 18 + k * 4, 13 - k * 3, xc - 15.5 + k * 4, 15.5 - k * 3, p["glow"], glow=True)   # 떠오르는 룬
    if tier >= 3:
        for sd in (-1, 1): wing(cv, xc + sd * 25, 30, sd, 9, p["metal2"], tone(p["metal"], 0.1), n=3)
    if tier >= 4:
        cv.arc(xc, 18, 10, 200, 340, 1.0, p["glow"], glow=True, steps=30)
        for k in range(5): cv.circ(xc - 12 + k * 6, 9 + (k % 2) * 2, 1.0, p["glow"], glow=True)

def tier_decor(cv, el, wt, tier):
    """단계가 오를수록 붙는 장식: 2단계 보석·룬, 3단계 날개·빛줄, 4단계 후광"""
    p = P_(el); xc = 32
    blade_types = ("sword", "greatsword", "dagger")
    if tier >= 2:
        if wt in blade_types:
            gy = {"sword": 49.5, "greatsword": 48.5, "dagger": 47.5}[wt]
            cv.gem(xc, gy, 3.2, p["gem"]); cv.circ(xc - 6, gy + 2.5, 1.0, p["glow"], glow=True); cv.circ(xc + 6, gy + 2.5, 1.0, p["glow"], glow=True)
        elif wt in ("staff", "hammer", "scythe"):
            for y in (36, 52): cv.gem(xc + (6 if wt == "scythe" else 0), y, 1.8, p["gem"])
        elif wt == "bow":
            cv.gem(15, 32, 3.0, p["gem"])
        elif wt == "shield":
            for k in range(6):
                a = math.radians(k * 60); cv.circ(xc + math.cos(a) * 12, 32 + math.sin(a) * 12, 1.2, p["metal2"])
    if tier >= 3:
        if wt in blade_types:
            gy = {"sword": 49.5, "greatsword": 48.5, "dagger": 47.5}[wt]
            for sd in (-1, 1): wing(cv, xc + sd * 4, gy, sd, 10 if wt != "dagger" else 7, p["metal2"], tone(p["metal"], 0.1), n=3)
        elif wt == "staff":
            cv.arc(xc, 13, 12, 0, 360, 0.9, p["glow"], glow=True, steps=48)
        elif wt == "hammer":
            for sd in (-1, 1): cv.poly([(xc + sd * 14, 9), (xc + sd * 20, 3), (xc + sd * 17, 11)], p["metal2"])
        elif wt == "scythe":
            cv.line([(42, 22), (50, 30), (52, 38)], 1.2, p["glow"], glow=True)
        elif wt == "bow":
            for sd in (-1, 1): wing(cv, 22, 32 + sd * 20, -1, 7, p["metal2"], tone(p["metal"], 0.1), n=3, up=(sd < 0))
        elif wt == "shield":
            for sd in (-1, 1): wing(cv, xc + sd * 22, 22, sd, 8, p["metal2"], tone(p["metal"], 0.1), n=3)
    if tier >= 4:
        if wt in ("staff", "hammer"):
            cv.arc(xc, 13, 16, 190, 350, 1.2, p["glow"], glow=True, steps=40)
        elif wt in blade_types:
            cv.arc(xc, 49, 9, 180, 360, 0.9, p["glow"], glow=True, steps=30)

def wood_design(wt):
    """나무 무기: 손으로 깎은 나무 + 노끈 감개, 보석·빛 없음"""
    cv = Canvas(0.95); el = "wood"; p = P_(el); xc = 32
    rope = (196, 172, 120)
    def wrap(y0, y1, w):
        y = y0
        while y < y1: cv.line([(xc - w, y), (xc + w, y + 1.0)], 0.7, rope); y += 1.5
    if wt in ("sword", "greatsword", "dagger"):
        top, bot, w = {"sword": (6, 48, 3.8), "greatsword": (3, 46, 6.2), "dagger": (16, 46, 3.2)}[wt]
        cv.poly([(xc - w, bot), (xc - w, top + w * 1.6), (xc, top), (xc + w, top + w * 1.6), (xc + w, bot)], p["blade"])
        cv.poly([(xc, top), (xc + w, top + w * 1.6), (xc + w, bot), (xc, bot)], p["blade2"])
        for k in range(3): cv.line([(xc - w * 0.5, top + 10 + k * 9), (xc - w * 0.3, top + 15 + k * 9)], 0.5, tone(p["blade2"], -0.1))
        cv.rect(xc - w - 4, bot, xc + w + 4, bot + 2.6, p["metal"]); cv.circ(xc - w - 2.5, bot + 1.3, 0.6, (90, 90, 96)); cv.circ(xc + w + 2.5, bot + 1.3, 0.6, (90, 90, 96))
        cv.rect(xc - 1.6, bot + 2.6, xc + 1.6, bot + 12, p["grip"]); wrap(bot + 3, bot + 11, 1.6)
        cv.circ(xc, bot + 13, 2.2, p["metal"])
    elif wt == "staff":
        cv.line([(xc + 1, 62), (xc - 1, 40), (xc + 1, 22), (xc - 1, 12)], 3.0, p["grip"])
        cv.line([(xc, 60), (xc - 2, 40), (xc, 22)], 0.7, tone(p["grip"], 0.15))
        cv.line([(xc - 1, 12), (xc - 7, 6), (xc - 8, 1)], 2.0, p["grip"]); cv.line([(xc - 1, 12), (xc + 6, 5), (xc + 5, 1)], 2.0, p["grip"])
        cv.circ(xc, 8, 3.6, p["gem"]); cv.circ(xc - 1, 7, 1.2, tone(p["gem"], 0.3))
        wrap(30, 38, 2)
    elif wt == "hammer":
        cv.rect(xc - 1.6, 20, xc + 1.6, 62, p["grip"]); wrap(48, 58, 1.6)
        cv.rect(xc - 12, 8, xc + 12, 22, p["blade2"]); cv.rect(xc - 12, 8, xc + 12, 12, p["blade"])
        for x in (xc - 12, xc + 9): cv.rect(x, 7, x + 3, 23, p["metal"])
        for k in range(3): cv.line([(xc - 8 + k * 7, 14), (xc - 6 + k * 7, 20)], 0.5, tone(p["blade2"], -0.12))
    elif wt == "shield":
        cv.circ(xc, 32, 23, p["metal"]); cv.circ(xc, 32, 21, p["blade"])
        for k in range(-3, 4):
            x = xc + k * 6
            cv.line([(x, 12), (x, 52)], 0.6, tone(p["blade2"], -0.05))
        cv.rect(xc - 21, 20, xc + 21, 23, p["metal"]); cv.rect(xc - 21, 41, xc + 21, 44, p["metal"])
        cv.circ(xc, 32, 5, p["metal"]); cv.circ(xc, 32, 3, p["metal2"])
    elif wt == "book":
        cv.poly([(xc - 23, 20), (xc, 24), (xc + 23, 20), (xc + 25, 44), (xc, 48), (xc - 25, 44)], p["grip"])
        for sd in (-1, 1):
            cv.poly([(xc, 24), (xc + sd * 21, 19.5), (xc + sd * 22.5, 41), (xc, 45)], (236, 222, 192))
            for k in range(5): cv.line([(xc + sd * 8, 26 + k * 3.6), (xc + sd * 19, 25 + k * 3.6)], 0.5, (170, 150, 120))
        cv.line([(xc, 24), (xc, 45)], 0.8, p["dark"]); cv.rect(xc - 1, 44, xc + 1, 53, (170, 60, 50))
    elif wt == "scythe":
        cv.line([(38, 8), (39, 30), (38, 50), (37, 62)], 3.0, p["grip"]); wrap(30, 40, 1.6)
        cv.poly([(37, 10), (20, 8), (8, 16), (4, 26), (12, 18), (24, 14), (37, 16)], p["blade2"])
        cv.poly([(37, 10), (20, 8), (8, 16), (14, 13), (26, 11)], p["blade"])
    elif wt == "bow":
        pts = [(46 + math.cos(math.radians(a)) * 30, 32 + math.sin(math.radians(a)) * 30) for a in range(140, 222, 4)]
        cv.line(pts, 3.2, p["grip"]); cv.line(pts, 1.0, tone(p["grip"], 0.2))
        cv.line([pts[0], pts[-1]], 0.6, (232, 226, 210))
        gx, gy = 16, 32
        cv.rect(gx - 2.4, gy - 5, gx + 2.4, gy + 5, p["blade2"]); wrap(gy - 4, gy + 4, 2.4)
    return cv.finish()

def design(el, wt, tier=1):
    if el == "wood": return wood_design(wt)
    cv = Canvas({1: 0.9, 2: 0.95, 3: 1.0, 4: 1.0}[tier]); p = P_(el)
    if wt == "book": book(cv, el, tier)
    else:
        base_design(cv, el, wt)
        tier_decor(cv, el, wt, tier)
    img, glow = cv.finish()
    if tier >= 4: img, glow = aura(img, glow, p["glow"], 1)
    return img, glow

