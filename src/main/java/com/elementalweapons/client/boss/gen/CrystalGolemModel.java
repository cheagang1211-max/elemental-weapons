// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class CrystalGolemModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "left_arm"},
			{"right_leg"},
			{"left_leg"}
	};

	public CrystalGolemModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(56, 0).addBox(-8F, -12F, -5F, 16F, 14F, 10F).texOffs(72, 48).addBox(-2F, -8F, -5.5F, 4F, 4F, 1F).texOffs(52, 48).addBox(-6F, -17F, -1F, 3F, 6F, 3F).texOffs(40, 48).addBox(2F, -18F, 0F, 3F, 7F, 3F).texOffs(28, 48).addBox(-1F, -20F, 1F, 3F, 9F, 3F).texOffs(64, 48).addBox(5F, -15F, -2F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 48).addBox(-3.5F, -6F, -4F, 7F, 6F, 7F), PartPose.offsetAndRotation(0F, -12F, -2F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 0).addBox(-6F, -2F, -3.5F, 7F, 18F, 7F).texOffs(56, 25).addBox(-6.5F, 14F, -4F, 8F, 6F, 8F), PartPose.offsetAndRotation(-9F, -10F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(28, 0).addBox(-1F, -2F, -3.5F, 7F, 18F, 7F).texOffs(88, 25).addBox(-1.5F, 14F, -4F, 8F, 6F, 8F), PartPose.offsetAndRotation(9F, -10F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 25).addBox(-3.5F, 0F, -3.5F, 7F, 16F, 7F), PartPose.offsetAndRotation(-4F, 8F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(28, 25).addBox(-3.5F, 0F, -3.5F, 7F, 16F, 7F), PartPose.offsetAndRotation(4F, 8F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
