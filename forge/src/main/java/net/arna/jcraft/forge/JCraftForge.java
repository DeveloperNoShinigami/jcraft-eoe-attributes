package net.arna.jcraft.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.arna.jcraft.JCraft;

@Mod(JCraft.MOD_ID)
public final class JCraftForge {

    public JCraftForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(JCraft.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        JCraft.init();
    }
}
