package net.arna.jcraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class BlocksparkParticle extends RisingParticle {
    protected final SpriteSet spriteProvider;

    BlocksparkParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
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

        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            BlocksparkParticle hitsparkParticle = new BlocksparkParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            hitsparkParticle.quadSize = this.scale;
            return hitsparkParticle;
        }
    }
}