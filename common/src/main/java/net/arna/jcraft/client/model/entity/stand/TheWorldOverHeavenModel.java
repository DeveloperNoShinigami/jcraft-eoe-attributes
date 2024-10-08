package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.entity.stand.TheWorldOverHeavenEntity;

/**
 * The {@link StandEntityModel} for {@link TheWorldOverHeavenEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.TheWorldOverHeavenRenderer TheWorldOverHeavenRenderer
 */
public class TheWorldOverHeavenModel extends StandEntityModel<TheWorldOverHeavenEntity> {
    public TheWorldOverHeavenModel() {
        super(StandType.THE_WORLD_OVER_HEAVEN, -0.1745329251f, -0.31f);
    }
}
