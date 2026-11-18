package com.elementalweapons.boss;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.item.Element;
import com.elementalweapons.item.ModItems;
import com.elementalweapons.spell.ElectricStormSpell;
import com.elementalweapons.spell.Fx;

/**
 * ⚡ 뇌신 볼트라 — 전기 속성 수호자 (체력 10억)
 *
 * 패턴
 *  1. 천둥 강림   : 플레이어 발밑에 경고 원 → 1초 뒤 벼락 (계속 움직여야 함)
 *  2. 뇌전 돌진   : 전기를 모은 뒤 플레이어 쪽으로 일직선 돌진 (3페이즈: 2연속)
 *  3. 방전 고리   : 주변에 큰 원 → 원 안에 있으면 감전 (2페이즈부터 바깥 고리 추가: 안쪽으로 피해야 함)
 *  4. 뇌신의 심판 : (3페이즈) 모든 플레이어를 표식으로 추적 → 고정 → 거대한 벼락
 * 약점: 풀 속성 (2배) / 전기 속성 공격은 절반
 */
public class VoltraBoss extends ElementalBoss {
	public static final long MAX_HEALTH = 1_000_000_000L;

	private static final DustParticleOptions SPARK = Fx.dust(0x9FF3FF, 1.4F);
	private static final DustParticleOptions CORE = Fx.dust(0xF2FBFF, 1.8F);

	private record Strike(Vec3 pos, int fireAt, float radius, float damage) {}
	private final List<Strike> strikes = new ArrayList<>();
	private Vec3 dashDir = Vec3.ZERO;
	private final Set<UUID> dashHit = new HashSet<>();
	private Vec3 novaCenter = Vec3.ZERO;
	private final List<Player> marked = new ArrayList<>();
	private final List<Vec3> markedPos = new ArrayList<>();

	private final List<BossPattern> patternList = List.of(
			new BossPattern("천둥 강림", 110, 1, 3, this::thunderRain),
			new BossPattern("뇌전 돌진", 70, 1, 2, this::lightningDash),
			new BossPattern("방전 고리", 75, 1, 2, this::dischargeNova),
			new BossPattern("뇌신의 심판", 95, 3, 3, this::judgment)
	);

