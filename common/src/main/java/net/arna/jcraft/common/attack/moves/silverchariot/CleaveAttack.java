package net.arna.jcraft.common.attack.moves.silverchariot;

import lombok.NonNull;
import net.arna.jcraft.common.attack.moves.base.AbstractChargeAttack;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.arna.jcraft.common.util.JParticleType;
import net.minecraft.world.entity.LivingEntity;

public final class CleaveAttack extends AbstractSimpleAttack<CleaveAttack, SilverChariotEntity> {
    public CleaveAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                        final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        hitSpark = JParticleType.HIT_SPARK_2;
    }

    @Override
    public void onInitiate(final SilverChariotEntity attacker) {
        super.onInitiate(attacker);

        final LivingEntity user = attacker.getUserOrThrow();
        AbstractChargeAttack.prepDetachmentMove(attacker, user);
        attacker.setFreePos((user.position().add(attacker.getUserOrThrow().getLookAngle().scale(1.5)).toVector3f()));
        attacker.setFree(true);
    }

    @Override
    protected @NonNull CleaveAttack getThis() {
        return this;
    }

    @Override
    public @NonNull CleaveAttack copy() {
        return copyExtras(new CleaveAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset()));
    }
}
