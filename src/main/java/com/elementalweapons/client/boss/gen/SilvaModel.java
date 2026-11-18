// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class SilvaModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "left_arm"},
			{"right_leg"},
			{"left_leg"}
	};

	public SilvaModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5F, -18F, -4F, 10F, 18F, 8F).texOffs(0, 48).addBox(-6F, -6F, -5F, 12F, 4F, 10F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(36, 0).addBox(-9F, -10F, -7F, 18F, 10F, 14F).texOffs(56, 26).addBox(-6F, -15F, -5F, 12F, 5F, 10F).texOffs(44, 48).addBox(-3F, -17F, -3F, 6F, 2F, 6F), PartPose.offsetAndRotation(0F, -18F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(24, 26).addBox(-3F, 0F, -2F, 4F, 14F, 4F).texOffs(68, 48).addBox(-5F, 12F, -1F, 3F, 4F, 2F).texOffs(78, 48).addBox(-1F, 13F, -1F, 2F, 4F, 2F), PartPose.offsetAndRotation(-6F, -15F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 26).addBox(-1F, 0F, -2F, 4F, 14F, 4F).texOffs(86, 48).addBox(2F, 12F, -1F, 3F, 4F, 2F).texOffs(96, 48).addBox(-1F, 13F, -1F, 2F, 4F, 2F), PartPose.offsetAndRotation(6F, -15F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(100, 0).addBox(-3F, 0F, -3F, 6F, 16F, 6F).texOffs(104, 48).addBox(-4F, 14F, -5F, 8F, 2F, 3F), PartPose.offsetAndRotation(-3F, 8F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 26).addBox(-3F, 0F, -3F, 6F, 16F, 6F).texOffs(0, 62).addBox(-4F, 14F, -5F, 8F, 2F, 3F), PartPose.offsetAndRotation(3F, 8F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
