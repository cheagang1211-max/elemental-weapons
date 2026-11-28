package com.elementalweapons.skill;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.IntPredicate;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.fx.FxOptions;
import com.elementalweapons.growth.ModGrowth;
import com.elementalweapons.growth.Tutorial;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.ElementalWeaponItem;
import com.elementalweapons.item.TrainingWeaponItem;
import com.elementalweapons.item.WeaponType;
import com.elementalweapons.spell.ElementArrowSpell;
import com.elementalweapons.spell.Fx;
import com.elementalweapons.spell.Spell;
import com.elementalweapons.spell.SpellManager;

/** Z·X·C 스킬 실행 (서버). 무기 종류마다 3개, 나무 무기는 Z(구르기)만 */
public final class Skills {
	private Skills() {}

	private static final Map<UUID, long[]> READY = new HashMap<>();

	/** 여러 틱에 걸친 스킬 */
	private static final class Timed extends Spell {
		private final IntPredicate step;
		Timed(ServerLevel level, Player caster, IntPredicate step) { super(level, caster); this.step = step; }
		@Override protected boolean update() { return casterGone() || step.test(age); }
	}

	static void run(ServerLevel level, Player p, IntPredicate step) { SpellManager.add(new Timed(level, p, step)); }

	public static void cast(ServerPlayer p, int slot) {
		if (slot == JobSkills.SLOT && p.isAlive() && !p.isSpectator()) { JobSkills.cast(p); return; }
		if (slot < 0 || slot > 2 || !p.isAlive() || p.isSpectator()) return;
		ServerLevel level = (ServerLevel) p.level();
		ItemStack held = p.getMainHandItem();
		WeaponType type; Element el = null; boolean wood = false;
		if (held.getItem() instanceof ElementalWeaponItem w) { type = w.type(); el = w.element(); }
		else if (held.getItem() instanceof TrainingWeaponItem t) { type = t.type(); wood = true; }
		else { p.sendOverlayMessage(Component.literal("스킬은 직업 무기를 손에 들고 써야 합니다").withStyle(ChatFormatting.GRAY)); return; }
		if (!ModGrowth.canUse(p, type)) { ModGrowth.denyMessage(p, type); return; }
		SkillInfo.Info[] infos = wood ? SkillInfo.WOOD : SkillInfo.of(type);
		if (wood && slot > 0) { p.sendOverlayMessage(Component.literal("X·C 스킬은 10레벨 속성 진화 후에 열립니다").withStyle(ChatFormatting.GOLD)); return; }
		long now = level.getGameTime();
		long[] ready = READY.computeIfAbsent(p.getUUID(), k -> new long[WeaponType.values().length * 3 + 3]);
		int key = wood ? WeaponType.values().length * 3 + slot : type.ordinal() * 3 + slot;   // 무기 종류마다 따로
		if (ready[key] > now) {
			p.sendOverlayMessage(Component.literal(infos[slot].name() + " 대기 " + String.format("%.1f", (ready[key] - now) / 20.0) + "초").withStyle(ChatFormatting.GRAY));
			return;
		}
		int tier = SkillInfo.tier(ModGrowth.get(p).level());
		float m = SkillInfo.tierMult(tier);
		boolean ok;
		try {
			ok = wood ? dodge(level, p) : skill(level, p, type, el == null ? Element.FIRE : el, slot, m);
		} catch (Throwable t) {
			com.elementalweapons.ElementalWeaponsMod.LOGGER.error("[SKILL] 오류", t);
			ok = false;
		}
		if (!ok) return;
		com.elementalweapons.spell.WeaponVisual.announce(p);   // 스킬마다 공격 동작
		int cd = (int) Math.round(infos[slot].cooldown() * (1 - ModGrowth.paragon(p).cooldownCut()));
		ready[key] = now + cd;
		SkillNet.sendCooldown(p, slot, cd);
		Tutorial.skillUsed(p, slot);
		if (!wood) com.elementalweapons.growth.Achieve.grant(p, "journey/skill");
		if (!wood) com.elementalweapons.growth.Jobs.flourish(p, el, slot == 2);
	}

	public static void forget(UUID id) { READY.remove(id); }

	// ---------------- 공통 도구 ----------------
	public static void launchSelf(Player p, Vec3 v) { p.setDeltaMovement(v); p.syncVelocity = true; p.needsSync = true; p.resetFallDistance(); }

