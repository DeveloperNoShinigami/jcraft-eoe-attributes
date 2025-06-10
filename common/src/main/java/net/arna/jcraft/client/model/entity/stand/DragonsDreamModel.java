package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.DragonsDreamEntity;
import net.arna.jcraft.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link DragonsDreamEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.DragonsDreamRenderer DragonsDreamRenderer
 */
public class DragonsDreamModel extends StandEntityModel<DragonsDreamEntity> {
    public DragonsDreamModel() {
        super(JStandTypeRegistry.DRAGONS_DREAM.get(), 0.0f, 1.5707f);
    }
}
