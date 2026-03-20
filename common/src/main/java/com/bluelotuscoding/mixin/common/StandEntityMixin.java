package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.arna.jcraft.api.attack.IAttacker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Class: StandEntity
 * Stand: Any
 * Purpose: Modifies the max gauge based on the user's STAND_GAUGE_MAX attribute.
 */
@Mixin(targets = "net.arna.jcraft.api.stand.StandEntity", remap = false)
public abstract class StandEntityMixin {

    @Inject(method = "getMaxStandGauge()F", at = @At("RETURN"), cancellable = true, remap = false)
    private void jcraft_attributes$modifyMaxGauge(CallbackInfoReturnable<Float> cir) {
        LivingEntity user = ((IAttacker<?, ?>) (Object) this).getUser();
        if (user != null) {
            AttributeInstance attr = user.getAttribute(JAttributeRegistry.STAND_GAUGE_MAX);
            if (attr != null && attr.getValue() != 0)
                cir.setReturnValue(cir.getReturnValue() + (float) attr.getValue());
        }
    }
}
