package com.elementalweapons.spell;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

/**
 * 여러 틱(1틱 = 1/20초)에 걸쳐 진행되는 마법.
 * SpellManager가 매 틱마다 update()를 호출합니다.
 */
public abstract class Spell {
	protected final ServerLevel level;
	protected final Player caster;
	/** 시전 후 지난 틱 수 */
	protected int age = 0;

	protected Spell(ServerLevel level, Player caster) {
		this.level = level;
		this.caster = caster;
	}

	final boolean tick() {
		boolean finished = update();
		age++;
		return finished;
	}

	/** 한 틱 진행. 끝났으면 true를 반환하세요. */
	protected abstract boolean update();

	/** 마법이 끝날 때 정리 작업 (소환물 제거 등) */
	protected void onEnd() {}

	protected boolean casterGone() {
		return caster.isRemoved() || caster.level() != level;
	}
}
