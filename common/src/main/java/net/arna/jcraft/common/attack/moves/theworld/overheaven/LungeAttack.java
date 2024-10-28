package net.arna.jcraft.common.attack.moves.theworld.overheaven;

import lombok.NonNull;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.attack.moves.shared.KnockdownAttack;
import net.arna.jcraft.common.entity.stand.StandEntity;

public final class LungeAttack extends AbstractSimpleAttack<LungeAttack, StandEntity<?,?>> {
    private final float originalMoveDistance;
    private final int beginMoveStun;
    private final int endMoveStun;

    public LungeAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                       final float hitboxSize, final float knockback, final float offset, final int beginMoveStun, final int endMoveStun) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        originalMoveDistance = moveDistance;
        this.beginMoveStun = beginMoveStun;
        this.endMoveStun = endMoveStun;
        if (endMoveStun >= beginMoveStun) throw new IllegalStateException("End movestun must be smaller than starting!");
        this.ranged = true;
    }

    @Override
    public void onInitiate(final StandEntity<?,?> attacker) {
        super.onInitiate(attacker);

        // Reset move distance
        withMoveDistance(originalMoveDistance);
    }

    @Override
    public void tick(final StandEntity<?,?> attacker, final int moveStun) {
        super.tick(attacker, moveStun);
        if (moveStun > endMoveStun && moveStun <= beginMoveStun) {
            withMoveDistance(getMoveDistance() + 0.15f);
        }
    }

    @SuppressWarnings("unchecked")
    public LungeAttack withCrouchingVariant(final KnockdownAttack<? extends StandEntity<?,?>> crouchingVariant) {
        return super.withCrouchingVariant((AbstractMove<?, ? super StandEntity<?, ?>>)crouchingVariant);
    }

    @Override
    protected @NonNull LungeAttack getThis() {
        return this;
    }

    @Override
    public @NonNull LungeAttack copy() {
        return copyExtras(new LungeAttack(getCooldown(), getWindup(), getDuration(), originalMoveDistance, getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset(), beginMoveStun, endMoveStun));
    }
}
