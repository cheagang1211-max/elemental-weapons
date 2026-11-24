"""직업별 갑옷 텍스처: 직업 복장(로브·사제복·은신복·사냥꾼옷·수의)의 평면 무늬 + 입체 장식 무늬 + 장비 JSON 생성"""
import json, os, random
from PIL import Image

ROOT = os.path.join(os.path.dirname(__file__), "..", "..", "src/main/resources/assets/elementalweapons")
T = ROOT + "/textures/entity"
ELS = ["electric", "fire", "water", "grass", "wind", "dark", "light", "neutral"]
# 속성 주색, 짙은색, 빛
EL = {
    "electric": ((58, 72, 160), (22, 26, 60), (255, 236, 90)),
    "fire":     ((176, 42, 30), (64, 14, 10), (255, 140, 40)),
    "water":    ((34, 98, 178), (10, 32, 70), (90, 210, 255)),
    "grass":    ((48, 128, 62), (16, 46, 22), (255, 160, 210)),
    "wind":     ((120, 196, 184), (40, 80, 78), (170, 255, 225)),
    "dark":     ((74, 34, 110), (24, 10, 38), (200, 90, 255)),
    "light":    ((236, 222, 176), (130, 108, 60), (255, 226, 110)),
    "neutral":  ((120, 120, 130), (44, 44, 52), (200, 220, 255)),
}
GOLD = (222, 176, 60)
CLASSES = ["mage", "warrior", "knight", "assassin", "support", "reaper", "ranger"]
CLOTH = {"mage", "assassin", "support", "reaper", "ranger"}

def mix(a, b, t): return tuple(int(round(a[i] + (b[i] - a[i]) * t)) for i in range(3))

def palette(cls, el):
    main, dark, glow = EL[el]
    if cls == "mage":     return dict(cloth=main, trim=GOLD, dark=dark, fur=mix(main, (255, 255, 255), 0.35), metal=GOLD, glow=glow)
    if cls == "support":  return dict(cloth=(238, 236, 226), trim=main, dark=mix((200, 200, 210), main, 0.2), fur=GOLD, metal=GOLD, glow=glow)
    if cls == "assassin": return dict(cloth=mix((28, 28, 34), main, 0.18), trim=main, dark=(14, 14, 18), fur=mix((40, 40, 46), main, 0.3), metal=(170, 176, 186), glow=glow)
    if cls == "reaper":   return dict(cloth=mix((22, 18, 28), main, 0.15), trim=mix(main, glow, 0.4), dark=(8, 6, 10), fur=(70, 64, 72), metal=(214, 206, 180), glow=glow)
    if cls == "ranger":   return dict(cloth=mix((92, 108, 52), main, 0.4), trim=mix((120, 80, 44), main, 0.2), dark=(70, 46, 26), fur=(150, 120, 80), metal=(160, 164, 170), glow=glow)
    if cls == "knight":   return dict(cloth=main, trim=GOLD, dark=dark, fur=(240, 240, 240), metal=(190, 196, 206), glow=glow)
    if cls == "warrior":  return dict(cloth=mix(main, (90, 40, 20), 0.35), trim=(150, 110, 60), dark=(60, 36, 20), fur=(122, 94, 66), metal=(150, 150, 156), glow=glow)

def shade(c, r, amt=14):
    return tuple(max(0, min(255, v + r)) for v in c)

def fill(px, x0, y0, w, h, c, folds=True, seed=0):
    rnd = random.Random(seed + x0 * 131 + y0 * 7)
    for y in range(y0, y0 + h):
        for x in range(x0, x0 + w):
            d = rnd.randint(-8, 8)
            if folds and (x - x0) % 4 == 3: d -= 14     # 옷 주름
            if folds and (x - x0) % 4 == 0: d += 6
            px[x, y] = shade(c, d) + (255,)

