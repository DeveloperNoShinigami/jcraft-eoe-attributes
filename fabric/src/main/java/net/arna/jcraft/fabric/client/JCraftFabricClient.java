package net.arna.jcraft.fabric.client;

import net.arna.jcraft.client.JCraftClient;
import net.arna.jcraft.client.events.JClientEvents;
import net.arna.jcraft.client.renderer.effects.*;
import net.arna.jcraft.registry.JItemRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public final class JCraftFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        JCraftClient.init();
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

        Identifier itemId = JItemRegistry.ITEMS.get(JItemRegistry.DEBUG_WAND);
        BigItemRenderer itemRenderer = new BigItemRenderer(itemId);


        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(itemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(JItemRegistry.DEBUG_WAND.get(), itemRenderer);

        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
            out.accept(new ModelIdentifier(new Identifier(itemId + "_gui"), "inventory"));
            out.accept(new ModelIdentifier(new Identifier(itemId + "_handheld"), "inventory"));
        });
    }
}
