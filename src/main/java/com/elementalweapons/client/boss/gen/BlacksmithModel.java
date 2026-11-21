// 자동 생성 파일 (tools/bossgen/mobs2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class BlacksmithModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "head", "swayB1"},
			{"body", "head", "swayB1", "swayB2"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"}
	};

	public BlacksmithModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-10F, 0F, -6F, 20F, 24F, 12F).texOffs(128, 0).addBox(-9F, 1F, -7.8F, 18F, 26F, 2F).texOffs(0, 78).addBox(-10F, 22F, -6F, 20F, 4F, 12F).texOffs(204, 94).addBox(-9.2F, 22.4F, -8.6F, 4F, 4F, 2F).texOffs(132, 94).addBox(3F, 15F, -9F, 6F, 6F, 2F), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(64, 0).addBox(-8F, -18F, -8F, 16F, 18F, 16F).texOffs(168, 0).addBox(-9F, -19.2F, -9F, 18F, 4F, 18F).texOffs(148, 94).addBox(-7F, -6F, -9.6F, 14F, 6F, 2F).texOffs(180, 94).addBox(4.8F, -19.4F, 7.4F, 4F, 6F, 2F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition swayB1 = head.addOrReplaceChild("swayB1", CubeListBuilder.create().texOffs(40, 94).addBox(-6F, 0F, -3F, 12F, 6F, 6F), PartPose.offsetAndRotation(0F, -2F, -9F, 0F, 0F, 0F));
		swayB1.addOrReplaceChild("swayB2", CubeListBuilder.create().texOffs(108, 94).addBox(-4F, 0F, -2F, 8F, 6F, 4F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(112, 36).addBox(-5F, 0F, -4F, 8F, 12F, 8F).texOffs(0, 36).addBox(-8F, -4F, -7F, 14F, 8F, 14F).texOffs(144, 78).addBox(-8F, -7F, -5F, 10F, 4F, 10F), PartPose.offsetAndRotation(-12F, 4F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(144, 36).addBox(-4F, 0F, -4F, 8F, 12F, 8F).texOffs(176, 36).addBox(-1F, 15F, -5F, 2F, 18F, 2F).texOffs(76, 94).addBox(-5F, 29F, -7F, 10F, 6F, 6F).texOffs(192, 94).addBox(-6.2F, 30F, -6F, 2F, 4F, 4F).texOffs(64, 78).addBox(-5F, 12F, -5F, 10F, 6F, 10F), PartPose.offsetAndRotation(-1F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(184, 36).addBox(-3F, 0F, -4F, 8F, 12F, 8F).texOffs(56, 36).addBox(-6F, -4F, -7F, 14F, 8F, 14F).texOffs(184, 78).addBox(-2F, -7F, -5F, 10F, 4F, 10F), PartPose.offsetAndRotation(12F, 4F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(216, 36).addBox(-4F, 0F, -4F, 8F, 12F, 8F).texOffs(0, 94).addBox(-5F, 4F, -5F, 10F, 4F, 10F).texOffs(104, 78).addBox(-5F, 12F, -5F, 10F, 6F, 10F), PartPose.offsetAndRotation(1F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 58).addBox(-4F, 0F, -4F, 8F, 12F, 8F), PartPose.offsetAndRotation(-5F, 0F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(32, 58).addBox(-4F, 0F, -4F, 8F, 12F, 8F).texOffs(128, 58).addBox(-6F, 8F, -10F, 12F, 4F, 14F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(64, 58).addBox(-4F, 0F, -4F, 8F, 12F, 8F), PartPose.offsetAndRotation(5F, 0F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(96, 58).addBox(-4F, 0F, -4F, 8F, 12F, 8F).texOffs(180, 58).addBox(-6F, 8F, -10F, 12F, 4F, 14F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
