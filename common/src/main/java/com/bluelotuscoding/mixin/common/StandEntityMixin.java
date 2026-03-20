package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
// import net.arna.jcraft.api.stand.StandEntity; // Unused with targets approach
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Class: StandEntity
 * Stand: Any
 * Purpose: Modifies stand-specific attributes like damage, speed, and durability based on the user's attributes.
 */
@Mixin(targets = "net.arna.jcraft.api.stand.StandEntity", remap = false)
public abstract class StandEntityMixin {

    @Shadow(remap = false)
    protected float maxStandGauge;
    @Shadow(remap = false)
    public boolean blocking;

    @Unique
    private LivingEntity jcraft_attributes$getUserReflect() {
        try {
            java.lang.reflect.Method m = this.getClass().getMethod("getUser");
            return (LivingEntity) m.invoke(this);
        } catch (Exception e) {
            return null;
        }
    }

    @Unique
    private float jcraftAttributes_baseMaxStandGauge = -1f;

    @ModifyVariable(method = "setDistanceOffset(F)V", at = @At("HEAD"), argsOnly = true, remap = false)
    private float jcraft_attributes$modifyDistanceOffset(float originalValue) {
        LivingEntity user = jcraft_attributes$getUserReflect();
        if (user != null) {
            float totalBonus = 0f;

            // Idle distance bonus
            AttributeInstance idleAttr = user.getAttribute(JAttributeRegistry.IDLE_DISTANCE);
            if (idleAttr != null) {
                totalBonus += (float) idleAttr.getValue();
            }

            // Blocking distance bonus
            if (this.blocking) {
                AttributeInstance blockAttr = user.getAttribute(JAttributeRegistry.BLOCK_DISTANCE);
                if (blockAttr != null) {
                    totalBonus += (float) blockAttr.getValue();
                }
            }

            return originalValue + totalBonus;
        }
        return originalValue;
    }

    @ModifyVariable(method = "setRotationOffset(F)V", at = @At("HEAD"), argsOnly = true, remap = false)
    private float jcraft_attributes$modifyRotationOffset(float originalValue) {
        LivingEntity user = jcraft_attributes$getUserReflect();
        if (user != null) {
            AttributeInstance attr = user.getAttribute(JAttributeRegistry.IDLE_ROTATION);
            if (attr != null) {
                return originalValue + (float) attr.getValue();
            }
        }
        return originalValue;
    }

    @Inject(method = "getMaxStandGauge()F", at = @At("RETURN"), cancellable = true, remap = false)
    private void jcraft_attributes$modifyMaxGauge(CallbackInfoReturnable<Float> cir) {
        LivingEntity user = jcraft_attributes$getUserReflect();
        if (user != null) {
            AttributeInstance attr = user.getAttribute(JAttributeRegistry.STAND_GAUGE_MAX);
            if (attr != null && attr.getValue() > 0) {
                cir.setReturnValue(cir.getReturnValue() + (float) attr.getValue());
            }
        }
    }
}
