// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class PrimaModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "ring"},
			{"body", "ring", "shard0"},
			{"body", "ring", "shard1"},
			{"body", "ring", "shard2"},
			{"body", "ring", "shard3"},
			{"body", "ring", "shard4"},
			{"body", "ring", "shard5"},
			{"body", "ring", "shard6"},
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
			{"body", "halo", "ray10"},
			{"body", "halo", "ray11"},
			{"body", "halo", "ray12"},
			{"body", "halo", "ray13"},
			{"body", "right_arm"},
			{"body", "left_arm"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"}
	};

	public PrimaModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -14F, -4F, 8F, 16F, 8F).texOffs(36, 36).addBox(-1.5F, -10F, -4.5F, 3F, 6F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(32, 0).addBox(-3F, -8F, -3F, 6F, 8F, 6F).texOffs(100, 36).addBox(-1F, -12F, -1F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, -14F, 0F, 0F, 0F, 0F));
		PartDefinition ring = body.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("shard0", CubeListBuilder.create().texOffs(48, 24).addBox(-1.5F, -4F, -1.5F, 3F, 8F, 3F), PartPose.offsetAndRotation(12F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("shard1", CubeListBuilder.create().texOffs(60, 24).addBox(-1.5F, -4F, -1.5F, 3F, 8F, 3F), PartPose.offsetAndRotation(7.4819F, 0F, 9.382F, 0F, 0F, 0F));
		ring.addOrReplaceChild("shard2", CubeListBuilder.create().texOffs(72, 24).addBox(-1.5F, -4F, -1.5F, 3F, 8F, 3F), PartPose.offsetAndRotation(-2.6703F, 0F, 11.6991F, 0F, 0F, 0F));
		ring.addOrReplaceChild("shard3", CubeListBuilder.create().texOffs(84, 24).addBox(-1.5F, -4F, -1.5F, 3F, 8F, 3F), PartPose.offsetAndRotation(-10.8116F, 0F, 5.2066F, 0F, 0F, 0F));
		ring.addOrReplaceChild("shard4", CubeListBuilder.create().texOffs(96, 24).addBox(-1.5F, -4F, -1.5F, 3F, 8F, 3F), PartPose.offsetAndRotation(-10.8116F, 0F, -5.2066F, 0F, 0F, 0F));
		ring.addOrReplaceChild("shard5", CubeListBuilder.create().texOffs(108, 24).addBox(-1.5F, -4F, -1.5F, 3F, 8F, 3F), PartPose.offsetAndRotation(-2.6703F, 0F, -11.6991F, 0F, 0F, 0F));
		ring.addOrReplaceChild("shard6", CubeListBuilder.create().texOffs(0, 36).addBox(-1.5F, -4F, -1.5F, 3F, 8F, 3F), PartPose.offsetAndRotation(7.4819F, 0F, -9.382F, 0F, 0F, 0F));
		PartDefinition halo = body.addOrReplaceChild("halo", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -8F, 5F, 0F, 0F, 0F));
		halo.addOrReplaceChild("ray0", CubeListBuilder.create().texOffs(44, 36).addBox(-0.5F, -16F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		halo.addOrReplaceChild("ray1", CubeListBuilder.create().texOffs(48, 36).addBox(-0.5F, -16F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0.4488F));
		halo.addOrReplaceChild("ray2", CubeListBuilder.create().texOffs(52, 36).addBox(-0.5F, -16F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0.8976F));
		halo.addOrReplaceChild("ray3", CubeListBuilder.create().texOffs(56, 36).addBox(-0.5F, -16F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 1.3464F));
		halo.addOrReplaceChild("ray4", CubeListBuilder.create().texOffs(60, 36).addBox(-0.5F, -16F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 1.7952F));
		halo.addOrReplaceChild("ray5", CubeListBuilder.create().texOffs(64, 36).addBox(-0.5F, -16F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 2.244F));
		halo.addOrReplaceChild("ray6", CubeListBuilder.create().texOffs(68, 36).addBox(-0.5F, -16F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 2.6928F));
		halo.addOrReplaceChild("ray7", CubeListBuilder.create().texOffs(72, 36).addBox(-0.5F, -16F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 3.1416F));
		halo.addOrReplaceChild("ray8", CubeListBuilder.create().texOffs(76, 36).addBox(-0.5F, -16F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 3.5904F));
		halo.addOrReplaceChild("ray9", CubeListBuilder.create().texOffs(80, 36).addBox(-0.5F, -16F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 4.0392F));
		halo.addOrReplaceChild("ray10", CubeListBuilder.create().texOffs(84, 36).addBox(-0.5F, -16F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 4.488F));
		halo.addOrReplaceChild("ray11", CubeListBuilder.create().texOffs(88, 36).addBox(-0.5F, -16F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 4.9368F));
		halo.addOrReplaceChild("ray12", CubeListBuilder.create().texOffs(92, 36).addBox(-0.5F, -16F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 5.3856F));
		halo.addOrReplaceChild("ray13", CubeListBuilder.create().texOffs(96, 36).addBox(-0.5F, -16F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 5.8344F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(56, 0).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(88, 0).addBox(-3F, 10F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(-9F, -10F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(72, 0).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(0, 24).addBox(-3F, 10F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(9F, -10F, 0F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(24, 24).addBox(-3F, 0F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(0F, 2F, 0F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(12, 36).addBox(-2F, 0F, -2F, 4F, 6F, 4F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(28, 36).addBox(-1F, 0F, -1F, 2F, 6F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
