package net.arna.jcraft.common.component.world;

import lombok.Data;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public interface CommonShockwaveHandlerComponent {
    void addShockwave(final double x, final double y, final double z, final float pitch, final float yaw, final float scale);

    default void addShockwave(final double x, final double y, final double z, final float pitch, final float yaw) {
        addShockwave(x, y, z, pitch, yaw, 1.0f);
    }

    default void addShockwave(final Vec3 pos, final float pitch, final float yaw, final float scale) {
        addShockwave(pos.x, pos.y, pos.z, pitch, yaw, scale);
    }

    default void addShockwave(final Vec3 pos, final float pitch, final float yaw) {
        addShockwave(pos.x, pos.y, pos.z, pitch, yaw);
    }

    default void addShockwave(final Vec3 pos, final Vec3 rotation, final float scale) {
        Vec2 polarRot = JUtils.rotationVectorToPolar(rotation);
        addShockwave(pos.x, pos.y, pos.z, polarRot.x, polarRot.y, scale);
    }

    default void addShockwave(final Vec3 pos, final Vec3 rotation) {
        Vec2 polarRot = JUtils.rotationVectorToPolar(rotation);
        addShockwave(pos.x, pos.y, pos.z, polarRot.x, polarRot.y);
    }

    List<Shockwave> getShockwaves();

    @Data
    class Shockwave {
        public static final int MAX_AGE = 6;
        public final double x, y, z;
        public final BlockPos blockPos;
        public final float pitch, yaw, scale;
        private int age;

        public Shockwave(double x, double y, double z, float pitch, float yaw, float scale, int age) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockPos = BlockPos.containing(x, y, z);
            this.pitch = pitch;
            this.yaw = yaw;
            this.scale = scale;
            this.age = age;
        }

        public Shockwave(double x, double y, double z, float pitch, float yaw, float scale) {
            this(x, y, z, pitch, yaw, scale, 0);
        }

        public void tick() {
            age++;
        }

        // Currently just the age, but this might change.
        public int getFrame() {
            return Math.min(age, MAX_AGE - 1);
        }
    }
}
