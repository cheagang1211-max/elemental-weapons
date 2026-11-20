// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class ZephyrosModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "head", "swayC1"},
			{"body", "head", "swayC1", "swayC2"},
			{"body", "head", "swayC1", "swayC2", "swayC3"},
			{"body", "head", "swayC1", "swayC2", "swayC3", "swayC4"},
			{"body", "right_wing"},
			{"body", "right_wing", "right_wing2"},
			{"body", "left_wing"},
			{"body", "left_wing", "left_wing2"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"}
	};

	public ZephyrosModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-12F, -12F, -18F, 24F, 24F, 36F).texOffs(196, 130).addBox(-10F, 2F, -16F, 20F, 12F, 16F).texOffs(188, 188).addBox(-8F, -10F, -20F, 16F, 12F, 2F).texOffs(96, 226).addBox(-2F, -6F, -21F, 4F, 4F, 2F).texOffs(404, 130).addBox(-10F, -14F, -8F, 20F, 2F, 24F), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(372, 96).addBox(-8F, -14F, -14F, 16F, 16F, 16F).texOffs(124, 188).addBox(-3F, -5F, -23F, 6F, 6F, 10F).texOffs(164, 212).addBox(-2F, -1F, -24F, 4F, 6F, 2F).texOffs(108, 212).addBox(-6F, -16F, -10F, 12F, 2F, 8F).texOffs(108, 226).addBox(-2F, -19F, -11F, 4F, 4F, 2F), PartPose.offsetAndRotation(0F, -8F, -18F, 0F, 0F, 0F));
		PartDefinition swayC1 = head.addOrReplaceChild("swayC1", CubeListBuilder.create().texOffs(224, 188).addBox(-2F, -4F, 0F, 4F, 8F, 6F), PartPose.offsetAndRotation(0F, -12F, 4F, 0.5236F, 0F, 0F));
		PartDefinition swayC2 = swayC1.addOrReplaceChild("swayC2", CubeListBuilder.create().texOffs(72, 212).addBox(-2F, -3F, 0F, 4F, 6F, 6F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition swayC3 = swayC2.addOrReplaceChild("swayC3", CubeListBuilder.create().texOffs(92, 212).addBox(-1F, -3F, 0F, 2F, 6F, 6F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		swayC3.addOrReplaceChild("swayC4", CubeListBuilder.create().texOffs(148, 212).addBox(-1F, -2F, 0F, 2F, 4F, 6F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(176, 212).addBox(-44F, -2F, -2F, 44F, 4F, 4F).texOffs(0, 60).addBox(-44F, 0F, 2F, 12F, 2F, 34F).texOffs(0, 162).addBox(-33F, 0F, 2F, 12F, 2F, 24F).texOffs(72, 162).addBox(-22F, 0F, 2F, 12F, 2F, 24F).texOffs(92, 60).addBox(-11F, 0F, 2F, 12F, 2F, 34F), PartPose.offsetAndRotation(-12F, -10F, -4F, 0F, 0F, -0.1745F));
		right_wing.addOrReplaceChild("right_wing2", CubeListBuilder.create().texOffs(272, 212).addBox(-44F, -2F, -2F, 44F, 4F, 4F).texOffs(120, 0).addBox(-21.68F, 0F, 2F, 2F, 2F, 40F).texOffs(288, 0).addBox(-26.96F, 0F, 2F, 2F, 2F, 36F).texOffs(368, 60).addBox(-32.24F, 0F, 2F, 2F, 2F, 32F).texOffs(76, 130).addBox(-37.52F, 0F, 2F, 2F, 2F, 28F).texOffs(0, 96).addBox(-44F, 0.6F, 2F, 8F, 2F, 32F).texOffs(144, 162).addBox(-35.2F, 0.6F, 2F, 8F, 2F, 24F).texOffs(436, 96).addBox(-26.4F, 0.6F, 2F, 8F, 2F, 30F).texOffs(416, 162).addBox(-17.6F, 0.6F, 2F, 8F, 2F, 22F).texOffs(268, 130).addBox(-8.8F, 0.6F, 2F, 8F, 2F, 26F).texOffs(80, 96).addBox(-44F, 1F, 4F, 4F, 2F, 32F), PartPose.offsetAndRotation(-44F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(368, 212).addBox(0F, -2F, -2F, 44F, 4F, 4F).texOffs(184, 60).addBox(0F, 0F, 2F, 12F, 2F, 34F).texOffs(208, 162).addBox(11F, 0F, 2F, 12F, 2F, 24F).texOffs(280, 162).addBox(22F, 0F, 2F, 12F, 2F, 24F).texOffs(276, 60).addBox(33F, 0F, 2F, 12F, 2F, 34F), PartPose.offsetAndRotation(12F, -10F, -4F, 0F, 0F, 0.1745F));
		left_wing.addOrReplaceChild("left_wing2", CubeListBuilder.create().texOffs(0, 226).addBox(0F, -2F, -2F, 44F, 4F, 4F).texOffs(204, 0).addBox(19.68F, 0F, 2F, 2F, 2F, 40F).texOffs(364, 0).addBox(24.96F, 0F, 2F, 2F, 2F, 36F).texOffs(152, 96).addBox(30.24F, 0F, 2F, 2F, 2F, 32F).texOffs(136, 130).addBox(35.52F, 0F, 2F, 2F, 2F, 28F).texOffs(220, 96).addBox(0F, 0.6F, 2F, 8F, 2F, 32F).texOffs(352, 162).addBox(8.8F, 0.6F, 2F, 8F, 2F, 24F).texOffs(0, 130).addBox(17.6F, 0.6F, 2F, 8F, 2F, 30F).texOffs(0, 188).addBox(26.4F, 0.6F, 2F, 8F, 2F, 22F).texOffs(336, 130).addBox(35.2F, 0.6F, 2F, 8F, 2F, 26F).texOffs(300, 96).addBox(40F, 1F, 4F, 4F, 2F, 32F), PartPose.offsetAndRotation(44F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(244, 188).addBox(-10F, -1F, 0F, 20F, 2F, 12F).texOffs(308, 188).addBox(-4F, -1F, 0F, 8F, 2F, 12F), PartPose.offsetAndRotation(0F, 0F, 18F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(348, 188).addBox(-12F, -1F, 0F, 24F, 2F, 12F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(420, 188).addBox(-14F, -1F, 0F, 28F, 2F, 12F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(60, 188).addBox(-4F, 0F, -4F, 8F, 12F, 8F), PartPose.offsetAndRotation(-6F, 0F, 4F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(156, 188).addBox(-2F, 0F, -2F, 4F, 12F, 4F).texOffs(0, 212).addBox(-4F, 8F, -8F, 8F, 4F, 10F).texOffs(120, 226).addBox(-4F, 10F, -11F, 2F, 2F, 4F).texOffs(132, 226).addBox(-1F, 10F, -11F, 2F, 2F, 4F).texOffs(144, 226).addBox(2F, 10F, -11F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(92, 188).addBox(-4F, 0F, -4F, 8F, 12F, 8F), PartPose.offsetAndRotation(6F, 0F, 4F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(172, 188).addBox(-2F, 0F, -2F, 4F, 12F, 4F).texOffs(36, 212).addBox(-4F, 8F, -8F, 8F, 4F, 10F).texOffs(156, 226).addBox(-4F, 10F, -11F, 2F, 2F, 4F).texOffs(168, 226).addBox(-1F, 10F, -11F, 2F, 2F, 4F).texOffs(180, 226).addBox(2F, 10F, -11F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
