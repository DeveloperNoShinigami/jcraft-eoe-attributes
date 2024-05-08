package net.arna.jcraft.common.gravity.util;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.gravity.api.RotationParameters;
import net.arna.jcraft.common.gravity.util.packet.*;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static net.arna.jcraft.common.gravity.util.NetworkUtil.*;

public class GravityChannel<P extends GravityPacket> {
    public static GravityChannel<OverwriteGravityPacket> OVERWRITE_GRAVITY = new GravityChannel<>(OverwriteGravityPacket::new, JCraft.id("g_overwrite_gravity_list"));
    public static GravityChannel<UpdateGravityPacket> UPDATE_GRAVITY = new GravityChannel<>(UpdateGravityPacket::new, JCraft.id("g_update_gravity_list"));
    public static GravityChannel<DefaultGravityPacket> DEFAULT_GRAVITY = new GravityChannel<>(DefaultGravityPacket::new, JCraft.id("g_default_gravity"));
    public static GravityChannel<InvertGravityPacket> INVERT_GRAVITY = new GravityChannel<>(InvertGravityPacket::new, JCraft.id("g_inverted"));

    private final Factory<P> packetFactory;
    private final Identifier channel;
    private final GravityVerifierRegistry<P> gravityVerifierRegistry;

    GravityChannel(Factory<P> _packetFactory, Identifier _channel) {
        packetFactory = _packetFactory;
        channel = _channel;
        gravityVerifierRegistry = new GravityVerifierRegistry<>();
    }

    public void sendToClient(Entity entity, P packet, PacketMode mode) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        packet.write(buf);
        sendToTracking(entity, channel, buf, mode);
    }

    public void receiveFromClient(PacketByteBuf buf, MinecraftServer server, ServerPlayerEntity player) {
        P packet = packetFactory.read(buf);
        Identifier verifier = buf.readIdentifier();
        PacketByteBuf verifierInfoBuf = new PacketByteBuf(Unpooled.buffer());
        verifierInfoBuf.writeBytes(buf.readByteArray());
        server.execute(() -> getGravityComponent(player).ifPresent(gc -> {
            GravityVerifierRegistry.VerifierFunction<P> v = gravityVerifierRegistry.get(verifier);
            if (v != null && v.check(player, verifierInfoBuf, packet)) {
                packet.run(gc);
                sendToClient(player, packet, PacketMode.EVERYONE_BUT_SELF);
            } else {
                sendFullStatePacket(player, PacketMode.ONLY_SELF, packet.getRotationParameters(), false);
            }
        }));
    }

    public static void sendFullStatePacket(Entity entity, PacketMode mode, RotationParameters rp, boolean initialGravity) {
        getGravityComponent(entity).ifPresent(gc -> {
            OVERWRITE_GRAVITY.sendToClient(entity, new OverwriteGravityPacket(gc.getGravity(), initialGravity), mode);
            DEFAULT_GRAVITY.sendToClient(entity, new DefaultGravityPacket(gc.getDefaultGravityDirection(), rp, initialGravity), mode);
            INVERT_GRAVITY.sendToClient(entity, new InvertGravityPacket(gc.getInvertGravity(), rp, initialGravity), mode);
        });
    }

    public GravityVerifierRegistry<P> getVerifierRegistry() {
        return gravityVerifierRegistry;
    }

    public void registerServerReceiver() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, channel, (buf, context) -> {
            receiveFromClient(buf, context.getPlayer().getServer(), (ServerPlayerEntity) context.getPlayer());
        });
    }

    public static void init() {
        DEFAULT_GRAVITY.registerServerReceiver();
        UPDATE_GRAVITY.registerServerReceiver();
        OVERWRITE_GRAVITY.registerServerReceiver();
        INVERT_GRAVITY.registerServerReceiver();
    }

    @FunctionalInterface
    interface Factory<T extends GravityPacket> {
        T read(PacketByteBuf buf);
    }
}
