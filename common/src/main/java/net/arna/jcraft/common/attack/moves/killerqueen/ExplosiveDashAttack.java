package net.arna.jcraft.common.attack.moves.killerqueen;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.AbstractKillerQueenEntity;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public final class ExplosiveDashAttack extends AbstractMove<ExplosiveDashAttack, AbstractKillerQueenEntity<?, ?>> {
    public ExplosiveDashAttack(final int cooldown) {
        super(cooldown, 0, 0, 0);
        dash = true;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final AbstractKillerQueenEntity<?, ?> attacker, final LivingEntity user, final MoveContext ctx) {
        final Vec3 lookVec = user.getLookAngle().scale(0.9);
        attacker.level().explode(user,
                user.getX() - lookVec.x,
                user.getY() + user.getBbHeight() / 2 - lookVec.y,
                user.getZ() - lookVec.z,
                1f, Level.ExplosionInteraction.NONE);

        user.setDeltaMovement(user.getDeltaMovement().add(lookVec));
        user.hurtMarked = true;

        attacker.playSound(JSoundRegistry.KQ_DETONATE.get(), 1, 1);

        return Set.of();
    }

    @Override
    protected @NonNull ExplosiveDashAttack getThis() {
        return this;
    }

    @Override
    public @NonNull ExplosiveDashAttack copy() {
        return copyExtras(new ExplosiveDashAttack(getCooldown()));
    }
}
