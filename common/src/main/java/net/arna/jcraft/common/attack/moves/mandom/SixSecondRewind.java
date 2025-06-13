// SixSecondRewind.java
package net.arna.jcraft.common.attack.moves.mandom;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.Unpooled;
import lombok.NonNull;
import net.arna.jcraft.api.attack.MoveType;
import net.arna.jcraft.api.attack.moves.AbstractMove;
import net.arna.jcraft.api.registry.JSoundRegistry;
import net.arna.jcraft.common.entity.stand.MandomEntity;
import net.arna.jcraft.common.network.s2c.ServerChannelFeedbackPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class SixSecondRewind extends AbstractMove<SixSecondRewind, MandomEntity> {
    private static final int REWIND_DURATION_TICKS = 120; // 6 seconds

    // Instance fields replacing MoveContext variables
    private final Map<Entity, CompoundTag> timeMarkerData = new WeakHashMap<>();
    private int ticksSinceMarker = 0;
    private boolean markerPlaced = false;
    private final List<RewindData> rewindInfo = new ArrayList<>();

    public SixSecondRewind(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public @NotNull MoveType<SixSecondRewind> getMoveType() {
        return Type.INSTANCE;
    }

    @Override
    public void onInitiate(MandomEntity attacker) {
        super.onInitiate(attacker);
        // Reset state variables
        ticksSinceMarker = 0;
        markerPlaced = false;
        timeMarkerData.clear();
        rewindInfo.clear();
    }

    @Override
    public void tick(final MandomEntity attacker) {
        super.tick(attacker);

        if (!markerPlaced) {
            return;
        }

        // Send visual feedback to show rewind positions
        tickRewindInfo(attacker, ticksSinceMarker);

        if (ticksSinceMarker >= REWIND_DURATION_TICKS) {
            // Time to rewind!
            rewindToMarker(attacker);
            // Reset the marker state
            markerPlaced = false;
            ticksSinceMarker = 0;
        } else {
            // Increment tick counter
            ticksSinceMarker++;
        }
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final MandomEntity attacker, final LivingEntity user) {
        // Step 1 & 2: Use the move and place the "time marker"
        final List<Entity> toCapture = attacker.level().getEntitiesOfClass(Entity.class, attacker.getBoundingBox().inflate(64),
                EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(e -> e != attacker));

        // Also include the user in the rewind (unlike ReturnToZero)
        toCapture.add(user);

        timeMarkerData.clear(); // Clear any previous marker data
        rewindInfo.clear(); // Clear any previous rewind info

        // Capture the current state of all entities
        for (Entity e : toCapture) {
            final CompoundTag data = new CompoundTag();
            e.saveWithoutId(data);
            timeMarkerData.put(e, data);
            rewindInfo.add(new RewindData(e.getEyePosition(), e));
        }

        // Mark that we've placed a time marker
        markerPlaced = true;
        ticksSinceMarker = 0;

        // Play sound or other feedback to indicate marker placement
        attacker.playSound(JSoundRegistry.MANDOM_COUNTDOWN.get(), 1, 1);

        return Set.of();
    }

    private void rewindToMarker(final MandomEntity attacker) {
        // Step 4: After 120 ticks, everything goes back to the time marker
        for (Map.Entry<Entity, CompoundTag> data : timeMarkerData.entrySet()) {
            final Entity ent = data.getKey();
            if (!ent.isAlive()) {
                continue;
            }
            final CompoundTag nbt = data.getValue();

            // Unlike ReturnToZero, we DON'T preserve user inventory/items - everything gets rewound
            if (ent instanceof final ServerPlayer serverPlayer) {
                // Send motion packet
                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(ent));

                // Teleport to original position from NBT
                final var posList = nbt.getList("Pos", 6);
                serverPlayer.teleportToWithTicket(posList.getDouble(0), posList.getDouble(1), posList.getDouble(2));
            }

            // Load the complete NBT data (including inventory for players)
            ent.load(nbt);
        }

        // Clear the marker data after rewinding
        timeMarkerData.clear();
        rewindInfo.clear();

        // Play rewind sound
        attacker.playSound(JSoundRegistry.MANDOM_REWIND.get(), 1, 1);
    }

    public void tickRewindInfo(final MandomEntity attacker, final int ticksSince) {
        if (!(attacker.getUser() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        // Only send particles every 3 ticks (slower generation)
        if (ticksSince % 3 != 0) {
            return;
        }

        for (RewindData data : rewindInfo) {
            final Entity entity = data.entity();
            if (entity == null || !entity.isAlive()) {
                continue;
            }
            final Vec3 position = data.originalPos();
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeShort(8); // Using packet ID 8 for Six Second Rewind

            buf.writeInt(entity.getId());

            buf.writeDouble(position.x());
            buf.writeDouble(position.y());
            buf.writeDouble(position.z());

            ServerChannelFeedbackPacket.send(serverPlayer, buf);
        }
    }

    @Override
    protected @NonNull SixSecondRewind getThis() {
        return this;
    }

    @Override
    public @NonNull SixSecondRewind copy() {
        return copyExtras(new SixSecondRewind(getCooldown(), getWindup(), getDuration(), getMoveDistance()));
    }

    record RewindData(Vec3 originalPos, Entity entity) {
        // nothing else needed
    }

    public static class Type extends AbstractMove.Type<SixSecondRewind> {
        public static final Type INSTANCE = new Type();

        @Override
        protected @NotNull App<RecordCodecBuilder.Mu<SixSecondRewind>, SixSecondRewind> buildCodec(RecordCodecBuilder.Instance<SixSecondRewind> instance) {
            return baseDefault(instance, SixSecondRewind::new);
        }
    }
}