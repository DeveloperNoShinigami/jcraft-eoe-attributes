package net.arna.jcraft.api.registry;

import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.JCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public interface JStatRegistry {

    // alphabetic order
    RegistrySupplier<ResourceLocation> BLOOD_SUCKED = registerCustomStat("blood_sucked");
    RegistrySupplier<ResourceLocation> DASHES = registerCustomStat("dashes");
    RegistrySupplier<ResourceLocation> RAW_DAMAGE = registerCustomStat("raw_damage");
    RegistrySupplier<ResourceLocation> SPECS_CHANGED = registerCustomStat("specs_changed");
    RegistrySupplier<ResourceLocation> STAND_EVOLVED = registerCustomStat("stands_evolved");
    RegistrySupplier<ResourceLocation> STAND_SUMMONED = registerCustomStat("stands_summoned");
    RegistrySupplier<ResourceLocation> STAND_USERS_KILLED = registerCustomStat("stand_users_killed");
    RegistrySupplier<ResourceLocation> TIME_STOPPED = registerCustomStat("time_stopped");
    RegistrySupplier<ResourceLocation> VAMPIRE_LASER = registerCustomStat("vampire_laser");
    RegistrySupplier<ResourceLocation> VAMPIRE_REVIVES = registerCustomStat("vampire_revives");

    private static RegistrySupplier<ResourceLocation> registerCustomStat(final String id) {
        final ResourceLocation jcraftId = JCraft.id(id);
        return JCraft.STATS.register(id, () -> jcraftId);
    }

    static void init() {
        // left empty on purpose
    }

    static void initFormatters() {
        Stats.CUSTOM.get(BLOOD_SUCKED.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(DASHES.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(RAW_DAMAGE.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(SPECS_CHANGED.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(STAND_EVOLVED.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(STAND_SUMMONED.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(STAND_USERS_KILLED.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(TIME_STOPPED.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(VAMPIRE_LASER.get(), StatFormatter.DEFAULT);
        Stats.CUSTOM.get(VAMPIRE_REVIVES.get(), StatFormatter.DEFAULT);
    }
}
