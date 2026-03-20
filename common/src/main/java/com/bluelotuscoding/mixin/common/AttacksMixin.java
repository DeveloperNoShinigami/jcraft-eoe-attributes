package com.bluelotuscoding.mixin.common;

import net.arna.jcraft.api.Attacks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.jetbrains.annotations.Nullable;

/**
 * Class: Attacks
 * Stand: Any
 * Purpose: Modifies damage and armor penetration based on stand attributes.
 */
@Mixin(value = Attacks.class, remap = false)
public interface AttacksMixin {

    /**
     * @reason Add Stand Damage and Resistance attributes to the JCraft damage path.
     */
    @Overwrite
    static void damage(@Nullable Entity attacker, float damage, DamageSource damageSource, LivingEntity ent) {
        if (!net.arna.jcraft.common.util.JUtils.canDamage(damageSource, ent)) {
            return;
        }

        float baseDamage = damage;
        
        // --- Custom Attribute Logic (Stand Damage) ---
        if (attacker != null) {
            Entity user = net.arna.jcraft.common.util.JUtils.getUserIfStand(attacker);
            if (user instanceof LivingEntity livingUser) {
                net.minecraft.world.entity.ai.attributes.AttributeInstance standDamageAttr = 
                    livingUser.getAttribute(com.bluelotuscoding.api.registry.JAttributeRegistry.STAND_DAMAGE);
                if (standDamageAttr != null) {
                    baseDamage += (float) standDamageAttr.getValue();
                }
            }
        }
        // ---------------------------------------------

        float scaling = ((net.arna.jcraft.common.util.IJCraftComboTracker) ent).jcraft$getDamageScaling();
        float modified = baseDamage * scaling;

        if (net.arna.jcraft.common.config.JServerConfig.HEALTH_TO_DAMAGE_SCALING.getValue()) {
            float healthRatio = ent.getMaxHealth() / 20.0f;
            float damageAdjustment = healthRatio - 1.0f;
            if (damageAdjustment > 0.0f) {
                modified *= (1.0f + damageAdjustment / 5.0f);
            }
        }

        // --- Custom Attribute Logic (Stand Resistance) ---
        net.minecraft.world.entity.ai.attributes.AttributeInstance standResAttr = 
            ent.getAttribute(com.bluelotuscoding.api.registry.JAttributeRegistry.STAND_RESISTANCE);
        if (standResAttr != null && standResAttr.getValue() > 0) {
            modified = (float) (modified * (1.0 / (1.0 + standResAttr.getValue())));
        }
        // -------------------------------------------------

        float armor = ent.getArmorValue();
        float toughness = (float) ent.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS).getValue();

        if (net.arna.jcraft.common.util.JUtils.getUserIfStand(attacker) instanceof net.minecraft.world.entity.player.Player player) {
            player.awardStat(net.arna.jcraft.api.registry.JStatRegistry.RAW_DAMAGE.get(), (int)modified);
        }

        if (ent instanceof net.minecraft.world.entity.player.Player) {
            armor = 20.0f;
            toughness = 12.0f;
        } else if (!(ent instanceof net.arna.jcraft.api.stand.StandEntity) && net.arna.jcraft.common.util.JUtils.getStand(ent) == null) {
            modified *= net.arna.jcraft.common.config.JServerConfig.VS_STANDLESS_DAMAGE_MULTIPLIER.getValue();
        }

        modified = net.arna.jcraft.common.util.JUtils.getDamageThroughArmor(modified, armor * 0.9f, toughness * 0.9f);
        modified = ((net.arna.jcraft.mixin.LivingEntityInvoker) ent).invokeModifyAppliedDamage(damageSource, modified);

        Attacks.applyAbsorptionAndStats(modified, damageSource, ent);
    }
}
