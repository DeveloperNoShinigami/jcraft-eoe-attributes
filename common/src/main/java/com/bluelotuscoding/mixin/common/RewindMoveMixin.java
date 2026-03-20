package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Class: RewindMove
 * Stand: Mandom only
 * Purpose: Extends the rewind reach using REWIND_REACH via @Redirect on the distance check.
 *
 * RewindMove.perform() line 57: JUtils.nullSafeDistanceSqr(entity, user) <= reach * reach
 * We redirect the nullSafeDistanceSqr call, returning a rescaled distance so the original
 * `<= reach * reach` comparison behaves as if reach were (reach + bonus).
 *
 * Rescaling formula: dist * (reach^2 / effectiveReach^2) <= reach^2
 *   is equivalent to: dist <= (reach + bonus)^2
 *
 * This never mutates the final `reach` field — safe across repeated activations.
 */
@Mixin(targets = "net.arna.jcraft.common.attack.moves.mandom.RewindMove", remap = false)
public abstract class RewindMoveMixin {

    // Shadow the private final reach field to read it without mutation
    @Shadow private int reach;

    @Redirect(
        method = "perform(Lnet/arna/jcraft/common/entity/stand/MandomEntity;Lnet/minecraft/class_1309;)Ljava/util/Set;",
        at = @At(value = "INVOKE",
                 target = "Lnet/arna/jcraft/common/util/JUtils;nullSafeDistanceSqr(Lnet/minecraft/class_1297;Lnet/minecraft/class_1297;)D"),
        remap = false
    )
    private double jcraft_attributes$scaleRewindDist(
            @Coerce Object e1, @Coerce Object e2,
            @Coerce Object attacker, @Coerce Object userObj) {
        double dist = net.arna.jcraft.common.util.JUtils.nullSafeDistanceSqr(
                (Entity) e1, (Entity) e2);

        if (!(userObj instanceof LivingEntity user)) return dist;
        AttributeInstance attr = user.getAttribute(JAttributeRegistry.REWIND_REACH);
        if (attr == null || attr.getValue() == 0) return dist;

        // Rescale dist so `dist <= reach*reach` behaves as `dist <= (reach+bonus)^2`
        double effectiveReach = this.reach + attr.getValue();
        if (effectiveReach <= 0) return dist;
        return dist * ((double)(this.reach * this.reach) / (effectiveReach * effectiveReach));
    }
}
