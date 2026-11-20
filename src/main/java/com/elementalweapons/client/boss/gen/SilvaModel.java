// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class SilvaModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"}
	};

	public SilvaModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7F, -26F, -5F, 14F, 24F, 10F).texOffs(16, 78).addBox(-2.5F, -18.5F, -6F, 5F, 5F, 1F).texOffs(28, 34).addBox(-8F, -26.5F, -5.5F, 16F, 5F, 11F).texOffs(0, 51).addBox(-7.5F, -5F, -5.5F, 15F, 4F, 11F).texOffs(28, 78).addBox(-8.5F, -11.5F, -3.5F, 2F, 3F, 3F).texOffs(142, 78).addBox(4.5F, -7F, -6F, 3F, 2F, 1F).texOffs(38, 78).addBox(-5F, -29.5F, -1F, 2F, 3F, 2F).texOffs(46, 78).addBox(-1F, -29.5F, -1F, 2F, 3F, 2F).texOffs(54, 78).addBox(3F, -29.5F, -1F, 2F, 3F, 2F), PartPose.offsetAndRotation(0F, 4F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(172, 0).addBox(-6F, -6.5F, -5F, 12F, 7F, 10F).texOffs(48, 0).addBox(-11F, -15.5F, -5F, 22F, 9F, 16F).texOffs(82, 34).addBox(-13F, -19F, -1F, 10F, 6F, 10F).texOffs(124, 0).addBox(1F, -20.5F, -4F, 12F, 7F, 12F).texOffs(52, 51).addBox(-5F, -22.5F, -1F, 10F, 5F, 10F).texOffs(132, 51).addBox(-11.5F, -10F, 0F, 5F, 6F, 8F).texOffs(158, 51).addBox(6.5F, -10F, 0F, 5F, 6F, 8F).texOffs(62, 78).addBox(-10F, -13F, -7F, 2F, 2F, 2F).texOffs(70, 78).addBox(5F, -20F, -6F, 2F, 2F, 2F).texOffs(78, 78).addBox(9F, -11F, -9F, 2F, 2F, 2F).texOffs(86, 78).addBox(-5F, -21F, 1F, 2F, 2F, 2F).texOffs(94, 78).addBox(-1F, -10F, -10.5F, 2F, 2F, 2F).texOffs(102, 78).addBox(-12F, -7F, 3F, 2F, 2F, 2F).texOffs(110, 78).addBox(-10F, -2F, -1F, 4F, 2F, 2F).texOffs(122, 78).addBox(6F, -2F, -1F, 4F, 2F, 2F), PartPose.offsetAndRotation(0F, -27F, 0F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(122, 34).addBox(-3.5F, 0F, -3F, 6F, 10F, 6F).texOffs(184, 51).addBox(-5F, -2F, -4.5F, 9F, 4F, 9F).texOffs(140, 66).addBox(-5F, -3.5F, -3.5F, 7F, 2F, 7F), PartPose.offsetAndRotation(-8F, -24F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(92, 51).addBox(-2.5F, 0F, -2.5F, 5F, 10F, 5F).texOffs(196, 66).addBox(-3F, 11F, -1F, 2F, 6F, 2F).texOffs(212, 66).addBox(0F, 12.5F, -1F, 2F, 5F, 2F).texOffs(220, 66).addBox(-2F, 12.5F, -3F, 4F, 3F, 4F).texOffs(134, 78).addBox(-1.5F, 2.5F, -3.5F, 3F, 3F, 1F), PartPose.offsetAndRotation(-0.5F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(146, 34).addBox(-2.5F, 0F, -3F, 6F, 10F, 6F).texOffs(220, 51).addBox(-4F, -2F, -4.5F, 9F, 4F, 9F).texOffs(168, 66).addBox(-2F, -3.5F, -3.5F, 7F, 2F, 7F), PartPose.offsetAndRotation(8F, -24F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(112, 51).addBox(-2.5F, 0F, -2.5F, 5F, 10F, 5F).texOffs(204, 66).addBox(1F, 11F, -1F, 2F, 6F, 2F).texOffs(236, 66).addBox(-2F, 12.5F, -1F, 2F, 5F, 2F).texOffs(0, 78).addBox(-2F, 12.5F, -3F, 4F, 3F, 4F), PartPose.offsetAndRotation(0.5F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(216, 0).addBox(-3.5F, 0F, -3.5F, 7F, 10F, 7F).texOffs(0, 66).addBox(-4.5F, 1.5F, -4.5F, 9F, 3F, 9F), PartPose.offsetAndRotation(-4F, 4F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(170, 34).addBox(-3F, 0F, -3F, 6F, 10F, 6F).texOffs(72, 66).addBox(-4F, 8F, -6F, 8F, 2F, 9F).texOffs(150, 78).addBox(-2.9F, 9F, -7.5F, 1F, 1F, 2F).texOffs(156, 78).addBox(-0.5F, 9F, -7.5F, 1F, 1F, 2F).texOffs(162, 78).addBox(1.9F, 9F, -7.5F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 34).addBox(-3.5F, 0F, -3.5F, 7F, 10F, 7F).texOffs(36, 66).addBox(-4.5F, 1.5F, -4.5F, 9F, 3F, 9F), PartPose.offsetAndRotation(4F, 4F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(194, 34).addBox(-3F, 0F, -3F, 6F, 10F, 6F).texOffs(106, 66).addBox(-4F, 8F, -6F, 8F, 2F, 9F).texOffs(168, 78).addBox(-2.9F, 9F, -7.5F, 1F, 1F, 2F).texOffs(174, 78).addBox(-0.5F, 9F, -7.5F, 1F, 1F, 2F).texOffs(180, 78).addBox(1.9F, 9F, -7.5F, 1F, 1F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
