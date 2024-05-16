package net.arna.jcraft.client.particle;

import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class AuraArcParticle extends RisingParticle {
    protected final SpriteSet spriteProvider;
    private final Entity parent;

    AuraArcParticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider, Vector3f color, Entity parent) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.spriteProvider = spriteProvider;
        this.setColor(color.x(), color.y(), color.z());
        this.alpha = 0.4f;
        this.quadSize = 0.5f;
        this.lifetime = 7;
        this.parent = parent;
        tryMatchParent();
        setSpriteFromAge(spriteProvider);
    }

    private void tryMatchParent() {
        if (parent != null) {
            Vec3 deltaPos = JUtils.deltaPos(parent);
            setParticleSpeed(deltaPos.x, deltaPos.y, deltaPos.z);
        }
    }

    public void tick() {
        super.tick();
        tryMatchParent();
        setSpriteFromAge(spriteProvider);
    }

    public ParticleRenderType getRenderType() {
        return JParticleTextureSheet.PARTICLE_SHEET_AURA;
    }

    @Override
    protected int getLightColor(float tint) {
        return 255;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;
        public static Vector3f color = new Vector3f(1f, 0f, 0f);
        public static Entity parent = null;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            return new AuraArcParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider, color, parent);
        }
    }
}