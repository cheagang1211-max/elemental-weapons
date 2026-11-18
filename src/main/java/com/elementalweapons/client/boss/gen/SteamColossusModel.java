// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class SteamColossusModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "left_arm"},
			{"right_leg"},
			{"left_leg"}
	};

	public SteamColossusModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8F, -14F, -6F, 16F, 16F, 12F).texOffs(44, 48).addBox(-4F, -8F, -7F, 8F, 6F, 1F).texOffs(0, 48).addBox(-6F, -21F, -2F, 3F, 7F, 3F).texOffs(114, 28).addBox(3F, -23F, -1F, 3F, 9F, 3F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(84, 28).addBox(-4F, -6F, -4F, 8F, 6F, 7F), PartPose.offsetAndRotation(0F, -14F, -2F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(24, 28).addBox(-5F, -2F, -3F, 6F, 10F, 6F).texOffs(28, 48).addBox(-4F, 8F, -2F, 4F, 4F, 4F).texOffs(48, 28).addBox(-7F, 12F, -4F, 10F, 8F, 8F), PartPose.offsetAndRotation(-9F, -12F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 28).addBox(-1F, -2F, -3F, 6F, 14F, 6F).texOffs(12, 48).addBox(0F, 12F, -2F, 4F, 6F, 4F).texOffs(62, 48).addBox(0.5F, 17F, -1.5F, 3F, 2F, 3F), PartPose.offsetAndRotation(9F, -12F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(56, 0).addBox(-3.5F, 0F, -3.5F, 7F, 16F, 7F), PartPose.offsetAndRotation(-4F, 8F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(84, 0).addBox(-3.5F, 0F, -3.5F, 7F, 16F, 7F), PartPose.offsetAndRotation(4F, 8F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
