package com.elementalweapons.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import com.elementalweapons.client.boss.ModModelLayers;
import com.elementalweapons.item.ElementalWeaponItem;

/** 클라이언트(게임 화면) 전용 초기화 */
public class ElementalWeaponsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ElementalWeaponItem.clientCastHook = CastCamera::start;
		ClientTickEvents.END_CLIENT_TICK.register(CastCamera::tick);
		ModModelLayers.initialize();
		com.elementalweapons.client.growth.GrowthClient.initialize();
		ShotHarness.initialize();
		WeaponAura.initialize();
		com.elementalweapons.client.armor.ModArmorDeco.initialize();
		net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry.getInstance().register(com.elementalweapons.fx.ModParticles.SLASH, com.elementalweapons.client.fx.SlashParticle.Provider::new);
		net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry.getInstance().register(com.elementalweapons.fx.ModParticles.FX,
				(net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry.PendingParticleProvider<com.elementalweapons.fx.FxOptions>) com.elementalweapons.client.fx.FxParticle.Provider::new);
		ClientTickEvents.END_CLIENT_TICK.register(com.elementalweapons.client.fx.SlashFx::tick);
	}
}
