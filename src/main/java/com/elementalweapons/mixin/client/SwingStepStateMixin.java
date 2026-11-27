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
	@Unique private final float[] elementalweapons$motionValue = new float[3];

	@Override public float[] elementalweapons$motion() { return elementalweapons$motionValue; }

	@Override public int elementalweapons$step() { return elementalweapons$stepValue; }
	@Override public void elementalweapons$setStep(int step) { elementalweapons$stepValue = step; }

	@Inject(method = "extractArmedEntityRenderState", at = @At("TAIL"), require = 0)
	private static void elementalweapons$extract(LivingEntity entity, ArmedEntityRenderState state, ItemModelResolver resolver, float partial, CallbackInfo ci) {
		((SwingStep) state).elementalweapons$setStep(SwingTracker.step(entity));
		float[] m = ((SwingStep) state).elementalweapons$motion();
		if (entity instanceof net.minecraft.world.entity.player.Player p) {
			com.elementalweapons.client.anim.Smooth sm = com.elementalweapons.client.anim.Smooth.of(p);
			float dt = sm.update(p, partial, 0, 0, 0, 0);
			float target = (p.onGround() ? 1F : 0.4F) * net.minecraft.util.Mth.clamp(sm.speed * 0.55F, 0F, 0.16F);
			if (p.isShiftKeyDown() || p.isSwimming() || p.isFallFlying() || p.isPassenger()) target = 0;
			m[0] = sm.lean = com.elementalweapons.client.anim.Smooth.follow(sm.lean, target, 0.12F, dt);
			m[1] = net.minecraft.util.Mth.clamp(sm.turn * 0.01F, -0.14F, 0.14F);
			m[2] = (p.isShiftKeyDown() || p.isSwimming() || p.isFallFlying() || p.isPassenger()) ? 0 : 1;
		} else { m[0] = 0; m[1] = 0; m[2] = 0; }
	}
}
