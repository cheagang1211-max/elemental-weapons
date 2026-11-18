// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class IgnisModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "neck1"},
			{"body", "neck1", "neck2"},
			{"body", "neck1", "neck2", "head"},
			{"body", "neck1", "neck2", "head", "jaw"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"},
			{"body", "tail1", "tail2", "tail3", "tail4"},
			{"body", "tail1", "tail2", "tail3", "tail4", "tail5"},
			{"body", "right_wing"},
			{"body", "left_wing"},
			{"right_leg"},
			{"left_leg"},
			{"right_arm"},
			{"left_arm"}
	};

	public IgnisModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6F, -5F, -10F, 12F, 10F, 20F).texOffs(64, 0).addBox(-5F, 4F, -9F, 10F, 2F, 18F).texOffs(0, 30).addBox(-1F, -7F, -6F, 2F, 2F, 14F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition neck1 = body.addOrReplaceChild("neck1", CubeListBuilder.create().texOffs(0, 61).addBox(-3F, -3F, -8F, 6F, 6F, 8F), PartPose.offsetAndRotation(0F, -2F, -10F, -0.5236F, 0F, 0F));
		PartDefinition neck2 = neck1.addOrReplaceChild("neck2", CubeListBuilder.create().texOffs(26, 75).addBox(-2.5F, -2.5F, -7F, 5F, 5F, 7F), PartPose.offsetAndRotation(0F, 0F, -8F, 0.3491F, 0F, 0F));
		PartDefinition head = neck2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 30).addBox(-4F, -4F, -9F, 8F, 7F, 9F).texOffs(102, 75).addBox(-3F, -1F, -14F, 6F, 4F, 5F).texOffs(18, 88).addBox(-3.5F, -7F, -3F, 1F, 4F, 1F).texOffs(22, 88).addBox(2.5F, -7F, -3F, 1F, 4F, 1F).texOffs(106, 88).addBox(-1F, -1.5F, -14.5F, 2F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, -7F, 0F, 0F, 0F));
		head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(50, 75).addBox(-3F, 0F, -9F, 6F, 2F, 9F), PartPose.offsetAndRotation(0F, 3F, -5F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(28, 61).addBox(-3F, -3F, 0F, 6F, 6F, 8F), PartPose.offsetAndRotation(0F, -1F, 10F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(0, 75).addBox(-2.5F, -2.5F, 0F, 5F, 5F, 8F), PartPose.offsetAndRotation(0F, 0F, 8F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(80, 75).addBox(-2F, -2F, 0F, 4F, 4F, 7F), PartPose.offsetAndRotation(0F, 0F, 8F, 0F, 0F, 0F));
		PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(0, 88).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 6F), PartPose.offsetAndRotation(0F, 0F, 7F, 0F, 0F, 0F));
		tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(26, 88).addBox(-2F, -0.5F, 0F, 4F, 1F, 4F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(42, 88).addBox(-14F, -1F, -1F, 14F, 2F, 2F).texOffs(0, 46).addBox(-18F, -0.5F, 0F, 18F, 1F, 14F), PartPose.offsetAndRotation(-6F, -4F, -4F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(74, 88).addBox(0F, -1F, -1F, 14F, 2F, 2F).texOffs(64, 46).addBox(0F, -0.5F, 0F, 18F, 1F, 14F), PartPose.offsetAndRotation(6F, -4F, -4F, 0F, 0F, 0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(56, 61).addBox(-2F, 0F, -2F, 4F, 10F, 4F), PartPose.offsetAndRotation(-5F, 14F, 6F, 0F, 0F, 0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(72, 61).addBox(-2F, 0F, -2F, 4F, 10F, 4F), PartPose.offsetAndRotation(5F, 14F, 6F, 0F, 0F, 0F));
		root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(88, 61).addBox(-2F, 0F, -2F, 4F, 10F, 4F), PartPose.offsetAndRotation(-5F, 14F, -6F, 0F, 0F, 0F));
		root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(104, 61).addBox(-2F, 0F, -2F, 4F, 10F, 4F), PartPose.offsetAndRotation(5F, 14F, -6F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
