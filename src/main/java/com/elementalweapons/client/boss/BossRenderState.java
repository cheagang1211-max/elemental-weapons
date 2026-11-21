package com.elementalweapons.client.boss;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/** 범용 보스를 그릴 때 필요한 정보 */
public class BossRenderState extends LivingEntityRenderState {
	public int attackPose;
	public boolean floating;
	/** 가게 주인·현자: 머리 위 큰 이름표 */
	public boolean bigLabel;
}
