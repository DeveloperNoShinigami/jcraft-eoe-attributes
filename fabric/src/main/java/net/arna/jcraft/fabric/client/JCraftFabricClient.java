package net.arna.jcraft.fabric.client;

import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import net.arna.jcraft.client.JCraftClient;
import net.arna.jcraft.client.events.JClientEvents;
import net.arna.jcraft.client.gui.hud.EpitaphOverlay;
import net.arna.jcraft.client.particle.*;
import net.arna.jcraft.client.registry.JEntityRendererRegister;
import net.arna.jcraft.client.registry.JItemPropertiesRegistry;
import net.arna.jcraft.client.registry.JModelPredicateProviderRegistry;
import net.arna.jcraft.client.renderer.block.CoffinTileRenderer;
import net.arna.jcraft.client.renderer.effects.*;
import net.arna.jcraft.registry.JBlockEntityTypeRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.arna.jcraft.registry.JParticleTypeRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public final class JCraftFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        JCraftClient.init();
        JModelPredicateProviderRegistry.register();
        JEntityRendererRegister.registerEntityRenderers();
        BlockEntityRenderers.register(JBlockEntityTypeRegistry.COFFIN_TILE.get(), CoffinTileRenderer::new);

        // Particle registration
        ParticleProviderRegistry.register(JParticleTypeRegistry.COMBO_BREAK, ComboBreakerParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.COOLDOWN_CANCEL, CooldownCancelParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.HITSPARK_1, provider -> new HitsparkParticle.Factory(provider, 0.4f, 5));
        ParticleProviderRegistry.register(JParticleTypeRegistry.HITSPARK_2, provider -> new HitsparkParticle.Factory(provider, 0.66f, 6));
        ParticleProviderRegistry.register(JParticleTypeRegistry.HITSPARK_3, provider -> new HitsparkParticle.Factory(provider, 1f, 8));
        ParticleProviderRegistry.register(JParticleTypeRegistry.KCPARTICLE, KCParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.BACKSTAB, BackstabParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.SPEED_PARTICLE, SpeedParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.BITES_THE_DUST, BitesTheDustParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.BOOM_1, BoomParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.PIXEL, PixelParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.BLOCKSPARK, provider -> new BlocksparkParticle.Factory(provider, 0.15f));
        ParticleProviderRegistry.register(JParticleTypeRegistry.GO, GoParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.AURA_ARC, AuraArcParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.AURA_BLOB, AuraBlobParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.INVERSION, InversionParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.SUN_LOCK_ON, BackstabParticle.Factory::new); // 9 frames, reusing
        ParticleProviderRegistry.register(JParticleTypeRegistry.PURPLE_HAZE_CLOUD, PurpleHazeCloudParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.PURPLE_HAZE_PARTICLE, PurpleHazeErraticParticle.Factory::new);

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            JClientEvents.afterTranslucent(context.matrixStack(), context.camera().getPosition(), context.worldRenderer());
        });

        WorldRenderEvents.LAST.register(context -> {
            JClientEvents.onLast(context.matrixStack(), context.camera().getPosition());
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            AttackHitboxEffectRenderer.render(context.matrixStack(), context.camera().getPosition(), context.worldRenderer(), context.consumers());
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            ShockwaveEffectRenderer.render(context.matrixStack(), context.camera().getPosition(), context.world(), context.consumers());
        });

        WorldRenderEvents.START.register(context -> {
            TimeAccelerationEffectRenderer.render(context.world());
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            SplatterEffectRenderer.render(context.matrixStack(), context.camera().getPosition(), context.world(), context.tickDelta());
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            TimeErasePredictionEffectRenderer.render(context.matrixStack(), context.camera().getPosition(), context.world(), context.tickDelta(), context.consumers());
        });

        ResourceLocation itemId = JItemRegistry.ITEMS.get(JItemRegistry.DEBUG_WAND);
        BigItemRenderer itemRenderer = new BigItemRenderer(itemId);


        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(itemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(JItemRegistry.DEBUG_WAND.get(), itemRenderer);

        ModelLoadingPlugin.register(ctx -> ctx.addModels(
                new ModelResourceLocation(new ResourceLocation(itemId + "_gui"), "inventory"),
                new ModelResourceLocation(new ResourceLocation(itemId + "_handheld"), "inventory")));

        JItemPropertiesRegistry.registerItemProperties();
        JCraftClient.registerKeyBindings(null);

        // Run when the MinecraftClient instance is fully initialized.
        Minecraft.getInstance().tell(EpitaphOverlay::preload);
    }
}
