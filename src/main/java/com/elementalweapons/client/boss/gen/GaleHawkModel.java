// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class GaleHawkModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"head"},
			{"right_wing"},
			{"left_wing"}
	};

	public GaleHawkModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3F, -2F, -4F, 6F, 4F, 9F).texOffs(0, 20).addBox(-2F, -1F, 5F, 4F, 1F, 4F), PartPose.offsetAndRotation(0F, 16F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(30, 0).addBox(-2F, -3F, -3F, 4F, 4F, 4F).texOffs(40, 20).addBox(-1F, -1F, -5F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, 14F, -4F, 0F, 0F, 0F));
		root.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 13).addBox(-10F, 0F, -2F, 10F, 1F, 6F).texOffs(16, 20).addBox(-12F, 0F, -1F, 2F, 1F, 4F), PartPose.offsetAndRotation(-3F, 15F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(32, 13).addBox(0F, 0F, -2F, 10F, 1F, 6F).texOffs(28, 20).addBox(10F, 0F, -1F, 2F, 1F, 4F), PartPose.offsetAndRotation(3F, 15F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
