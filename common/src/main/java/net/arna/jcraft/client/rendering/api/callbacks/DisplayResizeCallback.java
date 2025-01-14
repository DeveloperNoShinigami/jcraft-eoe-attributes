package net.arna.jcraft.client.rendering.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface DisplayResizeCallback {
    Event<DisplayResizeCallback> EVENT = EventFactory.of(callbacks ->
            (width, height) -> callbacks.forEach(c -> c.onResolutionChanged(width, height)));

    void onResolutionChanged(final int width, final int height);
}
