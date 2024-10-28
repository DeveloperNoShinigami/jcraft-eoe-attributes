package net.arna.jcraft.common.attack.moves.killerqueen;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.SheerHeartAttackEntity;
import net.arna.jcraft.common.entity.stand.KillerQueenEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public final class SheerHeartAttackAttack extends AbstractMove<SheerHeartAttackAttack, KillerQueenEntity> {
    public SheerHeartAttackAttack(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final KillerQueenEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final SheerHeartAttackEntity sha = new SheerHeartAttackEntity(attacker.level());
        sha.setMaster(user);
        sha.moveTo(attacker.getX(), attacker.getY() + 0.5, attacker.getZ(), attacker.getYRot(), attacker.getXRot());
        attacker.level().addFreshEntity(sha);

        return Set.of();
    }

    @Override
    protected @NonNull SheerHeartAttackAttack getThis() {
        return this;
    }

    @Override
    public @NonNull SheerHeartAttackAttack copy() {
        return copyExtras(new SheerHeartAttackAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
