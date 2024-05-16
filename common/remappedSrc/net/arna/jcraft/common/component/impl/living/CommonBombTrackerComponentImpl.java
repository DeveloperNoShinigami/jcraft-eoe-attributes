package net.arna.jcraft.common.component.impl.living;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.living.CommonBombTrackerComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
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
        Level world = entity.level();

        if (world.isClientSide) {
            JCraft.getClientEntityHandler().bombTrackerParticleTick(entity, main);
        } else {
            if (main.dirty) {
                sync(entity);
            }
            /*
            if (btd.dirty)
                sync();
             */
        }
    }

    public void sync(Entity entity) {
        //JComponentPlatformUtils.BOMB_TRACKER.sync(entity);
        main.dirty = false;
        btd.dirty = false;
    }

    public boolean shouldSyncWith(ServerPlayer player) {
        return player == entity;
    }

    public void syncBombData(FriendlyByteBuf buf, BombData bombData) {
        buf.writeBoolean(bombData.isBlock);
        buf.writeBoolean(bombData.isEntity);
        buf.writeBoolean(bombData.isItem);
        if (bombData.isEntity) {
            buf.writeVarInt(bombData.bombEntity.getId());
        }
        if (bombData.isBlock) {
            buf.writeBlockPos(bombData.bombBlock);
        }
    }

    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
        syncBombData(buf, main);
        //syncBombData(buf, btd);
    }

    public void readBombData(FriendlyByteBuf buf, BombData bombData) {
        bombData.isBlock = buf.readBoolean();
        bombData.isEntity = buf.readBoolean();
        bombData.isItem = buf.readBoolean();
        if (bombData.isEntity) {
            bombData.bombEntity = entity.level().getEntity(buf.readVarInt());
        }
        if (bombData.isBlock) {
            bombData.bombBlock = buf.readBlockPos();
        }
    }

    public void applySyncPacket(FriendlyByteBuf buf) {
        readBombData(buf, main);
        //readBombData(buf, btd);
    }


    public void readFromNbt(@NotNull CompoundTag tag) {
    }

    public void writeToNbt(@NotNull CompoundTag tag) {
    }
}
