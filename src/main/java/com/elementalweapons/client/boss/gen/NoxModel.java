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
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(8, 0).addBox(-12F, -36F, -8F, 24F, 36F, 16F).texOffs(24, 96).addBox(-6F, -34F, -10F, 12F, 12F, 2F).texOffs(76, 70).addBox(-14F, -35F, -9F, 28F, 6F, 18F).texOffs(308, 112).addBox(-2F, -32F, -10.6F, 4F, 4F, 2F).texOffs(320, 112).addBox(-12F, -10F, -10F, 24F, 4F, 2F).texOffs(260, 112).addBox(-13F, -41F, -1F, 2F, 6F, 2F).texOffs(268, 112).addBox(-5F, -41F, -1F, 2F, 6F, 2F).texOffs(276, 112).addBox(3F, -41F, -1F, 2F, 6F, 2F).texOffs(284, 112).addBox(11F, -41F, -1F, 2F, 6F, 2F), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(176, 0).addBox(-8F, -16F, -10F, 16F, 16F, 16F).texOffs(88, 0).addBox(-11F, -21F, -9F, 22F, 22F, 22F).texOffs(452, 70).addBox(-7F, -23F, -1F, 14F, 4F, 14F).texOffs(200, 112).addBox(-3F, -26F, 7F, 6F, 4F, 6F).texOffs(372, 112).addBox(-2F, -20F, -12F, 4F, 4F, 2F), PartPose.offsetAndRotation(0F, -40F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(168, 70).addBox(-4F, 0F, -3F, 6F, 18F, 6F).texOffs(328, 0).addBox(-5F, 0F, -5F, 10F, 16F, 10F).texOffs(308, 70).addBox(-7F, -4F, -6F, 12F, 8F, 12F).texOffs(96, 96).addBox(-7F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(-14F, -36F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(216, 70).addBox(-2F, 0F, -2F, 4F, 18F, 4F).texOffs(0, 0).addBox(-1F, -26F, -13F, 2F, 68F, 2F).texOffs(224, 112).addBox(-3F, -26F, -15F, 6F, 4F, 6F).texOffs(368, 0).addBox(-1F, -29F, -32F, 2F, 6F, 20F).texOffs(52, 96).addBox(-1F, -25F, -34F, 2F, 6F, 8F).texOffs(248, 112).addBox(-1F, -21F, -36F, 2F, 6F, 4F).texOffs(292, 112).addBox(-2F, 40F, -14F, 4F, 4F, 4F).texOffs(128, 96).addBox(-3F, 18F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(-1F, 18F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(192, 70).addBox(-2F, 0F, -3F, 6F, 18F, 6F).texOffs(412, 0).addBox(-5F, 0F, -5F, 10F, 16F, 10F).texOffs(356, 70).addBox(-5F, -4F, -6F, 12F, 8F, 12F).texOffs(152, 96).addBox(-1F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(14F, -36F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(232, 70).addBox(-2F, 0F, -2F, 4F, 18F, 4F).texOffs(184, 96).addBox(-3F, 21F, -5F, 6F, 6F, 6F).texOffs(208, 96).addBox(-3F, 18F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(1F, 18F, 0F, 0F, 0F, 0F));
		PartDefinition swayR1 = body.addOrReplaceChild("swayR1", CubeListBuilder.create().texOffs(240, 0).addBox(-13F, 0F, -9F, 26F, 10F, 18F), PartPose.offsetAndRotation(0F, 4F, 0F, 0F, 0F, 0F));
		PartDefinition swayR2 = swayR1.addOrReplaceChild("swayR2", CubeListBuilder.create().texOffs(0, 70).addBox(-11F, 0F, -8F, 22F, 10F, 16F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition swayR3 = swayR2.addOrReplaceChild("swayR3", CubeListBuilder.create().texOffs(248, 70).addBox(-9F, 0F, -6F, 18F, 10F, 12F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		swayR3.addOrReplaceChild("swayR4", CubeListBuilder.create().texOffs(404, 70).addBox(-7F, 0F, -5F, 14F, 10F, 10F).texOffs(72, 96).addBox(-8F, 9F, -4F, 4F, 6F, 8F).texOffs(0, 96).addBox(4F, 10F, -4F, 4F, 8F, 8F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition cape1 = body.addOrReplaceChild("cape1", CubeListBuilder.create().texOffs(232, 96).addBox(-14F, 0F, -1F, 28F, 10F, 2F), PartPose.offsetAndRotation(0F, -34F, 8F, 0F, 0F, 0F));
		PartDefinition cape2 = cape1.addOrReplaceChild("cape2", CubeListBuilder.create().texOffs(292, 96).addBox(-14F, 0F, -1F, 28F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition cape3 = cape2.addOrReplaceChild("cape3", CubeListBuilder.create().texOffs(352, 96).addBox(-14F, 0F, -1F, 28F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition cape4 = cape3.addOrReplaceChild("cape4", CubeListBuilder.create().texOffs(412, 96).addBox(-13F, 0F, -1F, 26F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		cape4.addOrReplaceChild("cape5", CubeListBuilder.create().texOffs(0, 112).addBox(-13F, 0F, -1F, 26F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition ring = body.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -20F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("shade0", CubeListBuilder.create().texOffs(56, 112).addBox(-3F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(28F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("shade1", CubeListBuilder.create().texOffs(80, 112).addBox(-3F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(14F, 0F, 24.2487F, 0F, 1.0472F, 0F));
		ring.addOrReplaceChild("shade2", CubeListBuilder.create().texOffs(104, 112).addBox(-3F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(-14F, 0F, 24.2487F, 0F, 2.0944F, 0F));
		ring.addOrReplaceChild("shade3", CubeListBuilder.create().texOffs(128, 112).addBox(-3F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(-28F, 0F, 0F, 0F, 3.1416F, 0F));
		ring.addOrReplaceChild("shade4", CubeListBuilder.create().texOffs(152, 112).addBox(-3F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(-14F, 0F, -24.2487F, 0F, 4.1888F, 0F));
		ring.addOrReplaceChild("shade5", CubeListBuilder.create().texOffs(176, 112).addBox(-3F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(14F, 0F, -24.2487F, 0F, 5.236F, 0F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
