package net.arna.jcraft.common.attack.moves.killerqueen;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractGrabAttack;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.KillerQueenEntity;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class KQGrabAttack extends AbstractGrabAttack<KQGrabAttack, KillerQueenEntity, KillerQueenEntity.State> {
    public KQGrabAttack(final int cooldown, final int windup, final int duration, final float attackDistance, final float damage, final int stun, final float hitboxSize,
                        final float knockback, final float offset, final AbstractMove<?, ? super KillerQueenEntity> hitMove, final KillerQueenEntity.State hitState) {
        super(cooldown, windup, duration, attackDistance, damage, stun, hitboxSize, knockback, offset, hitMove, hitState);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final KillerQueenEntity attacker, final LivingEntity user, final MoveContext ctx) {
        Set<LivingEntity> targets = super.perform(attacker, user, ctx);
        targets.stream().findFirst().ifPresent(JComponentPlatformUtils.getBombTracker(user).getMainBomb()::setBomb);
        return targets;
    }

    @Override
    protected @NonNull KQGrabAttack getThis() {
        return this;
    }

    @Override
    public @NonNull KQGrabAttack copy() {
        return copyExtras(new KQGrabAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getHitMove(), getHitState()));
    }
}
