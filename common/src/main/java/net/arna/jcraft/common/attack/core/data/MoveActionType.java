package net.arna.jcraft.common.attack.core.data;

import com.mojang.serialization.Codec;
import net.arna.jcraft.common.attack.core.MoveAction;

public interface MoveActionType<T extends MoveAction<? extends T, ?>> {
    Codec<T> getCodec();
}
