package net.arna.jcraft.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;

import net.arna.jcraft.JCraft;

import static net.arna.jcraft.JCraft.MOD_ID;

@Mod(MOD_ID)
public final class JCraftForge {


    public JCraftForge() {
        var modBus = Mod.EventBusSubscriber.Bus.MOD.bus().get();
        var forgeBus = Mod.EventBusSubscriber.Bus.FORGE.bus().get();

        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(MOD_ID, modBus);

        JCraft.init();

    }
}
