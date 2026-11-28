"""무기 v5: 픽셀아트 스프라이트(64x64)를 입체로 압출 (가장자리는 얇고 가운데는 두꺼운 베벨, 발광 픽셀은 빛나는 요소)
사용법: python3 tools/weapons/weapons5.py <프로젝트 루트>"""
import sys, os, json, math
sys.path.insert(0, os.path.dirname(__file__))
import weapons3d as W3
from sprite_designs import design
from PIL import Image
ROOT = sys.argv[1]; NS = "elementalweapons"
A = f"{ROOT}/src/main/resources/assets/{NS}"
from sprite_draw import N as SPR_N
U = 32 / SPR_N      # 스프라이트 전체 = 32 모델 단위
LEVELS = [(0, 1.0), (3, 1.9), (6, 2.8)]   # (가장자리에서 거리, 두께)

def dist_map(solid):
    N = len(solid); INF = 99
    d = [[0 if not solid[y][x] else INF for x in range(N)] for y in range(N)]
    for y in range(N):
        for x in range(N):
            if solid[y][x] and (x in (0, N - 1) or y in (0, N - 1)): d[y][x] = 1
    for _ in range(2):
        for y in range(N):
            for x in range(N):
                if d[y][x]:
                    for dx, dy in ((-1, 0), (0, -1), (-1, -1), (1, -1)):
                        xx, yy = x + dx, y + dy
                        v = d[yy][xx] + 1 if 0 <= xx < N and 0 <= yy < N else 1
                        d[y][x] = min(d[y][x], v)
        for y in range(N - 1, -1, -1):
            for x in range(N - 1, -1, -1):
                if d[y][x]:
                    for dx, dy in ((1, 0), (0, 1), (1, 1), (-1, 1)):
                        xx, yy = x + dx, y + dy
                        v = d[yy][xx] + 1 if 0 <= xx < N and 0 <= yy < N else 1
                        d[y][x] = min(d[y][x], v)
    return d

def rects(mask):
    """마스크를 가로 줄 단위 → 같은 폭이면 세로로 합친 직사각형 목록"""
    N = len(mask); out = []; open_ = {}
    for y in range(N + 1):
        runs = set()
        if y < N:
            x = 0
            while x < N:
                if mask[y][x]:
                    x0 = x
                    while x < N and mask[y][x]: x += 1
                    runs.add((x0, x))
                else: x += 1
        for r in list(open_.keys()):
            if r not in runs: out.append((r[0], open_.pop(r), r[1], y))
        for r in runs:
            if r not in open_: open_[r] = y
    return out

def extrude(img, glow, tex_id):
    N = img.width; px = img.load()
    solid = [[px[x, y][3] > 0 for x in range(N)] for y in range(N)]
    d = dist_map(solid)
    s = 16 / N
    els = []
    def el(x0, y0, x1, y1, t, emissive):
        X0, X1 = 8 + (x0 - N / 2) * U, 8 + (x1 - N / 2) * U
        Y0, Y1 = (N - y1) * U, (N - y0) * U
        f = {"north": {"uv": [x0 * s, y0 * s, x1 * s, y1 * s], "texture": "#t"},
             "south": {"uv": [x1 * s, y0 * s, x0 * s, y1 * s], "texture": "#t"},
             "east": {"uv": [(x1 - 1) * s, y0 * s, x1 * s, y1 * s], "texture": "#t"},
             "west": {"uv": [x0 * s, y0 * s, (x0 + 1) * s, y1 * s], "texture": "#t"},
             "up": {"uv": [x0 * s, y0 * s, x1 * s, (y0 + 1) * s], "texture": "#t"},
             "down": {"uv": [x0 * s, (y1 - 1) * s, x1 * s, y1 * s], "texture": "#t"}}
        e = {"from": [round(X0, 3), round(Y0, 3), round(8 - t / 2, 3)], "to": [round(X1, 3), round(Y1, 3), round(8 + t / 2, 3)], "faces": f}
        if emissive: e["light_emission"] = 15
        els.append(e)
    for k, (dd, t) in enumerate(LEVELS):
        mask = [[solid[y][x] and d[y][x] > dd and not glow[y][x] for x in range(N)] for y in range(N)]
        for x0, y0, x1, y1 in rects(mask): el(x0, y0, x1, y1, t, False)
    gmask = [[solid[y][x] and glow[y][x] for x in range(N)] for y in range(N)]
    for x0, y0, x1, y1 in rects(gmask):
        t = max(LEVELS[i][1] for i in range(len(LEVELS)) if d[y0][x0] > LEVELS[i][0]) + 0.2
        el(x0, y0, x1, y1, t, True)
    return els

