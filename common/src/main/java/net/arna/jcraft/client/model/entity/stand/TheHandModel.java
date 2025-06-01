package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.TheHandEntity;
import net.arna.jcraft.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link TheHandEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.TheHandRenderer TheHandRenderer
 */
public class TheHandModel extends StandEntityModel<TheHandEntity> {
    public TheHandModel() {
        super(JStandTypeRegistry.THE_HAND.get());
    }
}
