package net.arna.jcraft.common.attack.moves.shared;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.moves.base.AbstractTimeStopMove;
import net.arna.jcraft.common.entity.stand.StandEntity;

import java.util.function.IntSupplier;

@Getter
public final class TimeStopMove<A extends StandEntity<? extends A, ?>> extends AbstractTimeStopMove<TimeStopMove<A>, A> {
    public TimeStopMove(final int cooldown, final int windup, final int duration, final IntSupplier timeStopDuration) {
        super(cooldown, windup, duration, 1f, timeStopDuration);
    }

    @Override
    protected @NonNull TimeStopMove<A> getThis() {
        return this;
    }

    @Override
    public @NonNull TimeStopMove<A> copy() {
        return copyExtras(new TimeStopMove<>(getCooldown(), getWindup(), getDuration(), getTimeStopDuration()));
    }
}
