package com.elementalweapons.spell;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.WeaponType;
import com.elementalweapons.mob.ModMobs;

/**
 * 무기 우클릭 입체 연출 (피해는 원래 기술이 처리, 이건 보이는 부분만)
 *  검: 하늘에서 영검이 꽂힘 / 대검: 대검 6자루 원진이 돌며 내려꽂힘 / 망치: 뒤에서 거대 망치를 휘둘러 찍고 결정 가시가 솟음
 *  낫: 거대한 초승달 날이 두 바퀴 돎 / 마법서: 머리 위에 마도서가 펼쳐짐
 */
public class WeaponVisual extends Spell {
	/** GrandModels 의 우클릭 모형 번호 */
	private static final int BLADE = 12, BLADE_RING = 13, HAMMER = 14, SHOCK = 15, CRESCENT = 16, TOME = 17;
	private final Element el;
	private final WeaponType type;
	private final Vec3 target;
	private final float yaw;
	private final List<GrandVisual> vis = new ArrayList<>();
	private GrandVisual main, extra;

	private WeaponVisual(ServerLevel level, Player caster, Element el, WeaponType type, Vec3 target) {
		super(level, caster);
		this.el = el;
		this.type = type;
		this.target = target;
		this.yaw = caster.getYRot();
	}

	/** 우클릭 연출 시작 (target: 검이 꽂힐 곳, 다른 무기는 무시) */
	public static void start(ServerLevel level, Player caster, Element el, WeaponType type, Vec3 target) {
		if (type != WeaponType.SWORD && type != WeaponType.GREATSWORD && type != WeaponType.HAMMER && type != WeaponType.SCYTHE && type != WeaponType.BOOK) return;
		SpellManager.add(new WeaponVisual(level, caster, el, type, target == null ? caster.position() : target));
	}

	private GrandVisual spawn(int kind, Vec3 pos, float yRot) {
		GrandVisual g = ModMobs.GRAND.create(level, EntitySpawnReason.MOB_SUMMONED);
		if (g == null) return null;
		g.addTag(com.elementalweapons.world.VanillaPurge.ALLOWED);
		g.setKind(kind); g.setElem(el.ordinal());
		g.snapTo(pos.x, pos.y, pos.z, yRot, 0);
		g.setYBodyRot(yRot); g.setYHeadRot(yRot);
		level.addFreshEntity(g);
		vis.add(g);
		return g;
	}

	private static void place(GrandVisual g, Vec3 p, float yRot) {
		if (g == null) return;
		g.setPos(p.x, p.y, p.z);
		g.setYRot(yRot); g.setYBodyRot(yRot); g.setYHeadRot(yRot);
		g.life = 0;
	}

	private static float ease(float x) { x = Mth.clamp(x, 0, 1); return x * x * (3 - 2 * x); }

	@Override
	protected boolean update() {
		if (casterGone()) return true;
		boolean done = switch (type) {
			case SWORD -> sword();
			case GREATSWORD -> ring();
			case HAMMER -> hammer();
			case SCYTHE -> crescent();
			default -> tome();
		};
		return done;
	}

	@Override
	protected void onEnd() {
		for (GrandVisual g : vis) if (g != null && g.isAlive()) g.discard();
		vis.clear();
	}

	// 검: 영검 낙하 (4틱) → 꽂힌 채 빛남 → 땅속으로 가라앉음
	private boolean sword() {
		Vec3 t = target;
		if (age == 0) {
			main = spawn(BLADE, t.add(0, 16, 0), yaw);
			SpellManager.add(new MagicCircle(level, caster, el, t.add(0, 0.1, 0), 3.5, 26, false));
		}
		if (age <= 4) {
			float f = age / 4F;
			place(main, t.add(0, 16 * (1 - f * f) - 0.6 * f, 0), yaw);
			Fx.line(level, ElementFx.particle(el), t.add(0, 16 * (1 - f * f) + 6, 0), t.add(0, 24, 0), 1.2);
		}
		if (age == 4) {
			Fx.ring(level, ElementFx.dust(el, 1.8F), t.add(0, 0.2, 0), 2.2, 30, 0);
			Fx.burst(level, ElementFx.particle(el), t.add(0, 0.5, 0), 30, 0.8, 0.15);
			Fx.sound(level, t, SoundEvents.ANVIL_LAND, 0.8F, 1.3F);
		}
		if (main != null) main.setPhase(age < 4 ? 0 : Math.min(1, (age - 4) / 10F));
		if (age > 4 && age < 22 && age % 3 == 0) Fx.ring(level, ElementFx.particle(el), t.add(0, 0.3, 0), 1 + (age - 4) * 0.25, 18, age);
		if (age > 18) place(main, t.add(0, -0.6 - (age - 18) * 0.45, 0), yaw);
		return age >= 30;
	}

