// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class TwilightTwinsModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "twin1"},
			{"body", "twin1", "head1"},
			{"body", "twin1", "right_arm"},
			{"body", "twin1", "bob_1"},
			{"body", "twin1", "sway11"},
			{"body", "twin1", "sway11", "sway12"},
			{"body", "twin1", "sway11", "sway12", "sway13"},
			{"body", "twin2"},
			{"body", "twin2", "head2"},
			{"body", "twin2", "left_arm"},
			{"body", "twin2", "bob_2"},
			{"body", "twin2", "sway21"},
			{"body", "twin2", "sway21", "sway22"},
			{"body", "twin2", "sway21", "sway22", "sway23"},
			{"body", "ring"},
			{"body", "ring", "tw0"},
			{"body", "ring", "tw1"},
			{"body", "ring", "tw2"},
			{"body", "ring", "tw3"},
			{"body", "ring", "tws0"},
			{"body", "ring", "tws1"},
			{"body", "ring", "tws2"},
			{"body", "ring", "tws3"}
	};

	public TwilightTwinsModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(316, 40).addBox(-4F, -8F, -4F, 8F, 8F, 8F), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
		PartDefinition twin1 = body.addOrReplaceChild("twin1", CubeListBuilder.create().texOffs(0, 0).addBox(-8F, -40F, -6F, 16F, 28F, 12F).texOffs(112, 68).addBox(-4F, -40F, -7.4F, 8F, 8F, 2F).texOffs(280, 68).addBox(-2F, -38F, -8.2F, 4F, 4F, 2F), PartPose.offsetAndRotation(-12F, 0F, 0F, 0F, 0F, 0.1047F));
		twin1.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(160, 0).addBox(-7F, -16F, -7F, 14F, 16F, 14F).texOffs(204, 40).addBox(-8F, -18F, -3F, 16F, 12F, 12F).texOffs(0, 68).addBox(-6F, -19F, -6F, 12F, 2F, 12F).texOffs(292, 68).addBox(-1F, -23F, -5F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, -48F, 0F, 0F, 0F, 0F));
		twin1.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(112, 0).addBox(-5F, -1F, -3F, 6F, 26F, 6F).texOffs(348, 40).addBox(-6F, 24F, -6F, 8F, 8F, 8F), PartPose.offsetAndRotation(-12F, -42F, 0F, 0F, 0F, 0.3491F));
		twin1.addOrReplaceChild("bob_1", CubeListBuilder.create().texOffs(380, 40).addBox(-4F, -4F, -4F, 8F, 8F, 8F).texOffs(320, 68).addBox(-6F, -1F, -1F, 12F, 2F, 2F).texOffs(48, 68).addBox(-1F, -6F, -1F, 2F, 12F, 2F), PartPose.offsetAndRotation(-20F, -52F, -8F, 0F, 0F, 0F));
		PartDefinition sway11 = twin1.addOrReplaceChild("sway11", CubeListBuilder.create().texOffs(76, 40).addBox(-9F, 0F, -7F, 18F, 12F, 14F), PartPose.offsetAndRotation(0F, -8F, 0F, 0F, 0F, 0F));
		PartDefinition sway12 = sway11.addOrReplaceChild("sway12", CubeListBuilder.create().texOffs(272, 0).addBox(-10F, 0F, -8F, 20F, 12F, 16F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		sway12.addOrReplaceChild("sway13", CubeListBuilder.create().texOffs(344, 0).addBox(-11F, 0F, -8F, 22F, 12F, 16F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition twin2 = body.addOrReplaceChild("twin2", CubeListBuilder.create().texOffs(56, 0).addBox(-8F, -40F, -6F, 16F, 28F, 12F).texOffs(132, 68).addBox(-4F, -40F, -7.4F, 8F, 8F, 2F).texOffs(300, 68).addBox(-2F, -38F, -8.2F, 4F, 4F, 2F), PartPose.offsetAndRotation(12F, 0F, 0F, 0F, 0F, -0.1047F));
		twin2.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(216, 0).addBox(-7F, -16F, -7F, 14F, 16F, 14F).texOffs(260, 40).addBox(-8F, -18F, -3F, 16F, 12F, 12F).texOffs(56, 68).addBox(-6F, -19F, -6F, 12F, 2F, 12F).texOffs(312, 68).addBox(-1F, -23F, -5F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, -48F, 0F, 0F, 0F, 0F));
		twin2.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(136, 0).addBox(-1F, -1F, -3F, 6F, 26F, 6F).texOffs(412, 40).addBox(-2F, 24F, -6F, 8F, 8F, 8F), PartPose.offsetAndRotation(12F, -42F, 0F, 0F, 0F, -0.3491F));
		twin2.addOrReplaceChild("bob_2", CubeListBuilder.create().texOffs(444, 40).addBox(-4F, -4F, -4F, 8F, 8F, 8F).texOffs(348, 68).addBox(-6F, -1F, -1F, 12F, 2F, 2F).texOffs(104, 68).addBox(-1F, -6F, -1F, 2F, 12F, 2F), PartPose.offsetAndRotation(20F, -52F, -8F, 0F, 0F, 0F));
		PartDefinition sway21 = twin2.addOrReplaceChild("sway21", CubeListBuilder.create().texOffs(140, 40).addBox(-9F, 0F, -7F, 18F, 12F, 14F), PartPose.offsetAndRotation(0F, -8F, 0F, 0F, 0F, 0F));
		PartDefinition sway22 = sway21.addOrReplaceChild("sway22", CubeListBuilder.create().texOffs(420, 0).addBox(-10F, 0F, -8F, 20F, 12F, 16F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		sway22.addOrReplaceChild("sway23", CubeListBuilder.create().texOffs(0, 40).addBox(-11F, 0F, -8F, 22F, 12F, 16F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition ring = body.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("tw0", CubeListBuilder.create().texOffs(152, 68).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(30F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("tw1", CubeListBuilder.create().texOffs(168, 68).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(21.2132F, 0F, 21.2132F, 0F, 0.7854F, 0F));
		ring.addOrReplaceChild("tw2", CubeListBuilder.create().texOffs(184, 68).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(0F, 0F, 30F, 0F, 1.5708F, 0F));
		ring.addOrReplaceChild("tw3", CubeListBuilder.create().texOffs(200, 68).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-21.2132F, 0F, 21.2132F, 0F, 2.3562F, 0F));
		ring.addOrReplaceChild("tws0", CubeListBuilder.create().texOffs(216, 68).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(21.3274F, 0F, 21.0984F, 0F, 0F, 0F));
		ring.addOrReplaceChild("tws1", CubeListBuilder.create().texOffs(232, 68).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-21.0814F, 0F, 21.3442F, 0F, 0F, 0F));
		ring.addOrReplaceChild("tws2", CubeListBuilder.create().texOffs(248, 68).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(-21.361F, 0F, -21.0644F, 0F, 0F, 0F));
		ring.addOrReplaceChild("tws3", CubeListBuilder.create().texOffs(264, 68).addBox(-2F, -2F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(21.0474F, 0F, -21.3777F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
