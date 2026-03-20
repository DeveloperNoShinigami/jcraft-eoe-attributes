package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Class: ConsumeAttack (Cream)
 * Stand: Cream only
 * Purpose: Temporarily expands hitboxSize by ERASURE_SIZE for the duration of perform(),
 *          then restores the exact bonus — avoiding permanent field mutation on repeated activations.
 */
@Mixin(targets = "net.arna.jcraft.common.attack.moves.cream.ConsumeAttack", remap = false)
public abstract class ConsumeAttackMixin {

    @Unique private float jcraft_attributes$appliedBonus = 0f;

    @Inject(method = "perform(Lnet/arna/jcraft/common/entity/stand/CreamEntity;Lnet/minecraft/class_1309;)Ljava/util/Set;", at = @At("HEAD"), remap = false)
    private void jcraft_attributes$expandHitbox(@Coerce Object attacker, @Coerce Object userObj, CallbackInfoReturnable<?> cir) {
        if (!(userObj instanceof LivingEntity user)) return;
        AttributeInstance attr = user.getAttribute(JAttributeRegistry.ERASURE_SIZE);
        if (attr == null || attr.getValue() == 0) return;
        float bonus = (float) attr.getValue();
        AbstractSimpleAttack<?, ?> attack = (AbstractSimpleAttack<?, ?>) (Object) this;
        attack.withHitboxSize(attack.getHitboxSize() + bonus);
        jcraft_attributes$appliedBonus = bonus;
    }

    @Inject(method = "perform(Lnet/arna/jcraft/common/entity/stand/CreamEntity;Lnet/minecraft/class_1309;)Ljava/util/Set;", at = @At("RETURN"), remap = false)
    private void jcraft_attributes$restoreHitbox(@Coerce Object attacker, @Coerce Object userObj, CallbackInfoReturnable<?> cir) {
        if (jcraft_attributes$appliedBonus == 0f) return;
        AbstractSimpleAttack<?, ?> attack = (AbstractSimpleAttack<?, ?>) (Object) this;
        attack.withHitboxSize(attack.getHitboxSize() - jcraft_attributes$appliedBonus);
        jcraft_attributes$appliedBonus = 0f;
    }
}
