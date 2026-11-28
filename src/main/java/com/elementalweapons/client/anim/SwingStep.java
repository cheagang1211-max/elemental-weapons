package com.elementalweapons.client.anim;

/** 렌더 상태에 붙이는 '몇 번째 콤보 휘두르기인지' (1·2·3) */
public interface SwingStep {
	int elementalweapons$step();
	void elementalweapons$setStep(int step);
	/** 부드러운 움직임: {앞으로 기울기, 옆 기울기, 플레이어면 1} */
	float[] elementalweapons$motion();
	/** 활 쏘기: BowPose.update 결과 */
	float[] elementalweapons$bow();
}
