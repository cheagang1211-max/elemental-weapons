// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class StormWyvernModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "neck1"},
			{"body", "neck1", "head"},
			{"body", "neck1", "head", "jaw"},
			{"body", "right_wing"},
			{"body", "left_wing"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"},
			{"body", "tail1", "tail2", "tail3", "tail4"},
			{"right_leg"},
			{"left_leg"}
	};

	public StormWyvernModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5F, -4F, -9F, 10F, 8F, 18F).texOffs(56, 0).addBox(-4F, 3F, -8F, 8F, 2F, 16F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition neck1 = body.addOrReplaceChild("neck1", CubeListBuilder.create().texOffs(102, 43).addBox(-2.5F, -2.5F, -7F, 5F, 5F, 7F), PartPose.offsetAndRotation(0F, -1F, -9F, -0.4363F, 0F, 0F));
		PartDefinition head = neck1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(72, 43).addBox(-3.5F, -3F, -8F, 7F, 6F, 8F).texOffs(0, 72).addBox(-2.5F, -1F, -12F, 5F, 3F, 4F).texOffs(38, 72).addBox(-3F, -6F, -2F, 1F, 4F, 1F).texOffs(42, 72).addBox(2F, -6F, -2F, 1F, 4F, 1F), PartPose.offsetAndRotation(0F, 0F, -7F, 0F, 0F, 0F));
		head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(88, 60).addBox(-2.5F, 0F, -7F, 5F, 1F, 7F), PartPose.offsetAndRotation(0F, 2F, -5F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(46, 72).addBox(-16F, -1F, -1F, 16F, 2F, 2F).texOffs(0, 26).addBox(-20F, -0.5F, 0F, 20F, 1F, 16F), PartPose.offsetAndRotation(-5F, -3F, -3F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(82, 72).addBox(0F, -1F, -1F, 16F, 2F, 2F).texOffs(0, 43).addBox(0F, -0.5F, 0F, 20F, 1F, 16F), PartPose.offsetAndRotation(5F, -3F, -3F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 60).addBox(-2.5F, -2.5F, 0F, 5F, 5F, 7F), PartPose.offsetAndRotation(0F, -1F, 9F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(24, 60).addBox(-2F, -2F, 0F, 4F, 4F, 7F), PartPose.offsetAndRotation(0F, 0F, 7F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(70, 60).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 6F), PartPose.offsetAndRotation(0F, 0F, 7F, 0F, 0F, 0F));
		tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(18, 72).addBox(-2.5F, -0.5F, 0F, 5F, 1F, 5F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(46, 60).addBox(-1.5F, 0F, -1.5F, 3F, 8F, 3F), PartPose.offsetAndRotation(-3F, 14F, 4F, 0F, 0F, 0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(58, 60).addBox(-1.5F, 0F, -1.5F, 3F, 8F, 3F), PartPose.offsetAndRotation(3F, 14F, 4F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
