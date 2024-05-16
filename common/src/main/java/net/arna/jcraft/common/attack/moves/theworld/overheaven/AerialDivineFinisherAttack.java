package net.arna.jcraft.common.attack.moves.theworld.overheaven;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.entity.projectile.KnifeProjectile;
import net.arna.jcraft.common.entity.stand.TheWorldOverHeavenEntity;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public class AerialDivineFinisherAttack extends AbstractSimpleAttack<AerialDivineFinisherAttack, TheWorldOverHeavenEntity> {
    public AerialDivineFinisherAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                                      float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        ranged = true;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(TheWorldOverHeavenEntity attacker, LivingEntity user, MoveContext ctx) {
        Set<LivingEntity> targets = super.perform(attacker, user, ctx);

        Vec3 heightOffset = RotationUtil.vecPlayerToWorld(new Vec3(0, user.getBbHeight() / 2.0, 0), GravityChangerAPI.getGravityDirection(user));

        RandomSource random = attacker.getRandom();
        for (int i = 0; i < 8; i++) {
            KnifeProjectile knife = new KnifeProjectile(attacker.level(), user);
            knife.setLightning(true);
            knife.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            knife.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 2F, 1F);
            knife.setPos(user.position().add(heightOffset).add(
                    random.triangle(0, 0.5),
                    random.triangle(0, 0.5),
                    random.triangle(0, 0.5)));
            attacker.level().addFreshEntity(knife);
        }

        return targets;
    }

    @Override
    protected @NonNull AerialDivineFinisherAttack getThis() {
        return this;
    }

    @Override
    public @NonNull AerialDivineFinisherAttack copy() {
        return copyExtras(new AerialDivineFinisherAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(),
                getDamage(), getStun(), getHitboxSize(), getKnockback(), getOffset()));
    }
}
