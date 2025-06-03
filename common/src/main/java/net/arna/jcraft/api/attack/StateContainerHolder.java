package net.arna.jcraft.api.attack;

public interface StateContainerHolder<S extends Enum<S>> {
    void configureStateContainers(Class<S> stateClass);
}
