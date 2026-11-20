// 자동 생성 파일 (tools/bossgen/bosses2.py) - 직접 수정하지 마세요
package com.elementalweapons.client.boss.gen;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import com.elementalweapons.client.boss.GenericBossModel;

public class PrimaModel extends GenericBossModel {
	private static final String[][] PATHS = {
			{"body"},
			{"body", "head"},
			{"body", "halo"},
			{"body", "ring"},
			{"body", "ring", "porbfire"},
			{"body", "ring", "porbwater"},
			{"body", "ring", "porbgrass"},
			{"body", "ring", "porbwind"},
			{"body", "ring", "porbdark"},
			{"body", "ring", "porblight"},
			{"body", "ring", "porbelec"},
			{"body", "swayR1"},
			{"body", "swayR1", "swayR2"},
			{"body", "swayR1", "swayR2", "swayR3"},
			{"body", "swayR1", "swayR2", "swayR3", "swayR4"},
			{"body", "cape1"},
			{"body", "cape1", "cape2"},
			{"body", "cape1", "cape2", "cape3"},
			{"body", "cape1", "cape2", "cape3", "cape4"},
			{"body", "cape1", "cape2", "cape3", "cape4", "cape5"},
			{"body", "cape1", "cape2", "cape3", "cape4", "cape5", "cape6"},
			{"body", "right_arm"},
			{"body", "left_arm"},
			{"body", "swayP1"},
			{"body", "swayQ1"}
	};

