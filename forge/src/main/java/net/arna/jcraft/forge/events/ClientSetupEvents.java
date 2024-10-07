package net.arna.jcraft.forge.events;


import net.arna.jcraft.client.JCraftClient;
import net.arna.jcraft.client.gui.hud.EpitaphOverlay;
import net.arna.jcraft.client.gui.hud.JCraftHudOverlay;
import net.arna.jcraft.client.registry.JEntityRendererRegister;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents {

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void onInitializeClient(final EntityRenderersEvent.RegisterRenderers event) {
        JEntityRendererRegister.registerEntityRenderers(rendererData ->
                event.registerEntityRenderer(
                        rendererData.supplier().get(),
                        (EntityRendererProvider<Entity>) rendererData.provider()
                )
        );
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelow(VanillaGuiOverlay.CHAT_PANEL.id(), "hud", (gui, guiGraphics, partialTick, screenWidth, screenHeight) ->
                JCraftHudOverlay.render(guiGraphics));
        event.registerBelow(VanillaGuiOverlay.PLAYER_HEALTH.id(), "epitaph", (gui, guiGraphics, partialTick, screenWidth, screenHeight) ->
                EpitaphOverlay.render());
    }

    @SubscribeEvent(priority = EventPriority.HIGH) // run before Arch's event
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        JCraftClient.registerKeyBindings(event::register);
    }
}
