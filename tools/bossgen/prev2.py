import sys, os
sys.path.insert(0, os.path.dirname(__file__))
import bosses3
from bosses2 import SPECS
from bossgen import Part, pack, paint, preview, all_boxes
from PIL import Image, ImageDraw
ids = sys.argv[2:] or list(SPECS)
out = sys.argv[1]
tiles = []
SZ = int(os.environ.get('SZ', 360)); cols = int(os.environ.get('COLS', 4))
for bid in ids:
    roots, mats = SPECS[bid]()
    from hd import hdify; hdify(roots, 2)
    root = Part("root", children=roots, field=False)
    for tw in (512, 1024):
        try: pack(root, tw, tw); break
        except SystemExit: continue
    tex, glow = paint(root, tw, tw, mats, seed=7)
    if os.environ.get('AO'):
        from ao import bake; tex = bake(root, tex, glow)
    n = sum(1 for _ in all_boxes(root))
    for yaw in (-35, 145):
        im = preview(root, tex, size=SZ, yaw=yaw, pitch=12, glow=glow)
        d = ImageDraw.Draw(im); d.text((6, 4), f"{bid} {n}boxes {tw}", fill=(255, 255, 0))
        tiles.append(im)
sheet = Image.new("RGBA", (SZ * cols, SZ * ((len(tiles) + cols - 1) // cols)), (30, 30, 40, 255))
for i, t in enumerate(tiles): sheet.paste(t, ((i % cols) * SZ, (i // cols) * SZ))
sheet.save(out); print("saved", out)
