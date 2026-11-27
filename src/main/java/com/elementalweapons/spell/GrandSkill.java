package com.elementalweapons.spell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.WeaponType;
import com.elementalweapons.mob.ModMobs;

/**
 * 대형 기술 (직업 × 속성 49종). 모양·동작은 직업(무기)이, 색·재질·맞았을 때 효과는 속성이 정함.
 *  기사(검) 성검 강림 / 전사(대검·망치) 거신의 전부 / 암살자(단검) 천인참 / 궁수(활) 천궁 / 사신(낫) 명계의 대낫
 *  서포터(마법서) 여신의 성역 / 마법사(지팡이) 불 염제 · 전기 뇌신 · 물 리바이어던 · 풀 세계수 · 바람 폭풍의 대붕 · 어둠 심연의 문 · 빛 천국의 문
 */
public class GrandSkill extends Spell {
	public static final int SWORD = 0, AXE = 1, DAGGER = 2, BOW = 3, ARROW = 4, REAPER = 5, GODDESS = 6, TITAN = 7, LEVIATHAN = 8, TREE = 9, ROC = 10, ABYSS = 11;
	private static final Map<UUID, Long> COOL = new HashMap<>();
	public static final int COOLDOWN = 600;

	private final Element el;
	private final int kind;
	private final Vec3 target, fwd;
	private final List<GrandVisual> vis = new ArrayList<>();
	private final Set<Integer> hitOnce = new HashSet<>();
	private final Vec3[] diveTo = new Vec3[12];
	private GrandVisual main;
	private float baseYaw;

	public GrandSkill(ServerLevel level, Player caster, Element el, int kind) {
		super(level, caster);
		this.el = el;
		this.kind = kind;
		Vec3 look = caster.getLookAngle();
		this.fwd = new Vec3(look.x, 0, look.z).lengthSqr() < 1e-4 ? new Vec3(0, 0, 1) : new Vec3(look.x, 0, look.z).normalize();
		this.target = pickTarget(level, caster, fwd);
	}

	/** 이 무기·속성의 대형 기술 종류 (-1: 없음) */
	public static int kindFor(WeaponType t, Element e) {
		return switch (t) {
			case SWORD -> SWORD;
			case GREATSWORD, HAMMER -> AXE;
			case DAGGER -> DAGGER;
			case BOW -> BOW;
			case SCYTHE -> REAPER;
			case BOOK -> GODDESS;
			case STAFF -> switch (e) {
				case FIRE, ELECTRIC -> TITAN; case WATER -> LEVIATHAN; case GRASS -> TREE; case WIND -> ROC; case DARK -> ABYSS; case LIGHT -> -1;
			};
			default -> -1;
		};
	}
	public static String name(int kind, Element e) {
		return switch (kind) {
			case SWORD -> "성검 강림"; case AXE -> "거신의 전부"; case DAGGER -> "천인참"; case BOW -> "천궁"; case REAPER -> "명계의 대낫"; case GODDESS -> "여신의 성역";
			case TITAN -> e == Element.ELECTRIC ? "뇌신 강림" : "염제 강림"; case LEVIATHAN -> "심해의 군주"; case TREE -> "세계수"; case ROC -> "폭풍의 대붕"; case ABYSS -> "심연의 문";
			default -> "천국의 문";
		};
	}

	/** Shift + 우클릭 대형 기술 (지팡이가 아닌 무기). 쿨타임이면 false */
	public static boolean tryCast(ServerLevel level, ServerPlayer p, Element el, WeaponType type) {
		int kind = kindFor(type, el);
		if (kind < 0) return false;
		long now = level.getGameTime();
		Long ready = COOL.get(p.getUUID());
		if (ready != null && ready > now) {
			p.sendOverlayMessage(Component.literal(String.format("대형 기술 재사용까지 %d초", (ready - now + 19) / 20)).withStyle(ChatFormatting.GRAY));
			return true;
		}
		double cut = com.elementalweapons.growth.ModGrowth.get(p).staffCooldownFactor() * (1 - com.elementalweapons.growth.ModGrowth.paragon(p).cooldownCut());
		COOL.put(p.getUUID(), now + Math.round(COOLDOWN * cut));
		cast(level, p, el, kind);
		return true;
	}

