package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.AtumEntity;
import net.arna.jcraft.common.entity.stand.StandType;

/**
 * The {@link StandEntityModel} for {@link AtumEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.AtumRenderer AtumRenderer
 */
public class AtumModel extends StandEntityModel<AtumEntity> {
    public AtumModel() {
        super(StandType.ATUM);
    }
}
