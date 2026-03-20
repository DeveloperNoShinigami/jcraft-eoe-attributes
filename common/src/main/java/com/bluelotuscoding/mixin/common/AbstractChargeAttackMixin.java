package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.arna.jcraft.api.attack.moves.AbstractChargeAttack;
import net.arna.jcraft.api.stand.StandEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Class: AbstractChargeAttack
 * Stand: Any
 * Purpose: Modifies the charge distance multiplier using the CHARGE_DISTANCE_MULTIPLIER attribute.
 */
@Mixin(value = AbstractChargeAttack.class, remap = false)
public abstract class AbstractChargeAttackMixin {

    @ModifyVariable(method = "tickChargeAttack(Lnet/arna/jcraft/api/stand/StandEntity;ZFI)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float jcraft_attributes$modifyChargeDistance(float moveDistance, StandEntity<?, ?> attacker) {
        LivingEntity user = attacker.getUser();
        if (user != null) {
            AttributeInstance attr = user.getAttribute(JAttributeRegistry.CHARGE_DISTANCE_MULTIPLIER);
            if (attr != null) {
                return (float) (moveDistance * attr.getValue());
            }
        }
        return moveDistance;
    }
}
