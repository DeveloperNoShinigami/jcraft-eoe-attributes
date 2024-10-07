package net.arna.jcraft.common.attack.moves.horus;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.arna.jcraft.common.attack.moves.base.AbstractBarrageAttack;
import net.arna.jcraft.common.entity.stand.HorusEntity;

public final class HorusBarrageAttack extends AbstractBarrageAttack<HorusBarrageAttack, HorusEntity> {

    public HorusBarrageAttack(final int cooldown, final int windup, final int duration, final float attackDistance, final float damage, final int stun, final float hitboxSize, final float knockback, final float offset, final int interval) {
        super(cooldown, windup, duration, attackDistance, damage, stun, hitboxSize, knockback, offset, interval);
        withHoldable();
    }

    @Override
    public void onUserMoveInput(final HorusEntity attacker, final MoveInputType type, final boolean pressed, final boolean moveInitiated) {
        super.onUserMoveInput(attacker, type, pressed, moveInitiated);
        // Must be held
        if (type.getMoveType() == getMoveType() && !pressed) attacker.cancelMove();
    }

    @Override
    protected @NonNull HorusBarrageAttack getThis() {
        return this;
    }

    @Override
    public @NonNull HorusBarrageAttack copy() {
        return copyExtras(new HorusBarrageAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getInterval()));
    }
}
