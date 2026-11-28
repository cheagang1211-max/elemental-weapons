package com.elementalweapons.growth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

import com.elementalweapons.ElementalWeaponsMod;

/**
 * 캐릭터 슬롯 (플레이어당 최대 9명, 메이플스토리처럼 접속할 때 고름).
 * 지금 쓰는 캐릭터는 '진짜 플레이어 데이터', 나머지는 플레이어 저장 데이터(NBT) 통째로 보관.
 * 바꿀 때: 지금 상태를 저장 → 고른 캐릭터를 불러와 덮어씀 (가방·경험치·체력·위치·성장 등 전부).
 */
public final class Characters {
	private Characters() {}

	public static final int SLOTS = 9;
	private static final String ATTACH_KEY = "fabric:attachments";
	private static final String MY_KEY = ElementalWeaponsMod.MOD_ID + ":characters";

	/** active: 지금 쓰는 슬롯, slots: 각 슬롯의 저장본 (빈 태그 = 아직 없는 캐릭터. 지금 슬롯은 비워 둠) */
	public record Roster(int active, List<CompoundTag> slots) {
		public static final Codec<Roster> CODEC = RecordCodecBuilder.create(i -> i.group(
				Codec.INT.fieldOf("active").forGetter(Roster::active),
				CompoundTag.CODEC.listOf().fieldOf("slots").forGetter(Roster::slots)).apply(i, Roster::new));
		static Roster fresh() { List<CompoundTag> l = new ArrayList<>(); for (int i = 0; i < SLOTS; i++) l.add(new CompoundTag()); return new Roster(0, l); }
	}

	public static final AttachmentType<Roster> ROSTER = AttachmentRegistry.<Roster>builder()
			.persistent(Roster.CODEC).copyOnDeath()
			.buildAndRegister(Identifier.fromNamespaceAndPath(ElementalWeaponsMod.MOD_ID, "characters"));

	/** 화면에 보여 줄 슬롯 요약 */
	public record Info(int slot, boolean used, int level, int cls, int elem, String job, List<ItemStack> gear) {}

