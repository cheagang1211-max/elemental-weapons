package com.elementalweapons.growth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.ModItems;
import com.elementalweapons.item.PlayerClass;
import com.elementalweapons.item.WeaponType;
import com.elementalweapons.spell.Fx;
import com.elementalweapons.world.sky.Arenas;
import com.elementalweapons.world.sky.SkyLayout;

/**
 * 튜토리얼 (퀘스트가 아니라 처음 길잡이):
 *  0 나무 무기 받기(자동) → 1 나무 무기 기술 → 2 Z 구르기 → 3 원소 지역 → 4 몹 5마리 → 5 스탯
 *  → (6 광석: 없앰, 자동으로 건너뜀) → 7 10레벨 전직(직업+속성) → 8 X·C 스킬 → 9 보스방 → 10 완료(보상)
 *  10레벨 전까지는 모든 나무 무기를 써 볼 수 있음
 *  조건으로 판정하는 단계(직업·대교·스탯·진화·보스방)는 순서가 꼬여도 이미 채웠으면 바로 넘어감
 */
public final class Tutorial {
	private Tutorial() {}

	public record Data(int step, int count, int kits) {
		public static final Data DEFAULT = new Data(0, 0, 0);
		public static final Codec<Data> CODEC = RecordCodecBuilder.create(i -> i.group(
				Codec.INT.fieldOf("step").forGetter(Data::step),
				Codec.INT.fieldOf("count").forGetter(Data::count),
				Codec.INT.optionalFieldOf("kits", 0).forGetter(Data::kits)).apply(i, Data::new));
		public static final StreamCodec<ByteBuf, Data> STREAM = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, Data::step, ByteBufCodecs.VAR_INT, Data::count, ByteBufCodecs.VAR_INT, Data::kits, Data::new);
	}

	public static final int DONE = 10;
	/** 화면에 보이는 단계 수 (광석 단계를 뺀 8단계) */
	public static final int TOTAL = 8;

	/** 내부 단계 → 화면 번호 (1~8) */
	public static int display(int step) { return step <= 5 ? Math.max(1, step) : step - 1; }

	public static final AttachmentType<Data> DATA = AttachmentRegistry.<Data>builder()
			.persistent(Data.CODEC).copyOnDeath().initializer(() -> Data.DEFAULT)
			.syncWith(Data.STREAM, AttachmentSyncPredicate.targetOnly())
			.buildAndRegister(ElementalWeaponsMod.id("tutorial"));

	public static Data get(Player p) { Data d = p.getAttached(DATA); return d == null ? Data.DEFAULT : d; }

	// ---------------- 단계 문구 (클라이언트 HUD도 같이 씀) ----------------
	public static int goal(int step, PlayerClass c) { return step == 4 ? 5 : 1; }

	private static String weapon(PlayerClass c) {
		return switch (c) {
			case MAGE -> "나무 지팡이"; case WARRIOR -> "나무 대검(또는 망치)"; case KNIGHT -> "나무 검"; case ASSASSIN -> "나무 단검";
			case SUPPORT -> "나무 마법서"; case REAPER -> "나무 낫"; case RANGER -> "나무 활"; default -> "무기";
		};
	}

	public static String title(int step, PlayerClass c) {
		return switch (step) {
			case 0 -> "나무 무기 받기";
			case 1 -> c == PlayerClass.NONE ? "나무 무기 기술 써 보기" : weapon(c) + " 기술 써 보기";
			case 2 -> "[Z] 구르기로 피하기";
			case 3 -> "용을 타고(또는 대교로) 원소 지역으로";
			case 4 -> "지역 몹 처치";
			case 5 -> "[K] 성장 창에서 스탯 찍기";
			case 6 -> "섬에서 원소 광석 캐기";
			case 7 -> "10레벨 달성 → 전직 (직업·속성)";
			case 8 -> "[X]·[C] 속성 스킬 써 보기";
			case 9 -> "보스방 찾아가기";
			default -> "튜토리얼 완료!";
		};
	}

	public static String hint(int step, PlayerClass c) {
		return switch (step) {
			case 0 -> "곧 모든 직업의 나무 무기를 받아요";
			case 1 -> switch (c) {
				case MAGE -> "우클릭: 마력 구슬 (멀리 있는 적을 맞혀요)";
				case WARRIOR -> "우클릭: 대검은 넓게 베기, 망치는 땅 내리치기";
				case KNIGHT -> "우클릭: 앞의 적 찌르기 · 방패는 왼손에 들고 우클릭으로 막기";
				case ASSASSIN -> "우클릭: 짧게 돌진";
				case SUPPORT -> "우클릭: 주변 아군과 나를 치유";
				case REAPER -> "우클릭: 주변을 한 바퀴 베기";
				case RANGER -> "우클릭을 누르고 있다가 떼면 발사";
				default -> "아무 나무 무기나 들고 우클릭 · 10레벨 전까지 모든 무기를 써 볼 수 있어요";
			};
			case 2 -> "Z: 바라보는 쪽으로 구르며 잠깐 무적";
			case 3 -> "1층 남쪽 용의 둥지에서 용을 우클릭해 타세요 (대교로 걸어가도 돼요)";
			case 4 -> switch (c) {
				case MAGE, SUPPORT, RANGER -> "거리를 벌리고 싸우세요 · 밤에 몹이 더 많아요";
				default -> "좌클릭을 이어 치면 콤보 · 밤에 몹이 더 많아요";
			};
			case 5 -> "레벨이 오르면 포인트 3개 · 힘/마력/체력/민첩";
			case 6 -> "섬 땅속의 빛나는 광석 · 장비·강화 재료";
			case 7 -> "10레벨이 되면 전직 창이 열려요 · 직업을 고른 뒤 7속성 중 하나를 고르세요";
			case 8 -> {
				var i = com.elementalweapons.skill.SkillInfo.of(firstType(c));
				yield "X: " + i[1].name() + " · C: " + i[2].name();
			}
			case 9 -> "지역 본섬 가운데의 원형 무대";
			default -> "이제 자유롭게 탐험하세요 · 균열의 열쇠로 무한 도전";
		};
	}

	private static WeaponType firstType(PlayerClass c) {
		for (WeaponType t : WeaponType.values()) if (t.playerClass() == c && t != WeaponType.SHIELD) return t;
		return WeaponType.SWORD;
	}

	// ---------------- 서버: 진행 ----------------
	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 20 != 3) return;
			for (ServerPlayer p : server.getPlayerList().getPlayers()) {
				try { checkState(p); } catch (Throwable t) { ElementalWeaponsMod.LOGGER.error("[TUTORIAL] 오류", t); }
			}
		});
	}

	/** 상태로 판정하는 단계: 이미 조건을 채웠으면 바로 넘어감 (순서가 꼬여도 막히지 않게) */
	private static void checkState(ServerPlayer p) {
		Data d = get(p);
		Growth g = ModGrowth.get(p);
		switch (d.step) {
			case 0 -> { if ((d.kits & STARTER_BIT) != 0) advance(p); }
			case 6 -> advance(p); // 광석 단계는 없어짐
			case 3 -> { if (SkyLayout.regionAt(p.getX(), p.getZ()) >= 0 || !com.elementalweapons.world.sky.ModSky.active((ServerLevel) p.level())) advance(p); }
			case 5 -> { if (totalStats(p) > 0) advance(p); }
			case 7 -> { if (Evolution.element(p) >= 0) advance(p); }
			case 9 -> { if (Arenas.near(p.getX(), p.getZ(), Arenas.R + 6) != null || !com.elementalweapons.world.sky.ModSky.active((ServerLevel) p.level())) advance(p); }
			default -> {}
		}
	}

	private static int totalStats(Player p) { Growth g = ModGrowth.get(p); return g.str() + g.mag() + g.vit() + g.agi(); }

	/** 이벤트: 해당 단계면 1 증가 */
	public static void event(Player player, int step) {
		if (!(player instanceof ServerPlayer p)) return;
		Data d = get(p);
		if (d.step != step) return;
		int need = goal(step, ModGrowth.get(p).playerClass());
		if (d.count + 1 >= need) advance(p);
		else {
			p.setAttached(DATA, new Data(d.step, d.count + 1, d.kits));
			Fx.sound((ServerLevel) p.level(), p.position(), SoundEvents.EXPERIENCE_ORB_PICKUP, 0.6F, 1.4F);
		}
	}

	/** 예전 직업 특기 이벤트 (지금은 쓰지 않음) */
	public static void classEvent(Player player, String what) {}

	public static void skillUsed(ServerPlayer p, int slot) {
		int step = get(p).step;
		if (step == 2 && slot == 0) event(p, 2);
		else if (step == 8 && slot > 0) event(p, 8);
	}

	public static void evolved(ServerPlayer p) { if (get(p).step == 7) advance(p); }

	private static final int ARMOR_BIT = 1 << 30;

	private static final int AETHER_BIT = 1 << 27;
	private static final String[] AETHER_SET = {"aether_helmet", "aether_chestplate", "aether_leggings", "aether_boots"};
	private static final String[] IRON_SET = {"iron_helmet", "iron_chestplate", "iron_leggings", "iron_boots"};

	/** 에테르 갑옷 한 벌 (플레이어마다 한 번). 예전에 받은 철 갑옷은 에테르 갑옷으로 바꿔 줌 */
	private static int armor(ServerPlayer p, int kits) {
		if ((kits & AETHER_BIT) != 0) return kits;
		kits |= AETHER_BIT | ARMOR_BIT;
		net.minecraft.world.entity.EquipmentSlot[] slots = {net.minecraft.world.entity.EquipmentSlot.HEAD, net.minecraft.world.entity.EquipmentSlot.CHEST, net.minecraft.world.entity.EquipmentSlot.LEGS, net.minecraft.world.entity.EquipmentSlot.FEET};
		var inv = p.getInventory();
		for (int i = 0; i < 4; i++) {
			// 시작 철 갑옷 회수
			for (int k = 0; k < inv.getContainerSize(); k++)
				if (net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(inv.getItem(k).getItem()).getPath().equals(IRON_SET[i])) { inv.setItem(k, ItemStack.EMPTY); break; }
			ItemStack worn = p.getItemBySlot(slots[i]);
			if (net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(worn.getItem()).getPath().equals(IRON_SET[i])) p.setItemSlot(slots[i], ItemStack.EMPTY);
			ItemStack s = new ItemStack(com.elementalweapons.item.ModGear.ITEMS.get(AETHER_SET[i]));
			if (p.getItemBySlot(slots[i]).isEmpty()) p.setItemSlot(slots[i], s);
			else if (!inv.add(s)) p.spawnAtLocation((ServerLevel) p.level(), s);
		}
		p.sendSystemMessage(Component.literal("✦ 에테르 갑옷 한 벌을 받았습니다! (다이아몬드보다 튼튼하고 내구도 무한)").withStyle(ChatFormatting.AQUA));
		return kits;
	}

	private static final int STARTER_BIT = 1 << 29;

	/** 접속할 때: 전직 전이면 모든 직업의 나무 무기 + 철 방어구 (플레이어마다 한 번) */
	public static void grantArmorIfMissing(ServerPlayer p) {
		Data d = get(p);
		int kits = d.kits;
		if ((kits & STARTER_BIT) == 0 && Evolution.element(p) < 0) {
			kits |= STARTER_BIT;
			var inv = p.getInventory();
			for (WeaponType t : WeaponType.values()) {
				if (t.playerClass() == PlayerClass.NONE) continue;
				net.minecraft.world.item.Item w = ModItems.WOODEN.get(t);
				if (w == null) continue;
				boolean has = false;
				for (int i = 0; i < inv.getContainerSize(); i++) if (inv.getItem(i).is(w)) { has = true; break; }
				if (has) continue;
				ItemStack s = new ItemStack(w);
				if (!inv.add(s)) p.spawnAtLocation((ServerLevel) p.level(), s);
			}
			p.sendSystemMessage(Component.literal("✦ 모든 직업의 나무 무기를 받았습니다! 10레벨 전까지 마음껏 써 보고, 10레벨 전직 때 직업과 속성을 고르세요.").withStyle(ChatFormatting.AQUA));
			p.sendSystemMessage(Component.literal("⚔ 좌클릭을 연달아 3번 하면 무기마다 다른 마무리 기술이 나갑니다 (지팡이·마법서·활은 좌클릭으로 원거리 공격)").withStyle(ChatFormatting.YELLOW));
		}
		if ((kits & STARTER_BIT) == 0) kits |= STARTER_BIT; // 이미 전직한 사람
		kits = armor(p, kits);
		if (kits != d.kits) p.setAttached(DATA, new Data(d.step, d.count, kits));
	}

	public static void onClassChosen(ServerPlayer p, int cls) {
		Data d = get(p);
		PlayerClass c = PlayerClass.of(cls);
		int bit = 1 << cls;
		int kits = d.kits;
		if (Evolution.element(p) < 0 && ModGrowth.get(p).level() < Evolution.LEVEL && (kits & bit) == 0) {
			kits |= bit;
			for (ItemStack s : kit(c)) if (!p.getInventory().add(s)) p.spawnAtLocation((ServerLevel) p.level(), s);
			p.sendSystemMessage(Component.literal("✦ " + c.koName + "의 나무 무기를 받았습니다! 10레벨에 속성 진화를 하면 속성 무기로 바뀝니다.").withStyle(ChatFormatting.AQUA));
			p.sendSystemMessage(Component.literal("⚔ 좌클릭을 연달아 3번 하면 무기마다 다른 마무리 기술이 나갑니다 (지팡이·마법서·활은 좌클릭으로 원거리 공격)").withStyle(ChatFormatting.YELLOW));
		}
		kits = armor(p, kits);
		p.setAttached(DATA, new Data(d.step >= DONE ? DONE : Math.max(d.step, 1), 0, kits));
		Evolution.check(p);
	}

	/** 직업의 나무 무기 (직업이 쓰는 종류 전부) */
	private static ItemStack[] kit(PlayerClass c) {
		java.util.List<ItemStack> out = new java.util.ArrayList<>();
		for (WeaponType t : WeaponType.values()) if (t.playerClass() == c) out.add(new ItemStack(ModItems.WOODEN.get(t)));
		return out.toArray(new ItemStack[0]);
	}

	private static void advance(ServerPlayer p) {
		Data d = get(p);
		int next = d.step + 1;
		if (next == 6) next = 7; // 광석 단계 건너뜀
		p.setAttached(DATA, new Data(next, 0, d.kits));
		ServerLevel level = (ServerLevel) p.level();
		PlayerClass c = ModGrowth.get(p).playerClass();
		if (next >= DONE) {
			p.sendSystemMessage(Component.literal("✦ 튜토리얼 완료! 보상: 원소 정수 3개, 균열의 열쇠 1개. 이제 자유롭게 탐험하세요.").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
			for (ItemStack s : new ItemStack[]{new ItemStack(ModItems.ELEMENT_ESSENCE, 3), new ItemStack(com.elementalweapons.rift.RiftItems.RIFT_KEY, 1)})
				if (!p.getInventory().add(s)) p.spawnAtLocation(level, s);
			Fx.sound(level, p.position(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 1.0F);
			return;
		}
		ModGrowth.addXp(p, 20L * next);
		p.sendSystemMessage(Component.literal("✔ 튜토리얼 " + display(next) + "/" + TOTAL + " → " + title(next, c)).withStyle(ChatFormatting.GREEN));
		Fx.sound(level, p.position(), SoundEvents.PLAYER_LEVELUP, 0.6F, 1.6F);
		Fx.burst(level, Fx.dust(0x7CFFB0, 1.2F), p.position().add(0, 1, 0), 24, 0.5, 0.05);
	}

	/** 튜토리얼 건너뛰기 / 처음부터 */
	public static void set(ServerPlayer p, int step) { Data d = get(p); p.setAttached(DATA, new Data(step, 0, d.kits)); }
}
