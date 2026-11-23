package com.elementalweapons.client.anim;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import com.elementalweapons.item.Element;
import com.elementalweapons.item.WeaponType;

/**
 * 무기별 공격 동작 (3인칭 몸 전체 + 1인칭 손).
 *  콤보 1·2·3타가 모두 다른 동작이고, 한 동작은 '준비 → 빠른 타격 → 마무리 멈춤 → 제자리'로 부드럽게 이어짐.
 *  속성은 그 위에 변형을 얹음 (전기 두 번 빠르게, 빛 아래→위, 어둠 반대쪽, 바람 회전, 불 크게, 물 출렁, 풀 낮게).
 */
public final class WeaponPoses {
	private WeaponPoses() {}

	/** 팔·몸·다리 각도 묶음: 오른팔 xyz, 왼팔 xyz, 몸 x·y, 머리 x, 오른다리 x, 왼다리 x */
	public record Pose(float rx, float ry, float rz, float lx, float ly, float lz, float bx, float by, float hx, float rl, float ll) {
		Pose lerp(Pose o, float u) {
			return new Pose(Mth.lerp(u, rx, o.rx), Mth.lerp(u, ry, o.ry), Mth.lerp(u, rz, o.rz), Mth.lerp(u, lx, o.lx), Mth.lerp(u, ly, o.ly), Mth.lerp(u, lz, o.lz),
					Mth.lerp(u, bx, o.bx), Mth.lerp(u, by, o.by), Mth.lerp(u, hx, o.hx), Mth.lerp(u, rl, o.rl), Mth.lerp(u, ll, o.ll));
		}
	}

	private static Pose P(float rx, float ry, float rz, float lx, float ly, float lz, float bx, float by, float hx, float rl, float ll) {
		return new Pose(rx, ry, rz, lx, ly, lz, bx, by, hx, rl, ll);
	}

	/** [준비 자세, 타격 끝 자세, 몸 회전 여부] */
	private record Move(Pose from, Pose to, boolean spin, boolean aim) {}