DISP = {
    "dagger":     {"thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [0, 0, 1], "scale": [0.75, 0.75, 0.75]},
                   "firstperson_righthand": {"rotation": [0, 90, 20], "translation": [1, 2, 0], "scale": [0.45, 0.45, 0.45]}},
    "sword":      {"thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [0, 1.5, 1], "scale": [0.9, 0.9, 0.9]},
                   "firstperson_righthand": {"rotation": [0, 90, 20], "translation": [1, 2, 0], "scale": [0.5, 0.5, 0.5]}},
    "greatsword": {"thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [0, 3, 1], "scale": [1.05, 1.05, 1.05]},
                   "firstperson_righthand": {"rotation": [0, 90, 20], "translation": [1, 1, 0], "scale": [0.45, 0.45, 0.45]}},
    "staff":      {"thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [0, 0, 1], "scale": [1.25, 1.25, 1.25]},
                   "firstperson_righthand": {"rotation": [0, 90, 15], "translation": [2, 1, 0], "scale": [0.42, 0.42, 0.42]}},
    "hammer":     {"thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [0, 0, 1], "scale": [1.0, 1.0, 1.0]},
                   "firstperson_righthand": {"rotation": [0, 90, 20], "translation": [1, 0, 0], "scale": [0.42, 0.42, 0.42]}},
    "shield":     {"thirdperson_righthand": {"rotation": [0, 180, 0], "translation": [0, -2, -2], "scale": [0.7, 0.7, 0.7]},
                   "firstperson_righthand": {"rotation": [0, 180, 5], "translation": [0, -3, -2], "scale": [0.45, 0.45, 0.45]}},
    "book":       {"thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [0, 1, 1], "scale": [0.75, 0.75, 0.75]},
                   "firstperson_righthand": {"rotation": [0, 90, 15], "translation": [1, 2, 0], "scale": [0.45, 0.45, 0.45]}},
    "scythe":     {"thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [0, 2, 1], "scale": [1.3, 1.3, 1.3]},
                   "firstperson_righthand": {"rotation": [0, 90, 15], "translation": [2, 2, 0], "scale": [0.48, 0.48, 0.48]}},
    "bow":        {"thirdperson_righthand": {"rotation": [-80, 260, -40], "translation": [-1, -2, 2.5], "scale": [0.95, 0.95, 0.95]},
                   "firstperson_righthand": {"rotation": [0, -90, 25], "translation": [1.13, 3.2, 1.13], "scale": [0.68, 0.68, 0.68]}},
}
for v in DISP.values():
    v["thirdperson_lefthand"] = v["thirdperson_righthand"]; v["firstperson_lefthand"] = v["firstperson_righthand"]
    v["ground"] = {"rotation": [0, 0, 0], "translation": [0, 2, 0], "scale": [0.4, 0.4, 0.4]}
    v["head"] = {"rotation": [0, 0, 0], "translation": [0, 0, 0], "scale": [1, 1, 1]}

