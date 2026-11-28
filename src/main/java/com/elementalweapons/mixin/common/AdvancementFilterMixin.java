package com.elementalweapons.mixin.common;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerAdvancementManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 바닐라 도전과제(모험·네더·엔드·농사·마인크래프트 탭)를 모두 없애고 이 세계 전용 도전과제만 남김.
 * 제작법 해금용(minecraft:recipes/…)은 화면에 안 보이고 제작법 책에 필요하므로 그대로 둠.
 */
@Mixin(ServerAdvancementManager.class)
public abstract class AdvancementFilterMixin {
	@Shadow @Final @Mutable private Map<Identifier, AdvancementHolder> advancements;
	@Shadow @Final private AdvancementTree tree;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void elementalweapons$dropVanilla(net.minecraft.core.HolderLookup.Provider registries, CallbackInfo ci) {
		Set<Identifier> drop = new HashSet<>();
		for (Identifier id : advancements.keySet())
			if (id.getNamespace().equals("minecraft") && !id.getPath().startsWith("recipes/")) drop.add(id);
		if (drop.isEmpty()) return;
		tree.remove(drop);
		com.google.common.collect.ImmutableMap.Builder<Identifier, AdvancementHolder> b = com.google.common.collect.ImmutableMap.builder();
		for (var e : advancements.entrySet()) if (!drop.contains(e.getKey())) b.put(e.getKey(), e.getValue());
		advancements = b.build();
		com.elementalweapons.ElementalWeaponsMod.LOGGER.info("[도전과제] 바닐라 {}개 제거, 남은 것 {}개", drop.size(), advancements.size());
	}
}