	private static Move move(WeaponType t, int step) {
		return switch (t) {
			case SWORD -> switch (step) {
				case 1 -> new Move(P(-2.6F, -0.5F, 0.6F, -0.3F, 0, 0, 0, 0.5F, 0, 0.2F, -0.2F), P(-0.4F, 0.6F, -0.4F, -0.2F, 0, 0, 0.12F, -0.5F, 0, -0.3F, 0.3F), false, false);   // 사선 내려베기
				case 2 -> new Move(P(-0.5F, 0.9F, -0.3F, -0.3F, 0, 0, 0.05F, -0.6F, 0, 0.3F, -0.2F), P(-2.3F, -0.7F, 0.7F, -0.4F, 0, 0, -0.1F, 0.55F, -0.1F, -0.2F, 0.3F), false, false);   // 역수 올려베기
				default -> new Move(P(-0.9F, 0.15F, 0, -0.5F, 0, 0, 0, 0.45F, 0, 0.45F, -0.45F), P(-1.62F, -0.05F, 0, -0.2F, 0, 0.3F, 0.28F, -0.25F, 0.1F, -0.7F, 0.55F), false, true);   // 내디디며 찌르기
			};
			case GREATSWORD -> switch (step) {
				case 1 -> new Move(P(-1.35F, 1.2F, 0, -1.35F, 1.5F, 0, 0.05F, 0.95F, 0, 0.2F, -0.2F), P(-1.35F, -1.25F, 0, -1.35F, -0.95F, 0, 0.15F, -0.95F, 0, -0.3F, 0.3F), false, false);   // 수평 휩쓸기
				case 2 -> new Move(P(-1.35F, -1.25F, 0, -1.35F, -0.95F, 0, 0.15F, -0.95F, 0, -0.2F, 0.2F), P(-1.2F, 1.2F, 0, -1.2F, 1.5F, 0, 0.1F, 0.95F, 0, 0.3F, -0.3F), false, false);   // 되돌려 휩쓸기
				default -> new Move(P(-3.05F, -0.3F, 0, -3.05F, 0.3F, 0, -0.28F, 0, -0.2F, 0, 0), P(-0.55F, -0.3F, 0, -0.55F, 0.3F, 0, 0.55F, 0, 0.25F, -0.5F, 0.45F), false, false);   // 머리 위에서 내려찍기
			};
			case HAMMER -> switch (step) {
				case 1 -> new Move(P(-1.0F, 1.4F, 0.4F, -1.0F, 1.65F, 0, 0.1F, 1.05F, 0, 0.25F, -0.25F), P(-1.0F, -1.05F, -0.3F, -1.0F, -0.8F, 0, 0.25F, -0.85F, 0, -0.35F, 0.3F), false, false);   // 옆으로 후려치기
				case 2 -> new Move(P(0.35F, -0.2F, 0, 0.35F, 0.2F, 0, 0.4F, 0.2F, 0.2F, 0.3F, -0.4F), P(-2.85F, -0.2F, 0, -2.85F, 0.2F, 0, -0.32F, -0.1F, -0.45F, -0.2F, 0.2F), false, false);   // 아래에서 올려치기
				default -> new Move(P(-3.35F, -0.2F, 0, -3.35F, 0.2F, 0, -0.45F, 0, -0.35F, 0.1F, 0.1F), P(-0.25F, -0.2F, 0, -0.25F, 0.2F, 0, 0.75F, 0, 0.35F, -0.6F, 0.6F), false, false);   // 크게 들어 내리찍기
			};
			case DAGGER -> switch (step) {
				case 1 -> new Move(P(-0.75F, 0.25F, 0, -0.6F, 0, 0, 0, 0.35F, 0, 0.3F, -0.3F), P(-1.6F, -0.1F, 0, -0.4F, 0, 0, 0.12F, -0.35F, 0, -0.4F, 0.3F), false, true);   // 오른손 찌르기
				case 2 -> new Move(P(-0.6F, 0, 0, -2.0F, -0.6F, -0.5F, 0, -0.45F, 0, -0.2F, 0.3F), P(-0.6F, 0, 0, -0.45F, 0.75F, 0.4F, 0.1F, 0.45F, 0, 0.3F, -0.2F), false, false);   // 왼손 교차 베기
				default -> new Move(P(-1.5F, 0, -1.25F, -1.5F, 0, 1.25F, 0.1F, 0, 0, 0.2F, -0.2F), P(-1.5F, 0, -1.3F, -1.5F, 0, 1.3F, 0.1F, 0, 0, -0.2F, 0.2F), true, false);   // 두 손 회전 베기
			};
			case SCYTHE -> switch (step) {
				case 1 -> new Move(P(-1.0F, 1.4F, 0, -0.6F, 1.9F, 0, 0.28F, 1.05F, 0, 0.3F, -0.3F), P(-1.2F, -1.45F, 0, -0.9F, -0.8F, 0, 0.32F, -1.05F, 0, -0.4F, 0.35F), false, false);   // 낮게 크게 베어 가르기
				case 2 -> new Move(P(-0.2F, -0.9F, 0, 0, -0.4F, 0, 0.38F, -0.8F, 0.2F, -0.2F, 0.3F), P(-2.7F, 0.8F, 0, -2.3F, 1.2F, 0, -0.22F, 0.7F, -0.3F, 0.3F, -0.2F), false, false);   // 올려 베기
				default -> new Move(P(-1.3F, 0, -1.05F, -1.3F, 0, 0.65F, 0.15F, 0, 0, 0.2F, -0.2F), P(-1.3F, 0, -1.1F, -1.3F, 0, 0.7F, 0.2F, 0, 0, -0.2F, 0.2F), true, false);   // 한 바퀴 회전
			};
			case STAFF -> switch (step) {
				case 1 -> new Move(P(-1.0F, 0, 0, -0.3F, 0, 0, 0, 0.2F, 0, 0.2F, -0.2F), P(-1.75F, -0.1F, 0, -0.5F, 0, 0.2F, 0.15F, -0.2F, 0.1F, -0.3F, 0.3F), false, true);   // 지팡이 내밀기
				case 2 -> new Move(P(-1.6F, 1.0F, 0, -1.0F, 0.5F, 0, 0, 0.5F, 0, 0, 0), P(-1.6F, -0.9F, 0, -1.0F, -0.5F, 0, 0, -0.5F, 0, 0, 0), false, false);   // 원을 그리며 휘두르기
				default -> new Move(P(-3.1F, -0.1F, 0, -3.1F, 0.1F, 0, -0.22F, 0, -0.35F, 0, 0), P(-1.4F, -0.1F, 0, -1.2F, 0.2F, 0, 0.3F, 0, 0.15F, -0.3F, 0.3F), false, false);   // 들어 올렸다 내려꽂기
			};
			case BOOK -> switch (step) {
				case 1 -> new Move(P(-1.0F, -0.3F, 0, -0.5F, 0, 0, 0, 0, 0, 0, 0), P(-1.0F, -0.3F, 0, -1.65F, 0.2F, 0, 0.05F, 0.25F, 0, -0.2F, 0.2F), false, true);   // 책을 펼치고 손바닥 밀기
				case 2 -> new Move(P(-1.0F, -0.3F, 0, -1.4F, 0.9F, 0, 0, -0.3F, 0, 0, 0), P(-1.0F, -0.3F, 0, -1.4F, -0.6F, 0, 0, 0.35F, 0, 0, 0), false, false);   // 손으로 쓸어내기
				default -> new Move(P(-1.0F, -0.3F, 0, -0.5F, 0, 0, 0, 0, 0, 0, 0), P(-2.8F, -0.3F, -0.3F, -2.8F, 0.3F, 0.3F, -0.22F, 0, -0.45F, 0, 0), false, false);   // 두 팔 들어 축복
			};
			case BOW -> step == 3
					? new Move(P(-1.45F, -0.75F, 0, -1.57F, 0.15F, 0, -0.18F, 0.2F, 0, 0, 0), P(-1.6F, -0.15F, 0.3F, -1.57F, 0.15F, 0, -0.08F, 0.1F, 0, 0, 0), false, true)    // 몸을 젖히며 세 발
					: new Move(P(-1.45F, -0.65F, 0, -1.57F, 0.15F, 0, 0, 0.25F, 0, 0, 0), P(-1.6F, -0.15F, 0.25F, -1.57F, 0.15F, 0, 0, 0.15F, 0, 0, 0), false, true);              // 당겼다 놓기
			default -> null;
		};
	}

