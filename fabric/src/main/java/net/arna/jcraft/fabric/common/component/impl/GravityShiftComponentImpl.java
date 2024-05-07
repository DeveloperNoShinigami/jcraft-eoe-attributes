package net.arna.jcraft.fabric.common.component.impl;

import net.arna.jcraft.common.component.impl.CommonGravityShiftComponentImpl;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.living.GravityShiftComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class GravityShiftComponentImpl extends CommonGravityShiftComponentImpl implements GravityShiftComponent {

    private final LivingEntity user;

    public GravityShiftComponentImpl(LivingEntity user) {
        super(user);
        this.user = user;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void sync() {
        JComponents.GRAVITY_SHIFT.sync(user);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        if (player.squaredDistanceTo(user) > RANGE_SQR) return false;
        return GravityShiftComponent.super.shouldSyncWith(player);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        super.readFromNbt(tag);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        super.writeToNbt(tag);
    }
}
