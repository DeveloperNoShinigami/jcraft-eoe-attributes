package net.arna.jcraft.fabric.common.component.impl.living;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.component.impl.living.CommonStandComponentImpl;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.living.StandComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class StandComponentImpl extends CommonStandComponentImpl implements StandComponent {
    private final Entity entity;

    public StandComponentImpl(Entity entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public void sync() {
        JComponents.STAND.sync(entity);
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
    public void applySyncPacket(PacketByteBuf buf) {
        StandComponent.super.applySyncPacket(buf);

        super.applySyncPacket(buf);
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        StandComponent.super.writeSyncPacket(buf, recipient);

        super.writeSyncPacket(buf, recipient);
    }
}
