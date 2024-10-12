package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.entity.stand.TheHandEntity;

/**
 * The {@link StandEntityModel} for {@link TheHandEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.TheHandRenderer TheHandRenderer
 */
public class TheHandModel extends StandEntityModel<TheHandEntity> {
    public TheHandModel() {
        super(StandType.THE_HAND);
    }
}
