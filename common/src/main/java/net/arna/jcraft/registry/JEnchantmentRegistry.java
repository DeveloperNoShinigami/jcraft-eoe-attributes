package net.arna.jcraft.registry;

import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.enchantments.CinderellasKissEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static net.arna.jcraft.JCraft.ENCHANTMENT;

public interface JEnchantmentRegistry {
    RegistrySupplier<Enchantment> CINDERELLAS_KISS = ENCHANTMENT.register("cinderellas_kiss", () -> CinderellasKissEnchantment.INSTANCE);

    static void init() {
    }
}