def deco_tex(cls, el):
    P = palette(cls, el)
    im = Image.new("RGBA", (128, 128), (0, 0, 0, 0)); g = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
    px, gp = im.load(), g.load()
    fill(px, 0, 0, 64, 32, P["cloth"])
    fill(px, 64, 0, 64, 32, P["trim"], folds=False)
    for y in range(0, 32, 3):     # 테두리 무늬
        for x in range(64, 128): px[x, y] = shade(P["trim"], 30) + (255,)
    fill(px, 0, 32, 64, 32, P["dark"])
    fill(px, 64, 32, 64, 32, P["fur"], folds=False)
    rnd = random.Random(7)
    for _ in range(500):          # 털 결
        x, y = rnd.randint(64, 127), rnd.randint(32, 63); px[x, y] = shade(P["fur"], rnd.choice([-30, 25])) + (255,)
    fill(px, 0, 64, 64, 32, P["metal"], folds=False)
    for x in range(0, 64, 5):
        for y in range(64, 96): px[x, y] = shade(P["metal"], 40) + (255,)
    fill(px, 64, 64, 64, 32, P["glow"], folds=False)
    for y in range(64, 96):
        for x in range(64, 128):
            px[x, y] = mix(P["glow"], (255, 255, 255), 0.25 if (x + y) % 5 == 0 else 0) + (255,)
            gp[x, y] = px[x, y]
    # 테두리 칸의 반짝이는 줄은 빛도 냄 (금빛 자수)
    for y in range(0, 32, 3):
        for x in range(64, 128):
            if cls in ("mage", "support", "reaper", "knight") and x % 2 == 0: gp[x, y] = mix(P["trim"], (255, 255, 255), 0.2) + (200,)
    return im, g

# ---- 평면 갑옷 (64x32 사람 갑옷 배치) ----
def face(px, x, y, w, h, c, seed=0, folds=True):
    fill(px, x, y, w, h, c, folds, seed)

def box(px, u, v, w, h, d, c, seed=0, folds=True):
    """상자 전개도 전체를 칠함 (위·아래·네 옆면)"""
    face(px, u + d, v, w, d, c, seed, False); face(px, u + d + w, v, w, d, c, seed, False)
    face(px, u, v + d, 2 * (w + d), h, c, seed, folds)

def hline(px, x0, x1, y, c):
    for x in range(x0, x1): px[x, y] = c + (255,)
def vline(px, x, y0, y1, c):
    for y in range(y0, y1): px[x, y] = c + (255,)
def clear(px, x0, y0, w, h):
    for y in range(y0, y0 + h):
        for x in range(x0, x0 + w): px[x, y] = (0, 0, 0, 0)

