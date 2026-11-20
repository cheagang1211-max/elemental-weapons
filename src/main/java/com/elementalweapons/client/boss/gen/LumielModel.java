// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class LumielModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "halo"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"body", "right_wing"},
			{"body", "right_wing", "right_wing2"},
			{"body", "left_wing"},
			{"body", "left_wing", "left_wing2"},
			{"body", "swayS1"},
			{"body", "swayS1", "swayS2"},
			{"body", "swayS1", "swayS2", "swayS3"},
			{"body", "cape1"},
			{"body", "cape1", "cape2"},
			{"body", "cape1", "cape2", "cape3"},
			{"body", "cape1", "cape2", "cape3", "cape4"},
			{"body", "cape1", "cape2", "cape3", "cape4", "cape5"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"}
	};

	public LumielModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6F, -22F, -3.5F, 12F, 16F, 7F).texOffs(126, 93).addBox(-3F, -20F, -4.3F, 6F, 6F, 1F).texOffs(44, 108).addBox(-1F, -18F, -4.8F, 2F, 2F, 1F).texOffs(100, 80).addBox(-6.5F, -7.5F, -4F, 13F, 3F, 8F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(34, 63).addBox(-4F, -8F, -4F, 8F, 8F, 8F).texOffs(78, 80).addBox(-1F, -10F, -4.5F, 2F, 3F, 9F).texOffs(52, 93).addBox(-5F, -6.5F, -1.5F, 1F, 3F, 5F).texOffs(64, 93).addBox(4F, -6.5F, -1.5F, 1F, 3F, 5F).texOffs(180, 93).addBox(-6F, -9F, 1F, 1F, 4F, 2F).texOffs(186, 93).addBox(5F, -9F, 1F, 1F, 4F, 2F), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(96, 108).addBox(-6F, -6.5F, -0.5F, 12F, 1F, 1F).texOffs(122, 108).addBox(-6F, 5.5F, -0.5F, 12F, 1F, 1F).texOffs(138, 63).addBox(-6.5F, -6F, -0.5F, 1F, 12F, 1F).texOffs(142, 63).addBox(5.5F, -6F, -0.5F, 1F, 12F, 1F).texOffs(50, 108).addBox(-5.5F, -5.5F, -0.5F, 2F, 2F, 1F).texOffs(56, 108).addBox(3.5F, -5.5F, -0.5F, 2F, 2F, 1F).texOffs(62, 108).addBox(-5.5F, 3.5F, -0.5F, 2F, 2F, 1F).texOffs(68, 108).addBox(3.5F, 3.5F, -0.5F, 2F, 2F, 1F), PartPose.offsetAndRotation(0F, -33F, 3F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(146, 63).addBox(-2.5F, 0F, -2F, 4F, 9F, 4F).texOffs(142, 80).addBox(-4F, -2F, -3.5F, 7F, 4F, 7F).texOffs(140, 93).addBox(-4F, -3.5F, -2.5F, 5F, 2F, 5F), PartPose.offsetAndRotation(-7F, -22F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(162, 63).addBox(-2F, 0F, -2F, 4F, 9F, 4F).texOffs(76, 102).addBox(-1F, 10.5F, -4F, 2F, 3F, 2F).texOffs(74, 108).addBox(-3.5F, 13.5F, -4F, 7F, 1F, 2F).texOffs(130, 0).addBox(-1.5F, 15F, -3.5F, 3F, 20F, 1F).texOffs(92, 108).addBox(-0.5F, 35F, -3.5F, 1F, 2F, 1F).texOffs(76, 93).addBox(-2.5F, 9F, -2.5F, 5F, 3F, 5F), PartPose.offsetAndRotation(-0.5F, 9F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(178, 63).addBox(-1.5F, 0F, -2F, 4F, 9F, 4F).texOffs(170, 80).addBox(-3F, -2F, -3.5F, 7F, 4F, 7F).texOffs(160, 93).addBox(-1F, -3.5F, -2.5F, 5F, 2F, 5F), PartPose.offsetAndRotation(7F, -22F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(194, 63).addBox(-2F, 0F, -2F, 4F, 9F, 4F).texOffs(190, 44).addBox(2F, 0F, -4F, 1F, 10F, 8F).texOffs(96, 93).addBox(2.5F, 3F, -2F, 1F, 4F, 4F).texOffs(106, 93).addBox(-2.5F, 9F, -2.5F, 5F, 3F, 5F), PartPose.offsetAndRotation(0.5F, 9F, 0F, 0F, 0F, 0F));
		PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(84, 102).addBox(-20F, -1F, -1F, 20F, 2F, 2F).texOffs(138, 0).addBox(-20F, 0F, 1F, 20F, 1F, 20F), PartPose.offsetAndRotation(-4F, -20F, 4F, -0.1745F, -0.4363F, -0.6109F));
		right_wing.addOrReplaceChild("right_wing2", CubeListBuilder.create().texOffs(128, 102).addBox(-20F, -1F, -1F, 20F, 2F, 2F).texOffs(38, 0).addBox(-9.9F, 0F, 1F, 1F, 1F, 22F).texOffs(0, 23).addBox(-12.3F, 0F, 1F, 1F, 1F, 20F).texOffs(164, 23).addBox(-14.7F, 0F, 1F, 1F, 1F, 18F).texOffs(208, 44).addBox(-17.1F, 0F, 1F, 1F, 1F, 16F).texOffs(0, 44).addBox(-20F, 0.3F, 1F, 20F, 1F, 18F), PartPose.offsetAndRotation(-20F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(172, 102).addBox(0F, -1F, -1F, 20F, 2F, 2F).texOffs(42, 23).addBox(0F, 0F, 1F, 20F, 1F, 20F), PartPose.offsetAndRotation(4F, -20F, 4F, -0.1745F, 0.4363F, 0.6109F));
		left_wing.addOrReplaceChild("left_wing2", CubeListBuilder.create().texOffs(0, 108).addBox(0F, -1F, -1F, 20F, 2F, 2F).texOffs(84, 0).addBox(8.9F, 0F, 1F, 1F, 1F, 22F).texOffs(122, 23).addBox(11.3F, 0F, 1F, 1F, 1F, 20F).texOffs(76, 44).addBox(13.7F, 0F, 1F, 1F, 1F, 18F).texOffs(0, 63).addBox(16.1F, 0F, 1F, 1F, 1F, 16F).texOffs(114, 44).addBox(0F, 0.3F, 1F, 20F, 1F, 18F), PartPose.offsetAndRotation(20F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition swayS1 = body.addOrReplaceChild("swayS1", CubeListBuilder.create().texOffs(210, 63).addBox(-6F, 0F, -4F, 12F, 5F, 8F), PartPose.offsetAndRotation(0F, -6F, -1F, 0F, 0F, 0F));
		PartDefinition swayS2 = swayS1.addOrReplaceChild("swayS2", CubeListBuilder.create().texOffs(0, 80).addBox(-6F, 0F, -4F, 12F, 5F, 8F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		swayS2.addOrReplaceChild("swayS3", CubeListBuilder.create().texOffs(40, 80).addBox(-5.5F, 0F, -4F, 11F, 5F, 8F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition cape1 = body.addOrReplaceChild("cape1", CubeListBuilder.create().texOffs(192, 93).addBox(-6F, 0F, -0.5F, 12F, 5F, 1F), PartPose.offsetAndRotation(0F, -20F, 4.5F, 0F, 0F, 0F));
		PartDefinition cape2 = cape1.addOrReplaceChild("cape2", CubeListBuilder.create().texOffs(218, 93).addBox(-6F, 0F, -0.5F, 12F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition cape3 = cape2.addOrReplaceChild("cape3", CubeListBuilder.create().texOffs(0, 102).addBox(-6F, 0F, -0.5F, 12F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition cape4 = cape3.addOrReplaceChild("cape4", CubeListBuilder.create().texOffs(26, 102).addBox(-6F, 0F, -0.5F, 12F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		cape4.addOrReplaceChild("cape5", CubeListBuilder.create().texOffs(52, 102).addBox(-5.5F, 0F, -0.5F, 11F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(66, 63).addBox(-2.5F, 0F, -2.5F, 5F, 10F, 5F).texOffs(198, 80).addBox(-3.5F, 0F, -3.5F, 7F, 4F, 7F), PartPose.offsetAndRotation(-3F, -5F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(106, 63).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(0, 93).addBox(-3F, 8F, -5F, 6F, 2F, 7F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(86, 63).addBox(-2.5F, 0F, -2.5F, 5F, 10F, 5F).texOffs(226, 80).addBox(-3.5F, 0F, -3.5F, 7F, 4F, 7F), PartPose.offsetAndRotation(3F, -5F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(122, 63).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(26, 93).addBox(-3F, 8F, -5F, 6F, 2F, 7F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
