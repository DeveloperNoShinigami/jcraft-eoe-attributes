package net.arna.jcraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

// POC particle. Feel free to remove.
// Any particle that renders with the JParticleTextureSheet.INVERSION_SHEET
// will be rendered with an inverted effect.
// For the time being, colors are ignored
public class InversionParticle extends RisingParticle {

    protected InversionParticle(ClientLevel clientWorld, double d, double e, double f, double g, double h, double i,
                                SpriteSet spriteProvider) {
        super(clientWorld, d, e, f, g, h, i);
        quadSize *= 1f;
        pickSprite(spriteProvider);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return JParticleTextureSheet.INVERSION_SHEET;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new InversionParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
        }
    }
}
