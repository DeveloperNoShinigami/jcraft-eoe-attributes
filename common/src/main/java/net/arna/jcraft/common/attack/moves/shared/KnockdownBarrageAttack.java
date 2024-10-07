package net.arna.jcraft.common.attack.moves.shared;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.moves.base.AbstractEffectInflictingBarrageAttack;
import net.arna.jcraft.registry.JStatusRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import java.util.List;

@Getter
public final class KnockdownBarrageAttack<A extends IAttacker<? extends A, ?>> extends AbstractEffectInflictingBarrageAttack<KnockdownBarrageAttack<A>, A> {
    private final int knockdownDuration;

    public KnockdownBarrageAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                                  final float hitboxSize, final float knockback, final float offset, final int interval, final int knockdownDuration) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, interval,
                List.of(new MobEffectInstance(JStatusRegistry.KNOCKDOWN.get(), knockdownDuration, 0, true, false)));
        this.knockdownDuration = knockdownDuration;
    }

    @Override
    protected @NonNull KnockdownBarrageAttack<A> getThis() {
        return this;
    }

    @Override
    public @NonNull KnockdownBarrageAttack<A> copy() {
        return copyExtras(new KnockdownBarrageAttack<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(),
                getDamage(), getStun(), getHitboxSize(), getKnockback(), getOffset(), getInterval(), getKnockdownDuration()));
    }
}
