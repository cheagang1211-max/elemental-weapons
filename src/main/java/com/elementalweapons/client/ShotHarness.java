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
			if (t == 426) mc.gui.setScreen(new com.elementalweapons.client.growth.ClassSelectScreen());
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
			if (t == WEAP_START + 112) { server.execute(() -> { sp(server).teleportTo(baseX, baseY, baseZ); for (Entity e : gallery) e.discard(); gallery.clear(); }); }
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

	private static BlockPos town;

	private static int groundY = 0;

	private static void tp(IntegratedServer server, double x, double y, double z) { server.execute(() -> sp(server).teleportTo(x, y, z)); }

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
				{tx * 0.78 + tz * 0.12, 200, tz * 0.78 - tx * 0.12, tx, 210, tz, 6000},   // 원소 마법탑
				{330, 236, 60, 300, 222, -60, 6000},                          // 떠 있는 섬과 사슬
				{0, 230, 300, 0, 300, -150, 18000},                           // 밤의 수도와 별
				{60, 200, 60, -200, 420, -300, 18000},                        // 별이 가득한 밤하늘
				{dx * 420 - dz * 50, 168, dz * 420 + dx * 50, dx * 640, 162, dz * 640, 12600},   // 노을 무렵 대교
				{rc[0] - dx * 560 - dz * 120, 235, rc[1] - dz * 560 + dx * 120, rc[0], 165, rc[1], 6000}, // 지역 본섬 전경
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
				p.teleportTo(0.5, 172, 34.5);
				server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), "time set 6000");
			});
			armorSet(server, "aether");
			mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT);
		}
		if (tt > 0 && tt < 45) look(mc, 180, 8);
		if (tt == 40) shot(mc, "60_armor_aether_front");
		if (tt >= 45 && tt < 75) look(mc, 215, 5);
		if (tt == 72) shot(mc, "61_armor_aether_side");
		if (tt >= 75 && tt < 105) look(mc, 20, 5);
		if (tt == 102) shot(mc, "61b_armor_aether_back");
		if (tt == 105) armorSet(server, "orichalcum");
		if (tt > 105 && tt < 135) look(mc, 155, 8);
		if (tt == 132) shot(mc, "62_armor_orichalcum");
		if (tt == 135) {
			armorSet(server, "aether");
			server.execute(() -> sp(server).setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WEAPONS.get(Element.DARK).get(WeaponType.SCYTHE))));
			mc.options.setCameraType(CameraType.FIRST_PERSON);
		}
		if (tt > 135 && tt < 160) look(mc, 180, 5);
		if (tt == 158) shot(mc, "63_fp_scythe");
		if (tt == 160) {
			mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			server.execute(() -> {
				ServerPlayer p = sp(server);
				ServerLevel level = (ServerLevel) p.level();
				for (int i = 0; i < 6; i++) {
					Entity e = net.minecraft.world.entity.EntityTypes.HUSK.create(level, EntitySpawnReason.COMMAND);
					if (e == null) continue;
					double a = i * Math.PI / 3;
					e.snapTo(p.getX() + Math.cos(a) * 3.6, p.getY(), p.getZ() + Math.sin(a) * 3.6);
					if (e instanceof Mob m) m.setNoAi(true);
					level.addFreshEntity(e); dummies.add(e);
				}
			});
		}
		if (tt > 160 && tt < 200) look(mc, 180, 22);
		if (tt == 180) server.execute(() -> { ServerPlayer p = sp(server); com.elementalweapons.spell.SpellManager.add(new com.elementalweapons.spell.ReapSpell((ServerLevel) p.level(), p, Element.DARK)); });
		if (tt == 183) shot(mc, "64_reap_a");
		if (tt == 186) shot(mc, "64_reap_b");
		if (tt == 189) shot(mc, "64_reap_c");
		if (tt == 200) {
			server.execute(() -> { for (Entity e : dummies) e.discard(); dummies.clear();
				sp(server).setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.WEAPONS.get(Element.FIRE).get(WeaponType.BOW))); });
			mc.options.setCameraType(CameraType.FIRST_PERSON);
		}
		if (tt > 200 && tt < 260) look(mc, 180, -4);
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
		if (tt == 270) { mc.options.setCameraType(CameraType.FIRST_PERSON); armorSet(server, "orichalcum"); }
	}

	private static void townScenes(Minecraft mc, IntegratedServer server, int tt) {
		if (tt < 280) { demo(mc, server, tt); return; }
		tt -= 280;
		double[][] sc = scenes();
		int per = 420;
		int idx = tt / per, k = tt % per;
		if (idx >= sc.length) { if (k == 5) { log("DONE"); mc.stop(); } return; }
		double[] c = sc[idx];
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
		if (k == per - 10) shot(mc, String.format("5%d_sky_scene", idx));
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
		for (int dx = -5; dx <= 5; dx++) for (int dy = -1; dy <= 9; dy++)
			level.setBlock(new BlockPos((int) Math.floor(baseX) + dx, by + dy, wz), dx == -5 || dx == 5 || dy == -1 || dy == 9 ? Blocks.GILDED_BLACKSTONE.defaultBlockState() : wall, 3);
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
