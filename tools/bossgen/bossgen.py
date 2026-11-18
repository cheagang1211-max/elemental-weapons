"""
보스 모델 생성기
spec(파트/박스/재질) 하나로 -> 자바 모델 코드 + 텍스처 + 발광 텍스처 + 미리보기 PNG
마인크래프트 박스 UV 규칙(ModelPart.Cube)을 그대로 따릅니다.
"""
import math, random
from PIL import Image, ImageDraw

# ---------------- 데이터 구조 ----------------
class Box:
    def __init__(s, x, y, z, w, h, d, mat, faces=None):
        s.x, s.y, s.z, s.w, s.h, s.d = x, y, z, int(w), int(h), int(d)
        s.mat = mat; s.faces = faces or {}; s.u = s.v = 0
class Part:
    def __init__(s, name, pivot=(0,0,0), rot=(0,0,0), boxes=(), children=(), field=True):
        s.name, s.pivot, s.rot = name, pivot, rot
        s.boxes, s.children, s.field = list(boxes), list(children), field

# ---------------- UV 배치 (선반 패킹) ----------------
def all_boxes(part):
    for b in part.boxes: yield part, b
    for c in part.children: yield from all_boxes(c)
def all_parts(part):
    yield part
    for c in part.children: yield from all_parts(c)

def pack(root, tw, th):
    boxes = [b for _, b in all_boxes(root)]
    order = sorted(boxes, key=lambda b: -(b.d + b.h))
    x = y = rowh = 0
    for b in order:
        bw, bh = 2 * (b.d + b.w), b.d + b.h
        if x + bw > tw: x, y, rowh = 0, y + rowh, 0
        if y + bh > th: raise SystemExit(f"texture too small ({tw}x{th})")
        b.u, b.v = x, y; x += bw; rowh = max(rowh, bh)

# ---------------- 면 정의 (ModelPart.Cube 와 동일) ----------------
def faces_of(b):
    f, g, h = b.x, b.y, b.z; i, j, k = b.x + b.w, b.y + b.h, b.z + b.d
    v0=(f,g,h); v1=(i,g,h); v2=(i,j,h); v3=(f,j,h); v4=(f,g,k); v5=(i,g,k); v6=(i,j,k); v7=(f,j,k)
    u, v, w, hh, d = b.u, b.v, b.w, b.h, b.d
    l, m, n, o, p, q = u, u+d, u+d+w, u+d+w+w, u+d+w+d, u+d+w+d+w
    r, s, t = v, v+d, v+d+hh
    # (이름, 꼭짓점4, (u1,v1,u2,v2))  꼭짓점0->(u2,v1) 1->(u1,v1) 2->(u1,v2) 3->(u2,v2)
    return [("top",   [v5,v4,v0,v1], (m,r,n,s)),
            ("bottom",[v2,v3,v7,v6], (n,s,o,r)),
            ("right", [v0,v4,v7,v3], (l,s,m,t)),
            ("front", [v1,v0,v3,v2], (m,s,n,t)),
            ("left",  [v5,v1,v2,v6], (n,s,p,t)),
            ("back",  [v4,v5,v6,v7], (p,s,q,t))]

# ---------------- 텍스처 칠하기 ----------------
def paint(root, tw, th, materials, seed=1):
    tex = Image.new("RGBA", (tw, th), (0,0,0,0)); glow = Image.new("RGBA", (tw, th), (0,0,0,0))
    rnd = random.Random(seed)
    for part, b in all_boxes(root):
        for name, _, (u1, v1, u2, v2) in faces_of(b):
            x0, x1 = sorted((u1, u2)); y0, y1 = sorted((v1, v2))
            fw, fh = x1 - x0, y1 - y0
            if fw <= 0 or fh <= 0: continue
            painter = b.faces.get(name) or materials[b.mat]
            for yy in range(fh):
                for xx in range(fw):
                    c, gl = painter(xx, yy, fw, fh, name, rnd)
                    if c: tex.putpixel((x0+xx, y0+yy), c if len(c)==4 else c+(255,))
                    if gl: glow.putpixel((x0+xx, y0+yy), gl if len(gl)==4 else gl+(255,))
    return tex, glow

def shade(c, k):
    return tuple(max(0, min(255, int(v * k))) for v in c[:3])
FACE_LIGHT = {"top":1.12, "front":1.0, "right":0.86, "left":0.86, "back":0.78, "bottom":0.7}

# ---------------- 미리보기 렌더 ----------------
def matmul(a, b): return [[sum(a[i][k]*b[k][j] for k in range(3)) for j in range(3)] for i in range(3)]
def rx(t): c,s=math.cos(t),math.sin(t); return [[1,0,0],[0,c,-s],[0,s,c]]
def ry(t): c,s=math.cos(t),math.sin(t); return [[c,0,s],[0,1,0],[-s,0,c]]
def rz(t): c,s=math.cos(t),math.sin(t); return [[c,-s,0],[s,c,0],[0,0,1]]
def apply(m, p): return tuple(sum(m[i][k]*p[k] for k in range(3)) for i in range(3))

def collect_quads(part, M=None, T=(0,0,0), out=None):
    if M is None: M = [[1,0,0],[0,1,0],[0,0,1]]
    if out is None: out = []
    piv = apply(M, part.pivot); T2 = (T[0]+piv[0], T[1]+piv[1], T[2]+piv[2])
    M2 = matmul(M, matmul(rz(part.rot[2]), matmul(ry(part.rot[1]), rx(part.rot[0]))))
    for b in part.boxes:
        for name, verts, uv in faces_of(b):
            wv = [tuple(T2[i]+apply(M2, v)[i] for i in range(3)) for v in verts]
            out.append((wv, uv, name))
    for c in part.children: collect_quads(c, M2, T2, out)
    return out

