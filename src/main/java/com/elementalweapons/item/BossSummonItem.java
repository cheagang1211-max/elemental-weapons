package com.elementalweapons.item;

import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.boss.ElementalBoss;
import com.elementalweapons.spell.ElectricStormSpell;
import com.elementalweapons.spell.Fx;

/** 우클릭하면 앞쪽에 보스를 소환하는 아이템 (한 번 쓰면 사라짐) */
public class BossSummonItem extends Item {
	private final Supplier<EntityType<? extends ElementalBoss>> bossType;

	public BossSummonItem(Item.Properties properties, Supplier<EntityType<? extends ElementalBoss>> bossType) {
		super(properties);
		this.bossType = bossType;
	}

	@Override
	public InteractionResult use(Level level, Player user, InteractionHand hand) {
		if (!(level instanceof ServerLevel server)) {
			return InteractionResult.SUCCESS;
		}
		String bid = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(bossType.get()).getPath();
		if (!(user instanceof net.minecraft.server.level.ServerPlayer sp)) return InteractionResult.FAIL;
		// 공중 세계: 천공성 1층 '보스 관문'에서만 → 보스 전용 방으로 이동해 싸움
		if (com.elementalweapons.world.sky.ModSky.active(server)) {
			if (!com.elementalweapons.world.sky.BossGate.nearGate(sp)) {
				sp.sendSystemMessage(Component.literal("☠ 보스 소환석은 천공성 1층 '보스 관문' 앞에서 우클릭하세요. (B키로 천공성 귀환 · 스폰 바로 동쪽)").withStyle(ChatFormatting.LIGHT_PURPLE));
				return InteractionResult.FAIL;
			}
			ItemStack refund = user.isCreative() ? ItemStack.EMPTY : user.getItemInHand(hand).copyWithCount(1);
			if (!com.elementalweapons.world.sky.BossGate.start(sp, bid, refund)) return InteractionResult.FAIL;
		} else {
			Vec3 pos = user.position().add(Fx.flatLook(user).scale(8)).add(0, 1, 0);
			ElementalBoss boss = bossType.get().create(server, EntitySpawnReason.SPAWN_ITEM_USE);
			if (boss == null) return InteractionResult.FAIL;
			boss.snapTo(pos.x, pos.y, pos.z, user.getYRot() + 180.0F, 0.0F);
			server.addFreshEntity(boss);
			ItemStack refund = user.isCreative() ? ItemStack.EMPTY : user.getItemInHand(hand).copyWithCount(1);
			com.elementalweapons.boss.BossFight.start(server, boss, pos.add(0, -1, 0), null, sp, refund);
			ElectricStormSpell.drawBolt(server, pos);
			Fx.sound(server, pos, SoundEvents.WITHER_SPAWN, 2.0F, 1.4F);
		}

		ItemStack stack = user.getItemInHand(hand);
		if (!user.isCreative()) stack.shrink(1);
		return InteractionResult.SUCCESS;
	}
}
