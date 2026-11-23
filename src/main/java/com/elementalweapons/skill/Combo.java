package com.elementalweapons.skill;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.fx.FxOptions;
import com.elementalweapons.growth.ModGrowth;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.ElementalWeaponItem;
import com.elementalweapons.item.TrainingWeaponItem;
import com.elementalweapons.item.WeaponType;
import com.elementalweapons.spell.Fx;

/**
 * 좌클릭 콤보: 연속으로 휘두르면 3번째에 무기마다 다른 마무리 기술.
 * 지팡이·마법서·활은 좌클릭마다 약한 원거리 공격, 3번째는 강한 공격.
 */
public final class Combo {
	private Combo() {}

	private static final Map<UUID, long[]> STATE = new HashMap<>();   // {마지막 휘두름 시각, 콤보 수}
	private static final int WINDOW = 30;

	private static int interval(WeaponType t) {
		return switch (t) {
			case DAGGER -> 6; case SWORD, BOW -> 9; case STAFF, BOOK -> 10; case SCYTHE, SHIELD -> 11; case GREATSWORD -> 13; case HAMMER -> 15;
		};
	}

	public static String finisherName(WeaponType t) {
		return switch (t) {
			case SWORD -> "회전 베기"; case DAGGER -> "급소 찌르기"; case GREATSWORD -> "대지 가르기"; case HAMMER -> "땅 울림";
			case SCYTHE -> "사신의 원무"; case SHIELD -> "방패 강타"; case STAFF -> "원소 폭발탄"; case BOOK -> "축복의 파동"; case BOW -> "삼연사";
		};
	}

	public static void swing(ServerPlayer p) {
		if (!p.isAlive() || p.isSpectator()) return;
		ItemStack held = p.getMainHandItem();
		WeaponType type; Element el = null;
		if (held.getItem() instanceof ElementalWeaponItem w) { type = w.type(); el = w.element(); }
		else if (held.getItem() instanceof TrainingWeaponItem t) type = t.type();
		else return;
		if (!ModGrowth.canUse(p, type)) return;
		ServerLevel l = (ServerLevel) p.level();
		long now = l.getGameTime();
		long[] s = STATE.computeIfAbsent(p.getUUID(), k -> new long[]{-999, 0});
		if (now - s[0] < interval(type)) return;          // 너무 빠른 연타는 무시
		s[1] = now - s[0] > WINDOW ? 1 : s[1] + 1;
		s[0] = now;
		boolean fin = s[1] >= 3;
		if (fin) s[1] = 0;
		if (fin && el != null) com.elementalweapons.growth.Jobs.flourish(p, el, false);
		float m = el == null ? 0.8F : SkillInfo.tierMult(SkillInfo.tier(ModGrowth.get(p).level()));
		int rgb = el == null ? 0xFFF2DA : ElementFx.color(el);
		try {
			switch (type) {
				case STAFF -> staff(l, p, el, rgb, m, fin);
				case BOOK -> book(l, p, el, rgb, m, fin);
				case BOW -> bow(l, p, m, fin);
				default -> {
					if (type == WeaponType.SCYTHE && !fin) crescent(l, p, rgb, m);
					if (fin) {
						melee(l, p, type, el, rgb, m);
						if (el != null) ElementMotion.finisher(l, p, el, p.position().add(Fx.flatLook(p).scale(2)));
					}
				}
			}
		} catch (Throwable t) {
			com.elementalweapons.ElementalWeaponsMod.LOGGER.error("[COMBO] 오류", t);
		}
		int shown = fin ? 3 : (int) s[1];
		p.sendOverlayMessage(Component.literal(fin ? "✦ " + finisherName(type) + "!" : "⚔ 콤보 " + shown + " / 3")
				.withStyle(fin ? ChatFormatting.GOLD : ChatFormatting.WHITE));
	}

	public static void forget(UUID id) { STATE.remove(id); }

	// ---------------- 근접 마무리 ----------------
	private static void hurt(ServerLevel l, ServerPlayer p, LivingEntity e, float dmg) {
		Fx.hurt(l, p, e, dmg);
		e.setInvulnerableTime(0);   // 바로 이어지는 일반 공격도 들어가게
	}

