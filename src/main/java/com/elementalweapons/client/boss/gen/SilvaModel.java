// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class SilvaModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"}
	};

	public SilvaModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-14F, -52F, -10F, 28F, 48F, 20F).texOffs(32, 156).addBox(-5F, -37F, -12F, 10F, 10F, 2F).texOffs(56, 68).addBox(-16F, -53F, -11F, 32F, 10F, 22F).texOffs(0, 102).addBox(-15F, -10F, -11F, 30F, 8F, 22F).texOffs(56, 156).addBox(-17F, -23F, -7F, 4F, 6F, 6F).texOffs(284, 156).addBox(9F, -14F, -12F, 6F, 4F, 2F).texOffs(76, 156).addBox(-10F, -59F, -2F, 4F, 6F, 4F).texOffs(92, 156).addBox(-2F, -59F, -2F, 4F, 6F, 4F).texOffs(108, 156).addBox(6F, -59F, -2F, 4F, 6F, 4F), PartPose.offsetAndRotation(0F, -16F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(344, 0).addBox(-12F, -13F, -10F, 24F, 14F, 20F).texOffs(96, 0).addBox(-22F, -31F, -10F, 44F, 18F, 32F).texOffs(164, 68).addBox(-26F, -38F, -2F, 20F, 12F, 20F).texOffs(248, 0).addBox(2F, -41F, -8F, 24F, 14F, 24F).texOffs(104, 102).addBox(-10F, -45F, -2F, 20F, 10F, 20F).texOffs(264, 102).addBox(-23F, -20F, 0F, 10F, 12F, 16F).texOffs(316, 102).addBox(13F, -20F, 0F, 10F, 12F, 16F).texOffs(124, 156).addBox(-20F, -26F, -14F, 4F, 4F, 4F).texOffs(140, 156).addBox(10F, -40F, -12F, 4F, 4F, 4F).texOffs(156, 156).addBox(18F, -22F, -18F, 4F, 4F, 4F).texOffs(172, 156).addBox(-10F, -42F, 2F, 4F, 4F, 4F).texOffs(188, 156).addBox(-2F, -20F, -21F, 4F, 4F, 4F).texOffs(204, 156).addBox(-24F, -14F, 6F, 4F, 4F, 4F).texOffs(220, 156).addBox(-20F, -4F, -2F, 8F, 4F, 4F).texOffs(244, 156).addBox(12F, -4F, -2F, 8F, 4F, 4F), PartPose.offsetAndRotation(0F, -54F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(244, 68).addBox(-7F, 0F, -6F, 12F, 20F, 12F).texOffs(368, 102).addBox(-10F, -4F, -9F, 18F, 8F, 18F).texOffs(280, 132).addBox(-10F, -7F, -7F, 14F, 4F, 14F), PartPose.offsetAndRotation(-16F, -48F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(184, 102).addBox(-5F, 0F, -5F, 10F, 20F, 10F).texOffs(392, 132).addBox(-6F, 22F, -2F, 4F, 12F, 4F).texOffs(424, 132).addBox(0F, 25F, -2F, 4F, 10F, 4F).texOffs(440, 132).addBox(-4F, 25F, -6F, 8F, 6F, 8F).texOffs(268, 156).addBox(-3F, 5F, -7F, 6F, 6F, 2F), PartPose.offsetAndRotation(-1F, 20F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(292, 68).addBox(-5F, 0F, -6F, 12F, 20F, 12F).texOffs(440, 102).addBox(-8F, -4F, -9F, 18F, 8F, 18F).texOffs(336, 132).addBox(-4F, -7F, -7F, 14F, 4F, 14F), PartPose.offsetAndRotation(16F, -48F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(224, 102).addBox(-5F, 0F, -5F, 10F, 20F, 10F).texOffs(408, 132).addBox(2F, 22F, -2F, 4F, 12F, 4F).texOffs(472, 132).addBox(-4F, 25F, -2F, 4F, 10F, 4F).texOffs(0, 156).addBox(-4F, 25F, -6F, 8F, 6F, 8F), PartPose.offsetAndRotation(1F, 20F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(432, 0).addBox(-7F, 0F, -7F, 14F, 20F, 14F).texOffs(0, 132).addBox(-9F, 3F, -9F, 18F, 6F, 18F), PartPose.offsetAndRotation(-8F, -16F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(340, 68).addBox(-6F, 0F, -6F, 12F, 20F, 12F).texOffs(144, 132).addBox(-8F, 16F, -12F, 16F, 4F, 18F).texOffs(300, 156).addBox(-5.8F, 18F, -15F, 2F, 2F, 4F).texOffs(312, 156).addBox(-1F, 18F, -15F, 2F, 2F, 4F).texOffs(324, 156).addBox(3.8F, 18F, -15F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 20F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 68).addBox(-7F, 0F, -7F, 14F, 20F, 14F).texOffs(72, 132).addBox(-9F, 3F, -9F, 18F, 6F, 18F), PartPose.offsetAndRotation(8F, -16F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(388, 68).addBox(-6F, 0F, -6F, 12F, 20F, 12F).texOffs(212, 132).addBox(-8F, 16F, -12F, 16F, 4F, 18F).texOffs(336, 156).addBox(-5.8F, 18F, -15F, 2F, 2F, 4F).texOffs(348, 156).addBox(-1F, 18F, -15F, 2F, 2F, 4F).texOffs(360, 156).addBox(3.8F, 18F, -15F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 20F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
