package com.elementalweapons.client.anim;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.entity.LivingEntity;

/**
 * 엔티티마다 휘두르기가 새로 시작될 때를 잡아 콤보 단계(1→2→3→1)를 셈.
 * 서버 콤보와 같은 규칙: 1.5초 안에 다시 휘두르면 다음 단계.
 */
public final class SwingTracker {
	private SwingTracker() {}

	private static final class S { Object last; long start = -999; int step = 1; }
	private static final Map<Integer, S> MAP = new HashMap<>();
	/** 우클릭 특수 동작 예약: 엔티티 id → 받은 시각(ms) */
	private static final Map<Integer, Long> SPECIAL = new HashMap<>();

	public static void markSpecial(int id) { SPECIAL.put(id, System.currentTimeMillis()); }

	public static int step(LivingEntity e) {
		S s = MAP.computeIfAbsent(e.getId(), k -> new S());
		long now = e.tickCount;
		Object cur = e.isSwinging() ? e.getCurrentSwing() : null;   // 휘두를 때마다 새 기록이 생김
		if (cur != null && cur != s.last) {
			Long sp = SPECIAL.remove(e.getId());
			if (sp != null && System.currentTimeMillis() - sp < 600) { s.step = 4; s.start = now - 99; }
			else { s.step = now - s.start > 30 || s.step == 4 ? 1 : s.step % 3 + 1; s.start = now; }
		}
		s.last = cur;
		if (MAP.size() > 400) MAP.clear();
		return s.step;
	}
}
