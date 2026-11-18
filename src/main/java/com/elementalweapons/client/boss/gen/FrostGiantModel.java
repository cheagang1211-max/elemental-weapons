// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class FrostGiantModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "left_arm"},
			{"right_leg"},
			{"left_leg"}
	};

	public FrostGiantModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7F, -14F, -4F, 14F, 14F, 8F).texOffs(78, 22).addBox(-7.5F, -2F, -4.5F, 15F, 4F, 9F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(46, 22).addBox(-4F, -8F, -4F, 8F, 8F, 8F).texOffs(52, 44).addBox(-4F, -3F, -5F, 8F, 6F, 2F).texOffs(80, 44).addBox(-3.5F, -11F, -1F, 1F, 3F, 1F).texOffs(72, 44).addBox(-1.5F, -12F, -1F, 1F, 4F, 1F).texOffs(76, 44).addBox(0.5F, -12F, -1F, 1F, 4F, 1F).texOffs(84, 44).addBox(2.5F, -11F, -1F, 1F, 3F, 1F), PartPose.offsetAndRotation(0F, -14F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(44, 0).addBox(-5F, -2F, -3F, 5F, 16F, 6F).texOffs(0, 44).addBox(-5.5F, -3F, -3.5F, 6F, 5F, 7F).texOffs(24, 22).addBox(-5.5F, 12F, -2.5F, 6F, 12F, 5F), PartPose.offsetAndRotation(-8F, -12F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(66, 0).addBox(0F, -2F, -3F, 5F, 16F, 6F).texOffs(26, 44).addBox(-0.5F, -3F, -3.5F, 6F, 5F, 7F), PartPose.offsetAndRotation(8F, -12F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(88, 0).addBox(-3F, 0F, -3F, 6F, 16F, 6F), PartPose.offsetAndRotation(-3.5F, 8F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-3F, 0F, -3F, 6F, 16F, 6F), PartPose.offsetAndRotation(3.5F, 8F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
