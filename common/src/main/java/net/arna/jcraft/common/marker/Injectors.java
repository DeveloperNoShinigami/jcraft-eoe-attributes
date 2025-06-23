package net.arna.jcraft.common.marker;

import net.arna.jcraft.common.util.NbtUtils;
import net.arna.jcraft.common.util.TriConsumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

import static net.arna.jcraft.common.marker.Identifiers.*;

public interface Injectors {

    // We only land here if a property should be included
    // so if we *don't* find the id's tag we must assume it
    // wasn't there and hence must be deleted.

    TriConsumer<ResourceLocation, Entity, CompoundTag> ENTITY = (id, entity, compoundTag) -> {
        if (id == null) {
            return;
        }
        if (id.equals(POSITION) && compoundTag.contains(POSITION.toString()) && compoundTag.contains(PITCH.toString()) && compoundTag.contains(YAW.toString()) && compoundTag.contains(YAW_HEAD.toString())) {
            final Vec3 pos = NbtUtils.getVec3(compoundTag, POSITION.toString());
            final float pitch = compoundTag.getFloat(PITCH.toString());
            final float yaw = compoundTag.getFloat(YAW.toString());
            final float yawHead = compoundTag.getFloat(YAW_HEAD.toString());
            if (entity instanceof final ServerPlayer serverPlayer) {
                // use teleportTo with proper rotation handling
                serverPlayer.teleportTo(serverPlayer.serverLevel(), pos.x(), pos.y(), pos.z(),
                        EnumSet.noneOf(RelativeMovement.class), yaw, pitch);
                // force update head rotation for other players
                serverPlayer.setYHeadRot(yawHead);
                serverPlayer.connection.send(new ClientboundRotateHeadPacket(serverPlayer, (byte)((int)(yaw * 256.0F / 360.0F))));
                serverPlayer.connection.send(new ClientboundTeleportEntityPacket(serverPlayer));
            }
            else {
                entity.teleportTo(pos.x(), pos.y(), pos.z());
                entity.setYRot(yaw);
                entity.setXRot(pitch);
                entity.setYHeadRot(yawHead);
                entity.setYBodyRot(yaw);
                entity.yRotO = yaw;
                entity.xRotO = pitch;
                if (entity instanceof final LivingEntity livingEntity) {
                    livingEntity.yHeadRotO = yawHead;
                    livingEntity.yBodyRotO = yaw;
                }
            }
        }
        else if (id.equals(VELOCITY)) {
            if (compoundTag.contains(VELOCITY.toString())) {
                entity.setDeltaMovement(NbtUtils.getVec3(compoundTag, VELOCITY.toString()));
            }
            else {
                entity.setDeltaMovement(0d, 0d, 0d);
            }
            if (entity instanceof final ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
            }
        }
        else if (id.equals(FALL_DISTANCE)) {
            if (compoundTag.contains(FALL_DISTANCE.toString())) {
                entity.fallDistance = compoundTag.getFloat(FALL_DISTANCE.toString());
            }
            else {
                entity.fallDistance = 0f;
            }
        }
    };

}
