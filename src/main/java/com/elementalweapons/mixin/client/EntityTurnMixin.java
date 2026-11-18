package com.elementalweapons.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.elementalweapons.client.CastCamera;

/**
 * 빛 지팡이 시점 고정 중에는 마우스로 시점을 돌리지 못하게 막습니다.
 * (마우스 이동은 Entity.turn()을 통해 내 캐릭터의 방향을 바꿉니다)
 */
@Mixin(Entity.class)
public abstract class EntityTurnMixin {
	@Inject(method = "turn", at = @At("HEAD"), cancellable = true, require = 0)
	private void elementalweapons$lockViewDuringCinematic(CallbackInfo ci) {
		if (CastCamera.isViewLocked() && (Object) this == Minecraft.getInstance().player) {
			ci.cancel();
		}
	}
}
