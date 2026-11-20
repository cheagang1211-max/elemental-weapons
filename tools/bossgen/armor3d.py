"""입체 갑옷 장식 (겹겹이 쌓인 판금 + 가시 + 발광 장식)
사용법: python3 tools/bossgen/armor3d.py <프로젝트 루트> [미리보기 폴더]
- 휴머노이드 모델과 같은 파트(head/body/arms/legs)에 장식 파트를 붙여 플레이어 동작을 그대로 따라감
- 슬롯별 그룹: deco_helm / deco_chest·deco_rpaul·deco_lpaul / deco_tassets·deco_rknee·deco_lknee / deco_rboot·deco_lboot
- 텍스처는 2배 해상도(HD)로 칠함
"""
import sys, os, copy, math
sys.path.insert(0, os.path.dirname(__file__))
import materials2
from bossgen import Part, Box, pack, paint, preview, all_parts, all_boxes, fnum
from materials2 import M
from shapes import P, C, B

ROOT = sys.argv[1]
PREV = sys.argv[2] if len(sys.argv) > 2 else None
materials2.PS = 2
HD = 2

TIERS = {
    # 이미지처럼: 검보라 판금 + 보라 발광
    "aether": dict(plate=0x2E2A3C, dark=0x1C1A26, trim=0x5A4E7A, spike=0x47425A, gem=0xB46CFF, cloth=0x4A1E78),
    # 오리하르콘: 흑동 판금 + 금 테두리 + 주황 발광
    "orichalcum": dict(plate=0x3A2C26, dark=0x221A16, trim=0xC8913E, spike=0x5A4636, gem=0xFF8A3A, cloth=0x7A1E1E),
}


def spike(name, pivot, rot, length=5, base=2):
    """끝이 가늘어지는 가시 (y 위쪽으로 뻗음)"""
    a = max(1, round(length * 0.45)); b = length - a
    return P(name, pivot, rot, [C(0, -a / 2, 0, base, a, base, "spike"), C(0, -a - b / 2, 0, max(1, base - 1), b, max(1, base - 1), "spike")])


def mirror_x(part, suffix):
    q = copy.deepcopy(part)
    def walk(p):
        p.name = p.name[:-2] + "_" + suffix if p.name.endswith("_r") else p.name + "_" + suffix
        x, y, z = p.pivot; p.pivot = (-x, y, z)
        rx, ry, rz = p.rot; p.rot = (rx, -ry, -rz)
        for bx in p.boxes: bx.x = -(bx.x + bx.w)
        for c in p.children: walk(c)
    walk(q)
    return q


def helm():
    g = P("deco_helm", (0, 0, 0), (0, 0, 0), [
        B(-1, -11, -5.5, 2, 2, 11, "trim"),            # 정수리 볏
        B(-5.5, -6.5, -5.6, 11, 6, 1, "plate"),        # 얼굴 가리개
        B(-4, -5, -5.9, 8, 1, 1, "gem"),               # 눈 틈 (발광)
        B(-0.5, -6.5, -6.2, 1, 6, 1, "trim"),          # 코 가리개
        B(-6, -6, -4.5, 1, 7, 5, "plate"),             # 오른 볼 가리개
        B(5, -6, -4.5, 1, 7, 5, "plate"),              # 왼 볼 가리개
        B(-5.5, -2.5, 4.5, 11, 3, 1, "plate"),         # 목 가리개
        B(-5.5, -9.6, -5.5, 11, 1, 11, "dark"),        # 투구 테
    ], [
        # 뒤로 휘는 뿔 2개
        P("horn_r", (-5, -8, -1), (0.25, 0, -0.55), [C(0, -2, 0, 2, 4, 2, "spike")], [
            P("horn_r2", (0, -4, 0), (-0.5, 0, 0.15), [C(0, -2, 0, 2, 4, 2, "spike")], [
                P("horn_r3", (0, -4, 0), (-0.55, 0, 0.1), [C(0, -1.5, 0, 1, 3, 1, "spike")])])]),
        P("horn_l", (5, -8, -1), (0.25, 0, 0.55), [C(0, -2, 0, 2, 4, 2, "spike")], [
            P("horn_l2", (0, -4, 0), (-0.5, 0, -0.15), [C(0, -2, 0, 2, 4, 2, "spike")], [
                P("horn_l3", (0, -4, 0), (-0.55, 0, -0.1), [C(0, -1.5, 0, 1, 3, 1, "spike")])])]),
        # 볏 위 가시 왕관
        spike("crest1", (0, -11, -3), (-0.25, 0, 0), 4),
        spike("crest2", (0, -11, 0), (-0.35, 0, 0), 6),
        spike("crest3", (0, -11, 3), (-0.5, 0, 0), 5),
    ])
    return g


