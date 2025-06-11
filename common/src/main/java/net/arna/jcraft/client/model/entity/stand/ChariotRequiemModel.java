package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.ChariotRequiemEntity;
import net.arna.jcraft.api.registry.JStandTypeRegistry;

/**
 * The {@link StandEntityModel} for {@link ChariotRequiemEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.ChariotRequiemRenderer ChariotRequiemRenderer
 */
public class ChariotRequiemModel extends StandEntityModel<ChariotRequiemEntity> {
    public ChariotRequiemModel() {
        super(JStandTypeRegistry.CHARIOT_REQUIEM.get());
    }
}
