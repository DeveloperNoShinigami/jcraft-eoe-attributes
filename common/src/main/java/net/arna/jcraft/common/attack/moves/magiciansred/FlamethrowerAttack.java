package net.arna.jcraft.common.attack.moves.magiciansred;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractBarrageAttack;
import net.arna.jcraft.common.entity.stand.MagiciansRedEntity;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class FlamethrowerAttack extends AbstractBarrageAttack<FlamethrowerAttack, MagiciansRedEntity> {
    public FlamethrowerAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun, final float hitboxSize, final float knockback,
                              final float offset, final int interval) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, interval);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final MagiciansRedEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);
        for (LivingEntity target : targets) {
            if (!target.isOnFire()) {
                target.setSecondsOnFire(getInterval());
            }
        }
        return targets;
    }

    @Override
    protected @NonNull FlamethrowerAttack getThis() {
        return this;
    }

    @Override
    public @NonNull FlamethrowerAttack copy() {
        return copyExtras(new FlamethrowerAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset(), getInterval()));
    }
}
