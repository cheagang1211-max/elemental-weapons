// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
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
			{"body", "left_arm"},
			{"right_leg"},
			{"left_leg"}
	};

	public ShadowStalkerModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(24, 0).addBox(-4F, -10F, -2.5F, 8F, 12F, 5F).texOffs(38, 25).addBox(-1F, -9F, -3F, 2F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(10, 25).addBox(-3.5F, -7F, -3.5F, 7F, 7F, 7F), PartPose.offsetAndRotation(0F, -10F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(50, 0).addBox(-2F, 0F, -1.5F, 2F, 14F, 3F).texOffs(44, 25).addBox(-2.5F, 13F, -2F, 3F, 3F, 1F), PartPose.offsetAndRotation(-5F, -9F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 25).addBox(0F, 0F, -1.5F, 2F, 14F, 3F).texOffs(52, 25).addBox(-0.5F, 13F, -2F, 3F, 3F, 1F), PartPose.offsetAndRotation(5F, -9F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0F, -1.5F, 3F, 22F, 3F), PartPose.offsetAndRotation(-2F, 2F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(12, 0).addBox(-1.5F, 0F, -1.5F, 3F, 22F, 3F), PartPose.offsetAndRotation(2F, 2F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
