package net.arna.jcraft.common.attack.moves.silverchariot;

import lombok.NonNull;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.arna.jcraft.common.attack.MobilityType;
import net.minecraft.world.entity.LivingEntity;

public final class RayDartAttack extends AbstractSimpleAttack<RayDartAttack, SilverChariotEntity> {
    public RayDartAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                         final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        ranged = true;
        mobilityType = MobilityType.DASH;
    }

    @Override
    public void onInitiate(final SilverChariotEntity attacker) {
        super.onInitiate(attacker);

        final LivingEntity user = attacker.getUser();
        if (user != null && user.onGround()) {
            user.setDeltaMovement(user.getDeltaMovement().add(getRotVec(attacker).scale(1)));
            user.hurtMarked = true;
        }
    }

    @Override
    protected @NonNull RayDartAttack getThis() {
        return this;
    }

    @Override
    public @NonNull RayDartAttack copy() {
        return copyExtras(new RayDartAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }
}
