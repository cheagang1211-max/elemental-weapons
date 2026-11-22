package com.elementalweapons;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elementalweapons.boss.ModEntities;
import com.elementalweapons.mob.ModMobs;
import com.elementalweapons.world.ModBlocks;
import com.elementalweapons.item.ModItems;
import com.elementalweapons.spell.SpellManager;

public class ElementalWeaponsMod implements ModInitializer {
	public static final String MOD_ID = "elementalweapons";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.initialize();
		ModEntities.initialize();
		ModMobs.initialize();
		ModItems.initialize();
		com.elementalweapons.fx.ModParticles.initialize();
		com.elementalweapons.rift.RiftItems.initialize();
		com.elementalweapons.item.ModGear.initialize();
		com.elementalweapons.growth.ModGrowth.initialize();
		com.elementalweapons.growth.Evolution.initialize();
		com.elementalweapons.growth.Balance.initialize();
		com.elementalweapons.skill.SkillNet.initialize();
		com.elementalweapons.world.town.ModTowns.initialize();
		com.elementalweapons.world.sky.ModSky.initialize();
		SpellManager.initialize();
		com.elementalweapons.world.VanillaPurge.initialize();
		com.elementalweapons.boss.BossFight.initialize();
		DevSelfTest.initialize();
		LOGGER.info("원소 무기 모드 로딩 완료! (무기 28종 + 보스)");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
