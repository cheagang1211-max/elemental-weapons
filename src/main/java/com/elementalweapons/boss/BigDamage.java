package com.elementalweapons.boss;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.elementalweapons.item.Element;
import com.elementalweapons.item.ElementalWeaponItem;

/**
 * RPG식 큰 숫자 피해 계산.
 *
 * 보스 피해 = (바닐라 피해량) x BASE x 무기 배율 x 속성 상성 x (0.9~1.1 랜덤)
 *  - 원소 무기 검(공격력 8) 한 대 ≒ 8 x 100,000 x 4 = 약 320만
 *  - 속성 약점이면 2배, 같은 속성이면 절반
 *  - 원소 무기가 아닌 것(바닐라 검 등)은 거의 피해가 없음 → 원소 무기가 필수
 */
public final class BigDamage {
	private BigDamage() {}

	/** 바닐라 피해 1 = 보스 피해 10만 */
	public static final double BASE = 100_000;
	/** 원소 무기 배율 (나중에 무기 강화 단계가 생기면 여기서 올림) */
	public static final double ELEMENTAL_WEAPON = 4.0;
	/** 일반 무기/맨손 */
	public static final double NORMAL_WEAPON = 0.02;
	/** 플레이어가 아닌 피해 (다른 몹, 환경) */
	public static final double NON_PLAYER = 0.005;

	public record Result(long damage, double affinity, Element attackElement) {}

	public static Result compute(ElementalBoss boss, Entity attacker, float amount, RandomSource random) {
		double mult;
		double aff = 1.0;
		Element el = null;
		if (attacker instanceof Player p) {
			ItemStack held = p.getMainHandItem();
			if (held.getItem() instanceof ElementalWeaponItem w) {
				mult = ELEMENTAL_WEAPON;
				el = w.element();
				aff = affinity(el, boss.element());
			} else {
				mult = NORMAL_WEAPON;
			}
		} else {
			mult = NON_PLAYER;
		}
		double roll = 0.9 + random.nextDouble() * 0.2;
		long dmg = Math.round(amount * BASE * mult * aff * roll);
		return new Result(Math.max(1, dmg), aff, el);
	}

	/**
	 * 속성 상성: 공격 속성이 보스 속성의 약점이면 2.0, 같은 속성이면 0.5
	 *  불 ← 물,  물 ← 전기,  전기 ← 풀,  풀 ← 불,  바람 ← 전기,  어둠 ↔ 빛
	 */
	public static double affinity(Element attack, Element defend) {
		if (attack == defend) return 0.5;
		boolean weak = switch (defend) {
			case FIRE -> attack == Element.WATER;
			case WATER -> attack == Element.ELECTRIC;
			case ELECTRIC -> attack == Element.GRASS;
			case GRASS -> attack == Element.FIRE;
			case WIND -> attack == Element.ELECTRIC;
			case DARK -> attack == Element.LIGHT;
			case LIGHT -> attack == Element.DARK;
		};
		return weak ? 2.0 : 1.0;
	}

	/** 1234567 → "1,234,567" */
	public static String fmt(long n) {
		return String.format("%,d", n);
	}

	/** 공격한 플레이어 화면(핫바 위)에 띄울 피해 숫자 */
	public static Component hitMessage(Result r) {
		MutableComponent msg = Component.literal("⚔ " + fmt(r.damage()))
				.withStyle(ChatFormatting.BOLD, r.affinity() > 1 ? ChatFormatting.GOLD
						: r.affinity() < 1 ? ChatFormatting.GRAY : ChatFormatting.WHITE);
		if (r.affinity() > 1) {
			msg.append(Component.literal("  약점! x2").withStyle(ChatFormatting.YELLOW));
		} else if (r.affinity() < 1) {
			msg.append(Component.literal("  저항 x0.5").withStyle(ChatFormatting.DARK_GRAY));
		} else if (r.attackElement() == null) {
			msg.append(Component.literal("  (원소 무기가 아니라 거의 효과가 없다)").withStyle(ChatFormatting.DARK_GRAY));
		}
		return msg;
	}
}
