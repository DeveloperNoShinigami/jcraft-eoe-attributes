package net.arna.jcraft.registry;

import net.arna.jcraft.JCraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.StatType;

// UNUSED
@Deprecated()
public class JStatRegistry {
    //public static StatType<Item> TEST;

    private static <T> StatType<T> registerType(String id, Registry<T> registry) {
        return Registry.register(BuiltInRegistries.STAT_TYPE, JCraft.id(id), new StatType<>(registry));
    }

    public static void registerStatistics() {
        //TEST = registerType("test", Registry.ITEM);
    }
}
