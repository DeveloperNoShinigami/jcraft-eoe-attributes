package net.arna.jcraft.fabric.common.component.impl.living;

import net.arna.jcraft.common.component.impl.living.CommonHitPropertyComponentImpl;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.living.HitPropertyComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class HitPropertyComponentImpl extends CommonHitPropertyComponentImpl implements HitPropertyComponent {
    private final Entity entity;

    public HitPropertyComponentImpl(Entity entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void sync(Entity entity) {
        JComponents.HIT_PROPERTY.sync(entity);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return super.shouldSyncWith(player);
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        super.writeSyncPacket(buf, recipient);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        super.applySyncPacket(buf);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        super.readFromNbt(tag);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        super.writeToNbt(tag);
    }
}
