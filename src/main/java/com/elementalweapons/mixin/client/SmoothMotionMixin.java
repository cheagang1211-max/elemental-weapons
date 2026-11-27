package com.elementalweapons.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.elementalweapons.client.anim.SwingStep;

/**
 * 플레이어 움직임을 부드럽게: 걸음마다 살짝 내려앉는 몸, 달릴수록 앞으로 기울어지는 상체(허리 기준),
 * 몸을 돌릴 때 옆으로 기우는 자세, 걸을 때 어깨·머리가 엇갈려 흔들림, 가만히 있을 때 숨쉬기.
 * 갑옷·직업 복장 모형도 같은 계산을 거쳐서 몸에 딱 붙어 움직임.
 */
@Mixin(HumanoidModel.class)
public abstract class SmoothMotionMixin<T extends HumanoidRenderState> {
	@Shadow @Final public ModelPart body;
	@Shadow @Final public ModelPart rightArm;
	@Shadow @Final public ModelPart leftArm;
	@Shadow @Final public ModelPart head;

	@Inject(method = "setupAnim", at = @At("TAIL"), require = 0)
	private void elementalweapons$smooth(T s, CallbackInfo ci) {
		if (!(s instanceof SwingStep st)) return;
		float[] m = st.elementalweapons$motion();
		if (m == null || m[2] < 0.5F || s.isCrouching) return;
		float amt = Mth.clamp(s.walkAnimationSpeed, 0F, 1F), pos = s.walkAnimationPos, t = s.ageInTicks;
		float step = Mth.sin(pos * 0.6662F);
		float bob = -Math.abs(step) * 0.75F * amt + Mth.sin(t * 0.07F) * 0.18F * (1F - amt);   // 걸음 + 숨쉬기
		float lean = m[0], roll = m[1] + Mth.sin(pos * 0.3331F) * 0.035F * amt;
		float sl = Mth.sin(lean), cl = 1F - Mth.cos(lean);
		// 허리를 축으로 상체 기울이기
		body.xRot += lean; body.zRot += roll; body.yRot += step * 0.05F * amt;
		body.z -= 12F * sl; body.y += 12F * cl + bob;
		head.z -= 12F * sl; head.y += 12F * cl + bob; head.zRot += -roll * 0.5F;
		for (ModelPart a : new ModelPart[]{rightArm, leftArm}) {
			a.z -= 10F * sl; a.y += 10F * cl + bob;
			a.xRot += lean * 0.6F;
			a.zRot += roll * 0.5F;
		}
		rightArm.zRot += Mth.sin(t * 0.07F) * 0.02F * (1F - amt);
		leftArm.zRot -= Mth.sin(t * 0.07F) * 0.02F * (1F - amt);
	}
}
