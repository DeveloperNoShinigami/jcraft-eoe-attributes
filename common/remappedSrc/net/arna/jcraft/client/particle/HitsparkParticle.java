package net.arna.jcraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class HitsparkParticle extends JGlowingParticle {
    HitsparkParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
    }

    @Override
    protected void initialize() {
        this.alpha = 1f;
        this.quadSize = 0.5f;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;
        private final float scale;
        private final int maxAge;

        public Factory(SpriteSet spriteProvider, float scale, int maxAge) {
            this.spriteProvider = spriteProvider;
            this.scale = scale;
            this.maxAge = maxAge;
        }

        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            HitsparkParticle hitsparkParticle = new HitsparkParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            hitsparkParticle.quadSize = this.scale;
            hitsparkParticle.lifetime = this.maxAge;
            return hitsparkParticle;
        }
    }
}
