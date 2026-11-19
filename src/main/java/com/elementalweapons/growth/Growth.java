package com.elementalweapons.growth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** 플레이어의 원소 성장 데이터 (레벨·경험치·스탯) */
public record Growth(int level, long xp, int points, int str, int mag, int vit, int agi) {
	public static final int MAX_LEVEL = 100;
	public static final int MAX_STAT = 50;
	public static final int POINTS_PER_LEVEL = 3;
	public static final Growth DEFAULT = new Growth(1, 0, 0, 0, 0, 0, 0);

	public static final Codec<Growth> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.INT.fieldOf("level").forGetter(Growth::level),
			Codec.LONG.fieldOf("xp").forGetter(Growth::xp),
			Codec.INT.fieldOf("points").forGetter(Growth::points),
			Codec.INT.fieldOf("str").forGetter(Growth::str),
			Codec.INT.fieldOf("mag").forGetter(Growth::mag),
			Codec.INT.fieldOf("vit").forGetter(Growth::vit),
			Codec.INT.fieldOf("agi").forGetter(Growth::agi)).apply(i, Growth::new));

	public static final StreamCodec<ByteBuf, Growth> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, Growth::level,
			ByteBufCodecs.VAR_LONG, Growth::xp,
			ByteBufCodecs.VAR_INT, Growth::points,
			ByteBufCodecs.VAR_INT, Growth::str,
			ByteBufCodecs.VAR_INT, Growth::mag,
			ByteBufCodecs.VAR_INT, Growth::vit,
			ByteBufCodecs.VAR_INT, Growth::agi,
			Growth::new);

	/** 다음 레벨까지 필요한 경험치 */
	public static long need(int level) {
		return Math.round(4 * Math.pow(level, 1.5) + 96);
	}

	public int stat(int i) {
		return switch (i) { case 0 -> str; case 1 -> mag; case 2 -> vit; default -> agi; };
	}

	public Growth withStat(int i, int v) {
		return switch (i) {
			case 0 -> new Growth(level, xp, points, v, mag, vit, agi);
			case 1 -> new Growth(level, xp, points, str, v, vit, agi);
			case 2 -> new Growth(level, xp, points, str, mag, v, agi);
			default -> new Growth(level, xp, points, str, mag, vit, v);
		};
	}

	public Growth withPoints(int p) { return new Growth(level, xp, p, str, mag, vit, agi); }

	// ----- 스탯 효과 -----
	public double meleeBossBonus() { return 1.0 + 0.03 * str; }
	public double staffBossBonus() { return 1.0 + 0.03 * mag; }
	public double staffCooldownFactor() { return 1.0 - 0.006 * mag; }
	public double bonusHealth() { return vit; }
	public double speedBonus() { return 0.004 * agi; }
	public double attackSpeedBonus() { return 0.006 * agi; }
	public double attackDamageBonus() { return 0.2 * str; }

	private static final String[] TITLES = {"견습 원소술사", "원소 탐험가", "원소 전사", "원소 기사", "원소 마스터",
			"원소 영웅", "원소 대현자", "원소 군주", "원소 반신", "원소 신화", "원소의 지배자"};

	public String title() { return TITLES[Math.min(10, level / 10)]; }
}
