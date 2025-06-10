package net.arna.jcraft.api.attack;

/**
 * Any class that holds state containers should implement this interface.
 * The {@link #configureStateContainers(Class)} method is automatically called when the move implementing this is loaded.
 * @param <S>
 */
public interface StateContainerHolder<S extends Enum<S>> {
    void configureStateContainers(final Class<S> stateClass);
}
