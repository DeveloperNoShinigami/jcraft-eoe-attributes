package net.arna.jcraft.registry;

import net.arna.jcraft.JCraft;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface JParticleTypeRegistry {


    DefaultParticleType AURA_ARC = new DefaultParticleType(false);
    DefaultParticleType AURA_BLOB = new DefaultParticleType(false);
    DefaultParticleType COMBO_BREAK = new DefaultParticleType(false);
    DefaultParticleType COOLDOWN_CANCEL = new DefaultParticleType(false);
    DefaultParticleType HITSPARK_1 = new DefaultParticleType(false);
    DefaultParticleType HITSPARK_2 = new DefaultParticleType(false);
    DefaultParticleType HITSPARK_3 = new DefaultParticleType(false);
    DefaultParticleType KCPARTICLE = new DefaultParticleType(false);
    DefaultParticleType BACKSTAB = new DefaultParticleType(false);
    DefaultParticleType SPEED_PARTICLE = new DefaultParticleType(false);
    DefaultParticleType BITES_THE_DUST = new DefaultParticleType(false);
    DefaultParticleType BOOM_1 = new DefaultParticleType(false);
    DefaultParticleType PIXEL = new DefaultParticleType(false);
    DefaultParticleType BLOCKSPARK = new DefaultParticleType(false);
    DefaultParticleType GO = new DefaultParticleType(false);
    DefaultParticleType INVERSION = new DefaultParticleType(false);
    DefaultParticleType SUN_LOCK_ON = new DefaultParticleType(false);
    DefaultParticleType PURPLE_HAZE_CLOUD = new DefaultParticleType(false);
    DefaultParticleType PURPLE_HAZE_PARTICLE = new DefaultParticleType(false);

    private static void registerParticle(String identifier, ParticleType<?> type) {
        Registry.register(Registries.PARTICLE_TYPE, JCraft.id(identifier), type);
    }

    static void initParticleTypes() {
        registerParticle("combo_break", COMBO_BREAK);
        registerParticle("cooldown_cancel", COOLDOWN_CANCEL);
        registerParticle("hitspark_1", HITSPARK_1);
        registerParticle("hitspark_2", HITSPARK_2);
        registerParticle("hitspark_3", HITSPARK_3);
        registerParticle("kcparticle", KCPARTICLE);
        registerParticle("backstab", BACKSTAB);
        registerParticle("speedparticle", SPEED_PARTICLE);
        registerParticle("btd", BITES_THE_DUST);
        registerParticle("boom_1", BOOM_1);
        registerParticle("pixel", PIXEL);
        registerParticle("blockspark", BLOCKSPARK);
        registerParticle("go", GO);
        registerParticle("aura_arc", AURA_ARC);
        registerParticle("aura_blob", AURA_BLOB);
        registerParticle("inversion", INVERSION);
        registerParticle("sun_lock_on", SUN_LOCK_ON);
        registerParticle("purple_haze_cloud", PURPLE_HAZE_CLOUD);
        registerParticle("purple_haze_particle", PURPLE_HAZE_PARTICLE);
    }
}
