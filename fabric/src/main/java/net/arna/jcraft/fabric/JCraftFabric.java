package net.arna.jcraft.fabric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import lombok.Getter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.argumenttype.AttackArgumentType;
import net.arna.jcraft.common.argumenttype.SpecArgumentType;
import net.arna.jcraft.common.argumenttype.StandArgumentType;
import net.arna.jcraft.common.attack.core.data.MoveActionType;
import net.arna.jcraft.common.attack.core.data.MoveConditionType;
import net.arna.jcraft.common.attack.core.data.MoveSetLoader;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.events.JServerEvents;
import net.arna.jcraft.fabric.common.terrablender.JTerraFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.Consumer;

import static net.arna.jcraft.common.loot.JLootTableHelper.modifications;

public final class JCraftFabric implements ModInitializer {
    @Getter
    private static Codec<MoveType<?>> moveTypeCodec;
    @Getter
    private static Codec<MoveConditionType<?>> moveConditionTypeCodec;
    @Getter
    private static Codec<MoveActionType<?>> moveActionTypeCodec;

    @Override
    public void onInitialize() {
        JCraft.init();
        JTerraFabric.onModInitialized();

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            for (Consumer<LootTable.Builder> modification : modifications.get(id)) modification.accept(tableBuilder);
        });

        EntitySleepEvents.STOP_SLEEPING.register(JServerEvents::stopSleeping);
        EntitySleepEvents.ALLOW_BED.register(JServerEvents::allowBed);
        EntitySleepEvents.MODIFY_SLEEPING_DIRECTION.register(JServerEvents::modifySleepingDirection);
        EntitySleepEvents.ALLOW_SLEEP_TIME.register((player, sleepingPos, vanilla) -> JServerEvents.allowSleep(player, sleepingPos));

        ArgumentTypeRegistry.registerArgumentType(JCraft.id("stand"), StandArgumentType.class, SingletonArgumentInfo.contextFree(StandArgumentType::stand));
        ArgumentTypeRegistry.registerArgumentType(JCraft.id("spec"), SpecArgumentType.class, SingletonArgumentInfo.contextFree(SpecArgumentType::spec));
        ArgumentTypeRegistry.registerArgumentType(JCraft.id("attack"), AttackArgumentType.class, SingletonArgumentInfo.contextFree(AttackArgumentType::attack));

        registerMoveTypes();
        registerMoveConditions();
        registerMoveActions();

        JCraft.postInit();
    }

    private void registerMoveTypes() {
        MappedRegistry<MoveType<?>> registry = FabricRegistryBuilder.<MoveType<?>>createSimple(ResourceKey.createRegistryKey(JCraft.id("move_type")))
                .attribute(RegistryAttribute.SYNCED)
                .attribute(RegistryAttribute.MODDED)
                .buildAndRegister();
        moveTypeCodec = registry.byNameCodec();

        MoveSetLoader.registerMoves((id, supplier) -> registry.register(ResourceKey.create(registry.key(),
                JCraft.id(id)), supplier.get(), Lifecycle.stable()));
    }

    private void registerMoveConditions() {
        MappedRegistry<MoveConditionType<?>> registry = FabricRegistryBuilder.<MoveConditionType<?>>
                        createSimple(ResourceKey.createRegistryKey(JCraft.id("move_condition_type")))
                .attribute(RegistryAttribute.SYNCED)
                .attribute(RegistryAttribute.MODDED)
                .buildAndRegister();
        moveConditionTypeCodec = registry.byNameCodec();

        MoveSetLoader.registerConditions((id, supplier) -> registry.register(ResourceKey.create(registry.key(),
                JCraft.id(id)), supplier.get(), Lifecycle.stable()));
    }

    private void registerMoveActions() {
        MappedRegistry<MoveActionType<?>> registry = FabricRegistryBuilder.<MoveActionType<?>>
                        createSimple(ResourceKey.createRegistryKey(JCraft.id("move_action_type")))
                .attribute(RegistryAttribute.SYNCED)
                .attribute(RegistryAttribute.MODDED)
                .buildAndRegister();
        moveActionTypeCodec = registry.byNameCodec();

        MoveSetLoader.registerActions((id, supplier) -> registry.register(ResourceKey.create(registry.key(),
                JCraft.id(id)), supplier.get(), Lifecycle.stable()));
    }
}
