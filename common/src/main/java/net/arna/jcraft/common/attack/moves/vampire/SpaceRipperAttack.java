package net.arna.jcraft.common.attack.moves.vampire;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.IntMoveVariable;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.LaserProjectile;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.spec.VampireSpec;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public class SpaceRipperAttack extends AbstractMove<SpaceRipperAttack, VampireSpec> {
    public static final IntMoveVariable CHARGE_TIME = new IntMoveVariable();

    public SpaceRipperAttack(int cooldown, int windup, int duration, float attackDistance) {
        super(cooldown, windup, duration, attackDistance);
        ranged = true;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(VampireSpec attacker, LivingEntity user, MoveContext ctx) {
        Vec3 rotVec = user.getLookAngle();

        int chargeTime = ctx.getInt(CHARGE_TIME);

        for (int i = -1; i < 3; i += 2) {
            LaserProjectile laser = new LaserProjectile(attacker.getEntityWorld(), user);
            laser.setDeltaMovement(getRotVec(attacker).scale(2 + (chargeTime - 15) / 10.0));

            Vec3 sideOffset = rotVec.yRot(1.57079632679f * i).scale(0.125);
            Vec3 offset = RotationUtil.vecPlayerToWorld(sideOffset.x, sideOffset.y + (double) user.getEyeHeight(), sideOffset.z, GravityChangerAPI.getGravityDirection(user));
            Vec3 offsetHeightPos = attacker.getBaseEntity().position().add(offset);
            laser.setPos(offsetHeightPos);

            if (chargeTime > 24) {
                laser.setUnblockable(true);
            }

            attacker.getEntityWorld().addFreshEntity(laser);

            JComponentPlatformUtils.getShockwaveHandler(user.level()).addShockwave(offsetHeightPos, rotVec, 2);
        }

        return Set.of();
    }

    @Override
    public void registerContextEntries(MoveContext ctx) {
        ctx.register(CHARGE_TIME);
    }

    @Override
    protected @NonNull SpaceRipperAttack getThis() {
        return this;
    }

    @Override
    public @NonNull SpaceRipperAttack copy() {
        return copyExtras(new SpaceRipperAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
