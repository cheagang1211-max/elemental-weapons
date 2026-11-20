// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class IgnisModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "neck1"},
			{"body", "neck1", "neck2"},
			{"body", "neck1", "neck2", "neck3"},
			{"body", "neck1", "neck2", "neck3", "head"},
			{"body", "neck1", "neck2", "neck3", "head", "jaw"},
			{"body", "neck1", "neck2", "neck3", "head", "hornL1"},
			{"body", "neck1", "neck2", "neck3", "head", "hornL1", "hornL2"},
			{"body", "neck1", "neck2", "neck3", "head", "hornL1", "hornL2", "hornL3"},
			{"body", "neck1", "neck2", "neck3", "head", "hornR1"},
			{"body", "neck1", "neck2", "neck3", "head", "hornR1", "hornR2"},
			{"body", "neck1", "neck2", "neck3", "head", "hornR1", "hornR2", "hornR3"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"},
			{"body", "tail1", "tail2", "tail3", "tail4"},
			{"body", "tail1", "tail2", "tail3", "tail4", "tail5"},
			{"body", "tail1", "tail2", "tail3", "tail4", "tail5", "tail6"},
			{"body", "tail1", "tail2", "tail3", "tail4", "tail5", "tail6", "tail7"},
			{"body", "right_wing"},
			{"body", "right_wing", "right_wing2"},
			{"body", "left_wing"},
			{"body", "left_wing", "left_wing2"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"},
			{"front_right_leg"},
			{"front_right_leg", "front_right_leg2"},
			{"front_left_leg"},
			{"front_left_leg", "front_left_leg2"}
	};

	public IgnisModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8F, -6.5F, -13F, 16F, 13F, 26F).texOffs(158, 0).addBox(-6F, 5F, -11F, 12F, 2F, 22F).texOffs(180, 96).addBox(-7F, -8F, -13F, 14F, 3F, 8F).texOffs(100, 79).addBox(-9F, -6F, -12F, 4F, 8F, 8F).texOffs(124, 79).addBox(5F, -6F, -12F, 4F, 8F, 8F).texOffs(84, 0).addBox(-8.5F, -1F, -10F, 17F, 6F, 20F).texOffs(230, 120).addBox(-1F, -8.5F, -12F, 2F, 2F, 2F).texOffs(238, 120).addBox(-1F, -9F, -8.3333F, 2F, 2F, 2F).texOffs(222, 120).addBox(-1F, -9.5F, -4.6667F, 2F, 3F, 2F).texOffs(154, 120).addBox(-1F, -10F, -1F, 2F, 4F, 2F).texOffs(162, 120).addBox(-1F, -10.5F, 2.6667F, 2F, 4F, 2F).texOffs(170, 120).addBox(-1F, -11F, 6.3333F, 2F, 4F, 2F).texOffs(126, 120).addBox(-1F, -11.5F, 10F, 2F, 5F, 2F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		PartDefinition neck1 = body.addOrReplaceChild("neck1", CubeListBuilder.create().texOffs(148, 79).addBox(-4F, -4F, -6F, 8F, 8F, 6F).texOffs(184, 129).addBox(-0.5F, -6F, -1.5F, 1F, 2F, 1F).texOffs(188, 129).addBox(-0.5F, -6F, -5.5F, 1F, 2F, 1F), PartPose.offsetAndRotation(0F, -3F, -12F, -0.2094F, 0F, 0F));
		PartDefinition neck2 = neck1.addOrReplaceChild("neck2", CubeListBuilder.create().texOffs(0, 96).addBox(-3.5F, -3.5F, -6F, 7F, 7F, 6F).texOffs(192, 129).addBox(-0.5F, -5.5F, -1.5F, 1F, 2F, 1F).texOffs(196, 129).addBox(-0.5F, -5.5F, -5.5F, 1F, 2F, 1F), PartPose.offsetAndRotation(0F, 0F, -6F, 0F, 0F, 0F));
		PartDefinition neck3 = neck2.addOrReplaceChild("neck3", CubeListBuilder.create().texOffs(84, 96).addBox(-3F, -3F, -6F, 6F, 6F, 6F).texOffs(200, 129).addBox(-0.5F, -5F, -1.5F, 1F, 2F, 1F).texOffs(204, 129).addBox(-0.5F, -5F, -5.5F, 1F, 2F, 1F), PartPose.offsetAndRotation(0F, 0F, -6F, 0F, 0F, 0F));
		PartDefinition head = neck3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(108, 60).addBox(-5F, -5F, -9F, 10F, 8F, 10F).texOffs(26, 96).addBox(-4F, -2F, -16F, 8F, 5F, 8F).texOffs(26, 120).addBox(-5.5F, -4.5F, -9F, 11F, 2F, 6F).texOffs(88, 133).addBox(-2.5F, -2.5F, -16.5F, 1F, 1F, 1F).texOffs(92, 133).addBox(1.5F, -2.5F, -16.5F, 1F, 1F, 1F).texOffs(134, 120).addBox(-6F, -0.5F, -5F, 1F, 3F, 4F).texOffs(144, 120).addBox(5F, -0.5F, -5F, 1F, 3F, 4F).texOffs(208, 129).addBox(-3.5F, 3F, -15.5F, 1F, 2F, 1F).texOffs(212, 129).addBox(-1.5F, 3F, -15.5F, 1F, 2F, 1F).texOffs(216, 129).addBox(0.5F, 3F, -15.5F, 1F, 2F, 1F).texOffs(220, 129).addBox(2.5F, 3F, -15.5F, 1F, 2F, 1F).texOffs(120, 109).addBox(-3F, 2F, -14F, 6F, 1F, 8F), PartPose.offsetAndRotation(0F, 0F, -6F, 0F, 0F, 0F));
		head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(176, 79).addBox(-4F, 0F, -12F, 8F, 2F, 12F).texOffs(224, 129).addBox(-3.5F, -2F, -11F, 1F, 2F, 1F).texOffs(228, 129).addBox(-1.5F, -2F, -11F, 1F, 2F, 1F).texOffs(232, 129).addBox(0.5F, -2F, -11F, 1F, 2F, 1F).texOffs(236, 129).addBox(2.5F, -2F, -11F, 1F, 2F, 1F), PartPose.offsetAndRotation(0F, 3F, -2F, 0F, 0F, 0F));
		PartDefinition hornL1 = head.addOrReplaceChild("hornL1", CubeListBuilder.create().texOffs(178, 120).addBox(-1F, -4F, -1F, 2F, 4F, 2F), PartPose.offsetAndRotation(3F, -4F, 2F, -0.9599F, 0F, 0F));
		PartDefinition hornL2 = hornL1.addOrReplaceChild("hornL2", CubeListBuilder.create().texOffs(186, 120).addBox(-1F, -4F, -1F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, -3.5F, 0F, -0.6109F, 0F, 0F));
		hornL2.addOrReplaceChild("hornL3", CubeListBuilder.create().texOffs(246, 120).addBox(-0.5F, -3F, -0.5F, 1F, 3F, 1F), PartPose.offsetAndRotation(0F, -3.5F, 0F, -0.6109F, 0F, 0F));
		PartDefinition hornR1 = head.addOrReplaceChild("hornR1", CubeListBuilder.create().texOffs(194, 120).addBox(-1F, -4F, -1F, 2F, 4F, 2F), PartPose.offsetAndRotation(-3F, -4F, 2F, -0.9599F, 0F, 0F));
		PartDefinition hornR2 = hornR1.addOrReplaceChild("hornR2", CubeListBuilder.create().texOffs(202, 120).addBox(-1F, -4F, -1F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, -3.5F, 0F, -0.6109F, 0F, 0F));
		hornR2.addOrReplaceChild("hornR3", CubeListBuilder.create().texOffs(250, 120).addBox(-0.5F, -3F, -0.5F, 1F, 3F, 1F), PartPose.offsetAndRotation(0F, -3.5F, 0F, -0.6109F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(216, 79).addBox(-4F, -4F, 0F, 8F, 8F, 6F).texOffs(0, 129).addBox(-0.5F, -7F, 0.5F, 1F, 3F, 1F).texOffs(4, 129).addBox(-0.5F, -7F, 4.5F, 1F, 3F, 1F), PartPose.offsetAndRotation(0F, -2F, 12F, -0.1396F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(58, 96).addBox(-3.5F, -3.5F, 0F, 7F, 7F, 6F).texOffs(8, 129).addBox(-0.5F, -6.2F, 0.5F, 1F, 3F, 1F).texOffs(12, 129).addBox(-0.5F, -6.2F, 4.5F, 1F, 3F, 1F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(108, 96).addBox(-3F, -3F, 0F, 6F, 6F, 6F).texOffs(240, 129).addBox(-0.5F, -5.4F, 0.5F, 1F, 2F, 1F).texOffs(244, 129).addBox(-0.5F, -5.4F, 4.5F, 1F, 2F, 1F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(224, 96).addBox(-2.5F, -2.5F, 0F, 5F, 5F, 6F).texOffs(248, 129).addBox(-0.5F, -4.6F, 0.5F, 1F, 2F, 1F).texOffs(252, 129).addBox(-0.5F, -4.6F, 4.5F, 1F, 2F, 1F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition tail5 = tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(40, 109).addBox(-2F, -2F, 0F, 4F, 4F, 6F).texOffs(0, 133).addBox(-0.5F, -3.8F, 0.5F, 1F, 2F, 1F).texOffs(4, 133).addBox(-0.5F, -3.8F, 4.5F, 1F, 2F, 1F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition tail6 = tail5.addOrReplaceChild("tail6", CubeListBuilder.create().texOffs(148, 109).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 6F).texOffs(8, 133).addBox(-0.5F, -3F, 0.5F, 1F, 2F, 1F).texOffs(12, 133).addBox(-0.5F, -3F, 4.5F, 1F, 2F, 1F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		tail6.addOrReplaceChild("tail7", CubeListBuilder.create().texOffs(60, 120).addBox(-1F, -1F, 0F, 2F, 2F, 6F).texOffs(96, 133).addBox(-0.5F, -2.2F, 0.5F, 1F, 1F, 1F).texOffs(100, 133).addBox(-0.5F, -2.2F, 4.5F, 1F, 1F, 1F).texOffs(76, 120).addBox(-2.5F, -2F, 4F, 5F, 4F, 4F).texOffs(210, 120).addBox(-1.5F, -3.5F, 6.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(16, 129).addBox(-17F, -1F, -1F, 17F, 2F, 2F).texOffs(84, 39).addBox(-17F, 0F, 1F, 17F, 1F, 18F), PartPose.offsetAndRotation(-6F, -6F, -6F, 0F, 0.1745F, -0.6981F));
		right_wing.addOrReplaceChild("right_wing2", CubeListBuilder.create().texOffs(54, 129).addBox(-17F, -1F, -1F, 17F, 2F, 2F).texOffs(0, 39).addBox(-9F, 0F, 1F, 1F, 1F, 20F).texOffs(154, 39).addBox(-11.55F, 0F, 1F, 1F, 1F, 18F).texOffs(148, 60).addBox(-14.1F, 0F, 1F, 1F, 1F, 16F).texOffs(182, 60).addBox(-17F, 0.3F, 1F, 17F, 1F, 16F).texOffs(92, 129).addBox(-19F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-17F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(100, 129).addBox(0F, -1F, -1F, 17F, 2F, 2F).texOffs(0, 60).addBox(0F, 0F, 1F, 17F, 1F, 18F), PartPose.offsetAndRotation(6F, -6F, -6F, 0F, -0.1745F, 0.6981F));
		left_wing.addOrReplaceChild("left_wing2", CubeListBuilder.create().texOffs(138, 129).addBox(0F, -1F, -1F, 17F, 2F, 2F).texOffs(42, 39).addBox(8F, 0F, 1F, 1F, 1F, 20F).texOffs(70, 60).addBox(10.55F, 0F, 1F, 1F, 1F, 18F).texOffs(0, 79).addBox(13.1F, 0F, 1F, 1F, 1F, 16F).texOffs(34, 79).addBox(0F, 0.3F, 1F, 17F, 1F, 16F).texOffs(176, 129).addBox(17F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(17F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(132, 96).addBox(-3F, 0F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(-6F, 14F, 9F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(166, 109).addBox(-2.5F, 0F, -2.5F, 5F, 4F, 5F).texOffs(60, 109).addBox(-3.5F, 2F, -5.5F, 7F, 2F, 8F).texOffs(16, 133).addBox(-2.5F, 3F, -7F, 1F, 1F, 2F).texOffs(22, 133).addBox(-0.5F, 3F, -7F, 1F, 1F, 2F).texOffs(28, 133).addBox(1.5F, 3F, -7F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(156, 96).addBox(-3F, 0F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(6F, 14F, 9F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(186, 109).addBox(-2.5F, 0F, -2.5F, 5F, 4F, 5F).texOffs(90, 109).addBox(-3.5F, 2F, -5.5F, 7F, 2F, 8F).texOffs(34, 133).addBox(-2.5F, 3F, -7F, 1F, 1F, 2F).texOffs(40, 133).addBox(-0.5F, 3F, -7F, 1F, 1F, 2F).texOffs(46, 133).addBox(1.5F, 3F, -7F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition front_right_leg = root.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(0, 109).addBox(-2.5F, 0F, -2.5F, 5F, 6F, 5F), PartPose.offsetAndRotation(-6F, 14F, -8F, 0F, 0F, 0F));
		front_right_leg.addOrReplaceChild("front_right_leg2", CubeListBuilder.create().texOffs(94, 120).addBox(-2F, 0F, -2F, 4F, 4F, 4F).texOffs(206, 109).addBox(-3F, 2F, -5F, 6F, 2F, 7F).texOffs(52, 133).addBox(-2.1F, 3F, -6.5F, 1F, 1F, 2F).texOffs(58, 133).addBox(-0.5F, 3F, -6.5F, 1F, 1F, 2F).texOffs(64, 133).addBox(1.1F, 3F, -6.5F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition front_left_leg = root.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(20, 109).addBox(-2.5F, 0F, -2.5F, 5F, 6F, 5F), PartPose.offsetAndRotation(6F, 14F, -8F, 0F, 0F, 0F));
		front_left_leg.addOrReplaceChild("front_left_leg2", CubeListBuilder.create().texOffs(110, 120).addBox(-2F, 0F, -2F, 4F, 4F, 4F).texOffs(0, 120).addBox(-3F, 2F, -5F, 6F, 2F, 7F).texOffs(70, 133).addBox(-2.1F, 3F, -6.5F, 1F, 1F, 2F).texOffs(76, 133).addBox(-0.5F, 3F, -6.5F, 1F, 1F, 2F).texOffs(82, 133).addBox(1.1F, 3F, -6.5F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
