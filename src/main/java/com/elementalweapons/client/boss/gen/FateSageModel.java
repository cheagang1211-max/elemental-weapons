// 자동 생성 파일 (tools/bossgen/mobs2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class FateSageModel extends GenericBossModel {
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
			{"body", "ring"},
			{"body", "ring", "sr0"},
			{"body", "ring", "sr1"},
			{"body", "ring", "sr2"},
			{"body", "ring", "sr3"},
			{"body", "ring", "sr4"},
			{"body", "ring", "sr5"},
			{"body", "ring", "sr6"},
			{"body", "ring", "sr7"}
	};

	public FateSageModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(172, 0).addBox(-8F, 0F, -6F, 16F, 24F, 12F).texOffs(20, 0).addBox(-10F, 22F, -8F, 20F, 26F, 16F).texOffs(8, 0).addBox(-2F, 2F, -8.2F, 4F, 44F, 2F).texOffs(160, 54).addBox(-9F, 20F, -7F, 18F, 4F, 14F).texOffs(152, 106).addBox(-2F, 20F, -9F, 4F, 4F, 2F).texOffs(120, 88).addBox(8.2F, 24F, -5F, 2F, 8F, 6F), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 54).addBox(-8F, -18F, -9F, 16F, 18F, 16F).texOffs(92, 0).addBox(-10F, -21F, -8F, 20F, 22F, 20F).texOffs(72, 88).addBox(-6F, -23F, 0F, 12F, 4F, 12F).texOffs(184, 106).addBox(-8F, -2F, -10.2F, 16F, 2F, 2F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition swayB1 = head.addOrReplaceChild("swayB1", CubeListBuilder.create().texOffs(80, 106).addBox(-6F, 0F, -2F, 12F, 6F, 4F), PartPose.offsetAndRotation(0F, -2F, -9F, 0F, 0F, 0F));
		PartDefinition swayB2 = swayB1.addOrReplaceChild("swayB2", CubeListBuilder.create().texOffs(112, 106).addBox(-4F, 0F, -2F, 8F, 6F, 4F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		swayB2.addOrReplaceChild("swayB3", CubeListBuilder.create().texOffs(136, 106).addBox(-3F, 0F, -1F, 6F, 6F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(224, 54).addBox(-4F, 0F, -3F, 6F, 12F, 6F).texOffs(64, 54).addBox(-7F, -4F, -6F, 12F, 8F, 12F).texOffs(224, 88).addBox(-7F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(-10F, 4F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(0, 88).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(0, 0).addBox(-1F, -18F, -5F, 2F, 52F, 2F).texOffs(0, 106).addBox(-3F, -23F, -7F, 6F, 6F, 6F).texOffs(24, 106).addBox(-3F, -29F, -7F, 6F, 6F, 6F).texOffs(164, 106).addBox(-1F, -33F, -5F, 2F, 4F, 2F).texOffs(136, 88).addBox(-4F, 12F, -4F, 8F, 6F, 8F), PartPose.offsetAndRotation(-1F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(24, 88).addBox(-2F, 0F, -3F, 6F, 12F, 6F).texOffs(112, 54).addBox(-5F, -4F, -6F, 12F, 8F, 12F).texOffs(48, 106).addBox(-1F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(10F, 4F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(48, 88).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(168, 88).addBox(-4F, 19F, -6F, 8F, 10F, 4F).texOffs(172, 106).addBox(-2F, 22F, -7.4F, 4F, 4F, 2F).texOffs(192, 88).addBox(-4F, 12F, -4F, 8F, 6F, 8F), PartPose.offsetAndRotation(1F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition ring = body.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -8F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("sr0", CubeListBuilder.create().texOffs(220, 106).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(18F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("sr1", CubeListBuilder.create().texOffs(228, 106).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(12.7279F, 0F, 12.7279F, 0F, 0.7854F, 0F));
		ring.addOrReplaceChild("sr2", CubeListBuilder.create().texOffs(236, 106).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, 0F, 18F, 0F, 1.5708F, 0F));
		ring.addOrReplaceChild("sr3", CubeListBuilder.create().texOffs(244, 106).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-12.7279F, 0F, 12.7279F, 0F, 2.3562F, 0F));
		ring.addOrReplaceChild("sr4", CubeListBuilder.create().texOffs(0, 118).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-18F, 0F, 0F, 0F, 3.1416F, 0F));
		ring.addOrReplaceChild("sr5", CubeListBuilder.create().texOffs(8, 118).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-12.7279F, 0F, -12.7279F, 0F, 3.927F, 0F));
		ring.addOrReplaceChild("sr6", CubeListBuilder.create().texOffs(16, 118).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, 0F, -18F, 0F, 4.7124F, 0F));
		ring.addOrReplaceChild("sr7", CubeListBuilder.create().texOffs(24, 118).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(12.7279F, 0F, -12.7279F, 0F, 5.4978F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
