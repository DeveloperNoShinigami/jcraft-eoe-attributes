package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.common.entity.stand.FooFightersEntity;
import net.arna.jcraft.common.entity.stand.StandType;

/**
 * The {@link StandEntityModel} for {@link FooFightersEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.FooFightersRenderer FooFightersRenderer
 */
public class FooFightersModel extends StandEntityModel<FooFightersEntity> {
    public FooFightersModel() {
        super(StandType.FOO_FIGHTERS);
    }
}
