// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class SparkWispModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"head"},
			{"ring"},
			{"ring", "orb0"},
			{"ring", "orb1"},
			{"ring", "orb2"},
			{"ring", "orb3"},
			{"ring", "orb4"},
			{"ring", "orb5"},
			{"tail1"},
			{"tail1", "tail2"}
	};

	public SparkWispModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3F, -3F, -3F, 6F, 6F, 6F).texOffs(36, 0).addBox(-1.5F, -1.5F, -3.5F, 3F, 3F, 1F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition ring = root.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb0", CubeListBuilder.create().texOffs(48, 0).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(5F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb1", CubeListBuilder.create().texOffs(52, 0).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(2.5F, 0F, 4.3301F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb2", CubeListBuilder.create().texOffs(56, 0).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(-2.5F, 0F, 4.3301F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb3", CubeListBuilder.create().texOffs(60, 0).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(-5F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb4", CubeListBuilder.create().texOffs(0, 12).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(-2.5F, 0F, -4.3301F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb5", CubeListBuilder.create().texOffs(4, 12).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(2.5F, 0F, -4.3301F, 0F, 0F, 0F));
		PartDefinition tail1 = root.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(24, 0).addBox(-1.5F, 0F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(0F, 13F, 0F, 0F, 0F, 0F));
		tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(44, 0).addBox(-0.5F, 0F, -0.5F, 1F, 3F, 1F), PartPose.offsetAndRotation(0F, 3F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 64, 64);
	}
}
