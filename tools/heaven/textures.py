"""천상의 원환문·대천사 텍스처 (256×256 아틀라스, 64×64 칸마다 재질 하나)"""
import os, random
from PIL import Image

OUT = os.path.join(os.path.dirname(__file__), "..", "..", "src/main/resources/assets/elementalweapons/textures/entity")
random.seed(11)
S = 256
base = Image.new("RGBA", (S, S), (0, 0, 0, 0)); bp = base.load()
glow = Image.new("RGBA", (S, S), (0, 0, 0, 0)); gp = glow.load()

def region(u, v, fn, g=None):
    for y in range(64):
        for x in range(64):
            bp[u + x, v + y] = fn(x, y)
            if g: gp[u + x, v + y] = g(x, y)

def clamp(c): return tuple(max(0, min(255, int(k))) for k in c)
def noise(a): return random.uniform(-a, a)

def marble(x, y):   # 흰 대리석 + 옅은 회색 결
    vein = 18 if (x * 3 + y * 5 + int(8 * __import__("math").sin(y * 0.3))) % 23 == 0 else 0
    edge = 14 if x % 8 == 0 or y % 8 == 0 else 0
    v = 236 + noise(6) - vein - edge
    return clamp((v, v, v + 4)) + (255,)
def gold(x, y):
    t = (y % 8) / 8
    hl = 40 if y % 8 == 1 else 0
    return clamp((214 + 30 * (1 - t) + hl + noise(6), 168 + 40 * (1 - t) + hl + noise(6), 70 + 30 * (1 - t) + hl)) + (255,)
def gold_glow(x, y): return (255, 220, 120, 150 if y % 8 in (1, 2) else 70)
def crystal(x, y):
    sp = 30 if (x + y) % 9 == 0 else 0
    return clamp((236 + sp, 240 + sp, 255, )) + (255,)
def crystal_glow(x, y): return (255, 246, 200, 200 if (x + y) % 9 == 0 else 120)
def light(x, y): return clamp((255, 248 - abs(x - 32) * 0.6, 214 - abs(y - 32) * 0.8)) + (255,)
def light_glow(x, y): return (255, 244, 200, 235)
def feather(x, y):   # 깃털: 가운데 깃대 + 결
    shaft = 30 if x % 4 == 1 else 0
    barb = 10 if (x + y) % 3 == 0 else 0
    v = 248 - shaft - barb + noise(4)
    return clamp((v, v, v + 2)) + (255,)
def feather_gold(x, y):
    if x % 4 == 0 or x % 4 == 3: return gold(x, y)
    return feather(x, y)
def feather_gold_glow(x, y): return (255, 220, 120, 110) if x % 4 in (0, 3) else (0, 0, 0, 0)
def armor(x, y):   # 은백색 판금 + 금 새김선
    if y % 16 == 0 or x % 16 == 0: return gold(x, y)
    v = 222 - (y % 16) * 2 + noise(5)
    return clamp((v, v + 2, v + 8)) + (255,)
def armor_glow(x, y): return (255, 220, 120, 90) if y % 16 == 0 or x % 16 == 0 else (0, 0, 0, 0)
def darkgold(x, y): return clamp((150 + noise(8), 110 + noise(8), 50)) + (255,)

region(0, 0, marble)
region(64, 0, gold, gold_glow)
region(128, 0, crystal, crystal_glow)
region(192, 0, light, light_glow)
region(0, 64, feather)
region(64, 64, feather_gold, feather_gold_glow)
region(128, 64, armor, armor_glow)
region(192, 64, darkgold)
for name in ("heaven_gate", "archangel"):
    base.save(f"{OUT}/{name}.png"); glow.save(f"{OUT}/{name}_glow.png")
print("ok")
