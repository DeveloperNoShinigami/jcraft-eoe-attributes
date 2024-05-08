package net.arna.jcraft.common.component.living;

import net.minecraft.util.math.Vec3d;

public interface CommonHitPropertyComponent {
    // Hit Animation
    long endHitAnimTime();

    Vec3d getRandomRotation();

    HitAnimation getHitAnimation();

    void setHitAnimation(HitAnimation hitAnimation, int duration);

    enum HitAnimation {
        HIGH,
        MID,
        LOW,
        CRUSH,
        LAUNCH,
        ROLL
    }
}
