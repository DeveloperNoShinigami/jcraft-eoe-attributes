package net.arna.jcraft.fabric;

import dev.architectury.event.EventResult;
import net.arna.jcraft.registry.JCommandRegistry;
import net.arna.jcraft.registry.JEventsRegistry;
import net.fabricmc.api.ModInitializer;

import net.arna.jcraft.JCraft;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class JCraftFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(JCommandRegistry::registerCommands);

        JCraft.init();

        EntitySleepEvents.STOP_SLEEPING.register(JEventsRegistry::stopSleeping);
        EntitySleepEvents.ALLOW_BED.register(JEventsRegistry::allowBed);
        EntitySleepEvents.MODIFY_SLEEPING_DIRECTION.register(JEventsRegistry::modifySleepingDirection);
        EntitySleepEvents.ALLOW_SLEEP_TIME.register((player, sleepingPos, vanilla) -> JEventsRegistry.allowSleep(player, sleepingPos));
    }
}
