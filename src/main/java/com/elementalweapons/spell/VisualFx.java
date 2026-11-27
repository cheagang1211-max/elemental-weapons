package com.elementalweapons.spell;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.item.Element;
import com.elementalweapons.mob.ModMobs;

/** 시전자 없는 입체 연출 하나 (보스 쓰러짐·페이즈 전환 등): 수명 동안 진행도 0→1 후 사라짐 */
public class VisualFx extends Spell {
	private final GrandVisual v;
	private final int life;

	private VisualFx(ServerLevel level, GrandVisual v, int life) {
		super(level, null);
		this.v = v;
		this.life = life;
	}

	/** size: 충격 고리 크기 배율 (0 = 기본) */
	public static void spawn(ServerLevel level, int kind, Element el, Vec3 pos, int life, float size) {
		GrandVisual g = ModMobs.GRAND.create(level, EntitySpawnReason.MOB_SUMMONED);
		if (g == null) return;
		g.addTag(com.elementalweapons.world.VanillaPurge.ALLOWED);
		g.setKind(kind); g.setElem(el.ordinal()); g.setPitch(size);
		g.snapTo(pos.x, pos.y, pos.z, level.getRandom().nextFloat() * 360, 0);
		level.addFreshEntity(g);
		SpellManager.add(new VisualFx(level, g, life));
	}

	@Override
	protected boolean update() {
		if (!v.isAlive()) return true;
		v.setPhase(Math.min(1, age / (float) life));
		v.life = 0;
		return age >= life;
	}

	@Override
	protected void onEnd() { if (v.isAlive()) v.discard(); }

	@Override
	protected boolean casterGone() { return false; }
}
