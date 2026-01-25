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

import java.util.UUID;

public class CommonHamonComponentImpl implements CommonHamonComponent {

    private final LivingEntity entity;
    // only needs to be synchronized to the client, not persisted
    @Getter
    private float hamonCharge = 0.0f;
    // only needs to be synchronized to the client, not persisted
    @Getter
    private boolean hamonizeReady = false;
    // doesn't need to be synchronized to the client and also not persisted
    @Getter
    private UUID lastZoomPunched;
    @Getter
    private int lastZoomPunchedTick = Integer.MIN_VALUE;
    // doesn't need to be synchronized to the client and also not persisted
    @Getter
    private UUID lastSendoed;
    @Getter
    private int lastSendoedTick = Integer.MIN_VALUE;

    public CommonHamonComponentImpl(final LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void setHamonCharge(float charge) {
        hamonCharge = charge;
        sync(entity);
    }

    public void setHamonizeReady(final boolean ready) {
        hamonizeReady = ready;
        sync(entity);
    }

    @Override
    public void setLastZoomPunched(final @NonNull UUID lastZoomPunched, final int tick) {
        this.lastZoomPunched = lastZoomPunched;
        lastZoomPunchedTick = tick;
    }

    public void resetLastZoomPunched() {
        lastZoomPunched = null;
        lastZoomPunchedTick = Integer.MIN_VALUE;
    }

    @Override
    public void setLastSendoed(final @NonNull UUID lastSendoed, final int tick) {
        this.lastSendoed = lastSendoed;
        lastSendoedTick = tick;
    }

    public void resetLastSendoed() {
        lastSendoed = null;
        lastSendoedTick = Integer.MIN_VALUE;
    }

    public void sync(final Entity entity) {
    }

    public boolean shouldSyncWith(final ServerPlayer player) {
        return true;
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
