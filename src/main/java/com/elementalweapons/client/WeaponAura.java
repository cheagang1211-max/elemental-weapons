package com.elementalweapons.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.item.ElementalWeaponItem;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.Enhancement;
import com.elementalweapons.item.WeaponType;

/** 원소 무기를 들고 있으면 무기 주변에 속성 파티클이 떠다님 (클라이언트 전용) */
public final class WeaponAura {
	private WeaponAura() {}

	private static int tick = 0;

	public static void initialize() {
		ClientTickEvents.END_CLIENT_TICK.register(WeaponAura::tick);
	}

	private static void tick(Minecraft mc) {
		if (mc.level == null || mc.player == null || mc.isPaused()) return;
		tick++;
		ClientLevel level = mc.level;
		for (Player p : level.players()) {
			if (p.distanceToSqr(mc.player) > 48 * 48 || p.isInvisible()) continue;
			ItemStack held = p.getMainHandItem();
			if (!(held.getItem() instanceof ElementalWeaponItem w)) continue;
			boolean self = p == mc.player;
			int lv = Enhancement.level(held);
			int count = 1 + lv / 5;
			if (self && mc.options.getCameraType().isFirstPerson() && tick % 2 == 0) continue;
			for (int i = 0; i < count; i++) spawn(level, p, w.element(), w.type(), lv, self && mc.options.getCameraType().isFirstPerson());
		}
	}

	private static void spawn(ClientLevel level, Player p, Element el, WeaponType type, int lv, boolean firstPerson) {
		RandomSource r = level.getRandom();
		float yaw = p.yBodyRot * Mth.DEG_TO_RAD;
		Vec3 right = new Vec3(-Mth.cos(yaw), 0, -Mth.sin(yaw));
		Vec3 fwd = new Vec3(-Mth.sin(yaw), 0, Mth.cos(yaw));
		double len = switch (type) { case DAGGER -> 0.5; case SWORD -> 0.9; case GREATSWORD -> 1.3; case STAFF -> 1.5; };
		Vec3 hand;
		if (firstPerson) {
			Vec3 look = p.getLookAngle();
			hand = p.getEyePosition().add(look.scale(1.7)).add(right.scale(0.75)).add(0, -0.75, 0);
		} else {
			hand = p.position().add(right.scale(0.38)).add(fwd.scale(0.25)).add(0, 0.75, 0);
		}
		double along = r.nextDouble() * len;
		double ang = (tick * 0.25 + r.nextDouble() * Math.PI * 2);
		double rad = 0.12 + r.nextDouble() * 0.18;
		Vec3 pos = hand.add(0, along * (firstPerson ? 0.5 : 0.9), 0).add(fwd.scale(along * (firstPerson ? 0.2 : 0.35)))
				.add(right.scale(Math.cos(ang) * rad)).add(fwd.scale(Math.sin(ang) * rad));
		double vx = (r.nextDouble() - 0.5) * 0.01, vz = (r.nextDouble() - 0.5) * 0.01;
		// 속성색 빛 가루 (항상)
		float sz = firstPerson ? 0.15F + r.nextFloat() * 0.1F : 0.25F + r.nextFloat() * 0.2F + lv * 0.02F;
		level.addParticle(ElementFx.dust(el, sz), pos.x, pos.y, pos.z, vx, 0.01, vz);
		// 속성 고유 파티클 (가끔)
		if (r.nextInt(firstPerson ? 6 : 3) != 0) return;
		ParticleOptions sig = switch (el) {
			case ELECTRIC -> ParticleTypes.ELECTRIC_SPARK;
			case FIRE -> r.nextInt(4) == 0 ? ParticleTypes.FLAME : ParticleTypes.SMALL_FLAME;
			case WATER -> ParticleTypes.DRIPPING_WATER;
			case GRASS -> ParticleTypes.CHERRY_LEAVES;
			case WIND -> ParticleTypes.WHITE_ASH;
			case DARK -> ParticleTypes.PORTAL;
			case LIGHT -> ParticleTypes.END_ROD;
		};
		double vy = switch (el) { case FIRE -> 0.03; case LIGHT -> 0.015; case WATER -> -0.02; case WIND -> 0.0; default -> 0.005; };
		if (el == WIND_ELEMENT) { vx = Math.cos(ang + 1.6) * 0.05; vz = Math.sin(ang + 1.6) * 0.05; }
		if (el == Element.LIGHT && sig == ParticleTypes.END_ROD) { vx *= 0.3; vz *= 0.3; vy = 0.01; }
		level.addParticle(sig, pos.x, pos.y, pos.z, vx, vy, vz);
	}

	private static final Element WIND_ELEMENT = Element.WIND;
}
