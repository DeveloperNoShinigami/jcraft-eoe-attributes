package net.arna.jcraft.common.marker;

import com.mojang.datafixers.util.Pair;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record BlockMarkerType(MarkerPredicate<BlockPos, BlockState> predicate) implements MarkerPredicate<BlockPos, BlockState>, MarkerType<BlockPos, BlockState, BlockMarker> {

    @Override
    public boolean shouldSave(final @NonNull BlockPos id, final @NonNull BlockState object) {
        return predicate.shouldSave(id, object);
    }

    @NotNull
    @Override
    public BlockMarker save(final @NonNull BlockPos id, final @NonNull BlockState object) {
        return new BlockMarker(id, object);
    }

    @NotNull
    @Override
    public Optional<Pair<BlockPos, BlockState>> load(final @NonNull BlockMarker marker, final @NonNull ServerLevel level) {
        level.setBlockAndUpdate(marker.pos(), marker.state());
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
