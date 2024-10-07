package net.arna.jcraft.client.gravity.util;

import net.arna.jcraft.common.component.entity.CommonGravityComponent;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import java.util.Optional;

public class NetworkUtilClient {

    public static Optional<CommonGravityComponent> getGravityComponent(final Minecraft client, final int entityId) {
        if (client.level == null) {
            return Optional.empty();
        }
        final Entity entity = client.level.getEntity(entityId);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(GravityChangerAPI.getGravityComponent(entity));
    }
}
