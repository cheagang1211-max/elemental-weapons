package com.elementalweapons.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import com.elementalweapons.item.ElementalWeaponItem;

/** 클라이언트(게임 화면) 전용 초기화 */
public class ElementalWeaponsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ElementalWeaponItem.clientCastHook = CastCamera::start;
		ClientTickEvents.END_CLIENT_TICK.register(CastCamera::tick);
	}
}
