package net.arna.jcraft.common.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import net.minecraft.world.entity.Entity;

public interface JEntityEvents<T> {
    /**
     * Invoked directly after an entity has been successfully added.
     */
    Event<PostAdd> POST_ADD = EventFactory.createEventResult();

    void add(T instance);

    interface PostAdd {
        EventResult add(Entity entity, boolean worldGenSpawned);
    }
}
