package com.elementalweapons.client;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.boss.ModEntities;
import com.elementalweapons.client.growth.GrowthScreen;
import com.elementalweapons.growth.Growth;
import com.elementalweapons.growth.ModGrowth;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.ModGear;
import com.elementalweapons.item.ModItems;
import com.elementalweapons.item.WeaponType;
import com.elementalweapons.mob.ModMobs;
import com.elementalweapons.world.ModBlocks;

/**
 * 자동 스크린샷 (GitHub Actions 전용, 환경변수 EW_SHOTS=1).
 * 실제 게임 클라이언트로 세계에 들어가 HUD·성장 창·몹·보스 화면을 찍고 종료.
 */
public final class ShotHarness {
	private ShotHarness() {}

	private static int t = -1;
	private static double baseX, baseY, baseZ;
	private static final List<EntityType<?>> SUBJECTS = new ArrayList<>();
	private static Entity current;
	private static final int WEAP_START = 545, ENT_START = 1220, ENT_STEP = 70;
	private static final List<Entity> gallery = new ArrayList<>();

	public static void initialize() {
		if (!"1".equals(System.getenv("EW_SHOTS"))) return;
		ElementalWeaponsMod.LOGGER.info("[SHOTS] 활성화");
		ClientTickEvents.END_CLIENT_TICK.register(ShotHarness::tick);
	}

	private static void log(String s) { ElementalWeaponsMod.LOGGER.info("[SHOTS] " + s); }

	private static void shot(Minecraft mc, String name) {
		Screenshot.grab(mc.gameDirectory, name + ".png", mc.gameRenderer.mainRenderTarget(), 1, msg -> log(name + " -> " + msg.getString()));
	}

