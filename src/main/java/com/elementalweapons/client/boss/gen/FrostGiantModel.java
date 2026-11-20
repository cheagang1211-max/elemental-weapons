// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class FrostGiantModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "head", "swayB1"},
			{"body", "head", "swayB1", "swayB2"},
			{"body", "head", "swayB1", "swayB2", "swayB3"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"body", "cape1"},
			{"body", "cape1", "cape2"},
			{"body", "cape1", "cape2", "cape3"},
			{"body", "cape1", "cape2", "cape3", "cape4"},
			{"body", "cape1", "cape2", "cape3", "cape4", "cape5"},
			{"body", "cape1", "cape2", "cape3", "cape4", "cape5", "cape6"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"}
	};

	public FrostGiantModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-16F, -52F, -10F, 32F, 44F, 20F).texOffs(216, 0).addBox(-20F, -51F, -12F, 40F, 14F, 24F).texOffs(384, 64).addBox(-14F, -32F, -11.4F, 28F, 24F, 2F).texOffs(272, 64).addBox(-17F, -12F, -11F, 34F, 8F, 22F).texOffs(108, 164).addBox(-4F, -11F, -12.6F, 8F, 6F, 2F).texOffs(24, 164).addBox(-18F, -57F, -2F, 4F, 6F, 4F).texOffs(40, 164).addBox(-7.3333F, -57F, -2F, 4F, 6F, 4F).texOffs(56, 164).addBox(3.3333F, -57F, -2F, 4F, 6F, 4F).texOffs(72, 164).addBox(14F, -57F, -2F, 4F, 6F, 4F), PartPose.offsetAndRotation(0F, -32F, 0F, 0F, 0F, 0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(48, 64).addBox(-9F, -17F, -9F, 18F, 18F, 18F).texOffs(128, 164).addBox(-9F, -5F, -10F, 18F, 6F, 2F).texOffs(168, 164).addBox(-7F, -25F, -1F, 2F, 6F, 2F).texOffs(388, 150).addBox(-3F, -27F, -1F, 2F, 10F, 2F).texOffs(396, 150).addBox(1F, -27F, -1F, 2F, 10F, 2F).texOffs(176, 164).addBox(5F, -25F, -1F, 2F, 6F, 2F).texOffs(68, 128).addBox(-9F, -19F, -9F, 18F, 2F, 18F), PartPose.offsetAndRotation(0F, -64F, 0F, 0F, 0F, 0F));
		PartDefinition swayB1 = head.addOrReplaceChild("swayB1", CubeListBuilder.create().texOffs(404, 150).addBox(-8F, 0F, -2F, 16F, 8F, 4F), PartPose.offsetAndRotation(0F, 0F, -8F, 0F, 0F, 0F));
		PartDefinition swayB2 = swayB1.addOrReplaceChild("swayB2", CubeListBuilder.create().texOffs(444, 150).addBox(-6F, 0F, -2F, 12F, 8F, 4F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		swayB2.addOrReplaceChild("swayB3", CubeListBuilder.create().texOffs(88, 164).addBox(-4F, 0F, -1F, 8F, 8F, 2F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(344, 0).addBox(-7F, 0F, -6F, 12F, 26F, 12F).texOffs(0, 102).addBox(-10F, -4F, -9F, 18F, 8F, 18F).texOffs(252, 128).addBox(-10F, -7F, -7F, 14F, 4F, 14F), PartPose.offsetAndRotation(-20F, -60F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(120, 64).addBox(-6F, 0F, -6F, 12F, 24F, 12F).texOffs(72, 102).addBox(-3F, 14F, -7F, 6F, 20F, 6F).texOffs(216, 64).addBox(-7F, 32F, -11F, 14F, 20F, 14F).texOffs(476, 150).addBox(-9F, 36F, -6F, 4F, 8F, 4F).texOffs(492, 150).addBox(5F, 42F, -6F, 4F, 8F, 4F).texOffs(0, 164).addBox(-3F, 50F, -7F, 6F, 6F, 6F).texOffs(312, 102).addBox(-7F, 3F, -7F, 14F, 10F, 14F).texOffs(140, 128).addBox(-7F, 24F, -7F, 14F, 6F, 14F), PartPose.offsetAndRotation(-1F, 26F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(392, 0).addBox(-5F, 0F, -6F, 12F, 26F, 12F).texOffs(96, 102).addBox(-8F, -4F, -9F, 18F, 8F, 18F).texOffs(308, 128).addBox(-4F, -7F, -7F, 14F, 4F, 14F), PartPose.offsetAndRotation(20F, -60F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(168, 64).addBox(-6F, 0F, -6F, 12F, 24F, 12F).texOffs(368, 102).addBox(-7F, 3F, -7F, 14F, 10F, 14F).texOffs(196, 128).addBox(-7F, 24F, -7F, 14F, 6F, 14F), PartPose.offsetAndRotation(1F, 26F, 0F, 0F, 0F, 0F));
		PartDefinition cape1 = body.addOrReplaceChild("cape1", CubeListBuilder.create().texOffs(364, 128).addBox(-18F, 0F, -2F, 36F, 10F, 4F), PartPose.offsetAndRotation(0F, -58F, 12F, 0F, 0F, 0F));
		PartDefinition cape2 = cape1.addOrReplaceChild("cape2", CubeListBuilder.create().texOffs(0, 150).addBox(-18F, 0F, -2F, 36F, 10F, 4F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition cape3 = cape2.addOrReplaceChild("cape3", CubeListBuilder.create().texOffs(80, 150).addBox(-18F, 0F, -2F, 36F, 10F, 4F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition cape4 = cape3.addOrReplaceChild("cape4", CubeListBuilder.create().texOffs(160, 150).addBox(-17F, 0F, -2F, 34F, 10F, 4F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition cape5 = cape4.addOrReplaceChild("cape5", CubeListBuilder.create().texOffs(236, 150).addBox(-17F, 0F, -2F, 34F, 10F, 4F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		cape5.addOrReplaceChild("cape6", CubeListBuilder.create().texOffs(312, 150).addBox(-17F, 0F, -2F, 34F, 10F, 4F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(104, 0).addBox(-7F, 0F, -7F, 14F, 26F, 14F).texOffs(168, 102).addBox(-9F, 0F, -9F, 18F, 8F, 18F), PartPose.offsetAndRotation(-8F, -28F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(440, 0).addBox(-6F, 0F, -6F, 12F, 26F, 12F).texOffs(424, 102).addBox(-8F, 22F, -12F, 16F, 4F, 18F), PartPose.offsetAndRotation(0F, 26F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(160, 0).addBox(-7F, 0F, -7F, 14F, 26F, 14F).texOffs(240, 102).addBox(-9F, 0F, -9F, 18F, 8F, 18F), PartPose.offsetAndRotation(8F, -28F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(0, 64).addBox(-6F, 0F, -6F, 12F, 26F, 12F).texOffs(0, 128).addBox(-8F, 22F, -12F, 16F, 4F, 18F), PartPose.offsetAndRotation(0F, 26F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
