package com.elementalweapons.spell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.fx.P;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.WeaponType;
import com.elementalweapons.skill.ElementMotion;
import com.elementalweapons.skill.Skills;

/**
 * 무기 × 속성 우클릭 기술 (지팡이 제외 8종 × 7속성 = 56개). 모두 서로 다른 기술.
 * 방패는 우클릭이 막기라서 Shift+우클릭.
 */
public final class ElementSkills {
	private ElementSkills() {}

	/** 기술 이름 [무기][속성] (툴팁·안내용) */
	public static String name(WeaponType t, Element e) {
		String[] n = switch (t) {
			case DAGGER -> new String[]{"섬광 연쇄", "화염 질주", "물거품 분신", "독가시 부채", "질풍 난무", "그림자 암살", "빛의 표창"};
			case SWORD -> new String[]{"뇌신 강림", "화염 회오리 베기", "해일 가르기", "덩굴 감옥", "진공 삼연참", "그림자 검무", "심판의 성검"};
			case GREATSWORD -> new String[]{"번개 검진", "용암 균열", "소용돌이 베기", "가시 정원", "폭풍 회전", "심연 낙하", "성역 낙인"};
			case HAMMER -> new String[]{"천둥 망치", "화산 분화", "빙결 진동", "뿌리 융기", "대기 붕괴", "중력 붕괴", "신성 충격"};
			case SCYTHE -> new String[]{"번개 수확", "불꽃 원무", "조류의 낫", "생명 수확", "회오리 낫", "영혼 수확", "초승달 섬광"};
			case BOOK -> new String[]{"신속의 서", "투지의 서", "치유의 서", "재생의 정원", "비상의 서", "흡혈의 서", "가호의 서"};
			case BOW -> new String[]{"뇌전 화살비", "유성 화살", "빙우", "덩굴 화살", "관통 돌풍", "그림자 추적", "빛의 화살비"};
			case SHIELD -> new String[]{"방전 방벽", "화염 돌진", "물의 장막", "가시 갑옷", "반사 돌풍", "그림자 흡수", "성스러운 방패"};
			default -> null;
		};
		return n == null ? "" : n[e.ordinal()];
	}

	// =====================================================================================
	/** 우클릭 기술 실행. 실행했으면 true */
	public static boolean cast(WeaponType t, Element e, ServerLevel l, Player p) {
		Ctx c = new Ctx(l, p, e);
		if (t != WeaponType.BOW) WeaponVisual.announce(p);
		switch (t) {
			case DAGGER -> dagger(c);
			case SWORD -> sword(c);
			case GREATSWORD -> greatsword(c);
			case HAMMER -> hammer(c);
			case SCYTHE -> scythe(c);
			case BOOK -> book(c);
			case BOW -> bow(c);
			case SHIELD -> shield(c);
			default -> { return false; }
		}
		if (p instanceof ServerPlayer sp)
			sp.sendOverlayMessage(net.minecraft.network.chat.Component.literal("◆ " + name(t, e)).withColor(e.color));
		return true;
	}

	// ------------------------------------------------------------------ 공용 도구
	static final class Ctx {
		final ServerLevel l; final Player p; final Element el; final float m;
		Ctx(ServerLevel l, Player p, Element el) { this.l = l; this.p = p; this.el = el; this.m = ElementMotion.mult(p); }
		Vec3 pos() { return p.position(); }
		Vec3 chest() { return p.position().add(0, 1, 0); }
		Vec3 look() { return Fx.flatLook(p); }
		ParticleOptions part() { return ElementFx.particle(el); }
		ParticleOptions dust(float s) { return ElementFx.dust(el, s); }
		List<LivingEntity> foes(Vec3 at, double r) { List<LivingEntity> f = Fx.enemies(l, at, r); f.remove(p); return f; }
		void hit(LivingEntity e, float d) { Fx.hurt(l, p, e, d * m); e.setInvulnerableTime(0); }
		List<LivingEntity> area(Vec3 at, double r, float d) { List<LivingEntity> f = foes(at, r); for (LivingEntity e : f) hit(e, d); return f; }
		void eff(LivingEntity e, Holder<MobEffect> ef, int dur, int amp) { e.addEffect(new MobEffectInstance(ef, dur, amp)); }
		void push(LivingEntity e, Vec3 from, double s, double up) {
			Vec3 d = e.position().subtract(from).multiply(1, 0, 1);
			d = d.lengthSqr() < 1.0E-4 ? new Vec3(0, 0, 0) : d.normalize().scale(s);
			Fx.launch(e, d.x, up, d.z);
		}
		void pull(LivingEntity e, Vec3 to, double s) {
			Vec3 d = to.subtract(e.position()).multiply(1, 0, 1);
			if (d.lengthSqr() > 1) d = d.normalize().scale(s);
			else d = d.scale(s * 0.5);
			Fx.launch(e, d.x, 0.12, d.z);
		}
		void snd(Vec3 at, SoundEvent s, float v, float pi) { Fx.sound(l, at, s, v, pi); }
		void snd(Vec3 at, Holder<SoundEvent> s, float v, float pi) { Fx.sound(l, at, s, v, pi); }
		void ring(Vec3 at, double r) { Fx.ring(l, dust(1.6F), at.add(0, 0.15, 0), r, (int) (r * 10), 0); }
		void shock(Vec3 at, double r, int life) { VisualFx.spawn(l, 15, el, at, life, (float) (r / 4.5)); }
		/** 근처 땅 높이 (위아래 6칸) */
		Vec3 ground(Vec3 at) {
			BlockPos.MutableBlockPos bp = BlockPos.containing(at.x, at.y + 2, at.z).mutable();
			for (int i = 0; i < 10 && l.getBlockState(bp.below()).isAir(); i++) bp.move(0, -1, 0);
			for (int i = 0; i < 6 && !l.getBlockState(bp).isAir(); i++) bp.move(0, 1, 0);
			return new Vec3(at.x, bp.getY(), at.z);
		}
		/** 바라보는 땅 지점 (허공이면 앞쪽 range*0.4) */
		Vec3 aim(double range) {
			var hit = p.pick(range, 0.0F, false);
			if (hit.getType() == net.minecraft.world.phys.HitResult.Type.MISS) return ground(pos().add(look().scale(range * 0.4)));
			return hit.getLocation();
		}
		void run(Tick t) { SpellManager.add(new Timed(l, p, t)); }
		void visual(WeaponType type, Vec3 target) { WeaponVisual.start(l, p, el, type, target); }
	}

	@FunctionalInterface interface Tick { boolean tick(int age); }

	static final class Timed extends Spell {
		private final Tick t;
		Timed(ServerLevel l, Player p, Tick t) { super(l, p); this.t = t; }
		@Override protected boolean update() { return casterGone() || t.tick(age); }
	}

