package net.arna.jcraft.common.attack.moves.mandom;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.common.entity.stand.MandomEntity;
import net.arna.jcraft.common.marker.BlockMarker;
import net.arna.jcraft.common.marker.EntityMarker;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class RewindMove extends AbstractMove<RewindMove, MandomEntity> {

    @Getter
    private final int reach; // in Euclidian distance in meters

    public RewindMove(final int cooldown, final int windup, final int duration, final float moveDistance, final int reach) {
        super(cooldown, windup, duration, moveDistance);
        if (reach < 0) {
            throw new IllegalArgumentException("Teleport reach cannot be negative!");
        }
        this.reach = reach;
    }

    @Override
    public @NotNull MoveType<RewindMove> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final MandomEntity attacker, final LivingEntity user) {
        CountdownMove countdownMove = findCountdownMove(attacker);
        if (countdownMove == null || !countdownMove.isCountdownActive()) {
            return Set.of();
        }

        ServerLevel level = (ServerLevel) attacker.level();

        final List<BlockMarker> blockMarkers = countdownMove.getTimeBlockMarkers();
        countdownMove.setResolving(true);
        for (final BlockMarker marker : blockMarkers) {
            if (CountdownMove.BLOCK_MARKER_TYPE.shouldLoad(marker, level)) {
                CountdownMove.BLOCK_MARKER_TYPE.load(marker, level);
            }
        }
        final List<EntityMarker> entityMarkers = countdownMove.getTimeEntityMarkers();
        for (final EntityMarker marker : entityMarkers) {
            if (CountdownMove.ENTITY_MARKER_TYPE.shouldLoad(marker, level)) {
                CountdownMove.ENTITY_MARKER_TYPE.load(marker, level);
            }
        }

        // Clean up
        entityMarkers.clear();
        blockMarkers.clear();
        countdownMove.getRewindInfo().clear();
        countdownMove.setCountdownActive(false);

        return Set.of();
    }

    private CountdownMove findCountdownMove(MandomEntity attacker) {
        return attacker.getMoveMap().asMovesList().stream()
                .filter(move -> move instanceof CountdownMove)
                .map(CountdownMove.class::cast)
                .findFirst()
                .orElse(null);
    }

    private void applyModernNBT(final CompoundTag receiver, final CompoundTag sender, final Set<String> identifiers) {
        for (String identifier : identifiers) {
            if (sender.contains(identifier)) {
                receiver.put(identifier, Objects.requireNonNull(sender.get(identifier)));
            }
        }
    }

    @Override
    protected @NonNull RewindMove getThis() {
        return this;
    }

    @Override
    public @NonNull RewindMove copy() {
        return copyExtras(new RewindMove(getCooldown(), getWindup(), getDuration(), getMoveDistance(), 200));
    }

    public static final class Type extends AbstractMove.Type<RewindMove> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<RewindMove>, RewindMove> buildCodec(RecordCodecBuilder.Instance<RewindMove> instance) {
            return instance.group(extras(), cooldown(), windup(), duration(), moveDistance(), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("reach").forGetter(RewindMove::getReach)).apply(instance, applyExtras(RewindMove::new));
        }
    }
}