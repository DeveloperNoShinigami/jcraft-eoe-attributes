package net.arna.jcraft.fabric.common.component.impl.entity;

import net.arna.jcraft.common.component.impl.entity.CommonGrabComponentImpl;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.entity.GrabComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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

    @Override
    public void sync(Entity entity) {
        JComponents.GRAB.sync(grabbed);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayer player) {
        return super.shouldSyncWith(player);
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
        super.writeSyncPacket(buf, recipient);
    }

    @Override
    public void applySyncPacket(FriendlyByteBuf buf) {
        super.applySyncPacket(buf);
    }

    @Override
    public void readFromNbt(@NotNull CompoundTag tag) {
        super.readFromNbt(tag);
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag) {
        super.writeToNbt(tag);
    }
}
