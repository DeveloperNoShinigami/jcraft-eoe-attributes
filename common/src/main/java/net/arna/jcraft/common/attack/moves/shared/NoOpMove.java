package net.arna.jcraft.common.attack.moves.shared;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

/**
 * A move that doesn't do anything.
 * Meant to be used either as a temporary placeholder when developing stands or specs or
 * for moves that don't really do anything apart from what's done in the initMove method
 * in the stand/spec.
 *
 * @param <A>
 */
public final class NoOpMove<A extends IAttacker<? extends A, ?>> extends AbstractMove<NoOpMove<A>, A> {
    public NoOpMove() {
        this(0, 0, 0f);
    }

    public NoOpMove(final int cooldown, final int duration, final float moveDistance) {
        super(cooldown, duration + 1, duration, moveDistance);
    }

    @Override
    public @NonNull MoveType<NoOpMove<A>> getMoveType() {
        return Type.INSTANCE.cast();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final A attacker, final LivingEntity user, final MoveContext ctx) {
        return Set.of();
    }

    @Override
    protected @NonNull NoOpMove<A> getThis() {
        return this;
    }

    @Override
    public @NonNull NoOpMove<A> copy() {
        return copyExtras(new NoOpMove<>(getCooldown(), getDuration(), getMoveDistance()));
    }

    public static class Type extends AbstractMove.Type<NoOpMove<?>> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<NoOpMove<?>>, NoOpMove<?>> buildCodec(RecordCodecBuilder.Instance<NoOpMove<?>> instance) {
            return instance.group(extras(), cooldown(), duration(), moveDistance())
                    .apply(instance, applyExtras((cooldown, duration, moveDistance) ->
                            new NoOpMove<>(cooldown, duration, moveDistance)));
        }
    }
}
