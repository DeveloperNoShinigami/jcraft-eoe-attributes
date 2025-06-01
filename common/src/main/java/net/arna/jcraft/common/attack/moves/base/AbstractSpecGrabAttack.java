package net.arna.jcraft.common.attack.moves.base;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Function12;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.common.attack.core.AttackMoveExtras;
import net.arna.jcraft.common.attack.core.BaseMoveExtras;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.StunType;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.common.util.SpecAnimationState;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

@Getter
public abstract class AbstractSpecGrabAttack<T extends AbstractSpecGrabAttack<T, A, S>, A extends JSpec<A, S>,
        S extends Enum<S> & SpecAnimationState<A>> extends AbstractSimpleAttack<T, A> {
    private final AbstractMove<?, ? super A> hitMove;
    private final S hitState;
    private final int grabDuration;
    private final double grabOffset;

    protected AbstractSpecGrabAttack(final int cooldown, final int windup, final int duration, final float moveDistance,
                                     final float damage, final int stun, final float hitboxSize, final float knockback,
                                     final float offset, final AbstractMove<?, ? super A> hitMove, final S hitState) {
        this(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback,
                offset, hitMove, hitState, hitMove.getWindup() - 1, 1);
    }

    protected AbstractSpecGrabAttack(final int cooldown, final int windup, final int duration, final float moveDistance,
                                     final float damage, final int stun, final float hitboxSize, final float knockback,
                                     final float offset, final AbstractMove<?, ? super A> hitMove, final S hitState,
                                     final int grabDuration, final double grabOffset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);

        grab = true;

        this.hitMove = hitMove;
        this.hitState = hitState;
        this.grabDuration = grabDuration;
        this.grabOffset = grabOffset;
        withHitAnimation(null);

        // Spec grabs cannot be burst out, but CAN be blocked
        withStunType(StunType.UNBURSTABLE);
        withOverrideStun();
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final A attacker, final LivingEntity user, final MoveContext ctx) {
        final Set<LivingEntity> targets = super.perform(attacker, user, ctx);
        if (targets.isEmpty()) {
            return targets;
        }
        boolean anyHit = false;

        for (LivingEntity target : targets) {
            final StandEntity<?, ?> stand = JUtils.getStand(target);
            if (stand != null && stand.blocking) {
                continue;
            }

            anyHit = true;
            JUtils.cancelMoves(target);
            JComponentPlatformUtils.getGrab(target).startGrab(attacker.getBaseEntity(), grabDuration, grabOffset);
            JUtils.setVelocity(target, 0, 0, 0);
        }

        //noinspection ConstantValue // not true
        if (anyHit) {
            attacker.setMove(hitMove, hitState);
            attacker.setMoveStun(grabDuration);
            attacker.setAnimation(hitState.getKey(attacker), grabDuration, 1f);
        }

        return targets;
    }

    protected abstract static class Type<M extends AbstractSpecGrabAttack<? extends M, ?, ?>> extends AbstractSimpleAttack.Type<M> {
        @SuppressWarnings("unchecked")
        protected <A extends IAttacker<? extends A, ?>> RecordCodecBuilder<M, AbstractMove<?, A>> hitMove() {
            return JRegistries.MOVE_CODEC
                    .fieldOf("hit_move")
                    .<AbstractMove<?, A>>xmap(m -> (AbstractMove<?, A>) m, m -> m)
                    .forGetter(m -> (AbstractMove<?, A>) m.getHitMove());
        }

        protected RecordCodecBuilder<M, Integer> grabDuration() {
            return Codec.INT
                    .fieldOf("grab_duration")
                    .forGetter(AbstractSpecGrabAttack::getGrabDuration);
        }

        protected RecordCodecBuilder<M, Double> grabOffset() {
            return Codec.DOUBLE
                    .optionalFieldOf("grab_offset", 1.0)
                    .forGetter(AbstractSpecGrabAttack::getGrabOffset);
        }

        protected <A extends IAttacker<? extends A, ?>> Products.P14<RecordCodecBuilder.Mu<M>, BaseMoveExtras,
                AttackMoveExtras, Integer, Integer, Integer, Float, Float, Integer, Float, Float, Float, AbstractMove<?, A>, Integer, Double>
        grabDefault(final RecordCodecBuilder.Instance<M> instance) {
            return instance.group(extras(), attackExtras(), cooldown(), windup(), duration(), moveDistance(), damage(),
                    stun(), hitboxSize(), knockback(), offset(), hitMove(), grabDuration(), grabOffset());
        }

        protected <A extends IAttacker<? extends A, ?>> App<RecordCodecBuilder.Mu<M>, M>
        grabDefault(final RecordCodecBuilder.Instance<M> instance, final Function12<Integer, Integer, Integer, Float,
                Float, Integer, Float, Float, Float, AbstractMove<?, A>, Integer, Double, M> function) {
            return this.<A>grabDefault(instance).apply(instance, applyAttackExtras(function));
        }
    }
}