	/** v4.8 확인: 바닐라 몹 차단 · 잡템 → 원소 가루/고기/솜털 */
	private static void classTest(Minecraft mc, IntegratedServer server, int f) {
		if (f == 2) server.execute(() -> { ServerPlayer p = sp(server);
			ServerLevel l = (ServerLevel) p.level();
			for (var t : new net.minecraft.world.entity.EntityType<?>[]{net.minecraft.world.entity.EntityTypes.WANDERING_TRADER, net.minecraft.world.entity.EntityTypes.PILLAGER, net.minecraft.world.entity.EntityTypes.COW}) {
				Entity e = t.create(l, net.minecraft.world.entity.EntitySpawnReason.NATURAL);
				e.snapTo(p.getX() + 3, p.getY(), p.getZ() + 3, 0, 0); l.addFreshEntity(e);
			}
			var ie = new net.minecraft.world.entity.item.ItemEntity(l, p.getX() + 1, p.getY() + 1, p.getZ(), new ItemStack(net.minecraft.world.item.Items.LIGHT_GRAY_DYE, 3));
			ie.setPickUpDelay(200); l.addFreshEntity(ie);
			p.getInventory().setItem(20, new ItemStack(net.minecraft.world.item.Items.AZURE_BLUET, 5));
			p.getInventory().setItem(21, new ItemStack(net.minecraft.world.item.Items.BEEF, 2));
			p.getInventory().setItem(22, new ItemStack(net.minecraft.world.item.Items.LEATHER, 4));
			p.getInventory().setItem(23, new ItemStack(net.minecraft.world.item.Items.STONE_BRICKS, 10));
			p.getInventory().setItem(24, new ItemStack(net.minecraft.world.item.Items.WHEAT_SEEDS, 6)); });
		if (f == 30) server.execute(() -> { ServerPlayer p = sp(server);
			ServerLevel l = (ServerLevel) p.level();
			int vanilla = 0;
			for (var e : l.getEntitiesOfClass(net.minecraft.world.entity.Mob.class, p.getBoundingBox().inflate(16)))
				if (BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getNamespace().equals("minecraft") && !e.getTags().contains(com.elementalweapons.world.VanillaPurge.ALLOWED)) vanilla++;
			StringBuilder drops = new StringBuilder();
			for (var ie : l.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, p.getBoundingBox().inflate(6)))
				drops.append(BuiltInRegistries.ITEM.getKey(ie.getItem().getItem()).getPath()).append('x').append(ie.getItem().getCount()).append(' ');
			StringBuilder inv = new StringBuilder();
			for (int i = 20; i <= 24; i++) { var st = p.getInventory().getItem(i); inv.append(BuiltInRegistries.ITEM.getKey(st.getItem()).getPath()).append('x').append(st.getCount()).append(' '); }
			log("V48 남은 바닐라 몹=" + vanilla + " | 바닥 아이템: " + drops + "| 가방: " + inv); });
	}

	private static long lastXp;

	private static ServerPlayer sp(IntegratedServer s) { return s.getPlayerList().getPlayers().get(0); }

	private static int idle = 0;

	private static void tick(Minecraft mc) {
		if (mc.player == null || mc.level == null || mc.getSingleplayerServer() == null) {
			idle++;
			net.minecraft.client.gui.screens.Screen sc = mc.gui.screen();
			if (idle % 200 == 0) {
				StringBuilder sb = new StringBuilder();
				if (sc != null) for (var c : sc.children()) if (c instanceof net.minecraft.client.gui.components.AbstractWidget w) sb.append("[").append(w.getMessage().getString()).append("] ");
				log("대기 " + idle + " 화면=" + (sc == null ? "없음" : sc.getClass().getName()) + " 버튼=" + sb);
			}
			if (idle == 300 || idle == 1100) shot(mc, "00_debug_" + idle);
			if (idle % 100 == 50 && sc instanceof net.minecraft.client.gui.screens.BackupConfirmScreen) {
				int n = 0;
				for (var c : sc.children()) {
					if (c instanceof net.minecraft.client.gui.components.Button b && ++n == 2) {
						log("실험적 설정 경고 → '" + b.getMessage().getString() + "' 클릭");
						b.onPress(new net.minecraft.client.input.KeyEvent(257, 0, 0));
						break;
					}
				}
			}
			return;
		}
		IntegratedServer server = mc.getSingleplayerServer();
		t++;
		if (FOCUS && t > 60) {
			try { focus(mc, server, t - 60); } catch (Throwable e) { ElementalWeaponsMod.LOGGER.error("[SHOTS] 오류 f=" + (t - 60), e); }
			return;
		}
		try {
			if (t == 40) {
				SUBJECTS.addAll(ModMobs.MOBS.values());
				SUBJECTS.add(ModMobs.FLUFFLE); SUBJECTS.add(ModMobs.HORNED_DEER); SUBJECTS.add(ModMobs.MERCHANT); SUBJECTS.add(ModMobs.SAGE);
				SUBJECTS.add(ModEntities.VOLTRA);
				SUBJECTS.addAll(ModEntities.BOSSES.values());
				server.execute(() -> setup(server));
			}
			if (t > 45 && t < 300) look(mc, 0, 6);
			if (t == 270 || t == 285) server.execute(() -> ModGrowth.addXp(sp(server), 25));
			if (t == 300) shot(mc, "01_hud");
			if (t == 320) server.execute(() -> ModGrowth.addXp(sp(server), Growth.need(24)));
			if (t == 334) shot(mc, "02_levelup");
			if (t == 400) mc.gui.setScreen(new GrowthScreen());
			if (t == 425) shot(mc, "03_growth_screen");
			if (t == 426) mc.gui.setScreen(new com.elementalweapons.client.skill.EvolveScreen());
			if (t == 442) shot(mc, "03b_class_select");
			if (t == 443) mc.gui.setScreen(new com.elementalweapons.client.growth.SageScreen());
			if (t == 445) shot(mc, "03c_sage");
			if (t == 448) {
				mc.gui.setScreen(null);
				server.execute(() -> {
					ServerPlayer p = sp(server);
					p.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModGear.ITEMS.get("orichalcum_helmet")));
					p.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModGear.ITEMS.get("orichalcum_chestplate")));
					p.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModGear.ITEMS.get("orichalcum_leggings")));
					p.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModGear.ITEMS.get("orichalcum_boots")));
				});
				mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			}
			if (t == 470) shot(mc, "04_player_armor_staff");
			if (t == 475) mc.options.setCameraType(CameraType.FIRST_PERSON);
			if (t == 480) server.execute(() -> sp(server).setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WEAPONS.get(Element.FIRE).get(WeaponType.GREATSWORD))));
			if (t == 500) shot(mc, "05_firstperson_greatsword");
			if (t == 505) server.execute(() -> sp(server).setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WEAPONS.get(Element.LIGHT).get(WeaponType.STAFF))));
			if (t == 515) shot(mc, "06_firstperson_light_staff");
			if (t == 516) server.execute(() -> { ServerPlayer p = sp(server);
				p.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WEAPONS.get(Element.LIGHT).get(WeaponType.SWORD)));
				p.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(ModItems.WEAPONS.get(Element.FIRE).get(WeaponType.SHIELD))); });
			if (t == 530) shot(mc, "06b_firstperson_sword_shield");
			if (t == 531) server.execute(() -> { ServerPlayer p = sp(server);
				p.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
				p.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WEAPONS.get(Element.WATER).get(WeaponType.BOOK))); });
			if (t == 543) shot(mc, "06c_firstperson_book");
			if (t == WEAP_START) server.execute(() -> showcase(server));
			if (t > WEAP_START && t < WEAP_START + 120) look(mc, 0, -14);

			if (t == WEAP_START + 80) shot(mc, "07_showcase_wall");
			if (t == WEAP_START + 85) server.execute(() -> sp(server).teleportTo(baseX, baseY + 2, baseZ + 5.5));
			if (t == WEAP_START + 110) shot(mc, "07b_showcase_close");
			if (t == WEAP_START + 111) server.execute(() -> sp(server).teleportTo(baseX + 12.5, baseY + 1.5, baseZ + 5.5));
			if (t > WEAP_START + 111 && t < WEAP_START + 130) look(mc, 0, -8);
			if (t == WEAP_START + 128) shot(mc, "07c_tier_wall");
			if (t == WEAP_START + 131) { server.execute(() -> { sp(server).teleportTo(baseX, baseY, baseZ); for (Entity e : gallery) e.discard(); gallery.clear(); }); }
			if (t >= 1020 && t < 1020 + 7 * 25) {
				int k = (t - 1020) / 25, ph = (t - 1020) % 25;
				Element el = Element.values()[k];
				if (ph == 0) server.execute(() -> sp(server).setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WEAPONS.get(el).get(k % 2 == 0 ? WeaponType.SWORD : WeaponType.GREATSWORD))));
				if (ph > 0) look(mc, 0, 25);
				if (ph == 22) shot(mc, "08_aura_" + el.name().toLowerCase());
			}
			if (t >= ENT_START) {
				int idx = (t - ENT_START) / ENT_STEP, k = (t - ENT_START) % ENT_STEP;
				if (idx >= SUBJECTS.size()) {
					int tt = t - (ENT_START + SUBJECTS.size() * ENT_STEP);
					townScenes(mc, server, tt);
					return;
				}
				EntityType<?> type = SUBJECTS.get(idx);
				if (k == 0) server.execute(() -> spawn(server, type));
				if (k > 0 && current != null) aim(mc, current);
				if (k == 50) shot(mc, String.format("%02d_%s", 10 + idx, BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath()));
				if (k == 60) server.execute(() -> { if (current != null) current.discard(); current = null; });
			}
		} catch (Throwable e) {
			ElementalWeaponsMod.LOGGER.error("[SHOTS] 오류 t=" + t, e);
		}
	}

	// ================= v4.1: 천공성 · 용 · 가게 주인 · 지역 마을 =================
	private static final boolean FOCUS = true;
	private static SkyDragon_ ride;
	private record SkyDragon_(net.minecraft.world.entity.Entity e) {}

	private static double[] lotView(com.elementalweapons.world.sky.Capital.Kind kind, int floor, double off, double up) {
		for (var l : com.elementalweapons.world.sky.Capital.LOTS) if (l.kind() == kind && l.floor() == floor) {
			double d = Math.hypot(l.x(), l.z()), ux = -l.x() / d, uz = -l.z() / d;
			return new double[]{l.x() + ux * off - uz * 5, l.y() + up, l.z() + uz * off + ux * 5, l.x(), l.y() + 3, l.z(), 6000};
		}
		return new double[]{0, 200, 100, 0, 171, 0, 6000};
	}

	private static double[][] focusScenes() {
		var C = com.elementalweapons.world.sky.Capital.class;
		int[] FY = com.elementalweapons.world.sky.Capital.FY;
		java.util.List<double[]> L = new java.util.ArrayList<>();
		L.add(new double[]{100, 225, 115, 0, 212, 0, 6000});                        // 0 천공성 전경 (지형 불러오기용)
		L.add(new double[]{0, 200, 165, 0, 220, 0, 6000});                          // 1 남쪽 정면
		L.add(new double[]{14, FY[0] + 6, 46, 0, FY[0] + 8, 0, 6000});              // 2 1층 광장과 탑
		L.add(new double[]{0, FY[0] + 14, 66, 0, FY[0] + 1, 108, 6000});            // 3 용의 둥지
		L.add(lotView(com.elementalweapons.world.sky.Capital.Kind.MARKET, 0, 24, 8));  // 4
		L.add(lotView(com.elementalweapons.world.sky.Capital.Kind.SMITH, 0, 22, 7));   // 5
		L.add(new double[]{70, FY[0] + 4, 20, 20, FY[1] - 6, 0, 6000});             // 6 윗층 밑면 (매달린 등불)
		L.add(new double[]{0, FY[1] + 10, 84, 0, FY[1] + 2, 0, 6000});              // 7 2층 전경
		L.add(lotView(com.elementalweapons.world.sky.Capital.Kind.FORGE, 1, 22, 6));   // 8
		L.add(lotView(com.elementalweapons.world.sky.Capital.Kind.ALCHEMY, 1, 16, 5)); // 9
		L.add(lotView(com.elementalweapons.world.sky.Capital.Kind.ENCHANT, 2, 18, 8)); // 10
		L.add(new double[]{0, FY[2] + 9, 68, 0, FY[2] + 2, 0, 6000});               // 11 3층
		L.add(lotView(com.elementalweapons.world.sky.Capital.Kind.RIFT_GATE, 3, 14, 6)); // 12 4층
		L.add(new double[]{0, FY[4] + 12, 58, 0, FY[4] + 10, 0, 6000});             // 13 5층 왕성
		L.add(new double[]{-30, 262, -130, -20, 230, -55, 6000});                   // 14 서북쪽: 바깥 큰 계단
		L.add(new double[]{120, 225, 120, 0, 215, 0, 18000});                       // 15 밤의 천공성
		return L.toArray(new double[0][]);
	}

	private static int scene = -1;

	private static void focus(Minecraft mc, IntegratedServer server, int f) {
		if (f < 60) { classTest(mc, server, f); return; }
		f -= 60;
		double[][] sc = focusScenes();
		int per = 300;
		if (f < sc.length * per) {
			int idx = f / per, k = f % per;
			double[] c = sc[idx];
			if (k == 0) {
				mc.options.setCameraType(CameraType.FIRST_PERSON);
				server.execute(() -> { ServerPlayer p = sp(server);
					p.getAbilities().flying = true; p.getAbilities().mayfly = true; p.onUpdateAbilities();
					p.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
					p.teleportTo(c[0] + 0.5, c[1], c[2] + 0.5);
					server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set " + (long) c[6]); });
			}
			if (k > 0) { lookAt(mc, c[3], c[4], c[5]); mc.player.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO); }
			if (k == per - 40) server.execute(() -> server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set " + (long) c[6]));
			if (k == per - 10) shot(mc, String.format("9%02d_citadel", idx));
			if (k == per - 12) server.execute(() -> { ServerPlayer p = sp(server);
				log("장면 " + idx + " 상인 " + p.level().getEntitiesOfClass(com.elementalweapons.mob.ElementalMerchant.class, p.getBoundingBox().inflate(80)).size()
						+ " 용 " + p.level().getEntitiesOfClass(com.elementalweapons.mob.SkyDragon.class, p.getBoundingBox().inflate(120)).size()); });
			return;
		}
		f -= sc.length * per;
		// --- 가게 주인 · 천공룡 근접 사진 ---
		EntityType<?>[] who = {ModMobs.BLACKSMITH, ModMobs.ENCHANTER, ModMobs.ALCHEMIST, ModMobs.MERCHANT, ModMobs.SKY_DRAGON};
		int per2 = 60;
		if (f < who.length * per2) {
			int idx = f / per2, k = f % per2;
			if (k == 0) server.execute(() -> { ServerPlayer p = sp(server);
				p.getAbilities().flying = false; p.onUpdateAbilities();
				server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set 6000");
				p.teleportTo(0.5, com.elementalweapons.world.sky.Capital.FY[4] + 1, 22.5);
				if (current != null) current.discard();
				Entity e = who[idx].create(p.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
				double dist = idx == 4 ? 9 : 3.2;
				e.snapTo(0.5, com.elementalweapons.world.sky.Capital.FY[4] + 1, 22.5 + dist, 180, 0);
				p.level().addFreshEntity(e); current = e; });
			if (k > 2 && current != null) { mc.options.setCameraType(CameraType.FIRST_PERSON); lookAt(mc, current.getX() - 0.5, current.getY() + current.getBbHeight() * 0.55, current.getZ() - 0.5); }
			if (k == 39 && idx == 0) { mc.options.setCameraType(CameraType.FIRST_PERSON); }
			if (k == 40) shot(mc, String.format("95%d_%s", idx, BuiltInRegistries.ENTITY_TYPE.getKey(who[idx]).getPath()));
			if (k == 50 && idx < who.length - 1) server.execute(() -> { if (current != null) current.discard(); current = null; });
			return;
		}
		f -= who.length * per2;
		// --- 용 타고 날기 (W 누르기) ---
		if (f < 260) {
			if (f == 0) server.execute(() -> { ServerPlayer p = sp(server);
				if (current != null) { current.teleportTo(0.5, 200, 60.5); p.teleportTo(0.5, 202, 60.5); p.startRiding(current); } });
			if (f == 5) mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			if (f == 20) { savedInput = mc.player.input; mc.player.input = new net.minecraft.client.player.ClientInput() { @Override public void tick() { this.moveVector = new net.minecraft.world.phys.Vec2(0, 1); } }; }
			if (f > 5 && f < 250) look(mc, f < 160 ? 180 : 150, f < 160 ? -8 : 10);
			if (f == 60) log("용 비행 시작 y=" + String.format("%.1f", mc.player.getY()) + " z=" + String.format("%.1f", mc.player.getZ()));
			if (f == 120) shot(mc, "960_dragon_flight");
			if (f == 150) log("용 비행 중 y=" + String.format("%.1f", mc.player.getY()) + " z=" + String.format("%.1f", mc.player.getZ()) + " 탑승=" + mc.player.isPassenger());
			if (f == 175) shot(mc, "961_dragon_flight_side");
			if (f == 250) { if (savedInput != null) mc.player.input = savedInput; server.execute(() -> { sp(server).stopRiding(); if (current != null) current.discard(); current = null; }); }
			return;
		}
		f -= 260;
		// --- 승강기 시험 ---
		if (f < 300) {
			if (f == 0) server.execute(() -> { ServerPlayer p = sp(server); p.getAbilities().flying = false; p.getAbilities().mayfly = false; p.onUpdateAbilities();
				p.teleportTo(0.5, com.elementalweapons.world.sky.Capital.FY[0] + 1, 0.5); });
			if (f == 2) mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			if (f > 2) look(mc, 0, -20);
			if (f % 40 == 0) log("승강기 f=" + f + " y=" + String.format("%.1f", mc.player.getY()));
			if (f == 150) shot(mc, "962_lift");
			return;
		}
		f -= 300;
		// --- 스킬 이동 동기화 시험 (나무 단검 구르기) ---
		if (f < 80) {
			if (f == 0) server.execute(() -> { ServerPlayer p = sp(server);
				p.teleportTo(0.5, com.elementalweapons.world.sky.Capital.FY[0] + 1, 26.5);
				ModGrowth.set(p, ModGrowth.get(p).withClass(com.elementalweapons.item.PlayerClass.ASSASSIN.ordinal()));
				p.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WOODEN.get(WeaponType.DAGGER))); });
			if (f > 0) look(mc, 0, 0);
			if (f == 30) { dodgeZ = mc.player.getZ(); com.elementalweapons.client.skill.SkillClient.press(0); }
			if (f == 40) log("구르기 이동 거리 = " + String.format("%.2f", mc.player.getZ() - dodgeZ) + " (0이면 동기화 실패)");
			return;
		}
		f -= 80;
		// --- 좌클릭 콤보 시험 (나무 단검 3연타 → 급소 찌르기) ---
		if (f < 90) {
			if (f == 0) server.execute(() -> { ServerPlayer p = sp(server);
				p.teleportTo(0.5, com.elementalweapons.world.sky.Capital.FY[4] + 1, 22.5);
				if (current != null) current.discard();
				Entity z = net.minecraft.world.entity.EntityTypes.ZOMBIE.create(p.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
				if (z != null) z.addTag(com.elementalweapons.world.VanillaPurge.ALLOWED);
				z.snapTo(0.5, com.elementalweapons.world.sky.Capital.FY[4] + 1, 24.5, 0, 0);
				((net.minecraft.world.entity.Mob) z).setNoAi(true);
				p.level().addFreshEntity(z); current = z; });
			if (f > 2 && current != null) lookAt(mc, current.getX() - 0.5, current.getY() + 1, current.getZ() - 0.5);
			if (f == 10) comboHp = current instanceof net.minecraft.world.entity.LivingEntity le ? le.getHealth() : -1;
			if (f == 20 || f == 35 || f == 50) net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new com.elementalweapons.skill.SkillNet.Swing());
			if (f == 52) shot(mc, "963_combo_finisher");
			if (f == 70) { float hp = current instanceof net.minecraft.world.entity.LivingEntity le ? le.getHealth() : -1; log("콤보 시험: 좀비 체력 " + comboHp + " → " + hp); }
			if (f == 85) server.execute(() -> { if (current != null) current.discard(); current = null; });
			return;
		}
		f -= 90;
		// --- 튜토리얼 바닥 화살표 (3단계: 용의 둥지로) ---
		if (f < 80) {
			if (f == 0) server.execute(() -> { ServerPlayer p = sp(server);
				p.getAbilities().flying = false; p.onUpdateAbilities();
				var d = com.elementalweapons.growth.Tutorial.get(p);
				p.setAttached(com.elementalweapons.growth.Tutorial.DATA, new com.elementalweapons.growth.Tutorial.Data(3, 0, d.kits()));
				p.teleportTo(-14.5, com.elementalweapons.world.sky.Capital.FY[0] + 1, 40.5);
				server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set 13000"); });
			if (f == 3) mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			if (f > 3) look(mc, -13, 30);
			if (f == 70) shot(mc, "964_tutorial_arrows");
			return;
		}
		f -= 80;
		// --- 큰 계단 검사: 구간마다 가서 걷는 칸이 막힘 없이 이어지는지 ---
		if (f < 4 * 80) {
			int k = 4 - f / 80, kk = f % 80;
			var C = com.elementalweapons.world.sky.Capital.class;
			double Rs = com.elementalweapons.world.sky.Capital.STAIR_R[k];
			double a0 = Math.toRadians(com.elementalweapons.world.sky.Capital.STAIR_START[k]);
			int[] FY = com.elementalweapons.world.sky.Capital.FY;
			int L = 2 * (FY[k] - FY[k - 1]);
			if (kk == 0) server.execute(() -> { ServerPlayer p = sp(server);
				p.getAbilities().flying = true; p.getAbilities().mayfly = true; p.onUpdateAbilities();
				double am = a0 + (L / 2.0) / Rs;
				p.teleportTo(Math.cos(am) * (Rs + 8), FY[k] + 6, Math.sin(am) * (Rs + 8)); });
			if (kk == 60) server.execute(() -> { ServerPlayer p = sp(server);
				var lv = p.level(); int bad = 0, first = -1;
				for (int si = 0; si <= L; si++) {
					double a = a0 + (si + 0.5) / Rs;
					int x = (int) Math.floor(Math.cos(a) * Rs), z = (int) Math.floor(Math.sin(a) * Rs), y = FY[k] - si / 2;
					var bp = new BlockPos(x, y, z);
					boolean ok = !lv.getBlockState(bp).isAir() && lv.getBlockState(bp.above()).getCollisionShape(lv, bp.above()).isEmpty() && lv.getBlockState(bp.above(2)).getCollisionShape(lv, bp.above(2)).isEmpty();
					if (!ok) { bad++; if (first < 0) first = si; }
				}
				log("큰 계단 " + k + "층→" + (k - 1) + "층: 칸 " + (L + 1) + "개 중 막힘/빈칸 " + bad + (first >= 0 ? " (처음 " + first + "번째)" : "")); });
			if (kk == 62 && k == 2) { lookAt(mc, 0, FY[2] - 10, 0); }
			if (kk == 70 && k == 2) shot(mc, "965_grand_stairs");
			return;
		}
		f -= 4 * 80;
		// --- 지역 마을 · 야영지 · 망루 · 유적 · 용의 둥지 ---
		java.util.List<double[]> rs = new java.util.ArrayList<>();
		for (int region = 0; region < 7; region++) for (int[] q : com.elementalweapons.world.sky.Outposts.pois(region)) if (q[0] == 0) {
			rs.add(new double[]{q[1] + 46, q[2] + 30, q[3] + 30, q[1], q[2] + 4, q[3], 6000});
			rs.add(new double[]{q[1] + 8, q[2] + 6, q[3] + 30, q[1], q[2] + 6, q[3], 6000});
			break;
		}
		for (int region : new int[]{4}) {
			boolean[] seen = new boolean[9];
			seen[0] = true;
			for (int[] q : com.elementalweapons.world.sky.Outposts.pois(region)) {
				if (seen[q[0]]) continue;
				seen[q[0]] = true;
				double off = q[0] == 0 ? 30 : 14;
				rs.add(new double[]{q[1] + off, q[2] + (q[0] == 0 ? 16 : 9), q[3] + off * 0.6, q[1], q[2] + 3, q[3], 6000});
			}
		}
		int[] ro = com.elementalweapons.world.sky.Outposts.roost(0);
		if (ro != null) rs.add(new double[]{ro[0] + 16, ro[1] + 10, ro[2] + 10, ro[0], ro[1] + 1, ro[2], 6000});
		double[] rc = com.elementalweapons.world.sky.SkyLayout.REGION_CENTER[0];
		rs.add(new double[]{rc[0] * 0.55, 240, rc[1] * 0.55, rc[0], 170, rc[1], 6000});
		int per3 = 320;
		if (f < rs.size() * per3) {
			int idx = f / per3, k = f % per3;
			double[] c = rs.get(idx);
			if (k == 0) server.execute(() -> { ServerPlayer p = sp(server);
				p.getAbilities().flying = true; p.getAbilities().mayfly = true; p.onUpdateAbilities();
				p.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
				p.teleportTo(c[0] + 0.5, c[1], c[2] + 0.5);
				server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set 6000"); });
			if (k == 1) mc.options.setCameraType(CameraType.FIRST_PERSON);
			if (k > 0) { lookAt(mc, c[3], c[4], c[5]); mc.player.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO); }
			if (k == per3 - 10) shot(mc, String.format("97%02d_region", idx));
			return;
		}
		if (f == rs.size() * per3 + 5) { log("DONE"); mc.stop(); }
	}

	private static double dodgeZ;
	private static float comboHp;
	private static net.minecraft.client.player.ClientInput savedInput;

	private static BlockPos town;

	private static int groundY = 0;

	private static void tp(IntegratedServer server, double x, double y, double z) { server.execute(() -> sp(server).teleportTo(x, y, z)); }

	private static double[] lotScene(com.elementalweapons.world.sky.Capital.Kind kind) {
		for (var l : com.elementalweapons.world.sky.Capital.LOTS) if (l.kind() == kind) {
			double d = Math.hypot(l.x(), l.z()), ux = -l.x() / d, uz = -l.z() / d;
			double off = kind == com.elementalweapons.world.sky.Capital.Kind.HOUSE ? 16 : 26;
			return new double[]{l.x() + ux * off - uz * 6, 171 + (kind == com.elementalweapons.world.sky.Capital.Kind.HOUSE ? 6 : 10), l.z() + uz * off + ux * 6, l.x(), 174, l.z(), 6000};
		}
		return new double[]{0, 200, 100, 0, 171, 0, 6000};
	}

	/** 공중 세계 장면: {x, y, z, 바라볼 x, y, z} */
	private static double[][] scenes() {
		double a0 = com.elementalweapons.world.sky.SkyLayout.regionAngle(0);
		double dx = Math.cos(a0), dz = Math.sin(a0);
		double[] rc = com.elementalweapons.world.sky.SkyLayout.REGION_CENTER[0];
		double ta = com.elementalweapons.world.sky.SkyLayout.regionAngle(0) + Math.PI / 7;
		double tx = Math.cos(ta) * 246, tz = Math.sin(ta) * 246;
		return new double[][]{
				{0, 250, 360, 0, 215, 0, 6000},                              // 수도 전경 + 하늘 수정
				{0, 192, 112, 0, 238, -6, 6000},                             // 왕성 정면 + 마법진
				lotScene(com.elementalweapons.world.sky.Capital.Kind.INN), lotScene(com.elementalweapons.world.sky.Capital.Kind.SMITH),
				lotScene(com.elementalweapons.world.sky.Capital.Kind.MARKET), lotScene(com.elementalweapons.world.sky.Capital.Kind.SAGE_HALL),
				lotScene(com.elementalweapons.world.sky.Capital.Kind.RIFT_GATE), lotScene(com.elementalweapons.world.sky.Capital.Kind.HOUSE),
				{tx * 0.78 + tz * 0.12, 200, tz * 0.78 - tx * 0.12, tx, 210, tz, 6000},   // 원소 마법탑
				{330, 236, 60, 300, 222, -60, 6000},                          // 떠 있는 섬과 사슬
				{0, 230, 300, 0, 300, -150, 18000},                           // 밤의 수도와 별
				{60, 200, 60, -200, 420, -300, 18000},                        // 별이 가득한 밤하늘
				{dx * 420 - dz * 50, 168, dz * 420 + dx * 50, dx * 640, 162, dz * 640, 12600},   // 노을 무렵 대교
				{rc[0] - dx * 560 - dz * 120, 235, rc[1] - dz * 560 + dx * 120, rc[0], 165, rc[1], 6000}, // 지역 본섬 전경
				{rc[0] - dx * 150, com.elementalweapons.world.sky.SkyLayout.surfaceTop((int) (rc[0] - dx * 150), (int) (rc[1] - dz * 150)) + 6, rc[1] - dz * 150, rc[0] - dx * 120 + dz * 30, com.elementalweapons.world.sky.SkyLayout.surfaceTop((int) (rc[0] - dx * 150), (int) (rc[1] - dz * 150)), rc[1] - dz * 120 - dx * 30, 6000}, // 지역 땅 위: 몹 스폰 확인
		};
	}

	private static final List<Entity> dummies = new ArrayList<>();

	private static void armorSet(IntegratedServer server, String tier) {
		server.execute(() -> {
			ServerPlayer p = sp(server);
			p.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModGear.ITEMS.get(tier + "_helmet")));
			p.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModGear.ITEMS.get(tier + "_chestplate")));
			p.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModGear.ITEMS.get(tier + "_leggings")));
			p.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModGear.ITEMS.get(tier + "_boots")));
			p.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY); p.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
		});
	}

	/** 갑옷(정면/옆) + 낫 회전 베기 + 활 당기기/연사 장면 */
	private static void demo(Minecraft mc, IntegratedServer server, int tt) {
		if (tt == 0) {
			server.execute(() -> {
				ServerPlayer p = sp(server);
				p.getAbilities().flying = false; p.onUpdateAbilities();
				p.teleportTo(0.5, 172, 50.5);
				server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set 6000");
				server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "attribute " + p.getScoreboardName() + " minecraft:camera_distance base set 2.6");
			});
			armorSet(server, "aether");
			mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT);
		}
		if (tt > 0 && tt < 45) look(mc, 0, 8);
		if (tt == 40) shot(mc, "60_armor_aether_front");
		if (tt >= 45 && tt < 75) look(mc, 40, 5);
		if (tt == 72) shot(mc, "61_armor_aether_side");
		if (tt >= 75 && tt < 105) look(mc, 200, 5);
		if (tt == 102) shot(mc, "61b_armor_aether_back");
		if (tt == 105) armorSet(server, "orichalcum");
		if (tt > 105 && tt < 135) look(mc, -25, 8);
		if (tt == 132) shot(mc, "62_armor_orichalcum");
		if (tt == 135) {
			armorSet(server, "aether");
			server.execute(() -> sp(server).setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WEAPONS.get(Element.DARK).get(WeaponType.SCYTHE))));
			mc.options.setCameraType(CameraType.FIRST_PERSON);
		}
		if (tt > 135 && tt < 160) look(mc, 0, 5);
		if (tt == 158) shot(mc, "63_fp_scythe");
		if (tt == 160) {
			mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			server.execute(() -> {
				ServerPlayer p = sp(server);
				ServerLevel level = (ServerLevel) p.level();
				for (int i = 0; i < 6; i++) {
					Entity e = net.minecraft.world.entity.EntityTypes.HUSK.create(level, EntitySpawnReason.COMMAND);
					if (e != null) e.addTag(com.elementalweapons.world.VanillaPurge.ALLOWED);
					if (e == null) continue;
					double a = i * Math.PI / 3;
					e.snapTo(p.getX() + Math.cos(a) * 3.6, p.getY(), p.getZ() + Math.sin(a) * 3.6);
					if (e instanceof Mob m) m.setNoAi(true);
					level.addFreshEntity(e); dummies.add(e);
				}
			});
		}
		if (tt > 160 && tt < 200) look(mc, 0, 25);
		if (tt == 180) server.execute(() -> { ServerPlayer p = sp(server); com.elementalweapons.spell.SpellManager.add(new com.elementalweapons.spell.ReapSpell((ServerLevel) p.level(), p, Element.DARK)); });
		if (tt == 183) shot(mc, "64_reap_a");
		if (tt == 186) shot(mc, "64_reap_b");
		if (tt == 189) shot(mc, "64_reap_c");
		if (tt == 200) {
			server.execute(() -> { for (Entity e : dummies) e.discard(); dummies.clear();
				sp(server).setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WEAPONS.get(Element.FIRE).get(WeaponType.BOW))); });
			mc.options.setCameraType(CameraType.FIRST_PERSON);
		}
		if (tt > 200 && tt < 260) look(mc, 0, -4);
		if (tt == 210) mc.player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);
		if (tt == 228) shot(mc, "65_fp_bow_draw");
		if (tt == 229) mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
		if (tt == 238) shot(mc, "66_bow_draw_back");
		if (tt == 240) {
			mc.player.stopUsingItem();
			server.execute(() -> { ServerPlayer p = sp(server);
				com.elementalweapons.spell.SpellManager.add(new com.elementalweapons.spell.ElementArrowSpell((ServerLevel) p.level(), p, Element.FIRE, 1.0F, p.getMainHandItem())); });
		}
		if (tt == 244) shot(mc, "67_bow_volley");
		if (tt == 250) shot(mc, "67b_bow_volley");
		// 무기 동작 + 베기 궤적
		Object[][] seq = {{280, Element.FIRE, WeaponType.GREATSWORD, 3}, {330, Element.ELECTRIC, WeaponType.SWORD, 3}, {370, Element.WIND, WeaponType.HAMMER, 1},
				{390, Element.DARK, WeaponType.SCYTHE, 2}, {415, Element.LIGHT, WeaponType.DAGGER, 1}};
		for (Object[] q : seq) {
			int t0 = (Integer) q[0], n = (Integer) q[3];
			Element el = (Element) q[1]; WeaponType wt = (WeaponType) q[2];
			if (tt == t0) {
				server.execute(() -> { ServerPlayer p = sp(server); p.teleportTo(0.5, 172, 50.5);
					p.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WEAPONS.get(el).get(wt))); });
				mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			}
			if (tt > t0 && tt < t0 + 8 + n * 10) look(mc, 25, 38);
			for (int i = 0; i < n; i++) {
				int ts = t0 + 6 + i * 10;
				if (tt == ts) mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND, net.minecraft.world.item.component.SwingAnimation.DEFAULT, true);
				if (tt == ts + (wt == WeaponType.SCYTHE ? 5 : wt == WeaponType.SWORD ? 3 : 4)) shot(mc, String.format("68_%s_%d", wt.id, i));
			}
		}
		if (tt == 427) { mc.options.setCameraType(CameraType.FIRST_PERSON); server.execute(() -> sp(server).setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WEAPONS.get(Element.FIRE).get(WeaponType.GREATSWORD)))); }
		if (tt > 427 && tt < 440) look(mc, 25, 10);
		if (tt == 431) mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND, net.minecraft.world.item.component.SwingAnimation.DEFAULT, true);
		if (tt == 434) shot(mc, "69_fp_greatsword_slash");
		if (tt == 438) armorSet(server, "orichalcum");
		// 초월 성장 창
		if (tt == 442) server.execute(() -> { ServerPlayer p = sp(server);
			com.elementalweapons.growth.Growth g = ModGrowth.get(p);
			ModGrowth.set(p, new com.elementalweapons.growth.Growth(100, 0, 0, g.str(), g.mag(), g.vit(), g.agi(), g.cls()));
			ModGrowth.addParagonXp(p, 900_000);
			ModGrowth.spendParagon(p, 0, 9); ModGrowth.spendParagon(p, 2, 6); ModGrowth.spendParagon(p, 3, 4);
			ModGrowth.setParagon(p, ModGrowth.paragon(p).withBest(12)); });
		if (tt == 455) { com.elementalweapons.client.growth.GrowthScreen.paragonMode = true; mc.gui.setScreen(new GrowthScreen()); }
		if (tt == 472) shot(mc, "70_paragon_screen");
		if (tt == 474) { mc.gui.setScreen(null); com.elementalweapons.client.growth.GrowthScreen.paragonMode = false; }
		// 원소 균열
		if (tt == 480) server.execute(() -> { ServerPlayer p = sp(server); p.teleportTo(0.5, 172, 50.5);
			server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "gamemode creative " + p.getScoreboardName());
			ModGrowth.setParagon(p, ModGrowth.paragon(p).withBest(7));
			com.elementalweapons.rift.RiftItems.open(p, false); });
		if (tt == 481) mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
		if (tt > 481 && tt < 600) look(mc, 0, 32);
		if (tt == 575) shot(mc, "71_rift");
		if (tt == 600) server.execute(() -> { for (var r : new ArrayList<>(com.elementalweapons.rift.RiftItems.ACTIVE)) r.fail("테스트 종료"); com.elementalweapons.rift.RiftItems.ACTIVE.clear(); });
		// 보스방 (화염 지역: 이그니스)
		var ar = com.elementalweapons.world.sky.Arenas.of("ignis");
		if (ar != null) {
			if (tt == 610) server.execute(() -> { ServerPlayer p = sp(server);
				p.getAbilities().flying = true; p.getAbilities().mayfly = true; p.onUpdateAbilities();
				p.teleportTo(ar.x() + 0.5, ar.floor() + 30, ar.z() + 50.5); });
			if (tt == 611) mc.options.setCameraType(CameraType.FIRST_PERSON);
			if (tt > 611 && tt < 820) { mc.player.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO); lookAt(mc, ar.x(), ar.floor(), ar.z()); }
			if (tt == 815) shot(mc, "72_arena_overview");
			if (tt == 820) server.execute(() -> { ServerPlayer p = sp(server);
				p.getAbilities().flying = false; p.onUpdateAbilities();
				p.teleportTo(ar.x() + 0.5, ar.floor() + 1, ar.z() + 15.5);
				ServerLevel level = (ServerLevel) p.level();
				var boss = com.elementalweapons.boss.ModEntities.BOSSES.get("ignis").create(level, EntitySpawnReason.COMMAND);
				boss.snapTo(ar.x() + 0.5, ar.floor() + 3, ar.z() + 0.5, 0, 0);
				level.addFreshEntity(boss);
				bossRef = boss;
				com.elementalweapons.boss.BossFight.start(level, boss, new net.minecraft.world.phys.Vec3(ar.x() + 0.5, ar.floor() + 1, ar.z() + 0.5), ar); });
			if (tt == 821) mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			if (tt > 821 && tt < 920) lookAt(mc, ar.x(), ar.floor() + 4, ar.z());
			if (tt == 860) shot(mc, "73_arena_fight");
			if (tt == 865) server.execute(() -> { if (bossRef != null) bossRef.debugDamage((ServerLevel) bossRef.level(), (long) (bossRef.maxBigHealth() * 0.7)); });
			if (tt == 905) shot(mc, "74_arena_phase3");
			if (tt == 915) server.execute(() -> { if (bossRef != null) bossRef.discard(); bossRef = null; ServerPlayer p = sp(server);
				server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "gamemode survival " + p.getScoreboardName()); });
		}
		// 나무 무기 1인칭 + 스킬바 / 속성 진화 창 / 스킬 연출
		if (tt == 932) server.execute(() -> { ServerPlayer p = sp(server); p.teleportTo(0.5, 172, 48.5);
			ModGrowth.set(p, ModGrowth.get(p).withClass(com.elementalweapons.item.PlayerClass.KNIGHT.ordinal()));
			p.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WOODEN.get(WeaponType.SWORD)));
			p.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(ModItems.WOODEN.get(WeaponType.SHIELD))); });
		if (tt == 933) mc.options.setCameraType(CameraType.FIRST_PERSON);
		if (tt > 933 && tt < 960) look(mc, 0, 4);
		if (tt == 955) shot(mc, "75_wooden_firstperson");
		if (tt == 958) server.execute(() -> sp(server).setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY));
		if (tt == 960) mc.gui.setScreen(new com.elementalweapons.client.skill.EvolveScreen());
		if (tt == 975) shot(mc, "76_evolve_screen");
		if (tt == 977) mc.gui.setScreen(null);
		if (tt >= 980 && tt < 980 + SKILL_SHOTS.length * 40) skillDemo(mc, server, tt - 980);
		if (tt == 1310) { mc.options.setCameraType(CameraType.FIRST_PERSON); armorSet(server, "orichalcum"); }
	}

	private static com.elementalweapons.boss.ElementalBoss bossRef;

	/** 스킬 연출 촬영: {무기 종류, 속성, 슬롯, 직업} */
	private static final Object[][] SKILL_SHOTS = {
			{WeaponType.GREATSWORD, Element.FIRE, 1, com.elementalweapons.item.PlayerClass.WARRIOR},
			{WeaponType.SCYTHE, Element.DARK, 2, com.elementalweapons.item.PlayerClass.REAPER},
			{WeaponType.STAFF, Element.LIGHT, 2, com.elementalweapons.item.PlayerClass.MAGE},
			{WeaponType.HAMMER, Element.WIND, 0, com.elementalweapons.item.PlayerClass.WARRIOR},
			{WeaponType.SWORD, Element.WATER, 1, com.elementalweapons.item.PlayerClass.KNIGHT},
			{WeaponType.BOW, Element.ELECTRIC, 2, com.elementalweapons.item.PlayerClass.RANGER},
			{WeaponType.BOOK, Element.GRASS, 2, com.elementalweapons.item.PlayerClass.SUPPORT},
			{WeaponType.DAGGER, Element.FIRE, 1, com.elementalweapons.item.PlayerClass.ASSASSIN},
	};

	private static void skillDemo(Minecraft mc, IntegratedServer server, int tt) {
		int idx = tt / 40, k = tt % 40;
		if (idx >= SKILL_SHOTS.length) return;
		Object[] q = SKILL_SHOTS[idx];
		WeaponType wt = (WeaponType) q[0]; Element el = (Element) q[1]; int slot = (Integer) q[2];
		var cls = (com.elementalweapons.item.PlayerClass) q[3];
		if (k == 0) {
			server.execute(() -> { ServerPlayer p = sp(server);
				ModGrowth.set(p, ModGrowth.get(p).withClass(cls.ordinal()));
				ItemStack st = new ItemStack(ModItems.WEAPONS.get(el).get(wt));
				com.elementalweapons.growth.Evolution.setTier(st, 4);
				p.setItemSlot(EquipmentSlot.MAINHAND, st);
				p.teleportTo(0.5, 172, 48.5); });
			mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
		}
		if (k > 0) look(mc, 0, 22);
		if (k == 8) com.elementalweapons.client.skill.SkillClient.press(slot);
		if (k == 15) shot(mc, String.format("8%d_skill_%s_%d", idx, wt.id, slot));
		if (k == 26 && (wt == WeaponType.STAFF || wt == WeaponType.BOOK || wt == WeaponType.SCYTHE)) shot(mc, String.format("8%d_skill_%s_%d_b", idx, wt.id, slot));
	}

	private static void lookAt(Minecraft mc, double x, double y, double z) {
		double ddx = x + 0.5 - mc.player.getX(), ddz = z + 0.5 - mc.player.getZ(), ddy = y - mc.player.getEyeY();
		look(mc, (float) Math.toDegrees(Math.atan2(-ddx, ddz)), (float) -Math.toDegrees(Math.atan2(ddy, Math.hypot(ddx, ddz))));
	}

	private static void townScenes(Minecraft mc, IntegratedServer server, int tt) {
		if (tt < 1320) { demo(mc, server, tt); return; }
		tt -= 1320;
		double[][] sc = scenes();
		int per = 420;
		int idx = tt / per, k = tt % per;
		if (idx >= sc.length) { if (k == 5) { log("DONE"); mc.stop(); } return; }
		double[] c = sc[idx];
		if (k == 0) mc.options.setCameraType(CameraType.FIRST_PERSON);
		if (k == 0) server.execute(() -> {
			ServerPlayer p = sp(server);
			p.getAbilities().flying = true; p.getAbilities().mayfly = true; p.onUpdateAbilities();
			p.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
			p.teleportTo(c[0] + 0.5, c[1], c[2] + 0.5);
			server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set " + (long) c[6]);
		});
		if (k == per - 40) server.execute(() -> server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set " + (long) c[6]));
		if (k > 0) {
			double ddx = c[3] - mc.player.getX(), ddz = c[5] - mc.player.getZ(), ddy = c[4] - mc.player.getEyeY();
			float yaw = (float) Math.toDegrees(Math.atan2(-ddx, ddz));
			float pitch = (float) -Math.toDegrees(Math.atan2(ddy, Math.hypot(ddx, ddz)));
			look(mc, yaw, pitch);
			mc.player.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
		}
		if (k == per - 10) shot(mc, String.format("5%02d_sky_scene", idx));
		if (k == per - 12) server.execute(() -> { ServerPlayer p = sp(server);
			int n = p.level().getEntitiesOfClass(com.elementalweapons.mob.ElementalMob.class, p.getBoundingBox().inflate(64)).size();
			int cc = p.level().getEntitiesOfClass(com.elementalweapons.mob.ElementalCreature.class, p.getBoundingBox().inflate(64)).size();
			log("장면 " + idx + " 주변 몹 " + n + " / 동물 " + cc); });
	}

	private static void look(Minecraft mc, float yaw, float pitch) {
		mc.player.setYRot(yaw); mc.player.setXRot(pitch);
		mc.player.setYHeadRot(yaw);
	}

	private static void aim(Minecraft mc, Entity e) {
		double dy = e.getY() + e.getBbHeight() * 0.5 - (mc.player.getY() + 1.62);
		double dz = e.getZ() - mc.player.getZ();
		float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.max(1, dz)));
		look(mc, 0, pitch);
	}

	private static void setup(IntegratedServer server) {
		ServerPlayer p = sp(server);
		ServerLevel level = (ServerLevel) p.level();
		int bx = (int) Math.floor(p.getX()), bz = (int) Math.floor(p.getZ());
		int top = 0;
		for (int dx = -4; dx <= 4; dx++) for (int dz = -2; dz <= 40; dz++)
			top = Math.max(top, level.getHeight(Heightmap.Types.MOTION_BLOCKING, bx + dx, bz + dz));
		BlockState floor = ModBlocks.BLOCKS.containsKey("blossom_grass") ? ModBlocks.BLOCKS.get("blossom_grass").defaultBlockState() : Blocks.GRASS_BLOCK.defaultBlockState();
		for (int dx = -5; dx <= 5; dx++) for (int dz = -3; dz <= 42; dz++)
			level.setBlock(new BlockPos(bx + dx, top, bz + dz), floor, 3);
		baseX = bx + 0.5; baseY = top + 1; baseZ = bz + 0.5;
		p.setGameMode(GameType.SURVIVAL);
		p.getAbilities().invulnerable = true;
		p.onUpdateAbilities();
		p.teleportTo(baseX, baseY, baseZ);
		Item[] hot = {ModItems.WEAPONS.get(Element.ELECTRIC).get(WeaponType.STAFF), ModItems.WEAPONS.get(Element.FIRE).get(WeaponType.GREATSWORD),
				ModItems.WEAPONS.get(Element.LIGHT).get(WeaponType.SWORD), ModItems.WEAPONS.get(Element.DARK).get(WeaponType.DAGGER),
				ModGear.ITEMS.get("orichalcum_pickaxe"), ModItems.ELEMENT_ESSENCE, ModMobs.COOKED_ELEMENTAL_MEAT};
		for (int i = 0; i < hot.length; i++) if (hot[i] != null) p.getInventory().setItem(i, new ItemStack(hot[i], i == 5 ? 12 : 1));
		long xp = Math.round(Growth.need(23) * 0.68);
		ModGrowth.set(p, new Growth(23, xp, 3, 18, 10, 14, 10, 3));
		ModGrowth.applyStats(p);
		log("setup at " + baseX + "," + baseY + "," + baseZ);
	}

	/** 무기 진열 벽: 액자 7열 × 7행 (위에서부터 종류, 가로는 속성) */
	private static void showcase(IntegratedServer server) {
		ServerLevel level = (ServerLevel) sp(server).level();
		int wz = (int) Math.floor(baseZ) + 9, by = (int) Math.floor(baseY);
		WeaponType[] order = {WeaponType.STAFF, WeaponType.GREATSWORD, WeaponType.HAMMER, WeaponType.SWORD, WeaponType.SHIELD, WeaponType.DAGGER, WeaponType.BOOK, WeaponType.SCYTHE, WeaponType.BOW};
		BlockState wall = Blocks.POLISHED_DEEPSLATE.defaultBlockState();
		for (int dx = -5; dx <= 5; dx++) for (int dy = -2; dy <= 9; dy++)
			level.setBlock(new BlockPos((int) Math.floor(baseX) + dx, by + dy, wz), dx == -5 || dx == 5 || dy == -2 || dy == 9 ? Blocks.GILDED_BLACKSTONE.defaultBlockState() : wall, 3);
		for (int c = 0; c < 9; c++) {   // 맨 아래 줄: 나무 무기
			BlockPos pos = new BlockPos((int) Math.floor(baseX) - 4 + c, by - 1, wz - 1);
			net.minecraft.world.entity.decoration.GlowItemFrame f = new net.minecraft.world.entity.decoration.GlowItemFrame(level, pos, net.minecraft.core.Direction.NORTH);
			f.setItem(new ItemStack(ModItems.WOODEN.get(WeaponType.values()[c])));
			level.addFreshEntity(f); gallery.add(f);
		}
		// 단계 벽: 1~4단계 (가로) × 대검·지팡이·낫·활 (세로)
		int tx = (int) Math.floor(baseX) + 12;
		WeaponType[] tw = {WeaponType.GREATSWORD, WeaponType.STAFF, WeaponType.SCYTHE, WeaponType.BOW};
		Element[] te = {Element.FIRE, Element.WATER, Element.DARK, Element.ELECTRIC};
		for (int dx = -3; dx <= 4; dx++) for (int dy = -1; dy <= 5; dy++)
			level.setBlock(new BlockPos(tx + dx, by + dy, wz), dx == -3 || dx == 4 || dy == -1 || dy == 5 ? Blocks.GILDED_BLACKSTONE.defaultBlockState() : wall, 3);
		for (int r = 0; r < 4; r++) for (int t = 0; t < 4; t++) {
			BlockPos pos = new BlockPos(tx - 2 + t * 2 - (t > 1 ? 1 : 0), by + 4 - r, wz - 1);
			net.minecraft.world.entity.decoration.GlowItemFrame f = new net.minecraft.world.entity.decoration.GlowItemFrame(level, pos, net.minecraft.core.Direction.NORTH);
			ItemStack st = new ItemStack(ModItems.WEAPONS.get(te[r]).get(tw[r]));
			com.elementalweapons.growth.Evolution.setTier(st, t + 1);
			f.setItem(st);
			level.addFreshEntity(f); gallery.add(f);
		}
		for (int r = 0; r < order.length; r++) {
			for (int c = 0; c < 7; c++) {
				BlockPos pos = new BlockPos((int) Math.floor(baseX) - 3 + c, by + 8 - r, wz - 1);
				net.minecraft.world.entity.decoration.GlowItemFrame f = new net.minecraft.world.entity.decoration.GlowItemFrame(level, pos, net.minecraft.core.Direction.NORTH);
				f.setItem(new ItemStack(ModItems.WEAPONS.get(Element.values()[c]).get(order[r])));
				level.addFreshEntity(f);
				gallery.add(f);
			}
		}
	}

	private static void gallery(IntegratedServer server, WeaponType wt) {
		ServerLevel level = (ServerLevel) sp(server).level();
		int i = 0;
		for (Element el : Element.values()) {
			Entity e = net.minecraft.world.entity.EntityTypes.HUSK.create(level, EntitySpawnReason.COMMAND);
			if (e != null) e.addTag(com.elementalweapons.world.VanillaPurge.ALLOWED);
			if (e == null) continue;
			e.snapTo(baseX + (i - 3) * 1.25, baseY, baseZ + 3.2);
			e.setYRot(180);
			if (e instanceof Mob m) {
				m.setNoAi(true); m.setYHeadRot(180); m.setYBodyRot(180);
				m.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WEAPONS.get(el).get(wt)));
			}
			level.addFreshEntity(e);
			gallery.add(e);
			i++;
		}
	}

	private static void spawn(IntegratedServer server, EntityType<?> type) {
		ServerLevel level = (ServerLevel) sp(server).level();
		Entity e = type.create(level, EntitySpawnReason.COMMAND);
		if (e == null) { log("create failed " + type); return; }
		double dist = 5 + e.getBbWidth() * 1.6 + e.getBbHeight() * 0.7;
		e.snapTo(baseX, baseY, baseZ + dist);
		e.setYRot(215);
		if (e instanceof Mob m) { m.setNoAi(true); m.setYHeadRot(215); m.setYBodyRot(215); }
		level.addFreshEntity(e);
		current = e;
	}
}