	private static int hit(ServerLevel l, Player p, Vec3 c, double r, float dmg, Set<LivingEntity> once) {
		int n = 0;
		for (LivingEntity e : Fx.enemies(l, c, r)) {
			if (once != null && !once.add(e)) continue;
			Fx.hurt(l, p, e, dmg); n++;
		}
		return n;
	}

	private static FxOptions slash(int rgb, float size, int life) { return FxOptions.of(FxOptions.SLASH, rgb, size, life); }

	private static boolean dodge(ServerLevel l, ServerPlayer p) {
		Vec3 f = Fx.flatLook(p);
		launchSelf(p, new Vec3(f.x * 1.15, 0.28, f.z * 1.15));
		p.setInvulnerableTime(12);
		Fx.burst(l, FxOptions.of(FxOptions.SMOKE, 0xD8C8A8, 0.35F, 14).grow(1.6F), p.position().add(0, 0.2, 0), 6, 0.3, 0.02);
		Fx.sound(l, p.position(), SoundEvents.PLAYER_ATTACK_NODAMAGE, 1F, 1.4F);
		return true;
	}

	/** 몸(2칸)이 들어갈 빈 공간인지 */
	public static boolean standable(ServerLevel l, net.minecraft.core.BlockPos b) {
		return l.getBlockState(b).getCollisionShape(l, b).isEmpty() && l.getBlockState(b.above()).getCollisionShape(l, b.above()).isEmpty();
	}

	/** 바라보는 방향으로 최대 dist칸, 벽·땅에 박히지 않는 발 위치 */
	public static Vec3 safeBlink(ServerLevel l, ServerPlayer p, Vec3 look, double dist) {
		Vec3 feet = p.position();
		Vec3 best = feet;
		for (double d = 0.5; d <= dist; d += 0.5) {
			Vec3 c = feet.add(look.scale(d));
			net.minecraft.core.BlockPos b = net.minecraft.core.BlockPos.containing(c);
			if (standable(l, b)) { best = c; continue; }
			// 낮은 턱이면 한 칸 위로
			if (standable(l, b.above())) { best = new Vec3(c.x, b.getY() + 1, c.z); continue; }
			break;   // 벽 — 그 앞에서 멈춤
		}
		return best;
	}

