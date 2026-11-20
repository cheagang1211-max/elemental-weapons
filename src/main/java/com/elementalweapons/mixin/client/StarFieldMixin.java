package com.elementalweapons.mixin.client;

import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * 밤하늘의 별을 바닐라(1500회 시도 → 약 800개)보다 훨씬 많이(약 7천 개) 만들고,
 * 크기 편차를 키워 작은 별 사이사이 밝게 빛나는 큰 별이 섞이도록 합니다.
 */
@Mixin(SkyRenderer.class)
public abstract class StarFieldMixin {
	@ModifyConstant(method = "buildStars", constant = @Constant(intValue = 1500))
	private int elementalweapons$moreStars(int original) {
		return 14000;
	}

	@ModifyConstant(method = "buildStars", constant = @Constant(floatValue = 0.15F), require = 0)
	private float elementalweapons$baseSize(float original) {
		return 0.09F;
	}

	@ModifyConstant(method = "buildStars", constant = @Constant(floatValue = 0.1F), require = 0)
	private float elementalweapons$sizeSpread(float original) {
		return 0.26F;
	}
}
