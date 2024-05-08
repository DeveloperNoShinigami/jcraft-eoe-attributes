package net.arna.jcraft.fabric.client;

import net.arna.jcraft.client.events.JClientEvents;
import net.arna.jcraft.client.renderer.effects.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

public final class JCraftFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            JClientEvents.afterTranslucent(context.matrixStack(), context.camera().getPos(), context.worldRenderer());
        });

        WorldRenderEvents.LAST.register(context -> {
            JClientEvents.onLast(context.matrixStack(), context.camera().getPos(), context.worldRenderer());
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            AttackHitboxEffectRenderer.render(context.matrixStack(), context.camera().getPos(), context.worldRenderer(), context.consumers());
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            ShockwaveEffectRenderer.render(context.matrixStack(), context.camera().getPos(), context.world(), context.consumers());
        });

        WorldRenderEvents.START.register(context -> {
            TimeAccelerationEffectRenderer.render(context.matrixStack(), context.camera().getPos(), context.world(), context.tickDelta());
        });

        WorldRenderEvents.START.register(context -> {
            SplatterEffectRenderer.render(context.matrixStack(), context.camera().getPos(), context.world(), context.tickDelta());
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            TimeErasePredictionEffectRenderer.render(context.matrixStack(), context.camera().getPos(), context.world(), context.tickDelta(), context.consumers());
        });
    }
}
