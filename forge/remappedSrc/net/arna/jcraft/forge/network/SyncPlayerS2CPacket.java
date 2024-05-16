package net.arna.jcraft.forge.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncPlayerS2CPacket {

    private final UUID uuid;
    private final CompoundTag nbt;

    public SyncPlayerS2CPacket(FriendlyByteBuf buf) {
        // Decode data into a message
        uuid = buf.readUUID();
        nbt = buf.readNbt();
    }

    public SyncPlayerS2CPacket(UUID uuid, CompoundTag nbt) {
        this.uuid = uuid;
        this.nbt = nbt;
        // Message creation
    }

    public void encode(FriendlyByteBuf buf) {
        // Encode data into the buf
        buf.writeUUID(uuid);
        buf.writeNbt(nbt);
    }

    @OnlyIn(Dist.CLIENT)
    public void applyClient(Supplier<NetworkManager.PacketContext> ctx){
        Player player = Minecraft.getInstance().level.getPlayerByUUID(uuid);
        //JPlayerDataCapability.getCapabilityOptional(player).ifPresent(c -> c.deserializeNBT(nbt));
    }
}
