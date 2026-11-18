// 자동 생성 파일 (tools/bossgen) - 모양을 바꾸려면 생성기 spec을 수정하세요. setupAnim 은 spec의 애니메이션 코드에서 옵니다.
package com.elementalweapons.client.boss;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;


public class VoltraModel extends EntityModel<VoltraRenderState> {
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart right_arm;
	private final ModelPart left_arm;
	private final ModelPart halo;
	private final ModelPart tail1;
	private final ModelPart tail2;
	private final ModelPart tail3;
	private final ModelPart tail4;

	public VoltraModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.head = root.getChild("body").getChild("head");
		this.right_arm = root.getChild("body").getChild("right_arm");
		this.left_arm = root.getChild("body").getChild("left_arm");
		this.halo = root.getChild("body").getChild("halo");
		this.tail1 = root.getChild("body").getChild("tail1");
		this.tail2 = root.getChild("body").getChild("tail1").getChild("tail2");
		this.tail3 = root.getChild("body").getChild("tail1").getChild("tail2").getChild("tail3");
		this.tail4 = root.getChild("body").getChild("tail1").getChild("tail2").getChild("tail3").getChild("tail4");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6F, -6F, -3.5F, 12F, 10F, 7F).texOffs(96, 17).addBox(-4.5F, 4F, -2.5F, 9F, 4F, 5F).texOffs(86, 28).addBox(-2F, -4F, -4.5F, 4F, 4F, 1F), PartPose.offsetAndRotation(0F, 1F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(38, 0).addBox(-4F, -8F, -4F, 8F, 8F, 8F).texOffs(72, 28).addBox(-3F, -3F, -5F, 6F, 5F, 1F).texOffs(0, 37).addBox(-2F, 2F, -5F, 4F, 3F, 1F).texOffs(96, 28).addBox(-3.5F, -11F, -1F, 1F, 3F, 2F).texOffs(52, 28).addBox(-0.5F, -13F, -1F, 1F, 5F, 2F).texOffs(102, 28).addBox(2.5F, -11F, -1F, 1F, 3F, 2F).texOffs(10, 37).addBox(-6F, -9F, -0.5F, 2F, 1F, 1F).texOffs(108, 28).addBox(-7F, -13F, -0.5F, 1F, 4F, 1F).texOffs(16, 37).addBox(4F, -9F, -0.5F, 2F, 1F, 1F).texOffs(112, 28).addBox(6F, -13F, -0.5F, 1F, 4F, 1F), PartPose.offsetAndRotation(0F, -6F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(102, 0).addBox(-4F, -3F, -3.5F, 6F, 4F, 7F).texOffs(70, 0).addBox(-3F, 0F, -2F, 4F, 10F, 4F).texOffs(26, 17).addBox(-3.5F, 7F, -2.5F, 5F, 5F, 5F), PartPose.offsetAndRotation(-7F, -4F, 0F, 0F, 0F, 0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 17).addBox(-2F, -3F, -3.5F, 6F, 4F, 7F).texOffs(86, 0).addBox(-1F, 0F, -2F, 4F, 10F, 4F).texOffs(46, 17).addBox(-1.5F, 7F, -2.5F, 5F, 5F, 5F), PartPose.offsetAndRotation(7F, -4F, 0F, 0F, 0F, 0F));
		PartDefinition halo = body.addOrReplaceChild("halo", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -4F, 5F, 0F, 0F, 0F));
		halo.addOrReplaceChild("ray0", CubeListBuilder.create().texOffs(124, 17).addBox(-0.5F, -15F, -0.5F, 1F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
		halo.addOrReplaceChild("ray1", CubeListBuilder.create().texOffs(0, 28).addBox(-0.5F, -15F, -0.5F, 1F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0.7854F));
		halo.addOrReplaceChild("ray2", CubeListBuilder.create().texOffs(4, 28).addBox(-0.5F, -15F, -0.5F, 1F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 1.5708F));
		halo.addOrReplaceChild("ray3", CubeListBuilder.create().texOffs(8, 28).addBox(-0.5F, -15F, -0.5F, 1F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 2.3562F));
		halo.addOrReplaceChild("ray4", CubeListBuilder.create().texOffs(12, 28).addBox(-0.5F, -15F, -0.5F, 1F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 3.1416F));
		halo.addOrReplaceChild("ray5", CubeListBuilder.create().texOffs(16, 28).addBox(-0.5F, -15F, -0.5F, 1F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 3.927F));
		halo.addOrReplaceChild("ray6", CubeListBuilder.create().texOffs(20, 28).addBox(-0.5F, -15F, -0.5F, 1F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 4.7124F));
		halo.addOrReplaceChild("ray7", CubeListBuilder.create().texOffs(24, 28).addBox(-0.5F, -15F, -0.5F, 1F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 5.4978F));
		PartDefinition tail1 = body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(66, 17).addBox(-4.5F, 0F, -3F, 9F, 4F, 6F), PartPose.offsetAndRotation(0F, 8F, 0F, 0F, 0F, 0F));
		PartDefinition tail2 = tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(28, 28).addBox(-3.5F, 0F, -2.5F, 7F, 4F, 5F), PartPose.offsetAndRotation(0F, 4F, 0F, 0F, 0F, 0F));
		PartDefinition tail3 = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(58, 28).addBox(-2F, 0F, -1.5F, 4F, 4F, 3F), PartPose.offsetAndRotation(0F, 4F, 0F, 0F, 0F, 0F));
		tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(116, 28).addBox(-1F, 0F, -1F, 2F, 3F, 2F), PartPose.offsetAndRotation(0F, 4F, 0F, 0F, 0F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}

	@Override
	public void setupAnim(VoltraRenderState state) {
		super.setupAnim(state);
		float t = state.ageInTicks;
		// 둥둥 떠 있는 몸
		this.body.y += Mth.sin(t * 0.1F) * 1.0F;
		// 머리는 바라보는 방향으로
		this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
		this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
		// 등 뒤 후광 회전
		this.halo.zRot = t * 0.03F;
		// 번개 구름 꼬리 흔들림
		this.tail1.zRot = Mth.sin(t * 0.08F) * 0.06F;
		this.tail2.zRot = Mth.sin(t * 0.08F + 0.8F) * 0.10F;
		this.tail3.zRot = Mth.sin(t * 0.08F + 1.6F) * 0.14F;
		this.tail4.zRot = Mth.sin(t * 0.08F + 2.4F) * 0.22F;
		this.tail1.xRot = Mth.sin(t * 0.06F) * 0.05F;
		// 기본 팔 자세 (살짝 벌림)
		float sway = Mth.sin(t * 0.06F) * 0.05F;
		this.right_arm.zRot = 0.18F + sway;
		this.left_arm.zRot = -0.18F - sway;
		this.right_arm.xRot = sway;
		this.left_arm.xRot = -sway;
		// 공격 자세
		switch (state.attackPose) {
			case 1 -> { // 시전: 두 팔을 하늘로
				this.right_arm.xRot = -2.7F; this.right_arm.zRot = 0.35F;
				this.left_arm.xRot = -2.7F; this.left_arm.zRot = -0.35F;
				this.halo.zRot = t * 0.15F;
			}
			case 2 -> { // 돌진: 몸을 숙이고 팔을 뒤로
				this.body.xRot = 0.55F;
				this.right_arm.xRot = 0.9F; this.left_arm.xRot = 0.9F;
			}
			case 3 -> { // 방전 준비: 팔을 활짝
				this.right_arm.zRot = 1.35F; this.left_arm.zRot = -1.35F;
				this.halo.zRot = t * 0.25F;
			}
			default -> { }
		}
	}
}
