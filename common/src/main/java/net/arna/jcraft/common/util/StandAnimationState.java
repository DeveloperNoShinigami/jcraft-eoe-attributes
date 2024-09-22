package net.arna.jcraft.common.util;

import net.arna.jcraft.common.attack.core.IAttacker;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;

public interface StandAnimationState<A extends IAttacker<A, ?> & GeoEntity> {

    void playAnimation(A attacker, AnimationState<A> state);

    default void configureController(A attacker, AnimationController<A> controller) {
        // no-op by default
    }
}
