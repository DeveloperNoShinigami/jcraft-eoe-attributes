package net.arna.jcraft.api.attack.moves;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.util.Function11;
import com.mojang.datafixers.util.Function13;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.attack.IAttacker;
import net.arna.jcraft.api.attack.enums.BlockableType;
import net.arna.jcraft.api.attack.enums.StunType;
import net.arna.jcraft.api.attack.StateContainer;
import net.arna.jcraft.api.attack.StateContainerHolder;
import net.arna.jcraft.common.attack.core.data.AttackMoveExtras;
import net.arna.jcraft.common.attack.core.data.BaseMoveExtras;
import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

@Getter
public abstract class AbstractGrabAttack<T extends AbstractGrabAttack<T, A, S>, A extends IAttacker<A, S>, S extends Enum<S>>
        extends AbstractSimpleAttack<T, A> implements StateContainerHolder<S> {
    private final AbstractMove<?, ? super A> hitMove;
    private final StateContainer<S> hitState; // NOTE: look at #configureStateContainers
    private final int grabDuration;
    private final double grabOffset;

    protected AbstractGrabAttack(final int cooldown, final int windup, final int duration, final float moveDistance,
                                 final float damage, final int stun, final float hitboxSize, final float knockback,
                                 final float offset, final AbstractMove<?, ? super A> hitMove, final StateContainer<S> hitState) {
        this(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset, hitMove, hitState, hitMove.getWindup() - 1, 1);
    }

    protected AbstractGrabAttack(final int cooldown, final int windup, final int duration, final float moveDistance,
                                 final float damage, final int stun, final float hitboxSize, final float knockback,
                                 final float offset, final AbstractMove<?, ? super A> hitMove, final StateContainer<S> hitState,
                                 final int grabDuration, final double grabOffset) {
        super(cooldown, windup, duration, moveDistance, damage, stun, hitboxSize, knockback, offset);

        grab = true;

        this.hitMove = hitMove;
        this.hitState = hitState.copy();
        this.grabDuration = grabDuration;
        this.grabOffset = grabOffset;
        withHitAnimation(null);

        // Grabs cannot be burst out of, or blocked
        withStunType(StunType.UNBURSTABLE);
        withOverrideStun();
        withBlockableType(BlockableType.NON_BLOCKABLE);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final A attacker, final LivingEntity user) {
        final Set<LivingEntity> targets = super.perform(attacker, user);
        if (targets.isEmpty()) {
            return targets;
        }

        final boolean unblockable = getBlockableType() == BlockableType.NON_BLOCKABLE;

        boolean anyHit = false;
        for (LivingEntity target : targets) {
            if (JUtils.isBlocking(target) && !unblockable) {
                continue;
            }

            StandEntity<?, ?> stand = JUtils.getStand(target);
            if (stand != null) {
                stand.blocking = false;
            }
            JUtils.cancelMoves(target);

            JComponentPlatformUtils.getGrab(target).startGrab(attacker.getBaseEntity(), grabDuration, grabOffset);

            JUtils.setVelocity(target, 0, 0, 0);

            anyHit = true;
        }

        if (anyHit) {
            attacker.setMove(hitMove, hitState.getValue());
        }

        return targets;
    }

    @Override
    public void configureStateContainers(Class<S> stateClass) {
        hitState.configure(stateClass);
    }

    protected abstract static class Type<M extends AbstractGrabAttack<? extends M, ?, ?>> extends AbstractSimpleAttack.Type<M> {
        @SuppressWarnings("unchecked")
        protected <A extends IAttacker<? extends A, ?>> RecordCodecBuilder<M, AbstractMove<?, ? super A>> hitMove() {
            return JRegistries.MOVE_CODEC
                    .fieldOf("hit_move")
                    .<AbstractMove<?, ? super A>>xmap(move -> (AbstractMove<?, ? super A>) move, move -> move)
                    .forGetter(atk -> (AbstractMove<?, ? super A>) atk.getHitMove());
        }

        @SuppressWarnings("unchecked")
        protected <S extends Enum<S>> RecordCodecBuilder<M, StateContainer<S>> hitState() {
            return StateContainer.CODEC
                    .xmap(s -> (StateContainer<S>) s, s -> s)
                    .fieldOf("hit_state")
                    .forGetter(atk -> (StateContainer<S>) atk.getHitState());
        }

        protected RecordCodecBuilder<M, Integer> grabDuration() {
            return Codec.INT
                    .fieldOf("grab_duration")
                    .forGetter(AbstractGrabAttack::getGrabDuration);
        }

        protected RecordCodecBuilder<M, Double> grabOffset() {
            return Codec.DOUBLE
                    .optionalFieldOf("grab_offset", 1.0)
                    .forGetter(AbstractGrabAttack::getGrabOffset);
        }

        protected <A extends IAttacker<? extends A, ?>, S extends Enum<S>> Products.P15<RecordCodecBuilder.Mu<M>, BaseMoveExtras,
                AttackMoveExtras, Integer, Integer, Integer, Float, Float, Integer, Float, Float, Float,
                AbstractMove<?, ? super A>, StateContainer<S>, Integer, Double>
        grabFullDefault(RecordCodecBuilder.Instance<M> instance) {
            return instance.group(extras(), attackExtras(), cooldown(), windup(), duration(), moveDistance(), damage(), stun(),
                    hitboxSize(), knockback(), offset(), hitMove(), hitState(), grabDuration(), grabOffset());
        }

        protected <A extends IAttacker<? extends A, ?>, S extends Enum<S>> App<RecordCodecBuilder.Mu<M>, M>
        grabFullDefault(RecordCodecBuilder.Instance<M> instance, Function13<Integer, Integer, Integer, Float, Float,
                        Integer, Float, Float, Float, AbstractMove<?, ? super A>, StateContainer<S>, Integer, Double, M> function) {
            return this.<A, S>grabFullDefault(instance).apply(instance, applyAttackExtras(function));
        }

        protected <A extends IAttacker<? extends A, ?>, S extends Enum<S>> Products.P13<RecordCodecBuilder.Mu<M>, BaseMoveExtras,
                AttackMoveExtras, Integer, Integer, Integer, Float, Float, Integer, Float, Float, Float,
                AbstractMove<?, ? super A>, StateContainer<S>>
        grabDefault(RecordCodecBuilder.Instance<M> instance) {
            return instance.group(extras(), attackExtras(), cooldown(), windup(), duration(), moveDistance(), damage(), stun(),
                    hitboxSize(), knockback(), offset(), hitMove(), hitState());
        }

        protected <A extends IAttacker<? extends A, ?>, S extends Enum<S>> App<RecordCodecBuilder.Mu<M>, M>
        grabDefault(RecordCodecBuilder.Instance<M> instance, Function11<Integer, Integer, Integer, Float, Float, Integer,
                        Float, Float, Float, AbstractMove<?, ? super A>, StateContainer<S>, M> function) {
            return this.<A, S>grabDefault(instance).apply(instance, applyAttackExtras(function));
        }
    }
}
