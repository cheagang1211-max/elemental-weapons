// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class ZephyrosModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "right_wing"},
			{"body", "left_wing"},
			{"body", "tail1"},
			{"body", "tail1", "tail2"},
			{"right_leg"},
			{"left_leg"}
	};

	public ZephyrosModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5F, -5F, -7F, 10F, 9F, 14F).texOffs(68, 49).addBox(-4F, -3F, -7.5F, 8F, 6F, 1F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(48, 0).addBox(-3.5F, -6F, -6F, 7F, 7F, 7F).texOffs(86, 49).addBox(-1.5F, -3F, -10F, 3F, 3F, 4F).texOffs(104, 36).addBox(-0.5F, -10F, -3F, 1F, 5F, 6F), PartPose.offsetAndRotation(0F, -3F, -7F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(32, 60).addBox(-12F, -1F, -1F, 12F, 2F, 3F).texOffs(0, 23).addBox(-22F, 0F, -1F, 22F, 1F, 12F).texOffs(0, 49).addBox(-24F, 0F, 0F, 3F, 1F, 10F), PartPose.offsetAndRotation(-5F, -4F, -2F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(62, 60).addBox(0F, -1F, -1F, 12F, 2F, 3F).texOffs(0, 36).addBox(0F, 0F, -1F, 22F, 1F, 12F).texOffs(26, 49).addBox(21F, 0F, 0F, 3F, 1F, 10F), PartPose.offsetAndRotation(5F, -4F, -2F, 0F, 0F, 0F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(68, 36).addBox(-4F, -1F, 0F, 8F, 2F, 10F), PartPose.offsetAndRotation(0F, -2F, 7F, 0F, 0F, 0F));
		tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(0, 60).addBox(-5F, -0.5F, 0F, 10F, 1F, 6F), PartPose.offsetAndRotation(0F, 0F, 10F, 0F, 0F, 0F));
		root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(52, 49).addBox(-1F, 0F, -1F, 2F, 8F, 2F).texOffs(92, 60).addBox(-2F, 8F, -3F, 4F, 1F, 4F), PartPose.offsetAndRotation(-3F, 14F, 2F, 0F, 0F, 0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(60, 49).addBox(-1F, 0F, -1F, 2F, 8F, 2F).texOffs(108, 60).addBox(-2F, 8F, -3F, 4F, 1F, 4F), PartPose.offsetAndRotation(3F, 14F, 2F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
