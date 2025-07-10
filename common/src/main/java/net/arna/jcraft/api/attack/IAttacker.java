package net.arna.jcraft.api.attack;

import net.arna.jcraft.api.MoveUsage;
import net.arna.jcraft.api.attack.enums.MoveClass;
import net.arna.jcraft.api.attack.enums.MoveInputType;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Anything that can use moves must implement this interface.
 * It provides basic functionality that moves need.
 *
 * @param <A> The type of the class implementing this interface
 * @param <S> The type of the animation state enum
 */
public interface IAttacker<A extends IAttacker<? extends A, S>, S extends Enum<?>> {
    boolean hasUser();

    LivingEntity getUser();

    LivingEntity getUserOrThrow();

    int getMoveStun();

    void setMoveStun(final int moveStun);

    // This cannot be called getWorld because it doesn't get remapped since it's in an interface.
    // StandEntity implements
    default Level getEntityWorld() {
        return getBaseEntity().level();
    }

    LivingEntity getBaseEntity();

    DamageSource getDamageSource();

    MoveMap<A, S> getMoveMap();

    boolean initMove(final MoveClass type);

    boolean canHoldMove(final @Nullable MoveInputType type);

    default void onUserMoveInput(final AbstractMove<?, ? super A> currentMove, final MoveInputType type, final boolean pressed, final boolean moveInitiated) {
        if ((moveInitiated && pressed && canHoldMove(type))) {
            setHoldingType(type);
        }
        if (getHoldingType() == type) {
            setHolding(pressed);
        }
        if (currentMove != null) {
            currentMove.onUserMoveInput(getThis(), type, pressed, moveInitiated);
        }
    }

    boolean isHolding();

    void setHolding(final boolean holding);

    MoveInputType getHoldingType();

    void setHoldingType(final MoveInputType holdingType);

    boolean canAttack();

    void cancelMove();

    boolean isRemote();

    AbstractMove<?, ? super A> getCurrentMove();

    void setCurrentMove(final AbstractMove<?, ? super A> move);

    default void setMove(final AbstractMove<?, ? super A> move, S state) {
        setCurrentMove(move);
        setState(state);
    }

    S getState();

    void setState(final S state);

    default void playAttackerSound(final SoundEvent sound, final float volume, final float pitch) {
        getBaseEntity().playSound(sound, volume, pitch);
    }

    /**
     * Called when a move is performed.
     * @param move The move that was performed. Should probably not be used.
     * @param targets The targets that were hit by the move. May be empty.
     */
    default void onPerform(AbstractMove<?, ? super A> move, Set<LivingEntity> targets) {}

    void setPerformedThisTick(final boolean b);
    boolean performedThisTick();

    A getThis();

    MoveUsage getMoveUsage();
}
