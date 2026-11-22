package com.elementalweapons.growth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** 플레이어의 원소 성장 데이터 (레벨·경험치·스탯) */
public record Growth(int level, long xp, int points, int str, int mag, int vit, int agi, int cls) {
	/** 최대 레벨 (이후는 초월) */
	public static final int MAX_LEVEL = 200;
	public static final int MAX_STAT = 100;
	public static final int POINTS_PER_LEVEL = 2;
	/** 그 레벨까지 받는 스탯 포인트 합계: 최대 레벨(200)에 정확히 4스탯 × 100 = 400 — 남는 포인트 없음 */
	public static int totalPoints(int level) { return (int) ((long) (Math.min(level, MAX_LEVEL) - 1) * MAX_STAT * 4 / (MAX_LEVEL - 1)); }
	public static final Growth DEFAULT = new Growth(1, 0, 0, 0, 0, 0, 0, 0);

	public static final Codec<Growth> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.INT.fieldOf("level").forGetter(Growth::level),
			Codec.LONG.fieldOf("xp").forGetter(Growth::xp),
			Codec.INT.fieldOf("points").forGetter(Growth::points),
			Codec.INT.fieldOf("str").forGetter(Growth::str),
			Codec.INT.fieldOf("mag").forGetter(Growth::mag),
			Codec.INT.fieldOf("vit").forGetter(Growth::vit),
			Codec.INT.fieldOf("agi").forGetter(Growth::agi),
			Codec.INT.optionalFieldOf("cls", 0).forGetter(Growth::cls)).apply(i, Growth::new));

	public static final StreamCodec<ByteBuf, Growth> STREAM_CODEC = StreamCodec.of((buf, g) -> {
		ByteBufCodecs.VAR_INT.encode(buf, g.level()); ByteBufCodecs.VAR_LONG.encode(buf, g.xp()); ByteBufCodecs.VAR_INT.encode(buf, g.points());
		ByteBufCodecs.VAR_INT.encode(buf, g.str()); ByteBufCodecs.VAR_INT.encode(buf, g.mag()); ByteBufCodecs.VAR_INT.encode(buf, g.vit());
		ByteBufCodecs.VAR_INT.encode(buf, g.agi()); ByteBufCodecs.VAR_INT.encode(buf, g.cls());
	}, buf -> new Growth(ByteBufCodecs.VAR_INT.decode(buf), ByteBufCodecs.VAR_LONG.decode(buf), ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf), ByteBufCodecs.VAR_INT.decode(buf), ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf), ByteBufCodecs.VAR_INT.decode(buf)));

	/** 다음 레벨까지 필요한 경험치 */
	public static long need(int level) {
		return Math.round(3.5 * Math.pow(level, 1.5) + 90);
	}

	public int stat(int i) {
		return switch (i) { case 0 -> str; case 1 -> mag; case 2 -> vit; default -> agi; };
	}

	public Growth withStat(int i, int v) {
		return switch (i) {
			case 0 -> new Growth(level, xp, points, v, mag, vit, agi, cls);
			case 1 -> new Growth(level, xp, points, str, v, vit, agi, cls);
			case 2 -> new Growth(level, xp, points, str, mag, v, agi, cls);
			default -> new Growth(level, xp, points, str, mag, vit, v, cls);
		};
	}

	public Growth withPoints(int p) { return new Growth(level, xp, p, str, mag, vit, agi, cls); }

	// ----- 스탯 효과 -----
	public double meleeBossBonus() { return 1.0 + 0.03 * str; }
	public double staffBossBonus() { return 1.0 + 0.03 * mag; }
	public double staffCooldownFactor() { return 1.0 - 0.004 * mag; }
	public double bonusHealth() { return vit; }
	public double speedBonus() { return 0.002 * agi; }
	public double attackSpeedBonus() { return 0.004 * agi; }
	public double attackDamageBonus() { return 0.2 * str; }

	public com.elementalweapons.item.PlayerClass playerClass() { return com.elementalweapons.item.PlayerClass.of(cls); }

	public Growth withClass(int c) { return new Growth(level, xp, points, str, mag, vit, agi, c); }
}
