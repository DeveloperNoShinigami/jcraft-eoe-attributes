package net.arna.jcraft.common.attack.moves.dirtydeedsdonedirtcheap;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.data.MoveType;
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
import org.jetbrains.annotations.NotNull;

public class D4CCounterAttack extends AbstractCounterAttack<D4CCounterAttack, D4CEntity> {
    private final CounterMissMove<D4CEntity> counterMiss = new CounterMissMove<>(10);

    public D4CCounterAttack(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NotNull MoveType<D4CCounterAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void whiff(final @NonNull D4CEntity attacker, final @NonNull LivingEntity user) {
        attacker.setMove(counterMiss, D4CEntity.State.COUNTER_MISS);
        JCraft.stun(user, counterMiss.getDuration(), 0);
    }

    @Override
    public void counter(final @NonNull D4CEntity attacker, final Entity countered, final DamageSource counteredDamageSource) {
        super.counter(attacker, countered, counteredDamageSource);
        final boolean bl = counteredDamageSource.is(DamageTypes.MOB_PROJECTILE);
        final boolean bl2 = counteredDamageSource.is(DamageTypes.MAGIC);

        if (countered == null || !attacker.hasUser() || bl || bl2) {
            return;
        }

        final LivingEntity user = attacker.getUserOrThrow();
        final Vec3 trueKnockback = countered.position().subtract(user.position()).normalize().scale(1.5);
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

    public static class Type extends AbstractCounterAttack.Type<D4CCounterAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<D4CCounterAttack>, D4CCounterAttack> buildCodec(RecordCodecBuilder.Instance<D4CCounterAttack> instance) {
            return baseDefault(instance, D4CCounterAttack::new);
        }
    }
}
