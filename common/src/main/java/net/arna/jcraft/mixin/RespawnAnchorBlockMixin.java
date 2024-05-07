package net.arna.jcraft.mixin;

import net.arna.jcraft.common.config.JServerConfig;
import net.minecraft.block.RespawnAnchorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {
    @ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;Lnet/minecraft/util/math/Vec3d;FZLnet/minecraft/world/World$ExplosionSourceType;)Lnet/minecraft/world/explosion/Explosion;"), index = 4)
    private float modifyExplosionPower(float d) {
        if (JServerConfig.REDUCE_DEADLY_EXPLOSIONS.getValue())
            return 1.5f;
        return 5.0f;
    }
}
