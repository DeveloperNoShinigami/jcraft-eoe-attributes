package net.arna.jcraft.common.marker;

import lombok.NonNull;

@FunctionalInterface
public interface MarkerPredicate<I, T> {

    MarkerPredicate<?, ?> ALL = (id, object) -> true;

    MarkerPredicate<?, ?> NONE = (id, object) -> false;

    boolean shouldSave(final @NonNull I id, final @NonNull T object);

    @NonNull
    default MarkerPredicate<I,T> and(final @NonNull MarkerPredicate<I,T> other) {
        return (id, object) -> shouldSave(id, object) && other.shouldSave(id, object);
    }

    @NonNull
    default MarkerPredicate<I,T> or(final @NonNull MarkerPredicate<I,T> other) {
        return (id, object) -> shouldSave(id, object) || other.shouldSave(id, object);
    }

    @NonNull
    default MarkerPredicate<I,T> negate() {
        return (id, object) -> !shouldSave(id, object);
    }

    @SuppressWarnings("unchecked")
    static <J, U> MarkerPredicate<J, U> all() {
        return (MarkerPredicate<J, U>)ALL;
    }

    @SuppressWarnings("unchecked")
    static <J, U> MarkerPredicate<J, U> none() {
        return (MarkerPredicate<J, U>)NONE;
    }

}
