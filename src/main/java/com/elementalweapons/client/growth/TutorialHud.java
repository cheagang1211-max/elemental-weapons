package com.elementalweapons.client.growth;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import com.elementalweapons.growth.ModGrowth;
import com.elementalweapons.growth.Tutorial;
import com.elementalweapons.item.PlayerClass;
import com.elementalweapons.world.sky.Arenas;

/** 화면 오른쪽 위 튜토리얼 안내 */
public final class TutorialHud {
	private TutorialHud() {}

	public static void render(GuiGraphicsExtractor g, DeltaTracker dt) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;
		Tutorial.Data d = Tutorial.get(mc.player);
		if (d.step() >= Tutorial.DONE) return;
		Font font = mc.font;
		PlayerClass c = ModGrowth.get(mc.player).playerClass();
		String title = Tutorial.title(d.step(), c);
		int goal = Tutorial.goal(d.step(), c);
		if (goal > 1) title += "  (" + d.count() + "/" + goal + ")";
		String hint = Tutorial.hint(d.step(), c);
		TutorialArrows.Target tg = TutorialArrows.target(mc);
		if (tg != null) hint = "바닥 화살표를 따라가세요 → " + tg.name() + " (" + (int) Math.hypot(tg.pos().x - mc.player.getX(), tg.pos().z - mc.player.getZ()) + "칸)";
		int w = Math.max(font.width(title), (int) (font.width(hint) * 0.75F)) + 16;
		w = Math.max(w, 150);
		int x = g.guiWidth() - w - 6, y = 6, h = 42;
		g.fillGradient(x, y, x + w, y + h, 0xD0151A33, 0xD0160E28);
		g.outline(x, y, w, h, 0xFF7CFFB0);
		g.fill(x + 1, y + 1, x + 3, y + h - 1, c == PlayerClass.NONE ? 0xFF7CFFB0 : c.color);
		g.text(font, "튜토리얼 " + Tutorial.display(d.step()) + "/" + Tutorial.TOTAL
				+ (c != PlayerClass.NONE ? " · " + c.koName : ""), x + 8, y + 5, 0xFF7CFFB0, false);
		g.text(font, title, x + 8, y + 16, 0xFFFFFFFF, true);
		g.pose().pushMatrix();
		g.pose().translate(x + 8, y + 29);
		g.pose().scale(0.75F, 0.75F);
		g.text(font, hint, 0, 0, 0xFFC9C2E6, false);
		g.pose().popMatrix();
	}
}
