package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.CreamEntity;
import net.arna.jcraft.api.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link CreamEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.CreamRenderer CreamRenderer
 */
public class CreamModel extends StandEntityModel<CreamEntity> {
    public CreamModel() {
        super(JStandTypeRegistry.CREAM.get(), -0.1745329251f, -0.1f);
    }
}
