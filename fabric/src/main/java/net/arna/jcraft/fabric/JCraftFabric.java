package net.arna.jcraft.fabric;

import net.arna.jcraft.registry.JEventsRegistry;
import net.fabricmc.api.ModInitializer;

import net.arna.jcraft.JCraft;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootTable;

import java.util.function.Consumer;

public final class JCraftFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        JCraft.init();
        EntitySleepEvents.STOP_SLEEPING.register(JEventsRegistry::stopSleeping);
        EntitySleepEvents.ALLOW_BED.register(JEventsRegistry::allowBed);
        EntitySleepEvents.MODIFY_SLEEPING_DIRECTION.register(JEventsRegistry::modifySleepingDirection);
        EntitySleepEvents.ALLOW_SLEEP_TIME.register((player, sleepingPos, vanilla) -> JEventsRegistry.allowSleep(player, sleepingPos));
    }
}
