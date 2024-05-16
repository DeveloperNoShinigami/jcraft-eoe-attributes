package net.arna.jcraft.common.component.impl.player;

import lombok.NonNull;
import net.arna.jcraft.common.component.player.CommonSpecComponent;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.common.spec.SpecType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public abstract class CommonSpecComponentImpl implements CommonSpecComponent {
    private final Player player;
    private SpecType type = SpecType.NONE;
    private JSpec<?, ?> spec;

    public CommonSpecComponentImpl(Player player) {
        this.player = player;
    }

    @Override
    public SpecType getType() {
        return type;
    }

    @Override
    public void setType(@NonNull SpecType type) {
        setTypeRaw(type);
        sync(player);
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

    public void sync(Entity entity) {
    }

    public void readFromNbt(@NonNull CompoundTag tag) {
        setTypeRaw(SpecType.fromId(tag.getInt("Type")));
    }

    public void writeToNbt(@NonNull CompoundTag tag) {
        tag.putInt("Type", type.getId());
    }

    public boolean shouldSyncWith(ServerPlayer player) {
        return player == this.player; // Only our player needs to know, I believe.
    }
}
