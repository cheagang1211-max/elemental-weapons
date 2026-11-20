// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
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
			{"body", "right_wing"},
			{"body", "right_wing", "right_wing2"},
			{"body", "left_wing"},
			{"body", "left_wing", "left_wing2"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"},
			{"body", "tail1", "tail2", "tail3", "tail4"},
			{"body", "tail1", "tail2", "tail3", "tail4", "tail5"},
			{"halo"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"},
			{"front_right_leg"},
			{"front_right_leg", "front_right_leg2"},
			{"front_left_leg"},
			{"front_left_leg", "front_left_leg2"}
	};

	public SphinxModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7F, -6F, -12F, 14F, 12F, 24F).texOffs(196, 36).addBox(-7F, -9F, -14F, 14F, 10F, 6F).texOffs(212, 53).addBox(-6F, -9F, -15F, 12F, 6F, 6F).texOffs(154, 81).addBox(-2F, -8F, -15.7F, 4F, 4F, 1F).texOffs(76, 0).addBox(-5F, 6F, -10F, 10F, 1F, 20F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 53).addBox(-4F, -8F, -5F, 8F, 8F, 8F).texOffs(164, 81).addBox(-1F, 0F, -5.5F, 2F, 4F, 1F).texOffs(212, 0).addBox(-5.5F, -9.5F, -3.5F, 11F, 9F, 9F).texOffs(0, 69).addBox(-6.5F, -3F, -3F, 2F, 8F, 4F).texOffs(12, 69).addBox(4.5F, -3F, -3F, 2F, 8F, 4F).texOffs(104, 69).addBox(-5F, -10F, -4F, 10F, 1F, 8F).texOffs(170, 81).addBox(-1F, -11.5F, -5F, 2F, 3F, 1F).texOffs(84, 81).addBox(-1.5F, 1F, 3.5F, 3F, 6F, 2F), PartPose.offsetAndRotation(0F, -10F, -13F, 0F, 0F, 0F));
		PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(176, 81).addBox(-16F, -1F, -1F, 16F, 2F, 2F).texOffs(0, 36).addBox(-16F, 0F, 1F, 16F, 1F, 16F), PartPose.offsetAndRotation(-6F, -6F, -4F, -0.3491F, -0.3491F, -0.8727F));
		right_wing.addOrReplaceChild("right_wing2", CubeListBuilder.create().texOffs(212, 81).addBox(-16F, -1F, -1F, 16F, 2F, 2F).texOffs(136, 0).addBox(-8.5F, 0F, 1F, 1F, 1F, 18F).texOffs(64, 36).addBox(-10.9F, 0F, 1F, 1F, 1F, 16F).texOffs(32, 53).addBox(-13.3F, 0F, 1F, 1F, 1F, 14F).texOffs(62, 53).addBox(-16F, 0.3F, 1F, 16F, 1F, 14F), PartPose.offsetAndRotation(-16F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 90).addBox(0F, -1F, -1F, 16F, 2F, 2F).texOffs(98, 36).addBox(0F, 0F, 1F, 16F, 1F, 16F), PartPose.offsetAndRotation(6F, -6F, -4F, -0.3491F, 0.3491F, 0.8727F));
		left_wing.addOrReplaceChild("left_wing2", CubeListBuilder.create().texOffs(36, 90).addBox(0F, -1F, -1F, 16F, 2F, 2F).texOffs(174, 0).addBox(7.5F, 0F, 1F, 1F, 1F, 18F).texOffs(162, 36).addBox(9.9F, 0F, 1F, 1F, 1F, 16F).texOffs(122, 53).addBox(12.3F, 0F, 1F, 1F, 1F, 14F).texOffs(152, 53).addBox(0F, 0.3F, 1F, 16F, 1F, 14F), PartPose.offsetAndRotation(16F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(94, 81).addBox(-1F, -1F, 0F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, -2F, 12F, -0.5236F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(106, 81).addBox(-1F, -1F, 0F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 0F, 4F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(118, 81).addBox(-1F, -1F, 0F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 0F, 4F, 0F, 0F, 0F));
		PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(130, 81).addBox(-1F, -1F, 0F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 0F, 4F, 0F, 0F, 0F));
		tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(142, 81).addBox(-1F, -1F, 0F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 0F, 4F, 0F, 0F, 0F));
		root.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(140, 69).addBox(-4F, -4F, -0.5F, 8F, 8F, 1F).texOffs(72, 90).addBox(6F, -1F, -0.5F, 2F, 2F, 1F).texOffs(78, 90).addBox(3.9497F, 3.9497F, -0.5F, 2F, 2F, 1F).texOffs(84, 90).addBox(-1F, 6F, -0.5F, 2F, 2F, 1F).texOffs(90, 90).addBox(-5.9497F, 3.9497F, -0.5F, 2F, 2F, 1F).texOffs(96, 90).addBox(-8F, -1F, -0.5F, 2F, 2F, 1F).texOffs(102, 90).addBox(-5.9497F, -5.9497F, -0.5F, 2F, 2F, 1F).texOffs(108, 90).addBox(-1F, -8F, -0.5F, 2F, 2F, 1F).texOffs(114, 90).addBox(3.9497F, -5.9497F, -0.5F, 2F, 2F, 1F), PartPose.offsetAndRotation(0F, -26F, -9F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(24, 69).addBox(-2.5F, 0F, -2.5F, 5F, 6F, 5F), PartPose.offsetAndRotation(-5F, 13F, 9F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(158, 69).addBox(-2F, 0F, -2F, 4F, 5F, 4F).texOffs(174, 69).addBox(-3F, 3F, -5F, 6F, 2F, 7F).texOffs(120, 90).addBox(-2.1F, 4F, -6.5F, 1F, 1F, 2F).texOffs(126, 90).addBox(-0.5F, 4F, -6.5F, 1F, 1F, 2F).texOffs(132, 90).addBox(1.1F, 4F, -6.5F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(44, 69).addBox(-2.5F, 0F, -2.5F, 5F, 6F, 5F), PartPose.offsetAndRotation(5F, 13F, 9F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(200, 69).addBox(-2F, 0F, -2F, 4F, 5F, 4F).texOffs(216, 69).addBox(-3F, 3F, -5F, 6F, 2F, 7F).texOffs(138, 90).addBox(-2.1F, 4F, -6.5F, 1F, 1F, 2F).texOffs(144, 90).addBox(-0.5F, 4F, -6.5F, 1F, 1F, 2F).texOffs(150, 90).addBox(1.1F, 4F, -6.5F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition front_right_leg = root.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(64, 69).addBox(-2.5F, 0F, -2.5F, 5F, 6F, 5F), PartPose.offsetAndRotation(-5F, 13F, -9F, 0F, 0F, 0F));
		front_right_leg.addOrReplaceChild("front_right_leg2", CubeListBuilder.create().texOffs(0, 81).addBox(-2F, 0F, -2F, 4F, 5F, 4F).texOffs(16, 81).addBox(-3F, 3F, -5F, 6F, 2F, 7F).texOffs(156, 90).addBox(-2.1F, 4F, -6.5F, 1F, 1F, 2F).texOffs(162, 90).addBox(-0.5F, 4F, -6.5F, 1F, 1F, 2F).texOffs(168, 90).addBox(1.1F, 4F, -6.5F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition front_left_leg = root.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(84, 69).addBox(-2.5F, 0F, -2.5F, 5F, 6F, 5F), PartPose.offsetAndRotation(5F, 13F, -9F, 0F, 0F, 0F));
		front_left_leg.addOrReplaceChild("front_left_leg2", CubeListBuilder.create().texOffs(42, 81).addBox(-2F, 0F, -2F, 4F, 5F, 4F).texOffs(58, 81).addBox(-3F, 3F, -5F, 6F, 2F, 7F).texOffs(174, 90).addBox(-2.1F, 4F, -6.5F, 1F, 1F, 2F).texOffs(180, 90).addBox(-0.5F, 4F, -6.5F, 1F, 1F, 2F).texOffs(186, 90).addBox(1.1F, 4F, -6.5F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
