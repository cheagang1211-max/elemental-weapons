// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class NoxModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"body", "swayR1"},
			{"body", "swayR1", "swayR2"},
			{"body", "swayR1", "swayR2", "swayR3"},
			{"body", "swayR1", "swayR2", "swayR3", "swayR4"},
			{"body", "cape1"},
			{"body", "cape1", "cape2"},
			{"body", "cape1", "cape2", "cape3"},
			{"body", "cape1", "cape2", "cape3", "cape4"},
			{"body", "cape1", "cape2", "cape3", "cape4", "cape5"},
			{"body", "ring"},
			{"body", "ring", "shade0"},
			{"body", "ring", "shade1"},
			{"body", "ring", "shade2"},
			{"body", "ring", "shade3"},
			{"body", "ring", "shade4"},
			{"body", "ring", "shade5"}
	};

	public NoxModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(4, 0).addBox(-6F, -18F, -4F, 12F, 18F, 8F).texOffs(12, 48).addBox(-3F, -17F, -5F, 6F, 6F, 1F).texOffs(38, 35).addBox(-7F, -17.5F, -4.5F, 14F, 3F, 9F).texOffs(154, 56).addBox(-1F, -16F, -5.3F, 2F, 2F, 1F).texOffs(160, 56).addBox(-6F, -5F, -5F, 12F, 2F, 1F).texOffs(130, 56).addBox(-6.5F, -20.5F, -0.5F, 1F, 3F, 1F).texOffs(134, 56).addBox(-2.5F, -20.5F, -0.5F, 1F, 3F, 1F).texOffs(138, 56).addBox(1.5F, -20.5F, -0.5F, 1F, 3F, 1F).texOffs(142, 56).addBox(5.5F, -20.5F, -0.5F, 1F, 3F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(88, 0).addBox(-4F, -8F, -5F, 8F, 8F, 8F).texOffs(44, 0).addBox(-5.5F, -10.5F, -4.5F, 11F, 11F, 11F).texOffs(226, 35).addBox(-3.5F, -11.5F, -0.5F, 7F, 2F, 7F).texOffs(100, 56).addBox(-1.5F, -13F, 3.5F, 3F, 2F, 3F).texOffs(186, 56).addBox(-1F, -10F, -6F, 2F, 2F, 1F), PartPose.offsetAndRotation(0F, -20F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(84, 35).addBox(-2F, 0F, -1.5F, 3F, 9F, 3F).texOffs(164, 0).addBox(-2.5F, 0F, -2.5F, 5F, 8F, 5F).texOffs(154, 35).addBox(-3.5F, -2F, -3F, 6F, 4F, 6F).texOffs(48, 48).addBox(-3.5F, -3.5F, -2F, 4F, 2F, 4F), PartPose.offsetAndRotation(-7F, -18F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(108, 35).addBox(-1F, 0F, -1F, 2F, 9F, 2F).texOffs(0, 0).addBox(-0.5F, -13F, -6.5F, 1F, 34F, 1F).texOffs(112, 56).addBox(-1.5F, -13F, -7.5F, 3F, 2F, 3F).texOffs(184, 0).addBox(-0.5F, -14.5F, -16F, 1F, 3F, 10F).texOffs(26, 48).addBox(-0.5F, -12.5F, -17F, 1F, 3F, 4F).texOffs(124, 56).addBox(-0.5F, -10.5F, -18F, 1F, 3F, 2F).texOffs(146, 56).addBox(-1F, 20F, -7F, 2F, 2F, 2F).texOffs(64, 48).addBox(-1.5F, 9F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(-0.5F, 9F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(96, 35).addBox(-1F, 0F, -1.5F, 3F, 9F, 3F).texOffs(206, 0).addBox(-2.5F, 0F, -2.5F, 5F, 8F, 5F).texOffs(178, 35).addBox(-2.5F, -2F, -3F, 6F, 4F, 6F).texOffs(76, 48).addBox(-0.5F, -3.5F, -2F, 4F, 2F, 4F), PartPose.offsetAndRotation(7F, -18F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(116, 35).addBox(-1F, 0F, -1F, 2F, 9F, 2F).texOffs(92, 48).addBox(-1.5F, 10.5F, -2.5F, 3F, 3F, 3F).texOffs(104, 48).addBox(-1.5F, 9F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(0.5F, 9F, 0F, 0F, 0F, 0F));
		PartDefinition swayR1 = body.addOrReplaceChild("swayR1", CubeListBuilder.create().texOffs(120, 0).addBox(-6.5F, 0F, -4.5F, 13F, 5F, 9F), PartPose.offsetAndRotation(0F, 2F, 0F, 0F, 0F, 0F));
		PartDefinition swayR2 = swayR1.addOrReplaceChild("swayR2", CubeListBuilder.create().texOffs(0, 35).addBox(-5.5F, 0F, -4F, 11F, 5F, 8F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition swayR3 = swayR2.addOrReplaceChild("swayR3", CubeListBuilder.create().texOffs(124, 35).addBox(-4.5F, 0F, -3F, 9F, 5F, 6F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		swayR3.addOrReplaceChild("swayR4", CubeListBuilder.create().texOffs(202, 35).addBox(-3.5F, 0F, -2.5F, 7F, 5F, 5F).texOffs(36, 48).addBox(-4F, 4.5F, -2F, 2F, 3F, 4F).texOffs(0, 48).addBox(2F, 5F, -2F, 2F, 4F, 4F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition cape1 = body.addOrReplaceChild("cape1", CubeListBuilder.create().texOffs(116, 48).addBox(-7F, 0F, -0.5F, 14F, 5F, 1F), PartPose.offsetAndRotation(0F, -17F, 4F, 0F, 0F, 0F));
		PartDefinition cape2 = cape1.addOrReplaceChild("cape2", CubeListBuilder.create().texOffs(146, 48).addBox(-7F, 0F, -0.5F, 14F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition cape3 = cape2.addOrReplaceChild("cape3", CubeListBuilder.create().texOffs(176, 48).addBox(-7F, 0F, -0.5F, 14F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition cape4 = cape3.addOrReplaceChild("cape4", CubeListBuilder.create().texOffs(206, 48).addBox(-6.5F, 0F, -0.5F, 13F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		cape4.addOrReplaceChild("cape5", CubeListBuilder.create().texOffs(0, 56).addBox(-6.5F, 0F, -0.5F, 13F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition ring = body.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -10F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("shade0", CubeListBuilder.create().texOffs(28, 56).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(14F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("shade1", CubeListBuilder.create().texOffs(40, 56).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(7F, 0F, 12.1244F, 0F, 1.0472F, 0F));
		ring.addOrReplaceChild("shade2", CubeListBuilder.create().texOffs(52, 56).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(-7F, 0F, 12.1244F, 0F, 2.0944F, 0F));
		ring.addOrReplaceChild("shade3", CubeListBuilder.create().texOffs(64, 56).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(-14F, 0F, 0F, 0F, 3.1416F, 0F));
		ring.addOrReplaceChild("shade4", CubeListBuilder.create().texOffs(76, 56).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(-7F, 0F, -12.1244F, 0F, 4.1888F, 0F));
		ring.addOrReplaceChild("shade5", CubeListBuilder.create().texOffs(88, 56).addBox(-1.5F, -1.5F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(7F, 0F, -12.1244F, 0F, 5.236F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
