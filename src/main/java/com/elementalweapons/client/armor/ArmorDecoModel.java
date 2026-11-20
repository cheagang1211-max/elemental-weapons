package com.elementalweapons.client.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import com.elementalweapons.ElementalWeaponsMod;

/** 입체 갑옷 장식: 휴머노이드와 같은 뼈대라 착용자 동작을 그대로 따라가고, 착용한 부위만 보입니다. */
public class ArmorDecoModel extends HumanoidModel<HumanoidRenderState> {
	private final String tier;
	private final ModelPart helm, chest, rpaul, lpaul, tassets, rknee, lknee, rboot, lboot;

	public ArmorDecoModel(ModelPart root, String tier) {
		super(root);
		this.tier = tier;
		helm = head.getChild("deco_helm");
		chest = body.getChild("deco_chest");
		tassets = body.getChild("deco_tassets");
		rpaul = rightArm.getChild("deco_rpaul");
		lpaul = leftArm.getChild("deco_lpaul");
		rknee = rightLeg.getChild("deco_rknee");
		lknee = leftLeg.getChild("deco_lknee");
		rboot = rightLeg.getChild("deco_rboot");
		lboot = leftLeg.getChild("deco_lboot");
	}

	public static String tierOf(ItemStack stack) {
		if (stack == null || stack.isEmpty()) return null;
		var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
		if (!id.getNamespace().equals(ElementalWeaponsMod.MOD_ID)) return null;
		String p = id.getPath();
		if (p.startsWith("aether_")) return "aether";
		if (p.startsWith("orichalcum_")) return "orichalcum";
		return null;
	}

	public boolean wears(HumanoidRenderState s) {
		return tier.equals(tierOf(s.headEquipment)) || tier.equals(tierOf(s.chestEquipment))
				|| tier.equals(tierOf(s.legsEquipment)) || tier.equals(tierOf(s.feetEquipment));
	}

	@Override
	public void setupAnim(HumanoidRenderState s) {
		super.setupAnim(s);
		boolean h = tier.equals(tierOf(s.headEquipment)), c = tier.equals(tierOf(s.chestEquipment));
		boolean l = tier.equals(tierOf(s.legsEquipment)), f = tier.equals(tierOf(s.feetEquipment));
		helm.visible = h;
		chest.visible = c; rpaul.visible = c; lpaul.visible = c;
		tassets.visible = l; rknee.visible = l; lknee.visible = l;
		rboot.visible = f; lboot.visible = f;
		hat.visible = false;
	}
}