	private static float smooth(float x) { x = Mth.clamp(x, 0, 1); return x * x * (3 - 2 * x); }
	private static float cubic(float x) { x = Mth.clamp(x, 0, 1); return x < 0.5F ? 4 * x * x * x : 1 - (float) Math.pow(-2 * x + 2, 3) / 2; }

	/** 시간 t(0~1)를 속성에 맞게 바꿈 */
	public static float time(Element el, float t) {
		if (el == null) return t;
		return switch (el) {
			case ELECTRIC -> (t * 2F) % 1F;
			case LIGHT -> 1F - t;
			case FIRE -> t < 0.5F ? t * t * 2F : t;
			default -> t;
		};
	}

	/** 3인칭 몸 자세 */
	public static boolean apply(WeaponType type, Element el, int step, float t0,
			ModelPart head, ModelPart body, ModelPart rightArm, ModelPart leftArm, ModelPart rightLeg, ModelPart leftLeg) {
		Move m = move(type, step);
		if (m == null) return false;
		float t = time(el, t0);
		// 무게: 처음·끝에서 원래 자세와 부드럽게 섞임
		float w = t0 < 0.15F ? smooth(t0 / 0.15F) : t0 > 0.82F ? 1 - smooth((t0 - 0.82F) / 0.18F) : 1F;
		// 경로: 준비(0~0.25) → 빠른 타격(0.25~0.55) → 멈춤
		Pose p = t < 0.25F ? m.from : t < 0.55F ? m.from.lerp(m.to, cubic((t - 0.25F) / 0.3F)) : m.to;
		boolean melee = type != WeaponType.STAFF && type != WeaponType.BOOK && type != WeaponType.BOW;
		float k = Mth.sin(t0 * (float) Math.PI);
		float aim = m.aim ? head.xRot : 0;
		float rx = p.rx + aim, ry = p.ry, rz = p.rz, lx = p.lx + (m.aim && type == WeaponType.BOW ? aim : 0), ly = p.ly, lz = p.lz, bx = p.bx, by = p.by, hx = p.hx;
		if (m.spin) by += cubic((t - 0.2F) / 0.6F) * (float) Math.PI * 2;
		if (melee && el != null) switch (el) {
			case FIRE -> { rx *= 1.15F; lx *= 1.15F; bx += 0.12F * k; }
			case WATER -> { float a = t0 * (float) Math.PI * 2; rz += 0.5F * Mth.sin(a) * k; by += 0.3F * Mth.sin(a) * k; }
			case GRASS -> { bx += 0.3F * k; hx -= 0.2F * k; rx *= 0.8F; ry *= 1.25F; ly *= 1.25F; }
			case WIND -> by += t * (float) Math.PI * 2;
			case DARK -> { ry = -ry; rz = -rz; ly = -ly; lz = -lz; by = -by; bx += 0.4F * k; hx += 0.15F * k; }
			default -> {}
		}
		rightArm.xRot = Mth.lerp(w, rightArm.xRot, rx); rightArm.yRot = Mth.lerp(w, rightArm.yRot, ry); rightArm.zRot = Mth.lerp(w, rightArm.zRot, rz);
		leftArm.xRot = Mth.lerp(w, leftArm.xRot, lx); leftArm.yRot = Mth.lerp(w, leftArm.yRot, ly); leftArm.zRot = Mth.lerp(w, leftArm.zRot, lz);
		body.xRot = Mth.lerp(w, body.xRot, bx); body.yRot = Mth.lerp(w, body.yRot, by);
		head.xRot += hx * w;
		rightLeg.xRot = Mth.lerp(w * 0.8F, rightLeg.xRot, p.rl); leftLeg.xRot = Mth.lerp(w * 0.8F, leftLeg.xRot, p.ll);
		float b = body.yRot;
		rightArm.x = -Mth.cos(b) * 5.0F; rightArm.z = Mth.sin(b) * 5.0F;
		leftArm.x = Mth.cos(b) * 5.0F; leftArm.z = -Mth.sin(b) * 5.0F;
		return true;
	}

