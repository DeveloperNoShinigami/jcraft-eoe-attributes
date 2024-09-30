package net.arna.jcraft.fabric.common.component.impl.living;

import lombok.NonNull;
import net.arna.jcraft.common.component.impl.living.CommonStandComponentImpl;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.living.StandComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class StandComponentImpl extends CommonStandComponentImpl implements StandComponent {

    public StandComponentImpl(Entity entity) {
        super(entity);
    }

    @Override
    public void sync(Entity entity) {
        JComponents.STAND.sync(entity);
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
    public void applySyncPacket(FriendlyByteBuf buf) {
        StandComponent.super.applySyncPacket(buf);

        super.applySyncPacket(buf);
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
        StandComponent.super.writeSyncPacket(buf, recipient);

        super.writeSyncPacket(buf, recipient);
    }
}
