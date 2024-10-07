package net.arna.jcraft.common.attack.moves.whitesnake;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.projectile.WSAcidProjectile;
import net.arna.jcraft.common.entity.stand.WhiteSnakeEntity;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.entity.LivingEntity;
import java.util.Set;

public final class MeltYourHeartAttack extends AbstractSimpleAttack<MeltYourHeartAttack, WhiteSnakeEntity> {
    public MeltYourHeartAttack(final int cooldown, final int windup, final int duration, final float moveDistance, final float damage, final int stun,
                               final float hitboxSize, final float knockback, final float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final WhiteSnakeEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        for (int i = 0; i < 10; i++) {
            final float yaw = i * 36F - 180F + i * 3.6F;
            for (int j = 0; j < 10; j++) {
                WSAcidProjectile acidProjectile = new WSAcidProjectile(attacker.level(), user);
                acidProjectile.markMeltYourHeart();
                JUtils.shoot(acidProjectile, user, j * 36F - 180F, yaw, 0, 0.66F, 0);
                acidProjectile.setPos(attacker.getEyePosition());
                attacker.level().addFreshEntity(acidProjectile);
            }
        }

        return targets;
    }

    @Override
    protected @NonNull MeltYourHeartAttack getThis() {
        return this;
    }

    @Override
    public @NonNull MeltYourHeartAttack copy() {
        return copyExtras(new MeltYourHeartAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }
}
