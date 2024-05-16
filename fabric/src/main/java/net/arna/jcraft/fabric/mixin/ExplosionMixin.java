package net.arna.jcraft.fabric.mixin;

import net.arna.jcraft.common.util.IJExplosion;
import net.arna.jcraft.common.util.JExplosionModifier;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Explosion.class)
public class ExplosionMixin implements IJExplosion {
    @Shadow
    @Final
    private boolean createFire;
    @Shadow
    @Final
    private Explosion.BlockInteraction destructionType;
    @Shadow
    @Final
    private Level world;
    private @Unique JExplosionModifier modifier;

    // Interface implementation
    @Override
    public void jcraft$setModifier(JExplosionModifier modifier) {
        this.modifier = modifier;
    }

    // Functionality
    @Redirect(method = "finalizeExplosion", at = @At(value = "FIELD",
            target = "Lnet/minecraft/world/level/Explosion;blockInteraction:Lnet/minecraft/world/level/Explosion$BlockInteraction;")
            , require = 2)
    private Explosion.BlockInteraction overrideDestructionType(Explosion thiz) {
        return modifier == null || modifier.getDestructionType() == null ? destructionType : modifier.getDestructionType();
    }

    @Redirect(method = "finalizeExplosion", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/Explosion;fire:Z"))
    private boolean overrideCreateFire(Explosion thiz) {
        return modifier == null || modifier.getCreateFire() == null ? createFire : modifier.getCreateFire();
    }

    @ModifyVariable(method = "finalizeExplosion", at = @At("HEAD"), argsOnly = true)
    private boolean overrideParticlesArgument(boolean particles) {
        return particles || modifier != null && modifier.getParticle() != null;
    }

    @ModifyArg(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), require = 2)
    private ParticleOptions overrideParticleEffect(ParticleOptions particle) {
        return modifier == null || modifier.getParticle() == null ? particle : modifier.getParticle();
    }

    @ModifyArg(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"),
            require = 2, index = 4)
    private double overrideParticleVelocityX(double x) {
        return modifier == null || modifier.getParticleVelocity() == null ? x : modifier.getParticleVelocity().x;
    }

    @ModifyArg(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"),
            require = 2, index = 5)
    private double overrideParticleVelocityY(double y) {
        return modifier == null || modifier.getParticleVelocity() == null ? y : modifier.getParticleVelocity().y;
    }

    @ModifyArg(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"),
            require = 2, index = 6)
    private double overrideParticleVelocityZ(double z) {
        return modifier == null || modifier.getParticleVelocity() == null ? z : modifier.getParticleVelocity().z;
    }

    @ModifyArg(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"))
    private SoundEvent overrideSound(SoundEvent sound) {
        return modifier == null || modifier.getSound() == null ? sound : modifier.getSound();
    }

    @ModifyArg(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"))
    private SoundSource overrideSoundCategory(SoundSource category) {
        return modifier == null || modifier.getSoundCategory() == null ? category : modifier.getSoundCategory();
    }

    @ModifyArg(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"), index = 5)
    private float overrideVolume(float volume) {
        return modifier == null || modifier.getVolumeGetter() == null ? volume : modifier.getVolumeGetter().apply(world.random);
    }

    @ModifyArg(method = "finalizeExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"), index = 6)
    private float overridePitch(float pitch) {
        return modifier == null || modifier.getPitchGetter() == null ? pitch : modifier.getPitchGetter().apply(world.random);
    }
}
