package net.arna.jcraft.api.attack;

import com.mojang.serialization.Codec;
import net.arna.jcraft.common.attack.core.data.MoveSetImpl;

import java.util.Optional;

/**
 * Required to allow for (de)serialization of state values.
 * If any move needs a state, it is required to be of this type and
 * said move must implement {@link StateContainerHolder}.
 * The {@link StateContainerHolder#configureStateContainers(Class)} method is then called
 * by {@link MoveSetImpl} upon deserialization.
 * @param <S>
 */
public class StateContainer<S extends Enum<S>> {
    // Upon deserialization with this codec, the deserialized container will have to be configured.
    public static final Codec<StateContainer<?>> CODEC = Codec.STRING.xmap(StateContainer::new, stateContainer -> stateContainer.valueName);
    private final String valueName;
    private Optional<S> value;

    private StateContainer(final String valueName) {
        this(valueName, Optional.empty());
    }

    private StateContainer(final String valueName, final Optional<S> value) {
        this.valueName = valueName;
        this.value = value;
    }

    public static <S extends Enum<S>> StateContainer<S> of(final S value) {
        return new StateContainer<>(value.name(), Optional.of(value));
    }

    public S getValue() {
        return value.orElseThrow(() -> new IllegalStateException("StateContainer has not yet been configured."));
    }

    public void configure(Class<S> stateClass) {
        value = Optional.of(Enum.valueOf(stateClass, valueName));
    }

    public StateContainer<S> copy() {
        return new StateContainer<>(valueName, value);
    }
}
