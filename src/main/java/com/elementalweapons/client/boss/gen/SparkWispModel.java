// 자동 생성 파일 (tools/bossgen/mobs2.py) - 직접 수정하지 마세요
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
			{"ring", "wo0"},
			{"ring", "wo1"},
			{"ring", "wo2"},
			{"ring", "wo3"},
			{"ring", "wo4"},
			{"ring", "wo5"},
			{"tail1"},
			{"tail1", "tail2"},
			{"tail1", "tail2", "tail3"}
	};

	public SparkWispModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-7F, -7F, -7F, 14F, 14F, 14F).texOffs(0, 28).addBox(-3F, -3F, -8.6F, 6F, 6F, 2F).texOffs(16, 28).addBox(-3F, -9F, -3F, 6F, 2F, 6F).texOffs(24, 36).addBox(-1F, -13F, -1F, 2F, 4F, 2F).texOffs(56, 0).addBox(-9F, -3F, -3F, 2F, 6F, 6F).texOffs(72, 0).addBox(7F, -3F, -3F, 2F, 6F, 6F), PartPose.offsetAndRotation(0F, -4F, 0F, 0F, 0F, 0F));
		PartDefinition ring = root.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -4F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("wo0", CubeListBuilder.create().texOffs(40, 28).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(12F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("wo1", CubeListBuilder.create().texOffs(56, 28).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(6F, 0F, 10.3923F, 0F, 1.0472F, 0F));
		ring.addOrReplaceChild("wo2", CubeListBuilder.create().texOffs(72, 28).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-6F, 0F, 10.3923F, 0F, 2.0944F, 0F));
		ring.addOrReplaceChild("wo3", CubeListBuilder.create().texOffs(88, 28).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-12F, 0F, 0F, 0F, 3.1416F, 0F));
		ring.addOrReplaceChild("wo4", CubeListBuilder.create().texOffs(104, 28).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-6F, 0F, -10.3923F, 0F, 4.1888F, 0F));
		ring.addOrReplaceChild("wo5", CubeListBuilder.create().texOffs(0, 36).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(6F, 0F, -10.3923F, 0F, 5.236F, 0F));
		PartDefinition tail1 = root.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(88, 0).addBox(-3F, 0F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(0F, 3F, 0F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(112, 0).addBox(-2F, 0F, -2F, 4F, 6F, 4F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(16, 36).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
