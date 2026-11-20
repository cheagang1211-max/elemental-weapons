// 자동 생성 파일 (tools/bossgen/mobs2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class LightSentinelModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "halo"},
			{"body", "halo", "lh0"},
			{"body", "halo", "lh1"},
			{"body", "halo", "lh2"},
			{"body", "halo", "lh3"},
			{"body", "halo", "lh4"},
			{"body", "halo", "lh5"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"}
	};

	public LightSentinelModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(8, 0).addBox(-8F, -16F, -5F, 16F, 24F, 10F).texOffs(160, 82).addBox(-3F, -15F, -6.6F, 6F, 6F, 2F).texOffs(0, 66).addBox(-9F, 5F, -6F, 18F, 4F, 12F), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(184, 0).addBox(-6F, -12F, -6F, 12F, 12F, 12F).texOffs(96, 46).addBox(-1F, -15F, -7F, 2F, 4F, 14F).texOffs(64, 82).addBox(-8F, -11F, -1F, 2F, 6F, 6F).texOffs(80, 82).addBox(6F, -11F, -1F, 2F, 6F, 6F), PartPose.offsetAndRotation(0F, -16F, 0F, 0F, 0F, 0F));
		PartDefinition halo = body.addOrReplaceChild("halo", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -32F, 4F, 0F, 0F, 0F));
		halo.addOrReplaceChild("lh0", CubeListBuilder.create().texOffs(200, 82).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(8F, 0F, 0F, 0F, 0F, 0F));
		halo.addOrReplaceChild("lh1", CubeListBuilder.create().texOffs(208, 82).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(4F, 0F, 6.9282F, 0F, 1.0472F, 0F));
		halo.addOrReplaceChild("lh2", CubeListBuilder.create().texOffs(216, 82).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-4F, 0F, 6.9282F, 0F, 2.0944F, 0F));
		halo.addOrReplaceChild("lh3", CubeListBuilder.create().texOffs(224, 82).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-8F, 0F, 0F, 0F, 3.1416F, 0F));
		halo.addOrReplaceChild("lh4", CubeListBuilder.create().texOffs(232, 82).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-4F, 0F, -6.9282F, 0F, 4.1888F, 0F));
		halo.addOrReplaceChild("lh5", CubeListBuilder.create().texOffs(240, 82).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(4F, 0F, -6.9282F, 0F, 5.236F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(60, 66).addBox(-4F, 0F, -3F, 6F, 10F, 6F).texOffs(0, 46).addBox(-7F, -4F, -6F, 12F, 8F, 12F).texOffs(96, 82).addBox(-7F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(-10F, -12F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(84, 66).addBox(-3F, 0F, -3F, 6F, 10F, 6F).texOffs(0, 0).addBox(-1F, -10F, -5F, 2F, 44F, 2F).texOffs(176, 82).addBox(-3F, -15F, -5F, 6F, 6F, 2F).texOffs(192, 82).addBox(-1F, -20F, -5F, 2F, 6F, 2F).texOffs(0, 82).addBox(-4F, 10F, -4F, 8F, 6F, 8F), PartPose.offsetAndRotation(-1F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(108, 66).addBox(-2F, 0F, -3F, 6F, 10F, 6F).texOffs(48, 46).addBox(-5F, -4F, -6F, 12F, 8F, 12F).texOffs(128, 82).addBox(-1F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(10F, -12F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(132, 66).addBox(-3F, 0F, -3F, 6F, 10F, 6F).texOffs(60, 0).addBox(3F, 0F, -6F, 2F, 16F, 12F).texOffs(32, 82).addBox(-4F, 10F, -4F, 8F, 6F, 8F), PartPose.offsetAndRotation(1F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(88, 0).addBox(-3F, 0F, -3F, 6F, 20F, 6F).texOffs(128, 46).addBox(-5F, 0F, -5F, 10F, 8F, 10F), PartPose.offsetAndRotation(-4F, -16F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(112, 0).addBox(-3F, 0F, -3F, 6F, 20F, 6F).texOffs(156, 66).addBox(-5F, 16F, -9F, 10F, 4F, 12F), PartPose.offsetAndRotation(0F, 20F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(136, 0).addBox(-3F, 0F, -3F, 6F, 20F, 6F).texOffs(168, 46).addBox(-5F, 0F, -5F, 10F, 8F, 10F), PartPose.offsetAndRotation(4F, -16F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(160, 0).addBox(-3F, 0F, -3F, 6F, 20F, 6F).texOffs(200, 66).addBox(-5F, 16F, -9F, 10F, 4F, 12F), PartPose.offsetAndRotation(0F, 20F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
