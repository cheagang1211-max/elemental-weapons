// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
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
			{"body", "head1"},
			{"body", "head2"},
			{"body", "right_arm"},
			{"body", "left_arm"},
			{"body", "right_wing"},
			{"body", "left_wing"},
			{"body", "ring"},
			{"body", "ring", "orb0"},
			{"body", "ring", "orb1"},
			{"body", "ring", "orb2"},
			{"body", "ring", "orb3"},
			{"body", "ring", "orb4"},
			{"body", "ring", "orb5"},
			{"body", "ring", "orb6"},
			{"body", "ring", "orb7"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"body", "tail1", "tail2", "tail3"}
	};

	public TwilightTwinsModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(4, 0).addBox(-6F, -10F, -3F, 6F, 11F, 6F).texOffs(28, 0).addBox(0F, -10F, -3F, 6F, 11F, 6F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(0, 38).addBox(-3F, -6F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(-3F, -10F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(24, 38).addBox(-3F, -6F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(3F, -10F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 23).addBox(-3F, -1F, -2F, 4F, 11F, 4F).texOffs(90, 0).addBox(-2F, 10F, -12F, 1F, 2F, 14F), PartPose.offsetAndRotation(-7F, -8F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(16, 23).addBox(-1F, -1F, -2F, 4F, 11F, 4F).texOffs(0, 0).addBox(1F, -6F, -0.5F, 1F, 22F, 1F).texOffs(62, 38).addBox(0F, -9F, -1.5F, 3F, 3F, 3F), PartPose.offsetAndRotation(7F, -8F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(32, 23).addBox(-16F, -6F, 0F, 16F, 12F, 1F), PartPose.offsetAndRotation(-3F, -8F, 3F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(66, 23).addBox(0F, -6F, 0F, 16F, 12F, 1F), PartPose.offsetAndRotation(3F, -8F, 3F, 0F, 0F, 0F));
		PartDefinition ring = body.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -19F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb0", CubeListBuilder.create().texOffs(74, 38).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(6F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb1", CubeListBuilder.create().texOffs(78, 38).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(4.2426F, 0F, 4.2426F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb2", CubeListBuilder.create().texOffs(82, 38).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 6F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb3", CubeListBuilder.create().texOffs(86, 38).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(-4.2426F, 0F, 4.2426F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb4", CubeListBuilder.create().texOffs(90, 38).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(-6F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb5", CubeListBuilder.create().texOffs(94, 38).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(-4.2426F, 0F, -4.2426F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb6", CubeListBuilder.create().texOffs(98, 38).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, -6F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb7", CubeListBuilder.create().texOffs(102, 38).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(4.2426F, 0F, -4.2426F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(52, 0).addBox(-6F, 0F, -3.5F, 12F, 10F, 7F), PartPose.offsetAndRotation(0F, 1F, 0F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(100, 23).addBox(-4.5F, 0F, -2.5F, 9F, 8F, 5F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(48, 38).addBox(-2F, 0F, -1.5F, 4F, 5F, 3F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
