package net.arna.jcraft.client.registry;

import net.arna.jcraft.client.events.JJoinServerEvents;
import net.arna.jcraft.client.events.JWorldRenderEvents;

public interface JClientEventsRegistry {
    static void registerClientEvents() {
        JJoinServerEvents.init();
    }
}
