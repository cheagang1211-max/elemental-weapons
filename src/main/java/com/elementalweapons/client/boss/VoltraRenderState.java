package com.elementalweapons.client.boss;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/** 볼트라를 그릴 때 필요한 정보 (서버에서 동기화된 공격 자세) */
public class VoltraRenderState extends LivingEntityRenderState {
	public int attackPose;
}
