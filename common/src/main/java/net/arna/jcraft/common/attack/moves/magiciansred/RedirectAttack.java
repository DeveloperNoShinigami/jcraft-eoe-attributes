package net.arna.jcraft.common.attack.moves.magiciansred;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.AnkhProjectile;
import net.arna.jcraft.common.entity.stand.MagiciansRedEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.util.MobilityType;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.Set;

public final class RedirectAttack extends AbstractMove<RedirectAttack, MagiciansRedEntity> {
    public RedirectAttack(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        mobilityType = MobilityType.TELEPORT; // this is a LIE, it just tells the AI to use it at a range of >3m
    }

    @Override
    public @NonNull Set<LivingEntity> perform(MagiciansRedEntity attacker, LivingEntity user, MoveContext ctx) {
        List<AnkhProjectile> ankhs = attacker.level().getEntitiesOfClass(AnkhProjectile.class,
                attacker.getBoundingBox().inflate(32), EntitySelector.NO_CREATIVE_OR_SPECTATOR);

        Vec3 eyePos = getOffsetHeightPos(attacker);
        if (!ankhs.isEmpty()) {
            Vec3 pos = JUtils.raycastAll(user, eyePos, eyePos.add(user.getLookAngle().scale(24)), ClipContext.Fluid.NONE);

            for (AnkhProjectile ankh : ankhs) {
                if (ankh.getOwner() != user) {
                    continue;
                }
                ankh.setVariation(false);
                ankh.setDeltaMovement(pos.subtract(ankh.position()).normalize().scale(0.6));
                ankh.hurtMarked = true;
            }
        }

        return Set.of();
    }

    @Override
    protected @NonNull RedirectAttack getThis() {
        return this;
    }

    @Override
    public @NonNull RedirectAttack copy() {
        return copyExtras(new RedirectAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
