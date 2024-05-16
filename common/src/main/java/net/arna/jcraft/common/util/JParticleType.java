package net.arna.jcraft.common.util;

import lombok.Getter;
import net.arna.jcraft.registry.JParticleTypeRegistry;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;

@Getter
public enum JParticleType {
    BOOM(JParticleTypeRegistry.BOOM_1.get()),
    BITES_THE_DUST(JParticleTypeRegistry.BITES_THE_DUST.get()),
    SWEEP_ATTACK(ParticleTypes.SWEEP_ATTACK),
    BACK_STAB(JParticleTypeRegistry.BACKSTAB.get()),
    FLASH(ParticleTypes.FLASH),
    COMBO_BREAK(JParticleTypeRegistry.COMBO_BREAK.get()),
    COOLDOWN_CANCEL(JParticleTypeRegistry.COOLDOWN_CANCEL.get()),
    HIT_SPARK_1(JParticleTypeRegistry.HITSPARK_1.get()),
    HIT_SPARK_2(JParticleTypeRegistry.HITSPARK_2.get()),
    HIT_SPARK_3(JParticleTypeRegistry.HITSPARK_3.get()),
    PIXEL(JParticleTypeRegistry.PIXEL.get()),
    BLOCK_SPARK(JParticleTypeRegistry.BLOCKSPARK.get()),
    GO(JParticleTypeRegistry.GO.get()),
    AURA_ARC(JParticleTypeRegistry.AURA_ARC.get()),
    AURA_BLOB(JParticleTypeRegistry.AURA_BLOB.get());

    private final DefaultParticleType particleType;

    JParticleType(DefaultParticleType particleType) {
        this.particleType = particleType;
    }
}
