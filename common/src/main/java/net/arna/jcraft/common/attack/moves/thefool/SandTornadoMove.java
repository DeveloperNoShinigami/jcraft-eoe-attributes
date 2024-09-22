package net.arna.jcraft.common.attack.moves.thefool;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.SandTornadoEntity;
import net.arna.jcraft.common.entity.stand.TheFoolEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public final class SandTornadoMove extends AbstractMove<SandTornadoMove, TheFoolEntity> {
    public SandTornadoMove(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(TheFoolEntity attacker, LivingEntity user, MoveContext ctx) {
        final SandTornadoEntity sandTornado = new SandTornadoEntity(attacker.level());
        sandTornado.setMaster(user);
        sandTornado.moveTo(attacker.getX(), attacker.getY() + 1.5, attacker.getZ(), attacker.getYRot(), attacker.getXRot());
        attacker.level().addFreshEntity(sandTornado);

        return Set.of();
    }

    @Override
    protected @NonNull SandTornadoMove getThis() {
        return this;
    }

    @Override
    public @NonNull SandTornadoMove copy() {
        return copyExtras(new SandTornadoMove(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
