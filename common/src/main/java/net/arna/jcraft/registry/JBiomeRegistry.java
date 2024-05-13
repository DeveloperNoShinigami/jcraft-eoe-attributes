package net.arna.jcraft.registry;

import net.arna.jcraft.JCraft;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;

public interface JBiomeRegistry {
    RegistryKey<Biome> DEVILS_PALM = RegistryKey.of(RegistryKeys.BIOME, JCraft.id("devils_palm"));
}
