package net.arna.jcraft.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.JCraft;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static net.arna.jcraft.JCraft.PARTICLES;

public interface JParticleTypeRegistry {

    RegistrySupplier<DefaultParticleType> AURA_ARC = PARTICLES.register("aura_arc", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> COMBO_BREAK = PARTICLES.register("combo_break", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> COOLDOWN_CANCEL = PARTICLES.register("cooldown_cancel", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> HITSPARK_1 = PARTICLES.register("hitspark_1", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> HITSPARK_2 = PARTICLES.register("hitspark_2", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> HITSPARK_3 = PARTICLES.register("hitspark_3", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> AURA_BLOB = PARTICLES.register("aura_blob", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> KCPARTICLE = PARTICLES.register("kcparticle", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> BACKSTAB = PARTICLES.register("backstab", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> SPEED_PARTICLE = PARTICLES.register("speedparticle", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> BITES_THE_DUST = PARTICLES.register("btd", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> BOOM_1 = PARTICLES.register("boom_1", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> PIXEL = PARTICLES.register("pixel", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> BLOCKSPARK = PARTICLES.register("blockspark", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> GO = PARTICLES.register("go", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> INVERSION = PARTICLES.register("inversion", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> SUN_LOCK_ON = PARTICLES.register("sun_lock_on", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> PURPLE_HAZE_CLOUD = PARTICLES.register("purple_haze_cloud", () -> new DefaultParticleType(false));
    RegistrySupplier<DefaultParticleType> PURPLE_HAZE_PARTICLE = PARTICLES.register("purple_haze_particle", () -> new DefaultParticleType(false));


    static void initParticleTypes() {
    }
}
