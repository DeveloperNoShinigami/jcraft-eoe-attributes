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

    public CommonStandComponentImpl(final Entity entity) {
        this.entity = entity;
    }

    @Override
    public void setTypeAndSkin(final @Nullable StandType type, final int skin) {
        this.type = type;
        this.skin = skin;
        sync(entity);
    }

    @Override
    public void setSkin(final int skin) {
        if (type == null) {
            return;
        }

        this.skin = Mth.clamp(skin, 0, type.getSkinCount() - 1);
        sync(entity);
    }

    @Override
    public void setStand(final @Nullable StandEntity<?, ?> stand) {
        // if (this.stand != null) this.stand.setUser(null);
        this.stand = stand;
        sync(entity);
    }

    @Nullable
    @Override
    public StandType getType() {
        if (type == null && stand != null) {
            // this.type = stand.getStandType();
            JCraft.LOGGER.warn("StandType is null despite non-null stand " + stand);
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

    public void readFromNbt(final @NonNull CompoundTag tag) {
        int rawType = tag.getInt("Type");
        type = rawType == 0 ? null : StandType.fromIdOrOrdinal(rawType);
        skin = tag.getInt("Skin");
    }

    public void writeToNbt(final @NonNull CompoundTag tag) {
        tag.putInt("Type", type == null ? 0 : type.ordinal());
        tag.putInt("Skin", skin);
    }

    /**
     * Makes a certain entity be considered the component holders stand.
     */
    public void applySyncPacket(final FriendlyByteBuf buf) {
        Entity entity = buf.readBoolean() ? this.entity.level().getEntity(buf.readVarInt()) : null;
        if (entity == null || entity instanceof StandEntity<?, ?>) {
            stand = (StandEntity<?, ?>) entity;
        }
    }

    public void writeSyncPacket(final FriendlyByteBuf buf, final ServerPlayer recipient) {
        buf.writeBoolean(stand != null);
        if (stand != null) {
            buf.writeVarInt(stand.getId());
        }
    }
}
