package net.arna.jcraft.common.attack.moves.thefool;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public final class PoundAttack extends AbstractSimpleAttack<PoundAttack, TheFoolEntity> {
    public PoundAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                       final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final TheFoolEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        for (LivingEntity target : targets) {
            Vec3 vel = target.getDeltaMovement();
            target.setDeltaMovement(vel.x, (attacker.getMoveStun() > 14) ? 0.5 : -1, vel.y);
            target.hurtMarked = true;
        }

        return targets;
    }

    @Override
    protected @NonNull PoundAttack getThis() {
        return this;
    }

    @Override
    public @NonNull PoundAttack copy() {
        return copyExtras(new PoundAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }
}
