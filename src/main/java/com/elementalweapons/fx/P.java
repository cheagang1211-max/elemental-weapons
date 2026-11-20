package com.elementalweapons.fx;

import static com.elementalweapons.fx.FxOptions.*;

/** 바닐라 입자 대신 쓰는 손그림 연출 입자 모음 */
public final class P {
	private P() {}

	public static final FxOptions END_ROD = of(STAR, 0xFFF6D8, 0.13F, 14).spin(6F);
	public static final FxOptions ELECTRIC_SPARK = of(SPARK, 0xBFF6FF, 0.16F, 7);
	public static final FxOptions CLOUD = of(SMOKE, 0xF2F6FF, 0.32F, 16).grow(1.8F);
	public static final FxOptions SQUID_INK = of(SMOKE, 0x24102F, 0.3F, 16).grow(1.6F);
	public static final FxOptions SPLASH = of(DROP, 0x7FD0FF, 0.12F, 12).gravity(0.04F);
	public static final FxOptions FLAME = of(FxOptions.FLAME, 0xFF8A2A, 0.2F, 12).gravity(-0.01F);
	public static final FxOptions SWEEP_ATTACK = of(SLASH, 0xFFFFFF, 0.9F, 6);
	public static final FxOptions REVERSE_PORTAL = of(SPARK, 0xB46CFF, 0.12F, 12).spin(10F);
	public static final FxOptions LAVA = of(FxOptions.FLAME, 0xFF4A10, 0.26F, 14).gravity(0.03F);
	public static final FxOptions FALLING_WATER = of(DROP, 0x5AB0FF, 0.12F, 16).gravity(0.06F);
	public static final FxOptions CHERRY_LEAVES = of(PETAL, 0xFFB7D5, 0.16F, 30).spin(9F).gravity(0.012F);
	public static final FxOptions SOUL = of(FxOptions.FLAME, 0x7FF7FF, 0.18F, 16).gravity(-0.02F);
	public static final FxOptions LARGE_SMOKE = of(SMOKE, 0x6A6A70, 0.45F, 20).grow(1.8F);
	public static final FxOptions WHITE_ASH = of(SMOKE, 0xE6E6EE, 0.1F, 20);
	public static final FxOptions SMALL_FLAME = of(FxOptions.FLAME, 0xFFB050, 0.12F, 10).gravity(-0.01F);
	public static final FxOptions PORTAL = of(SPARK, 0x9A5CFF, 0.12F, 14);
	public static final FxOptions HAPPY_VILLAGER = of(SPARK, 0x8CFF7A, 0.14F, 12);
	public static final FxOptions GUST = of(SWIRL, 0xF2FFFA, 0.8F, 12).grow(1.6F).spin(-14F);
	public static final FxOptions EXPLOSION = of(STAR, 0xFFF4D0, 1.1F, 8).grow(1.8F).spin(4F);
	public static final FxOptions DRIPPING_WATER = of(DROP, 0x5AB0FF, 0.1F, 14).gravity(0.04F);
}
