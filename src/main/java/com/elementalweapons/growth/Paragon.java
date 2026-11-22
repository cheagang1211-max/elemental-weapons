package com.elementalweapons.growth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * 초월(최대 레벨 200 이후) 성장: 끝이 없는 초월 레벨과 상한 없는 초월 스탯, 균열 최고 단계
 *  파괴: 모든 피해 +1%/점   비전: 마법 피해 +1%/점, 쿨타임 감소(점점 줄어듦)
 *  불멸: 최대 체력 +1/점, 방어 강도(점점 줄어듦)   신속: 이동·공격 속도(점점 줄어듦)
 */
public record Paragon(int level, long xp, int points, int pow, int arc, int imm, int swf, int best) {
	public static final Paragon DEFAULT = new Paragon(0, 0, 0, 0, 0, 0, 0, 0);
	public static final String[] NAMES = {"파괴", "비전", "불멸", "신속"};

	public static final Codec<Paragon> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.INT.fieldOf("level").forGetter(Paragon::level),
			Codec.LONG.fieldOf("xp").forGetter(Paragon::xp),
			Codec.INT.fieldOf("points").forGetter(Paragon::points),
			Codec.INT.fieldOf("pow").forGetter(Paragon::pow),
			Codec.INT.fieldOf("arc").forGetter(Paragon::arc),
			Codec.INT.fieldOf("imm").forGetter(Paragon::imm),
			Codec.INT.fieldOf("swf").forGetter(Paragon::swf),
			Codec.INT.optionalFieldOf("best", 0).forGetter(Paragon::best)).apply(i, Paragon::new));

	public static final StreamCodec<ByteBuf, Paragon> STREAM_CODEC = StreamCodec.of((buf, g) -> {
		ByteBufCodecs.VAR_INT.encode(buf, g.level()); ByteBufCodecs.VAR_LONG.encode(buf, g.xp()); ByteBufCodecs.VAR_INT.encode(buf, g.points());
		ByteBufCodecs.VAR_INT.encode(buf, g.pow()); ByteBufCodecs.VAR_INT.encode(buf, g.arc()); ByteBufCodecs.VAR_INT.encode(buf, g.imm());
		ByteBufCodecs.VAR_INT.encode(buf, g.swf()); ByteBufCodecs.VAR_INT.encode(buf, g.best());
	}, buf -> new Paragon(ByteBufCodecs.VAR_INT.decode(buf), ByteBufCodecs.VAR_LONG.decode(buf), ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf), ByteBufCodecs.VAR_INT.decode(buf), ByteBufCodecs.VAR_INT.decode(buf),
			ByteBufCodecs.VAR_INT.decode(buf), ByteBufCodecs.VAR_INT.decode(buf)));

	/** 다음 초월 레벨까지 필요한 경험치 (계속 늘어남) */
	public static long need(int level) { return 20_000L + 2_500L * level + (long) (40 * Math.pow(level, 1.6)); }

	public int stat(int i) { return switch (i) { case 0 -> pow; case 1 -> arc; case 2 -> imm; default -> swf; }; }

	public Paragon withStat(int i, int v) {
		return switch (i) {
			case 0 -> new Paragon(level, xp, points, v, arc, imm, swf, best);
			case 1 -> new Paragon(level, xp, points, pow, v, imm, swf, best);
			case 2 -> new Paragon(level, xp, points, pow, arc, v, swf, best);
			default -> new Paragon(level, xp, points, pow, arc, imm, v, best);
		};
	}

	public Paragon withPoints(int p) { return new Paragon(level, xp, p, pow, arc, imm, swf, best); }
	public Paragon withBest(int b) { return new Paragon(level, xp, points, pow, arc, imm, swf, b); }

	/** 점점 줄어드는 효과 (최대치에 무한히 다가감) */
	private static double soft(int pts, double max, double k) { return max * (1 - Math.exp(-pts / k)); }

	public double damageMult() { return 1.0 + 0.01 * pow; }
	public double spellMult() { return 1.0 + 0.01 * arc + 0.01 * pow; }
	public double cooldownCut() { return soft(arc, 0.35, 150); }
	public double health() { return imm; }
	public double toughness() { return soft(imm, 16, 160); }
	public double speed() { return soft(swf, 0.3, 120); }
	public double attackSpeed() { return soft(swf, 0.5, 120); }
}
