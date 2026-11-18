// 자동 생성 파일 (tools/bossgen/bosses.py) - 직접 수정하지 마세요
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
			{"body", "left_arm"}
	};

	public ElementalMerchantModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(38, 0).addBox(-4F, 0F, -3F, 8F, 12F, 6F).texOffs(6, 0).addBox(-4.5F, 11F, -3.5F, 9F, 13F, 7F).texOffs(0, 0).addBox(-1F, 1F, -3.5F, 2F, 22F, 1F).texOffs(0, 23).addBox(-4F, 2F, 3F, 8F, 9F, 4F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(66, 0).addBox(-4F, -10F, -4F, 8F, 10F, 8F).texOffs(24, 23).addBox(-4.5F, -11F, -4.5F, 9F, 4F, 9F).texOffs(60, 23).addBox(-1F, -11.5F, -5F, 2F, 2F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(98, 0).addBox(-3F, -2F, -2F, 3F, 12F, 4F), PartPose.offsetAndRotation(-5F, 2F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(112, 0).addBox(0F, -2F, -2F, 3F, 12F, 4F), PartPose.offsetAndRotation(5F, 2F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}
}
