package net.arna.jcraft.common.util;

import dev.architectury.registry.registries.RegistrySupplier;
import lombok.Getter;
import net.arna.jcraft.registry.JParticleTypeRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

@Getter
public enum JParticleType {
    BOOM(JParticleTypeRegistry.BOOM_1),
    BITES_THE_DUST(JParticleTypeRegistry.BITES_THE_DUST),
    SWEEP_ATTACK(ParticleTypes.SWEEP_ATTACK),
    BACK_STAB(JParticleTypeRegistry.BACKSTAB),
    FLASH(ParticleTypes.FLASH),
    COMBO_BREAK(JParticleTypeRegistry.COMBO_BREAK),
    COOLDOWN_CANCEL(JParticleTypeRegistry.COOLDOWN_CANCEL),
    HIT_SPARK_1(JParticleTypeRegistry.HITSPARK_1),
    HIT_SPARK_2(JParticleTypeRegistry.HITSPARK_2),
    HIT_SPARK_3(JParticleTypeRegistry.HITSPARK_3),
    INVERTED_HIT_SPARK_3(JParticleTypeRegistry.INVERTED_HITSPARK_3),
    PIXEL(JParticleTypeRegistry.PIXEL),
    BLOCK_SPARK(JParticleTypeRegistry.BLOCKSPARK),
    GO(JParticleTypeRegistry.GO),
    AURA_ARC(JParticleTypeRegistry.AURA_ARC),
    AURA_BLOB(JParticleTypeRegistry.AURA_BLOB),
    STUN_SLASH(JParticleTypeRegistry.STUN_SLASH),
    STUN_PIERCE(JParticleTypeRegistry.STUN_PIERCE);

    private final SimpleParticleType particleType;

    JParticleType(SimpleParticleType particleType) {
        this.particleType = particleType;
    }
    JParticleType(RegistrySupplier<SimpleParticleType> supplier) {
        this.particleType = supplier.get();
    }
}
