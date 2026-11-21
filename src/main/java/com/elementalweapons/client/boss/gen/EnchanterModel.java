// 자동 생성 파일 (tools/bossgen/mobs2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class EnchanterModel extends GenericBossModel {
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
			{"body", "bob_book"},
			{"body", "ring"},
			{"body", "ring", "er0"},
			{"body", "ring", "er1"},
			{"body", "ring", "er2"},
			{"body", "ring", "er3"},
			{"body", "ring", "er4"},
			{"body", "ring", "er5"}
	};

	public EnchanterModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(88, 0).addBox(-8F, 0F, -6F, 16F, 24F, 12F).texOffs(12, 0).addBox(-11F, 22F, -8F, 22F, 26F, 16F).texOffs(0, 0).addBox(-2F, 2F, -8.2F, 4F, 44F, 2F).texOffs(88, 106).addBox(-7F, 33F, -9.4F, 2F, 2F, 2F).texOffs(96, 106).addBox(5F, 39F, -9.4F, 2F, 2F, 2F).texOffs(104, 106).addBox(-5F, 43F, -9.4F, 2F, 2F, 2F).texOffs(0, 72).addBox(-9F, 20F, -7F, 18F, 4F, 14F), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(144, 0).addBox(-8F, -18F, -8F, 16F, 18F, 16F).texOffs(0, 46).addBox(-12F, -19F, -12F, 24F, 2F, 24F).texOffs(96, 46).addBox(-7F, -25F, -6F, 14F, 6F, 14F).texOffs(216, 72).addBox(-5F, -31F, -2F, 10F, 6F, 10F).texOffs(216, 90).addBox(-3F, -35F, 2F, 6F, 4F, 6F).texOffs(80, 106).addBox(-1F, -38F, 6F, 2F, 4F, 2F).texOffs(112, 106).addBox(-5F, -25F, -7.4F, 2F, 2F, 2F).texOffs(120, 106).addBox(3F, -22F, -8.2F, 2F, 2F, 2F).texOffs(128, 106).addBox(-7F, -20.2F, -11F, 14F, 2F, 2F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition swayB1 = head.addOrReplaceChild("swayB1", CubeListBuilder.create().texOffs(0, 106).addBox(-5F, 0F, -2F, 10F, 6F, 4F), PartPose.offsetAndRotation(0F, -2F, -9F, 0F, 0F, 0F));
		PartDefinition swayB2 = swayB1.addOrReplaceChild("swayB2", CubeListBuilder.create().texOffs(28, 106).addBox(-4F, 0F, -2F, 8F, 6F, 4F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		swayB2.addOrReplaceChild("swayB3", CubeListBuilder.create().texOffs(52, 106).addBox(-2F, 0F, -1F, 4F, 6F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(64, 72).addBox(-4F, 0F, -3F, 6F, 12F, 6F).texOffs(152, 46).addBox(-7F, -4F, -6F, 12F, 8F, 12F).texOffs(152, 90).addBox(-7F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(-10F, 4F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(88, 72).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(80, 90).addBox(-1F, 18F, -5F, 2F, 12F, 2F).texOffs(64, 106).addBox(-2F, 16F, -6F, 4F, 4F, 4F).texOffs(88, 90).addBox(-4F, 12F, -4F, 8F, 6F, 8F), PartPose.offsetAndRotation(-1F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(112, 72).addBox(-2F, 0F, -3F, 6F, 12F, 6F).texOffs(200, 46).addBox(-5F, -4F, -6F, 12F, 8F, 12F).texOffs(184, 90).addBox(-1F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(10F, 4F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(136, 72).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(120, 90).addBox(-4F, 12F, -4F, 8F, 6F, 8F), PartPose.offsetAndRotation(1F, 12F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("bob_book", CubeListBuilder.create().texOffs(160, 72).addBox(-6F, -1F, -8F, 12F, 2F, 16F).texOffs(0, 90).addBox(-5F, -2.6F, -7F, 10F, 2F, 14F).texOffs(48, 90).addBox(-1F, -3.8F, -7F, 2F, 2F, 14F), PartPose.offsetAndRotation(16F, 8F, -16F, -0.3491F, -0.5236F, 0F));
		PartDefinition ring = body.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -4F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("er0", CubeListBuilder.create().texOffs(160, 106).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(18F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("er1", CubeListBuilder.create().texOffs(168, 106).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(9F, 0F, 15.5885F, 0F, 1.0472F, 0F));
		ring.addOrReplaceChild("er2", CubeListBuilder.create().texOffs(176, 106).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-9F, 0F, 15.5885F, 0F, 2.0944F, 0F));
		ring.addOrReplaceChild("er3", CubeListBuilder.create().texOffs(184, 106).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-18F, 0F, 0F, 0F, 3.1416F, 0F));
		ring.addOrReplaceChild("er4", CubeListBuilder.create().texOffs(192, 106).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-9F, 0F, -15.5885F, 0F, 4.1888F, 0F));
		ring.addOrReplaceChild("er5", CubeListBuilder.create().texOffs(200, 106).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(9F, 0F, -15.5885F, 0F, 5.236F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
