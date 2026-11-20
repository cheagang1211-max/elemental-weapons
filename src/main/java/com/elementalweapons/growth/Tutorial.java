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
 * 직업별 튜토리얼 (퀘스트가 아니라 처음 길잡이):
 *  0 직업 고르기 → 1 직업 스킬 써 보기 → 2 직업 특기 익히기 → 3 대교 건너기 → 4 지역 몹 5마리
 *  → 5 스탯 찍기 → 6 원소 광석 캐기 → 7 보스방 찾아가기 → 8 완료(보상)
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

	public static final int DONE = 8;

	public static final AttachmentType<Data> DATA = AttachmentRegistry.<Data>builder()
			.persistent(Data.CODEC).copyOnDeath().initializer(() -> Data.DEFAULT)
			.syncWith(Data.STREAM, AttachmentSyncPredicate.targetOnly())
			.buildAndRegister(ElementalWeaponsMod.id("tutorial"));

	public static Data get(Player p) { Data d = p.getAttached(DATA); return d == null ? Data.DEFAULT : d; }

	// ---------------- 단계 문구 (클라이언트 HUD도 같이 씀) ----------------
	/** 각 단계 목표 개수 */
	public static int goal(int step, PlayerClass c) {
		return switch (step) {
			case 2 -> switch (c) { case WARRIOR, ASSASSIN -> 3; case SUPPORT -> 2; default -> 1; };
			case 4 -> 5;
			default -> 1;
		};
	}

	public static String title(int step, PlayerClass c) {
		return switch (step) {
			case 0 -> "직업 고르기";
			case 1 -> switch (c) {
				case MAGE -> "지팡이 대마법 써 보기";
				case WARRIOR -> "대검 6방향 강타 써 보기";
				case KNIGHT -> "검의 원거리 일격 써 보기";
				case ASSASSIN -> "단검 돌진 써 보기";
				case SUPPORT -> "마법서 축복 써 보기";
				case REAPER -> "낫 회전 베기 써 보기";
				case RANGER -> "활 당겨 쏘기";
				default -> "직업 스킬 써 보기";
			};
			case 2 -> switch (c) {
				case MAGE -> "지팡이 마법으로 적 처치";
				case WARRIOR -> "근접 콤보: 적을 연속으로 때리기";
				case KNIGHT -> "방패로 공격 막아 반격하기";
				case ASSASSIN -> "빠른 단검으로 연속 공격";
				case SUPPORT -> "마법서 축복 다시 쓰기";
				case REAPER -> "회전 베기로 적 2마리 이상 동시에 베기";
				case RANGER -> "끝까지 당겨 5연발 쏘기";
				default -> "직업 특기 익히기";
			};
			case 3 -> "대교를 건너 원소 지역으로";
			case 4 -> "지역 몹 처치";
			case 5 -> "[K] 성장 창에서 스탯 찍기";
			case 6 -> "섬에서 원소 광석 캐기";
			case 7 -> "보스방 찾아가기";
			default -> "튜토리얼 완료!";
		};
	}

	public static String hint(int step, PlayerClass c) {
		return switch (step) {
			case 0 -> "K 키를 누르면 직업 선택 창이 열려요";
			case 1 -> switch (c) {
				case MAGE -> "지팡이를 들고 우클릭 (멀리 있는 적도 OK)";
				case WARRIOR -> "대검을 들고 우클릭 · 망치는 내려찍기";
				case KNIGHT -> "검을 들고 바라보는 곳에 우클릭";
				case ASSASSIN -> "단검을 들고 우클릭하면 앞으로 돌진";
				case SUPPORT -> "마법서를 들고 우클릭 → 주변 아군 강화";
				case REAPER -> "낫을 들고 우클릭 → 한 바퀴 베기";
				case RANGER -> "우클릭을 누르고 있다가 떼면 발사";
				default -> "무기를 들고 우클릭";
			};
			case 2 -> switch (c) {
				case MAGE -> "지팡이 마법은 적대 몹만 노려요";
				case WARRIOR -> "좌클릭을 이어서 하면 궤적이 콤보로 바뀌어요";
				case KNIGHT -> "왼손 방패를 우클릭으로 들고 공격을 받아내기";
				case ASSASSIN -> "단검은 약하지만 매우 빨라요";
				case SUPPORT -> "쿨타임이 끝나면 다시 우클릭";
				case REAPER -> "적이 몰려 있을 때 우클릭";
				case RANGER -> "1초 이상 당기면 최대 위력 (부채꼴 5발)";
				default -> "";
			};
			case 3 -> "수도 가장자리의 대교로 걸어가세요. 떨어져도 안전해요";
			case 4 -> switch (c) {
				case MAGE, SUPPORT -> "멀리서 마법으로 · 가까이 오면 물러나기";
				case RANGER -> "거리를 벌리고 활로 · 몹은 지역마다 속성이 달라요";
				default -> "몹은 지역마다 속성이 달라요 · 밤에 더 많이 나와요";
			};
			case 5 -> "레벨이 오르면 포인트 3개 · 힘/마력/체력/민첩";
			case 6 -> "섬 땅속의 빛나는 광석 · 장비와 강화 재료";
			case 7 -> "지역 본섬 가운데의 원형 무대";
			default -> "이제 자유롭게 탐험하세요 · 균열의 열쇠로 무한 도전";
		};
	}

	// ---------------- 서버: 진행 ----------------
	public static void initialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 20 != 3) return;
			for (ServerPlayer p : server.getPlayerList().getPlayers()) {
				Data d = get(p);
				if (d.step == 3 && SkyLayout.regionAt(p.getX(), p.getZ()) >= 0) advance(p);
				else if (d.step == 7 && Arenas.near(p.getX(), p.getZ(), Arenas.R + 4) != null) advance(p);
				else if (d.step == 0 && ModGrowth.get(p).cls() != 0) onClassChosen(p, ModGrowth.get(p).cls());
				else if (d.step == 5 && ModGrowth.get(p).points() == 0 && totalStats(p) > 0) advance(p);
			}
		});
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

	/** 직업별 특기 이벤트 이름 → 해당하면 2단계 진행 */
	public static void classEvent(Player player, String what) {
		if (!(player instanceof ServerPlayer p) || get(p).step != 2) return;
		PlayerClass c = ModGrowth.get(p).playerClass();
		boolean ok = switch (c) {
			case MAGE -> what.equals("magic_kill");
			case WARRIOR, ASSASSIN -> what.equals("hit");
			case KNIGHT -> what.equals("block");
			case SUPPORT -> what.equals("skill");
			case REAPER -> what.equals("reap2");
			case RANGER -> what.equals("full_volley");
			default -> false;
		};
		if (ok) event(p, 2);
	}

	public static void onClassChosen(ServerPlayer p, int cls) {
		Data d = get(p);
		PlayerClass c = PlayerClass.of(cls);
		int bit = 1 << cls;
		int kits = d.kits;
		if ((kits & bit) == 0) {
			kits |= bit;
			for (ItemStack s : kit(c)) if (!p.getInventory().add(s)) p.spawnAtLocation((ServerLevel) p.level(), s);
			p.sendSystemMessage(Component.literal("✦ " + c.koName + "의 첫 무기를 받았습니다! 화면 오른쪽 위 튜토리얼을 따라가 보세요.").withStyle(ChatFormatting.AQUA));
		}
		int step = d.step >= DONE ? DONE : Math.max(1, Math.min(d.step, 1));
		p.setAttached(DATA, new Data(d.step >= DONE ? DONE : step, 0, kits));
	}

	private static ItemStack[] kit(PlayerClass c) {
		return switch (c) {
			case MAGE -> new ItemStack[]{w(Element.FIRE, WeaponType.STAFF)};
			case WARRIOR -> new ItemStack[]{w(Element.FIRE, WeaponType.GREATSWORD)};
			case KNIGHT -> new ItemStack[]{w(Element.WATER, WeaponType.SWORD), w(Element.WATER, WeaponType.SHIELD)};
			case ASSASSIN -> new ItemStack[]{w(Element.WIND, WeaponType.DAGGER)};
			case SUPPORT -> new ItemStack[]{w(Element.GRASS, WeaponType.BOOK)};
			case REAPER -> new ItemStack[]{w(Element.DARK, WeaponType.SCYTHE)};
			case RANGER -> new ItemStack[]{w(Element.ELECTRIC, WeaponType.BOW)};
			default -> new ItemStack[0];
		};
	}

	private static ItemStack w(Element e, WeaponType t) { return new ItemStack(ModItems.get(e, t)); }

	private static void advance(ServerPlayer p) {
		Data d = get(p);
		int next = d.step + 1;
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
		p.sendSystemMessage(Component.literal("✔ 튜토리얼 " + next + "/" + (DONE - 1) + " → " + title(next, c)).withStyle(ChatFormatting.GREEN));
		Fx.sound(level, p.position(), SoundEvents.PLAYER_LEVELUP, 0.6F, 1.6F);
		Fx.burst(level, Fx.dust(0x7CFFB0, 1.2F), p.position().add(0, 1, 0), 24, 0.5, 0.05);
	}

	/** 튜토리얼 건너뛰기 / 처음부터 */
	public static void set(ServerPlayer p, int step) { Data d = get(p); p.setAttached(DATA, new Data(step, 0, d.kits)); }
}
