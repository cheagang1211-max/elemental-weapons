// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
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
			{"body", "ring"},
			{"body", "ring", "orb0"},
			{"body", "ring", "orb1"},
			{"body", "ring", "orb2"},
			{"body", "ring", "orb3"},
			{"body", "ring", "orb4"},
			{"body", "ring", "orb5"},
			{"body", "right_arm"},
			{"body", "left_arm"},
			{"right_leg"},
			{"left_leg"}
	};

	public LightSentinelModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 0).addBox(-4F, -8F, -2.5F, 8F, 12F, 5F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(10, 23).addBox(-3F, -6F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(0F, -8F, 0F, 0F, 0F, 0F));
		PartDefinition ring = body.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -16F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb0", CubeListBuilder.create().texOffs(34, 23).addBox(-0.5F, -0.5F, -0.5F, 1F, 1F, 1F), PartPose.offsetAndRotation(4F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb1", CubeListBuilder.create().texOffs(38, 23).addBox(-0.5F, -0.5F, -0.5F, 1F, 1F, 1F), PartPose.offsetAndRotation(2F, 0F, 3.4641F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb2", CubeListBuilder.create().texOffs(42, 23).addBox(-0.5F, -0.5F, -0.5F, 1F, 1F, 1F), PartPose.offsetAndRotation(-2F, 0F, 3.4641F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb3", CubeListBuilder.create().texOffs(46, 23).addBox(-0.5F, -0.5F, -0.5F, 1F, 1F, 1F), PartPose.offsetAndRotation(-4F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb4", CubeListBuilder.create().texOffs(50, 23).addBox(-0.5F, -0.5F, -0.5F, 1F, 1F, 1F), PartPose.offsetAndRotation(-2F, 0F, -3.4641F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb5", CubeListBuilder.create().texOffs(54, 23).addBox(-0.5F, -0.5F, -0.5F, 1F, 1F, 1F), PartPose.offsetAndRotation(2F, 0F, -3.4641F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(54, 0).addBox(-2F, 0F, -1.5F, 2F, 10F, 3F).texOffs(24, 0).addBox(-1.5F, -6F, -0.5F, 1F, 18F, 1F), PartPose.offsetAndRotation(-5F, -6F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 23).addBox(0F, 0F, -1.5F, 2F, 10F, 3F), PartPose.offsetAndRotation(5F, -6F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0F, -1.5F, 3F, 20F, 3F), PartPose.offsetAndRotation(-2F, 4F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(12, 0).addBox(-1.5F, 0F, -1.5F, 3F, 20F, 3F), PartPose.offsetAndRotation(2F, 4F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
