package com.elementalweapons.spell;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 * 천국의 문 연출용 거대 입체 모형 (천상의 원환문 · 대천사). 마법이 위치와 열림 정도를 매 틱 정해 줌.
 * 부딪히거나 맞지 않고, 저장되지 않으며, 마법이 끝나면 사라짐.
 */
public class HeavenVisual extends Mob {
	private static final EntityDataAccessor<Float> OPEN = SynchedEntityData.defineId(HeavenVisual.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> STRIKE = SynchedEntityData.defineId(HeavenVisual.class, EntityDataSerializers.INT);
	public int life;

	public HeavenVisual(EntityType<? extends HeavenVisual> type, Level level) {
		super(type, level);
		setNoAi(true);
		setNoGravity(true);
		setInvulnerable(true);
		setSilent(true);
		noPhysics = true;
	}

	public static AttributeSupplier.Builder attributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 1000.0);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder b) {
		super.defineSynchedData(b);
		b.define(OPEN, 0F);
		b.define(STRIKE, 0);
	}

	/** 문이 열린 정도 / 대천사의 날개 펼침 (0~1) */
	public float open() { return entityData.get(OPEN); }
	public void setOpen(float v) { entityData.set(OPEN, v); }
	/** 대천사가 창을 내지른 게임 시간 (찌르기 동작용) */
	public int strike() { return entityData.get(STRIKE); }
	public void setStrike(int tick) { entityData.set(STRIKE, tick); }

	@Override
	public void tick() {
		super.tick();
		if (!level().isClientSide() && ++life > 40) discard();   // 마법이 갑자기 끝나면 2초 뒤 정리
	}

	@Override public boolean isPickable() { return false; }
	@Override public boolean isPushable() { return false; }
	@Override public boolean shouldBeSaved() { return false; }
	@Override public boolean hurtServer(ServerLevel level, DamageSource source, float amount) { return false; }
	@Override protected void doPush(net.minecraft.world.entity.Entity e) {}
	@Override public boolean removeWhenFarAway(double d) { return false; }
}
