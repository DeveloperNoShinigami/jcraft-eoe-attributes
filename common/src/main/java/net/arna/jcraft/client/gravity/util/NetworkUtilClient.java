package net.arna.jcraft.client.gravity.util;

import net.arna.jcraft.common.component.entity.CommonGravityComponent;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import java.util.Optional;

public class NetworkUtilClient {

    public static Optional<CommonGravityComponent> getGravityComponent(Minecraft client, int entityId) {
        if (client.level == null) {
            return Optional.empty();
        }
        Entity entity = client.level.getEntity(entityId);
        if (entity == null) {
            return Optional.empty();
        }
        CommonGravityComponent gc = GravityChangerAPI.getGravityComponent(entity);
        if (gc == null) {
            return Optional.empty();
        }
        return Optional.of(gc);
    }
}
