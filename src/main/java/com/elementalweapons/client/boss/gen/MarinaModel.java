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
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(8, 0).addBox(-10F, -24F, -6F, 20F, 24F, 12F).texOffs(272, 0).addBox(-11F, -23F, -7F, 22F, 8F, 14F).texOffs(484, 82).addBox(-3F, -23F, -8F, 6F, 6F, 2F).texOffs(0, 62).addBox(-11F, -1F, -7F, 22F, 6F, 14F).texOffs(176, 96).addBox(-2F, 0F, -8.6F, 4F, 4F, 2F).texOffs(344, 0).addBox(-12F, -12F, -5F, 2F, 12F, 10F).texOffs(368, 0).addBox(10F, -12F, -5F, 2F, 12F, 10F), PartPose.offsetAndRotation(0F, -28F, 0F, 0F, 0F, 0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(72, 0).addBox(-8F, -16F, -8F, 16F, 16F, 16F).texOffs(136, 0).addBox(-9F, -17F, -6F, 18F, 8F, 16F).texOffs(484, 62).addBox(-10F, -10F, -3F, 2F, 8F, 6F).texOffs(0, 82).addBox(8F, -10F, -3F, 2F, 8F, 6F).texOffs(72, 62).addBox(-9F, -20F, -9F, 18F, 2F, 18F).texOffs(500, 82).addBox(-7F, -26F, -9F, 2F, 6F, 2F).texOffs(0, 96).addBox(-1F, -26F, -9F, 2F, 6F, 2F).texOffs(8, 96).addBox(5F, -26F, -9F, 2F, 6F, 2F).texOffs(188, 96).addBox(-1F, -28F, -9F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, -28F, 0F, 0F, 0F, 0F));
		PartDefinition swayH1 = head.addOrReplaceChild("swayH1", CubeListBuilder.create().texOffs(180, 82).addBox(-8F, 0F, -2F, 16F, 8F, 4F), PartPose.offsetAndRotation(0F, -12F, 8F, 0F, 0F, 0F));
		PartDefinition swayH2 = swayH1.addOrReplaceChild("swayH2", CubeListBuilder.create().texOffs(220, 82).addBox(-7F, 0F, -2F, 14F, 8F, 4F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		PartDefinition swayH3 = swayH2.addOrReplaceChild("swayH3", CubeListBuilder.create().texOffs(416, 82).addBox(-6F, 0F, -1F, 12F, 8F, 2F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		swayH3.addOrReplaceChild("swayH4", CubeListBuilder.create().texOffs(444, 82).addBox(-5F, 0F, -1F, 10F, 8F, 2F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(144, 62).addBox(-4F, 0F, -3F, 6F, 14F, 6F).texOffs(168, 62).addBox(-7F, -4F, -6F, 12F, 8F, 12F).texOffs(256, 82).addBox(-7F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(-12F, -24F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(216, 62).addBox(-3F, 0F, -3F, 6F, 14F, 6F).texOffs(0, 0).addBox(-1F, -38F, -9F, 2F, 60F, 2F).texOffs(196, 96).addBox(-7F, -39F, -9F, 14F, 2F, 2F).texOffs(468, 82).addBox(-7F, -46F, -9F, 2F, 8F, 2F).texOffs(16, 82).addBox(-1F, -52F, -9F, 2F, 12F, 2F).texOffs(476, 82).addBox(5F, -46F, -9F, 2F, 8F, 2F).texOffs(288, 82).addBox(-3F, -39F, -11F, 6F, 6F, 6F).texOffs(24, 82).addBox(-4F, 14F, -4F, 8F, 6F, 8F), PartPose.offsetAndRotation(-1F, 14F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(240, 62).addBox(-2F, 0F, -3F, 6F, 14F, 6F).texOffs(264, 62).addBox(-5F, -4F, -6F, 12F, 8F, 12F).texOffs(312, 82).addBox(-1F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(12F, -24F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(312, 62).addBox(-3F, 0F, -3F, 6F, 14F, 6F).texOffs(424, 62).addBox(-4F, 14F, -8F, 8F, 8F, 8F).texOffs(56, 82).addBox(-4F, 14F, -4F, 8F, 6F, 8F), PartPose.offsetAndRotation(1F, 14F, 0F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(204, 0).addBox(-10F, 0F, -7F, 20F, 10F, 14F).texOffs(344, 82).addBox(-1F, 1F, 6F, 2F, 8F, 4F), PartPose.offsetAndRotation(0F, 4F, 0F, 0.1745F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(392, 0).addBox(-9F, 0F, -6F, 18F, 10F, 12F).texOffs(356, 82).addBox(-1F, 1F, 5F, 2F, 8F, 4F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(336, 62).addBox(-7F, 0F, -5F, 14F, 10F, 10F).texOffs(368, 82).addBox(-1F, 1F, 4F, 2F, 8F, 4F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(384, 62).addBox(-6F, 0F, -4F, 12F, 10F, 8F).texOffs(380, 82).addBox(-1F, 1F, 3F, 2F, 8F, 4F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition tail5 = tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(456, 62).addBox(-4F, 0F, -3F, 8F, 10F, 6F).texOffs(392, 82).addBox(-1F, 1F, 2F, 2F, 8F, 4F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		tail5.addOrReplaceChild("tail6", CubeListBuilder.create().texOffs(88, 82).addBox(-3F, 0F, -2F, 6F, 10F, 4F).texOffs(404, 82).addBox(-1F, 1F, 1F, 2F, 8F, 4F).texOffs(108, 82).addBox(-16F, 4F, -1F, 16F, 12F, 2F).texOffs(144, 82).addBox(0F, 4F, -1F, 16F, 12F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition ring = body.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb0", CubeListBuilder.create().texOffs(16, 96).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(26F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb1", CubeListBuilder.create().texOffs(32, 96).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(21.0344F, 0F, 15.2824F, 0F, 0.6283F, 0F));
		ring.addOrReplaceChild("orb2", CubeListBuilder.create().texOffs(48, 96).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(8.0344F, 0F, 24.7275F, 0F, 1.2566F, 0F));
		ring.addOrReplaceChild("orb3", CubeListBuilder.create().texOffs(64, 96).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-8.0344F, 0F, 24.7275F, 0F, 1.885F, 0F));
		ring.addOrReplaceChild("orb4", CubeListBuilder.create().texOffs(80, 96).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-21.0344F, 0F, 15.2824F, 0F, 2.5133F, 0F));
		ring.addOrReplaceChild("orb5", CubeListBuilder.create().texOffs(96, 96).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-26F, 0F, 0F, 0F, 3.1416F, 0F));
		ring.addOrReplaceChild("orb6", CubeListBuilder.create().texOffs(112, 96).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-21.0344F, 0F, -15.2824F, 0F, 3.7699F, 0F));
		ring.addOrReplaceChild("orb7", CubeListBuilder.create().texOffs(128, 96).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-8.0344F, 0F, -24.7275F, 0F, 4.3982F, 0F));
		ring.addOrReplaceChild("orb8", CubeListBuilder.create().texOffs(144, 96).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(8.0344F, 0F, -24.7275F, 0F, 5.0265F, 0F));
		ring.addOrReplaceChild("orb9", CubeListBuilder.create().texOffs(160, 96).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(21.0344F, 0F, -15.2824F, 0F, 5.6549F, 0F));
		ring.addOrReplaceChild("pearlo0", CubeListBuilder.create().texOffs(228, 96).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(18F, -6F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("pearlo1", CubeListBuilder.create().texOffs(236, 96).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(5.5623F, -6F, 17.119F, 0F, 1.2566F, 0F));
		ring.addOrReplaceChild("pearlo2", CubeListBuilder.create().texOffs(244, 96).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-14.5623F, -6F, 10.5801F, 0F, 2.5133F, 0F));
		ring.addOrReplaceChild("pearlo3", CubeListBuilder.create().texOffs(252, 96).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-14.5623F, -6F, -10.5801F, 0F, 3.7699F, 0F));
		ring.addOrReplaceChild("pearlo4", CubeListBuilder.create().texOffs(260, 96).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(5.5623F, -6F, -17.119F, 0F, 5.0265F, 0F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
