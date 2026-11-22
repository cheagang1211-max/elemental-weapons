package com.elementalweapons.rift;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.growth.ModGrowth;
import com.elementalweapons.item.ModItems;

/** 균열의 열쇠 + 진행 중인 균열 관리 */
public final class RiftItems {
	private RiftItems() {}

	public static final List<Rift> ACTIVE = new ArrayList<>();

	public static final Item RIFT_KEY = ModItems.register(ResourceKey.create(Registries.ITEM, ElementalWeaponsMod.id("rift_key")), RiftKey::new,
			new Item.Properties().rarity(Rarity.EPIC).stacksTo(16));

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (Rift r : new ArrayList<>(ACTIVE)) {
				try { r.tick(); } catch (Throwable t) { ElementalWeaponsMod.LOGGER.error("[RIFT] 오류", t); r.fail("균열이 불안정해 닫혔습니다"); }
				if (r.finished) ACTIVE.remove(r);
			}
		});
		// 도전자가 모두 쓰러지면 균열 종료 + 열쇠 반환
		net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
			if (!(entity instanceof ServerPlayer dead)) return;
			for (Rift r : new ArrayList<>(ACTIVE)) {
				if (r.finished || !r.players.contains(dead.getUUID())) continue;
				boolean others = false;
				for (java.util.UUID id : r.players) {
					ServerPlayer q = r.level.getServer().getPlayerList().getPlayer(id);
					if (q != null && q != dead && q.isAlive() && q.level() == r.level && q.position().distanceToSqr(r.c) < 48 * 48) { others = true; break; }
				}
				if (others) continue;
				r.fail("도전자가 모두 쓰러졌습니다");
				if (r.opener != null) com.elementalweapons.boss.BossFight.refundLater(r.opener, new net.minecraft.world.item.ItemStack(RIFT_KEY));
			}
		});
	}

	/** 균열 열기: 기본은 (최고 기록 + 1)단계, 웅크리고 쓰면 최고 기록 단계 다시 도전 */
	public static boolean open(ServerPlayer p, boolean replay) {
		for (Rift r : ACTIVE) if (r.level == p.level() && r.c.distanceToSqr(p.position()) < 60 * 60) {
			p.sendOverlayMessage(Component.literal("근처에 이미 균열이 열려 있습니다.").withStyle(ChatFormatting.RED));
			return false;
		}
		int best = ModGrowth.paragon(p).best();
		int tier = replay ? Math.max(1, best) : best + 1;
		ServerLevel level = (ServerLevel) p.level();
		List<ServerPlayer> who = new ArrayList<>();
		for (ServerPlayer q : level.players()) if (q.distanceToSqr(p) < Rift.R * Rift.R) who.add(q);
		Rift rift = new Rift(level, p.position(), tier, who);
		if (!p.isCreative()) rift.opener = p.getUUID();
		ACTIVE.add(rift);
		return true;
	}

	public static class RiftKey extends Item {
		public RiftKey(Item.Properties props) { super(props); }

		@Override
		public InteractionResult use(Level level, Player user, InteractionHand hand) {
			if (level instanceof ServerLevel && user instanceof ServerPlayer sp) {
				if (!open(sp, sp.isShiftKeyDown())) return InteractionResult.FAIL;
				if (!sp.isCreative()) sp.getItemInHand(hand).shrink(1);
			}
			return InteractionResult.SUCCESS;
		}
	}
}
