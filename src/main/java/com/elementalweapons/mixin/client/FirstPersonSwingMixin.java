package com.elementalweapons.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.elementalweapons.client.anim.SwingTracker;
import com.elementalweapons.client.anim.WeaponPoses;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.ElementalWeaponItem;
import com.elementalweapons.item.TrainingWeaponItem;
import com.elementalweapons.item.WeaponType;

/** 1인칭 무기 공격 동작: 바닐라 칼 휘두르기 대신 무기·콤보·속성마다 다른 손 동작 */
@Mixin(FirstPersonHandsAndItemsRenderer.class)
public abstract class FirstPersonSwingMixin {
	@Inject(method = "swingArm", at = @At("HEAD"), cancellable = true, require = 0)
	private void elementalweapons$swing(float attack, PoseStack pose, int invert, HumanoidArm arm, CallbackInfo ci) {
		var mc = Minecraft.getInstance();
		var p = mc.player;
		if (p == null || arm != p.getMainArm()) return;
		var item = p.getMainHandItem().getItem();
		WeaponType type; Element el = null;
		if (item instanceof ElementalWeaponItem w) { type = w.type(); el = w.element(); }
		else if (item instanceof TrainingWeaponItem w) type = w.type();
		else return;
		float[] f = WeaponPoses.firstPerson(type, el, SwingTracker.step(p), attack, arm == HumanoidArm.RIGHT);
		if (f == null) return;
		pose.translate(f[0], f[1], f[2]);
		pose.rotateDegrees(Axis.YP, f[4]);
		pose.rotateDegrees(Axis.XP, f[3]);
		pose.rotateDegrees(Axis.ZP, f[5]);
		ci.cancel();
	}
}
