package net.arna.jcraft.fabric;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.architectury.event.EventResult;
import net.arna.jcraft.registry.JCommandRegistry;
import net.arna.jcraft.registry.JEventsRegistry;
import net.fabricmc.api.ModInitializer;

import net.arna.jcraft.JCraft;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.impl.gamerule.rule.BoundedIntRule;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

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
