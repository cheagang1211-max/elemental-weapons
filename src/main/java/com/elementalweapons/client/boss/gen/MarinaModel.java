// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class MarinaModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_arm"},
			{"body", "left_arm"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"},
			{"body", "tail1", "tail2", "tail3", "tail4"},
			{"body", "tail1", "tail2", "tail3", "tail4", "tail5"},
			{"body", "ring"},
			{"body", "ring", "orb0"},
			{"body", "ring", "orb1"},
			{"body", "ring", "orb2"},
			{"body", "ring", "orb3"},
			{"body", "ring", "orb4"},
			{"body", "ring", "orb5"},
			{"body", "ring", "orb6"},
			{"body", "ring", "orb7"}
	};

	public MarinaModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(4, 0).addBox(-5F, -9F, -3F, 10F, 10F, 6F).texOffs(30, 25).addBox(-5.5F, -8F, -3.5F, 11F, 3F, 7F).texOffs(90, 25).addBox(-5.5F, 0F, -3.5F, 11F, 2F, 7F), PartPose.offsetAndRotation(0F, -2F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(36, 0).addBox(-4F, -8F, -4F, 8F, 8F, 8F).texOffs(68, 0).addBox(-4.5F, -8.5F, 1F, 9F, 12F, 4F).texOffs(60, 36).addBox(-3F, -11F, -1F, 1F, 3F, 1F).texOffs(30, 36).addBox(-0.5F, -13F, -1F, 1F, 5F, 1F).texOffs(64, 36).addBox(2F, -11F, -1F, 1F, 3F, 1F).texOffs(68, 36).addBox(-3.5F, -9F, -1.5F, 7F, 1F, 3F), PartPose.offsetAndRotation(0F, -10F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(94, 0).addBox(-3F, -1F, -1.5F, 3F, 11F, 3F).texOffs(0, 0).addBox(-2F, -14F, -0.5F, 1F, 24F, 1F).texOffs(32, 45).addBox(-4F, -16F, -0.5F, 5F, 1F, 1F).texOffs(88, 36).addBox(-4F, -19F, -0.5F, 1F, 3F, 1F).texOffs(34, 36).addBox(-2F, -21F, -0.5F, 1F, 5F, 1F).texOffs(92, 36).addBox(0F, -19F, -0.5F, 1F, 3F, 1F), PartPose.offsetAndRotation(-6F, -9F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(106, 0).addBox(0F, -1F, -1.5F, 3F, 11F, 3F), PartPose.offsetAndRotation(6F, -9F, 0F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 25).addBox(-4.5F, 0F, -3F, 9F, 5F, 6F), PartPose.offsetAndRotation(0F, 1F, 0F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(66, 25).addBox(-3.5F, 0F, -2.5F, 7F, 5F, 5F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(0, 36).addBox(-2.5F, 0F, -2F, 5F, 5F, 4F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition tail4 = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(18, 36).addBox(-1.5F, 0F, -1.5F, 3F, 4F, 3F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(38, 36).addBox(-5F, 0F, -0.5F, 10F, 4F, 1F), PartPose.offsetAndRotation(0F, 4F, 0F, 0F, 0F, 0F));
		PartDefinition ring = body.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -8F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb0", CubeListBuilder.create().texOffs(96, 36).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(10F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb1", CubeListBuilder.create().texOffs(104, 36).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(7.0711F, 0F, 7.0711F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb2", CubeListBuilder.create().texOffs(112, 36).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, 0F, 10F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb3", CubeListBuilder.create().texOffs(120, 36).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-7.0711F, 0F, 7.0711F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb4", CubeListBuilder.create().texOffs(0, 45).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-10F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb5", CubeListBuilder.create().texOffs(8, 45).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-7.0711F, 0F, -7.0711F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb6", CubeListBuilder.create().texOffs(16, 45).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, 0F, -10F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb7", CubeListBuilder.create().texOffs(24, 45).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(7.0711F, 0F, -7.0711F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
