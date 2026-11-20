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

import com.elementalweapons.item.ElementalWeaponItem;

/**
 * 무기 종류별 휘두르기 자세 (3인칭):
 *  대검·망치 = 두 손으로 머리 위까지 들어 내려찍기 + 상체 숙이기
 *  낫 = 상체를 크게 비틀며 옆으로 쓸어 베기
 *  단검 = 앞으로 빠르게 찌르기,  검 = 사선으로 크게 베기
 */
@Mixin(HumanoidModel.class)
public abstract class WeaponSwingPoseMixin<T extends HumanoidRenderState> {
	@Shadow @Final public ModelPart body;
	@Shadow @Final public ModelPart rightArm;
	@Shadow @Final public ModelPart leftArm;
	@Shadow @Final public ModelPart head;

	@Inject(method = "setupAttackAnimation", at = @At("TAIL"), require = 0)
	private void elementalweapons$weaponSwing(T s, CallbackInfo ci) {
		float t = s.swingAnimation;
		if (t <= 0 || s.rightHandItemStack == null || !(s.rightHandItemStack.getItem() instanceof ElementalWeaponItem w)) return;
		switch (w.type()) {
			case GREATSWORD, HAMMER -> {
				// 0~0.35 들어 올리기, 0.35~0.7 내려찍기, 이후 회복
				float up = t < 0.35F ? t / 0.35F : Math.max(0, 1 - (t - 0.35F) / 0.3F);
				float x = Mth.lerp(up, -0.35F, -2.9F);
				rightArm.xRot = x; leftArm.xRot = x;
				rightArm.yRot = -0.35F; leftArm.yRot = 0.35F;
				rightArm.zRot = 0; leftArm.zRot = 0;
				body.xRot = t < 0.35F ? -0.12F * up : 0.38F * Mth.sin((float) Math.PI * Math.min(1, (t - 0.35F) / 0.65F));
				head.xRot += body.xRot * 0.5F;
			}
			case SCYTHE -> {
				float k = Mth.sin(t * (float) Math.PI);
				float sweep = Mth.lerp(t, 1.1F, -1.3F);
				body.yRot = sweep * 0.7F;
				rightArm.xRot = -1.45F * k - 0.2F;
				rightArm.yRot = sweep;
				leftArm.xRot = -1.0F * k;
				leftArm.yRot = sweep + 0.6F;
			}
			case DAGGER -> {
				float k = Mth.sin(t * (float) Math.PI);
				rightArm.xRot = -1.55F * k - 0.1F;
				rightArm.yRot = -0.15F;
				body.yRot = -0.35F * k;
			}
			case SWORD -> {
				float k = Mth.sin(t * (float) Math.PI);
				rightArm.xRot = Mth.lerp(t, -2.6F, -0.3F);
				rightArm.zRot = Mth.lerp(t, 0.9F, -0.5F) * k;
				body.yRot = Mth.lerp(t, 0.45F, -0.55F) * k;
			}
			default -> {}
		}
	}
}
