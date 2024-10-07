package net.arna.jcraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class PixelParticle extends RisingParticle {
    PixelParticle(final ClientLevel world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ, final SpriteSet spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.setSpriteFromAge(spriteProvider);
        this.setColor(1.0f, 0.7f, 0.4f);
        this.quadSize = 0.03f;
        this.lifetime = 7 + this.random.nextIntBetweenInclusive(-3, 3);
        this.gravity = 4.9f; // g / 20
        this.hasPhysics = true;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    public void tick() {
        super.tick();
        float c = (float) (lifetime - age) / lifetime;
        this.setColor(1.0f, 0.5f + c * 0.5f, 0.2f + c * 0.8f);
    }

    @Override
    protected int getLightColor(final float tint) {
        return 255;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(final SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(final SimpleParticleType defaultParticleType, final ClientLevel clientWorld, final double d, final double e, final double f, final double g, final double h, final double i) {
            return new PixelParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}
