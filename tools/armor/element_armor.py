"""속성별 갑옷 색: 에테르·오리하르콘 갑옷 텍스처를 속성 색으로 다시 칠함 (판금 = 속성의 어두운 금속색, 발광 = 속성 빛)"""
import colorsys, json, os
from PIL import Image

ROOT = os.path.join(os.path.dirname(__file__), "..", "..", "src/main/resources/assets/elementalweapons")
T = ROOT + "/textures/entity"
# 판금 어두운색, 판금 밝은색, 발광색, 오리하르콘 테두리색
PAL = {
    "electric": ((24, 28, 46), (132, 142, 178), (255, 236, 90), (140, 232, 255)),
    "fire":     ((38, 10, 8), (175, 70, 45), (255, 128, 40), (240, 170, 60)),
    "water":    ((8, 26, 46), (70, 145, 195), (70, 195, 255), (120, 210, 230)),
    "grass":    ((14, 34, 18), (95, 155, 90), (255, 150, 200), (170, 220, 110)),
    "wind":     ((52, 64, 66), (222, 236, 232), (150, 255, 220), (210, 230, 235)),
    "dark":     None,
    "light":    ((86, 74, 44), (252, 244, 214), (255, 222, 100), (255, 215, 90)),
}

def mix(a, b, t):
    return tuple(int(round(a[i] + (b[i] - a[i]) * t)) for i in range(3))

def recolor(im, pal, tier):
    dark, light, glow, trim = pal
    im = im.convert("RGBA"); px = im.load()
    for y in range(im.height):
        for x in range(im.width):
            r, g, b, a = px[x, y]
            if a == 0: continue
            h, l, s = colorsys.rgb_to_hls(r / 255, g / 255, b / 255)
            if s > 0.45 and l > 0.25:
                # 강조색: 오리하르콘의 금색 테두리(주황~노랑) / 보석·발광(보라·주황)
                if tier == "orichalcum" and 0.08 < h < 0.18 and l < 0.6:
                    c = mix(tuple(v // 3 for v in trim), trim, min(1, l * 1.6))
                else:
                    c = mix(tuple(v // 2 for v in glow), glow, min(1, l * 1.4))
                    c = mix(c, (255, 255, 255), max(0, l - 0.75))
            else:
                c = mix(dark, light, min(1, l * 1.7))
            px[x, y] = (c[0], c[1], c[2], a)
    return im

def main():
    for tier in ("aether", "orichalcum"):
        for el, pal in PAL.items():
            name = f"{tier}_{el}"
            for sub in ("equipment/humanoid", "equipment/humanoid_leggings"):
                src = Image.open(f"{T}/{sub}/{tier}.png")
                (src.copy() if pal is None else recolor(src, pal, tier)).save(f"{T}/{sub}/{name}.png")
            d = Image.open(f"{T}/armor_deco/{tier}.png"); gl = Image.open(f"{T}/armor_deco/{tier}_glow.png")
            if pal is None:
                d.save(f"{T}/armor_deco/{name}.png"); gl.save(f"{T}/armor_deco/{name}_glow.png")
            else:
                recolor(d, pal, tier).save(f"{T}/armor_deco/{name}.png")
                recolor(gl, pal, tier).save(f"{T}/armor_deco/{name}_glow.png")
            json.dump({"layers": {"humanoid": [{"texture": f"elementalweapons:{name}"}],
                                  "humanoid_leggings": [{"texture": f"elementalweapons:{name}"}]}},
                      open(f"{ROOT}/equipment/{name}.json", "w"), indent="\t")
    print("ok")

if __name__ == "__main__":
    main()
