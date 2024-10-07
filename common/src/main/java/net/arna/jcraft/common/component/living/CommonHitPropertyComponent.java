package net.arna.jcraft.common.component.living;

import net.arna.jcraft.common.component.JComponent;
import net.minecraft.world.phys.Vec3;

public interface CommonHitPropertyComponent extends JComponent {
    // Hit Animation
    long endHitAnimTime();

    Vec3 getRandomRotation();

    HitAnimation getHitAnimation();

    void setHitAnimation(final HitAnimation hitAnimation, final int duration);

    enum HitAnimation {
        HIGH,
        MID,
        LOW,
        CRUSH,
        LAUNCH,
        ROLL
    }
}
