// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class HydraModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "neckh1x1"},
			{"body", "neckh1x1", "neckh1x2"},
			{"body", "neckh1x1", "neckh1x2", "neckh1x3"},
			{"body", "neckh1x1", "neckh1x2", "neckh1x3", "neckh1x4"},
			{"body", "neckh1x1", "neckh1x2", "neckh1x3", "neckh1x4", "head1"},
			{"body", "neckh1x1", "neckh1x2", "neckh1x3", "neckh1x4", "head1", "jawh1"},
			{"body", "neckh2x1"},
			{"body", "neckh2x1", "neckh2x2"},
			{"body", "neckh2x1", "neckh2x2", "neckh2x3"},
			{"body", "neckh2x1", "neckh2x2", "neckh2x3", "neckh2x4"},
			{"body", "neckh2x1", "neckh2x2", "neckh2x3", "neckh2x4", "head2"},
			{"body", "neckh2x1", "neckh2x2", "neckh2x3", "neckh2x4", "head2", "jawh2"},
			{"body", "neckh3x1"},
			{"body", "neckh3x1", "neckh3x2"},
			{"body", "neckh3x1", "neckh3x2", "neckh3x3"},
			{"body", "neckh3x1", "neckh3x2", "neckh3x3", "neckh3x4"},
			{"body", "neckh3x1", "neckh3x2", "neckh3x3", "neckh3x4", "head3"},
			{"body", "neckh3x1", "neckh3x2", "neckh3x3", "neckh3x4", "head3", "jawh3"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"},
			{"body", "tail1", "tail2", "tail3", "tail4"},
			{"body", "tail1", "tail2", "tail3", "tail4", "tail5"},
			{"body", "tail1", "tail2", "tail3", "tail4", "tail5", "tail6"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"},
			{"front_right_leg"},
			{"front_right_leg", "front_right_leg2"},
			{"front_left_leg"},
			{"front_left_leg", "front_left_leg2"}
	};

	public HydraModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-18F, -14F, -24F, 36F, 28F, 48F).texOffs(360, 0).addBox(-14F, 12F, -20F, 28F, 4F, 40F).texOffs(168, 0).addBox(-20F, -6F, -22F, 4F, 4F, 44F).texOffs(264, 0).addBox(16F, -6F, -22F, 4F, 4F, 44F).texOffs(312, 172).addBox(-2F, -20F, -18F, 4F, 6F, 4F).texOffs(328, 172).addBox(-2F, -20.5F, -9F, 4F, 6F, 4F).texOffs(408, 152).addBox(-2F, -21F, 0F, 4F, 8F, 4F).texOffs(424, 152).addBox(-2F, -21.5F, 9F, 4F, 8F, 4F).texOffs(440, 152).addBox(-2F, -22F, 18F, 4F, 8F, 4F).texOffs(344, 172).addBox(-14F, -16F, -10F, 4F, 4F, 4F).texOffs(360, 172).addBox(10F, -16F, 2F, 4F, 4F, 4F).texOffs(376, 172).addBox(-10F, -16F, 14F, 4F, 4F, 4F), PartPose.offsetAndRotation(0F, -4F, 0F, 0F, 0F, 0F));
		PartDefinition neckh1x1 = body.addOrReplaceChild("neckh1x1", CubeListBuilder.create().texOffs(284, 76).addBox(-6F, -12F, -6F, 12F, 12F, 12F).texOffs(456, 152).addBox(-3F, -11F, 6F, 6F, 10F, 2F), PartPose.offsetAndRotation(-20F, -12F, -12F, -0.0873F, 0F, -0.5236F));
		PartDefinition neckh1x2 = neckh1x1.addOrReplaceChild("neckh1x2", CubeListBuilder.create().texOffs(332, 76).addBox(-6F, -12F, -6F, 12F, 12F, 12F).texOffs(472, 152).addBox(-3F, -11F, 5.6667F, 6F, 10F, 2F), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
		PartDefinition neckh1x3 = neckh1x2.addOrReplaceChild("neckh1x3", CubeListBuilder.create().texOffs(144, 106).addBox(-5F, -12F, -5F, 10F, 12F, 10F).texOffs(488, 152).addBox(-3F, -11F, 5.3333F, 6F, 10F, 2F), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
		PartDefinition neckh1x4 = neckh1x3.addOrReplaceChild("neckh1x4", CubeListBuilder.create().texOffs(184, 106).addBox(-5F, -12F, -5F, 10F, 12F, 10F).texOffs(0, 172).addBox(-3F, -11F, 5F, 6F, 10F, 2F), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
		PartDefinition head1 = neckh1x4.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(116, 76).addBox(-7F, -8F, -13F, 14F, 12F, 14F).texOffs(144, 130).addBox(-5F, -4F, -24F, 10F, 8F, 12F).texOffs(440, 172).addBox(-4F, 4F, -23F, 2F, 4F, 2F).texOffs(448, 172).addBox(0F, 4F, -23F, 2F, 4F, 2F).texOffs(16, 172).addBox(-1F, -11F, -8F, 2F, 4F, 8F).texOffs(456, 172).addBox(-10F, -10F, 2F, 4F, 4F, 2F).texOffs(468, 172).addBox(6F, -10F, 2F, 4F, 4F, 2F).texOffs(480, 172).addBox(-2F, -13F, 2F, 4F, 4F, 2F), PartPose.offsetAndRotation(0F, -12F, -4F, 0F, 0F, 0F));
		head1.addOrReplaceChild("jawh1", CubeListBuilder.create().texOffs(188, 130).addBox(-6F, 0F, -16F, 12F, 4F, 16F).texOffs(492, 172).addBox(-5F, -4F, -15F, 2F, 4F, 2F).texOffs(500, 172).addBox(-1F, -4F, -15F, 2F, 4F, 2F).texOffs(0, 184).addBox(3F, -4F, -15F, 2F, 4F, 2F).texOffs(36, 172).addBox(-4F, -1F, -11F, 8F, 2F, 10F), PartPose.offsetAndRotation(0F, 4F, -4F, 0.2618F, 0F, 0F));
		PartDefinition neckh2x1 = body.addOrReplaceChild("neckh2x1", CubeListBuilder.create().texOffs(380, 76).addBox(-6F, -12F, -6F, 12F, 12F, 12F).texOffs(72, 172).addBox(-3F, -11F, 6F, 6F, 10F, 2F), PartPose.offsetAndRotation(0F, -12F, -12F, -0.2618F, 0F, 0F));
		PartDefinition neckh2x2 = neckh2x1.addOrReplaceChild("neckh2x2", CubeListBuilder.create().texOffs(428, 76).addBox(-6F, -12F, -6F, 12F, 12F, 12F).texOffs(88, 172).addBox(-3F, -11F, 5.6667F, 6F, 10F, 2F), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
		PartDefinition neckh2x3 = neckh2x2.addOrReplaceChild("neckh2x3", CubeListBuilder.create().texOffs(224, 106).addBox(-5F, -12F, -5F, 10F, 12F, 10F).texOffs(104, 172).addBox(-3F, -11F, 5.3333F, 6F, 10F, 2F), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
		PartDefinition neckh2x4 = neckh2x3.addOrReplaceChild("neckh2x4", CubeListBuilder.create().texOffs(264, 106).addBox(-5F, -12F, -5F, 10F, 12F, 10F).texOffs(120, 172).addBox(-3F, -11F, 5F, 6F, 10F, 2F), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
		PartDefinition head2 = neckh2x4.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(172, 76).addBox(-7F, -8F, -13F, 14F, 12F, 14F).texOffs(244, 130).addBox(-5F, -4F, -24F, 10F, 8F, 12F).texOffs(8, 184).addBox(-4F, 4F, -23F, 2F, 4F, 2F).texOffs(16, 184).addBox(0F, 4F, -23F, 2F, 4F, 2F).texOffs(136, 172).addBox(-1F, -11F, -8F, 2F, 4F, 8F).texOffs(24, 184).addBox(-10F, -10F, 2F, 4F, 4F, 2F).texOffs(36, 184).addBox(6F, -10F, 2F, 4F, 4F, 2F).texOffs(48, 184).addBox(-2F, -13F, 2F, 4F, 4F, 2F), PartPose.offsetAndRotation(0F, -12F, -4F, 0F, 0F, 0F));
		head2.addOrReplaceChild("jawh2", CubeListBuilder.create().texOffs(288, 130).addBox(-6F, 0F, -16F, 12F, 4F, 16F).texOffs(60, 184).addBox(-5F, -4F, -15F, 2F, 4F, 2F).texOffs(68, 184).addBox(-1F, -4F, -15F, 2F, 4F, 2F).texOffs(76, 184).addBox(3F, -4F, -15F, 2F, 4F, 2F).texOffs(156, 172).addBox(-4F, -1F, -11F, 8F, 2F, 10F), PartPose.offsetAndRotation(0F, 4F, -4F, 0.2618F, 0F, 0F));
		PartDefinition neckh3x1 = body.addOrReplaceChild("neckh3x1", CubeListBuilder.create().texOffs(0, 106).addBox(-6F, -12F, -6F, 12F, 12F, 12F).texOffs(192, 172).addBox(-3F, -11F, 6F, 6F, 10F, 2F), PartPose.offsetAndRotation(20F, -12F, -12F, -0.0873F, 0F, 0.5236F));
		PartDefinition neckh3x2 = neckh3x1.addOrReplaceChild("neckh3x2", CubeListBuilder.create().texOffs(48, 106).addBox(-6F, -12F, -6F, 12F, 12F, 12F).texOffs(208, 172).addBox(-3F, -11F, 5.6667F, 6F, 10F, 2F), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
		PartDefinition neckh3x3 = neckh3x2.addOrReplaceChild("neckh3x3", CubeListBuilder.create().texOffs(304, 106).addBox(-5F, -12F, -5F, 10F, 12F, 10F).texOffs(224, 172).addBox(-3F, -11F, 5.3333F, 6F, 10F, 2F), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
		PartDefinition neckh3x4 = neckh3x3.addOrReplaceChild("neckh3x4", CubeListBuilder.create().texOffs(344, 106).addBox(-5F, -12F, -5F, 10F, 12F, 10F).texOffs(240, 172).addBox(-3F, -11F, 5F, 6F, 10F, 2F), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
		PartDefinition head3 = neckh3x4.addOrReplaceChild("head3", CubeListBuilder.create().texOffs(228, 76).addBox(-7F, -8F, -13F, 14F, 12F, 14F).texOffs(344, 130).addBox(-5F, -4F, -24F, 10F, 8F, 12F).texOffs(84, 184).addBox(-4F, 4F, -23F, 2F, 4F, 2F).texOffs(92, 184).addBox(0F, 4F, -23F, 2F, 4F, 2F).texOffs(256, 172).addBox(-1F, -11F, -8F, 2F, 4F, 8F).texOffs(100, 184).addBox(-10F, -10F, 2F, 4F, 4F, 2F).texOffs(112, 184).addBox(6F, -10F, 2F, 4F, 4F, 2F).texOffs(124, 184).addBox(-2F, -13F, 2F, 4F, 4F, 2F), PartPose.offsetAndRotation(0F, -12F, -4F, 0F, 0F, 0F));
		head3.addOrReplaceChild("jawh3", CubeListBuilder.create().texOffs(388, 130).addBox(-6F, 0F, -16F, 12F, 4F, 16F).texOffs(136, 184).addBox(-5F, -4F, -15F, 2F, 4F, 2F).texOffs(144, 184).addBox(-1F, -4F, -15F, 2F, 4F, 2F).texOffs(152, 184).addBox(3F, -4F, -15F, 2F, 4F, 2F).texOffs(276, 172).addBox(-4F, -1F, -11F, 8F, 2F, 10F), PartPose.offsetAndRotation(0F, 4F, -4F, 0.2618F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 76).addBox(-9F, -9F, 0F, 18F, 18F, 12F).texOffs(392, 172).addBox(-2F, -13F, 4F, 4F, 4F, 4F), PartPose.offsetAndRotation(0F, 0F, 24F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(60, 76).addBox(-8F, -8F, 0F, 16F, 16F, 12F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(96, 106).addBox(-6F, -6F, 0F, 12F, 12F, 12F).texOffs(408, 172).addBox(-2F, -10.2F, 4F, 4F, 4F, 4F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(384, 106).addBox(-5F, -5F, 0F, 10F, 10F, 12F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition tail5 = tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(180, 152).addBox(-3F, -3F, 0F, 6F, 6F, 12F).texOffs(424, 172).addBox(-2F, -7.4F, 4F, 4F, 4F, 4F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		tail5.addOrReplaceChild("tail6", CubeListBuilder.create().texOffs(376, 152).addBox(-2F, -2F, 0F, 4F, 4F, 12F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(428, 106).addBox(-6F, 0F, -6F, 12F, 10F, 12F), PartPose.offsetAndRotation(-16F, 6F, 16F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(216, 152).addBox(-5F, 0F, -5F, 10F, 8F, 10F).texOffs(444, 130).addBox(-7F, 4F, -11F, 14F, 4F, 16F).texOffs(160, 184).addBox(-5F, 6F, -14F, 2F, 2F, 4F).texOffs(172, 184).addBox(-1F, 6F, -14F, 2F, 2F, 4F).texOffs(184, 184).addBox(3F, 6F, -14F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 130).addBox(-6F, 0F, -6F, 12F, 10F, 12F), PartPose.offsetAndRotation(16F, 6F, 16F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(256, 152).addBox(-5F, 0F, -5F, 10F, 8F, 10F).texOffs(0, 152).addBox(-7F, 4F, -11F, 14F, 4F, 16F).texOffs(196, 184).addBox(-5F, 6F, -14F, 2F, 2F, 4F).texOffs(208, 184).addBox(-1F, 6F, -14F, 2F, 2F, 4F).texOffs(220, 184).addBox(3F, 6F, -14F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition front_right_leg = root.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(48, 130).addBox(-6F, 0F, -6F, 12F, 10F, 12F), PartPose.offsetAndRotation(-16F, 6F, -16F, 0F, 0F, 0F));
		front_right_leg.addOrReplaceChild("front_right_leg2", CubeListBuilder.create().texOffs(296, 152).addBox(-5F, 0F, -5F, 10F, 8F, 10F).texOffs(60, 152).addBox(-7F, 4F, -11F, 14F, 4F, 16F).texOffs(232, 184).addBox(-5F, 6F, -14F, 2F, 2F, 4F).texOffs(244, 184).addBox(-1F, 6F, -14F, 2F, 2F, 4F).texOffs(256, 184).addBox(3F, 6F, -14F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition front_left_leg = root.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(96, 130).addBox(-6F, 0F, -6F, 12F, 10F, 12F), PartPose.offsetAndRotation(16F, 6F, -16F, 0F, 0F, 0F));
		front_left_leg.addOrReplaceChild("front_left_leg2", CubeListBuilder.create().texOffs(336, 152).addBox(-5F, 0F, -5F, 10F, 8F, 10F).texOffs(120, 152).addBox(-7F, 4F, -11F, 14F, 4F, 16F).texOffs(268, 184).addBox(-5F, 6F, -14F, 2F, 2F, 4F).texOffs(280, 184).addBox(-1F, 6F, -14F, 2F, 2F, 4F).texOffs(292, 184).addBox(3F, 6F, -14F, 2F, 2F, 4F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
