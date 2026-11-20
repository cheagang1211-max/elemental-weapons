"""무기 스프라이트 그리기 (64x64 픽셀, 4배 슈퍼샘플 벡터 드로잉) — 위가 칼끝, 아래가 손잡이"""
import math, colorsys
from PIL import Image, ImageDraw

SS = 4
N = 64

def hexc(h): return ((h >> 16) & 255, (h >> 8) & 255, h & 255)
def tone(c, dl, dh=0.0, ds=0.0):
    r, g, b = [v / 255 for v in c]; h, l, s = colorsys.rgb_to_hls(r, g, b)
    return tuple(int(max(0, min(1, v)) * 255) for v in colorsys.hls_to_rgb((h + dh) % 1, max(0, min(1, l + dl)), max(0, min(1, s + ds))))

PAL = {
 "electric": dict(blade=0x7FD8FF, blade2=0x2A6AC0, metal=0xC8D4E8, metal2=0xF0C040, dark=0x16203A, glow=0x9FF8FF, gem=0x60E8FF, grip=0x20284A),
 "fire":     dict(blade=0xFF8A3A, blade2=0xB02010, metal=0x3A2420, metal2=0xF0A030, dark=0x1A0E0C, glow=0xFFD060, gem=0xFF5020, grip=0x4A1A10),
 "water":    dict(blade=0x6FC8F0, blade2=0x1A5A9A, metal=0xE0E8F0, metal2=0x40C8D8, dark=0x102A40, glow=0xB0F4FF, gem=0x40D8FF, grip=0x1A4A6A),
 "grass":    dict(blade=0x7CD86A, blade2=0x2A7A30, metal=0x6A4A2A, metal2=0xC8D870, dark=0x1E140A, glow=0xFFB0D8, gem=0xFF70B0, grip=0x3A2814),
 "wind":     dict(blade=0xE8FFF8, blade2=0x6ABAB0, metal=0xF4F8F8, metal2=0x7FE0D0, dark=0x1E3A3A, glow=0xD0FFF4, gem=0x80FFE8, grip=0x2A5A58),
 "dark":     dict(blade=0xB070FF, blade2=0x3A1070, metal=0x2A2234, metal2=0x7A4AB0, dark=0x0A0610, glow=0xE0A0FF, gem=0xC060FF, grip=0x140A1E),
 "light":    dict(blade=0xFFF4D0, blade2=0xD8A840, metal=0xF0C040, metal2=0xFFFFFF, dark=0x5A4010, glow=0xFFFFC0, gem=0xFFE070, grip=0xE8D8B0),
}

class Canvas:
    def __init__(self):
        self.col = Image.new("RGBA", (N * SS, N * SS), (0, 0, 0, 0))
        self.glw = Image.new("L", (N * SS, N * SS), 0)
        self.d = ImageDraw.Draw(self.col); self.g = ImageDraw.Draw(self.glw)
    def P(self, pts): return [(x * SS, y * SS) for x, y in pts]
    def poly(self, pts, c, glow=False):
        self.d.polygon(self.P(pts), fill=c + (255,))
        if glow: self.g.polygon(self.P(pts), fill=255)
        else: self.g.polygon(self.P(pts), fill=0)
    def rect(self, x0, y0, x1, y1, c, glow=False): self.poly([(x0, y0), (x1, y0), (x1, y1), (x0, y1)], c, glow)
    def circ(self, cx, cy, r, c, glow=False):
        b = [(cx - r) * SS, (cy - r) * SS, (cx + r) * SS, (cy + r) * SS]
        self.d.ellipse(b, fill=c + (255,)); self.g.ellipse(b, fill=255 if glow else 0)
    def line(self, pts, w, c, glow=False):
        self.d.line(self.P(pts), fill=c + (255,), width=int(w * SS), joint="curve")
        self.g.line(self.P(pts), fill=255 if glow else 0, width=int(w * SS))
    def arc(self, cx, cy, r, a0, a1, w, c, glow=False, steps=24):
        pts = [(cx + math.cos(math.radians(a0 + (a1 - a0) * i / steps)) * r, cy + math.sin(math.radians(a0 + (a1 - a0) * i / steps)) * r) for i in range(steps + 1)]
        self.line(pts, w, c, glow)
    def gem(self, cx, cy, r, base, glow=True):
        self.poly([(cx, cy - r), (cx + r, cy), (cx, cy + r), (cx - r, cy)], tone(base, -0.18), glow)
        self.poly([(cx, cy - r * 0.7), (cx + r * 0.7, cy), (cx, cy + r * 0.25), (cx - r * 0.7, cy)], base, glow)
        self.circ(cx - r * 0.25, cy - r * 0.3, max(0.45, r * 0.22), (255, 255, 255), glow)

    def finish(self, outline=True):
        """슈퍼샘플 → 64x64 픽셀, 외곽선, 빛 방향 음영"""
        small = self.col.resize((N, N), Image.BOX)
        gsmall = self.glw.resize((N, N), Image.BOX)
        px = small.load(); gp = gsmall.load()
        out = Image.new("RGBA", (N, N), (0, 0, 0, 0)); glow = [[False] * N for _ in range(N)]
        o = out.load()
        for y in range(N):
            for x in range(N):
                r, g, b, a = px[x, y]
                if a >= 110:
                    k = 255 / max(1, a)
                    o[x, y] = (min(255, int(r * k)), min(255, int(g * k)), min(255, int(b * k)), 255)
                    glow[y][x] = gp[x, y] > 128
        if outline:
            solid = [[o[x, y][3] > 0 for x in range(N)] for y in range(N)]
            for y in range(N):
                for x in range(N):
                    if not solid[y][x]:
                        continue
                    edge = any(not (0 <= x + dx < N and 0 <= y + dy < N and solid[y + dy][x + dx]) for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)))
                    if edge and not glow[y][x]:
                        c = o[x, y]; lit = any(not (0 <= x + dx < N and 0 <= y + dy < N and solid[y + dy][x + dx]) for dx, dy in ((-1, 0), (0, -1)))
                        o[x, y] = tone(c[:3], 0.12 if lit else -0.22) + (255,)
            # 바깥 테두리 (어두운 1px 윤곽)
            add = []
            for y in range(N):
                for x in range(N):
                    if solid[y][x]: continue
                    nb = [(x + dx, y + dy) for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)) if 0 <= x + dx < N and 0 <= y + dy < N and solid[y + dy][x + dx]]
                    if nb: add.append((x, y, tone(o[nb[0][0], nb[0][1]][:3], -0.38, 0, -0.1)))
            for x, y, c in add: o[x, y] = c + (255,)
        return out, glow
