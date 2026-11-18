package com.elementalweapons.boss;

import java.util.List;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.elementalweapons.spell.Fx;

/** 보스가 플레이어를 공격할 때 쓰는 도구 (마법 Fx와 반대로 '플레이어'를 노림) */
public final class BossFx {
	private BossFx() {}

	public static final DustParticleOptions WARN_RED = Fx.dust(0xFF3030, 1.6F);
	public static final DustParticleOptions WARN_YELLOW = Fx.dust(0xFFE040, 1.4F);

	/** 반경 안의 공격 가능한 플레이어 (크리에이티브/관전자 제외) */
	public static List<Player> players(ServerLevel level, Vec3 c, double r) {
		AABB box = new AABB(c.x - r, c.y - r, c.z - r, c.x + r, c.y + r, c.z + r);
		return level.getEntitiesOfClass(Player.class, box,
				p -> p.isAlive() && !p.isSpectator() && !p.isCreative() && p.distanceToSqr(c) <= r * r);
	}

	/** 반경 안의 플레이어에게 보스 공격 피해 */
	public static void hurtPlayers(ServerLevel level, ElementalBoss boss, Vec3 c, double r, float damage) {
		for (Player p : players(level, c, r)) {
			p.hurtServer(level, level.damageSources().mobAttack(boss), damage);
		}
	}

	/** 바닥에 경고 원 (곧 공격이 떨어질 자리) */
	public static void warnRing(ServerLevel level, Vec3 c, double r, DustParticleOptions color, double phase) {
		int pts = Math.max(12, (int) (r * 10));
		Fx.ring(level, color, c.add(0, 0.15, 0), r, pts, phase);
	}

	/** (x, z) 지점의 지면 높이 */
	public static Vec3 ground(ServerLevel level, double x, double z) {
		int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) Math.floor(x), (int) Math.floor(z));
		return new Vec3(x, y, z);
	}
}
