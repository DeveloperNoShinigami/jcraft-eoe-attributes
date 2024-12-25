package net.arna.jcraft.common.attack.core.data;

public interface StateContainerHolder<S extends Enum<S>> {
    void configureStateContainers(Class<S> stateClass);
}
