package net.arna.jcraft.client.renderer.effects;

import lombok.experimental.UtilityClass;
import net.arna.jcraft.common.network.s2c.TimeAccelStatePacket;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;

@UtilityClass
public class TimeAccelerationEffectRenderer {
    

    public static void render(MatrixStack stack, Vec3d camPos, ClientWorld world, float tickDelta) {
        if (!world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) return;

        double acceleration = TimeAccelStatePacket.getAcceleration(world);

        long currentTime = Util.getMeasuringTimeMs();
        if (acceleration == 0) {
            TimeAccelStatePacket.lastUpdate = currentTime;
            return;
        }

        double multiplier = (currentTime - TimeAccelStatePacket.lastUpdate) / 1000d;
        world.setTimeOfDay((long) (world.getTimeOfDay() + acceleration * multiplier));

        TimeAccelStatePacket.lastUpdate = currentTime;
    }
}
