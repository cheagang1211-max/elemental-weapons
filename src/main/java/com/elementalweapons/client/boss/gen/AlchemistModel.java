// 자동 생성 파일 (tools/bossgen/mobs2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class AlchemistModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"}
	};

	public AlchemistModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(72, 0).addBox(-8F, 0F, -6F, 16F, 24F, 12F).texOffs(0, 0).addBox(-10F, 22F, -8F, 20F, 26F, 16F).texOffs(192, 0).addBox(-6F, 26F, -9.2F, 12F, 20F, 2F).texOffs(0, 62).addBox(-9F, 20F, -7F, 18F, 4F, 14F).texOffs(64, 80).addBox(-8F, 23F, -10F, 4F, 6F, 4F).texOffs(80, 80).addBox(-2F, 23F, -10F, 4F, 6F, 4F).texOffs(96, 80).addBox(4F, 23F, -10F, 4F, 6F, 4F), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(128, 0).addBox(-8F, -18F, -8F, 16F, 18F, 16F).texOffs(0, 42).addBox(-9F, -19F, -6F, 18F, 4F, 16F).texOffs(68, 42).addBox(-9F, -16F, 2F, 18F, 12F, 8F).texOffs(112, 80).addBox(-7F, -13F, -9.8F, 6F, 6F, 2F).texOffs(128, 80).addBox(1F, -13F, -9.8F, 6F, 6F, 2F).texOffs(168, 80).addBox(-9F, -11F, -9.4F, 18F, 2F, 2F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(64, 62).addBox(-4F, 0F, -3F, 6F, 12F, 6F).texOffs(120, 42).addBox(-7F, -4F, -6F, 12F, 8F, 12F).texOffs(0, 80).addBox(-7F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(-10F, 4F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(88, 62).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(160, 62).addBox(-3F, 22F, -7F, 6F, 8F, 6F).texOffs(144, 80).addBox(-2F, 25F, -6F, 4F, 4F, 4F).texOffs(160, 80).addBox(-1F, 19F, -5F, 2F, 4F, 2F).texOffs(184, 62).addBox(-4F, 12F, -4F, 8F, 6F, 8F), PartPose.offsetAndRotation(-1F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(112, 62).addBox(-2F, 0F, -3F, 6F, 12F, 6F).texOffs(168, 42).addBox(-5F, -4F, -6F, 12F, 8F, 12F).texOffs(32, 80).addBox(-1F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(10F, 4F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(136, 62).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(216, 62).addBox(-4F, 12F, -4F, 8F, 6F, 8F), PartPose.offsetAndRotation(1F, 12F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
