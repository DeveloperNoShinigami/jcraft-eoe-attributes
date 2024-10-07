package net.arna.jcraft.common.attack.moves.thefool;

import com.google.common.reflect.TypeToken;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JBlockRegistry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class SandstormAttack extends AbstractSimpleAttack<SandstormAttack, TheFoolEntity> {

    public static final MoveVariable<LivingEntity> SUPER_TARGET = new MoveVariable<>(LivingEntity.class);

    public static final MoveVariable<List<FallingBlockEntity>> SANDS = new MoveVariable<>(new TypeToken<>() {
    });

    public SandstormAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun, final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        hitSpark = JParticleType.HIT_SPARK_3;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final TheFoolEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);
        if (targets.isEmpty()) {
            return targets;
        }

        final LivingEntity superTarget = JUtils.getUserIfStand(targets.stream().findFirst().orElseThrow());
        ctx.set(SUPER_TARGET, superTarget);

        final List<FallingBlockEntity> sands = ctx.get(SANDS);

        for (int i = 0; i < 8; i++) {
            final FallingBlockEntity sand = FallingBlockEntity.fall(attacker.level(), superTarget.blockPosition(),
                    JBlockRegistry.FOOLISH_SAND_BLOCK.get().defaultBlockState());
            sand.time = -160;
            sand.noPhysics = true;
            sand.dropItem = false;
            sand.setBoundingBox(new AABB(0, 0, 0, 0, 0, 0));
            sand.setNoGravity(true);
            sands.add(sand);
        }

        return targets;
    }

    public void tickSandstorm(final TheFoolEntity attacker) {
        final MoveContext ctx = attacker.getMoveContext();

        final List<FallingBlockEntity> sands = ctx.get(SANDS);
        final LivingEntity superTarget = ctx.get(SUPER_TARGET);

        if (superTarget == null) {
            return;
        } else {
            if (sands.isEmpty()) {
                ctx.set(SUPER_TARGET, null);
                return;
            }
            if (!superTarget.isAlive()) {
                ctx.set(SUPER_TARGET, null);
                discardSands(attacker);
                return;
            }
        }

        superTarget.addEffect(
                new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 0, true, false)
        );
        superTarget.addEffect(
                new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, true, false)
        );

        final int age = attacker.tickCount;
        if (age % 20 == 0) {
            sands.get(0).discard();
            sands.remove(0);
        }

        final RandomSource random = attacker.getRandom();
        final Vec3 targetPos = superTarget.position().add(0, superTarget.getBbHeight() / 2, 0);

        int i = 0;
        int j = 1;
        for (FallingBlockEntity sand : sands) {
            if (sand == null || sand.isRemoved()) {
                continue;
            }
            i++;
            j *= -1;

            final Vec3 newVel = sand.getDeltaMovement().scale(0.25).add( // Suppress current velocity
                    // And add tracking
                    targetPos.subtract(
                            // MathHelper.sin(t) * 2, (isEven ? MathHelper.sin(t) : MathHelper.cos(t)) * 2, MathHelper.cos(t) * 2
                            sand.position().add(
                                    random.nextDouble() - 0.5 + Math.sin(age * i / 10.0 * j),
                                    random.nextDouble() * 2 - 1,
                                    random.nextDouble() - 0.5 + Math.cos(age * i / 10.0 * j))
                    ).normalize().scale(0.5)
            );

            sand.setDeltaMovement(newVel);
            sand.hurtMarked = true;
        }
    }

    public void discardSands(final TheFoolEntity attacker) {
        final List<FallingBlockEntity> sands = attacker.getMoveContext().get(SANDS);
        sands.forEach(Entity::discard);
        sands.clear();
    }

    @Override
    public void registerContextEntries(final MoveContext ctx) {
        ctx.register(SUPER_TARGET);
        ctx.register(SANDS, new ArrayList<>());
    }

    @Override
    protected @NonNull SandstormAttack getThis() {
        return this;
    }

    @Override
    public @NonNull SandstormAttack copy() {
        return copyExtras(new SandstormAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }
}
