package net.arna.jcraft.forge.client;

import me.shedaniel.autoconfig.AutoConfig;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.JClientConfig;
import net.arna.jcraft.client.JCraftClient;
import net.arna.jcraft.client.gui.hud.EpitaphOverlay;
import net.arna.jcraft.client.particle.*;
import net.arna.jcraft.client.registry.JEntityRendererRegister;
import net.arna.jcraft.client.registry.JModelPredicateProviderRegistry;
import net.arna.jcraft.client.renderer.block.CoffinTileRenderer;
import net.arna.jcraft.registry.JBlockEntityTypeRegistry;
import net.arna.jcraft.registry.JParticleTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = JCraft.MOD_ID)
public class JCraftForgeClient {
    @SubscribeEvent
    public static void handleClientSetup(final FMLClientSetupEvent ignoredEvent) {
        JCraftClient.init();
        //JModelPredicateProviderRegistry.register();

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(
                (minecraft, screen) -> AutoConfig.getConfigScreen(JClientConfig.class, screen).get()));

        ignoredEvent.enqueueWork(() -> {
            JModelPredicateProviderRegistry.register();

        });

        BlockEntityRenderers.register(JBlockEntityTypeRegistry.COFFIN_TILE.get(), CoffinTileRenderer::new);
        JEntityRendererRegister.registerEntityRenderers();

        // Run when the MinecraftClient instance is fully initialized.
        Minecraft.getInstance().tell(EpitaphOverlay::preload);
    }

    @SubscribeEvent
    public static void onParticleFactoryRegistration(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(JParticleTypeRegistry.COMBO_BREAK.get(), ComboBreakerParticle.Factory::new);
        event.registerSpriteSet(JParticleTypeRegistry.COOLDOWN_CANCEL.get(), CooldownCancelParticle.Factory::new);
        event.registerSpriteSet(JParticleTypeRegistry.HITSPARK_1.get(), provider -> new HitsparkParticle.Factory(provider, 0.4f, 5));
        event.registerSpriteSet(JParticleTypeRegistry.HITSPARK_2.get(), provider -> new HitsparkParticle.Factory(provider, 0.66f, 6));
        event.registerSpriteSet(JParticleTypeRegistry.HITSPARK_3.get(), provider -> new HitsparkParticle.Factory(provider, 1f, 8));
        event.registerSpriteSet(JParticleTypeRegistry.STUN_SLASH.get(), provider -> new HitsparkParticle.Factory(provider, 0.6f, 6));
        event.registerSpriteSet(JParticleTypeRegistry.STUN_PIERCE.get(), provider -> new HitsparkParticle.Factory(provider, 0.6f, 6));
        event.registerSpriteSet(JParticleTypeRegistry.KCPARTICLE.get(), KCParticle.Factory::new);
        event.registerSpriteSet(JParticleTypeRegistry.BACKSTAB.get(), BackstabParticle.Factory::new);
        event.registerSpriteSet(JParticleTypeRegistry.SPEED_PARTICLE.get(), SpeedParticle.Factory::new);
        event.registerSpriteSet(JParticleTypeRegistry.BITES_THE_DUST.get(), BitesTheDustParticle.Factory::new);
        event.registerSpriteSet(JParticleTypeRegistry.BOOM_1.get(), BoomParticle.Factory::new);
        event.registerSpriteSet(JParticleTypeRegistry.PIXEL.get(), PixelParticle.Factory::new);
        event.registerSpriteSet(JParticleTypeRegistry.BLOCKSPARK.get(), provider -> new BlocksparkParticle.Factory(provider, 0.15f));
        event.registerSpriteSet(JParticleTypeRegistry.GO.get(), GoParticle.Factory::new);
        event.registerSpriteSet(JParticleTypeRegistry.AURA_ARC.get(), AuraArcParticle.Factory::new);
        event.registerSpriteSet(JParticleTypeRegistry.AURA_BLOB.get(), AuraBlobParticle.Factory::new);
        event.registerSpriteSet(JParticleTypeRegistry.INVERSION.get(), InversionParticle.Factory::new);
        event.registerSpriteSet(JParticleTypeRegistry.SUN_LOCK_ON.get(), BackstabParticle.Factory::new); // 9 frames, reusing
        event.registerSpriteSet(JParticleTypeRegistry.PURPLE_HAZE_CLOUD.get(), PurpleHazeCloudParticle.Factory::new);
        event.registerSpriteSet(JParticleTypeRegistry.PURPLE_HAZE_PARTICLE.get(), PurpleHazeErraticParticle.Factory::new);
    }
}
