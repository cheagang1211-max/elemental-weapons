package com.elementalweapons.world.sky;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.spell.Fx;

/**
 * 6층 영웅의 투기장 결투.
 *  · 투기장 안에서 상대를 보고 Shift + 우클릭 → 결투 신청, 상대도 20초 안에 Shift + 우클릭 → 시작
 *  · 양쪽 끝으로 이동 → 3초 카운트다운 → 결투 (서로에게만 피해, 다른 사람 공격은 막힘)
 *  · 체력이 다 닳으면 죽지 않고 패배 처리, 둘 다 회복. 경기장 밖으로 나가거나 접속을 끊으면 기권, 3분이 지나면 무승부
 *  · 승리 기록은 세계에 저장, 투기장 북쪽 '명예의 전당' 전광판에 상위 5명
 */
public final class ArenaDuel {
	private ArenaDuel() {}

	private record Request(UUID from, UUID to, long until) {}
	private static final class Duel {
		final UUID a, b; final long start; int countdown = 60; boolean over;
		Duel(UUID a, UUID b, long start) { this.a = a; this.b = b; this.start = start; }
		UUID other(UUID u) { return u.equals(a) ? b : a; }
	}

	private static final Map<UUID, Request> REQUESTS = new HashMap<>();
	private static final Map<UUID, Duel> DUELS = new HashMap<>();
	private static final int TIME_LIMIT = 20 * 180;

	public static final AttachmentType<Map<String, Integer>> WINS = AttachmentRegistry.<Map<String, Integer>>builder()
			.persistent(com.mojang.serialization.Codec.unboundedMap(com.mojang.serialization.Codec.STRING, com.mojang.serialization.Codec.INT))
			.buildAndRegister(ElementalWeaponsMod.id("arena_wins"));
	private static boolean boardDirty = true;

	public static boolean inArena(Entity p) {
		double r = Math.hypot(p.getX(), p.getZ());
		return r < 34 && p.getY() > Annex.Y6 - 1 && p.getY() < Annex.Y6 + 30;
	}
	private static boolean onField(Entity p) {
		double r = Math.hypot(p.getX(), p.getZ());
		return r < 21 && p.getY() > Annex.Y6 - 1 && p.getY() < Annex.Y6 + 16;
	}

	public static boolean dueling(Player p) { Duel d = DUELS.get(p.getUUID()); return d != null && !d.over; }
	/** 둘이 지금 서로 결투 중인지 (카운트다운이 끝난 뒤) */
	public static boolean opponents(Player x, Player y) {
		Duel d = DUELS.get(x.getUUID());
		return d != null && !d.over && d.countdown <= 0 && d.other(x.getUUID()).equals(y.getUUID());
	}

