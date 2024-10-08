package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.entity.stand.TheSunEntity;

/**
 * The {@link StandEntityModel} for {@link TheSunEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.SunRenderer SunRenderer
 */
public class TheSunModel extends StandEntityModel<TheSunEntity> {
    public TheSunModel() {
        super(StandType.THE_SUN);
    }

    @Override
    protected boolean skipCustomAnimations() {
        return true;
    }
}
