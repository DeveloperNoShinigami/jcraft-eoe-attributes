package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.arna.jcraft.api.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link MetallicaEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.MetallicaRenderer MetallicaRenderer
 */
public class MetallicaModel extends StandEntityModel<MetallicaEntity> {
    public MetallicaModel() {
        super(JStandTypeRegistry.METALLICA.get());
    }
}
