package com.elementalweapons.item;

import java.util.function.BiConsumer;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.spell.ElementEffects;
import com.elementalweapons.spell.Spell;
import com.elementalweapons.spell.SpellManager;

/**
 * 모든 원소 무기 (속성 7종 x 종류 4종) 가 이 클래스 하나를 씁니다.
 *
 * 우클릭 능력:
 *  - 단검: 바라보는 방향으로 돌진
 *  - 검: 바라보는 곳(30블록)에 속성 공격 1회
 *  - 대검: 내 주위 6곳에 속성 공격
 *  - 지팡이: 속성별 대마법 (spell 패키지 참고)
 */
public class ElementalWeaponItem extends Item {
	private static final int DAGGER_COOLDOWN = 30;
	private static final int SWORD_COOLDOWN = 40;
	private static final int GREATSWORD_COOLDOWN = 120;

	/**
	 * 지팡이 시전 시 클라이언트에서 호출됩니다. (카메라 연출용)
	 * 클라이언트 초기화 코드(ElementalWeaponsClient)가 채워 넣으며, 서버에서는 항상 null입니다.
	 */
	public static BiConsumer<Element, Player> clientCastHook = null;

	private final Element element;
	private final WeaponType type;

	public ElementalWeaponItem(Item.Properties properties, Element element, WeaponType type) {
		super(properties);
		this.element = element;
		this.type = type;
	}

	public Element element() {
		return element;
	}

	public WeaponType type() {
		return type;
	}

	@Override
	public InteractionResult use(Level level, Player user, InteractionHand hand) {
		// 강화: 웅크리고 우클릭 + 왼손에 원소 정수
		if (hand == InteractionHand.MAIN_HAND && user.isShiftKeyDown() && user.getOffhandItem().is(ModItems.ELEMENT_ESSENCE)) {
			if (level instanceof ServerLevel server) {
				Enhancement.tryUpgrade(server, user, user.getItemInHand(hand), user.getOffhandItem());
			}
			return InteractionResult.SUCCESS;
		}
		// 직업 확인
		if (!com.elementalweapons.growth.ModGrowth.canUse(user, type)) {
			com.elementalweapons.growth.ModGrowth.denyMessage(user, type);
			return InteractionResult.FAIL;
		}
		if (type == WeaponType.SHIELD) return super.use(level, user, hand); // 방패 막기
		if (type == WeaponType.BOW) { user.startUsingItem(hand); return InteractionResult.CONSUME; } // 활 당기기
		if (type == WeaponType.DAGGER) {
			// 단검: 속성마다 다른 몸놀림 (서버가 움직이고 동기화)
			if (level instanceof ServerLevel server && user instanceof net.minecraft.server.level.ServerPlayer sp) {
				com.elementalweapons.skill.ElementMotion.opener(server, sp, element);
				ElementEffects.dashBurst(element, server, user.position());
				com.elementalweapons.growth.Tutorial.event(user, 1);
			}
			user.getCooldowns().addCooldown(user.getItemInHand(hand), DAGGER_COOLDOWN);
			return InteractionResult.SUCCESS;
		}

		if (!(level instanceof ServerLevel server)) {
			// 클라이언트(내 화면): 지팡이 시전 카메라 연출 시작
			if (type == WeaponType.STAFF && clientCastHook != null) {
				clientCastHook.accept(element, user);
			}
			return InteractionResult.PASS; // 실제 능력은 서버에서만 처리
		}

		// 근접 무기: 속성마다 다른 시작 동작 (순간 이동·돌진·후퇴·속박·도약·기습·강림)
		if ((type == WeaponType.SWORD || type == WeaponType.GREATSWORD || type == WeaponType.HAMMER || type == WeaponType.SCYTHE)
				&& user instanceof net.minecraft.server.level.ServerPlayer sp)
			com.elementalweapons.skill.ElementMotion.opener(server, sp, element);

		switch (type) {
			case SWORD -> {
				HitResult hit = user.pick(30.0, 0.0F, false);
				if (hit.getType() == HitResult.Type.MISS) return InteractionResult.PASS;
				ElementEffects.strike(element, server, user, hit.getLocation());
				user.getCooldowns().addCooldown(user.getItemInHand(hand), SWORD_COOLDOWN);
			}
			case GREATSWORD -> {
				Vec3 c = user.position();
				for (int i = 0; i < 6; i++) {
					double a = i * Math.PI / 3;
					ElementEffects.strike(element, server, user, c.add(Math.cos(a) * 4.5, 0, Math.sin(a) * 4.5));
				}
				user.getCooldowns().addCooldown(user.getItemInHand(hand), GREATSWORD_COOLDOWN);
			}
			case BOOK -> {
				ElementEffects.supportAura(element, server, user);
				user.getCooldowns().addCooldown(user.getItemInHand(hand), (int) Math.round(300 * (com.elementalweapons.growth.ModGrowth.get(user).staffCooldownFactor() * (1 - com.elementalweapons.growth.ModGrowth.paragon(user).cooldownCut()))));
			}
			case SCYTHE -> {
				SpellManager.add(new com.elementalweapons.spell.ReapSpell(server, user, element, com.elementalweapons.skill.ElementMotion.mult(user)));
				user.getCooldowns().addCooldown(user.getItemInHand(hand), 45);
			}
			case HAMMER -> {
				ElementEffects.hammerSlam(element, server, user);
				user.getCooldowns().addCooldown(user.getItemInHand(hand), 100);
			}
			case STAFF -> {
				Spell spell = ElementEffects.staffSpell(element, server, user);
				if (spell == null) return InteractionResult.PASS;
				SpellManager.add(spell);
				user.getCooldowns().addCooldown(user.getItemInHand(hand), (int) Math.round(element.staffCooldown * (com.elementalweapons.growth.ModGrowth.get(user).staffCooldownFactor() * (1 - com.elementalweapons.growth.ModGrowth.paragon(user).cooldownCut()))));
			}
			default -> {
				return InteractionResult.PASS;
			}
		}
		com.elementalweapons.growth.Tutorial.classEvent(user, "skill");
		com.elementalweapons.growth.Tutorial.event(user, 1);
		if (user instanceof net.minecraft.server.level.ServerPlayer jp) com.elementalweapons.growth.Jobs.flourish(jp, element, type == WeaponType.STAFF);
		return InteractionResult.SUCCESS;
	}

