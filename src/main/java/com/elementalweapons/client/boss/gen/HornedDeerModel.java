// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class HornedDeerModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"leg1"},
			{"leg2"},
			{"leg3"},
			{"leg4"}
	};

	public HornedDeerModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -4F, -8F, 8F, 7F, 16F).texOffs(0, 23).addBox(-3F, 2F, -7F, 6F, 2F, 14F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(8, 39).addBox(-2.5F, -5F, -5F, 5F, 5F, 6F).texOffs(38, 39).addBox(-1.5F, -2F, -7F, 3F, 2F, 2F).texOffs(30, 39).addBox(-3F, -9F, -1F, 1F, 4F, 1F).texOffs(34, 39).addBox(2F, -9F, -1F, 1F, 4F, 1F).texOffs(48, 39).addBox(-5F, -10F, -1F, 2F, 1F, 1F).texOffs(54, 39).addBox(3F, -10F, -1F, 2F, 1F, 1F), PartPose.offsetAndRotation(0F, 4F, -7F, 0F, 0F, 0F));
		root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(40, 23).addBox(-1F, 0F, -1F, 2F, 11F, 2F), PartPose.offsetAndRotation(-3F, 13F, -6F, 0F, 0F, 0F));
		root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(48, 23).addBox(-1F, 0F, -1F, 2F, 11F, 2F), PartPose.offsetAndRotation(3F, 13F, -6F, 0F, 0F, 0F));
		root.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(56, 23).addBox(-1F, 0F, -1F, 2F, 11F, 2F), PartPose.offsetAndRotation(-3F, 13F, 6F, 0F, 0F, 0F));
		root.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 39).addBox(-1F, 0F, -1F, 2F, 11F, 2F), PartPose.offsetAndRotation(3F, 13F, 6F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
