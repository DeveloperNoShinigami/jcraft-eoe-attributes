package net.arna.jcraft.common.attack.moves.shared;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.IAttacker;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractHoldableMove;

public final class SimpleHoldableMove<A extends IAttacker<A, ?>> extends AbstractHoldableMove<SimpleHoldableMove<A>, A> {
    public SimpleHoldableMove(final int cooldown, final int windup, final int duration, final float moveDistance, final int minimumCharge) {
        super(cooldown, windup, duration, moveDistance, minimumCharge);
    }

    @Override
    public @NonNull MoveType<SimpleHoldableMove<A>> getMoveType() {
        return Type.INSTANCE.cast();
    }

    @Override
    protected @NonNull SimpleHoldableMove<A> getThis() {
        return this;
    }

    @Override
    public @NonNull SimpleHoldableMove<A> copy() {
        SimpleHoldableMove<A> copy = new SimpleHoldableMove<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getMinimumCharge());
        if (setMoveStun) {
            copy.shouldSetMoveStun();
        }
        return copyExtras(copy);
    }

    public static class Type extends AbstractHoldableMove.Type<SimpleHoldableMove<?>> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<SimpleHoldableMove<?>>, SimpleHoldableMove<?>> buildCodec(RecordCodecBuilder.Instance<SimpleHoldableMove<?>> instance) {
            return holdableDefault(instance, SimpleHoldableMove::new);
        }
    }
}
