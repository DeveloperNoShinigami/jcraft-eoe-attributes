package net.arna.jcraft.common.attack.moves.base;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AbstractEffectInflictingAttack<T extends AbstractEffectInflictingAttack<T, A>, A extends IAttacker<? extends A, ?>>
        extends AbstractSimpleAttack<T, A> {
    private final List<MobEffectInstance> effects = new ArrayList<>();

    protected AbstractEffectInflictingAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                                             final float hitboxSize, final float knockback, final float offset, final @NonNull List<MobEffectInstance> effects) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        this.effects.addAll(effects);
    }

    @Override
    protected void processTarget(final A attacker, final LivingEntity target, final Vec3 kbVec, final DamageSource damageSource) {
        super.processTarget(attacker, target, kbVec, damageSource);

        inflictEffects(target, effects, getBlockableType().isNonBlockableEffects());
    }

    static void inflictEffects(final LivingEntity target, final List<MobEffectInstance> effects, final boolean nonBlockableEffects) {
        if (nonBlockableEffects || !JUtils.isBlocking(target)) {
            effects.forEach(effect -> target.addEffect(new MobEffectInstance(effect)));
        }
    }
}
