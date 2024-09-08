package net.arna.jcraft.fabric.common.component.impl.player;

import lombok.NonNull;
import net.arna.jcraft.common.component.impl.player.CommonSpecComponentImpl;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.player.SpecComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SpecComponentImpl extends CommonSpecComponentImpl implements SpecComponent {
    public SpecComponentImpl(LivingEntity livingEntity) {
        super(livingEntity);
    }

    @Override
    public void sync(Entity entity) {
        JComponents.SPEC.sync(user);
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
