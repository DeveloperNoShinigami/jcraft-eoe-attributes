package net.arna.jcraft.fabric.common.component.impl.player;

import lombok.NonNull;
import net.arna.jcraft.common.component.impl.player.CommonPhComponentImpl;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.player.PhComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class PhComponentImpl extends CommonPhComponentImpl implements PhComponent {
    private final PlayerEntity player;

    public PhComponentImpl(PlayerEntity player) {
        super(player);
        this.player = player;
    }

    @Override
    public void sync(Entity entity) {
        JComponents.PH.sync(player);
    }

    @Override
    public void readFromNbt(@NonNull NbtCompound tag) {
        super.readFromNbt(tag);
    }

    @Override
    public void writeToNbt(@NonNull NbtCompound tag) {
        super.writeToNbt(tag);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return super.shouldSyncWith(player);
    }
}
