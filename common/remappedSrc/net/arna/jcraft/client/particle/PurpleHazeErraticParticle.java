package net.arna.jcraft.client.particle;

import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

public class PurpleHazeErraticParticle extends RisingParticle {
    protected PurpleHazeErraticParticle(ClientLevel clientWorld, double d, double e, double f, double g, double h, double i, SpriteSet spriteProvider) {
        super(clientWorld, d, e, f, g, h, i);
        this.lifetime = 16;
        this.quadSize = 0.05f;
        this.setColor(0.6f, 0.3f, 0.8f);
        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 velocity = new Vec3(
                xd,
                yd,
                zd
        );

        velocity = velocity.add(JUtils.randUnitVec(random)).scale(0.5);

        this.setParticleSpeed(velocity.x, velocity.y, velocity.z);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            return new PurpleHazeErraticParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}
