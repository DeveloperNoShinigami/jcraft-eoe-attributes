package net.arna.jcraft.common.attack.moves.whitesnake;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.projectile.WSAcidProjectile;
import net.arna.jcraft.common.entity.stand.WhiteSnakeEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import java.util.Set;

public final class ChargedSpewAttack extends AbstractSimpleAttack<ChargedSpewAttack, WhiteSnakeEntity> {
    public ChargedSpewAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                             float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        this.ranged = true;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(WhiteSnakeEntity attacker, LivingEntity user, MoveContext ctx) {
        Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        Direction gravity = GravityChangerAPI.getGravityDirection(user);
        for (int i = 0; i < 5; i++) {
            WSAcidProjectile acidProjectile = new WSAcidProjectile(attacker.level(), user);

            Vec2 corrected = RotationUtil.rotPlayerToWorld(user.getYRot() - 75F + i * 37.5F, user.getXRot(), gravity);
            JUtils.shoot(acidProjectile, user, corrected.y, corrected.x, 0, 0.66F, 0);

            acidProjectile.setPos(attacker.getEyePosition());
            attacker.level().addFreshEntity(acidProjectile);
        }

        return targets;
    }

    @Override
    protected @NonNull ChargedSpewAttack getThis() {
        return this;
    }

    @Override
    public @NonNull ChargedSpewAttack copy() {
        return copyExtras(new ChargedSpewAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset()));
    }
}
