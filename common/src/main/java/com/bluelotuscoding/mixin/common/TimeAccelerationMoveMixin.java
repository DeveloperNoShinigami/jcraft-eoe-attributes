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
 * Class: TimeAccelerationMove
 * Stand: Made In Heaven
 * Purpose: Modifies acceleration ticks using the ACCEL_DURATION attribute.
 */
@Mixin(targets = "net.arna.jcraft.common.attack.moves.madeinheaven.TimeAccelerationMove", remap = false)
public abstract class TimeAccelerationMoveMixin {

    // Inject at RETURN so perform() has already called attacker.setAccelTime(baseAccelTime).
    // Reading accelTime here gives the base value — we add the bonus on top cleanly.
    @Inject(method = "perform(Lnet/arna/jcraft/common/entity/stand/MadeInHeavenEntity;Lnet/minecraft/class_1309;)Ljava/util/Set;", at = @At("RETURN"), remap = false)
    private void jcraft_attributes$modifyAccelTicks(@Coerce Object attacker, @Coerce Object userObj, CallbackInfoReturnable<?> cir) {
        if (userObj instanceof LivingEntity user) {
            AttributeInstance attr = user.getAttribute(JAttributeRegistry.ACCEL_DURATION);
            if (attr != null && attr.getValue() != 0) {
                try {
                    // attacker is the MadeInHeavenEntity instance
                    java.lang.reflect.Method getAccel = attacker.getClass().getMethod("getAccelTime");
                    java.lang.reflect.Method setAccel = attacker.getClass().getMethod("setAccelTime", int.class);
                    int current = (int) getAccel.invoke(attacker);
                    setAccel.invoke(attacker, current + (int)attr.getValue());
                } catch (Exception e) {
                    // Log error if needed, but avoid crashing
                }
            }
        }
    }
}
