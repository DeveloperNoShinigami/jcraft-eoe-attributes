package net.arna.jcraft.forge.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncPlayerS2CPacket {

    private final UUID uuid;
    private final NbtCompound nbt;

    public SyncPlayerS2CPacket(PacketByteBuf buf) {
        // Decode data into a message
        uuid = buf.readUuid();
        nbt = buf.readNbt();
    }

    public SyncPlayerS2CPacket(UUID uuid, NbtCompound nbt) {
        this.uuid = uuid;
        this.nbt = nbt;
        // Message creation
    }

    public void encode(PacketByteBuf buf) {
        // Encode data into the buf
        buf.writeUuid(uuid);
        buf.writeNbt(nbt);
    }

    @OnlyIn(Dist.CLIENT)
    public void applyClient(Supplier<NetworkManager.PacketContext> ctx){
        PlayerEntity player = MinecraftClient.getInstance().world.getPlayerByUuid(uuid);
        //JPlayerDataCapability.getCapabilityOptional(player).ifPresent(c -> c.deserializeNBT(nbt));
    }
}
