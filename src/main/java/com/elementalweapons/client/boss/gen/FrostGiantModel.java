// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class FrostGiantModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "head", "swayB1"},
			{"body", "head", "swayB1", "swayB2"},
			{"body", "head", "swayB1", "swayB2", "swayB3"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
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

	public FrostGiantModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8F, -26F, -5F, 16F, 22F, 10F).texOffs(108, 0).addBox(-10F, -25.5F, -6F, 20F, 7F, 12F).texOffs(192, 32).addBox(-7F, -16F, -5.7F, 14F, 12F, 1F).texOffs(136, 32).addBox(-8.5F, -6F, -5.5F, 17F, 4F, 11F).texOffs(54, 82).addBox(-2F, -5.5F, -6.3F, 4F, 3F, 1F).texOffs(12, 82).addBox(-9F, -28.5F, -1F, 2F, 3F, 2F).texOffs(20, 82).addBox(-3.6667F, -28.5F, -1F, 2F, 3F, 2F).texOffs(28, 82).addBox(1.6667F, -28.5F, -1F, 2F, 3F, 2F).texOffs(36, 82).addBox(7F, -28.5F, -1F, 2F, 3F, 2F), PartPose.offsetAndRotation(0F, -4F, 0F, 0F, 0F, 0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(24, 32).addBox(-4.5F, -8.5F, -4.5F, 9F, 9F, 9F).texOffs(64, 82).addBox(-4.5F, -2.5F, -5F, 9F, 3F, 1F).texOffs(84, 82).addBox(-3.5F, -12.5F, -0.5F, 1F, 3F, 1F).texOffs(194, 75).addBox(-1.5F, -13.5F, -0.5F, 1F, 5F, 1F).texOffs(198, 75).addBox(0.5F, -13.5F, -0.5F, 1F, 5F, 1F).texOffs(88, 82).addBox(2.5F, -12.5F, -0.5F, 1F, 3F, 1F).texOffs(34, 64).addBox(-4.5F, -9.5F, -4.5F, 9F, 1F, 9F), PartPose.offsetAndRotation(0F, -32F, 0F, 0F, 0F, 0F));
		PartDefinition swayB1 = head.addOrReplaceChild("swayB1", CubeListBuilder.create().texOffs(202, 75).addBox(-4F, 0F, -1F, 8F, 4F, 2F), PartPose.offsetAndRotation(0F, 0F, -4F, 0F, 0F, 0F));
		PartDefinition swayB2 = swayB1.addOrReplaceChild("swayB2", CubeListBuilder.create().texOffs(222, 75).addBox(-3F, 0F, -1F, 6F, 4F, 2F), PartPose.offsetAndRotation(0F, 4F, 0F, 0F, 0F, 0F));
		swayB2.addOrReplaceChild("swayB3", CubeListBuilder.create().texOffs(44, 82).addBox(-2F, 0F, -0.5F, 4F, 4F, 1F), PartPose.offsetAndRotation(0F, 4F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(172, 0).addBox(-3.5F, 0F, -3F, 6F, 13F, 6F).texOffs(0, 51).addBox(-5F, -2F, -4.5F, 9F, 4F, 9F).texOffs(126, 64).addBox(-5F, -3.5F, -3.5F, 7F, 2F, 7F), PartPose.offsetAndRotation(-10F, -30F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(60, 32).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(36, 51).addBox(-1.5F, 7F, -3.5F, 3F, 10F, 3F).texOffs(108, 32).addBox(-3.5F, 16F, -5.5F, 7F, 10F, 7F).texOffs(238, 75).addBox(-4.5F, 18F, -3F, 2F, 4F, 2F).texOffs(246, 75).addBox(2.5F, 21F, -3F, 2F, 4F, 2F).texOffs(0, 82).addBox(-1.5F, 25F, -3.5F, 3F, 3F, 3F).texOffs(156, 51).addBox(-3.5F, 1.5F, -3.5F, 7F, 5F, 7F).texOffs(70, 64).addBox(-3.5F, 12F, -3.5F, 7F, 3F, 7F), PartPose.offsetAndRotation(-0.5F, 13F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(196, 0).addBox(-2.5F, 0F, -3F, 6F, 13F, 6F).texOffs(48, 51).addBox(-4F, -2F, -4.5F, 9F, 4F, 9F).texOffs(154, 64).addBox(-2F, -3.5F, -3.5F, 7F, 2F, 7F), PartPose.offsetAndRotation(10F, -30F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(84, 32).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(184, 51).addBox(-3.5F, 1.5F, -3.5F, 7F, 5F, 7F).texOffs(98, 64).addBox(-3.5F, 12F, -3.5F, 7F, 3F, 7F), PartPose.offsetAndRotation(0.5F, 13F, 0F, 0F, 0F, 0F));
		PartDefinition cape1 = body.addOrReplaceChild("cape1", CubeListBuilder.create().texOffs(182, 64).addBox(-9F, 0F, -1F, 18F, 5F, 2F), PartPose.offsetAndRotation(0F, -29F, 6F, 0F, 0F, 0F));
		PartDefinition cape2 = cape1.addOrReplaceChild("cape2", CubeListBuilder.create().texOffs(0, 75).addBox(-9F, 0F, -1F, 18F, 5F, 2F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition cape3 = cape2.addOrReplaceChild("cape3", CubeListBuilder.create().texOffs(40, 75).addBox(-9F, 0F, -1F, 18F, 5F, 2F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition cape4 = cape3.addOrReplaceChild("cape4", CubeListBuilder.create().texOffs(80, 75).addBox(-8.5F, 0F, -1F, 17F, 5F, 2F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition cape5 = cape4.addOrReplaceChild("cape5", CubeListBuilder.create().texOffs(118, 75).addBox(-8.5F, 0F, -1F, 17F, 5F, 2F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		cape5.addOrReplaceChild("cape6", CubeListBuilder.create().texOffs(156, 75).addBox(-8.5F, 0F, -1F, 17F, 5F, 2F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(52, 0).addBox(-3.5F, 0F, -3.5F, 7F, 13F, 7F).texOffs(84, 51).addBox(-4.5F, 0F, -4.5F, 9F, 4F, 9F), PartPose.offsetAndRotation(-4F, -2F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(220, 0).addBox(-3F, 0F, -3F, 6F, 13F, 6F).texOffs(212, 51).addBox(-4F, 11F, -6F, 8F, 2F, 9F), PartPose.offsetAndRotation(0F, 13F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(80, 0).addBox(-3.5F, 0F, -3.5F, 7F, 13F, 7F).texOffs(120, 51).addBox(-4.5F, 0F, -4.5F, 9F, 4F, 9F), PartPose.offsetAndRotation(4F, -2F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(0, 32).addBox(-3F, 0F, -3F, 6F, 13F, 6F).texOffs(0, 64).addBox(-4F, 11F, -6F, 8F, 2F, 9F), PartPose.offsetAndRotation(0F, 13F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
