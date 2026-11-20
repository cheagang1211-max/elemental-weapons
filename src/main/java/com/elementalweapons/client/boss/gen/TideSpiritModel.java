// 자동 생성 파일 (tools/bossgen/mobs2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class TideSpiritModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "head", "swayH1"},
			{"body", "head", "swayH1", "swayH2"},
			{"body", "head", "swayH1", "swayH2", "swayH3"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"},
			{"body", "tail1", "tail2", "tail3", "tail4"}
	};

	public TideSpiritModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-8F, -12F, -5F, 16F, 20F, 10F).texOffs(12, 72).addBox(-3F, -11F, -6.6F, 6F, 6F, 2F).texOffs(0, 30).addBox(-9F, -12F, -6F, 18F, 4F, 12F), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(52, 0).addBox(-7F, -14F, -7F, 14F, 14F, 14F).texOffs(80, 46).addBox(-5F, -16F, -5F, 10F, 2F, 10F).texOffs(44, 72).addBox(-1F, -18F, -5F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
		PartDefinition swayH1 = head.addOrReplaceChild("swayH1", CubeListBuilder.create().texOffs(72, 60).addBox(-6F, 0F, -2F, 12F, 6F, 4F), PartPose.offsetAndRotation(0F, -12F, 6F, 0F, 0F, 0F));
		PartDefinition swayH2 = swayH1.addOrReplaceChild("swayH2", CubeListBuilder.create().texOffs(104, 60).addBox(-4F, 0F, -2F, 8F, 6F, 4F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		swayH2.addOrReplaceChild("swayH3", CubeListBuilder.create().texOffs(28, 72).addBox(-3F, 0F, -1F, 6F, 6F, 2F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(104, 30).addBox(-3F, 0F, -2F, 4F, 10F, 4F), PartPose.offsetAndRotation(-10F, -8F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(0, 46).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(0, 60).addBox(-3F, 10F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(-1F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(16, 46).addBox(-1F, 0F, -2F, 4F, 10F, 4F), PartPose.offsetAndRotation(10F, -8F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(32, 46).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(24, 60).addBox(-3F, 10F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(1F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(60, 30).addBox(-7F, 0F, -4F, 14F, 8F, 8F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(48, 46).addBox(-5F, 0F, -3F, 10F, 8F, 6F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(48, 60).addBox(-4F, 0F, -2F, 8F, 8F, 4F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(0, 72).addBox(-2F, 0F, -1F, 4F, 8F, 2F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
