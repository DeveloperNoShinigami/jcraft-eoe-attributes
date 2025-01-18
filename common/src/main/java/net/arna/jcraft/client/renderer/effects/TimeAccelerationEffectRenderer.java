package net.arna.jcraft.client.renderer.effects;

import lombok.experimental.UtilityClass;
import net.arna.jcraft.common.network.s2c.TimeAccelStatePacket;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.GameRules;

@UtilityClass
public class TimeAccelerationEffectRenderer {

    public static void render(final ClientLevel world) {
        if (!world.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            return;
        }

        TimeAccelStatePacket.applyAcceleration(world, world::setDayTime);
    }
}
