package com.elementalweapons.world.sky;

import java.time.LocalDate;
import java.time.ZoneId;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.Enhancement;
import com.elementalweapons.item.ModItems;
import com.elementalweapons.spell.Fx;

/**
 * 7층 천상의 정상에서 할 수 있는 것
 *  · 강화의 제단 (돔 신전 안 북쪽): 우클릭 → 무기 강화 창
 *  · 천상의 종 (돔 신전 안 남쪽): 하루에 한 번 하사품 (강화석·원소 가루·속성 수정, 가끔 가호석·원소 정수)
 *  · 천상의 샘 (성문 문턱의 빛): 들어서면 체력·나쁜 효과 회복
 */
public final class Summit {
	private Summit() {}

	public static final BlockPos ALTAR = new BlockPos(0, Annex.Y7 + 1, -7);
	public static final BlockPos BELL = new BlockPos(0, Annex.Y7 + 1, 7);
	private static final int PATCH = 1;

	public static final AttachmentType<Integer> BUILT = AttachmentRegistry.<Integer>builder()
			.persistent(com.mojang.serialization.Codec.INT).buildAndRegister(ElementalWeaponsMod.id("summit_patch"));
	public static final AttachmentType<Long> DAILY = AttachmentRegistry.<Long>builder()
			.persistent(com.mojang.serialization.Codec.LONG).copyOnDeath().buildAndRegister(ElementalWeaponsMod.id("summit_daily"));

