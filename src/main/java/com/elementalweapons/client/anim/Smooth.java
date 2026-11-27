package com.elementalweapons.client.anim;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * 부드러운 움직임용 값 (엔티티마다): 프레임마다 목표값을 시간에 맞춰 천천히 따라감.
 *  자세 전환(공격 자세 섞기), 걷기 세기, 고개 돌림 지연, 몸 돌릴 때 기울기, 오르내림, 맞았을 때 움찔
 */
public final class Smooth {
	public float time = Float.NaN, lastBodyYaw;
	public final float[] pose = new float[4];
	public float walk, headYaw, headPitch, turn, vy, flinch, speed, lean;

	private static final Map<Entity, Smooth> MAP = new WeakHashMap<>();

	public static Smooth of(Entity e) { return MAP.computeIfAbsent(e, k -> new Smooth()); }

	/** k: 틱당 따라가는 정도 (0~1) */
	public static float follow(float v, float target, float k, float dt) {
		return v + (target - v) * (1F - (float) Math.pow(1F - k, dt));
	}

	/** 한 프레임 갱신. 반환: 지난 프레임부터 흐른 틱 */
	public float update(LivingEntity e, float partial, int attackPose, float walkSpeed, float headYawDeg, float headPitchDeg) {
		float now = e.tickCount + partial;
		float dt = Float.isNaN(time) ? 10F : Mth.clamp(now - time, 0F, 10F);
		boolean first = Float.isNaN(time);
		time = now;
		for (int i = 0; i < 4; i++) pose[i] = follow(pose[i], attackPose == i ? 1F : 0F, 0.32F, dt);
		walk = follow(walk, Mth.clamp(walkSpeed, 0F, 1F), 0.22F, dt);
		headYaw = follow(headYaw, headYawDeg, first ? 1F : 0.35F, dt);
		headPitch = follow(headPitch, headPitchDeg, first ? 1F : 0.35F, dt);
		float by = Mth.rotLerp(partial, e.yBodyRotO, e.yBodyRot);
		float rate = first || dt <= 0 ? 0 : Mth.wrapDegrees(by - lastBodyYaw) / Math.max(dt, 0.05F);
		lastBodyYaw = by;
		turn = follow(turn, Mth.clamp(rate, -25F, 25F), 0.15F, dt);
		vy = follow(vy, (float) e.getDeltaMovement().y, 0.2F, dt);
		float hv = (float) e.getDeltaMovement().horizontalDistance();
		speed = follow(speed, hv, 0.2F, dt);
		float hurt = e.hurtTime > 0 ? e.hurtTime / 10F : 0F;
		flinch = Math.max(hurt * hurt, follow(flinch, 0F, 0.25F, dt));
		return dt;
	}
}
