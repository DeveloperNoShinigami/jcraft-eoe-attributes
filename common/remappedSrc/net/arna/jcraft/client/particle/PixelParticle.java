package net.arna.jcraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class PixelParticle extends RisingParticle {
    PixelParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
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
    protected int getLightColor(float tint) {
        return 255;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            return new PixelParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}
