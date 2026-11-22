package com.elementalweapons.growth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.spell.Fx;
import com.elementalweapons.world.sky.Capital;

/** B키 귀환: 어디에 있든 1초(20틱) 뒤 천공성 1층 승강기 앞으로 */
public final class Recall {
	private Recall() {}

	private static final Map<UUID, Integer> PENDING = new HashMap<>();

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (Iterator<Map.Entry<UUID, Integer>> it = PENDING.entrySet().iterator(); it.hasNext(); ) {
				var e = it.next();
				ServerPlayer p = server.getPlayerList().getPlayer(e.getKey());
				if (p == null || !p.isAlive()) { it.remove(); continue; }
				int left = e.getValue() - 1;
				if (left > 0) {
					e.setValue(left);
					if (left % 4 == 0) Fx.burst((ServerLevel) p.level(), Fx.dust(0x9FE8FF, 1.2F), p.position().add(0, 1, 0), 8, 0.5, 0.05);
					continue;
				}
				it.remove();
				go(p);
			}
		});
	}

	public static void request(ServerPlayer p) {
		if (PENDING.containsKey(p.getUUID()) || !p.isAlive()) return;
		PENDING.put(p.getUUID(), 20);
		p.sendOverlayMessage(Component.literal("✦ 1초 뒤 천공성 1층 승강기 앞으로 귀환합니다").withStyle(ChatFormatting.AQUA));
		Fx.sound((ServerLevel) p.level(), p.position(), SoundEvents.BEACON_ACTIVATE, 0.8F, 1.6F);
	}

	private static void go(ServerPlayer p) {
		ServerLevel home = p.level().getServer().overworld();
		if (p.isPassenger()) p.stopRiding();
		double x = 0.5, y = Capital.FY[0] + 1, z = Capital.SHAFT_R + 3.5;   // 승강기 빛 기둥 바로 남쪽, 기둥을 바라봄
		if (p.level() != home) { p.sendOverlayMessage(Component.literal("이곳에서는 귀환할 수 없습니다").withStyle(ChatFormatting.RED)); return; }
		p.teleportTo(x, y, z);
		p.setYRot(180F); p.setYHeadRot(180F);
		p.setDeltaMovement(Vec3.ZERO); p.resetFallDistance();
		Fx.sound(home, p.position(), SoundEvents.ENDERMAN_TELEPORT, 0.8F, 1.2F);
		Fx.burst(home, Fx.dust(0x9FE8FF, 1.4F), p.position().add(0, 1, 0), 30, 0.6, 0.08);
	}
}
