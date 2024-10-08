package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.HorusEntity;
import net.arna.jcraft.common.entity.stand.StandType;

/**
 * The {@link StandEntityModel} for {@link HorusEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.HorusRenderer HorusRenderer
 */
public class HorusModel extends StandEntityModel<HorusEntity> {
    public HorusModel() {
        super(StandType.HORUS, 0f, 0f);
    }
}
