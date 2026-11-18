// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
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
			{"body", "head", "jaw"},
			{"body", "tail1"},
			{"body", "leg1"},
			{"body", "leg1", "foot1"},
			{"body", "leg5"},
			{"body", "leg5", "foot5"},
			{"body", "leg2"},
			{"body", "leg2", "foot2"},
			{"body", "leg6"},
			{"body", "leg6", "foot6"},
			{"body", "leg3"},
			{"body", "leg3", "foot3"},
			{"body", "leg7"},
			{"body", "leg7", "foot7"},
			{"body", "leg4"},
			{"body", "leg4", "foot4"},
			{"body", "leg8"},
			{"body", "leg8", "foot8"}
	};

	public LavaQueenModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(56, 0).addBox(-5F, -4F, -5F, 10F, 7F, 10F), PartPose.offsetAndRotation(0F, 14F, 0F, 0F, 0F, 0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(96, 0).addBox(-4F, -3F, -6F, 8F, 6F, 6F).texOffs(84, 40).addBox(-3F, -5F, -4F, 1F, 2F, 1F).texOffs(80, 24).addBox(-0.5F, -6F, -4F, 1F, 3F, 1F).texOffs(88, 40).addBox(2F, -5F, -4F, 1F, 2F, 1F), PartPose.offsetAndRotation(0F, -1F, -5F, 0F, 0F, 0F));
		head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(64, 24).addBox(-3F, 0F, -2F, 2F, 3F, 2F).texOffs(72, 24).addBox(1F, 0F, -2F, 2F, 3F, 2F), PartPose.offsetAndRotation(0F, 2F, -6F, 0F, 0F, 0F));
		body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 0).addBox(-7F, -5F, 0F, 14F, 10F, 14F), PartPose.offsetAndRotation(0F, -1F, 5F, 0F, 0F, 0F));
		PartDefinition leg1 = body.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(84, 24).addBox(-12F, -1F, -1F, 12F, 2F, 2F), PartPose.offsetAndRotation(-5F, 0F, -3F, 0F, -0.5236F, 0.5236F));
		leg1.addOrReplaceChild("foot1", CubeListBuilder.create().texOffs(0, 24).addBox(-1F, 0F, -1F, 2F, 10F, 2F), PartPose.offsetAndRotation(-12F, 0F, 0F, 0F, 0F, 1.0472F));
		PartDefinition leg5 = body.addOrReplaceChild("leg5", CubeListBuilder.create().texOffs(0, 36).addBox(0F, -1F, -1F, 12F, 2F, 2F), PartPose.offsetAndRotation(5F, 0F, -3F, 0F, 0.5236F, -0.5236F));
		leg5.addOrReplaceChild("foot5", CubeListBuilder.create().texOffs(8, 24).addBox(-1F, 0F, -1F, 2F, 10F, 2F), PartPose.offsetAndRotation(12F, 0F, 0F, 0F, 0F, -1.0472F));
		PartDefinition leg2 = body.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(28, 36).addBox(-12F, -1F, -1F, 12F, 2F, 2F), PartPose.offsetAndRotation(-5F, 0F, -1F, 0F, -0.1745F, 0.5236F));
		leg2.addOrReplaceChild("foot2", CubeListBuilder.create().texOffs(16, 24).addBox(-1F, 0F, -1F, 2F, 10F, 2F), PartPose.offsetAndRotation(-12F, 0F, 0F, 0F, 0F, 1.0472F));
		PartDefinition leg6 = body.addOrReplaceChild("leg6", CubeListBuilder.create().texOffs(56, 36).addBox(0F, -1F, -1F, 12F, 2F, 2F), PartPose.offsetAndRotation(5F, 0F, -1F, 0F, 0.1745F, -0.5236F));
		leg6.addOrReplaceChild("foot6", CubeListBuilder.create().texOffs(24, 24).addBox(-1F, 0F, -1F, 2F, 10F, 2F), PartPose.offsetAndRotation(12F, 0F, 0F, 0F, 0F, -1.0472F));
		PartDefinition leg3 = body.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(84, 36).addBox(-12F, -1F, -1F, 12F, 2F, 2F), PartPose.offsetAndRotation(-5F, 0F, 1F, 0F, 0.1745F, 0.5236F));
		leg3.addOrReplaceChild("foot3", CubeListBuilder.create().texOffs(32, 24).addBox(-1F, 0F, -1F, 2F, 10F, 2F), PartPose.offsetAndRotation(-12F, 0F, 0F, 0F, 0F, 1.0472F));
		PartDefinition leg7 = body.addOrReplaceChild("leg7", CubeListBuilder.create().texOffs(0, 40).addBox(0F, -1F, -1F, 12F, 2F, 2F), PartPose.offsetAndRotation(5F, 0F, 1F, 0F, -0.1745F, -0.5236F));
		leg7.addOrReplaceChild("foot7", CubeListBuilder.create().texOffs(40, 24).addBox(-1F, 0F, -1F, 2F, 10F, 2F), PartPose.offsetAndRotation(12F, 0F, 0F, 0F, 0F, -1.0472F));
		PartDefinition leg4 = body.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(28, 40).addBox(-12F, -1F, -1F, 12F, 2F, 2F), PartPose.offsetAndRotation(-5F, 0F, 3F, 0F, 0.5236F, 0.5236F));
		leg4.addOrReplaceChild("foot4", CubeListBuilder.create().texOffs(48, 24).addBox(-1F, 0F, -1F, 2F, 10F, 2F), PartPose.offsetAndRotation(-12F, 0F, 0F, 0F, 0F, 1.0472F));
		PartDefinition leg8 = body.addOrReplaceChild("leg8", CubeListBuilder.create().texOffs(56, 40).addBox(0F, -1F, -1F, 12F, 2F, 2F), PartPose.offsetAndRotation(5F, 0F, 3F, 0F, -0.5236F, -0.5236F));
		leg8.addOrReplaceChild("foot8", CubeListBuilder.create().texOffs(56, 24).addBox(-1F, 0F, -1F, 2F, 10F, 2F), PartPose.offsetAndRotation(12F, 0F, 0F, 0F, 0F, -1.0472F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
