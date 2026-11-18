"""⚡ 뇌신 볼트라 - 번개 구름 하반신, 금빛 왕관과 흰 수염의 거신"""
import math, sys, os
sys.path.insert(0, os.path.dirname(__file__))
from bossgen import *

R = math.radians
def n(rnd, a=10): return rnd.randint(-a, a)
def clamp(c): return tuple(max(0, min(255, int(v))) for v in c)

def armor(x, y, w, h, face, rnd):
    k = FACE_LIGHT[face]
    if min(w, h) > 2 and (x in (0, w-1) or y in (0, h-1)):
        return clamp(shade((226,178,72), k)), None            # 금테
    if face in ("front", "back") and w >= 6 and (x == w//2 or x == w//2-1) and y % 3 != 0:
        return (90, 220, 255), (120, 245, 255)                 # 가운데 번개 문양 (발광)
    base = (36 + n(rnd,4), 48 + n(rnd,4), 100 + n(rnd,6))
    if (x + y) % 5 == 0: base = (base[0]+10, base[1]+12, base[2]+18)
    return clamp(shade(base, k)), None

def gold(x, y, w, h, face, rnd):
    k = FACE_LIGHT[face]
    if min(w, h) > 2 and (x in (0, w-1) or y in (0, h-1)): return clamp(shade((150,96,24), k)), None
    c = (242 + n(rnd,8), 194 + n(rnd,10), 72 + n(rnd,10))
    if (x * 3 + y) % 6 == 0: c = (255, 232, 140)
    return clamp(shade(c, k)), None

def cloud(x, y, w, h, face, rnd):
    k = FACE_LIGHT[face]
    t = 0.5 + 0.5 * math.sin(x * 1.3 + y * 0.9 + rnd.random())
    c = (62 + 60 * t + n(rnd,5), 74 + 62 * t + n(rnd,5), 112 + 60 * t + n(rnd,6))
    if rnd.random() < 0.06: return (150, 235, 255), (150, 240, 255)   # 구름 속 번쩍임
    return clamp(shade(c, k)), None

def energy(x, y, w, h, face, rnd):
    c = (175 + n(rnd,15), 240 + n(rnd,10), 255)
    return clamp(c), clamp((200 + n(rnd,20), 250, 255))

def beard(x, y, w, h, face, rnd):
    k = FACE_LIGHT[face]
    v = 228 - (x % 2) * 14 + n(rnd,6)
    return clamp(shade((v, v + 4, v + 12), k)), None

def helmet_front(x, y, w, h, face, rnd):
    # 8x8 얼굴: 0~2줄 투구, 3~4줄 눈, 5줄 이하 피부(수염이 덮음)
    if y <= 2:
        if y == 2: return (226, 178, 72), None
        return armor(x, y, w, h, face, rnd)
    if y in (3, 4) and x in (1, 2, 5, 6):
        return (210, 255, 255), (170, 250, 255)                 # 빛나는 눈
    if y in (3, 4): return (40, 46, 70), None                   # 눈두덩 그림자
    return clamp((150 + n(rnd,5), 170 + n(rnd,5), 205 + n(rnd,5))), None

MATS = {"armor": armor, "gold": gold, "cloud": cloud, "energy": energy, "beard": beard}

def spec():
    rays = [Part(f"ray{i}", (0, 0, 0), (0, 0, R(i * 45)),
                 [Box(-0.5, -15, -0.5, 1, 8, 1, "energy")], field=False) for i in range(8)]
    head = Part("head", (0, -6, 0), boxes=[
        Box(-4, -8, -4, 8, 8, 8, "armor", faces={"front": helmet_front}),
        Box(-3, -3, -5, 6, 5, 1, "beard"),                     # 수염 (얼굴 아래 앞)
        Box(-2, 2, -5, 4, 3, 1, "beard"),                      # 수염 끝
        Box(-3.5, -11, -1, 1, 3, 2, "gold"), Box(-0.5, -13, -1, 1, 5, 2, "gold"), Box(2.5, -11, -1, 1, 3, 2, "gold"),
        Box(-6, -9, -0.5, 2, 1, 1, "energy"), Box(-7, -13, -0.5, 1, 4, 1, "energy"),   # 번개 뿔 (오른쪽)
        Box(4, -9, -0.5, 2, 1, 1, "energy"), Box(6, -13, -0.5, 1, 4, 1, "energy"),     # 번개 뿔 (왼쪽)
    ])
    right_arm = Part("right_arm", (-7, -4, 0), boxes=[
        Box(-4, -3, -3.5, 6, 4, 7, "gold"), Box(-3, 0, -2, 4, 10, 4, "armor"), Box(-3.5, 7, -2.5, 5, 5, 5, "gold")])
    left_arm = Part("left_arm", (7, -4, 0), boxes=[
        Box(-2, -3, -3.5, 6, 4, 7, "gold"), Box(-1, 0, -2, 4, 10, 4, "armor"), Box(-1.5, 7, -2.5, 5, 5, 5, "gold")])
    halo = Part("halo", (0, -4, 5), children=rays)
    tail4 = Part("tail4", (0, 4, 0), boxes=[Box(-1, 0, -1, 2, 3, 2, "energy")])
    tail3 = Part("tail3", (0, 4, 0), boxes=[Box(-2, 0, -1.5, 4, 4, 3, "cloud")], children=[tail4])
    tail2 = Part("tail2", (0, 4, 0), boxes=[Box(-3.5, 0, -2.5, 7, 4, 5, "cloud")], children=[tail3])
    tail1 = Part("tail1", (0, 8, 0), boxes=[Box(-4.5, 0, -3, 9, 4, 6, "cloud")], children=[tail2])
    body = Part("body", (0, 1, 0), boxes=[
        Box(-6, -6, -3.5, 12, 10, 7, "armor"),
        Box(-4.5, 4, -2.5, 9, 4, 5, "armor"),
        Box(-2, -4, -4.5, 4, 4, 1, "energy"),                  # 가슴 번개 문장
    ], children=[head, right_arm, left_arm, halo, tail1])
    return [body]

ANIM = """		float t = state.ageInTicks;
		// 둥둥 떠 있는 몸
		this.body.y += Mth.sin(t * 0.1F) * 1.0F;
		// 머리는 바라보는 방향으로
		this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
		this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
		// 등 뒤 후광 회전
		this.halo.zRot = t * 0.03F;
		// 번개 구름 꼬리 흔들림
		this.tail1.zRot = Mth.sin(t * 0.08F) * 0.06F;
		this.tail2.zRot = Mth.sin(t * 0.08F + 0.8F) * 0.10F;
		this.tail3.zRot = Mth.sin(t * 0.08F + 1.6F) * 0.14F;
		this.tail4.zRot = Mth.sin(t * 0.08F + 2.4F) * 0.22F;
		this.tail1.xRot = Mth.sin(t * 0.06F) * 0.05F;
		// 기본 팔 자세 (살짝 벌림)
		float sway = Mth.sin(t * 0.06F) * 0.05F;
		this.right_arm.zRot = 0.18F + sway;
		this.left_arm.zRot = -0.18F - sway;
		this.right_arm.xRot = sway;
		this.left_arm.xRot = -sway;
		// 공격 자세
		switch (state.attackPose) {
			case 1 -> { // 시전: 두 팔을 하늘로
				this.right_arm.xRot = -2.7F; this.right_arm.zRot = 0.35F;
				this.left_arm.xRot = -2.7F; this.left_arm.zRot = -0.35F;
				this.halo.zRot = t * 0.15F;
			}
			case 2 -> { // 돌진: 몸을 숙이고 팔을 뒤로
				this.body.xRot = 0.55F;
				this.right_arm.xRot = 0.9F; this.left_arm.xRot = 0.9F;
			}
			case 3 -> { // 방전 준비: 팔을 활짝
				this.right_arm.zRot = 1.35F; this.left_arm.zRot = -1.35F;
				this.halo.zRot = t * 0.25F;
			}
			default -> { }
		}"""

if __name__ == "__main__":
    # 사용법: python3 voltra.py <미리보기 출력 폴더> [프로젝트 루트]
    out = sys.argv[1]; mod_root = sys.argv[2] if len(sys.argv) > 2 else None
    parts = spec(); root = Part("root", children=parts, field=False)
    TW, TH = 128, 128
    pack(root, TW, TH)
    tex, glow = paint(root, TW, TH, MATS, seed=7)
    tex.save(f"{out}/voltra.png"); glow.save(f"{out}/voltra_glow.png")
    for i, (yaw, pitch) in enumerate([(-30, 12), (30, 8), (180 - 25, 10)]):
        preview(root, tex, size=480, yaw=yaw, pitch=pitch, glow=glow).save(f"{out}/voltra_preview{i}.png")
    code = java_model("com.elementalweapons.client.boss", "VoltraModel", "VoltraRenderState",
                      "", parts, TW, TH, ANIM)
    open(f"{out}/VoltraModel.java", "w").write(code)
    if mod_root:
        tex.save(f"{mod_root}/src/main/resources/assets/elementalweapons/textures/entity/voltra.png")
        glow.save(f"{mod_root}/src/main/resources/assets/elementalweapons/textures/entity/voltra_glow.png")
        open(f"{mod_root}/src/main/java/com/elementalweapons/client/boss/VoltraModel.java", "w").write(code)
    print("ok")
