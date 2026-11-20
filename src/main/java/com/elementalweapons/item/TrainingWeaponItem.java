package com.elementalweapons.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.fx.FxOptions;
import com.elementalweapons.growth.ModGrowth;
import com.elementalweapons.growth.Tutorial;
import com.elementalweapons.spell.Fx;

/**
 * 나무 무기 (직업을 고르면 받는 첫 무기, 10레벨 속성 진화 때 사라짐).
 * 우클릭: 종류별 간단한 기술 하나 · Z: 구르기
 */
public class TrainingWeaponItem extends Item {
	private static final int WOOD = 0xD8B27A, WHITE = 0xFFFFFF;
	private final WeaponType type;

	public TrainingWeaponItem(Item.Properties props, WeaponType type) {
		super(props);
		this.type = type;
	}

	public WeaponType type() { return type; }

	@Override
	public InteractionResult use(Level level, Player user, InteractionHand hand) {
		if (!ModGrowth.canUse(user, type)) { ModGrowth.denyMessage(user, type); return InteractionResult.FAIL; }
		if (type == WeaponType.SHIELD) return super.use(level, user, hand);
		if (type == WeaponType.BOW) { user.startUsingItem(hand); return InteractionResult.CONSUME; }
		ItemStack stack = user.getItemInHand(hand);
		if (type == WeaponType.DAGGER) {   // 짧은 돌진 (이동은 양쪽에서)
			Vec3 l = user.getLookAngle();
			user.setDeltaMovement(l.x * 1.1, Math.max(0.15, l.y * 0.6), l.z * 1.1);
			user.fallDistance = 0;
		}
		if (!(level instanceof ServerLevel sl)) return InteractionResult.SUCCESS;
		Vec3 eye = user.getEyePosition(), look = user.getLookAngle(), flat = Fx.flatLook(user);
		switch (type) {
			case DAGGER -> {
				Fx.burst(sl, FxOptions.of(FxOptions.SWIRL, WHITE, 0.4F, 10).spin(-20F), user.position().add(0, 0.6, 0), 4, 0.3, 0.02);
				cooldown(user, stack, 40);
			}
			case SWORD -> {   // 찌르기: 앞의 적 하나
				LivingEntity t = firstInFront(sl, user, 4.5);
				Fx.at(sl, FxOptions.of(FxOptions.SLASH, WHITE, 1.4F, 6).facing(user.getYRot() + 90, 0, -90), eye.add(look.scale(2)).add(0, -0.3, 0));
				if (t != null) { Fx.hurt(sl, user, t, 5F); Fx.launch(t, flat.x * 0.5, 0.2, flat.z * 0.5); }
				cooldown(user, stack, 30);
			}
			case GREATSWORD, SCYTHE -> {   // 넓게 베기
				double r = type == WeaponType.SCYTHE ? 3.4 : 3.2;
				Fx.at(sl, FxOptions.of(FxOptions.SLASH, WOOD, (float) r * 1.1F, 7).facing(user.getYRot(), 90, 0), user.position().add(0, 1.0, 0));
				for (LivingEntity e : Fx.enemies(sl, user.position(), r)) if (type == WeaponType.SCYTHE || inFront(user, e, 0.0)) Fx.hurt(sl, user, e, 6F);
				Fx.sound(sl, user.position(), SoundEvents.PLAYER_ATTACK_SWEEP, 1F, 0.8F);
				cooldown(user, stack, type == WeaponType.SCYTHE ? 50 : 60);
			}
			case HAMMER -> {   // 땅 내리치기
				com.elementalweapons.boss.ElementFx.groundRing(sl, WOOD, user.position(), 1.2, 2.6F, 10);
				for (LivingEntity e : Fx.enemies(sl, user.position(), 3.2)) { Fx.hurt(sl, user, e, 5F); Fx.launch(e, 0, 0.5, 0); }
				Fx.sound(sl, user.position(), SoundEvents.MACE_SMASH_GROUND, 1F, 1.1F);
				cooldown(user, stack, 70);
			}
			case STAFF -> {   // 마력 구슬
				Vec3 end = Fx.lookPoint(user, 16);
				LivingEntity t = firstInFront(sl, user, 16);
				if (t != null) end = t.position().add(0, t.getBbHeight() * 0.5, 0);
				Fx.line(sl, FxOptions.of(FxOptions.ORB, 0xB0D8FF, 0.18F, 6), eye.add(look.scale(0.8)), end, 0.6);
				Fx.at(sl, FxOptions.of(FxOptions.STAR, 0xE8F4FF, 0.6F, 6).grow(0.3F).spin(15F), end);
				if (t != null) Fx.hurt(sl, user, t, 5F);
				Fx.sound(sl, user.position(), SoundEvents.ILLUSIONER_CAST_SPELL, 0.8F, 1.6F);
				cooldown(user, stack, 25);
			}
			case BOOK -> {   // 작은 치유
				for (Player p : sl.getEntitiesOfClass(Player.class, user.getBoundingBox().inflate(6))) {
					p.heal(4F);
					Fx.burst(sl, FxOptions.of(FxOptions.LEAF, 0x9CF08A, 0.2F, 20).spin(8F).gravity(-0.01F), p.position().add(0, 1, 0), 6, 0.4, 0.02);
				}
				com.elementalweapons.boss.ElementFx.rune(sl, 0x9CF08A, user.position(), 1.6, 18, 4F);
				Fx.sound(sl, user.position(), SoundEvents.AMETHYST_BLOCK_CHIME, 1F, 1.2F);
				cooldown(user, stack, 120);
			}
			default -> { return InteractionResult.PASS; }
		}
		Tutorial.event(user, 1);
		return InteractionResult.SUCCESS;
	}

