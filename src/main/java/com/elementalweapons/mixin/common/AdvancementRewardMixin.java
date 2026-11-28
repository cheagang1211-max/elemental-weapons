package com.elementalweapons.mixin.common;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 이 세계 도전과제를 처음 달성하면 보상 (Achieve.reward) */
@Mixin(PlayerAdvancements.class)
public abstract class AdvancementRewardMixin {
	@Shadow private ServerPlayer player;
	@Unique private boolean elementalweapons$wasDone;

	@Inject(method = "award", at = @At("HEAD"))
	private void elementalweapons$before(AdvancementHolder h, String crit, CallbackInfoReturnable<Boolean> cir) {
		elementalweapons$wasDone = ((PlayerAdvancements) (Object) this).getOrStartProgress(h).isDone();
	}

	@Inject(method = "award", at = @At("RETURN"))
	private void elementalweapons$after(AdvancementHolder h, String crit, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValueZ() || elementalweapons$wasDone) return;
		if (!((PlayerAdvancements) (Object) this).getOrStartProgress(h).isDone()) return;
		com.elementalweapons.growth.Achieve.reward(player, h);
	}
}
