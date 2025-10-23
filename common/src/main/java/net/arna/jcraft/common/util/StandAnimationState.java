package net.arna.jcraft.common.util;

import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.AzAnimationControllerContainer;
import net.arna.jcraft.api.attack.IAttacker;

public interface StandAnimationState<A extends IAttacker<A, ?>> {

    void playAnimation(A attacker, AnimationState<A> state);

    default void configureController(A attacker, AzAnimationControllerContainer<A> controllers) {
        // no-op by default
    }
}
