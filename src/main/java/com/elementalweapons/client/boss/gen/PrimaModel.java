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
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-12F, -54F, -7F, 24F, 32F, 14F).texOffs(192, 96).addBox(-5F, -47F, -8.6F, 10F, 10F, 2F).texOffs(152, 46).addBox(-16F, -55F, -9F, 32F, 6F, 18F).texOffs(304, 46).addBox(-20F, -60F, -6F, 8F, 8F, 12F).texOffs(344, 46).addBox(12F, -60F, -6F, 8F, 8F, 12F).texOffs(384, 46).addBox(-13F, -26F, -8F, 26F, 4F, 16F), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(304, 0).addBox(-8F, -16F, -8F, 16F, 16F, 16F).texOffs(0, 76).addBox(-9F, -19F, -9F, 18F, 2F, 18F).texOffs(56, 112).addBox(-9F, -25F, -9F, 2F, 6F, 2F).texOffs(64, 112).addBox(-5F, -25F, -9F, 2F, 6F, 2F).texOffs(72, 112).addBox(-1F, -25F, -9F, 2F, 6F, 2F).texOffs(80, 112).addBox(3F, -25F, -9F, 2F, 6F, 2F).texOffs(88, 112).addBox(7F, -25F, -9F, 2F, 6F, 2F).texOffs(252, 46).addBox(-9F, -19F, 2F, 18F, 14F, 8F), PartPose.offsetAndRotation(0F, -60F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(208, 112).addBox(-14F, -19F, -1F, 28F, 2F, 2F).texOffs(268, 112).addBox(-14F, 17F, -1F, 28F, 2F, 2F).texOffs(368, 0).addBox(-19F, -14F, -1F, 2F, 28F, 2F).texOffs(376, 0).addBox(17F, -14F, -1F, 2F, 28F, 2F).texOffs(96, 112).addBox(15F, -3F, -1F, 6F, 6F, 2F).texOffs(112, 112).addBox(8.2228F, 11.073F, -1F, 6F, 6F, 2F).texOffs(128, 112).addBox(-7.0054F, 14.5487F, -1F, 6F, 6F, 2F).texOffs(144, 112).addBox(-19.2174F, 4.8099F, -1F, 6F, 6F, 2F).texOffs(160, 112).addBox(-19.2174F, -10.8099F, -1F, 6F, 6F, 2F).texOffs(176, 112).addBox(-7.0054F, -20.5487F, -1F, 6F, 6F, 2F).texOffs(192, 112).addBox(8.2228F, -17.073F, -1F, 6F, 6F, 2F), PartPose.offsetAndRotation(0F, -72F, 10F, 0F, 0F, 0F));
		PartDefinition ring = body.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -32F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("porbfire", CubeListBuilder.create().texOffs(232, 76).addBox(-4F, -4F, -4F, 8F, 8F, 8F).texOffs(264, 76).addBox(-2F, -6F, -2F, 4F, 12F, 4F), PartPose.offsetAndRotation(36F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("porbwater", CubeListBuilder.create().texOffs(280, 76).addBox(-4F, -4F, -4F, 8F, 8F, 8F).texOffs(312, 76).addBox(-2F, -6F, -2F, 4F, 12F, 4F), PartPose.offsetAndRotation(22.4456F, 0F, 28.1459F, 0F, 0F, 0F));
		ring.addOrReplaceChild("porbgrass", CubeListBuilder.create().texOffs(328, 76).addBox(-4F, -4F, -4F, 8F, 8F, 8F).texOffs(360, 76).addBox(-2F, -6F, -2F, 4F, 12F, 4F), PartPose.offsetAndRotation(-8.0108F, 0F, 35.0974F, 0F, 0F, 0F));
		ring.addOrReplaceChild("porbwind", CubeListBuilder.create().texOffs(376, 76).addBox(-4F, -4F, -4F, 8F, 8F, 8F).texOffs(408, 76).addBox(-2F, -6F, -2F, 4F, 12F, 4F), PartPose.offsetAndRotation(-32.4349F, 0F, 15.6198F, 0F, 0F, 0F));
		ring.addOrReplaceChild("porbdark", CubeListBuilder.create().texOffs(424, 76).addBox(-4F, -4F, -4F, 8F, 8F, 8F).texOffs(456, 76).addBox(-2F, -6F, -2F, 4F, 12F, 4F), PartPose.offsetAndRotation(-32.4349F, 0F, -15.6198F, 0F, 0F, 0F));
		ring.addOrReplaceChild("porblight", CubeListBuilder.create().texOffs(472, 76).addBox(-4F, -4F, -4F, 8F, 8F, 8F).texOffs(0, 96).addBox(-2F, -6F, -2F, 4F, 12F, 4F), PartPose.offsetAndRotation(-8.0108F, 0F, -35.0974F, 0F, 0F, 0F));
		ring.addOrReplaceChild("porbelec", CubeListBuilder.create().texOffs(16, 96).addBox(-4F, -4F, -4F, 8F, 8F, 8F).texOffs(48, 96).addBox(-2F, -6F, -2F, 4F, 12F, 4F), PartPose.offsetAndRotation(22.4456F, 0F, -28.1459F, 0F, 0F, 0F));
		PartDefinition swayR1 = body.addOrReplaceChild("swayR1", CubeListBuilder.create().texOffs(72, 46).addBox(-12F, 0F, -8F, 24F, 12F, 16F), PartPose.offsetAndRotation(0F, -20F, 0F, 0F, 0F, 0F));
		PartDefinition swayR2 = swayR1.addOrReplaceChild("swayR2", CubeListBuilder.create().texOffs(384, 0).addBox(-14F, 0F, -9F, 28F, 12F, 18F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition swayR3 = swayR2.addOrReplaceChild("swayR3", CubeListBuilder.create().texOffs(196, 0).addBox(-16F, 0F, -11F, 32F, 12F, 22F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		swayR3.addOrReplaceChild("swayR4", CubeListBuilder.create().texOffs(76, 0).addBox(-18F, 0F, -12F, 36F, 12F, 24F), PartPose.offsetAndRotation(0F, 12F, 0F, 0F, 0F, 0F));
		PartDefinition cape1 = body.addOrReplaceChild("cape1", CubeListBuilder.create().texOffs(216, 96).addBox(-14F, 0F, -1F, 28F, 10F, 2F), PartPose.offsetAndRotation(0F, -54F, 9F, 0F, 0F, 0F));
		PartDefinition cape2 = cape1.addOrReplaceChild("cape2", CubeListBuilder.create().texOffs(276, 96).addBox(-14F, 0F, -1F, 28F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition cape3 = cape2.addOrReplaceChild("cape3", CubeListBuilder.create().texOffs(336, 96).addBox(-14F, 0F, -1F, 28F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition cape4 = cape3.addOrReplaceChild("cape4", CubeListBuilder.create().texOffs(396, 96).addBox(-14F, 0F, -1F, 28F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		PartDefinition cape5 = cape4.addOrReplaceChild("cape5", CubeListBuilder.create().texOffs(456, 96).addBox(-13F, 0F, -1F, 26F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		cape5.addOrReplaceChild("cape6", CubeListBuilder.create().texOffs(0, 112).addBox(-13F, 0F, -1F, 26F, 10F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(476, 0).addBox(-5F, 0F, -3F, 6F, 24F, 6F).texOffs(64, 96).addBox(-6F, 23F, -4F, 8F, 8F, 8F).texOffs(72, 76).addBox(-7F, -2F, -5F, 10F, 8F, 10F), PartPose.offsetAndRotation(-16F, -50F, 0F, 0F, 0F, 0.4363F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 46).addBox(-1F, 0F, -3F, 6F, 24F, 6F).texOffs(96, 96).addBox(-2F, 23F, -4F, 8F, 8F, 8F).texOffs(112, 76).addBox(-3F, -2F, -5F, 10F, 8F, 10F), PartPose.offsetAndRotation(16F, -50F, 0F, 0F, 0F, -0.4363F));
		body.addOrReplaceChild("swayP1", CubeListBuilder.create().texOffs(24, 46).addBox(-5F, 0F, -3F, 6F, 24F, 6F).texOffs(128, 96).addBox(-6F, 23F, -4F, 8F, 8F, 8F).texOffs(152, 76).addBox(-7F, -2F, -5F, 10F, 8F, 10F), PartPose.offsetAndRotation(-16F, -36F, 0F, 0F, 0F, 0.9599F));
		body.addOrReplaceChild("swayQ1", CubeListBuilder.create().texOffs(48, 46).addBox(-1F, 0F, -3F, 6F, 24F, 6F).texOffs(160, 96).addBox(-2F, 23F, -4F, 8F, 8F, 8F).texOffs(192, 76).addBox(-3F, -2F, -5F, 10F, 8F, 10F), PartPose.offsetAndRotation(16F, -36F, 0F, 0F, 0F, -0.9599F));
		return LayerDefinition.create(mesh, 512, 512);
	}
}
