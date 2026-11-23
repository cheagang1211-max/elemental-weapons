package com.elementalweapons.client.anim;

/** 렌더 상태에 붙이는 '몇 번째 콤보 휘두르기인지' (1·2·3) */
public interface SwingStep {
	int elementalweapons$step();
	void elementalweapons$setStep(int step);
}
