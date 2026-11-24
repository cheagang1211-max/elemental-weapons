package com.elementalweapons.item;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.growth.Evolution;

/**
 * 속성 갑옷: 에테르·오리하르콘 갑옷을 입으면 착용자의 속성에 맞는 색과 장식으로 바뀜.
 * 장비의 모양 이름(aether → aether_fire 등)을 서버가 바꿔 주면 모든 사람 화면에 똑같이 보임.
 */
public final class ArmorStyle {
	private ArmorStyle() {}

	private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 20 != 15) return;
			for (ServerPlayer p : server.getPlayerList().getPlayers()) apply(p);
		});
	}

	private static void apply(ServerPlayer p) {
		int el = Evolution.element(p);
		for (EquipmentSlot slot : SLOTS) {
			ItemStack st = p.getItemBySlot(slot);
			if (st.isEmpty()) continue;
			var id = BuiltInRegistries.ITEM.getKey(st.getItem());
			if (!id.getNamespace().equals(ElementalWeaponsMod.MOD_ID)) continue;
			String path = id.getPath(), tier = path.startsWith("aether_") ? "aether" : path.startsWith("orichalcum_") ? "orichalcum" : null;
			if (tier == null) continue;
			String want = el < 0 ? tier : tier + "_" + Element.values()[el].id;
			Equippable eq = st.get(DataComponents.EQUIPPABLE);
			if (eq == null) continue;
			if (eq.assetId().isPresent() && eq.assetId().get().identifier().getPath().equals(want)) continue;
			Equippable next = Equippable.builder(eq.slot()).setEquipSound(eq.equipSound())
					.setAsset(ResourceKey.create(EquipmentAssets.ROOT_ID, ElementalWeaponsMod.id(want))).build();
			st.set(DataComponents.EQUIPPABLE, next);
		}
	}
}
