// 자동 생성 파일 (tools/bossgen/mobs2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class GaleHawkModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_wing"},
			{"body", "right_wing", "right_wing2"},
			{"body", "left_wing"},
			{"body", "left_wing", "left_wing2"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"}
	};

	public GaleHawkModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6F, -6F, -9F, 12F, 12F, 18F).texOffs(204, 30).addBox(-5F, 1F, -8F, 10F, 6F, 8F).texOffs(0, 48).addBox(-4F, -7.4F, -4F, 8F, 2F, 12F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(148, 0).addBox(-5F, -9F, -9F, 10F, 10F, 10F).texOffs(28, 76).addBox(-2F, -3F, -14F, 4F, 4F, 6F).texOffs(64, 62).addBox(-1F, -12F, -4F, 2F, 4F, 8F), PartPose.offsetAndRotation(0F, -4F, -10F, 0F, 0F, 0F));
		PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(136, 76).addBox(-22F, -2F, -2F, 22F, 4F, 4F).texOffs(188, 0).addBox(-22F, 0F, 2F, 6F, 2F, 16F).texOffs(40, 48).addBox(-16.5F, 0F, 2F, 6F, 2F, 12F).texOffs(76, 48).addBox(-11F, 0F, 2F, 6F, 2F, 12F).texOffs(0, 30).addBox(-5.5F, 0F, 2F, 6F, 2F, 16F), PartPose.offsetAndRotation(-6F, -4F, -2F, 0F, 0F, -0.1745F));
		right_wing.addOrReplaceChild("right_wing2", CubeListBuilder.create().texOffs(188, 76).addBox(-22F, -2F, -2F, 22F, 4F, 4F).texOffs(60, 0).addBox(-12F, 0F, 2F, 2F, 2F, 20F).texOffs(44, 30).addBox(-15.3F, 0F, 2F, 2F, 2F, 16F).texOffs(112, 48).addBox(-18.6F, 0F, 2F, 2F, 2F, 12F).texOffs(140, 48).addBox(-22F, 0.6F, 2F, 6F, 2F, 12F).texOffs(84, 62).addBox(-16.5F, 0.6F, 2F, 6F, 2F, 10F).texOffs(116, 62).addBox(-11F, 0.6F, 2F, 6F, 2F, 10F).texOffs(48, 76).addBox(-5.5F, 0.6F, 2F, 6F, 2F, 8F), PartPose.offsetAndRotation(-22F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 88).addBox(0F, -2F, -2F, 22F, 4F, 4F).texOffs(80, 30).addBox(0F, 0F, 2F, 6F, 2F, 16F).texOffs(176, 48).addBox(5.5F, 0F, 2F, 6F, 2F, 12F).texOffs(212, 48).addBox(11F, 0F, 2F, 6F, 2F, 12F).texOffs(124, 30).addBox(16.5F, 0F, 2F, 6F, 2F, 16F), PartPose.offsetAndRotation(6F, -4F, -2F, 0F, 0F, 0.1745F));
		left_wing.addOrReplaceChild("left_wing2", CubeListBuilder.create().texOffs(52, 88).addBox(0F, -2F, -2F, 22F, 4F, 4F).texOffs(104, 0).addBox(10F, 0F, 2F, 2F, 2F, 20F).texOffs(168, 30).addBox(13.3F, 0F, 2F, 2F, 2F, 16F).texOffs(0, 62).addBox(16.6F, 0F, 2F, 2F, 2F, 12F).texOffs(28, 62).addBox(0F, 0.6F, 2F, 6F, 2F, 12F).texOffs(148, 62).addBox(5.5F, 0.6F, 2F, 6F, 2F, 10F).texOffs(180, 62).addBox(11F, 0.6F, 2F, 6F, 2F, 10F).texOffs(76, 76).addBox(16.5F, 0.6F, 2F, 6F, 2F, 8F), PartPose.offsetAndRotation(22F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(104, 88).addBox(-5F, -1F, 0F, 10F, 2F, 6F), PartPose.offsetAndRotation(0F, 0F, 8F, 0F, 0F, 0F));
		tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(136, 88).addBox(-6F, -1F, 0F, 12F, 2F, 6F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(104, 76).addBox(-2F, 0F, -2F, 4F, 6F, 4F), PartPose.offsetAndRotation(-3F, 12F, 2F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(172, 88).addBox(-1F, 0F, -1F, 2F, 6F, 2F).texOffs(212, 62).addBox(-3F, 2F, -7F, 6F, 4F, 8F).texOffs(188, 88).addBox(-4F, 4F, -10F, 2F, 2F, 4F).texOffs(200, 88).addBox(-1F, 4F, -10F, 2F, 2F, 4F).texOffs(212, 88).addBox(2F, 4F, -10F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(120, 76).addBox(-2F, 0F, -2F, 4F, 6F, 4F), PartPose.offsetAndRotation(3F, 12F, 2F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(180, 88).addBox(-1F, 0F, -1F, 2F, 6F, 2F).texOffs(0, 76).addBox(-3F, 2F, -7F, 6F, 4F, 8F).texOffs(224, 88).addBox(-4F, 4F, -10F, 2F, 2F, 4F).texOffs(236, 88).addBox(-1F, 4F, -10F, 2F, 2F, 4F).texOffs(0, 96).addBox(2F, 4F, -10F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
