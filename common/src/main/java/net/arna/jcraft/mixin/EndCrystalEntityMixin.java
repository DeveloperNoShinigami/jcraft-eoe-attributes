package net.arna.jcraft.mixin;

import net.arna.jcraft.common.config.JServerConfig;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EndCrystal.class)
public class EndCrystalEntityMixin {
    @ModifyArg(method = "hurt", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;")
            , index = 6)
    private float modifyExplosionPower(float d) {
        if (JServerConfig.REDUCE_DEADLY_EXPLOSIONS.getValue()) {
            return 1.5f;
        }
        return 6.0f;
    }
}