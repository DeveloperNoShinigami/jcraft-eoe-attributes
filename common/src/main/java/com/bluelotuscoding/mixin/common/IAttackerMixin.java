package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.arna.jcraft.api.attack.IAttacker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Class: IAttacker
 * Stand: Any
 * Purpose: Provides accessor methods for attribute-based combat modifiers.
 */
@Mixin(value = IAttacker.class, remap = false)
public interface IAttackerMixin {

    @Overwrite
    default double getEngagementDistance() {
        LivingEntity user = null;
        try {
            // Reflect to avoid return type mismatch in production descriptors
            java.lang.reflect.Method m = this.getClass().getMethod("getUser");
            Object result = m.invoke(this);
            if (result instanceof LivingEntity) {
                user = (LivingEntity) result;
            }
        } catch (Exception ignored) {}

        double base = 6.0;
        if (user != null) {
            AttributeInstance attr = user.getAttribute(JAttributeRegistry.ENGAGEMENT_DISTANCE);
            if (attr != null) {
                return base + attr.getValue();
            }
        }
        return base;
    }
}
