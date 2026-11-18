// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class LumielModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
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
			{"right_leg"},
			{"left_leg"}
	};

	public LumielModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(58, 0).addBox(-5.5F, -10F, -3F, 11F, 11F, 6F).texOffs(34, 49).addBox(-4.5F, 1F, -2.5F, 9F, 3F, 5F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 20).addBox(-4F, -8F, -4F, 8F, 8F, 8F).texOffs(70, 36).addBox(-0.5F, -12F, -3F, 1F, 4F, 8F), PartPose.offsetAndRotation(0F, -10F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(88, 36).addBox(-3.5F, -3F, -3.5F, 6F, 4F, 7F).texOffs(32, 20).addBox(-2.5F, 0F, -2F, 4F, 11F, 4F).texOffs(22, 0).addBox(-1.5F, 10F, -14F, 2F, 2F, 16F).texOffs(72, 49).addBox(-3F, 9.5F, -1F, 5F, 3F, 2F), PartPose.offsetAndRotation(-7F, -8F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 49).addBox(-2.5F, -3F, -3.5F, 6F, 4F, 7F).texOffs(48, 20).addBox(-1.5F, 0F, -2F, 4F, 11F, 4F).texOffs(0, 0).addBox(2.5F, 2F, -5F, 1F, 10F, 10F).texOffs(62, 49).addBox(3F, 5F, -2F, 1F, 4F, 4F), PartPose.offsetAndRotation(7F, -8F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(64, 20).addBox(-18F, -6F, 0F, 18F, 12F, 1F), PartPose.offsetAndRotation(-3F, -8F, 3F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 36).addBox(0F, -6F, 0F, 18F, 12F, 1F), PartPose.offsetAndRotation(3F, -8F, 3F, 0F, 0F, 0F));
		PartDefinition ring = body.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -21F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb0", CubeListBuilder.create().texOffs(86, 49).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(5F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb1", CubeListBuilder.create().texOffs(90, 49).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(3.5355F, 0F, 3.5355F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb2", CubeListBuilder.create().texOffs(94, 49).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 5F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb3", CubeListBuilder.create().texOffs(98, 49).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(-3.5355F, 0F, 3.5355F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb4", CubeListBuilder.create().texOffs(102, 49).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(-5F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb5", CubeListBuilder.create().texOffs(106, 49).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(-3.5355F, 0F, -3.5355F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb6", CubeListBuilder.create().texOffs(110, 49).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, -5F, 0F, 0F, 0F));
		ring.addOrReplaceChild("orb7", CubeListBuilder.create().texOffs(114, 49).addBox(-0.75F, -0.75F, -0.75F, 1F, 1F, 1F), PartPose.offsetAndRotation(3.5355F, 0F, -3.5355F, 0F, 0F, 0F));
		body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(38, 36).addBox(-5F, 0F, -3F, 10F, 7F, 6F).texOffs(26, 49).addBox(-1.5F, 0F, -3.5F, 3F, 8F, 1F), PartPose.offsetAndRotation(0F, 4F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(92, 0).addBox(-2F, 0F, -2F, 4F, 13F, 4F), PartPose.offsetAndRotation(-2.5F, 11F, 0F, 0F, 0F, 0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(108, 0).addBox(-2F, 0F, -2F, 4F, 13F, 4F), PartPose.offsetAndRotation(2.5F, 11F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
