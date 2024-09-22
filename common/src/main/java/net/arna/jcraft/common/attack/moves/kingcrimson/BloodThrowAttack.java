package net.arna.jcraft.common.attack.moves.kingcrimson;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.BloodProjectile;
import net.arna.jcraft.common.entity.stand.KingCrimsonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import java.util.Set;

public final class BloodThrowAttack extends AbstractMove<BloodThrowAttack, KingCrimsonEntity> {
    public BloodThrowAttack(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public void onInitiate(KingCrimsonEntity attacker) {
        super.onInitiate(attacker);

        attacker.getUserOrThrow().hurt(attacker.level().damageSources().magic(), 0.1f); // User throws their blood, dealing a bit of damage.
    }

    @Override
    public @NonNull Set<LivingEntity> perform(KingCrimsonEntity attacker, LivingEntity user, MoveContext ctx) {
        final BloodProjectile bloodProjectile = new BloodProjectile(attacker.level(), user);
        bloodProjectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        bloodProjectile.shootFromRotation(user, user.getXRot(), user.getYRot(), 0, user.isShiftKeyDown() ? 1.33F : 0.66F, 0);
        bloodProjectile.setPos(attacker.getEyePosition());
        attacker.level().addFreshEntity(bloodProjectile);

        return Set.of();
    }

    @Override
    protected @NonNull BloodThrowAttack getThis() {
        return this;
    }

    @Override
    public @NonNull BloodThrowAttack copy() {
        return copyExtras(new BloodThrowAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
