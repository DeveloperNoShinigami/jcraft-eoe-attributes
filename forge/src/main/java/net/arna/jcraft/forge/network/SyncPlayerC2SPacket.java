package net.arna.jcraft.forge.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class SyncPlayerC2SPacket {

    private final NbtCompound nbt;

    public SyncPlayerC2SPacket(PacketByteBuf buf) {
        // Decode data into a messag
        nbt = buf.readNbt();
    }

    public SyncPlayerC2SPacket(NbtCompound nbt) {
        this.nbt = nbt;
        // Message creation
    }

    public void encode(PacketByteBuf buf) {
        // Encode data into the buf
        buf.writeNbt(nbt);
    }


    @OnlyIn(Dist.DEDICATED_SERVER)
    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        PlayerEntity player = ctx.get().getPlayer();
        // On receive
        //JPlayerDataCapability.getCapabilityOptional(player).ifPresent(c -> c.deserializeNBT(nbt));
    }
}
