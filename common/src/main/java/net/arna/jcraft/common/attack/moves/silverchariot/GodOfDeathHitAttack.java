package net.arna.jcraft.common.attack.moves.silverchariot;

import it.unimi.dsi.fastutil.ints.IntCollection;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMultiHitAttack;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class GodOfDeathHitAttack extends AbstractMultiHitAttack<GodOfDeathHitAttack, SilverChariotEntity> {
    public GodOfDeathHitAttack(final int cooldown, final int duration, final float moveDistance, final float damage, final int stun,
                               final float hitboxSize, final float knockback, final float offset, final IntCollection hitMoments) {
        super(cooldown, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitMoments);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final SilverChariotEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        if (getBlow(attacker) == 1) {
            attacker.setCurrentMove(getFollowup());
        }

        return targets;
    }

    @Override
    protected @NonNull GodOfDeathHitAttack getThis() {
        return this;
    }

    @Override
    public @NonNull GodOfDeathHitAttack copy() {
        return copyExtras(new GodOfDeathHitAttack(getCooldown(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getHitMoments()));
    }
}
