// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class SteamColossusModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"body", "spin_g920"},
			{"body", "spin_g921"},
			{"body", "bob_s1"},
			{"body", "bob_s2"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"}
	};

	public SteamColossusModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-24F, -52F, -14F, 48F, 40F, 28F).texOffs(0, 164).addBox(-10F, -40F, -15.4F, 20F, 20F, 2F).texOffs(188, 186).addBox(-12F, -31F, -16.2F, 24F, 2F, 2F).texOffs(240, 186).addBox(-12F, -23F, -16.2F, 24F, 2F, 2F).texOffs(0, 68).addBox(-26F, -58F, -15F, 52F, 8F, 30F).texOffs(0, 106).addBox(-18F, -14F, -12F, 36F, 8F, 24F).texOffs(80, 186).addBox(-22F, -22F, -15.8F, 4F, 4F, 2F).texOffs(0, 138).addBox(-15F, -82F, 7F, 6F, 20F, 6F).texOffs(24, 138).addBox(9F, -82F, 7F, 6F, 20F, 6F).texOffs(468, 164).addBox(-16F, -84F, 6F, 8F, 2F, 8F).texOffs(0, 186).addBox(8F, -84F, 6F, 8F, 2F, 8F).texOffs(360, 138).addBox(-16F, -50F, 13F, 32F, 20F, 4F), PartPose.offsetAndRotation(0F, -32F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(276, 68).addBox(-10F, -16F, -9F, 20F, 16F, 18F).texOffs(268, 164).addBox(-6F, -20F, -6F, 12F, 4F, 12F).texOffs(316, 164).addBox(-14F, -12F, -4F, 4F, 8F, 8F).texOffs(340, 164).addBox(10F, -12F, -4F, 4F, 8F, 8F).texOffs(32, 186).addBox(-2F, -25F, -2F, 4F, 6F, 4F), PartPose.offsetAndRotation(0F, -60F, -4F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(164, 68).addBox(-8F, 0F, -7F, 14F, 22F, 14F).texOffs(120, 106).addBox(-11F, -4F, -10F, 20F, 8F, 20F).texOffs(68, 164).addBox(-11F, -7F, -8F, 16F, 4F, 16F), PartPose.offsetAndRotation(-28F, -54F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(352, 68).addBox(-7F, 0F, -7F, 14F, 20F, 14F).texOffs(44, 164).addBox(-3F, 16F, -3F, 6F, 16F, 6F).texOffs(200, 106).addBox(-10F, 29F, -7F, 20F, 14F, 14F).texOffs(132, 164).addBox(-12F, 31F, -5F, 2F, 10F, 10F).texOffs(156, 164).addBox(10F, 31F, -5F, 2F, 10F, 10F).texOffs(48, 138).addBox(-9F, 2F, -9F, 18F, 8F, 18F), PartPose.offsetAndRotation(-1F, 22F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(220, 68).addBox(-6F, 0F, -7F, 14F, 22F, 14F).texOffs(268, 106).addBox(-9F, -4F, -10F, 20F, 8F, 20F).texOffs(180, 164).addBox(-5F, -7F, -8F, 16F, 4F, 16F), PartPose.offsetAndRotation(28F, -54F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(408, 68).addBox(-7F, 0F, -7F, 14F, 20F, 14F).texOffs(120, 138).addBox(-9F, 2F, -9F, 18F, 8F, 18F).texOffs(432, 138).addBox(-6F, 20F, -8F, 12F, 12F, 12F).texOffs(48, 186).addBox(-4F, 32F, -6F, 8F, 2F, 8F), PartPose.offsetAndRotation(1F, 22F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("spin_g920", CubeListBuilder.create().texOffs(364, 164).addBox(-6F, -6F, -1F, 12F, 12F, 2F).texOffs(92, 186).addBox(-8F, -2F, -1F, 16F, 4F, 2F).texOffs(244, 164).addBox(-2F, -8F, -1F, 4F, 16F, 2F).texOffs(128, 186).addBox(-2F, -2F, -2F, 4F, 4F, 2F), PartPose.offsetAndRotation(-18F, -40F, -15.2F, 0F, 0F, 0F));
		body.addOrReplaceChild("spin_g921", CubeListBuilder.create().texOffs(392, 164).addBox(-6F, -6F, -1F, 12F, 12F, 2F).texOffs(140, 186).addBox(-8F, -2F, -1F, 16F, 4F, 2F).texOffs(256, 164).addBox(-2F, -8F, -1F, 4F, 16F, 2F).texOffs(176, 186).addBox(-2F, -2F, -2F, 4F, 4F, 2F), PartPose.offsetAndRotation(18F, -42F, -15.2F, 0F, 0F, 0F));
		body.addOrReplaceChild("bob_s1", CubeListBuilder.create().texOffs(420, 164).addBox(-3F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(-12F, -88F, 10F, 0F, 0F, 0F));
		body.addOrReplaceChild("bob_s2", CubeListBuilder.create().texOffs(444, 164).addBox(-3F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(12F, -90F, 10F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(152, 0).addBox(-8F, 0F, -8F, 16F, 24F, 16F).texOffs(348, 106).addBox(-10F, 0F, -10F, 20F, 8F, 20F), PartPose.offsetAndRotation(-12F, -24F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(216, 0).addBox(-8F, 0F, -8F, 16F, 24F, 16F).texOffs(192, 138).addBox(-10F, 20F, -14F, 20F, 4F, 22F), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(280, 0).addBox(-8F, 0F, -8F, 16F, 24F, 16F).texOffs(428, 106).addBox(-10F, 0F, -10F, 20F, 8F, 20F), PartPose.offsetAndRotation(12F, -24F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(344, 0).addBox(-8F, 0F, -8F, 16F, 24F, 16F).texOffs(276, 138).addBox(-10F, 20F, -14F, 20F, 4F, 22F), PartPose.offsetAndRotation(0F, 24F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
