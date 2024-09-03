package net.arna.jcraft.common.component.impl.living;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class CommonStandComponentImpl implements CommonStandComponent {
    private final Entity entity;
    private StandEntity<?, ?> stand;
    private StandType type;
    @Getter
    private int skin;

    public CommonStandComponentImpl(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void setTypeAndSkin(@Nullable StandType type, int skin) {
        this.type = type;
        this.skin = skin;
        sync(entity);
    }

    @Override
    public void setSkin(int skin) {
        if (type == null) {
            return;
        }

        this.skin = Mth.clamp(skin, 0, type.getSkinCount() - 1);
        sync(entity);
    }

    @Override
    public void setStand(@Nullable StandEntity<?, ?> stand) {
        this.stand = stand;
        sync(entity);
    }

    @Nullable
    @Override
    public StandType getType() {
        if (type == null && stand != null) {
            this.type = stand.getStandType();
            JCraft.LOGGER.error("StandType was null despite non-null stand " + stand);
        }
        return this.type;
    }

    @Nullable
    @Override
    public StandEntity<?, ?> getStand() {
        if (stand != null && !stand.isAlive()) {
            setStand(null);
        }
        // Checks if the stand user has a passenger, and updates the stand if the passenger and stand do not match
        if (entity.getFirstPassenger() instanceof StandEntity<?, ?> passenger && stand != passenger) {
            setStand(passenger);
        }
        // Otherwise, returns the stored stand value
        return stand;
    }

    public void sync(Entity entity) {
    }

    public void readFromNbt(@NonNull CompoundTag tag) {
        int rawType = tag.getInt("Type");
        type = rawType == 0 ? null : StandType.fromIdOrOrdinal(rawType);
        skin = tag.getInt("Skin");
    }

    public void writeToNbt(@NonNull CompoundTag tag) {
        tag.putInt("Type", type == null ? 0 : type.ordinal());
        tag.putInt("Skin", skin);
    }

    public void applySyncPacket(FriendlyByteBuf buf) {

        Entity entity = buf.readBoolean() ? this.entity.level().getEntity(buf.readVarInt()) : null;
        if (entity == null || entity instanceof StandEntity<?, ?>) {
            stand = (StandEntity<?, ?>) entity;
        }
    }

    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {

        buf.writeBoolean(stand != null);
        if (stand != null) {
            buf.writeVarInt(stand.getId());
        }
    }
}
