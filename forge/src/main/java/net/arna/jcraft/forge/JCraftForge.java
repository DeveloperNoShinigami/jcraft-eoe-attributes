package net.arna.jcraft.forge;

import dev.architectury.platform.forge.EventBuses;
import net.arna.jcraft.registry.JCommandRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKeys;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.arna.jcraft.JCraft;
import net.minecraftforge.registries.DeferredRegister;

import static net.arna.jcraft.JCraft.MOD_ID;

@Mod(MOD_ID)
public final class JCraftForge {



    public JCraftForge() {
        var modBus = Mod.EventBusSubscriber.Bus.MOD.bus().get();
        var forgeBus = Mod.EventBusSubscriber.Bus.FORGE.bus().get();

        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(MOD_ID, modBus);


        forgeBus.addListener(JCraftForge::registerCommands);

        JCraft.init();


    }

    private static void registerCommands(RegisterCommandsEvent event) {
        JCommandRegistry.registerCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }
}
