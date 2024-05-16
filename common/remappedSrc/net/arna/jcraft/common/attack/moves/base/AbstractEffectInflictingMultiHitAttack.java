package net.arna.jcraft.common.attack.moves.base;

import it.unimi.dsi.fastutil.ints.IntCollection;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AbstractEffectInflictingMultiHitAttack<T extends AbstractEffectInflictingMultiHitAttack<T, A>, A extends IAttacker<? extends A, ?>>
        extends AbstractMultiHitAttack<T, A> {
    private final List<MobEffectInstance> effects = new ArrayList<>();

    protected AbstractEffectInflictingMultiHitAttack(int cooldown, int duration, float moveDistance, float damage, int stun,
                                                     float hitboxSize, float knockback, float offset,
                                                     @NonNull IntCollection hitMoments, @NonNull List<MobEffectInstance> effects) {
        super(cooldown, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitMoments);
        this.effects.addAll(effects);
    }

    @Override
    protected void processTarget(A attacker, LivingEntity target, Vec3 kbVec, DamageSource damageSource) {
        super.processTarget(attacker, target, kbVec, damageSource);

        AbstractEffectInflictingAttack.inflictEffects(target, effects, getBlockableType().isNonBlockableEffects());
    }
}
