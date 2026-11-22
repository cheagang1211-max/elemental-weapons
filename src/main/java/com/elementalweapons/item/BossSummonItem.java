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
		Vec3 pos = user.position().add(Fx.flatLook(user).scale(8)).add(0, 1, 0);
		ElementalBoss boss = bossType.get().create(server, EntitySpawnReason.SPAWN_ITEM_USE);
		if (boss == null) return InteractionResult.FAIL;
		// 공중 세계: 보스방 안에서 쓰면 제단 위에 강림 + 기믹 전투
		com.elementalweapons.world.sky.Arenas.Arena arena = null;
		String bid = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(boss.getType()).getPath();
		if (com.elementalweapons.world.sky.ModSky.active(server)) {
			com.elementalweapons.world.sky.Arenas.Arena near = com.elementalweapons.world.sky.Arenas.near(user.getX(), user.getZ(), com.elementalweapons.world.sky.Arenas.R + 6);
			if (near != null) arena = near;
			else {
				com.elementalweapons.world.sky.Arenas.Arena home = com.elementalweapons.world.sky.Arenas.of(bid);
				if (home != null) user.sendSystemMessage(Component.literal("✦ 이 보스의 보스방: (" + home.x() + ", " + home.floor() + ", " + home.z() + ") — 보스방에서 소환하면 결계·수정 탑·장판 기믹 전투가 펼쳐집니다.")
						.withStyle(ChatFormatting.AQUA));
			}
		}
		if (arena != null) pos = new Vec3(arena.x() + 0.5, arena.floor() + 3, arena.z() + 0.5);
		boss.snapTo(pos.x, pos.y, pos.z, user.getYRot() + 180.0F, 0.0F);
		server.addFreshEntity(boss);
		ItemStack refund = user.isCreative() ? ItemStack.EMPTY : user.getItemInHand(hand).copyWithCount(1);
		com.elementalweapons.boss.BossFight.start(server, boss, arena != null ? new Vec3(arena.x() + 0.5, arena.floor() + 1, arena.z() + 0.5) : pos.add(0, -1, 0), arena,
				user instanceof net.minecraft.server.level.ServerPlayer sp ? sp : null, refund);

		// 등장 연출
		ElectricStormSpell.drawBolt(server, pos);
		ElectricStormSpell.drawBolt(server, pos.add(1.5, 0, 0));
		ElectricStormSpell.drawBolt(server, pos.add(-1.5, 0, 0));
		Fx.sound(server, pos, SoundEvents.WITHER_SPAWN, 2.0F, 1.4F);
		Component msg = boss.bossName().copy().append(Component.literal(" 이(가) 강림했다!").withStyle(ChatFormatting.WHITE));
		for (Player p : server.players()) {
			if (p.distanceToSqr(user) < 96 * 96) p.sendSystemMessage(msg);
		}

		ItemStack stack = user.getItemInHand(hand);
		if (!user.isCreative()) stack.shrink(1);
		return InteractionResult.SUCCESS;
	}
}
