// 자동 생성 파일 (tools/bossgen/mobs2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class ShadowStalkerModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
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

	public ShadowStalkerModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8F, -20F, -5F, 16F, 24F, 10F).texOffs(104, 34).addBox(-2F, -18F, -6.4F, 4F, 16F, 2F).texOffs(180, 34).addBox(-9F, -20F, -6F, 18F, 4F, 12F), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(24, 34).addBox(-6F, -13F, -7F, 12F, 12F, 12F).texOffs(52, 0).addBox(-8F, -16F, -6F, 16F, 16F, 16F).texOffs(88, 62).addBox(-4F, -19F, 2F, 8F, 4F, 8F), PartPose.offsetAndRotation(0F, -20F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(116, 34).addBox(-3F, 0F, -2F, 4F, 14F, 4F), PartPose.offsetAndRotation(-10F, -18F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(132, 34).addBox(-2F, 0F, -2F, 4F, 14F, 4F).texOffs(148, 0).addBox(-2F, 46F, -10F, 4F, 20F, 12F).texOffs(88, 34).addBox(-6F, 48F, -6F, 4F, 16F, 4F).texOffs(120, 62).addBox(-3F, 14F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(-1F, 14F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(148, 34).addBox(-1F, 0F, -2F, 4F, 14F, 4F), PartPose.offsetAndRotation(10F, -18F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(164, 34).addBox(-2F, 0F, -2F, 4F, 14F, 4F).texOffs(148, 0).addBox(-2F, 46F, -10F, 4F, 20F, 12F).texOffs(88, 34).addBox(-6F, 48F, -6F, 4F, 16F, 4F).texOffs(144, 62).addBox(-3F, 14F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(1F, 14F, 0F, 0F, 0F, 0F));
		PartDefinition cape1 = body.addOrReplaceChild("cape1", CubeListBuilder.create().texOffs(168, 62).addBox(-8F, 0F, -1F, 16F, 8F, 2F), PartPose.offsetAndRotation(0F, -18F, 5.6F, 0F, 0F, 0F));
		PartDefinition cape2 = cape1.addOrReplaceChild("cape2", CubeListBuilder.create().texOffs(204, 62).addBox(-8F, 0F, -1F, 16F, 8F, 2F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		PartDefinition cape3 = cape2.addOrReplaceChild("cape3", CubeListBuilder.create().texOffs(0, 78).addBox(-8F, 0F, -1F, 16F, 8F, 2F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		PartDefinition cape4 = cape3.addOrReplaceChild("cape4", CubeListBuilder.create().texOffs(36, 78).addBox(-8F, 0F, -1F, 16F, 8F, 2F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		cape4.addOrReplaceChild("cape5", CubeListBuilder.create().texOffs(72, 78).addBox(-8F, 0F, -1F, 16F, 8F, 2F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(180, 0).addBox(-3F, 0F, -3F, 6F, 22F, 6F), PartPose.offsetAndRotation(-4F, -20F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(204, 0).addBox(-3F, 0F, -3F, 6F, 22F, 6F).texOffs(0, 62).addBox(-5F, 18F, -9F, 10F, 4F, 12F), PartPose.offsetAndRotation(0F, 22F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(228, 0).addBox(-3F, 0F, -3F, 6F, 22F, 6F), PartPose.offsetAndRotation(4F, -20F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(0, 34).addBox(-3F, 0F, -3F, 6F, 22F, 6F).texOffs(44, 62).addBox(-5F, 18F, -9F, 10F, 4F, 12F), PartPose.offsetAndRotation(0F, 22F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