def chest():
    kids = [
        spike("bspike_r", (-3, 0, 3.6), (-0.55, 0, -0.25), 6),
        spike("bspike_l", (3, 0, 3.6), (-0.55, 0, 0.25), 6),
        spike("bspike_c", (0, 2, 3.8), (-0.8, 0, 0), 4),
    ]
    boxes = [
        B(-4.5, -2.5, -3.5, 9, 2, 7, "trim"),          # 목둘레 고리
        B(-5.5, -1, -3.8, 11, 6, 1, "plate"),          # 윗가슴 판
        B(-0.5, -1, -4.4, 1, 6, 1, "trim"),            # 가운데 능선
        B(-1, 1.5, -4.6, 2, 2, 1, "gem"),               # 가슴 보석 (발광)
        B(-5, 5, -3.6, 10, 2, 1, "plate"),             # 복부 판 1
        B(-4, 6.6, -3.5, 8, 1, 1, "gem"),              # 발광선
        B(-4.5, 7.5, -3.4, 9, 2, 1, "plate"),          # 복부 판 2
        B(-5.5, 10.2, -3.6, 11, 2, 7, "dark"),         # 허리띠
        B(-1, 10, -4, 2, 2, 1, "gem"),                 # 버클
        B(-5.5, -1, 3, 11, 9, 1, "plate"),             # 등판
        B(-4, 3, 3.6, 8, 1, 1, "gem"),                 # 등 발광선
    ]
    return P("deco_chest", (0, 0, 0), (0, 0, 0), boxes, kids)


def pauldron_r():
    # 오른팔 기준(바깥쪽 = -x). 3겹 견갑 + 가시 3개 + 팔꿈치 가시 + 팔목 보호대
    boxes = [
        B(-5.5, -4.5, -3.8, 7, 2, 8, "plate"),         # 윗덮개
        B(-6.5, -3, -4, 2, 4, 8, "plate"),             # 바깥 1겹
        B(-6, 0.5, -3.6, 2, 3, 7, "plate"),            # 바깥 2겹
        B(-6.8, -2, -1, 1, 1, 2, "gem"),               # 발광 장식
        B(-5.5, -4.8, -3.8, 7, 1, 1, "trim"),          # 앞 테두리
        B(-4.5, 7, -3.5, 7, 3, 7, "plate"),            # 팔목 보호대
        B(-4.6, 7.4, -3.6, 1, 2, 1, "gem"),
        B(-4.5, 9.6, -3.5, 7, 1, 7, "trim"),
    ]
    kids = [
        spike("ps1_r", (-4, -4.5, -2.4), (0.15, 0, -0.5), 5),
        spike("ps2_r", (-4.5, -4.5, 0.2), (0, 0, -0.65), 7),
        spike("ps3_r", (-4, -4.5, 2.8), (-0.15, 0, -0.5), 5),
        P("elbow_r", (-1, 5.5, 3), (1.25, 0, 0), [C(0, -1, 0, 2, 2, 2, "spike"), C(0, -3, 0, 1, 2, 1, "spike")]),
    ]
    return P("deco_rpaul", (0, 0, 0), (0, 0, 0), boxes, kids)


