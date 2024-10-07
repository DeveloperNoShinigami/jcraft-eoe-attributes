package net.arna.jcraft.common.attack.moves.vampire;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.attack.moves.base.AbstractSpecGrabAttack;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.common.util.SpecAnimationState;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.Set;

public final class BloodSuckAttack<A extends JSpec<A, S>, S extends Enum<S> & SpecAnimationState<A>> extends AbstractSpecGrabAttack<BloodSuckAttack<A, S>, A, S> {
    public static final MoveVariable<LivingEntity> TARGET = new MoveVariable<>(LivingEntity.class);

    public BloodSuckAttack(final int cooldown, final int windup, final int duration, final float attackDistance, final float damage, final int stun,
                           final float hitboxSize, final float knockback, final float offset, final AbstractMove<?, ? super A> hitMove, final S hitState, final int grabDuration, final double grabOffset) {
        super(cooldown, windup, duration, attackDistance, damage, stun, hitboxSize, knockback, offset, hitMove, hitState, grabDuration, grabOffset);
    }

    public BloodSuckAttack(final int cooldown, final int windup, final int duration, final float attackDistance, final float damage, final int stun,
                           final float hitboxSize, final float knockback, final float offset, final AbstractMove<?, ? super A> hitMove, final S hitState) {
        super(cooldown, windup, duration, attackDistance, damage, stun, hitboxSize, knockback, offset, hitMove, hitState);
    }

    @Override
    public void registerContextEntries(final MoveContext ctx) {
        ctx.register(TARGET, null);
    }

    @Override
    public void performHook(final A attacker, final Set<LivingEntity> targets, final Set<AABB> boxes, final DamageSource damageSource, final Vec3 forwardPos, final Vec3 rotationVector, final MoveContext ctx) {
        super.performHook(attacker, targets, boxes, damageSource, forwardPos, rotationVector, ctx);
        if (!targets.isEmpty()) {
            ctx.set(TARGET, targets.stream().findFirst().get());
        }
    }

    @Override
    protected @NonNull BloodSuckAttack<A, S> getThis() {
        return this;
    }

    @Override
    public @NonNull BloodSuckAttack<A, S> copy() {
        return copyExtras(new BloodSuckAttack<>(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getHitMove(), getHitState(), getGrabDuration(), getGrabOffset()));
    }
}
