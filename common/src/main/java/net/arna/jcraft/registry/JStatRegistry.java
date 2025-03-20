package net.arna.jcraft.registry;

import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.JCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public interface JStatRegistry {

    RegistrySupplier<ResourceLocation> STAND_SUMMONED = registerCustomStat("stands_summoned");
    RegistrySupplier<ResourceLocation> STAND_EVOLVED = registerCustomStat("stands_evolved");

    private static RegistrySupplier<ResourceLocation> registerCustomStat(final String id) {
        final ResourceLocation jcraftId = JCraft.id(id);
        return JCraft.STATS.register(id, () -> jcraftId);
    }

    static void init() {
        // left empty on purpose
    }

    static void initFormatters() {
        Stats.CUSTOM.get(STAND_SUMMONED.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(STAND_EVOLVED.get(), StatFormatter.DEFAULT);
    }
}
