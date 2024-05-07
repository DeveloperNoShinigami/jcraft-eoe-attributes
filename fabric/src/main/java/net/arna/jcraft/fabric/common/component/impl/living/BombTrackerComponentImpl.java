package net.arna.jcraft.fabric.common.component.impl.living;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.impl.living.CommonBombTrackerComponentImpl;
import net.arna.jcraft.common.component.living.CommonBombTrackerComponent;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.living.BombTrackerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class BombTrackerComponentImpl extends CommonBombTrackerComponentImpl implements BombTrackerComponent {
    private final Entity entity;

    public BombTrackerComponentImpl(@NotNull Entity entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void sync() {
        JComponents.BOMB_TRACKER.sync(entity);
        super.sync();
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == entity;
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
