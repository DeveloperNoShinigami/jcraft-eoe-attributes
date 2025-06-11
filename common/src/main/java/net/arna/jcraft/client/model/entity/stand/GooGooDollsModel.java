package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.GooGooDollsEntity;
import net.arna.jcraft.api.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link GooGooDollsEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.GooGooDollsRenderer GooGooDollsRenderer
 */
public class GooGooDollsModel extends StandEntityModel<GooGooDollsEntity> {
    public GooGooDollsModel() {
        super(JStandTypeRegistry.GOO_GOO_DOLLS.get());
    }
}
