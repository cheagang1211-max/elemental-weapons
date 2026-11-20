// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class StormWyvernModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "neck1"},
			{"body", "neck1", "neck2"},
			{"body", "neck1", "neck2", "neck3"},
			{"body", "neck1", "neck2", "neck3", "head"},
			{"body", "neck1", "neck2", "neck3", "head", "jaw"},
			{"body", "neck1", "neck2", "neck3", "head", "hornWL1"},
			{"body", "neck1", "neck2", "neck3", "head", "hornWL1", "hornWL2"},
			{"body", "neck1", "neck2", "neck3", "head", "hornWR1"},
			{"body", "neck1", "neck2", "neck3", "head", "hornWR1", "hornWR2"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"},
			{"body", "tail1", "tail2", "tail3", "tail4"},
			{"body", "tail1", "tail2", "tail3", "tail4", "tail5"},
			{"body", "tail1", "tail2", "tail3", "tail4", "tail5", "tail6"},
			{"body", "tail1", "tail2", "tail3", "tail4", "tail5", "tail6", "tail7"},
			{"body", "right_wing"},
			{"body", "right_wing", "right_wing2"},
			{"body", "left_wing"},
			{"body", "left_wing", "left_wing2"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"}
	};

	public StormWyvernModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-12F, -11F, -20F, 24F, 22F, 40F).texOffs(260, 148).addBox(-9F, 8F, -16F, 18F, 4F, 32F).texOffs(356, 290).addBox(-1F, -15F, -17F, 2F, 4F, 2F).texOffs(364, 290).addBox(-1F, -15.5F, -9F, 2F, 4F, 2F).texOffs(372, 290).addBox(-1F, -16F, -1F, 2F, 4F, 2F).texOffs(212, 274).addBox(-1F, -16.5F, 7F, 2F, 6F, 2F).texOffs(220, 274).addBox(-1F, -17F, 15F, 2F, 6F, 2F), PartPose.offsetAndRotation(0F, -8F, 0F, 0F, 0F, 0F));
		PartDefinition neck1 = body.addOrReplaceChild("neck1", CubeListBuilder.create().texOffs(408, 220).addBox(-6F, -6F, -10F, 12F, 12F, 10F).texOffs(228, 274).addBox(-1F, -10F, -7F, 2F, 4F, 4F), PartPose.offsetAndRotation(0F, -4F, -18F, -0.4363F, 0F, 0F));
		PartDefinition neck2 = neck1.addOrReplaceChild("neck2", CubeListBuilder.create().texOffs(452, 220).addBox(-6F, -6F, -10F, 12F, 12F, 10F).texOffs(240, 274).addBox(-1F, -9.5F, -7F, 2F, 4F, 4F), PartPose.offsetAndRotation(0F, 0F, -10F, 0F, 0F, 0F));
		PartDefinition neck3 = neck2.addOrReplaceChild("neck3", CubeListBuilder.create().texOffs(220, 252).addBox(-5F, -5F, -10F, 10F, 10F, 10F).texOffs(252, 274).addBox(-1F, -9F, -7F, 2F, 4F, 4F), PartPose.offsetAndRotation(0F, 0F, -10F, 0F, 0F, 0F));
		PartDefinition head = neck3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(232, 220).addBox(-8F, -8F, -14F, 16F, 12F, 16F).texOffs(0, 252).addBox(-6F, -4F, -27F, 12F, 8F, 14F).texOffs(380, 290).addBox(-5F, 4F, -26F, 2F, 4F, 2F).texOffs(388, 290).addBox(-1F, 4F, -26F, 2F, 4F, 2F).texOffs(396, 290).addBox(3F, 4F, -26F, 2F, 4F, 2F).texOffs(88, 274).addBox(-2F, -10F, -10F, 4F, 2F, 12F), PartPose.offsetAndRotation(0F, 0F, -10F, 0F, 0F, 0F));
		head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(296, 220).addBox(-6F, 0F, -20F, 12F, 4F, 20F).texOffs(404, 290).addBox(-5F, -4F, -19F, 2F, 4F, 2F).texOffs(412, 290).addBox(-1F, -4F, -19F, 2F, 4F, 2F).texOffs(420, 290).addBox(3F, -4F, -19F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, 4F, -6F, 0F, 0F, 0F));
		PartDefinition hornWL1 = head.addOrReplaceChild("hornWL1", CubeListBuilder.create().texOffs(120, 274).addBox(-2F, -10F, -2F, 4F, 10F, 4F), PartPose.offsetAndRotation(6F, -6F, 2F, -1.0472F, 0F, 0F));
		hornWL1.addOrReplaceChild("hornWL2", CubeListBuilder.create().texOffs(196, 274).addBox(-1F, -8F, -1F, 2F, 8F, 2F), PartPose.offsetAndRotation(0F, -9F, 0F, -0.5236F, 0F, 0F));
		PartDefinition hornWR1 = head.addOrReplaceChild("hornWR1", CubeListBuilder.create().texOffs(136, 274).addBox(-2F, -10F, -2F, 4F, 10F, 4F), PartPose.offsetAndRotation(-6F, -6F, 2F, -1.0472F, 0F, 0F));
		hornWR1.addOrReplaceChild("hornWR2", CubeListBuilder.create().texOffs(204, 274).addBox(-1F, -8F, -1F, 2F, 8F, 2F), PartPose.offsetAndRotation(0F, -9F, 0F, -0.5236F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(360, 220).addBox(-6F, -6F, 0F, 12F, 12F, 12F).texOffs(264, 274).addBox(-1F, -8F, 3F, 2F, 2F, 6F), PartPose.offsetAndRotation(0F, -2F, 18F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(52, 252).addBox(-5F, -5F, 0F, 10F, 10F, 12F).texOffs(280, 274).addBox(-1F, -7.3333F, 3F, 2F, 2F, 6F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(96, 252).addBox(-5F, -5F, 0F, 10F, 10F, 12F).texOffs(296, 274).addBox(-1F, -6.6667F, 3F, 2F, 2F, 6F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(260, 252).addBox(-4F, -4F, 0F, 8F, 8F, 12F).texOffs(312, 274).addBox(-1F, -6F, 3F, 2F, 2F, 6F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition tail5 = tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(300, 252).addBox(-3F, -3F, 0F, 6F, 6F, 12F).texOffs(328, 274).addBox(-1F, -5.3333F, 3F, 2F, 2F, 6F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition tail6 = tail5.addOrReplaceChild("tail6", CubeListBuilder.create().texOffs(336, 252).addBox(-3F, -3F, 0F, 6F, 6F, 12F).texOffs(344, 274).addBox(-1F, -4.6667F, 3F, 2F, 2F, 6F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		tail6.addOrReplaceChild("tail7", CubeListBuilder.create().texOffs(444, 252).addBox(-2F, -2F, 0F, 4F, 4F, 12F).texOffs(360, 274).addBox(-1F, -4F, 3F, 2F, 2F, 6F).texOffs(152, 274).addBox(-6F, -1F, 7F, 12F, 2F, 10F).texOffs(372, 252).addBox(-1F, -8F, 7F, 2F, 8F, 10F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(376, 274).addBox(-50F, -2F, -2F, 50F, 4F, 4F).texOffs(0, 62).addBox(-50F, 0F, 2F, 12F, 2F, 42F).texOffs(84, 186).addBox(-37.5F, 0F, 2F, 12F, 2F, 30F).texOffs(168, 186).addBox(-25F, 0F, 2F, 12F, 2F, 30F).texOffs(108, 62).addBox(-12.5F, 0F, 2F, 12F, 2F, 42F), PartPose.offsetAndRotation(-10F, -8F, -8F, 0F, 0.1745F, -0.4363F));
		right_wing.addOrReplaceChild("right_wing2", CubeListBuilder.create().texOffs(0, 290).addBox(-50F, -2F, -2F, 50F, 4F, 4F).texOffs(128, 0).addBox(-24.5F, 0F, 2F, 2F, 2F, 48F).texOffs(328, 0).addBox(-30.5F, 0F, 2F, 2F, 2F, 44F).texOffs(0, 106).addBox(-36.5F, 0F, 2F, 2F, 2F, 40F).texOffs(368, 106).addBox(-42.5F, 0F, 2F, 2F, 2F, 36F).texOffs(84, 106).addBox(-50F, 0.6F, 2F, 10F, 2F, 40F).texOffs(252, 186).addBox(-40F, 0.6F, 2F, 10F, 2F, 30F).texOffs(0, 148).addBox(-30F, 0.6F, 2F, 10F, 2F, 36F).texOffs(80, 220).addBox(-20F, 0.6F, 2F, 10F, 2F, 28F).texOffs(360, 148).addBox(-10F, 0.6F, 2F, 10F, 2F, 32F).texOffs(108, 290).addBox(-54F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-50F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(124, 290).addBox(0F, -2F, -2F, 50F, 4F, 4F).texOffs(216, 62).addBox(0F, 0F, 2F, 12F, 2F, 42F).texOffs(332, 186).addBox(12.5F, 0F, 2F, 12F, 2F, 30F).texOffs(416, 186).addBox(25F, 0F, 2F, 12F, 2F, 30F).texOffs(324, 62).addBox(37.5F, 0F, 2F, 12F, 2F, 42F), PartPose.offsetAndRotation(10F, -8F, -8F, 0F, -0.1745F, 0.4363F));
		left_wing.addOrReplaceChild("left_wing2", CubeListBuilder.create().texOffs(232, 290).addBox(0F, -2F, -2F, 50F, 4F, 4F).texOffs(228, 0).addBox(22.5F, 0F, 2F, 2F, 2F, 48F).texOffs(420, 0).addBox(28.5F, 0F, 2F, 2F, 2F, 44F).texOffs(184, 106).addBox(34.5F, 0F, 2F, 2F, 2F, 40F).texOffs(92, 148).addBox(40.5F, 0F, 2F, 2F, 2F, 36F).texOffs(268, 106).addBox(0F, 0.6F, 2F, 10F, 2F, 40F).texOffs(0, 220).addBox(10F, 0.6F, 2F, 10F, 2F, 30F).texOffs(168, 148).addBox(20F, 0.6F, 2F, 10F, 2F, 36F).texOffs(156, 220).addBox(30F, 0.6F, 2F, 10F, 2F, 28F).texOffs(0, 186).addBox(40F, 0.6F, 2F, 10F, 2F, 32F).texOffs(340, 290).addBox(50F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(50F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(140, 252).addBox(-5F, 0F, -5F, 10F, 12F, 10F), PartPose.offsetAndRotation(-10F, 0F, 10F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(396, 252).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(0, 274).addBox(-5F, 8F, -9F, 10F, 4F, 12F).texOffs(428, 290).addBox(-4F, 10F, -12F, 2F, 2F, 4F).texOffs(440, 290).addBox(-1F, 10F, -12F, 2F, 2F, 4F).texOffs(452, 290).addBox(2F, 10F, -12F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(180, 252).addBox(-5F, 0F, -5F, 10F, 12F, 10F), PartPose.offsetAndRotation(10F, 0F, 10F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(420, 252).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(44, 274).addBox(-5F, 8F, -9F, 10F, 4F, 12F).texOffs(464, 290).addBox(-4F, 10F, -12F, 2F, 2F, 4F).texOffs(476, 290).addBox(-1F, 10F, -12F, 2F, 2F, 4F).texOffs(488, 290).addBox(2F, 10F, -12F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
