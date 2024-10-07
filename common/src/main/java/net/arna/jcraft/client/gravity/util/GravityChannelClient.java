package net.arna.jcraft.client.gravity.util;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.gravity.util.GravityVerifierRegistry;
import net.arna.jcraft.common.gravity.util.packet.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class GravityChannelClient<P extends GravityPacket> {
    public static GravityChannelClient<OverwriteGravityPacket> OVERWRITE_GRAVITY = new GravityChannelClient<>(OverwriteGravityPacket::new, JCraft.id("g_overwrite_gravity_list"));
    public static GravityChannelClient<UpdateGravityPacket> UPDATE_GRAVITY = new GravityChannelClient<>(UpdateGravityPacket::new, JCraft.id("g_update_gravity_list"));
    public static GravityChannelClient<DefaultGravityPacket> DEFAULT_GRAVITY = new GravityChannelClient<>(DefaultGravityPacket::new, JCraft.id("g_default_gravity"));
    public static GravityChannelClient<InvertGravityPacket> INVERT_GRAVITY = new GravityChannelClient<>(InvertGravityPacket::new, JCraft.id("g_inverted"));

    private final Function<FriendlyByteBuf, P> packetFactory;
    private final ResourceLocation channel;
    private final GravityVerifierRegistry<P> gravityVerifierRegistry;

    GravityChannelClient(final Function<FriendlyByteBuf, P> _packetFactory, final ResourceLocation _channel) {
        packetFactory = _packetFactory;
        channel = _channel;
        gravityVerifierRegistry = new GravityVerifierRegistry<>();
    }

    public void receiveFromServer(final Minecraft client, final FriendlyByteBuf buf) {
        final int entityId = buf.readInt();
        final P packet = packetFactory.apply(buf);
        client.execute(() -> NetworkUtilClient.getGravityComponent(client, entityId).ifPresent(packet::run));
    }

    public void sendToServer(final P packet, final ResourceLocation verifier, final FriendlyByteBuf verifierInfoBuf) {
        final FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packet.write(buf);
        buf.writeResourceLocation(verifier);
        buf.writeByteArray(verifierInfoBuf.array());
        NetworkManager.sendToServer(channel, buf);
    }

    public GravityVerifierRegistry<P> getVerifierRegistry() {
        return gravityVerifierRegistry;
    }

    public void registerClientReceiver() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, channel, (buf, d) -> receiveFromServer(Minecraft.getInstance(), buf));
    }

    public static void init() {
        DEFAULT_GRAVITY.registerClientReceiver();
        UPDATE_GRAVITY.registerClientReceiver();
        OVERWRITE_GRAVITY.registerClientReceiver();
        INVERT_GRAVITY.registerClientReceiver();
    }
}
