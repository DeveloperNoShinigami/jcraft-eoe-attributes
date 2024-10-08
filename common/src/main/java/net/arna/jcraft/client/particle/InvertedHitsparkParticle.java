package net.arna.jcraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class InvertedHitsparkParticle extends HitsparkParticle {
    InvertedHitsparkParticle(final ClientLevel world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ, final SpriteSet spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return JParticleTextureSheet.INVERSION_SHEET;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;
        private final float scale;
        private final int maxAge;

        public Factory(final SpriteSet spriteProvider, final float scale, final int maxAge) {
            this.spriteProvider = spriteProvider;
            this.scale = scale;
            this.maxAge = maxAge;
        }

        public Particle createParticle(final SimpleParticleType defaultParticleType, final ClientLevel clientWorld, final double d, final double e, final double f, final double g, final double h, final double i) {
            InvertedHitsparkParticle hitsparkParticle = new InvertedHitsparkParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            hitsparkParticle.quadSize = this.scale;
            hitsparkParticle.lifetime = this.maxAge;
            return hitsparkParticle;
        }
    }
}
