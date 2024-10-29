package net.arna.jcraft.registry;

import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.common.enchantments.CinderellasKissEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;

import static net.arna.jcraft.JCraft.ENCHANTMENT;

public interface JEnchantmentRegistry {
    RegistrySupplier<Enchantment> CINDERELLAS_KISS = ENCHANTMENT.register("cinderellas_kiss", () -> CinderellasKissEnchantment.INSTANCE);

    static void init() {
        // intentionally left empty
    }
}
