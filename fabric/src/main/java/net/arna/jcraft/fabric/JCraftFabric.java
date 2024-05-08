package net.arna.jcraft.fabric;

import net.arna.jcraft.common.events.JServerEvents;
import net.arna.jcraft.registry.JEventsRegistry;
import net.fabricmc.api.ModInitializer;

import net.arna.jcraft.JCraft;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.loot.LootTable;

import java.util.function.Consumer;

import static net.arna.jcraft.common.loot.JLootTableHelper.modifications;

public final class JCraftFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        JCraft.init();

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            for (Consumer<LootTable.Builder> modification : modifications.get(id)) modification.accept(tableBuilder);
        });

        EntitySleepEvents.STOP_SLEEPING.register(JServerEvents::stopSleeping);
        EntitySleepEvents.ALLOW_BED.register(JServerEvents::allowBed);
        EntitySleepEvents.MODIFY_SLEEPING_DIRECTION.register(JServerEvents::modifySleepingDirection);
        EntitySleepEvents.ALLOW_SLEEP_TIME.register((player, sleepingPos, vanilla) -> JServerEvents.allowSleep(player, sleepingPos));
    }
}
