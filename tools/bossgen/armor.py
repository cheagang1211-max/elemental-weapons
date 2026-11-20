"""갑옷 착용 텍스처 HD (128x64, 휴머노이드 UV 배치). 사용법: python3 tools/bossgen/armor.py <프로젝트 루트>"""
import sys, os
sys.path.insert(0, os.path.dirname(__file__))
import materials2
from bossgen import Box, Part, paint
from materials2 import M, FACE
K = 2
materials2.PS = 2
ROOT = sys.argv[1]
OUT = f"{ROOT}/src/main/resources/assets/elementalweapons/textures/entity/equipment"
TIERS = {"aether": dict(base=0x2E2A3C, trim=0x5A4E7A, gem=0xB46CFF), "orichalcum": dict(base=0x3A2C26, trim=0xC8913E, gem=0xFF8A3A)}
def bx(u, v, w, h, d, mat, faces=None):
    b = Box(0, 0, 0, w * K, h * K, d * K, mat, faces or {}); b.u, b.v = u * K, v * K; return b
for tier, c in TIERS.items():
    m = {"plate": M(c["base"], "plate", trim=c["trim"]), "trim": M(c["trim"], "metal"), "under": M(0x3A3A48, "metal")}
    m["helm"] = FACE(m["plate"], c["gem"], mode="visor", brow=False)
    def chest(x, y, w, h, face, rnd, base=m["plate"]):
        col, g = base(x, y, w, h, face, rnd)
        cx = w // 2
        if face == "front" and abs(x - cx + 0.5) < 2 and h * 0.25 < y < h * 0.42:
            return materials2.hexc(c["gem"]), materials2.hexc(c["gem"])
        if face == "front" and (abs(x - cx + 0.5) < 1 or y in (h // 2, h // 2 + 1)):
            return m["trim"](x, y, w, h, face, rnd)
        return col, g
    for layer, boxes in (("humanoid", [bx(0, 0, 8, 8, 8, "plate", {"front": m["helm"]}), bx(16, 16, 8, 12, 4, "plate", {"front": chest}),
                                        bx(40, 16, 4, 12, 4, "plate"), bx(0, 16, 4, 12, 4, "plate")]),
                         ("humanoid_leggings", [bx(16, 16, 8, 12, 4, "plate"), bx(0, 16, 4, 12, 4, "plate")])):
        root = Part("root", (0, 0, 0), (0, 0, 0), boxes, [], False)
        tex, glow = paint(root, 64 * K, 32 * K, m, seed=5)
        tex.save(f"{OUT}/{layer}/{tier}.png")
print("armor ok")