def flat(cls, el):
    P = palette(cls, el)
    a = Image.new("RGBA", (64, 32), (0, 0, 0, 0)); b = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    pa, pb = a.load(), b.load()
    c, t, dk = P["cloth"], P["trim"], P["dark"]
    hood = cls in ("assassin", "reaper", "ranger")
    # 머리: 두건(얼굴 부분은 비움) / 마법사·서포터는 머리띠만
    box(pa, 0, 0, 8, 8, 8, c, 1)
    clear(pa, 8, 8, 8, 8)                         # 앞면 비움
    if hood:
        hline(pa, 8, 16, 8, t if cls != "reaper" else dk); hline(pa, 8, 16, 9, c)
        vline(pa, 8, 8, 16, c); vline(pa, 15, 8, 16, c)
        if cls == "reaper":
            for y in range(10, 16): hline(pa, 9, 15, y, (10, 8, 12))
            pa[10, 12] = P["glow"] + (255,); pa[13, 12] = P["glow"] + (255,)     # 두건 속 눈빛
        if cls == "assassin":
            for y in range(12, 16): hline(pa, 8, 16, y, dk)                       # 복면
            hline(pa, 8, 16, 12, t)
    else:
        clear(pa, 0, 0, 32, 8); clear(pa, 0, 8, 8, 8); clear(pa, 16, 8, 16, 8)
        # 머리띠 (앞·옆·뒤 한 줄)
        for x in range(0, 32): pa[x, 10] = (GOLD if cls == "mage" else t) + (255,)
        pa[11, 10] = pa[12, 10] = P["glow"] + (255,)
    # 몸통 (상의)
    box(pa, 16, 16, 8, 12, 4, c, 2)
    if cls in ("mage", "support"):
        vline(pa, 23, 20, 32, t); vline(pa, 24, 20, 32, t)       # 앞여밈 자수
        hline(pa, 20, 28, 20, t); hline(pa, 20, 28, 27, P["metal"]); hline(pa, 20, 28, 28, dk)   # 깃·허리띠
        if cls == "support":
            for y in range(20, 32): pa[21, y] = P["trim"] + (255,); pa[26, y] = P["trim"] + (255,)   # 영대(스톨)
            pa[23, 23] = pa[24, 23] = pa[23, 22] = pa[24, 24] = P["glow"] + (255,)
    elif cls == "assassin":
        for y in range(20, 32): pa[20 + (y - 20) % 8, y] = dk + (255,)   # 사선 띠
        hline(pa, 20, 28, 27, dk); pa[24, 27] = P["metal"] + (255,)
    elif cls == "reaper":
        for y in range(20, 32, 2): hline(pa, 20, 28, y, shade(c, -8))
        vline(pa, 23, 20, 32, t)
    elif cls == "ranger":
        for y in range(20, 32): pa[20 + (y - 20) * 8 // 12, y] = P["trim"] + (255,)   # 화살통 끈
        hline(pa, 20, 28, 27, P["trim"]); pa[24, 27] = P["metal"] + (255,)
    # 팔 (소매)
    box(pa, 40, 16, 4, 12, 4, c, 3)
    for x in range(40, 56):
        pa[x, 29] = t + (255,)
        if cls in ("assassin", "ranger"):
            for y in range(26, 32): pa[x, y] = (P["trim"] if cls == "ranger" else dk) + (255,)   # 팔 보호대
    # 신발 (다리 아래쪽)
    box(pa, 0, 16, 4, 12, 4, dk, 4, False)
    for y in range(20, 27): clear(pa, 0, y, 16, 1)
    clear(pa, 4, 16, 4, 4)
    for x in range(0, 16): pa[x, 27] = t + (255,)
    # 하의(레깅스 층): 허리 + 다리
    box(pb, 16, 16, 8, 12, 4, c, 5); clear(pb, 16, 16, 24, 4)
    for y in range(20, 26): clear(pb, 16, y, 24, 1)
    hline(pb, 16, 40, 26, t)
    box(pb, 0, 16, 4, 12, 4, c if cls in ("mage", "support", "reaper") else shade(c, -18), 6)
    if cls in ("mage", "support", "reaper"):
        for x in range(0, 16): pb[x, 31] = t + (255,)     # 로브 자락 끝단
    else:
        for x in range(0, 16): pb[x, 24] = P["dark"] + (255,)   # 무릎
    return a, b

def main():
    os.makedirs(f"{T}/armor_deco/class", exist_ok=True)
    for cls in CLASSES:
        for el in ELS:
            d, g = deco_tex(cls, el)
            d.save(f"{T}/armor_deco/class/{cls}_{el}.png"); g.save(f"{T}/armor_deco/class/{cls}_{el}_glow.png")
            if cls in CLOTH:
                a, b = flat(cls, el)
                a.save(f"{T}/equipment/humanoid/cls_{cls}_{el}.png"); b.save(f"{T}/equipment/humanoid_leggings/cls_{cls}_{el}.png")
            for tier in ("aether", "orichalcum"):
                tex = f"cls_{cls}_{el}" if cls in CLOTH else (tier if el == "neutral" else f"{tier}_{el}")
                json.dump({"layers": {"humanoid": [{"texture": f"elementalweapons:{tex}"}],
                                      "humanoid_leggings": [{"texture": f"elementalweapons:{tex}"}]}},
                          open(f"{ROOT}/equipment/{tier}_{cls}_{el}.json", "w"), indent="\t")
    # 직업 없이 속성도 없을 때를 위한 기본 이름은 이미 있음 (aether.json)
    print("ok")

if __name__ == "__main__":
    main()
