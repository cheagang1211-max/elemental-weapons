package com.elementalweapons.mob;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import com.elementalweapons.item.ModItems;
import com.elementalweapons.world.ModBlocks;

/** 천공성 층마다 있는 가게 주인들: 대장장이 · 마법부여사 · 연금술사 */
public final class Shopkeepers {
	private Shopkeepers() {}

	private static void offer(MerchantOffers o, Item cost, int n, ItemStack result) {
		o.add(new MerchantOffer(new ItemCost(cost, n), result, 32, 3, 0.05F));
	}

	private static void offer(MerchantOffers o, Item cost, int n, Item cost2, int n2, ItemStack result) {
		o.add(new MerchantOffer(new ItemCost(cost, n), java.util.Optional.of(new ItemCost(cost2, n2)), result, 32, 3, 0.05F));
	}

	/** 대장장이: 무기 강화 재료, 갑옷·도구, 모루 */
	public static class Blacksmith extends ElementalMerchant {
		public Blacksmith(EntityType<? extends Blacksmith> type, Level level) { super(type, level); }

		@Override
		protected void updateTrades(ServerLevel level) {
			MerchantOffers o = getOffers();
			Item ae = ModBlocks.item("aether_ingot"), ori = ModBlocks.item("orichalcum_ingot");
			var G = com.elementalweapons.item.ModGear.ITEMS;
			offer(o, ori, 2, new ItemStack(ModItems.ELEMENT_ESSENCE, 1));               // 강화 재료 (무기·방패 강화)
			// 최고 등급 오리하르콘 장비 (내구도 무한)
			offer(o, ori, 8, new ItemStack(G.get("orichalcum_chestplate")));
			offer(o, ori, 7, new ItemStack(G.get("orichalcum_leggings")));
			offer(o, ori, 5, new ItemStack(G.get("orichalcum_helmet")));
			offer(o, ori, 4, new ItemStack(G.get("orichalcum_boots")));
			offer(o, ori, 3, new ItemStack(G.get("orichalcum_pickaxe")));
			offer(o, ori, 3, new ItemStack(G.get("orichalcum_axe")));
			offer(o, ori, 2, new ItemStack(G.get("orichalcum_shovel")));
			// 에테르 도구
			offer(o, ae, 3, new ItemStack(G.get("aether_pickaxe")));
			offer(o, ae, 3, new ItemStack(G.get("aether_axe")));
			offer(o, ae, 2, new ItemStack(G.get("aether_shovel")));
			// 주괴 바꾸기
			offer(o, ae, 4, new ItemStack(ori, 1));
			offer(o, ae, 3, new ItemStack(Items.DIAMOND, 2));
			offer(o, ae, 1, new ItemStack(Items.ARROW, 32));
		}
	}

	/** 마법부여사: 마법이 부여된 책, 청금석, 경험치 병, 마법부여대 */
	public static class Enchanter extends ElementalMerchant {
		public Enchanter(EntityType<? extends Enchanter> type, Level level) { super(type, level); }

		private static ItemStack book(ServerLevel level, ResourceKey<Enchantment> key, int lv) {
			ItemStack b = new ItemStack(Items.ENCHANTED_BOOK);
			try {
				Holder<Enchantment> h = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
				ItemEnchantments.Mutable m = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
				m.set(h, lv);
				b.set(DataComponents.STORED_ENCHANTMENTS, m.toImmutable());
			} catch (Throwable ignored) {}
			return b;
		}

