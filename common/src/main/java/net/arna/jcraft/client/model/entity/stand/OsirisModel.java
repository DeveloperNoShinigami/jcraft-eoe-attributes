package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.OsirisEntity;
import net.arna.jcraft.common.entity.stand.StandType;

/**
 * The {@link StandEntityModel} for {@link OsirisEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.OsirisRenderer OsirisRenderer
 */
public class OsirisModel extends StandEntityModel<OsirisEntity> {
    public OsirisModel() {
        super(StandType.OSIRIS);
    }
}
