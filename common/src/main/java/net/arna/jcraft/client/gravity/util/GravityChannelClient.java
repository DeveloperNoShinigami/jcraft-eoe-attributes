package net.arna.jcraft.client.gravity.util;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.gravity.util.GravityVerifierRegistry;
import net.arna.jcraft.common.gravity.util.packet.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class GravityChannelClient<P extends GravityPacket> {
    public static GravityChannelClient<OverwriteGravityPacket> OVERWRITE_GRAVITY = new GravityChannelClient<>(OverwriteGravityPacket::new, JCraft.id("g_overwrite_gravity_list"));
    public static GravityChannelClient<UpdateGravityPacket> UPDATE_GRAVITY = new GravityChannelClient<>(UpdateGravityPacket::new, JCraft.id("g_update_gravity_list"));
    public static GravityChannelClient<DefaultGravityPacket> DEFAULT_GRAVITY = new GravityChannelClient<>(DefaultGravityPacket::new, JCraft.id("g_default_gravity"));
    public static GravityChannelClient<InvertGravityPacket> INVERT_GRAVITY = new GravityChannelClient<>(InvertGravityPacket::new, JCraft.id("g_inverted"));

    private final Factory<P> packetFactory;
    private final Identifier channel;
    private final GravityVerifierRegistry<P> gravityVerifierRegistry;

    GravityChannelClient(Factory<P> _packetFactory, Identifier _channel) {
        packetFactory = _packetFactory;
        channel = _channel;
        gravityVerifierRegistry = new GravityVerifierRegistry<>();
    }

    public void receiveFromServer(MinecraftClient client, PacketByteBuf buf) {
        int entityId = buf.readInt();
        P packet = packetFactory.read(buf);
        client.execute(() -> NetworkUtilClient.getGravityComponent(client, entityId).ifPresent(packet::run));
    }

    public void sendToServer(P packet, Identifier verifier, PacketByteBuf verifierInfoBuf) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.write(buf);
        buf.writeIdentifier(verifier);
        buf.writeByteArray(verifierInfoBuf.array());
        NetworkManager.sendToServer(channel, buf);
    }

    public GravityVerifierRegistry<P> getVerifierRegistry() {
        return gravityVerifierRegistry;
    }

    public void registerClientReceiver() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, channel, (buf, d) -> receiveFromServer(MinecraftClient.getInstance(), buf));
    }

    public static void init() {
        DEFAULT_GRAVITY.registerClientReceiver();
        UPDATE_GRAVITY.registerClientReceiver();
        OVERWRITE_GRAVITY.registerClientReceiver();
        INVERT_GRAVITY.registerClientReceiver();
    }

    @FunctionalInterface
    interface Factory<T extends GravityPacket> {
        T read(PacketByteBuf buf);
    }
}
