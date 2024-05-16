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

    protected AbstractEffectInflictingAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                                             float hitboxSize, float knockback, float offset, @NonNull List<MobEffectInstance> effects) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        this.effects.addAll(effects);
    }

    @Override
    protected void processTarget(A attacker, LivingEntity target, Vec3 kbVec, DamageSource damageSource) {
        super.processTarget(attacker, target, kbVec, damageSource);

        inflictEffects(target, effects, getBlockableType().isNonBlockableEffects());
    }

    static void inflictEffects(LivingEntity target, List<MobEffectInstance> effects, boolean nonBlockableEffects) {
        if (nonBlockableEffects || !JUtils.isBlocking(target)) {
            effects.forEach(effect -> target.addEffect(new MobEffectInstance(effect)));
        }
    }
}
