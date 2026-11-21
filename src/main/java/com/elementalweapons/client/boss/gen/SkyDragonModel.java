// 자동 생성 파일 (tools/bossgen/mobs2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class SkyDragonModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "neck1"},
			{"body", "neck1", "neck2"},
			{"body", "neck1", "neck2", "neck3"},
			{"body", "neck1", "neck2", "neck3", "head"},
			{"body", "neck1", "neck2", "neck3", "head", "jaw"},
			{"body", "neck1", "neck2", "neck3", "head", "hornDL1"},
			{"body", "neck1", "neck2", "neck3", "head", "hornDL1", "hornDL2"},
			{"body", "neck1", "neck2", "neck3", "head", "hornDR1"},
			{"body", "neck1", "neck2", "neck3", "head", "hornDR1", "hornDR2"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"},
			{"body", "tail1", "tail2", "tail3", "tail4"},
			{"body", "tail1", "tail2", "tail3", "tail4", "tail5"},
			{"body", "tail1", "tail2", "tail3", "tail4", "tail5", "tail6"},
			{"body", "right_wing"},
			{"body", "right_wing", "right_wing2"},
			{"body", "left_wing"},
			{"body", "left_wing", "left_wing2"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"},
			{"front_right_leg"},
			{"front_right_leg", "front_right_leg2"},
			{"front_left_leg"},
			{"front_left_leg", "front_left_leg2"}
	};

	public SkyDragonModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-12F, -11F, -20F, 24F, 22F, 40F).texOffs(252, 102).addBox(-9F, 8F, -16F, 18F, 4F, 32F).texOffs(384, 266).addBox(-1F, -15F, 11F, 2F, 4F, 2F).texOffs(392, 266).addBox(-1F, -16F, 14F, 2F, 4F, 2F).texOffs(256, 250).addBox(-1F, -17F, 17F, 2F, 6F, 2F).texOffs(208, 204).addBox(-9F, -14.4F, -11F, 18F, 4F, 18F).texOffs(264, 250).addBox(-8F, -17.6F, 4.4F, 16F, 4F, 4F).texOffs(400, 266).addBox(-2F, -17.2F, -11.4F, 4F, 4F, 2F).texOffs(488, 174).addBox(-13.4F, -12F, -5F, 2F, 18F, 6F).texOffs(0, 204).addBox(11.4F, -12F, -5F, 2F, 18F, 6F).texOffs(304, 250).addBox(-14.2F, 4F, -4F, 2F, 4F, 4F).texOffs(316, 250).addBox(12.2F, 4F, -4F, 2F, 4F, 4F).texOffs(16, 204).addBox(-12F, -14.8F, -10F, 4F, 8F, 16F).texOffs(56, 204).addBox(8F, -14.8F, -10F, 4F, 8F, 16F), PartPose.offsetAndRotation(0F, -8F, 0F, 0F, 0F, 0F));
		PartDefinition neck1 = body.addOrReplaceChild("neck1", CubeListBuilder.create().texOffs(280, 204).addBox(-6F, -6F, -10F, 12F, 12F, 10F).texOffs(328, 250).addBox(-1F, -10F, -7F, 2F, 4F, 4F), PartPose.offsetAndRotation(0F, -4F, -18F, -0.5236F, 0F, 0F));
		PartDefinition neck2 = neck1.addOrReplaceChild("neck2", CubeListBuilder.create().texOffs(324, 204).addBox(-6F, -6F, -10F, 12F, 12F, 10F).texOffs(340, 250).addBox(-1F, -9.5F, -7F, 2F, 4F, 4F), PartPose.offsetAndRotation(0F, 0F, -10F, 0F, 0F, 0F));
		PartDefinition neck3 = neck2.addOrReplaceChild("neck3", CubeListBuilder.create().texOffs(40, 228).addBox(-5F, -5F, -10F, 10F, 10F, 10F).texOffs(352, 250).addBox(-1F, -9F, -7F, 2F, 4F, 4F), PartPose.offsetAndRotation(0F, 0F, -10F, 0F, 0F, 0F));
		PartDefinition head = neck3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(296, 174).addBox(-8F, -8F, -14F, 16F, 12F, 16F).texOffs(368, 204).addBox(-6F, -4F, -27F, 12F, 8F, 14F).texOffs(412, 266).addBox(-5F, 4F, -26F, 2F, 4F, 2F).texOffs(420, 266).addBox(-1F, 4F, -26F, 2F, 4F, 2F).texOffs(428, 266).addBox(3F, 4F, -26F, 2F, 4F, 2F).texOffs(88, 250).addBox(-2F, -10F, -10F, 4F, 2F, 12F).texOffs(152, 250).addBox(-9.8F, -2F, -8F, 2F, 4F, 8F).texOffs(172, 250).addBox(7.8F, -2F, -8F, 2F, 4F, 8F), PartPose.offsetAndRotation(0F, 0F, -10F, 0F, 0F, 0F));
		head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(96, 204).addBox(-6F, 0F, -20F, 12F, 4F, 20F).texOffs(436, 266).addBox(-5F, -4F, -19F, 2F, 4F, 2F).texOffs(444, 266).addBox(-1F, -4F, -19F, 2F, 4F, 2F).texOffs(452, 266).addBox(3F, -4F, -19F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, 4F, -6F, 0F, 0F, 0F));
		PartDefinition hornDL1 = head.addOrReplaceChild("hornDL1", CubeListBuilder.create().texOffs(120, 250).addBox(-2F, -10F, -2F, 4F, 10F, 4F), PartPose.offsetAndRotation(6F, -6F, 2F, -1.0472F, 0F, 0F));
		hornDL1.addOrReplaceChild("hornDL2", CubeListBuilder.create().texOffs(240, 250).addBox(-1F, -8F, -1F, 2F, 8F, 2F), PartPose.offsetAndRotation(0F, -9F, 0F, -0.5236F, 0F, 0F));
		PartDefinition hornDR1 = head.addOrReplaceChild("hornDR1", CubeListBuilder.create().texOffs(136, 250).addBox(-2F, -10F, -2F, 4F, 10F, 4F), PartPose.offsetAndRotation(-6F, -6F, 2F, -1.0472F, 0F, 0F));
		hornDR1.addOrReplaceChild("hornDR2", CubeListBuilder.create().texOffs(248, 250).addBox(-1F, -8F, -1F, 2F, 8F, 2F), PartPose.offsetAndRotation(0F, -9F, 0F, -0.5236F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(160, 204).addBox(-6F, -6F, 0F, 12F, 12F, 12F).texOffs(364, 250).addBox(-1F, -8F, 3F, 2F, 2F, 6F), PartPose.offsetAndRotation(0F, -2F, 18F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(420, 204).addBox(-5F, -5F, 0F, 10F, 10F, 12F).texOffs(380, 250).addBox(-1F, -7.2F, 3F, 2F, 2F, 6F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(80, 228).addBox(-4F, -4F, 0F, 8F, 8F, 12F).texOffs(396, 250).addBox(-1F, -6.4F, 3F, 2F, 2F, 6F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(120, 228).addBox(-4F, -4F, 0F, 8F, 8F, 12F).texOffs(412, 250).addBox(-1F, -5.6F, 3F, 2F, 2F, 6F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition tail5 = tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(224, 228).addBox(-3F, -3F, 0F, 6F, 6F, 12F).texOffs(428, 250).addBox(-1F, -4.8F, 3F, 2F, 2F, 6F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		tail5.addOrReplaceChild("tail6", CubeListBuilder.create().texOffs(380, 228).addBox(-2F, -2F, 0F, 4F, 4F, 12F).texOffs(444, 250).addBox(-1F, -4F, 3F, 2F, 2F, 6F).texOffs(192, 250).addBox(-7F, -1F, 5F, 14F, 2F, 10F).texOffs(260, 228).addBox(-1F, -8F, 5F, 2F, 8F, 10F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 266).addBox(-40F, -2F, -2F, 40F, 4F, 4F).texOffs(0, 62).addBox(-40F, 0F, 2F, 10F, 2F, 38F).texOffs(300, 140).addBox(-30F, 0F, 2F, 10F, 2F, 28F).texOffs(376, 140).addBox(-20F, 0F, 2F, 10F, 2F, 28F).texOffs(96, 62).addBox(-10F, 0F, 2F, 10F, 2F, 38F), PartPose.offsetAndRotation(-10F, -8F, -6F, 0F, 0.1745F, -0.3491F));
		right_wing.addOrReplaceChild("right_wing2", CubeListBuilder.create().texOffs(88, 266).addBox(-40F, -2F, -2F, 40F, 4F, 4F).texOffs(128, 0).addBox(-19.8F, 0F, 2F, 2F, 2F, 44F).texOffs(312, 0).addBox(-24.6F, 0F, 2F, 2F, 2F, 40F).texOffs(384, 62).addBox(-29.4F, 0F, 2F, 2F, 2F, 36F).texOffs(352, 102).addBox(-34.2F, 0F, 2F, 2F, 2F, 32F).texOffs(0, 102).addBox(-40F, 0.6F, 2F, 8F, 2F, 36F).texOffs(0, 174).addBox(-32F, 0.6F, 2F, 8F, 2F, 28F).texOffs(420, 102).addBox(-24F, 0.6F, 2F, 8F, 2F, 32F).texOffs(360, 174).addBox(-16F, 0.6F, 2F, 8F, 2F, 24F).texOffs(148, 140).addBox(-8F, 0.6F, 2F, 8F, 2F, 30F).texOffs(176, 266).addBox(-44F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-40F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(192, 266).addBox(0F, -2F, -2F, 40F, 4F, 4F).texOffs(192, 62).addBox(0F, 0F, 2F, 10F, 2F, 38F).texOffs(72, 174).addBox(10F, 0F, 2F, 10F, 2F, 28F).texOffs(148, 174).addBox(20F, 0F, 2F, 10F, 2F, 28F).texOffs(288, 62).addBox(30F, 0F, 2F, 10F, 2F, 38F), PartPose.offsetAndRotation(10F, -8F, -6F, 0F, -0.1745F, 0.3491F));
		left_wing.addOrReplaceChild("left_wing2", CubeListBuilder.create().texOffs(280, 266).addBox(0F, -2F, -2F, 40F, 4F, 4F).texOffs(220, 0).addBox(17.8F, 0F, 2F, 2F, 2F, 44F).texOffs(396, 0).addBox(22.6F, 0F, 2F, 2F, 2F, 40F).texOffs(88, 102).addBox(27.4F, 0F, 2F, 2F, 2F, 36F).texOffs(0, 140).addBox(32.2F, 0F, 2F, 2F, 2F, 32F).texOffs(164, 102).addBox(0F, 0.6F, 2F, 8F, 2F, 36F).texOffs(224, 174).addBox(8F, 0.6F, 2F, 8F, 2F, 28F).texOffs(68, 140).addBox(16F, 0.6F, 2F, 8F, 2F, 32F).texOffs(424, 174).addBox(24F, 0.6F, 2F, 8F, 2F, 24F).texOffs(224, 140).addBox(32F, 0.6F, 2F, 8F, 2F, 30F).texOffs(368, 266).addBox(40F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(40F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(464, 204).addBox(-5F, 0F, -5F, 10F, 12F, 10F), PartPose.offsetAndRotation(-10F, 0F, 12F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(284, 228).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(412, 228).addBox(-5F, 8F, -9F, 10F, 4F, 12F).texOffs(460, 266).addBox(-4F, 10F, -12F, 2F, 2F, 4F).texOffs(472, 266).addBox(-1F, 10F, -12F, 2F, 2F, 4F).texOffs(484, 266).addBox(2F, 10F, -12F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 228).addBox(-5F, 0F, -5F, 10F, 12F, 10F), PartPose.offsetAndRotation(10F, 0F, 12F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(308, 228).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(456, 228).addBox(-5F, 8F, -9F, 10F, 4F, 12F).texOffs(496, 266).addBox(-4F, 10F, -12F, 2F, 2F, 4F).texOffs(0, 274).addBox(-1F, 10F, -12F, 2F, 2F, 4F).texOffs(12, 274).addBox(2F, 10F, -12F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition front_right_leg = root.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(160, 228).addBox(-4F, 0F, -4F, 8F, 12F, 8F), PartPose.offsetAndRotation(-10F, 0F, -12F, 0F, 0F, 0F));
		front_right_leg.addOrReplaceChild("front_right_leg2", CubeListBuilder.create().texOffs(332, 228).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(0, 250).addBox(-5F, 8F, -9F, 10F, 4F, 12F).texOffs(24, 274).addBox(-4F, 10F, -12F, 2F, 2F, 4F).texOffs(36, 274).addBox(-1F, 10F, -12F, 2F, 2F, 4F).texOffs(48, 274).addBox(2F, 10F, -12F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition front_left_leg = root.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(192, 228).addBox(-4F, 0F, -4F, 8F, 12F, 8F), PartPose.offsetAndRotation(10F, 0F, -12F, 0F, 0F, 0F));
		front_left_leg.addOrReplaceChild("front_left_leg2", CubeListBuilder.create().texOffs(356, 228).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(44, 250).addBox(-5F, 8F, -9F, 10F, 4F, 12F).texOffs(60, 274).addBox(-4F, 10F, -12F, 2F, 2F, 4F).texOffs(72, 274).addBox(-1F, 10F, -12F, 2F, 2F, 4F).texOffs(84, 274).addBox(2F, 10F, -12F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
