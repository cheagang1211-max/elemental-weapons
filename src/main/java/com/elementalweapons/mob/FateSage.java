package com.elementalweapons.mob;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import com.elementalweapons.boss.PosedEntity;
import com.elementalweapons.growth.GrowthPayloads;

/** 운명의 현자: 말을 걸면 직업·스탯 초기화 창이 열림. 움직이지 않고 죽지 않음 */
public class FateSage extends PathfinderMob implements PosedEntity {
	public FateSage(EntityType<? extends FateSage> type, Level level) {
		super(type, level);
		this.setPersistenceRequired();
		this.setPermanentlyInvulnerable(true);
	}

	public static AttributeSupplier.Builder attributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 100).add(Attributes.MOVEMENT_SPEED, 0.0).add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 10.0F, 1.0F));
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (player instanceof ServerPlayer sp) ServerPlayNetworking.send(sp, GrowthPayloads.OpenSage.INSTANCE);
		return InteractionResult.SUCCESS;
	}

	@Override public boolean isPushable() { return false; }
	@Override public boolean removeWhenFarAway(double d) { return false; }
	@Override public int getAttackPose() { return 0; }
	@Override public boolean isFloatingBoss() { return false; }
}