def solve_h(src, dst):
    # src(화면)->dst(텍스처) 투영 변환 계수 (PIL PERSPECTIVE용)
    import numpy as np
    A=[]; B=[]
    for (x,y),(u,v) in zip(src,dst):
        A.append([x,y,1,0,0,0,-u*x,-u*y]); B.append(u)
        A.append([0,0,0,x,y,1,-v*x,-v*y]); B.append(v)
    return list(np.linalg.solve(np.array(A,float), np.array(B,float)))

def preview(root, tex, size=520, yaw=-35, pitch=18, scale=None, bg=(34,34,46), glow=None, show_glow=True):
    quads = collect_quads(root)
    # 렌더러와 같이 모델 공간(y 아래) -> 화면: y 뒤집고, 앞(-Z)이 보이도록 회전
    V = matmul(rx(math.radians(-pitch)), ry(math.radians(yaw)))
    pts = []
    for wv, uv, name in quads:
        pv = [apply(V, (x, -y, -z)) for (x,y,z) in wv]
        pts.append((pv, uv, name))
    xs=[p[0] for q in pts for p in q[0]]; ys=[p[1] for q in pts for p in q[0]]
    cx, cy = (max(xs)+min(xs))/2, (max(ys)+min(ys))/2
    span = max(max(xs)-min(xs), max(ys)-min(ys)); sc = scale or (size*0.86/span)
    img = Image.new("RGBA", (size, size), bg+(255,))
    src_tex = tex.copy()
    if glow is not None and show_glow:
        src_tex = Image.alpha_composite(src_tex, glow)
    pts.sort(key=lambda q: sum(p[2] for p in q[0])/4)  # 먼 것부터 (z 작을수록 멀다고 가정)
    for pv, (u1,v1,u2,v2), name in pts:
        scr = [(size/2+(p[0]-cx)*sc, size/2-(p[1]-cy)*sc) for p in pv]
        # 뒷면 제거
        (ax,ay),(bx,by),(cx2,cy2) = scr[0],scr[1],scr[2]
        if (bx-ax)*(cy2-ay)-(by-ay)*(cx2-ax) > 0: continue
        dst = [(u2,v1),(u1,v1),(u1,v2),(u2,v2)]
        try: coeffs = solve_h(scr, dst)
        except Exception: continue
        layer = src_tex.transform((size,size), Image.PERSPECTIVE, coeffs, Image.NEAREST)
        mask = Image.new("L", (size,size), 0); ImageDraw.Draw(mask).polygon(scr, fill=255)
        la = layer.split()[3]; from PIL import ImageChops
        m2 = ImageChops.multiply(mask, la)
        img.paste(layer, (0,0), m2)
    return img

# ---------------- 자바 코드 ----------------
def fnum(v):
    v = float(v)
    s = ("%.4f" % v).rstrip("0").rstrip(".")
    if s in ("-0", ""): s = "0"
    return s + "F"

def java_parts(part, parent_var, lines, depth=1):
    var = part.name.replace("-", "_")
    cubes = "CubeListBuilder.create()"
    for b in part.boxes:
        cubes += f".texOffs({b.u}, {b.v}).addBox({fnum(b.x)}, {fnum(b.y)}, {fnum(b.z)}, {fnum(b.w)}, {fnum(b.h)}, {fnum(b.d)})"
    px, py, pz = part.pivot; ax, ay, az = part.rot
    pose = f"PartPose.offsetAndRotation({fnum(px)}, {fnum(py)}, {fnum(pz)}, {fnum(ax)}, {fnum(ay)}, {fnum(az)})"
    decl = f"PartDefinition {var} = " if part.children else ""
    lines.append(f"\t\t{decl}{parent_var}.addOrReplaceChild(\"{part.name}\", {cubes}, {pose});")
    for c in part.children: java_parts(c, var, lines, depth+1)

def java_model(pkg, cls, state_cls, state_import, root_parts, tw, th, anim_code, extra_imports=""):
    lines = []
    for p in root_parts: java_parts(p, "root", lines)
    # 필드: 이름 -> getChild 경로
    fields = []
    def walk(p, path):
        path = path + [p.name]
        if p.field: fields.append((p.name, path))
        for c in p.children: walk(c, path)
    for p in root_parts: walk(p, [])
    fdecl = "\n".join(f"\tprivate final ModelPart {n};" for n,_ in fields)
    finit = "\n".join(f"\t\tthis.{n} = root" + "".join(f".getChild(\"{x}\")" for x in path) + ";" for n,path in fields)
    return f"""// 자동 생성 파일 (tools/bossgen) - 모양을 바꾸려면 생성기 spec을 수정하세요. setupAnim 은 spec의 애니메이션 코드에서 옵니다.
package {pkg};

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
{state_import}
{extra_imports}
public class {cls} extends EntityModel<{state_cls}> {{
{fdecl}

	public {cls}(ModelPart root) {{
		super(root);
{finit}
	}}

	public static LayerDefinition createBodyLayer() {{
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
{chr(10).join(lines)}
		return LayerDefinition.create(mesh, {tw}, {th});
	}}

	@Override
	public void setupAnim({state_cls} state) {{
		super.setupAnim(state);
{anim_code}
	}}
}}
"""
