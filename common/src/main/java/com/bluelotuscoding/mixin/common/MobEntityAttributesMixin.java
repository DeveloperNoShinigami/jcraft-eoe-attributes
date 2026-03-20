package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.arna.jcraft.api.stand.StandEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Class: Mob
 * Stand: Any
 * Purpose: Synchronizes user attributes with stand attributes during tick.
 */
@Mixin(Mob.class)
public abstract class MobEntityAttributesMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void jcraft_attributes$applyStandAttributes(CallbackInfo ci) {
        if ((Object) this instanceof StandEntity stand) {
            LivingEntity user = stand.getUser();
            if (user != null) {
                // Alpha Override
                AttributeInstance alphaAttr = user.getAttribute(JAttributeRegistry.ALPHA_OVERRIDE);
                if (alphaAttr != null && alphaAttr.getValue() >= 0) {
                    stand.setAlphaOverride((float) alphaAttr.getValue());
                }
            }
        }
    }
}
