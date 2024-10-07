package net.arna.jcraft.common.attack.moves.dirtydeedsdonedirtcheap;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractGrabAttack;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.D4CEntity;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public class D4CGrabAttack extends AbstractGrabAttack<D4CGrabAttack, D4CEntity, D4CEntity.State> {
    public D4CGrabAttack(final int cooldown, final int windup, final int duration, final float attackDistance, final float damage, final int stun,
                         final float hitboxSize, final float knockback, final float offset, final AbstractMove<?, ? super D4CEntity> hitMove, final D4CEntity.State hitState, final int grabDuration, final double grabOffset) {
        super(cooldown, windup, duration, attackDistance, damage, stun, hitboxSize, knockback, offset, hitMove, hitState, grabDuration, grabOffset);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final D4CEntity attacker, final LivingEntity user, final MoveContext ctx) {
        Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        if (targets.isEmpty()) {
            attacker.getMainHandItem().shrink(1);
        }

        return targets;
    }

    @Override
    protected @NonNull D4CGrabAttack getThis() {
        return this;
    }

    @Override
    public @NonNull D4CGrabAttack copy() {
        return copyExtras(new D4CGrabAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset(), getHitMove(), getHitState(), getGrabDuration(), getGrabOffset()));
    }
}
