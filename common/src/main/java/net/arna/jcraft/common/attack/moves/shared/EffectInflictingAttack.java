package net.arna.jcraft.common.attack.moves.shared;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.moves.base.AbstractEffectInflictingAttack;
import net.minecraft.world.effect.MobEffectInstance;
import java.util.List;

@Getter
public final class EffectInflictingAttack<A extends IAttacker<? extends A, ?>> extends AbstractEffectInflictingAttack<EffectInflictingAttack<A>, A> {
    public EffectInflictingAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                                  final float hitboxSize, final float knockback, final float offset, final @NonNull List<MobEffectInstance> effects) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, effects);
    }

    @Override
    protected @NonNull EffectInflictingAttack<A> getThis() {
        return this;
    }

    @Override
    public @NonNull EffectInflictingAttack<A> copy() {
        return copyExtras(new EffectInflictingAttack<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getEffects()));
    }
}
