// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class CrystalGolemModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"body", "pulse_core"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"}
	};

	public CrystalGolemModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-22F, -48F, -14F, 44F, 36F, 28F).texOffs(384, 136).addBox(-8F, -38F, -16F, 16F, 16F, 2F).texOffs(304, 102).addBox(-12F, -42F, -15F, 24F, 24F, 2F).texOffs(192, 64).addBox(-16F, -16F, -12F, 32F, 12F, 24F).texOffs(144, 0).addBox(-24F, -52F, -15F, 48F, 8F, 30F).texOffs(84, 136).addBox(-15F, -66.4F, 5F, 6F, 16F, 6F).texOffs(420, 136).addBox(-9.4F, -61.6F, 7F, 6F, 12F, 6F).texOffs(224, 162).addBox(-18.2F, -58.2F, 4F, 4F, 8F, 4F).texOffs(356, 102).addBox(9F, -71.6F, 3F, 6F, 20F, 6F).texOffs(444, 136).addBox(15.4F, -64.4F, 5F, 6F, 12F, 6F).texOffs(48, 162).addBox(5.2F, -61.8F, 2F, 4F, 10F, 4F).texOffs(468, 136).addBox(-2F, -66.2F, 10F, 4F, 14F, 4F).texOffs(64, 162).addBox(2.8F, -61.8F, 12F, 4F, 10F, 4F).texOffs(240, 162).addBox(-5.6F, -59.6F, 8F, 4F, 8F, 4F), PartPose.offsetAndRotation(0F, -20F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(304, 64).addBox(-10F, -14F, -11F, 20F, 16F, 18F).texOffs(108, 136).addBox(-12F, -17F, -9F, 24F, 4F, 18F).texOffs(484, 136).addBox(-2F, -28F, 0F, 4F, 12F, 4F).texOffs(256, 162).addBox(2F, -24F, 2F, 4F, 8F, 4F).texOffs(328, 162).addBox(-5F, -22F, -2F, 4F, 6F, 4F).texOffs(344, 162).addBox(-9F, -22.2F, 3F, 2F, 8F, 2F).texOffs(384, 162).addBox(-6.2F, -19.8F, 5F, 2F, 6F, 2F).texOffs(392, 162).addBox(-12.1F, -18.1F, 0F, 4F, 4F, 4F), PartPose.offsetAndRotation(0F, -54F, -10F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(300, 0).addBox(-9F, 0F, -8F, 16F, 22F, 16F).texOffs(192, 136).addBox(-5F, -21.8F, -3F, 6F, 16F, 6F).texOffs(0, 162).addBox(0.2F, -16.2F, -1F, 6F, 10F, 6F).texOffs(272, 162).addBox(-7.9F, -13.9F, -4F, 4F, 8F, 4F).texOffs(128, 102).addBox(-12F, -4F, -11F, 22F, 8F, 22F).texOffs(216, 136).addBox(-12F, -7F, -9F, 18F, 4F, 18F), PartPose.offsetAndRotation(-30F, -44F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(380, 64).addBox(-7F, 0F, -7F, 14F, 20F, 14F).texOffs(436, 64).addBox(-9F, 20F, -9F, 18F, 16F, 18F).texOffs(80, 162).addBox(-9F, 35F, -9F, 18F, 4F, 10F).texOffs(136, 162).addBox(-8F, 10.2F, -2F, 4F, 10F, 4F).texOffs(352, 162).addBox(-4.8F, 13.8F, 0F, 4F, 6F, 4F).texOffs(408, 162).addBox(-10.4F, 15.6F, -4F, 4F, 4F, 4F), PartPose.offsetAndRotation(-1F, 22F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(364, 0).addBox(-7F, 0F, -8F, 16F, 22F, 16F).texOffs(288, 136).addBox(-1F, -21.8F, -3F, 6F, 16F, 6F).texOffs(24, 162).addBox(4.2F, -16.2F, -1F, 6F, 10F, 6F).texOffs(288, 162).addBox(-3.9F, -13.9F, -4F, 4F, 8F, 4F).texOffs(216, 102).addBox(-10F, -4F, -11F, 22F, 8F, 22F).texOffs(312, 136).addBox(-6F, -7F, -9F, 18F, 4F, 18F), PartPose.offsetAndRotation(30F, -44F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(0, 102).addBox(-7F, 0F, -7F, 14F, 20F, 14F).texOffs(56, 102).addBox(-9F, 20F, -9F, 18F, 16F, 18F).texOffs(152, 162).addBox(-9F, 35F, -9F, 18F, 4F, 10F).texOffs(208, 162).addBox(4F, 10.2F, -2F, 4F, 10F, 4F).texOffs(368, 162).addBox(7.2F, 13.8F, 0F, 4F, 6F, 4F).texOffs(424, 162).addBox(1.6F, 15.6F, -4F, 4F, 4F, 4F), PartPose.offsetAndRotation(1F, 22F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("pulse_core", CubeListBuilder.create().texOffs(304, 162).addBox(-4F, -4F, -2F, 8F, 8F, 4F), PartPose.offsetAndRotation(0F, -30F, -16F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(428, 0).addBox(-8F, 0F, -8F, 16F, 22F, 16F), PartPose.offsetAndRotation(-12F, -20F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(0, 64).addBox(-8F, 0F, -8F, 16F, 22F, 16F).texOffs(380, 102).addBox(-10F, 18F, -14F, 20F, 4F, 22F), PartPose.offsetAndRotation(0F, 22F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(64, 64).addBox(-8F, 0F, -8F, 16F, 22F, 16F), PartPose.offsetAndRotation(12F, -20F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(128, 64).addBox(-8F, 0F, -8F, 16F, 22F, 16F).texOffs(0, 136).addBox(-10F, 18F, -14F, 20F, 4F, 22F), PartPose.offsetAndRotation(0F, 22F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
