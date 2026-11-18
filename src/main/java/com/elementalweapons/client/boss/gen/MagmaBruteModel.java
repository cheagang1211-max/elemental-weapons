// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
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
			{"body", "left_arm"},
			{"right_leg"},
			{"left_leg"}
	};

	public MagmaBruteModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 19).addBox(-6F, 0F, -3.5F, 12F, 11F, 7F).texOffs(52, 37).addBox(-2F, 3F, -4F, 4F, 4F, 1F), PartPose.offsetAndRotation(0F, 2F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(20, 37).addBox(-4F, -6F, -4F, 8F, 6F, 8F), PartPose.offsetAndRotation(0F, 2F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -2F, -2.5F, 4F, 14F, 5F), PartPose.offsetAndRotation(-7F, 4F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(18, 0).addBox(0F, -2F, -2.5F, 4F, 14F, 5F), PartPose.offsetAndRotation(7F, 4F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(38, 19).addBox(-2.5F, 0F, -2.5F, 5F, 11F, 5F), PartPose.offsetAndRotation(-3F, 13F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 37).addBox(-2.5F, 0F, -2.5F, 5F, 11F, 5F), PartPose.offsetAndRotation(3F, 13F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
