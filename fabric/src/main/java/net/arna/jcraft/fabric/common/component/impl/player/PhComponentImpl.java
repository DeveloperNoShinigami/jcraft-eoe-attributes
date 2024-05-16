package net.arna.jcraft.fabric.common.component.impl.player;

import lombok.NonNull;
import net.arna.jcraft.common.component.impl.player.CommonPhComponentImpl;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.player.PhComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class PhComponentImpl extends CommonPhComponentImpl implements PhComponent {
    private final Player player;

    public PhComponentImpl(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public void sync(Entity entity) {
        JComponents.PH.sync(player);
    }

    @Override
    public void readFromNbt(@NonNull CompoundTag tag) {
        super.readFromNbt(tag);
    }

    @Override
    public void writeToNbt(@NonNull CompoundTag tag) {
        super.writeToNbt(tag);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayer player) {
        return super.shouldSyncWith(player);
    }
}
