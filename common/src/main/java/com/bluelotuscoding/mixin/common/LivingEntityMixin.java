package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Class: LivingEntity
 * Stand: Any
 * Purpose: Modifies internal attributes e.g armor and damage reduction based on custom attributes.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    // Targets LivingEntity#hurt (Mojang mapping) — remapped from Yarn's "damage" by Loom
    @ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
    private float jcraft_attributes$applyArmorBonus(float amount, DamageSource source) {
        LivingEntity instance = (LivingEntity) (Object) this;
        AttributeInstance attr = instance.getAttribute(JAttributeRegistry.ARMOR_BONUS);
        if (attr != null && attr.getValue() > 0) {
            // Simple reduction formula: Final = Original * (1 - (Bonus / (Bonus + 20)))
            // Or a flat reduction:
            float reduction = (float) attr.getValue();
            return Math.max(0.1f, amount - reduction);
        }
        return amount;
    }
}
