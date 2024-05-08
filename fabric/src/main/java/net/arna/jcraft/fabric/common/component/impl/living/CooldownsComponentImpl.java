package net.arna.jcraft.fabric.common.component.impl.living;

import lombok.NonNull;
import net.arna.jcraft.common.component.impl.living.CommonCooldownsComponentImpl;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.living.CooldownsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class CooldownsComponentImpl extends CommonCooldownsComponentImpl implements CooldownsComponent {

    private final Entity entity;

    public CooldownsComponentImpl(@NonNull Entity entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public void sync() {
        if (skipSync) return; // To avoid packet spam.
        JComponents.COOLDOWNS.sync(entity);
    }

    @Override
    public void tick() {
        super.tick();
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
