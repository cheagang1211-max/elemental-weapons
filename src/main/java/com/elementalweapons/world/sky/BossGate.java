package com.elementalweapons.world.sky;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.boss.BossFight;
import com.elementalweapons.boss.ElementalBoss;
import com.elementalweapons.boss.ModEntities;
import com.elementalweapons.spell.ElectricStormSpell;
import com.elementalweapons.spell.Fx;

/**
 * 천공성 1층의 '보스 관문'과 '제작소'.
 *  · 보스 관문: 소환석을 들고 관문 앞에서 우클릭 → 그 보스 전용 방(BossRooms)으로 일행과 함께 이동해 보스전.
 *    이기면 15초 뒤(또는 B키) 천공성으로, 모두 쓰러지면 보스전 종료·소환석 반환.
 *  · 제작소: 작업대·화로·용광로·모루·대장장이 작업대 등을 한곳에 모아 둔 곳 (스폰 바로 옆, 떠 있는 큰 이름표)
 *  지금 세계에도 바로 생기도록 서버가 실행 중에 지어 넣음 (한 번만).
 */
public final class BossGate {
	private BossGate() {}

	public static final BlockPos GATE = new BlockPos(16, Capital.FY[0] + 1, 12);
	public static final BlockPos CRAFT = new BlockPos(-16, Capital.FY[0] + 1, 12);
	private static final int PATCH_VERSION = 1;

	public static final AttachmentType<Integer> PATCH = AttachmentRegistry.<Integer>builder()
			.persistent(com.mojang.serialization.Codec.INT).buildAndRegister(ElementalWeaponsMod.id("capital_patch"));
	public static final AttachmentType<Long> BUILT = AttachmentRegistry.<Long>builder()
			.persistent(com.mojang.serialization.Codec.LONG).buildAndRegister(ElementalWeaponsMod.id("boss_rooms_built"));

