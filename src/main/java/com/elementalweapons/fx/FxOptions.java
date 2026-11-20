package com.elementalweapons.fx;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.StreamCodec;

/**
 * 손그림 연출 입자 하나의 설정.
 * kind: 그림 종류(Fx.ORB, SLASH ...), color: RGB, size: 크기(블록), grow: 사라질 때 크기 배율, life: 수명(틱),
 * spin: 회전 속도(도/틱), gravity: 중력, yaw/pitch/roll: 방향(도). pitch가 BILLBOARD 이면 항상 화면을 바라봄.
 */
public record FxOptions(int kind, int color, float size, float grow, int life, float spin, float gravity, float yaw, float pitch, float roll)
		implements ParticleOptions {
	public static final float BILLBOARD = 999F;
	public static final int ORB = 0, SLASH = 1, RING = 2, SPARK = 3, FLAME = 4, PETAL = 5, SWIRL = 6, BOLT = 7, SMOKE = 8, STAR = 9,
			DROP = 10, RUNE = 11, SHARD = 12, LEAF = 13;

	public static final MapCodec<FxOptions> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			Codec.INT.fieldOf("kind").forGetter(FxOptions::kind),
			Codec.INT.fieldOf("color").forGetter(FxOptions::color),
			Codec.FLOAT.fieldOf("size").forGetter(FxOptions::size),
			Codec.FLOAT.optionalFieldOf("grow", 1F).forGetter(FxOptions::grow),
			Codec.INT.optionalFieldOf("life", 12).forGetter(FxOptions::life),
			Codec.FLOAT.optionalFieldOf("spin", 0F).forGetter(FxOptions::spin),
			Codec.FLOAT.optionalFieldOf("gravity", 0F).forGetter(FxOptions::gravity),
			Codec.FLOAT.optionalFieldOf("yaw", 0F).forGetter(FxOptions::yaw),
			Codec.FLOAT.optionalFieldOf("pitch", BILLBOARD).forGetter(FxOptions::pitch),
			Codec.FLOAT.optionalFieldOf("roll", 0F).forGetter(FxOptions::roll)).apply(i, FxOptions::new));

	public static final StreamCodec<ByteBuf, FxOptions> STREAM = StreamCodec.of((buf, o) -> {
		buf.writeByte(o.kind); buf.writeInt(o.color); buf.writeFloat(o.size); buf.writeFloat(o.grow); buf.writeShort(o.life);
		buf.writeFloat(o.spin); buf.writeFloat(o.gravity); buf.writeFloat(o.yaw); buf.writeFloat(o.pitch); buf.writeFloat(o.roll);
	}, buf -> new FxOptions(buf.readByte(), buf.readInt(), buf.readFloat(), buf.readFloat(), buf.readShort(), buf.readFloat(), buf.readFloat(),
			buf.readFloat(), buf.readFloat(), buf.readFloat()));

	@Override
	public ParticleType<FxOptions> getType() { return ModParticles.FX; }

	// ---------- 편하게 만들기 ----------
	public static FxOptions of(int kind, int color, float size, int life) { return new FxOptions(kind, color, size, 1F, life, 0F, 0F, 0F, BILLBOARD, 0F); }
	public FxOptions grow(float g) { return new FxOptions(kind, color, size, g, life, spin, gravity, yaw, pitch, roll); }
	public FxOptions spin(float s) { return new FxOptions(kind, color, size, grow, life, s, gravity, yaw, pitch, roll); }
	public FxOptions gravity(float g) { return new FxOptions(kind, color, size, grow, life, spin, g, yaw, pitch, roll); }
	/** 고정 방향 (yaw: 수평 방향, pitch: 90이면 바닥에 눕힘, roll: 면 안에서 회전) */
	public FxOptions facing(float yaw, float pitch, float roll) { return new FxOptions(kind, color, size, grow, life, spin, gravity, yaw, pitch, roll); }
	public FxOptions flat() { return facing(0F, 90F, 0F); }
}
