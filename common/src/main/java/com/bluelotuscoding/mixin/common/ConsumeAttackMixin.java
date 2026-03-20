package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

/**
 * Class: ConsumeAttack (Cream)
 * Stand: Cream only
 * Purpose: Temporarily expands hitboxSize by ERASURE_SIZE for the duration of perform(),
 *          then restores the exact bonus — avoiding permanent field mutation on repeated activations.
 *
 * hitboxSize is private in AbstractSimpleAttack (not in ConsumeAttack directly), so @Shadow
 * cannot reach it. Instead we use reflection traversing the class hierarchy, tracking only
 * the delta applied so we can precisely undo it at RETURN.
 */
@Mixin(targets = "net.arna.jcraft.common.attack.moves.cream.ConsumeAttack", remap = false)
public abstract class ConsumeAttackMixin {

    // Tracks the bonus we added at HEAD so we can subtract it exactly at RETURN
    @Unique private float jcraft_attributes$appliedBonus = 0f;

    @Inject(method = "perform(Lnet/arna/jcraft/common/entity/stand/CreamEntity;Lnet/minecraft/class_1309;)Ljava/util/Set;", at = @At("HEAD"), remap = false)
    private void jcraft_attributes$expandHitbox(@Coerce Object attacker, @Coerce Object userObj, CallbackInfoReturnable<?> cir) {
        if (!(userObj instanceof LivingEntity user)) return;
        AttributeInstance attr = user.getAttribute(JAttributeRegistry.ERASURE_SIZE);
        if (attr == null || attr.getValue() == 0) return;
        float bonus = (float) attr.getValue();
        try {
            Field field = jcraft_attributes$findField(this.getClass(), "hitboxSize");
            float current = field.getFloat(this);
            field.setFloat(this, current + bonus);
            jcraft_attributes$appliedBonus = bonus;
        } catch (Exception ignored) {}
    }

    @Inject(method = "perform(Lnet/arna/jcraft/common/entity/stand/CreamEntity;Lnet/minecraft/class_1309;)Ljava/util/Set;", at = @At("RETURN"), remap = false)
    private void jcraft_attributes$restoreHitbox(@Coerce Object attacker, @Coerce Object userObj, CallbackInfoReturnable<?> cir) {
        if (jcraft_attributes$appliedBonus == 0f) return;
        try {
            Field field = jcraft_attributes$findField(this.getClass(), "hitboxSize");
            float current = field.getFloat(this);
            field.setFloat(this, current - jcraft_attributes$appliedBonus);
        } catch (Exception ignored) {}
        jcraft_attributes$appliedBonus = 0f;
    }

    // Walks the class hierarchy to find a field by name — handles private fields in parent classes
    @Unique
    private static Field jcraft_attributes$findField(Class<?> cls, String name) throws NoSuchFieldException {
        while (cls != null && cls != Object.class) {
            try {
                Field f = cls.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                cls = cls.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }
}
