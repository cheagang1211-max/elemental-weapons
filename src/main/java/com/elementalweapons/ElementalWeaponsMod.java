package com.elementalweapons;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elementalweapons.boss.ModEntities;
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
		ModItems.initialize();
		SpellManager.initialize();
		DevSelfTest.initialize();
		LOGGER.info("원소 무기 모드 로딩 완료! (무기 28종 + 보스)");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
