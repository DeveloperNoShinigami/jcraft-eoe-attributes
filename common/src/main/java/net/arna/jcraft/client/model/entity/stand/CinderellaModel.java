package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.CinderellaEntity;
import net.arna.jcraft.common.entity.stand.StandType;

/**
 * The {@link StandEntityModel} for {@link CinderellaEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.CinderellaRenderer CinderellaRenderer
 */
public class CinderellaModel extends StandEntityModel<CinderellaEntity> {
    public CinderellaModel() {
        super(StandType.CINDERELLA);
    }
}
