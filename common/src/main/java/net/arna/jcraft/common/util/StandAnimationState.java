package net.arna.jcraft.common.util;

import net.arna.jcraft.common.attack.core.IAttacker;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.animation.RawAnimation;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;
import mod.azure.azurelib.core.animatable.GeoAnimatable;

public interface StandAnimationState<A extends IAttacker<A, ?> & GeoEntity> {

    void playAnimation(A attacker, AnimationState state);

    default void configureController(A attacker, AnimationController<A> controller) {
        // no-op by default
    }
}
