package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.HGEntity;
import net.arna.jcraft.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link HGEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.HGRenderer HGRenderer
 */
public class HGModel extends StandEntityModel<HGEntity> {
    public HGModel() {
        super(JStandTypeRegistry.HIEROPHANT_GREEN.get(), 0f, -0.2f);
    }
}
