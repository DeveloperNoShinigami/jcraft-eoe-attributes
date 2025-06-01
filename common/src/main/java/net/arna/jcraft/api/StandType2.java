package net.arna.jcraft.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

// TODO rename to StandType once we're finished with the migration
/**
 * A very basic class representing a stand type.
 * Mainly used as a helper class to get the current stand data and
 * as a marker that an entity is a stand.
 */
@ToString
@Getter
@RequiredArgsConstructor(staticName = "of")
public class StandType2 implements IAttackerType {
    private final ResourceLocation id;
    private final Supplier<EntityType<? extends StandEntity<?, ?>>> entityType;

    public EntityType<? extends StandEntity<?, ?>> getEntityType() {
        return entityType.get();
    }

    public StandData getData() {
        return StandData.EMPTY; // TODO get the corresponding StandData from the registry
    }

    /**
     * Creates a new StandEntity of this type in the given level.
     * @param level The level in which to create the entity.
     * @return A new StandEntity of this type in the given level.
     */
    public StandEntity<?, ?> createEntity(Level level) {
        return getEntityType().create(level);
    }

    @Override
    public final String kind() {
        return "stand";
    }
}
