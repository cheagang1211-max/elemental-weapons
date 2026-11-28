package com.elementalweapons.skill;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.fx.FxOptions;
import com.elementalweapons.item.ElementalWeaponItem;
import com.elementalweapons.item.TrainingWeaponItem;
import com.elementalweapons.item.WeaponType;
import com.elementalweapons.spell.Fx;

/**
 * 단검(암살자) 고유 능력
 * - 급소: 단검 평타 30% 확률 치명타(+80%), 뒤에서 찌르면 +60%
 * - 출혈: 단검 공격마다 출혈 1중첩(최대 6) → 0.5초마다 중첩 × 공격력 12% 피해, 4초 지속(맞을 때마다 갱신)
 */
public final class DaggerArts {
	private DaggerArts() {}

	private static final class Bleed { int stacks; long until; UUID owner; ServerLevel level; LivingEntity target; }
	private static final Map<Integer, Bleed> BLEED = new HashMap<>();
	private static boolean inner;   // 추가 피해를 넣는 중 (다시 걸리지 않게)
	public static final int MAX_STACKS = 6;

	public static boolean holdsDagger(LivingEntity p) {
		var it = p.getMainHandItem().getItem();
		return (it instanceof ElementalWeaponItem w && w.type() == WeaponType.DAGGER) || (it instanceof TrainingWeaponItem t && t.type() == WeaponType.DAGGER);
	}

	public static boolean behind(LivingEntity attacker, LivingEntity target) {
		Vec3 face = Vec3.directionFromRotation(0, target.getYRot());
		Vec3 to = target.position().subtract(attacker.position()).multiply(1, 0, 1);
		return to.lengthSqr() > 1.0E-4 && face.dot(to.normalize()) > 0.35;
	}

	/** 출혈 중첩 추가 */
	public static void bleed(ServerLevel l, ServerPlayer p, LivingEntity e, int n) {
		if (!e.isAlive()) return;
		Bleed b = BLEED.computeIfAbsent(e.getId(), k -> new Bleed());
		b.stacks = Math.min(MAX_STACKS, b.stacks + n);
		b.until = l.getGameTime() + 80; b.owner = p.getUUID(); b.level = l; b.target = e;
		Fx.burst(l, FxOptions.of(FxOptions.SPARK, 0xD01828, 0.35F, 10).gravity(0.04F), e.position().add(0, e.getBbHeight() * 0.6, 0), 3 + n * 2, 0.25, 0.08);
	}

	public static int stacks(LivingEntity e) { Bleed b = BLEED.get(e.getId()); return b == null ? 0 : b.stacks; }

	/** 기술 피해 (치명·뒤치기 포함) */
	public static float strike(ServerLevel l, ServerPlayer p, LivingEntity e, float dmg, boolean sureCrit) {
		boolean back = behind(p, e);
		boolean crit = sureCrit || l.getRandom().nextFloat() < 0.3F;
		float d = dmg * (crit ? 1.8F : 1F) * (back ? 1.6F : 1F);
		inner = true;
		try { Fx.hurt(l, p, e, d); } finally { inner = false; }
		if (crit || back) mark(l, e, back);
		return d;
	}

	private static void mark(ServerLevel l, LivingEntity e, boolean back) {
		Vec3 c = e.position().add(0, e.getBbHeight() * 0.65, 0);
		Fx.at(l, FxOptions.of(FxOptions.SLASH, back ? 0xFF3040 : 0xFFE070, 1.1F, 6).facing(l.getRandom().nextFloat() * 360, 0, 50), c);
		Fx.burst(l, FxOptions.of(FxOptions.STAR, back ? 0xFF6070 : 0xFFF0A0, 0.3F, 8), c, 5, 0.3, 0.12);
		Fx.sound(l, c, SoundEvents.PLAYER_ATTACK_CRIT, 0.9F, back ? 0.8F : 1.5F);
	}

	public static void initialize() {
		// 단검 평타: 치명·뒤치기 추가 피해 + 출혈
		ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, base, taken, blocked) -> {
			if (inner || blocked || taken <= 0) return;
			if (!(source.getEntity() instanceof ServerPlayer p) || source.getDirectEntity() != p || !holdsDagger(p)) return;
			if (!source.is(net.minecraft.world.damagesource.DamageTypes.PLAYER_ATTACK)) return;
			ServerLevel l = (ServerLevel) p.level();
			boolean back = behind(p, entity);
			boolean crit = l.getRandom().nextFloat() < 0.3F;
			float extra = taken * ((crit ? 0.8F : 0F) + (back ? 0.6F : 0F));
			if (extra > 0) {
				inner = true;
				try { entity.setInvulnerableTime(0); entity.hurtServer(l, l.damageSources().playerAttack(p), extra); } finally { inner = false; }
				mark(l, entity, back);
				if (back) p.sendOverlayMessage(Component.literal(crit ? "✦ 급소 치명타!" : "✦ 급소!").withStyle(ChatFormatting.RED));
			}
			bleed(l, p, entity, 1);
		});
		ServerTickEvents.END_SERVER_TICK.register(DaggerArts::tick);
	}

	private static void tick(MinecraftServer server) {
		if (BLEED.isEmpty()) return;
		Iterator<Bleed> it = BLEED.values().iterator();
		while (it.hasNext()) {
			Bleed b = it.next();
			LivingEntity e = b.target;
			if (e == null || !e.isAlive() || e.isRemoved() || b.level.getGameTime() > b.until) { it.remove(); continue; }
			if (b.level.getGameTime() % 10 != 0) continue;
			ServerPlayer p = server.getPlayerList().getPlayer(b.owner);
			if (p == null) { it.remove(); continue; }
			float atk = (float) p.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
			inner = true;
			try { e.setInvulnerableTime(0); e.hurtServer(b.level, b.level.damageSources().indirectMagic(p, p), b.stacks * atk * 0.12F); } finally { inner = false; }
			Fx.burst(b.level, FxOptions.of(FxOptions.SPARK, 0xB01020, 0.3F, 12).gravity(0.05F), e.position().add(0, e.getBbHeight() * 0.5, 0), 2 + b.stacks, 0.3, 0.03);
		}
		if (BLEED.size() > 2000) BLEED.clear();
	}

	public static void forget(Entity e) { BLEED.remove(e.getId()); }
}
