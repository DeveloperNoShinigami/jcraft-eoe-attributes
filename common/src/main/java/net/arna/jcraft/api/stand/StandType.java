package net.arna.jcraft.api.stand;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.arna.jcraft.api.IAttackerType;
import net.arna.jcraft.common.data.AttackerDataLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/**
 * A very basic class representing a stand type.
 * Mainly used as a helper class to get the current stand data and
 * as a marker that an entity is a stand.
 */
@ToString
@Getter
@RequiredArgsConstructor(staticName = "of")
public class StandType implements IAttackerType {
    private final ResourceLocation id;
    private final Supplier<? extends EntityType<? extends StandEntity<?, ?>>> entityType;

    /**
     * Gets the entity type associated with this stand type.
     * Used to create new stand entities of this type.
     * @return The entity type associated with this stand type
     */
    public EntityType<? extends StandEntity<?, ?>> getEntityType() {
        return entityType.get();
    }

    /**
     * Gets the StandData for this StandType.
     * <p>
     * <b>Important:</b> if you have a StandEntity instance, use {@link StandEntity#getStandData()} instead
     * as it may return a different instance based on its state.
     * @return The StandData for this StandType.
     */
    public StandData getData() {
        return AttackerDataLoader.getStandData(getId());
    }

    /**
     * Creates a new StandEntity of this type in the given level.
     * @param level The level in which to create the entity.
     * @return A new StandEntity of this type in the given level.
     */
    public StandEntity<?, ?> createEntity(final Level level) {
        return getEntityType().create(level);
    }

    @Override
    public final String kind() {
        return "stand";
    }
}
