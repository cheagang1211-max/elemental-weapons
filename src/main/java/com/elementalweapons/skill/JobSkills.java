package com.elementalweapons.skill;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.boss.ElementalBoss;
import com.elementalweapons.growth.Evolution;
import com.elementalweapons.growth.Jobs;
import com.elementalweapons.growth.ModGrowth;
import com.elementalweapons.item.Element;
import com.elementalweapons.spell.Fx;
import com.elementalweapons.spell.MagicCircle;
import com.elementalweapons.spell.SpellManager;

/** 전직 기술 (V키): 2차 전직 갈래마다 하나, 3차에 '각성'으로 더 크고 강해짐 */
public final class JobSkills {
	private JobSkills() {}

	public static final int SLOT = 3;
	private static final Map<UUID, Long> READY = new HashMap<>();

	public static int cooldown(int stage) { return stage >= 3 ? 320 : 400; }

	public static void cast(ServerPlayer p) {
		Jobs.Data d = Jobs.get(p);
		int cls = ModGrowth.get(p).cls();
		int el = Evolution.element(p);
		if (d.stage() < 2 || d.branch() == 0 || cls <= 0 || el < 0) {
			p.sendOverlayMessage(Component.literal("V 기술은 " + Jobs.LV2 + "레벨 2차 전직 후에 열립니다 (천공성 전직 교관)").withStyle(ChatFormatting.GOLD));
			return;
		}
		ServerLevel l = (ServerLevel) p.level();
		long now = l.getGameTime();
		Long r = READY.get(p.getUUID());
		if (r != null && r > now) {
			p.sendOverlayMessage(Component.literal(Jobs.skill(cls, d.branch(), d.stage()) + " 대기 " + String.format("%.1f", (r - now) / 20.0) + "초").withStyle(ChatFormatting.GRAY));
			return;
		}
		boolean awake = d.stage() >= 3;
		float m = SkillInfo.tierMult(SkillInfo.tier(ModGrowth.get(p).level())) * (awake ? 1.8F : 1F);
		Element e = Element.values()[el];
		boolean ok;
		try { ok = run(l, p, cls, d.branch(), awake, e, m); }
		catch (Throwable t) { com.elementalweapons.ElementalWeaponsMod.LOGGER.error("[JOB] 기술 오류", t); ok = false; }
		if (!ok) return;
		int cd = (int) Math.round(cooldown(d.stage()) * (1 - ModGrowth.paragon(p).cooldownCut()));
		READY.put(p.getUUID(), now + cd);
		SkillNet.sendCooldown(p, SLOT, cd);
		p.sendOverlayMessage(Component.literal("✦ " + Jobs.skill(cls, d.branch(), d.stage())).withStyle(st -> st.withColor(ElementFx.color(e)).withBold(true)));
	}

	private static void hurt(ServerLevel l, Player p, LivingEntity e, float dmg) { Fx.hurt(l, p, e, dmg); e.setInvulnerableTime(0); }

	private static int area(ServerLevel l, Player p, Vec3 c, double r, float dmg) {
		int n = 0;
		for (LivingEntity e : Fx.enemies(l, c, r)) { hurt(l, p, e, dmg); n++; }
		return n;
	}

	private static void circle(ServerLevel l, Player p, Element e, Vec3 c, double r, int dur, boolean awake) {
		if (awake) SpellManager.add(new MagicCircle(l, p, e, c.add(0, 0.1, 0), r, dur, true));
		else ElementFx.rune(l, ElementFx.color(e), c.add(0, 0.1, 0), r, Math.min(dur, 40), 4F);
	}

