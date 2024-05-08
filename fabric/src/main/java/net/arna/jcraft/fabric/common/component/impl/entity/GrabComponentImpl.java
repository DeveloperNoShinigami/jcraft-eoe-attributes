package net.arna.jcraft.fabric.common.component.impl.entity;

import net.arna.jcraft.common.component.impl.entity.CommonGrabComponentImpl;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.entity.GrabComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class GrabComponentImpl extends CommonGrabComponentImpl implements GrabComponent {

    private Entity grabbed;

    public GrabComponentImpl(Entity grabbed) {
        super(grabbed);
        this.grabbed = grabbed;
    }

    @Override
    public void tick() {
        super.tick();
    }

    public void sync() {
        JComponents.GRAB.sync(grabbed);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return super.shouldSyncWith(player);
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
    public void readFromNbt(@NotNull NbtCompound tag) {
        super.readFromNbt(tag);
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        super.writeToNbt(tag);
    }
}
