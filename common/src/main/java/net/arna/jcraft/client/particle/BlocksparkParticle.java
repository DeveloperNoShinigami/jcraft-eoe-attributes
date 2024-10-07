package net.arna.jcraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class BlocksparkParticle extends RisingParticle {
    protected final SpriteSet spriteProvider;

    BlocksparkParticle(final ClientLevel world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ, final SpriteSet spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        this.setColor(0.3f, 0.9f, 1.0f);
        this.lifetime = 6;
        setSpriteFromAge(spriteProvider);
    }

    public void tick() {
        super.tick();
        if (age % 2 == 0) {
            setSprite(spriteProvider.get(random));
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float tint) {
        return 255;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;
        private final float scale;

        public Factory(SpriteSet spriteProvider, float scale) {
            this.spriteProvider = spriteProvider;
            this.scale = scale;
        }

        public Particle createParticle(final SimpleParticleType defaultParticleType, final ClientLevel clientWorld, final double d, final double e, final double f, final double g, final double h, final double i) {
            BlocksparkParticle hitsparkParticle = new BlocksparkParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            hitsparkParticle.quadSize = this.scale;
            return hitsparkParticle;
        }
    }
}