	public record Open(int active, List<Info> infos) implements CustomPacketPayload {
		public static final Type<Open> TYPE = new Type<>(ElementalWeaponsMod.id("characters_open"));
		public static final StreamCodec<RegistryFriendlyByteBuf, Open> CODEC = StreamCodec.of((buf, o) -> {
			buf.writeVarInt(o.active);
			buf.writeVarInt(o.infos.size());
			for (Info f : o.infos) {
				buf.writeVarInt(f.slot); buf.writeBoolean(f.used); buf.writeVarInt(f.level); buf.writeVarInt(f.cls); buf.writeVarInt(f.elem + 1);
				buf.writeUtf(f.job);
				buf.writeVarInt(f.gear.size());
				for (ItemStack s : f.gear) ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, s);
			}
		}, buf -> {
			int a = buf.readVarInt(), n = buf.readVarInt();
			List<Info> l = new ArrayList<>();
			for (int k = 0; k < n; k++) {
				int slot = buf.readVarInt(); boolean used = buf.readBoolean(); int lv = buf.readVarInt(), cls = buf.readVarInt(), el = buf.readVarInt() - 1;
				String job = buf.readUtf();
				int g = buf.readVarInt();
				List<ItemStack> gear = new ArrayList<>();
				for (int j = 0; j < g; j++) gear.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
				l.add(new Info(slot, used, lv, cls, el, job, gear));
			}
			return new Open(a, l);
		});
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	public record Pick(int slot) implements CustomPacketPayload {
		public static final Type<Pick> TYPE = new Type<>(ElementalWeaponsMod.id("characters_pick"));
		public static final StreamCodec<io.netty.buffer.ByteBuf, Pick> CODEC = ByteBufCodecs.VAR_INT.map(Pick::new, Pick::slot);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	/** 아직 캐릭터를 고르는 중인 플레이어 (피해를 받지 않음) */
	private static final Set<UUID> CHOOSING = new HashSet<>();

	public static boolean choosing(ServerPlayer p) { return CHOOSING.contains(p.getUUID()); }

	public static void initialize() {
		PayloadTypeRegistry.clientboundPlay().register(Open.TYPE, Open.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(Pick.TYPE, Pick.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(Pick.TYPE, (pl, ctx) -> pick(ctx.player(), pl.slot()));
		ServerPlayConnectionEvents.JOIN.register((h, sender, server) -> server.execute(() -> open(h.getPlayer())));
		ServerPlayConnectionEvents.DISCONNECT.register((h, server) -> CHOOSING.remove(h.getPlayer().getUUID()));
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((e, src, amt) -> !(e instanceof ServerPlayer p && choosing(p)));
	}

	static Roster roster(ServerPlayer p) {
		Roster r = p.getAttached(ROSTER);
		if (r == null || r.slots().size() != SLOTS) { r = Roster.fresh(); p.setAttached(ROSTER, r); }
		return r;
	}

	/** 접속할 때: 캐릭터 선택 화면 열기 */
	public static void open(ServerPlayer p) {
		Roster r = roster(p);
		CHOOSING.add(p.getUUID());
		List<Info> infos = new ArrayList<>();
		for (int i = 0; i < SLOTS; i++) infos.add(i == r.active() ? liveInfo(p, i) : storedInfo(p, i, r.slots().get(i)));
		ServerPlayNetworking.send(p, new Open(r.active(), infos));
	}

	private static String job(int cls, Jobs.Data d) {
		try { return Jobs.title(cls, d.branch(), d.stage()); } catch (Throwable t) { return ""; }
	}

	private static Info liveInfo(ServerPlayer p, int slot) {
		Growth g = ModGrowth.get(p);
		List<ItemStack> gear = new ArrayList<>();
		for (EquipmentSlot s : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND})
			gear.add(p.getItemBySlot(s).copy());
		return new Info(slot, true, g.level(), g.cls(), Evolution.element(p), job(g.cls(), Jobs.get(p)), gear);
	}

	private static Info storedInfo(ServerPlayer p, int slot, CompoundTag t) {
		if (t.isEmpty()) return new Info(slot, false, 0, 0, -1, "", List.of());
		CompoundTag sum = t.getCompoundOrEmpty("sum");
		List<ItemStack> gear = new ArrayList<>();
		var ops = p.registryAccess().createSerializationContext(NbtOps.INSTANCE);
		for (Tag it : t.getListOrEmpty("gear")) gear.add(ItemStack.OPTIONAL_CODEC.parse(ops, it).result().orElse(ItemStack.EMPTY));
		return new Info(slot, true, sum.getIntOr("lv", 1), sum.getIntOr("cls", 0), sum.getIntOr("el", -1), sum.getStringOr("job", ""), gear);
	}

	// ------------------------------------------------------------------ 고르기
	static void pick(ServerPlayer p, int slot) {
		if (!CHOOSING.remove(p.getUUID())) return;
		if (slot < 0 || slot >= SLOTS) slot = roster(p).active();
		Roster r = roster(p);
		if (slot == r.active()) {
			p.sendSystemMessage(Component.literal("✦ 캐릭터 " + (slot + 1) + " (으)로 플레이합니다.").withStyle(ChatFormatting.GOLD));
			return;
		}
		try {
			List<CompoundTag> slots = new ArrayList<>(r.slots());
			slots.set(r.active(), snapshot(p));
			CompoundTag target = slots.get(slot);
			slots.set(slot, new CompoundTag());
			Long daily = p.getAttached(com.elementalweapons.world.sky.Summit.DAILY);   // 매일 선물은 계정 하나에 한 번
			if (target.isEmpty()) fresh(p); else restore(p, target.getCompoundOrEmpty("data"));
			if (daily != null) p.setAttached(com.elementalweapons.world.sky.Summit.DAILY, daily);
			p.setAttached(ROSTER, new Roster(slot, slots));
			resync(p);
			p.sendSystemMessage(Component.literal(target.isEmpty() ? "✦ 새 캐릭터 " + (slot + 1) + " 을(를) 시작합니다!" : "✦ 캐릭터 " + (slot + 1) + " (으)로 플레이합니다.").withStyle(ChatFormatting.GOLD));
		} catch (Throwable ex) {
			ElementalWeaponsMod.LOGGER.error("[캐릭터] 바꾸기 실패", ex);
			p.sendSystemMessage(Component.literal("캐릭터를 바꾸지 못했습니다. 지금 캐릭터로 계속합니다.").withStyle(ChatFormatting.RED));
		}
	}

	/** 지금 플레이어 상태를 저장본으로 */
	static CompoundTag snapshot(ServerPlayer p) {
		TagValueOutput out = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, p.registryAccess());
		p.saveWithoutId(out);
		CompoundTag data = out.buildResult();
		CompoundTag att = data.getCompoundOrEmpty(ATTACH_KEY);
		att.remove(MY_KEY);
		data.put(ATTACH_KEY, att);
		data.putString("ew_dim", p.level().dimension().identifier().toString());
		Growth g = ModGrowth.get(p);
		CompoundTag sum = new CompoundTag();
		sum.putInt("lv", g.level()); sum.putInt("cls", g.cls()); sum.putInt("el", Evolution.element(p));
		sum.putString("job", job(g.cls(), Jobs.get(p)));
		net.minecraft.nbt.ListTag gear = new net.minecraft.nbt.ListTag();
		var ops = p.registryAccess().createSerializationContext(NbtOps.INSTANCE);
		for (EquipmentSlot s : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND})
			gear.add(ItemStack.OPTIONAL_CODEC.encodeStart(ops, p.getItemBySlot(s)).result().orElse(new CompoundTag()));
		CompoundTag t = new CompoundTag();
		t.put("data", data); t.put("sum", sum); t.put("gear", gear);
		return t;
	}

