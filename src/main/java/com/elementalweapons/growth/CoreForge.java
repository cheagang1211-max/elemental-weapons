package com.elementalweapons.growth;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.item.ModItems;
import com.elementalweapons.spell.Fx;

/**
 * 핵 강화: 보스 핵을 바쳐 개인 능력치를 영구히 올림 (K 성장 창 오른쪽 칸)
 * 핵마다 다른 능력, 최대 10단계, 다음 단계에 (단계+1)개 필요 (항상 성공)
 */
public final class CoreForge {
	private CoreForge() {}

	public static final int MAX = 10;
	public static final String[] IDS = {"voltra", "ignis", "marina", "silva", "zephyros", "nox", "lumiel", "crystal_golem",
			"lava_queen", "frost_giant", "sphinx", "steam_colossus", "storm_wyvern", "twilight_twins", "hydra", "prima"};
	public static final String[] NAMES = {"뇌신", "이그니스", "마리나", "실바", "제피로스", "녹스", "루미엘", "골렘",
			"용암 여왕", "서리 거인", "스핑크스", "증기 거신", "폭풍 와이번", "황혼 쌍둥이", "히드라", "프리마"};
	/** 능력 이름, 단계당 수치, 단위 (%면 백분율 표시) */
	public static final String[] STAT = {"공격 속도", "근접 공격력", "최대 체력", "방어력", "이동 속도", "보스 피해", "마법 피해", "방어 강도",
			"근접 공격력", "넉백 저항", "쿨타임 감소", "방어력", "이동 속도", "공격 속도", "최대 체력", "모든 피해"};
	public static final double[] PER = {0.02, 0.015, 1, 0.5, 0.01, 0.02, 0.02, 0.4, 0.3, 0.03, 0.01, 0.5, 0.01, 0.02, 2, 0.02};
	public static final boolean[] PCT = {true, true, false, false, true, true, true, false, false, true, true, false, true, true, false, true};
	public static final int[] COLORS = {0xFFFFE14D, 0xFFFF6A3D, 0xFF4DA6FF, 0xFF5BD96A, 0xFFA8F0E0, 0xFF9A6AFF, 0xFFFFF0A0, 0xFF9FE8FF,
			0xFFFF8A3D, 0xFFBEE6FF, 0xFFE8C77A, 0xFFC0A080, 0xFF7AD0FF, 0xFFD080FF, 0xFF4DE0B0, 0xFFFFD34D};

	private static final Codec<List<Integer>> CODEC = Codec.INT.listOf();
	private static final StreamCodec<io.netty.buffer.ByteBuf, List<Integer>> STREAM = ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list());

	public static final AttachmentType<List<Integer>> DATA = AttachmentRegistry.<List<Integer>>builder()
			.persistent(CODEC)
			.copyOnDeath()
			.initializer(List::of)
			.syncWith(STREAM, AttachmentSyncPredicate.targetOnly())
			.buildAndRegister(Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "core_forge"));

	public record Up(int idx) implements CustomPacketPayload {
		public static final Type<Up> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "core_up"));
		public static final StreamCodec<io.netty.buffer.ByteBuf, Up> CODEC = ByteBufCodecs.VAR_INT.map(Up::new, Up::idx);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	public static void initialize() {
		PayloadTypeRegistry.serverboundPlay().register(Up.TYPE, Up.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(Up.TYPE, (pl, ctx) -> upgrade(ctx.player(), pl.idx()));
	}

	public static Item core(int i) { return i == 0 ? ModItems.VOLTRA_CORE : ModItems.BOSS_CORES.get(IDS[i]); }

	public static int level(Player p, int i) {
		List<Integer> l = p.getAttached(DATA);
		return l == null || i >= l.size() ? 0 : l.get(i);
	}

	/** 다음 단계에 필요한 핵 개수 */
	public static int cost(int lv) { return lv + 1; }

	/** 단계에 따른 수치 */
	public static double value(int i, int lv) { return PER[i] * lv; }
	public static double value(Player p, int i) { return value(i, level(p, i)); }

	public static String fmt(int i, double v) {
		if (PCT[i]) return "+" + (Math.round(v * 1000) / 10.0) + "%";
		return "+" + (v == Math.floor(v) ? String.valueOf((int) v) : String.valueOf(Math.round(v * 10) / 10.0));
	}

	public static int count(Player p, Item it) {
		Inventory inv = p.getInventory();
		int n = 0;
		for (int k = 0; k < inv.getContainerSize(); k++) if (inv.getItem(k).is(it)) n += inv.getItem(k).getCount();
		return n;
	}

	// ── 다른 곳에서 쓰는 배율 ──
	/** 보스 피해 배율 (녹스 + 프리마) */
	public static double bossMult(Player p) { return 1 + value(p, 5) + value(p, 15); }
	/** 마법 피해 배율 (루미엘 + 프리마) */
	public static double spellMult(Player p) { return 1 + value(p, 6) + value(p, 15); }
	/** 쿨타임 남는 비율 (스핑크스) */
	public static double cooldownFactor(Player p) { return 1 - value(p, 10); }

	public static void upgradeForTest(ServerPlayer p, int i) { upgrade(p, i); }

	static void upgrade(ServerPlayer p, int i) {
		if (i < 0 || i >= IDS.length) return;
		int lv = level(p, i);
		if (lv >= MAX) { p.sendOverlayMessage(Component.literal("이미 최대 단계입니다.").withStyle(ChatFormatting.GRAY)); return; }
		Item it = core(i);
		int need = cost(lv);
		if (it == null || count(p, it) < need) {
			p.sendOverlayMessage(Component.literal(NAMES[i] + "의 핵이 " + need + "개 필요합니다.").withStyle(ChatFormatting.RED));
			return;
		}
		Inventory inv = p.getInventory();
		int left = need;
		for (int k = 0; k < inv.getContainerSize() && left > 0; k++) {
			ItemStack s = inv.getItem(k);
			if (!s.is(it)) continue;
			int t = Math.min(left, s.getCount());
			s.shrink(t);
			left -= t;
		}
		List<Integer> l = new ArrayList<>(p.getAttachedOrElse(DATA, List.of()));
		while (l.size() < IDS.length) l.add(0);
		l.set(i, lv + 1);
		p.setAttached(DATA, List.copyOf(l));
		ModGrowth.applyStats(p);
		ServerLevel sl = (ServerLevel) p.level();
		int c = COLORS[i] & 0xFFFFFF;
		Fx.burst(sl, Fx.dust(c, 1.6F), p.position().add(0, 1.0, 0), 60, 0.8, 0.08);
		Fx.ring(sl, Fx.dust(c, 1.4F), p.position().add(0, 0.2, 0), 1.8, 32, 0);
		Fx.sound(sl, p.position(), SoundEvents.BEACON_POWER_SELECT, 0.8F, 1.4F);
		p.sendOverlayMessage(Component.literal("◆ " + NAMES[i] + "의 핵 " + (lv + 1) + "단계 — " + STAT[i] + " " + fmt(i, value(i, lv + 1)))
				.withStyle(ChatFormatting.GOLD));
	}
}
