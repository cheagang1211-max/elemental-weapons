// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class StormWyvernModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "neck1"},
			{"body", "neck1", "neck2"},
			{"body", "neck1", "neck2", "neck3"},
			{"body", "neck1", "neck2", "neck3", "head"},
			{"body", "neck1", "neck2", "neck3", "head", "jaw"},
			{"body", "neck1", "neck2", "neck3", "head", "hornWL1"},
			{"body", "neck1", "neck2", "neck3", "head", "hornWL1", "hornWL2"},
			{"body", "neck1", "neck2", "neck3", "head", "hornWR1"},
			{"body", "neck1", "neck2", "neck3", "head", "hornWR1", "hornWR2"},
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
			{"left_leg", "left_leg2"}
	};

	public StormWyvernModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6F, -5.5F, -10F, 12F, 11F, 20F).texOffs(166, 77).addBox(-4.5F, 4F, -8F, 9F, 2F, 16F).texOffs(0, 123).addBox(-0.5F, -7.5F, -8.5F, 1F, 2F, 1F).texOffs(4, 123).addBox(-0.5F, -7.75F, -4.5F, 1F, 2F, 1F).texOffs(8, 123).addBox(-0.5F, -8F, -0.5F, 1F, 2F, 1F).texOffs(194, 110).addBox(-0.5F, -8.25F, 3.5F, 1F, 3F, 1F).texOffs(198, 110).addBox(-0.5F, -8.5F, 7.5F, 1F, 3F, 1F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		PartDefinition neck1 = body.addOrReplaceChild("neck1", CubeListBuilder.create().texOffs(56, 98).addBox(-3F, -3F, -5F, 6F, 6F, 5F).texOffs(202, 110).addBox(-0.5F, -5F, -3.5F, 1F, 2F, 2F), PartPose.offsetAndRotation(0F, -2F, -9F, -0.4363F, 0F, 0F));
		PartDefinition neck2 = neck1.addOrReplaceChild("neck2", CubeListBuilder.create().texOffs(78, 98).addBox(-3F, -3F, -5F, 6F, 6F, 5F).texOffs(208, 110).addBox(-0.5F, -4.75F, -3.5F, 1F, 2F, 2F), PartPose.offsetAndRotation(0F, 0F, -5F, 0F, 0F, 0F));
		PartDefinition neck3 = neck2.addOrReplaceChild("neck3", CubeListBuilder.create().texOffs(210, 98).addBox(-2.5F, -2.5F, -5F, 5F, 5F, 5F).texOffs(214, 110).addBox(-0.5F, -4.5F, -3.5F, 1F, 2F, 2F), PartPose.offsetAndRotation(0F, 0F, -5F, 0F, 0F, 0F));
		PartDefinition head = neck3.addOrReplaceChild("head", CubeListBuilder.create().texOffs(216, 77).addBox(-4F, -4F, -7F, 8F, 6F, 8F).texOffs(100, 98).addBox(-3F, -2F, -13.5F, 6F, 4F, 7F).texOffs(12, 123).addBox(-2.5F, 2F, -13F, 1F, 2F, 1F).texOffs(16, 123).addBox(-0.5F, 2F, -13F, 1F, 2F, 1F).texOffs(20, 123).addBox(1.5F, 2F, -13F, 1F, 2F, 1F).texOffs(132, 110).addBox(-1F, -5F, -5F, 2F, 1F, 6F), PartPose.offsetAndRotation(0F, 0F, -5F, 0F, 0F, 0F));
		head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 98).addBox(-3F, 0F, -10F, 6F, 2F, 10F).texOffs(24, 123).addBox(-2.5F, -2F, -9.5F, 1F, 2F, 1F).texOffs(28, 123).addBox(-0.5F, -2F, -9.5F, 1F, 2F, 1F).texOffs(32, 123).addBox(1.5F, -2F, -9.5F, 1F, 2F, 1F), PartPose.offsetAndRotation(0F, 2F, -3F, 0F, 0F, 0F));
		PartDefinition hornWL1 = head.addOrReplaceChild("hornWL1", CubeListBuilder.create().texOffs(148, 110).addBox(-1F, -5F, -1F, 2F, 5F, 2F), PartPose.offsetAndRotation(3F, -3F, 1F, -1.0472F, 0F, 0F));
		hornWL1.addOrReplaceChild("hornWL2", CubeListBuilder.create().texOffs(186, 110).addBox(-0.5F, -4F, -0.5F, 1F, 4F, 1F), PartPose.offsetAndRotation(0F, -4.5F, 0F, -0.5236F, 0F, 0F));
		PartDefinition hornWR1 = head.addOrReplaceChild("hornWR1", CubeListBuilder.create().texOffs(156, 110).addBox(-1F, -5F, -1F, 2F, 5F, 2F), PartPose.offsetAndRotation(-3F, -3F, 1F, -1.0472F, 0F, 0F));
		hornWR1.addOrReplaceChild("hornWR2", CubeListBuilder.create().texOffs(190, 110).addBox(-0.5F, -4F, -0.5F, 1F, 4F, 1F), PartPose.offsetAndRotation(0F, -4.5F, 0F, -0.5236F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(32, 98).addBox(-3F, -3F, 0F, 6F, 6F, 6F).texOffs(220, 110).addBox(-0.5F, -4F, 1.5F, 1F, 1F, 3F), PartPose.offsetAndRotation(0F, -1F, 9F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(126, 98).addBox(-2.5F, -2.5F, 0F, 5F, 5F, 6F).texOffs(228, 110).addBox(-0.5F, -3.6667F, 1.5F, 1F, 1F, 3F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(148, 98).addBox(-2.5F, -2.5F, 0F, 5F, 5F, 6F).texOffs(236, 110).addBox(-0.5F, -3.3333F, 1.5F, 1F, 1F, 3F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(230, 98).addBox(-2F, -2F, 0F, 4F, 4F, 6F).texOffs(244, 110).addBox(-0.5F, -3F, 1.5F, 1F, 1F, 3F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition tail5 = tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(0, 110).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 6F).texOffs(0, 119).addBox(-0.5F, -2.6667F, 1.5F, 1F, 1F, 3F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition tail6 = tail5.addOrReplaceChild("tail6", CubeListBuilder.create().texOffs(18, 110).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 6F).texOffs(8, 119).addBox(-0.5F, -2.3333F, 1.5F, 1F, 1F, 3F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		tail6.addOrReplaceChild("tail7", CubeListBuilder.create().texOffs(72, 110).addBox(-1F, -1F, 0F, 2F, 2F, 6F).texOffs(16, 119).addBox(-0.5F, -2F, 1.5F, 1F, 1F, 3F).texOffs(164, 110).addBox(-3F, -0.5F, 3.5F, 6F, 1F, 5F).texOffs(36, 110).addBox(-0.5F, -4F, 3.5F, 1F, 4F, 5F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(24, 119).addBox(-25F, -1F, -1F, 25F, 2F, 2F).texOffs(0, 31).addBox(-25F, 0F, 1F, 25F, 1F, 22F), PartPose.offsetAndRotation(-5F, -4F, -4F, 0F, 0.1745F, -0.4363F));
		right_wing.addOrReplaceChild("right_wing2", CubeListBuilder.create().texOffs(78, 119).addBox(-25F, -1F, -1F, 25F, 2F, 2F).texOffs(64, 0).addBox(-12.25F, 0F, 1F, 1F, 1F, 24F).texOffs(94, 31).addBox(-15.25F, 0F, 1F, 1F, 1F, 22F).texOffs(46, 54).addBox(-18.25F, 0F, 1F, 1F, 1F, 20F).texOffs(90, 77).addBox(-21.25F, 0F, 1F, 1F, 1F, 18F).texOffs(88, 54).addBox(-25F, 0.3F, 1F, 25F, 1F, 20F).texOffs(132, 119).addBox(-27F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-25F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(140, 119).addBox(0F, -1F, -1F, 25F, 2F, 2F).texOffs(140, 31).addBox(0F, 0F, 1F, 25F, 1F, 22F), PartPose.offsetAndRotation(5F, -4F, -4F, 0F, -0.1745F, 0.4363F));
		left_wing.addOrReplaceChild("left_wing2", CubeListBuilder.create().texOffs(194, 119).addBox(0F, -1F, -1F, 25F, 2F, 2F).texOffs(114, 0).addBox(11.25F, 0F, 1F, 1F, 1F, 24F).texOffs(0, 54).addBox(14.25F, 0F, 1F, 1F, 1F, 22F).texOffs(178, 54).addBox(17.25F, 0F, 1F, 1F, 1F, 20F).texOffs(128, 77).addBox(20.25F, 0F, 1F, 1F, 1F, 18F).texOffs(0, 77).addBox(0F, 0.3F, 1F, 25F, 1F, 20F).texOffs(248, 119).addBox(25F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(25F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(170, 98).addBox(-2.5F, 0F, -2.5F, 5F, 6F, 5F), PartPose.offsetAndRotation(-5F, 12F, 5F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(48, 110).addBox(-1.5F, 0F, -1.5F, 3F, 6F, 3F).texOffs(88, 110).addBox(-2.5F, 4F, -4.5F, 5F, 2F, 6F).texOffs(36, 123).addBox(-2F, 5F, -6F, 1F, 1F, 2F).texOffs(42, 123).addBox(-0.5F, 5F, -6F, 1F, 1F, 2F).texOffs(48, 123).addBox(1F, 5F, -6F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(190, 98).addBox(-2.5F, 0F, -2.5F, 5F, 6F, 5F), PartPose.offsetAndRotation(5F, 12F, 5F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(60, 110).addBox(-1.5F, 0F, -1.5F, 3F, 6F, 3F).texOffs(110, 110).addBox(-2.5F, 4F, -4.5F, 5F, 2F, 6F).texOffs(54, 123).addBox(-2F, 5F, -6F, 1F, 1F, 2F).texOffs(60, 123).addBox(-0.5F, 5F, -6F, 1F, 1F, 2F).texOffs(66, 123).addBox(1F, 5F, -6F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