def build(name, img, glow, wt):
    es = extrude(img, glow, name)
    disp = json.loads(json.dumps(DISP[wt]))
    disp["gui"] = W3.gui_transform(es, "front" if wt in ("shield", "book") else "diag")
    fx = json.loads(json.dumps(disp["gui"])); fx["scale"] = [round(v * 2.3, 3) for v in fx["scale"]]; fx["translation"] = [round(v * 2.3, 3) for v in fx["translation"]]
    disp["fixed"] = fx
    img.save(f"{A}/textures/item/3d/{name}.png")
    json.dump({"textures": {"t": f"{NS}:item/3d/{name}", "particle": f"{NS}:item/3d/{name}"}, "gui_light": "front", "display": disp, "elements": es},
              open(f"{A}/models/item/3d/{name}.json", "w"))
    return len(es)

def model_ref(name): return {"type": "minecraft:model", "model": f"{NS}:item/3d/{name}"}

if __name__ == "__main__":
    os.makedirs(f"{A}/textures/item/3d", exist_ok=True); os.makedirs(f"{A}/models/item/3d", exist_ok=True)
    total = 0
    ONLY = os.environ.get("ONLY")
    def tiers(el, wt, suf): return {"type": "minecraft:range_dispatch", "property": "minecraft:custom_model_data", "index": 0,
                              "entries": [{"threshold": t, "model": model_ref(f"{el}_{wt}_t{t}{suf}")} for t in (2, 3, 4)],
                              "fallback": model_ref(f"{el}_{wt}_t1{suf}")}
    def bow_item(idle, pulled):
        """활: 쓰는 중이면 당긴 시간(0.05/틱 → 20틱=1.0)에 따라 시위를 당긴 모델 0·1·2"""
        return {"type": "minecraft:condition", "property": "minecraft:using_item", "on_false": idle,
                "on_true": {"type": "minecraft:range_dispatch", "property": "minecraft:use_duration", "scale": 0.05,
                            "entries": [{"threshold": 0.4, "model": pulled(1)}, {"threshold": 0.85, "model": pulled(2)}], "fallback": pulled(0)}}
    for el in W3.EL:
        for wt in DISP:
            if ONLY and wt != ONLY: continue
            if wt == "bow":
                for tier in (1, 2, 3, 4):
                    for k in (0, 1, 2):
                        img, glow = design(el, wt, tier, k)
                        total += build(f"{el}_{wt}_t{tier}_pull{k}", img, glow, wt)
            for tier in (1, 2, 3, 4):
                img, glow = design(el, wt, tier)
                total += build(f"{el}_{wt}_t{tier}", img, glow, wt)
            # 레벨 단계(custom_model_data 0번)에 따라 모양이 바뀜: 1=10~29, 2=30~59, 3=60~99, 4=100+
            item = {"model": {"type": "minecraft:range_dispatch", "property": "minecraft:custom_model_data", "index": 0,
                              "entries": [{"threshold": t, "model": model_ref(f"{el}_{wt}_t{t}")} for t in (2, 3, 4)],
                              "fallback": model_ref(f"{el}_{wt}_t1")}}
            if wt == "bow": item = {"model": bow_item(item["model"], lambda k, el=el, wt=wt: tiers(el, wt, f"_pull{k}"))}
            json.dump(item, open(f"{A}/items/{el}_{wt}.json", "w"), indent="\t")
    for wt in DISP:
        if ONLY and wt != ONLY: continue
        img, glow = design("wood", wt)
        total += build(f"wooden_{wt}", img, glow, wt)
        m = model_ref(f"wooden_{wt}")
        if wt == "bow":
            for k in (0, 1, 2):
                img, glow = design("wood", wt, 1, k); total += build(f"wooden_{wt}_pull{k}", img, glow, wt)
            m = bow_item(m, lambda k: model_ref(f"wooden_bow_pull{k}"))
        json.dump({"model": m}, open(f"{A}/items/wooden_{wt}.json", "w"), indent="\t")
    print("weapons5 ok, elements total", total)
