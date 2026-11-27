package com.elementalweapons.item;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.spell.Fx;
import com.elementalweapons.world.ModBlocks;

/**
 * 원소 무기 강화 (+1 ~ +15) — 7층 천상의 정상 '강화의 제단'에서.
 *  재료: 강화석 · 무기 속성의 수정 · 원소 가루 (+6부터 원소 정수)
 *  확률: +1~3 100% → +15 10%. 실패할 때마다 그 무기의 다음 확률 +5% (최대 +30%)
 *  +8 이상에 도전하다 실패하면 한 단계 내려감 — 천상의 가호석을 쓰면 내려가지 않음
 *  효과: 단계마다 공격력 +0.6, 기술 피해 +5%, 보스 피해 +40%
 */
public final class Enhancement {
	private Enhancement() {}

	public static final int MAX = 15;
	private static final String KEY = "ew_level", FAIL = "ew_fail";
	private static final int[] CHANCE = {100, 100, 100, 95, 90, 80, 70, 60, 50, 40, 30, 25, 20, 15, 10};
	private static final Identifier MOD_ID = ElementalWeaponsMod.id("enhance");

	public record Cost(int stones, int crystals, int essence, int dust) {}

	public static int level(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data == null ? 0 : data.copyTag().getIntOr(KEY, 0);
	}
	public static int fails(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data == null ? 0 : data.copyTag().getIntOr(FAIL, 0);
	}

	/** 보스 피해 배율 */
	public static double multiplier(ItemStack stack) { return 1.0 + 0.4 * level(stack); }
	/** 기술(마법) 피해 배율 */
	public static double skillMult(ItemStack stack) { return 1.0 + 0.05 * level(stack); }
	public static double attackBonus(int lv) { return 0.6 * lv; }

	/** n단계로 올리는 비용 */
	public static Cost cost(int n) {
		int stones = n <= 3 ? 1 : n <= 6 ? 2 : n <= 9 ? 3 : n <= 12 ? 4 + (n - 10) : 6 + (n - 12) * 2;
		int essence = n < 6 ? 0 : n <= 10 ? n - 5 : 5 + (n - 10) * 2;
		return new Cost(stones, n + 1, essence, 4 * n);
	}
	public static int chance(ItemStack w) {
		int n = level(w) + 1;
		if (n > MAX) return 0;
		return Math.min(100, CHANCE[n - 1] + Math.min(30, 5 * fails(w)));
	}
	/** 이 단계에 도전하다 실패하면 내려가는지 */
	public static boolean dropsOnFail(int n) { return n >= 8; }

	public static Item crystalFor(Element e) {
		return ModBlocks.item(switch (e) {
			case ELECTRIC -> "storm_crystal"; case FIRE -> "ember_crystal"; case WATER -> "tide_crystal"; case GRASS -> "bloom_crystal";
			case WIND -> "gale_crystal"; case DARK -> "umbral_crystal"; case LIGHT -> "radiant_crystal";
		});
	}
	public static Item dust() { return ModBlocks.item("element_dust"); }

	public static int count(Inventory inv, Item item) {
		int n = 0;
		for (int i = 0; i < inv.getContainerSize(); i++) { ItemStack s = inv.getItem(i); if (s.is(item)) n += s.getCount(); }
		return n;
	}
	private static void take(Inventory inv, Item item, int need) {
		for (int i = 0; i < inv.getContainerSize() && need > 0; i++) {
			ItemStack s = inv.getItem(i);
			if (!s.is(item)) continue;
			int t = Math.min(need, s.getCount()); s.shrink(t); need -= t;
		}
	}

	public static ChatFormatting color(int lv) {
		return lv >= 15 ? ChatFormatting.LIGHT_PURPLE : lv >= 10 ? ChatFormatting.GOLD : lv >= 5 ? ChatFormatting.AQUA : ChatFormatting.WHITE;
	}

