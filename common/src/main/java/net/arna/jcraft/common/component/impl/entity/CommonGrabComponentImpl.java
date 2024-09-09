package net.arna.jcraft.common.component.impl.entity;

import lombok.Getter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.entity.CommonGrabComponent;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class CommonGrabComponentImpl implements CommonGrabComponent {
    /**
     * The grabbed entity.
     * Grab logic is ran from the side of the victim, which prevents multiple attackers from attempting to grab it.
     */
    @Getter
    private final Entity grabbed;
    @Getter
    public Entity attacker = null;
    @Getter
    public int duration = 0;
    private double distance, verticalOffset = 0.4;

    public CommonGrabComponentImpl(Entity grabbed) {
        this.grabbed = grabbed;
    }

    @Override
    public void startGrab(Entity attacker, int duration, double distance) {
        startGrab(attacker, duration, distance, 0.4);
    }

    @Override
    public void startGrab(Entity attacker, int duration, double distance, double verticalOffset) {
        if (attacker == null) {
            JCraft.LOGGER.warn("Null attacker tried to grab: " + grabbed);
            return;
        }

        this.attacker = attacker;
        this.duration = duration;
        this.distance = distance;
        this.verticalOffset = verticalOffset;
        sync(grabbed);
    }

    @Override
    public void endGrab() {
        this.attacker = null;
        this.duration = 0;
        sync(grabbed);
    }

    public void tick() {
        if (attacker != null) {
            if (attacker.isAlive() && duration-- > 0) {
                Direction gravity = GravityChangerAPI.getGravityDirection(attacker);
                Vec3 newPos = attacker.position()
                        .add(RotationUtil.vecPlayerToWorld(new Vec3(0, verticalOffset, 0), gravity))
                        .add(attacker.getLookAngle().scale(distance));
                if (!attacker.level().loadedAndEntityCanStandOn(BlockPos.containing(newPos), grabbed)) {
                    grabbed.setPos(newPos);
                }
                grabbed.setDeltaMovement(Vec3.ZERO);
            } else {
                endGrab();
            }
        }
    }

    public void sync(Entity entity) {
        //JComponentPlatformUtils.GRAB.sync(grabbed);
    }

    public boolean shouldSyncWith(ServerPlayer player) {
        // It'll be passively synced in a choppy way for those far away
        return player.distanceToSqr(grabbed) <= 6400; // 5 chunks
    }

    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
        boolean notGrabbing = attacker == null;
        buf.writeBoolean(notGrabbing);
        if (notGrabbing) {
            return;
        }
        buf.writeVarInt(attacker.getId());
        buf.writeVarInt(duration);
        buf.writeDouble(distance);
        buf.writeDouble(verticalOffset);
    }

    public void applySyncPacket(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            return;
        }
        attacker = grabbed.level().getEntity(buf.readVarInt());
        duration = buf.readVarInt();
        distance = buf.readDouble();
        verticalOffset = buf.readDouble();
    }

    public void readFromNbt(@NotNull CompoundTag tag) {
    }

    public void writeToNbt(@NotNull CompoundTag tag) {
    }
}
