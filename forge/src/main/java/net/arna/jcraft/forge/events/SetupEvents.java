package net.arna.jcraft.forge.events;

import dev.architectury.platform.Platform;
import net.arna.jcraft.client.registry.JItemPropertiesRegistry;
import net.arna.jcraft.forge.capability.impl.entity.GrabCapability;
import net.arna.jcraft.forge.capability.impl.entity.TimeStopCapability;
import net.arna.jcraft.forge.capability.impl.living.*;
import net.arna.jcraft.forge.capability.impl.player.PhCapability;
import net.arna.jcraft.forge.capability.impl.player.SpecCapability;
import net.arna.jcraft.forge.capability.impl.world.ExclusiveStandsCapability;
import net.arna.jcraft.forge.capability.impl.world.ShockwaveHandlerCapability;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SetupEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        //Player
        event.register(PhCapability.class);
        event.register(SpecCapability.class);

        //Entity
        event.register(GrabCapability.class);
        event.register(TimeStopCapability.class);

        //Living
        event.register(BombTrackerCapability.class);
        event.register(CooldownsCapability.class);
        event.register(HitPropertyCapability.class);
        event.register(MiscCapability.class);
        event.register(StandCapability.class);
        event.register(VampireCapability.class);

        //World
        event.register(ShockwaveHandlerCapability.class);
        event.register(ExclusiveStandsCapability.class);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void register(RegisterEvent event) {
        if (event.getRegistryKey() != Registries.ITEM) return;

        // Items are registered at this point (cuz priority is low, so this one runs later)
        // Register properties for items if on client
        if (Platform.getEnv() == Dist.CLIENT) {
            JItemPropertiesRegistry.registerItemProperties();
        }
    }
}
