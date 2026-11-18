// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class FluffleModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"leg1"},
			{"leg2"},
			{"leg3"},
			{"leg4"}
	};

	public FluffleModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5F, -5F, -5F, 10F, 10F, 10F).texOffs(40, 0).addBox(-4F, -8F, -1F, 2F, 3F, 2F).texOffs(48, 0).addBox(2F, -8F, -1F, 2F, 3F, 2F), PartPose.offsetAndRotation(0F, 17F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(56, 0).addBox(-1F, 0F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-3F, 22F, -3F, 0F, 0F, 0F));
		root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 20).addBox(-1F, 0F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(3F, 22F, -3F, 0F, 0F, 0F));
		root.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(8, 20).addBox(-1F, 0F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-3F, 22F, 3F, 0F, 0F, 0F));
		root.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(16, 20).addBox(-1F, 0F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(3F, 22F, 3F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
