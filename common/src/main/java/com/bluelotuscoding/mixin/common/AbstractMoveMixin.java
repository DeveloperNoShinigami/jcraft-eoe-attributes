package com.bluelotuscoding.mixin.common;

import com.bluelotuscoding.api.registry.JAttributeRegistry;
import com.bluelotuscoding.util.MoveContext;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.api.attack.moves.AbstractSimpleAttack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Class: AbstractMove
 * Stand: Any
 * Purpose: Appends attribute-aware total statistics to move descriptions.
 */
@Mixin(targets = "net.arna.jcraft.api.attack.moves.AbstractMove", remap = false)
public abstract class AbstractMoveMixin {

    @Inject(method = "getDescription()Lnet/minecraft/class_2561;", at = @At("RETURN"), cancellable = true, remap = false)
    private void jcraft_attributes$appendAttributeStats(CallbackInfoReturnable<Component> cir) {
        LivingEntity user = MoveContext.getPlayer();
        if (user == null) return;

        AbstractMove<?, ?> move = (AbstractMove<?, ?>) (Object) this;
        int baseCooldown = move.getCooldown();
        int baseDuration = move.getDuration();
        float baseReach  = move.getMoveDistance();
        float baseDamage = ((Object) this instanceof AbstractSimpleAttack<?, ?> atk) ? atk.getDamage() : 0f;

        MutableComponent statsText = Component.empty();
        boolean hasStats = false;

        // 1. Cooldown Calculation
        AttributeInstance cdrAttr = user.getAttribute(JAttributeRegistry.COOLDOWN_REDUCTION);
        if (cdrAttr != null && cdrAttr.getValue() != 0 && baseCooldown > 0) {
            double totalCooldown = baseCooldown * (1.0 - cdrAttr.getValue());
            statsText.append(Component.literal("\n"))
                    .append(Component.literal("Total Cooldown: ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(String.format("%.1fs", totalCooldown / 20.0)).withStyle(ChatFormatting.AQUA));
            hasStats = true;
        }

        // 2. Duration Calculation
        double durMult = 1.0;
        AttributeInstance multAttr = user.getAttribute(JAttributeRegistry.DURATION_MULTIPLIER);
        if (multAttr != null) durMult = multAttr.getValue();

        double durationBonus = 0;
        String className = this.getClass().getSimpleName();
        if (className.contains("TimeStop")) {
            AttributeInstance tsAttr = user.getAttribute(JAttributeRegistry.TIME_STOP_DURATION);
            if (tsAttr != null) durationBonus = tsAttr.getValue();
        } else if (className.contains("TimeAcceleration")) {
            AttributeInstance taAttr = user.getAttribute(JAttributeRegistry.ACCEL_DURATION);
            if (taAttr != null) durationBonus = taAttr.getValue();
        } else if (className.contains("TimeErase") || className.contains("Consume")) {
            AttributeInstance teAttr = user.getAttribute(JAttributeRegistry.ERASURE_DURATION);
            if (teAttr != null) durationBonus = teAttr.getValue();
        }

        if (baseDuration > 0 && (durMult != 1.0 || durationBonus != 0)) {
            double totalDuration = (baseDuration * durMult) + durationBonus;
            statsText.append(Component.literal("\n"))
                    .append(Component.literal("Total Duration: ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(String.format("%.1fs", totalDuration / 20.0)).withStyle(ChatFormatting.YELLOW));
            hasStats = true;
        }

        // 3. Reach Calculation
        AttributeInstance reachAttr = user.getAttribute(JAttributeRegistry.ATTACK_RANGE_BONUS);
        if (reachAttr != null && reachAttr.getValue() != 0 && baseReach > 0) {
            double totalReach = baseReach + reachAttr.getValue();
            statsText.append(Component.literal("\n"))
                    .append(Component.literal("Total Reach: ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(String.format("%.1fm", totalReach)).withStyle(ChatFormatting.GREEN));
            hasStats = true;
        }

        // 4. Damage Calculation
        AttributeInstance sdAttr = user.getAttribute(JAttributeRegistry.STAND_DAMAGE);
        double sdBonus = (sdAttr != null) ? sdAttr.getValue() : 0.0;
        if (baseDamage > 0 && sdBonus != 0) {
            double totalDamage = baseDamage + sdBonus;
            statsText.append(Component.literal("\n"))
                    .append(Component.literal("Total Damage: ").withStyle(ChatFormatting.GRAY))
                    .append(Component.literal(String.format("%.1f", totalDamage)).withStyle(ChatFormatting.RED));
            hasStats = true;
        }

        if (hasStats) {
            Component original = cir.getReturnValue();
            MutableComponent newDescription = original.copy();
            newDescription.append(statsText);
            cir.setReturnValue(newDescription);
        }
    }
}
