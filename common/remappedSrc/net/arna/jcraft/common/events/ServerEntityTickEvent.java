package net.arna.jcraft.common.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.events.common.TickEvent;

public interface ServerEntityTickEvent<T> {

    Event<Entity> ENTITY_POST = EventFactory.createLoop();

    void tick(T instance);

    interface Entity extends TickEvent<net.minecraft.world.entity.Entity> {
    }
}
