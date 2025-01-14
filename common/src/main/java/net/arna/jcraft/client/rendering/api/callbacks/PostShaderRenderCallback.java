package net.arna.jcraft.client.rendering.api.callbacks;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface PostShaderRenderCallback {
    // Much more efficient than using EventFactory#createLoop which uses reflection and proxies.
    Event<PostShaderRenderCallback> EVENT = EventFactory.of(callbacks ->
            tickDelta -> callbacks.forEach(c -> c.renderEffect(tickDelta)));

    void renderEffect(float tickDelta);
}