	// ---------------- 활 ----------------
	@Override
	public net.minecraft.world.item.ItemUseAnimation getUseAnimation(net.minecraft.world.item.ItemStack stack) {
		return type == WeaponType.BOW ? net.minecraft.world.item.ItemUseAnimation.BOW : super.getUseAnimation(stack);
	}

	@Override
	public int getUseDuration(net.minecraft.world.item.ItemStack stack, net.minecraft.world.entity.LivingEntity entity) {
		return type == WeaponType.BOW ? 72000 : super.getUseDuration(stack, entity);
	}

	@Override
	public boolean releaseUsing(net.minecraft.world.item.ItemStack stack, Level level, net.minecraft.world.entity.LivingEntity entity, int timeLeft) {
		if (type != WeaponType.BOW || !(entity instanceof Player p)) return super.releaseUsing(stack, level, entity, timeLeft);
		int used = getUseDuration(stack, entity) - timeLeft;
		float charge = Math.min(1.0F, used / 20.0F);
		if (charge < 0.15F) return false;
		if (level instanceof ServerLevel server) {
			SpellManager.add(new com.elementalweapons.spell.ElementArrowSpell(server, p, element, charge, stack));
			if (charge >= 1.0F) com.elementalweapons.growth.Tutorial.classEvent(p, "full_volley");
			com.elementalweapons.growth.Tutorial.event(p, 1);
			p.getCooldowns().addCooldown(stack, charge >= 1.0F ? 30 : 8);
		}
		return true;
	}
}
