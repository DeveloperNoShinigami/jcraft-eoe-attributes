package net.arna.jcraft.common.attack.moves.killerqueen.bitesthedust;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.ctx.MoveVariable;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.projectile.BubbleProjectile;
import net.arna.jcraft.common.entity.stand.KQBTDEntity;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class BubbleAttack extends AbstractMove<BubbleAttack, KQBTDEntity> {
    public static final MoveVariable<BubbleProjectile> BUBBLE_PROJECTILE = new MoveVariable<>(BubbleProjectile.class);

    public BubbleAttack(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
        ranged = true;
    }

    @Override
    public @NotNull MoveType<BubbleAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void tick(final KQBTDEntity attacker) {
        if (attacker.hasUser())
            tickBubble(attacker);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final KQBTDEntity attacker, final LivingEntity user, final MoveContext ctx) {
        final BubbleProjectile bubbleProjectile = new BubbleProjectile(attacker.level(), user);
        bubbleProjectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        bubbleProjectile.shootFromRotation(user, user.getXRot(), user.getYRot(), 0, 0.5f, 0f);
        bubbleProjectile.setPos(attacker.position().add(0, 1.25, 0));
        attacker.level().addFreshEntity(bubbleProjectile);
        ctx.set(BUBBLE_PROJECTILE, bubbleProjectile);

        JComponentPlatformUtils.getBombTracker(user).getMainBomb().setBomb(bubbleProjectile);

        return Set.of();
    }

    public void tickBubble(final KQBTDEntity stand) {
        final BubbleProjectile bubbleProjectile = stand.getMoveContext().get(BUBBLE_PROJECTILE);
        if (bubbleProjectile != null && !bubbleProjectile.isInGround() && stand.hasUser()) {
            bubbleProjectile.setDeltaMovement(stand.getUserOrThrow().getLookAngle().scale(0.5));
            bubbleProjectile.hurtMarked = true;
        }
    }

    @Override
    public void registerExtraContextEntries(final MoveContext ctx) {
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

    public static class Type extends AbstractMove.Type<BubbleAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<BubbleAttack>, BubbleAttack> buildCodec(RecordCodecBuilder.Instance<BubbleAttack> instance) {
            return baseDefault(instance, BubbleAttack::new);
        }
    }
}
