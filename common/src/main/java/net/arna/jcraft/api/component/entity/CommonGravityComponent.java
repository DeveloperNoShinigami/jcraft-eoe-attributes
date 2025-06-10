package net.arna.jcraft.api.component.entity;

import net.arna.jcraft.common.gravity.RotationAnimation;
import net.arna.jcraft.common.gravity.api.RotationParameters;
import net.arna.jcraft.common.gravity.util.Gravity;
import net.minecraft.core.Direction;
import java.util.List;

public interface CommonGravityComponent {
    //Internal

    void onGravityChanged(final Direction oldGravity, final Direction newGravity, final RotationParameters rotationParameters, final boolean initialGravity);

    void updateGravity(final RotationParameters rotationParameters, final boolean initialGravity);

    //Get

    Direction getGravityDirection();

    Direction getPrevGravityDirection();

    Direction getDefaultGravityDirection();

    Direction getActualGravityDirection();

    List<Gravity> getGravity();

    boolean getInvertGravity();

    RotationAnimation getGravityAnimation();

    //Set

    void setGravity(final List<Gravity> gravityList, final boolean initialGravity);

    void invertGravity(final boolean isInverted, final RotationParameters rotationParameters, final boolean initialGravity);

    void setDefaultGravityDirection(final Direction gravityDirection, final RotationParameters rotationParameters, final boolean initialGravity);

    void addGravity(final Gravity gravity, final boolean initialGravity);

    void clearGravity(final RotationParameters rotationParameters, final boolean initialGravity);
}
