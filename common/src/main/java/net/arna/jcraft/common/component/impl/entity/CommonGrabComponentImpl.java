package net.arna.jcraft.common.component.impl.entity;

import lombok.Getter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.entity.CommonGrabComponent;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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
        sync();
    }

    @Override
    public void endGrab() {
        this.attacker = null;
        this.duration = 0;
        sync();
    }

    public void tick() {
        if (attacker != null) {
            if (attacker.isAlive() && duration-- > 0) {
                Direction gravity = GravityChangerAPI.getGravityDirection(attacker);
                Vec3d newPos = attacker.getPos()
                        .add(RotationUtil.vecPlayerToWorld(new Vec3d(0, verticalOffset, 0), gravity))
                        .add(attacker.getRotationVector().multiply(distance));
                if (!attacker.getWorld().isTopSolid(BlockPos.ofFloored(newPos), grabbed)) {
                    grabbed.setPosition(newPos);
                }
            } else {
                endGrab();
            }
        }
    }

    public void sync() {
        //JComponentPlatformUtils.GRAB.sync(grabbed);
    }

    public boolean shouldSyncWith(ServerPlayerEntity player) {
        // It'll be passively synced in a choppy way for those far away
        return player.squaredDistanceTo(grabbed) <= 6400; // 5 chunks
    }

    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
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

    public void applySyncPacket(PacketByteBuf buf) {
        if (buf.readBoolean()) {
            return;
        }
        attacker = grabbed.getWorld().getEntityById(buf.readVarInt());
        duration = buf.readVarInt();
        distance = buf.readDouble();
        verticalOffset = buf.readDouble();
    }

    public void readFromNbt(@NotNull NbtCompound tag) {
    }

    public void writeToNbt(@NotNull NbtCompound tag) {
    }
}
