package net.arna.jcraft.common.attack.moves.shared;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.api.attack.IAttacker;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractBarrageAttack;
import org.jetbrains.annotations.NotNull;

/**
 * A simple attack that performs at a set interval.
 */
public final class BarrageAttack<A extends IAttacker<? extends A, ?>> extends AbstractBarrageAttack<BarrageAttack<A>, A> {

    public BarrageAttack(final int cooldown, final int windup, final int duration, final float moveDistance,
                         final float damage, final int stun, final float hitboxSize, final float knockback,
                         final float offset, final int interval) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, interval);
    }

    @Override
    public @NotNull MoveType<BarrageAttack<A>> getMoveType() {
        return Type.INSTANCE.cast();
    }

    @Override
    protected @NonNull BarrageAttack<A> getThis() {
        return this;
    }

    @Override
    public @NonNull BarrageAttack<A> copy() {
        return copyExtras(new BarrageAttack<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getInterval()));
    }

    public static class Type extends AbstractBarrageAttack.Type<BarrageAttack<?>> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<BarrageAttack<?>>, BarrageAttack<?>> buildCodec(final RecordCodecBuilder.Instance<BarrageAttack<?>> instance) {
            return barrageDefault(instance, BarrageAttack::new);
        }
    }
}
