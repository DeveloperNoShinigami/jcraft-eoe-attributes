package net.arna.jcraft.common.attack.moves.thefool;

import lombok.NonNull;
import net.arna.jcraft.common.attack.moves.base.AbstractBarrageAttack;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.minecraft.world.entity.LivingEntity;

public final class AirBarrageAttack extends AbstractBarrageAttack<AirBarrageAttack, TheFoolEntity> {
    public AirBarrageAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                            final float hitboxSize, final float knockback, final float offset, final int interval) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, interval);
    }

    @Override
    public void tick(final TheFoolEntity attacker, final int moveStun) {
        super.tick(attacker, moveStun);

        if (!attacker.hasUser()) {
            return;
        }

        final LivingEntity user = attacker.getUserOrThrow();
        user.setDeltaMovement(user.getDeltaMovement().scale(0.5).add(0, 0.01, 0));
        user.hurtMarked = true;
    }

    @Override
    protected @NonNull AirBarrageAttack getThis() {
        return this;
    }

    @Override
    public @NonNull AirBarrageAttack copy() {
        return copyExtras(new AirBarrageAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(),
                getDamage(), getStun(), getHitboxSize(), getKnockback(), getOffset(), getInterval()));
    }
}
