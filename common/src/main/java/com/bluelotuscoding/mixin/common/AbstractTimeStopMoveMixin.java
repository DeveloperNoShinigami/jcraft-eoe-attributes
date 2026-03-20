package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Class: AbstractTimeStopMove
 * Stand: The World / Star Platinum / SP:TW
 * Purpose: Modifies the duration of time stop using the TIME_STOP_DURATION attribute.
 */
@Mixin(value = net.arna.jcraft.api.attack.moves.AbstractTimeStopMove.class, remap = false)
public abstract class AbstractTimeStopMoveMixin {

    @org.spongepowered.asm.mixin.injection.ModifyVariable(
        method = "perform(Lnet/arna/jcraft/api/stand/StandEntity;Lnet/minecraft/class_1309;)Ljava/util/Set;", 
        at = @At(value = "STORE", ordinal = 0), 
        ordinal = 0, 
        remap = false
    )
    private int jcraft_attributes$modifyTimeStopDuration(int duration, @Coerce Object stand, @Coerce Object user) {
        if (user instanceof LivingEntity living) {
            AttributeInstance attr = living.getAttribute(JAttributeRegistry.TIME_STOP_DURATION);
            if (attr != null && attr.getValue() > 0) {
                // System.out.println("[JCraft Attributes] Modifying TS duration: " + duration + " -> " + (duration + (int)attr.getValue()));
                return duration + (int)attr.getValue();
            }
        }
        return duration;
    }
}