	private static void cooldown(Player p, ItemStack s, int t) { p.getCooldowns().addCooldown(s, t); }

	static boolean inFront(Player p, LivingEntity e, double dot) {
		Vec3 d = e.position().subtract(p.position()).multiply(1, 0, 1);
		return d.lengthSqr() < 1.0E-4 || d.normalize().dot(Fx.flatLook(p)) > dot;
	}

	static LivingEntity firstInFront(ServerLevel sl, Player p, double range) {
		LivingEntity best = null; double bd = Double.MAX_VALUE;
		Vec3 eye = p.getEyePosition(), look = p.getLookAngle();
		for (LivingEntity e : Fx.enemies(sl, eye.add(look.scale(range / 2)), range / 2 + 1.5)) {
			Vec3 to = e.position().add(0, e.getBbHeight() * 0.5, 0).subtract(eye);
			double along = to.dot(look);
			if (along < 0 || along > range) continue;
			if (to.subtract(look.scale(along)).length() > 1.4 + e.getBbWidth() * 0.5) continue;
			if (along < bd) { bd = along; best = e; }
		}
		return best;
	}

	// ---------------- 나무 활 ----------------
	@Override
	public net.minecraft.world.item.ItemUseAnimation getUseAnimation(ItemStack stack) {
		return type == WeaponType.BOW ? net.minecraft.world.item.ItemUseAnimation.BOW : super.getUseAnimation(stack);
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) { return type == WeaponType.BOW ? 72000 : super.getUseDuration(stack, entity); }

	@Override
	public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
		if (type != WeaponType.BOW || !(entity instanceof Player p)) return super.releaseUsing(stack, level, entity, timeLeft);
		float charge = Math.min(1F, (getUseDuration(stack, entity) - timeLeft) / 20F);
		if (charge < 0.2F) return false;
		if (level instanceof ServerLevel sl) {
			var a = new net.minecraft.world.entity.projectile.arrow.Arrow(sl, p, new ItemStack(net.minecraft.world.item.Items.ARROW), stack);
			a.pickup = net.minecraft.world.entity.projectile.arrow.AbstractArrow.Pickup.DISALLOWED;
			a.setBaseDamage(2.0 + 3.0 * charge);
			a.shootFromRotation(p, p.getXRot(), p.getYRot(), 0F, 1.0F + 2.0F * charge, 1F);
			sl.addFreshEntity(a);
			Fx.sound(sl, p.position(), SoundEvents.ARROW_SHOOT, 1F, 1F);
			p.getCooldowns().addCooldown(stack, 10);
			Tutorial.event(p, 1);
		}
		return true;
	}
}
