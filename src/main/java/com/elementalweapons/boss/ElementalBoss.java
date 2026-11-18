package com.elementalweapons.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.item.Element;

/**
 * 모든 보스의 공통 기반.
 *
 *  - 큰 숫자 체력(long): 바닐라 체력은 항상 가득 채워 두고, 실제 체력은 bigHealth로 관리
 *  - 보스바: 이름 + "현재 체력 / 최대 체력" 숫자 표시
 *  - 페이즈: 체력 66% / 33% 아래로 내려가면 2, 3 페이즈
 *  - 패턴 실행기: 쉬는 시간 → 패턴 하나 골라 실행 → 쉬는 시간 ...
 *  - 공중에 떠서 대상 주위를 맴돌며 이동
 */
public abstract class ElementalBoss extends Monster {
	private static final EntityDataAccessor<Integer> ATTACK_POSE =
			SynchedEntityData.defineId(ElementalBoss.class, EntityDataSerializers.INT);

	protected final ServerBossEvent bossBar;
	private long bigHealth;
	private int phase = 1;

	private BossPattern current = null;
	private BossPattern last = null;
	private int patternTick = 0;
	private int restTicks = 60;
	private boolean defeated = false;
	/** 진단용: AI가 몇 번 실행됐는지 */
	public int debugAiSteps = 0;
	public String debugState = "";
	/** 테스트 전용 강제 표적 (실제 게임에서는 항상 null) */
	public LivingEntity debugForcedTarget = null;

	protected ElementalBoss(EntityType<? extends ElementalBoss> type, Level level) {
		super(type, level);
		this.bossBar = new ServerBossEvent(UUID.randomUUID(), Component.literal(""), barColor(), BossEvent.BossBarOverlay.NOTCHED_10);
		this.bossBar.setDarkenScreen(true);
		this.bigHealth = maxBigHealth();
		this.setNoGravity(true);
		this.setPersistenceRequired();
		this.xpReward = 1000;
		updateBossBar();
	}

	// ===== 각 보스가 정하는 것 =====
	public abstract Element element();
	public abstract long maxBigHealth();
	public abstract Component bossName();
	protected abstract BossEvent.BossBarColor barColor();
	protected abstract List<BossPattern> patterns();
	/** 쓰러졌을 때 보상 */
	protected abstract void dropRewards(ServerLevel level);
	/** 페이즈가 바뀔 때 (연출, 대사 등) */
	protected void onPhaseChange(ServerLevel level, int newPhase) {}
	/** 패턴 사이 쉬는 시간 (틱) */
	protected int restTime(int phase) {
		return switch (phase) { case 1 -> 50; case 2 -> 35; default -> 22; };
	}
	/** 대상과 유지할 거리, 높이 */
	protected double hoverDistance() { return 7.0; }
	protected double hoverHeight() { return 3.5; }
	protected double moveSpeed() { return 0.28 + phase * 0.04; }

