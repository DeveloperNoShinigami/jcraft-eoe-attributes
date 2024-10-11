package net.arna.jcraft.common.gravity.util;

import dev.architectury.networking.NetworkManager;
import lombok.NonNull;
import net.arna.jcraft.common.component.entity.CommonGravityComponent;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.api.RotationParameters;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class NetworkUtil {
    //PacketMode
    public enum PacketMode {
        EVERYONE,
        EVERYONE_BUT_SELF,
        ONLY_SELF
    }

    //Access gravity component

    public static Optional<CommonGravityComponent> getGravityComponent(Entity entity) {
        CommonGravityComponent gc = GravityChangerAPI.getGravityComponent(entity);
        if (gc == null) {
            return Optional.empty();
        }
        return Optional.of(gc);
    }

    //Sending packets to players that are tracking an entity
    public static void sendToTracking(@NonNull final Entity entity, final ResourceLocation channel,
                                      final FriendlyByteBuf buf, final PacketMode mode) {
        // PlayerLookup.tracking(entity) might not return the player if entity is a player, so it has to be done separately
        if (mode != PacketMode.EVERYONE_BUT_SELF) {
            if (entity instanceof ServerPlayer player) {
                // NetworkManager.sendToPlayer methods consume the buffer.
                NetworkManager.sendToPlayer(player, channel, new FriendlyByteBuf(buf.copy()));
            }
        }
        if (mode != PacketMode.ONLY_SELF) {
            final Set<ServerPlayer> recipients = JUtils.tracking(entity)
                    .stream()
                    .filter(serverPlayer -> serverPlayer != entity)
                    .collect(Collectors.toUnmodifiableSet());
            NetworkManager.sendToPlayers(recipients, channel, buf);
        }
    }

    //Writing to buffer

    public static void writeDirection(FriendlyByteBuf buf, Direction direction) {
        buf.writeByte(direction == null ? -1 : direction.get3DDataValue());
    }

    public static void writeRotationParameters(FriendlyByteBuf buf, RotationParameters rotationParameters) {
        buf.writeBoolean(rotationParameters.rotateVelocity());
        buf.writeBoolean(rotationParameters.rotateView());
        buf.writeBoolean(rotationParameters.alternateCenter());
        buf.writeInt(rotationParameters.rotationTime());
    }

    public static void writeGravity(FriendlyByteBuf buf, Gravity gravity) {
        writeDirection(buf, gravity.direction());
        buf.writeInt(gravity.priority());
        buf.writeInt(gravity.duration());
        buf.writeUtf(gravity.source());
        writeRotationParameters(buf, gravity.rotationParameters());
    }

    //Reading from buffer

    public static RotationParameters readRotationParameters(FriendlyByteBuf buf) {
        return new RotationParameters(
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readInt()
        );
    }

    public static Direction readDirection(FriendlyByteBuf buf) {
        int rawDirection = buf.readByte();
        return (0 <= rawDirection && rawDirection < Direction.values().length) ? Direction.from3DDataValue(rawDirection) : null;
    }

    public static Gravity readGravity(FriendlyByteBuf buf) {
        return new Gravity(
                readDirection(buf),
                buf.readInt(),
                buf.readInt(),
                buf.readUtf(),
                readRotationParameters(buf)
        );
    }
}