	public static void initialize() {
		Enhancement.NEAR_ALTAR = p -> p.level() == p.level().getServer().overworld() && p.position().distanceToSqr(Vec3.atCenterOf(ALTAR)) < 7 * 7;
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ServerLevel ow = server.overworld();
			if (!Annex.done || !ModSky.active(ow)) return;
			try {
				int t = server.getTickCount();
				if (t % 40 == 23) patch(ow);
				if (t % 100 == 41) labels(server, ow);
				if (t % 5 == 0) fx(ow, t);
				if (t % 10 == 3) spring(ow);
			} catch (Throwable e) { ElementalWeaponsMod.LOGGER.error("[SUMMIT] 오류", e); }
		});
		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			if (world.isClientSide() || !(player instanceof ServerPlayer p) || hand != net.minecraft.world.InteractionHand.MAIN_HAND) return InteractionResult.PASS;
			BlockPos b = hit.getBlockPos();
			if (near(b, ALTAR, 2, 3)) { ServerPlayNetworking.send(p, Enhancement.Open.INSTANCE); Fx.sound((ServerLevel) world, Vec3.atCenterOf(ALTAR), SoundEvents.SMITHING_TABLE_USE, 0.8F, 1.2F); return InteractionResult.SUCCESS; }
			if (near(b, BELL, 1, 3)) { daily(p); return InteractionResult.SUCCESS; }
			return InteractionResult.PASS;
		});
	}

	private static boolean near(BlockPos b, BlockPos c, int r, int up) {
		return Math.abs(b.getX() - c.getX()) <= r && Math.abs(b.getZ() - c.getZ()) <= r && b.getY() >= c.getY() - 1 && b.getY() <= c.getY() + up;
	}

	private static BlockState b(String id) { return Annex.Builder.b(id); }
	private static void set(ServerLevel l, int x, int y, int z, BlockState s) { l.setBlock(new BlockPos(x, y, z), s, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE); }

	private static void patch(ServerLevel ow) {
		Integer v = ow.getAttached(BUILT);
		if (v != null && v >= PATCH) return;
		if (!ow.isLoaded(ALTAR) || !ow.isLoaded(BELL)) return;
		int y = ALTAR.getY(), az = ALTAR.getZ();
		// 강화의 제단: 수정 받침 3×3 + 모루 + 네 모서리 금기둥과 빛 막대
		for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) set(ow, dx, y, az + dz, b(dx == 0 && dz == 0 ? "gold_block" : "chiseled_quartz_block"));
		set(ow, 0, y + 1, az, Annex.Builder.b("anvil", "facing", "east"));
		for (int dx = -1; dx <= 1; dx += 2) for (int dz = -1; dz <= 1; dz += 2) { set(ow, dx, y + 1, az + dz, b("gold_block")); set(ow, dx, y + 2, az + dz, b("end_rod")); }
		set(ow, 0, y + 3, az, b("pearlescent_froglight"));
		// 천상의 종
		int bz = BELL.getZ();
		for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) set(ow, dx, y, bz + dz, b(dx == 0 && dz == 0 ? "gold_block" : "smooth_quartz"));
		set(ow, 0, y + 1, bz, Annex.Builder.b("bell", "attachment", "floor", "facing", "north"));
		for (int dx = -1; dx <= 1; dx += 2) { set(ow, dx, y + 1, bz, b("quartz_pillar")); set(ow, dx, y + 2, bz, b("gold_block")); }
		set(ow, 0, y + 2, bz, b("air")); set(ow, -1, y + 3, bz, b("smooth_quartz_slab")); set(ow, 0, y + 3, bz, b("gold_block")); set(ow, 1, y + 3, bz, b("smooth_quartz_slab"));
		ow.setAttached(BUILT, PATCH);
		ElementalWeaponsMod.LOGGER.info("[SUMMIT] 강화의 제단 · 천상의 종을 놓았습니다");
	}

	private static void labels(MinecraftServer server, ServerLevel ow) {
		BossGate.label(server, ow, ALTAR.offset(0, 5, 0), "ew_label_forge", "⚒ 강화의 제단", "#FFD34D", "무기를 들고 우클릭: 강화 (+15까지)");
		BossGate.label(server, ow, BELL.offset(0, 5, 0), "ew_label_bell", "♪ 천상의 종", "#9FE7FF", "하루 한 번 우클릭: 천상의 하사품");
		BossGate.label(server, ow, new BlockPos(0, Annex.Y7 + 12, -27), "ew_label_spring", "✦ 천상의 샘", "#FFFFFF", "성문의 빛에 들어서면 회복");
	}

	private static void fx(ServerLevel ow, int t) {
		Vec3 a = Vec3.atCenterOf(ALTAR).add(0, 2.2, 0);
		if (ow.getNearestPlayer(a.x, a.y, a.z, 40, false) == null) return;
		for (int k = 0; k < 3; k++) {
			double ang = t * 0.08 + k * Math.PI * 2 / 3;
			Fx.at(ow, Fx.dust(0xFFD34D, 1.1F), a.add(Math.cos(ang) * 1.6, Math.sin(t * 0.05 + k) * 0.4, Math.sin(ang) * 1.6));
		}
	}

	/** 성문 문턱의 빛 = 천상의 샘 */
	private static void spring(ServerLevel ow) {
		for (ServerPlayer p : ow.players()) {
			if (Math.abs(p.getX()) > 4.6 || p.getZ() < -30 || p.getZ() > -24.5 || Math.abs(p.getY() - (Annex.Y7 + 1)) > 1.5) continue;
			boolean any = p.getHealth() < p.getMaxHealth() || p.getActiveEffects().stream().anyMatch(e -> !e.getEffect().value().isBeneficial());
			if (!any) continue;
			p.heal(4);
			p.getActiveEffects().stream().filter(e -> !e.getEffect().value().isBeneficial()).map(e -> e.getEffect()).toList().forEach(p::removeEffect);
			p.clearFire();
			Fx.burst(ow, Fx.dust(0xFFFFFF, 1.2F), p.position().add(0, 1, 0), 10, 0.5, 0.05);
			if (p.tickCount % 40 < 10) Fx.sound(ow, p.position(), SoundEvents.AMETHYST_BLOCK_CHIME, 0.6F, 1.6F);
		}
	}

	private static long today() { return LocalDate.now(ZoneId.of("Asia/Seoul")).toEpochDay(); }

	private static void daily(ServerPlayer p) {
		ServerLevel l = (ServerLevel) p.level();
		Long last = p.getAttached(DAILY);
		long d = today();
		Vec3 at = Vec3.atCenterOf(BELL).add(0, 1, 0);
		if (last != null && last >= d) {
			Fx.sound(l, at, SoundEvents.BELL_BLOCK, 0.5F, 0.7F);
			p.sendOverlayMessage(Component.literal("오늘의 하사품은 이미 받았습니다. 내일 다시 울려 주세요").withStyle(ChatFormatting.GRAY));
			return;
		}
		p.setAttached(DAILY, d);
		com.elementalweapons.growth.Achieve.grant(p, "explore/summit");
		var r = l.getRandom();
		Integer el = p.getAttached(com.elementalweapons.growth.Evolution.ELEMENT);
		Element e = el != null && el >= 0 ? Element.values()[el] : Element.values()[r.nextInt(7)];
		java.util.List<ItemStack> gifts = new java.util.ArrayList<>();
		gifts.add(new ItemStack(ModItems.ENHANCE_STONE, 3 + r.nextInt(3)));
		gifts.add(new ItemStack(Enhancement.dust(), 20));
		gifts.add(new ItemStack(Enhancement.crystalFor(e), 3 + r.nextInt(3)));
		if (r.nextFloat() < 0.3F) gifts.add(new ItemStack(ModItems.BLESS_STONE));
		if (r.nextFloat() < 0.25F) gifts.add(new ItemStack(ModItems.ELEMENT_ESSENCE));
		StringBuilder sb = new StringBuilder();
		for (ItemStack s : gifts) {
			sb.append(sb.length() == 0 ? "" : ", ").append(s.getHoverName().getString()).append(" ×").append(s.getCount());
			if (!p.getInventory().add(s)) p.spawnAtLocation((ServerLevel) p.level(), s);
		}
		Fx.sound(l, at, SoundEvents.BELL_BLOCK, 1F, 1.2F);
		Fx.sound(l, at, SoundEvents.PLAYER_LEVELUP, 0.7F, 1.4F);
		Fx.burst(l, com.elementalweapons.fx.P.END_ROD, at, 30, 0.8, 0.12);
		p.sendSystemMessage(Component.literal("♪ 천상의 하사품: " + sb).withStyle(ChatFormatting.AQUA));
	}
}
