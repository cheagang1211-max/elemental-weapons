// 자동 생성 파일 (tools/bossgen/mobs2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class ThornBeastModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "head", "jaw"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"},
			{"front_right_leg"},
			{"front_right_leg", "front_right_leg2"},
			{"front_left_leg"},
			{"front_left_leg", "front_left_leg2"}
	};

	public ThornBeastModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-10F, -10F, -13F, 20F, 16F, 26F).texOffs(152, 0).addBox(-8F, 6F, -11F, 16F, 2F, 22F).texOffs(68, 72).addBox(-1F, -16F, -11F, 2F, 6F, 2F).texOffs(76, 72).addBox(-1F, -16.5F, -6F, 2F, 6F, 2F).texOffs(24, 72).addBox(-1F, -17F, -1F, 2F, 8F, 2F).texOffs(32, 72).addBox(-1F, -17.5F, 4F, 2F, 8F, 2F).texOffs(40, 72).addBox(-1F, -18F, 9F, 2F, 8F, 2F).texOffs(84, 72).addBox(-12F, -8F, 4F, 4F, 4F, 4F).texOffs(100, 72).addBox(8F, -6F, -8F, 4F, 4F, 4F), PartPose.offsetAndRotation(0F, 4F, 0F, 0F, 0F, 0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(92, 0).addBox(-8F, -9F, -13F, 16F, 12F, 14F).texOffs(0, 58).addBox(-10F, -12F, -7F, 20F, 4F, 10F).texOffs(116, 72).addBox(-10F, -13F, -1F, 2F, 6F, 2F).texOffs(124, 72).addBox(8F, -13F, -1F, 2F, 6F, 2F), PartPose.offsetAndRotation(0F, -2F, -14F, 0F, 0F, 0F));
		head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 42).addBox(-6F, 0F, -12F, 12F, 4F, 12F), PartPose.offsetAndRotation(0F, 3F, -2F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(156, 58).addBox(-3F, -3F, 0F, 6F, 6F, 6F), PartPose.offsetAndRotation(0F, -4F, 12F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(48, 72).addBox(-2F, -2F, 0F, 4F, 4F, 6F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(132, 72).addBox(-1F, -1F, 0F, 2F, 2F, 6F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(60, 58).addBox(-3F, 0F, -3F, 6F, 8F, 6F), PartPose.offsetAndRotation(-8F, 10F, 8F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(180, 58).addBox(-3F, 0F, -3F, 6F, 6F, 6F).texOffs(48, 42).addBox(-5F, 2F, -9F, 10F, 4F, 12F).texOffs(148, 72).addBox(-4F, 4F, -12F, 2F, 2F, 4F).texOffs(160, 72).addBox(-1F, 4F, -12F, 2F, 2F, 4F).texOffs(172, 72).addBox(2F, 4F, -12F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(84, 58).addBox(-3F, 0F, -3F, 6F, 8F, 6F), PartPose.offsetAndRotation(8F, 10F, 8F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(204, 58).addBox(-3F, 0F, -3F, 6F, 6F, 6F).texOffs(92, 42).addBox(-5F, 2F, -9F, 10F, 4F, 12F).texOffs(184, 72).addBox(-4F, 4F, -12F, 2F, 2F, 4F).texOffs(196, 72).addBox(-1F, 4F, -12F, 2F, 2F, 4F).texOffs(208, 72).addBox(2F, 4F, -12F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		PartDefinition front_right_leg = root.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(108, 58).addBox(-3F, 0F, -3F, 6F, 8F, 6F), PartPose.offsetAndRotation(-8F, 10F, -8F, 0F, 0F, 0F));
		front_right_leg.addOrReplaceChild("front_right_leg2", CubeListBuilder.create().texOffs(228, 58).addBox(-3F, 0F, -3F, 6F, 6F, 6F).texOffs(136, 42).addBox(-5F, 2F, -9F, 10F, 4F, 12F).texOffs(220, 72).addBox(-4F, 4F, -12F, 2F, 2F, 4F).texOffs(232, 72).addBox(-1F, 4F, -12F, 2F, 2F, 4F).texOffs(244, 72).addBox(2F, 4F, -12F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		PartDefinition front_left_leg = root.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(132, 58).addBox(-3F, 0F, -3F, 6F, 8F, 6F), PartPose.offsetAndRotation(8F, 10F, -8F, 0F, 0F, 0F));
		front_left_leg.addOrReplaceChild("front_left_leg2", CubeListBuilder.create().texOffs(0, 72).addBox(-3F, 0F, -3F, 6F, 6F, 6F).texOffs(180, 42).addBox(-5F, 2F, -9F, 10F, 4F, 12F).texOffs(0, 84).addBox(-4F, 4F, -12F, 2F, 2F, 4F).texOffs(12, 84).addBox(-1F, 4F, -12F, 2F, 2F, 4F).texOffs(24, 84).addBox(2F, 4F, -12F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
