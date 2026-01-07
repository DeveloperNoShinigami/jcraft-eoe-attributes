package net.arna.jcraft.common.component.impl.living;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.api.component.living.CommonHamonComponent;
import net.arna.jcraft.common.spec.HamonSpec;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CommonHamonComponentImpl implements CommonHamonComponent {
    private final LivingEntity entity;

    public CommonHamonComponentImpl(final LivingEntity entity) {
        this.entity = entity;
    }

    @Getter
    private float hamonCharge = 0.0f;

    @Override
    public void setHamonCharge(float charge) {
        hamonCharge = charge;
        sync(entity);
    }

    @Getter
    private boolean hamonizeReady = false;

    public void setHamonizeReady(final boolean ready) {
        hamonizeReady = ready;
        sync(entity);
    }

    public void sync(final Entity entity) {
    }

    public boolean shouldSyncWith(final ServerPlayer player) {
        return player == entity;
    }

    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
        buf.writeFloat(hamonCharge);
        buf.writeBoolean(hamonizeReady);
    }

    public void applySyncPacket(FriendlyByteBuf buf) {
        hamonCharge = buf.readFloat();
        hamonizeReady = buf.readBoolean();
    }

    public void readFromNbt(@NonNull CompoundTag tag) {
        // intentionally left empty
    }

    public void writeToNbt(@NonNull CompoundTag tag) {
        // intentionally left empty
    }

    public void tick() {
        if (entity.level().isClientSide()) {
            return;
        }
        if (hamonCharge > 0 || hamonizeReady) {
            if (!(JUtils.getSpec(entity) instanceof HamonSpec)) {
                hamonCharge = 0;
                hamonizeReady = false;
                sync(entity);
            }
        }
    }

}