	// ---------------- 속성 무기 스킬 27종 ----------------
	private static boolean skill(ServerLevel l, ServerPlayer p, WeaponType t, Element el, int s, float m) {
		int c1 = ElementFx.color(el), c2 = ElementFx.color2(el);
		Vec3 pos = p.position(), f = Fx.flatLook(p), look = p.getLookAngle();
		float yaw = p.getYRot();
		switch (t) {
			case SWORD -> {
				if (s == 0) {   // 질풍 찌르기
					launchSelf(p, new Vec3(f.x * 2.2, 0.12, f.z * 2.2));
					Set<LivingEntity> once = new HashSet<>();
					run(l, p, a -> {
						Fx.at(l, slash(c2, 1.2F, 5).facing(yaw + 90, 0, -90), p.position().add(0, 1.1, 0).add(f.scale(1.2)));
						Fx.burst(l, ElementFx.particle(el), p.position().add(0, 1, 0), 3, 0.3, 0.02);
						hit(l, p, p.position().add(0, 1, 0), 2.2, 9 * m, once);
						return a >= 7;
					});
					Fx.sound(l, pos, SoundEvents.TRIDENT_RIPTIDE_1.value(), 1F, 1.3F);
				} else if (s == 1) {   // 십자 베기
					Vec3 front = pos.add(f.scale(2.2)).add(0, 1.1, 0);
					Fx.at(l, slash(c1, 2.6F, 8).facing(yaw + 90, 0, 45), front);
					Fx.at(l, slash(c2, 2.4F, 8).facing(yaw + 90, 0, -45), front);
					ElementFx.impact(l, el, pos.add(f.scale(2.5)), 3);
					for (LivingEntity e : Fx.enemies(l, pos.add(f.scale(2)), 3.5)) Fx.hurt(l, p, e, 12 * m);
					Fx.sound(l, pos, SoundEvents.PLAYER_ATTACK_SWEEP, 1.2F, 0.7F);
				} else {   // 성역 결계
					Vec3 c = pos;
					run(l, p, a -> {
						if (a % 20 == 0) {
							ElementFx.rune(l, c1, c, 5, 24, 3F);
							for (Player q : l.getEntitiesOfClass(Player.class, new net.minecraft.world.phys.AABB(c, c).inflate(5))) {
								q.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0));
								q.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 40, 0));
							}
							for (LivingEntity e : Fx.enemies(l, c, 5)) { e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1)); Fx.hurt(l, p, e, 2 * m); }
						}
						return a >= 120;
					});
					Fx.sound(l, pos, SoundEvents.BEACON_ACTIVATE, 1F, 1.4F);
				}
			}
			case GREATSWORD -> {
				if (s == 0) {   // 도약 내려찍기
					launchSelf(p, new Vec3(f.x * 1.0, 0.95, f.z * 1.0));
					run(l, p, a -> {
						if (a > 5 && (p.onGround() || a > 40)) {
							ElementFx.impact(l, el, p.position(), 4);
							ElementFx.groundRing(l, 0xFFFFFF, p.position(), 1.5, 3.2F, 9);
							for (LivingEntity e : Fx.enemies(l, p.position(), 4)) { Fx.hurt(l, p, e, 14 * m); Fx.launch(e, 0, 0.5, 0); }
							Fx.sound(l, p.position(), SoundEvents.MACE_SMASH_GROUND_HEAVY, 1F, 0.9F);
							p.fallDistance = 0;
							return true;
						}
						if (a % 2 == 0) Fx.at(l, ElementFx.particle(el), p.position().add(0, 1, 0));
						return false;
					});
				} else if (s == 1) {   // 회오리 베기
					run(l, p, a -> {
						if (a % 5 == 0) Fx.at(l, slash(a % 10 == 0 ? c1 : c2, 4.2F, 7).facing(yaw + a * 24, 90, 0), p.position().add(0, 1.0, 0));
						if (a % 10 == 0) { hit(l, p, p.position(), 4.2, 7 * m, null); Fx.sound(l, p.position(), SoundEvents.PLAYER_ATTACK_SWEEP, 1F, 1.0F + a / 60F); }
						return a >= 30;
					});
				} else {   // 검기 방출
					Vec3 start = pos.add(0, 1.0, 0);
					Set<LivingEntity> once = new HashSet<>();
					run(l, p, a -> {
						Vec3 c = start.add(f.scale(1.3 * a));
						Fx.at(l, slash(c2, 2.8F, 5).facing(yaw, 90, 0), c);
						Fx.at(l, slash(c1, 3.2F, 6).facing(yaw + 90, 0, 0), c);
						Fx.burst(l, ElementFx.particle(el), c, 2, 0.6, 0.02);
						hit(l, p, c, 2.4, 18 * m, once);
						return a >= 14;
					});
					Fx.sound(l, pos, SoundEvents.WITHER_SHOOT, 0.7F, 1.6F);
				}
			}
			case HAMMER -> {
				if (s == 0) {   // 지진
					Vec3 c = pos;
					run(l, p, a -> {
						if (a % 8 == 0) {
							double r = 3 + a / 8 * 3;
							ElementFx.groundRing(l, c1, c, r * 0.42, 2.3F, 10);
							for (LivingEntity e : Fx.enemies(l, c, r + 1.5)) if (e.distanceToSqr(c) > (r - 1.5) * (r - 1.5)) { Fx.hurt(l, p, e, 8 * m); Fx.launch(e, 0, 0.45, 0); }
							Fx.sound(l, c, SoundEvents.MACE_SMASH_GROUND, 1.2F, 0.7F + a / 40F);
						}
						return a >= 16;
					});
				} else if (s == 1) {   // 망치 투척 (갔다가 돌아옴)
					Vec3 start = pos.add(0, 1.2, 0);
					Set<LivingEntity> out = new HashSet<>(), back = new HashSet<>();
					run(l, p, a -> {
						double d = a <= 10 ? a : 20 - a;
						Vec3 c = (a <= 10 ? start : p.position().add(0, 1.2, 0)).add(f.scale(d));
						Fx.at(l, FxOptions.of(FxOptions.SHARD, c2, 0.8F, 3).spin(60F), c);
						Fx.at(l, ElementFx.particle(el), c);
						hit(l, p, c, 1.9, 10 * m, a <= 10 ? out : back);
						return a >= 20;
					});
				} else {   // 대지 분쇄
					launchSelf(p, new Vec3(f.x * 0.4, 1.15, f.z * 0.4));
					run(l, p, a -> {
						if (a > 6 && (p.onGround() || a > 45)) {
							Vec3 c = p.position();
							for (int k = 0; k < 3; k++) ElementFx.groundRing(l, k == 1 ? 0xFFFFFF : c1, c, 1.2 + k * 1.6, 2.4F, 10 + k * 3);
							ElementFx.impact(l, el, c, 7);
							for (LivingEntity e : Fx.enemies(l, c, 7)) { Fx.hurt(l, p, e, 20 * m); e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 3)); Fx.launch(e, 0, 0.7, 0); }
							Fx.sound(l, c, SoundEvents.MACE_SMASH_GROUND_HEAVY, 1.5F, 0.6F);
							p.fallDistance = 0;
							return true;
						}
						return false;
					});
				}
			}
			case DAGGER -> {
				if (s == 0) {   // 그림자 걸음: 가장 가까운 적 등 뒤로 순간 이동 → 확정 치명 + 출혈 2. 적이 없으면 앞으로 7칸 은신 이동
					List<LivingEntity> es = Fx.enemies(l, pos, 12);
					Fx.burst(l, FxOptions.of(FxOptions.SMOKE, 0x1A1024, 0.5F, 16).grow(1.6F), pos.add(0, 1, 0), 10, 0.4, 0.02);
					if (es.isEmpty()) {
						Vec3 to = safeBlink(l, p, f, 7);
						Fx.line(l, FxOptions.of(FxOptions.SMOKE, 0x2A2038, 0.35F, 10), pos.add(0, 1, 0), to.add(0, 1, 0), 0.5);
						p.teleportTo(to.x, to.y, to.z);
						p.addEffect(new MobEffectInstance(MobEffects.SPEED, 40, 1, false, false));
					} else {
						LivingEntity e = es.get(0);
						for (LivingEntity c : es) {   // 바라보는 쪽(앞 60도 안)의 적을 먼저
							Vec3 to = c.position().subtract(pos).multiply(1, 0, 1);
							if (to.lengthSqr() > 1.0E-4 && to.normalize().dot(f) > 0.5) { e = c; break; }
						}
						if (DaggerArts.DEBUG) com.elementalweapons.ElementalWeaponsMod.LOGGER.info("[DAGGER] Z 대상 {} {}", e.getType().toShortString(), e.position());
						final LivingEntity tgt = e;
						Vec3 back = e.position().subtract(Vec3.directionFromRotation(0, e.getYRot()).scale(1.2 + e.getBbWidth() * 0.5));
						if (!standable(l, net.minecraft.core.BlockPos.containing(back.x, e.getY(), back.z))) back = e.position();
						Fx.line(l, FxOptions.of(FxOptions.SMOKE, 0x2A2038, 0.35F, 10), pos.add(0, 1, 0), back.add(0, 1, 0), 0.5);
						p.teleportTo(back.x, e.getY(), back.z);
						Vec3 d = e.position().subtract(back);
						float ny = (float) (Math.toDegrees(Math.atan2(-d.x, d.z)));
						p.setYRot(ny); p.setYHeadRot(ny);
						p.connection.teleport(p.getX(), p.getY(), p.getZ(), ny, p.getXRot());
						run(l, p, a -> {
							if (a == 2 || a == 5) {
								if (tgt.isAlive()) { DaggerArts.strike(l, p, tgt, (a == 2 ? 11 : 7) * m, true); DaggerArts.bleed(l, p, tgt, 1); }
								Fx.at(l, slash(a == 2 ? c2 : c1, 1.5F, 5).facing(p.getYRot() + 90, 0, a == 2 ? 55 : -55), tgt.position().add(0, 1, 0));
							}
							return a >= 5;
						});
					}
					Fx.burst(l, FxOptions.of(FxOptions.SMOKE, 0x1A1024, 0.5F, 16).grow(1.6F), p.position().add(0, 1, 0), 10, 0.4, 0.02);
					Fx.sound(l, p.position(), SoundEvents.ENDERMAN_TELEPORT, 0.8F, 1.6F);
				} else if (s == 1) {   // 칼날 폭풍: 단검 9자루를 부채꼴로 연달아 던짐 (관통, 맞을 때마다 출혈)
					Set<LivingEntity> got = new HashSet<>();
					run(l, p, a -> {
						if (a % 2 == 0) {
							Vec3 eye = p.getEyePosition();
							int wave = a / 2;
							for (int k = -1; k <= 1; k++) {
								float ang = p.getYRot() + (k * 3 + (wave - 1)) * 7F;
								Vec3 dir = Vec3.directionFromRotation(p.getXRot(), ang);
								Vec3 end = eye.add(dir.scale(13));
								for (LivingEntity e : Fx.enemies(l, eye.add(dir.scale(6.5)), 7.5)) {
									Vec3 to = e.position().add(0, e.getBbHeight() / 2, 0).subtract(eye);
									double along = to.dot(dir);
									if (along > 0 && along < 13 && to.subtract(dir.scale(along)).length() < 1.1 + e.getBbWidth() * 0.5) {
										DaggerArts.strike(l, p, e, 5.5F * m, false);
										DaggerArts.bleed(l, p, e, 1);
										got.add(e);
									}
								}
								Fx.line(l, FxOptions.of(FxOptions.SHARD, k == 0 ? c2 : c1, 0.2F, 6).spin(50F), eye.add(dir), end, 0.9);
							}
							Fx.sound(l, p.position(), SoundEvents.TRIDENT_THROW.value(), 0.8F, 1.5F + wave * 0.1F);
						}
						return a >= 4;
					});
				} else {   // 천인 난무: 주변 적(최대 8)을 차례로 순간 이동하며 베고, 마지막에 원래 자리로 돌아와 은신
					List<LivingEntity> es = new java.util.ArrayList<>(Fx.enemies(l, pos, 11));
					if (es.size() > 8) es = es.subList(0, 8);
					List<LivingEntity> targets = es;
					Vec3 home = pos;
					p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 50, 4, false, false));
					int hits = Math.max(1, targets.size()) * (targets.size() <= 2 ? 3 : 1);
					run(l, p, a -> {
						if (targets.isEmpty()) {   // 적이 없으면 연막만
							if (a == 0) {
								p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 80, 0, false, false));
								p.addEffect(new MobEffectInstance(MobEffects.SPEED, 80, 1, false, false));
								Fx.burst(l, FxOptions.of(FxOptions.SMOKE, 0x3A3448, 0.8F, 24).grow(1.8F), home.add(0, 1, 0), 24, 2.2, 0.01);
							}
							return true;
						}
						if (a % 3 == 0 && a / 3 < hits) {
							LivingEntity e = targets.get((a / 3) % targets.size());
							if (e.isAlive()) {
								double ang = l.getRandom().nextDouble() * Math.PI * 2;
								Vec3 at = e.position().add(Math.cos(ang) * (1.1 + e.getBbWidth() * 0.5), 0, Math.sin(ang) * (1.1 + e.getBbWidth() * 0.5));
								if (!standable(l, net.minecraft.core.BlockPos.containing(at))) at = e.position();
								Fx.line(l, FxOptions.of(FxOptions.SLASH, c2, 0.5F, 4), p.position().add(0, 1, 0), at.add(0, 1, 0), 0.8);
								p.teleportTo(at.x, at.y, at.z);
								Fx.at(l, slash(a % 6 == 0 ? c1 : c2, 2.0F, 5).facing(l.getRandom().nextFloat() * 360, 0, l.getRandom().nextFloat() * 120 - 60), e.position().add(0, e.getBbHeight() * 0.6, 0));
								Fx.at(l, slash(0xFFFFFF, 1.6F, 4).facing(l.getRandom().nextFloat() * 360, 0, l.getRandom().nextFloat() * 120 - 60), e.position().add(0, e.getBbHeight() * 0.6, 0));
								DaggerArts.strike(l, p, e, 13 * m, false);
								DaggerArts.bleed(l, p, e, 2);
								Fx.sound(l, at, SoundEvents.PLAYER_ATTACK_SWEEP, 0.9F, 1.4F + l.getRandom().nextFloat() * 0.4F);
							}
						}
						if (a >= hits * 3 + 2) {
							Fx.line(l, FxOptions.of(FxOptions.SMOKE, 0x2A2038, 0.35F, 10), p.position().add(0, 1, 0), home.add(0, 1, 0), 0.6);
							p.teleportTo(home.x, home.y, home.z);
							p.fallDistance = 0;
							p.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 50, 0, false, false));
							Fx.burst(l, FxOptions.of(FxOptions.SMOKE, 0x1A1024, 0.6F, 18).grow(1.7F), home.add(0, 1, 0), 16, 0.8, 0.02);
							for (LivingEntity e : targets) if (e.isAlive()) Fx.at(l, slash(0xFF3040, 2.6F, 7).facing(l.getRandom().nextFloat() * 360, 0, 90), e.position().add(0, e.getBbHeight() * 0.6, 0));
							Fx.sound(l, home, SoundEvents.ENDERMAN_TELEPORT, 0.9F, 0.8F);
							return true;
						}
						return false;
					});
					Fx.sound(l, pos, SoundEvents.FIRE_EXTINGUISH, 1F, 0.6F);
				}
			}
			case STAFF -> {
				if (s == 0) {   // 원소 화살 (최대 3마리 유도)
					List<LivingEntity> es = Fx.enemies(l, pos, 16);
					Vec3 eye = p.getEyePosition();
					for (int k = 0; k < 3; k++) {
						LivingEntity tg = es.isEmpty() ? null : es.get(k % es.size());
						Vec3 from = eye.add(Vec3.directionFromRotation(-20, yaw - 40 + k * 40).scale(1.2));
						Vec3 to = tg != null ? tg.position().add(0, tg.getBbHeight() / 2, 0) : eye.add(look.scale(16)).add(0, k - 1, 0);
						run(l, p, a -> {
							double tt = a / 10.0;
							Vec3 c = from.lerp(to, tt).add(0, Math.sin(tt * Math.PI) * 1.5, 0);
							ElementFx.orb(l, el, c, 0.35);
							if (a >= 10) { if (tg != null && tg.isAlive()) Fx.hurt(l, p, tg, 6 * m); ElementFx.impact(l, el, to, 1.4); return true; }
							return false;
						});
					}
					Fx.sound(l, pos, SoundEvents.EVOKER_CAST_SPELL, 1F, 1.5F);
				} else if (s == 1) {   // 순간 이동
					Vec3 to = safeBlink(l, p, look, 8);
					Fx.burst(l, ElementFx.particle(el), pos.add(0, 1, 0), 14, 0.4, 0.08);
					ElementFx.rune(l, c1, pos, 1.2, 12, 10F);
					p.teleportTo(to.x, to.y, to.z);
					p.fallDistance = 0;
					ElementFx.rune(l, c1, p.position(), 1.2, 12, -10F);
					Fx.burst(l, ElementFx.particle(el), p.position().add(0, 1, 0), 14, 0.4, 0.08);
					Fx.sound(l, p.position(), SoundEvents.ENDERMAN_TELEPORT, 1F, 1.2F);
				} else {   // 원소 폭풍
					Vec3 c = Fx.lookPoint(p, 24);
					com.elementalweapons.spell.SpellManager.add(new com.elementalweapons.spell.MagicCircle(l, p, el, c.add(0, 0.1, 0), 6, 105, false));
					run(l, p, a -> {
						if (a % 10 == 0) {
							for (LivingEntity e : Fx.enemies(l, c, 5)) Fx.hurt(l, p, e, 4 * m);
							ElementFx.impact(l, el, c.add((l.getRandom().nextDouble() - 0.5) * 6, 0, (l.getRandom().nextDouble() - 0.5) * 6), 2);
						}
						Fx.burst(l, ElementFx.particle(el), c.add(0, 3, 0), 3, 3, 0.05);
						return a >= 100;
					});
				}
			}
			case BOOK -> {
				if (s == 0) {   // 치유의 빛
					for (Player q : l.getEntitiesOfClass(Player.class, p.getBoundingBox().inflate(8))) {
						Fx.supportHeal(p, q, 0.15F);
						q.addEffect(new MobEffectInstance(MobEffects.REGENERATION, q == p ? 30 : 60, 1));
						for (double y = 0; y < 3; y += 0.5) Fx.at(l, FxOptions.of(FxOptions.STAR, 0xFFF4C0, 0.3F, 12).spin(10F), q.position().add(0, y, 0));
					}
					ElementFx.rune(l, 0xFFE27A, pos, 3, 20, 5F);
					Fx.sound(l, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, 1F, 1.3F);
				} else if (s == 1) {   // 수호의 막
					for (Player q : l.getEntitiesOfClass(Player.class, p.getBoundingBox().inflate(8))) {
						q.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 1));
						ElementFx.groundRing(l, c1, q.position(), 0.8, 1.6F, 14);
						Fx.at(l, FxOptions.of(FxOptions.RING, c2, 0.9F, 14).grow(1.4F).facing(0, 0, 0), q.position().add(0, 1, 0));
					}
					Fx.sound(l, pos, SoundEvents.ARMOR_EQUIP_DIAMOND.value(), 1F, 1.2F);
				} else {   // 축복의 장
					Vec3 c = pos;
					run(l, p, a -> {
						if (a % 20 == 0) {
							ElementFx.rune(l, c1, c, 7, 24, 2F);
							for (Player q : l.getEntitiesOfClass(Player.class, new net.minecraft.world.phys.AABB(c, c).inflate(7))) {
								q.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 40, 0));
								q.addEffect(new MobEffectInstance(MobEffects.SPEED, 40, 0));
							}
							for (LivingEntity e : Fx.enemies(l, c, 7)) { e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0)); e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 0)); }
						}
						if (a % 4 == 0) Fx.burst(l, ElementFx.particle(el), c.add(0, 0.5, 0), 3, 5, 0.01);
						return a >= 160;
					});
					Fx.sound(l, pos, SoundEvents.BEACON_POWER_SELECT, 1F, 1.5F);
				}
			}
			case SCYTHE -> {
				if (s == 0) {   // 영혼 수확
					for (LivingEntity e : Fx.enemies(l, pos, 8)) {
						Vec3 d = pos.subtract(e.position()).multiply(1, 0, 1);
						if (d.lengthSqr() > 1) d = d.normalize().scale(1.1);
						Fx.launch(e, d.x, 0.25, d.z);
						Fx.line(l, FxOptions.of(FxOptions.SMOKE, c1, 0.25F, 8), e.position().add(0, 1, 0), pos.add(0, 1, 0), 0.8);
					}
					run(l, p, a -> {
						if (a == 7) {
							Fx.at(l, slash(c2, 3.6F, 8).facing(yaw, 90, 0), p.position().add(0, 1, 0));
							Fx.at(l, slash(c1, 3.4F, 8).facing(yaw + 180, 90, 0), p.position().add(0, 1, 0));
							hit(l, p, p.position(), 3.5, 9 * m, null);
							Fx.sound(l, p.position(), SoundEvents.PLAYER_ATTACK_SWEEP, 1.2F, 0.6F);
							return true;
						}
						return false;
					});
				} else if (s == 1) {   // 망령 질주
					launchSelf(p, new Vec3(f.x * 2.4, 0.1, f.z * 2.4));
					Set<LivingEntity> once = new HashSet<>();
					run(l, p, a -> {
						Fx.burst(l, FxOptions.of(FxOptions.SMOKE, 0x2A1838, 0.5F, 18).grow(1.6F), p.position().add(0, 1, 0), 3, 0.3, 0.01);
						Fx.at(l, FxOptions.of(FxOptions.FLAME, c2, 0.4F, 12).gravity(-0.02F), p.position().add(0, 1.2, 0));
						hit(l, p, p.position().add(0, 1, 0), 2.2, 10 * m, once);
						return a >= 8;
					});
					Fx.sound(l, pos, SoundEvents.PHANTOM_SWOOP, 1F, 1.2F);
				} else {   // 사신의 낫
					run(l, p, a -> {
						if (a % 2 == 0) Fx.at(l, slash(a % 4 == 0 ? c1 : c2, 7.5F, 6).facing(yaw + a * 36, 90, 0), p.position().add(0, 1.2, 0));
						if (a == 5 || a == 15) {
							int n = hit(l, p, p.position(), 7, 12 * m, null);
							p.heal(Math.min(10, n * 2));
							Fx.sound(l, p.position(), SoundEvents.WITHER_SHOOT, 0.8F, 0.6F);
						}
						return a >= 20;
					});
				}
			}
			case BOW -> {
				if (s == 0) {   // 후퇴 사격
					launchSelf(p, new Vec3(-f.x * 1.3, 0.5, -f.z * 1.3));
					SpellManager.add(new ElementArrowSpell(l, p, el, 0.6F, p.getMainHandItem()));
				} else if (s == 1) {   // 화살비
					Vec3 c = Fx.lookPoint(p, 30);
					ElementFx.rune(l, c1, c, 4, 44, 3F);
					run(l, p, a -> {
						if (a % 4 == 0) {
							for (int k = 0; k < 4; k++) {
								Vec3 g = c.add((l.getRandom().nextDouble() - 0.5) * 8, 0, (l.getRandom().nextDouble() - 0.5) * 8);
								Fx.line(l, FxOptions.of(FxOptions.SHARD, c2, 0.2F, 5), g.add(0, 8, 0), g, 1.0);
								Fx.at(l, ElementFx.particle(el), g);
							}
							for (LivingEntity e : Fx.enemies(l, c, 4.5)) Fx.hurt(l, p, e, 4 * m);
							Fx.sound(l, c, SoundEvents.ARROW_HIT, 1F, 0.9F);
						}
						return a >= 40;
					});
				} else {   // 관통 화살
					Vec3 eye = p.getEyePosition();
					Vec3 end = eye.add(look.scale(30));
					for (LivingEntity e : Fx.enemies(l, eye.add(look.scale(15)), 16)) {
						Vec3 to = e.position().add(0, e.getBbHeight() / 2, 0).subtract(eye);
						double along = to.dot(look);
						if (along > 0 && along < 30 && to.subtract(look.scale(along)).length() < 1.6) Fx.hurt(l, p, e, 20 * m);
					}
					Fx.line(l, FxOptions.of(FxOptions.ORB, c2, 0.35F, 10), eye.add(look), end, 0.5);
					Fx.line(l, FxOptions.of(FxOptions.STAR, 0xFFFFFF, 0.25F, 8).spin(10F), eye.add(look), end, 1.5);
					for (int k = 0; k < 6; k++) Fx.at(l, FxOptions.of(FxOptions.RING, c1, 0.6F, 10).grow(1.8F).facing(yaw, -p.getXRot(), 0), eye.add(look.scale(2 + k * 4)));
					Fx.sound(l, pos, SoundEvents.TRIDENT_THUNDER.value(), 0.6F, 1.6F);
				}
			}
			case SHIELD -> {
				if (s == 0) {   // 방패 돌진
					launchSelf(p, new Vec3(f.x * 1.7, 0.1, f.z * 1.7));
					Set<LivingEntity> once = new HashSet<>();
					run(l, p, a -> {
						for (LivingEntity e : Fx.enemies(l, p.position(), 2.2)) if (once.add(e)) { Fx.hurt(l, p, e, 6 * m); Fx.launch(e, f.x * 1.2, 0.4, f.z * 1.2); }
						Fx.at(l, FxOptions.of(FxOptions.RING, c2, 0.9F, 5).facing(yaw, 0, 0), p.position().add(0, 1, 0).add(f));
						return a >= 7;
					});
					Fx.sound(l, pos, SoundEvents.SHIELD_BLOCK.value(), 1F, 0.7F);
				} else if (s == 1) {   // 도발의 함성
					for (LivingEntity e : Fx.enemies(l, pos, 12)) if (e instanceof Mob mob) mob.setTarget(p);
					p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 100, 0));
					for (int k = 0; k < 3; k++) ElementFx.groundRing(l, k == 1 ? 0xFFFFFF : c1, pos, 1 + k * 1.5, 2.5F, 12 + k * 3);
					Fx.sound(l, pos, SoundEvents.RAVAGER_ROAR, 0.7F, 1.4F);
				} else {   // 철벽
					p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 80, 2));
					p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 160, 1));
					ElementFx.rune(l, c1, pos, 2.5, 80, 4F);
					Fx.at(l, FxOptions.of(FxOptions.RING, c2, 1.4F, 20).grow(1.2F).facing(yaw, 0, 0), pos.add(0, 1, 0));
					Fx.sound(l, pos, SoundEvents.ANVIL_PLACE, 0.8F, 1.4F);
				}
			}
		}
		return true;
	}
}
