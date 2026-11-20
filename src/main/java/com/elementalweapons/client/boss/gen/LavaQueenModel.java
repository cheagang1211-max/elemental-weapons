// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class LavaQueenModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"body", "tail1"},
			{"body", "leg1"},
			{"body", "leg1", "leg1b"},
			{"body", "leg2"},
			{"body", "leg2", "leg2b"},
			{"body", "leg3"},
			{"body", "leg3", "leg3b"},
			{"body", "leg4"},
			{"body", "leg4", "leg4b"},
			{"body", "leg5"},
			{"body", "leg5", "leg5b"},
			{"body", "leg6"},
			{"body", "leg6", "leg6b"},
			{"body", "leg7"},
			{"body", "leg7", "leg7b"},
			{"body", "leg8"},
			{"body", "leg8", "leg8b"}
	};

	public LavaQueenModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(136, 0).addBox(-12F, -8F, -14F, 24F, 16F, 28F).texOffs(240, 0).addBox(-13F, -2F, -15F, 26F, 8F, 30F).texOffs(352, 0).addBox(-9F, -24F, -26F, 18F, 20F, 12F).texOffs(160, 62).addBox(-10F, -21F, -27F, 20F, 6F, 14F).texOffs(228, 62).addBox(-10F, -5F, -27F, 20F, 6F, 14F).texOffs(408, 112).addBox(-5F, 3F, -31F, 2F, 6F, 2F).texOffs(416, 112).addBox(3F, 3F, -31F, 2F, 6F, 2F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(104, 62).addBox(-7F, -13F, -7F, 14F, 14F, 14F).texOffs(296, 62).addBox(-8F, -16F, -8F, 16F, 4F, 16F).texOffs(424, 112).addBox(-7F, -22F, -7F, 2F, 6F, 2F).texOffs(432, 112).addBox(-3F, -22F, -7F, 2F, 6F, 2F).texOffs(440, 112).addBox(1F, -22F, -7F, 2F, 6F, 2F).texOffs(448, 112).addBox(5F, -22F, -7F, 2F, 6F, 2F).texOffs(160, 132).addBox(-1F, -24F, -7F, 2F, 4F, 2F).texOffs(360, 62).addBox(-6F, -12F, 7F, 12F, 16F, 4F), PartPose.offsetAndRotation(0F, -28F, -20F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(488, 62).addBox(-4F, 0F, -3F, 6F, 12F, 6F).texOffs(392, 62).addBox(-7F, -4F, -6F, 12F, 8F, 12F).texOffs(84, 94).addBox(-7F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(-10F, -20F, -20F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(0, 94).addBox(-2F, 0F, -2F, 4F, 14F, 4F).texOffs(360, 112).addBox(-1F, 12F, -3F, 2F, 8F, 2F).texOffs(116, 94).addBox(-3F, 14F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(-1F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(16, 94).addBox(-2F, 0F, -3F, 6F, 12F, 6F).texOffs(440, 62).addBox(-5F, -4F, -6F, 12F, 8F, 12F).texOffs(140, 94).addBox(-1F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(10F, -20F, -20F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(40, 94).addBox(-2F, 0F, -2F, 4F, 14F, 4F).texOffs(368, 112).addBox(-1F, 12F, -3F, 2F, 8F, 2F).texOffs(172, 94).addBox(-3F, 14F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(1F, 12F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 0).addBox(-16F, -19F, -2F, 32F, 26F, 36F).texOffs(0, 62).addBox(-12F, -22F, 2F, 24F, 4F, 28F).texOffs(56, 94).addBox(-6F, -12F, 34F, 12F, 12F, 2F).texOffs(456, 112).addBox(-2F, -26F, 2F, 4F, 4F, 4F).texOffs(472, 112).addBox(-2F, -26.6667F, 10F, 4F, 4F, 4F).texOffs(376, 112).addBox(-2F, -27.3333F, 18F, 4F, 6F, 4F).texOffs(392, 112).addBox(-2F, -28F, 26F, 4F, 6F, 4F), PartPose.offsetAndRotation(0F, -2F, 12F, -0.2618F, 0F, 0F));
		PartDefinition leg1 = body.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(196, 94).addBox(-24F, -3F, -3F, 24F, 6F, 6F).texOffs(256, 94).addBox(-25F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(-8F, 2F, -12F, 0F, 0.6981F, 0.5236F));
		leg1.addOrReplaceChild("leg1b", CubeListBuilder.create().texOffs(0, 124).addBox(-28F, -2F, -2F, 28F, 4F, 4F).texOffs(64, 124).addBox(-30F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-24F, 0F, 0F, 0F, 0F, -1.2217F));
		PartDefinition leg2 = body.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(280, 94).addBox(0F, -3F, -3F, 24F, 6F, 6F).texOffs(340, 94).addBox(19F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(8F, 2F, -12F, 0F, -0.6981F, -0.5236F));
		leg2.addOrReplaceChild("leg2b", CubeListBuilder.create().texOffs(80, 124).addBox(0F, -2F, -2F, 28F, 4F, 4F).texOffs(144, 124).addBox(26F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(24F, 0F, 0F, 0F, 0F, 1.2217F));
		PartDefinition leg3 = body.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(364, 94).addBox(-24F, -3F, -3F, 24F, 6F, 6F).texOffs(424, 94).addBox(-25F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(-8F, 2F, -4F, 0F, 0.2269F, 0.5236F));
		leg3.addOrReplaceChild("leg3b", CubeListBuilder.create().texOffs(160, 124).addBox(-28F, -2F, -2F, 28F, 4F, 4F).texOffs(224, 124).addBox(-30F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-24F, 0F, 0F, 0F, 0F, -1.2217F));
		PartDefinition leg4 = body.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(448, 94).addBox(0F, -3F, -3F, 24F, 6F, 6F).texOffs(0, 112).addBox(19F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(8F, 2F, -4F, 0F, -0.2269F, -0.5236F));
		leg4.addOrReplaceChild("leg4b", CubeListBuilder.create().texOffs(240, 124).addBox(0F, -2F, -2F, 28F, 4F, 4F).texOffs(304, 124).addBox(26F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(24F, 0F, 0F, 0F, 0F, 1.2217F));
		PartDefinition leg5 = body.addOrReplaceChild("leg5", CubeListBuilder.create().texOffs(24, 112).addBox(-24F, -3F, -3F, 24F, 6F, 6F).texOffs(84, 112).addBox(-25F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(-8F, 2F, 4F, 0F, -0.2443F, 0.5236F));
		leg5.addOrReplaceChild("leg5b", CubeListBuilder.create().texOffs(320, 124).addBox(-28F, -2F, -2F, 28F, 4F, 4F).texOffs(384, 124).addBox(-30F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-24F, 0F, 0F, 0F, 0F, -1.2217F));
		PartDefinition leg6 = body.addOrReplaceChild("leg6", CubeListBuilder.create().texOffs(108, 112).addBox(0F, -3F, -3F, 24F, 6F, 6F).texOffs(168, 112).addBox(19F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(8F, 2F, 4F, 0F, 0.2443F, -0.5236F));
		leg6.addOrReplaceChild("leg6b", CubeListBuilder.create().texOffs(400, 124).addBox(0F, -2F, -2F, 28F, 4F, 4F).texOffs(464, 124).addBox(26F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(24F, 0F, 0F, 0F, 0F, 1.2217F));
		PartDefinition leg7 = body.addOrReplaceChild("leg7", CubeListBuilder.create().texOffs(192, 112).addBox(-24F, -3F, -3F, 24F, 6F, 6F).texOffs(252, 112).addBox(-25F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(-8F, 2F, 12F, 0F, -0.7156F, 0.5236F));
		leg7.addOrReplaceChild("leg7b", CubeListBuilder.create().texOffs(0, 132).addBox(-28F, -2F, -2F, 28F, 4F, 4F).texOffs(64, 132).addBox(-30F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-24F, 0F, 0F, 0F, 0F, -1.2217F));
		PartDefinition leg8 = body.addOrReplaceChild("leg8", CubeListBuilder.create().texOffs(276, 112).addBox(0F, -3F, -3F, 24F, 6F, 6F).texOffs(336, 112).addBox(19F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(8F, 2F, 12F, 0F, 0.7156F, -0.5236F));
		leg8.addOrReplaceChild("leg8b", CubeListBuilder.create().texOffs(80, 132).addBox(0F, -2F, -2F, 28F, 4F, 4F).texOffs(144, 132).addBox(26F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(24F, 0F, 0F, 0F, 0F, 1.2217F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
