package com.elementalweapons.mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.boss.BossFx;
import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.boss.PosedEntity;
import com.elementalweapons.item.Element;
import com.elementalweapons.spell.Fx;

/** 속성 적대 몹 (근접/원거리/비행/추적자) */
public class ElementalMob extends Monster implements PosedEntity {
	private static final EntityDataAccessor<Integer> POSE = SynchedEntityData.defineId(ElementalMob.class, EntityDataSerializers.INT);
	public static final Map<EntityType<?>, MobDef> DEFS = new HashMap<>();

	private MobDef def;
	private int cooldown = 40;
	private static final class Orb { Vec3 pos, vel; int life; Orb(Vec3 p, Vec3 v) { pos = p; vel = v; life = 50; } }
	private final List<Orb> orbs = new ArrayList<>();

	public ElementalMob(EntityType<? extends ElementalMob> type, Level level) {
		super(type, level);
		if (def().kind() == MobDef.Kind.FLYING) this.setNoGravity(true);
		this.xpReward = 10;
	}

	public static AttributeSupplier.Builder attributes(MobDef d) {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, d.health())
				.add(Attributes.ATTACK_DAMAGE, d.damage())
				.add(Attributes.MOVEMENT_SPEED, d.speed())
				.add(Attributes.FLYING_SPEED, 0.6)
				.add(Attributes.FOLLOW_RANGE, 32.0);
	}

	public MobDef def() {
		if (def == null) def = DEFS.get(this.getType());
		return def;
	}

	public Element element() { return def().element(); }

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(POSE, 0);
	}
	@Override public int getAttackPose() { return this.entityData.get(POSE); }
	@Override public boolean isFloatingBoss() { return def().kind() == MobDef.Kind.FLYING; }

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		if (def().kind() != MobDef.Kind.FLYING) {
			this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, false));
			this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.9));
		}
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	@Override
	public boolean doHurtTarget(ServerLevel level, Entity target) {
		boolean hit = super.doHurtTarget(level, target);
		if (hit && target instanceof Player p) ElementFx.onHit(element(), p);
		return hit;
	}

	@Override
	protected void customServerAiStep(ServerLevel level) {
		super.customServerAiStep(level);
		LivingEntity target = this.getTarget();
		if (cooldown > 0) cooldown--;
		if (this.tickCount % 10 == 0) this.entityData.set(POSE, 0);
		switch (def().kind()) {
			case FLYING -> fly(level, target);
			case RANGED -> {
				if (target != null && cooldown == 0 && this.distanceToSqr(target) < 16 * 16 && this.getSensing().hasLineOfSight(target)) {
					Vec3 from = this.position().add(0, getBbHeight() * 0.7, 0);
					Vec3 dir = target.position().add(0, target.getBbHeight() * 0.5, 0).subtract(from).normalize();
					orbs.add(new Orb(from, dir.scale(0.6)));
					Fx.sound(level, from, SoundEvents.BLAZE_SHOOT, 0.8F, 1.6F);
					this.entityData.set(POSE, 1);
					cooldown = 45;
				}
			}
			case STALKER -> {
				if (target != null && cooldown == 0 && this.distanceToSqr(target) > 5 * 5 && this.distanceToSqr(target) < 20 * 20) {
					Vec3 look = target.getLookAngle();
					Vec3 behind = target.position().subtract(new Vec3(look.x, 0, look.z).normalize().scale(2.0));
					Vec3 g = BossFx.ground(level, behind.x, behind.z);
					Fx.burst(level, ElementFx.particle(element()), this.position().add(0, 1, 0), 20, 0.5, 0.05);
					this.teleportTo(g.x, g.y, g.z);
					Fx.sound(level, g, SoundEvents.ENDERMAN_TELEPORT, 1.0F, 0.8F);
					cooldown = 100;
				}
			}
			default -> { }
		}
	}

	/** 비행 몹: 대상 위를 맴돌다가 급강하 */
	private void fly(ServerLevel level, LivingEntity target) {
		Vec3 want;
		double speed = 0.3;
		if (target == null) {
			if (this.tickCount % 60 == 0 || this.getDeltaMovement().lengthSqr() < 1.0E-3) {
				Vec3 g = BossFx.ground(level, getX() + (getRandom().nextDouble() - 0.5) * 16, getZ() + (getRandom().nextDouble() - 0.5) * 16);
				this.setDeltaMovement(g.add(0, 6, 0).subtract(position()).normalize().scale(0.15));
			}
			return;
		}
		if (cooldown > 15) {
			double a = this.tickCount * 0.05;
			want = target.position().add(Math.cos(a) * 5, 5, Math.sin(a) * 5);
		} else {
			want = target.position().add(0, 0.5, 0); // 급강하
			speed = 0.7;
			this.entityData.set(POSE, 2);
			if (this.distanceToSqr(target) < 2.5 * 2.5) {
				this.doHurtTarget(level, target);
				cooldown = 60;
			}
		}
		Vec3 d = want.subtract(position());
		Vec3 v = d.lengthSqr() > speed * speed ? d.normalize().scale(speed) : d;
		this.setDeltaMovement(this.getDeltaMovement().scale(0.6).add(v.scale(0.4)));
		float yaw = (float) (Mth.atan2(d.z, d.x) * Mth.RAD_TO_DEG) - 90.0F;
		this.setYRot(yaw);
		this.setYBodyRot(yaw);
	}

	@Override
	public void tick() {
		super.tick();
		if (!(this.level() instanceof ServerLevel level) || orbs.isEmpty()) return;
		orbs.removeIf(o -> {
			o.pos = o.pos.add(o.vel);
			o.life--;
			ElementFx.orb(level, element(), o.pos, 0.25);
			List<Player> hit = BossFx.players(level, o.pos, 1.0);
			boolean ground = !level.getBlockState(BlockPos.containing(o.pos)).isAir();
			if (!hit.isEmpty() || ground || o.life <= 0) {
				for (Player p : hit) {
					p.hurtServer(level, level.damageSources().mobAttack(this), (float) def().damage());
					ElementFx.onHit(element(), p);
				}
				Fx.burst(level, ElementFx.particle(element()), o.pos, 8, 0.3, 0.05);
				return true;
			}
			return false;
		});
	}

	@Override
	public boolean causeFallDamage(double distance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
		return def().kind() != MobDef.Kind.FLYING && super.causeFallDamage(distance, multiplier, source);
	}
}
