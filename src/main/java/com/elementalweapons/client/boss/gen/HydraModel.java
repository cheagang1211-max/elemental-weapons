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
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-9F, -7F, -12F, 18F, 14F, 24F).texOffs(180, 0).addBox(-7F, 6F, -10F, 14F, 2F, 20F).texOffs(84, 0).addBox(-10F, -3F, -11F, 2F, 2F, 22F).texOffs(132, 0).addBox(8F, -3F, -11F, 2F, 2F, 22F).texOffs(156, 86).addBox(-1F, -10F, -9F, 2F, 3F, 2F).texOffs(164, 86).addBox(-1F, -10.25F, -4.5F, 2F, 3F, 2F).texOffs(204, 76).addBox(-1F, -10.5F, 0F, 2F, 4F, 2F).texOffs(212, 76).addBox(-1F, -10.75F, 4.5F, 2F, 4F, 2F).texOffs(220, 76).addBox(-1F, -11F, 9F, 2F, 4F, 2F).texOffs(172, 86).addBox(-7F, -8F, -5F, 2F, 2F, 2F).texOffs(180, 86).addBox(5F, -8F, 1F, 2F, 2F, 2F).texOffs(188, 86).addBox(-5F, -8F, 7F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition neckh1x1 = body.addOrReplaceChild("neckh1x1", CubeListBuilder.create().texOffs(142, 38).addBox(-3F, -6F, -3F, 6F, 6F, 6F).texOffs(228, 76).addBox(-1.5F, -5.5F, 3F, 3F, 5F, 1F), PartPose.offsetAndRotation(-10F, -6F, -6F, -0.0873F, 0F, -0.5236F));
		PartDefinition neckh1x2 = neckh1x1.addOrReplaceChild("neckh1x2", CubeListBuilder.create().texOffs(166, 38).addBox(-3F, -6F, -3F, 6F, 6F, 6F).texOffs(236, 76).addBox(-1.5F, -5.5F, 2.8333F, 3F, 5F, 1F), PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, 0F));
		PartDefinition neckh1x3 = neckh1x2.addOrReplaceChild("neckh1x3", CubeListBuilder.create().texOffs(72, 53).addBox(-2.5F, -6F, -2.5F, 5F, 6F, 5F).texOffs(244, 76).addBox(-1.5F, -5.5F, 2.6667F, 3F, 5F, 1F), PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, 0F));
		PartDefinition neckh1x4 = neckh1x3.addOrReplaceChild("neckh1x4", CubeListBuilder.create().texOffs(92, 53).addBox(-2.5F, -6F, -2.5F, 5F, 6F, 5F).texOffs(0, 86).addBox(-1.5F, -5.5F, 2.5F, 3F, 5F, 1F), PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, 0F));
		PartDefinition head1 = neckh1x4.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(58, 38).addBox(-3.5F, -4F, -6.5F, 7F, 6F, 7F).texOffs(72, 65).addBox(-2.5F, -2F, -12F, 5F, 4F, 6F).texOffs(220, 86).addBox(-2F, 2F, -11.5F, 1F, 2F, 1F).texOffs(224, 86).addBox(0F, 2F, -11.5F, 1F, 2F, 1F).texOffs(8, 86).addBox(-0.5F, -5.5F, -4F, 1F, 2F, 4F).texOffs(228, 86).addBox(-5F, -5F, 1F, 2F, 2F, 1F).texOffs(234, 86).addBox(3F, -5F, 1F, 2F, 2F, 1F).texOffs(240, 86).addBox(-1F, -6.5F, 1F, 2F, 2F, 1F), PartPose.offsetAndRotation(0F, -6F, -2F, 0F, 0F, 0F));
		head1.addOrReplaceChild("jawh1", CubeListBuilder.create().texOffs(94, 65).addBox(-3F, 0F, -8F, 6F, 2F, 8F).texOffs(246, 86).addBox(-2.5F, -2F, -7.5F, 1F, 2F, 1F).texOffs(250, 86).addBox(-0.5F, -2F, -7.5F, 1F, 2F, 1F).texOffs(0, 92).addBox(1.5F, -2F, -7.5F, 1F, 2F, 1F).texOffs(18, 86).addBox(-2F, -0.5F, -5.5F, 4F, 1F, 5F), PartPose.offsetAndRotation(0F, 2F, -2F, 0.2618F, 0F, 0F));
		PartDefinition neckh2x1 = body.addOrReplaceChild("neckh2x1", CubeListBuilder.create().texOffs(190, 38).addBox(-3F, -6F, -3F, 6F, 6F, 6F).texOffs(36, 86).addBox(-1.5F, -5.5F, 3F, 3F, 5F, 1F), PartPose.offsetAndRotation(0F, -6F, -6F, -0.2618F, 0F, 0F));
		PartDefinition neckh2x2 = neckh2x1.addOrReplaceChild("neckh2x2", CubeListBuilder.create().texOffs(214, 38).addBox(-3F, -6F, -3F, 6F, 6F, 6F).texOffs(44, 86).addBox(-1.5F, -5.5F, 2.8333F, 3F, 5F, 1F), PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, 0F));
		PartDefinition neckh2x3 = neckh2x2.addOrReplaceChild("neckh2x3", CubeListBuilder.create().texOffs(112, 53).addBox(-2.5F, -6F, -2.5F, 5F, 6F, 5F).texOffs(52, 86).addBox(-1.5F, -5.5F, 2.6667F, 3F, 5F, 1F), PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, 0F));
		PartDefinition neckh2x4 = neckh2x3.addOrReplaceChild("neckh2x4", CubeListBuilder.create().texOffs(132, 53).addBox(-2.5F, -6F, -2.5F, 5F, 6F, 5F).texOffs(60, 86).addBox(-1.5F, -5.5F, 2.5F, 3F, 5F, 1F), PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, 0F));
		PartDefinition head2 = neckh2x4.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(86, 38).addBox(-3.5F, -4F, -6.5F, 7F, 6F, 7F).texOffs(122, 65).addBox(-2.5F, -2F, -12F, 5F, 4F, 6F).texOffs(4, 92).addBox(-2F, 2F, -11.5F, 1F, 2F, 1F).texOffs(8, 92).addBox(0F, 2F, -11.5F, 1F, 2F, 1F).texOffs(68, 86).addBox(-0.5F, -5.5F, -4F, 1F, 2F, 4F).texOffs(12, 92).addBox(-5F, -5F, 1F, 2F, 2F, 1F).texOffs(18, 92).addBox(3F, -5F, 1F, 2F, 2F, 1F).texOffs(24, 92).addBox(-1F, -6.5F, 1F, 2F, 2F, 1F), PartPose.offsetAndRotation(0F, -6F, -2F, 0F, 0F, 0F));
		head2.addOrReplaceChild("jawh2", CubeListBuilder.create().texOffs(144, 65).addBox(-3F, 0F, -8F, 6F, 2F, 8F).texOffs(30, 92).addBox(-2.5F, -2F, -7.5F, 1F, 2F, 1F).texOffs(34, 92).addBox(-0.5F, -2F, -7.5F, 1F, 2F, 1F).texOffs(38, 92).addBox(1.5F, -2F, -7.5F, 1F, 2F, 1F).texOffs(78, 86).addBox(-2F, -0.5F, -5.5F, 4F, 1F, 5F), PartPose.offsetAndRotation(0F, 2F, -2F, 0.2618F, 0F, 0F));
		PartDefinition neckh3x1 = body.addOrReplaceChild("neckh3x1", CubeListBuilder.create().texOffs(0, 53).addBox(-3F, -6F, -3F, 6F, 6F, 6F).texOffs(96, 86).addBox(-1.5F, -5.5F, 3F, 3F, 5F, 1F), PartPose.offsetAndRotation(10F, -6F, -6F, -0.0873F, 0F, 0.5236F));
		PartDefinition neckh3x2 = neckh3x1.addOrReplaceChild("neckh3x2", CubeListBuilder.create().texOffs(24, 53).addBox(-3F, -6F, -3F, 6F, 6F, 6F).texOffs(104, 86).addBox(-1.5F, -5.5F, 2.8333F, 3F, 5F, 1F), PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, 0F));
		PartDefinition neckh3x3 = neckh3x2.addOrReplaceChild("neckh3x3", CubeListBuilder.create().texOffs(152, 53).addBox(-2.5F, -6F, -2.5F, 5F, 6F, 5F).texOffs(112, 86).addBox(-1.5F, -5.5F, 2.6667F, 3F, 5F, 1F), PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, 0F));
		PartDefinition neckh3x4 = neckh3x3.addOrReplaceChild("neckh3x4", CubeListBuilder.create().texOffs(172, 53).addBox(-2.5F, -6F, -2.5F, 5F, 6F, 5F).texOffs(120, 86).addBox(-1.5F, -5.5F, 2.5F, 3F, 5F, 1F), PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, 0F));
		PartDefinition head3 = neckh3x4.addOrReplaceChild("head3", CubeListBuilder.create().texOffs(114, 38).addBox(-3.5F, -4F, -6.5F, 7F, 6F, 7F).texOffs(172, 65).addBox(-2.5F, -2F, -12F, 5F, 4F, 6F).texOffs(42, 92).addBox(-2F, 2F, -11.5F, 1F, 2F, 1F).texOffs(46, 92).addBox(0F, 2F, -11.5F, 1F, 2F, 1F).texOffs(128, 86).addBox(-0.5F, -5.5F, -4F, 1F, 2F, 4F).texOffs(50, 92).addBox(-5F, -5F, 1F, 2F, 2F, 1F).texOffs(56, 92).addBox(3F, -5F, 1F, 2F, 2F, 1F).texOffs(62, 92).addBox(-1F, -6.5F, 1F, 2F, 2F, 1F), PartPose.offsetAndRotation(0F, -6F, -2F, 0F, 0F, 0F));
		head3.addOrReplaceChild("jawh3", CubeListBuilder.create().texOffs(194, 65).addBox(-3F, 0F, -8F, 6F, 2F, 8F).texOffs(68, 92).addBox(-2.5F, -2F, -7.5F, 1F, 2F, 1F).texOffs(72, 92).addBox(-0.5F, -2F, -7.5F, 1F, 2F, 1F).texOffs(76, 92).addBox(1.5F, -2F, -7.5F, 1F, 2F, 1F).texOffs(138, 86).addBox(-2F, -0.5F, -5.5F, 4F, 1F, 5F), PartPose.offsetAndRotation(0F, 2F, -2F, 0.2618F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 38).addBox(-4.5F, -4.5F, 0F, 9F, 9F, 6F).texOffs(196, 86).addBox(-1F, -6.5F, 2F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, 0F, 12F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(30, 38).addBox(-4F, -4F, 0F, 8F, 8F, 6F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(48, 53).addBox(-3F, -3F, 0F, 6F, 6F, 6F).texOffs(204, 86).addBox(-1F, -5.1F, 2F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(192, 53).addBox(-2.5F, -2.5F, 0F, 5F, 5F, 6F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition tail5 = tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(90, 76).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 6F).texOffs(212, 86).addBox(-1F, -3.7F, 2F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		tail5.addOrReplaceChild("tail6", CubeListBuilder.create().texOffs(188, 76).addBox(-1F, -1F, 0F, 2F, 2F, 6F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(214, 53).addBox(-3F, 0F, -3F, 6F, 5F, 6F), PartPose.offsetAndRotation(-8F, 15F, 8F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(108, 76).addBox(-2.5F, 0F, -2.5F, 5F, 4F, 5F).texOffs(222, 65).addBox(-3.5F, 2F, -5.5F, 7F, 2F, 8F).texOffs(80, 92).addBox(-2.5F, 3F, -7F, 1F, 1F, 2F).texOffs(86, 92).addBox(-0.5F, 3F, -7F, 1F, 1F, 2F).texOffs(92, 92).addBox(1.5F, 3F, -7F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 65).addBox(-3F, 0F, -3F, 6F, 5F, 6F), PartPose.offsetAndRotation(8F, 15F, 8F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(128, 76).addBox(-2.5F, 0F, -2.5F, 5F, 4F, 5F).texOffs(0, 76).addBox(-3.5F, 2F, -5.5F, 7F, 2F, 8F).texOffs(98, 92).addBox(-2.5F, 3F, -7F, 1F, 1F, 2F).texOffs(104, 92).addBox(-0.5F, 3F, -7F, 1F, 1F, 2F).texOffs(110, 92).addBox(1.5F, 3F, -7F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition front_right_leg = root.addOrReplaceChild("front_right_leg", CubeListBuilder.create().texOffs(24, 65).addBox(-3F, 0F, -3F, 6F, 5F, 6F), PartPose.offsetAndRotation(-8F, 15F, -8F, 0F, 0F, 0F));
		front_right_leg.addOrReplaceChild("front_right_leg2", CubeListBuilder.create().texOffs(148, 76).addBox(-2.5F, 0F, -2.5F, 5F, 4F, 5F).texOffs(30, 76).addBox(-3.5F, 2F, -5.5F, 7F, 2F, 8F).texOffs(116, 92).addBox(-2.5F, 3F, -7F, 1F, 1F, 2F).texOffs(122, 92).addBox(-0.5F, 3F, -7F, 1F, 1F, 2F).texOffs(128, 92).addBox(1.5F, 3F, -7F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition front_left_leg = root.addOrReplaceChild("front_left_leg", CubeListBuilder.create().texOffs(48, 65).addBox(-3F, 0F, -3F, 6F, 5F, 6F), PartPose.offsetAndRotation(8F, 15F, -8F, 0F, 0F, 0F));
		front_left_leg.addOrReplaceChild("front_left_leg2", CubeListBuilder.create().texOffs(168, 76).addBox(-2.5F, 0F, -2.5F, 5F, 4F, 5F).texOffs(60, 76).addBox(-3.5F, 2F, -5.5F, 7F, 2F, 8F).texOffs(134, 92).addBox(-2.5F, 3F, -7F, 1F, 1F, 2F).texOffs(140, 92).addBox(-0.5F, 3F, -7F, 1F, 1F, 2F).texOffs(146, 92).addBox(1.5F, 3F, -7F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
