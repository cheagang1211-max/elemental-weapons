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
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-16F, -13F, -26F, 32F, 26F, 52F).texOffs(316, 0).addBox(-12F, 10F, -22F, 24F, 4F, 44F).texOffs(256, 216).addBox(-14F, -16F, -26F, 28F, 6F, 16F).texOffs(80, 156).addBox(-18F, -12F, -24F, 8F, 16F, 16F).texOffs(128, 156).addBox(10F, -12F, -24F, 8F, 16F, 16F).texOffs(168, 0).addBox(-17F, -2F, -20F, 34F, 12F, 40F).texOffs(460, 264).addBox(-2F, -17F, -24F, 4F, 4F, 4F).texOffs(476, 264).addBox(-2F, -18F, -16.6667F, 4F, 4F, 4F).texOffs(444, 264).addBox(-2F, -19F, -9.3333F, 4F, 6F, 4F).texOffs(308, 264).addBox(-2F, -20F, -2F, 4F, 8F, 4F).texOffs(324, 264).addBox(-2F, -21F, 5.3333F, 4F, 8F, 4F).texOffs(340, 264).addBox(-2F, -22F, 12.6667F, 4F, 8F, 4F).texOffs(252, 264).addBox(-2F, -23F, 20F, 4F, 10F, 4F), PartPose.offsetAndRotation(0F, -8F, 0F, 0F, 0F, 0F));
		PartDefinition neck1 = body.addOrReplaceChild("neck1", CubeListBuilder.create().texOffs(320, 156).addBox(-8F, -8F, -12F, 16F, 16F, 12F).texOffs(368, 282).addBox(-1F, -12F, -3F, 2F, 4F, 2F).texOffs(376, 282).addBox(-1F, -12F, -11F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, -6F, -24F, -0.2094F, 0F, 0F));
		PartDefinition neck2 = neck1.addOrReplaceChild("neck2", CubeListBuilder.create().texOffs(0, 190).addBox(-7F, -7F, -12F, 14F, 14F, 12F).texOffs(384, 282).addBox(-1F, -11F, -3F, 2F, 4F, 2F).texOffs(392, 282).addBox(-1F, -11F, -11F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, 0F, -12F, 0F, 0F, 0F));
		PartDefinition neck3 = neck2.addOrReplaceChild("neck3", CubeListBuilder.create().texOffs(64, 216).addBox(-6F, -6F, -12F, 12F, 12F, 12F).texOffs(400, 282).addBox(-1F, -10F, -3F, 2F, 4F, 2F).texOffs(408, 282).addBox(-1F, -10F, -11F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, 0F, -12F, 0F, 0F, 0F));
		PartDefinition head = neck3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(320, 78).addBox(-10F, -10F, -18F, 20F, 16F, 20F).texOffs(52, 190).addBox(-8F, -4F, -32F, 16F, 10F, 16F).texOffs(52, 264).addBox(-11F, -9F, -18F, 22F, 4F, 12F).texOffs(176, 290).addBox(-5F, -5F, -33F, 2F, 2F, 2F).texOffs(184, 290).addBox(3F, -5F, -33F, 2F, 2F, 2F).texOffs(268, 264).addBox(-12F, -1F, -10F, 2F, 6F, 8F).texOffs(288, 264).addBox(10F, -1F, -10F, 2F, 6F, 8F).texOffs(416, 282).addBox(-7F, 6F, -31F, 2F, 4F, 2F).texOffs(424, 282).addBox(-3F, 6F, -31F, 2F, 4F, 2F).texOffs(432, 282).addBox(1F, 6F, -31F, 2F, 4F, 2F).texOffs(440, 282).addBox(5F, 6F, -31F, 2F, 4F, 2F).texOffs(240, 242).addBox(-6F, 4F, -28F, 12F, 2F, 16F), PartPose.offsetAndRotation(0F, 0F, -12F, 0F, 0F, 0F));
		head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(376, 156).addBox(-8F, 0F, -24F, 16F, 4F, 24F).texOffs(448, 282).addBox(-7F, -4F, -22F, 2F, 4F, 2F).texOffs(456, 282).addBox(-3F, -4F, -22F, 2F, 4F, 2F).texOffs(464, 282).addBox(1F, -4F, -22F, 2F, 4F, 2F).texOffs(472, 282).addBox(5F, -4F, -22F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, 6F, -4F, 0F, 0F, 0F));
		PartDefinition hornL1 = head.addOrReplaceChild("hornL1", CubeListBuilder.create().texOffs(356, 264).addBox(-2F, -8F, -2F, 4F, 8F, 4F), PartPose.offsetAndRotation(6F, -8F, 4F, -0.9599F, 0F, 0F));
		PartDefinition hornL2 = hornL1.addOrReplaceChild("hornL2", CubeListBuilder.create().texOffs(372, 264).addBox(-2F, -8F, -2F, 4F, 8F, 4F), PartPose.offsetAndRotation(0F, -7F, 0F, -0.6109F, 0F, 0F));
		hornL2.addOrReplaceChild("hornL3", CubeListBuilder.create().texOffs(492, 264).addBox(-1F, -6F, -1F, 2F, 6F, 2F), PartPose.offsetAndRotation(0F, -7F, 0F, -0.6109F, 0F, 0F));
		PartDefinition hornR1 = head.addOrReplaceChild("hornR1", CubeListBuilder.create().texOffs(388, 264).addBox(-2F, -8F, -2F, 4F, 8F, 4F), PartPose.offsetAndRotation(-6F, -8F, 4F, -0.9599F, 0F, 0F));
		PartDefinition hornR2 = hornR1.addOrReplaceChild("hornR2", CubeListBuilder.create().texOffs(404, 264).addBox(-2F, -8F, -2F, 4F, 8F, 4F), PartPose.offsetAndRotation(0F, -7F, 0F, -0.6109F, 0F, 0F));
		hornR2.addOrReplaceChild("hornR3", CubeListBuilder.create().texOffs(500, 264).addBox(-1F, -6F, -1F, 2F, 6F, 2F), PartPose.offsetAndRotation(0F, -7F, 0F, -0.6109F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(456, 156).addBox(-8F, -8F, 0F, 16F, 16F, 12F).texOffs(0, 282).addBox(-1F, -14F, 1F, 2F, 6F, 2F).texOffs(8, 282).addBox(-1F, -14F, 9F, 2F, 6F, 2F), PartPose.offsetAndRotation(0F, -4F, 24F, -0.1396F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(116, 190).addBox(-7F, -7F, 0F, 14F, 14F, 12F).texOffs(16, 282).addBox(-1F, -12.4F, 1F, 2F, 6F, 2F).texOffs(24, 282).addBox(-1F, -12.4F, 9F, 2F, 6F, 2F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(112, 216).addBox(-6F, -6F, 0F, 12F, 12F, 12F).texOffs(480, 282).addBox(-1F, -10.8F, 1F, 2F, 4F, 2F).texOffs(488, 282).addBox(-1F, -10.8F, 9F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(344, 216).addBox(-5F, -5F, 0F, 10F, 10F, 12F).texOffs(496, 282).addBox(-1F, -9.2F, 1F, 2F, 4F, 2F).texOffs(504, 282).addBox(-1F, -9.2F, 9F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition tail5 = tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(80, 242).addBox(-4F, -4F, 0F, 8F, 8F, 12F).texOffs(0, 290).addBox(-1F, -7.6F, 1F, 2F, 4F, 2F).texOffs(8, 290).addBox(-1F, -7.6F, 9F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition tail6 = tail5.addOrReplaceChild("tail6", CubeListBuilder.create().texOffs(296, 242).addBox(-3F, -3F, 0F, 6F, 6F, 12F).texOffs(16, 290).addBox(-1F, -6F, 1F, 2F, 4F, 2F).texOffs(24, 290).addBox(-1F, -6F, 9F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		tail6.addOrReplaceChild("tail7", CubeListBuilder.create().texOffs(120, 264).addBox(-2F, -2F, 0F, 4F, 4F, 12F).texOffs(192, 290).addBox(-1F, -4.4F, 1F, 2F, 2F, 2F).texOffs(200, 290).addBox(-1F, -4.4F, 9F, 2F, 2F, 2F).texOffs(152, 264).addBox(-5F, -4F, 8F, 10F, 8F, 8F).texOffs(420, 264).addBox(-3F, -7F, 13F, 6F, 6F, 6F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(32, 282).addBox(-34F, -2F, -2F, 34F, 4F, 4F).texOffs(400, 78).addBox(-34F, 0F, 2F, 8F, 2F, 34F).texOffs(168, 190).addBox(-25.5F, 0F, 2F, 8F, 2F, 24F).texOffs(232, 190).addBox(-17F, 0F, 2F, 8F, 2F, 24F).texOffs(0, 120).addBox(-8.5F, 0F, 2F, 8F, 2F, 34F), PartPose.offsetAndRotation(-12F, -12F, -12F, 0F, 0.1745F, -0.6981F));
		right_wing.addOrReplaceChild("right_wing2", CubeListBuilder.create().texOffs(108, 282).addBox(-34F, -2F, -2F, 34F, 4F, 4F).texOffs(0, 78).addBox(-18F, 0F, 2F, 2F, 2F, 40F).texOffs(168, 78).addBox(-23.1F, 0F, 2F, 2F, 2F, 36F).texOffs(252, 120).addBox(-28.2F, 0F, 2F, 2F, 2F, 32F).texOffs(320, 120).addBox(-34F, 0.6F, 2F, 8F, 2F, 32F).texOffs(296, 190).addBox(-25.5F, 0.6F, 2F, 8F, 2F, 24F).texOffs(176, 156).addBox(-17F, 0.6F, 2F, 8F, 2F, 28F).texOffs(388, 216).addBox(-8.5F, 0.6F, 2F, 8F, 2F, 20F).texOffs(184, 282).addBox(-38F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-34F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(200, 282).addBox(0F, -2F, -2F, 34F, 4F, 4F).texOffs(84, 120).addBox(0F, 0F, 2F, 8F, 2F, 34F).texOffs(360, 190).addBox(8.5F, 0F, 2F, 8F, 2F, 24F).texOffs(424, 190).addBox(17F, 0F, 2F, 8F, 2F, 24F).texOffs(168, 120).addBox(25.5F, 0F, 2F, 8F, 2F, 34F), PartPose.offsetAndRotation(12F, -12F, -12F, 0F, -0.1745F, 0.6981F));
		left_wing.addOrReplaceChild("left_wing2", CubeListBuilder.create().texOffs(276, 282).addBox(0F, -2F, -2F, 34F, 4F, 4F).texOffs(84, 78).addBox(16F, 0F, 2F, 2F, 2F, 40F).texOffs(244, 78).addBox(21.1F, 0F, 2F, 2F, 2F, 36F).texOffs(400, 120).addBox(26.2F, 0F, 2F, 2F, 2F, 32F).texOffs(0, 156).addBox(0F, 0.6F, 2F, 8F, 2F, 32F).texOffs(0, 216).addBox(8.5F, 0.6F, 2F, 8F, 2F, 24F).texOffs(248, 156).addBox(17F, 0.6F, 2F, 8F, 2F, 28F).texOffs(444, 216).addBox(25.5F, 0.6F, 2F, 8F, 2F, 20F).texOffs(352, 282).addBox(34F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(34F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(160, 216).addBox(-6F, 0F, -6F, 12F, 12F, 12F), PartPose.offsetAndRotation(-12F, 4F, 18F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(332, 242).addBox(-5F, 0F, -5F, 10F, 8F, 10F).texOffs(120, 242).addBox(-7F, 4F, -11F, 14F, 4F, 16F).texOffs(32, 290).addBox(-5F, 6F, -14F, 2F, 2F, 4F).texOffs(44, 290).addBox(-1F, 6F, -14F, 2F, 2F, 4F).texOffs(56, 290).addBox(3F, 6F, -14F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(208, 216).addBox(-6F, 0F, -6F, 12F, 12F, 12F), PartPose.offsetAndRotation(12F, 4F, 18F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(372, 242).addBox(-5F, 0F, -5F, 10F, 8F, 10F).texOffs(180, 242).addBox(-7F, 4F, -11F, 14F, 4F, 16F).texOffs(68, 290).addBox(-5F, 6F, -14F, 2F, 2F, 4F).texOffs(80, 290).addBox(-1F, 6F, -14F, 2F, 2F, 4F).texOffs(92, 290).addBox(3F, 6F, -14F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition front_right_leg = root.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(0, 242).addBox(-5F, 0F, -5F, 10F, 12F, 10F), PartPose.offsetAndRotation(-12F, 4F, -16F, 0F, 0F, 0F));
		front_right_leg.addOrReplaceChild("front_right_leg2", CubeListBuilder.create().texOffs(188, 264).addBox(-4F, 0F, -4F, 8F, 8F, 8F).texOffs(412, 242).addBox(-6F, 4F, -10F, 12F, 4F, 14F).texOffs(104, 290).addBox(-4.2F, 6F, -13F, 2F, 2F, 4F).texOffs(116, 290).addBox(-1F, 6F, -13F, 2F, 2F, 4F).texOffs(128, 290).addBox(2.2F, 6F, -13F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition front_left_leg = root.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(40, 242).addBox(-5F, 0F, -5F, 10F, 12F, 10F), PartPose.offsetAndRotation(12F, 4F, -16F, 0F, 0F, 0F));
		front_left_leg.addOrReplaceChild("front_left_leg2", CubeListBuilder.create().texOffs(220, 264).addBox(-4F, 0F, -4F, 8F, 8F, 8F).texOffs(0, 264).addBox(-6F, 4F, -10F, 12F, 4F, 14F).texOffs(140, 290).addBox(-4.2F, 6F, -13F, 2F, 2F, 4F).texOffs(152, 290).addBox(-1F, 6F, -13F, 2F, 2F, 4F).texOffs(164, 290).addBox(2.2F, 6F, -13F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
