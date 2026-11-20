// 자동 생성 파일 (tools/bossgen/mobs2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class MagmaBruteModel extends GenericBossModel {
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

	public MagmaBruteModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 44).addBox(-13F, -1F, -8F, 26F, 22F, 16F).texOffs(196, 128).addBox(-5F, 3F, -9.4F, 10F, 10F, 2F).texOffs(148, 44).addBox(-14F, -3F, -9F, 28F, 6F, 18F).texOffs(44, 148).addBox(-10F, -9F, 2F, 4F, 6F, 4F).texOffs(60, 148).addBox(-2F, -9F, 2F, 4F, 6F, 4F).texOffs(76, 148).addBox(6F, -9F, 2F, 4F, 6F, 4F), PartPose.offsetAndRotation(0F, -18F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(84, 44).addBox(-8F, -13F, -10F, 16F, 14F, 16F).texOffs(92, 148).addBox(-10F, -16F, -1F, 2F, 8F, 2F).texOffs(100, 148).addBox(8F, -16F, -1F, 2F, 8F, 2F).texOffs(0, 148).addBox(-6F, -15F, -3F, 12F, 2F, 10F), PartPose.offsetAndRotation(0F, 2F, -2F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(128, 82).addBox(-6F, 0F, -5F, 10F, 12F, 10F).texOffs(0, 82).addBox(-9F, -4F, -8F, 16F, 8F, 16F).texOffs(100, 128).addBox(-9F, -7F, -6F, 12F, 4F, 12F), PartPose.offsetAndRotation(-16F, 6F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(168, 82).addBox(-5F, 0F, -5F, 10F, 12F, 10F).texOffs(96, 0).addBox(-12F, 38F, -12F, 24F, 20F, 24F).texOffs(108, 148).addBox(-6F, 28F, -6F, 12F, 2F, 8F), PartPose.offsetAndRotation(-1F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(208, 82).addBox(-4F, 0F, -5F, 10F, 12F, 10F).texOffs(64, 82).addBox(-7F, -4F, -8F, 16F, 8F, 16F).texOffs(148, 128).addBox(-3F, -7F, -6F, 12F, 4F, 12F), PartPose.offsetAndRotation(16F, 6F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(0, 106).addBox(-5F, 0F, -5F, 10F, 12F, 10F).texOffs(96, 0).addBox(-12F, 38F, -12F, 24F, 20F, 24F).texOffs(148, 148).addBox(-6F, 28F, -6F, 12F, 2F, 8F), PartPose.offsetAndRotation(1F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(40, 106).addBox(-5F, 0F, -5F, 10F, 12F, 10F), PartPose.offsetAndRotation(-6F, 2F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(120, 106).addBox(-5F, 0F, -5F, 10F, 10F, 10F).texOffs(160, 106).addBox(-7F, 6F, -11F, 14F, 4F, 16F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(80, 106).addBox(-5F, 0F, -5F, 10F, 12F, 10F), PartPose.offsetAndRotation(6F, 2F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(0, 128).addBox(-5F, 0F, -5F, 10F, 10F, 10F).texOffs(40, 128).addBox(-7F, 6F, -11F, 14F, 4F, 16F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