	/** 단계·실패 기록을 무기에 새김 (이름, 설명, 공격력) */
	public static void apply(ItemStack weapon, int lv) { apply(weapon, lv, fails(weapon)); }
	public static void apply(ItemStack weapon, int lv, int fail) {
		CustomData.update(DataComponents.CUSTOM_DATA, weapon, tag -> { tag.putInt(KEY, lv); tag.putInt(FAIL, fail); });
		List<Component> lore = new ArrayList<>();
		if (lv > 0) {
			lore.add(Component.literal("강화 +" + lv).withStyle(color(lv), ChatFormatting.BOLD));
			lore.add(Component.literal(String.format("공격력 +%.1f · 기술 피해 +%d%% · 보스 피해 x%.1f", attackBonus(lv), lv * 5, 1.0 + 0.4 * lv)).withStyle(ChatFormatting.GRAY));
		}
		if (fail > 0) lore.add(Component.literal("실패 보정 +" + Math.min(30, fail * 5) + "%").withStyle(ChatFormatting.DARK_GRAY));
		weapon.set(DataComponents.LORE, new ItemLore(lore));
		Component base = Component.translatable(weapon.getItem().getDescriptionId());
		if (lv > 0) weapon.set(DataComponents.ITEM_NAME, Component.empty().append(base).append(" +" + lv).withStyle(color(lv)));
		else weapon.remove(DataComponents.ITEM_NAME);
		ItemAttributeModifiers mods = weapon.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
		mods = mods.withModifierAdded(Attributes.ATTACK_DAMAGE, new AttributeModifier(MOD_ID, attackBonus(lv), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
		weapon.set(DataComponents.ATTRIBUTE_MODIFIERS, mods);
	}

	/** 들고 있는 무기를 강화할 수 있는지와 이유 */
	public static String problem(Player p, boolean protect) {
		ItemStack w = p.getMainHandItem();
		if (!(w.getItem() instanceof ElementalWeaponItem ew)) return "오른손에 원소 무기를 드세요";
		int n = level(w) + 1;
		if (n > MAX) return "이미 최대 강화(+" + MAX + ")입니다";
		Cost c = cost(n);
		Inventory inv = p.getInventory();
		if (count(inv, ModItems.ENHANCE_STONE) < c.stones) return "강화석이 부족합니다";
		if (count(inv, crystalFor(ew.element())) < c.crystals) return "속성 수정이 부족합니다";
		if (count(inv, dust()) < c.dust) return "원소 가루가 부족합니다";
		if (count(inv, ModItems.ELEMENT_ESSENCE) < c.essence) return "원소 정수가 부족합니다";
		if (protect && count(inv, ModItems.BLESS_STONE) < 1) return "천상의 가호석이 없습니다";
		return null;
	}

	// ---------------- 통신 ----------------
	public record Open() implements CustomPacketPayload {
		public static final Open INSTANCE = new Open();
		public static final Type<Open> TYPE = new Type<>(ElementalWeaponsMod.id("enhance_open"));
		public static final StreamCodec<ByteBuf, Open> CODEC = StreamCodec.unit(INSTANCE);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}
	public record Try(boolean protect) implements CustomPacketPayload {
		public static final Type<Try> TYPE = new Type<>(ElementalWeaponsMod.id("enhance_try"));
		public static final StreamCodec<ByteBuf, Try> CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, Try::protect, Try::new);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}
	/** 서버 → 클라: 결과 (0 실패·유지, 1 성공, 2 실패·하락, 3 실패·가호석으로 지킴) */
	public record Result(int kind, int level) implements CustomPacketPayload {
		public static final Type<Result> TYPE = new Type<>(ElementalWeaponsMod.id("enhance_result"));
		public static final StreamCodec<ByteBuf, Result> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, Result::kind, ByteBufCodecs.VAR_INT, Result::level, Result::new);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	/** 강화 창을 열 수 있는 곳 (7층 강화의 제단 근처) */
	public static java.util.function.Predicate<ServerPlayer> NEAR_ALTAR = p -> false;

	public static void initialize() {
		PayloadTypeRegistry.clientboundPlay().register(Open.TYPE, Open.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(Result.TYPE, Result.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(Try.TYPE, Try.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(Try.TYPE, (t, ctx) -> attempt(ctx.player(), t.protect()));
	}

	private static void title(ServerPlayer p, Component big, Component sub) {
		p.connection.send(new ClientboundSetTitlesAnimationPacket(2, 28, 10));
		p.connection.send(new ClientboundSetSubtitleTextPacket(sub));
		p.connection.send(new ClientboundSetTitleTextPacket(big));
	}

	public static void attempt(ServerPlayer p, boolean protect) {
		if (!NEAR_ALTAR.test(p)) { p.sendOverlayMessage(Component.literal("강화는 7층 천상의 정상 '강화의 제단'에서 할 수 있습니다").withStyle(ChatFormatting.GRAY)); return; }
		String why = problem(p, protect);
		if (why != null) { p.sendOverlayMessage(Component.literal(why).withStyle(ChatFormatting.RED)); return; }
		ServerLevel level = (ServerLevel) p.level();
		ItemStack w = p.getMainHandItem();
		ElementalWeaponItem ew = (ElementalWeaponItem) w.getItem();
		int lv = level(w), n = lv + 1, ch = chance(w);
		Cost c = cost(n);
		Inventory inv = p.getInventory();
		take(inv, ModItems.ENHANCE_STONE, c.stones);
		take(inv, crystalFor(ew.element()), c.crystals);
		take(inv, dust(), c.dust);
		take(inv, ModItems.ELEMENT_ESSENCE, c.essence);
		var pos = p.position().add(0, 1.1, 0);
		int col = com.elementalweapons.boss.ElementFx.color(ew.element());
		Fx.sound(level, pos, SoundEvents.ANVIL_USE, 1F, 1.2F);
		if (level.getRandom().nextInt(100) < ch) {
			apply(w, n, 0);
			Fx.burst(level, Fx.dust(col, 1.7F), pos, 80, 0.9, 0.08);
			Fx.burst(level, com.elementalweapons.fx.P.END_ROD, pos, n >= 10 ? 60 : 25, 0.6, 0.2);
			Fx.sound(level, pos, SoundEvents.PLAYER_LEVELUP, 1F, 1.2F);
			if (n >= 10) Fx.sound(level, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1F, 1F);
			title(p, Component.literal("강화 성공! +" + n).withStyle(color(n), ChatFormatting.BOLD), Component.literal(String.format("공격력 +%.1f · 기술 피해 +%d%% · 보스 피해 x%.1f", attackBonus(n), n * 5, 1.0 + 0.4 * n)).withStyle(ChatFormatting.GRAY));
			ServerPlayNetworking.send(p, new Result(1, n));
			if (n >= 10) for (ServerPlayer o : level.getServer().getPlayerList().getPlayers())
				o.sendSystemMessage(Component.literal("✦ " + p.getName().getString() + "님이 " + w.getHoverName().getString() + " 강화에 성공했습니다!").withStyle(color(n)));
		} else {
			int kind = 0, to = lv;
			if (dropsOnFail(n)) {
				if (protect) { take(inv, ModItems.BLESS_STONE, 1); kind = 3; }
				else { to = lv - 1; kind = 2; }
			}
			apply(w, to, fails(w) + 1);
			Fx.burst(level, com.elementalweapons.fx.P.LARGE_SMOKE, pos, 20, 0.4, 0.04);
			Fx.sound(level, pos, SoundEvents.ANVIL_DESTROY, 0.8F, 0.9F);
			String sub = kind == 2 ? "+" + lv + " → +" + to + " 으로 내려갔습니다" : kind == 3 ? "천상의 가호석이 무기를 지켰습니다" : "단계는 그대로입니다";
			title(p, Component.literal("강화 실패").withStyle(ChatFormatting.RED, ChatFormatting.BOLD), Component.literal(sub + " · 다음 확률 +5%").withStyle(ChatFormatting.GRAY));
			ServerPlayNetworking.send(p, new Result(kind, to));
		}
	}

	/** 예전 방식(웅크리고 우클릭) 안내 */
	public static void tryUpgrade(ServerLevel level, Player player, ItemStack weapon, ItemStack essence) {
		player.sendOverlayMessage(Component.literal("강화는 7층 천상의 정상 '강화의 제단'에서 할 수 있습니다").withStyle(ChatFormatting.GOLD));
	}
}
