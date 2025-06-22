package net.arna.jcraft.common.marker;

import com.mojang.datafixers.util.Pair;
import lombok.NonNull;

import java.util.Optional;

public interface MarkerType<I, T, M extends Marker<I, M>> {

    @NonNull Optional<M> save(final @NonNull I id, final @NonNull T object);

    @NonNull Optional<Pair<I,T>> load(final @NonNull M marker);

}
