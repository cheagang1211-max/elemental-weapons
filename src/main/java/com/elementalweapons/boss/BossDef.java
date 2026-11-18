package com.elementalweapons.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.ChatFormatting;
import net.minecraft.world.BossEvent;
import net.minecraft.world.item.Item;

import com.elementalweapons.item.Element;

/** 보스 하나의 설정 (이름, 속성, 체력, 크기, 패턴, 보상, 대사) */
public final class BossDef {
	public record Drop(Supplier<Item> item, int min, int max) {}

	public final String id, ko, en;
	public Element primary, secondary;
	/** true면 체력 1/7마다 속성이 바뀜 (최종 보스) */
	public boolean shifting;
	public long health = 1_000_000_000L;
	public BossEvent.BossBarColor barColor = BossEvent.BossBarColor.WHITE;
	public ChatFormatting nameColor = ChatFormatting.WHITE;
	public String icon = "✦";
	public float width = 2.4F, height = 4.0F, eyeHeight = 3.2F, renderScale = 2.0F, shadow = 1.5F;
	public double hoverHeight = 3.5, hoverDistance = 7.0, speed = 0.28;
	/** 공중에 떠서 둥둥 떠다니는 애니메이션 */
	public boolean floating = true;
	public String intro = "", phase2 = "", phase3 = "";
	public final List<PatternSpec> patterns = new ArrayList<>();
	public final List<Drop> drops = new ArrayList<>();

	private BossDef(String id, String ko, String en) {
		this.id = id; this.ko = ko; this.en = en;
	}

	public static BossDef of(String id, String ko, String en) {
		return new BossDef(id, ko, en);
	}

	public BossDef element(Element primary, Element secondary) { this.primary = primary; this.secondary = secondary; return this; }
	public BossDef shifting() { this.shifting = true; return this; }
	public BossDef health(long hp) { this.health = hp; return this; }
	public BossDef look(String icon, ChatFormatting nameColor, BossEvent.BossBarColor bar) { this.icon = icon; this.nameColor = nameColor; this.barColor = bar; return this; }
	public BossDef size(float w, float h, float eye, float renderScale, float shadow) { this.width = w; this.height = h; this.eyeHeight = eye; this.renderScale = renderScale; this.shadow = shadow; return this; }
	public BossDef hover(double height, double distance, double speed, boolean floating) { this.hoverHeight = height; this.hoverDistance = distance; this.speed = speed; this.floating = floating; return this; }
	public BossDef lines(String intro, String phase2, String phase3) { this.intro = intro; this.phase2 = phase2; this.phase3 = phase3; return this; }
	public BossDef pattern(PatternType type, String name, Element e, int minPhase, int weight, float damage, int count, double radius) {
		patterns.add(PatternSpec.of(type, name, e, minPhase, weight, damage, count, radius)); return this;
	}
	public BossDef drop(Supplier<Item> item, int min, int max) { drops.add(new Drop(item, min, max)); return this; }
}