	private static boolean run(ServerLevel l, ServerPlayer p, int cls, int b, boolean awake, Element e, float m) {
		Vec3 pos = p.position(), look = Fx.flatLook(p), chest = pos.add(0, 1, 0);
		int c1 = ElementFx.color(e);
		switch (cls * 10 + b) {
			case 11 -> {   // 원소술사: 원소 폭격 — 하늘에서 원소 덩어리가 연달아 떨어짐
				Vec3 tg = Fx.lookPoint(p, 30);
				SpellManager.add(new MagicCircle(l, p, e, tg.add(0, 12, 0), awake ? 8 : 6, 60, false));
				int n = awake ? 9 : 5;
				Skills.run(l, p, a -> {
					if (a % 6 == 0 && a / 6 < n) {
						var rnd = l.getRandom();
						Vec3 hitAt = tg.add((rnd.nextDouble() - 0.5) * 8, 0, (rnd.nextDouble() - 0.5) * 8);
						Fx.line(l, ElementFx.dust(e, 1.6F), hitAt.add(0, 12, 0), hitAt, 0.4);
						ElementFx.impact(l, e, hitAt, 3);
						area(l, p, hitAt.add(0, 1, 0), 3.5, 7 * m);
						Fx.sound(l, hitAt, SoundEvents.GENERIC_EXPLODE, 0.6F, 1.3F);
					}
					return a >= n * 6 + 2;
				});
			}
			case 12 -> {   // 소환술사: 원소 정령 소환 — 주위를 도는 정령이 적을 쏨
				int spirits = awake ? 4 : 2;
				if (awake) SpellManager.add(new MagicCircle(l, p, e, pos.add(0, 0.1, 0), 4, 40, false));
				Skills.run(l, p, a -> {
					Vec3 cc = p.position().add(0, 1.6, 0);
					for (int k = 0; k < spirits; k++) {
						double ang = a * 0.15 + k * Math.PI * 2 / spirits;
						Vec3 sp = cc.add(Math.cos(ang) * 1.6, Math.sin(a * 0.2 + k) * 0.3, Math.sin(ang) * 1.6);
						ElementFx.orb(l, e, sp, 0.3);
						if ((a + k * 4) % 14 == 0) {
							List<LivingEntity> es = Fx.enemies(l, sp, 14);
							if (!es.isEmpty()) {
								LivingEntity t = es.get(l.getRandom().nextInt(es.size()));
								Fx.line(l, ElementFx.dust(e, 0.8F), sp, t.position().add(0, t.getBbHeight() / 2, 0), 0.4);
								hurt(l, p, t, 3.5F * m);
							}
						}
					}
					return a >= 200;
				});
				Fx.sound(l, pos, SoundEvents.EVOKER_PREPARE_SUMMON, 1F, 1.3F);
			}
			case 21 -> {   // 광전사: 광폭화
				int dur = awake ? 240 : 160;
				p.addEffect(new MobEffectInstance(MobEffects.STRENGTH, dur, awake ? 2 : 1));
				p.addEffect(new MobEffectInstance(MobEffects.SPEED, dur, 1));
				p.addEffect(new MobEffectInstance(MobEffects.HASTE, dur, 1));
				area(l, p, chest, 5, 5 * m);
				ElementFx.groundRing(l, c1, pos.add(0, 0.1, 0), 5, 2F, 14);
				Fx.burst(l, ElementFx.particle(e), chest, 40, 0.8, 0.2);
				Skills.run(l, p, a -> { if (a % 5 == 0) Fx.burst(l, ElementFx.dust(e, 1.0F), p.position().add(0, 1, 0), 4, 0.4, 0.03); return a >= dur; });
				Fx.sound(l, pos, SoundEvents.RAVAGER_ROAR, 1F, 1.2F);
			}
			case 22 -> {   // 파쇄자: 대지 분쇄 — 앞으로 땅이 갈라지며 적을 띄움
				int lines = awake ? 3 : 1;
				Set<LivingEntity> once = new HashSet<>();
				circle(l, p, e, pos, 3, 30, awake);
				Skills.run(l, p, a -> {
					if (a % 2 == 0 && a / 2 < 9) {
						double d = 2 + a / 2 * 1.4;
						for (int k = 0; k < lines; k++) {
							double ang = Math.atan2(look.z, look.x) + (k - (lines - 1) / 2.0) * 0.45;
							Vec3 at = pos.add(Math.cos(ang) * d, 0, Math.sin(ang) * d);
							ElementFx.impact(l, e, at, 1.5);
							for (LivingEntity t : Fx.enemies(l, at.add(0, 1, 0), 2.2)) if (once.add(t)) { hurt(l, p, t, 7 * m); Fx.launch(t, 0, 1.0, 0); }
						}
						Fx.sound(l, pos.add(look.scale(d)), SoundEvents.GENERIC_EXPLODE, 0.4F, 0.7F);
					}
					return a >= 18;
				});
			}
			case 31 -> {   // 성기사: 신성 결계
				double r = awake ? 9 : 6;
				Vec3 cc = pos;
				SpellManager.add(new MagicCircle(l, p, e, cc.add(0, 0.1, 0), r, 160, false));
				Skills.run(l, p, a -> {
					if (a % 20 == 0) {
						for (Player q : l.getEntitiesOfClass(Player.class, new net.minecraft.world.phys.AABB(cc, cc).inflate(r))) {
							q.heal(2);
							q.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 30, awake ? 1 : 0));
						}
						area(l, p, cc.add(0, 1, 0), r, 2.5F * m);
					}
					return a >= 160;
				});
				Fx.sound(l, pos, SoundEvents.BEACON_ACTIVATE, 1.2F, 1.2F);
			}
			case 32 -> {   // 용기사: 용의 도약 — 높이 뛰어올라 내리꽂음
				Vec3 tg = Fx.lookPoint(p, awake ? 22 : 16);
				Skills.launchSelf(p, new Vec3(0, 1.6, 0));
				Skills.run(l, p, a -> {
					if (a == 10) {
						p.teleportTo(tg.x, tg.y + 6, tg.z);
						Skills.launchSelf(p, new Vec3(0, -3, 0));
					}
					if (a == 14 || (a > 10 && p.onGround() && a < 30)) {
						circle(l, p, e, tg, 4.5, 30, awake);
						ElementFx.impact(l, e, p.position(), 4.5);
						for (LivingEntity t : Fx.enemies(l, p.position().add(0, 1, 0), 4.8)) {
							hurt(l, p, t, 9 * m);
							Vec3 dd = t.position().subtract(p.position()).multiply(1, 0, 1);
							if (dd.lengthSqr() > 0.01) dd = dd.normalize();
							Fx.launch(t, dd.x * 1.2, 0.6, dd.z * 1.2);
						}
						if (awake) {   // 각성: 착지 후 용의 숨결
							Vec3 f = Fx.flatLook(p);
							for (double d = 1; d < 8; d += 0.8) Fx.burst(l, ElementFx.particle(e), p.position().add(f.scale(d)).add(0, 1, 0), 6, d * 0.2, 0.03);
							for (LivingEntity t : Fx.enemies(l, p.position().add(f.scale(4)).add(0, 1, 0), 4)) hurt(l, p, t, 6 * m);
						}
						Fx.sound(l, p.position(), SoundEvents.ENDER_DRAGON_GROWL, 0.8F, 1.4F);
						p.resetFallDistance();
						return true;
					}
					return a >= 30;
				});
			}
			case 41 -> {   // 그림자: 그림자 분신 — 적 사이를 순간 이동하며 벰
				List<LivingEntity> es = Fx.enemies(l, chest, 12);
				if (es.isEmpty()) { p.sendOverlayMessage(Component.literal("주변에 적이 없습니다").withStyle(ChatFormatting.GRAY)); return false; }
				int hits = awake ? 7 : 4;
				Vec3 start = pos;
				Skills.run(l, p, a -> {
					if (a % 3 == 0 && a / 3 < hits) {
						List<LivingEntity> cur = Fx.enemies(l, start.add(0, 1, 0), 12);
						if (!cur.isEmpty()) {
							LivingEntity t = cur.get((a / 3) % cur.size());
							Vec3 from = p.position().add(0, 1, 0);
							Vec3 to = t.position().add(t.getLookAngle().multiply(1, 0, 1).scale(-1.2));
							p.teleportTo(to.x, t.getY(), to.z);
							Fx.line(l, ElementFx.dust(e, 1.1F), from, p.position().add(0, 1, 0), 0.4);
							hurt(l, p, t, 6 * m);
						}
					}
					if (a == hits * 3 + 1) { p.teleportTo(start.x, start.y, start.z); p.resetFallDistance(); return true; }
					return false;
				});
				Fx.sound(l, pos, SoundEvents.ENDERMAN_TELEPORT, 1F, 0.8F);
			}
			case 42 -> {   // 칼날무희: 칼날 폭풍
				double r = awake ? 5 : 4;
				p.addEffect(new MobEffectInstance(MobEffects.SPEED, 70, 2));
				circle(l, p, e, pos, r, 60, awake);
				Skills.run(l, p, a -> {
					Vec3 cc = p.position().add(0, 1, 0);
					for (int k = 0; k < 6; k++) {
						double ang = a * 0.5 + k * Math.PI / 3;
						Fx.at(l, ElementFx.dust(e, 1.0F), cc.add(Math.cos(ang) * r * 0.7, 0, Math.sin(ang) * r * 0.7));
					}
					if (a % 5 == 0) area(l, p, cc, r, (awake ? 3 : 2) * m);
					return a >= 60;
				});
				Fx.sound(l, pos, SoundEvents.PLAYER_ATTACK_SWEEP, 1.2F, 1.5F);
			}
			case 51 -> {   // 사제: 대치유
				SpellManager.add(new MagicCircle(l, p, e, pos.add(0, 0.1, 0), awake ? 8 : 6, 50, false));
				for (Player q : l.getEntitiesOfClass(Player.class, p.getBoundingBox().inflate(10))) {
					q.heal(16);
					q.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
					q.removeEffect(MobEffects.POISON); q.removeEffect(MobEffects.WITHER); q.removeEffect(MobEffects.SLOWNESS); q.removeEffect(MobEffects.WEAKNESS);
					if (awake) q.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 400, 1));
					for (double y = 0; y < 4; y += 0.4) Fx.at(l, ElementFx.dust(e, 1.0F), q.position().add(0, y, 0));
				}
				Fx.sound(l, pos, SoundEvents.BEACON_POWER_SELECT, 1.2F, 1.6F);
			}
			case 52 -> {   // 음유시인: 전투의 노래
				int amp = awake ? 1 : 0;
				circle(l, p, e, pos, awake ? 10 : 8, 60, awake);
				for (Player q : l.getEntitiesOfClass(Player.class, p.getBoundingBox().inflate(12))) {
					q.addEffect(new MobEffectInstance(MobEffects.SPEED, 300, amp));
					q.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 300, amp));
					q.addEffect(new MobEffectInstance(MobEffects.HASTE, 300, amp));
				}
				for (LivingEntity t : Fx.enemies(l, chest, 8)) {
					t.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 160, 1 + amp));
					t.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 160, amp));
				}
				Skills.run(l, p, a -> { if (a % 4 == 0) Fx.ring(l, ElementFx.dust(e, 0.8F), p.position().add(0, 2.2, 0), 1.2, 6, a * 0.2); return a >= 60; });
				Fx.sound(l, pos, SoundEvents.AMETHYST_BLOCK_CHIME, 1.5F, 1.2F);
			}
			case 61 -> {   // 영혼 수확자: 영혼 수확 — 끌어모아 베고 처형
				double r = awake ? 12 : 8;
				SpellManager.add(new MagicCircle(l, p, e, pos.add(0, 0.1, 0), r * 0.7, 30, false));
				for (LivingEntity t : Fx.enemies(l, chest, r)) {
					Vec3 dd = pos.subtract(t.position()).multiply(1, 0, 1);
					if (dd.lengthSqr() > 1) dd = dd.normalize().scale(Math.min(1.6, dd.length() * 0.25));
					Fx.launch(t, dd.x, 0.25, dd.z);
				}
				Skills.run(l, p, a -> {
					if (a == 10) {
						int n = 0;
						for (LivingEntity t : Fx.enemies(l, p.position().add(0, 1, 0), 5)) {
							if (awake && !(t instanceof ElementalBoss) && t.getHealth() < t.getMaxHealth() * 0.15F) t.kill(l);   // 각성: 처형
							else hurt(l, p, t, 9 * m);
							n++;
						}
						p.heal(Math.min(10, n * 2));
						Fx.ring(l, ElementFx.dust(e, 1.4F), p.position().add(0, 1, 0), 4, 36, 0);
						Fx.sound(l, p.position(), SoundEvents.WITHER_SHOOT, 0.6F, 0.7F);
						return true;
					}
					return false;
				});
			}
			case 62 -> {   // 저주술사: 재앙의 낙인
				List<LivingEntity> es = Fx.enemies(l, chest, 10);
				if (es.isEmpty()) { p.sendOverlayMessage(Component.literal("주변에 적이 없습니다").withStyle(ChatFormatting.GRAY)); return false; }
				circle(l, p, e, pos, 6, 40, awake);
				for (LivingEntity t : es) {
					t.addEffect(new MobEffectInstance(MobEffects.WITHER, 120, 1));
					t.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 1));
					t.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 120, 1));
				}
				Skills.run(l, p, a -> {
					if (a % 20 == 0) for (LivingEntity t : es) if (t.isAlive()) {
						hurt(l, p, t, 3 * m);
						Fx.ring(l, ElementFx.dust(e, 0.7F), t.position().add(0, t.getBbHeight() + 0.3, 0), 0.5, 8, a);
					}
					if (a == 120 && awake) for (LivingEntity t : es) if (t.isAlive()) { ElementFx.impact(l, e, t.position(), 2.5); area(l, p, t.position().add(0, 1, 0), 3, 6 * m); }
					return a >= 120;
				});
				Fx.sound(l, pos, SoundEvents.EVOKER_CAST_SPELL, 1F, 0.6F);
			}
			case 71 -> {   // 저격수: 관통 저격
				Vec3 eye = p.getEyePosition(), dir = p.getLookAngle();
				int rays = awake ? 3 : 1;
				for (int k = 0; k < rays; k++) {
					Vec3 dk = dir.yRot((float) ((k - (rays - 1) / 2.0) * 0.12));
					Vec3 end = eye.add(dk.scale(40));
					var hit = l.clip(new net.minecraft.world.level.ClipContext(eye, end, net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, p));
					end = hit.getLocation();
					Fx.line(l, ElementFx.dust(e, 1.3F), eye, end, 0.3);
					Set<LivingEntity> once = new HashSet<>();
					for (double d = 0; d < eye.distanceTo(end); d += 0.8)
						for (LivingEntity t : Fx.enemies(l, eye.add(dk.scale(d)), 1.2)) if (once.add(t)) hurt(l, p, t, (awake ? 16 : 12) * m);
					ElementFx.impact(l, e, end, 1.5);
				}
				Fx.sound(l, pos, SoundEvents.TRIDENT_THUNDER.value(), 0.7F, 1.8F);
			}
			case 72 -> {   // 사냥꾼: 화살비
				Vec3 tg = Fx.lookPoint(p, 30);
				double r = awake ? 7 : 5;
				int dur = awake ? 80 : 60;
				SpellManager.add(new MagicCircle(l, p, e, tg.add(0, 14, 0), r, dur, false));
				Skills.run(l, p, a -> {
					var rnd = l.getRandom();
					for (int k = 0; k < 4; k++) {
						double ang = rnd.nextDouble() * Math.PI * 2, rr = Math.sqrt(rnd.nextDouble()) * r;
						Vec3 at = tg.add(Math.cos(ang) * rr, 0, Math.sin(ang) * rr);
						Fx.line(l, ElementFx.dust(e, 0.6F), at.add(0, 6, 0), at, 0.7);
					}
					if (a % 5 == 0) area(l, p, tg.add(0, 1, 0), r, 2.5F * m);
					return a >= dur;
				});
				Fx.sound(l, tg, SoundEvents.ARROW_SHOOT, 1.5F, 0.6F);
			}
			default -> { return false; }
		}
		return true;
	}
}
