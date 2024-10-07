package net.arna.jcraft.common.effects;

import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

// KNOCKDOWN prevents attacking, and sets you into a horizontal pose
public class KnockdownStatusEffect extends MobEffect {

    public KnockdownStatusEffect() {
        super(MobEffectCategory.NEUTRAL, 0x444444);
        this.addAttributeModifier(
                Attributes.ARMOR,
                "BB2CA307-EEA6-B54C-B324-F7EB036289BF",
                30.0,
                AttributeModifier.Operation.ADDITION
        ).addAttributeModifier(
                Attributes.ARMOR_TOUGHNESS,
                "3B9E3E15-2B1A-13F6-73D8-AB84287E7DF0",
                20.0,
                AttributeModifier.Operation.ADDITION
        ).addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                "ADA0E470-7D4D-DF4E-DDB2-A1858C84236C",
                0.0,
                AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public boolean isDurationEffectTick(final int duration, final int amplifier) {
        return duration <= 5;
    }

    @Override
    public void addAttributeModifiers(final LivingEntity entity, final AttributeMap attributes, final int amplifier) {
        super.addAttributeModifiers(entity, attributes, amplifier);
        if (entity.isPassenger()) {
            entity.stopRiding();
        }
        entity.setPose(entity instanceof Player ? Pose.SWIMMING : Pose.SLEEPING);
    }

    @Override
    public void removeAttributeModifiers(final LivingEntity entity, final AttributeMap attributes, final int amplifier) {
        super.removeAttributeModifiers(entity, attributes, amplifier);
        entity.setPose(Pose.STANDING);
    }

    @Override
    public void applyEffectTick(final LivingEntity entity, final int amplifier) {// 5 tick (0.25s) stun immunity window after knockdown
        entity.removeEffect(JStatusRegistry.DAZED.get());
    }
}
