package net.arna.jcraft.common.attack.moves.goldexperience.requiem;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.IntMoveVariable;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.GERScorpionEntity;
import net.arna.jcraft.common.entity.stand.GEREntity;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

@Getter
public final class LifeBeamAttack extends AbstractMove<LifeBeamAttack, GEREntity> {
    public static final IntMoveVariable CHARGE_TIME = new IntMoveVariable();

    public LifeBeamAttack(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final GEREntity attacker, final LivingEntity user, final MoveContext ctx) {
        final GERScorpionEntity scorpion = new GERScorpionEntity(JEntityTypeRegistry.GER_SCORPION.get(), attacker.level());
        if (ctx.getInt(CHARGE_TIME) >= 18) {
            scorpion.charge();
        }
        scorpion.setInitialVel(user.getLookAngle().scale(2));
        final Vec3 ePos = attacker.getEyePosition();
        scorpion.moveTo(ePos.x, ePos.y, ePos.z, -user.getYRot() - 90f, attacker.getXRot());
        scorpion.setMaster(user);
        attacker.level().addFreshEntity(scorpion);

        return Set.of();
    }

    @Override
    public void registerContextEntries(final MoveContext ctx) {
        ctx.register(CHARGE_TIME);
    }

    @Override
    protected @NonNull LifeBeamAttack getThis() {
        return this;
    }

    @Override
    public @NonNull LifeBeamAttack copy() {
        return copyExtras(new LifeBeamAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
