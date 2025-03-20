package net.arna.jcraft.registry;

import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.JCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public interface JStatRegistry {

    // alphabetic order
    RegistrySupplier<ResourceLocation> BLOOD_SUCKED = registerCustomStat("bloodSucked");
    RegistrySupplier<ResourceLocation> DASHS = registerCustomStat("dashs");
    RegistrySupplier<ResourceLocation> STAND_EVOLVED = registerCustomStat("standsEvolved");
    RegistrySupplier<ResourceLocation> STAND_SUMMONED = registerCustomStat("standsSummoned");
    RegistrySupplier<ResourceLocation> TIME_STOPPED = registerCustomStat("timeStopped");
    RegistrySupplier<ResourceLocation> VAMPIRE_LASER = registerCustomStat("vampireLaser");

    private static RegistrySupplier<ResourceLocation> registerCustomStat(final String id) {
        final ResourceLocation jcraftId = JCraft.id(id);
        return JCraft.STATS.register(id, () -> jcraftId);
    }

    static void init() {
        // left empty on purpose
    }

    static void initFormatters() {
        Stats.CUSTOM.get(BLOOD_SUCKED.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(DASHS.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(STAND_EVOLVED.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(STAND_SUMMONED.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(TIME_STOPPED.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(VAMPIRE_LASER.get(), StatFormatter.DEFAULT);
    }
}
