package net.arna.jcraft.common.attack.moves.killerqueen.bitesthedust;

import lombok.NonNull;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.BubbleProjectile;
import net.arna.jcraft.common.entity.stand.KQBTDEntity;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import java.util.Set;

public final class BubbleAttack extends AbstractMove<BubbleAttack, KQBTDEntity> {
    public static final MoveVariable<BubbleProjectile> BUBBLE_PROJECTILE = new MoveVariable<>(BubbleProjectile.class);

    public BubbleAttack(int cooldown, int windup, int duration, float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(KQBTDEntity attacker, LivingEntity user, MoveContext ctx) {
        final BubbleProjectile bubbleProjectile = new BubbleProjectile(attacker.level(), user);
        bubbleProjectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        bubbleProjectile.shootFromRotation(user, user.getXRot(), user.getYRot(), 0, 0.5f, 0f);
        bubbleProjectile.setPos(attacker.position().add(0, 1.25, 0));
        attacker.level().addFreshEntity(bubbleProjectile);
        ctx.set(BUBBLE_PROJECTILE, bubbleProjectile);

        JComponentPlatformUtils.getBombTracker(user).getMainBomb().setBomb(bubbleProjectile);

        return Set.of();
    }

    public void tickBubble(KQBTDEntity stand) {
        final BubbleProjectile bubbleProjectile = stand.getMoveContext().get(BUBBLE_PROJECTILE);
        if (bubbleProjectile != null && !bubbleProjectile.isInGround() && stand.hasUser()) {
            bubbleProjectile.setDeltaMovement(stand.getUserOrThrow().getLookAngle().scale(0.5));
            bubbleProjectile.hurtMarked = true;
        }
    }

    @Override
    public void registerContextEntries(MoveContext ctx) {
        ctx.register(BUBBLE_PROJECTILE);
    }

    @Override
    protected @NonNull BubbleAttack getThis() {
        return this;
    }

    @Override
    public @NonNull BubbleAttack copy() {
        return copyExtras(new BubbleAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }
}
