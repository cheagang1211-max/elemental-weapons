package com.elementalweapons.boss;

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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.item.Element;
import com.elementalweapons.spell.Fx;

/**
 * 설정(BossDef)으로 동작하는 범용 보스. 패턴 12종(PatternType)을 구현합니다.
 * 각 보스는 BossDefs 에서 이름/속성/체력/패턴/보상을 정합니다.
 */
public class ConfiguredBoss extends ElementalBoss implements PosedEntity {
	private static final Element[] SHIFT_ORDER = {
			Element.ELECTRIC, Element.FIRE, Element.WATER, Element.GRASS, Element.WIND, Element.DARK, Element.LIGHT};

	private BossDef def;
	private List<BossPattern> patternList;

	// ----- 패턴 상태 -----
	private record Strike(Vec3 pos, int fireAt, double radius, float damage, Element element) {}
	private static final class Proj {
		Vec3 pos, vel; int life; final Element element; final float damage; final double radius;
		final LivingEntity homing; final boolean meteor; final Vec3 landAt;
		Proj(Vec3 pos, Vec3 vel, int life, Element e, float dmg, double r, LivingEntity homing, boolean meteor, Vec3 landAt) {
			this.pos = pos; this.vel = vel; this.life = life; this.element = e; this.damage = dmg; this.radius = r;
			this.homing = homing; this.meteor = meteor; this.landAt = landAt;
		}
	}
	private final List<Strike> strikes = new ArrayList<>();
	private final List<Proj> projs = new ArrayList<>();
	private final List<Entity> minions = new ArrayList<>();
	private final List<com.elementalweapons.spell.GrandVisual> meteorVis = new ArrayList<>(), beamVis = new ArrayList<>();
	private Vec3 dashDir = Vec3.ZERO;
	private final Set<UUID> hitOnce = new HashSet<>();
	private final Map<UUID, Integer> lastHit = new HashMap<>();
	private Vec3 anchor = Vec3.ZERO;
	private final List<Player> marked = new ArrayList<>();
	private final List<Vec3> markedPos = new ArrayList<>();
	private double beamAngle = 0;
	private int lastStage = -1;

