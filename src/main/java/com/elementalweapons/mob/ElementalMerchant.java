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
		Item dust = ModBlocks.item("element_dust");
		// ★ 성장에 바로 쓰이는 귀한 물건
		sell(offers, ori, 2, com.elementalweapons.rift.RiftItems.RIFT_KEY, 1);                     // 균열의 열쇠
		sell(offers, ori, 2, com.elementalweapons.item.ModItems.ELEMENT_ESSENCE, 1);                // 원소 정수 (강화 재료)
		sell(offers, aether, 2, Items.EXPERIENCE_BOTTLE, 6);                                          // 경험치 병 (게임 경험치로 들어감)
		// 1단계 보스 소환석 (재료 없이 바로)
		String[][] tier1 = {{"silva", "bloom_crystal"}, {"marina", "tide_crystal"}, {"voltra", "storm_crystal"}, {"zephyros", "gale_crystal"},
				{"ignis", "ember_crystal"}, {"lumiel", "radiant_crystal"}, {"nox", "umbral_crystal"}};
		for (String[] t : tier1) {
			Item stone = com.elementalweapons.item.ModItems.BOSS_SUMMONS.get(t[0]);
			if (stone != null) sellTwo(offers, ori, 2, ModBlocks.item(t[1]), 4, stone);
		}
		// 재료 바꾸기
		sell(offers, aether, 4, ori, 1);                                                               // 에테르 주괴 4 → 오리하르콘 주괴 1
		sell(offers, dust, 10, ModBlocks.item("raw_aether"), 1);
		sell(offers, dust, 24, ModBlocks.item("orichalcum_chunk"), 1);
		sell(offers, dust, 30, com.elementalweapons.item.ModItems.ELEMENT_ESSENCE, 1);
		for (String[] t : tier1) sell(offers, dust, 6, ModBlocks.item(t[1]), 1);                     // 원소 가루 → 원하는 속성 수정
		// 생활
		sell(offers, aether, 2, Items.GOLDEN_APPLE, 1);
		sell(offers, ori, 6, Items.TOTEM_OF_UNDYING, 1);
		// 강화 재료
		sell(offers, dust, 12, com.elementalweapons.item.ModItems.ENHANCE_STONE, 1);
		sell(offers, ori, 5, com.elementalweapons.item.ModItems.BLESS_STONE, 1);
	}

	protected static void sellTwo(MerchantOffers offers, Item cost, int n, Item cost2, int n2, Item result) {
		offers.add(new MerchantOffer(new ItemCost(cost, n), java.util.Optional.of(new ItemCost(cost2, n2)), new ItemStack(result), 16, 4, 0.05F));
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

	/** 천공성 가게 자리 (null이면 자유 상인). 자리가 있는 주인은 저장하지 않고 서버가 늘 다시 세움 → 중복·뭉침 없음 */
	private net.minecraft.core.BlockPos post;
	public void setPost(net.minecraft.core.BlockPos p) { this.post = p; }
	public net.minecraft.core.BlockPos post() { return post; }
	@Override public boolean shouldBeSaved() { return post == null && super.shouldBeSaved(); }

	private boolean tradesFresh;

	@Override
	public void tick() {
		super.tick();
		// 불러올 때마다 거래 목록을 새로 만듦 (예전에 저장된 옛 거래가 남지 않게)
		if (!tradesFresh && !this.level().isClientSide() && this.level() instanceof ServerLevel sl && this.getTradingPlayer() == null) {
			tradesFresh = true;
			this.getOffers().clear();
			this.updateTrades(sl);
		}
		// 밀리거나 떨어져도 자기 자리로 돌아감
		if (post != null && !this.level().isClientSide() && this.tickCount % 20 == 0
				&& this.distanceToSqr(post.getX() + 0.5, post.getY(), post.getZ() + 0.5) > 2.25) {
			this.teleportTo(post.getX() + 0.5, post.getY(), post.getZ() + 0.5);
			this.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
		}
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
