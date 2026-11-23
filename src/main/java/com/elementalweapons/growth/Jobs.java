package com.elementalweapons.growth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import com.elementalweapons.ElementalWeaponsMod;
import com.elementalweapons.boss.ElementFx;
import com.elementalweapons.item.Element;
import com.elementalweapons.item.PlayerClass;
import com.elementalweapons.spell.Fx;
import com.elementalweapons.spell.MagicCircle;
import com.elementalweapons.spell.SpellManager;

/**
 * 직업별 전직:
 *  1차 (Lv10) 직업 + 속성  →  2차 (Lv50) 직업마다 두 갈래 중 하나  →  3차 (Lv120) 그 갈래의 상위 직업
 *  천공성의 '전직 교관'에게 가서 시험(원소 지역 몹 처치 + 보스 처치)을 마치면 전직.
 *  전직하면: 새 기술(V키, 3차에 각성), 몸을 감싸는 속성 오라·광륜, 이름 앞 칭호, 화려한 전직 연출.
 */
public final class Jobs {
	private Jobs() {}

	public static final int LV2 = 50, LV3 = 120;
	public static final int KILLS2 = 50, BOSSES2 = 1, KILLS3 = 150, BOSSES3 = 3;

	/** stage: 1 = 1차까지(또는 전직 전), 2, 3 · branch: 0 없음, 1·2 · trial: 진행 중인 시험(0, 2, 3) */
	public record Data(int stage, int branch, int trial, int kills, int bosses) {
		public static final Data DEFAULT = new Data(1, 0, 0, 0, 0);
		public static final Codec<Data> CODEC = RecordCodecBuilder.create(i -> i.group(
				Codec.INT.fieldOf("stage").forGetter(Data::stage),
				Codec.INT.fieldOf("branch").forGetter(Data::branch),
				Codec.INT.fieldOf("trial").forGetter(Data::trial),
				Codec.INT.fieldOf("kills").forGetter(Data::kills),
				Codec.INT.fieldOf("bosses").forGetter(Data::bosses)).apply(i, Data::new));
		public static final StreamCodec<ByteBuf, Data> STREAM = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, Data::stage, ByteBufCodecs.VAR_INT, Data::branch, ByteBufCodecs.VAR_INT, Data::trial,
				ByteBufCodecs.VAR_INT, Data::kills, ByteBufCodecs.VAR_INT, Data::bosses, Data::new);
	}

	public static final AttachmentType<Data> DATA = AttachmentRegistry.<Data>builder()
			.persistent(Data.CODEC).copyOnDeath().initializer(() -> Data.DEFAULT)
			.syncWith(Data.STREAM, AttachmentSyncPredicate.targetOnly())
			.buildAndRegister(ElementalWeaponsMod.id("job"));

	// [직업][갈래 1·2][단계 2·3]
	private static final String[][][] NAMES = {
			{},
			{{"원소술사", "원소의 군주"}, {"소환술사", "차원의 지배자"}},          // 마법사
			{{"광전사", "전쟁의 화신"}, {"파쇄자", "대지의 파괴자"}},            // 전사
			{{"성기사", "성전의 수호자"}, {"용기사", "용왕 기사"}},              // 기사
			{{"그림자", "밤의 군주"}, {"칼날무희", "천검무희"}},                // 암살자
			{{"사제", "대주교"}, {"음유시인", "별의 노래꾼"}},                  // 서포터
			{{"영혼 수확자", "명계의 왕"}, {"저주술사", "재앙의 사도"}},          // 사신
			{{"저격수", "천공의 사수"}, {"사냥꾼", "야수의 왕"}}};              // 궁수
	private static final String[][] DESC = {
			{},
			{"원소를 하늘에서 쏟아붓는 파괴의 마법사", "원소 정령을 불러 함께 싸우는 마법사"},
			{"피가 끓을수록 강해지는 광폭한 전사", "땅을 갈라 적을 띄우는 전사"},
			{"아군을 지키고 치유하는 신성한 기사", "하늘 높이 뛰어올라 내리꽂는 기사"},
			{"적 사이를 순간 이동하며 베는 암살자", "칼날을 두르고 춤추듯 베는 암살자"},
			{"아군을 크게 치유하고 보호하는 서포터", "노래로 아군을 강하게, 적을 약하게"},
			{"적을 끌어모아 영혼째 베어 가르는 사신", "저주의 낙인으로 적을 서서히 무너뜨리는 사신"},
			{"한 줄로 모든 것을 꿰뚫는 저격수", "하늘에서 화살비를 퍼붓는 궁수"}};
	private static final String[][] SKILL = {
			{},
			{"원소 폭격", "원소 정령 소환"},
			{"광폭화", "대지 분쇄"},
			{"신성 결계", "용의 도약"},
			{"그림자 분신", "칼날 폭풍"},
			{"대치유", "전투의 노래"},
			{"영혼 수확", "재앙의 낙인"},
			{"관통 저격", "화살비"}};

	public static Data get(ServerPlayer p) { return p.getAttachedOrCreate(DATA); }

	public static String title(int cls, int branch, int stage) {
		if (cls <= 0 || cls >= NAMES.length || branch < 1 || branch > 2 || stage < 2) return PlayerClass.of(Math.max(0, cls)).koName;
		return NAMES[cls][branch - 1][Math.min(3, stage) - 2];
	}
	public static String desc(int cls, int branch) { return cls <= 0 || cls >= DESC.length || branch < 1 ? "" : DESC[cls][branch - 1]; }
	public static String skill(int cls, int branch, int stage) {
		if (cls <= 0 || cls >= SKILL.length || branch < 1) return "";
		return (stage >= 3 ? "각성: " : "") + SKILL[cls][branch - 1];
	}

	/** 지금 할 수 있는 일 (화면용): 0 없음, 1 시험 받기 가능, 2 시험 중, 3 시험 완료(전직 가능) */
	public static int state(int level, boolean evolved, Data d) {
		if (!evolved || d.stage() >= 3) return 0;
		int next = d.stage() + 1;
		if (d.trial() == next) return done(d) ? 3 : 2;
		return level >= (next == 2 ? LV2 : LV3) ? 1 : 0;
	}
	public static boolean done(Data d) {
		return d.trial() == 2 ? d.kills() >= KILLS2 && d.bosses() >= BOSSES2 : d.trial() == 3 && d.kills() >= KILLS3 && d.bosses() >= BOSSES3;
	}

	// ---------------- 네트워크 ----------------
	public record Open() implements CustomPacketPayload {
		public static final Open INSTANCE = new Open();
		public static final Type<Open> TYPE = new Type<>(ElementalWeaponsMod.id("job_open"));
		public static final StreamCodec<ByteBuf, Open> CODEC = StreamCodec.unit(INSTANCE);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}
	/** action 1 = 시험 받기, 2 = 2차 전직(arg 갈래 1·2), 3 = 3차 전직 */
	public record Action(int action, int arg) implements CustomPacketPayload {
		public static final Type<Action> TYPE = new Type<>(ElementalWeaponsMod.id("job_action"));
		public static final StreamCodec<ByteBuf, Action> CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, Action::action, ByteBufCodecs.VAR_INT, Action::arg, Action::new);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	public static void initialize() {
		PayloadTypeRegistry.clientboundPlay().register(Open.TYPE, Open.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(Action.TYPE, Action.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(Action.TYPE, (a, ctx) -> action(ctx.player(), a.action(), a.arg()));
		// 오라·광륜 (전직 단계가 보이게) + 이름 칭호
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			int t = server.getTickCount();
			for (ServerPlayer p : server.getPlayerList().getPlayers()) {
				if (t % 100 == 13) updateTeam(p);
				if (t % 4 == 0) aura(p, t);
			}
		});
	}

	public static void open(ServerPlayer p) {
		if (ServerPlayNetworking.canSend(p, Open.TYPE)) ServerPlayNetworking.send(p, Open.INSTANCE);
	}

	public static void action(ServerPlayer p, int action, int arg) {
		Growth g = ModGrowth.get(p);
		Data d = get(p);
		boolean evolved = Evolution.element(p) >= 0;
		int st = state(g.level(), evolved, d);
		switch (action) {
			case 1 -> {
				if (st != 1) return;
				int next = d.stage() + 1;
				p.setAttached(DATA, new Data(d.stage(), d.branch(), next, 0, 0));
				p.sendSystemMessage(Component.literal("⚔ " + next + "차 전직 시험 시작! 원소 지역 몹 " + (next == 2 ? KILLS2 : KILLS3) + "마리와 보스 "
						+ (next == 2 ? BOSSES2 : BOSSES3) + "마리를 쓰러뜨린 뒤 전직 교관에게 돌아오세요.").withStyle(ChatFormatting.GOLD));
			}
			case 2 -> { if (st == 3 && d.trial() == 2 && (arg == 1 || arg == 2)) advance(p, 2, arg); }
			case 3 -> { if (st == 3 && d.trial() == 3 && d.branch() > 0) advance(p, 3, d.branch()); }
			default -> {}
		}
	}

	/** 일반 몹 처치 (원소 지역) */
	public static void onKill(ServerPlayer p) {
		Data d = get(p);
		if (d.trial() < 2 || done(d)) return;
		if (com.elementalweapons.world.sky.SkyLayout.regionAt(p.getX(), p.getZ()) < 0) return;
		Data n = new Data(d.stage(), d.branch(), d.trial(), d.kills() + 1, d.bosses());
		p.setAttached(DATA, n);
		progressMsg(p, n);
	}

	public static void onBossKill(ServerPlayer p) {
		Data d = get(p);
		if (d.trial() < 2 || done(d)) return;
		Data n = new Data(d.stage(), d.branch(), d.trial(), d.kills(), d.bosses() + 1);
		p.setAttached(DATA, n);
		progressMsg(p, n);
	}

	private static void progressMsg(ServerPlayer p, Data d) {
		int k = d.trial() == 2 ? KILLS2 : KILLS3, b = d.trial() == 2 ? BOSSES2 : BOSSES3;
		if (done(d)) {
			p.sendSystemMessage(Component.literal("✦ " + d.trial() + "차 전직 시험 완료! 천공성의 전직 교관에게 가세요.").withStyle(ChatFormatting.GREEN));
			Fx.sound((ServerLevel) p.level(), p.position(), SoundEvents.PLAYER_LEVELUP, 1F, 1.4F);
		} else p.sendOverlayMessage(Component.literal("⚔ 전직 시험: 몹 " + Math.min(k, d.kills()) + "/" + k + " · 보스 " + Math.min(b, d.bosses()) + "/" + b).withStyle(ChatFormatting.GOLD));
	}

	/** 현자 초기화 · 직업 교체 때 */
	public static void reset(ServerPlayer p) {
		p.setAttached(DATA, Data.DEFAULT);
		updateTeam(p);
		ModGrowth.applyStats(p);
	}

	private static void advance(ServerPlayer p, int stage, int branch) {
		ServerLevel l = (ServerLevel) p.level();
		p.setAttached(DATA, new Data(stage, branch, 0, 0, 0));
		int cls = ModGrowth.get(p).cls();
		int el = Evolution.element(p);
		Element e = Element.values()[Math.max(0, el)];
		String name = title(cls, branch, stage);
		int rgb = ElementFx.color(e);
		// 연출: 발밑 마법진 + 빛기둥 + 큰 제목
		SpellManager.add(new MagicCircle(l, p, e, p.position().add(0, 0.1, 0), stage == 3 ? 6 : 4.5, 80, false));
		Vec3 c = p.position();
		for (double y = 0; y < 30; y += 0.5) Fx.burst(l, ElementFx.dust(e, 1.6F), c.add(0, y, 0), 2, 0.25, 0.0);
		Fx.burst(l, ElementFx.particle(e), c.add(0, 1, 0), 80, 1.2, 0.3);
		Fx.sound(l, c, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.2F, 1F);
		Fx.sound(l, c, SoundEvents.BEACON_POWER_SELECT, 1.5F, 0.8F);
		p.connection.send(new ClientboundSetTitlesAnimationPacket(10, 60, 20));
		p.connection.send(new ClientboundSetTitleTextPacket(Component.literal(name).withStyle(st -> st.withColor(rgb).withBold(true))));
		p.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(stage + "차 전직 · 새 기술 [V] " + skill(cls, branch, stage)).withStyle(ChatFormatting.WHITE)));
		Component all = Component.literal("✦ " + p.getName().getString() + "님이 ").withStyle(ChatFormatting.GOLD)
				.append(Component.literal("[" + name + "]").withStyle(st -> st.withColor(rgb).withBold(true)))
				.append(Component.literal("(으)로 " + stage + "차 전직했습니다!").withStyle(ChatFormatting.GOLD));
		for (ServerPlayer q : l.getServer().getPlayerList().getPlayers()) q.sendSystemMessage(all);
		updateTeam(p);
		ModGrowth.applyStats(p);
		p.sendSystemMessage(Component.literal("✦ 모든 기술의 피해가 " + (stage >= 3 ? "1.7" : "1.3") + "배가 되고 연출이 화려해졌습니다.").withStyle(ChatFormatting.AQUA));
	}

	/** 전직 단계 피해 배율: 기존 기술 모두(우클릭·Z·X·C·콤보·평타) — 2차 1.3배, 3차 1.7배 */
	public static float damageMult(net.minecraft.world.entity.player.Player p) {
		Data d = p.getAttached(DATA);
		if (d == null) return 1F;
		return d.stage() >= 3 ? 1.7F : d.stage() >= 2 ? 1.3F : 1F;
	}

	private static final java.util.Map<java.util.UUID, Long> LAST_FX = new java.util.HashMap<>();

	/**
	 * 전직하면 기존 기술 연출이 더 화려해짐 (모든 직업 공통).
	 *  2차: 발밑 속성 고리 + 몸을 감아 오르는 나선 + 속성 입자 폭발
	 *  3차: 발밑 마법진 + 하늘로 솟는 빛기둥 + 퍼져 나가는 충격파 고리 (궁극기면 더 크게)
	 */
	public static void flourish(ServerPlayer p, Element e, boolean ultimate) {
		Data d = get(p);
		if (d.stage() < 2 || e == null) return;
		ServerLevel l = (ServerLevel) p.level();
		long now = l.getGameTime();
		Long last = LAST_FX.get(p.getUUID());
		if (last != null && now - last < (ultimate ? 4 : 10)) return;
		LAST_FX.put(p.getUUID(), now);
		Vec3 c = p.position();
		int rgb = ElementFx.color(e), rgb2 = ElementFx.color2(e);
		boolean three = d.stage() >= 3;
		double r = (three ? 3.2 : 2.2) * (ultimate ? 1.6 : 1);
		ElementFx.groundRing(l, rgb, c.add(0, 0.1, 0), r, 2F, ultimate ? 16 : 10);
		for (double t = 0; t < (three ? 3.0 : 2.0); t += 0.08) {   // 감아 오르는 나선
			double a = t * Math.PI * 2 + now * 0.2;
			Fx.at(l, ElementFx.dust(e, three ? 1.1F : 0.8F), c.add(Math.cos(a) * 0.9, t * 0.9, Math.sin(a) * 0.9));
		}
		Fx.burst(l, ElementFx.particle(e), c.add(0, 1, 0), three ? 30 : 16, 0.8, 0.12);
		if (three) {
			SpellManager.add(new MagicCircle(l, p, e, c.add(0, 0.1, 0), ultimate ? 5 : 3, ultimate ? 40 : 22, true));
			for (double y = 0; y < (ultimate ? 14 : 8); y += 0.4) Fx.at(l, Fx.dust(rgb2, 1.3F), c.add(0, y, 0));
			for (int k = 0; k < 2; k++)
				Fx.at(l, com.elementalweapons.fx.FxOptions.of(com.elementalweapons.fx.FxOptions.RING, rgb, ultimate ? 2.6F : 1.6F, 14).grow(3.2F).flat(), c.add(0, 0.15 + k * 0.4, 0));
			Fx.sound(l, c, SoundEvents.AMETHYST_BLOCK_RESONATE, ultimate ? 1.4F : 0.7F, 1.4F);
		}
		if (ultimate) Fx.sound(l, c, SoundEvents.BEACON_POWER_SELECT, 0.6F, 1.8F);
	}

	/** 이름 앞 칭호 (머리 위 이름표·탭 목록) */
	private static void updateTeam(ServerPlayer p) {
		Scoreboard sb = p.level().getServer().getScoreboard();
		Data d = get(p);
		int cls = ModGrowth.get(p).cls();
		int el = Evolution.element(p);
		String want = d.stage() >= 2 && cls > 0 && d.branch() > 0 ? "ewjob_" + cls + "_" + d.branch() + "_" + d.stage() + "_" + Math.max(0, el) : null;
		PlayerTeam cur = sb.getPlayersTeam(p.getScoreboardName());
		if (cur != null && cur.getName().startsWith("ewjob_") && !cur.getName().equals(want)) sb.removePlayerFromTeam(p.getScoreboardName(), cur);
		if (want == null) return;
		if (cur != null && cur.getName().equals(want)) return;
		if (cur != null && !cur.getName().startsWith("ewjob_")) return;   // 다른 팀(관리자가 만든 것)은 건드리지 않음
		PlayerTeam team = sb.getPlayerTeam(want);
		if (team == null) {
			team = sb.addPlayerTeam(want);
			int rgb = ElementFx.color(Element.values()[Math.max(0, el)]);
			team.setPlayerPrefix(Component.literal("[" + title(cls, d.branch(), d.stage()) + "] ").withStyle(st -> st.withColor(rgb).withBold(d.stage() >= 3)));
		}
		sb.addPlayerToTeam(p.getScoreboardName(), team);
	}

	/** 전직 외형: 2차 = 발밑에서 피어오르는 속성 기운, 3차 = 머리 위 광륜 + 등 뒤 빛 날개 */
	private static void aura(ServerPlayer p, int t) {
		if (p.isSpectator() || p.isInvisible()) return;
		Data d = get(p);
		if (d.stage() < 2) return;
		int el = Evolution.element(p);
		if (el < 0) return;
		Element e = Element.values()[el];
		ServerLevel l = (ServerLevel) p.level();
		Vec3 c = p.position();
		var rnd = l.getRandom();
		for (int i = 0; i < 2; i++) {
			double a = rnd.nextDouble() * Math.PI * 2;
			Fx.at(l, ElementFx.dust(e, 0.7F), c.add(Math.cos(a) * 0.55, 0.1 + rnd.nextDouble() * 0.6, Math.sin(a) * 0.55));
		}
		if (d.stage() >= 3) {
			// 광륜
			double spin = t * 0.08;
			for (int k = 0; k < 10; k++) {
				double a = spin + k * Math.PI / 5;
				Fx.at(l, ElementFx.dust(e, 0.5F), c.add(Math.cos(a) * 0.38, 2.35, Math.sin(a) * 0.38));
			}
			// 등 뒤 날개 (빛 입자 두 갈래)
			if (t % 8 == 0) {
				Vec3 back = Vec3.directionFromRotation(0, p.yBodyRot).multiply(1, 0, 1).normalize().scale(-0.35);
				Vec3 side = new Vec3(-back.z, 0, back.x).normalize();
				for (int s = -1; s <= 1; s += 2)
					for (double u = 0.2; u <= 1.3; u += 0.18) {
						double h = 1.35 + Math.sin(u * 2.2) * 0.55 + Math.sin(t * 0.15) * 0.05;
						Fx.at(l, ElementFx.dust(e, 0.6F), c.add(back).add(side.scale(s * u)).add(0, h, 0));
					}
			}
		}
	}
}
