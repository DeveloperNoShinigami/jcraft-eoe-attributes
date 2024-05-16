package net.arna.jcraft.common.effects;

import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

// KNOCKDOWN prevents attacking, and sets you into a horizontal pose
public class KnockdownStatusEffect extends StatusEffect {

    public KnockdownStatusEffect() {
        super(StatusEffectCategory.NEUTRAL, 0x444444);
        this.addAttributeModifier(
                EntityAttributes.GENERIC_ARMOR,
                "BB2CA307-EEA6-B54C-B324-F7EB036289BF",
                30.0,
                EntityAttributeModifier.Operation.ADDITION
        ).addAttributeModifier(
                EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
                "3B9E3E15-2B1A-13F6-73D8-AB84287E7DF0",
                20.0,
                EntityAttributeModifier.Operation.ADDITION
        ).addAttributeModifier(
                EntityAttributes.GENERIC_MOVEMENT_SPEED,
                "ADA0E470-7D4D-DF4E-DDB2-A1858C84236C",
                0.0,
                EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration <= 5;
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        if (entity.hasVehicle()) {
            entity.stopRiding();
        }
        entity.setPose(entity instanceof PlayerEntity ? EntityPose.SWIMMING : EntityPose.SLEEPING);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
        entity.setPose(EntityPose.STANDING);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {// 5 tick (0.25s) stun immunity window after knockdown
        entity.removeStatusEffect(JStatusRegistry.DAZED.get());
    }
}
