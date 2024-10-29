package net.arna.jcraft.common.gravity.util;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import io.netty.buffer.Unpooled;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.gravity.api.RotationParameters;
import net.arna.jcraft.common.gravity.util.packet.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import static net.arna.jcraft.common.gravity.util.NetworkUtil.*;

public class GravityChannel<P extends GravityPacket> {
    public static GravityChannel<OverwriteGravityPacket> OVERWRITE_GRAVITY = new GravityChannel<>(OverwriteGravityPacket::new, JCraft.id("g_overwrite_gravity_list"));
    public static GravityChannel<UpdateGravityPacket> UPDATE_GRAVITY = new GravityChannel<>(UpdateGravityPacket::new, JCraft.id("g_update_gravity_list"));
    public static GravityChannel<DefaultGravityPacket> DEFAULT_GRAVITY = new GravityChannel<>(DefaultGravityPacket::new, JCraft.id("g_default_gravity"));
    public static GravityChannel<InvertGravityPacket> INVERT_GRAVITY = new GravityChannel<>(InvertGravityPacket::new, JCraft.id("g_inverted"));

    private final Factory<P> packetFactory;
    private final ResourceLocation channel;
    private final GravityVerifierRegistry<P> gravityVerifierRegistry;

    GravityChannel(Factory<P> _packetFactory, ResourceLocation _channel) {
        packetFactory = _packetFactory;
        channel = _channel;
        gravityVerifierRegistry = new GravityVerifierRegistry<>();
    }

    public void sendToClient(Entity entity, P packet, PacketMode mode) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(entity.getId());
        packet.write(buf);
        sendToTracking(entity, channel, buf, mode);
    }

    public void receiveFromClient(FriendlyByteBuf buf, MinecraftServer server, ServerPlayer player) {
        P packet = packetFactory.read(buf);
        ResourceLocation verifier = buf.readResourceLocation();
        FriendlyByteBuf verifierInfoBuf = new FriendlyByteBuf(Unpooled.buffer());
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

    //todo: figure out why this is called this way.
    //      its really confusing given S2C means its CLIENTside
    public void registerServerReceiver() {
        if (Platform.getEnvironment() == Env.SERVER) return;
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, channel, (buf, context) -> {
            receiveFromClient(buf, context.getPlayer().getServer(), (ServerPlayer) context.getPlayer());
        });
    }

    public static void registerReceivers() {
        DEFAULT_GRAVITY.registerServerReceiver();
        UPDATE_GRAVITY.registerServerReceiver();
        OVERWRITE_GRAVITY.registerServerReceiver();
        INVERT_GRAVITY.registerServerReceiver();
    }

    @FunctionalInterface
    interface Factory<T extends GravityPacket> {
        T read(FriendlyByteBuf buf);
    }
}
