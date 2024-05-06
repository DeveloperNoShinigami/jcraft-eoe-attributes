package net.arna.jcraft.fabric;

import net.fabricmc.api.ModInitializer;

import net.arna.jcraft.JCraft;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class JCraftFabric implements ModInitializer {

    RegistryKey<ItemGroup> JCRAFT_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier(JCraft.MOD_ID));

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        JCraft.init();
    }
}
