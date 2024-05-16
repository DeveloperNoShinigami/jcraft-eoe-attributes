package net.arna.jcraft.fabric.common.component.impl;

import net.arna.jcraft.common.component.impl.CommonGravityShiftComponentImpl;
import net.arna.jcraft.fabric.common.component.JComponents;
import net.arna.jcraft.fabric.common.component.living.GravityShiftComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

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
    public boolean shouldSyncWith(ServerPlayer player) {
        if (player.distanceToSqr(user) > RANGE_SQR) return false;
        return GravityShiftComponent.super.shouldSyncWith(player);
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        super.readFromNbt(tag);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        super.writeToNbt(tag);
    }
}
