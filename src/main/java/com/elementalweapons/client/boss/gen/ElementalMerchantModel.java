// 자동 생성 파일 (tools/bossgen/mobs2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class ElementalMerchantModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"}
	};

	public ElementalMerchantModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(76, 0).addBox(-8F, 0F, -6F, 16F, 24F, 12F).texOffs(12, 0).addBox(-9F, 22F, -7F, 18F, 26F, 14F).texOffs(0, 0).addBox(-2F, 2F, -8.2F, 4F, 44F, 2F).texOffs(0, 68).addBox(-9F, 20F, -7F, 18F, 4F, 14F).texOffs(196, 0).addBox(-9F, 2F, 5F, 18F, 20F, 10F).texOffs(160, 68).addBox(-10F, 0F, 4F, 20F, 4F, 12F).texOffs(80, 46).addBox(-11F, 10F, 6F, 2F, 12F, 8F).texOffs(160, 86).addBox(-6F, 22F, 9F, 12F, 4F, 6F).texOffs(44, 100).addBox(4F, 6F, 15F, 4F, 4F, 2F), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(132, 0).addBox(-8F, -18F, -8F, 16F, 18F, 16F).texOffs(196, 86).addBox(-6F, -6F, -9.6F, 12F, 8F, 2F).texOffs(0, 100).addBox(-4F, -3F, -10.6F, 8F, 6F, 2F).texOffs(0, 46).addBox(-10F, -19F, -10F, 20F, 2F, 20F).texOffs(100, 46).addBox(-7F, -25F, -7F, 14F, 6F, 14F).texOffs(64, 86).addBox(-4F, -29F, -2F, 8F, 4F, 8F).texOffs(20, 100).addBox(-2F, -32F, 2F, 4F, 4F, 4F).texOffs(56, 100).addBox(-2F, -21F, -11F, 4F, 4F, 2F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(64, 68).addBox(-4F, 0F, -3F, 6F, 12F, 6F).texOffs(156, 46).addBox(-7F, -4F, -6F, 12F, 8F, 12F).texOffs(96, 86).addBox(-7F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(-10F, 4F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(88, 68).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(36, 100).addBox(-1F, 21F, -5F, 2F, 6F, 2F).texOffs(224, 68).addBox(-3F, 26F, -7F, 6F, 8F, 6F).texOffs(224, 86).addBox(-4F, 25F, -8F, 8F, 2F, 8F).texOffs(0, 86).addBox(-4F, 12F, -4F, 8F, 6F, 8F), PartPose.offsetAndRotation(-1F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(112, 68).addBox(-2F, 0F, -3F, 6F, 12F, 6F).texOffs(204, 46).addBox(-5F, -4F, -6F, 12F, 8F, 12F).texOffs(128, 86).addBox(-1F, -7F, -4F, 8F, 4F, 8F), PartPose.offsetAndRotation(10F, 4F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(136, 68).addBox(-3F, 0F, -3F, 6F, 12F, 6F).texOffs(32, 86).addBox(-4F, 12F, -4F, 8F, 6F, 8F), PartPose.offsetAndRotation(1F, 12F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
