package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.HorusEntity;
import net.arna.jcraft.api.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link HorusEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.HorusRenderer HorusRenderer
 */
public class HorusModel extends StandEntityModel<HorusEntity> {
    public HorusModel() {
        super(JStandTypeRegistry.HORUS.get(), 0f, 0f);
    }
}