def tassets():
    return P("deco_tassets", (0, 0, 0), (0, 0, 0), [
        B(-5, 12, -3.7, 4, 5, 1, "plate"),             # 앞 허벅지 판 (오른)
        B(1, 12, -3.7, 4, 5, 1, "plate"),              # (왼)
        B(-5, 16.2, -3.8, 4, 1, 1, "trim"),
        B(1, 16.2, -3.8, 4, 1, 1, "trim"),
        B(-1.5, 12, -3.9, 3, 8, 1, "cloth"),           # 가운데 보라 천
        B(-0.5, 13, -4.1, 1, 1, 1, "gem"),
        B(-6, 12, -2.5, 1, 4, 5, "plate"),             # 옆 판
        B(5, 12, -2.5, 1, 4, 5, "plate"),
        B(-5, 12, 2.7, 10, 4, 1, "plate"),             # 뒤 판
    ])


def knee_r():
    return P("deco_rknee", (0, 0, 0), (0, 0, 0), [
        B(-3, 0.5, -3.4, 6, 4, 1, "plate"),            # 허벅지 판
        B(-3, 4, -3.8, 6, 3, 1, "plate"),              # 무릎 덮개
        B(-1, 4.8, -4.2, 2, 1, 1, "gem"),
        B(-3, 7, -3.4, 6, 3, 1, "dark"),               # 정강이 판
    ], [P("kspike_r", (0, 5.5, -4.2), (-1.35, 0, 0), [C(0, -1, 0, 2, 2, 2, "spike"), C(0, -2.5, 0, 1, 2, 1, "spike")])])


def boot_r():
    return P("deco_rboot", (0, 0, 0), (0, 0, 0), [
        B(-3.5, 7.5, -3.5, 7, 2, 7, "trim"),           # 발목 고리
        B(-3, 9.5, -5, 6, 3, 2, "plate"),              # 발등 판
        B(-2, 11, -6, 4, 1, 1, "dark"),                # 발끝
    ], [P("heel_r", (0, 10.5, 3.3), (-1.4, 0, 0), [C(0, -1.5, 0, 1, 3, 1, "spike")])])


def build():
    helm_g, chest_g, tas_g = helm(), chest(), tassets()
    rpaul, rknee, rboot = pauldron_r(), knee_r(), boot_r()
    lpaul = mirror_x(rpaul, "l"); lpaul.name = "deco_lpaul"
    lknee = mirror_x(rknee, "l"); lknee.name = "deco_lknee"
    lboot = mirror_x(rboot, "l"); lboot.name = "deco_lboot"
    base = {
        "head": ((0, 0, 0), [helm_g]),
        "body": ((0, 0, 0), [chest_g, tas_g]),
        "right_arm": ((-5, 2, 0), [rpaul]),
        "left_arm": ((5, 2, 0), [lpaul]),
        "right_leg": ((-1.9, 12, 0), [rknee, rboot]),
        "left_leg": ((1.9, 12, 0), [lknee, lboot]),
    }
    return base


def java_part(part, parent, lines):
    var = "p_" + part.name
    cubes = "CubeListBuilder.create()"
    for b in part.boxes:
        cubes += f".texOffs({b.u}, {b.v}).addBox({fnum(b.x)}, {fnum(b.y)}, {fnum(b.z)}, {fnum(b.w)}, {fnum(b.h)}, {fnum(b.d)})"
    px, py, pz = part.pivot; ax, ay, az = part.rot
    lines.append(f"\t\tPartDefinition {var} = {parent}.addOrReplaceChild(\"{part.name}\", {cubes}, PartPose.offsetAndRotation({fnum(px)}, {fnum(py)}, {fnum(pz)}, {fnum(ax)}, {fnum(ay)}, {fnum(az)}));")
    for c in part.children: java_part(c, var, lines)


