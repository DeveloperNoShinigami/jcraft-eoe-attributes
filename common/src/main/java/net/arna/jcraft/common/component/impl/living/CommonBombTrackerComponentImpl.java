package net.arna.jcraft.common.component.impl.living;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.living.CommonBombTrackerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class CommonBombTrackerComponentImpl implements CommonBombTrackerComponent {
    private final Entity entity;
    public final BombData main = new BombData();
    public final BombData btd = new BombData();

    public CommonBombTrackerComponentImpl(@NotNull Entity entity) {
        this.entity = entity;
    }

    @Override
    public BombData getMainBomb() {
        return main;
    }

    @Deprecated
    @Override
    public BombData getBTD() {
        return btd;
    }

    public void tick() {
        World world = entity.getWorld();

        if (world.isClient) {
            JCraft.getClientEntityHandler().bombTrackerParticleTick(entity, main);
        } else {
            if (main.dirty)
                sync();
            /*
            if (btd.dirty)
                sync();
             */
        }
    }

    public void sync() {
        //JComponentPlatformUtils.BOMB_TRACKER.sync(entity);
        main.dirty = false;
        btd.dirty = false;
    }

    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == entity;
    }

    public void syncBombData(PacketByteBuf buf, BombData bombData) {
        buf.writeBoolean(bombData.isBlock);
        buf.writeBoolean(bombData.isEntity);
        buf.writeBoolean(bombData.isItem);
        if (bombData.isEntity)
            buf.writeVarInt(bombData.bombEntity.getId());
        if (bombData.isBlock)
            buf.writeBlockPos(bombData.bombBlock);
    }

    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        syncBombData(buf, main);
        //syncBombData(buf, btd);
    }

    public void readBombData(PacketByteBuf buf, BombData bombData) {
        bombData.isBlock = buf.readBoolean();
        bombData.isEntity = buf.readBoolean();
        bombData.isItem = buf.readBoolean();
        if (bombData.isEntity)
            bombData.bombEntity = entity.getWorld().getEntityById(buf.readVarInt());
        if (bombData.isBlock)
            bombData.bombBlock = buf.readBlockPos();
    }

    public void applySyncPacket(PacketByteBuf buf) {
        readBombData(buf, main);
        //readBombData(buf, btd);
    }


    public void readFromNbt(@NotNull NbtCompound tag) { }

    public void writeToNbt(@NotNull NbtCompound tag) { }
}
