// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class SphinxModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "halo"},
			{"body", "halo", "ray0"},
			{"body", "halo", "ray1"},
			{"body", "halo", "ray2"},
			{"body", "halo", "ray3"},
			{"body", "halo", "ray4"},
			{"body", "halo", "ray5"},
			{"body", "halo", "ray6"},
			{"body", "halo", "ray7"},
			{"body", "halo", "ray8"},
			{"body", "halo", "ray9"},
			{"body", "right_wing"},
			{"body", "left_wing"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"}
	};

	public SphinxModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6F, -5F, -10F, 12F, 10F, 20F).texOffs(54, 30).addBox(-6F, 5F, -9F, 4F, 7F, 4F).texOffs(70, 30).addBox(2F, 5F, -9F, 4F, 7F, 4F).texOffs(86, 30).addBox(-6F, 5F, 5F, 4F, 7F, 4F).texOffs(102, 30).addBox(2F, 5F, 5F, 4F, 7F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(64, 0).addBox(-6F, -9F, -3F, 12F, 12F, 6F).texOffs(0, 30).addBox(-4F, -8F, -4F, 8F, 8F, 7F).texOffs(32, 56).addBox(-1F, 0F, -5F, 2F, 4F, 1F), PartPose.offsetAndRotation(0F, -4F, -10F, 0F, 0F, 0F));
		PartDefinition halo = body.addOrReplaceChild("halo", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -10F, -4F, 0F, 0F, 0F));
		halo.addOrReplaceChild("ray0", CubeListBuilder.create().texOffs(118, 45).addBox(-0.5F, -14F, -0.5F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		halo.addOrReplaceChild("ray1", CubeListBuilder.create().texOffs(122, 45).addBox(-0.5F, -14F, -0.5F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0.6283F));
		halo.addOrReplaceChild("ray2", CubeListBuilder.create().texOffs(0, 56).addBox(-0.5F, -14F, -0.5F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 1.2566F));
		halo.addOrReplaceChild("ray3", CubeListBuilder.create().texOffs(4, 56).addBox(-0.5F, -14F, -0.5F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 1.885F));
		halo.addOrReplaceChild("ray4", CubeListBuilder.create().texOffs(8, 56).addBox(-0.5F, -14F, -0.5F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 2.5133F));
		halo.addOrReplaceChild("ray5", CubeListBuilder.create().texOffs(12, 56).addBox(-0.5F, -14F, -0.5F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 3.1416F));
		halo.addOrReplaceChild("ray6", CubeListBuilder.create().texOffs(16, 56).addBox(-0.5F, -14F, -0.5F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 3.7699F));
		halo.addOrReplaceChild("ray7", CubeListBuilder.create().texOffs(20, 56).addBox(-0.5F, -14F, -0.5F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 4.3982F));
		halo.addOrReplaceChild("ray8", CubeListBuilder.create().texOffs(24, 56).addBox(-0.5F, -14F, -0.5F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 5.0265F));
		halo.addOrReplaceChild("ray9", CubeListBuilder.create().texOffs(28, 56).addBox(-0.5F, -14F, -0.5F, 1F, 5F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 5.6549F));
		body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 45).addBox(-16F, -1F, 0F, 16F, 1F, 10F), PartPose.offsetAndRotation(-5F, -5F, -4F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(52, 45).addBox(0F, -1F, 0F, 16F, 1F, 10F), PartPose.offsetAndRotation(5F, -5F, -4F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(30, 30).addBox(-1F, -1F, 0F, 2F, 2F, 10F), PartPose.offsetAndRotation(0F, -3F, 10F, 0F, 0F, 0F));
		tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(104, 45).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 4F), PartPose.offsetAndRotation(0F, 0F, 10F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