	public static void cast(ServerLevel level, Player p, Element el, int kind) {
		SpellManager.add(new GrandSkill(level, p, el, kind));
		if (p instanceof ServerPlayer sp) {
			sp.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket(2, 24, 8));
			sp.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(Component.literal("")));
			sp.connection.send(new net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket(Component.literal("✦ " + name(kind, el) + " ✦").withStyle(s -> s.withColor(ElementFx.color(el)).withBold(true))));
		}
		Fx.sound(level, p.position(), SoundEvents.BEACON_ACTIVATE, 1.2F, 0.7F);
	}

	// ---------------- 도움 함수 ----------------
	private static Vec3 pickTarget(ServerLevel level, Player p, Vec3 fwd) {
		HitResult hit = p.pick(40, 0, false);
		Vec3 t = hit.getType() == HitResult.Type.MISS ? p.position().add(fwd.scale(16)) : hit.getLocation();
		var foes = Fx.enemies(level, t, 10);
		if (!foes.isEmpty()) t = foes.get(0).position();
		return ground(level, t);
	}
	private static Vec3 ground(ServerLevel level, Vec3 v) {
		BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos(Mth.floor(v.x), Mth.floor(v.y) + 4, Mth.floor(v.z));
		for (int i = 0; i < 40; i++) {
			if (!level.getBlockState(m).isAir() && level.getBlockState(m.above()).isAir()) return new Vec3(v.x, m.getY() + 1, v.z);
			m.move(0, -1, 0);
		}
		return v;
	}
	private GrandVisual spawn(int k, Vec3 pos) {
		GrandVisual g = ModMobs.GRAND.create(level, EntitySpawnReason.MOB_SUMMONED);
		if (g == null) return null;
		g.addTag(com.elementalweapons.world.VanillaPurge.ALLOWED);
		g.setKind(k); g.setElem(el.ordinal());
		g.snapTo(pos.x, pos.y, pos.z, 0, 0);
		level.addFreshEntity(g);
		vis.add(g);
		return g;
	}
	private static float yawOf(Vec3 dir) { return (float) Math.toDegrees(Math.atan2(-dir.x, dir.z)); }
	private static void place(GrandVisual g, Vec3 pos, float yaw) {
		if (g == null) return;
		g.setPos(pos.x, pos.y, pos.z);
		g.setYRot(yaw); g.setYBodyRot(yaw); g.setYHeadRot(yaw);
		g.life = 0;
	}
	private void strike(GrandVisual g, int side) { if (g != null) g.setStrike(g.tickCount * 2 + 2 + side); }
	private void hurt(Vec3 c, double r, float dmg) {
		for (LivingEntity e : Fx.enemies(level, c, r)) {
			if (e == caster) continue;
			Fx.hurt(level, caster, e, dmg);
		}
	}
	private List<LivingEntity> foes(double r) { return Fx.enemies(level, target, r); }
	private Vec3 randAround(Vec3 c, double r) {
		double a = level.getRandom().nextDouble() * Math.PI * 2, d = Math.sqrt(level.getRandom().nextDouble()) * r;
		return ground(level, c.add(Math.cos(a) * d, 0, Math.sin(a) * d));
	}
	private Vec3 foeOrRandom(double r) {
		var f = foes(r);
		return f.isEmpty() ? randAround(target, r * 0.7) : f.get(level.getRandom().nextInt(f.size())).position();
	}
	private void boom(Vec3 p, double r, float dmg) {
		ElementFx.impact(level, el, p, r);
		hurt(p, r, dmg);
		Fx.sound(level, p, SoundEvents.GENERIC_EXPLODE.value(), 1.4F, 0.7F);
	}
	private static float ease(float x) { x = Mth.clamp(x, 0, 1); return x * x * (3 - 2 * x); }

	@Override
	protected boolean update() {
		if (!caster.isAlive() || caster.level() != level) { end(); return true; }
		boolean done = switch (kind) {
			case SWORD -> sword(); case AXE -> axe(); case DAGGER -> dagger(); case BOW -> bow(); case REAPER -> reaper(); case GODDESS -> goddess();
			case TITAN -> titan(); case LEVIATHAN -> leviathan(); case TREE -> tree(); case ROC -> roc(); default -> abyss();
		};
		if (done) end();
		return done;
	}
	private void end() { for (GrandVisual g : vis) if (g != null && g.isAlive()) { Fx.burst(level, ElementFx.dust(el, 1.6F), g.position(), 30, 2, 0.05); g.discard(); } vis.clear(); }

	// ---------------- 기사: 성검 강림 ----------------
	private boolean sword() {
		float yaw = yawOf(caster.position().subtract(target));
		if (age == 0) {
			SpellManager.add(new MagicCircle(level, caster, el, target.add(0, 0.1, 0), 7, 110, false));
			SpellManager.add(new MagicCircle(level, caster, el, target.add(0, 58, 0), 7, 30, true));
			main = spawn(SWORD, target.add(0, 60, 0));
		}
		if (age <= 18) {
			float f = age / 18F;
			place(main, target.add(0, 60 * (1 - f * f) - 4 * f, 0), yaw);
			if (age % 2 == 0) Fx.line(level, ElementFx.particle(el), target.add(0, 60 * (1 - f * f) + 34, 0), target.add(0, 90, 0), 1.5);
		}
		if (age == 18) {
			boom(target, 8, 40);
			for (int i = 0; i < 6; i++) { double a = i * Math.PI / 3; ElementEffects.strike(el, level, caster, ground(level, target.add(Math.cos(a) * 6, 0, Math.sin(a) * 6))); }
			Fx.sound(level, target, SoundEvents.ANVIL_LAND, 1.5F, 0.5F);
		}
		if (age > 18 && age <= 40) {
			double r = (age - 18) * 0.7;
			Fx.ring(level, ElementFx.particle(el), target.add(0, 0.3, 0), r, (int) (12 + r * 6), age * 0.1);
			for (LivingEntity e : Fx.enemies(level, target, r + 1)) if (e.distanceTo(caster) > 0 && e.position().distanceTo(target) > r - 1.2 && hitOnce.add(e.getId())) { Fx.hurt(level, caster, e, 10); e.push(0, 0.6, 0); }
		}
		if (age > 18 && age < 100) {
			place(main, target.add(0, -4, 0), yaw);
			if (main != null) main.setPhase(Math.min(1, (age - 18) / 20F));
			if (age % 12 == 0) { hurt(target, 7, 6); ElementEffects.strike(el, level, caster, randAround(target, 6)); }
		}
		if (age >= 100) place(main, target.add(0, -4 + (age - 100) * (age - 100) * 0.18, 0), yaw);
		return age >= 120;
	}

	// ---------------- 전사: 거신의 전부 ----------------
	private boolean axe() {
		Vec3 away = target.subtract(caster.position()); away = new Vec3(away.x, 0, away.z).normalize();
		float yaw = yawOf(away.scale(-1));
		Vec3 pivot = target.add(0, 26.5, 0);
		if (age == 0) { SpellManager.add(new MagicCircle(level, caster, el, target.add(0, 0.1, 0), 6, 100, false)); main = spawn(AXE, pivot); }
		place(main, pivot, yaw);
		for (int k = 0; k < 3; k++) {
			int s = k * 35;
			if (age == s) strike(main, 0);
			if (age == s + 14) {
				float dmg = k == 2 ? 45 : 30; double r = k == 2 ? 9 : 6;
				boom(target, r, dmg);
				for (int i = 1; i <= 3; i++) ElementEffects.strike(el, level, caster, ground(level, target.add(away.scale(i * 3.5))));
				for (LivingEntity e : Fx.enemies(level, target, r)) e.push(0, 0.7, 0);
				Fx.sound(level, target, SoundEvents.ANVIL_LAND, 1.6F, 0.4F);
			}
		}
		return age >= 105;
	}

	// ---------------- 암살자: 천인참 ----------------
	private boolean dagger() {
		if (age == 0) {
			SpellManager.add(new MagicCircle(level, caster, el, target.add(0, 20, 0), 12, 80, true));
			for (int i = 0; i < 12; i++) spawn(DAGGER, target.add(0, 30, 0));
		}
		double spin = age * 0.06;
		for (int i = 0; i < vis.size(); i++) {
			GrandVisual g = vis.get(i);
			if (g == null || !g.isAlive()) continue;
			double a = spin + i * Math.PI / 6;
			Vec3 ring = target.add(Math.cos(a) * 11, 20 + (age < 25 ? (1 - ease(age / 25F)) * 20 : 0), Math.sin(a) * 11);
			int dive = 28 + i * 4;
			if (age < dive) place(g, ring, (float) Math.toDegrees(a));
			else if (age < dive + 4) {
				if (diveTo[i] == null) diveTo[i] = foeOrRandom(9);
				float f = (age - dive) / 4F;
				place(g, ring.lerp(diveTo[i].add(0, -1.5, 0), f * f), (float) Math.toDegrees(a));
				Fx.line(level, ElementFx.particle(el), ring, g.position(), 2);
			} else if (age == dive + 4) {
				hurt(diveTo[i], 3, 14);
				ElementEffects.strike(el, level, caster, diveTo[i]);
				Fx.sound(level, diveTo[i], SoundEvents.TRIDENT_HIT, 1.2F, 0.7F);
			} else if (age > dive + 12) { Fx.burst(level, ElementFx.dust(el, 1.4F), g.position(), 12, 1, 0.05); g.discard(); }
		}
		if (age == 82) {
			for (int k = -1; k <= 1; k += 2) Fx.line(level, ElementFx.particle(el), target.add(-8, 1, -8 * k), target.add(8, 1, 8 * k), 0.3);
			boom(target, 8, 30);
			Fx.sound(level, target, SoundEvents.PLAYER_ATTACK_SWEEP, 1.5F, 0.6F);
		}
		return age >= 92;
	}

	// ---------------- 궁수: 천궁 ----------------
	private GrandVisual arrow;
	private boolean bow() {
		Vec3 bowPos = caster.position().add(fwd.scale(-6)).add(0, 22, 0);
		Vec3 dir = target.subtract(bowPos);
		float yaw = yawOf(dir), pitch = (float) Math.toDegrees(Math.atan2(-dir.y, Math.hypot(dir.x, dir.z)));
		if (age == 0) { main = spawn(BOW, bowPos); SpellManager.add(new MagicCircle(level, caster, el, target.add(0, 0.1, 0), 9, 100, false)); }
		if (main != null && main.isAlive()) {
			place(main, bowPos, yaw); main.setPitch(pitch);
			main.setPhase(age < 30 ? ease(age / 30F) : age < 34 ? 1 - (age - 30) / 4F : 0);
		}
		if (age == 30) {
			strike(main, 0);
			arrow = spawn(ARROW, bowPos);
			Fx.sound(level, bowPos, SoundEvents.CROSSBOW_SHOOT, 1.6F, 0.5F);
		}
		if (arrow != null && age > 30 && age <= 38) {
			float f = (age - 30) / 8F;
			place(arrow, bowPos.lerp(target.add(0, 1, 0), f), yaw); arrow.setPitch(pitch);
			Fx.burst(level, ElementFx.dust(el, 1.5F), arrow.position(), 6, 0.5, 0.02);
		}
		if (age == 38) {
			boom(target, 6, 40);
			if (arrow != null) { arrow.discard(); vis.remove(arrow); }
		}
		if (age > 38 && age < 92 && age % 2 == 0) {
			Vec3 p = randAround(target, 9);
			Fx.line(level, ElementFx.particle(el), p.add(0, 18, 0), p, 1.2);
			hurt(p, 1.8, 6);
			if (age % 6 == 0) ElementEffects.strike(el, level, caster, p);
		}
		if (age == 70 && main != null) { main.discard(); vis.remove(main); main = null; }
		return age >= 95;
	}

	// ---------------- 사신: 명계의 대낫 ----------------
	private boolean reaper() {
		if (age == 0) { main = spawn(REAPER, target.add(0, -44, 0)); SpellManager.add(new MagicCircle(level, caster, el, target.add(0, 0.1, 0), 9, 125, false)); baseYaw = yawOf(caster.position().subtract(target)); }
		float y = age < 25 ? -44 + 44 * ease(age / 25F) : age > 100 ? -44 * ease((age - 100) / 25F) : 0;
		float yaw = baseYaw;
		if (age >= 35 && age < 55) yaw = baseYaw + (age - 35) / 20F * 360;
		if (age >= 70 && age < 90) yaw = baseYaw - (age - 70) / 20F * 360;
		place(main, target.add(0, y, 0), yaw);
		if (main != null) main.setPhase(age < 35 ? ease((age - 20) / 15F) : 1);
		if ((age >= 35 && age < 55) || (age >= 70 && age < 90)) {
			double ang = Math.toRadians(yaw) + Math.PI;
			for (int k = 0; k < 5; k++) Fx.burst(level, ElementFx.dust(el, 1.8F), target.add(Math.cos(ang) * (8 + k * 2.5), 3, Math.sin(ang) * (8 + k * 2.5)), 2, 0.4, 0.02);
			if (age == 45 || age == 80) { boom(target, 16, 30); for (LivingEntity e : foes(12)) ElementEffects.strike(el, level, caster, e.position()); Fx.sound(level, target, SoundEvents.PLAYER_ATTACK_SWEEP, 1.6F, 0.4F); }
		}
		return age >= 128;
	}

	// ---------------- 서포터: 여신의 성역 ----------------
	private Vec3 stand;
	private boolean goddess() {
		if (age == 0) {
			stand = ground(level, caster.position().add(fwd.scale(-12)));
			main = spawn(GODDESS, stand.add(0, -46, 0));
			SpellManager.add(new MagicCircle(level, caster, el, caster.position().add(0, 0.1, 0), 13, 175, false));
		}
		float y = age < 30 ? -46 + 46 * ease(age / 30F) : age > 165 ? -46 * ease((age - 165) / 15F) : 0;
		place(main, stand.add(0, y, 0), yawOf(fwd));
		if (main != null) main.setPhase(age < 30 ? 0 : ease((age - 30) / 25F));
		if (age == 30) ElementEffects.supportAura(el, level, caster);
		if (age >= 40 && age <= 165 && age % 10 == 0) {
			Vec3 c = caster.position();
			for (Player p : level.getEntitiesOfClass(Player.class, caster.getBoundingBox().inflate(14))) Fx.supportHeal(caster, p, 0.05F);
			hurt(c, 14, 6);
			Fx.ring(level, ElementFx.particle(el), c.add(0, 0.3, 0), 14, 60, age * 0.05);
			if (age % 20 == 0) { var f = Fx.enemies(level, c, 14); if (!f.isEmpty()) ElementEffects.strike(el, level, caster, f.get(level.getRandom().nextInt(f.size())).position()); }
			for (int i = 0; i < 3; i++) { Vec3 p = randAround(c, 12); Fx.line(level, ElementFx.particle(el), p, p.add(0, 10, 0), 0.8); }
		}
		return age >= 180;
	}

	// ---------------- 마법사: 염제 / 뇌신 ----------------
	private boolean titan() {
		float yaw = yawOf(caster.position().subtract(target));
		Vec3 base = target.add(target.subtract(caster.position()).multiply(1, 0, 1).normalize().scale(12));
		if (age == 0) { main = spawn(TITAN, base.add(0, -40, 0)); SpellManager.add(new MagicCircle(level, caster, el, base.add(0, 0.1, 0), 10, 140, false)); }
		float y = age < 25 ? -40 + 40 * ease(age / 25F) : age > 130 ? -40 * ease((age - 130) / 10F) : 0;
		place(main, base.add(0, y, 0), yaw);
		if (main != null) main.setPhase(age < 112 ? 0.5F : Math.min(1, 0.5F + (age - 112) / 16F * 0.5F));
		int k = (age - 30) / 16;
		if (age >= 30 && age < 110 && (age - 30) % 16 == 0) { strike(main, k & 1); diveTo[k % 12] = foeOrRandom(14); }
		if (age >= 42 && age < 122 && (age - 42) % 16 == 0) {
			Vec3 p = diveTo[((age - 42) / 16) % 12];
			if (p != null) {
				if (el == Element.ELECTRIC) { for (int i = 0; i < 3; i++) ElectricStormSpell.bolt(level, caster, randAround(p, 2.5), 12F); }
				else { FireStormSpell.blast(level, caster, p); hurt(p, 4, 15); }
				Fx.sound(level, p, SoundEvents.GENERIC_EXPLODE.value(), 1.2F, 0.8F);
			}
		}
		if (age == 128) {
			for (int i = 0; i < 10; i++) {
				Vec3 p = randAround(target, 10);
				if (el == Element.ELECTRIC) ElectricStormSpell.bolt(level, caster, p, 14F); else FireStormSpell.blast(level, caster, p);
			}
			boom(target, 10, 40);
		}
		return age >= 140;
	}

	// ---------------- 마법사(물): 리바이어던 ----------------
	private boolean leviathan() {
		float yaw = yawOf(caster.position().subtract(target));
		if (age == 0) { main = spawn(LEVIATHAN, target.add(0, -45, 0)); SpellManager.add(new MagicCircle(level, caster, el, target.add(0, 0.1, 0), 10, 120, false)); }
		if (main == null) return true;
		if (age < 25) { place(main, target.add(0, -45 + 57 * ease(age / 25F), 0), yaw); main.setPitch(-70); if (age % 3 == 0) WaterOrbSpell.splash(level, caster, target, 0F, 2.5); }
		else if (age < 92) {
			place(main, target.add(Math.sin(age * 0.05) * 2, 12 + Math.sin(age * 0.1) * 1.2, 0), yaw); main.setPitch(-70 + (float) Math.sin(age * 0.08) * 8);
			if ((age - 25) % 20 == 0) {
				strike(main, 0);
				Fx.sound(level, main.position(), SoundEvents.ELDER_GUARDIAN_CURSE, 1.4F, 0.6F);
				for (LivingEntity e : foes(14)) WaterOrbSpell.splash(level, caster, e.position(), 12F, 3.5);
				for (int i = 0; i < 4; i++) WaterOrbSpell.splash(level, caster, randAround(target, 10), 6F, 3);
			}
		} else if (age < 108) {
			float f = (age - 92) / 16F;
			main.setPitch(-70 + 140 * ease(f));
			place(main, target.add(0, 12 + 12 * Mth.sin(f * Mth.PI) - 26 * f * f, 0), yaw);
		} else if (age == 108) {
			boom(target, 10, 40);
			for (int i = 0; i < 8; i++) { double a = i * Math.PI / 4; WaterOrbSpell.splash(level, caster, target.add(Math.cos(a) * 6, 0, Math.sin(a) * 6), 8F, 3.5); }
			main.discard(); vis.remove(main); main = null;
		}
		return age >= 112;
	}

	// ---------------- 마법사(풀): 세계수 ----------------
	private boolean tree() {
		if (age == 0) { main = spawn(TREE, target); SpellManager.add(new MagicCircle(level, caster, el, target.add(0, 0.1, 0), 12, 170, false)); }
		place(main, target, yawOf(fwd));
		if (main != null) main.setPhase(age < 40 ? ease(age / 40F) : age > 150 ? 1 - ease((age - 150) / 20F) : 1);
		if (age < 40 && age % 4 == 0) Fx.burst(level, ElementFx.dust(el, 1.4F), target.add(0, 0.5, 0), 20, 4, 0.1);
		if (age >= 40 && age <= 150 && age % 15 == 0) {
			for (LivingEntity e : foes(12)) {
				Fx.hurt(level, caster, e, 7);
				e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 3));
				Fx.line(level, ElementFx.particle(el), target.add(0, 0.3, 0), e.position().add(0, 0.3, 0), 0.6);
			}
			for (Player p : level.getEntitiesOfClass(Player.class, new net.minecraft.world.phys.AABB(target, target).inflate(12))) Fx.supportHeal(caster, p, 0.03F);
			var f = foes(12);
			if (!f.isEmpty()) ElementEffects.strike(el, level, caster, f.get(0).position());
		}
		if (age == 150) { boom(target, 12, 35); Fx.burst(level, ElementFx.dust(el, 2F), target.add(0, 12, 0), 120, 8, 0.1); }
		return age >= 170;
	}

	// ---------------- 마법사(바람): 폭풍의 대붕 ----------------
	private boolean roc() {
		if (age == 0) { main = spawn(ROC, target.add(20, 24, 0)); SpellManager.add(new MagicCircle(level, caster, el, target.add(0, 0.1, 0), 12, 140, false)); }
		double a = age * 0.09;
		Vec3 pos = target.add(Math.cos(a) * 20, 24 + Math.sin(age * 0.2) * 2, Math.sin(a) * 20);
		Vec3 vel = new Vec3(-Math.sin(a), 0, Math.cos(a));
		place(main, pos, yawOf(vel));
		if (main != null) main.setPitch(8);
		if (age >= 20 && age < 112 && (age - 20) % 18 == 0) {
			strike(main, 0);
			Vec3 p = foeOrRandom(14);
			Fx.line(level, ElementFx.particle(el), pos, p, 0.8);
			GaleSpell.gust(level, caster, p);
			hurt(p, 4, 12);
		}
		if (age >= 112 && age < 135) {
			for (LivingEntity e : foes(14)) {
				Vec3 d = target.subtract(e.position());
				e.setDeltaMovement(e.getDeltaMovement().add(d.normalize().scale(0.25)).add(0, 0.12, 0));
				e.needsSync = true;
				if (age % 5 == 0) Fx.hurt(level, caster, e, 4);
			}
			for (int i = 0; i < 6; i++) { double t2 = age * 0.5 + i; double rr = 2 + i * 1.4; Fx.at(level, ElementFx.dust(el, 1.5F), target.add(Math.cos(t2) * rr, i * 2.2, Math.sin(t2) * rr)); }
		}
		if (age == 135) boom(target, 10, 30);
		return age >= 140;
	}

	// ---------------- 마법사(어둠): 심연의 문 ----------------
	private boolean abyss() {
		float yaw = yawOf(caster.position().subtract(target));
		Vec3 pos = target.add(0, 14, 0);
		if (age == 0) { main = spawn(ABYSS, pos); SpellManager.add(new MagicCircle(level, caster, el, target.add(0, 0.1, 0), 10, 140, false)); Fx.sound(level, pos, SoundEvents.WITHER_SPAWN, 1F, 0.6F); }
		place(main, pos, yaw);
		if (main != null) main.setPhase(Math.min(1, age / 40F));
		int k = (age - 40) / 18;
		if (age >= 40 && age < 112 && (age - 40) % 18 == 0) { strike(main, k & 1); diveTo[k % 12] = foeOrRandom(12); }
		if (age >= 52 && age < 124 && (age - 52) % 18 == 0) {
			Vec3 p = diveTo[((age - 52) / 18) % 12];
			if (p != null) { DarkSwarmSpell.burst(level, caster, p); hurt(p, 4, 16); Fx.sound(level, p, SoundEvents.WARDEN_SONIC_BOOM, 0.8F, 0.6F); }
		}
		if (age >= 118 && age < 130) for (LivingEntity e : foes(12)) { Vec3 d = target.subtract(e.position()); e.setDeltaMovement(d.normalize().scale(0.4)); e.needsSync = true; }
		if (age == 130) { boom(target, 10, 40); for (LivingEntity e : foes(10)) e.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1)); }
		return age >= 140;
	}
}
