package com.elementalweapons.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.elementalweapons.item.ElementalWeaponItem;
import com.elementalweapons.item.WeaponType;

/**
 * 무기 공격 동작 (3인칭): 무기 종류 × 콤보 1·2·3타 × 속성 변형 — 자세한 동작은 WeaponPoses
 */
@Mixin(HumanoidModel.class)
public abstract class WeaponSwingPoseMixin<T extends HumanoidRenderState> {
	@Shadow @Final public ModelPart body;
	@Shadow @Final public ModelPart rightArm;
	@Shadow @Final public ModelPart leftArm;
	@Shadow @Final public ModelPart head;
	@Shadow @Final public ModelPart rightLeg;
	@Shadow @Final public ModelPart leftLeg;

	@Inject(method = "setupAttackAnimation", at = @At("TAIL"), require = 0)
	private void elementalweapons$weaponSwing(T s, CallbackInfo ci) {
		float t = s.swingAnimation;
		if (t <= 0 || s.rightHandItemStack == null) return;
		WeaponType type; com.elementalweapons.item.Element el = null;
		if (s.rightHandItemStack.getItem() instanceof ElementalWeaponItem w) { type = w.type(); el = w.element(); }
		else if (s.rightHandItemStack.getItem() instanceof com.elementalweapons.item.TrainingWeaponItem w) type = w.type();
		else return;
		int step = ((com.elementalweapons.client.anim.SwingStep) s).elementalweapons$step();
		com.elementalweapons.client.anim.WeaponPoses.apply(type, el, step, t, head, body, rightArm, leftArm, rightLeg, leftLeg);
	}
}