	private static void melee(ServerLevel l, ServerPlayer p, WeaponType t, Element el, int rgb, float m) {
		Vec3 look = Fx.flatLook(p), pos = p.position(), front = pos.add(look.scale(2.0));
		switch (t) {
			case SWORD -> {   // 회전 베기
				for (LivingEntity e : Fx.enemies(l, pos.add(0, 1, 0), 3.8)) hurt(l, p, e, 6 * m);
				for (int k = 0; k < 3; k++) Fx.ring(l, FxOptions.of(FxOptions.SLASH, rgb, 0.9F, 8).facing((float) (k * 120), 999, 0), pos.add(0, 1 + k * 0.2, 0), 2.6, 10, k);
				Fx.sound(l, pos, SoundEvents.PLAYER_ATTACK_SWEEP, 1F, 0.8F);
			}
			case DAGGER -> {   // 급소 찌르기: 뒤에서 찌르면 1.6배
				var es = Fx.enemies(l, front.add(0, 1, 0), 2.4);
				if (!es.isEmpty()) {
					LivingEntity e = es.get(0);
					Vec3 face = Vec3.directionFromRotation(0, e.getYRot());
					boolean back = face.dot(look) > 0.4;
					hurt(l, p, e, (back ? 11 : 7) * m);
					Fx.burst(l, FxOptions.of(FxOptions.SPARK, back ? 0xFF4040 : rgb, 0.5F, 10), e.position().add(0, 1, 0), 10, 0.4, 0.15);
					if (back) p.sendOverlayMessage(Component.literal("✦ 급소!").withStyle(ChatFormatting.RED));
				}
				Fx.sound(l, pos, SoundEvents.PLAYER_ATTACK_CRIT, 1F, 1.4F);
			}
			case GREATSWORD -> {   // 대지 가르기: 앞으로 뻗는 충격파
				for (int d = 1; d <= 7; d++) {
					Vec3 c = pos.add(look.scale(d));
					Fx.at(l, FxOptions.of(FxOptions.RING, rgb, 0.8F, 12).grow(1.6F).flat(), c.add(0, 0.1, 0));
					for (LivingEntity e : Fx.enemies(l, c.add(0, 0.8, 0), 1.6)) { hurt(l, p, e, 8 * m); Fx.launch(e, look.x * 0.6, 0.35, look.z * 0.6); }
				}
				Fx.sound(l, pos, SoundEvents.GENERIC_EXPLODE, 0.6F, 1.4F);
			}
			case HAMMER -> {   // 땅 울림: 주변을 띄우고 느리게
				for (LivingEntity e : Fx.enemies(l, pos, 4.5)) {
					hurt(l, p, e, 7 * m);
					Fx.launch(e, 0, 0.6, 0);
					e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1));
				}
				ElementFx.groundRing(l, rgb, pos.add(0, 0.1, 0), 4.5, 2.2F, 14);
				Fx.sound(l, pos, SoundEvents.ANVIL_LAND, 0.7F, 0.6F);
			}
			case SCYTHE -> {   // 사신의 원무: 주변을 끌어당기며 크게 한 바퀴, 맞힌 수만큼 크게 회복
				int n = 0;
				for (LivingEntity e : Fx.enemies(l, pos.add(0, 1, 0), 5.0)) {
					Vec3 d = pos.subtract(e.position()).multiply(1, 0, 1);
					if (d.lengthSqr() > 1) d = d.normalize().scale(0.6);
					hurt(l, p, e, 10 * m); n++;
					Fx.launch(e, d.x, 0.2, d.z);
					Fx.line(l, FxOptions.of(FxOptions.SWIRL, 0x9A60FF, 0.4F, 12), e.position().add(0, 1, 0), p.position().add(0, 1, 0), 0.6);
				}
				if (n > 0) p.heal(Math.min(6, n * 1.5F));
				for (int k = 0; k < 4; k++) Fx.ring(l, FxOptions.of(FxOptions.SLASH, k % 2 == 0 ? 0xB080FF : rgb, 1.2F, 9).facing((float) (p.getYRot() + k * 90), 999, 0), pos.add(0, 1 + k * 0.1, 0), 3.2, 12, k);
				Fx.sound(l, pos, SoundEvents.PLAYER_ATTACK_SWEEP, 1.2F, 0.5F);
				Fx.sound(l, pos, SoundEvents.WITHER_SHOOT, 0.3F, 1.6F);
			}
			case SHIELD -> {   // 방패 강타: 앞으로 크게 밀치고 잠깐 단단해짐
				for (LivingEntity e : Fx.enemies(l, front.add(0, 1, 0), 2.6)) { hurt(l, p, e, 5 * m); Fx.launch(e, look.x * 1.5, 0.4, look.z * 1.5); }
				p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 40, 0));
				Fx.burst(l, FxOptions.of(FxOptions.STAR, rgb, 0.6F, 10), front.add(0, 1, 0), 8, 0.4, 0.1);
				Fx.sound(l, pos, SoundEvents.SHIELD_BLOCK, 1F, 0.7F);
			}
			default -> { }
		}
		if (el != null) ElementFx.impact(l, el, front.add(0, 0.6, 0), 1.6);
	}

	/** 낫 기본 휘두르기: 앞쪽 반원을 함께 베고 조금 흡혈 */
	private static void crescent(ServerLevel l, ServerPlayer p, int rgb, float m) {
		Vec3 pos = p.position(), look = Fx.flatLook(p);
		int n = 0;
		for (LivingEntity e : Fx.enemies(l, pos.add(0, 1, 0), 3.8)) {
			Vec3 d = e.position().subtract(pos).multiply(1, 0, 1);
			if (d.lengthSqr() > 0.01 && d.normalize().dot(look) < 0.15) continue;
			hurt(l, p, e, 3.5F * m); n++;
		}
		if (n > 0) p.heal(Math.min(2, n * 0.5F));
		Fx.at(l, FxOptions.of(FxOptions.SLASH, rgb, 1.5F, 7).facing(p.getYRot(), 90, 0), pos.add(look.scale(1.2)).add(0, 1.1, 0));
	}

	// ---------------- 원거리 좌클릭 ----------------
	/** 시선을 따라가며 처음 닿는 적 (벽에 막힘) */
	private static LivingEntity ray(ServerLevel l, ServerPlayer p, double range, Vec3[] end) {
		Vec3 eye = p.getEyePosition(), look = p.getLookAngle();
		HitResult block = l.clip(new ClipContext(eye, eye.add(look.scale(range)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p));
		double max = block.getType() == HitResult.Type.MISS ? range : block.getLocation().distanceTo(eye);
		for (double d = 1; d <= max; d += 0.5) {
			Vec3 c = eye.add(look.scale(d));
			var es = Fx.enemies(l, c, 1.1);
			if (!es.isEmpty()) { end[0] = c; return es.get(0); }
		}
		end[0] = eye.add(look.scale(max));
		return null;
	}

	private static void staff(ServerLevel l, ServerPlayer p, Element el, int rgb, float m, boolean fin) {
		Vec3[] end = new Vec3[1];
		LivingEntity e = ray(l, p, 18, end);
		Fx.line(l, FxOptions.of(FxOptions.ORB, rgb, fin ? 0.35F : 0.22F, 8), p.getEyePosition().add(0, -0.3, 0), end[0], 0.5);
		if (fin) {
			for (LivingEntity t : Fx.enemies(l, end[0], 2.8)) hurt(l, p, t, 6 * m);
			if (el != null) ElementFx.impact(l, el, end[0], 2.2);
			else Fx.burst(l, FxOptions.of(FxOptions.STAR, rgb, 0.7F, 12), end[0], 10, 0.6, 0.1);
			Fx.sound(l, end[0], SoundEvents.FIREWORK_ROCKET_BLAST, 1F, 1.2F);
		} else {
			if (e != null) hurt(l, p, e, 3 * m);
			Fx.burst(l, FxOptions.of(FxOptions.SPARK, rgb, 0.4F, 8), end[0], 5, 0.2, 0.08);
			Fx.sound(l, p.position(), SoundEvents.AMETHYST_BLOCK_CHIME, 0.8F, 1.6F);
		}
	}

	private static void book(ServerLevel l, ServerPlayer p, Element el, int rgb, float m, boolean fin) {
		if (fin) {   // 축복의 파동: 나와 주변 아군 회복 + 주변 적 피해
			for (var ally : l.getEntitiesOfClass(net.minecraft.world.entity.player.Player.class, p.getBoundingBox().inflate(6))) ally.heal(3);
			for (LivingEntity t : Fx.enemies(l, p.position(), 4.5)) hurt(l, p, t, 4 * m);
			ElementFx.groundRing(l, 0xFFF2A0, p.position().add(0, 0.1, 0), 5, 2.4F, 16);
			Fx.burst(l, FxOptions.of(FxOptions.PETAL, 0xFFF2A0, 0.4F, 18), p.position().add(0, 1.2, 0), 16, 1.2, 0.04);
			Fx.sound(l, p.position(), SoundEvents.AMETHYST_BLOCK_RESONATE, 1F, 1.4F);
			return;
		}
		Vec3[] end = new Vec3[1];
		LivingEntity e = ray(l, p, 16, end);
		Fx.line(l, FxOptions.of(FxOptions.STAR, 0xFFF2A0, 0.2F, 8), p.getEyePosition().add(0, -0.3, 0), end[0], 0.7);
		if (e != null) { hurt(l, p, e, 2.5F * m); p.heal(1); }
		Fx.sound(l, p.position(), SoundEvents.AMETHYST_BLOCK_CHIME, 0.8F, 1.9F);
	}

	private static void bow(ServerLevel l, ServerPlayer p, float m, boolean fin) {
		int n = fin ? 3 : 1;
		for (int i = 0; i < n; i++) {
			Arrow a = new Arrow(l, p, new ItemStack(net.minecraft.world.item.Items.ARROW), p.getMainHandItem());
			a.pickup = net.minecraft.world.entity.projectile.arrow.AbstractArrow.Pickup.DISALLOWED;
			float spread = (i - (n - 1) / 2F) * 6F;
			a.shootFromRotation(p, p.getXRot(), p.getYRot() + spread, 0, 2.4F, 1.0F);
			a.setBaseDamage(1.2 * m);
			l.addFreshEntity(a);
		}
		Fx.sound(l, p.position(), SoundEvents.ARROW_SHOOT, 1F, fin ? 0.9F : 1.3F);
	}
}
