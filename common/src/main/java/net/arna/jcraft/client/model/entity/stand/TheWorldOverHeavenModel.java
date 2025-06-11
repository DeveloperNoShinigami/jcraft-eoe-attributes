package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.TheWorldOverHeavenEntity;
import net.arna.jcraft.api.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link TheWorldOverHeavenEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.TheWorldOverHeavenRenderer TheWorldOverHeavenRenderer
 */
public class TheWorldOverHeavenModel extends StandEntityModel<TheWorldOverHeavenEntity> {
    public TheWorldOverHeavenModel() {
        super(JStandTypeRegistry.THE_WORLD_OVER_HEAVEN.get(), -0.1745329251f, -0.31f);
    }
}
