// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class SteamColossusModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"body", "spin_g920"},
			{"body", "spin_g921"},
			{"body", "bob_s1"},
			{"body", "bob_s2"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"}
	};

	public SteamColossusModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-12F, -26F, -7F, 24F, 20F, 14F).texOffs(0, 82).addBox(-5F, -20F, -7.7F, 10F, 10F, 1F).texOffs(94, 93).addBox(-6F, -15.5F, -8.1F, 12F, 1F, 1F).texOffs(120, 93).addBox(-6F, -11.5F, -8.1F, 12F, 1F, 1F).texOffs(0, 34).addBox(-13F, -29F, -7.5F, 26F, 4F, 15F).texOffs(0, 53).addBox(-9F, -7F, -6F, 18F, 4F, 12F).texOffs(40, 93).addBox(-11F, -11F, -7.9F, 2F, 2F, 1F).texOffs(0, 69).addBox(-7.5F, -41F, 3.5F, 3F, 10F, 3F).texOffs(12, 69).addBox(4.5F, -41F, 3.5F, 3F, 10F, 3F).texOffs(234, 82).addBox(-8F, -42F, 3F, 4F, 1F, 4F).texOffs(0, 93).addBox(4F, -42F, 3F, 4F, 1F, 4F).texOffs(180, 69).addBox(-8F, -25F, 6.5F, 16F, 10F, 2F), PartPose.offsetAndRotation(0F, -4F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(138, 34).addBox(-5F, -8F, -4.5F, 10F, 8F, 9F).texOffs(134, 82).addBox(-3F, -10F, -3F, 6F, 2F, 6F).texOffs(158, 82).addBox(-7F, -6F, -2F, 2F, 4F, 4F).texOffs(170, 82).addBox(5F, -6F, -2F, 2F, 4F, 4F).texOffs(16, 93).addBox(-1F, -12.5F, -1F, 2F, 3F, 2F), PartPose.offsetAndRotation(0F, -30F, -2F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(82, 34).addBox(-4F, 0F, -3.5F, 7F, 11F, 7F).texOffs(60, 53).addBox(-5.5F, -2F, -5F, 10F, 4F, 10F).texOffs(34, 82).addBox(-5.5F, -3.5F, -4F, 8F, 2F, 8F), PartPose.offsetAndRotation(-14F, -27F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(176, 34).addBox(-3.5F, 0F, -3.5F, 7F, 10F, 7F).texOffs(22, 82).addBox(-1.5F, 8F, -1.5F, 3F, 8F, 3F).texOffs(100, 53).addBox(-5F, 14.5F, -3.5F, 10F, 7F, 7F).texOffs(66, 82).addBox(-6F, 15.5F, -2.5F, 1F, 5F, 5F).texOffs(78, 82).addBox(5F, 15.5F, -2.5F, 1F, 5F, 5F).texOffs(24, 69).addBox(-4.5F, 1F, -4.5F, 9F, 4F, 9F), PartPose.offsetAndRotation(-0.5F, 11F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(110, 34).addBox(-3F, 0F, -3.5F, 7F, 11F, 7F).texOffs(134, 53).addBox(-4.5F, -2F, -5F, 10F, 4F, 10F).texOffs(90, 82).addBox(-2.5F, -3.5F, -4F, 8F, 2F, 8F), PartPose.offsetAndRotation(14F, -27F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(204, 34).addBox(-3.5F, 0F, -3.5F, 7F, 10F, 7F).texOffs(60, 69).addBox(-4.5F, 1F, -4.5F, 9F, 4F, 9F).texOffs(216, 69).addBox(-3F, 10F, -4F, 6F, 6F, 6F).texOffs(24, 93).addBox(-2F, 16F, -3F, 4F, 1F, 4F), PartPose.offsetAndRotation(0.5F, 11F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("spin_g920", CubeListBuilder.create().texOffs(182, 82).addBox(-3F, -3F, -0.5F, 6F, 6F, 1F).texOffs(46, 93).addBox(-4F, -1F, -0.5F, 8F, 2F, 1F).texOffs(122, 82).addBox(-1F, -4F, -0.5F, 2F, 8F, 1F).texOffs(64, 93).addBox(-1F, -1F, -1F, 2F, 2F, 1F), PartPose.offsetAndRotation(-9F, -20F, -7.6F, 0F, 0F, 0F));
		body.addOrReplaceChild("spin_g921", CubeListBuilder.create().texOffs(196, 82).addBox(-3F, -3F, -0.5F, 6F, 6F, 1F).texOffs(70, 93).addBox(-4F, -1F, -0.5F, 8F, 2F, 1F).texOffs(128, 82).addBox(-1F, -4F, -0.5F, 2F, 8F, 1F).texOffs(88, 93).addBox(-1F, -1F, -1F, 2F, 2F, 1F), PartPose.offsetAndRotation(9F, -21F, -7.6F, 0F, 0F, 0F));
		body.addOrReplaceChild("bob_s1", CubeListBuilder.create().texOffs(210, 82).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(-6F, -44F, 5F, 0F, 0F, 0F));
		body.addOrReplaceChild("bob_s2", CubeListBuilder.create().texOffs(222, 82).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(6F, -45F, 5F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(76, 0).addBox(-4F, 0F, -4F, 8F, 12F, 8F).texOffs(174, 53).addBox(-5F, 0F, -5F, 10F, 4F, 10F), PartPose.offsetAndRotation(-6F, 0F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(108, 0).addBox(-4F, 0F, -4F, 8F, 12F, 8F).texOffs(96, 69).addBox(-5F, 10F, -7F, 10F, 2F, 11F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(140, 0).addBox(-4F, 0F, -4F, 8F, 12F, 8F).texOffs(214, 53).addBox(-5F, 0F, -5F, 10F, 4F, 10F), PartPose.offsetAndRotation(6F, 0F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(172, 0).addBox(-4F, 0F, -4F, 8F, 12F, 8F).texOffs(138, 69).addBox(-5F, 10F, -7F, 10F, 2F, 11F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
