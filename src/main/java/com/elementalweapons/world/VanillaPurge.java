package com.elementalweapons.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.elementalweapons.mob.ModMobs;

/**
 * 바닐라 흔적 정리:
 *  · 바닐라 몹(떠돌이 상인, 우민, 좀비, 동물 등)은 나타나는 즉시 사라짐 — 이 세계엔 이 세계 몹만
 *  · 쓸모없는 바닐라 잡동사니(염료·꽃·뼈·실·화약 …)는 '원소 가루'로, 고기는 원소 고기로, 가죽은 솜털로 바뀜
 *    (건축 블록·농사 씨앗·도구·횃불처럼 쓰는 것은 그대로)
 */
public final class VanillaPurge {
	private VanillaPurge() {}

	/** 이 태그가 붙은 바닐라 몹은 남겨 둠 (어둠 마법의 박쥐 등 연출용) */
	public static final String ALLOWED = "ew_allowed";

	private static final Set<String> DUST = Set.of(
			"dandelion", "poppy", "blue_orchid", "allium", "azure_bluet", "red_tulip", "orange_tulip", "white_tulip", "pink_tulip",
			"oxeye_daisy", "cornflower", "lily_of_the_valley", "wither_rose", "torchflower", "sunflower", "lilac", "rose_bush", "peony",
			"pitcher_plant", "pink_petals", "wildflowers", "spore_blossom", "open_eyeblossom", "closed_eyeblossom", "cactus_flower",
			"bone", "string", "gunpowder", "spider_eye", "fermented_spider_eye", "feather", "ink_sac", "glow_ink_sac", "slime_ball",
			"phantom_membrane", "rabbit_hide", "rabbit_foot", "rotten_flesh", "poisonous_potato", "prismarine_shard", "prismarine_crystals",
			"nautilus_shell", "turtle_scute", "armadillo_scute", "blaze_rod", "blaze_powder", "breeze_rod", "ghast_tear", "magma_cream",
			"wind_charge", "egg", "blue_egg", "brown_egg", "honeycomb", "pufferfish", "tropical_fish");
	private static final Set<String> MEAT = Set.of("beef", "porkchop", "chicken", "mutton", "rabbit", "cod", "salmon");
	private static final Set<String> COOKED = Set.of("cooked_beef", "cooked_porkchop", "cooked_chicken", "cooked_mutton", "cooked_rabbit", "cooked_cod", "cooked_salmon");

	private static final List<Entity> DOOMED = new ArrayList<>();

	public static void initialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
			if (entity instanceof Mob && isVanilla(entity) && !entity.getTags().contains(ALLOWED)) {
				synchronized (DOOMED) { DOOMED.add(entity); }
			} else if (entity instanceof ItemEntity ie) {
				ItemStack c = convert(ie.getItem());
				if (c != null) ie.setItem(c);
			}
		});
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			synchronized (DOOMED) {
				for (Entity e : DOOMED) if (!e.isRemoved()) e.discard();
				DOOMED.clear();
			}
			if (server.getTickCount() % 20 != 9) return;
			for (ServerPlayer p : server.getPlayerList().getPlayers()) {
				if (p.isCreative()) continue;
				var inv = p.getInventory();
				for (int i = 0; i < inv.getContainerSize(); i++) {
					ItemStack c = convert(inv.getItem(i));
					if (c != null) inv.setItem(i, c);
				}
			}
		});
	}

	private static boolean isVanilla(Entity e) {
		return BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).getNamespace().equals("minecraft");
	}

	/** 바꿀 게 없으면 null */
	public static ItemStack convert(ItemStack s) {
		if (s.isEmpty()) return null;
		Identifier id = BuiltInRegistries.ITEM.getKey(s.getItem());
		if (!id.getNamespace().equals("minecraft")) return null;
		String path = id.getPath();
		Item to = null;
		if (path.endsWith("_dye") || DUST.contains(path)) to = ModBlocks.item("element_dust");
		else if (MEAT.contains(path)) to = ModMobs.ELEMENTAL_MEAT;
		else if (COOKED.contains(path)) to = ModMobs.COOKED_ELEMENTAL_MEAT;
		else if (path.equals("leather")) to = ModMobs.FLUFF;
		if (to == null) return null;
		return new ItemStack(to, s.getCount());
	}
}
