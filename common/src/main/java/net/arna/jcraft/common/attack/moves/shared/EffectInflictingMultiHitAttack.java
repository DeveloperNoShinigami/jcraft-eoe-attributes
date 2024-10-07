package net.arna.jcraft.common.attack.moves.shared;

import it.unimi.dsi.fastutil.ints.IntCollection;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.moves.base.AbstractEffectInflictingMultiHitAttack;
import net.minecraft.world.effect.MobEffectInstance;
import java.util.List;

@Getter
public final class EffectInflictingMultiHitAttack<A extends IAttacker<? extends A, ?>> extends AbstractEffectInflictingMultiHitAttack<EffectInflictingMultiHitAttack<A>, A> {
    public EffectInflictingMultiHitAttack(final int cooldown, final int duration, final float attackDistance, final float damage, final int stun,
                                          final float hitboxSize, final float knockback, final float offset,
                                          final @NonNull IntCollection hitMoments, final @NonNull List<MobEffectInstance> effects) {
        super(cooldown, duration, attackDistance, damage, stun, hitboxSize, knockback, offset, hitMoments, effects);
    }

    @Override
    protected @NonNull EffectInflictingMultiHitAttack<A> getThis() {
        return this;
    }

    @Override
    public @NonNull EffectInflictingMultiHitAttack<A> copy() {
        return copyExtras(new EffectInflictingMultiHitAttack<>(getCooldown(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getHitMoments(), getEffects()));
    }
}
