package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import com.bluelotuscoding.util.MoveContext;
import net.arna.jcraft.common.entity.stand.TheHandEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.phys.Vec3; // Unused in this version
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

/**
 * Class: EraseSpaceAttack
 * Stand: The Hand
 * Purpose: Modifies the erasure reach using the ERASURE_REACH attribute.
 */
@Mixin(targets = "net.arna.jcraft.common.attack.moves.thehand.EraseSpaceAttack", remap = false)
public abstract class EraseSpaceAttackMixin {

    @Inject(method = "perform(Lnet/arna/jcraft/common/entity/stand/TheHandEntity;Lnet/minecraft/class_1309;)Ljava/util/Set;", at = @At("HEAD"), remap = false)
    private void jcraft_attributes$setPlayerContext(TheHandEntity attacker, LivingEntity user, CallbackInfoReturnable<Set<LivingEntity>> cir) {
        MoveContext.setPlayer(user instanceof Player ? (Player) user : null);
    }

    @ModifyArg(
            method = "perform(Lnet/arna/jcraft/common/entity/stand/TheHandEntity;Lnet/minecraft/class_1309;)Ljava/util/Set;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/class_243;method_1021(D)Lnet/minecraft/class_243;"),
            index = 0,
            remap = false
    )
    private double jcraft_attributes$modifyEraseReach(double baseRange) {
        if (baseRange == 16.0) {
            LivingEntity player = MoveContext.getPlayer();
            if (player != null) {
                AttributeInstance attr = player.getAttribute(JAttributeRegistry.ERASURE_REACH);
                if (attr != null && attr.getValue() > 0) {
                    // Additive: extend base 16-block raycast by the attribute bonus
                    return 16.0 + attr.getValue();
                }
            }
        }
        return baseRange;
    }
}
