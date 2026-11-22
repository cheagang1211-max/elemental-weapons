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
import com.elementalweapons.item.WeaponType;

/**
 * 근접 속성 무기의 휘두르기 자세 (3인칭) — 같은 무기라도 속성마다 다름:
 *  전기 = 두 번 빠른 찌르기 / 불 = 치켜들어 내려찍기 / 물 = 8자 흐르는 베기 / 풀 = 낮게 수평 쓸기
 *  바람 = 한 바퀴 회전 / 어둠 = 웅크려 역수 찌르기 / 빛 = 아래에서 위로 올려 베기
 *  대검·망치·낫은 두 손, 단검은 작게, 망치는 크게
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
		WeaponType type = w.type();
		if (type != WeaponType.SWORD && type != WeaponType.GREATSWORD && type != WeaponType.HAMMER && type != WeaponType.SCYTHE && type != WeaponType.DAGGER) return;
		boolean twoHand = type == WeaponType.GREATSWORD || type == WeaponType.HAMMER || type == WeaponType.SCYTHE;
		float amp = type == WeaponType.DAGGER ? 0.75F : type == WeaponType.HAMMER ? 1.15F : 1.0F;
		float PI = (float) Math.PI, k = Mth.sin(t * PI);
		rightArm.zRot = 0; leftArm.zRot = 0;
		switch (w.element()) {
			case ELECTRIC -> {   // 번개: 순식간에 두 번 찌르는 직선 공격
				float tt = (t * 2F) % 1F, kk = Mth.sin(tt * PI);
				rightArm.xRot = (-1.65F * kk - 0.1F) * amp; rightArm.yRot = -0.1F;
				body.yRot = -0.35F * kk;
				if (twoHand) { leftArm.xRot = rightArm.xRot * 0.85F; leftArm.yRot = 0.5F; }
			}
			case FIRE -> {   // 불: 머리 위로 크게 치켜들었다가 힘껏 내려찍기
				float up = t < 0.4F ? t / 0.4F : Math.max(0, 1 - (t - 0.4F) / 0.35F);
				float x = Mth.lerp(up, -0.4F, -3.0F) * amp;
				rightArm.xRot = x; rightArm.yRot = -0.3F;
				leftArm.xRot = twoHand ? x : x * 0.5F; leftArm.yRot = 0.3F;
				body.xRot = t < 0.4F ? -0.18F * up : 0.42F * Mth.sin(PI * Math.min(1, (t - 0.4F) / 0.6F));
				head.xRot += body.xRot * 0.5F;
			}
			case WATER -> {   // 물: 8자로 흐르듯 이어 베기
				float a = t * PI * 2;
				rightArm.xRot = (-1.3F - 0.55F * Mth.sin(a)) * amp;
				rightArm.zRot = 0.75F * Mth.sin(a + PI / 2) * k;
				body.yRot = 0.45F * Mth.sin(a) * k;
				leftArm.xRot = twoHand ? rightArm.xRot * 0.8F : -0.4F * k; leftArm.zRot = twoHand ? -rightArm.zRot : 0;
			}
			case GRASS -> {   // 풀: 몸을 낮춰 땅을 스치듯 수평으로 쓸어 베기
				body.xRot = 0.4F * k;
				head.xRot -= 0.25F * k;
				rightArm.xRot = (-0.95F * k - 0.35F) * amp;
				rightArm.yRot = Mth.lerp(t, 1.25F, -1.25F);
				body.yRot = Mth.lerp(t, 0.6F, -0.6F) * k;
				if (twoHand) { leftArm.xRot = rightArm.xRot; leftArm.yRot = rightArm.yRot + 0.6F; }
			}
			case WIND -> {   // 바람: 팔을 뻗고 몸을 한 바퀴 돌리는 회전 베기
				body.yRot = t * PI * 2;
				rightArm.xRot = -0.15F; rightArm.zRot = -1.45F * k * amp;
				leftArm.xRot = -0.15F; leftArm.zRot = (twoHand ? 1.45F : 0.9F) * k;
			}
			case DARK -> {   // 어둠: 몸을 웅크리고 옆구리에서 역수로 꿰찌르기
				body.xRot = 0.5F * k;
				head.xRot += 0.2F * k;
				rightArm.xRot = (-0.55F * k + 0.25F) * amp;
				rightArm.yRot = 0.95F * k;
				rightArm.zRot = 0.45F * k;
				leftArm.xRot = -0.8F * k; leftArm.yRot = -0.4F * k;
			}
			case LIGHT -> {   // 빛: 아래에서 하늘로 올려 베며 몸을 젖힘
				rightArm.xRot = Mth.lerp(t, 0.35F, -3.05F) * amp;
				rightArm.zRot = -0.3F * k;
				body.xRot = -0.28F * k;
				head.xRot -= 0.35F * k;
				leftArm.xRot = twoHand ? rightArm.xRot : -1.3F * k;
			}
		}
		// 몸통이 돌면 어깨 위치도 같이 돌아가게 (바닐라와 같은 방식)
		float by = body.yRot;
		rightArm.x = -Mth.cos(by) * 5.0F; rightArm.z = Mth.sin(by) * 5.0F;
		leftArm.x = Mth.cos(by) * 5.0F; leftArm.z = -Mth.sin(by) * 5.0F;
	}
}
