package net.arna.jcraft.common.attack.moves.shared;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public final class StandbyActivationMove<A extends IAttacker<? extends A, ?>> extends AbstractMove<StandbyActivationMove<A>, A> {

    protected StandbyActivationMove(int cooldown, int windup, int duration) {
        super(cooldown, windup, duration, 0f);
    }

    @Override
    public @NonNull MoveType<StandbyActivationMove<A>> getMoveType() {
        return Type.INSTANCE.cast();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(A attacker, LivingEntity user, MoveContext ctx) {
        attacker.setStandby(true);
        return Set.of();
    }

    @Override
    protected @NonNull StandbyActivationMove<A> getThis() {
        return this;
    }

    @Override
    public @NonNull StandbyActivationMove<A> copy() {
        return copyExtras(new StandbyActivationMove<>(getCooldown(), getWindup(), getDuration()));
    }

    public static class Type extends AbstractMove.Type<StandbyActivationMove<?>> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<StandbyActivationMove<?>>, StandbyActivationMove<?>> buildCodec(RecordCodecBuilder.Instance<StandbyActivationMove<?>> instance) {
            return instance.group(cooldown(), windup(), duration()).apply(instance, StandbyActivationMove::new);
        }
    }
}
