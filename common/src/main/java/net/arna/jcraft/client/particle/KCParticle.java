package net.arna.jcraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class KCParticle extends RisingParticle {
    private final SpriteSet spriteProvider;

    KCParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        this.quadSize = 0.2f + random.nextFloat() * 0.2f;
        this.lifetime = 4;
        this.setSpriteFromAge(spriteProvider);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void tick() {
        super.tick();
        float c = 1f - (float) age / (float) lifetime;
        this.setColor(c, c, c);

        if (!this.removed) {
            this.setSprite(spriteProvider.get(random));
        }
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
            return new KCParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}
