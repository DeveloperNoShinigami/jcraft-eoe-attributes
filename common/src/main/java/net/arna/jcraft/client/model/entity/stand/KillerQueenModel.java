package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.KillerQueenEntity;
import net.arna.jcraft.api.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link KillerQueenEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.KillerQueenRenderer KillerQueenRenderer
 */
public class KillerQueenModel extends StandEntityModel<KillerQueenEntity> {
    public KillerQueenModel() {
        super(JStandTypeRegistry.KILLER_QUEEN.get(), -0.1745329251f, -0.36f);
    }
}
