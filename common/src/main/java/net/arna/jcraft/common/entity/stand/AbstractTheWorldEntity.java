package net.arna.jcraft.common.entity.stand;

import net.arna.jcraft.common.util.StandAnimationState;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public abstract sealed class AbstractTheWorldEntity<E extends AbstractTheWorldEntity<E, S>, S extends Enum<S> & StandAnimationState<E>> extends StandEntity<E, S>
permits TheWorldEntity, ShadowTheWorldEntity{
    protected AbstractTheWorldEntity(StandType type, Level world, Supplier<SoundEvent> soundEvent) {
        super(type, world, soundEvent);
        idleRotation = 225f;
    }

    @Override
    public void desummon() {
        if (tsTime > 0) {
            return;
        }
        super.desummon();
    }
}
