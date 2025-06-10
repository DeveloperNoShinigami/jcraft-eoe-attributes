package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.KQBTDEntity;
import net.arna.jcraft.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link KQBTDEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.KQBTDRenderer KQBTDRenderer
 */
public class KQBTDModel extends StandEntityModel<KQBTDEntity> {
    public KQBTDModel() {
        super(JStandTypeRegistry.KILLER_QUEEN_BITES_THE_DUST.get(), 0f, -0.2f);
    }
}
