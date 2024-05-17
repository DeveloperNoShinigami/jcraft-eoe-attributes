package net.arna.jcraft.forge.client;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.JCraftClient;
import net.arna.jcraft.client.gui.hud.EpitaphOverlay;
import net.arna.jcraft.client.registry.JEntityRendererRegister;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static jdk.javadoc.doclet.DocletEnvironment.ModuleMode.API;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = JCraft.MOD_ID)
public class JCraftForgeClient {
    @SubscribeEvent
    public static void handleClientSetup(final FMLClientSetupEvent ignoredEvent) {
        JCraftClient.init();
        //JModelPredicateProviderRegistry.register();
        JEntityRendererRegister.registerEntityRenderers();
        //TODO BlockEntityRendererFactories.register(JBlockEntityTypeRegistry.COFFIN_TILE.get(), CoffinTileRenderer::new);
        // Run when the MinecraftClient instance is fully initialized.
        Minecraft.getInstance().tell(EpitaphOverlay::preload);
    }
}
