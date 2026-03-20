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

import java.util.Map;

/**
 * Class: AttributeSupplier.Builder
 * Stand: Any
 * Purpose: Dynamically registers custom attributes to common entity types.
 */
@Mixin(value = net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder.class)
public class AttributeSupplierBuilderMixin {
    @Shadow @Final private Map<Attribute, AttributeInstance> builder;

    @Inject(method = "build", at = @At("HEAD"))
    private void jcraft_attributes$onBuild(CallbackInfoReturnable<AttributeSupplier> cir) {
        for (JAttributeRegistry.AttributeEntry e : JAttributeRegistry.ALL) {
            if (!this.builder.containsKey(e.attribute())) {
                this.builder.put(e.attribute(), new AttributeInstance(e.attribute(), (inst) -> {}));
            }
        }
    }
}
