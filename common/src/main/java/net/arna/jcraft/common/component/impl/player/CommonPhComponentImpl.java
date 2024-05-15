package net.arna.jcraft.common.component.impl.player;

import lombok.NonNull;
import net.arna.jcraft.common.component.player.CommonPhComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class CommonPhComponentImpl implements CommonPhComponent {
    private final PlayerEntity player;
    private int level = 0;

    public CommonPhComponentImpl(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void increaseLevel() {
        level++;
    }

    @Override
    public void resetLevel() {
        level = 0;
    }

    public void sync(Entity entity) {
    }

    public void readFromNbt(@NonNull NbtCompound tag) {
        level = tag.getInt("level");
    }

    public void writeToNbt(@NonNull NbtCompound tag) {
        tag.putInt("level", level);
    }

    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.player; // Only our player needs to know, I believe.
    }
}
