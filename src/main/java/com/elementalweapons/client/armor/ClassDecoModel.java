package com.elementalweapons.client.armor;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import com.elementalweapons.item.PlayerClass;

/** 직업 하나의 입체 복장 모형: 입은 부위만 보이고, 망토·자락은 흔들리고, 룬 고리·광륜·영혼불은 돌아감 */
public class ClassDecoModel extends HumanoidModel<HumanoidRenderState> {
	private record P(ModelPart part, char slot, char anim, int idx) {}
	private final List<P> parts = new ArrayList<>();

	public ClassDecoModel(ModelPart root, int idx) {
		super(root);
		for (String[] e : ClassArmorMesh.PARTS.get(idx)) {
			ModelPart bone = switch (e[0]) {
				case "head" -> head; case "body" -> body; case "right_arm" -> rightArm; case "left_arm" -> leftArm;
				case "right_leg" -> rightLeg; default -> leftLeg;
			};
			String[] k = e[1].split("\\|");
			parts.add(new P(bone.getChild(e[1]), k[0].charAt(0), k[1].charAt(0), Integer.parseInt(k[2])));
		}
	}

	/** 착용한 갑옷 모양 이름(aether_mage_fire)에서 직업을 읽음. 없으면 0 */
	public static int classOf(HumanoidRenderState s) {
		for (ItemStack st : new ItemStack[]{s.chestEquipment, s.headEquipment, s.legsEquipment, s.feetEquipment}) {
			if (st == null || st.isEmpty() || ArmorDecoModel.tierOf(st) == null) continue;
			var eq = st.get(net.minecraft.core.component.DataComponents.EQUIPPABLE);
			if (eq == null || eq.assetId().isEmpty()) continue;
			String[] k = eq.assetId().get().identifier().getPath().split("_");
			if (k.length < 3) continue;
			for (PlayerClass c : PlayerClass.values()) if (c.name().equalsIgnoreCase(k[1])) return c.ordinal();
		}
		return 0;
	}

	private static boolean mod(ItemStack s) { return ArmorDecoModel.tierOf(s) != null; }

	@Override
	public void setupAnim(HumanoidRenderState s) {
		super.setupAnim(s);
		hat.visible = false;
		boolean h = mod(s.headEquipment), c = mod(s.chestEquipment), l = mod(s.legsEquipment), f = mod(s.feetEquipment);
		float walk = Mth.clamp(s.walkAnimationSpeed, 0, 1);
		float t = s.ageInTicks;
		float fwd = Math.min(0, Math.min(rightLeg.xRot, leftLeg.xRot)), back = Math.max(0, Math.max(rightLeg.xRot, leftLeg.xRot));
		for (P p : parts) {
			p.part.visible = switch (p.slot) { case 'h' -> h; case 'c' -> c; case 'l' -> l; default -> f; };
			if (!p.part.visible) continue;
			switch (p.anim) {
				case 'c' -> p.part.xRot += 0.06F + walk * 0.9F + Mth.sin(t * 0.09F) * 0.03F + (s.isCrouching ? 0.3F : 0);
				case 'f' -> p.part.xRot += fwd * 0.75F;
				case 'b' -> p.part.xRot += back * 0.75F;
				case 't' -> { p.part.xRot += walk * 0.7F + Mth.sin(t * 0.13F + p.idx) * 0.06F; p.part.zRot += Mth.sin(t * 0.1F + p.idx * 1.7F) * 0.05F; }
				case 'r' -> p.part.zRot += t * 0.03F;
				case 'R' -> p.part.zRot -= t * 0.05F;
				case 'o' -> { p.part.yRot += t * 0.06F; p.part.y += Mth.sin(t * 0.08F) * 0.8F; }
				default -> {}
			}
		}
	}
}
