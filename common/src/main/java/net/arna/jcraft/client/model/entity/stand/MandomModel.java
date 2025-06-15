package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.arna.jcraft.common.entity.stand.MandomEntity;

public class MandomModel extends StandEntityModel<MandomEntity> {
    public MandomModel() {
        super(JStandTypeRegistry.MANDOM.get(), 0f, 0f);
    }
}