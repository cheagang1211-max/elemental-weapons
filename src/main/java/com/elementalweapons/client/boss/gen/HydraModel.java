// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class HydraModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "neck1"},
			{"body", "neck1", "head1"},
			{"body", "neck2"},
			{"body", "neck2", "head2"},
			{"body", "neck3"},
			{"body", "neck3", "head3"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"},
			{"right_leg"},
			{"left_leg"}
	};

	public HydraModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8F, -6F, -7F, 16F, 12F, 14F).texOffs(56, 26).addBox(-7F, 5F, -6F, 14F, 1F, 12F).texOffs(20, 52).addBox(-8F, 6F, 4F, 4F, 6F, 4F).texOffs(36, 52).addBox(4F, 6F, 4F, 4F, 6F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition neck1 = body.addOrReplaceChild("neck1", CubeListBuilder.create().texOffs(92, 0).addBox(-2F, -2F, -10F, 4F, 4F, 10F), PartPose.offsetAndRotation(-5F, -5F, -6F, -0.6109F, 0F, 0.3491F));
		neck1.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(24, 40).addBox(-3F, -3F, -6F, 6F, 5F, 6F).texOffs(72, 52).addBox(-4F, -5F, -2F, 8F, 2F, 4F).texOffs(24, 63).addBox(-2F, 0F, -9F, 4F, 2F, 3F), PartPose.offsetAndRotation(0F, 0F, -10F, 0F, 0F, 0F));
		PartDefinition neck2 = body.addOrReplaceChild("neck2", CubeListBuilder.create().texOffs(60, 0).addBox(-2F, -2F, -12F, 4F, 4F, 12F), PartPose.offsetAndRotation(0F, -6F, -6F, -0.7854F, 0F, 0F));
		neck2.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(48, 40).addBox(-3F, -3F, -6F, 6F, 5F, 6F).texOffs(96, 52).addBox(-4F, -5F, -2F, 8F, 2F, 4F), PartPose.offsetAndRotation(0F, 0F, -12F, 0F, 0F, 0F));
		PartDefinition neck3 = body.addOrReplaceChild("neck3", CubeListBuilder.create().texOffs(0, 26).addBox(-2F, -2F, -10F, 4F, 4F, 10F), PartPose.offsetAndRotation(5F, -5F, -6F, -0.6109F, 0F, -0.3491F));
		neck3.addOrReplaceChild("head3", CubeListBuilder.create().texOffs(72, 40).addBox(-3F, -3F, -6F, 6F, 5F, 6F).texOffs(0, 63).addBox(-4F, -5F, -2F, 8F, 2F, 4F).texOffs(38, 63).addBox(-2F, 0F, -9F, 4F, 2F, 3F), PartPose.offsetAndRotation(0F, 0F, -10F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(28, 26).addBox(-3F, -3F, 0F, 6F, 6F, 8F), PartPose.offsetAndRotation(0F, 0F, 7F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(0, 40).addBox(-2F, -2F, 0F, 4F, 4F, 8F), PartPose.offsetAndRotation(0F, 0F, 8F, 0F, 0F, 0F));
		tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(52, 52).addBox(-3F, -3F, 0F, 6F, 6F, 4F), PartPose.offsetAndRotation(0F, 0F, 8F, 0F, 0F, 0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(96, 40).addBox(-2.5F, 0F, -2.5F, 5F, 6F, 5F), PartPose.offsetAndRotation(-6F, 18F, -4F, 0F, 0F, 0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 52).addBox(-2.5F, 0F, -2.5F, 5F, 6F, 5F), PartPose.offsetAndRotation(6F, 18F, -4F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
