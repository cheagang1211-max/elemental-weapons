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
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(158, 20).addBox(-2F, -4F, -2F, 4F, 4F, 4F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition twin1 = body.addOrReplaceChild("twin1", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -20F, -3F, 8F, 14F, 6F).texOffs(56, 34).addBox(-2F, -20F, -3.7F, 4F, 4F, 1F).texOffs(140, 34).addBox(-1F, -19F, -4.1F, 2F, 2F, 1F), PartPose.offsetAndRotation(-6F, 0F, 0F, 0F, 0F, 0.1047F));
		twin1.addOrReplaceChild("head1", CubeListBuilder.create().texOffs(80, 0).addBox(-3.5F, -8F, -3.5F, 7F, 8F, 7F).texOffs(102, 20).addBox(-4F, -9F, -1.5F, 8F, 6F, 6F).texOffs(0, 34).addBox(-3F, -9.5F, -3F, 6F, 1F, 6F).texOffs(146, 34).addBox(-0.5F, -11.5F, -2.5F, 1F, 2F, 1F), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		twin1.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(56, 0).addBox(-2.5F, -0.5F, -1.5F, 3F, 13F, 3F).texOffs(174, 20).addBox(-3F, 12F, -3F, 4F, 4F, 4F), PartPose.offsetAndRotation(-6F, -21F, 0F, 0F, 0F, 0.3491F));
		twin1.addOrReplaceChild("bob_1", CubeListBuilder.create().texOffs(190, 20).addBox(-2F, -2F, -2F, 4F, 4F, 4F).texOffs(160, 34).addBox(-3F, -0.5F, -0.5F, 6F, 1F, 1F).texOffs(24, 34).addBox(-0.5F, -3F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(-10F, -26F, -4F, 0F, 0F, 0F));
		PartDefinition sway11 = twin1.addOrReplaceChild("sway11", CubeListBuilder.create().texOffs(38, 20).addBox(-4.5F, 0F, -3.5F, 9F, 6F, 7F), PartPose.offsetAndRotation(0F, -4F, 0F, 0F, 0F, 0F));
		PartDefinition sway12 = sway11.addOrReplaceChild("sway12", CubeListBuilder.create().texOffs(136, 0).addBox(-5F, 0F, -4F, 10F, 6F, 8F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		sway12.addOrReplaceChild("sway13", CubeListBuilder.create().texOffs(172, 0).addBox(-5.5F, 0F, -4F, 11F, 6F, 8F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition twin2 = body.addOrReplaceChild("twin2", CubeListBuilder.create().texOffs(28, 0).addBox(-4F, -20F, -3F, 8F, 14F, 6F).texOffs(66, 34).addBox(-2F, -20F, -3.7F, 4F, 4F, 1F).texOffs(150, 34).addBox(-1F, -19F, -4.1F, 2F, 2F, 1F), PartPose.offsetAndRotation(6F, 0F, 0F, 0F, 0F, -0.1047F));
		twin2.addOrReplaceChild("head2", CubeListBuilder.create().texOffs(108, 0).addBox(-3.5F, -8F, -3.5F, 7F, 8F, 7F).texOffs(130, 20).addBox(-4F, -9F, -1.5F, 8F, 6F, 6F).texOffs(28, 34).addBox(-3F, -9.5F, -3F, 6F, 1F, 6F).texOffs(156, 34).addBox(-0.5F, -11.5F, -2.5F, 1F, 2F, 1F), PartPose.offsetAndRotation(0F, -24F, 0F, 0F, 0F, 0F));
		twin2.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(68, 0).addBox(-0.5F, -0.5F, -1.5F, 3F, 13F, 3F).texOffs(206, 20).addBox(-1F, 12F, -3F, 4F, 4F, 4F), PartPose.offsetAndRotation(6F, -21F, 0F, 0F, 0F, -0.3491F));
		twin2.addOrReplaceChild("bob_2", CubeListBuilder.create().texOffs(222, 20).addBox(-2F, -2F, -2F, 4F, 4F, 4F).texOffs(174, 34).addBox(-3F, -0.5F, -0.5F, 6F, 1F, 1F).texOffs(52, 34).addBox(-0.5F, -3F, -0.5F, 1F, 6F, 1F), PartPose.offsetAndRotation(10F, -26F, -4F, 0F, 0F, 0F));
		PartDefinition sway21 = twin2.addOrReplaceChild("sway21", CubeListBuilder.create().texOffs(70, 20).addBox(-4.5F, 0F, -3.5F, 9F, 6F, 7F), PartPose.offsetAndRotation(0F, -4F, 0F, 0F, 0F, 0F));
		PartDefinition sway22 = sway21.addOrReplaceChild("sway22", CubeListBuilder.create().texOffs(210, 0).addBox(-5F, 0F, -4F, 10F, 6F, 8F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		sway22.addOrReplaceChild("sway23", CubeListBuilder.create().texOffs(0, 20).addBox(-5.5F, 0F, -4F, 11F, 6F, 8F), PartPose.offsetAndRotation(0F, 6F, 0F, 0F, 0F, 0F));
		PartDefinition ring = body.addOrReplaceChild("ring", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -12F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("tw0", CubeListBuilder.create().texOffs(76, 34).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(15F, 0F, 0F, 0F, 0F, 0F));
		ring.addOrReplaceChild("tw1", CubeListBuilder.create().texOffs(84, 34).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(10.6066F, 0F, 10.6066F, 0F, 0.7854F, 0F));
		ring.addOrReplaceChild("tw2", CubeListBuilder.create().texOffs(92, 34).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, 0F, 15F, 0F, 1.5708F, 0F));
		ring.addOrReplaceChild("tw3", CubeListBuilder.create().texOffs(100, 34).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-10.6066F, 0F, 10.6066F, 0F, 2.3562F, 0F));
		ring.addOrReplaceChild("tws0", CubeListBuilder.create().texOffs(108, 34).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(10.6637F, 0F, 10.5492F, 0F, 0F, 0F));
		ring.addOrReplaceChild("tws1", CubeListBuilder.create().texOffs(116, 34).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-10.5407F, 0F, 10.6721F, 0F, 0F, 0F));
		ring.addOrReplaceChild("tws2", CubeListBuilder.create().texOffs(124, 34).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(-10.6805F, 0F, -10.5322F, 0F, 0F, 0F));
		ring.addOrReplaceChild("tws3", CubeListBuilder.create().texOffs(132, 34).addBox(-1F, -1F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(10.5237F, 0F, -10.6889F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 256, 256);
	}
}
