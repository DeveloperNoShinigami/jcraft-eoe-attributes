package net.arna.jcraft.common.attack.moves.shared;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

@Getter
public final class UppercutAttack<A extends IAttacker<? extends A, ?>> extends AbstractSimpleAttack<UppercutAttack<A>, A> {
    private final float strength;

    public UppercutAttack(int cooldown, int windup, int duration, float moveDistance, float damage, int stun,
                          float hitboxSize, float knockback, float offset, float strength) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);
        this.strength = strength;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(A attacker, LivingEntity user, MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);
        for (LivingEntity target : targets) {
            final Vec3 upDir = new Vec3(GravityChangerAPI.getGravityDirection(user).step()).scale(-strength);
            JUtils.addVelocity(target, upDir);
        }

        return targets;
    }

    @Override
    protected @NonNull UppercutAttack<A> getThis() {
        return this;
    }

    @Override
    public @NonNull UppercutAttack<A> copy() {
        return copyExtras(new UppercutAttack<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(),
                getStun(), getHitboxSize(), getKnockback(), getOffset(), strength));
    }
}
