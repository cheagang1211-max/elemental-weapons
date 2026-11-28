package com.elementalweapons.skill;

import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.growth.ModGrowth;
import com.elementalweapons.item.Element;
import com.elementalweapons.spell.Fx;

/**
 * 같은 무기라도 속성마다 다른 몸놀림과 추가 공격.
 *  우클릭(검·대검·망치·낫·단검) 시작 동작:
 *   전기 = 번개처럼 순간 이동하며 지나간 자리를 감전 / 불 = 앞으로 돌진하며 불태움 / 물 = 뒤로 미끄러지며 물결로 밀어냄
 *   풀 = 제자리에서 덩굴로 끌어당겨 묶고 회복 / 바람 = 높이 도약하며 주변을 띄움 / 어둠 = 가장 가까운 적 등 뒤로 순간 이동해 기습
 *   빛 = 살짝 떠올라 주변 적에게 빛을 내리꽂고 아군 회복
 *  콤보 마무리(3타) 뒤에는 속성별 추가타.
 */
public final class ElementMotion {
	private ElementMotion() {}

	public static float mult(Player p) { return SkillInfo.tierMult(SkillInfo.tier(ModGrowth.get(p).level())); }

	private static void hurt(ServerLevel l, ServerPlayer p, LivingEntity e, float dmg) {
		Fx.hurt(l, p, e, dmg);
		e.damageCooldownTime = 0;
	}

