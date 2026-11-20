package com.elementalweapons.spell;

import java.util.Comparator;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** 파티클, 소리, 피해 등 마법 연출에 쓰는 공통 도구 모음 */
public final class Fx {
	private Fx() {}

	// ---------- 파티클 ----------

	/** 원하는 색의 점 파티클 (rgb 예: 0xFF8800) */
	public static DustParticleOptions dust(int rgb, float size) {
		return new DustParticleOptions(rgb, size);
	}

	public static void at(ServerLevel level, ParticleOptions p, Vec3 pos) {
		level.sendParticles(p, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
	}

	public static void burst(ServerLevel level, ParticleOptions p, Vec3 pos, int count, double spread, double speed) {
		level.sendParticles(p, pos.x, pos.y, pos.z, count, spread, spread, spread, speed);
	}

	/** a에서 b까지 파티클로 선 긋기 */
	public static void line(ServerLevel level, ParticleOptions p, Vec3 a, Vec3 b, double step) {
		Vec3 d = b.subtract(a);
		double len = d.length();
		int n = Math.max(1, (int) (len / step));
		for (int i = 0; i <= n; i++) {
			at(level, p, a.add(d.scale((double) i / n)));
		}
	}

	/** 수평 원 */
	public static void ring(ServerLevel level, ParticleOptions p, Vec3 center, double radius, int points, double phase) {
		for (int i = 0; i < points; i++) {
			double ang = phase + 2 * Math.PI * i / points;
			at(level, p, center.add(Math.cos(ang) * radius, 0, Math.sin(ang) * radius));
		}
	}

	/** 구 표면에 무작위 점 */
	public static void sphere(ServerLevel level, ParticleOptions p, Vec3 center, double radius, int points) {
		RandomSource r = level.getRandom();
		for (int i = 0; i < points; i++) {
			Vec3 dir = new Vec3(r.nextGaussian(), r.nextGaussian(), r.nextGaussian());
			if (dir.lengthSqr() < 1.0E-4) continue;
			at(level, p, center.add(dir.normalize().scale(radius)));
		}
	}

	// ---------- 소리 ----------

	public static void sound(ServerLevel level, Vec3 pos, SoundEvent sound, float volume, float pitch) {
		level.playSound((Player) null, pos.x, pos.y, pos.z, sound, SoundSource.PLAYERS, volume, pitch);
	}

	public static void sound(ServerLevel level, Vec3 pos, Holder<SoundEvent> sound, float volume, float pitch) {
		level.playSound((Player) null, pos.x, pos.y, pos.z, sound, SoundSource.PLAYERS, volume, pitch);
	}

	// ---------- 전투 ----------

	/** center 반경 radius 안의 적대적 몹을 가까운 순으로 */
	public static List<LivingEntity> enemies(ServerLevel level, Vec3 center, double radius) {
		AABB box = new AABB(center.x - radius, center.y - radius, center.z - radius,
				center.x + radius, center.y + radius, center.z + radius);
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, box,
				e -> e instanceof Enemy && e.isAlive() && e.distanceToSqr(center) <= radius * radius);
		list.sort(Comparator.comparingDouble(e -> e.distanceToSqr(center)));
		return list;
	}

	/** 마법 피해 (시전자가 준 피해로 처리) */
	public static void hurt(ServerLevel level, Player caster, LivingEntity target, float amount) {
		float m = caster == null ? 1F : (float) com.elementalweapons.growth.ModGrowth.paragon(caster).spellMult();
		target.hurtServer(level, level.damageSources().indirectMagic(caster, caster), amount * m);
	}

	/** 엔티티를 날려 보내기 */
	public static void launch(Entity e, double x, double y, double z) {
		// 기존 속도를 지우고 push()로 새 속도를 줌 (push가 클라이언트 동기화 표시까지 처리)
		e.setDeltaMovement(Vec3.ZERO);
		e.push(x, y, z);
	}

	/** 플레이어가 바라보는 지점 (막힌 곳이 없으면 range 앞 허공) */
	public static Vec3 lookPoint(Player player, double range) {
		HitResult hit = player.pick(range, 0.0F, false);
		if (hit.getType() == HitResult.Type.MISS) {
			return player.getEyePosition().add(player.getLookAngle().scale(range));
		}
		return hit.getLocation();
	}

	/** 시선의 수평 방향 (단위 벡터) */
	public static Vec3 flatLook(Player player) {
		Vec3 look = player.getLookAngle();
		Vec3 flat = new Vec3(look.x, 0, look.z);
		return flat.lengthSqr() < 1.0E-4 ? new Vec3(0, 0, 1) : flat.normalize();
	}
}
