package com.elementalweapons.growth;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import com.elementalweapons.ElementalWeaponsMod;

/** 클라이언트 → 서버: 스탯 찍기 / 초기화 */
public final class GrowthPayloads {
	private GrowthPayloads() {}

	public record Spend(int stat, int amount) implements CustomPacketPayload {
		public static final Type<Spend> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "growth_spend"));
		public static final StreamCodec<ByteBuf, Spend> CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, Spend::stat, ByteBufCodecs.VAR_INT, Spend::amount, Spend::new);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	public record Reset() implements CustomPacketPayload {
		public static final Reset INSTANCE = new Reset();
		public static final Type<Reset> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "growth_reset"));
		public static final StreamCodec<ByteBuf, Reset> CODEC = StreamCodec.unit(INSTANCE);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}
}
