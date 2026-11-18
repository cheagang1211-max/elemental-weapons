// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class ThornBeastModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"head"},
			{"leg1"},
			{"leg2"},
			{"leg3"},
			{"leg4"}
	};

	public ThornBeastModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5F, -5F, -6F, 10F, 8F, 12F).texOffs(40, 33).addBox(-1F, -8F, -4F, 1F, 3F, 1F).texOffs(44, 33).addBox(2F, -8F, 0F, 1F, 3F, 1F).texOffs(48, 33).addBox(-3F, -8F, 2F, 1F, 3F, 1F), PartPose.offsetAndRotation(0F, 14F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 20).addBox(-4F, -4F, -6F, 8F, 7F, 6F).texOffs(12, 33).addBox(-5F, -6F, -3F, 10F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, -6F, 0F, 0F, 0F));
		root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(28, 20).addBox(-1.5F, 0F, -1.5F, 3F, 7F, 3F), PartPose.offsetAndRotation(-4F, 17F, -4F, 0F, 0F, 0F));
		root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(40, 20).addBox(-1.5F, 0F, -1.5F, 3F, 7F, 3F), PartPose.offsetAndRotation(4F, 17F, -4F, 0F, 0F, 0F));
		root.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(52, 20).addBox(-1.5F, 0F, -1.5F, 3F, 7F, 3F), PartPose.offsetAndRotation(-4F, 17F, 4F, 0F, 0F, 0F));
		root.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 33).addBox(-1.5F, 0F, -1.5F, 3F, 7F, 3F), PartPose.offsetAndRotation(4F, 17F, 4F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
