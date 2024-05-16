package net.arna.jcraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.particle.SpriteSet;

public class JGlowingParticle extends RisingParticle {
    protected final SpriteSet spriteProvider;

    JGlowingParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        initialize();
        this.setSpriteFromAge(spriteProvider);
    }

    protected void initialize() {

    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteProvider);
    }

    @Override
    protected int getLightColor(float tint) {
        return 255;
    }
}
