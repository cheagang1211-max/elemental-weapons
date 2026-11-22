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
 * 근접 속성 무기의 휘두르기 자세 (3인칭) = 무기 종류별 기본 동작 × 속성별 변형 → 조합마다 다른 동작
 *  기본: 대검·망치 내려찍기 / 낫 옆으로 쓸기 / 단검 찌르기 / 검 사선 베기
 *  변형: 전기 두 번 빠르게 / 불 크게 치켜들어 세게 / 물 출렁이며 / 풀 낮고 넓게 / 바람 몸 회전 / 어둠 반대쪽에서 웅크려 / 빛 아래에서 위로
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
		float PI = (float) Math.PI, k = Mth.sin(t * PI);
		boolean twoHand = type == WeaponType.GREATSWORD || type == WeaponType.HAMMER || type == WeaponType.SCYTHE;
		switch (w.element()) {
			case ELECTRIC -> elementalweapons$base(type, (t * 2F) % 1F);          // 번개: 같은 동작을 순식간에 두 번
			case LIGHT -> {                                       // 빛: 동작을 거꾸로 — 아래에서 위로 올려 베며 몸을 젖힘
				elementalweapons$base(type, 1F - t);
				body.xRot -= 0.28F * k; head.xRot -= 0.3F * k;
			}
			case FIRE -> {                                        // 불: 천천히 크게 치켜들었다가 더 세게
				elementalweapons$base(type, t < 0.5F ? t * t * 2F : t);
				rightArm.xRot *= 1.2F; if (twoHand) leftArm.xRot *= 1.2F;
				body.xRot += 0.18F * k;
			}
			case WATER -> {                                       // 물: 팔과 몸이 물결처럼 출렁이며 이어짐
				elementalweapons$base(type, t);
				float a = t * PI * 2;
				rightArm.zRot += 0.6F * Mth.sin(a) * k;
				if (twoHand) leftArm.zRot -= 0.6F * Mth.sin(a) * k;
				body.yRot += 0.35F * Mth.sin(a);
			}
			case GRASS -> {                                       // 풀: 자세를 낮추고 더 넓게 땅을 쓸듯
				elementalweapons$base(type, t);
				body.xRot += 0.35F * k; head.xRot -= 0.2F * k;
				rightArm.xRot *= 0.75F; rightArm.yRot *= 1.3F;
				if (twoHand) { leftArm.xRot *= 0.75F; leftArm.yRot *= 1.3F; }
			}
			case WIND -> {                                        // 바람: 동작하면서 몸이 한 바퀴 회전
				elementalweapons$base(type, t);
				body.yRot += t * PI * 2;
			}
			case DARK -> {                                        // 어둠: 반대쪽에서, 몸을 웅크리고
				elementalweapons$base(type, t);
				rightArm.yRot = -rightArm.yRot; rightArm.zRot = -rightArm.zRot;
				leftArm.yRot = -leftArm.yRot; leftArm.zRot = -leftArm.zRot;
				body.yRot = -body.yRot;
				body.xRot += 0.45F * k; head.xRot += 0.2F * k;
			}
		}
		// 몸통이 돌면 어깨 위치도 같이 돌아가게 (바닐라와 같은 방식)
		float by = body.yRot;
		rightArm.x = -Mth.cos(by) * 5.0F; rightArm.z = Mth.sin(by) * 5.0F;
		leftArm.x = Mth.cos(by) * 5.0F; leftArm.z = -Mth.sin(by) * 5.0F;
	}

	/** 무기 종류별 기본 동작 */
	private void elementalweapons$base(WeaponType type, float t) {
		float k = Mth.sin(t * (float) Math.PI);
		rightArm.zRot = 0; leftArm.zRot = 0;
		switch (type) {
			case GREATSWORD, HAMMER -> {   // 두 손으로 머리 위까지 들어 내려찍기
				float up = t < 0.35F ? t / 0.35F : Math.max(0, 1 - (t - 0.35F) / 0.3F);
				float x = Mth.lerp(up, -0.35F, -2.9F) * (type == WeaponType.HAMMER ? 1.08F : 1F);
				rightArm.xRot = x; leftArm.xRot = x;
				rightArm.yRot = -0.35F; leftArm.yRot = 0.35F;
				body.xRot = t < 0.35F ? -0.12F * up : 0.38F * Mth.sin((float) Math.PI * Math.min(1, (t - 0.35F) / 0.65F));
				head.xRot += body.xRot * 0.5F;
			}
			case SCYTHE -> {   // 상체를 크게 비틀며 옆으로 쓸어 베기
				float sweep = Mth.lerp(t, 1.1F, -1.3F);
				body.yRot = sweep * 0.7F;
				rightArm.xRot = -1.45F * k - 0.2F;
				rightArm.yRot = sweep;
				leftArm.xRot = -1.0F * k;
				leftArm.yRot = sweep + 0.6F;
			}
			case DAGGER -> {   // 앞으로 빠르게 찌르기
				rightArm.xRot = -1.55F * k - 0.1F;
				rightArm.yRot = -0.15F;
				body.yRot = -0.35F * k;
			}
			case SWORD -> {   // 사선으로 크게 베기
				rightArm.xRot = Mth.lerp(t, -2.6F, -0.3F);
				rightArm.zRot = Mth.lerp(t, 0.9F, -0.5F) * k;
				body.yRot = Mth.lerp(t, 0.45F, -0.55F) * k;
			}
			default -> {}
		}
	}
}
