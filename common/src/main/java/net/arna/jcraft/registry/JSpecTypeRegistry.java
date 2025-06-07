package net.arna.jcraft.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.spec.SpecType2;
import net.arna.jcraft.common.spec.AnubisSpec;
import net.arna.jcraft.common.spec.BrawlerSpec;
import net.arna.jcraft.common.spec.VampireSpec;
import net.minecraft.Util;

public interface JSpecTypeRegistry {
    DeferredRegister<SpecType2> SPEC_TYPE_REGISTRY = DeferredRegister.create(JCraft.MOD_ID, JRegistries.SPEC_TYPE_REGISTRY_KEY);

    RegistrySupplier<SpecType2> NONE = register("none", user -> null);
    RegistrySupplier<SpecType2> BRAWLER = register("brawler", BrawlerSpec::new);
    RegistrySupplier<SpecType2> ANUBIS = register("anubis", AnubisSpec::new);
    RegistrySupplier<SpecType2> VAMPIRE = register("vampire", VampireSpec::new);

    Int2ObjectMap<RegistrySupplier<SpecType2>> LEGACY_ORDINALS = Util.make(new Int2ObjectArrayMap<>(), map -> {
        map.put(0, NONE);
        map.put(1, BRAWLER);
        map.put(2, ANUBIS);
        map.put(3, VAMPIRE);
    });

    private static RegistrySupplier<SpecType2> register(String name, SpecType2.SpecFactory factory) {
        return SPEC_TYPE_REGISTRY.register(name, () -> SpecType2.of(JCraft.id(name), factory));
    }
}