	public static void initialize() {
		UseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
			if (world.isClientSide() || hand != net.minecraft.world.InteractionHand.MAIN_HAND) return InteractionResult.PASS;
			if (!(player instanceof ServerPlayer p) || !(entity instanceof ServerPlayer q) || !p.isShiftKeyDown()) return InteractionResult.PASS;
			if (!inArena(p) || !inArena(q)) return InteractionResult.PASS;
			request(p, q);
			return InteractionResult.SUCCESS;
		});
		// 결투 중에는 상대 말고 다른 사람에게 맞지 않고, 상대만 때릴 수 있음
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (!(entity instanceof ServerPlayer v)) return true;
			Entity atk = source.getEntity();
			if (atk == v) return !dueling(v);   // 결투 중 자기 기술에 맞지 않음
			if (!(atk instanceof Player ap)) return true;
			if (dueling(v) || dueling(ap)) return opponents(ap, v);
			return true;
		});
		// 쓰러지면 죽지 않고 패배
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
			if (!(entity instanceof ServerPlayer v) || !dueling(v)) return true;
			Duel d = DUELS.get(v.getUUID());
			v.setHealth(1);
			finish(v.level().getServer(), d, d.other(v.getUUID()), "상대를 쓰러뜨렸습니다");
			return false;
		});
		ServerPlayConnectionEvents.DISCONNECT.register((h, server) -> server.execute(() -> {
			Duel d = DUELS.get(h.getPlayer().getUUID());
			if (d != null && !d.over) finish(server, d, d.other(h.getPlayer().getUUID()), "상대가 접속을 끊었습니다");
		}));
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			try { tick(server); } catch (Throwable t) { ElementalWeaponsMod.LOGGER.error("[ARENA] 오류", t); }
		});
	}

	private static void title(ServerPlayer p, String big, ChatFormatting c, String sub) {
		p.connection.send(new ClientboundSetTitlesAnimationPacket(3, 30, 8));
		p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(sub).withStyle(ChatFormatting.GRAY)));
		p.connection.send(new ClientboundSetTitleTextPacket(Component.literal(big).withStyle(c, ChatFormatting.BOLD)));
	}

	private static void request(ServerPlayer p, ServerPlayer q) {
		if (dueling(p) || dueling(q)) { p.sendOverlayMessage(Component.literal("이미 결투 중입니다").withStyle(ChatFormatting.RED)); return; }
		long now = p.level().getGameTime();
		Request back = REQUESTS.get(q.getUUID());
		if (back != null && back.to.equals(p.getUUID()) && back.until > now) {   // 수락
			REQUESTS.remove(q.getUUID()); REQUESTS.remove(p.getUUID());
			start(p.level().getServer(), q, p);
			return;
		}
		REQUESTS.put(p.getUUID(), new Request(p.getUUID(), q.getUUID(), now + 400));
		p.sendSystemMessage(Component.literal("⚔ " + q.getName().getString() + "님에게 결투를 신청했습니다 (20초 안에 상대가 수락해야 함)").withStyle(ChatFormatting.GOLD));
		q.sendSystemMessage(Component.literal("⚔ " + p.getName().getString() + "님이 결투를 신청했습니다! 수락하려면 20초 안에 그 사람을 보고 Shift + 우클릭").withStyle(ChatFormatting.GOLD));
		title(q, "⚔ 결투 신청", ChatFormatting.GOLD, p.getName().getString() + " · Shift + 우클릭으로 수락");
		Fx.sound((ServerLevel) q.level(), q.position(), SoundEvents.BELL_BLOCK, 1F, 1.2F);
	}

	private static void start(MinecraftServer server, ServerPlayer a, ServerPlayer b) {
		Duel d = new Duel(a.getUUID(), b.getUUID(), a.level().getGameTime());
		DUELS.put(a.getUUID(), d); DUELS.put(b.getUUID(), d);
		ready(a, -12, 0F); ready(b, 12, 180F);
		broadcast(server, Component.literal("⚔ 영웅의 투기장: " + a.getName().getString() + " vs " + b.getName().getString() + " 결투 시작!").withStyle(ChatFormatting.GOLD));
	}

	private static void ready(ServerPlayer p, double z, float yaw) {
		p.teleportTo(0.5, Annex.Y6 + 1, z + 0.5);
		p.setYRot(yaw); p.setYHeadRot(yaw);
		p.setHealth(p.getMaxHealth());
		p.removeEffect(MobEffects.POISON); p.removeEffect(MobEffects.WITHER); p.removeEffect(MobEffects.SLOWNESS); p.removeEffect(MobEffects.WEAKNESS);
		p.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 9, false, false));
	}

	private static void tick(MinecraftServer server) {
		ServerLevel ow = server.overworld();
		long now = ow.getGameTime();
		REQUESTS.values().removeIf(r -> r.until < now);
		for (Duel d : new java.util.HashSet<>(DUELS.values())) {
			if (d.over) continue;
			ServerPlayer a = server.getPlayerList().getPlayer(d.a), b = server.getPlayerList().getPlayer(d.b);
			if (a == null || b == null) { finish(server, d, a == null ? d.b : d.a, "상대가 사라졌습니다"); continue; }
			if (d.countdown > 0) {
				if (d.countdown % 20 == 0) {
					String n = String.valueOf(d.countdown / 20);
					for (ServerPlayer p : new ServerPlayer[]{a, b}) { title(p, n, ChatFormatting.YELLOW, "결투 준비"); Fx.sound(ow, p.position(), SoundEvents.NOTE_BLOCK_HAT.value(), 1F, 1F); }
				}
				d.countdown--;
				if (d.countdown == 0) for (ServerPlayer p : new ServerPlayer[]{a, b}) { title(p, "시작!", ChatFormatting.RED, "상대의 체력을 모두 깎으세요"); Fx.sound(ow, p.position(), SoundEvents.RAID_HORN.value(), 0.8F, 1.2F); }
				continue;
			}
			if (!onField(a)) { finish(server, d, d.b, a.getName().getString() + "님이 경기장을 벗어났습니다"); continue; }
			if (!onField(b)) { finish(server, d, d.a, b.getName().getString() + "님이 경기장을 벗어났습니다"); continue; }
			if (now - d.start > TIME_LIMIT + 60) { finish(server, d, null, "시간 초과"); continue; }
			if (now % 10 == 0) {   // 두 사람 머리 위 표시
				for (ServerPlayer p : new ServerPlayer[]{a, b}) Fx.at(ow, Fx.dust(p == a ? 0xFF5A3C : 0x3CA0FF, 1.4F), p.position().add(0, 2.4, 0));
				long left = (TIME_LIMIT + 60 - (now - d.start)) / 20;
				for (ServerPlayer p : new ServerPlayer[]{a, b}) {
					ServerPlayer o = p == a ? b : a;
					p.sendOverlayMessage(Component.literal(String.format("⚔ 결투 · 상대 %s ❤ %.0f / %.0f · 남은 시간 %d초", o.getName().getString(), o.getHealth(), o.getMaxHealth(), left)).withStyle(ChatFormatting.GOLD));
				}
			}
		}
		if (now % 100 == 17 && Annex.done) board(server, ow);
	}

	private static void finish(MinecraftServer server, Duel d, UUID winner, String why) {
		if (d.over) return;
		d.over = true;
		DUELS.remove(d.a); DUELS.remove(d.b);
		ServerPlayer a = server.getPlayerList().getPlayer(d.a), b = server.getPlayerList().getPlayer(d.b);
		ServerPlayer w = winner == null ? null : server.getPlayerList().getPlayer(winner);
		ServerPlayer l = winner == null ? null : server.getPlayerList().getPlayer(d.other(winner));
		ServerLevel ow = server.overworld();
		for (ServerPlayer p : new ServerPlayer[]{a, b}) {
			if (p == null) continue;
			p.setHealth(p.getMaxHealth());
			p.clearFire();
			p.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 60, 4, false, false));
		}
		if (w == null) {
			for (ServerPlayer p : new ServerPlayer[]{a, b}) if (p != null) title(p, "무승부", ChatFormatting.GRAY, why);
			broadcast(server, Component.literal("⚔ 투기장 결투가 무승부로 끝났습니다 (" + why + ")").withStyle(ChatFormatting.GRAY));
			return;
		}
		Map<String, Integer> wins = new LinkedHashMap<>(ow.getAttachedOrElse(WINS, Map.of()));
		int n = wins.merge(w.getName().getString(), 1, Integer::sum);
		ow.setAttached(WINS, wins);
		boardDirty = true;
		title(w, "승리!", ChatFormatting.GOLD, why + " · 통산 " + n + "승");
		if (l != null) title(l, "패배", ChatFormatting.RED, why);
		Fx.sound(ow, w.position(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1F, 1F);
		Fx.burst(ow, com.elementalweapons.fx.P.END_ROD, w.position().add(0, 1, 0), 40, 1.0, 0.4);
		broadcast(server, Component.literal("⚔ 영웅의 투기장: " + w.getName().getString() + "님이 " + (l != null ? l.getName().getString() + "님을 " : "") + "꺾고 승리했습니다! (통산 " + n + "승)").withStyle(ChatFormatting.GOLD));
	}

	private static void broadcast(MinecraftServer server, Component c) {
		for (ServerPlayer p : server.getPlayerList().getPlayers()) p.sendSystemMessage(c);
	}

	/** 명예의 전당 전광판 (투기장 북쪽 입구 위) */
	private static void board(MinecraftServer server, ServerLevel ow) {
		BlockPos at = new BlockPos(0, Annex.Y6 + 9, -31);
		if (!ow.isLoaded(at)) return;
		var old = ow.getEntities((Entity) null, new AABB(at).inflate(3), e -> e.entityTags().contains("ew_arena_board"));
		if (!boardDirty && !old.isEmpty()) return;
		for (Entity e : old) e.discard();
		boardDirty = false;
		Map<String, Integer> wins = ow.getAttachedOrElse(WINS, Map.of());
		StringBuilder sb = new StringBuilder();
		var top = wins.entrySet().stream().sorted((x, y) -> y.getValue() - x.getValue()).limit(5).toList();
		if (top.isEmpty()) sb.append("아직 기록이 없습니다\\nShift + 우클릭으로 결투 신청");
		for (int i = 0; i < top.size(); i++) sb.append(i == 0 ? "" : "\\n").append(i + 1).append(". ").append(top.get(i).getKey().replace("\"", "")).append(" — ").append(top.get(i).getValue()).append("승");
		BossGate.label(server, ow, at, "ew_arena_board", "★ 명예의 전당", "#FFD34D", sb.toString());
	}
}
