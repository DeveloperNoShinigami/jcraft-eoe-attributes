package net.arna.jcraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class PurpleHazeCloudParticle extends RisingParticle {
    private final SpriteSet spriteProvider;
    private final float decrement;

    protected PurpleHazeCloudParticle(final ClientLevel clientWorld, final double d, final double e, final double f, final double g, final double h, final double i, final SpriteSet spriteProvider) {
        super(clientWorld, d, e, f, g, h, i);
        this.spriteProvider = spriteProvider;
        this.lifetime = 16;
        this.decrement = 0.5f / lifetime;
        this.quadSize = 1.0f;
        this.alpha = 1.0f;
        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(spriteProvider);
        this.quadSize -= decrement;
        this.alpha -= decrement;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(final SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(final SimpleParticleType defaultParticleType, final ClientLevel clientWorld, final double d, final double e, final double f, final double g, final double h, final double i) {
            return new PurpleHazeCloudParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}
