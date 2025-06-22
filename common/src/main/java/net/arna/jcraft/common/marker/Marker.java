package net.arna.jcraft.common.marker;

import com.mojang.serialization.Codec;
import lombok.NonNull;

public interface Marker<I, T extends Marker<I, ?>> {

    @NonNull I getId();

    @NonNull Codec<T> getCodec();

}
