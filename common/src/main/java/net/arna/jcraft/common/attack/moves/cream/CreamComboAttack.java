package net.arna.jcraft.common.attack.moves.cream;

import it.unimi.dsi.fastutil.ints.IntCollection;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMultiHitAttack;
import net.arna.jcraft.common.entity.stand.CreamEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public final class CreamComboAttack extends AbstractMultiHitAttack<CreamComboAttack, CreamEntity> {
    public CreamComboAttack(final int cooldown, final int duration, final float moveDistance, final float damage, final int stun,
                            final float hitboxSize, final float knockback, final float offset,
                            final @NonNull IntCollection hitMoments) {
        super(cooldown, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitMoments);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final CreamEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        if (getBlow(attacker) == 2) {
            final Vec3 rV = getRotVec(attacker);

            for (LivingEntity target : targets) {
                target.knockback(1, rV.x, rV.z);
                target.hurtMarked = true;
            }
        }

        return targets;
    }

    @Override
    protected @NonNull CreamComboAttack getThis() {
        return this;
    }

    @Override
    public @NonNull CreamComboAttack copy() {
        return copyExtras(new CreamComboAttack(getCooldown(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset(), getHitMoments()));
    }
}
