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

	/** 서버 → 클라: 현자 창 열기 */
	public record OpenSage() implements CustomPacketPayload {
		public static final OpenSage INSTANCE = new OpenSage();
		public static final Type<OpenSage> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "open_sage"));
		public static final StreamCodec<ByteBuf, OpenSage> CODEC = StreamCodec.unit(INSTANCE);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	/** 클라 → 서버: 현자에게 직업·스탯 초기화 요청 */
	public record ResetClass() implements CustomPacketPayload {
		public static final ResetClass INSTANCE = new ResetClass();
		public static final Type<ResetClass> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "reset_class"));
		public static final StreamCodec<ByteBuf, ResetClass> CODEC = StreamCodec.unit(INSTANCE);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	public record ChooseClass(int cls) implements CustomPacketPayload {
		public static final Type<ChooseClass> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "choose_class"));
		public static final StreamCodec<ByteBuf, ChooseClass> CODEC = ByteBufCodecs.VAR_INT.map(ChooseClass::new, ChooseClass::cls);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	/** 초월 스탯 찍기 */
	public record SpendParagon(int stat, int amount) implements CustomPacketPayload {
		public static final Type<SpendParagon> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "paragon_spend"));
		public static final StreamCodec<ByteBuf, SpendParagon> CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, SpendParagon::stat, ByteBufCodecs.VAR_INT, SpendParagon::amount, SpendParagon::new);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	/** 튜토리얼 건너뛰기(false) / 처음부터(true) */
	public record TutorialSkip(boolean restart) implements CustomPacketPayload {
		public static final Type<TutorialSkip> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "tutorial_skip"));
		public static final StreamCodec<ByteBuf, TutorialSkip> CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, TutorialSkip::restart, TutorialSkip::new);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}
}
