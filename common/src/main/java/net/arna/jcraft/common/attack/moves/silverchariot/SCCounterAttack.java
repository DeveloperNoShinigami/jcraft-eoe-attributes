package net.arna.jcraft.common.attack.moves.silverchariot;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractCounterAttack;
import net.arna.jcraft.common.attack.moves.shared.CounterMissMove;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class SCCounterAttack extends AbstractCounterAttack<SCCounterAttack, SilverChariotEntity> {

    private final CounterMissMove<SilverChariotEntity> counterMiss = new CounterMissMove<>(20);

    public SCCounterAttack(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NonNull MoveType<SCCounterAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void whiff(final @NonNull SilverChariotEntity attacker, final @NonNull LivingEntity user) {
        attacker.setMove(counterMiss, SilverChariotEntity.State.COUNTER_MISS);
        JCraft.stun(user, counterMiss.getDuration(), 0);
    }

    @Override
    public void counter(final @NonNull SilverChariotEntity attacker, final Entity countered, final DamageSource counteredDamageSource) {
        super.counter(attacker, countered, counteredDamageSource);

        if (!(countered instanceof LivingEntity ent)) {
            return;
        }
        JCraft.stun(ent, 30, 0);
        JUtils.cancelMoves(ent);
    }

    @Override
    protected @NonNull SCCounterAttack getThis() {
        return this;
    }

    @Override
    public @NonNull SCCounterAttack copy() {
        return copyExtras(new SCCounterAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractCounterAttack.Type<SCCounterAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<SCCounterAttack>, SCCounterAttack> buildCodec(RecordCodecBuilder.Instance<SCCounterAttack> instance) {
            return baseDefault(instance, SCCounterAttack::new);
        }
    }
}