	public VoltraBoss(EntityType<? extends VoltraBoss> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 1000.0)
				.add(Attributes.MOVEMENT_SPEED, 0.3)
				.add(Attributes.FLYING_SPEED, 0.6)
				.add(Attributes.FOLLOW_RANGE, 64.0)
				.add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
				.add(Attributes.ARMOR, 10.0)
				.add(Attributes.ATTACK_DAMAGE, 10.0);
	}

	@Override public Element element() { return Element.ELECTRIC; }
	@Override public long maxBigHealth() { return MAX_HEALTH; }
	@Override protected BossEvent.BossBarColor barColor() { return BossEvent.BossBarColor.BLUE; }
	@Override protected List<BossPattern> patterns() { return patternList; }

	@Override
	public Component bossName() {
		return Component.literal("⚡ 뇌신 볼트라").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD);
	}

	// ======================= 공통 =======================

	@Override
	public void tick() {
		super.tick();
		if (this.level() instanceof ServerLevel level) {
			tickStrikes(level);
			// 몸 주변에 항상 흐르는 전기
			if (this.tickCount % 3 == 0) {
				Fx.burst(level, ParticleTypes.ELECTRIC_SPARK, this.position().add(0, 2.5, 0), 3, 1.2, 0.05);
			}
			// 3페이즈: 무작위 낙뢰 (경고 후)
			if (getPhase() >= 3 && this.tickCount % 50 == 0 && this.getTarget() != null) {
				for (Player p : BossFx.players(level, this.position(), 40)) {
					Vec3 g = BossFx.ground(level, p.getX() + rnd(4), p.getZ() + rnd(4));
					strikes.add(new Strike(g, this.tickCount + 25, 2.5F, 7.0F));
				}
			}
		}
	}

	/** 예약된 낙뢰: 경고 원을 그리다가 시간이 되면 벼락 */
	private void tickStrikes(ServerLevel level) {
		strikes.removeIf(s -> {
			int left = s.fireAt() - this.tickCount;
			if (left > 0) {
				BossFx.warnRing(level, s.pos(), s.radius() * (0.4 + 0.6 * Math.min(1.0, left / 20.0)),
						left < 8 ? BossFx.WARN_RED : BossFx.WARN_YELLOW, this.tickCount * 0.2);
				if (left % 4 == 0) Fx.burst(level, ParticleTypes.ELECTRIC_SPARK, s.pos().add(0, 0.3, 0), 3, s.radius() * 0.5, 0.02);
				return false;
			}
			ElectricStormSpell.drawBolt(level, s.pos());
			BossFx.hurtPlayers(level, this, s.pos(), s.radius(), s.damage());
			return true;
		});
	}

	private double rnd(double r) {
		return (this.getRandom().nextDouble() * 2 - 1) * r;
	}

	@Override
	protected void onPhaseChange(ServerLevel level, int newPhase) {
		String line = newPhase == 2 ? "하늘이 분노한다! 번개의 속도를 보여주마!" : "심판의 시간이다. 그 누구도 피할 수 없다!";
		Component msg = Component.literal("⚡ 볼트라: ").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD)
				.append(Component.literal(line).withStyle(ChatFormatting.WHITE));
		for (Player p : BossFx.players(level, this.position(), 64)) p.sendSystemMessage(msg);
		Fx.sound(level, this.position(), SoundEvents.LIGHTNING_BOLT_THUNDER, 4.0F, 0.6F);
		Fx.burst(level, ParticleTypes.ELECTRIC_SPARK, this.position().add(0, 2.5, 0), 200, 3.0, 0.6);
		Fx.sphere(level, CORE, this.position().add(0, 2.5, 0), 4.0, 120);
		if (newPhase == 3) this.bossBar.setColor(BossEvent.BossBarColor.PURPLE);
		// 페이즈 전환 즉시 주변에 벼락 6발
		for (int i = 0; i < 6; i++) {
			double a = i * Math.PI / 3;
			Vec3 g = BossFx.ground(level, getX() + Math.cos(a) * 6, getZ() + Math.sin(a) * 6);
			strikes.add(new Strike(g, this.tickCount + 20 + i * 3, 2.5F, 8.0F));
		}
	}

	// ======================= 패턴 1: 천둥 강림 =======================
	private void thunderRain(ServerLevel level, LivingEntity target, int t) {
		setAttackPose(1);
		hold();
		if (t == 0) Fx.sound(level, this.position(), SoundEvents.BEACON_ACTIVATE, 2.0F, 1.6F);
		// 손 위로 전기가 모임
		Fx.burst(level, SPARK, this.position().add(0, 5.5, 0), 4, 0.6, 0.0);
		int interval = getPhase() >= 2 ? 6 : 9;
		if (t < 80 && t % interval == 0) {
			for (Player p : BossFx.players(level, this.position(), 32)) {
				// 2페이즈부터는 정확히 발밑, 1페이즈는 근처 무작위
				double spread = getPhase() >= 2 ? 0.5 : 3.0;
				Vec3 g = BossFx.ground(level, p.getX() + rnd(spread), p.getZ() + rnd(spread));
				strikes.add(new Strike(g, this.tickCount + 20, 2.5F, 8.0F));
			}
		}
	}

	// ======================= 패턴 2: 뇌전 돌진 =======================
	private void lightningDash(ServerLevel level, LivingEntity target, int t) {
		int dashes = getPhase() >= 3 ? 2 : 1;
		int cycle = 35;
		int k = t % cycle;
		if (t / cycle >= dashes) { hold(); return; }

		if (k < 18) {
			// 충전: 제자리에서 전기를 끌어모음
			setAttackPose(1);
			hold();
			Fx.sphere(level, SPARK, this.position().add(0, 2.5, 0), 3.0 - k * 0.12, 10);
			if (k == 0) Fx.sound(level, this.position(), SoundEvents.BEACON_POWER_SELECT, 2.0F, 1.8F);
			if (k == 17) {
				Vec3 aim = target.position().add(0, 1, 0).subtract(this.position().add(0, 2, 0));
				dashDir = aim.lengthSqr() < 1.0E-3 ? Vec3.ZERO : aim.normalize();
				dashHit.clear();
				// 돌진 경로 경고선
				Fx.line(level, BossFx.WARN_RED, this.position().add(0, 2, 0), this.position().add(0, 2, 0).add(dashDir.scale(22)), 0.6);
			}
		} else if (k < 30) {
			// 돌진
			setAttackPose(2);
			this.setDeltaMovement(dashDir.scale(1.7));
			Fx.burst(level, CORE, this.position().add(0, 2.5, 0), 6, 0.8, 0.0);
			Fx.burst(level, ParticleTypes.ELECTRIC_SPARK, this.position().add(0, 2.5, 0), 8, 1.0, 0.2);
			if (k == 18) Fx.sound(level, this.position(), SoundEvents.LIGHTNING_BOLT_THUNDER, 2.0F, 1.6F);
			for (Player p : BossFx.players(level, this.position().add(0, 2.5, 0), 3.2)) {
				if (dashHit.add(p.getUUID())) {
					p.hurtServer(level, level.damageSources().mobAttack(this), 10.0F);
				}
			}
		} else {
			setAttackPose(0);
			hold();
		}
	}

	// ======================= 패턴 3: 방전 고리 =======================
	private void dischargeNova(ServerLevel level, LivingEntity target, int t) {
		final double inner = 9.0, outer = 15.0;
		if (t == 0) {
			novaCenter = BossFx.ground(level, getX(), getZ());
			Fx.sound(level, this.position(), SoundEvents.BEACON_ACTIVATE, 2.5F, 0.7F);
		}
		hold();
		setAttackPose(3);
		// 1단계: 안쪽 원 경고 (t 0~40)
		if (t < 40) {
			BossFx.warnRing(level, novaCenter, inner, t > 28 ? BossFx.WARN_RED : BossFx.WARN_YELLOW, t * 0.1);
			Fx.sphere(level, SPARK, this.position().add(0, 2.5, 0), 1.0 + t * 0.05, 8);
		}
		if (t == 40) {
			shockwave(level, novaCenter, 0, inner, 10.0F);
		}
		// 2페이즈부터: 바깥 고리 (안으로 들어와야 안전)
		if (getPhase() >= 2) {
			if (t > 40 && t < 62) {
				BossFx.warnRing(level, novaCenter, inner, BossFx.WARN_YELLOW, -t * 0.1);
				BossFx.warnRing(level, novaCenter, outer, t > 52 ? BossFx.WARN_RED : BossFx.WARN_YELLOW, t * 0.1);
			}
			if (t == 62) shockwave(level, novaCenter, inner, outer, 10.0F);
		}
	}

	private void shockwave(ServerLevel level, Vec3 c, double from, double to, float damage) {
		for (double r = Math.max(1, from); r <= to; r += 1.0) {
			Fx.ring(level, ParticleTypes.ELECTRIC_SPARK, c.add(0, 0.3, 0), r, (int) (r * 6), r);
			Fx.ring(level, CORE, c.add(0, 0.2, 0), r, (int) (r * 4), -r);
		}
		Fx.sound(level, c, SoundEvents.LIGHTNING_BOLT_IMPACT, 3.0F, 0.8F);
		for (Player p : BossFx.players(level, c, to + 2)) {
			double d = Math.sqrt(Math.pow(p.getX() - c.x, 2) + Math.pow(p.getZ() - c.z, 2));
			if (d >= from && d <= to && p.getY() - c.y < 3.0) {
				p.hurtServer(level, level.damageSources().mobAttack(this), damage);
			}
		}
	}

	// ======================= 패턴 4: 뇌신의 심판 (3페이즈) =======================
	private void judgment(ServerLevel level, LivingEntity target, int t) {
		setAttackPose(1);
		hold();
		if (t == 0) {
			marked.clear();
			markedPos.clear();
			marked.addAll(BossFx.players(level, this.position(), 48));
			Fx.sound(level, this.position(), SoundEvents.ELDER_GUARDIAN_CURSE, 1.5F, 1.4F);
			for (Player p : marked) {
				p.sendOverlayMessage(Component.literal("⚡ 뇌신의 표식이 새겨졌다! 움직여라!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
			}
		}
		// 몸 위로 거대한 구체가 모임
		Fx.sphere(level, CORE, this.position().add(0, 7, 0), Math.min(3.0, t * 0.06), 25);

		if (t < 55) {
			// 표식이 플레이어를 따라다님 (점점 좁아짐)
			for (Player p : marked) {
				if (!p.isAlive()) continue;
				Vec3 g = BossFx.ground(level, p.getX(), p.getZ());
				BossFx.warnRing(level, g, 3.5 - t * 0.02, BossFx.WARN_YELLOW, t * 0.3);
			}
		} else if (t == 55) {
			// 위치 고정
			for (Player p : marked) {
				markedPos.add(BossFx.ground(level, p.getX(), p.getZ()));
			}
			Fx.sound(level, this.position(), SoundEvents.BEACON_DEACTIVATE, 2.0F, 0.6F);
		} else if (t < 72) {
			for (Vec3 g : markedPos) BossFx.warnRing(level, g, 3.5, BossFx.WARN_RED, t * 0.3);
		} else if (t == 72) {
			for (Vec3 g : markedPos) {
				ElectricStormSpell.drawBolt(level, g);
				ElectricStormSpell.drawBolt(level, g.add(rnd(1), 0, rnd(1)));
				Fx.burst(level, CORE, g.add(0, 1, 0), 80, 2.0, 0.1);
				BossFx.hurtPlayers(level, this, g, 3.8, 14.0F);
				// 주변 여진
				for (int i = 0; i < 3; i++) {
					strikes.add(new Strike(BossFx.ground(level, g.x + rnd(6), g.z + rnd(6)), this.tickCount + 10 + i * 4, 2.0F, 6.0F));
				}
			}
		}
	}

	// ======================= 보상 =======================
	@Override
	protected void dropRewards(ServerLevel level) {
		RandomSource r = this.getRandom();
		this.spawnAtLocation(level, new ItemStack(ModItems.VOLTRA_CORE, 1 + r.nextInt(2)));
		this.spawnAtLocation(level, new ItemStack(com.elementalweapons.world.ModBlocks.item("orichalcum_ingot"), 2 + r.nextInt(3)));
		this.spawnAtLocation(level, new ItemStack(com.elementalweapons.world.ModBlocks.item("storm_crystal"), 8 + r.nextInt(9)));
		Fx.burst(level, ParticleTypes.ELECTRIC_SPARK, this.position().add(0, 2, 0), 300, 4.0, 0.8);
		ElectricStormSpell.drawBolt(level, this.position());
	}
}