	// ===== 큰 숫자 체력 =====
	public long getBigHealth() { return bigHealth; }
	public int getPhase() { return phase; }

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		if (this.isDeadOrDying() || amount <= 0) return false;
		// /kill 같은 무조건 피해는 그대로
		if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return super.hurtServer(level, source, amount);
		}
		if (this.isInvulnerableTo(level, source)) return false;

		Entity attacker = source.getEntity();
		BigDamage.Result result = BigDamage.compute(this, attacker, amount, this.getRandom());

		// 바닐라 쪽은 맞는 연출/어그로만 (체력은 다시 가득)
		super.hurtServer(level, source, 0.01F);
		this.setHealth(this.getMaxHealth());

		bigHealth = Math.max(0, bigHealth - result.damage());
		if (attacker instanceof Player p) {
			p.sendOverlayMessage(BigDamage.hitMessage(result));
		}
		updateBossBar();
		checkPhase(level);

		if (bigHealth <= 0) defeat(level, source);
		return true;
	}

	/** 체력 0: 보상을 직접 떨어뜨리고 쓰러짐 (바닐라 전리품 경로에 의존하지 않음) */
	private void defeat(ServerLevel level, DamageSource source) {
		if (defeated) return;
		defeated = true;
		dropRewards(level);
		Component msg = Component.literal("☆ ").withStyle(ChatFormatting.GOLD)
				.append(bossName().copy())
				.append(Component.literal(" 을(를) 쓰러뜨렸다!").withStyle(ChatFormatting.GOLD));
		for (ServerPlayer p : level.players()) {
			if (p.distanceToSqr(this) < 128 * 128) p.sendSystemMessage(msg);
		}
		bossBar.removeAllPlayers();
		this.setHealth(0.0F);
		this.die(source);
	}

	/** 개발/테스트용: 플레이어 없이 큰 숫자 피해를 직접 줌 */
	public void debugDamage(ServerLevel level, long amount) {
		bigHealth = Math.max(0, bigHealth - amount);
		updateBossBar();
		checkPhase(level);
		if (bigHealth <= 0) defeat(level, level.damageSources().generic());
	}

	private void checkPhase(ServerLevel level) {
		double ratio = (double) bigHealth / maxBigHealth();
		int newPhase = ratio > 0.66 ? 1 : ratio > 0.33 ? 2 : 3;
		if (newPhase > phase) {
			phase = newPhase;
			onPhaseChange(level, phase);
		}
	}

	protected void updateBossBar() {
		float progress = (float) ((double) bigHealth / maxBigHealth());
		bossBar.setProgress(Mth.clamp(progress, 0.0F, 1.0F));
		bossBar.setName(bossName().copy()
				.append(Component.literal("   " + BigDamage.fmt(bigHealth) + " / " + BigDamage.fmt(maxBigHealth()))
						.withStyle(ChatFormatting.WHITE)));
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		bossBar.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		bossBar.removePlayer(player);
	}

	// ===== 저장 =====
	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putLong("BigHealth", bigHealth);
		output.putInt("Phase", phase);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		bigHealth = input.getLongOr("BigHealth", maxBigHealth());
		phase = input.getIntOr("Phase", 1);
		updateBossBar();
	}

	@Override
	public boolean removeWhenFarAway(double distance) {
		return false;
	}

	// ===== 애니메이션용 공격 자세 (클라이언트와 동기화) =====
	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(ATTACK_POSE, 0);
	}

	public int getAttackPose() { return this.entityData.get(ATTACK_POSE); }
	protected void setAttackPose(int pose) { this.entityData.set(ATTACK_POSE, pose); }

	// ===== AI =====
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 48.0F));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
	}

	@Override
	protected void customServerAiStep(ServerLevel level) {
		super.customServerAiStep(level);
		debugAiSteps++;
		LivingEntity target = debugForcedTarget != null ? debugForcedTarget : this.getTarget();
		debugState = (target == null ? "no-target" : target.isAlive() ? "target-ok" : "target-dead")
				+ " pattern=" + (current == null ? "-" : current.name()) + " rest=" + restTicks;

		if (target == null || !target.isAlive()
				|| (target instanceof Player p && (p.isCreative() || p.isSpectator()))) {
			if (current != null) endPattern();
			this.setDeltaMovement(this.getDeltaMovement().scale(0.8));
			return;
		}

		faceTowards(target);

		if (current == null) {
			hoverAround(target);
			if (--restTicks <= 0) startPattern();
		} else {
			current.action().tick(level, target, patternTick);
			patternTick++;
			if (current != null && patternTick >= current.duration()) endPattern();
		}
	}

	private void startPattern() {
		List<BossPattern> usable = new ArrayList<>();
		int total = 0;
		for (BossPattern p : patterns()) {
			if (p.minPhase() <= phase && (p != last || patterns().size() == 1)) {
				usable.add(p);
				total += p.weight();
			}
		}
		if (usable.isEmpty()) { restTicks = 20; return; }
		int roll = this.getRandom().nextInt(total);
		for (BossPattern p : usable) {
			roll -= p.weight();
			if (roll < 0) { current = p; break; }
		}
		patternTick = 0;
	}

	protected void endPattern() {
		last = current;
		current = null;
		patternTick = 0;
		restTicks = restTime(phase);
		setAttackPose(0);
	}

	protected void faceTowards(Entity target) {
		Vec3 d = target.position().subtract(this.position());
		float yaw = (float) (Mth.atan2(d.z, d.x) * Mth.RAD_TO_DEG) - 90.0F;
		this.setYRot(yaw);
		this.setYHeadRot(yaw);
		this.setYBodyRot(yaw);
	}

	/** 대상 주위를 일정 거리/높이로 맴돌며 이동 */
	protected void hoverAround(LivingEntity target) {
		Vec3 me = this.position();
		Vec3 flat = new Vec3(me.x - target.getX(), 0, me.z - target.getZ());
		if (flat.lengthSqr() < 1.0E-3) flat = new Vec3(1, 0, 0);
		// 거리 유지 + 천천히 옆으로 돌기
		Vec3 dir = flat.normalize();
		double ang = 0.02;
		Vec3 orbit = new Vec3(dir.x * Math.cos(ang) - dir.z * Math.sin(ang), 0, dir.x * Math.sin(ang) + dir.z * Math.cos(ang));
		Vec3 wanted = target.position().add(orbit.scale(hoverDistance())).add(0, hoverHeight(), 0);
		moveTowards(wanted, moveSpeed());
	}

	protected void moveTowards(Vec3 wanted, double speed) {
		Vec3 delta = wanted.subtract(this.position());
		Vec3 vel = delta.lengthSqr() > speed * speed ? delta.normalize().scale(speed) : delta;
		this.setDeltaMovement(this.getDeltaMovement().scale(0.6).add(vel.scale(0.4)));
	}

	/** 패턴 중 제자리에 멈추기 */
	protected void hold() {
		this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean causeFallDamage(double distance, float multiplier, DamageSource source) {
		return false;
	}
}
