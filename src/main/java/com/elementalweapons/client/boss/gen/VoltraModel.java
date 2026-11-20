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
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(8, 0).addBox(-12F, -46F, -7F, 24F, 32F, 14F).texOffs(192, 96).addBox(-6F, -41F, -8.6F, 12F, 10F, 2F).texOffs(104, 112).addBox(-2F, -39F, -9.4F, 4F, 6F, 2F).texOffs(0, 74).addBox(-13F, -17F, -8F, 26F, 6F, 16F).texOffs(292, 0).addBox(-12F, -12F, -7F, 24F, 12F, 14F), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(84, 0).addBox(-8F, -16F, -8F, 16F, 16F, 16F).texOffs(308, 74).addBox(-2F, -19F, -8F, 4F, 4F, 16F).texOffs(348, 74).addBox(-11F, -17F, -2F, 2F, 10F, 8F).texOffs(368, 74).addBox(9F, -17F, -2F, 2F, 10F, 8F).texOffs(80, 96).addBox(-13F, -22F, 1F, 2F, 8F, 6F).texOffs(96, 96).addBox(11F, -22F, 1F, 2F, 8F, 6F).texOffs(228, 112).addBox(-4F, -5F, -9.6F, 8F, 2F, 2F), PartPose.offsetAndRotation(0F, -52F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(368, 0).addBox(-5F, 0F, -4F, 8F, 18F, 8F).texOffs(84, 74).addBox(-8F, -4F, -7F, 14F, 8F, 14F).texOffs(112, 96).addBox(-8F, -7F, -5F, 10F, 4F, 10F), PartPose.offsetAndRotation(-16F, -48F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(400, 0).addBox(-4F, 0F, -4F, 8F, 18F, 8F).texOffs(0, 0).addBox(-1F, -24F, -7F, 2F, 72F, 2F).texOffs(220, 96).addBox(-3F, -29F, -9F, 6F, 6F, 6F).texOffs(244, 96).addBox(-1F, -39F, -7F, 2F, 10F, 2F).texOffs(116, 112).addBox(-4F, -33F, -7F, 2F, 6F, 2F).texOffs(124, 112).addBox(2F, -33F, -7F, 2F, 6F, 2F).texOffs(0, 96).addBox(-5F, 18F, -5F, 10F, 6F, 10F), PartPose.offsetAndRotation(-1F, 18F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(432, 0).addBox(-3F, 0F, -4F, 8F, 18F, 8F).texOffs(140, 74).addBox(-6F, -4F, -7F, 14F, 8F, 14F).texOffs(152, 96).addBox(-2F, -7F, -5F, 10F, 4F, 10F), PartPose.offsetAndRotation(16F, -48F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(464, 0).addBox(-4F, 0F, -4F, 8F, 18F, 8F).texOffs(252, 96).addBox(-3F, 21F, -5F, 6F, 6F, 6F).texOffs(40, 96).addBox(-5F, 18F, -5F, 10F, 6F, 10F), PartPose.offsetAndRotation(1F, 18F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(132, 112).addBox(-3F, -3F, -1F, 6F, 6F, 2F).texOffs(148, 112).addBox(15F, -3F, -1F, 2F, 6F, 2F).texOffs(180, 112).addBox(9.3137F, 9.3137F, -1F, 4F, 4F, 2F).texOffs(156, 112).addBox(-1F, 13F, -1F, 2F, 6F, 2F).texOffs(192, 112).addBox(-13.3137F, 9.3137F, -1F, 4F, 4F, 2F).texOffs(164, 112).addBox(-17F, -3F, -1F, 2F, 6F, 2F).texOffs(204, 112).addBox(-13.3137F, -13.3137F, -1F, 4F, 4F, 2F).texOffs(172, 112).addBox(-1F, -19F, -1F, 2F, 6F, 2F).texOffs(216, 112).addBox(9.3137F, -13.3137F, -1F, 4F, 4F, 2F), PartPose.offsetAndRotation(0F, -60F, 12F, 0F, 0F, 0F));
		PartDefinition cape1 = body.addOrReplaceChild("cape1", CubeListBuilder.create().texOffs(276, 96).addBox(-13F, 0F, -1F, 26F, 10F, 2F), PartPose.offsetAndRotation(0F, -44F, 9F, 0F, 0F, 0F));
		PartDefinition cape2 = cape1.addOrReplaceChild("cape2", CubeListBuilder.create().texOffs(332, 96).addBox(-13F, 0F, -1F, 26F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition cape3 = cape2.addOrReplaceChild("cape3", CubeListBuilder.create().texOffs(388, 96).addBox(-13F, 0F, -1F, 26F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition cape4 = cape3.addOrReplaceChild("cape4", CubeListBuilder.create().texOffs(444, 96).addBox(-13F, 0F, -1F, 26F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition cape5 = cape4.addOrReplaceChild("cape5", CubeListBuilder.create().texOffs(0, 112).addBox(-12F, 0F, -1F, 24F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		cape5.addOrReplaceChild("cape6", CubeListBuilder.create().texOffs(52, 112).addBox(-12F, 0F, -1F, 24F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(148, 0).addBox(-5F, 0F, -5F, 10F, 18F, 10F).texOffs(196, 74).addBox(-7F, 0F, -7F, 14F, 8F, 14F), PartPose.offsetAndRotation(-6F, -32F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(188, 0).addBox(-4F, 0F, -4F, 8F, 20F, 8F).texOffs(388, 74).addBox(-6F, 16F, -10F, 12F, 4F, 14F), PartPose.offsetAndRotation(0F, 18F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(220, 0).addBox(-5F, 0F, -5F, 10F, 18F, 10F).texOffs(252, 74).addBox(-7F, 0F, -7F, 14F, 8F, 14F), PartPose.offsetAndRotation(6F, -32F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(260, 0).addBox(-4F, 0F, -4F, 8F, 20F, 8F).texOffs(440, 74).addBox(-6F, 16F, -10F, 12F, 4F, 14F), PartPose.offsetAndRotation(0F, 18F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