	// ====================================================================== 단검 (빠른 몸놀림)
	private static void dagger(Ctx c) {
		Player p = c.p; ServerLevel l = c.l;
		switch (c.el) {
			case ELECTRIC -> {   // 섬광 연쇄: 근처 적 최대 4명에게 차례로 번쩍이며 벰
				List<LivingEntity> ts = c.foes(c.chest(), 9);
				if (ts.isEmpty()) { Vec3 to = Skills.safeBlink(l, (ServerPlayer) p, c.look(), 7); Fx.line(l, c.part(), c.chest(), to.add(0, 1, 0), 0.6); p.teleportTo(to.x, to.y, to.z); return; }
				final List<LivingEntity> list = new ArrayList<>(ts.subList(0, Math.min(4, ts.size())));
				c.run(age -> {
					if (age % 3 != 0) return false;
					int i = age / 3;
					if (i >= list.size()) return true;
					LivingEntity t = list.get(i);
					if (!t.isAlive()) return false;
					Vec3 from = c.chest();
					Vec3 back = t.position().add(Vec3.directionFromRotation(0, t.getYRot()).scale(-1.3));
					p.teleportTo(back.x, t.getY(), back.z);
					p.resetFallDistance();
					Fx.line(l, c.dust(1.2F), from, t.position().add(0, 1, 0), 0.35);
					Fx.line(l, P.ELECTRIC_SPARK, from, t.position().add(0, 1, 0), 0.5);
					c.hit(t, 6);
					c.eff(t, MobEffects.SLOWNESS, 30, 3);
					c.snd(t.position(), SoundEvents.LIGHTNING_BOLT_IMPACT, 0.5F, 1.8F);
					return false;
				});
			}
			case FIRE -> {   // 화염 질주: 앞으로 돌진, 지나간 길이 3초 동안 불탐
				Vec3 start = c.pos(), f = c.look();
				Skills.launchSelf(p, new Vec3(f.x * 1.8, 0.15, f.z * 1.8));
				c.snd(start, SoundEvents.BLAZE_SHOOT, 1F, 0.8F);
				c.run(age -> {
					if (age % 5 == 0) for (double d = 0; d <= 9; d += 1.5) {
						Vec3 q = start.add(f.scale(d));
						Fx.burst(l, P.FLAME, q.add(0, 0.4, 0), 4, 0.4, 0.03);
						if (age % 10 == 0) for (LivingEntity e : c.foes(q.add(0, 0.8, 0), 1.6)) { c.hit(e, 2.5F); e.igniteForSeconds(3); }
					}
					return age >= 60;
				});
			}
			case WATER -> {   // 물거품 분신: 뒤로 물러나며 물방울 분신 3개를 남기고, 1초 뒤 터짐
				Vec3 at = c.pos(), f = c.look();
				Skills.launchSelf(p, new Vec3(-f.x * 1.2, 0.3, -f.z * 1.2));
				final Vec3[] bub = {at, at.add(f.scale(1.6)).add(-f.z * 1.6, 0, f.x * 1.6), at.add(f.scale(1.6)).add(f.z * 1.6, 0, -f.x * 1.6)};
				c.run(age -> {
					for (Vec3 b : bub) if (age % 2 == 0) Fx.sphere(l, P.SPLASH, b.add(0, 1, 0), 0.7, 10);
					if (age == 20) for (Vec3 b : bub) {
						WaterOrbSpell.splash(l, p, b.add(0, 0.5, 0), 5.0F * c.m, 2.8);
						for (LivingEntity e : c.foes(b.add(0, 1, 0), 2.8)) c.eff(e, MobEffects.SLOWNESS, 60, 2);
					}
					return age >= 20;
				});
			}
			case GRASS -> {   // 독가시 부채: 가시 7개를 부채꼴로 던짐 (중독)
				Vec3 eye = p.getEyePosition();
				double yaw = Math.toRadians(p.getYRot());
				final Vec3[] dir = new Vec3[7]; final Vec3[] cur = new Vec3[7]; final Set<LivingEntity> hit = new HashSet<>();
				for (int i = 0; i < 7; i++) { double a = yaw + Math.toRadians((i - 3) * 9); dir[i] = new Vec3(-Math.sin(a), -0.05, Math.cos(a)).scale(1.4); cur[i] = eye; }
				c.snd(eye, SoundEvents.SNOW_GOLEM_SHOOT, 1F, 0.7F);
				c.run(age -> {
					for (int i = 0; i < 7; i++) {
						if (cur[i] == null) continue;
						Vec3 nx = cur[i].add(dir[i]);
						Fx.line(l, Fx.dust(0x7AE06A, 0.8F), cur[i], nx, 0.35);
						cur[i] = nx;
						for (LivingEntity e : c.foes(nx, 1.0)) if (hit.add(e)) { c.hit(e, 4); c.eff(e, MobEffects.POISON, 80, 1); Fx.burst(l, P.HAPPY_VILLAGER, e.position().add(0, 1, 0), 8, 0.4, 0); cur[i] = null; break; }
						if (cur[i] != null && !l.getBlockState(BlockPos.containing(nx)).isAir()) cur[i] = null;
					}
					return age >= 12;
				});
			}
			case WIND -> {   // 질풍 난무: 공중으로 솟아 주변을 여섯 번 베고 착지
				Skills.launchSelf(p, new Vec3(0, 0.75, 0));
				p.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 30, 0, false, false));
				c.run(age -> {
					if (age % 3 == 0 && age <= 18) {
						Vec3 cc = c.chest();
						double a = age * 0.9;
						Fx.line(l, P.SWEEP_ATTACK, cc.add(Math.cos(a) * 3, -0.5, Math.sin(a) * 3), cc.add(-Math.cos(a) * 3, 0.3, -Math.sin(a) * 3), 1.2);
						Fx.ring(l, P.GUST, cc.add(0, -0.8, 0), 2.5, 10, a);
						for (LivingEntity e : c.foes(cc, 4)) { c.hit(e, 2.2F); Fx.launch(e, 0, 0.35, 0); }
						c.snd(cc, SoundEvents.PLAYER_ATTACK_SWEEP, 0.7F, 1.4F);
					}
					return age >= 20;
				});
			}
			case DARK -> {   // 그림자 암살: 바라보는 적(12칸)의 등 뒤로 → 강한 일격 + 실명
				var hit = net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(p, p.getEyePosition(), p.getEyePosition().add(p.getLookAngle().scale(12)),
						p.getBoundingBox().expandTowards(p.getLookAngle().scale(12)).inflate(1.5), e -> e instanceof LivingEntity le && c.foes(le.position(), 0.1).contains(le) || e instanceof net.minecraft.world.entity.monster.Enemy, 144);
				LivingEntity t = hit != null && hit.getEntity() instanceof LivingEntity le ? le : null;
				if (t == null) { List<LivingEntity> es = c.foes(c.chest(), 8); if (!es.isEmpty()) t = es.get(0); }
				if (t == null) { Vec3 to = Skills.safeBlink(l, (ServerPlayer) p, c.look(), 5); p.teleportTo(to.x, to.y, to.z); return; }
				Fx.burst(l, c.dust(1.5F), c.chest(), 30, 0.5, 0.05);
				Vec3 back = t.position().add(Vec3.directionFromRotation(0, t.getYRot()).scale(-1.4));
				p.teleportTo(back.x, t.getY(), back.z);
				Vec3 face = t.position().subtract(back);
				float yaw = (float) Math.toDegrees(Math.atan2(-face.x, face.z));
				p.setYRot(yaw); p.setYHeadRot(yaw);
				p.resetFallDistance();
				c.hit(t, 12);
				c.eff(t, MobEffects.BLINDNESS, 60, 0);
				c.eff(t, MobEffects.WEAKNESS, 80, 1);
				Fx.burst(l, P.SQUID_INK, t.position().add(0, 1, 0), 30, 0.5, 0.05);
				c.snd(t.position(), SoundEvents.PLAYER_ATTACK_CRIT, 1F, 0.6F);
			}
			case LIGHT -> {   // 빛의 표창: 적 사이를 최대 5번 튕겨 다님
				final Vec3[] cur = {p.getEyePosition()};
				final Set<LivingEntity> done = new HashSet<>();
				final LivingEntity[] tgt = {null};
				final Vec3 dir0 = p.getLookAngle();
				c.snd(cur[0], SoundEvents.TRIDENT_THROW.value(), 1F, 1.6F);
				c.run(age -> {
					if (tgt[0] == null || !tgt[0].isAlive()) {
						List<LivingEntity> es = c.foes(cur[0], done.isEmpty() ? 14 : 9);
						es.removeAll(done);
						tgt[0] = es.isEmpty() ? null : es.get(0);
						if (tgt[0] == null) { if (done.isEmpty() && age < 8) { Vec3 nx = cur[0].add(dir0.scale(1.6)); Fx.line(l, P.END_ROD, cur[0], nx, 0.3); cur[0] = nx; return false; } return true; }
					}
					Vec3 to = tgt[0].position().add(0, tgt[0].getBbHeight() * 0.6, 0);
					Vec3 d = to.subtract(cur[0]);
					Vec3 nx = d.length() < 2.0 ? to : cur[0].add(d.normalize().scale(2.0));
					Fx.line(l, P.END_ROD, cur[0], nx, 0.3);
					Fx.burst(l, Fx.dust(0xFFF4B0, 1.4F), nx, 3, 0.1, 0);
					cur[0] = nx;
					if (nx.distanceTo(to) < 0.2) {
						c.hit(tgt[0], 5); c.eff(tgt[0], MobEffects.GLOWING, 100, 0);
						done.add(tgt[0]); tgt[0] = null;
						c.snd(nx, SoundEvents.AMETHYST_BLOCK_CHIME, 1F, 1.6F);
						if (done.size() >= 5) return true;
					}
					return age > 60;
				});
			}
		}
	}

	// ====================================================================== 검 (바라보는 곳 한 지점)
	private static void sword(Ctx c) {
		Player p = c.p; ServerLevel l = c.l;
		Vec3 t = c.aim(30);
		switch (c.el) {
			case ELECTRIC -> {   // 뇌신 강림: 영검이 번개처럼 꽂히고 주변 4명에게 연쇄 번개
				c.visual(WeaponType.SWORD, t);
				c.run(age -> {
					if (age == 2) {
						ElectricStormSpell.bolt(l, p, t, 8.0F * c.m);
						Vec3 prev = t.add(0, 1, 0);
						int n = 0;
						for (LivingEntity e : c.foes(t, 8)) {
							if (n++ >= 4) break;
							Fx.line(l, P.ELECTRIC_SPARK, prev, e.position().add(0, 1, 0), 0.3);
							Fx.line(l, c.dust(0.9F), prev, e.position().add(0, 1, 0), 0.5);
							c.hit(e, 5); c.eff(e, MobEffects.SLOWNESS, 40, 3);
							prev = e.position().add(0, 1, 0);
						}
					}
					return age >= 2;
				});
			}
			case FIRE -> {   // 화염 회오리 베기: 지점에 3초간 불꽃 회오리 (끌어당기며 태움)
				c.snd(t, SoundEvents.BLAZE_SHOOT, 1.2F, 0.6F);
				c.run(age -> {
					for (int k = 0; k < 3; k++) {
						double a = age * 0.6 + k * 2.1, h = (age % 10) * 0.5;
						for (double y = 0; y < 6; y += 0.8) {
							double r = 0.6 + y * 0.35;
							Fx.at(l, y < 3 ? P.FLAME : P.LAVA, t.add(Math.cos(a + y) * r, y, Math.sin(a + y) * r));
						}
					}
					if (age % 5 == 0) for (LivingEntity e : c.foes(t.add(0, 1.5, 0), 4)) { c.pull(e, t, 0.35); c.hit(e, 2.2F); e.igniteForSeconds(3); }
					if (age == 59) FireStormSpell.fireBlast(l, p, t, 3.5, 6F * c.m);
					return age >= 60;
				});
			}
			case WATER -> {   // 해일 가르기: 내 앞에서 바라본 곳까지 물결이 밀고 나감
				Vec3 s = c.pos(), d = t.subtract(s).multiply(1, 0, 1);
				double len = Math.max(6, Math.min(16, d.length()));
				Vec3 dir = d.lengthSqr() < 0.01 ? c.look() : d.normalize();
				Vec3 side = new Vec3(-dir.z, 0, dir.x);
				final Set<LivingEntity> hit = new HashSet<>();
				c.snd(s, SoundEvents.PLAYER_SPLASH_HIGH_SPEED, 1.2F, 0.6F);
				c.run(age -> {
					double x = 1 + age * 1.2;
					if (x > len) return true;
					Vec3 front = s.add(dir.scale(x));
					for (double w = -2.5; w <= 2.5; w += 0.5) {
						Vec3 q = front.add(side.scale(w));
						Fx.at(l, P.SPLASH, q.add(0, 0.4 + Math.random() * 1.6, 0));
						Fx.at(l, P.FALLING_WATER, q.add(0, 2.2, 0));
					}
					for (LivingEntity e : c.foes(front.add(0, 1, 0), 2.8)) {
						if (hit.add(e)) c.hit(e, 7);
						Fx.launch(e, dir.x * 0.9, 0.25, dir.z * 0.9);
					}
					return false;
				});
			}
			case GRASS -> {   // 덩굴 감옥: 지점 주변을 3초 묶고, 1.5초 뒤 가시가 터짐
				List<LivingEntity> caught = c.foes(t.add(0, 1, 0), 4.5);
				for (LivingEntity e : caught) { c.eff(e, MobEffects.SLOWNESS, 60, 6); c.eff(e, MobEffects.WEAKNESS, 60, 0); }
				c.ring(t, 4.5);
				c.snd(t, SoundEvents.AZALEA_LEAVES_BREAK, 1.2F, 0.6F);
				c.run(age -> {
					for (LivingEntity e : caught) if (e.isAlive() && age % 3 == 0)
						for (int k = 0; k < 4; k++) { double a = age * 0.4 + k * Math.PI / 2; Fx.at(l, Fx.dust(0x3E8E3A, 1.0F), e.position().add(Math.cos(a) * 0.7, (age % 20) * 0.1, Math.sin(a) * 0.7)); }
					if (age == 30) {
						for (LivingEntity e : caught) if (e.isAlive()) { c.hit(e, 9); Fx.burst(l, P.CHERRY_LEAVES, e.position().add(0, 1, 0), 20, 0.6, 0.05); }
						c.shock(t, 3.5, 20);
						c.snd(t, SoundEvents.SWEET_BERRY_BUSH_BREAK, 1.2F, 0.6F);
					}
					return age >= 30;
				});
			}
			case WIND -> {   // 진공 삼연참: 바람 칼날 3개가 부채꼴로 날아감 (관통)
				Vec3 eye = p.getEyePosition().add(0, -0.4, 0);
				double yaw = Math.toRadians(p.getYRot());
				final Vec3[] dir = new Vec3[3]; final Vec3[] cur = new Vec3[3]; final Set<LivingEntity> hit = new HashSet<>();
				for (int i = 0; i < 3; i++) { double a = yaw + Math.toRadians((i - 1) * 14); dir[i] = new Vec3(-Math.sin(a), 0, Math.cos(a)); cur[i] = eye; }
				c.snd(eye, SoundEvents.BREEZE_SHOOT, 1F, 1F);
				c.run(age -> {
					for (int i = 0; i < 3; i++) {
						Vec3 nx = cur[i].add(dir[i].scale(1.6));
						Vec3 side = new Vec3(-dir[i].z, 0, dir[i].x);
						for (double w = -1.4; w <= 1.4; w += 0.35) Fx.at(l, P.GUST, nx.add(side.scale(w)).add(0, -Math.abs(w) * 0.3, 0));
						cur[i] = nx;
						for (LivingEntity e : c.foes(nx, 1.8)) if (hit.add(e)) { c.hit(e, 7); Fx.launch(e, dir[i].x * 0.8, 0.3, dir[i].z * 0.8); }
					}
					return age >= 12;
				});
			}
			case DARK -> {   // 그림자 검무: 그림자 검 5자루가 하나씩 지점의 적을 찌름
				List<LivingEntity> es = c.foes(t.add(0, 1, 0), 5);
				c.snd(t, SoundEvents.WITHER_SHOOT, 0.6F, 1.2F);
				c.run(age -> {
					if (age % 4 != 0) return false;
					int i = age / 4;
					if (i >= 5) return true;
					double a = i * Math.PI * 2 / 5;
					Vec3 tgt = es.isEmpty() ? t.add(0, 1, 0) : es.get(i % es.size()).position().add(0, 1, 0);
					Vec3 from = tgt.add(Math.cos(a) * 4, 3, Math.sin(a) * 4);
					Fx.line(l, c.dust(1.4F), from, tgt, 0.25);
					Fx.line(l, P.SQUID_INK, from, tgt, 0.5);
					for (LivingEntity e : c.foes(tgt, 1.6)) { c.hit(e, 4); c.eff(e, MobEffects.WITHER, 60, 0); }
					p.heal(1.0F);
					c.snd(tgt, SoundEvents.PLAYER_ATTACK_CRIT, 0.8F, 0.7F);
					return false;
				});
			}
			case LIGHT -> {   // 심판의 성검: 빛기둥을 따라 성검이 천천히 내려와 큰 피해 + 실명
				c.visual(WeaponType.SWORD, t);
				c.run(age -> {
					if (age < 7) Fx.ring(l, P.END_ROD, t.add(0, 0.2, 0), 3.5 - age * 0.4, 20, age);
					if (age == 7) {
						HeavenGateSpell.lightPillar(l, p, t);
						for (LivingEntity e : c.area(t.add(0, 1, 0), 4, 12)) { c.eff(e, MobEffects.BLINDNESS, 40, 0); c.eff(e, MobEffects.GLOWING, 120, 0); }
					}
					return age >= 7;
				});
			}
		}
	}

	// ====================================================================== 대검 (내 주변 넓게)
	private static void greatsword(Ctx c) {
		Player p = c.p; ServerLevel l = c.l;
		Vec3 o = c.pos();
		switch (c.el) {
			case ELECTRIC -> {   // 번개 검진: 대검 원진이 꽂히는 6곳에 번개, 가운데 적 감전
				c.visual(WeaponType.GREATSWORD, null);
				c.run(age -> {
					if (age == 6) for (int i = 0; i < 6; i++) {
						double a = Math.toRadians(p.getYRot() + 54) + i * Math.PI / 3;
						ElectricStormSpell.bolt(l, p, c.ground(o.add(Math.cos(a) * 4.5, 0, Math.sin(a) * 4.5)), 6.0F * c.m);
					}
					if (age > 6 && age % 6 == 0) for (LivingEntity e : c.foes(o.add(0, 1, 0), 4.5)) { c.hit(e, 2); c.eff(e, MobEffects.SLOWNESS, 20, 4); Fx.burst(l, P.ELECTRIC_SPARK, e.position().add(0, 1, 0), 8, 0.4, 0.05); }
					return age >= 30;
				});
			}
			case FIRE -> {   // 용암 균열: 여섯 갈래로 땅이 갈라지며 바깥으로 차례로 분출
				double y0 = Math.toRadians(p.getYRot());
				c.snd(o, SoundEvents.GENERIC_EXPLODE.value(), 0.8F, 0.6F);
				c.run(age -> {
					if (age % 3 != 0) return false;
					int step = age / 3;
					if (step > 6) return true;
					for (int i = 0; i < 6; i++) {
						double a = y0 + i * Math.PI / 3;
						Vec3 q = c.ground(o.add(Math.cos(a) * (1.5 + step * 1.3), 0, Math.sin(a) * (1.5 + step * 1.3)));
						Fx.burst(l, P.LAVA, q.add(0, 0.3, 0), 10, 0.3, 0.2);
						Fx.burst(l, P.FLAME, q.add(0, 0.6, 0), 8, 0.2, 0.15);
						for (LivingEntity e : c.foes(q.add(0, 0.8, 0), 1.6)) { c.hit(e, 3); e.igniteForSeconds(4); Fx.launch(e, 0, 0.5, 0); }
					}
					c.snd(o, SoundEvents.FIRECHARGE_USE, 0.7F, 0.6F + step * 0.1F);
					return false;
				});
			}
			case WATER -> {   // 소용돌이 베기: 1.5초 끌어당긴 뒤 큰 물보라
				c.snd(o, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.2F, 0.8F);
				c.run(age -> {
					Vec3 cc = c.pos();
					for (int k = 0; k < 3; k++) { double a = age * 0.5 + k * 2.1, r = 7 - (age % 15) * 0.4; Fx.at(l, P.SPLASH, cc.add(Math.cos(a) * r, 0.5, Math.sin(a) * r)); }
					Fx.ring(l, P.DRIPPING_WATER, cc.add(0, 0.3, 0), 6.5, 30, age * 0.2);
					if (age % 3 == 0) for (LivingEntity e : c.foes(cc.add(0, 1, 0), 7.5)) c.pull(e, cc, 0.45);
					if (age == 30) {
						WaterOrbSpell.splash(l, p, cc.add(0, 0.5, 0), 11F * c.m, 4.5);
						c.shock(cc, 4, 22);
					}
					return age >= 30;
				});
			}
			case GRASS -> {   // 가시 정원: 안쪽·바깥쪽 두 겹 가시가 솟아 묶고, 나는 회복
				c.snd(o, SoundEvents.AZALEA_LEAVES_BREAK, 1.2F, 0.5F);
				c.run(age -> {
					if (age == 0 || age == 8) {
						double r = age == 0 ? 3 : 5.5;
						c.shock(o, r, 26);
						for (LivingEntity e : c.foes(o.add(0, 1, 0), r + 1)) {
							if (age == 8 && e.position().distanceTo(o) < 3) continue;
							c.hit(e, 6); c.eff(e, MobEffects.SLOWNESS, 60, 3); c.eff(e, MobEffects.POISON, 60, 0);
						}
						Fx.ring(l, P.CHERRY_LEAVES, o.add(0, 0.5, 0), r, 30, 0);
						p.heal(2 * c.m);
					}
					return age >= 8;
				});
			}
			case WIND -> {   // 폭풍 회전: 3초 동안 회오리가 되어 계속 베고 밀어냄 (이동 가능)
				p.addEffect(new MobEffectInstance(MobEffects.SPEED, 60, 1, false, false));
				c.snd(o, SoundEvents.BREEZE_IDLE_GROUND, 1.2F, 0.6F);
				c.run(age -> {
					Vec3 cc = c.pos();
					for (int k = 0; k < 2; k++) { double a = age * 0.9 + k * Math.PI; for (double y = 0; y < 3; y += 0.6) Fx.at(l, P.GUST, cc.add(Math.cos(a + y) * (1.5 + y), y, Math.sin(a + y) * (1.5 + y))); }
					if (age % 4 == 0) {
						for (LivingEntity e : c.foes(cc.add(0, 1, 0), 3.8)) { c.hit(e, 2.4F); c.push(e, cc, 0.7, 0.3); }
						c.snd(cc, SoundEvents.PLAYER_ATTACK_SWEEP, 0.6F, 1.2F);
					}
					return age >= 60;
				});
			}
			case DARK -> {   // 심연 낙하: 원진이 땅에서 솟고, 적을 땅에 붙잡아 시듦
				c.visual(WeaponType.GREATSWORD, null);
				c.run(age -> {
					if (age == 6) for (LivingEntity e : c.area(o.add(0, 1, 0), 5.5, 7)) { c.eff(e, MobEffects.SLOWNESS, 80, 5); c.eff(e, MobEffects.WITHER, 80, 1); Fx.launch(e, 0, -0.8, 0); }
					if (age > 6 && age % 10 == 0) { for (LivingEntity e : c.area(o.add(0, 1, 0), 5.5, 1.5F)) Fx.burst(l, P.SQUID_INK, e.position().add(0, 0.5, 0), 10, 0.3, 0.02); p.heal(1); }
					return age >= 40;
				});
			}
			case LIGHT -> {   // 성역 낙인: 원진 안에 4초 동안 성역 — 적은 매초 피해, 아군은 회복
				c.visual(WeaponType.GREATSWORD, null);
				c.run(age -> {
					if (age >= 6 && age % 10 == 0) {
						Fx.ring(l, P.END_ROD, o.add(0, 0.3, 0), 5, 40, age);
						for (LivingEntity e : c.area(o.add(0, 1, 0), 5.5, 3)) c.eff(e, MobEffects.GLOWING, 40, 0);
						for (Player a : l.getEntitiesOfClass(Player.class, new AABB(o, o).inflate(5.5))) { a.heal(1.5F); Fx.burst(l, P.END_ROD, a.position().add(0, 1, 0), 6, 0.4, 0.02); }
					}
					return age >= 86;
				});
			}
		}
	}

	// ====================================================================== 망치 (내려찍기)
	private static void hammer(Ctx c) {
		Player p = c.p; ServerLevel l = c.l;
		Vec3 o = c.pos(), f = c.look();
		Vec3 front = c.ground(o.add(f.scale(3.2)));
		switch (c.el) {
			case ELECTRIC -> {   // 천둥 망치: 망치가 떨어지고 번개 8개가 원을 그리며 퍼짐
				c.visual(WeaponType.HAMMER, null);
				c.run(age -> {
					if (age == 5) { c.area(front.add(0, 1, 0), 4, 10); c.ring(front, 4); }
					if (age > 5 && age <= 13 && age % 2 == 0) {
						int k = (age - 6) / 2;
						for (int j = 0; j < 2; j++) {
							double a = (k * 2 + j) * Math.PI / 4;
							ElectricStormSpell.bolt(l, p, c.ground(front.add(Math.cos(a) * (2 + k), 0, Math.sin(a) * (2 + k))), 4.5F * c.m);
						}
					}
					return age >= 13;
				});
			}
			case FIRE -> {   // 화산 분화: 망치가 떨어진 뒤 주변 7칸에 불기둥이 세 번 솟음
				c.visual(WeaponType.HAMMER, null);
				c.run(age -> {
					if (age == 5) FireStormSpell.fireBlast(l, p, front, 4.0, 9F * c.m);
					if (age == 12 || age == 20 || age == 28) for (int i = 0; i < 5; i++) {
						Vec3 q = c.ground(front.add((Math.random() - 0.5) * 12, 0, (Math.random() - 0.5) * 12));
						for (double y = 0; y < 4; y += 0.4) Fx.at(l, y < 2 ? P.LAVA : P.FLAME, q.add((Math.random() - 0.5) * 0.6, y, (Math.random() - 0.5) * 0.6));
						for (LivingEntity e : c.foes(q.add(0, 1, 0), 2.2)) { c.hit(e, 4); e.igniteForSeconds(5); Fx.launch(e, 0, 0.7, 0); }
						c.snd(q, SoundEvents.FIRECHARGE_USE, 0.7F, 0.6F);
					}
					return age >= 28;
				});
			}
			case WATER -> {   // 빙결 진동: 주변을 3초 얼린 뒤 산산조각
				List<LivingEntity> fr = c.area(o.add(0, 1, 0), 6.5, 6);
				for (LivingEntity e : fr) { c.eff(e, MobEffects.SLOWNESS, 60, 10); e.setTicksFrozen(Math.max(e.getTicksFrozen(), 200)); }
				c.shock(o, 5, 60);
				c.snd(o, SoundEvents.GLASS_BREAK, 1.2F, 0.5F);
				c.run(age -> {
					if (age % 6 == 0) for (LivingEntity e : fr) if (e.isAlive()) Fx.burst(l, Fx.dust(0xCFF4FF, 1.2F), e.position().add(0, 1, 0), 6, 0.4, 0);
					if (age == 50) {
						for (LivingEntity e : fr) if (e.isAlive()) { c.hit(e, 10); Fx.burst(l, Fx.dust(0xE8FAFF, 1.6F), e.position().add(0, 1, 0), 30, 0.6, 0.15); }
						c.snd(o, SoundEvents.GLASS_BREAK, 1.4F, 0.8F);
					}
					return age >= 50;
				});
			}
			case GRASS -> {   // 뿌리 융기: 앞으로 12칸 뿌리가 땅을 뚫고 차례로 솟아 띄움
				c.snd(o, SoundEvents.ROOTED_DIRT_BREAK, 1.2F, 0.5F);
				final Set<LivingEntity> hit = new HashSet<>();
				c.run(age -> {
					double d = 1.5 + age * 1.2;
					if (d > 13) return true;
					Vec3 q = c.ground(o.add(f.scale(d)));
					for (double y = 0; y < 2.5; y += 0.35) Fx.at(l, Fx.dust(0x6B4A2A, 1.4F), q.add((Math.random() - 0.5) * 0.8, y, (Math.random() - 0.5) * 0.8));
					Fx.burst(l, P.CHERRY_LEAVES, q.add(0, 2.4, 0), 4, 0.4, 0.05);
					for (LivingEntity e : c.foes(q.add(0, 1, 0), 2.2)) if (hit.add(e)) { c.hit(e, 9); Fx.launch(e, f.x * 0.3, 1.0, f.z * 0.3); c.eff(e, MobEffects.POISON, 60, 0); }
					return false;
				});
			}
			case WIND -> {   // 대기 붕괴: 10칸 안 적을 한가운데로 빨아들인 뒤 위로 터뜨림
				c.snd(o, SoundEvents.BREEZE_INHALE, 1.4F, 0.6F);
				c.run(age -> {
					Fx.ring(l, P.GUST, o.add(0, 0.5, 0), 10 - age * 0.45, 24, age * 0.3);
					if (age < 18 && age % 2 == 0) for (LivingEntity e : c.foes(o.add(0, 1, 0), 10)) c.pull(e, o, 0.55);
					if (age == 20) {
						for (LivingEntity e : c.area(o.add(0, 1, 0), 4.5, 12)) Fx.launch(e, 0, 1.3, 0);
						GaleSpell.gust(l, p, o);
						c.shock(o, 3.5, 20);
						c.snd(o, SoundEvents.BREEZE_WIND_CHARGE_BURST.value(), 1.4F, 0.6F);
					}
					return age >= 20;
				});
			}
			case DARK -> {   // 중력 붕괴: 주변 적을 땅에 짓눌러 2초간 맥박 피해
				List<LivingEntity> ps = c.foes(o.add(0, 1, 0), 7);
				c.shock(o, 5.5, 40);
				c.snd(o, SoundEvents.WARDEN_SONIC_BOOM, 0.6F, 0.5F);
				c.run(age -> {
					for (LivingEntity e : ps) if (e.isAlive()) { Fx.launch(e, 0, -0.6, 0); c.eff(e, MobEffects.SLOWNESS, 10, 6); }
					if (age % 8 == 0) {
						for (LivingEntity e : ps) if (e.isAlive()) { c.hit(e, 3.5F); Fx.burst(l, c.dust(1.4F), e.position().add(0, 0.3, 0), 12, 0.5, 0); }
						Fx.ring(l, P.SQUID_INK, o.add(0, 0.2, 0), 1 + (age / 8) * 1.2, 30, age);
					}
					return age >= 40;
				});
			}
			case LIGHT -> {   // 신성 충격: 빛의 충격파 세 겹 (적 피해, 아군 회복)
				c.visual(WeaponType.HAMMER, null);
				final Set<LivingEntity> hit = new HashSet<>();
				c.run(age -> {
					if (age >= 5 && age <= 21 && (age - 5) % 8 == 0) {
						double r = 3 + (age - 5) / 8 * 2.5;
						Fx.ring(l, P.END_ROD, front.add(0, 0.3, 0), r, (int) (r * 12), 0);
						c.shock(front, r, 16);
						for (LivingEntity e : c.foes(front.add(0, 1, 0), r)) if (hit.add(e) || age == 21) { c.hit(e, 6); c.eff(e, MobEffects.GLOWING, 80, 0); }
						for (Player a : l.getEntitiesOfClass(Player.class, new AABB(front, front).inflate(r))) a.heal(1.5F);
						c.snd(front, SoundEvents.BEACON_POWER_SELECT, 1F, 1.6F);
					}
					return age >= 21;
				});
			}
		}
	}

	// ====================================================================== 낫 (회전 베기 계열)
	private static void scythe(Ctx c) {
		Player p = c.p; ServerLevel l = c.l;
		switch (c.el) {
			case ELECTRIC -> {   // 번개 수확: 회전 베기 + 맞은 적마다 번개가 다음 적으로 튐
				SpellManager.add(new ReapSpell(l, p, c.el, c.m));
				c.visual(WeaponType.SCYTHE, null);
				c.run(age -> {
					if (age == 8) for (LivingEntity e : c.foes(c.chest(), 5)) {
						List<LivingEntity> nb = c.foes(e.position(), 5); nb.remove(e);
						if (nb.isEmpty()) continue;
						LivingEntity n = nb.get(0);
						Fx.line(l, P.ELECTRIC_SPARK, e.position().add(0, 1, 0), n.position().add(0, 1, 0), 0.3);
						c.hit(n, 4);
					}
					return age >= 8;
				});
			}
			case FIRE -> {   // 불꽃 원무: 회전 베기 + 발밑에 3초간 타는 불의 원
				SpellManager.add(new ReapSpell(l, p, c.el, c.m));
				c.visual(WeaponType.SCYTHE, null);
				Vec3 o = c.pos();
				c.run(age -> {
					if (age % 3 == 0) Fx.ring(l, P.FLAME, o.add(0, 0.3, 0), 4.5, 40, age);
					if (age % 10 == 0) for (LivingEntity e : c.foes(o.add(0, 1, 0), 5)) { c.hit(e, 2); e.igniteForSeconds(3); }
					return age >= 60;
				});
			}
			case WATER -> {   // 조류의 낫: 낫을 던지면 앞으로 갔다가 되돌아옴 (가는 길·오는 길 모두 벰)
				Vec3 dir = p.getLookAngle().multiply(1, 0.2, 1).normalize();
				final Vec3[] cur = {c.chest()};
				final Set<LivingEntity> out = new HashSet<>(), back = new HashSet<>();
				c.snd(cur[0], SoundEvents.TRIDENT_THROW.value(), 1F, 0.8F);
				c.run(age -> {
					boolean ret = age >= 10;
					Vec3 nx = ret ? cur[0].add(c.chest().subtract(cur[0]).normalize().scale(1.6)) : cur[0].add(dir.scale(1.6));
					for (int k = 0; k < 6; k++) { double a = age * 1.4 + k * Math.PI / 3; Fx.at(l, P.SPLASH, nx.add(Math.cos(a) * 1.4, Math.sin(a) * 0.3, Math.sin(a) * 1.4)); }
					Fx.burst(l, c.dust(1.4F), nx, 4, 0.2, 0);
					cur[0] = nx;
					for (LivingEntity e : c.foes(nx, 2.2)) if ((ret ? back : out).add(e)) { c.hit(e, 6); c.eff(e, MobEffects.SLOWNESS, 40, 2); }
					return ret && nx.distanceTo(c.chest()) < 1.8 || age > 30;
				});
			}
			case GRASS -> {   // 생명 수확: 회전 베기로 벤 만큼 크게 회복 + 주변 아군도
				SpellManager.add(new ReapSpell(l, p, c.el, c.m));
				c.visual(WeaponType.SCYTHE, null);
				c.run(age -> {
					if (age == 12) {
						int n = c.foes(c.chest(), 5).size();
						for (Player a : l.getEntitiesOfClass(Player.class, p.getBoundingBox().inflate(8))) { a.heal(1.5F + n * 1.2F); Fx.burst(l, P.HAPPY_VILLAGER, a.position().add(0, 1, 0), 10, 0.4, 0.02); }
						Fx.ring(l, P.CHERRY_LEAVES, c.pos().add(0, 0.5, 0), 5, 40, 0);
					}
					return age >= 12;
				});
			}
			case WIND -> {   // 회오리 낫: 세 바퀴, 더 넓게, 적을 안으로 끌어들임
				SpellManager.add(new ReapSpell(l, p, c.el, c.m));
				c.visual(WeaponType.SCYTHE, null);
				c.run(age -> {
					Vec3 cc = c.pos();
					if (age % 2 == 0) for (LivingEntity e : c.foes(cc.add(0, 1, 0), 8)) c.pull(e, cc, 0.4);
					Fx.ring(l, P.GUST, cc.add(0, 0.8, 0), 6.5, 20, age * 0.5);
					if (age == 14) for (LivingEntity e : c.area(cc.add(0, 1, 0), 6.5, 5)) Fx.launch(e, 0, 0.8, 0);
					return age >= 14;
				});
			}
			case DARK -> {   // 영혼 수확: 주변 적에 표식 → 1.5초 뒤 영혼을 뜯어내 피해 + 그만큼 회복
				List<LivingEntity> ms = c.foes(c.chest(), 8);
				for (LivingEntity e : ms) c.eff(e, MobEffects.GLOWING, 40, 0);
				c.visual(WeaponType.SCYTHE, null);
				c.snd(c.pos(), SoundEvents.SOUL_ESCAPE.value(), 1.4F, 0.6F);
				c.run(age -> {
					for (LivingEntity e : ms) if (e.isAlive() && age % 4 == 0) Fx.ring(l, P.SOUL, e.position().add(0, e.getBbHeight() + 0.3, 0), 0.6, 8, age);
					if (age == 30) for (LivingEntity e : ms) if (e.isAlive()) {
						c.hit(e, 9); c.eff(e, MobEffects.WITHER, 80, 1);
						Fx.line(l, P.SOUL, e.position().add(0, 1, 0), c.chest(), 0.4);
						p.heal(1.5F);
					}
					return age >= 30;
				});
			}
			case LIGHT -> {   // 초승달 섬광: 거대한 빛의 초승달이 앞으로 날아감 (관통)
				Vec3 dir = c.look();
				Vec3 side = new Vec3(-dir.z, 0, dir.x);
				final Vec3[] cur = {c.chest()};
				final Set<LivingEntity> hit = new HashSet<>();
				c.snd(cur[0], SoundEvents.PLAYER_ATTACK_SWEEP, 1.4F, 0.6F);
				c.run(age -> {
					Vec3 nx = cur[0].add(dir.scale(1.8));
					for (double w = -3.2; w <= 3.2; w += 0.3) {
						double back = (w * w) * 0.12;
						Fx.at(l, P.END_ROD, nx.add(side.scale(w)).add(dir.scale(-back)));
						Fx.at(l, Fx.dust(0xFFF4B0, 1.2F), nx.add(side.scale(w * 0.9)).add(dir.scale(-back - 0.3)));
					}
					cur[0] = nx;
					for (LivingEntity e : c.foes(nx, 3.3)) if (hit.add(e)) { c.hit(e, 10); c.eff(e, MobEffects.GLOWING, 100, 0); }
					return age >= 12;
				});
			}
		}
	}

	// ====================================================================== 마법서 (지원 + 속성 효과)
	private static void book(Ctx c) {
		Player p = c.p; ServerLevel l = c.l;
		ElementEffects.supportAura(c.el, l, p);   // 속성별 버프 (기존)
		c.visual(WeaponType.BOOK, null);
		Vec3 o = c.pos();
		switch (c.el) {
			case ELECTRIC -> c.run(age -> {   // 신속의 서: 주변 적 3명에게 번개
				if (age == 10 || age == 16 || age == 22) {
					List<LivingEntity> es = c.foes(o.add(0, 1, 0), 10);
					if (!es.isEmpty()) ElectricStormSpell.bolt(l, p, es.get((age / 6) % es.size()).position(), 5.0F * c.m);
				}
				return age >= 22;
			});
			case FIRE -> c.run(age -> {   // 투지의 서: 불꽃 고리가 퍼져 적을 태움
				double r = age * 0.5;
				Fx.ring(l, P.FLAME, o.add(0, 0.4, 0), r, (int) (r * 8) + 4, 0);
				if (age % 3 == 0) for (LivingEntity e : c.foes(o.add(0, 1, 0), r + 0.6)) if (e.position().distanceTo(o) > r - 1.6) { c.hit(e, 2); e.igniteForSeconds(4); }
				return age >= 16;
			});
			case WATER -> {   // 치유의 서: 나쁜 효과 정화 + 큰 회복
				for (Player a : l.getEntitiesOfClass(Player.class, p.getBoundingBox().inflate(10))) {
					for (Holder<MobEffect> bad : List.of(MobEffects.POISON, MobEffects.WITHER, MobEffects.SLOWNESS, MobEffects.WEAKNESS, MobEffects.BLINDNESS, MobEffects.DARKNESS)) a.removeEffect(bad);
					Fx.supportHeal(p, a, 0.15F);
					Fx.burst(l, P.SPLASH, a.position().add(0, 1.5, 0), 20, 0.5, 0.05);
				}
			}
			case GRASS -> c.run(age -> {   // 재생의 정원: 6초 동안 꽃밭 (매초 아군 회복, 적 느려짐)
				if (age % 20 == 0) {
					Fx.ring(l, P.CHERRY_LEAVES, o.add(0, 0.4, 0), 6, 40, age);
					for (Player a : l.getEntitiesOfClass(Player.class, new AABB(o, o).inflate(6))) a.heal(1.5F);
					for (LivingEntity e : c.foes(o.add(0, 1, 0), 6)) c.eff(e, MobEffects.SLOWNESS, 25, 1);
				}
				return age >= 120;
			});
			case WIND -> {   // 비상의 서: 아군은 공중으로 뜨고 천천히 낙하, 적은 멀리 밀려남
				for (Player a : l.getEntitiesOfClass(Player.class, p.getBoundingBox().inflate(10))) { a.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 0)); Skills.launchSelf(a, new Vec3(0, 0.9, 0)); }
				for (LivingEntity e : c.foes(o.add(0, 1, 0), 7)) { c.push(e, o, 1.6, 0.4); c.hit(e, 3); }
				GaleSpell.gust(l, p, o);
			}
			case DARK -> c.run(age -> {   // 흡혈의 서: 4초 동안 주변 적의 생명을 빨아 아군에게
				if (age % 10 == 0) {
					List<LivingEntity> es = c.foes(o.add(0, 1, 0), 8);
					for (LivingEntity e : es) { c.hit(e, 1.5F); Fx.line(l, c.dust(0.8F), e.position().add(0, 1, 0), c.chest(), 0.6); }
					if (!es.isEmpty()) for (Player a : l.getEntitiesOfClass(Player.class, p.getBoundingBox().inflate(8))) a.heal(0.6F * es.size());
				}
				return age >= 80;
			});
			case LIGHT -> {   // 가호의 서: 짧은 무적에 가까운 보호막
				for (Player a : l.getEntitiesOfClass(Player.class, p.getBoundingBox().inflate(10))) {
					a.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 60, 3));
					Fx.sphere(l, P.END_ROD, a.position().add(0, 1, 0), 1.3, 30);
				}
			}
		}
	}

	// ====================================================================== 활 (완전히 당겨 쐈을 때 추가 기술)
	public static void bowFull(Element e, ServerLevel l, Player p) {
		Ctx c = new Ctx(l, p, e);
		Vec3 t = c.aim(40);
		switch (e) {
			case ELECTRIC -> c.run(age -> {   // 뇌전 화살비: 바라본 곳에 번개 6발
				if (age >= 8 && age % 3 == 0) ElectricStormSpell.bolt(l, p, c.ground(t.add((Math.random() - 0.5) * 7, 0, (Math.random() - 0.5) * 7)), 4.5F * c.m);
				return age >= 24;
			});
			case FIRE -> c.run(age -> {   // 유성 화살: 하늘에서 불덩이가 떨어져 폭발
				Vec3 m = t.add(0, 16 * (1 - age / 12.0), 0);
				Fx.burst(l, P.FLAME, m, 10, 0.4, 0.05); Fx.burst(l, P.LARGE_SMOKE, m.add(0, 1, 0), 3, 0.3, 0.02);
				if (age == 12) { FireStormSpell.fireBlast(l, p, t, 4.5, 12F * c.m); c.shock(t, 3.5, 20); }
				return age >= 12;
			});
			case WATER -> c.run(age -> {   // 빙우: 4초 동안 얼음 비 (느려짐)
				for (int i = 0; i < 6; i++) Fx.move(l, P.FALLING_WATER, t.add((Math.random() - 0.5) * 9, 6, (Math.random() - 0.5) * 9), new Vec3(0, -0.6, 0));
				if (age % 10 == 0) for (LivingEntity en : c.area(t.add(0, 1, 0), 4.8, 2)) { c.eff(en, MobEffects.SLOWNESS, 30, 3); en.setTicksFrozen(Math.max(en.getTicksFrozen(), 140)); }
				return age >= 80;
			});
			case GRASS -> {   // 덩굴 화살: 바라본 곳 주변을 묶고 중독
				for (LivingEntity en : c.area(t.add(0, 1, 0), 4, 5)) { c.eff(en, MobEffects.SLOWNESS, 80, 6); c.eff(en, MobEffects.POISON, 80, 1); }
				c.shock(t, 3.5, 30);
			}
			case WIND -> {   // 관통 돌풍: 25칸 일직선 바람 (모두 관통, 밀어냄)
				Vec3 s = p.getEyePosition(), d = p.getLookAngle();
				Set<LivingEntity> hit = new HashSet<>();
				for (double x = 1; x < 25; x += 1) {
					Vec3 q = s.add(d.scale(x));
					Fx.at(l, P.GUST, q);
					for (LivingEntity en : c.foes(q, 1.8)) if (hit.add(en)) { c.hit(en, 8); Fx.launch(en, d.x * 1.2, 0.3, d.z * 1.2); }
				}
				c.snd(s, SoundEvents.BREEZE_SHOOT, 1.2F, 0.6F);
			}
			case DARK -> {   // 그림자 추적: 박쥐 그림자 5마리가 가까운 적을 쫓아 묾
				List<LivingEntity> es = c.foes(t, 14);
				if (es.isEmpty()) es = c.foes(c.chest(), 14);
				final List<LivingEntity> tg = es.subList(0, Math.min(5, es.size()));
				final Vec3[] cur = new Vec3[tg.size()];
				for (int i = 0; i < cur.length; i++) cur[i] = c.chest();
				c.run(age -> {
					boolean any = false;
					for (int i = 0; i < cur.length; i++) {
						if (cur[i] == null || !tg.get(i).isAlive()) continue;
						any = true;
						Vec3 to = tg.get(i).position().add(0, 1, 0), dd = to.subtract(cur[i]);
						Vec3 nx = dd.length() < 1.4 ? to : cur[i].add(dd.normalize().scale(1.4)).add(0, Math.sin(age + i) * 0.3, 0);
						Fx.burst(l, P.SQUID_INK, nx, 3, 0.15, 0);
						Fx.at(l, c.dust(1.2F), nx);
						cur[i] = nx;
						if (nx.distanceTo(to) < 0.3) { c.hit(tg.get(i), 6); c.eff(tg.get(i), MobEffects.WITHER, 60, 0); p.heal(0.5F); cur[i] = null; }
					}
					return !any || age > 60;
				});
			}
			case LIGHT -> c.run(age -> {   // 빛의 화살비: 빛기둥 5개
				if (age >= 6 && age % 4 == 2 && age <= 22) HeavenGateSpell.lightPillar(l, p, c.ground(t.add((Math.random() - 0.5) * 8, 0, (Math.random() - 0.5) * 8)));
				return age >= 22;
			});
		}
		if (p instanceof ServerPlayer sp) sp.sendOverlayMessage(net.minecraft.network.chat.Component.literal("◆ " + name(WeaponType.BOW, e)).withColor(e.color));
	}

	private static void bow(Ctx c) { bowFull(c.el, c.l, c.p); }

	// ====================================================================== 방패 (Shift+우클릭)
	private static void shield(Ctx c) {
		Player p = c.p; ServerLevel l = c.l;
		Vec3 o = c.pos();
		switch (c.el) {
			case ELECTRIC -> {   // 방전 방벽: 2초 동안 막다가 주변으로 방전
				p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 40, 2));
				c.run(age -> {
					Fx.sphere(l, P.ELECTRIC_SPARK, c.chest(), 1.6, 12);
					if (age == 40) for (LivingEntity e : c.foes(c.chest(), 7)) { ElectricStormSpell.drawBolt(l, e.position()); c.hit(e, 7); c.eff(e, MobEffects.SLOWNESS, 40, 4); }
					return age >= 40;
				});
			}
			case FIRE -> {   // 화염 돌진: 방패를 앞세워 돌진, 부딪힌 적을 밀치고 태움
				Vec3 f = c.look();
				Skills.launchSelf(p, new Vec3(f.x * 1.9, 0.12, f.z * 1.9));
				final Set<LivingEntity> hit = new HashSet<>();
				c.run(age -> {
					Fx.burst(l, P.FLAME, c.chest().add(f.scale(0.8)), 8, 0.4, 0.03);
					for (LivingEntity e : c.foes(c.chest().add(f.scale(1.2)), 2.0)) if (hit.add(e)) { c.hit(e, 7); e.igniteForSeconds(5); Fx.launch(e, f.x * 1.6, 0.5, f.z * 1.6); }
					return age >= 10;
				});
			}
			case WATER -> c.run(age -> {   // 물의 장막: 5초 동안 물 돔 (안의 아군 피해 감소, 적은 밀려남)
				if (age % 4 == 0) Fx.sphere(l, P.SPLASH, o.add(0, 1, 0), 4.5, 40);
				if (age % 10 == 0) {
					for (Player a : l.getEntitiesOfClass(Player.class, new AABB(o, o).inflate(4.5))) a.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 15, 1));
					for (LivingEntity e : c.foes(o.add(0, 1, 0), 4.5)) c.push(e, o, 0.8, 0.2);
				}
				return age >= 100;
			});
			case GRASS -> c.run(age -> {   // 가시 갑옷: 6초 동안 매초 주변 가시 폭발 + 재생
				if (age == 0) p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 120, 1));
				if (age % 20 == 0) { for (LivingEntity e : c.area(c.chest(), 3.2, 3)) c.eff(e, MobEffects.POISON, 40, 0); Fx.sphere(l, Fx.dust(0x5BD96A, 1.2F), c.chest(), 2.4, 30); }
				return age >= 120;
			});
			case WIND -> {   // 반사 돌풍: 주변 적을 멀리 날리고 날아오는 투사체를 지움
				for (LivingEntity e : c.foes(o.add(0, 1, 0), 8)) { c.push(e, o, 2.0, 0.6); c.hit(e, 4); }
				for (Projectile pr : l.getEntitiesOfClass(Projectile.class, p.getBoundingBox().inflate(8), pr -> pr.getOwner() != p)) { Fx.burst(l, P.CLOUD, pr.position(), 6, 0.2, 0.02); pr.discard(); }
				GaleSpell.gust(l, p, o);
				c.shock(o, 4, 18);
			}
			case DARK -> {   // 그림자 흡수: 주변 적의 생명을 빨아 보호막으로
				List<LivingEntity> es = c.area(c.chest(), 7, 4);
				for (LivingEntity e : es) Fx.line(l, c.dust(0.9F), e.position().add(0, 1, 0), c.chest(), 0.5);
				p.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, Math.min(4, es.size())));
				c.snd(o, SoundEvents.WITHER_AMBIENT, 0.6F, 1.4F);
			}
			case LIGHT -> {   // 성스러운 방패: 나는 강한 방어, 주변 몹은 나를 노림, 아군 회복
				p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 100, 2));
				for (LivingEntity e : c.foes(o.add(0, 1, 0), 12)) if (e instanceof net.minecraft.world.entity.Mob mob) mob.setTarget(p);
				for (Player a : l.getEntitiesOfClass(Player.class, p.getBoundingBox().inflate(8))) if (a != p) a.heal(4);
				Fx.sphere(l, P.END_ROD, c.chest(), 1.8, 50);
				c.snd(o, SoundEvents.BEACON_ACTIVATE, 1F, 1.4F);
			}
		}
		c.snd(o, SoundEvents.SHIELD_BLOCK.value(), 1F, 0.8F);
	}
}
