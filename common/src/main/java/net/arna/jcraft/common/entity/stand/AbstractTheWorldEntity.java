package net.arna.jcraft.common.entity.stand;

import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.api.stand.StandType;
import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.world.level.Level;

public abstract sealed class AbstractTheWorldEntity<E extends AbstractTheWorldEntity<E, S>, S extends Enum<S> & StandAnimationState<E>> extends StandEntity<E, S>
permits TheWorldEntity, ShadowTheWorldEntity{
    protected AbstractTheWorldEntity(StandType type, Level world) {
        super(type, world);
    }

    @Override
    public void desummon() {
        if (tsTime > 0) {
            return;
        }
        super.desummon();
    }
}
