package net.arna.jcraft.forge.events;

import net.arna.jcraft.forge.capability.impl.entity.GrabCapability;
import net.arna.jcraft.forge.capability.impl.entity.TimeStopCapability;
import net.arna.jcraft.forge.capability.impl.living.*;
import net.arna.jcraft.forge.capability.impl.player.PhCapability;
import net.arna.jcraft.forge.capability.impl.player.SpecCapability;
import net.arna.jcraft.forge.capability.impl.world.ShockwaveHandlerCapability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
    }
}