	private static void clearPersonal(ServerPlayer p) {
		p.removeAllEffects();
		p.getInventory().clearContent();
		p.getEnderChestInventory().clearContent();
		p.removeAttached(ModGrowth.GROWTH);
		p.removeAttached(ModGrowth.PARAGON);
		p.removeAttached(CoreForge.DATA);
		p.removeAttached(Evolution.ELEMENT);
		p.removeAttached(Jobs.DATA);
		p.removeAttached(Tutorial.DATA);
	}

	/** 새 캐릭터: 처음 접속한 사람처럼 */
	private static void fresh(ServerPlayer p) {
		clearPersonal(p);
		p.setExperienceLevels(0); p.setExperiencePoints(0);
		p.setHealth(p.getMaxHealth());
		p.getFoodData().setFoodLevel(20); p.getFoodData().setSaturation(5);
		ServerLevel ow = p.level().getServer().overworld();
		var sp = com.elementalweapons.world.sky.SkyLayout.SPAWN;
		p.teleportTo(ow, sp.getX() + 0.5, sp.getY(), sp.getZ() + 0.5, Set.of(), p.getYRot(), 0, false);
		Tutorial.grantArmorIfMissing(p);
	}

	/** 저장본 불러오기 */
	private static void restore(ServerPlayer p, CompoundTag data) {
		clearPersonal(p);
		CompoundTag d = data.copy();
		for (String k : new String[]{"UUID", "playerGameType", "previousPlayerGameType", "active_effects", "RootVehicle", "ShoulderEntityLeft", "ShoulderEntityRight", "abilities"}) d.remove(k);
		CompoundTag att = d.getCompoundOrEmpty(ATTACH_KEY);
		att.remove(MY_KEY);
		d.put(ATTACH_KEY, att);
		Roster keep = p.getAttached(ROSTER);
		p.load(TagValueInput.create(ProblemReporter.DISCARDING, p.registryAccess(), d));
		if (keep != null) p.setAttached(ROSTER, keep);
		// 위치·차원
		var pos = d.getListOrEmpty("Pos");
		double x = pos.getDoubleOr(0, p.getX()), y = pos.getDoubleOr(1, p.getY()), z = pos.getDoubleOr(2, p.getZ());
		var rot = d.getListOrEmpty("Rotation");
		float yaw = rot.getFloatOr(0, 0), pitch = rot.getFloatOr(1, 0);
		ServerLevel lv = p.level().getServer().overworld();
		String dim = d.getStringOr("ew_dim", "");
		if (!dim.isEmpty()) {
			ServerLevel l2 = p.level().getServer().getLevel(ResourceKey.create(Registries.DIMENSION, Identifier.parse(dim)));
			if (l2 != null) lv = l2;
		}
		p.teleportTo(lv, x, y, z, Set.of(), yaw, pitch, false);
	}

	/** 바꾼 뒤 내 화면에 다시 보내기 */
	private static void resync(ServerPlayer p) {
		for (AttachmentType<?> t : List.<AttachmentType<?>>of(ModGrowth.GROWTH, ModGrowth.PARAGON, CoreForge.DATA, Evolution.ELEMENT, Jobs.DATA, Tutorial.DATA)) resyncOne(p, t);
		p.inventoryMenu.sendAllDataToRemote();
		p.containerMenu.sendAllDataToRemote();
		p.resetSentInfo();
		p.onUpdateAbilities();
		ModGrowth.applyStats(p);
	}

	private static <T> void resyncOne(ServerPlayer p, AttachmentType<T> t) {
		T v = p.getAttached(t);
		if (v != null) { p.removeAttached(t); p.setAttached(t, v); }
		else p.removeAttached(t);
	}

	static boolean isLevel(Level l) { return l != null; }
}
