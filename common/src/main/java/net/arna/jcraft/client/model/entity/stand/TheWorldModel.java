package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.TheWorldEntity;
import net.arna.jcraft.api.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link TheWorldModel}.
 * @see net.arna.jcraft.client.renderer.entity.stands.TheWorldRenderer
 */
public class TheWorldModel extends StandEntityModel<TheWorldEntity> {
    public TheWorldModel() {
        super(JStandTypeRegistry.THE_WORLD.get(), -0.1745329251f, -0.1745329251f);
    }
}
