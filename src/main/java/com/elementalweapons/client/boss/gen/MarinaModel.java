// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class MarinaModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "head", "swayH1"},
			{"body", "head", "swayH1", "swayH2"},
			{"body", "head", "swayH1", "swayH2", "swayH3"},
			{"body", "head", "swayH1", "swayH2", "swayH3", "swayH4"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"},
			{"body", "tail1", "tail2", "tail3", "tail4"},
			{"body", "tail1", "tail2", "tail3", "tail4", "tail5"},
			{"body", "tail1", "tail2", "tail3", "tail4", "tail5", "tail6"},
			{"body", "ring"},
			{"body", "ring", "orb0"},
			{"body", "ring", "orb1"},
			{"body", "ring", "orb2"},
			{"body", "ring", "orb3"},
			{"body", "ring", "orb4"},
			{"body", "ring", "orb5"},
			{"body", "ring", "orb6"},
			{"body", "ring", "orb7"},
			{"body", "ring", "orb8"},
			{"body", "ring", "orb9"},
			{"body", "ring", "pearlo0"},
			{"body", "ring", "pearlo1"},
			{"body", "ring", "pearlo2"},
			{"body", "ring", "pearlo3"},
			{"body", "ring", "pearlo4"}
	};

	public MarinaModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(4, 0).addBox(-5F, -12F, -3F, 10F, 12F, 6F).texOffs(136, 0).addBox(-5.5F, -11.5F, -3.5F, 11F, 4F, 7F).texOffs(242, 41).addBox(-1.5F, -11.5F, -4F, 3F, 3F, 1F).texOffs(0, 31).addBox(-5.5F, -0.5F, -3.5F, 11F, 3F, 7F).texOffs(88, 48).addBox(-1F, 0F, -4.3F, 2F, 2F, 1F).texOffs(172, 0).addBox(-6F, -6F, -2.5F, 1F, 6F, 5F).texOffs(184, 0).addBox(5F, -6F, -2.5F, 1F, 6F, 5F), PartPose.offsetAndRotation(0F, -2F, 0F, 0F, 0F, 0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(36, 0).addBox(-4F, -8F, -4F, 8F, 8F, 8F).texOffs(68, 0).addBox(-4.5F, -8.5F, -3F, 9F, 4F, 8F).texOffs(242, 31).addBox(-5F, -5F, -1.5F, 1F, 4F, 3F).texOffs(0, 41).addBox(4F, -5F, -1.5F, 1F, 4F, 3F).texOffs(36, 31).addBox(-4.5F, -10F, -4.5F, 9F, 1F, 9F).texOffs(250, 41).addBox(-3.5F, -13F, -4.5F, 1F, 3F, 1F).texOffs(0, 48).addBox(-0.5F, -13F, -4.5F, 1F, 3F, 1F).texOffs(4, 48).addBox(2.5F, -13F, -4.5F, 1F, 3F, 1F).texOffs(94, 48).addBox(-0.5F, -14F, -4.5F, 1F, 2F, 1F), PartPose.offsetAndRotation(0F, -14F, 0F, 0F, 0F, 0F));
		PartDefinition swayH1 = head.addOrReplaceChild("swayH1", CubeListBuilder.create().texOffs(90, 41).addBox(-4F, 0F, -1F, 8F, 4F, 2F), PartPose.offsetAndRotation(0F, -6F, 4F, 0F, 0F, 0F));
		PartDefinition swayH2 = swayH1.addOrReplaceChild("swayH2", CubeListBuilder.create().texOffs(110, 41).addBox(-3.5F, 0F, -1F, 7F, 4F, 2F), PartPose.offsetAndRotation(0F, 4F, 0F, 0F, 0F, 0F));
		PartDefinition swayH3 = swayH2.addOrReplaceChild("swayH3", CubeListBuilder.create().texOffs(208, 41).addBox(-3F, 0F, -0.5F, 6F, 4F, 1F), PartPose.offsetAndRotation(0F, 4F, 0F, 0F, 0F, 0F));
		swayH3.addOrReplaceChild("swayH4", CubeListBuilder.create().texOffs(222, 41).addBox(-2.5F, 0F, -0.5F, 5F, 4F, 1F), PartPose.offsetAndRotation(0F, 4F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(72, 31).addBox(-2F, 0F, -1.5F, 3F, 7F, 3F).texOffs(84, 31).addBox(-3.5F, -2F, -3F, 6F, 4F, 6F).texOffs(128, 41).addBox(-3.5F, -3.5F, -2F, 4F, 2F, 4F), PartPose.offsetAndRotation(-6F, -12F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(108, 31).addBox(-1.5F, 0F, -1.5F, 3F, 7F, 3F).texOffs(0, 0).addBox(-0.5F, -19F, -4.5F, 1F, 30F, 1F).texOffs(98, 48).addBox(-3.5F, -19.5F, -4.5F, 7F, 1F, 1F).texOffs(234, 41).addBox(-3.5F, -23F, -4.5F, 1F, 4F, 1F).texOffs(8, 41).addBox(-0.5F, -26F, -4.5F, 1F, 6F, 1F).texOffs(238, 41).addBox(2.5F, -23F, -4.5F, 1F, 4F, 1F).texOffs(144, 41).addBox(-1.5F, -19.5F, -5.5F, 3F, 3F, 3F).texOffs(12, 41).addBox(-2F, 7F, -2F, 4F, 3F, 4F), PartPose.offsetAndRotation(-0.5F, 7F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(120, 31).addBox(-1F, 0F, -1.5F, 3F, 7F, 3F).texOffs(132, 31).addBox(-2.5F, -2F, -3F, 6F, 4F, 6F).texOffs(156, 41).addBox(-0.5F, -3.5F, -2F, 4F, 2F, 4F), PartPose.offsetAndRotation(6F, -12F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(156, 31).addBox(-1.5F, 0F, -1.5F, 3F, 7F, 3F).texOffs(212, 31).addBox(-2F, 7F, -4F, 4F, 4F, 4F).texOffs(28, 41).addBox(-2F, 7F, -2F, 4F, 3F, 4F), PartPose.offsetAndRotation(0.5F, 7F, 0F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(102, 0).addBox(-5F, 0F, -3.5F, 10F, 5F, 7F).texOffs(172, 41).addBox(-0.5F, 0.5F, 3F, 1F, 4F, 2F), PartPose.offsetAndRotation(0F, 2F, 0F, 0.1745F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(196, 0).addBox(-4.5F, 0F, -3F, 9F, 5F, 6F).texOffs(178, 41).addBox(-0.5F, 0.5F, 2.5F, 1F, 4F, 2F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(168, 31).addBox(-3.5F, 0F, -2.5F, 7F, 5F, 5F).texOffs(184, 41).addBox(-0.5F, 0.5F, 2F, 1F, 4F, 2F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(192, 31).addBox(-3F, 0F, -2F, 6F, 5F, 4F).texOffs(190, 41).addBox(-0.5F, 0.5F, 1.5F, 1F, 4F, 2F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition tail5 = tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(228, 31).addBox(-2F, 0F, -1.5F, 4F, 5F, 3F).texOffs(196, 41).addBox(-0.5F, 0.5F, 1F, 1F, 4F, 2F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		tail5.addOrReplaceChild("tail6", CubeListBuilder.create().texOffs(44, 41).addBox(-1.5F, 0F, -1F, 3F, 5F, 2F).texOffs(202, 41).addBox(-0.5F, 0.5F, 0.5F, 1F, 4F, 2F).texOffs(54, 41).addBox(-8F, 2F, -0.5F, 8F, 6F, 1F).texOffs(72, 41).addBox(0F, 2F, -0.5F, 8F, 6F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition ring = body.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb0", CubeListBuilder.create().texOffs(8, 48).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(13F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb1", CubeListBuilder.create().texOffs(16, 48).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(10.5172F, 0F, 7.6412F, 0F, 0.6283F, 0F));
		ring.addOrReplaceChild("orb2", CubeListBuilder.create().texOffs(24, 48).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(4.0172F, 0F, 12.3637F, 0F, 1.2566F, 0F));
		ring.addOrReplaceChild("orb3", CubeListBuilder.create().texOffs(32, 48).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-4.0172F, 0F, 12.3637F, 0F, 1.885F, 0F));
		ring.addOrReplaceChild("orb4", CubeListBuilder.create().texOffs(40, 48).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-10.5172F, 0F, 7.6412F, 0F, 2.5133F, 0F));
		ring.addOrReplaceChild("orb5", CubeListBuilder.create().texOffs(48, 48).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-13F, 0F, 0F, 0F, 3.1416F, 0F));
		ring.addOrReplaceChild("orb6", CubeListBuilder.create().texOffs(56, 48).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-10.5172F, 0F, -7.6412F, 0F, 3.7699F, 0F));
		ring.addOrReplaceChild("orb7", CubeListBuilder.create().texOffs(64, 48).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-4.0172F, 0F, -12.3637F, 0F, 4.3982F, 0F));
		ring.addOrReplaceChild("orb8", CubeListBuilder.create().texOffs(72, 48).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(4.0172F, 0F, -12.3637F, 0F, 5.0265F, 0F));
		ring.addOrReplaceChild("orb9", CubeListBuilder.create().texOffs(80, 48).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(10.5172F, 0F, -7.6412F, 0F, 5.6549F, 0F));
		ring.addOrReplaceChild("pearlo0", CubeListBuilder.create().texOffs(114, 48).addBox(-0.5F, -0.5F, -0.5F, 1F, 1F, 1F), PartPose.offsetAndRotation(9F, -3F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("pearlo1", CubeListBuilder.create().texOffs(118, 48).addBox(-0.5F, -0.5F, -0.5F, 1F, 1F, 1F), PartPose.offsetAndRotation(2.7812F, -3F, 8.5595F, 0F, 1.2566F, 0F));
		ring.addOrReplaceChild("pearlo2", CubeListBuilder.create().texOffs(122, 48).addBox(-0.5F, -0.5F, -0.5F, 1F, 1F, 1F), PartPose.offsetAndRotation(-7.2812F, -3F, 5.2901F, 0F, 2.5133F, 0F));
		ring.addOrReplaceChild("pearlo3", CubeListBuilder.create().texOffs(126, 48).addBox(-0.5F, -0.5F, -0.5F, 1F, 1F, 1F), PartPose.offsetAndRotation(-7.2812F, -3F, -5.2901F, 0F, 3.7699F, 0F));
		ring.addOrReplaceChild("pearlo4", CubeListBuilder.create().texOffs(130, 48).addBox(-0.5F, -0.5F, -0.5F, 1F, 1F, 1F), PartPose.offsetAndRotation(2.7812F, -3F, -8.5595F, 0F, 5.0265F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
