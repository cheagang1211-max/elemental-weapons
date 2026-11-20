// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class CrystalGolemModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "right_arm", "right_arm2"},
			{"body", "left_arm"},
			{"body", "left_arm", "left_arm2"},
			{"body", "pulse_core"},
			{"right_leg"},
			{"right_leg", "right_leg2"},
			{"left_leg"},
			{"left_leg", "left_leg2"}
	};

	public CrystalGolemModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-11F, -24F, -7F, 22F, 18F, 14F).texOffs(192, 68).addBox(-4F, -19F, -8F, 8F, 8F, 1F).texOffs(152, 51).addBox(-6F, -21F, -7.5F, 12F, 12F, 1F).texOffs(96, 32).addBox(-8F, -8F, -6F, 16F, 6F, 12F).texOffs(72, 0).addBox(-12F, -26F, -7.5F, 24F, 4F, 15F).texOffs(42, 68).addBox(-7.5F, -33.2F, 2.5F, 3F, 8F, 3F).texOffs(210, 68).addBox(-4.7F, -30.8F, 3.5F, 3F, 6F, 3F).texOffs(112, 81).addBox(-9.1F, -29.1F, 2F, 2F, 4F, 2F).texOffs(178, 51).addBox(4.5F, -35.8F, 1.5F, 3F, 10F, 3F).texOffs(222, 68).addBox(7.7F, -32.2F, 2.5F, 3F, 6F, 3F).texOffs(24, 81).addBox(2.6F, -30.9F, 1F, 2F, 5F, 2F).texOffs(234, 68).addBox(-1F, -33.1F, 5F, 2F, 7F, 2F).texOffs(32, 81).addBox(1.4F, -30.9F, 6F, 2F, 5F, 2F).texOffs(120, 81).addBox(-2.8F, -29.8F, 4F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, 2F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(152, 32).addBox(-5F, -7F, -5.5F, 10F, 8F, 9F).texOffs(54, 68).addBox(-6F, -8.5F, -4.5F, 12F, 2F, 9F).texOffs(242, 68).addBox(-1F, -14F, 0F, 2F, 6F, 2F).texOffs(128, 81).addBox(1F, -12F, 1F, 2F, 4F, 2F).texOffs(164, 81).addBox(-2.5F, -11F, -1F, 2F, 3F, 2F).texOffs(172, 81).addBox(-4.5F, -11.1F, 1.5F, 1F, 4F, 1F).texOffs(192, 81).addBox(-3.1F, -9.9F, 2.5F, 1F, 3F, 1F).texOffs(196, 81).addBox(-6.05F, -9.05F, 0F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, -27F, -5F, 0F, 0F, 0F));
		PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(150, 0).addBox(-4.5F, 0F, -4F, 8F, 11F, 8F).texOffs(96, 68).addBox(-2.5F, -10.9F, -1.5F, 3F, 8F, 3F).texOffs(0, 81).addBox(0.1F, -8.1F, -0.5F, 3F, 5F, 3F).texOffs(136, 81).addBox(-3.95F, -6.95F, -2F, 2F, 4F, 2F).texOffs(64, 51).addBox(-6F, -2F, -5.5F, 11F, 4F, 11F).texOffs(108, 68).addBox(-6F, -3.5F, -4.5F, 9F, 2F, 9F), PartPose.offsetAndRotation(-15F, -22F, 0F, 0F, 0F, 0F));
		right_arm.addOrReplaceChild("right_arm2", CubeListBuilder.create().texOffs(190, 32).addBox(-3.5F, 0F, -3.5F, 7F, 10F, 7F).texOffs(218, 32).addBox(-4.5F, 10F, -4.5F, 9F, 8F, 9F).texOffs(40, 81).addBox(-4.5F, 17.5F, -4.5F, 9F, 2F, 5F).texOffs(68, 81).addBox(-4F, 5.1F, -1F, 2F, 5F, 2F).texOffs(176, 81).addBox(-2.4F, 6.9F, 0F, 2F, 3F, 2F).texOffs(204, 81).addBox(-5.2F, 7.8F, -2F, 2F, 2F, 2F), PartPose.offsetAndRotation(-0.5F, 11F, 0F, 0F, 0F, 0F));
		PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(182, 0).addBox(-3.5F, 0F, -4F, 8F, 11F, 8F).texOffs(144, 68).addBox(-0.5F, -10.9F, -1.5F, 3F, 8F, 3F).texOffs(12, 81).addBox(2.1F, -8.1F, -0.5F, 3F, 5F, 3F).texOffs(144, 81).addBox(-1.95F, -6.95F, -2F, 2F, 4F, 2F).texOffs(108, 51).addBox(-5F, -2F, -5.5F, 11F, 4F, 11F).texOffs(156, 68).addBox(-3F, -3.5F, -4.5F, 9F, 2F, 9F), PartPose.offsetAndRotation(15F, -22F, 0F, 0F, 0F, 0F));
		left_arm.addOrReplaceChild("left_arm2", CubeListBuilder.create().texOffs(0, 51).addBox(-3.5F, 0F, -3.5F, 7F, 10F, 7F).texOffs(28, 51).addBox(-4.5F, 10F, -4.5F, 9F, 8F, 9F).texOffs(76, 81).addBox(-4.5F, 17.5F, -4.5F, 9F, 2F, 5F).texOffs(104, 81).addBox(2F, 5.1F, -1F, 2F, 5F, 2F).texOffs(184, 81).addBox(3.6F, 6.9F, 0F, 2F, 3F, 2F).texOffs(212, 81).addBox(0.8F, 7.8F, -2F, 2F, 2F, 2F), PartPose.offsetAndRotation(0.5F, 11F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("pulse_core", CubeListBuilder.create().texOffs(152, 81).addBox(-2F, -2F, -1F, 4F, 4F, 2F), PartPose.offsetAndRotation(0F, -15F, -8F, 0F, 0F, 0F));
		PartDefinition right_leg = root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(214, 0).addBox(-4F, 0F, -4F, 8F, 11F, 8F), PartPose.offsetAndRotation(-6F, 2F, 0F, 0F, 0F, 0F));
		right_leg.addOrReplaceChild("right_leg2", CubeListBuilder.create().texOffs(0, 32).addBox(-4F, 0F, -4F, 8F, 11F, 8F).texOffs(190, 51).addBox(-5F, 9F, -7F, 10F, 2F, 11F), PartPose.offsetAndRotation(0F, 11F, 0F, 0F, 0F, 0F));
		PartDefinition left_leg = root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(32, 32).addBox(-4F, 0F, -4F, 8F, 11F, 8F), PartPose.offsetAndRotation(6F, 2F, 0F, 0F, 0F, 0F));
		left_leg.addOrReplaceChild("left_leg2", CubeListBuilder.create().texOffs(64, 32).addBox(-4F, 0F, -4F, 8F, 11F, 8F).texOffs(0, 68).addBox(-5F, 9F, -7F, 10F, 2F, 11F), PartPose.offsetAndRotation(0F, 11F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