	// ---------------- 1인칭 ----------------
	/** 1인칭 손에 든 무기: 회전(도) 시작·끝 + 앞으로 내미는 거리 */
	private record FMove(float x0, float y0, float z0, float x1, float y1, float z1, float tx, float ty, float tz, boolean spin) {}

	private static FMove fmove(WeaponType t, int step) {
		return switch (t) {
			case SWORD -> step == 1 ? new FMove(-20, 10, 70, -40, -20, -65, 0.15F, 0.05F, -0.15F, false)
					: step == 2 ? new FMove(-30, -15, -70, -10, 25, 70, -0.1F, 0.1F, -0.15F, false)
					: new FMove(10, 0, 0, -15, 0, 0, 0, 0.05F, -0.55F, false);
			case GREATSWORD -> step == 1 ? new FMove(-15, 65, 10, -25, -75, -15, 0.25F, 0, -0.1F, false)
					: step == 2 ? new FMove(-25, -70, -10, -15, 70, 15, -0.25F, 0, -0.1F, false)
					: new FMove(60, 0, 0, -85, 0, 0, 0, 0.2F, -0.2F, false);
			case HAMMER -> step == 1 ? new FMove(-30, 75, 20, -30, -65, -20, 0.25F, -0.05F, -0.1F, false)
					: step == 2 ? new FMove(-70, 0, 0, 65, 0, 0, 0, 0.25F, -0.2F, false)
					: new FMove(75, 0, 0, -95, 0, 0, 0, 0.3F, -0.25F, false);
			case DAGGER -> step == 1 ? new FMove(10, 0, 0, -10, 0, 0, 0, 0, -0.5F, false)
					: step == 2 ? new FMove(-20, 0, 85, -20, 0, -85, -0.1F, 0.05F, -0.2F, false)
					: new FMove(0, 0, 0, 0, 360, 0, 0, 0.05F, -0.25F, true);
			case SCYTHE -> step == 1 ? new FMove(-10, 85, 35, -30, -90, -30, 0.3F, -0.1F, -0.1F, false)
					: step == 2 ? new FMove(40, -40, 0, -80, 60, 0, 0, 0.25F, -0.15F, false)
					: new FMove(0, 0, 20, 0, 360, 20, 0, 0, -0.2F, true);
			case STAFF -> step == 1 ? new FMove(10, 0, 0, -25, 0, 0, 0, 0.05F, -0.4F, false)
					: step == 2 ? new FMove(-15, 50, 20, -15, -50, -20, 0, 0.05F, -0.15F, false)
					: new FMove(70, 0, 0, -45, 0, 0, 0, 0.3F, -0.25F, false);
			case BOOK -> step == 1 ? new FMove(0, 0, 0, -20, 0, 0, 0, 0.1F, -0.25F, false)
					: step == 2 ? new FMove(-10, 30, 15, -10, -30, -15, 0, 0.05F, -0.1F, false)
					: new FMove(0, 0, 0, -40, 0, 0, 0, 0.3F, -0.15F, false);
			case BOW -> new FMove(0, 0, 0, -8, 0, 0, 0, 0, step == 3 ? 0.25F : 0.18F, false);
			default -> null;
		};
	}

