package com.elementalweapons.mixin.client;

import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.elementalweapons.client.anim.SwingStep;
import com.elementalweapons.client.anim.SwingTracker;

/** 렌더 상태에 콤보 단계를 실어 보냄 (내 캐릭터·다른 플레이어 모두) */
@Mixin(ArmedEntityRenderState.class)
public abstract class SwingStepStateMixin implements SwingStep {
	@Unique private int elementalweapons$stepValue = 1;

	@Override public int elementalweapons$step() { return elementalweapons$stepValue; }
	@Override public void elementalweapons$setStep(int step) { elementalweapons$stepValue = step; }

	@Inject(method = "extractArmedEntityRenderState", at = @At("TAIL"), require = 0)
	private static void elementalweapons$extract(LivingEntity entity, ArmedEntityRenderState state, ItemModelResolver resolver, float partial, CallbackInfo ci) {
		((SwingStep) state).elementalweapons$setStep(SwingTracker.step(entity));
	}
}
