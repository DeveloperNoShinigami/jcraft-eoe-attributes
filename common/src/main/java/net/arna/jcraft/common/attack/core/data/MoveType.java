package net.arna.jcraft.common.attack.core.data;

import com.mojang.serialization.Codec;
import lombok.NonNull;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;

public interface MoveType<M extends AbstractMove<? extends M, ?>> {
    @NonNull
    Codec<M> getCodec();

    /**
     * Cast this move type to a specific type.
     * Should only be used to suppress warnings for irrelevant type parameters.
     * (Such as the attacker type)
     * @return The cast move type.
     * @param <M1> The type to cast to.
     */
    @SuppressWarnings("unchecked")
    default <M1 extends AbstractMove<? extends M1, ?>> MoveType<M1> cast() {
        return (MoveType<M1>) this;
    }
}