	/** 1인칭: PoseStack 변형 값 [이동 x, y, z, 회전 x, y, z(도)] — null이면 바닐라 동작 */
	public static float[] firstPerson(WeaponType type, Element el, int step, float t0, boolean rightArm) {
		FMove m = fmove(type, step);
		if (m == null) return null;
		float t = time(el, t0);
		float w = t0 < 0.12F ? smooth(t0 / 0.12F) : t0 > 0.8F ? 1 - smooth((t0 - 0.8F) / 0.2F) : 1F;
		float u = t < 0.25F ? 0 : t < 0.55F ? cubic((t - 0.25F) / 0.3F) : 1;
		float rx = Mth.lerp(u, m.x0, m.x1), ry = Mth.lerp(u, m.y0, m.y1), rz = Mth.lerp(u, m.z0, m.z1);
		if (m.spin) { rx = m.x0; rz = m.z0; ry = u * 360; }
		// 앞으로 내미는 정도: 타격 순간 가장 멀리 (활은 당겼다가 놓으면 튀어나감)
		float thrust = type == WeaponType.BOW ? (t < 0.45F ? smooth(t / 0.45F) : -0.6F * (1 - smooth((t - 0.45F) / 0.4F))) : Mth.sin(u * (float) Math.PI * 0.9F + 0.1F) * (t < 0.25F ? smooth(t / 0.25F) * 0.3F : 1F);
		float tx = m.tx * thrust, ty = m.ty * thrust, tz = m.tz * thrust;
		if (type != WeaponType.STAFF && type != WeaponType.BOOK && type != WeaponType.BOW && el != null) switch (el) {
			case FIRE -> { rx *= 1.2F; ry *= 1.2F; rz *= 1.2F; }
			case WATER -> rz += 15 * Mth.sin(t0 * (float) Math.PI * 2);
			case GRASS -> ty -= 0.08F;
			case WIND -> rz += 90 * smooth(t);
			case DARK -> { ry = -ry; rz = -rz; tx = -tx; }
			default -> {}
		}
		float s = rightArm ? 1 : -1;
		return new float[]{s * tx * w, ty * w, tz * w, rx * w, s * ry * w, s * rz * w};
	}
}
