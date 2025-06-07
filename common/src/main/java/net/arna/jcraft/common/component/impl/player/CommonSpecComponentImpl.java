package net.arna.jcraft.common.component.impl.player;

import lombok.NonNull;
import net.arna.jcraft.api.spec.SpecType2;
import net.arna.jcraft.api.stand.SpecTypeUtil;
import net.arna.jcraft.common.component.player.CommonSpecComponent;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.registry.JSpecTypeRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public abstract class CommonSpecComponentImpl implements CommonSpecComponent {
    protected final LivingEntity user;
    private SpecType2 type = JSpecTypeRegistry.NONE.get();
    private JSpec<?, ?> spec;

    public CommonSpecComponentImpl(final LivingEntity livingEntity) {
        this.user = livingEntity;
    }

    @Override
    public SpecType2 getType() {
        return type;
    }

    @Override
    public void setType(final @NonNull SpecType2 type) {
        setTypeRaw(type);
        sync(user);
    }

    private void setTypeRaw(final SpecType2 type) {
        this.type = type;
        spec = type.createSpec(user);
    }

    @Nullable
    @Override
    public JSpec<?, ?> getSpec() {
        return spec;
    }

    public void sync(final Entity entity) {
    }

    public void readFromNbt(final @NonNull CompoundTag tag) {
        SpecType2 type = SpecTypeUtil.readFromNBT(tag, "Type");
        if (type != null) setTypeRaw(type);
    }

    public void writeToNbt(final @NonNull CompoundTag tag) {
        tag.putString("Type", type.getId().toString());
    }

    public boolean shouldSyncWith(final ServerPlayer player) {
        return player == this.user; // Only our player needs to know.
    }
}