	public PrimaModel(ModelPart root) {
		super(root, PATHS);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6F, -27F, -3.5F, 12F, 16F, 7F).texOffs(96, 48).addBox(-2.5F, -23.5F, -4.3F, 5F, 5F, 1F).texOffs(76, 23).addBox(-8F, -27.5F, -4.5F, 16F, 3F, 9F).texOffs(152, 23).addBox(-10F, -30F, -3F, 4F, 4F, 6F).texOffs(172, 23).addBox(6F, -30F, -3F, 4F, 4F, 6F).texOffs(192, 23).addBox(-6.5F, -13F, -4F, 13F, 2F, 8F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(152, 0).addBox(-4F, -8F, -4F, 8F, 8F, 8F).texOffs(0, 38).addBox(-4.5F, -9.5F, -4.5F, 9F, 1F, 9F).texOffs(28, 56).addBox(-4.5F, -12.5F, -4.5F, 1F, 3F, 1F).texOffs(32, 56).addBox(-2.5F, -12.5F, -4.5F, 1F, 3F, 1F).texOffs(36, 56).addBox(-0.5F, -12.5F, -4.5F, 1F, 3F, 1F).texOffs(40, 56).addBox(1.5F, -12.5F, -4.5F, 1F, 3F, 1F).texOffs(44, 56).addBox(3.5F, -12.5F, -4.5F, 1F, 3F, 1F).texOffs(126, 23).addBox(-4.5F, -9.5F, 1F, 9F, 7F, 4F), PartPose.offsetAndRotation(0F, -30F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(104, 56).addBox(-7F, -9.5F, -0.5F, 14F, 1F, 1F).texOffs(134, 56).addBox(-7F, 8.5F, -0.5F, 14F, 1F, 1F).texOffs(184, 0).addBox(-9.5F, -7F, -0.5F, 1F, 14F, 1F).texOffs(188, 0).addBox(8.5F, -7F, -0.5F, 1F, 14F, 1F).texOffs(48, 56).addBox(7.5F, -1.5F, -0.5F, 3F, 3F, 1F).texOffs(56, 56).addBox(4.1114F, 5.5365F, -0.5F, 3F, 3F, 1F).texOffs(64, 56).addBox(-3.5027F, 7.2744F, -0.5F, 3F, 3F, 1F).texOffs(72, 56).addBox(-9.6087F, 2.405F, -0.5F, 3F, 3F, 1F).texOffs(80, 56).addBox(-9.6087F, -5.405F, -0.5F, 3F, 3F, 1F).texOffs(88, 56).addBox(-3.5027F, -10.2744F, -0.5F, 3F, 3F, 1F).texOffs(96, 56).addBox(4.1114F, -8.5365F, -0.5F, 3F, 3F, 1F), PartPose.offsetAndRotation(0F, -36F, 5F, 0F, 0F, 0F));
		PartDefinition ring = body.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -16F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("porbfire", CubeListBuilder.create().texOffs(116, 38).addBox(-2F, -2F, -2F, 4F, 4F, 4F).texOffs(132, 38).addBox(-1F, -3F, -1F, 2F, 6F, 2F), PartPose.offsetAndRotation(18F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("porbwater", CubeListBuilder.create().texOffs(140, 38).addBox(-2F, -2F, -2F, 4F, 4F, 4F).texOffs(156, 38).addBox(-1F, -3F, -1F, 2F, 6F, 2F), PartPose.offsetAndRotation(11.2228F, 0F, 14.073F, 0F, 0F, 0F));
		ring.addOrReplaceChild("porbgrass", CubeListBuilder.create().texOffs(164, 38).addBox(-2F, -2F, -2F, 4F, 4F, 4F).texOffs(180, 38).addBox(-1F, -3F, -1F, 2F, 6F, 2F), PartPose.offsetAndRotation(-4.0054F, 0F, 17.5487F, 0F, 0F, 0F));
		ring.addOrReplaceChild("porbwind", CubeListBuilder.create().texOffs(188, 38).addBox(-2F, -2F, -2F, 4F, 4F, 4F).texOffs(204, 38).addBox(-1F, -3F, -1F, 2F, 6F, 2F), PartPose.offsetAndRotation(-16.2174F, 0F, 7.8099F, 0F, 0F, 0F));
		ring.addOrReplaceChild("porbdark", CubeListBuilder.create().texOffs(212, 38).addBox(-2F, -2F, -2F, 4F, 4F, 4F).texOffs(228, 38).addBox(-1F, -3F, -1F, 2F, 6F, 2F), PartPose.offsetAndRotation(-16.2174F, 0F, -7.8099F, 0F, 0F, 0F));
		ring.addOrReplaceChild("porblight", CubeListBuilder.create().texOffs(236, 38).addBox(-2F, -2F, -2F, 4F, 4F, 4F).texOffs(0, 48).addBox(-1F, -3F, -1F, 2F, 6F, 2F), PartPose.offsetAndRotation(-4.0054F, 0F, -17.5487F, 0F, 0F, 0F));
		ring.addOrReplaceChild("porbelec", CubeListBuilder.create().texOffs(8, 48).addBox(-2F, -2F, -2F, 4F, 4F, 4F).texOffs(24, 48).addBox(-1F, -3F, -1F, 2F, 6F, 2F), PartPose.offsetAndRotation(11.2228F, 0F, -14.073F, 0F, 0F, 0F));
		PartDefinition swayR1 = body.addOrReplaceChild("swayR1", CubeListBuilder.create().texOffs(36, 23).addBox(-6F, 0F, -4F, 12F, 6F, 8F), PartPose.offsetAndRotation(0F, -10F, 0F, 0F, 0F, 0F));
		PartDefinition swayR2 = swayR1.addOrReplaceChild("swayR2", CubeListBuilder.create().texOffs(192, 0).addBox(-7F, 0F, -4.5F, 14F, 6F, 9F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition swayR3 = swayR2.addOrReplaceChild("swayR3", CubeListBuilder.create().texOffs(98, 0).addBox(-8F, 0F, -5.5F, 16F, 6F, 11F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		swayR3.addOrReplaceChild("swayR4", CubeListBuilder.create().texOffs(38, 0).addBox(-9F, 0F, -6F, 18F, 6F, 12F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition cape1 = body.addOrReplaceChild("cape1", CubeListBuilder.create().texOffs(108, 48).addBox(-7F, 0F, -0.5F, 14F, 5F, 1F), PartPose.offsetAndRotation(0F, -27F, 4.5F, 0F, 0F, 0F));
		PartDefinition cape2 = cape1.addOrReplaceChild("cape2", CubeListBuilder.create().texOffs(138, 48).addBox(-7F, 0F, -0.5F, 14F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition cape3 = cape2.addOrReplaceChild("cape3", CubeListBuilder.create().texOffs(168, 48).addBox(-7F, 0F, -0.5F, 14F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition cape4 = cape3.addOrReplaceChild("cape4", CubeListBuilder.create().texOffs(198, 48).addBox(-7F, 0F, -0.5F, 14F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		PartDefinition cape5 = cape4.addOrReplaceChild("cape5", CubeListBuilder.create().texOffs(228, 48).addBox(-6.5F, 0F, -0.5F, 13F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		cape5.addOrReplaceChild("cape6", CubeListBuilder.create().texOffs(0, 56).addBox(-6.5F, 0F, -0.5F, 13F, 5F, 1F), PartPose.offsetAndRotation(0F, 5F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(238, 0).addBox(-2.5F, 0F, -1.5F, 3F, 12F, 3F).texOffs(32, 48).addBox(-3F, 11.5F, -2F, 4F, 4F, 4F).texOffs(36, 38).addBox(-3.5F, -1F, -2.5F, 5F, 4F, 5F), PartPose.offsetAndRotation(-8F, -25F, 0F, 0F, 0F, 0.4363F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 23).addBox(-0.5F, 0F, -1.5F, 3F, 12F, 3F).texOffs(48, 48).addBox(-1F, 11.5F, -2F, 4F, 4F, 4F).texOffs(56, 38).addBox(-1.5F, -1F, -2.5F, 5F, 4F, 5F), PartPose.offsetAndRotation(8F, -25F, 0F, 0F, 0F, -0.4363F));
		body.addOrReplaceChild("swayP1", CubeListBuilder.create().texOffs(12, 23).addBox(-2.5F, 0F, -1.5F, 3F, 12F, 3F).texOffs(64, 48).addBox(-3F, 11.5F, -2F, 4F, 4F, 4F).texOffs(76, 38).addBox(-3.5F, -1F, -2.5F, 5F, 4F, 5F), PartPose.offsetAndRotation(-8F, -18F, 0F, 0F, 0F, 0.9599F));
		body.addOrReplaceChild("swayQ1", CubeListBuilder.create().texOffs(24, 23).addBox(-0.5F, 0F, -1.5F, 3F, 12F, 3F).texOffs(80, 48).addBox(-1F, 11.5F, -2F, 4F, 4F, 4F).texOffs(96, 38).addBox(-1.5F, -1F, -2.5F, 5F, 4F, 5F), PartPose.offsetAndRotation(8F, -18F, 0F, 0F, 0F, -0.9599F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
