package net.arna.jcraft.api.attack;

import com.mojang.serialization.Codec;
import net.arna.jcraft.common.attack.core.MoveCondition;

public interface MoveConditionType<C extends MoveCondition<C, ?>> {
    Codec<C> getCodec();
}