	/** 우클릭 시작 동작 */
	public static void opener(ServerLevel l, ServerPlayer p, Element el) {
		float m = mult(p);
		Vec3 pos = p.position(), f = Fx.flatLook(p), chest = pos.add(0, 1, 0);
		switch (el) {
			case ELECTRIC -> {   // 섬광 이동
				Vec3 to = Skills.safeBlink(l, p, f, 7);
				for (double d = 0; d <= to.distanceTo(pos); d += 1.0)
					for (LivingEntity e : Fx.enemies(l, pos.add(f.scale(d)).add(0, 1, 0), 1.8)) {
						hurt(l, p, e, 5 * m);
						e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 25, 4));
					}
				Fx.line(l, ElementFx.dust(el, 1.3F), chest, to.add(0, 1, 0), 0.4);
				Fx.line(l, ElementFx.particle(el), chest, to.add(0, 1, 0), 0.7);
				p.teleportTo(to.x, to.y, to.z);
				p.resetFallDistance();
				Fx.sound(l, to, SoundEvents.LIGHTNING_BOLT_THUNDER, 0.5F, 1.8F);
			}
			case FIRE -> {   // 화염 돌진
				Skills.launchSelf(p, new Vec3(f.x * 1.4, 0.18, f.z * 1.4));
				Vec3 ahead = pos.add(f.scale(3));
				for (LivingEntity e : Fx.enemies(l, ahead.add(0, 1, 0), 3.2)) {
					hurt(l, p, e, 5 * m);
					e.igniteForSeconds(4);
				}
				for (double d = 0; d < 5; d += 0.5) Fx.burst(l, ElementFx.particle(el), pos.add(f.scale(d)).add(0, 0.6, 0), 3, 0.3, 0.02);
				Fx.sound(l, pos, SoundEvents.BLAZE_SHOOT, 0.9F, 0.9F);
			}
			case WATER -> {   // 물살 미끄러짐: 뒤로 빠지며 앞을 쓸어냄
				Skills.launchSelf(p, new Vec3(-f.x * 1.1, 0.28, -f.z * 1.1));
				for (LivingEntity e : Fx.enemies(l, chest, 5.5)) {
					if (e.position().subtract(pos).normalize().dot(f) < 0.25) continue;
					hurt(l, p, e, 4 * m);
					Fx.launch(e, f.x * 1.3, 0.35, f.z * 1.3);
					e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 2));
				}
				for (int k = -3; k <= 3; k++) {
					double a = Math.atan2(f.z, f.x) + k * 0.25;
					for (double r = 1; r < 5.5; r += 0.7) Fx.at(l, ElementFx.particle(el), pos.add(Math.cos(a) * r, 0.5, Math.sin(a) * r));
				}
				Fx.sound(l, pos, SoundEvents.PLAYER_SPLASH_HIGH_SPEED, 0.8F, 1.1F);
			}
			case GRASS -> {   // 덩굴 속박: 끌어당겨 묶고 회복
				for (LivingEntity e : Fx.enemies(l, chest, 6.5)) {
					Vec3 d = pos.subtract(e.position()).multiply(1, 0, 1);
					if (d.lengthSqr() > 1) d = d.normalize().scale(0.8);
					Fx.launch(e, d.x, 0.15, d.z);
					e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 50, 5));
					hurt(l, p, e, 3 * m);
					Fx.line(l, ElementFx.dust(el, 0.9F), e.position().add(0, 0.5, 0), pos.add(0, 0.3, 0), 0.5);
				}
				p.heal(2 * m);
				ElementFx.groundRing(l, ElementFx.color(el), pos.add(0, 0.1, 0), 6.5, 2F, 14);
				Fx.sound(l, pos, SoundEvents.AZALEA_LEAVES_BREAK, 1F, 0.7F);
			}
			case WIND -> {   // 도약: 높이 뛰며 주변을 띄움
				for (LivingEntity e : Fx.enemies(l, chest, 4.5)) {
					hurt(l, p, e, 4 * m);
					Fx.launch(e, 0, 0.95, 0);
				}
				Skills.launchSelf(p, new Vec3(f.x * 0.7, 0.95, f.z * 0.7));
				p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 30, 0, false, false));
				Fx.ring(l, ElementFx.particle(el), pos.add(0, 0.3, 0), 3, 24, 0);
				Fx.sound(l, pos, SoundEvents.BREEZE_JUMP, 1F, 1F);
			}
			case DARK -> {   // 그림자 습격: 가장 가까운 적의 등 뒤로
				List<LivingEntity> es = Fx.enemies(l, chest, 10);
				if (!es.isEmpty()) {
					LivingEntity t = es.get(0);
					Vec3 back = Vec3.directionFromRotation(0, t.getYRot()).multiply(1, 0, 1).normalize().scale(-1.6);
					Vec3 to = t.position().add(back);
					Fx.burst(l, ElementFx.dust(el, 1.4F), chest, 20, 0.5, 0.05);
					p.teleportTo(to.x, t.getY(), to.z);
					Vec3 face = t.position().subtract(to);
					float yaw = (float) Math.toDegrees(Math.atan2(-face.x, face.z));
					p.setYRot(yaw); p.setYHeadRot(yaw);
					hurt(l, p, t, 6 * m);
					p.heal(2);
					Fx.burst(l, ElementFx.dust(el, 1.4F), t.position().add(0, 1, 0), 20, 0.5, 0.05);
				} else {
					Vec3 to = Skills.safeBlink(l, p, f, 4);
					p.teleportTo(to.x, to.y, to.z);
				}
				p.resetFallDistance();
				Fx.sound(l, p.position(), SoundEvents.ENDERMAN_TELEPORT, 0.7F, 0.6F);
			}
			case LIGHT -> {   // 성광 강림
				Skills.launchSelf(p, new Vec3(0, 0.55, 0));
				List<LivingEntity> es = Fx.enemies(l, chest, 8);
				for (int i = 0; i < Math.min(3, es.size()); i++) {
					LivingEntity e = es.get(i);
					hurt(l, p, e, 4 * m);
					e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 80, 0));
					for (double y = 0; y < 6; y += 0.5) Fx.at(l, ElementFx.particle(el), e.position().add(0, y, 0));
				}
				for (Player q : l.getEntitiesOfClass(Player.class, p.getBoundingBox().inflate(6))) q.heal(2);
				Fx.sound(l, pos, SoundEvents.BEACON_POWER_SELECT, 0.8F, 1.6F);
			}
		}
	}

	/** 콤보 마무리 뒤 속성 추가타 */
	public static void finisher(ServerLevel l, ServerPlayer p, Element el, Vec3 at) {
		float m = mult(p);
		Vec3 c = at.add(0, 1, 0);
		switch (el) {
			case ELECTRIC -> {   // 연쇄 번개
				Vec3 from = c;
				int n = 0;
				for (LivingEntity e : Fx.enemies(l, c, 7)) {
					if (n++ >= 3) break;
					hurt(l, p, e, 3 * m);
					Fx.line(l, ElementFx.dust(el, 0.9F), from, e.position().add(0, 1, 0), 0.4);
					from = e.position().add(0, 1, 0);
				}
			}
			case FIRE -> { for (LivingEntity e : Fx.enemies(l, c, 3.5)) { e.igniteForSeconds(3); hurt(l, p, e, 2 * m); } }
			case WATER -> {
				for (LivingEntity e : Fx.enemies(l, c, 4)) {
					Vec3 d = e.position().subtract(p.position()).multiply(1, 0, 1);
					if (d.lengthSqr() > 0.01) d = d.normalize();
					Fx.launch(e, d.x * 1.1, 0.3, d.z * 1.1);
					e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1));
				}
			}
			case GRASS -> {
				p.heal(2);
				for (LivingEntity e : Fx.enemies(l, c, 4)) e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 30, 4));
			}
			case WIND -> { for (LivingEntity e : Fx.enemies(l, c, 3.5)) Fx.launch(e, 0, 0.7, 0); }
			case DARK -> {
				int n = 0;
				for (LivingEntity e : Fx.enemies(l, c, 3.5)) { e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0)); n++; }
				p.heal(Math.min(3, n));
			}
			case LIGHT -> {
				for (Player q : l.getEntitiesOfClass(Player.class, p.getBoundingBox().inflate(6))) q.heal(1.5F);
				for (LivingEntity e : Fx.enemies(l, c, 4)) e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0));
			}
		}
	}
}