		@Override
		protected void updateTrades(ServerLevel level) {
			MerchantOffers o = getOffers();
			Item ae = ModBlocks.item("aether_ingot"), ori = ModBlocks.item("orichalcum_ingot");
			Item lightC = ModBlocks.item("radiant_crystal"), darkC = ModBlocks.item("umbral_crystal");
			offerBook(o, ori, 4, new ItemStack(Items.BOOK), book(level, Enchantments.SHARPNESS, 5));
			offerBook(o, ori, 3, new ItemStack(Items.BOOK), book(level, Enchantments.SMITE, 5));
			offerBook(o, ori, 4, new ItemStack(Items.BOOK), book(level, Enchantments.LOOTING, 3));
			offerBook(o, ori, 3, new ItemStack(Items.BOOK), book(level, Enchantments.SWEEPING_EDGE, 3));
			offerBook(o, ori, 4, new ItemStack(Items.BOOK), book(level, Enchantments.PROTECTION, 4));
			offerBook(o, ori, 2, new ItemStack(Items.BOOK), book(level, Enchantments.FEATHER_FALLING, 4));
			offerBook(o, ori, 3, new ItemStack(Items.BOOK), book(level, Enchantments.EFFICIENCY, 5));
			offerBook(o, ori, 4, new ItemStack(Items.BOOK), book(level, Enchantments.FORTUNE, 3));
			offerBook(o, ori, 4, new ItemStack(Items.BOOK), book(level, Enchantments.POWER, 5));
			offerBook(o, ori, 6, new ItemStack(Items.BOOK), book(level, Enchantments.MENDING, 1));
			offer(o, ae, 2, new ItemStack(Items.LAPIS_LAZULI, 8));
			offer(o, lightC == Items.AIR ? ae : lightC, 2, new ItemStack(Items.EXPERIENCE_BOTTLE, 6));
			offer(o, ae, 6, new ItemStack(Items.ENCHANTING_TABLE));
			offer(o, ae, 2, new ItemStack(Items.BOOKSHELF, 3));
			if (darkC != Items.AIR) offer(o, darkC, 3, new ItemStack(Items.ENDER_PEARL, 2));
		}

		private void offerBook(MerchantOffers o, Item cost, int n, ItemStack cost2, ItemStack result) {
			o.add(new MerchantOffer(new ItemCost(cost, n), java.util.Optional.of(new ItemCost(cost2.getItem(), cost2.getCount())), result, 16, 5, 0.05F));
		}
	}

	/** 연금술사: 물약, 황금 사과, 양조 재료 */
	public static class Alchemist extends ElementalMerchant {
		public Alchemist(EntityType<? extends Alchemist> type, Level level) { super(type, level); }

		private static ItemStack potion(Holder<Potion> p) { return PotionContents.createItemStack(Items.POTION, p); }

		@Override
		protected void updateTrades(ServerLevel level) {
			MerchantOffers o = getOffers();
			Item ae = ModBlocks.item("aether_ingot"), ori = ModBlocks.item("orichalcum_ingot");
			offer(o, ae, 2, potion(Potions.STRONG_HEALING));
			offer(o, ae, 2, potion(Potions.LONG_REGENERATION));
			offer(o, ae, 3, potion(Potions.STRONG_STRENGTH));
			offer(o, ae, 2, potion(Potions.LONG_SWIFTNESS));
			offer(o, ae, 2, potion(Potions.LONG_NIGHT_VISION));
			offer(o, ae, 2, potion(Potions.LONG_SLOW_FALLING));
			offer(o, ae, 2, potion(Potions.LONG_FIRE_RESISTANCE));
			offer(o, ae, 2, new ItemStack(Items.GOLDEN_APPLE, 2));
			offer(o, ori, 6, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));
			offer(o, ae, 1, potion(Potions.STRONG_HEALING));
			offer(o, ori, 5, new ItemStack(Items.TOTEM_OF_UNDYING));
			offer(o, ae, 2, new ItemStack(Items.EXPERIENCE_BOTTLE, 6));
		}
	}

	/** 전직 교관: 말을 걸면 전직 창 (거래 없음) */
	public static class Instructor extends ElementalMerchant {
		public Instructor(EntityType<? extends Instructor> type, Level level) { super(type, level); }

		@Override
		protected void updateTrades(ServerLevel level) {}

		@Override
		public net.minecraft.world.InteractionResult mobInteract(net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
			if (player instanceof net.minecraft.server.level.ServerPlayer sp) com.elementalweapons.growth.Jobs.open(sp);
			return net.minecraft.world.InteractionResult.SUCCESS;
		}
	}
}
