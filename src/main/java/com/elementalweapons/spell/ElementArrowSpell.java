package com.elementalweapons.spell;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.item.Element;

/** 활: 속성 화살 여러 발 발사, 날아가는 동안 속성 궤적, 맞거나 박히면 속성 폭발 */
public class ElementArrowSpell extends Spell {
	private final Element element;
	private final List<AbstractArrow> arrows = new ArrayList<>();
	private final List<Vec3> last = new ArrayList<>();
	private final List<Boolean> done = new ArrayList<>();
	private final float charge;

	public ElementArrowSpell(ServerLevel level, Player caster, Element element, float charge, ItemStack bow) {
		super(level, caster);
		this.element = element;
		this.charge = charge;
		int n = charge >= 1.0F ? 5 : charge >= 0.5F ? 3 : 1;
		float spread = 7.0F;
		for (int i = 0; i < n; i++) {
			float yawOff = (i - (n - 1) / 2.0F) * spread;
			Arrow a = new Arrow(level, caster, new ItemStack(Items.ARROW), bow);
			a.pickup = AbstractArrow.Pickup.DISALLOWED;
			a.setBaseDamage(3.0 + 4.0 * charge);
			a.setCritArrow(charge >= 1.0F);
			double yaw = Math.toRadians(caster.getYRot() + yawOff), pitch = Math.toRadians(caster.getXRot());
			double dx = -Math.sin(yaw) * Math.cos(pitch), dy = -Math.sin(pitch), dz = Math.cos(yaw) * Math.cos(pitch);
			a.shoot(dx, dy, dz, 1.2F + 2.4F * charge, 0.3F);
			level.addFreshEntity(a);
			arrows.add(a); last.add(a.position()); done.add(false);
		}
		Fx.sound(level, caster.position(), SoundEvents.ARROW_SHOOT, 1.0F, 0.8F + charge * 0.4F);
		if (charge >= 1.0F) Fx.sound(level, caster.position(), SoundEvents.ILLUSIONER_CAST_SPELL, 0.6F, 1.4F);
		ElementFx.impact(level, element, caster.getEyePosition().add(caster.getLookAngle().scale(1.2)), 0.8);
	}

	@Override
	protected boolean update() {
		boolean any = false;
		for (int i = 0; i < arrows.size(); i++) {
			if (done.get(i)) continue;
			AbstractArrow a = arrows.get(i);
			boolean stuck = age > 3 && !a.isRemoved() && a.getDeltaMovement().lengthSqr() < 1.0E-3;
			if (a.isRemoved() || stuck || age > 100) {
				explode(last.get(i));
				if (!a.isRemoved()) a.discard();
				done.set(i, true);
				continue;
			}
			any = true;
			Vec3 p = a.position();
			Fx.line(level, ElementFx.dust(element, 0.9F), last.get(i), p, 0.5);
			Fx.at(level, ElementFx.particle(element), p);
			last.set(i, p);
		}
		return !any;
	}

	private void explode(Vec3 pos) {
		double r = 1.6 + 1.6 * charge;
		ElementFx.impact(level, element, pos, r);
		for (LivingEntity e : Fx.enemies(level, pos, r)) Fx.hurt(level, caster, e, 3.0F + 5.0F * charge);
		if (charge >= 1.0F && arrows.size() > 2) ElementEffects.strike(element, level, caster, pos);
	}
}
