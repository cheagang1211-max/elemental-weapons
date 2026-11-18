package com.elementalweapons.spell;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import com.elementalweapons.ElementalWeaponsMod;

/** 진행 중인 마법들을 매 서버 틱마다 돌려주는 관리자 */
public final class SpellManager {
	private static final List<Spell> ACTIVE = new ArrayList<>();

	private SpellManager() {}

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> tickAll());
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			for (Spell s : ACTIVE) s.onEnd();
			ACTIVE.clear();
		});
	}

	public static void add(Spell spell) {
		ACTIVE.add(spell);
	}

	private static void tickAll() {
		if (ACTIVE.isEmpty()) return;
		// 마법이 진행 중에 새 마법을 추가할 수도 있으니 복사본으로 순회
		for (Spell spell : new ArrayList<>(ACTIVE)) {
			boolean finished;
			try {
				finished = spell.casterGone() || spell.tick();
			} catch (Exception e) {
				ElementalWeaponsMod.LOGGER.error("마법 처리 중 오류", e);
				finished = true;
			}
			if (finished) {
				spell.onEnd();
				ACTIVE.remove(spell);
			}
		}
	}
}