def main():
    base = build()
    groups = [g for _, (_, gs) in base.items() for g in gs]
    holder = Part("root", children=groups, field=False)
    names = [p.name for p in all_parts(holder)]
    assert len(names) == len(set(names)), [n for n in names if names.count(n) > 1]
    tw = th = 128
    pack(holder, tw, th)
    # 2배 해상도로 칠하기
    hd = copy.deepcopy(holder)
    for _, b in all_boxes(hd):
        b.u, b.v, b.w, b.h, b.d = b.u * HD, b.v * HD, b.w * HD, b.h * HD, b.d * HD
        b.x, b.y, b.z = b.x * HD, b.y * HD, b.z * HD
    for p in all_parts(hd): p.pivot = tuple(v * HD for v in p.pivot)
    tdir = f"{ROOT}/src/main/resources/assets/elementalweapons/textures/entity/armor_deco"
    os.makedirs(tdir, exist_ok=True)
    for tier, c in TIERS.items():
        mats = {
            "plate": M(c["plate"], "plate", trim=c["trim"]),
            "dark": M(c["dark"], "metal"),
            "trim": M(c["trim"], "metal"),
            "spike": M(c["spike"], "metal"),
            "gem": M(c["gem"], "gem", glow=c["gem"]),
            "cloth": M(c["cloth"], "cloth"),
        }
        tex, glow = paint(hd, tw * HD, th * HD, mats, seed=11)
        tex.save(f"{tdir}/{tier}.png"); glow.save(f"{tdir}/{tier}_glow.png")
        if PREV:
            # 미리보기용: 갑옷 기본 몸체(바닐라 갑옷 크기) + 장식을 함께 칠해서 확인
            body = {"head": [B(-5, -9, -5, 10, 10, 10, "plate")], "body": [B(-5, -1, -3, 10, 14, 6, "plate")],
                    "right_arm": [B(-4, -3, -3, 6, 14, 6, "plate")], "left_arm": [B(-2, -3, -3, 6, 14, 6, "plate")],
                    "right_leg": [B(-3, 0, -3, 6, 13, 6, "dark")], "left_leg": [B(-3, 0, -3, 6, 13, 6, "dark")]}
            pr = Part("root", children=[], field=False)
            for pname, (piv, gs) in base.items():
                pr.children.append(Part(pname, piv, (0, 0, 0), copy.deepcopy(body[pname]), copy.deepcopy(gs), False))
            pack(pr, 256, 256)
            for _, b in all_boxes(pr):
                b.u, b.v, b.w, b.h, b.d = b.u * HD, b.v * HD, b.w * HD, b.h * HD, b.d * HD
                b.x, b.y, b.z = b.x * HD, b.y * HD, b.z * HD
            for p in all_parts(pr): p.pivot = tuple(v * HD for v in p.pivot)
            ptex, pglow = paint(pr, 512, 512, mats, seed=11)
            for yaw in (-30, 35, 160):
                preview(pr, ptex, size=520, yaw=yaw, pitch=10, glow=pglow).save(f"{PREV}/armor3d_{tier}_{yaw}.png")
    # 자바 레이어 정의
    lines = []
    for pname, (piv, gs) in base.items():
        lines.append(f"\t\tPartDefinition {pname} = root.addOrReplaceChild(\"{pname}\", CubeListBuilder.create(), PartPose.offset({fnum(piv[0])}, {fnum(piv[1])}, {fnum(piv[2])}));")
        if pname == "head": lines.append("\t\thead.addOrReplaceChild(\"hat\", CubeListBuilder.create(), PartPose.ZERO);")
        for g in gs: java_part(g, pname, lines)
    jpath = f"{ROOT}/src/main/java/com/elementalweapons/client/armor/ArmorDecoMesh.java"
    os.makedirs(os.path.dirname(jpath), exist_ok=True)
    open(jpath, "w").write(f"""// 자동 생성 파일 (tools/bossgen/armor3d.py) - 모양을 바꾸려면 생성기를 수정하세요.
package com.elementalweapons.client.armor;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public final class ArmorDecoMesh {{
	private ArmorDecoMesh() {{}}

	public static LayerDefinition create() {{
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
{chr(10).join(lines)}
		return LayerDefinition.create(mesh, {tw}, {th});
	}}
}}
""")
    print("armor3d ok", tw, th)


main()
