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
	private final ModelPart[] elHelm = new ModelPart[7], elBack = new ModelPart[7];
	private final ModelPart hornR, hornL;

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
		for (int i = 0; i < 7; i++) {
			elHelm[i] = head.getChild("el_" + ElementArmorParts.IDS[i] + "_helm");
			elBack[i] = body.getChild("el_" + ElementArmorParts.IDS[i] + "_back");
		}
		hornR = helm.getChild("horn_r"); hornL = helm.getChild("horn_l");
	}

	/** 착용한 갑옷의 속성 (서버가 착용자 속성에 맞춰 장비 모양 이름을 '_fire' 등으로 바꿔 줌). 없으면 -1 */
	public static int elementOf(HumanoidRenderState s) {
		for (ItemStack st : new ItemStack[]{s.chestEquipment, s.headEquipment, s.legsEquipment, s.feetEquipment}) {
			if (st == null || st.isEmpty() || tierOf(st) == null) continue;
			var eq = st.get(net.minecraft.core.component.DataComponents.EQUIPPABLE);
			if (eq == null || eq.assetId().isEmpty()) continue;
			String path = eq.assetId().get().identifier().getPath();
			int u = path.lastIndexOf('_');
			if (u < 0) continue;
			String id = path.substring(u + 1);
			for (int i = 0; i < 7; i++) if (ElementArmorParts.IDS[i].equals(id)) return i;
		}
		return -1;
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
		int el = elementOf(s);
		int cls = ClassDecoModel.classOf(s);
		for (int i = 0; i < 7; i++) { elHelm[i].visible = h && el == i; elBack[i].visible = c && el == i && cls != 3; }   // 기사는 망토가 등을 덮음
		hornR.visible = el < 0 || el == 5 || cls == 8; hornL.visible = hornR.visible;   // 굽은 뿔: 어둠(기본)·전사
		if (cls != 0 && cls != 2 && cls != 3 && cls != 8) {   // 천·가죽 복장 직업은 판금 장식 없음
			helm.visible = chest.visible = rpaul.visible = lpaul.visible = tassets.visible = false;
			rknee.visible = lknee.visible = rboot.visible = lboot.visible = false;
			for (int i = 0; i < 7; i++) { elHelm[i].visible = false; elBack[i].visible = false; }
		}
		hat.visible = false;
	}
}
