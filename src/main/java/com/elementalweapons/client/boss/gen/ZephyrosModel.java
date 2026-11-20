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
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6F, -6F, -9F, 12F, 12F, 18F).texOffs(128, 66).addBox(-5F, 1F, -8F, 10F, 6F, 8F).texOffs(16, 83).addBox(-4F, -5F, -10F, 8F, 6F, 1F).texOffs(198, 91).addBox(-1F, -3F, -10.5F, 2F, 2F, 1F).texOffs(164, 66).addBox(-5F, -7F, -4F, 10F, 1F, 12F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(36, 66).addBox(-4F, -7F, -7F, 8F, 8F, 8F).texOffs(240, 66).addBox(-1.5F, -2.5F, -11.5F, 3F, 3F, 5F).texOffs(0, 91).addBox(-1F, -0.5F, -12F, 2F, 3F, 1F).texOffs(226, 83).addBox(-3F, -8F, -5F, 6F, 1F, 4F).texOffs(204, 91).addBox(-1F, -9.5F, -5.5F, 2F, 2F, 1F), PartPose.offsetAndRotation(0F, -4F, -9F, 0F, 0F, 0F));
		PartDefinition swayC1 = head.addOrReplaceChild("swayC1", CubeListBuilder.create().texOffs(34, 83).addBox(-1F, -2F, 0F, 2F, 4F, 3F), PartPose.offsetAndRotation(0F, -6F, 2F, 0.5236F, 0F, 0F));
		PartDefinition swayC2 = swayC1.addOrReplaceChild("swayC2", CubeListBuilder.create().texOffs(208, 83).addBox(-1F, -1.5F, 0F, 2F, 3F, 3F), PartPose.offsetAndRotation(0F, 0F, 3F, 0F, 0F, 0F));
		PartDefinition swayC3 = swayC2.addOrReplaceChild("swayC3", CubeListBuilder.create().texOffs(218, 83).addBox(-0.5F, -1.5F, 0F, 1F, 3F, 3F), PartPose.offsetAndRotation(0F, 0F, 3F, 0F, 0F, 0F));
		swayC3.addOrReplaceChild("swayC4", CubeListBuilder.create().texOffs(246, 83).addBox(-0.5F, -1F, 0F, 1F, 2F, 3F), PartPose.offsetAndRotation(0F, 0F, 3F, 0F, 0F, 0F));
		PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(6, 91).addBox(-22F, -1F, -1F, 22F, 2F, 2F).texOffs(144, 0).addBox(-22F, 0F, 1F, 22F, 1F, 18F), PartPose.offsetAndRotation(-6F, -5F, -2F, 0F, 0F, -0.1745F));
		right_wing.addOrReplaceChild("right_wing2", CubeListBuilder.create().texOffs(54, 91).addBox(-22F, -1F, -1F, 22F, 2F, 2F).texOffs(60, 0).addBox(-10.84F, 0F, 1F, 1F, 1F, 20F).texOffs(0, 30).addBox(-13.48F, 0F, 1F, 1F, 1F, 18F).texOffs(156, 30).addBox(-16.12F, 0F, 1F, 1F, 1F, 16F).texOffs(68, 66).addBox(-18.76F, 0F, 1F, 1F, 1F, 14F).texOffs(0, 49).addBox(-22F, 0.3F, 1F, 22F, 1F, 16F).texOffs(76, 49).addBox(-22F, 0.5F, 2F, 2F, 1F, 16F), PartPose.offsetAndRotation(-22F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(102, 91).addBox(0F, -1F, -1F, 22F, 2F, 2F).texOffs(38, 30).addBox(0F, 0F, 1F, 22F, 1F, 18F), PartPose.offsetAndRotation(6F, -5F, -2F, 0F, 0F, 0.1745F));
		left_wing.addOrReplaceChild("left_wing2", CubeListBuilder.create().texOffs(150, 91).addBox(0F, -1F, -1F, 22F, 2F, 2F).texOffs(102, 0).addBox(9.84F, 0F, 1F, 1F, 1F, 20F).texOffs(118, 30).addBox(12.48F, 0F, 1F, 1F, 1F, 18F).texOffs(112, 49).addBox(15.12F, 0F, 1F, 1F, 1F, 16F).texOffs(98, 66).addBox(17.76F, 0F, 1F, 1F, 1F, 14F).texOffs(146, 49).addBox(0F, 0.3F, 1F, 22F, 1F, 16F).texOffs(0, 66).addBox(20F, 0.5F, 2F, 2F, 1F, 16F), PartPose.offsetAndRotation(22F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(44, 83).addBox(-5F, -0.5F, 0F, 10F, 1F, 6F).texOffs(76, 83).addBox(-2F, -0.5F, 0F, 4F, 1F, 6F), PartPose.offsetAndRotation(0F, 0F, 9F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(96, 83).addBox(-6F, -0.5F, 0F, 12F, 1F, 6F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(132, 83).addBox(-7F, -0.5F, 0F, 14F, 1F, 6F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(208, 66).addBox(-2F, 0F, -2F, 4F, 6F, 4F), PartPose.offsetAndRotation(-3F, 12F, 2F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(0, 83).addBox(-1F, 0F, -1F, 2F, 6F, 2F).texOffs(172, 83).addBox(-2F, 4F, -4F, 4F, 2F, 5F).texOffs(210, 91).addBox(-2F, 5F, -5.5F, 1F, 1F, 2F).texOffs(216, 91).addBox(-0.5F, 5F, -5.5F, 1F, 1F, 2F).texOffs(222, 91).addBox(1F, 5F, -5.5F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(224, 66).addBox(-2F, 0F, -2F, 4F, 6F, 4F), PartPose.offsetAndRotation(3F, 12F, 2F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(8, 83).addBox(-1F, 0F, -1F, 2F, 6F, 2F).texOffs(190, 83).addBox(-2F, 4F, -4F, 4F, 2F, 5F).texOffs(228, 91).addBox(-2F, 5F, -5.5F, 1F, 1F, 2F).texOffs(234, 91).addBox(-0.5F, 5F, -5.5F, 1F, 1F, 2F).texOffs(240, 91).addBox(1F, 5F, -5.5F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
