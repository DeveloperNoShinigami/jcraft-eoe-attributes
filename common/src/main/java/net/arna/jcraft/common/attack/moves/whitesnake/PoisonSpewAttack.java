package net.arna.jcraft.common.attack.moves.whitesnake;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.projectile.WSAcidProjectile;
import net.arna.jcraft.common.entity.stand.WhiteSnakeEntity;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class PoisonSpewAttack extends AbstractSimpleAttack<PoisonSpewAttack, WhiteSnakeEntity> {
    public PoisonSpewAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                            final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        this.ranged = true;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final WhiteSnakeEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final WSAcidProjectile acidProjectile = new WSAcidProjectile(attacker.level(), user);
        acidProjectile.shootFromRotation(user, user.getXRot(), user.getYRot(), 0, 1.33F, 0);
        acidProjectile.setPos(attacker.getEyePosition());
        attacker.level().addFreshEntity(acidProjectile);

        return super.perform(attacker, user, ctx);
    }

    @Override
    protected @NonNull PoisonSpewAttack getThis() {
        return this;
    }

    @Override
    public @NonNull PoisonSpewAttack copy() {
        return copyExtras(new PoisonSpewAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }
}
