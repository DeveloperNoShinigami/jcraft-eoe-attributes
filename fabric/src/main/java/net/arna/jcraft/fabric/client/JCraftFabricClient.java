package net.arna.jcraft.fabric.client;

import net.arna.jcraft.client.events.JWorldRenderEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public final class JCraftFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            JWorldRenderEvents.afterTranslucent(context.matrixStack(), context.camera().getPos(), context.worldRenderer());
        });

        WorldRenderEvents.LAST.register(context -> {
            JWorldRenderEvents.onLast(context.matrixStack(), context.camera().getPos(), context.worldRenderer());
        });
    }
}