	private record Run(int room, ElementalBoss boss, UUID summoner, ItemStack refund, long start) {}
	private static final Map<Integer, Run> RUNS = new HashMap<>();
	private static final Map<Integer, Long> RETURN_AT = new HashMap<>();
	private static final Map<Integer, Integer> EMPTY_FOR = new HashMap<>();

	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			ServerLevel ow = server.overworld();
			if (!ModSky.active(ow)) return;
			try {
				if (server.getTickCount() % 40 == 11) patch(ow);
				if (server.getTickCount() % 100 == 37) labels(server, ow);
				if (server.getTickCount() % 4 == 0) gateFx(ow, server.getTickCount());
				if (server.getTickCount() % 20 == 3) tickRuns(ow);
			} catch (Throwable t) { ElementalWeaponsMod.LOGGER.error("[GATE] 오류", t); }
		});
	}

	public static boolean nearGate(ServerPlayer p) {
		return p.level() == p.level().getServer().overworld() && p.position().distanceToSqr(Vec3.atCenterOf(GATE)) < 9 * 9;
	}

	// ---------------- 천공성에 지어 넣기 ----------------
	private static BlockState b(String id) { return BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("minecraft", id)).defaultBlockState(); }
	private static void set(ServerLevel l, BlockPos p, BlockState s) { l.setBlock(p, s, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE); }

	private static void patch(ServerLevel ow) {
		Integer v = ow.getAttached(PATCH);
		if (v != null && v >= PATCH_VERSION) return;
		if (!ow.isLoaded(GATE) || !ow.isLoaded(CRAFT) || !ow.isLoaded(GATE.offset(6, 0, 4)) || !ow.isLoaded(CRAFT.offset(-6, 0, 4))) return;
		buildCraftHall(ow);
		buildGate(ow);
		ow.setAttached(PATCH, PATCH_VERSION);
		ElementalWeaponsMod.LOGGER.info("[GATE] 천공성에 보스 관문·제작소를 지었습니다");
	}

	private static void buildCraftHall(ServerLevel l) {
		BlockPos c = CRAFT;
		// 자리 비우고 바닥
		for (int x = -5; x <= 5; x++) for (int z = -4; z <= 4; z++) {
			set(l, c.offset(x, -1, z), (Math.abs(x) == 5 || Math.abs(z) == 4) ? b("polished_andesite") : (x + z) % 2 == 0 ? b("spruce_planks") : b("stripped_spruce_log"));
			for (int y = 0; y <= 6; y++) set(l, c.offset(x, y, z), b("air"));
		}
		// 기둥 + 지붕
		int[][] posts = {{-5, -4}, {5, -4}, {-5, 4}, {5, 4}};
		for (int[] q : posts) for (int y = 0; y <= 4; y++) set(l, c.offset(q[0], y, q[1]), b("stripped_dark_oak_log"));
		for (int x = -6; x <= 6; x++) for (int z = -5; z <= 5; z++) {
			int ry = 5 + (Math.abs(z) <= 1 ? 1 : 0);
			set(l, c.offset(x, ry, z), Math.abs(z) == 5 ? b("dark_oak_slab") : b("dark_oak_planks"));
		}
		// 작업대 줄 (북쪽) + 화로 줄 (남쪽)
		String[] north = {"crafting_table", "crafting_table", "smithing_table", "anvil", "grindstone", "stonecutter", "loom", "crafting_table", "crafting_table"};
		for (int i = 0; i < north.length; i++) set(l, c.offset(-4 + i, 0, -3), b(north[i]));
		String[] south = {"furnace", "furnace", "blast_furnace", "blast_furnace", "smoker", "barrel", "barrel", "cartography_table", "fletching_table"};
		for (int i = 0; i < south.length; i++) set(l, c.offset(-4 + i, 0, 3), b(south[i]));
		// 가운데 큰 작업대 + 등
		set(l, c.offset(0, 0, 0), b("crafting_table"));
		for (int[] q : posts) set(l, c.offset(q[0], 5, q[1]).below(), b("lantern"));
		set(l, c.offset(0, 4, 0), b("lantern").trySetValue(net.minecraft.world.level.block.LanternBlock.HANGING, true));
		for (int x = -4; x <= 4; x += 4) set(l, c.offset(x, -1, 0), b("glowstone"));
	}

	private static void buildGate(ServerLevel l) {
		BlockPos c = GATE;
		for (int x = -5; x <= 5; x++) for (int z = -3; z <= 3; z++) {
			set(l, c.offset(x, -1, z), Math.abs(x) == 5 || Math.abs(z) == 3 ? b("polished_blackstone_bricks") : b("crying_obsidian"));
			for (int y = 0; y <= 10; y++) set(l, c.offset(x, y, z), b("air"));
		}
		// 둥근 관문 틀 (남북을 바라봄)
		for (double a = 0; a < Math.PI * 2; a += 0.05) {
			int x = (int) Math.round(Math.cos(a) * 4.2), y = (int) Math.round(Math.sin(a) * 4.2) + 4;
			if (y < 0) continue;
			set(l, c.offset(x, y, 0), y % 3 == 0 ? b("crying_obsidian") : b("polished_blackstone_bricks"));
		}
		for (int y = 0; y <= 1; y++) for (int x = -4; x <= 4; x += 8) set(l, c.offset(x, y, 0), b("chiseled_polished_blackstone"));
		set(l, c.offset(0, 8, 0), b("respawn_anchor"));
		for (int x = -5; x <= 5; x += 10) for (int z = -3; z <= 3; z += 6) { set(l, c.offset(x, 0, z), b("polished_blackstone_wall")); set(l, c.offset(x, 1, z), b("soul_lantern")); }
	}

	/** 떠 있는 큰 이름표 (text_display) */
	private static void labels(MinecraftServer server, ServerLevel ow) {
		label(server, ow, GATE.offset(0, 9, 0), "ew_label_gate", "☠ 보스 관문", "#FF6A6A", "소환석을 들고 여기서 우클릭");
		label(server, ow, CRAFT.offset(0, 7, 0), "ew_label_craft", "⚒ 제작소", "#FFD34D", "작업대 · 화로 · 모루 · 대장장이 작업대");
	}

	static void label(MinecraftServer server, ServerLevel ow, BlockPos at, String tag, String title, String color, String sub) {
		if (!ow.isLoaded(at)) return;
		var list = ow.getEntities((net.minecraft.world.entity.Entity) null, new AABB(at).inflate(3), e -> e.entityTags().contains(tag));
		if (!list.isEmpty()) return;
		String cmd = String.format(java.util.Locale.ROOT,
				"summon minecraft:text_display %d %d %d {Tags:[\"%s\"],billboard:\"center\",shadow:1b,background:1140850688,see_through:0b,"
						+ "transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[2.5f,2.5f,2.5f]},"
						+ "text:[{text:\"%s\",color:\"%s\",bold:true},{text:\"\\n%s\",color:\"white\",bold:false}]}",
				at.getX(), at.getY(), at.getZ(), tag, title, color, sub);
		server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), cmd);
	}

	private static void gateFx(ServerLevel ow, int t) {
		Vec3 c = Vec3.atCenterOf(GATE).add(0, 3.5, 0);
		if (ow.getNearestPlayer(c.x, c.y, c.z, 40, false) == null) return;
		for (int k = 0; k < 4; k++) {
			double a = t * 0.12 + k * Math.PI / 2, r = 1.2 + (t % 40) / 40.0 * 2.4;
			Fx.at(ow, Fx.dust(0xC060FF, 1.2F), c.add(Math.cos(a) * r, Math.sin(a) * r, 0));
		}
		if (t % 20 == 0) Fx.burst(ow, com.elementalweapons.fx.P.PORTAL, c, 12, 1.5, 0.3);
		// 제작소 반짝임
		if (t % 12 == 0) Fx.burst(ow, Fx.dust(0xFFD34D, 1.0F), Vec3.atCenterOf(CRAFT).add(0, 3, 0), 4, 3, 0.02);
	}

	// ---------------- 보스전 ----------------
	/** 관문 앞에서 소환석 사용 */
	public static boolean start(ServerPlayer p, String bossId, ItemStack refund) {
		ServerLevel ow = (ServerLevel) p.level();
		int idx = BossRooms.index(bossId);
		if (idx < 0) return false;
		if (RUNS.containsKey(idx)) {
			p.sendOverlayMessage(Component.literal("다른 도전자가 " + BossRooms.NAMES[idx] + "에서 싸우는 중입니다. 잠시 뒤에 다시 오세요.").withStyle(ChatFormatting.RED));
			return false;
		}
		long built = ow.getAttachedOrElse(BUILT, 0L);
		if ((built & (1L << idx)) == 0) {
			BossRooms.build(ow, idx);
			ow.setAttached(BUILT, built | (1L << idx));
		}
		BlockPos c = BossRooms.center(idx), en = BossRooms.entrance(idx);
		List<ServerPlayer> party = new ArrayList<>();
		for (ServerPlayer q : ow.players()) if (q.isAlive() && !q.isSpectator() && q.distanceToSqr(p) < 8 * 8) party.add(q);
		for (ServerPlayer q : party) {
			if (q.isPassenger()) q.stopRiding();
			q.teleportTo(en.getX() + 0.5, en.getY(), en.getZ() + 0.5);
			q.setYRot(180F); q.setYHeadRot(180F);
			q.resetFallDistance();
			q.sendSystemMessage(Component.literal("☠ " + BossRooms.NAMES[idx] + " — 보스가 깨어납니다!").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
		}
		var type = ModEntities.BOSSES.get(bossId);
		ElementalBoss boss = type == null ? null : type.create(ow, EntitySpawnReason.SPAWN_ITEM_USE);
		if (boss == null) return false;
		Vec3 pos = Vec3.atBottomCenterOf(c).add(0, 3, 0);
		boss.snapTo(pos.x, pos.y, pos.z, 0F, 0F);
		ow.addFreshEntity(boss);
		BossFight.start(ow, boss, Vec3.atBottomCenterOf(c).add(0, 1, 0), null, p, refund, true);
		ElectricStormSpell.drawBolt(ow, pos);
		Fx.sound(ow, pos, SoundEvents.WITHER_SPAWN, 2.0F, 1.4F);
		RUNS.put(idx, new Run(idx, boss, p.getUUID(), refund.copy(), ow.getGameTime()));
		return true;
	}

	private static void tickRuns(ServerLevel ow) {
		long now = ow.getGameTime();
		for (var it = RUNS.entrySet().iterator(); it.hasNext(); ) {
			var e = it.next();
			int idx = e.getKey();
			Run run = e.getValue();
			Vec3 c = Vec3.atBottomCenterOf(BossRooms.center(idx));
			List<ServerPlayer> in = new ArrayList<>();
			for (ServerPlayer q : ow.players()) if (q.isAlive() && q.position().distanceToSqr(c) < 60 * 60) in.add(q);
			boolean won = run.boss.isRemoved() && run.boss.getHealth() <= 0 || run.boss.isDeadOrDying();
			if (won) {
				Long at = RETURN_AT.get(idx);
				if (at == null) {
					RETURN_AT.put(idx, now + 300);
					for (ServerPlayer q : in) q.sendSystemMessage(Component.literal("✦ 승리! 15초 뒤 천공성 보스 관문으로 돌아갑니다. (보상을 챙기세요 · B키로 바로 귀환)").withStyle(ChatFormatting.GOLD));
				} else if (now >= at) {
					for (ServerPlayer q : in) sendBack(q);
					RETURN_AT.remove(idx); EMPTY_FOR.remove(idx);
					it.remove();
				}
				continue;
			}
			if (run.boss.isRemoved()) { RETURN_AT.remove(idx); EMPTY_FOR.remove(idx); it.remove(); continue; }   // 실패로 끝남 (소환석은 BossFight가 반환)
			// 아무도 없이 10초 → 보스전 취소 + 소환석 반환
			if (in.isEmpty()) {
				int n = EMPTY_FOR.merge(idx, 1, Integer::sum);
				if (n >= 10) {
					run.boss.cancelFight();
					if (run.summoner != null && !run.refund.isEmpty()) BossFight.refundLater(run.summoner, run.refund);
					ServerPlayer s = ow.getServer().getPlayerList().getPlayer(run.summoner);
					if (s != null && s.isAlive()) BossFight.giveRefundNow(s);
					EMPTY_FOR.remove(idx);
					it.remove();
				}
			} else EMPTY_FOR.remove(idx);
		}
	}

	public static void sendBack(ServerPlayer q) {
		BlockPos g = GATE.offset(0, 0, 5);
		q.teleportTo(g.getX() + 0.5, g.getY(), g.getZ() + 0.5);
		q.resetFallDistance();
	}

	/** 보스방 안인지 (죽었을 때 부활 위치 등) */
	public static int roomAt(Vec3 p) {
		for (int i = 0; i < BossRooms.BOSSES.size(); i++) if (p.distanceToSqr(Vec3.atCenterOf(BossRooms.center(i))) < 70 * 70) return i;
		return -1;
	}
}
