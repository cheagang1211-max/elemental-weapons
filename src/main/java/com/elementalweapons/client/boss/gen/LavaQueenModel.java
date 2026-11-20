// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class LavaQueenModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"body", "tail1"},
			{"body", "leg1"},
			{"body", "leg1", "leg1b"},
			{"body", "leg2"},
			{"body", "leg2", "leg2b"},
			{"body", "leg3"},
			{"body", "leg3", "leg3b"},
			{"body", "leg4"},
			{"body", "leg4", "leg4b"},
			{"body", "leg5"},
			{"body", "leg5", "leg5b"},
			{"body", "leg6"},
			{"body", "leg6", "leg6b"},
			{"body", "leg7"},
			{"body", "leg7", "leg7b"},
			{"body", "leg8"},
			{"body", "leg8", "leg8b"}
	};

	public LavaQueenModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(68, 0).addBox(-6F, -4F, -7F, 12F, 8F, 14F).texOffs(120, 0).addBox(-6.5F, -1F, -7.5F, 13F, 4F, 15F).texOffs(176, 0).addBox(-4.5F, -12F, -13F, 9F, 10F, 6F).texOffs(80, 31).addBox(-5F, -10.5F, -13.5F, 10F, 3F, 7F).texOffs(114, 31).addBox(-5F, -2.5F, -13.5F, 10F, 3F, 7F).texOffs(204, 56).addBox(-2.5F, 1.5F, -15.5F, 1F, 3F, 1F).texOffs(208, 56).addBox(1.5F, 1.5F, -15.5F, 1F, 3F, 1F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(52, 31).addBox(-3.5F, -6.5F, -3.5F, 7F, 7F, 7F).texOffs(148, 31).addBox(-4F, -8F, -4F, 8F, 2F, 8F).texOffs(212, 56).addBox(-3.5F, -11F, -3.5F, 1F, 3F, 1F).texOffs(216, 56).addBox(-1.5F, -11F, -3.5F, 1F, 3F, 1F).texOffs(220, 56).addBox(0.5F, -11F, -3.5F, 1F, 3F, 1F).texOffs(224, 56).addBox(2.5F, -11F, -3.5F, 1F, 3F, 1F).texOffs(80, 66).addBox(-0.5F, -12F, -3.5F, 1F, 2F, 1F).texOffs(180, 31).addBox(-3F, -6F, 3.5F, 6F, 8F, 2F), PartPose.offsetAndRotation(0F, -14F, -10F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(244, 31).addBox(-2F, 0F, -1.5F, 3F, 6F, 3F).texOffs(196, 31).addBox(-3.5F, -2F, -3F, 6F, 4F, 6F).texOffs(42, 47).addBox(-3.5F, -3.5F, -2F, 4F, 2F, 4F), PartPose.offsetAndRotation(-5F, -10F, -10F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(0, 47).addBox(-1F, 0F, -1F, 2F, 7F, 2F).texOffs(180, 56).addBox(-0.5F, 6F, -1.5F, 1F, 4F, 1F).texOffs(58, 47).addBox(-1.5F, 7F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(-0.5F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(8, 47).addBox(-1F, 0F, -1.5F, 3F, 6F, 3F).texOffs(220, 31).addBox(-2.5F, -2F, -3F, 6F, 4F, 6F).texOffs(70, 47).addBox(-0.5F, -3.5F, -2F, 4F, 2F, 4F), PartPose.offsetAndRotation(5F, -10F, -10F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(20, 47).addBox(-1F, 0F, -1F, 2F, 7F, 2F).texOffs(184, 56).addBox(-0.5F, 6F, -1.5F, 1F, 4F, 1F).texOffs(86, 47).addBox(-1.5F, 7F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(0.5F, 6F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 0).addBox(-8F, -9.5F, -1F, 16F, 13F, 18F).texOffs(0, 31).addBox(-6F, -11F, 1F, 12F, 2F, 14F).texOffs(28, 47).addBox(-3F, -6F, 17F, 6F, 6F, 1F).texOffs(228, 56).addBox(-1F, -13F, 1F, 2F, 2F, 2F).texOffs(236, 56).addBox(-1F, -13.3333F, 5F, 2F, 2F, 2F).texOffs(188, 56).addBox(-1F, -13.6667F, 9F, 2F, 3F, 2F).texOffs(196, 56).addBox(-1F, -14F, 13F, 2F, 3F, 2F), PartPose.offsetAndRotation(0F, -1F, 6F, -0.2618F, 0F, 0F));
		PartDefinition leg1 = body.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(98, 47).addBox(-12F, -1.5F, -1.5F, 12F, 3F, 3F).texOffs(128, 47).addBox(-12.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(-4F, 1F, -6F, 0F, 0.6981F, 0.5236F));
		leg1.addOrReplaceChild("leg1b", CubeListBuilder.create().texOffs(0, 62).addBox(-14F, -1F, -1F, 14F, 2F, 2F).texOffs(32, 62).addBox(-15F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-12F, 0F, 0F, 0F, 0F, -1.2217F));
		PartDefinition leg2 = body.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(140, 47).addBox(0F, -1.5F, -1.5F, 12F, 3F, 3F).texOffs(170, 47).addBox(9.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(4F, 1F, -6F, 0F, -0.6981F, -0.5236F));
		leg2.addOrReplaceChild("leg2b", CubeListBuilder.create().texOffs(40, 62).addBox(0F, -1F, -1F, 14F, 2F, 2F).texOffs(72, 62).addBox(13F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(12F, 0F, 0F, 0F, 0F, 1.2217F));
		PartDefinition leg3 = body.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(182, 47).addBox(-12F, -1.5F, -1.5F, 12F, 3F, 3F).texOffs(212, 47).addBox(-12.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(-4F, 1F, -2F, 0F, 0.2269F, 0.5236F));
		leg3.addOrReplaceChild("leg3b", CubeListBuilder.create().texOffs(80, 62).addBox(-14F, -1F, -1F, 14F, 2F, 2F).texOffs(112, 62).addBox(-15F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-12F, 0F, 0F, 0F, 0F, -1.2217F));
		PartDefinition leg4 = body.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(224, 47).addBox(0F, -1.5F, -1.5F, 12F, 3F, 3F).texOffs(0, 56).addBox(9.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(4F, 1F, -2F, 0F, -0.2269F, -0.5236F));
		leg4.addOrReplaceChild("leg4b", CubeListBuilder.create().texOffs(120, 62).addBox(0F, -1F, -1F, 14F, 2F, 2F).texOffs(152, 62).addBox(13F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(12F, 0F, 0F, 0F, 0F, 1.2217F));
		PartDefinition leg5 = body.addOrReplaceChild("leg5", CubeListBuilder.create().texOffs(12, 56).addBox(-12F, -1.5F, -1.5F, 12F, 3F, 3F).texOffs(42, 56).addBox(-12.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(-4F, 1F, 2F, 0F, -0.2443F, 0.5236F));
		leg5.addOrReplaceChild("leg5b", CubeListBuilder.create().texOffs(160, 62).addBox(-14F, -1F, -1F, 14F, 2F, 2F).texOffs(192, 62).addBox(-15F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-12F, 0F, 0F, 0F, 0F, -1.2217F));
		PartDefinition leg6 = body.addOrReplaceChild("leg6", CubeListBuilder.create().texOffs(54, 56).addBox(0F, -1.5F, -1.5F, 12F, 3F, 3F).texOffs(84, 56).addBox(9.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(4F, 1F, 2F, 0F, 0.2443F, -0.5236F));
		leg6.addOrReplaceChild("leg6b", CubeListBuilder.create().texOffs(200, 62).addBox(0F, -1F, -1F, 14F, 2F, 2F).texOffs(232, 62).addBox(13F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(12F, 0F, 0F, 0F, 0F, 1.2217F));
		PartDefinition leg7 = body.addOrReplaceChild("leg7", CubeListBuilder.create().texOffs(96, 56).addBox(-12F, -1.5F, -1.5F, 12F, 3F, 3F).texOffs(126, 56).addBox(-12.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(-4F, 1F, 6F, 0F, -0.7156F, 0.5236F));
		leg7.addOrReplaceChild("leg7b", CubeListBuilder.create().texOffs(0, 66).addBox(-14F, -1F, -1F, 14F, 2F, 2F).texOffs(32, 66).addBox(-15F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-12F, 0F, 0F, 0F, 0F, -1.2217F));
		PartDefinition leg8 = body.addOrReplaceChild("leg8", CubeListBuilder.create().texOffs(138, 56).addBox(0F, -1.5F, -1.5F, 12F, 3F, 3F).texOffs(168, 56).addBox(9.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(4F, 1F, 6F, 0F, 0.7156F, -0.5236F));
		leg8.addOrReplaceChild("leg8b", CubeListBuilder.create().texOffs(40, 66).addBox(0F, -1F, -1F, 14F, 2F, 2F).texOffs(72, 66).addBox(13F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(12F, 0F, 0F, 0F, 0F, 1.2217F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
