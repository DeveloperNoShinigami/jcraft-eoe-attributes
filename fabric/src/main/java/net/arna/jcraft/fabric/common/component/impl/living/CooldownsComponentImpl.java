package net.arna.jcraft.fabric.common.component.impl.living;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap;
import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.impl.living.CommonCooldownsComponentImpl;
import net.arna.jcraft.common.config.JServerConfig;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.common.util.JParticleType;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.living.CooldownsComponent;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;

public class CooldownsComponentImpl extends CommonCooldownsComponentImpl implements CooldownsComponent {

    private final Entity entity;

    public CooldownsComponentImpl(@NonNull Entity entity) {
        super(entity);
        this.entity = entity;
    }

    @Override
    public void sync() {
        if (skipSync) return; // To avoid packet spam.
        JComponents.COOLDOWNS.sync(entity);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void readFromNbt(@NonNull NbtCompound tag) {
        readFromNbt(tag);
    }


    @Override
    public void writeToNbt(@NonNull NbtCompound tag) {
        super.writeToNbt(tag);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return super.shouldSyncWith(player);
    }
}
