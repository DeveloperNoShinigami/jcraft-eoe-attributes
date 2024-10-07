package net.arna.jcraft.client.renderer.effects;

import lombok.experimental.UtilityClass;
import net.arna.jcraft.common.network.s2c.TimeAccelStatePacket;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.GameRules;

@UtilityClass
public class TimeAccelerationEffectRenderer {

    public static void render(final ClientLevel world) {
        if (!world.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            return;
        }

        final double acceleration = TimeAccelStatePacket.getAcceleration(world);

        final long currentTime = Util.getMillis();
        if (acceleration == 0) {
            TimeAccelStatePacket.lastUpdate = currentTime;
            return;
        }

        final double multiplier = (currentTime - TimeAccelStatePacket.lastUpdate) / 1000d;
        world.setDayTime((long) (world.getDayTime() + acceleration * multiplier));

        TimeAccelStatePacket.lastUpdate = currentTime;
    }
}
