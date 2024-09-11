package net.arna.jcraft.common.attack.moves.dirtydeedsdonedirtcheap;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.moves.base.AbstractCounterAttack;
import net.arna.jcraft.common.attack.moves.shared.CounterMissMove;
import net.arna.jcraft.common.entity.stand.D4CEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class D4CCounterAttack extends AbstractCounterAttack<D4CCounterAttack, D4CEntity> {
    private final CounterMissMove<D4CEntity> counterMiss = new CounterMissMove<>(10);

    public D4CCounterAttack(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public void whiff(@NonNull D4CEntity attacker, @NonNull LivingEntity user) {
        attacker.setMove(counterMiss, D4CEntity.State.COUNTER_MISS);
        JCraft.stun(user, counterMiss.getDuration(), 0);
    }

    @Override
    public void counter(@NonNull D4CEntity attacker, Entity countered, DamageSource counteredDamageSource) {
        super.counter(attacker, countered, counteredDamageSource);
        var bl = counteredDamageSource.is(DamageTypes.MOB_PROJECTILE);
        var bl2 = counteredDamageSource.is(DamageTypes.MAGIC);

        if (countered == null || !attacker.hasUser() || bl || bl2) {
            return;
        }

        LivingEntity user = attacker.getUserOrThrow();
        Vec3 trueKnockback = countered.position().subtract(user.position()).normalize().scale(1.5);
        countered.push(trueKnockback.x, 0.5, trueKnockback.z);
        countered.hurtMarked = true;

        if (countered instanceof LivingEntity livingEntity) {
            livingEntity.hurt(livingEntity.level().damageSources().mobAttack(user), 10);
            JCraft.stun(livingEntity, 20, 3);
            JUtils.cancelMoves(livingEntity);
        }

        attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1f, 1f);
        attacker.playSound(JSoundRegistry.D4C_COUNTER.get(), 1, 1);
    }

    @Override
    protected @NonNull D4CCounterAttack getThis() {
        return this;
    }

    @Override
    public @NonNull D4CCounterAttack copy() {
        return copyExtras(new D4CCounterAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
