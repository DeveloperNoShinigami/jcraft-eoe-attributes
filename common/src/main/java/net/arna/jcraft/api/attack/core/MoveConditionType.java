package net.arna.jcraft.api.attack.core;

import com.mojang.serialization.Codec;

public interface MoveConditionType<C extends MoveCondition<C, ?>> {
    Codec<C> getCodec();
}
