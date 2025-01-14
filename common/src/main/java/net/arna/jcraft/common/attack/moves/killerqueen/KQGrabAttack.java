package net.arna.jcraft.common.attack.moves.killerqueen;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.NonNull;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.StateContainer;
import net.arna.jcraft.common.attack.moves.base.AbstractGrabAttack;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.KillerQueenEntity;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public final class KQGrabAttack extends AbstractGrabAttack<KQGrabAttack, KillerQueenEntity, KillerQueenEntity.State> {
    public KQGrabAttack(final int cooldown, final int windup, final int duration, final float moveDistance,
                        final float damage, final int stun, final float hitboxSize, final float knockback,
                        final float offset, final AbstractMove<?, ? super KillerQueenEntity> hitMove) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitMove,
                StateContainer.of(KillerQueenEntity.State.GRAB_HIT));
    }

    @Override
    public @NotNull MoveType<KQGrabAttack> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final KillerQueenEntity attacker, final LivingEntity user, final MoveContext ctx) {
        Set<LivingEntity> targets = super.perform(attacker, user, ctx);
        targets.stream().findFirst().ifPresent(JComponentPlatformUtils.getBombTracker(user).getMainBomb()::setBomb);
        return targets;
    }

    @Override
    protected @NonNull KQGrabAttack getThis() {
        return this;
    }

    @Override
    public @NonNull KQGrabAttack copy() {
        return copyExtras(new KQGrabAttack(getCooldown(), getWindup(), getDuration(), getMoveDistance(), getDamage(), getStun(),
                getHitboxSize(), getKnockback(), getOffset(), getHitMove()));
    }

    public static class Type extends AbstractGrabAttack.Type<KQGrabAttack> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NonNull App<RecordCodecBuilder.Mu<KQGrabAttack>, KQGrabAttack> buildCodec(RecordCodecBuilder.Instance<KQGrabAttack> instance) {
            return instance.group(extras(), attackExtras(), cooldown(), windup(), duration(), moveDistance(), damage(), stun(),
                    hitboxSize(), knockback(), offset(), this.<KillerQueenEntity>hitMove()).apply(instance, applyAttackExtras(KQGrabAttack::new));
        }
    }
}
