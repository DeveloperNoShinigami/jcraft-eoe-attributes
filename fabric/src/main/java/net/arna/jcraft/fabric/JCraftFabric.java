package net.arna.jcraft.fabric;

import net.arna.jcraft.registry.JCommandRegistry;
import net.fabricmc.api.ModInitializer;

import net.arna.jcraft.JCraft;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
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
    }
}
