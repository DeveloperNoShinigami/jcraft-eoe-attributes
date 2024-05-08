package net.arna.jcraft.fabric.common.component.impl.world;

import net.arna.jcraft.common.component.impl.world.CommonShockwaveHandlerComponentImpl;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.world.ShockwaveHandlerComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ShockwaveHandlerComponentImpl extends CommonShockwaveHandlerComponentImpl implements ShockwaveHandlerComponent {

    private final World world;

    public ShockwaveHandlerComponentImpl(World world) {
        super(world);
        this.world = world;
    }


    @Override
    public void sync(Shockwave shockwave) {
        JComponents.SHOCKWAVE_HANDLER.sync(world, (buf, player) -> writeSyncPacket(buf, shockwave));
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {
        super.readFromNbt(tag);
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        super.writeToNbt(tag);
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        super.writeSyncPacket(buf, recipient);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        super.applySyncPacket(buf);
    }

    @Override
    public void tick() {
        super.tick();
    }
}
