package net.arna.jcraft.common.util;

import net.minecraft.world.entity.LivingEntity;

public interface IOwnable {
    LivingEntity getMaster();

    void setMaster(LivingEntity m);
}
