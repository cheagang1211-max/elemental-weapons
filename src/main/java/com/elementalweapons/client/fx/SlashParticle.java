package com.elementalweapons.client.fx;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.RandomSource;

/** 빛나는 베기 궤적 입자: 짧게 살고, 빠르게 옅어지며 줄어듦 */
public class SlashParticle extends SingleQuadParticle {
	private final float baseSize, baseAlpha;

	protected SlashParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites, ColorParticleOption o) {
		super(level, x, y, z, sprites.first());
		this.xd = dx; this.yd = dy; this.zd = dz;
		this.hasPhysics = false;
		this.gravity = 0;
		this.friction = 0.82F;
		setColor(o.getRed(), o.getGreen(), o.getBlue());
		this.baseSize = 0.04F + o.getAlpha() * 0.6F;
		this.quadSize = baseSize;
		this.baseAlpha = 0.95F;
		setAlpha(baseAlpha);
		setLifetime(5 + random.nextInt(4));
	}

	@Override
	public void tick() {
		super.tick();
		float t = (float) age / lifetime;
		setAlpha(baseAlpha * (1 - t * t));
		quadSize = baseSize * (1 - 0.6F * t);
	}

	@Override
	protected Layer getLayer() {
		return Layer.TRANSLUCENT;
	}

	@Override
	protected int getLightCoords(float partialTick) {
		return 0xF000F0;
	}

	public static class Provider implements ParticleProvider<ColorParticleOption> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) { this.sprites = sprites; }

		@Override
		public Particle createParticle(ColorParticleOption o, ClientLevel level, double x, double y, double z, double dx, double dy, double dz, RandomSource random) {
			return new SlashParticle(level, x, y, z, dx, dy, dz, sprites, o);
		}
	}
}
