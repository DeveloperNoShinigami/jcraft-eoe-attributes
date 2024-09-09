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

    public LungeAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                       float hitboxSize, float knockback, float offset, int beginMoveStun, int endMoveStun) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        originalMoveDistance = moveDistance;
        this.beginMoveStun = beginMoveStun;
        this.endMoveStun = endMoveStun;
        if (endMoveStun >= beginMoveStun) throw new IllegalStateException("End movestun must be smaller than starting!");
        this.ranged = true;
    }

    @Override
    public void onInitiate(StandEntity<?,?> attacker) {
        super.onInitiate(attacker);

        // Reset move distance
        withMoveDistance(originalMoveDistance);
    }

    @Override
    public void tick(StandEntity<?,?> attacker) {
        super.tick(attacker);

        int moveStun = attacker.getMoveStun();
        if (moveStun > endMoveStun && moveStun <= beginMoveStun) {
            withMoveDistance(getMoveDistance() + 0.15f);
        }
    }

    public LungeAttack withCrouchingVariant(KnockdownAttack<? extends StandEntity<?,?>> crouchingVariant) {
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
