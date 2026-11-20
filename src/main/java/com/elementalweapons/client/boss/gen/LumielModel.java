// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class LumielModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "halo"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"body", "right_wing"},
			{"body", "right_wing", "right_wing2"},
			{"body", "left_wing"},
			{"body", "left_wing", "left_wing2"},
			{"body", "swayS1"},
			{"body", "swayS1", "swayS2"},
			{"body", "swayS1", "swayS2", "swayS3"},
			{"body", "cape1"},
			{"body", "cape1", "cape2"},
			{"body", "cape1", "cape2", "cape3"},
			{"body", "cape1", "cape2", "cape3", "cape4"},
			{"body", "cape1", "cape2", "cape3", "cape4", "cape5"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"}
	};

	public LumielModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-12F, -44F, -7F, 24F, 32F, 14F).texOffs(60, 236).addBox(-6F, -40F, -8.6F, 12F, 12F, 2F).texOffs(352, 252).addBox(-2F, -36F, -9.6F, 4F, 4F, 2F).texOffs(0, 214).addBox(-13F, -15F, -8F, 26F, 6F, 16F), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(80, 124).addBox(-8F, -16F, -8F, 16F, 16F, 16F).texOffs(460, 188).addBox(-2F, -20F, -9F, 4F, 6F, 18F).texOffs(412, 214).addBox(-10F, -13F, -3F, 2F, 6F, 10F).texOffs(436, 214).addBox(8F, -13F, -3F, 2F, 6F, 10F).texOffs(168, 236).addBox(-12F, -18F, 2F, 2F, 8F, 4F).texOffs(180, 236).addBox(10F, -18F, 2F, 2F, 8F, 4F), PartPose.offsetAndRotation(0F, -48F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(456, 252).addBox(-12F, -13F, -1F, 24F, 2F, 2F).texOffs(0, 260).addBox(-12F, 11F, -1F, 24F, 2F, 2F).texOffs(440, 158).addBox(-13F, -12F, -1F, 2F, 24F, 2F).texOffs(448, 158).addBox(11F, -12F, -1F, 2F, 24F, 2F).texOffs(364, 252).addBox(-11F, -11F, -1F, 4F, 4F, 2F).texOffs(376, 252).addBox(7F, -11F, -1F, 4F, 4F, 2F).texOffs(388, 252).addBox(-11F, 7F, -1F, 4F, 4F, 2F).texOffs(400, 252).addBox(7F, 7F, -1F, 4F, 4F, 2F), PartPose.offsetAndRotation(0F, -66F, 6F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(456, 158).addBox(-5F, 0F, -4F, 8F, 18F, 8F).texOffs(84, 214).addBox(-8F, -4F, -7F, 14F, 8F, 14F).texOffs(88, 236).addBox(-8F, -7F, -5F, 10F, 4F, 10F), PartPose.offsetAndRotation(-14F, -44F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(0, 188).addBox(-4F, 0F, -4F, 8F, 18F, 8F).texOffs(448, 236).addBox(-2F, 21F, -8F, 4F, 6F, 4F).texOffs(412, 252).addBox(-7F, 27F, -8F, 14F, 2F, 4F).texOffs(260, 0).addBox(-3F, 30F, -7F, 6F, 40F, 2F).texOffs(448, 252).addBox(-1F, 70F, -7F, 2F, 4F, 2F).texOffs(460, 214).addBox(-5F, 18F, -5F, 10F, 6F, 10F), PartPose.offsetAndRotation(-1F, 18F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 188).addBox(-3F, 0F, -4F, 8F, 18F, 8F).texOffs(140, 214).addBox(-6F, -4F, -7F, 14F, 8F, 14F).texOffs(128, 236).addBox(-2F, -7F, -5F, 10F, 4F, 10F), PartPose.offsetAndRotation(14F, -44F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(64, 188).addBox(-4F, 0F, -4F, 8F, 18F, 8F).texOffs(252, 86).addBox(4F, 0F, -8F, 2F, 20F, 16F).texOffs(0, 236).addBox(5F, 6F, -4F, 2F, 8F, 8F).texOffs(20, 236).addBox(-5F, 18F, -5F, 10F, 6F, 10F), PartPose.offsetAndRotation(1F, 18F, 0F, 0F, 0F, 0F));
		PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 252).addBox(-40F, -2F, -2F, 40F, 4F, 4F).texOffs(0, 46).addBox(-40F, 0F, 2F, 10F, 2F, 38F).texOffs(296, 124).addBox(-30F, 0F, 2F, 10F, 2F, 28F).texOffs(372, 124).addBox(-20F, 0F, 2F, 10F, 2F, 28F).texOffs(96, 46).addBox(-10F, 0F, 2F, 10F, 2F, 38F), PartPose.offsetAndRotation(-8F, -40F, 8F, -0.1745F, -0.4363F, -0.6109F));
		right_wing.addOrReplaceChild("right_wing2", CubeListBuilder.create().texOffs(88, 252).addBox(-40F, -2F, -2F, 40F, 4F, 4F).texOffs(76, 0).addBox(-19.8F, 0F, 2F, 2F, 2F, 44F).texOffs(276, 0).addBox(-24.6F, 0F, 2F, 2F, 2F, 40F).texOffs(384, 46).addBox(-29.4F, 0F, 2F, 2F, 2F, 36F).texOffs(288, 86).addBox(-34.2F, 0F, 2F, 2F, 2F, 32F).texOffs(0, 86).addBox(-40F, 0.6F, 2F, 8F, 2F, 36F).texOffs(0, 158).addBox(-32F, 0.6F, 2F, 8F, 2F, 28F).texOffs(356, 86).addBox(-24F, 0.6F, 2F, 8F, 2F, 32F).texOffs(96, 188).addBox(-16F, 0.6F, 2F, 8F, 2F, 24F).texOffs(144, 124).addBox(-8F, 0.6F, 2F, 8F, 2F, 30F), PartPose.offsetAndRotation(-40F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(176, 252).addBox(0F, -2F, -2F, 40F, 4F, 4F).texOffs(192, 46).addBox(0F, 0F, 2F, 10F, 2F, 38F).texOffs(72, 158).addBox(10F, 0F, 2F, 10F, 2F, 28F).texOffs(148, 158).addBox(20F, 0F, 2F, 10F, 2F, 28F).texOffs(288, 46).addBox(30F, 0F, 2F, 10F, 2F, 38F), PartPose.offsetAndRotation(8F, -40F, 8F, -0.1745F, 0.4363F, 0.6109F));
		left_wing.addOrReplaceChild("left_wing2", CubeListBuilder.create().texOffs(264, 252).addBox(0F, -2F, -2F, 40F, 4F, 4F).texOffs(168, 0).addBox(17.8F, 0F, 2F, 2F, 2F, 44F).texOffs(360, 0).addBox(22.6F, 0F, 2F, 2F, 2F, 40F).texOffs(88, 86).addBox(27.4F, 0F, 2F, 2F, 2F, 36F).texOffs(436, 86).addBox(32.2F, 0F, 2F, 2F, 2F, 32F).texOffs(164, 86).addBox(0F, 0.6F, 2F, 8F, 2F, 36F).texOffs(224, 158).addBox(8F, 0.6F, 2F, 8F, 2F, 28F).texOffs(0, 124).addBox(16F, 0.6F, 2F, 8F, 2F, 32F).texOffs(160, 188).addBox(24F, 0.6F, 2F, 8F, 2F, 24F).texOffs(220, 124).addBox(32F, 0.6F, 2F, 8F, 2F, 30F), PartPose.offsetAndRotation(40F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition swayS1 = body.addOrReplaceChild("swayS1", CubeListBuilder.create().texOffs(224, 188).addBox(-12F, 0F, -8F, 24F, 10F, 16F), PartPose.offsetAndRotation(0F, -12F, -2F, 0F, 0F, 0F));
		PartDefinition swayS2 = swayS1.addOrReplaceChild("swayS2", CubeListBuilder.create().texOffs(304, 188).addBox(-12F, 0F, -8F, 24F, 10F, 16F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		swayS2.addOrReplaceChild("swayS3", CubeListBuilder.create().texOffs(384, 188).addBox(-11F, 0F, -8F, 22F, 10F, 16F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition cape1 = body.addOrReplaceChild("cape1", CubeListBuilder.create().texOffs(192, 236).addBox(-12F, 0F, -1F, 24F, 10F, 2F), PartPose.offsetAndRotation(0F, -40F, 9F, 0F, 0F, 0F));
		PartDefinition cape2 = cape1.addOrReplaceChild("cape2", CubeListBuilder.create().texOffs(244, 236).addBox(-12F, 0F, -1F, 24F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition cape3 = cape2.addOrReplaceChild("cape3", CubeListBuilder.create().texOffs(296, 236).addBox(-12F, 0F, -1F, 24F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition cape4 = cape3.addOrReplaceChild("cape4", CubeListBuilder.create().texOffs(348, 236).addBox(-12F, 0F, -1F, 24F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		cape4.addOrReplaceChild("cape5", CubeListBuilder.create().texOffs(400, 236).addBox(-11F, 0F, -1F, 22F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(296, 158).addBox(-5F, 0F, -5F, 10F, 20F, 10F).texOffs(196, 214).addBox(-7F, 0F, -7F, 14F, 8F, 14F), PartPose.offsetAndRotation(-6F, -34F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(376, 158).addBox(-4F, 0F, -4F, 8F, 20F, 8F).texOffs(308, 214).addBox(-6F, 16F, -10F, 12F, 4F, 14F), PartPose.offsetAndRotation(0F, 20F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(336, 158).addBox(-5F, 0F, -5F, 10F, 20F, 10F).texOffs(252, 214).addBox(-7F, 0F, -7F, 14F, 8F, 14F), PartPose.offsetAndRotation(6F, -34F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(408, 158).addBox(-4F, 0F, -4F, 8F, 20F, 8F).texOffs(360, 214).addBox(-6F, 16F, -10F, 12F, 4F, 14F), PartPose.offsetAndRotation(0F, 20F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
