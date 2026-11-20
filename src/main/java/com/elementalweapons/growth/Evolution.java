package com.elementalweapons.growth;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.ElementalWeaponItem;
import com.elementalweapons.item.ModItems;
import com.elementalweapons.item.PlayerClass;
import com.elementalweapons.item.TrainingWeaponItem;
import com.elementalweapons.item.WeaponType;
import com.elementalweapons.skill.SkillInfo;
import com.elementalweapons.skill.SkillNet;
import com.elementalweapons.spell.Fx;

/** 10레벨 속성 진화: 7속성 중 하나를 골라 나무 무기 → 속성 무기. 무기 외형은 레벨 단계(1~4)에 따라 바뀜 */
public final class Evolution {
	private Evolution() {}

	public static final int LEVEL = 10;

	public static final AttachmentType<Integer> ELEMENT = AttachmentRegistry.<Integer>builder()
			.persistent(Codec.INT).copyOnDeath().initializer(() -> -1)
			.syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.targetOnly())
			.buildAndRegister(ElementalWeaponsMod.id("element"));

	public static int element(Player p) { Integer v = p.getAttached(ELEMENT); return v == null ? -1 : v; }

	public static boolean pending(Player p) {
		Growth g = ModGrowth.get(p);
		return g.cls() != 0 && g.level() >= LEVEL && element(p) < 0;
	}

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 40 != 11) return;
			for (ServerPlayer p : server.getPlayerList().getPlayers()) {
				updateTiers(p);
				if (pending(p) && server.getTickCount() % 1200 == 11)
					p.sendSystemMessage(Component.literal("✦ 속성 진화를 할 수 있습니다! [K] 성장 창 → '속성 진화'").withStyle(ChatFormatting.GOLD));
			}
		});
	}

	/** 레벨이 오르는 순간 / 직업을 고르는 순간 호출 */
	public static void check(ServerPlayer p) { if (pending(p)) SkillNet.openEvolve(p); }

	public static void choose(ServerPlayer p, int el) {
		if (!pending(p) || el < 0 || el >= Element.values().length) return;
		Element e = Element.values()[el];
		PlayerClass c = ModGrowth.get(p).playerClass();
		p.setAttached(ELEMENT, el);
		// 나무 무기는 사라지고 속성 무기 지급
		var inv = p.getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) if (inv.getItem(i).getItem() instanceof TrainingWeaponItem) inv.setItem(i, ItemStack.EMPTY);
		for (WeaponType t : WeaponType.values()) if (t.playerClass() == c) {
			ItemStack s = new ItemStack(ModItems.get(e, t));
			setTier(s, SkillInfo.tier(ModGrowth.get(p).level()));
			if (!inv.add(s)) p.spawnAtLocation((ServerLevel) p.level(), s);
		}
		ServerLevel l = (ServerLevel) p.level();
		ElementFx.impact(l, e, p.position(), 5);
		ElementFx.rune(l, ElementFx.color(e), p.position(), 3, 40, 6F);
		for (double y = 0; y < 8; y += 0.6) Fx.at(l, ElementFx.particle(e), p.position().add(0, y, 0));
		Fx.sound(l, p.position(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1F, 1.2F);
		p.sendSystemMessage(Component.literal("✦ 속성 진화! " + e.koName + " 속성의 " + c.koName + "이(가) 되었습니다. X·C 스킬이 열렸습니다.").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
		Tutorial.evolved(p);
	}

	/** 운명의 현자 초기화 */
	public static void reset(ServerPlayer p) { p.setAttached(ELEMENT, -1); }

	public static void setTier(ItemStack s, int tier) {
		s.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(java.util.List.of((float) tier), java.util.List.of(), java.util.List.of(), java.util.List.of()));
	}

	/** 가방 속 속성 무기의 외형 단계를 레벨에 맞춤 */
	private static void updateTiers(ServerPlayer p) {
		int tier = SkillInfo.tier(ModGrowth.get(p).level());
		var inv = p.getInventory();
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack s = inv.getItem(i);
			if (!(s.getItem() instanceof ElementalWeaponItem)) continue;
			CustomModelData d = s.get(DataComponents.CUSTOM_MODEL_DATA);
			if (d == null || d.floats().isEmpty() || d.floats().get(0) != tier) setTier(s, tier);
		}
	}
}
