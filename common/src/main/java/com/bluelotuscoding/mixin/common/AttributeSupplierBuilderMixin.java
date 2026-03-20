package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Class: AttributeSupplier.Builder
 * Stand: Any
 * Purpose: Dynamically registers custom attributes to common entity types.
 */
import java.util.Map;

@Mixin(value = net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder.class)
public class AttributeSupplierBuilderMixin {
    @Shadow @Final private Map<Attribute, AttributeInstance> builder;

    @Inject(method = "build", at = @At("HEAD"))
    private void jcraft_attributes$onBuild(CallbackInfoReturnable<AttributeSupplier> cir) {
        System.out.println("[JCraft Attributes] Injecting attributes into Supplier...");
        if (!this.builder.containsKey(JAttributeRegistry.STAND_DAMAGE)) {
            this.builder.put(JAttributeRegistry.STAND_DAMAGE, new AttributeInstance(JAttributeRegistry.STAND_DAMAGE, (inst) -> {}));
        }
        if (!this.builder.containsKey(JAttributeRegistry.STAND_RESISTANCE)) {
            this.builder.put(JAttributeRegistry.STAND_RESISTANCE, new AttributeInstance(JAttributeRegistry.STAND_RESISTANCE, (inst) -> {}));
        }
        if (!this.builder.containsKey(JAttributeRegistry.STAND_GAUGE_MAX)) {
            this.builder.put(JAttributeRegistry.STAND_GAUGE_MAX, new AttributeInstance(JAttributeRegistry.STAND_GAUGE_MAX, (inst) -> {}));
        }

        // Global Stand Stats
        inject(JAttributeRegistry.IDLE_DISTANCE);
        inject(JAttributeRegistry.IDLE_ROTATION);
        inject(JAttributeRegistry.BLOCK_DISTANCE);
        inject(JAttributeRegistry.ENGAGEMENT_DISTANCE);
        inject(JAttributeRegistry.ALPHA_OVERRIDE);

        // Core Move Stats
        inject(JAttributeRegistry.COOLDOWN_REDUCTION);
        inject(JAttributeRegistry.WINDUP_REDUCTION);
        inject(JAttributeRegistry.DURATION_MULTIPLIER);
        inject(JAttributeRegistry.MOVE_DISTANCE_MULTIPLIER);
        inject(JAttributeRegistry.ARMOR_BONUS);
        inject(JAttributeRegistry.CHARGE_DISTANCE_MULTIPLIER);
        inject(JAttributeRegistry.LIFE_STEAL);
        inject(JAttributeRegistry.KNOCKBACK_MODIFIER);
        inject(JAttributeRegistry.BLOCK_STUN_REDUCTION);
        inject(JAttributeRegistry.ATTACK_RANGE_BONUS);

        // Specialized Ability Stats
        inject(JAttributeRegistry.TIME_STOP_DURATION);
        inject(JAttributeRegistry.ACCEL_DURATION);
        inject(JAttributeRegistry.ERASURE_DURATION);
        inject(JAttributeRegistry.REWIND_REACH);
        inject(JAttributeRegistry.ERASURE_REACH);
    }

    @Unique
    private void inject(Attribute attribute) {
        if (!this.builder.containsKey(attribute)) {
            this.builder.put(attribute, new AttributeInstance(attribute, (inst) -> {}));
        }
    }
}
