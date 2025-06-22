package net.arna.jcraft.common.marker;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityMarkerType implements MarkerPredicate<UUID, Entity>, MarkerType<UUID, Entity, EntityMarker> {

    @NonNull
    @Getter
    protected final MarkerPredicate<UUID, Entity> predicate;

    @NonNull
    protected final Set<ResourceLocation> ids = new HashSet<>();

    @NonNull
    protected final Set<EntityDataHandler> dataHandlers = new HashSet<>();

    /**
     * @see #EntityMarkerType(MarkerPredicate, Collection, Collection)
     */
    public EntityMarkerType(final @NonNull Predicate<Entity> predicate, final @NonNull Collection<ResourceLocation> ids, final @Nullable Collection<EntityDataHandler> dataHandlers) {
        this((id, entity) -> predicate.test(entity), ids, dataHandlers);
    }

    /**
     * @param predicate if you want to save
     * @param ids what you want to save
     * @param dataHandlers how do you want to save (and load)
     */
    public EntityMarkerType(final @NonNull MarkerPredicate<UUID, Entity> predicate, final @NonNull Collection<ResourceLocation> ids, final @Nullable Collection<EntityDataHandler> dataHandlers) {
        this.predicate = predicate;
        this.ids.addAll(ids);
        if (dataHandlers != null) {
            this.dataHandlers.addAll(dataHandlers);
        }
    }

    @Override
    public boolean shouldSave(final @NonNull UUID id, final @NonNull Entity object) {
        return predicate.shouldSave(id, object);
    }

    @NotNull
    @Override
    public EntityMarker save(final @NonNull UUID id, final @NonNull Entity object) {
        final CompoundTag compoundTag = new CompoundTag();
        for (final ResourceLocation loc : ids) {
            for (final EntityDataHandler handler : dataHandlers) {
                if (handler.predicate().test(loc, object)) {
                    handler.extractor().accept(loc, object, compoundTag);
                    break;
                }
            }
        }
        return new EntityMarker(id, compoundTag);
    }

    @NonNull
    @Override
    public Optional<Pair<UUID, Entity>> load(final @NonNull EntityMarker marker, final @NonNull ServerLevel level) {
        final CompoundTag compoundTag = marker.state();
        final Entity entity = level.getEntity(marker.id());
        if (entity == null) {
            return Optional.empty();
        }
        for (final ResourceLocation loc : ids) {
            for (final EntityDataHandler handler : dataHandlers) {
                if (handler.predicate().test(loc, entity)) {
                    handler.injector().accept(loc, entity, compoundTag);
                    break;
                }
            }
        }
        return Optional.of(Pair.of(marker.id(), entity));
    }

    @NonNull
    @Override
    public EntityMarkerType and(final @NonNull MarkerPredicate<UUID, Entity> other) {
        return new EntityMarkerType(predicate.and(other), ids, dataHandlers);
    }

    public EntityMarkerType and(final @NonNull Predicate<Entity> other) {
        return and((id, entity) -> other.test(entity));
    }

    @NonNull
    @Override
    public EntityMarkerType or(final @NonNull MarkerPredicate<UUID, Entity> other) {
        return new EntityMarkerType(predicate.or(other), ids, dataHandlers);
    }

    public EntityMarkerType or(final @NonNull Predicate<Entity> other) {
        return or((id, entity) -> other.test(entity));
    }

    @NonNull
    @Override
    public EntityMarkerType negate() {
        return new EntityMarkerType(predicate.negate(), ids, dataHandlers);
    }
}
