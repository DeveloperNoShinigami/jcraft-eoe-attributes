package net.arna.jcraft.forge.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class SyncPlayerC2SPacket {

    private final CompoundTag nbt;

    public SyncPlayerC2SPacket(FriendlyByteBuf buf) {
        // Decode data into a messag
        nbt = buf.readNbt();
    }

    public SyncPlayerC2SPacket(CompoundTag nbt) {
        this.nbt = nbt;
        // Message creation
    }

    public void encode(FriendlyByteBuf buf) {
        // Encode data into the buf
        buf.writeNbt(nbt);
    }


    @OnlyIn(Dist.DEDICATED_SERVER)
    public void apply(Supplier<NetworkManager.PacketContext> ctx) {
        Player player = ctx.get().getPlayer();
        // On receive
        //JPlayerDataCapability.getCapabilityOptional(player).ifPresent(c -> c.deserializeNBT(nbt));
    }
}
