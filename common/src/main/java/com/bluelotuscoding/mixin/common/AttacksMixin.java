package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.arna.jcraft.api.Attacks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Class: Attacks
 * Stand: Any
 * Purpose: Intercepts the pre-armor damage value inside Attacks.damage() to apply
 *          STAND_DAMAGE (flat bonus) and STAND_RESISTANCE (diminishing-returns reduction).
 *          Uses @Redirect on getDamageThroughArmor so the rest of the original pipeline
 *          (modifyAppliedDamage, applyAbsorptionAndStats) runs untouched.
 */
@Mixin(value = Attacks.class, remap = false)
public interface AttacksMixin {

    @Redirect(
        method = "damage(Lnet/minecraft/class_1297;FLnet/minecraft/class_1282;Lnet/minecraft/class_1309;)V",
        at = @At(value = "INVOKE",
                 target = "Lnet/arna/jcraft/common/util/JUtils;getDamageThroughArmor(FFF)F"),
        remap = false
    )
    private static float jcraft_attributes$applyAttributesAndArmor(
            float modified, float armor, float toughness,
            @Nullable Entity attacker, float damage, DamageSource damageSource, LivingEntity ent) {

        // STAND_DAMAGE — flat bonus from attacker's stand user, applied pre-armor
        if (attacker != null) {
            Entity user = net.arna.jcraft.common.util.JUtils.getUserIfStand(attacker);
            if (user instanceof LivingEntity livingUser) {
                AttributeInstance sdAttr = livingUser.getAttribute(JAttributeRegistry.STAND_DAMAGE);
                if (sdAttr != null && sdAttr.getValue() != 0)
                    modified += (float) sdAttr.getValue();
            }
        }

        // STAND_RESISTANCE — diminishing-returns reduction on victim, applied pre-armor
        AttributeInstance srAttr = ent.getAttribute(JAttributeRegistry.STAND_RESISTANCE);
        if (srAttr != null && srAttr.getValue() > 0)
            modified = (float) (modified * (1.0 / (1.0 + srAttr.getValue())));

        return net.arna.jcraft.common.util.JUtils.getDamageThroughArmor(modified, armor, toughness);
    }
}
