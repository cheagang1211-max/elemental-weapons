// 자동 생성 파일 (tools/bossgen/mobs2.py) - 직접 수정하지 마세요
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
			{"body", "swayE1"},
			{"body", "swayF1"},
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
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-10F, -9F, -10F, 20F, 18F, 20F).texOffs(36, 50).addBox(-2F, 1F, -11.4F, 4F, 2F, 2F).texOffs(32, 38).addBox(-3F, 1F, 9F, 6F, 6F, 4F).texOffs(80, 0).addBox(-11.6F, -2F, -5F, 2F, 8F, 10F).texOffs(104, 0).addBox(9.6F, -2F, -5F, 2F, 8F, 10F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("swayE1", CubeListBuilder.create().texOffs(0, 38).addBox(-2F, -8F, -2F, 4F, 8F, 4F).texOffs(20, 50).addBox(-1F, -7F, -3F, 2F, 6F, 2F), PartPose.offsetAndRotation(-6F, -10F, 0F, 0F, 0F, -0.2618F));
		body.addOrReplaceChild("swayF1", CubeListBuilder.create().texOffs(16, 38).addBox(-2F, -8F, -2F, 4F, 8F, 4F).texOffs(28, 50).addBox(-1F, -7F, -3F, 2F, 6F, 2F), PartPose.offsetAndRotation(6F, -10F, 0F, 0F, 0F, 0.2618F));
		root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(52, 38).addBox(-2F, 0F, -3F, 4F, 4F, 6F), PartPose.offsetAndRotation(-6F, 20F, -6F, 0F, 0F, 0F));
		root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(72, 38).addBox(-2F, 0F, -3F, 4F, 4F, 6F), PartPose.offsetAndRotation(6F, 20F, -6F, 0F, 0F, 0F));
		root.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(92, 38).addBox(-2F, 0F, -3F, 4F, 4F, 6F), PartPose.offsetAndRotation(-6F, 20F, 6F, 0F, 0F, 0F));
		root.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(0, 50).addBox(-2F, 0F, -3F, 4F, 4F, 6F), PartPose.offsetAndRotation(6F, 20F, 6F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
