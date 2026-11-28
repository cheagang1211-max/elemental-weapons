package com.elementalweapons.client.anim;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import com.elementalweapons.item.ElementalWeaponItem;
import com.elementalweapons.item.TrainingWeaponItem;
import com.elementalweapons.item.WeaponType;

/**
 * 활 쏘기 3인칭 동작: ① 시위 당기기(손이 활에서 뺨까지 천천히, 활 팔은 들어올리며 조준)
 * → ② 만작에서 미세한 떨림 → ③ 놓기(당긴 손이 뒤로 튕겨 나가고 활 끝이 반동으로 튐) → ④ 잔심 후 원래 자세로.
 */
public final class BowPose {
	private BowPose() {}

	private static final class S { boolean using; float draw; float relAt = -999; float relDraw; boolean left; }
	private static final Map<Integer, S> MAP = new HashMap<>();
	public static final float FULL = 20F, RELEASE = 12F;

	public static boolean isBow(ItemStack st) {
		if (st == null || st.isEmpty()) return false;
		if (st.getItem() instanceof ElementalWeaponItem w) return w.type() == WeaponType.BOW;
		if (st.getItem() instanceof TrainingWeaponItem w) return w.type() == WeaponType.BOW;
		return false;
	}

	/** out = {당김 0~1 (안 당기면 -1), 놓은 뒤 진행 0~1 (아니면 -1), 활 든 팔이 왼팔이면 1, 놓을 때 당김} */
	public static void update(LivingEntity e, float partial, float[] out) {
		S s = MAP.computeIfAbsent(e.getId(), k -> new S());
		float now = e.tickCount + partial;
		boolean bow = e.isUsingItem() && isBow(e.getUseItem());
		if (bow) {
			s.using = true;
			s.draw = Mth.clamp(e.getTicksUsingItem(partial) / FULL, 0F, 1F);
			HumanoidArm arm = e.getUsedItemHand() == InteractionHand.MAIN_HAND ? e.getMainArm() : e.getMainArm().getOpposite();
			s.left = arm == HumanoidArm.LEFT;
			s.relAt = -999;
		} else if (s.using) {
			s.using = false;
			if (s.draw >= 0.15F) { s.relAt = now; s.relDraw = s.draw; }
		}
		float r = (now - s.relAt) / RELEASE;
		out[0] = bow ? s.draw : -1;
		out[1] = !bow && r >= 0 && r < 1 ? r : -1;
		out[2] = s.left ? 1 : 0;
		out[3] = s.relDraw;
		if (MAP.size() > 400) MAP.clear();
	}

	private static float smooth(float x) { x = Mth.clamp(x, 0, 1); return x * x * (3 - 2 * x); }

	public static void apply(float[] b, float age, ModelPart head, ModelPart body, ModelPart rightArm, ModelPart leftArm) {
		if (b == null || (b[0] < 0 && b[1] < 0)) return;
		boolean left = b[2] > 0.5F;
		ModelPart bowArm = left ? leftArm : rightArm, drawArm = left ? rightArm : leftArm;
		float s = left ? -1 : 1;          // 당기는 쪽 방향
		float hx = head.xRot, hy = head.yRot, up = -Mth.HALF_PI + hx;
		if (b[0] >= 0) {
			// ① 당기기: 처음엔 활을 가슴 높이에서 걸고, 당기면서 눈높이로 들어 올림
			float e = smooth(b[0]);
			float raise = 0.32F * (1 - smooth(b[0] * 1.6F));
			bowArm.xRot = up + raise; bowArm.yRot = -s * 0.1F + hy; bowArm.zRot = 0;
			drawArm.xRot = up + raise - 0.14F * e; drawArm.yRot = hy + s * (0.06F + 0.62F * e); drawArm.zRot = s * 0.12F * e;
			// 상체를 옆으로 틀어 활 쪽 어깨가 과녁을 향함
			twist(body, rightArm, leftArm, -s * 0.18F * e);
			if (b[0] >= 1F) {   // ② 만작: 힘이 들어가 조금씩 떨림
				drawArm.yRot += s * 0.010F * Mth.sin(age * 1.9F);
				bowArm.xRot += 0.008F * Mth.sin(age * 2.6F + 1F);
			}
			return;
		}
		// ③ 놓기 → ④ 잔심 → 원래 자세
		float r = b[1], k = Mth.clamp(b[3], 0.3F, 1F);
		float q = smooth(r / 0.22F);
		float fx_bow = up - 0.22F * k * Mth.sin(Mth.PI * Mth.clamp(r / 0.3F, 0, 1)) + 0.06F * smooth((r - 0.2F) / 0.3F);
		float fy_bow = -s * 0.1F + hy - s * 0.06F * k * q;
		float fx_draw = up - 0.14F * k + 0.10F * q;
		float fy_draw = hy + s * (0.68F * k + 0.6F * k * q);
		float fz_draw = s * (0.12F + 0.25F * q) * k;
		float w = r < 0.6F ? 1F : 1F - smooth((r - 0.6F) / 0.4F);
		bowArm.xRot = Mth.lerp(w, bowArm.xRot, fx_bow); bowArm.yRot = Mth.lerp(w, bowArm.yRot, fy_bow); bowArm.zRot = Mth.lerp(w, bowArm.zRot, 0);
		drawArm.xRot = Mth.lerp(w, drawArm.xRot, fx_draw); drawArm.yRot = Mth.lerp(w, drawArm.yRot, fy_draw); drawArm.zRot = Mth.lerp(w, drawArm.zRot, fz_draw);
		twist(body, rightArm, leftArm, -s * 0.18F * k * w);
	}

	/** 상체를 틀고 어깨(팔 위치)도 같이 돌림 */
	private static void twist(ModelPart body, ModelPart rightArm, ModelPart leftArm, float a) {
		body.yRot += a;
		float sn = Mth.sin(a) * 5F, cs = (1F - Mth.cos(a)) * 5F;
		rightArm.z += sn; rightArm.x += cs;
		leftArm.z -= sn; leftArm.x -= cs;
	}
}
