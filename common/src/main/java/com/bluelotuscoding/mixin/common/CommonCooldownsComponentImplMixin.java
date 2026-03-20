package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.arna.jcraft.common.component.impl.living.CommonCooldownsComponentImpl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// Targets JCraft internal class (not stable API) — verified against JCraft 0.17.6
// Class: net.arna.jcraft.common.component.impl.living.CommonCooldownsComponentImpl
// Purpose: Injects COOLDOWN_REDUCTION into setCooldown().
//          Applies a percentage reduction so all Stand move cooldowns scale with the attribute.
//          Formula: finalCooldown = baseCooldown * (1 - cooldownReduction)
/**
 * Class: CommonCooldownsComponentImpl
 * Stand: Any
 * Purpose: Implements custom cooldown modifiers using attributes.
 */
@Mixin(value = CommonCooldownsComponentImpl.class, remap = false)
public abstract class CommonCooldownsComponentImplMixin {

    @Shadow @Final private Entity entity;

    @ModifyVariable(method = "setCooldown(Lnet/arna/jcraft/common/util/CooldownType;I)V", at = @At("HEAD"), argsOnly = true)
    private int jcraft_attributes$modifyCooldown(int duration) {
        if (duration <= 0) return duration;
        
        if (entity instanceof LivingEntity provider) {
            AttributeInstance attr = provider.getAttribute(JAttributeRegistry.COOLDOWN_REDUCTION);
            if (attr != null) {
                // duration * (1 - reduction)
                double reduction = attr.getValue();
                return (int) Math.max(0, duration * (1.0 - reduction));
            }
        }
        
        return duration;
    }
}
