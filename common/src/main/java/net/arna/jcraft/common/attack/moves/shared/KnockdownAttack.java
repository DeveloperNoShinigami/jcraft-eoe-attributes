package net.arna.jcraft.common.attack.moves.shared;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.moves.base.AbstractEffectInflictingAttack;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import java.util.List;

@Getter
public final class KnockdownAttack<A extends IAttacker<? extends A, ?>> extends AbstractEffectInflictingAttack<KnockdownAttack<A>, A> {
    private final int knockdownDuration;

    public KnockdownAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                           final float hitboxSize, final float knockback, final float offset, final int knockdownDuration) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset,
                List.of(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), knockdownDuration, 0)));
        this.knockdownDuration = knockdownDuration;
    }

    @Override
    protected @NonNull KnockdownAttack<A> getThis() {
        return this;
    }

    @Override
    public @NonNull KnockdownAttack<A> copy() {
        return copyExtras(new KnockdownAttack<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getKnockdownDuration()));
    }
}
