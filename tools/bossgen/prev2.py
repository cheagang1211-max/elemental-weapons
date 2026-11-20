import sys, os
sys.path.insert(0, os.path.dirname(__file__))
from bosses2 import SPECS
from bossgen import Part, pack, paint, preview, all_boxes
from PIL import Image, ImageDraw
ids = sys.argv[2:] or list(SPECS)
out = sys.argv[1]
tiles = []
for bid in ids:
    roots, mats = SPECS[bid]()
    root = Part("root", children=roots, field=False)
    for tw in (256, 512):
        try: pack(root, tw, tw); break
        except SystemExit: continue
    tex, glow = paint(root, tw, tw, mats, seed=7)
    n = sum(1 for _ in all_boxes(root))
    for yaw in (-35, 145):
        im = preview(root, tex, size=360, yaw=yaw, pitch=12, glow=glow)
        d = ImageDraw.Draw(im); d.text((6, 4), f"{bid} {n}boxes {tw}", fill=(255, 255, 0))
        tiles.append(im)
cols = 4
sheet = Image.new("RGBA", (360 * cols, 360 * ((len(tiles) + cols - 1) // cols)), (30, 30, 40, 255))
for i, t in enumerate(tiles): sheet.paste(t, ((i % cols) * 360, (i // cols) * 360))
sheet.save(out); print("saved", out)
