package net.arna.jcraft.common.marker;

import com.mojang.datafixers.util.Pair;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record BlockMarkerType(MarkerPredicate<BlockPos, BlockState> predicate) implements MarkerPredicate<BlockPos, BlockState>, MarkerType<BlockPos, BlockState, BlockMarker> {

    @Override
    public boolean shouldSave(final @NonNull BlockPos id, final @NonNull BlockState object) {
        return predicate.shouldSave(id, object);
    }

    @NonNull
    @Override
    public Optional<BlockMarker> save(final @NotNull BlockPos id, final @NotNull BlockState object) {
        if (shouldSave(id, object)) {
            return Optional.of(new BlockMarker(id, object));
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<Pair<BlockPos, BlockState>> load(final @NotNull BlockMarker marker) {
        return Optional.of(Pair.of(marker.pos(), marker.state()));
    }

    @NonNull
    @Override
    public BlockMarkerType and(@NonNull MarkerPredicate<BlockPos, BlockState> other) {
        return new BlockMarkerType(predicate.and(other));
    }

    @NonNull
    @Override
    public BlockMarkerType or(@NonNull MarkerPredicate<BlockPos, BlockState> other) {
        return new BlockMarkerType(predicate.or(other));
    }

    @NonNull
    @Override
    public BlockMarkerType negate() {
        return new BlockMarkerType(predicate.negate());
    }
}
