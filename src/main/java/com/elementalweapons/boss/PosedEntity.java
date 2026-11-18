package com.elementalweapons.boss;

/** 범용 모델/렌더러로 그려지는 엔티티 (보스, 몹, 상인 공통) */
public interface PosedEntity {
	/** 0 = 평소, 1 = 시전, 2 = 돌진, 3 = 방출 */
	int getAttackPose();
	/** 둥둥 떠 있는 애니메이션 여부 */
	boolean isFloatingBoss();
}
