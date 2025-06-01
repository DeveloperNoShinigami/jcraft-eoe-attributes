package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.TheSunEntity;
import net.arna.jcraft.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link TheSunEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.SunRenderer SunRenderer
 */
public class TheSunModel extends StandEntityModel<TheSunEntity> {
    public TheSunModel() {
        super(JStandTypeRegistry.THE_SUN.get());
    }

    @Override
    protected boolean skipCustomAnimations() {
        return true;
    }
}
