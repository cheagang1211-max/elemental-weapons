// 자동 생성 파일 (tools/bossgen/mobs2.py) - 직접 수정하지 마세요
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
			{"body", "neck1"},
			{"body", "neck1", "head"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"},
			{"front_right_leg"},
			{"front_right_leg", "front_right_leg2"},
			{"front_left_leg"},
			{"front_left_leg", "front_left_leg2"}
	};

	public HornedDeerModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8F, -7F, -15F, 16F, 14F, 30F).texOffs(92, 0).addBox(-6F, 5.4F, -13F, 12F, 2F, 26F).texOffs(76, 60).addBox(-2F, -5F, 14F, 4F, 6F, 4F), PartPose.offsetAndRotation(0F, -4F, 0F, 0F, 0F, 0F));
		PartDefinition neck1 = body.addOrReplaceChild("neck1", CubeListBuilder.create().texOffs(212, 0).addBox(-4F, -10F, -6F, 8F, 12F, 8F), PartPose.offsetAndRotation(0F, -6F, -12F, -0.5236F, 0F, 0F));
		neck1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(168, 0).addBox(-5F, -9F, -10F, 10F, 10F, 12F).texOffs(36, 60).addBox(-3F, -3F, -14F, 6F, 6F, 6F).texOffs(92, 60).addBox(-7F, -10F, -2F, 2F, 4F, 4F).texOffs(104, 60).addBox(5F, -10F, -2F, 2F, 4F, 4F).texOffs(60, 60).addBox(-5F, -17F, -3F, 2F, 10F, 2F).texOffs(148, 60).addBox(-10F, -17F, -3F, 6F, 2F, 2F).texOffs(116, 60).addBox(-10F, -22F, -3F, 2F, 6F, 2F).texOffs(124, 60).addBox(-6F, -23F, -3F, 2F, 6F, 2F).texOffs(68, 60).addBox(3F, -17F, -3F, 2F, 10F, 2F).texOffs(164, 60).addBox(4F, -17F, -3F, 6F, 2F, 2F).texOffs(132, 60).addBox(8F, -22F, -3F, 2F, 6F, 2F).texOffs(140, 60).addBox(4F, -23F, -3F, 2F, 6F, 2F), PartPose.offsetAndRotation(0F, -10F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 44).addBox(-2F, 0F, -2F, 4F, 12F, 4F), PartPose.offsetAndRotation(-6F, 2F, 10F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(64, 44).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(80, 44).addBox(-4F, 6F, -8F, 8F, 4F, 10F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 44).addBox(-2F, 0F, -2F, 4F, 12F, 4F), PartPose.offsetAndRotation(6F, 2F, 10F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(116, 44).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(132, 44).addBox(-4F, 6F, -8F, 8F, 4F, 10F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition front_right_leg = root.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(32, 44).addBox(-2F, 0F, -2F, 4F, 12F, 4F), PartPose.offsetAndRotation(-6F, 2F, -10F, 0F, 0F, 0F));
		front_right_leg.addOrReplaceChild("front_right_leg2", CubeListBuilder.create().texOffs(168, 44).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(184, 44).addBox(-4F, 6F, -8F, 8F, 4F, 10F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition front_left_leg = root.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(48, 44).addBox(-2F, 0F, -2F, 4F, 12F, 4F), PartPose.offsetAndRotation(6F, 2F, -10F, 0F, 0F, 0F));
		front_left_leg.addOrReplaceChild("front_left_leg2", CubeListBuilder.create().texOffs(220, 44).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(0, 60).addBox(-4F, 6F, -8F, 8F, 4F, 10F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
