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
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-14F, -12F, -24F, 28F, 24F, 48F).texOffs(136, 72).addBox(-14F, -18F, -28F, 28F, 20F, 12F).texOffs(0, 138).addBox(-12F, -18F, -30F, 24F, 12F, 12F).texOffs(360, 184).addBox(-4F, -16F, -31.4F, 8F, 8F, 2F).texOffs(152, 0).addBox(-10F, 12F, -20F, 20F, 2F, 40F), PartPose.offsetAndRotation(0F, -8F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(216, 72).addBox(-8F, -16F, -10F, 16F, 16F, 16F).texOffs(380, 184).addBox(-2F, 0F, -11F, 4F, 8F, 2F).texOffs(424, 0).addBox(-11F, -19F, -7F, 22F, 18F, 18F).texOffs(72, 138).addBox(-13F, -6F, -6F, 4F, 16F, 8F).texOffs(96, 138).addBox(9F, -6F, -6F, 4F, 16F, 8F).texOffs(264, 162).addBox(-10F, -20F, -8F, 20F, 2F, 16F).texOffs(392, 184).addBox(-2F, -23F, -10F, 4F, 6F, 2F).texOffs(220, 184).addBox(-3F, 2F, 7F, 6F, 12F, 4F), PartPose.offsetAndRotation(0F, -20F, -26F, 0F, 0F, 0F));
		PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(404, 184).addBox(-32F, -2F, -2F, 32F, 4F, 4F).texOffs(280, 72).addBox(-32F, 0F, 2F, 8F, 2F, 30F).texOffs(120, 138).addBox(-24F, 0F, 2F, 8F, 2F, 22F).texOffs(180, 138).addBox(-16F, 0F, 2F, 8F, 2F, 22F).texOffs(356, 72).addBox(-8F, 0F, 2F, 8F, 2F, 30F), PartPose.offsetAndRotation(-12F, -12F, -8F, -0.3491F, -0.3491F, -0.8727F));
		right_wing.addOrReplaceChild("right_wing2", CubeListBuilder.create().texOffs(0, 202).addBox(-32F, -2F, -2F, 32F, 4F, 4F).texOffs(272, 0).addBox(-17F, 0F, 2F, 2F, 2F, 36F).texOffs(0, 72).addBox(-21.8F, 0F, 2F, 2F, 2F, 32F).texOffs(76, 106).addBox(-26.6F, 0F, 2F, 2F, 2F, 28F).texOffs(136, 106).addBox(-32F, 0.6F, 2F, 8F, 2F, 28F).texOffs(240, 138).addBox(-24F, 0.6F, 2F, 8F, 2F, 22F).texOffs(340, 106).addBox(-16F, 0.6F, 2F, 8F, 2F, 24F).texOffs(160, 162).addBox(-8F, 0.6F, 2F, 8F, 2F, 18F), PartPose.offsetAndRotation(-32F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(72, 202).addBox(0F, -2F, -2F, 32F, 4F, 4F).texOffs(432, 72).addBox(0F, 0F, 2F, 8F, 2F, 30F).texOffs(300, 138).addBox(8F, 0F, 2F, 8F, 2F, 22F).texOffs(360, 138).addBox(16F, 0F, 2F, 8F, 2F, 22F).texOffs(0, 106).addBox(24F, 0F, 2F, 8F, 2F, 30F), PartPose.offsetAndRotation(12F, -12F, -8F, -0.3491F, 0.3491F, 0.8727F));
		left_wing.addOrReplaceChild("left_wing2", CubeListBuilder.create().texOffs(144, 202).addBox(0F, -2F, -2F, 32F, 4F, 4F).texOffs(348, 0).addBox(15F, 0F, 2F, 2F, 2F, 36F).texOffs(68, 72).addBox(19.8F, 0F, 2F, 2F, 2F, 32F).texOffs(208, 106).addBox(24.6F, 0F, 2F, 2F, 2F, 28F).texOffs(268, 106).addBox(0F, 0.6F, 2F, 8F, 2F, 28F).texOffs(420, 138).addBox(8F, 0.6F, 2F, 8F, 2F, 22F).texOffs(404, 106).addBox(16F, 0.6F, 2F, 8F, 2F, 24F).texOffs(212, 162).addBox(24F, 0.6F, 2F, 8F, 2F, 18F), PartPose.offsetAndRotation(32F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(240, 184).addBox(-2F, -2F, 0F, 4F, 4F, 8F), PartPose.offsetAndRotation(0F, -4F, 24F, -0.5236F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(264, 184).addBox(-2F, -2F, 0F, 4F, 4F, 8F), PartPose.offsetAndRotation(0F, 0F, 8F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(288, 184).addBox(-2F, -2F, 0F, 4F, 4F, 8F), PartPose.offsetAndRotation(0F, 0F, 8F, 0F, 0F, 0F));
		PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(312, 184).addBox(-2F, -2F, 0F, 4F, 4F, 8F), PartPose.offsetAndRotation(0F, 0F, 8F, 0F, 0F, 0F));
		tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(336, 184).addBox(-2F, -2F, 0F, 4F, 4F, 8F), PartPose.offsetAndRotation(0F, 0F, 8F, 0F, 0F, 0F));
		root.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(336, 162).addBox(-8F, -8F, -1F, 16F, 16F, 2F).texOffs(216, 202).addBox(12F, -2F, -1F, 4F, 4F, 2F).texOffs(228, 202).addBox(7.8995F, 7.8995F, -1F, 4F, 4F, 2F).texOffs(240, 202).addBox(-2F, 12F, -1F, 4F, 4F, 2F).texOffs(252, 202).addBox(-11.8995F, 7.8995F, -1F, 4F, 4F, 2F).texOffs(264, 202).addBox(-16F, -2F, -1F, 4F, 4F, 2F).texOffs(276, 202).addBox(-11.8995F, -11.8995F, -1F, 4F, 4F, 2F).texOffs(288, 202).addBox(-2F, -16F, -1F, 4F, 4F, 2F).texOffs(300, 202).addBox(7.8995F, -11.8995F, -1F, 4F, 4F, 2F), PartPose.offsetAndRotation(0F, -76F, -18F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 162).addBox(-5F, 0F, -5F, 10F, 12F, 10F), PartPose.offsetAndRotation(-10F, 2F, 18F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(372, 162).addBox(-4F, 0F, -4F, 8F, 10F, 8F).texOffs(404, 162).addBox(-6F, 6F, -10F, 12F, 4F, 14F).texOffs(312, 202).addBox(-4.2F, 8F, -13F, 2F, 2F, 4F).texOffs(324, 202).addBox(-1F, 8F, -13F, 2F, 2F, 4F).texOffs(336, 202).addBox(2.2F, 8F, -13F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(40, 162).addBox(-5F, 0F, -5F, 10F, 12F, 10F), PartPose.offsetAndRotation(10F, 2F, 18F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(456, 162).addBox(-4F, 0F, -4F, 8F, 10F, 8F).texOffs(0, 184).addBox(-6F, 6F, -10F, 12F, 4F, 14F).texOffs(348, 202).addBox(-4.2F, 8F, -13F, 2F, 2F, 4F).texOffs(360, 202).addBox(-1F, 8F, -13F, 2F, 2F, 4F).texOffs(372, 202).addBox(2.2F, 8F, -13F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition front_right_leg = root.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(80, 162).addBox(-5F, 0F, -5F, 10F, 12F, 10F), PartPose.offsetAndRotation(-10F, 2F, -18F, 0F, 0F, 0F));
		front_right_leg.addOrReplaceChild("front_right_leg2", CubeListBuilder.create().texOffs(52, 184).addBox(-4F, 0F, -4F, 8F, 10F, 8F).texOffs(84, 184).addBox(-6F, 6F, -10F, 12F, 4F, 14F).texOffs(384, 202).addBox(-4.2F, 8F, -13F, 2F, 2F, 4F).texOffs(396, 202).addBox(-1F, 8F, -13F, 2F, 2F, 4F).texOffs(408, 202).addBox(2.2F, 8F, -13F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition front_left_leg = root.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(120, 162).addBox(-5F, 0F, -5F, 10F, 12F, 10F), PartPose.offsetAndRotation(10F, 2F, -18F, 0F, 0F, 0F));
		front_left_leg.addOrReplaceChild("front_left_leg2", CubeListBuilder.create().texOffs(136, 184).addBox(-4F, 0F, -4F, 8F, 10F, 8F).texOffs(168, 184).addBox(-6F, 6F, -10F, 12F, 4F, 14F).texOffs(420, 202).addBox(-4.2F, 8F, -13F, 2F, 2F, 4F).texOffs(432, 202).addBox(-1F, 8F, -13F, 2F, 2F, 4F).texOffs(444, 202).addBox(2.2F, 8F, -13F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
