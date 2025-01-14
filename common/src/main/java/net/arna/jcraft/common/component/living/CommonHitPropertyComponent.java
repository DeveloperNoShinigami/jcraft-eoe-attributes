package net.arna.jcraft.common.component.living;

import com.mojang.serialization.Codec;
import net.arna.jcraft.common.component.JComponent;
import net.arna.jcraft.common.util.JCodecUtils;
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
        ROLL;

        public static final Codec<HitAnimation> CODEC = JCodecUtils.createEnumCodec(HitAnimation.class);
    }
}
