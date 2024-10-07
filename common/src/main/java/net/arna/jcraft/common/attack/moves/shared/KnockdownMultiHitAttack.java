package net.arna.jcraft.common.attack.moves.shared;

import it.unimi.dsi.fastutil.ints.IntCollection;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.moves.base.AbstractEffectInflictingMultiHitAttack;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import java.util.List;

@Getter
public final class KnockdownMultiHitAttack<A extends IAttacker<? extends A, ?>> extends AbstractEffectInflictingMultiHitAttack<KnockdownMultiHitAttack<A>, A> {
    private final int knockdownDuration;

    public KnockdownMultiHitAttack(final int cooldown, final int duration, final float attackDistance, final float damage, final int stun, final float hitboxSize,
                                   final float knockback, final float offset, final @NonNull IntCollection hitMoments, final int knockdownDuration) {
        super(cooldown, duration, attackDistance, damage, stun, hitboxSize, knockback, offset, hitMoments,
                List.of(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), knockdownDuration, 0, true, false)));
        this.knockdownDuration = knockdownDuration;
    }

    @Override
    protected @NonNull KnockdownMultiHitAttack<A> getThis() {
        return this;
    }

    @Override
    public @NonNull KnockdownMultiHitAttack<A> copy() {
        return copyExtras(new KnockdownMultiHitAttack<>(getCooldown(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getHitMoments(), knockdownDuration));
    }
}
