package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.MadeInHeavenEntity;
import net.arna.jcraft.api.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link MadeInHeavenEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.MadeInHeavenRenderer MadeInHeavenRenderer
 */
public class MadeInHeavenModel extends StandEntityModel<MadeInHeavenEntity> {
    public MadeInHeavenModel() {
        super(JStandTypeRegistry.MADE_IN_HEAVEN.get(), -0.1745329251f, -0.1745329251f);
    }
}
