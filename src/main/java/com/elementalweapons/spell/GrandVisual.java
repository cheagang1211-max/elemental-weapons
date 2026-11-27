package com.elementalweapons.spell;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * 대형 기술(직업×속성)의 거대 입체 모형. KIND = 모양, ELEM = 속성 색, PHASE = 진행(0~1), STRIKE = 동작 시작 틱, PITCH = 앞뒤 기울기.
 */
public class GrandVisual extends HeavenVisual {
	private static final EntityDataAccessor<Integer> KIND = SynchedEntityData.defineId(GrandVisual.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> ELEM = SynchedEntityData.defineId(GrandVisual.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> PITCH = SynchedEntityData.defineId(GrandVisual.class, EntityDataSerializers.FLOAT);

	public GrandVisual(EntityType<? extends GrandVisual> type, Level level) { super(type, level); }

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder b) {
		super.defineSynchedData(b);
		b.define(KIND, 0);
		b.define(ELEM, 0);
		b.define(PITCH, 0F);
	}

	public int kind() { return entityData.get(KIND); }
	public void setKind(int k) { entityData.set(KIND, k); }
	public int elem() { return entityData.get(ELEM); }
	public void setElem(int e) { entityData.set(ELEM, e); }
	public float pitch() { return entityData.get(PITCH); }
	public void setPitch(float p) { entityData.set(PITCH, p); }
	/** 진행 정도 (0~1) = OPEN */
	public float phase() { return open(); }
	public void setPhase(float v) { setOpen(v); }
}
