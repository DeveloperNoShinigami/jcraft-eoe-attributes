package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.arna.jcraft.common.entity.stand.KingCrimsonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Class: TimeEraseMove
 * Stand: King Crimson
 * Purpose: Modifies the duration of time erasure using the ERASURE_DURATION attribute.
 */
@Mixin(targets = "net.arna.jcraft.common.attack.moves.kingcrimson.TimeEraseMove", remap = false)
public abstract class TimeEraseMoveMixin {

    @Inject(method = "perform(Lnet/arna/jcraft/common/entity/stand/KingCrimsonEntity;Lnet/minecraft/class_1309;)Ljava/util/Set;", at = @At("RETURN"), remap = false)
    private void jcraft_attributes$modifyEraseDuration(KingCrimsonEntity attacker, @Coerce Object userObj, CallbackInfoReturnable<?> cir) {
        if (userObj instanceof LivingEntity user) {
            AttributeInstance attr = user.getAttribute(JAttributeRegistry.ERASURE_DURATION);
            if (attr != null && attr.getValue() != 0)
                attacker.setTETime(attacker.getTETime() + (int) attr.getValue());
        }
    }
}