	public ConfiguredBoss(EntityType<? extends ConfiguredBoss> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 1000.0)
				.add(Attributes.MOVEMENT_SPEED, 0.3)
				.add(Attributes.FLYING_SPEED, 0.6)
				.add(Attributes.FOLLOW_RANGE, 64.0)
				.add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
				.add(Attributes.ARMOR, 12.0)
				.add(Attributes.ATTACK_DAMAGE, 10.0);
	}

	public BossDef def() {
		if (def == null) def = BossDefs.BY_TYPE.get(this.getType());
		return def;
	}

	// ======================= 설정 연결 =======================
	@Override
	public Element element() {
		if (def().shifting) return SHIFT_ORDER[stage()];
		return def().primary;
	}

	@Override
	public Element secondaryElement() {
		return def().secondary;
	}

	@Override public long maxBigHealth() { return def().health; }
	@Override protected BossEvent.BossBarColor barColor() { return def().barColor; }
	@Override protected double hoverDistance() { return def().hoverDistance; }
	@Override protected double hoverHeight() { return def().hoverHeight; }
	@Override protected double moveSpeed() { return def().speed + getPhase() * 0.04; }

	@Override
	public Component bossName() {
		return Component.literal(def().icon + " " + def().ko).withStyle(def().nameColor, ChatFormatting.BOLD);
	}

	public boolean isFloatingBoss() {
		return def().floating;
	}

	@Override
	protected List<BossPattern> patterns() {
		if (patternList == null) {
			patternList = new ArrayList<>();
			for (PatternSpec spec : def().patterns) {
				patternList.add(new BossPattern(spec.name(), spec.type().duration, spec.minPhase(), spec.weight(),
						(level, target, t) -> run(spec, level, target, t)));
			}
		}
		return patternList;
	}

	/** 최종 보스: 체력 1/7마다 단계(속성)가 바뀜 */
	private int stage() {
		double ratio = (double) getBigHealth() / maxBigHealth();
		return Math.max(0, Math.min(6, (int) ((1.0 - ratio) * 7)));
	}

	private Element el(PatternSpec spec) {
		return spec.element() != null ? spec.element() : element();
	}

	// ======================= 매 틱 =======================
	@Override
	public void tick() {
		super.tick();
		if (!(this.level() instanceof ServerLevel level)) return;
		tickStrikes(level);
		tickProjs(level);
		if (this.tickCount % 3 == 0) {
			Fx.burst(level, ElementFx.particle(element()), this.position().add(0, getBbHeight() * 0.5, 0), 2, getBbWidth() * 0.5, 0.02);
			if (secondaryElement() != null) {
				Fx.burst(level, ElementFx.particle(secondaryElement()), this.position().add(0, getBbHeight() * 0.5, 0), 2, getBbWidth() * 0.5, 0.02);
			}
		}
		// 3페이즈: 주기적으로 플레이어 발밑에 착탄
		if (getPhase() >= 3 && this.tickCount % 60 == 0 && this.getTarget() != null) {
			for (Player p : BossFx.players(level, this.position(), 40)) {
				schedule(BossFx.ground(level, p.getX() + rnd(3), p.getZ() + rnd(3)), 25, 2.5, 7.0F, element());
			}
		}
		// 최종 보스: 속성 변화
		if (def().shifting) {
			int st = stage();
			if (lastStage >= 0 && st != lastStage) announceShift(level, SHIFT_ORDER[st]);
			lastStage = st;
		}
	}

	private void announceShift(ServerLevel level, Element e) {
		Component msg = bossName().copy().append(Component.literal(" 의 속성이 [" + e.koName + "](으)로 변했다!").withStyle(ChatFormatting.WHITE));
		for (Player p : BossFx.players(level, this.position(), 80)) p.sendSystemMessage(msg);
		this.bossBar.setColor(switch (e) {
			case ELECTRIC, WATER -> BossEvent.BossBarColor.BLUE;
			case FIRE -> BossEvent.BossBarColor.RED;
			case GRASS -> BossEvent.BossBarColor.GREEN;
			case WIND -> BossEvent.BossBarColor.WHITE;
			case DARK -> BossEvent.BossBarColor.PURPLE;
			case LIGHT -> BossEvent.BossBarColor.YELLOW;
		});
		ElementFx.impact(level, e, BossFx.ground(level, getX(), getZ()), 6);
		Fx.sphere(level, ElementFx.dust(e, 2.0F), this.position().add(0, getBbHeight() * 0.5, 0), 4, 120);
	}

	private void schedule(Vec3 pos, int delay, double r, float dmg, Element e) {
		strikes.add(new Strike(pos, this.tickCount + delay, r, dmg, e));
	}

	private void tickStrikes(ServerLevel level) {
		strikes.removeIf(s -> {
			int left = s.fireAt() - this.tickCount;
			if (left > 0) {
				BossFx.warnRing(level, s.pos(), s.radius() * (0.4 + 0.6 * Math.min(1.0, left / 20.0)),
						left < 8 ? BossFx.WARN_RED : BossFx.WARN_YELLOW, this.tickCount * 0.2);
				return false;
			}
			ElementFx.impact(level, s.element(), s.pos(), s.radius());
			hitPlayers(level, s.pos(), s.radius(), s.damage(), s.element());
			return true;
		});
	}

	private void tickProjs(ServerLevel level) {
		projs.removeIf(p -> {
			if (p.homing != null && p.homing.isAlive() && !p.meteor) {
				Vec3 want = p.homing.position().add(0, 1, 0).subtract(p.pos);
				if (want.lengthSqr() > 1.0E-3) p.vel = p.vel.add(want.normalize().scale(0.035)).normalize().scale(p.vel.length());
			}
			p.pos = p.pos.add(p.vel);
			p.life--;
			ElementFx.orb(level, p.element, p.pos, p.meteor ? 1.6 : 0.45);
			boolean hitPlayer = !BossFx.players(level, p.pos, p.meteor ? 0 : 1.3).isEmpty();
			boolean hitGround = !level.getBlockState(BlockPos.containing(p.pos)).isAir();
			boolean landed = p.meteor && p.landAt != null && p.pos.y <= p.landAt.y + 0.5;
			if (hitPlayer || hitGround || landed || p.life <= 0) {
				Vec3 at = landed ? p.landAt : p.pos;
				ElementFx.impact(level, p.element, at, p.radius);
				hitPlayers(level, at, p.radius, p.damage, p.element);
				return true;
			}
			return false;
		});
	}

	private void hitPlayers(ServerLevel level, Vec3 c, double r, float dmg, Element e) {
		for (Player p : BossFx.players(level, c, r)) hurt(level, p, dmg, e);
	}

	private void hurt(ServerLevel level, Player p, float dmg, Element e) {
		p.hurtServer(level, level.damageSources().mobAttack(this), dmg);
		ElementFx.onHit(e, p);
	}

	/** 같은 플레이어를 cooldown 틱 안에 다시 때리지 않음 */
	private boolean canHit(Player p, int cooldown) {
		Integer last = lastHit.get(p.getUUID());
		if (last != null && this.tickCount - last < cooldown) return false;
		lastHit.put(p.getUUID(), this.tickCount);
		return true;
	}

	private double rnd(double r) {
		return (this.getRandom().nextDouble() * 2 - 1) * r;
	}

	private Vec3 center() {
		return this.position().add(0, getBbHeight() * 0.55, 0);
	}

	@Override
	protected void onPhaseChange(ServerLevel level, int newPhase) {
		String line = newPhase == 2 ? def().phase2 : def().phase3;
		if (!line.isEmpty()) {
			Component msg = Component.literal(def().icon + " " + def().ko + ": ").withStyle(def().nameColor, ChatFormatting.BOLD)
					.append(Component.literal(line).withStyle(ChatFormatting.WHITE));
			for (Player p : BossFx.players(level, this.position(), 64)) p.sendSystemMessage(msg);
		}
		Fx.sound(level, this.position(), SoundEvents.ENDER_DRAGON_GROWL, 3.0F, newPhase == 2 ? 1.2F : 0.8F);
		Fx.sphere(level, ElementFx.dust(element(), 2.0F), center(), 4.5, 150);
		if (newPhase == 3 && !def().shifting) this.bossBar.setColor(BossEvent.BossBarColor.RED);
		for (int i = 0; i < 6; i++) {
			double a = i * Math.PI / 3;
			schedule(BossFx.ground(level, getX() + Math.cos(a) * 7, getZ() + Math.sin(a) * 7), 20 + i * 3, 2.8, 8.0F, element());
		}
	}

	/** 촬영·시험용: 이 종류의 패턴을 t 틱째로 한 번 진행 */
	public void debugRun(PatternType type, ServerLevel level, int t) {
		for (PatternSpec sp : def().patterns) if (sp.type() == type) { run(sp, level, level.getNearestPlayer(this, 40), t); return; }
	}

	// ======================= 패턴 =======================
	private void run(PatternSpec s, ServerLevel level, LivingEntity target, int t) {
		Element e = el(s);
		switch (s.type()) {
			case RAIN -> rain(s, e, level, t);
			case DASH -> dash(s, e, level, target, t);
			case NOVA -> nova(s, e, level, t);
			case MARK -> mark(s, e, level, t);
			case BARRAGE -> barrage(s, e, level, t);
			case SLAM -> slam(s, e, level, t);
			case VORTEX -> vortex(s, e, level, t);
			case SUMMON -> summon(s, e, level, target, t);
			case BEAM -> beam(s, e, level, t);
			case ORBIT -> orbit(s, e, level, t);
			case METEOR -> meteor(s, e, level, t);
			case BLINK -> blink(s, e, level, target, t);
		}
	}

	private void rain(PatternSpec s, Element e, ServerLevel level, int t) {
		setAttackPose(1);
		hold();
		Fx.burst(level, ElementFx.dust(e, 1.4F), center().add(0, getBbHeight() * 0.6, 0), 4, 0.8, 0);
		int interval = getPhase() >= 2 ? 6 : 9;
		if (t < 80 && t % interval == 0) {
			double spread = getPhase() >= 2 ? 0.6 : 3.0;
			for (Player p : BossFx.players(level, this.position(), 32)) {
				schedule(BossFx.ground(level, p.getX() + rnd(spread), p.getZ() + rnd(spread)), 20, s.radius(), s.damage(), e);
			}
			for (int i = 0; i < s.count(); i++) {
				schedule(BossFx.ground(level, getX() + rnd(12), getZ() + rnd(12)), 20, s.radius(), s.damage(), e);
			}
		}
	}

	private void dash(PatternSpec s, Element e, ServerLevel level, LivingEntity target, int t) {
		int dashes = s.count() + (getPhase() >= 3 ? 1 : 0);
		int cycle = 35, k = t % cycle;
		if (t / cycle >= dashes) { hold(); setAttackPose(0); return; }
		if (k < 18) {
			setAttackPose(1);
			hold();
			Fx.sphere(level, ElementFx.dust(e, 1.4F), center(), 3.0 - k * 0.12, 10);
			if (k == 17) {
				Vec3 aim = target.position().add(0, 1, 0).subtract(center());
				dashDir = aim.lengthSqr() < 1.0E-3 ? Vec3.ZERO : aim.normalize();
				hitOnce.clear();
				Fx.line(level, BossFx.WARN_RED, center(), center().add(dashDir.scale(22)), 0.6);
				Fx.sound(level, this.position(), SoundEvents.ENDER_DRAGON_FLAP, 2.0F, 1.2F);
			}
		} else if (k < 30) {
			setAttackPose(2);
			this.setDeltaMovement(dashDir.scale(1.7));
			ElementFx.orb(level, e, center(), 1.2);
			for (Player p : BossFx.players(level, center(), s.radius())) {
				if (hitOnce.add(p.getUUID())) hurt(level, p, s.damage(), e);
			}
			if (k == 29) {
				Vec3 g = BossFx.ground(level, getX(), getZ());
				ElementFx.impact(level, e, g, 3);
				hitPlayers(level, g, 3, s.damage() * 0.5F, e);
			}
		} else {
			setAttackPose(0);
			hold();
		}
	}

	private void nova(PatternSpec s, Element e, ServerLevel level, int t) {
		double inner = s.radius(), outer = s.radius() + 6;
		if (t == 0) {
			anchor = BossFx.ground(level, getX(), getZ());
			Fx.sound(level, this.position(), SoundEvents.BEACON_ACTIVATE, 2.5F, 0.7F);
		}
		hold();
		setAttackPose(3);
		if (t < 40) {
			BossFx.warnRing(level, anchor, inner, t > 28 ? BossFx.WARN_RED : BossFx.WARN_YELLOW, t * 0.1);
			Fx.sphere(level, ElementFx.dust(e, 1.3F), center(), 1.0 + t * 0.05, 8);
		}
		if (t == 40) shock(level, e, anchor, 0, inner, s.damage());
		if (getPhase() >= 2) {
			if (t > 40 && t < 62) {
				BossFx.warnRing(level, anchor, inner, BossFx.WARN_YELLOW, -t * 0.1);
				BossFx.warnRing(level, anchor, outer, t > 52 ? BossFx.WARN_RED : BossFx.WARN_YELLOW, t * 0.1);
			}
			if (t == 62) shock(level, e, anchor, inner, outer, s.damage());
		}
	}

	private void shock(ServerLevel level, Element e, Vec3 c, double from, double to, float dmg) {
		for (double r = Math.max(1, from); r <= to; r += 1.0) {
			Fx.ring(level, ElementFx.particle(e), c.add(0, 0.3, 0), r, (int) (r * 5), r);
			Fx.ring(level, ElementFx.dust(e, 1.6F), c.add(0, 0.2, 0), r, (int) (r * 4), -r);
		}
		Fx.sound(level, c, SoundEvents.GENERIC_EXPLODE, 2.0F, 0.8F);
		for (Player p : BossFx.players(level, c, to + 2)) {
			double d = Math.hypot(p.getX() - c.x, p.getZ() - c.z);
			if (d >= from && d <= to && p.getY() - c.y < 3.0) hurt(level, p, dmg, e);
		}
	}

	private void mark(PatternSpec s, Element e, ServerLevel level, int t) {
		setAttackPose(1);
		hold();
		if (t == 0) {
			marked.clear();
			markedPos.clear();
			marked.addAll(BossFx.players(level, this.position(), 48));
			Fx.sound(level, this.position(), SoundEvents.ELDER_GUARDIAN_CURSE, 1.5F, 1.2F);
			for (Player p : marked) {
				p.sendOverlayMessage(Component.literal("⚠ " + s.name() + "! 움직여라!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
			}
		}
		Fx.sphere(level, ElementFx.dust(e, 1.8F), center().add(0, getBbHeight() * 0.7, 0), Math.min(3.0, t * 0.06), 20);
		if (t < 55) {
			for (Player p : marked) {
				if (p.isAlive()) BossFx.warnRing(level, BossFx.ground(level, p.getX(), p.getZ()), s.radius() - t * 0.02, BossFx.WARN_YELLOW, t * 0.3);
			}
		} else if (t == 55) {
			for (Player p : marked) markedPos.add(BossFx.ground(level, p.getX(), p.getZ()));
		} else if (t < 72) {
			for (Vec3 g : markedPos) BossFx.warnRing(level, g, s.radius(), BossFx.WARN_RED, t * 0.3);
		} else if (t == 72) {
			for (Vec3 g : markedPos) {
				ElementFx.impact(level, e, g, s.radius());
				ElementFx.impact(level, e, g.add(rnd(1), 0, rnd(1)), s.radius() * 0.6);
				hitPlayers(level, g, s.radius(), s.damage(), e);
				for (int i = 0; i < 3; i++) {
					schedule(BossFx.ground(level, g.x + rnd(6), g.z + rnd(6)), 10 + i * 4, 2.0, s.damage() * 0.45F, e);
				}
			}
		}
	}

	private void barrage(PatternSpec s, Element e, ServerLevel level, int t) {
		setAttackPose(1);
		hold();
		int interval = Math.max(3, 9 - getPhase() * 2);
		if (t < 60 && t % interval == 0) {
			List<Player> ps = BossFx.players(level, this.position(), 36);
			for (int i = 0; i < Math.min(4, ps.size()); i++) {
				Player p = ps.get(i);
				Vec3 dir = p.position().add(0, 1, 0).subtract(center()).normalize();
				for (int k = 0; k < s.count(); k++) {
					double spread = (k - (s.count() - 1) / 2.0) * 0.18;
					Vec3 d = new Vec3(dir.x * Math.cos(spread) - dir.z * Math.sin(spread), dir.y, dir.x * Math.sin(spread) + dir.z * Math.cos(spread));
					projs.add(new Proj(center(), d.scale(0.7 + getPhase() * 0.1), 70, e, s.damage(), s.radius(), p, false, null));
				}
			}
			Fx.sound(level, this.position(), SoundEvents.BLAZE_SHOOT, 1.2F, 1.3F);
		}
	}

	private void slam(PatternSpec s, Element e, ServerLevel level, int t) {
		if (t == 0) {
			anchor = BossFx.ground(level, getX(), getZ());
			hitOnce.clear();
		}
		if (t < 15) {
			setAttackPose(1);
			this.setDeltaMovement(0, 0.45, 0);
			BossFx.warnRing(level, anchor, 3 + t * 0.3, BossFx.WARN_YELLOW, t * 0.2);
		} else if (t < 25) {
			setAttackPose(2);
			this.setDeltaMovement(0, -1.6, 0);
		} else {
			setAttackPose(0);
			hold();
			int k = t - 25;
			if (k == 0) {
				ElementFx.impact(level, e, anchor, 5);
				Fx.sound(level, anchor, SoundEvents.GENERIC_EXPLODE, 3.0F, 0.6F);
			}
			double[] rings = getPhase() >= 2 ? new double[] {k * 0.75, (k - 10) * 0.75} : new double[] {k * 0.75};
			for (double rr : rings) {
				if (rr <= 0.5 || rr > s.radius()) continue;
				Fx.ring(level, ElementFx.dust(e, 1.8F), anchor.add(0, 0.3, 0), rr, (int) (rr * 6), k * 0.1);
				Fx.ring(level, ElementFx.particle(e), anchor.add(0, 0.4, 0), rr, (int) (rr * 3), 0);
				for (Player p : BossFx.players(level, anchor, rr + 1.5)) {
					double d = Math.hypot(p.getX() - anchor.x, p.getZ() - anchor.z);
					if (Math.abs(d - rr) < 0.9 && p.onGround() && canHit(p, 8)) hurt(level, p, s.damage(), e);
				}
			}
		}
	}

	private void vortex(PatternSpec s, Element e, ServerLevel level, int t) {
		setAttackPose(3);
		hold();
		if (t == 0) {
			anchor = this.position();
			Fx.sound(level, anchor, SoundEvents.ELYTRA_FLYING, 2.0F, 0.6F);
		}
		double pullRange = s.radius() * 2.6;
		if (t < 60) {
			for (int k = 0; k < 4; k++) {
				double a = t * 0.3 + k * Math.PI / 2;
				double r = pullRange * (1 - (t % 20) / 20.0);
				Fx.at(level, ElementFx.dust(e, 1.5F), anchor.add(Math.cos(a) * r, 0.5 + k * 0.4, Math.sin(a) * r));
			}
			if (t % 6 == 0) {
				for (Player p : BossFx.players(level, anchor, pullRange)) {
					Vec3 d = anchor.subtract(p.position());
					if (d.lengthSqr() < 1.0) continue;
					p.setInvulnerableTime(0);
					p.hurtServer(level, level.damageSources().mobAttack(this), 0.5F); // 움직임 동기화용 약한 피해
					Vec3 v = d.normalize().scale(0.55);
					p.setDeltaMovement(v.x, 0.15, v.z); p.syncVelocity = true;
				}
			}
			if (t >= 42) BossFx.warnRing(level, BossFx.ground(level, anchor.x, anchor.z), s.radius(), BossFx.WARN_RED, t * 0.2);
		}
		if (t == 60) {
			ElementFx.impact(level, e, BossFx.ground(level, anchor.x, anchor.z), s.radius());
			Fx.sphere(level, ElementFx.dust(e, 2.0F), anchor, s.radius(), 100);
			hitPlayers(level, anchor, s.radius(), s.damage(), e);
		}
	}

	private void summon(PatternSpec s, Element e, ServerLevel level, LivingEntity target, int t) {
		setAttackPose(1);
		hold();
		if (t < 20) Fx.burst(level, ElementFx.dust(e, 1.6F), center(), 6, 2.0, 0.05);
		if (t == 20) {
			minions.removeIf(m -> !m.isAlive());
			int n = Math.min(s.count() + getPhase() - 1, 8 - minions.size());
			// 바닐라 몹 대신 이 세계의 속성 몹
			EntityType<?> type = com.elementalweapons.mob.ModMobs.MOBS.get(BossFight.mobFor(e));
			if (type == null) return;
			for (int i = 0; i < n; i++) {
				Entity m = type.create(level, EntitySpawnReason.MOB_SUMMONED);
				if (m == null) continue;
				Vec3 g = BossFx.ground(level, getX() + rnd(5), getZ() + rnd(5));
				m.snapTo(g.x, g.y + 0.2, g.z);
				if (m instanceof Mob mob) mob.setTarget(target);
				level.addFreshEntity(m);
				minions.add(m);
				ElementFx.impact(level, e, g, 1.5);
			}
		}
	}

	private void beam(PatternSpec s, Element e, ServerLevel level, int t) {
		setAttackPose(3);
		if (t == 0) {
			anchor = BossFx.ground(level, getX(), getZ());
			beamAngle = this.getRandom().nextDouble() * Math.PI * 2;
			lastHit.clear();
		}
		moveTowards(anchor.add(0, 1.0, 0), 0.25);
		int n = Math.max(1, s.count());
		double y = anchor.y + 1.2;
		if (t < 25) {
			if (t % 3 == 0) {
				for (int i = 0; i < n; i++) {
					double a = beamAngle + i * Math.PI * 2 / n;
					Fx.line(level, BossFx.WARN_YELLOW, new Vec3(getX(), y, getZ()),
							new Vec3(getX() + Math.cos(a) * s.radius(), y, getZ() + Math.sin(a) * s.radius()), 0.8);
				}
			}
			return;
		}
		if (t >= 95) { for (var v : beamVis) if (v.isAlive()) v.discard(); beamVis.clear(); return; }
		if (t == 25) {
			for (var v : beamVis) if (v.isAlive()) v.discard();
			beamVis.clear();
			for (int i = 0; i < n; i++) {
				var v = com.elementalweapons.mob.ModMobs.GRAND.create(level, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
				if (v == null) continue;
				v.addTag(com.elementalweapons.world.VanillaPurge.ALLOWED);
				v.setKind(20); v.setElem(e.ordinal()); v.setPhase((float) (s.radius() / 3.0));
				v.snapTo(getX(), y, getZ(), 0, 0);
				level.addFreshEntity(v);
				beamVis.add(v);
			}
		}
		beamAngle += 0.035 + getPhase() * 0.012;
		for (int i = 0; i < n; i++) {
			double a = beamAngle + i * Math.PI * 2 / n;
			Vec3 from = new Vec3(getX(), y, getZ());
			Vec3 to = new Vec3(getX() + Math.cos(a) * s.radius(), y, getZ() + Math.sin(a) * s.radius());
			if (i < beamVis.size()) {
				var v = beamVis.get(i);
				float yaw = (float) Math.toDegrees(Math.atan2(-Math.cos(a), Math.sin(a)));
				v.setPos(from.x, from.y, from.z); v.setYRot(yaw); v.setYBodyRot(yaw); v.setYHeadRot(yaw); v.yBodyRotO = yaw; v.life = 0;
			}
			if (t % 2 == 0) {
				Fx.line(level, ElementFx.dust(e, 2.2F), from, to, 0.45);
				Fx.line(level, ElementFx.particle(e), from, to, 1.5);
			}
			for (Player p : BossFx.players(level, from, s.radius())) {
				double dx = p.getX() - from.x, dz = p.getZ() - from.z;
				double along = dx * Math.cos(a) + dz * Math.sin(a);
				double perp = Math.abs(-dx * Math.sin(a) + dz * Math.cos(a));
				if (along > 0 && perp < 1.1 && Math.abs(p.getY() + 0.9 - y) < 1.8 && canHit(p, 10)) hurt(level, p, s.damage(), e);
			}
		}
		if (t % 10 == 0) Fx.sound(level, this.position(), SoundEvents.BEACON_AMBIENT, 2.0F, 1.6F);
	}

	private void orbit(PatternSpec s, Element e, ServerLevel level, int t) {
		setAttackPose(3);
		hold();
		if (t == 0) { anchor = center(); lastHit.clear(); }
		if (t >= 85) return;
		int layers = getPhase() >= 2 ? 2 : 1;
		double r = 2 + t * 0.18;
		for (int layer = 0; layer < layers; layer++) {
			int dir = layer == 0 ? 1 : -1;
			for (int i = 0; i < s.count(); i++) {
				double a = dir * t * 0.08 + i * Math.PI * 2 / s.count() + layer * 0.4;
				Vec3 pos = anchor.add(Math.cos(a) * r, -1.5 + Math.sin(t * 0.15 + i) * 0.8, Math.sin(a) * r);
				ElementFx.orb(level, e, pos, 0.5);
				for (Player p : BossFx.players(level, pos, 1.5)) {
					if (canHit(p, 10)) hurt(level, p, s.damage(), e);
				}
			}
		}
	}

	private void meteor(PatternSpec s, Element e, ServerLevel level, int t) {
		setAttackPose(1);
		hold();
		if (t == 0) {
			markedPos.clear();
			List<Player> ps = BossFx.players(level, this.position(), 40);
			for (int i = 0; i < Math.min(4, ps.size()); i++) {
				Player p = ps.get(i);
				markedPos.add(BossFx.ground(level, p.getX(), p.getZ()));
			}
			if (markedPos.isEmpty()) markedPos.add(BossFx.ground(level, getX() + rnd(8), getZ() + rnd(8)));
			Fx.sound(level, this.position(), SoundEvents.WITHER_SPAWN, 1.0F, 1.6F);
		}
		if (t == 0) {
			for (var v : meteorVis) if (v.isAlive()) v.discard();
			meteorVis.clear();
			for (Vec3 g : markedPos) {
				var v = com.elementalweapons.mob.ModMobs.GRAND.create(level, net.minecraft.world.entity.EntitySpawnReason.MOB_SUMMONED);
				if (v == null) continue;
				v.addTag(com.elementalweapons.world.VanillaPurge.ALLOWED);
				v.setKind(19); v.setElem(e.ordinal());
				v.snapTo(g.x, g.y + 31, g.z, 0, 0);
				level.addFreshEntity(v);
				meteorVis.add(v);
			}
		}
		if (t < 50) {
			int i = 0;
			for (Vec3 g : markedPos) {
				BossFx.warnRing(level, g, s.radius(), t > 35 ? BossFx.WARN_RED : BossFx.WARN_YELLOW, t * 0.1);
				BossFx.warnRing(level, g, s.radius() * 0.5, BossFx.WARN_YELLOW, -t * 0.1);
				double f = t / 50.0, h = 30 * (1 - f * f) + 1;
				ElementFx.orb(level, e, g.add(0, h, 0), 1.8);
				if (i < meteorVis.size()) { var v = meteorVis.get(i); v.setPos(g.x, g.y + h - 1.2, g.z); v.life = 0; }
				i++;
			}
		} else if (t == 50) {
			for (var v : meteorVis) if (v.isAlive()) v.discard();
			meteorVis.clear();
			for (Vec3 g : markedPos) {
				ElementFx.impact(level, e, g, s.radius());
				ElementFx.impact(level, e, g.add(rnd(2), 0, rnd(2)), s.radius() * 0.7);
				Fx.sphere(level, ElementFx.dust(e, 2.0F), g.add(0, 1, 0), s.radius() * 0.8, 120);
				Fx.sound(level, g, SoundEvents.GENERIC_EXPLODE, 3.0F, 0.6F);
				hitPlayers(level, g, s.radius(), s.damage(), e);
			}
		}
	}

	private void blink(PatternSpec s, Element e, ServerLevel level, LivingEntity target, int t) {
		int times = getPhase() >= 3 ? 2 : 1;
		int cycle = 20, k = t % cycle;
		if (t / cycle >= times) { hold(); setAttackPose(0); return; }
		hold();
		if (k == 0) {
			ElementFx.impact(level, e, BossFx.ground(level, getX(), getZ()), 2);
			Vec3 look = target.getLookAngle();
			Vec3 flat = new Vec3(look.x, 0, look.z);
			flat = flat.lengthSqr() < 1.0E-3 ? new Vec3(1, 0, 0) : flat.normalize();
			Vec3 to = target.position().subtract(flat.scale(3.5));
			Vec3 g = BossFx.ground(level, to.x, to.z);
			this.teleportTo(g.x, g.y + 0.5, g.z);
			anchor = g;
			Fx.sound(level, g, SoundEvents.ENDERMAN_TELEPORT, 2.0F, 0.6F);
			setAttackPose(3);
		}
		if (k > 0 && k < 12) BossFx.warnRing(level, anchor, s.radius(), k > 6 ? BossFx.WARN_RED : BossFx.WARN_YELLOW, k * 0.3);
		if (k == 12) {
			ElementFx.impact(level, e, anchor, s.radius());
			hitPlayers(level, anchor, s.radius(), s.damage(), e);
		}
	}

	// ======================= 보상 =======================
	@Override
	protected void dropRewards(ServerLevel level) {
		for (Entity m : minions) {
			if (m.isAlive()) m.discard();
		}
		for (BossDef.Drop d : def().drops) {
			int n = d.min() + (d.max() > d.min() ? this.getRandom().nextInt(d.max() - d.min() + 1) : 0);
			if (n > 0) this.spawnAtLocation(level, new ItemStack(d.item().get(), n));
		}
		ElementFx.impact(level, element(), BossFx.ground(level, getX(), getZ()), 6);
		Fx.sphere(level, ElementFx.dust(element(), 2.0F), center(), 5, 200);
	}
}
