package net.arna.jcraft.common.attack.moves.silverchariot;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.RapierProjectile;
import net.arna.jcraft.common.entity.stand.SilverChariotEntity;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public class LastShotAttack extends AbstractMove<LastShotAttack, SilverChariotEntity> {
    public LastShotAttack(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(SilverChariotEntity attacker, LivingEntity user, MoveContext ctx) {
        if (!attacker.hasRapier()) {
            return Set.of();
        }

        RapierProjectile rapier = new RapierProjectile(attacker.level(), user, attacker);
        rapier.shootFromRotation(attacker, user.getXRot(), user.getYRot(), 0, 2, 1);
        rapier.setSkin(attacker.getMode() != SilverChariotEntity.Mode.ARMORLESS ?
                -attacker.getMode().ordinal() : // Armorless and possessed output -1 and -2
                attacker.getSkin());
        attacker.level().addFreshEntity(rapier);
        attacker.setHasRapier(false);

        return Set.of();
    }

    @Override
    protected @NonNull LastShotAttack getThis() {
        return this;
    }

    @Override
    public @NonNull LastShotAttack copy() {
        return copyExtras(new LastShotAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
