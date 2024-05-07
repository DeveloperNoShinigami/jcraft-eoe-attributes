package net.arna.jcraft.fabric.common.component.impl.living;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.component.impl.living.CommonMiscComponentImpl;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.living.MiscComponent;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MiscComponentImpl extends CommonMiscComponentImpl implements MiscComponent {
    private final Entity entity;

    public MiscComponentImpl(Entity entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void sync() {
        JComponents.MISC.sync(entity);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return super.shouldSyncWith(player);
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        MiscComponent.super.writeSyncPacket(buf, recipient);
        super.writeSyncPacket(buf, recipient);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        MiscComponent.super.applySyncPacket(buf);
        super.applySyncPacket(buf);
    }

    @Override
    public void readFromNbt(@NonNull NbtCompound tag) {
        super.readFromNbt(tag);
    }

    @Override
    public void writeToNbt(@NonNull NbtCompound tag) {
        super.writeToNbt(tag);
    }
}
