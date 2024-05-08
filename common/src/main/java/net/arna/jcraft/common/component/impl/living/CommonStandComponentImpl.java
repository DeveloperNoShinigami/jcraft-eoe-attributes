package net.arna.jcraft.common.component.impl.living;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class CommonStandComponentImpl implements CommonStandComponent {
    private final Entity entity;
    private StandEntity<?, ?> stand;
    @Getter
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
        sync();
    }

    @Override
    public void setSkin(int skin) {
        if (type == null) {
            return;
        }

        this.skin = MathHelper.clamp(skin, 0, type.getSkinCount());
        sync();
    }

    @Override
    public void setStand(@Nullable StandEntity<?, ?> stand) {
        this.stand = stand;
        sync();
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

    public void sync() {
    }

    public void readFromNbt(@NonNull NbtCompound tag) {
        int rawType = tag.getInt("Type");
        type = rawType == 0 ? null : StandType.fromId(rawType);
        skin = tag.getInt("Skin");
    }

    public void writeToNbt(@NonNull NbtCompound tag) {
        tag.putInt("Type", type == null ? 0 : type.getId());
        tag.putInt("Skin", skin);
    }

    public void applySyncPacket(PacketByteBuf buf) {

        Entity entity = buf.readBoolean() ? this.entity.getWorld().getEntityById(buf.readVarInt()) : null;
        if (entity == null || entity instanceof StandEntity<?, ?>) {
            stand = (StandEntity<?, ?>) entity;
        }
    }

    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {

        buf.writeBoolean(stand != null);
        if (stand != null) {
            buf.writeVarInt(stand.getId());
        }
    }
}
