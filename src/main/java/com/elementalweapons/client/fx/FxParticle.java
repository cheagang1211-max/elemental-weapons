package com.elementalweapons.client.fx;

import java.util.List;

import org.joml.Quaternionf;

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteSet;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;

import com.elementalweapons.fx.FxOptions;

/** 손그림 연출 입자: 4프레임 애니메이션, 크기 변화, 회전, 고정 방향(바닥 고리·베기 초승달) */
public class FxParticle extends SingleQuadParticle {
	private final List<TextureAtlasSprite> sprites;
	private final FxOptions o;
	private final float base;
	private final boolean oriented;
	private float angle;

	protected FxParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, List<TextureAtlasSprite> sprites, FxOptions o) {
		super(level, x, y, z, sprites.get(Math.min(sprites.size() - 1, o.kind() * 4)));
		this.sprites = sprites;
		this.o = o;
		this.xd = dx; this.yd = dy; this.zd = dz;
		this.hasPhysics = false;
		this.gravity = o.gravity();
		this.friction = 0.9F;
		this.base = o.size();
		this.quadSize = base;
		this.oriented = o.pitch() != FxOptions.BILLBOARD;
		this.angle = o.roll();
		this.roll = this.oRoll = (float) Math.toRadians(o.roll());
		int c = o.color();
		setColor(((c >> 16) & 255) / 255F, ((c >> 8) & 255) / 255F, (c & 255) / 255F);
		setLifetime(Math.max(2, o.life()));
	}

	@Override
	public void tick() {
		super.tick();
		float t = Math.min(1F, (float) age / lifetime);
		int frame = Math.min(3, (int) (t * 4));
		setSprite(sprites.get(Math.min(sprites.size() - 1, o.kind() * 4 + frame)));
		quadSize = base * (1F + (o.grow() - 1F) * t);
		setAlpha(t < 0.7F ? 1F : 1F - (t - 0.7F) / 0.3F);
		oRoll = roll;
		angle += o.spin();
		roll = (float) Math.toRadians(angle);
	}

	@Override
	public void extract(QuadParticleRenderState state, Camera camera, float partial) {
		if (!oriented) { super.extract(state, camera, partial); return; }
		float r = (float) Math.toRadians(angle + o.spin() * partial);
		Quaternionf q = new Quaternionf().rotationYXZ((float) Math.toRadians(-o.yaw()), (float) Math.toRadians(o.pitch()), r);
		extractRotatedQuad(state, camera, q, partial);
	}

	@Override
	protected Layer getLayer() { return Layer.TRANSLUCENT; }

	@Override
	protected int getLightCoords(float partial) { return 0xF000F0; }

	public static class Provider implements ParticleProvider<FxOptions> {
		private final FabricSpriteSet set;
		public Provider(FabricSpriteSet set) { this.set = set; }

		@Override
		public Particle createParticle(FxOptions o, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, RandomSource random) {
			return new FxParticle(level, x, y, z, dx, dy, dz, set.getSprites(), o);
		}
	}
}
