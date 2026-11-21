package com.elementalweapons.mob;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import com.elementalweapons.boss.PosedEntity;
import com.elementalweapons.world.ModBlocks;

/**
 * 원소족 상인: 새 세계에 없는 바닐라 자원(씨앗, 금, 레드스톤 등)을 원소 재료와 바꿔 줍니다.
 * 우클릭하면 거래 창이 열립니다. 사라지지 않습니다.
 */
public class ElementalMerchant extends WanderingTrader implements PosedEntity {
	public ElementalMerchant(EntityType<? extends ElementalMerchant> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder attributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0).add(Attributes.MOVEMENT_SPEED, 0.0).add(Attributes.KNOCKBACK_RESISTANCE, 1.0);   // 가게 자리를 지킴
	}

	@Override
	protected void updateTrades(ServerLevel level) {
		MerchantOffers offers = this.getOffers();
		Item aether = ModBlocks.item("aether_ingot");
		Item ori = ModBlocks.item("orichalcum_ingot");
		// 농사 시작용
		sell(offers, aether, 1, Items.WHEAT_SEEDS, 6);
		sell(offers, aether, 1, Items.POTATO, 3);
		sell(offers, aether, 1, Items.CARROT, 3);
		sell(offers, aether, 1, Items.BEETROOT_SEEDS, 6);
		sell(offers, aether, 2, Items.SUGAR_CANE, 2);
		sell(offers, aether, 2, Items.PUMPKIN_SEEDS, 2);
		sell(offers, aether, 2, Items.MELON_SEEDS, 2);
		sell(offers, aether, 1, Items.BONE_MEAL, 6);
		// 이 세계에 없는 광물
		sell(offers, aether, 3, Items.GOLD_INGOT, 1);
		sell(offers, aether, 3, Items.REDSTONE, 4);
		sell(offers, aether, 3, Items.LAPIS_LAZULI, 3);
		sell(offers, aether, 2, Items.COAL, 4);
		sell(offers, ori, 1, Items.EMERALD, 2);
		sell(offers, ori, 2, Items.NETHERITE_SCRAP, 1);
		// 기타
		sell(offers, ModBlocks.item("storm_crystal"), 2, Items.ENDER_PEARL, 1);
		sell(offers, ModBlocks.item("radiant_crystal"), 2, Items.EXPERIENCE_BOTTLE, 3);
		sell(offers, ModBlocks.item("bloom_crystal"), 2, Items.SADDLE, 1);
		sell(offers, aether, 4, Items.NAME_TAG, 1);
	}

	/** 돌아다니거나 밤에 투명 물약을 마시지 않고, 가게 자리에서 손님만 바라봄 */
	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new net.minecraft.world.entity.ai.goal.FloatGoal(this));
		this.goalSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal(this));
		this.goalSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal(this));
		this.goalSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(this, net.minecraft.world.entity.player.Player.class, 8.0F));
		this.goalSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.RandomLookAroundGoal(this));
	}

	@Override public boolean isPushable() { return false; }
	@Override public boolean removeWhenFarAway(double d) { return false; }
	@Override public boolean isInvulnerableTo(ServerLevel level, net.minecraft.world.damagesource.DamageSource source) { return !source.isCreativePlayer() || super.isInvulnerableTo(level, source); }

	protected static void sell(MerchantOffers offers, Item cost, int costCount, Item result, int resultCount) {
		offers.add(new MerchantOffer(new ItemCost(cost, costCount), new ItemStack(result, resultCount), 16, 2, 0.05F));
	}

	@Override public int getAttackPose() { return 0; }
	@Override public boolean isFloatingBoss() { return false; }
}