	// 대검: 원진이 머리 위에서 돌며 내려와 땅에 꽂힘
	private boolean ring() {
		Vec3 c = caster.position();
		if (age == 0) main = spawn(BLADE_RING, c.add(0, 7, 0), yaw);
		float f = ease(age / 6F);
		Vec3 base = target.add(0, 7 * (1 - f) - 0.8 * f, 0);
		if (age <= 6) place(main, base, yaw + age * 9);
		if (main != null) main.setPhase(Math.min(1, age / 6F));
		if (age == 6) {
			for (int i = 0; i < 6; i++) {
				double a = Math.toRadians(yaw + 54) + i * Math.PI / 3;
				Vec3 p = target.add(Math.cos(a) * 4.5, 0.3, Math.sin(a) * 4.5);
				Fx.burst(level, ElementFx.particle(el), p, 16, 0.4, 0.12);
			}
			Fx.ring(level, ElementFx.dust(el, 1.8F), target.add(0, 0.2, 0), 4.6, 48, 0);
			Fx.sound(level, target, SoundEvents.ANVIL_LAND, 0.9F, 0.8F);
		}
		if (age > 20) place(main, target.add(0, -0.8 - (age - 20) * 0.5, 0), yaw + 54);
		return age >= 32;
	}

	// 망치: 앞에 거대한 망치가 들려 올라갔다(7틱) 내리찍힘(3틱) → 결정 가시 고리
	private boolean hammer() {
		Vec3 fwd = Vec3.directionFromRotation(0, yaw);
		Vec3 pivot = caster.position().add(fwd.scale(3.6));
		if (age == 0) {
			main = spawn(HAMMER, pivot.add(0, 6.4, 0), yaw);
			if (main != null) main.setStrike(main.tickCount * 2 + 2);
		}
		if (main != null) place(main, pivot.add(0, 6.4, 0), yaw + 180);
		if (age == 9) {
			Vec3 hit = pivot;
			extra = spawn(SHOCK, caster.position(), yaw);
			Fx.burst(level, ElementFx.particle(el), hit.add(0, 0.4, 0), 50, 1.2, 0.25);
			Fx.sound(level, hit, SoundEvents.GENERIC_EXPLODE.value(), 0.9F, 0.8F);
		}
		if (extra != null) { extra.setPhase(Math.min(1, (age - 9) / 22F)); place(extra, caster.position(), yaw); }
		if (age == 26 && main != null) { main.discard(); main = null; }
		return age >= 34;
	}

	// 낫: 초승달 날이 시전자를 따라다니며 두 바퀴
	private boolean crescent() {
		if (age == 0) main = spawn(CRESCENT, caster.position().add(0, 1.0, 0), yaw);
		place(main, caster.position().add(0, 0.9, 0), yaw);
		if (main != null) main.setPhase(ease(age / 13F));
		return age >= 15;
	}

	// 마법서: 머리 위에서 펼쳐지는 책 + 룬 고리
	private boolean tome() {
		Vec3 p = caster.position().add(0, 2.9, 0);
		if (age == 0) main = spawn(TOME, p, yaw);
		place(main, p, yaw);
		if (main != null) main.setPhase(age < 30 ? Math.min(1, age / 12F) : 1 - (age - 30) / 10F);
		if (age % 4 == 0) Fx.burst(level, ElementFx.particle(el), p.add(0, 0.6, 0), 4, 0.6, 0.02);
		return age >= 40;
	}
}
