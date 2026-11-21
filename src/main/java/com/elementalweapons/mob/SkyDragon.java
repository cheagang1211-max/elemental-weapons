package com.elementalweapons.mob;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.boss.PosedEntity;
import com.elementalweapons.fx.FxOptions;
import com.elementalweapons.spell.Fx;

/**
 * 천공룡: 용 정류장에서 우클릭해 타는 비행 탈것.
 * 바라보는 방향으로 날고(W), Space 상승, 아래를 보며 W 하강, Shift 내리기.
 * 아무도 안 타고 20초가 지나면 자기 둥지로 돌아감. 죽지 않음.
 */
public class SkyDragon extends PathfinderMob implements PosedEntity {
	private BlockPos home;
	private int idle;
	private int boost;   // 앞으로 계속 날수록 빨라짐 (조종하는 쪽에서 계산)

	public SkyDragon(EntityType<? extends SkyDragon> type, Level level) {
		super(type, level);
		this.setNoGravity(true);
		this.setPersistenceRequired();
	}

	public static AttributeSupplier.Builder attributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 200).add(Attributes.MOVEMENT_SPEED, 0.0)
				.add(Attributes.FLYING_SPEED, 0.12).add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
	}

	public void setHome(BlockPos pos) { this.home = pos; }
	public BlockPos home() { return home; }

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 12.0F));
		this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
	}

	// ---------------- 타기 ----------------
	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (this.isVehicle() || player.isSecondaryUseActive()) return InteractionResult.PASS;
		if (!this.level().isClientSide()) {
			player.startRiding(this);
			player.sendSystemMessage(Component.literal("🐉 천공룡에 올라탔습니다!  W: 바라보는 방향으로 비행 · Space: 상승 · 아래를 보고 W: 하강 · Shift: 내리기")
					.withStyle(ChatFormatting.AQUA));
			Fx.sound((ServerLevel) this.level(), this.position(), SoundEvents.ENDER_DRAGON_FLAP, 1.0F, 1.3F);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public LivingEntity getControllingPassenger() {
		return this.getFirstPassenger() instanceof Player p ? p : super.getControllingPassenger();
	}

	@Override
	protected Vec3 getRiddenInput(Player player, Vec3 travel) {
		float strafe = player.xxa * 0.6F, up = 0, fwd = 0;
		if (player.zza != 0) {
			float pitch = player.getXRot() * Mth.DEG_TO_RAD;
			fwd = Mth.cos(pitch);
			up = -Mth.sin(pitch);
			if (player.zza < 0) { fwd *= -0.4F; up *= -0.4F; }
		}
		if (player.isJumping()) up += 0.7F;
		return new Vec3(strafe, up, fwd);
	}

	@Override
	protected void tickRidden(Player player, Vec3 input) {
		super.tickRidden(player, input);
		Vec2 rot = new Vec2(player.getXRot() * 0.5F, player.getYRot());
		float y = this.getYRot() + Mth.wrapDegrees(rot.y - this.getYRot()) * 0.25F;
		this.setRot(y, rot.x);
		this.yRotO = this.yBodyRot = this.yHeadRot = y;
		boost = player.zza > 0 ? Math.min(60, boost + 1) : Math.max(0, boost - 3);
	}

	@Override
	public void travel(Vec3 input) {
		if (this.isVehicle() && this.getControllingPassenger() instanceof Player) {
			float sp = 0.10F + 0.07F * boost / 60F;     // 최고 약 1.9칸/틱
			this.travelFlying(input, sp, sp, sp);
		} else {
			this.setDeltaMovement(this.getDeltaMovement().scale(0.6));
			this.travelFlying(input, 0.02F, 0.02F, 0.02F);
		}
	}

	@Override public boolean isFlyingVehicle() { return true; }

	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dims, float scale) {
		return new Vec3(0, dims.height() * 0.92, -0.15);
	}

	@Override
	public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
		Vec3 side = Vec3.directionFromRotation(0, this.getYRot() + 90).scale(this.getBbWidth() * 0.8);
		return this.position().add(side).add(0, 0.2, 0);
	}

	// ---------------- 둥지로 돌아가기 ----------------
	@Override
	public void tick() {
		super.tick();
		if (this.level() instanceof ServerLevel sl) {
			if (this.isVehicle()) {
				idle = 0;
				if (this.tickCount % 3 == 0 && this.getDeltaMovement().lengthSqr() > 0.4)
					Fx.at(sl, FxOptions.of(FxOptions.SWIRL, 0xBFF6E6, 0.5F, 14).spin(14F), this.position().add(0, 1.2, 0).subtract(this.getDeltaMovement().scale(2)));
			} else if (home != null && ++idle > 400 && this.distanceToSqr(Vec3.atBottomCenterOf(home)) > 16) {
				Fx.burst(sl, FxOptions.of(FxOptions.SMOKE, 0xD8F0E8, 0.8F, 18).grow(1.8F), this.position().add(0, 1, 0), 12, 1.2, 0.03);
				final BlockPos hp = home;
				if (!sl.getEntitiesOfClass(SkyDragon.class, new net.minecraft.world.phys.AABB(hp).inflate(4), d -> d != this && hp.equals(d.home())).isEmpty()) { this.discard(); return; }
				this.teleportTo(home.getX() + 0.5, home.getY(), home.getZ() + 0.5);
				this.setDeltaMovement(Vec3.ZERO);
				idle = 0;
			}
		}
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		if (home != null) output.putLong("Home", home.asLong());
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		long h = input.getLongOr("Home", Long.MIN_VALUE);
		home = h == Long.MIN_VALUE ? null : BlockPos.of(h);
	}

	@Override public boolean isInvulnerableTo(ServerLevel level, DamageSource source) { return !source.isCreativePlayer(); }
	@Override public boolean isPushable() { return false; }
	@Override protected void doPush(Entity e) {}
	@Override public boolean removeWhenFarAway(double d) { return false; }
	@Override public boolean causeFallDamage(double d, float m, DamageSource s) { return false; }
	@Override public int getAttackPose() { return this.isVehicle() && this.getDeltaMovement().lengthSqr() > 0.2 ? 2 : 0; }
	@Override public boolean isFloatingBoss() { return this.isVehicle(); }
}
