package com.elementalweapons.skill;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.growth.Evolution;

/** 스킬·속성 진화 통신 */
public final class SkillNet {
	private SkillNet() {}

	public record Cast(int slot) implements CustomPacketPayload {
		public static final Type<Cast> TYPE = new Type<>(ElementalWeaponsMod.id("skill_cast"));
		public static final StreamCodec<ByteBuf, Cast> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, Cast::slot, Cast::new);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	/** 좌클릭 휘두름 (콤보) */
	public record Swing() implements CustomPacketPayload {
		public static final Type<Swing> TYPE = new Type<>(ElementalWeaponsMod.id("combo_swing"));
		public static final StreamCodec<ByteBuf, Swing> CODEC = StreamCodec.unit(new Swing());
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	public record Cooldown(int slot, int ticks) implements CustomPacketPayload {
		public static final Type<Cooldown> TYPE = new Type<>(ElementalWeaponsMod.id("skill_cooldown"));
		public static final StreamCodec<ByteBuf, Cooldown> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, Cooldown::slot, ByteBufCodecs.VAR_INT, Cooldown::ticks, Cooldown::new);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	public record OpenEvolve() implements CustomPacketPayload {
		public static final OpenEvolve INSTANCE = new OpenEvolve();
		public static final Type<OpenEvolve> TYPE = new Type<>(ElementalWeaponsMod.id("open_evolve"));
		public static final StreamCodec<ByteBuf, OpenEvolve> CODEC = StreamCodec.unit(INSTANCE);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	public record ChooseElement(int cls, int element) implements CustomPacketPayload {
		public static final Type<ChooseElement> TYPE = new Type<>(ElementalWeaponsMod.id("choose_element"));
		public static final StreamCodec<ByteBuf, ChooseElement> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, ChooseElement::cls, ByteBufCodecs.VAR_INT, ChooseElement::element, ChooseElement::new);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	public static void initialize() {
		PayloadTypeRegistry.serverboundPlay().register(Cast.TYPE, Cast.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(Swing.TYPE, Swing.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(Cooldown.TYPE, Cooldown.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(OpenEvolve.TYPE, OpenEvolve.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(ChooseElement.TYPE, ChooseElement.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(Cast.TYPE, (p, ctx) -> Skills.cast(ctx.player(), p.slot()));
		ServerPlayNetworking.registerGlobalReceiver(Swing.TYPE, (p, ctx) -> Combo.swing(ctx.player()));
		ServerPlayNetworking.registerGlobalReceiver(ChooseElement.TYPE, (p, ctx) -> Evolution.choose(ctx.player(), p.cls(), p.element()));
		ServerPlayConnectionEvents.DISCONNECT.register((h, server) -> { Skills.forget(h.getPlayer().getUUID()); Combo.forget(h.getPlayer().getUUID()); });
	}

	public static void sendCooldown(ServerPlayer p, int slot, int ticks) {
		if (ServerPlayNetworking.canSend(p, Cooldown.TYPE)) ServerPlayNetworking.send(p, new Cooldown(slot, ticks));
	}

	public static void openEvolve(ServerPlayer p) {
		if (ServerPlayNetworking.canSend(p, OpenEvolve.TYPE)) ServerPlayNetworking.send(p, OpenEvolve.INSTANCE);
	}
}
