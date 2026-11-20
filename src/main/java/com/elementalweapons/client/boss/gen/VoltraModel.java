// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class VoltraModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"body", "halo"},
			{"body", "cape1"},
			{"body", "cape1", "cape2"},
			{"body", "cape1", "cape2", "cape3"},
			{"body", "cape1", "cape2", "cape3", "cape4"},
			{"body", "cape1", "cape2", "cape3", "cape4", "cape5"},
			{"body", "cape1", "cape2", "cape3", "cape4", "cape5", "cape6"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"}
	};

	public VoltraModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(4, 0).addBox(-6F, -23F, -3.5F, 12F, 16F, 7F).texOffs(96, 48).addBox(-3F, -20.5F, -4.3F, 6F, 5F, 1F).texOffs(52, 56).addBox(-1F, -19.5F, -4.7F, 2F, 3F, 1F).texOffs(0, 37).addBox(-6.5F, -8.5F, -4F, 13F, 3F, 8F).texOffs(146, 0).addBox(-6F, -6F, -3.5F, 12F, 6F, 7F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(42, 0).addBox(-4F, -8F, -4F, 8F, 8F, 8F).texOffs(154, 37).addBox(-1F, -9.5F, -4F, 2F, 2F, 8F).texOffs(174, 37).addBox(-5.5F, -8.5F, -1F, 1F, 5F, 4F).texOffs(184, 37).addBox(4.5F, -8.5F, -1F, 1F, 5F, 4F).texOffs(40, 48).addBox(-6.5F, -11F, 0.5F, 1F, 4F, 3F).texOffs(48, 48).addBox(5.5F, -11F, 0.5F, 1F, 4F, 3F).texOffs(114, 56).addBox(-2F, -2.5F, -4.8F, 4F, 1F, 1F), PartPose.offsetAndRotation(0F, -26F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(184, 0).addBox(-2.5F, 0F, -2F, 4F, 9F, 4F).texOffs(42, 37).addBox(-4F, -2F, -3.5F, 7F, 4F, 7F).texOffs(56, 48).addBox(-4F, -3.5F, -2.5F, 5F, 2F, 5F), PartPose.offsetAndRotation(-8F, -24F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(200, 0).addBox(-2F, 0F, -2F, 4F, 9F, 4F).texOffs(0, 0).addBox(-0.5F, -12F, -3.5F, 1F, 36F, 1F).texOffs(110, 48).addBox(-1.5F, -14.5F, -4.5F, 3F, 3F, 3F).texOffs(122, 48).addBox(-0.5F, -19.5F, -3.5F, 1F, 5F, 1F).texOffs(58, 56).addBox(-2F, -16.5F, -3.5F, 1F, 3F, 1F).texOffs(62, 56).addBox(1F, -16.5F, -3.5F, 1F, 3F, 1F).texOffs(0, 48).addBox(-2.5F, 9F, -2.5F, 5F, 3F, 5F), PartPose.offsetAndRotation(-0.5F, 9F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(216, 0).addBox(-1.5F, 0F, -2F, 4F, 9F, 4F).texOffs(70, 37).addBox(-3F, -2F, -3.5F, 7F, 4F, 7F).texOffs(76, 48).addBox(-1F, -3.5F, -2.5F, 5F, 2F, 5F), PartPose.offsetAndRotation(8F, -24F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(232, 0).addBox(-2F, 0F, -2F, 4F, 9F, 4F).texOffs(126, 48).addBox(-1.5F, 10.5F, -2.5F, 3F, 3F, 3F).texOffs(20, 48).addBox(-2.5F, 9F, -2.5F, 5F, 3F, 5F), PartPose.offsetAndRotation(0.5F, 9F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(66, 56).addBox(-1.5F, -1.5F, -0.5F, 3F, 3F, 1F).texOffs(74, 56).addBox(7.5F, -1.5F, -0.5F, 1F, 3F, 1F).texOffs(90, 56).addBox(4.6569F, 4.6569F, -0.5F, 2F, 2F, 1F).texOffs(78, 56).addBox(-0.5F, 6.5F, -0.5F, 1F, 3F, 1F).texOffs(96, 56).addBox(-6.6569F, 4.6569F, -0.5F, 2F, 2F, 1F).texOffs(82, 56).addBox(-8.5F, -1.5F, -0.5F, 1F, 3F, 1F).texOffs(102, 56).addBox(-6.6569F, -6.6569F, -0.5F, 2F, 2F, 1F).texOffs(86, 56).addBox(-0.5F, -9.5F, -0.5F, 1F, 3F, 1F).texOffs(108, 56).addBox(4.6569F, -6.6569F, -0.5F, 2F, 2F, 1F), PartPose.offsetAndRotation(0F, -30F, 6F, 0F, 0F, 0F));
		PartDefinition cape1 = body.addOrReplaceChild("cape1", CubeListBuilder.create().texOffs(138, 48).addBox(-6.5F, 0F, -0.5F, 13F, 5F, 1F), PartPose.offsetAndRotation(0F, -22F, 4.5F, 0F, 0F, 0F));
		PartDefinition cape2 = cape1.addOrReplaceChild("cape2", CubeListBuilder.create().texOffs(166, 48).addBox(-6.5F, 0F, -0.5F, 13F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition cape3 = cape2.addOrReplaceChild("cape3", CubeListBuilder.create().texOffs(194, 48).addBox(-6.5F, 0F, -0.5F, 13F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition cape4 = cape3.addOrReplaceChild("cape4", CubeListBuilder.create().texOffs(222, 48).addBox(-6.5F, 0F, -0.5F, 13F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition cape5 = cape4.addOrReplaceChild("cape5", CubeListBuilder.create().texOffs(0, 56).addBox(-6F, 0F, -0.5F, 12F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		cape5.addOrReplaceChild("cape6", CubeListBuilder.create().texOffs(26, 56).addBox(-6F, 0F, -0.5F, 12F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(74, 0).addBox(-2.5F, 0F, -2.5F, 5F, 9F, 5F).texOffs(98, 37).addBox(-3.5F, 0F, -3.5F, 7F, 4F, 7F), PartPose.offsetAndRotation(-3F, -4F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(94, 0).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(194, 37).addBox(-3F, 8F, -5F, 6F, 2F, 7F), PartPose.offsetAndRotation(0F, 9F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(110, 0).addBox(-2.5F, 0F, -2.5F, 5F, 9F, 5F).texOffs(126, 37).addBox(-3.5F, 0F, -3.5F, 7F, 4F, 7F), PartPose.offsetAndRotation(3F, -4F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(130, 0).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(220, 37).addBox(-3F, 8F, -5F, 6F, 2F, 7F), PartPose.offsetAndRotation(0F, 9F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
