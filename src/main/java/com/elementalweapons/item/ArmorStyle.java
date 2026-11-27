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

	/** 직업별 갑옷 방어 보정 (4부위 모두 입었을 때): 방어력, 방어 강도, 밀치기 저항, 이동 속도(배율) */
	public static final double[][] CLASS_DEF = {
			{0, 0, 0, 0},          // 없음
			{-5, -1, 0, 0.04},     // 마법사: 로브 — 가볍고 빠름
			{3, 2, 0.15, 0.02},    // 전사: 모피 갑주 — 단단하면서 날렵
			{7, 3, 0.1, -0.03},    // 기사: 전신 판금 — 가장 단단, 조금 느림
			{-3, 0, 0, 0.08},      // 암살자: 가죽 은신복 — 가장 빠름
			{-4, 0, 0, 0.03},      // 서포터: 사제복
			{0, 1, 0, 0.03},       // 사신: 수의 망토
			{-2, 0, 0, 0.05},      // 궁수: 사냥꾼 가죽
			{6, 3, 0.3, -0.03},    // 파괴자: 육중한 판금 — 가장 안 밀림
	};
	private static final net.minecraft.resources.Identifier DEF = ElementalWeaponsMod.id("class_armor"), TOUGH = ElementalWeaponsMod.id("class_armor_tough"),
			KB = ElementalWeaponsMod.id("class_armor_kb"), SPEED = ElementalWeaponsMod.id("class_armor_speed");

	/** 이 직업 보정이 붙은 갑옷 이름 (예: aether_mage_fire). 직업이 없으면 예전 이름 */
	public static String assetFor(String tier, int cls, int el) {
		String e = el < 0 ? "neutral" : Element.values()[el].id;
		if (cls <= 0 || cls >= PlayerClass.values().length) return el < 0 ? tier : tier + "_" + e;
		return tier + "_" + PlayerClass.values()[cls].name().toLowerCase() + "_" + e;
	}

	private static void attr(ServerPlayer p, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> a, net.minecraft.resources.Identifier id, double v,
			net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation op) {
		var inst = p.getAttribute(a);
		if (inst == null) return;
		if (Math.abs(v) < 1e-6) { inst.removeModifier(id); return; }
		var cur = inst.getModifier(id);
		if (cur != null && Math.abs(cur.amount() - v) < 1e-6) return;
		inst.addOrUpdateTransientModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(id, v, op));
	}

	private static void apply(ServerPlayer p) {
		int el = Evolution.element(p);
		int cls = com.elementalweapons.growth.ModGrowth.get(p).cls();
		int worn = 0;
		for (EquipmentSlot slot : SLOTS) {
			ItemStack st = p.getItemBySlot(slot);
			if (st.isEmpty()) continue;
			var id = BuiltInRegistries.ITEM.getKey(st.getItem());
			if (!id.getNamespace().equals(ElementalWeaponsMod.MOD_ID)) continue;
			String path = id.getPath(), tier = path.startsWith("aether_") ? "aether" : path.startsWith("orichalcum_") ? "orichalcum" : null;
			if (tier == null) continue;
			worn++;
			String want = assetFor(tier, cls, el);
			Equippable eq = st.get(DataComponents.EQUIPPABLE);
			if (eq == null) continue;
			if (eq.assetId().isPresent() && eq.assetId().get().identifier().getPath().equals(want)) continue;
			Equippable next = Equippable.builder(eq.slot()).setEquipSound(eq.equipSound())
					.setAsset(ResourceKey.create(EquipmentAssets.ROOT_ID, ElementalWeaponsMod.id(want))).build();
			st.set(DataComponents.EQUIPPABLE, next);
		}
		double[] d = CLASS_DEF[cls >= 0 && cls < CLASS_DEF.length ? cls : 0];
		double f = worn / 4.0;
		var add = net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;
		attr(p, net.minecraft.world.entity.ai.attributes.Attributes.ARMOR, DEF, d[0] * f, add);
		attr(p, net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS, TOUGH, d[1] * f, add);
		attr(p, net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE, KB, d[2] * f, add);
		attr(p, net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, SPEED, d[3] * f, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
	}
}
