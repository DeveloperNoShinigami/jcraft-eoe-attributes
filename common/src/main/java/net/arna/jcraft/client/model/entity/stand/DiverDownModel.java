package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.DiverDownEntity;
import net.arna.jcraft.api.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link DiverDownEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.DiverDownRenderer DiverDownRenderer
 */
public class DiverDownModel extends StandEntityModel<DiverDownEntity> {
    public DiverDownModel() {
        super(JStandTypeRegistry.DIVER_DOWN.get());
    }

}
