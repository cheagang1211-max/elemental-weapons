package com.elementalweapons.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;

import com.elementalweapons.spell.Fx;

/**
 * 원소 무기 강화 (+1 ~ +10)
 * 방법: 오른손에 원소 무기, 왼손에 원소 정수 → 웅크린 채(Shift) 우클릭
 * 효과: 강화 1단계마다 보스 피해 +50% (최대 +10 = 6배)
 */
public final class Enhancement {
	private Enhancement() {}

	public static final int MAX = 10;
	private static final String KEY = "ew_level";

	public static int level(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data == null ? 0 : data.copyTag().getIntOr(KEY, 0);
	}

	public static double multiplier(ItemStack stack) {
		return 1.0 + 0.5 * level(stack);
	}

	public static void apply(ItemStack weapon, int next) {
		CustomData.update(DataComponents.CUSTOM_DATA, weapon, tag -> tag.putInt(KEY, next));
		weapon.set(DataComponents.LORE, new ItemLore(List.<Component>of(
				Component.literal("강화 +" + next).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
				Component.literal("보스 피해 x" + String.format("%.1f", 1.0 + 0.5 * next)).withStyle(ChatFormatting.GRAY))));
	}

	public static void tryUpgrade(ServerLevel level, Player player, ItemStack weapon, ItemStack essence) {
		int lv = level(weapon);
		if (lv >= MAX) {
			player.sendOverlayMessage(Component.literal("이미 최대 강화(+" + MAX + ")입니다.").withStyle(ChatFormatting.GRAY));
			return;
		}
		int next = lv + 1;
		apply(weapon, next);
		if (!player.isCreative()) essence.shrink(1);
		Fx.burst(level, Fx.dust(0xFFE27A, 1.6F), player.position().add(0, 1.2, 0), 60, 0.8, 0.05);
		Fx.sound(level, player.position(), SoundEvents.ANVIL_USE, 1.0F, 1.4F);
		Fx.sound(level, player.position(), SoundEvents.PLAYER_LEVELUP, 1.0F, 1.2F);
		player.sendOverlayMessage(Component.literal("강화 성공! +" + next).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
	}
}
