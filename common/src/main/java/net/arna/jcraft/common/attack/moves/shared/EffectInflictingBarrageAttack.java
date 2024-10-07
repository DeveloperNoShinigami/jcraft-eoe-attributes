package net.arna.jcraft.common.attack.moves.shared;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.moves.base.AbstractEffectInflictingBarrageAttack;
import net.minecraft.world.effect.MobEffectInstance;
import java.util.List;

@Getter
public final class EffectInflictingBarrageAttack<A extends IAttacker<? extends A, ?>> extends AbstractEffectInflictingBarrageAttack<EffectInflictingBarrageAttack<A>, A> {
    public EffectInflictingBarrageAttack(final int cooldown, final int windup, final int duration, final float attackDistance, final float damage,
                                         final int stun, final float hitboxSize, final float knockback, final float offset, final int interval,
                                         final @NonNull List<MobEffectInstance> effects) {
        super(cooldown, windup, duration, attackDistance, damage, stun, hitboxSize, knockback, offset, interval, effects);
    }

    @Override
    protected @NonNull EffectInflictingBarrageAttack<A> getThis() {
        return this;
    }

    @Override
    public @NonNull EffectInflictingBarrageAttack<A> copy() {
        return copyExtras(new EffectInflictingBarrageAttack<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(),
                getDamage(), getStun(), getHitboxSize(), getKnockback(), getOffset(), getInterval(), getEffects()));
    }
}
