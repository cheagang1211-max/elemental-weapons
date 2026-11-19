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
		if (type == WeaponType.DAGGER) {
			// 플레이어 이동은 클라이언트가 처리하므로 양쪽에서 실행
			Vec3 look = user.getLookAngle();
			user.setDeltaMovement(look.x * 1.8, Math.max(look.y * 1.8, 0.25), look.z * 1.8);
			user.fallDistance = 0;
			if (level instanceof ServerLevel server) {
				ElementEffects.dashBurst(element, server, user.position());
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
			case STAFF -> {
				Spell spell = ElementEffects.staffSpell(element, server, user);
				if (spell == null) return InteractionResult.PASS;
				SpellManager.add(spell);
				user.getCooldowns().addCooldown(user.getItemInHand(hand), (int) Math.round(element.staffCooldown * com.elementalweapons.growth.ModGrowth.get(user).staffCooldownFactor()));
			}
			default -> {
				return InteractionResult.PASS;
			}
		}
		return InteractionResult.SUCCESS;
	}
}
