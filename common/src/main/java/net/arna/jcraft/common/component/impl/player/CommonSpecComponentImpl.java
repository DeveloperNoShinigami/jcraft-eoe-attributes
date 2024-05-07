package net.arna.jcraft.common.component.impl.player;

import lombok.NonNull;
import net.arna.jcraft.common.component.player.CommonSpecComponent;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.common.spec.SpecType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public abstract class CommonSpecComponentImpl implements CommonSpecComponent {
    private final PlayerEntity player;
    private SpecType type = SpecType.NONE;
    private JSpec<?, ?> spec;

    public CommonSpecComponentImpl(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public SpecType getType() {
        return type;
    }

    @Override
    public void setType(@NonNull SpecType type) {
        setTypeRaw(type);
        sync();
    }

    private void setTypeRaw(SpecType type) {
        this.type = type;
        spec = type.createNew(player);
    }

    @Nullable
    @Override
    public JSpec<?, ?> getSpec() {
        return spec;
    }

    public void sync() {
    }

    public void readFromNbt(@NonNull NbtCompound tag) {
        setTypeRaw(SpecType.fromId(tag.getInt("Type")));
    }

    public void writeToNbt(@NonNull NbtCompound tag) {
        tag.putInt("Type", type.getId());
    }

    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return player == this.player; // Only our player needs to know, I believe.
    }
}
