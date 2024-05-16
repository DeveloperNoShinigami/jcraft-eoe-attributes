package net.arna.jcraft.fabric.common.component.impl.living;

import net.arna.jcraft.common.component.impl.living.CommonBombTrackerComponentImpl;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.living.BombTrackerComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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
    public void sync(Entity entity) {
        JComponents.BOMB_TRACKER.sync(entity);
        super.sync(entity);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayer player) {
        return player == entity;
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
