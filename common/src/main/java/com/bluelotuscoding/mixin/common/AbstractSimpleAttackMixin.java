package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.arna.jcraft.api.attack.IAttacker;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

/**
 * Class: AbstractSimpleAttack
 * Purpose: Applies Life Steal logic to all hits using the LIFE_STEAL attribute.
 */
@Mixin(value = AbstractSimpleAttack.class, remap = false)
public abstract class AbstractSimpleAttackMixin {

    @Shadow public abstract float getDamage();

    @Inject(method = "perform(Lnet/arna/jcraft/api/attack/IAttacker;Lnet/minecraft/class_1309;)Ljava/util/Set;", at = @At("RETURN"), require = 0)
    private void jcraft_attributes$applyLifeSteal(@Coerce Object attacker, LivingEntity user,
            CallbackInfoReturnable<Set<LivingEntity>> cir) {
        Set<LivingEntity> targets = cir.getReturnValue();
        if (targets != null && !targets.isEmpty()) {
            LivingEntity actualUser = user;
            if (attacker instanceof IAttacker<?, ?> ia) {
                actualUser = ia.getUser();
            }
            AttributeInstance lifeStealAttr = actualUser.getAttribute(JAttributeRegistry.LIFE_STEAL);
            if (lifeStealAttr != null && lifeStealAttr.getValue() > 0) {
                float damage = this.getDamage();
                float totalDamage = damage * targets.size();
                float healAmount = (float) (totalDamage * lifeStealAttr.getValue());
                if (healAmount > 0) {
                    actualUser.heal(healAmount);
                }
            }
        }
    }
}
