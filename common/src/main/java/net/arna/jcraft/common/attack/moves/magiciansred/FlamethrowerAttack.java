package net.arna.jcraft.common.attack.moves.magiciansred;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractBarrageAttack;
import net.arna.jcraft.common.entity.stand.MagiciansRedEntity;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class FlamethrowerAttack extends AbstractBarrageAttack<FlamethrowerAttack, MagiciansRedEntity> {
    public FlamethrowerAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun, float hitboxSize, float knockback,
                              float offset, int interval) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, interval);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MagiciansRedEntity attacker, LivingEntity user, MoveContext ctx) {
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
