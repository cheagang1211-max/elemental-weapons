package com.elementalweapons.world.sky;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.growth.Evolution;
import com.elementalweapons.item.Element;
import com.elementalweapons.spell.Fx;

/**
 * 지하 원소의 성소: 무너진 신전의 제단을 우클릭하면 그 속성의 축복 (10분).
 *  자기 속성의 신전이면 '공명' — 효과 한 단계 위 + 15분.
 *  축복은 하나만 (새로 받으면 바뀜), 20분에 한 번.
 */
public final class ShrineBlessing {
	private ShrineBlessing() {}

	private static final Map<UUID, Long> NEXT = new HashMap<>();
	private static final int COOLDOWN = 20 * 60 * 20;

	@SuppressWarnings("unchecked")
	private static final Holder<MobEffect>[][] FX = new Holder[][]{
			{MobEffects.SPEED, MobEffects.HASTE},                    // 전기: 신속 + 성급함
			{MobEffects.STRENGTH, MobEffects.FIRE_RESISTANCE},       // 불: 힘 + 화염 저항
			{MobEffects.REGENERATION, MobEffects.WATER_BREATHING},   // 물: 재생 + 수중 호흡
			{MobEffects.HEALTH_BOOST, MobEffects.REGENERATION},      // 풀: 생명력 강화 + 재생
			{MobEffects.SPEED, MobEffects.JUMP_BOOST},               // 바람: 신속 + 점프
			{MobEffects.STRENGTH, MobEffects.ABSORPTION},            // 어둠: 힘 + 흡수
			{MobEffects.RESISTANCE, MobEffects.REGENERATION},        // 빛: 저항 + 재생
	};
	private static final String[] DESC = {
			"이동 속도·채굴 속도 증가", "공격력 증가·불 피해 무시", "체력 재생·물속 호흡", "최대 체력 증가·체력 재생",
			"이동 속도·점프력 증가", "공격력 증가·보호막", "받는 피해 감소·체력 재생"};

	public static void initialize() {
		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			if (world.isClientSide() || !(player instanceof ServerPlayer p) || hand != net.minecraft.world.InteractionHand.MAIN_HAND) return InteractionResult.PASS;
			var pos = hit.getBlockPos();
			if (pos.getY() < Annex.YB || pos.getY() > Annex.YB + 6) return InteractionResult.PASS;
			int k = shrineAt(pos.getX() + 0.5, pos.getZ() + 0.5, 2.6);
			if (k < 0) return InteractionResult.PASS;
			bless(p, k);
			return InteractionResult.SUCCESS;
		});
	}

	/** 그 위치가 몇 번째 신전 제단 근처인지 (-1: 아님) */
	static int shrineAt(double x, double z, double rad) {
		for (int k = 0; k < 7; k++) {
			double a = k * Math.PI * 2 / 7 - Math.PI / 2;
			double cx = Math.round(Math.cos(a) * 30) + 0.5, cz = Math.round(Math.sin(a) * 30) + 0.5;
			if (Math.hypot(x - cx, z - cz) <= rad) return k;
		}
		return -1;
	}

	private static void bless(ServerPlayer p, int k) {
		ServerLevel l = (ServerLevel) p.level();
		long now = l.getGameTime();
		Long next = NEXT.get(p.getUUID());
		Element e = Element.values()[k];
		if (next != null && next > now) {
			long s = (next - now) / 20;
			p.sendOverlayMessage(Component.literal(String.format("신전의 힘이 아직 돌아오지 않았습니다 (%d분 %d초 뒤)", s / 60, s % 60)).withStyle(ChatFormatting.GRAY));
			return;
		}
		NEXT.put(p.getUUID(), now + COOLDOWN);
		boolean res = Evolution.element(p) == k;
		int amp = res ? 1 : 0, dur = (res ? 15 : 10) * 60 * 20;
		for (Holder<MobEffect>[] set : FX) for (Holder<MobEffect> h : set) p.removeEffect(h);   // 축복은 하나만
		for (Holder<MobEffect> h : FX[k]) p.addEffect(new MobEffectInstance(h, dur, h == MobEffects.WATER_BREATHING || h == MobEffects.FIRE_RESISTANCE ? 0 : amp, true, true, true));
		p.connection.send(new ClientboundSetTitlesAnimationPacket(5, 50, 15));
		p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(DESC[k] + (res ? " · 공명! 한 단계 강화" : "") + " · " + (res ? 15 : 10) + "분").withStyle(ChatFormatting.GRAY)));
		p.connection.send(new ClientboundSetTitleTextPacket(Component.literal("◈ " + e.koName + "의 축복").withStyle(st -> st.withColor(e.color).withBold(true))));
		Vec3 c0 = p.position().add(0, 1, 0);
		Fx.sound(l, c0, SoundEvents.BEACON_POWER_SELECT, 1F, res ? 1.4F : 1.1F);
		Fx.sound(l, c0, SoundEvents.AMETHYST_BLOCK_CHIME, 1F, 0.8F);
		for (int i = 0; i < 40; i++) {
			double a = i * Math.PI / 10, y = i * 0.07;
			Fx.at(l, Fx.dust(e.color, 1.3F), c0.add(Math.cos(a) * 1.2, y - 0.8, Math.sin(a) * 1.2));
		}
		Fx.burst(l, com.elementalweapons.fx.P.END_ROD, c0, res ? 30 : 15, 0.8, 0.15);
	}
}
