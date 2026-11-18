package com.elementalweapons.client;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.item.Element;
import com.elementalweapons.spell.HeavenGateSpell;

/**
 * 지팡이 시전 카메라 연출 (클라이언트 전용)
 *
 *  - 모든 지팡이: 시전하면 삼인칭(뒤) 시점으로 전환 → 마법이 끝나면 원래 시점으로 복귀
 *  - 빛 지팡이: 천국의 문이 내려와 대천사가 나올 때까지 시선을 문에 고정 (마우스 입력 무시)
 */
public final class CastCamera {
	private CastCamera() {}

	/** 빛 지팡이 시점 고정 시간 (틱): 문이 내려오고 대천사가 강림할 때까지 */
	private static final int LIGHT_LOCK_TICKS = HeavenGateSpell.ANGEL_READY;
	/** 시선이 문을 따라가는 부드러움 (0~1, 클수록 빠르게 따라감) */
	private static final float AIM_SMOOTHING = 0.3F;

	private static CameraType previous = null;
	private static int remaining = 0;
	private static int age = 0;
	private static boolean lightCinematic = false;
	private static Vec3 gateTarget = null;

	/** 삼인칭을 유지할 시간 (틱) = 대략 각 마법의 길이 */
	private static int duration(Element element) {
		return switch (element) {
			case ELECTRIC -> 45;
			case FIRE -> 60;
			case WATER -> 70;
			case GRASS -> 60;
			case WIND -> 40;
			case DARK -> 90;
			case LIGHT -> 216;
		};
	}

	/** 지팡이를 쓴 순간 호출 */
	public static void start(Element element, Player player) {
		Minecraft mc = Minecraft.getInstance();
		if (player != mc.player) return;

		// 풀 지팡이는 주변에 적이 없으면 발동하지 않으므로 카메라도 그대로
		if (element == Element.GRASS && !hasEnemiesNear(player, 16)) return;

		if (previous == null) previous = mc.options.getCameraType();
		mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
		remaining = duration(element);
		age = 0;
		lightCinematic = element == Element.LIGHT;
		gateTarget = lightCinematic ? HeavenGateSpell.gateCenter(player) : null;
	}

	/** 지금 마우스 시점 조작을 막아야 하는지 (믹스인에서 사용) */
	public static boolean isViewLocked() {
		return lightCinematic && remaining > 0 && age < LIGHT_LOCK_TICKS;
	}

	/** 매 클라이언트 틱 */
	public static void tick(Minecraft mc) {
		if (remaining <= 0) return;
		if (mc.player == null) { // 월드를 나갔으면 정리
			reset(mc);
			return;
		}
		if (mc.isPaused()) return;

		if (isViewLocked() && gateTarget != null) {
			// 내려오는 문을 계속 바라보도록
			Vec3 gateNow = gateTarget.add(0, HeavenGateSpell.descentOffset(age), 0);
			aimAt(mc.player, gateNow);
		}

		age++;
		remaining--;
		if (remaining <= 0) reset(mc);
	}

	private static void aimAt(Player player, Vec3 target) {
		Vec3 d = target.subtract(player.getEyePosition());
		double horizontal = Math.sqrt(d.x * d.x + d.z * d.z);
		float yaw = (float) Math.toDegrees(Math.atan2(-d.x, d.z));
		float pitch = (float) -Math.toDegrees(Math.atan2(d.y, horizontal));

		float newYaw = player.getYRot() + Mth.wrapDegrees(yaw - player.getYRot()) * AIM_SMOOTHING;
		float newPitch = player.getXRot() + (pitch - player.getXRot()) * AIM_SMOOTHING;
		player.setYRot(newYaw);
		player.setXRot(Mth.clamp(newPitch, -90.0F, 90.0F));
		player.setYHeadRot(newYaw);
	}

	private static void reset(Minecraft mc) {
		if (previous != null) {
			mc.options.setCameraType(previous);
		}
		previous = null;
		remaining = 0;
		age = 0;
		lightCinematic = false;
		gateTarget = null;
	}

	private static boolean hasEnemiesNear(Player player, double radius) {
		return !player.level().getEntitiesOfClass(LivingEntity.class,
				player.getBoundingBox().inflate(radius),
				e -> e instanceof Enemy && e.isAlive()).isEmpty();
	}
}
