// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class NoxModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "left_arm"},
			{"body", "halo"},
			{"body", "halo", "ray0"},
			{"body", "halo", "ray1"},
			{"body", "halo", "ray2"},
			{"body", "halo", "ray3"},
			{"body", "halo", "ray4"},
			{"body", "halo", "ray5"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"}
	};

	public NoxModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(4, 0).addBox(-6F, -10F, -4F, 12F, 12F, 8F).texOffs(78, 31).addBox(-2F, -9F, -4.5F, 4F, 10F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(44, 0).addBox(-5F, -10F, -5F, 10F, 10F, 10F).texOffs(116, 48).addBox(-3.5F, -13F, -1F, 1F, 3F, 1F).texOffs(112, 48).addBox(-0.5F, -14F, -1F, 1F, 4F, 1F).texOffs(120, 48).addBox(2.5F, -13F, -1F, 1F, 3F, 1F), PartPose.offsetAndRotation(0F, -10F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(84, 0).addBox(-3F, -1F, -2.5F, 5F, 12F, 5F).texOffs(88, 48).addBox(-2.5F, 11F, -1.5F, 3F, 3F, 3F).texOffs(0, 0).addBox(-1.5F, -14F, -0.5F, 1F, 30F, 1F).texOffs(0, 59).addBox(-12F, -15F, -0.5F, 11F, 2F, 1F).texOffs(24, 59).addBox(-12F, -13F, -0.5F, 4F, 1F, 1F), PartPose.offsetAndRotation(-7F, -8F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(104, 0).addBox(-2F, -1F, -2.5F, 5F, 12F, 5F).texOffs(100, 48).addBox(-0.5F, 11F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(7F, -8F, 0F, 0F, 0F, 0F));
		PartDefinition halo = body.addOrReplaceChild("halo", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -10F, 6F, 0F, 0F, 0F));
		halo.addOrReplaceChild("ray0", CubeListBuilder.create().texOffs(64, 48).addBox(-0.5F, -15F, -0.5F, 1F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		halo.addOrReplaceChild("ray1", CubeListBuilder.create().texOffs(68, 48).addBox(-0.5F, -15F, -0.5F, 1F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 1.0472F));
		halo.addOrReplaceChild("ray2", CubeListBuilder.create().texOffs(72, 48).addBox(-0.5F, -15F, -0.5F, 1F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 2.0944F));
		halo.addOrReplaceChild("ray3", CubeListBuilder.create().texOffs(76, 48).addBox(-0.5F, -15F, -0.5F, 1F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 3.1416F));
		halo.addOrReplaceChild("ray4", CubeListBuilder.create().texOffs(80, 48).addBox(-0.5F, -15F, -0.5F, 1F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 4.1888F));
		halo.addOrReplaceChild("ray5", CubeListBuilder.create().texOffs(84, 48).addBox(-0.5F, -15F, -0.5F, 1F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 5.236F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 31).addBox(-6.5F, 0F, -4.5F, 13F, 8F, 9F).texOffs(0, 48).addBox(-6.6F, 6F, -4.6F, 13F, 2F, 9F), PartPose.offsetAndRotation(0F, 2F, 0F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(44, 31).addBox(-5F, 0F, -3.5F, 10F, 8F, 7F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(44, 48).addBox(-3F, 0F, -2F, 6F, 6F, 4F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
