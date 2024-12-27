package net.arna.jcraft.client.rendering.api.callbacks;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.Camera;

public interface PostWorldRenderCallback {
    // Much more efficient than using EventFactory#createLoop which uses reflection and proxies.
    Event<PostWorldRenderCallback> EVENT = EventFactory.of(callbacks ->
            (matrices, camera, tickDelta, nanoTime) ->
                    callbacks.forEach(c -> c.onWorldRendered(matrices, camera, tickDelta, nanoTime)));

    void onWorldRendered(PoseStack matrices, Camera camera, float tickDelta, long nanoTime);
}
